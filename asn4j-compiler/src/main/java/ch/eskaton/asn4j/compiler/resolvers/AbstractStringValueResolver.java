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

package ch.eskaton.asn4j.compiler.resolvers;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.CompilerUtils;
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.parser.Position;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.values.AmbiguousValue;
import ch.eskaton.asn4j.parser.ast.values.CharacterStringList;
import ch.eskaton.asn4j.parser.ast.values.HasStringValue;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.StringValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.types.TypeName;
import ch.eskaton.asn4j.runtime.verifiers.StringVerifier;

import java.util.stream.Collectors;

public abstract class AbstractStringValueResolver<T extends HasStringValue & Value> extends AbstractValueResolver<T> {

    private final TypeName typeName;

    private final Class<? extends Type> typeClass;

    private final StringVerifier verifier;

    public AbstractStringValueResolver(CompilerContext ctx, TypeName typeName, Class<? extends Type> typeClass, StringVerifier verifier) {
        super(ctx);

        this.typeName = typeName;
        this.typeClass = typeClass;
        this.verifier = verifier;
    }

    protected TypeName getTypeName() {
        return typeName;
    }

    @Override
    public T resolveGeneric(Type type, Value value) {
        var resolvedType = verifyType(type);

        if (value instanceof SimpleDefinedValue) {
            return resolve((SimpleDefinedValue) value);
        } else if (value instanceof AmbiguousValue) {
            var stringValue = CompilerUtils.resolveAmbiguousValue(value, StringValue.class);

            if (stringValue == null) {
                var characterStringListValue = resolveCharacterStringList(resolvedType, value);

                if (characterStringListValue != null) {
                    return characterStringListValue;
                }
            }

            value = stringValue;
        } else if (value instanceof StringValue) {
            // value is already a string
        }

        if (value == null) {
            throw new CompilerException("Failed to resolve a %s value", typeName);
        }

        var stringValue = (StringValue) value;
        var cString = getString(resolvedType, stringValue);

        return createValue(stringValue.getPosition(), cString);
    }

    protected T resolveCharacterStringList(Type resolvedType, Value value) {
        var resolvedValue = CompilerUtils.resolveAmbiguousValue(value, CharacterStringList.class);

        if (resolvedValue != null) {
            return createValue(value.getPosition(), resolvedValue.getValues()
                    .stream()
                    .map(v -> resolveCharSyms(resolvedType, v))
                    .collect(Collectors.joining()));
        }

        return null;
    }

    private Type verifyType(Type type) {
        var resolvedType = resolveTypeReference(type);

        if (!typeClass.isAssignableFrom(resolvedType.getClass())) {
            throw new CompilerException("Failed to resolve a value. Expected %s but found %s.",
                    typeName, type.getClass().getSimpleName());
        }

        return resolvedType;
    }

    private Type resolveTypeReference(Type type) {
        if (type instanceof TypeReference) {
            type = (Type) ctx.resolveTypeReference(type);
        }

        return type;
    }

    private String resolveCharSyms(Type type, Value value) {
        if (value instanceof StringValue) {
            return getString(type, (StringValue) value);
        } else if (value instanceof SimpleDefinedValue) {
            return resolve((SimpleDefinedValue) value).getValue();
        } else {
            throw new IllegalCompilerStateException("Unsupported value for %s: %s", typeName, value);
        }
    }

    private String getString(Type type, StringValue stringValue) {
        var cString = stringValue.getCString();

        verifier.verify(cString).ifPresent(v -> {
            throw new CompilerException("%s contains invalid characters: %s", typeName, v);
        });

        return cString;
    }

    protected abstract T createValue(Position position, String value);

}
