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

package ch.eskaton.asn4j.runtime.types;

import ch.eskaton.asn4j.runtime.Clazz;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;
import ch.eskaton.asn4j.runtime.utils.ToString;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

@ASN1Tag(clazz = Clazz.UNIVERSAL, tag = 9, mode = ASN1Tag.Mode.EXPLICIT, constructed = false)
public class ASN1Real implements ASN1Type, HasConstraint {

    public enum Type {
        NORMAL, PLUS_INFINITY, MINUS_INFINITY, NOT_A_NUMBER, MINUS_ZERO
    }

    public static final ASN1Real PLUS_INFINITY = new ASN1Real(Type.PLUS_INFINITY);

    public static final ASN1Real MINUS_INFINITY = new ASN1Real(Type.MINUS_INFINITY);

    public static final ASN1Real NOT_A_NUMBER = new ASN1Real(Type.NOT_A_NUMBER);

    public static final ASN1Real MINUS_ZERO = new ASN1Real(Type.MINUS_ZERO);

    private Type type = Type.NORMAL;

    private BigDecimal value;

    public ASN1Real() {
    }

    public ASN1Real(Type type) {
        this.type = type;
    }

    public ASN1Real(BigDecimal value) {
        setValue(value);
    }

    public ASN1Real(long mantissa, long base, int exponent) {
        setValue(BigDecimal.valueOf(mantissa).multiply(BigDecimal.valueOf(base).pow(exponent, new MathContext(9))));
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.type = Type.NORMAL;
        this.value = value.stripTrailingZeros();
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
        this.value = null;
    }

    public static ASN1Real valueOf(double d) {
        ASN1Real value = new ASN1Real();
        value.setValue(BigDecimal.valueOf(d));
        return value;
    }

    @Override
    public void checkConstraint() {
        if (Boolean.FALSE.equals(doCheckConstraint())) {
            throw new ConstraintViolatedException(String.format("%d doesn't satisfy a constraint", value));
        }

    }

    @Override
    public String toString() {
        return ToString.get(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ASN1Real other = (ASN1Real) obj;

        return type == other.type && Objects.equals(value, other.value);
    }

}
