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

import ch.eskaton.asn4j.compiler.results.AbstractCompiledField;
import ch.eskaton.asn4j.compiler.results.CompiledObjectClass;
import ch.eskaton.asn4j.compiler.results.CompiledTypeField;
import ch.eskaton.asn4j.compiler.results.CompiledVariableTypeValueField;
import ch.eskaton.asn4j.compiler.utils.TypeFormatter;
import ch.eskaton.asn4j.compiler.utils.ValueFormatter;
import ch.eskaton.asn4j.parser.Group;
import ch.eskaton.asn4j.parser.ObjectClassDefn;
import ch.eskaton.asn4j.parser.RequiredToken;
import ch.eskaton.asn4j.parser.ast.AbstractFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.FixedTypeValueFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.FixedTypeValueOrObjectFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.FixedTypeValueSetFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.FixedTypeValueSetOrObjectSetFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.LiteralNode;
import ch.eskaton.asn4j.parser.ast.ObjectFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.ObjectSetFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.PrimitiveFieldNameNode;
import ch.eskaton.asn4j.parser.ast.TypeFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.VariableTypeValueFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.VariableTypeValueSetFieldSpecNode;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ObjectClassDefnCompiler implements NamedCompiler<ObjectClassDefn, CompiledObjectClass> {

    @Override
    public CompiledObjectClass compile(CompilerContext ctx, String name, ObjectClassDefn node) {
        var compiledObjectClass = ctx.createCompiledObjectClass(name);
        var fieldSpecs = node.getFieldSpec();

        for (var unknownFieldSpec : fieldSpecs) {
            if (unknownFieldSpec instanceof FixedTypeValueOrObjectFieldSpecNode fieldSpec) {
                if (fieldSpec.getObjectFieldSpec().isPresent()) {
                    var objectFieldSpec = fieldSpec.getObjectFieldSpec().get();

                    try {
                        // Check whether reference refers to an object class
                        ctx.getCompiledObjectClass(objectFieldSpec.getObjectClassReference());
                    } catch (CompilerException e) {
                        // ignore
                    }

                    var compiledField = ctx.<ObjectFieldSpecNode, NamedCompiler<ObjectFieldSpecNode, AbstractCompiledField>>getCompiler(
                            (Class<ObjectFieldSpecNode>) objectFieldSpec.getClass()).compile(ctx, name, objectFieldSpec);

                    compiledObjectClass.addField(compiledField);
                    continue;
                }

                if (fieldSpec.getFixedTypeValueFieldSpec().isPresent()) {
                    var fixedTypeValueFieldSpec = fieldSpec.getFixedTypeValueFieldSpec().get();
                    var compiledField = ctx.<FixedTypeValueFieldSpecNode, NamedCompiler<FixedTypeValueFieldSpecNode, AbstractCompiledField>>getCompiler(
                            (Class<FixedTypeValueFieldSpecNode>) fixedTypeValueFieldSpec.getClass())
                            .compile(ctx, name, fixedTypeValueFieldSpec);

                    compiledObjectClass.addField(compiledField);
                }
            } else if (unknownFieldSpec instanceof TypeFieldSpecNode typeFieldSpec) {
                var compiledField = ctx.<TypeFieldSpecNode, NamedCompiler<TypeFieldSpecNode, AbstractCompiledField>>getCompiler(
                        (Class<TypeFieldSpecNode>) typeFieldSpec.getClass())
                        .compile(ctx, name, typeFieldSpec);

                compiledObjectClass.addField(compiledField);
            } else if (unknownFieldSpec instanceof VariableTypeValueFieldSpecNode variableTypeValueFieldSpec) {
                var compiledField = ctx.<VariableTypeValueFieldSpecNode, NamedCompiler<VariableTypeValueFieldSpecNode, AbstractCompiledField>>getCompiler(
                        (Class<VariableTypeValueFieldSpecNode>) variableTypeValueFieldSpec.getClass())
                        .compile(ctx, name, variableTypeValueFieldSpec);

                compiledObjectClass.addField(compiledField);
            } else if (unknownFieldSpec instanceof FixedTypeValueSetOrObjectSetFieldSpecNode fixedTypeValueSetOrObjectSetFieldSpec) {
                if (fixedTypeValueSetOrObjectSetFieldSpec.getObjectSetFieldSpec().isPresent()) {
                    var objectSetFieldSpec = fixedTypeValueSetOrObjectSetFieldSpec.getObjectSetFieldSpec().get();

                    try {
                        // Check whether reference refers to an object set
                        ctx.getCompiledObjectSet(objectSetFieldSpec.getReference());
                    } catch (CompilerException e) {
                        // ignore
                    }

                    var compiledField = ctx.<ObjectSetFieldSpecNode, NamedCompiler<ObjectSetFieldSpecNode, AbstractCompiledField>>getCompiler(
                            (Class<ObjectSetFieldSpecNode>) objectSetFieldSpec.getClass()).compile(ctx, name, objectSetFieldSpec);

                    compiledObjectClass.addField(compiledField);
                    continue;
                }

                if (fixedTypeValueSetOrObjectSetFieldSpec.getFixedTypeValueSetFieldSpec().isPresent()) {
                    var fixedTypeValueSetFieldSpec = fixedTypeValueSetOrObjectSetFieldSpec.getFixedTypeValueSetFieldSpec().get();
                    var compiledField = ctx.<FixedTypeValueSetFieldSpecNode, NamedCompiler<FixedTypeValueSetFieldSpecNode, AbstractCompiledField>>getCompiler(
                            (Class<FixedTypeValueSetFieldSpecNode>) fixedTypeValueSetFieldSpec.getClass())
                            .compile(ctx, name, fixedTypeValueSetFieldSpec);

                    compiledObjectClass.addField(compiledField);
                }
            } else if (unknownFieldSpec instanceof VariableTypeValueSetFieldSpecNode variableTypeValueSetFieldSpec) {
                var compiledField = ctx.<VariableTypeValueSetFieldSpecNode, NamedCompiler<VariableTypeValueSetFieldSpecNode, AbstractCompiledField>>getCompiler(
                        (Class<VariableTypeValueSetFieldSpecNode>) variableTypeValueSetFieldSpec.getClass())
                        .compile(ctx, name, variableTypeValueSetFieldSpec);

                compiledObjectClass.addField(compiledField);
            } else {
                throw new IllegalCompilerStateException("Field of type %s not yet supported",
                        unknownFieldSpec.getClass().getSimpleName());
            }
        }

        verifyIntegrity(ctx, name, compiledObjectClass, fieldSpecs);

        var syntax = node.getSyntaxSpec();

        if (syntax != null) {
            validateSyntax(compiledObjectClass, syntax);

            compiledObjectClass.setSyntax(syntax);
        }

        return compiledObjectClass;
    }

    private void validateSyntax(CompiledObjectClass compiledObjectClass, List<? extends Object> syntax) {
        var definedFields = new HashSet<String>();
        var groupLeaders = new LinkedList<Set<String>>();

        groupLeaders.add(new HashSet<>());

        validateSyntax(compiledObjectClass, syntax, false, groupLeaders, definedFields);

        var mandatoryFields = compiledObjectClass.getFields().stream()
                .filter(field -> !(field.isOptional() || field.getDefaultValue().isPresent()))
                .map(field -> field.getName())
                .collect(Collectors.toSet());

        mandatoryFields.removeAll(definedFields);

        if (!mandatoryFields.isEmpty()) {
            var fieldNames = mandatoryFields.stream().collect(Collectors.joining(", "));

            throw new CompilerException("Not all mandatory fields are defined in the syntax for object class '%s': %s",
                    compiledObjectClass.getName(), fieldNames);
        }
    }

    private void validateSyntax(CompiledObjectClass compiledObjectClass, List<? extends Object> syntax,
            boolean optional, Deque<Set<String>> groupLeaders, HashSet<String> definedFields) {
        var thisGroupLeader = Optional.<String>empty();
        var first = true;

        for (var spec : syntax) {
            if (spec instanceof Group group) {
                validateSyntax(compiledObjectClass, group.getGroup(), true, groupLeaders, definedFields);
            } else if (spec instanceof RequiredToken requiredToken) {
                var token = requiredToken.getToken();

                if (token instanceof PrimitiveFieldNameNode fieldNameNode) {
                    var fieldName = fieldNameNode.getReference();
                    var maybeField = compiledObjectClass.getField(fieldName);

                    if (maybeField.isEmpty()) {
                        throw new CompilerException(fieldNameNode.getPosition(),
                                "Syntax of object class '%s' references the undefined field '%s'",
                                compiledObjectClass.getName(), fieldName);
                    }

                    if (definedFields.contains(fieldName)) {
                        throw new CompilerException(fieldNameNode.getPosition(),
                                "Field '%s' already used in the syntax definition of object class '%s'",
                                fieldName, compiledObjectClass.getName());
                    }

                    definedFields.add(fieldName);

                    var field = maybeField.get();

                    if (optional && !(field.isOptional() || field.getDefaultValue().isPresent())) {
                        throw new CompilerException(fieldNameNode.getPosition(),
                                "'%s' in object class '%s' is defined in an optional group but refers to a mandatory field",
                                fieldName, compiledObjectClass.getName());
                    }
                } else if (token instanceof LiteralNode literal) {
                    if (groupLeaders.peek().contains(literal.getText())) {
                        throw new CompilerException(literal.getPosition(),
                                "Literal '%s' in object class '%s' is illegal at this position because it's also " +
                                        "used as the first literal of a preceding optional group",
                                literal.getText(), compiledObjectClass.getName());
                    }

                    if (!first) {
                        groupLeaders.peek().clear();
                    }

                    if (first == true && optional) {
                        thisGroupLeader = Optional.of(literal.getText());
                    }
                }

                if (first) {
                    groupLeaders.push(new HashSet<>());
                    first = false;
                }
            }
        }

        groupLeaders.pop();

        if (thisGroupLeader.isPresent()) {
            groupLeaders.peek().add(thisGroupLeader.get());
        }
    }

    private void verifyIntegrity(CompilerContext ctx, String name, CompiledObjectClass compiledObjectClass,
            List<AbstractFieldSpecNode> fieldSpecs) {
        for (var field : compiledObjectClass.getFields()) {
            if (field instanceof CompiledVariableTypeValueField compiledVariableTypeValueField) {
                verifyVariableTypeValueField(ctx, name, compiledObjectClass, fieldSpecs,
                        compiledVariableTypeValueField);
            }
        }
    }

    private void verifyVariableTypeValueField(CompilerContext ctx, String name, CompiledObjectClass compiledObjectClass,
            List<AbstractFieldSpecNode> fieldSpecs, CompiledVariableTypeValueField field) {
        var reference = field.getReference();
        var maybeReferencedField = compiledObjectClass.getField(reference);

        if (maybeReferencedField.isEmpty()) {
            var fieldSpec = getFieldSpecByName(fieldSpecs, field);
            throw new CompilerException(fieldSpec.getPosition(),
                    "'%s' in object class '%s' refers to the inexistent field '%s'",
                    field.getName(), name, reference);
        }

        var maybeDefaultValue = field.getDefaultValue();

        if (maybeDefaultValue.isPresent()) {
            var referencedField = maybeReferencedField.get();

            if (referencedField instanceof CompiledTypeField typeField) {
                var maybeReferencedDefault = typeField.getDefaultValue();

                if (maybeReferencedDefault.isEmpty()) {
                    var fieldSpec = getFieldSpecByName(fieldSpecs, field);

                    throw new CompilerException(fieldSpec.getPosition(),
                            "'%s' in object class '%s' defines a default value, " +
                                    "but the referenced type field '%s' has no default",
                            field.getName(), name, typeField.getName());
                }

                var compiledType = maybeReferencedDefault.get();
                var type = compiledType.getType();
                var valueType = ctx.getValueType(type);
                var defaultValue = maybeDefaultValue.get();

                try {
                    var resolvedValue = ctx.resolveGenericValue(valueType, type, defaultValue);

                    field.setDefaultValue(resolvedValue);
                } catch (ValueResolutionException e) {
                    var fieldSpec = getFieldSpecByName(fieldSpecs, field);
                    var formattedType = TypeFormatter.formatType(ctx, type);
                    var formattedValue = ValueFormatter.formatValue(defaultValue);

                    throw new CompilerException(fieldSpec.getPosition(),
                            "'%s' in object class '%s' expects a default value of type %s but found '%s'",
                            field.getName(), name, formattedType, formattedValue);
                }
            } else {
                var fieldSpec = getFieldSpecByName(fieldSpecs, field);

                throw new CompilerException(fieldSpec.getPosition(),
                        "'%s' in object class '%s' refers to the field '%s' which is not a type field",
                        field.getName(), name, reference);
            }
        }
    }

    private AbstractFieldSpecNode getFieldSpecByName(List<AbstractFieldSpecNode> fieldSpecs,
            CompiledVariableTypeValueField compiledVariableTypeValueField) {
        return fieldSpecs.stream()
                .filter(spec -> spec.getReference().equals(compiledVariableTypeValueField.getName()))
                .findAny()
                .get();
    }

}
