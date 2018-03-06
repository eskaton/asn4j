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

import java.math.BigDecimal;
import java.util.Objects;

@ASN1Tag(clazz = Clazz.Universal, tag = 9, mode = ASN1Tag.Mode.Explicit, constructed = false)
public class ASN1Real implements ASN1Type {

    public enum Type {
        NORMAL, PLUS_INFINITY, MINUS_INFINITY, NOT_A_NUMBER, MINUS_ZERO
    }

    public static final ASN1Real PLUS_INFINITY = new ASN1Real(
            Type.PLUS_INFINITY);

    public static final ASN1Real MINUS_INFINITY = new ASN1Real(
            Type.MINUS_INFINITY);

    public static final ASN1Real NOT_A_NUMBER = new ASN1Real(Type.NOT_A_NUMBER);

    public static final ASN1Real MINUS_ZERO = new ASN1Real(Type.MINUS_ZERO);

    private Type type = Type.NORMAL;

    private BigDecimal value;

    public ASN1Real() {
    }

    private ASN1Real(Type type) {
        this.type = type;
    }

    protected boolean checkConstraint(BigDecimal value)
            throws ConstraintViolatedException {
        return true;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) throws ConstraintViolatedException {
        if (!checkConstraint(value)) {
            throw new ConstraintViolatedException(String.format(
                    "%d doesn't satisfy a constraint", value));
        }

        this.type = Type.NORMAL;
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
        this.value = null;
    }

    public static ASN1Real valueOf(double d) throws ConstraintViolatedException {
        ASN1Real value = new ASN1Real();
        value.setValue(new BigDecimal(d));
        return value;
    }

    public String toString() {
        if (type == Type.NORMAL) {
            return String.valueOf(value);
        } else {
            return type.toString();
        }
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
