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

package ch.eskaton.asn4j.parser.ast.values;

import ch.eskaton.asn4j.parser.Position;

import java.math.BigDecimal;
import java.util.Objects;

public class RealValue extends AbstractValue {

    public enum RealType {
        POSITIVE_INF, NEGATIVE_INF, NAN, NORMAL, SPECIAL
    }

    private RealType realType;

    private BigDecimal value;

    private Long mantissa;

    private Integer base;

    private Integer exponent;

    public RealValue(Position position, RealType realType) {
        super(position);

        this.realType = realType;
    }

    public RealValue(Position position, BigDecimal value) {
        super(position);

        this.realType = RealType.NORMAL;
        this.value = value;
    }

    public RealValue(Position position, Long mantissa, Integer base, Integer exponent) {
        super(position);

        this.realType = RealType.SPECIAL;
        this.mantissa = mantissa;
        this.base = base;
        this.exponent = exponent;
    }

    public BigDecimal getValue() {
        return value;
    }

    public Long getMantissa() {
        return mantissa;
    }

    public Integer getBase() {
        return base;
    }

    public Integer getExponent() {
        return exponent;
    }

    public RealType getRealType() {
        return realType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RealValue realValue = (RealValue) o;

        return realType == realValue.realType &&
                Objects.equals(value, realValue.value) &&
                Objects.equals(mantissa, realValue.mantissa) &&
                Objects.equals(base, realValue.base) &&
                Objects.equals(exponent, realValue.exponent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(realType, value, mantissa, base, exponent);
    }

}
