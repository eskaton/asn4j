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

package ch.eskaton.asn4j.compiler.types;

import ch.eskaton.asn4j.compiler.Clone;
import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.compiler.NamedCompiler;
import ch.eskaton.asn4j.compiler.Parameters;
import ch.eskaton.asn4j.compiler.results.AbstractCompiledField;
import ch.eskaton.asn4j.compiler.results.CompiledFixedTypeValueField;
import ch.eskaton.asn4j.compiler.results.CompiledObjectField;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.compiler.results.CompiledTypeField;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.asn4j.parser.ast.types.ObjectClassFieldType;
import ch.eskaton.asn4j.parser.ast.types.OpenType;
import ch.eskaton.asn4j.parser.ast.types.Type;

import java.util.Optional;

public class ObjectClassFieldTypeCompiler implements NamedCompiler<ObjectClassFieldType, CompiledType> {

    @Override
    public CompiledType compile(CompilerContext ctx, String name, ObjectClassFieldType node,
            Optional<Parameters> maybeParameters) {
        var objectClassReference = node.getObjectClassReference();
        var compiledObjectClass = ctx.getCompiledObjectClass(objectClassReference.getReference());
        var fieldNames = node.getFieldName().getPrimitiveFieldNames();
        AbstractCompiledField<?> field = null;

        for (var i = 0; i < fieldNames.size(); i++) {
            var fieldName = fieldNames.get(i);
            var objectClassName = compiledObjectClass.getName();

            field = compiledObjectClass.getField(fieldName.getReference())
                    .orElseThrow(() -> new CompilerException("Unknown field '%s' in object class %s",
                            fieldName.getReference(), objectClassName));

            if (i < fieldNames.size() - 1) {
                if (field instanceof CompiledObjectField compiledObjectField) {
                    compiledObjectClass = compiledObjectField.getObjectClass();
                } else {
                    throw new CompilerException(fieldName.getPosition(), "&%s doesn't refer to an object class",
                            field.getName());
                }
            }
        }

        if (field instanceof CompiledFixedTypeValueField) {
            return defineFixedTypeValueField(ctx, node, name, (CompiledFixedTypeValueField) field, maybeParameters);
        } else if (field instanceof CompiledTypeField) {
            return defineTypeField(ctx, node, name, maybeParameters);
        } else if (field == null) {
            throw new IllegalCompilerStateException(node.getPosition(), "Failed to resolve field from %s", node);
        } else {
            throw new IllegalCompilerStateException(node.getPosition(), "Unexpected field type: %s",
                    field.getClass().getSimpleName());
        }
    }

    private CompiledType defineTypeField(CompilerContext ctx, Type type, String name,
            Optional<Parameters> maybeParameters) {
        var additionalConstraints = type.getConstraints();
        var openType = new OpenType();

        openType.setTags(type.getTags());
        openType.setTaggingModes(type.getTaggingModes());

        if (additionalConstraints != null) {
            var constraints = openType.getConstraints();

            if (constraints == null) {
                openType.setConstraints(additionalConstraints);
            } else {
                constraints.addAll(additionalConstraints);
            }
        }

        return compile(ctx, openType, name, maybeParameters);
    }

    private CompiledType defineFixedTypeValueField(CompilerContext ctx, Type type, String name,
            CompiledFixedTypeValueField field, Optional<Parameters> maybeParameters) {
        var additionalConstraints = type.getConstraints();
        var newType = Clone.clone(field.getCompiledType().getType());

        newType.setTags(type.getTags());

        if (additionalConstraints != null) {
            var constraints = newType.getConstraints();

            if (constraints == null) {
                newType.setConstraints(additionalConstraints);
            } else {
                constraints.addAll(additionalConstraints);
            }
        }

        return compile(ctx, newType, name, maybeParameters);
    }

    private CompiledType compile(CompilerContext ctx, Type type, String name, Optional<Parameters> maybeParameters) {
        var compiler = ctx.<NamedType, NamedTypeCompiler>getCompiler(NamedType.class);

        return compiler.compile(ctx, type, name, maybeParameters);
    }

}
