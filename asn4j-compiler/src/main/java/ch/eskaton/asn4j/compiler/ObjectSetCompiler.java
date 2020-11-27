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

import ch.eskaton.asn4j.compiler.objects.ObjectDefnCompiler;
import ch.eskaton.asn4j.compiler.results.CompiledFixedTypeValueField;
import ch.eskaton.asn4j.compiler.results.CompiledObjectClass;
import ch.eskaton.asn4j.compiler.results.CompiledObjectSet;
import ch.eskaton.asn4j.parser.ast.ObjectDefnNode;
import ch.eskaton.asn4j.parser.ast.ObjectReference;
import ch.eskaton.asn4j.parser.ast.ObjectSetElements;
import ch.eskaton.asn4j.parser.ast.ObjectSetReference;
import ch.eskaton.asn4j.parser.ast.ObjectSetSpecNode;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.commons.collections.Sets;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ObjectSetCompiler implements Compiler<ObjectSetSpecNode> {

    private CompilerContext ctx;

    public ObjectSetCompiler(CompilerContext ctx) {
        this.ctx = ctx;
    }

    public CompiledObjectSet compile(CompilerContext ctx, String objectSetName, CompiledObjectClass objectClass,
            ObjectSetSpecNode objectSet, Optional<Parameters> maybeParameters) {
        var elementSet = objectSet.getRootElements();

        return getCompiledObjectSet(ctx, objectSetName, objectClass, elementSet, maybeParameters);
    }

    public CompiledObjectSet getCompiledObjectSet(CompilerContext ctx, String objectSetName,
            CompiledObjectClass objectClass, ElementSet elementSet, Optional<Parameters> maybeParameters) {
        var compiledObjectSet = ctx.createCompiledObjectSet(objectSetName, objectClass);
        var values = compile(objectClass, elementSet, maybeParameters);

        objectClass.getFields().stream()
                .filter(CompiledFixedTypeValueField.class::isInstance)
                .map(CompiledFixedTypeValueField.class::cast)
                .filter(CompiledFixedTypeValueField::isUnique)
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

    private Set<Map<String, Object>> compile(CompiledObjectClass objectClass, ElementSet elementSet,
            Optional<Parameters> maybeParameters) {
        if (elementSet == null) {
            return Set.of();
        }

        var operands = elementSet.getOperands();

        switch (elementSet.getOperation()) {
            case ALL:
                return new HashSet<>();
            case EXCLUDE:
                if (operands.size() == 1) {
                    return Set.of();
                } else {
                    return exclusion(objectClass, operands, maybeParameters);
                }
            case UNION:
                return union(objectClass, operands, maybeParameters);
            case INTERSECTION:
                return intersection(objectClass, operands, maybeParameters);
            default:
                throw new CompilerException(elementSet.getPosition(), "Unimplemented operation: %s",
                        elementSet.getOperation());
        }
    }

    private Set<Map<String, Object>> exclusion(CompiledObjectClass objectClass, List<Elements> operands,
            Optional<Parameters> maybeParameters) {
        var op1 = compile(objectClass, operands.get(0), maybeParameters);
        var op2 = compile(objectClass, operands.get(1), maybeParameters);

        op1.removeAll(op2);

        return op1;
    }

    private Set<Map<String, Object>> intersection(CompiledObjectClass objectClass, List<Elements> operands,
            Optional<Parameters> maybeParameters) {
        return operands.stream().map(op -> compile(objectClass, op, maybeParameters)).reduce((op1, op2) -> {
            op1.retainAll(op2);

            return op1;
        }).orElse(new HashSet<>());
    }

    private Set<Map<String, Object>> union(CompiledObjectClass objectClass, List<Elements> operands,
            Optional<Parameters> maybeParameters) {
        return operands.stream().map(op -> compile(objectClass, op, maybeParameters)).reduce((op1, op2) -> {
            op1.addAll(op2);

            return op1;
        }).orElse(new HashSet<>());
    }

    private Set<Map<String, Object>> compile(CompiledObjectClass objectClass, Elements elements,
            Optional<Parameters> maybeParameters) {
        if (elements instanceof ElementSet) {
            return compile(objectClass, (ElementSet) elements, maybeParameters);
        } else if (elements instanceof ObjectSetElements) {
            return Sets.<Map<String, Object>>builder()
                    .addAll(compile(objectClass, (ObjectSetElements) elements, maybeParameters))
                    .build();
        } else {
            throw new CompilerException(elements.getPosition(), "Unsupported elements: %s",
                    elements.getClass().getSimpleName());
        }
    }

    private Set<Map<String, Object>> compile(CompiledObjectClass objectClass, ObjectSetElements elements,
            Optional<Parameters> maybeParameters) {
        var element = elements.getElement();

        if (element instanceof ObjectDefnNode objectDefn) {
            return Set.of(ctx.<ObjectDefnNode, ObjectDefnCompiler>getCompiler(ObjectDefnNode.class)
                    .compile(objectClass, objectDefn, maybeParameters));
        } else if (element instanceof ObjectReference objectReference) {
            return Set.of(ctx.getCompiledObject(objectReference).getObjectDefinition());
        } else if (element instanceof ObjectSetReference objectSetReference) {
            return ctx.getCompiledObjectSet(objectSetReference).getValues();
        } else {
            throw new CompilerException(elements.getPosition(), "Unsupported element: %s",
                    element.getClass().getSimpleName());
        }
    }

}
