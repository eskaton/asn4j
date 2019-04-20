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
import ch.eskaton.commons.utils.StringUtils;

import java.util.List;

public class BitStringValue extends AbstractValue {

    private int unusedBits;

    private Integer intValue;

    private byte[] byteValue;

    private List<String> namedValues;

    private Value value;

    public BitStringValue(Position position) {
        super(position);
    }

    public BitStringValue(Position position, Integer intValue, int unusedBits) {
        super(position);

        this.intValue = intValue;
        this.unusedBits = unusedBits;
    }

    public BitStringValue(Position position, byte[] byteValue, int unusedBits) {
        super(position);

        this.byteValue = byteValue;
        this.unusedBits = unusedBits;
    }

    public BitStringValue(Position position, List<String> namedValues) {
        super(position);

        this.namedValues = namedValues;
    }

    public BitStringValue(Position position, Value value) {
        super(position);

        this.value = value;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public byte[] getByteValue() {
        return byteValue;
    }

    public void setByteValue(byte[] byteValue) {
        this.byteValue = byteValue;
    }

    public Value getValue() {
        return value;
    }

    public List<String> getNamedValues() {
        return namedValues;
    }

    public int getUnusedBits() {
        return unusedBits;
    }

    @Override
    public String toString() {
        return StringUtils.concat(BitStringValue.class.getSimpleName() + "[",
                intValue != null ? Integer.toBinaryString(intValue) :
                        (namedValues != null ? StringUtils.join(namedValues, ", ") : value), "]");
    }

}
