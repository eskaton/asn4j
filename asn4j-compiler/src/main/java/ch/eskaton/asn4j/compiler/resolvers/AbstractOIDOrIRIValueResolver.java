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
import ch.eskaton.asn4j.compiler.TypeName;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Value;

import static ch.eskaton.asn4j.compiler.CompilerUtils.resolveAmbiguousValue;

public abstract class AbstractOIDOrIRIValueResolver<T extends Type, V extends Value>
        extends DefaultValueResolver<T, V> {

    public AbstractOIDOrIRIValueResolver(CompilerContext ctx, Class<T> typeClass, Class<V> valueClass) {
        super(ctx, typeClass, valueClass);
    }

    public V resolveValue(CompilerContext ctx, Value value, Class<V> valueClass) {
        V idValue;

        if (valueClass.isAssignableFrom(value.getClass())) {
            idValue = (V) value;
        } else if ((idValue = resolveAmbiguousValue(value, valueClass)) != null) {
            // do nothing
        } else if ((value = resolveAmbiguousValue(value, SimpleDefinedValue.class)) != null) {
            idValue = ctx.resolveValue(valueClass, (SimpleDefinedValue) value);
        } else {
            throw new CompilerException("Invalid " + getTypeName() + " value: " + value);
        }

        return idValue;
    }

    protected abstract TypeName getTypeName();

}
