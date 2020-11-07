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
import ch.eskaton.asn4j.compiler.Parameters;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.compiler.types.formatters.TypeFormatter;
import ch.eskaton.asn4j.compiler.values.formatters.ValueFormatter;
import ch.eskaton.asn4j.parser.ast.types.CollectionOfType;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.AmbiguousValue;
import ch.eskaton.asn4j.parser.ast.values.CollectionOfValue;
import ch.eskaton.asn4j.parser.ast.values.CollectionValue;
import ch.eskaton.asn4j.parser.ast.values.EmptyValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class CollectionOfValueCompiler extends AbstractValueCompiler<CollectionOfValue> {

    public CollectionOfValueCompiler(TypeName typeName) {
        super(typeName, CollectionOfValue.class);
    }

    @Override
    public CollectionOfValue doCompile(CompilerContext ctx, CompiledType compiledType, Value value,
            Optional<Parameters> maybeParameters) {
        var baseType = ctx.getCompiledBaseType(compiledType.getType()).getType();
        var elementType = ((CollectionOfType) baseType).getType();
        CollectionOfValue collectionOfValue = null;

        value = resolveValueFromObject(ctx, value);

        if (value instanceof EmptyValue) {
            return new CollectionOfValue(value.getPosition(), Collections.emptyList());
        } else if (value instanceof AmbiguousValue) {
            collectionOfValue = CompilerUtils.resolveAmbiguousValue(value, CollectionOfValue.class);
        } else if (value instanceof CollectionOfValue) {
            collectionOfValue = (CollectionOfValue) value;
        } else if (value instanceof CollectionValue collectionValue) {
            var maybeElementName = elementType instanceof NamedType namedType ?
                    Optional.of(namedType.getName()) :
                    Optional.empty();
            var values = collectionValue.getValues().stream().map(namedValue -> {
                var name = namedValue.getName();

                if (maybeElementName.isEmpty() ||
                        maybeElementName.isPresent() && !maybeElementName.get().equals(name)) {
                    var formattedValue = ValueFormatter.formatValue(namedValue);
                    var formattedType = TypeFormatter.formatType(ctx, baseType);

                    throw new ValueResolutionException(namedValue.getPosition(),
                            "The value '%s' references a named component in '%s' that doesn't exist",
                            formattedValue, formattedType);
                }

                return namedValue.getValue();
            }).collect(Collectors.toList());

            collectionOfValue = new CollectionOfValue(collectionValue.getPosition(), values);
        }

        if (collectionOfValue != null) {
            var valueClass = ctx.getValueType(elementType);

            var values = collectionOfValue.getValues().stream()
                    .map(v -> resolveElement(ctx, elementType, valueClass, v))
                    .collect(Collectors.toList());

            collectionOfValue.getValues().clear();
            collectionOfValue.getValues().addAll(values);

            return collectionOfValue;
        }

        throw invalidValueError(value);
    }

    private Value resolveElement(CompilerContext ctx, Type elementType, Class<? extends Value> valueClass,
            Value value) {
        if (value instanceof AmbiguousValue) {
            var resolvedValue = CompilerUtils.resolveAmbiguousValue(value, valueClass);

            if (resolvedValue != null) {
                value = resolvedValue;
            } else {
                var formattedValue = ValueFormatter.formatValue(value);
                var formattedType = TypeFormatter.formatType(ctx, elementType);

                throw new ValueResolutionException(value.getPosition(),
                        "Failed to resolve a value in a %s to type %s: %s",
                        getTypeName().getName(), formattedType, formattedValue);
            }
        }

        return ctx.getCompiledValue(elementType, value).getValue();
    }

}
