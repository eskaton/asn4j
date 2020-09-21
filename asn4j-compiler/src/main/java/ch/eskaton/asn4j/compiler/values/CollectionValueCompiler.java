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
import ch.eskaton.asn4j.parser.ast.types.Collection;
import ch.eskaton.asn4j.parser.ast.types.ComponentType;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.values.AbstractValue;
import ch.eskaton.asn4j.parser.ast.values.AmbiguousValue;
import ch.eskaton.asn4j.parser.ast.values.CollectionValue;
import ch.eskaton.asn4j.parser.ast.values.EmptyValue;
import ch.eskaton.asn4j.parser.ast.values.NamedValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CollectionValueCompiler extends AbstractValueCompiler<CollectionValue> {

    public CollectionValueCompiler(TypeName typeName, Class<CollectionValue> valueClass) {
        super(typeName, valueClass);
    }

    @Override
    public CollectionValue doCompile(CompilerContext ctx, CompiledType compiledType, Value value,
            Optional<Parameters> maybeParameters) {
        CollectionValue collectionValue = null;
        var typeName = getTypeName().getName();
        var type = compiledType.getType();

        if (value instanceof EmptyValue) {
            return new CollectionValue(value.getPosition(), Collections.emptyList());
        } else if (value instanceof AmbiguousValue) {
            collectionValue = CompilerUtils.resolveAmbiguousValue(value, CollectionValue.class);
        } else if (value instanceof CollectionValue) {
            collectionValue = (CollectionValue) value;
        }

        if (type instanceof TypeReference) {
            type = ctx.resolveTypeReference(type);
        }

        if (collectionValue != null) {
            var elementTypes = ((Collection) type).getAllComponents().stream()
                    .map(ComponentType::getNamedType)
                    .collect(Collectors.toMap(NamedType::getName, NamedType::getType));

            var values = collectionValue.getValues().stream()
                    .map(v -> {
                        var resolvedValue = (AbstractValue) getValue(ctx, typeName, elementTypes, v);

                        return new NamedValue(v.getPosition(), v.getName(), resolvedValue, resolvedValue.getType());
                    })
                    .collect(Collectors.toList());

            collectionValue.getValues().clear();
            collectionValue.getValues().addAll(values);

            return collectionValue;
        }

        throw invalidValueError(collectionValue != null ? collectionValue : value);
    }

    private Value getValue(CompilerContext ctx, String typeName, Map<String, Type> elementTypes, NamedValue value) {
        var elementType = getElementType(elementTypes, value);
        var valueType = ctx.getValueType(elementType);

        return resolveElement(ctx, typeName, elementType, valueType, value);
    }

    private Type getElementType(Map<String, Type> elementTypes, NamedValue value) {
        Type elementType = elementTypes.get(value.getName());

        if (elementType == null) {
            throw new ValueResolutionException(value.getPosition(),
                    "SEQUENCE value contains a component '%s' which isn't defined. Must be one of [%s]",
                    value.getName(), elementTypes.keySet().stream().collect(Collectors.joining(", ")));
        }

        return elementType;
    }

    private Value resolveElement(CompilerContext ctx, String typeName, Type elementType,
            Class<? extends Value> valueClass, NamedValue namedValue) {
        var value = namedValue.getValue();

        try {
            if (value instanceof AmbiguousValue) {
                var resolvedValue = CompilerUtils.resolveAmbiguousValue(value, valueClass);

                if (resolvedValue != null) {
                    value = resolvedValue;
                } else {
                    return resolveError(ctx, typeName, elementType, namedValue, value, Optional.empty());
                }
            }

            return ctx.getCompiledValue(elementType, value).getValue();
        } catch (ClassCastException | ValueResolutionException e) {
            return resolveError(ctx, typeName, elementType, namedValue, value, Optional.of(e));
        }
    }

    private Value resolveError(CompilerContext ctx, String typeName, Type elementType, NamedValue namedValue,
            Value value, Optional<Exception> e) {
        var message = "Failed to resolve value for component '%s' in a %s to type %s: %s";
        var formattedType = TypeFormatter.formatType(ctx, elementType);
        var formattedValue = ValueFormatter.formatValue(value);

        if (e.isPresent()) {
            throw new ValueResolutionException(message, e.get(), namedValue.getName(), typeName, formattedType,
                    formattedValue);
        } else {
            throw new ValueResolutionException(message, namedValue.getName(), typeName, formattedType, formattedValue);
        }
    }

}
