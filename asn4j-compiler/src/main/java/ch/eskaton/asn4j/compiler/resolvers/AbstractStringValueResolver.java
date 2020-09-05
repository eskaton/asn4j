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
import ch.eskaton.asn4j.compiler.CompilerUtils;
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.compiler.ValueResolutionException;
import ch.eskaton.asn4j.compiler.utils.ValueFormatter;
import ch.eskaton.asn4j.parser.Position;
import ch.eskaton.asn4j.parser.ast.QuadrupleNode;
import ch.eskaton.asn4j.parser.ast.TupleNode;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.values.AmbiguousValue;
import ch.eskaton.asn4j.parser.ast.values.CharacterStringList;
import ch.eskaton.asn4j.parser.ast.values.CollectionOfValue;
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

    private final Class<? extends Value> valueClass;

    private final StringVerifier verifier;

    public AbstractStringValueResolver(CompilerContext ctx, TypeName typeName, Class<? extends Type> typeClass,
            Class<? extends Value> valueClass, StringVerifier verifier) {
        super(ctx);

        this.typeName = typeName;
        this.typeClass = typeClass;
        this.valueClass = valueClass;
        this.verifier = verifier;
    }

    protected TypeName getTypeName() {
        return typeName;
    }

    @Override
    public T resolveGeneric(Type type, Value value) {
        var valuePosition = value.getPosition();
        var resolvedType = verifyType(type, valuePosition);

        if (typeClass.isAssignableFrom(resolvedType.getClass()) && valueClass.isAssignableFrom(value.getClass())) {
            return (T) value;
        } else if (value instanceof SimpleDefinedValue) {
            return resolve((SimpleDefinedValue) value);
        } else if (value instanceof AmbiguousValue) {
            var stringValue = CompilerUtils.resolveAmbiguousValue(value, StringValue.class);

            if (stringValue == null) {
                var characterStringListValue = resolveCharacterStringList(resolvedType, value);

                if (characterStringListValue != null) {
                    return characterStringListValue;
                }
            } else {
                var cString = getString(resolvedType, stringValue);

                return createValue(stringValue.getPosition(), cString);
            }
        } else if (value instanceof CollectionOfValue) {
            if (((CollectionOfValue) value).getValues().size() == 2) {
                return createValue(valuePosition,
                        verifyString(resolveTupleValue(((CollectionOfValue) value).toTuple()), valuePosition));
            } else if (((CollectionOfValue) value).getValues().size() == 4) {
                return createValue(value.getPosition(),
                        verifyString(resolveQuadrupleValue(((CollectionOfValue) value).toQuadruple()), valuePosition));
            }

            throw new ValueResolutionException(valuePosition, "Invalid value for type %s: %s", typeName,
                    ValueFormatter.formatValue(value));
        } else if (value instanceof StringValue) {
            var stringValue = (StringValue) value;
            var cString = getString(resolvedType, stringValue);

            return createValue(stringValue.getPosition(), cString);
        }

        throw new ValueResolutionException(valuePosition, "Failed to resolve a %s value", typeName);
    }

    protected String resolveTupleValue(TupleNode tuple) {
        throw new ValueResolutionException(tuple.getPosition(), "Tuple values not allowed for type %s: %s", typeName,
                ValueFormatter.formatValue(tuple));
    }

    protected String resolveQuadrupleValue(QuadrupleNode quadruple) {
        throw new ValueResolutionException(quadruple.getPosition(), "Quadruple values not allowed for type %s: %s",
                typeName, ValueFormatter.formatValue(quadruple));
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

    private Type verifyType(Type type, Position valuePosition) {
        var resolvedType = resolveTypeReference(type);

        if (!typeClass.isAssignableFrom(resolvedType.getClass())) {
            throw new ValueResolutionException(valuePosition, "Failed to resolve a value. Expected %s but found %s.",
                    typeName, type.getClass().getSimpleName());
        }

        return resolvedType;
    }

    private Type resolveTypeReference(Type type) {
        if (type instanceof TypeReference) {
            type = ctx.resolveTypeReference(type);
        }

        return type;
    }

    private String resolveCharSyms(Type type, Value value) {
        if (value instanceof StringValue) {
            return getString(type, (StringValue) value);
        } else if (value instanceof SimpleDefinedValue) {
            return resolve((SimpleDefinedValue) value).getValue();
        } else if (value instanceof CollectionOfValue) {
            return resolveGeneric(type, value).getValue();
        } else {
            throw new IllegalCompilerStateException("Unsupported value for %s: %s", typeName, value);
        }
    }

    private String getString(@SuppressWarnings("unused") Type type, StringValue stringValue) {
        return verifyString(stringValue.getCString(), stringValue.getPosition());
    }

    private String verifyString(String str, Position position) {
        verifier.verify(str).ifPresent(v -> {
            throw new ValueResolutionException(position, "%s contains invalid characters: %s", typeName, v);
        });

        return str;
    }

    protected abstract T createValue(Position position, String value);

}
