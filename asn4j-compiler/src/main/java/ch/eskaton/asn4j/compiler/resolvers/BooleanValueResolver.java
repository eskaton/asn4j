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
import ch.eskaton.asn4j.compiler.ValueResolutionException;
import ch.eskaton.asn4j.compiler.utils.ValueFormatter;
import ch.eskaton.asn4j.parser.ast.types.BooleanType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.values.BooleanValue;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.types.TypeName;

public class BooleanValueResolver extends AbstractValueResolver<BooleanValue> {

    public BooleanValueResolver(CompilerContext ctx) {
        super(ctx);
    }

    @Override
    public BooleanValue resolveGeneric(Type type, Value value) {
        if (type instanceof BooleanType) {
            if (value instanceof SimpleDefinedValue) {
                return ctx.tryResolveAllValueReferences((SimpleDefinedValue) value).map(this::resolve).orElse(null);
            } else if (value instanceof BooleanValue) {
                return (BooleanValue) value;
            }
        } else if (type instanceof TypeReference) {
            return resolveGeneric(ctx.resolveTypeReference(type), value);
        }

        throw new ValueResolutionException(value.getPosition(), "Failed to resolve a %s value: %s", TypeName.BOOLEAN,
                ValueFormatter.formatValue(value));
    }

}
