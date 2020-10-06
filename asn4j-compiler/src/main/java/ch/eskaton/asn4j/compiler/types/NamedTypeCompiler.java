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
import ch.eskaton.asn4j.compiler.Compiler;
import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.compiler.Parameters;
import ch.eskaton.asn4j.compiler.results.AbstractCompiledField;
import ch.eskaton.asn4j.compiler.results.CompiledFixedTypeValueField;
import ch.eskaton.asn4j.compiler.results.CompiledObjectField;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.compiler.results.CompiledTypeField;
import ch.eskaton.asn4j.parser.ast.ObjectClassFieldTypeNode;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.Choice;
import ch.eskaton.asn4j.parser.ast.types.EnumeratedType;
import ch.eskaton.asn4j.parser.ast.types.IRI;
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.asn4j.parser.ast.types.ObjectIdentifier;
import ch.eskaton.asn4j.parser.ast.types.OpenType;
import ch.eskaton.asn4j.parser.ast.types.RelativeIRI;
import ch.eskaton.asn4j.parser.ast.types.RelativeOID;
import ch.eskaton.asn4j.parser.ast.types.SequenceOfType;
import ch.eskaton.asn4j.parser.ast.types.SequenceType;
import ch.eskaton.asn4j.parser.ast.types.SetOfType;
import ch.eskaton.asn4j.parser.ast.types.SetType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;

import java.util.Optional;

public class NamedTypeCompiler implements Compiler<NamedType> {

    public CompiledType compile(CompilerContext ctx, NamedType namedType, Optional<Parameters> maybeParameters) {
        var name = namedType.getName();
        var type = namedType.getType();

        if (type instanceof TypeReference typeReference && maybeParameters.isPresent()) {
            type = ctx.getTypeParameter(maybeParameters.get(), typeReference).orElse(type);
        }

        return compile(ctx, type, name, maybeParameters);
    }

    public CompiledType compile(CompilerContext ctx, Type type, String name, Optional<Parameters> maybeParameters) {
        if (type instanceof EnumeratedType) {
            return compile(ctx, type, name, maybeParameters, ctx.isSubtypeNeeded(type));
        } else if (type instanceof IntegerType) {
            return compile(ctx, type, name, maybeParameters, ctx.isSubtypeNeeded(type));
        } else if (type instanceof BitString) {
            return compile(ctx, type, name, maybeParameters, ctx.isSubtypeNeeded(type));
        } else if (type instanceof SequenceType
                || type instanceof SequenceOfType
                || type instanceof SetType
                || type instanceof SetOfType
                || type instanceof Choice) {
            return compile(ctx, type, name, maybeParameters, true);
        } else if (type instanceof ObjectIdentifier
                || type instanceof RelativeOID
                || type instanceof IRI
                || type instanceof RelativeIRI) {
            return compile(ctx, type, name, maybeParameters, false);
        } else if (type instanceof ObjectClassFieldTypeNode objectClassFieldType) {
            var objectClassReference = objectClassFieldType.getObjectClassReference();
            var compiledObjectClass = ctx.getCompiledObjectClass(objectClassReference.getReference());
            var fieldNames = objectClassFieldType.getFieldName().getPrimitiveFieldNames();
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
                return defineFixedTypeValueField(ctx, type, name, (CompiledFixedTypeValueField) field, maybeParameters);
            } else if (field instanceof CompiledTypeField) {
                return defineTypeField(ctx, type, name, maybeParameters);
            } else if (field == null) {
                throw new IllegalCompilerStateException(type.getPosition(), "Failed to resolve field from %s", type);
            } else {
                throw new IllegalCompilerStateException(type.getPosition(), "Unexpected field type: %s",
                        field.getClass().getSimpleName());
            }
        }

        return ctx.createCompiledType(type, ctx.getTypeName(type, name), true);
    }

    private CompiledType compile(CompilerContext ctx, Type type, String name, Optional<Parameters> maybeParameters,
            boolean newType) {
        if (newType && name != null) {
            var compiledType = compileType(ctx, type, ctx.getTypeName(type, name), maybeParameters);

            compiledType.setSubtype(true);

            return compiledType;
        }

        return ctx.createCompiledType(type, ctx.getTypeName(type, name), ctx.isBuiltin(type));
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
        var newType = (Type) Clone.clone(field.getCompiledType().getType());

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

    private CompiledType compileType(CompilerContext ctx, Type type, String typeName,
            Optional<Parameters> maybeParameters) {
        return ctx.<Type, TypeCompiler>getCompiler(Type.class).compile(ctx, typeName, type, maybeParameters);
    }

}
