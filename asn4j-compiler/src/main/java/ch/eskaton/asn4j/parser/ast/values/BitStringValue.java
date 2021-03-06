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
import ch.eskaton.commons.utils.HexDump;
import ch.eskaton.commons.utils.StringUtils;

import java.util.List;
import java.util.Objects;

import static ch.eskaton.asn4j.parser.NoPosition.NO_POSITION;

public class BitStringValue extends ByteStringValue {

    private int unusedBits;

    private List<String> namedValues = List.of();

    private Value value;

    public BitStringValue(Position position) {
        super(position);
    }

    public BitStringValue(Position position, byte[] byteValue, int unusedBits) {
        super(position, byteValue);

        this.unusedBits = unusedBits;
    }

    public BitStringValue(byte[] byteValue, int unusedBits) {
        super(NO_POSITION, byteValue);

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

    public Value getValue() {
        return value;
    }

    public void setByteValue(byte[] byteValue, int unusedBits) {
        setByteValue(byteValue);

        this.unusedBits = unusedBits;
    }

    public List<String> getNamedValues() {
        return namedValues;
    }

    public int getUnusedBits() {
        return unusedBits;
    }

    public int getSize() {
        if (byteValue == null) {
            return -1;
        }

        return byteValue.length * 8 - unusedBits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        BitStringValue that = (BitStringValue) o;

        return unusedBits == that.unusedBits &&
                Objects.equals(namedValues, that.namedValues) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), unusedBits, namedValues, value);
    }

    @Override
    public String toString() {
        var namedValuesStr = namedValues != null ? StringUtils.join(namedValues, ", ") : String.valueOf(this.value);
        var valueStr = byteValue != null && byteValue.length > 0 ?
                "0x" + HexDump.toHexString(byteValue) :
                namedValuesStr;

        return "%s[%s]".formatted(getClass().getSimpleName(), valueStr);
    }

}
