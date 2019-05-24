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

public class RealValue extends AbstractValue {

    public enum Type {
        POSITIVE_INF, NEGATIVE_INF, NAN, NORMAL, SPECIAL
    }

    private Type type;

    private BigDecimal value;

    private Long mantissa;

    private Long base;

    private Long exponent;

    public RealValue(Position position, Type type) {
    	super(position);

    	this.type = type;
    }

    public RealValue(Position position, BigDecimal value) {
        super(position);

    	this.type = Type.NORMAL;
    	this.value = value;
    }

    public RealValue(Position position, Long mantissa, Long base, Long exponent) {
        super(position);

    	this.type = Type.SPECIAL;
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

    public Long getBase() {
    	return base;
    }

    public Long getExponent() {
    	return exponent;
    }

    public Type getType() {
    	return type;
    }

}
