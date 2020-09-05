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
import ch.eskaton.asn4j.compiler.ValueResolutionException;
import ch.eskaton.asn4j.compiler.utils.ValueFormatter;
import ch.eskaton.asn4j.parser.ast.ValueAssignmentNode;
import ch.eskaton.asn4j.parser.ast.types.Collection;
import ch.eskaton.asn4j.parser.ast.types.ComponentType;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.asn4j.parser.ast.types.SetOfType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.values.AbstractValue;
import ch.eskaton.asn4j.parser.ast.values.AmbiguousValue;
import ch.eskaton.asn4j.parser.ast.values.CollectionValue;
import ch.eskaton.asn4j.parser.ast.values.EmptyValue;
import ch.eskaton.asn4j.parser.ast.values.NamedValue;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.CompilerUtils.formatTypeName;
import static ch.eskaton.asn4j.compiler.CompilerUtils.formatValue;

public class CollectionValueResolver extends AbstractValueResolver<CollectionValue> {

    public CollectionValueResolver(CompilerContext ctx) {
        super(ctx);
    }

    @Override
    protected CollectionValue resolve(ValueAssignmentNode valueAssignment) {
        Type type = valueAssignment.getType();
        Value value = valueAssignment.getValue();

        return resolveGeneric(type, value);
    }

    @Override
    public CollectionValue resolveGeneric(Type type, Value value) {
        if (value instanceof SimpleDefinedValue) {
            return resolve((SimpleDefinedValue) value);
        }

        CollectionValue collectionValue = null;
        String typeName = getTypeName(type);

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
            Map<String, Type> elementTypes = ((Collection) type).getAllComponents().stream()
                    .map(ComponentType::getNamedType)
                    .collect(Collectors.toMap(NamedType::getName, NamedType::getType));

            List<? extends NamedValue> values = collectionValue.getValues().stream()
                    .map(v -> {
                        AbstractValue resolvedValue = (AbstractValue) getValue(typeName, elementTypes, v);

                        return new NamedValue(v.getPosition(), v.getName(), resolvedValue, resolvedValue.getType());
                    })
                    .collect(Collectors.toList());

            collectionValue.getValues().clear();
            collectionValue.getValues().addAll(values);

            return collectionValue;
        }

        throw error(typeName, value);
    }

    private Value getValue(String typeName, Map<String, Type> elementTypes, NamedValue value) {
        var elementType = getElementType(elementTypes, value);
        var valueType = ctx.getValueType(elementType);

        return resolveElement(typeName, elementType, valueType, value);
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

    private Value resolveElement(String typeName, Type elementType, Class<? extends Value> valueClass, NamedValue namedValue) {
        var value = namedValue.getValue();

        try {
            if (value instanceof AmbiguousValue) {
                Value resolvedValue = CompilerUtils.resolveAmbiguousValue(value, valueClass);

                if (resolvedValue != null) {
                    value = resolvedValue;
                } else {
                    return resolveError(typeName, elementType, namedValue, value, Optional.empty());
                }
            }

            return ctx.resolveGenericValue(valueClass, elementType, value);
        } catch (ClassCastException | ValueResolutionException e) {
            return resolveError(typeName, elementType, namedValue, value, Optional.of(e));
        }
    }

    private String getTypeName(Type type) {
        if (type instanceof SetOfType) {
            return TypeName.SET.name();
        }

        return TypeName.SEQUENCE.name();
    }

    private Value resolveError(String typeName, Type elementType, NamedValue namedValue, Value value,
            Optional<Exception> e) {
        final var message = "Failed to resolve value for component '%s' in a %s to type %s: %s";

        if (e.isPresent()) {
            throw new ValueResolutionException(message, e.get(), namedValue.getName(), typeName,
                    formatTypeName(elementType), formatValue(value));
        } else {
            throw new ValueResolutionException(message, namedValue.getName(), typeName, formatTypeName(elementType),
                    formatValue(value));
        }
    }

    protected ValueResolutionException error(String typeName, Value value) {
        return new ValueResolutionException(value.getPosition(), "Failed to resolve a %s value: %s", typeName,
                ValueFormatter.formatValue(value));
    }

}
