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

package ch.eskaton.asn4j.parser.ast;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.parser.Position;
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.utils.ToString;

import java.math.BigInteger;
import java.util.Objects;

import static ch.eskaton.asn4j.parser.NoPosition.NO_POSITION;

public class EndpointNode extends AbstractNode {

    private Value value;

    private boolean inclusive;

    public EndpointNode(Position position, Value value, boolean inclusive) {
        super(position);

        this.value = value;
        this.inclusive = inclusive;
    }

    public EndpointNode(Value value, boolean inclusive) {
        super(NO_POSITION);

        this.value = value;
        this.inclusive = inclusive;
    }

    public Value getValue() {
        return value;
    }

    public boolean isInclusive() {
        return inclusive;
    }

    public IntegerValue getLowerEndPointValue(CompilerContext ctx, long bound) {
        return canonicalizeLowerEndpoint(ctx, this, bound);
    }

    public IntegerValue getUpperEndPointValue(CompilerContext ctx, long bound) {
        return canonicalizeUpperEndpoint(ctx, this, bound);
    }

    static IntegerValue canonicalizeLowerEndpoint(CompilerContext ctx, EndpointNode node, long bound) {
        return canonicalizeEndpoint(ctx, node, true, bound);
    }

    static IntegerValue canonicalizeUpperEndpoint(CompilerContext ctx, EndpointNode node, long bound) {
        return canonicalizeEndpoint(ctx, node, false, bound);
    }

    /**
     * Canonicalizes an {@link EndpointNode}, i.e. resolves MIN and MAX values
     * and converts the value to inclusive.
     *
     * @param ctx     The compiler context
     * @param node    An {@link EndpointNode}
     * @param isLower true, if it's a lower {@link EndpointNode}
     * @return a canonical {@link EndpointNode}
     */
    private static IntegerValue canonicalizeEndpoint(CompilerContext ctx, EndpointNode node, boolean isLower, long bound) {
        var value = node.getValue();
        var inclusive = node.isInclusive();

        if (Value.MAX.equals(value)) {
            return new IntegerValue(inclusive ? bound : bound - 1);
        } else if (Value.MIN.equals(value)) {
            return new IntegerValue(inclusive ? bound : bound + 1);
        } else {
            var intValue = ctx.<IntegerValue>getValue(new IntegerType(NO_POSITION), value);

            if (inclusive) {
                return intValue;
            }

            return new IntegerValue(
                    isLower ? (intValue).getValue().add(BigInteger.ONE)
                            : (intValue).getValue().subtract(BigInteger.ONE));
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(inclusive, value);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        EndpointNode that = (EndpointNode) other;

        return inclusive == that.inclusive && Objects.equals(value, that.value);
    }

    @Override
    public String toString() {
        return ToString.get(this);
    }

}
