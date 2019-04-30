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

package ch.eskaton.asn4j.compiler.constraints;

import ch.eskaton.asn4j.parser.ast.values.BitStringValue;

import java.util.Objects;
import java.util.Set;

public class BitStringConstraintValues extends SetConstraintValues<BitStringValue, BitStringConstraintValues> {

    private boolean inverted;

    public BitStringConstraintValues() {
        super();
    }

    public BitStringConstraintValues(Set<BitStringValue> values) {
        super(values);
    }

    boolean isInverted() {
        return inverted;
    }

    void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    BitStringConstraintValues inverted(boolean inverted) {
        setInverted(inverted);

        return this;
    }

    public BitStringConstraintValues intersection(BitStringConstraintValues values) {
        BitStringConstraintValues copy;

        if (inverted && values.isInverted()) {
            copy = copy();
            copy.getValues().addAll(values.getValues());
        } else if (!inverted && !values.isInverted()) {
            copy = copy();
            copy.getValues().retainAll(values.getValues());
        } else if (inverted) {
            copy = values.copy();
            copy.getValues().removeAll(getValues());
        } else {
            copy = copy();
            copy.getValues().removeAll(values.getValues());
        }

        return copy;
    }

    public BitStringConstraintValues union(BitStringConstraintValues values) {
        BitStringConstraintValues copy = copy();

        if (inverted && values.isInverted()) {
            copy.getValues().retainAll(values.getValues());
        } else if (!inverted && !values.isInverted()) {
            copy.getValues().addAll(values.getValues());
        } else if (inverted) {
            copy.getValues().removeAll(values.getValues());
        } else {
            copy.getValues().removeAll(getValues());
        }

        return copy;
    }

    @Override
    public BitStringConstraintValues invert() {
        return copy().inverted(true);
    }

    @Override
    public BitStringConstraintValues copy() {
        return new BitStringConstraintValues(getValues());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        if (!super.equals(other)) {
            return false;
        }

        BitStringConstraintValues that = (BitStringConstraintValues) other;

        return inverted == that.inverted;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), inverted);
    }

}
