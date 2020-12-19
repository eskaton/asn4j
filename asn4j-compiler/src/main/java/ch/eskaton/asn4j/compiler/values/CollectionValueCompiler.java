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
import ch.eskaton.asn4j.compiler.parameters.Parameters;
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
        var typeName = getTypeName().getName();

        value = resolveValueFromObject(ctx, value);

        var maybeCollectionValue = getCollectionValue(value);

        if (maybeCollectionValue.isPresent()) {
            return resolveCollectionValue(ctx, typeName, compiledType, maybeCollectionValue.get());
        }

        throw invalidValueError(value);
    }

    private Optional<CollectionValue> getCollectionValue(Value value) {
        if (value instanceof EmptyValue) {
            return Optional.of(new CollectionValue(value.getPosition(), Collections.emptyList()));
        } else if (value instanceof AmbiguousValue) {
            return Optional.ofNullable(CompilerUtils.resolveAmbiguousValue(value, CollectionValue.class));
        } else if (value instanceof CollectionValue) {
            return Optional.of((CollectionValue) value);
        }

        return Optional.empty();
    }

    private CollectionValue resolveCollectionValue(CompilerContext ctx, String typeName, CompiledType compiledType,
            CollectionValue collectionValue) {
        var collection = getCollectionType(ctx, compiledType.getType());
        var componentTypes = collection.getAllComponents().stream()
                .map(ComponentType::getNamedType)
                .collect(Collectors.toMap(NamedType::getName, NamedType::getType));
        var values = collectionValue.getValues().stream()
                .map(v -> getNamedValue(ctx, typeName, componentTypes, v))
                .collect(Collectors.toList());

        collectionValue.getValues().clear();
        collectionValue.getValues().addAll(values);

        return collectionValue;
    }

    private NamedValue getNamedValue(CompilerContext ctx, String typeName, Map<String, Type> componentTypes,
            NamedValue namedValue) {
        var resolvedValue = (AbstractValue) getValue(ctx, typeName, componentTypes, namedValue);

        return new NamedValue(namedValue.getPosition(), namedValue.getName(), resolvedValue, resolvedValue.getType());
    }

    private Value getValue(CompilerContext ctx, String typeName, Map<String, Type> componentTypes,
            NamedValue value) {
        var componentType = getComponentType(componentTypes, value);
        var valueType = ctx.getValueType(componentType);

        return resolveComponent(ctx, typeName, componentType, valueType, value);
    }

    private Value resolveComponent(CompilerContext ctx, String typeName, Type componentType,
            Class<? extends Value> valueClass, NamedValue namedValue) {
        var value = namedValue.getValue();

        try {
            if (value instanceof AmbiguousValue) {
                var resolvedValue = CompilerUtils.resolveAmbiguousValue(value, valueClass);

                if (resolvedValue != null) {
                    value = resolvedValue;
                } else {
                    return resolveError(ctx, typeName, componentType, namedValue, value, Optional.empty());
                }
            }

            return ctx.getCompiledValue(componentType, value).getValue();
        } catch (ClassCastException | ValueResolutionException e) {
            return resolveError(ctx, typeName, componentType, namedValue, value, Optional.of(e));
        }
    }

    private Collection getCollectionType(CompilerContext ctx, Type type) {
        if (type instanceof TypeReference) {
            type = ctx.getCompiledType(type).getType();
        }

        if (type instanceof Collection collection) {
            return collection;
        }

        var typeName = getTypeName().getName();
        var formattedType = TypeFormatter.formatType(ctx, type);

        throw new ValueResolutionException(type.getPosition(),
                "Failed to resolve a %s value where a value of type %s is expected",
                typeName, formattedType);
    }

    private Type getComponentType(Map<String, Type> componentTypes, NamedValue value) {
        var componentName = value.getName();
        var componentType = componentTypes.get(componentName);

        if (componentType == null) {
            var typeName = getTypeName().getName();
            var componentNames = componentTypes.keySet().stream().collect(Collectors.joining(", "));

            throw new ValueResolutionException(value.getPosition(),
                    "%s value contains a component '%s' which isn't defined. Must be one of [%s]",
                    typeName, componentName, componentNames);
        }

        return componentType;
    }

    private Value resolveError(CompilerContext ctx, String typeName, Type componentType, NamedValue namedValue,
            Value value, Optional<Exception> e) {
        var message = "Failed to resolve value for component '%s' in a %s to type %s: %s";
        var formattedType = TypeFormatter.formatType(ctx, componentType);
        var formattedValue = ValueFormatter.formatValue(value);

        if (e.isPresent()) {
            throw new ValueResolutionException(message, e.get(), namedValue.getName(), typeName, formattedType,
                    formattedValue);
        } else {
            throw new ValueResolutionException(message, namedValue.getName(), typeName, formattedType, formattedValue);
        }
    }

}
