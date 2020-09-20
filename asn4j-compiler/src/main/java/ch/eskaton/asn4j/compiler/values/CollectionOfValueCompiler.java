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
import ch.eskaton.asn4j.parser.ast.types.CollectionOfType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.AmbiguousValue;
import ch.eskaton.asn4j.parser.ast.values.CollectionOfValue;
import ch.eskaton.asn4j.parser.ast.values.EmptyValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.CompilerUtils.formatTypeName;
import static ch.eskaton.asn4j.compiler.CompilerUtils.formatValue;

public class CollectionOfValueCompiler extends AbstractValueCompiler<CollectionOfValue> {

    public CollectionOfValueCompiler(TypeName typeName) {
        super(typeName, CollectionOfValue.class);
    }

    @Override
    public CollectionOfValue doCompile(CompilerContext ctx, CompiledType compiledType, Value value,
            Optional<Parameters> maybeParameters) {
        var baseType = ctx.getCompiledBaseType(compiledType.getType()).getType();
        CollectionOfValue collectionOfValue = null;

        if (value instanceof EmptyValue) {
            return new CollectionOfValue(value.getPosition(), Collections.emptyList());
        } else if (value instanceof AmbiguousValue) {
            // TODO does this ever occur?
            collectionOfValue = CompilerUtils.resolveAmbiguousValue(value, CollectionOfValue.class);
        } else if (value instanceof CollectionOfValue) {
            collectionOfValue = (CollectionOfValue) value;
        }

        if (collectionOfValue != null) {
            var elementType = ((CollectionOfType) baseType).getType();
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
        try {
            if (value instanceof AmbiguousValue) {
                var resolvedValue = CompilerUtils.resolveAmbiguousValue(value, valueClass);

                if (resolvedValue != null) {
                    value = resolvedValue;
                } else {
                    throw new ValueResolutionException("Failed to resolve a value in a %s to type %s: %s",
                            getTypeName().getName(), formatTypeName(elementType), formatValue(value));
                }
            }

            return new ValueCompiler().compile(ctx, null, elementType, value, Optional.empty()).getValue();
        } catch (ClassCastException e) {
            throw new ValueResolutionException("Failed to resolve a value in a %s to type %s: %s",
                    getTypeName().getName(), formatTypeName(elementType), formatValue(value));
        }
    }

}
