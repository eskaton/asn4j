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
import ch.eskaton.asn4j.compiler.TypeName;
import ch.eskaton.asn4j.parser.ast.ValueOrObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.types.Collection;
import ch.eskaton.asn4j.parser.ast.types.ComponentType;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.asn4j.parser.ast.types.SetOfType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.AmbiguousValue;
import ch.eskaton.asn4j.parser.ast.values.CollectionValue;
import ch.eskaton.asn4j.parser.ast.values.EmptyValue;
import ch.eskaton.asn4j.parser.ast.values.NamedValue;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Value;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.CompilerUtils.formatTypeName;
import static ch.eskaton.asn4j.compiler.CompilerUtils.formatValue;

public class CollectionValueResolver extends AbstractValueResolver<CollectionValue> {

    public CollectionValueResolver(CompilerContext ctx) {
        super(ctx);
    }

    @Override
    protected CollectionValue resolve(ValueOrObjectAssignmentNode<?, ?> valueAssignment) {
        Type type = (Type) valueAssignment.getType();
        Value value = (Value) valueAssignment.getValue();

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

        if (collectionValue != null) {
            Map<String, Type> elementTypes = ((Collection) type).getAllComponents().stream()
                    .map(ComponentType::getNamedType)
                    .collect(Collectors.toMap(NamedType::getName, NamedType::getType));

            List<? extends NamedValue> values = collectionValue.getValues().stream()
                    .map(v -> new NamedValue(v.getPosition(), v.getName(),
                            resolveElement(typeName, elementTypes.get(v.getName()),
                                    ctx.getValueType(elementTypes.get(v.getName())), v.getValue())))
                    .collect(Collectors.toList());

            collectionValue.getValues().clear();
            collectionValue.getValues().addAll(values);

            return collectionValue;
        }

        throw error(typeName);
    }

    private Value resolveElement(String typeName, Type elementType, Class<? extends Value> valueClass, Value value) {
        try {
            if (value instanceof AmbiguousValue) {
                Value resolvedValue = CompilerUtils.resolveAmbiguousValue(value, valueClass);

                if (resolvedValue != null) {
                    value = resolvedValue;
                } else {
                    throw new CompilerException("Failed to resolve a value in a " + typeName + " to type %s: %s",
                            formatTypeName(elementType), formatValue(value));
                }
            }

            return ctx.resolveGenericValue(valueClass, elementType, value);
        } catch (ClassCastException e) {
            throw new CompilerException("Failed to resolve a value in a " + typeName + " to type %s: %s",
                    formatTypeName(elementType), formatValue(value));
        }
    }

    private String getTypeName(Type type) {
        if (type instanceof SetOfType) {
            return TypeName.SET.name();
        }

        return TypeName.SEQUENCE.name();
    }

    protected CompilerException error(String typeName) {
        return new CompilerException("Failed to resolve a " + typeName + " value");
    }

}
