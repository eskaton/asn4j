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
import ch.eskaton.asn4j.parser.ast.AssignmentNode;
import ch.eskaton.asn4j.parser.ast.Node;
import ch.eskaton.asn4j.parser.ast.TypeAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ValueOrObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
import ch.eskaton.asn4j.parser.ast.types.RelativeOID;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.NamedNumber;
import ch.eskaton.asn4j.parser.ast.values.RelativeOIDValue;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;

import java.math.BigInteger;

import static ch.eskaton.asn4j.compiler.CompilerUtils.resolveAmbiguousValue;

public class IntegerValueResolver extends AbstractValueResolver<BigInteger> {

    public IntegerValueResolver(CompilerContext ctx) {
        super(ctx);
    }

    @Override
    protected BigInteger resolve(ValueOrObjectAssignmentNode<?, ?> valueAssignment) throws CompilerException {
        Node type = valueAssignment.getType();
        Node value = valueAssignment.getValue();

        if (type instanceof IntegerType) {
            if (!(value instanceof IntegerValue)) {
                throw new CompilerException("Integer expected");
            }
            return ((IntegerValue) value).getValue();
        } else if (type instanceof TypeReference) {
            if (resolveAmbiguousValue(value, SimpleDefinedValue.class) != null) {
                value = resolveAmbiguousValue(value, SimpleDefinedValue.class);
                AssignmentNode assignment = ctx.getModule().getBody().getAssignments(((TypeReference) type).getType());

                if (assignment != null && assignment instanceof TypeAssignmentNode) {
                    TypeAssignmentNode typeAssignment = (TypeAssignmentNode) assignment;

                    if (typeAssignment.getType() instanceof IntegerType) {
                        NamedNumber namedNumber = ((IntegerType) typeAssignment.getType())
                                .getNamedNumber(((SimpleDefinedValue) value).getValue());

                        if (namedNumber.getValue() != null) {
                            return namedNumber.getValue().getNumber();
                        } else {
                            return ctx.resolveValue(BigInteger.class, namedNumber.getRef());
                        }
                    }
                }
            } else if (value instanceof IntegerValue) {
                return ((IntegerValue) value).getValue();
            }
        }

        throw new CompilerException("Failed to resolve an integer value");
    }

}
