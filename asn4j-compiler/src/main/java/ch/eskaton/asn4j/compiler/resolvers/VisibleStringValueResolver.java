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
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.AmbiguousValue;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.StringValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.parser.ast.values.VisibleStringValue;
import ch.eskaton.asn4j.runtime.types.TypeName;
import ch.eskaton.asn4j.runtime.verifiers.ISO646Verifier;

public class VisibleStringValueResolver extends AbstractValueResolver<VisibleStringValue> {

    public static final ISO646Verifier VERIFIER = new ISO646Verifier();

    public VisibleStringValueResolver(CompilerContext ctx) {
        super(ctx);
    }

    @Override
    public VisibleStringValue resolveGeneric(Type type, Value value) {
        if (value instanceof SimpleDefinedValue) {
            value = ctx.tryResolveAllReferences((SimpleDefinedValue) value).map(this::resolve).orElse(null);
        } else if (value instanceof AmbiguousValue) {
            value = CompilerUtils.resolveAmbiguousValue(value, StringValue.class);
        } else if (value instanceof StringValue) {
            // value is already a string
        } else {
            throw new CompilerException("Failed to resolve a %s value", TypeName.VISIBLE_STRING);
        }

        var stringValue = (StringValue) value;
        var cString = stringValue.getCString();

        VERIFIER.verify(cString).ifPresent(v -> {
            throw new CompilerException("%s contains invalid characters: %s", TypeName.VISIBLE_STRING, v);
        });

        return new VisibleStringValue(stringValue.getPosition(), cString);
    }

}