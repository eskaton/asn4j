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
import ch.eskaton.asn4j.parser.ast.ValueOrObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.DefinedValue;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Value;

public abstract class AbstractValueResolver<V> implements ValueResolver<V> {

    protected CompilerContext ctx;

    public AbstractValueResolver(CompilerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public V resolve(DefinedValue ref) {
        return resolve(ctx.resolveDefinedValue(ref));
    }

    public V resolve(SimpleDefinedValue value) {
        try {
            return ctx.tryResolveAllReferences(value).map(this::resolve).orElseThrow(this::error);
        } catch (Throwable throwable) {
            throw (RuntimeException) throwable;
        }
    }

    @Override
    public V resolve(String ref) {
        return resolve(ctx.resolveReference(ref));
    }

    @Override
    public V resolve(Type type, V value) {
        return value;
    }

    protected V resolve(ValueOrObjectAssignmentNode<?, ?> valueAssignment) {
        Type type = (Type) valueAssignment.getType();
        Value value = (Value) valueAssignment.getValue();

        return resolveGeneric(type, value);
    }

    protected <T extends RuntimeException> T error() {
        return (T) new CompilerException("Failed to resolve a value");
    }

}
