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
import ch.eskaton.asn4j.parser.ast.Node;
import ch.eskaton.asn4j.parser.ast.ValueOrObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.Value;

public class DefaultValueResolver<T extends Type, V extends Value> extends AbstractValueResolver<V> {

    private Class<T> typeClass;

    private Class<V> valueClass;

    public DefaultValueResolver(CompilerContext ctx, Class<T> typeClass, Class<V> valueClass) {
        super(ctx);

        this.typeClass = typeClass;
        this.valueClass = valueClass;
    }

    @Override
    protected V resolve(ValueOrObjectAssignmentNode<?, ?> valueAssignment) {
        Node type = valueAssignment.getType();
        Node value = valueAssignment.getValue();

        type = ctx.resolveTypeReference(type);

        if (typeClass.isAssignableFrom(type.getClass())) {
            value = CompilerUtils.resolveAmbiguousValue(value, valueClass);

            if (!(valueClass.isAssignableFrom(value.getClass()))) {
                throw new CompilerException("Expected a value of type %s but found %s", valueClass.getSimpleName(),
                        value.getClass().getSimpleName());
            }

            return (V) value;
        }

        throw new CompilerException("Failed to resolve a value of type %s. Found type %s", typeClass.getSimpleName(),
                type.getClass().getSimpleName());
    }

    @Override
    public V resolveGeneric(Type type, Value value) {
        if (!valueClass.isAssignableFrom(value.getClass())) {
            throw new IllegalStateException("Value class " + value.getClass().getSimpleName() + " is not an instance of "
                    + valueClass.getClass().getSimpleName()+ ". resolveGeneric() must be overridden." );
        }

        return ctx.resolveValue(valueClass, type, (V) value);
    }

}
