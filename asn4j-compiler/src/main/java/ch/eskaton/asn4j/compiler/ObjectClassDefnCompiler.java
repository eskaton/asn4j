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
import ch.eskaton.asn4j.parser.ObjectClassDefn;
import ch.eskaton.asn4j.parser.ast.AbstractFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.FixedTypeValueFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.FixedTypeValueOrObjectFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.ObjectFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.TypeFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.VariableTypeValueFieldSpecNode;

import java.util.List;

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
                        ctx.getCompiledObjectClass(objectFieldSpec.getObjectClass());

                        var compiledField = ctx.<ObjectFieldSpecNode, NamedCompiler<ObjectFieldSpecNode, AbstractCompiledField>>getCompiler(
                                (Class<ObjectFieldSpecNode>) objectFieldSpec.getClass()).compile(ctx, name, objectFieldSpec);

                        compiledObjectClass.addField(compiledField);
                        continue;
                    } catch (CompilerException e) {
                        // ignore
                    }
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
            } else {
                throw new IllegalCompilerStateException("Field of type %s not yet supported",
                        unknownFieldSpec.getClass().getSimpleName());
            }
        }

        verifyIntegrity(ctx, name, compiledObjectClass, fieldSpecs);

        return compiledObjectClass;
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
                    "%s in object class %s refers to the inexistent field %s", field.getName(), name, reference);
        }

        var maybeDefaultValue = field.getDefaultValue();

        if (maybeDefaultValue.isPresent()) {
            var referencedField = maybeReferencedField.get();

            if (referencedField instanceof CompiledTypeField typeField) {
                var maybeReferencedDefault = typeField.getDefaultValue();

                if (maybeReferencedDefault.isEmpty()) {
                    var fieldSpec = getFieldSpecByName(fieldSpecs, field);

                    throw new CompilerException(fieldSpec.getPosition(),
                            "%s in object class %s defines a default value, " +
                                    "but the referenced type field %s has no default",
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

                    throw new CompilerException(fieldSpec.getPosition(),
                            "%s in object class %s expects a default value of type %s but found %s",
                            field.getName(), name, TypeFormatter.formatType(ctx, type),
                            ValueFormatter.formatValue(defaultValue));
                }
            } else {
                var fieldSpec = getFieldSpecByName(fieldSpecs, field);

                throw new CompilerException(fieldSpec.getPosition(),
                        "%s in object class %s refers to the field %s which is not a type field", field.getName(), name,
                        reference);
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
