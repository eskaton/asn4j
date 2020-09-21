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

package ch.eskaton.asn4j.compiler.values;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerUtils;
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.compiler.Parameters;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.compiler.types.formatters.TypeFormatter;
import ch.eskaton.asn4j.compiler.values.formatters.ValueFormatter;
import ch.eskaton.asn4j.parser.Position;
import ch.eskaton.asn4j.parser.ast.Quadruple;
import ch.eskaton.asn4j.parser.ast.Tuple;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.AmbiguousValue;
import ch.eskaton.asn4j.parser.ast.values.CharacterStringList;
import ch.eskaton.asn4j.parser.ast.values.CollectionOfValue;
import ch.eskaton.asn4j.parser.ast.values.DefinedValue;
import ch.eskaton.asn4j.parser.ast.values.HasStringValue;
import ch.eskaton.asn4j.parser.ast.values.StringValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.types.TypeName;
import ch.eskaton.asn4j.runtime.verifiers.StringVerifier;

import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractStringValueCompiler<V extends HasStringValue & Value> extends AbstractValueCompiler<V> {

    private final TypeName typeName;

    private final Class<? extends Type> typeClass;

    private final Class<V> valueClass;

    private final StringVerifier verifier;

    public AbstractStringValueCompiler(TypeName typeName, Class<? extends Type> typeClass,
            Class<V> valueClass, StringVerifier verifier) {
        super(typeName, valueClass);

        this.typeName = typeName;
        this.typeClass = typeClass;
        this.valueClass = valueClass;
        this.verifier = verifier;
    }

    @Override
    public V doCompile(CompilerContext ctx, CompiledType compiledType, Value value,
            Optional<Parameters> maybeParameters) {
        var valuePosition = value.getPosition();
        var verifiedCompiledBaseType = verifyType(ctx, compiledType.getType(), valuePosition);

        if (typeClass.isAssignableFrom(verifiedCompiledBaseType.getClass()) &&
                valueClass.isAssignableFrom(value.getClass())) {
            return (V) value;
        } else if (value instanceof AmbiguousValue) {
            var stringValue = CompilerUtils.resolveAmbiguousValue(value, StringValue.class);

            if (stringValue == null) {
                var characterStringListValue = resolveCharacterStringList(ctx, verifiedCompiledBaseType, value);

                if (characterStringListValue != null) {
                    return characterStringListValue;
                }
            } else {
                var cString = getString(verifiedCompiledBaseType, stringValue);

                return createValue(stringValue.getPosition(), cString);
            }
        } else if (value instanceof CollectionOfValue collectionOfValue) {
            var elementCount = collectionOfValue.getValues().size();

            if (elementCount == 2) {
                var string = resolveTupleValue(collectionOfValue.toTuple());

                return createValue(valuePosition, verifyString(string, valuePosition));
            } else if (elementCount == 4) {
                var string = resolveQuadrupleValue(collectionOfValue.toQuadruple());

                return createValue(valuePosition, verifyString(string, valuePosition));
            }

            throw new ValueResolutionException(valuePosition, "Invalid value for type %s: %s", typeName,
                    ValueFormatter.formatValue(value));
        } else if (value instanceof StringValue) {
            var stringValue = (StringValue) value;
            var cString = getString(verifiedCompiledBaseType, stringValue);

            return createValue(stringValue.getPosition(), cString);
        }

        throw new ValueResolutionException(valuePosition, "Failed to resolve a %s value", typeName);
    }

    protected String resolveTupleValue(Tuple tuple) {
        throw new ValueResolutionException(tuple.getPosition(), "Tuple values not allowed for type %s: %s", typeName,
                ValueFormatter.formatValue(tuple));
    }

    protected String resolveQuadrupleValue(Quadruple quadruple) {
        throw new ValueResolutionException(quadruple.getPosition(), "Quadruple values not allowed for type %s: %s",
                typeName, ValueFormatter.formatValue(quadruple));
    }

    protected V resolveCharacterStringList(CompilerContext ctx, Type resolvedType, Value value) {
        var resolvedValue = CompilerUtils.resolveAmbiguousValue(value, CharacterStringList.class);

        if (resolvedValue != null) {
            return createValue(value.getPosition(), resolvedValue.getValues()
                    .stream()
                    .map(v -> resolveCharSyms(ctx, resolvedType, v))
                    .collect(Collectors.joining()));
        }

        return null;
    }

    private Type verifyType(CompilerContext ctx, Type type, Position valuePosition) {
        var compiledBaseType = ctx.getCompiledBaseType(type);

        if (!typeClass.isAssignableFrom(compiledBaseType.getType().getClass())) {
            throw new ValueResolutionException(valuePosition, "Failed to resolve a value. Expected %s but found %s.",
                    typeName, type.getClass().getSimpleName());
        }

        return compiledBaseType.getType();
    }

    private String resolveCharSyms(CompilerContext ctx, Type type, Value value) {
        if (value instanceof StringValue) {
            return getString(type, (StringValue) value);
        } else if (value instanceof DefinedValue) {
            var stringValue = ctx.getCompiledValue(((DefinedValue) value)).getValue();

            if (stringValue.getClass().isAssignableFrom(valueClass)) {
                return ((HasStringValue) stringValue).getValue();
            }

            var nodeName = value.getClass().getSimpleName();
            var formattedValue = ValueFormatter.formatValue(value);
            var formattedType = TypeFormatter.formatType(ctx, type);

            throw new ValueResolutionException(value.getPosition(),
                    "%s '%s' resolved to '%s' but a value of type '%s' was expected",
                    nodeName, formattedValue, stringValue, formattedType);
        } else if (value instanceof CollectionOfValue) {
            var compiledValue = ctx.getCompiledValue(type, value);

            return ((HasStringValue) compiledValue.getValue()).getValue();
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

    protected abstract V createValue(Position position, String value);

}
