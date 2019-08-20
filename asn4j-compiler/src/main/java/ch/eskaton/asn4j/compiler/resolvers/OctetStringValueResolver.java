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
import ch.eskaton.asn4j.parser.ast.Node;
import ch.eskaton.asn4j.parser.ast.ValueOrObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.values.AbstractBaseXStringValue;
import ch.eskaton.asn4j.parser.ast.values.OctetStringValue;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Value;

import static ch.eskaton.asn4j.compiler.CompilerUtils.resolveAmbiguousValue;

public class OctetStringValueResolver extends AbstractValueResolver<OctetStringValue> {

    public OctetStringValueResolver(CompilerContext ctx) {
        super(ctx);
    }

    @Override
    protected OctetStringValue resolve(ValueOrObjectAssignmentNode<?, ?> valueAssignment) {
        Node type = valueAssignment.getType();
        Node value = valueAssignment.getValue();

        if (type instanceof OctetStringValue) {
            if (value instanceof AbstractBaseXStringValue) {
                return ((AbstractBaseXStringValue) value).toOctetString();
            } else if (value instanceof OctetStringValue) {
                return (OctetStringValue) value;
            }

            throw new CompilerException("OCTET STRING value expected");
        } else if (type instanceof TypeReference) {
            OctetStringValue octetStringValue = resolveAmbiguousValue(value, OctetStringValue.class);

            if (octetStringValue != null) {
                return resolve((Type) type, octetStringValue);
            }
        }

        throw new CompilerException("Failed to resolve a OCTET STRING value");
    }

    @Override
    public OctetStringValue resolveGeneric(Type type, Value value) {
        OctetStringValue octetStringValue = null;

        if (value instanceof AbstractBaseXStringValue) {
            octetStringValue = ((AbstractBaseXStringValue) value).toOctetString();
        } else {
            if (resolveAmbiguousValue(value, SimpleDefinedValue.class) != null) {
                octetStringValue = ctx.resolveValue(OctetStringValue.class,
                        resolveAmbiguousValue(value, SimpleDefinedValue.class));
            } else if (resolveAmbiguousValue(value, OctetStringValue.class) != null) {
                octetStringValue = ctx.resolveValue(OctetStringValue.class, type,
                        resolveAmbiguousValue(value, OctetStringValue.class));
            }
        }

        return octetStringValue;
    }

}
