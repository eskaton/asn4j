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

import java.util.Objects;

public class BitStringConstraint implements GenericConstraint<BitStringConstraint> {

    private BitStringValueConstraint values;

    private BitStringSizeConstraint sizes;

    private boolean inverted;

    public BitStringConstraint(BitStringValueConstraint values) {
        this.values = values;
        this.sizes = new BitStringSizeConstraint();
        this.inverted = values.isInverted();
    }

    public BitStringConstraint(BitStringValueConstraint values, BitStringSizeConstraint sizes) {
        this.values = values;
        this.sizes = sizes;
    }

    public BitStringConstraint() {
        values = new BitStringValueConstraint();
        sizes = new BitStringSizeConstraint();
    }

    public BitStringConstraint(BitStringSizeConstraint sizes) {
        this.values = new BitStringValueConstraint();
        this.sizes = sizes;
        this.inverted = sizes.isInverted();
    }

    public BitStringValueConstraint getValues() {
        return values;
    }

    public void setValues(BitStringValueConstraint values) {
        this.values = values;
    }

    public BitStringConstraint values(BitStringValueConstraint values) {
        this.values = values;

        return this;
    }

    public BitStringSizeConstraint getSizes() {
        return sizes;
    }

    public void setSizes(BitStringSizeConstraint sizes) {
        this.sizes = sizes;
    }

    public BitStringConstraint sizes(BitStringSizeConstraint sizes) {
        this.sizes = sizes;

        return this;
    }

    @Override
    public boolean isInverted() {
        return inverted;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public BitStringConstraint inverted(boolean inverted) {
        setInverted(inverted);

        return this;
    }

    @Override
    public BitStringConstraint intersection(BitStringConstraint constraint) {
        return copy().values(values.intersection(constraint.getValues()))
                .sizes(sizes.intersection(constraint.getSizes())).inverted(inverted && constraint.inverted);
    }

    @Override
    public BitStringConstraint union(BitStringConstraint constraint) {
        return copy().values(values.union(constraint.getValues())).sizes(sizes.union(constraint.getSizes()))
                .inverted(inverted || constraint.inverted);
    }

    @Override
    public BitStringConstraint exclude(BitStringConstraint constraint) {
        return copy().values(values.exclude(constraint.getValues())).sizes(sizes.exclude(constraint.getSizes()))
                .inverted(inverted || constraint.inverted);
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty() && sizes.isEmpty();
    }

    @Override
    public BitStringConstraint invert() {
        return copy().values(values.invert()).sizes(sizes.invert()).inverted(!inverted);
    }

    @Override
    public BitStringConstraint copy() {
        return new BitStringConstraint(values.copy(), sizes.copy());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        BitStringConstraint that = (BitStringConstraint) other;

        return Objects.equals(values, that.values) && Objects.equals(sizes, that.sizes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values, sizes);
    }

}
