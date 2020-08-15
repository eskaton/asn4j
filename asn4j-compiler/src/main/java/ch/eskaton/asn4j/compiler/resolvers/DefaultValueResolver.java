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
import ch.eskaton.asn4j.parser.ast.Node;
import ch.eskaton.asn4j.parser.ast.ValueAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ValueOrObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.AmbiguousValue;
import ch.eskaton.asn4j.parser.ast.values.DefinedValue;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.Assert;

import java.util.Optional;

public class DefaultValueResolver<T extends Type, V extends Value> extends AbstractValueResolver<V> {

    private Class<T> typeClass;

    private Class<V> valueClass;

    public DefaultValueResolver(CompilerContext ctx, Class<T> typeClass, Class<V> valueClass) {
        super(ctx);

        this.typeClass = typeClass;
        this.valueClass = valueClass;
    }

    @Override
    protected V resolve(ValueAssignmentNode valueAssignment) {
        Node type = valueAssignment.getType();
        Node value = valueAssignment.getValue();

        if (!(type instanceof Type)) {
            throw new CompilerException(type.getPosition(), "Invalid type %s", type.getClass().getSimpleName());
        }

        type = ctx.resolveTypeReference((Type) type);

        if (typeClass.isAssignableFrom(type.getClass())) {
            if (DefinedValue.class.isAssignableFrom(value.getClass())) {
                return resolve(ctx.resolveDefinedValue((DefinedValue) value));
            }

            var resolvedValue = CompilerUtils.resolveAmbiguousValue(value, valueClass);

            if (resolvedValue == null) {
                throw new CompilerException(value.getPosition(), "Expected a value of type %s but found %s",
                        valueClass.getSimpleName(), value.getClass().getSimpleName());
            }

            return resolvedValue;
        }

        throw new CompilerException(type.getPosition(), "Failed to resolve a value of type %s. Found type %s",
                typeClass.getSimpleName(), type.getClass().getSimpleName());
    }

    @Override
    @SuppressWarnings("java:S2259")
    public V resolveGeneric(Type type, Value value) {
        Assert.notNull(value, "value");

        var resolvedValue = value;

        if (value instanceof SimpleDefinedValue) {
            resolvedValue = ctx.tryResolveAllValueReferences((SimpleDefinedValue) value).map(this::resolve).orElse(null);
        } else if (value instanceof AmbiguousValue) {
            resolvedValue = CompilerUtils.resolveAmbiguousValue(value, valueClass);
        }

        if (resolvedValue == null || !valueClass.isAssignableFrom(resolvedValue.getClass())) {
            throw new IllegalCompilerStateException("Value class %s is not an instance of %s. resolveGeneric() must be overridden.",
                    value.getClass().getSimpleName(), valueClass.getSimpleName());
        }

        return resolve(Optional.of(type), (V) resolvedValue);
    }

}
