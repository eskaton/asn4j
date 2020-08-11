/*
 *  Copyright (c) 2015, Adrian Moser
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  * Neither the name of the author nor the
 *  names of its contributors may be used to endorse or promote products
 *  derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL AUTHOR BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ch.eskaton.asn4j.compiler;

import ch.eskaton.asn4j.compiler.results.CompiledFixedTypeValueField;
import ch.eskaton.asn4j.compiler.results.CompiledObjectClass;
import ch.eskaton.asn4j.compiler.results.CompiledObjectSet;
import ch.eskaton.asn4j.compiler.results.CompiledTypeField;
import ch.eskaton.asn4j.parser.ast.DefaultSyntaxNode;
import ch.eskaton.asn4j.parser.ast.FieldSettingNode;
import ch.eskaton.asn4j.parser.ast.ObjectDefnNode;
import ch.eskaton.asn4j.parser.ast.ObjectSetAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ObjectSetElements;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.commons.collections.Sets;
import ch.eskaton.commons.collections.Tuple2;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ObjectSetAssignmentCompiler implements Compiler<ObjectSetAssignmentNode> {

    private CompilerContext ctx;

    public CompiledObjectSet compile(CompilerContext ctx, ObjectSetAssignmentNode node) {
        this.ctx = ctx;

        var objectClassReference = node.getObjectClassReference();
        var objectSet = node.getObjectSet();
        var objectSetName = node.getReference();
        var objectClass = ctx.getCompiledObjectClass(objectClassReference);

        System.out.println("Compiling object set " + objectSetName);

        var compiledObjectSet = ctx.createCompiledObjectSet(objectSetName, objectClass);
        var values = compile(objectClass, objectSet.getRootElements());

        objectClass.getFields().stream()
                .filter(CompiledFixedTypeValueField.class::isInstance)
                .filter(f -> ((CompiledFixedTypeValueField) f).isUnique())
                .forEach(f -> {
                    var fieldName = f.getName();
                    var fieldValues = new HashSet<Value>();

                    for (var value : values) {
                        var fieldValue = (Value) value.get(fieldName);

                        if (fieldValue == null) {
                            // skip optional fields
                            continue;
                        }

                        if (fieldValues.contains(fieldValue)) {
                            throw new CompilerException(fieldValue.getPosition(), "Duplicate value in object set: %s",
                                    fieldValue);
                        }

                        fieldValues.add(fieldValue);
                    }
                });

        compiledObjectSet.setValues(values);

        return compiledObjectSet;
    }

    private Set<Map<String, Object>> compile(CompiledObjectClass objectClass, ElementSet elementSet) {
        List<Elements> operands = elementSet.getOperands();

        switch (elementSet.getOperation()) {
            case ALL:
                return new HashSet<>();
            case EXCLUDE:
                if (operands.size() == 1) {
                    return new HashSet<>();
                } else {
                    return exclusion(objectClass, operands);
                }
            case UNION:
                return union(objectClass, operands);
            case INTERSECTION:
                return intersection(objectClass, operands);
            default:
                throw new CompilerException(elementSet.getPosition(), "Unimplemented operation: %s",
                        elementSet.getOperation());
        }
    }

    private Set<Map<String, Object>> exclusion(CompiledObjectClass objectClass, List<Elements> operands) {
        var op1 = compile(objectClass, operands.get(0));
        var op2 = compile(objectClass, operands.get(1));

        op1.removeAll(op2);

        return op1;
    }

    private Set<Map<String, Object>> intersection(CompiledObjectClass objectClass, List<Elements> operands) {
        return operands.stream().map(op -> compile(objectClass, op)).reduce((op1, op2) -> {
            op1.retainAll(op2);

            return op1;
        }).orElse(new HashSet<>());
    }

    private Set<Map<String, Object>> union(CompiledObjectClass objectClass, List<Elements> operands) {
        return operands.stream().map(op -> compile(objectClass, op)).reduce((op1, op2) -> {
            op1.addAll(op2);

            return op1;
        }).orElse(new HashSet<>());
    }

    private Set<Map<String, Object>> compile(CompiledObjectClass objectClass, Elements elements) {
        if (elements instanceof ElementSet) {
            return compile(objectClass, (ElementSet) elements);
        } else if (elements instanceof ObjectSetElements) {
            return Sets.<Map<String, Object>>builder().add(compile(objectClass, (ObjectSetElements) elements)).build();
        } else {
            throw new CompilerException(elements.getPosition(), "Unsupported elements: %s",
                    elements.getClass().getSimpleName());
        }
    }

    private Map<String, Object> compile(CompiledObjectClass objectClass, ObjectSetElements elements) {
        var element = elements.getElement();

        if (element instanceof ObjectDefnNode) {
            return compile(objectClass, (ObjectDefnNode) element);
        } else {
            throw new CompilerException(elements.getPosition(), "Unsupported element: %s",
                    element.getClass().getSimpleName());
        }
    }

    private Map<String, Object> compile(CompiledObjectClass objectClass, ObjectDefnNode element) {
        var syntax = element.getSyntax();

        if (syntax instanceof DefaultSyntaxNode) {
            return compile(objectClass, (DefaultSyntaxNode) syntax);
        } else {
            throw new CompilerException(syntax.getPosition(), "Unsupported syntax: %s",
                    syntax.getClass().getSimpleName());
        }
    }

    private Map<String, Object> compile(CompiledObjectClass objectClass, DefaultSyntaxNode syntaxNode) {
        var values = syntaxNode.getFieldSetting().stream()
                .map(setting -> compile(objectClass, setting))
                .collect(Collectors.toMap(Tuple2::get_1, Tuple2::get_2));
        var fields = objectClass.getFields();

        for (var field : fields) {
            if (!field.isOptional()) {
                var fieldName = field.getName();

                if (!values.containsKey(fieldName)) {
                    var defaultValue = field.getDefaultValue();

                    if (defaultValue != null) {
                        values.put(fieldName, defaultValue);
                    } else {
                        throw new CompilerException(syntaxNode.getPosition(), "Field '%s' is mandatory", fieldName);
                    }
                }
            }
        }

        return values;
    }

    private Tuple2<String, Object> compile(CompiledObjectClass objectClass, FieldSettingNode fieldSettingNode) {
        var fieldName = fieldSettingNode.getFieldName();
        var reference = fieldName.getReference();
        var maybeField = objectClass.getField(reference);

        if (maybeField.isPresent()) {
            var field = maybeField.get();

            if (field instanceof CompiledFixedTypeValueField) {
                var compiledField = (CompiledFixedTypeValueField) field;
                var type = compiledField.getCompiledType().getType();
                var setting = fieldSettingNode.getSetting();
                var value = (Value) setting;

                return Tuple2.of(reference, ctx.resolveGenericValue(ctx.getValueType(type), type, value));
            } else if (field instanceof CompiledTypeField) {
                var setting = fieldSettingNode.getSetting();
                var type = (Type) setting;

                return Tuple2.of(reference, ctx.getCompiledType(type));
            } else {
                throw new IllegalCompilerStateException("Unsupported field of type %s",
                        field.getClass().getSimpleName());
            }
        } else {
            throw new CompilerException(fieldSettingNode.getPosition(), "Invalid reference %s", reference);
        }
    }

}
