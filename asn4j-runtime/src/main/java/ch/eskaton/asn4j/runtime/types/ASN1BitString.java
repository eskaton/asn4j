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
import ch.eskaton.asn4j.runtime.exceptions.ASN1RuntimeException;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;
import ch.eskaton.asn4j.runtime.utils.ToString;
import ch.eskaton.commons.utils.HexDump;
import ch.eskaton.commons.utils.StreamsUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@ASN1Tag(clazz = Clazz.UNIVERSAL, tag = 3, mode = ASN1Tag.Mode.EXPLICIT, constructed = false)
public class ASN1BitString implements ASN1Type, HasConstraint {

    protected byte[] value = new byte[0];

    protected int unusedBits;

    public ASN1BitString() {
    }

    public ASN1BitString(byte[] value, int unusedBits) {
        this.value = value;
        this.unusedBits = unusedBits;
    }

    public static ASN1BitString of(byte[] value) {
        return of(value, 0);
    }

    public static ASN1BitString of(byte[] value, int unusedBits) {
        return new ASN1BitString().value(value, unusedBits);
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        setValue(value, 0);
    }

    public void setValue(byte[] value, int unusedBits) {
        this.unusedBits = unusedBits;
        this.value = new byte[value.length];

        System.arraycopy(value, 0, this.value, 0, value.length);
    }

    public ASN1BitString value(byte[] value) {
        setValue(value);
        return this;
    }

    public ASN1BitString value(byte[] value, int unusedBits) {
        setValue(value, unusedBits);
        return this;
    }

    public void setBit(int bit) {
        int pos = getPos(bit);

        value[pos] = value[pos] |= getBit(bit);
    }

    public void clearBit(int bit) {
        int pos = getPos(bit);

        value[pos] = (byte) (value[pos] & ~getBit(bit));
    }

    public boolean testBit(int bit) {
        int pos = getPos(bit);

        return (value[pos] & getBit(bit)) != 0;
    }

    private int getPos(int bit) {
        int pos = bit / 8;

        if (pos >= value.length) {
            throw new ASN1RuntimeException("Bit position out of range");
        }

        return pos;
    }

    public int getUnusedBits() {
        return unusedBits;
    }

    public long getSize() {
        return getSize(value, unusedBits);
    }

    public static long getSize(byte[] value, int unusedBits) {
        return value.length * 8L - unusedBits;
    }

    private int getBit(int bit) {
        return 1 << 7 - (bit % 8);
    }

    @Override
    public void checkConstraint() {
        if (!doCheckConstraint()) {
            throw new ConstraintViolatedException(String.format("'%s'B (%d unused bits) doesn't satisfy a constraint",
                    StreamsUtils.toIntStream(value)
                            .mapToObj(b -> Integer.toBinaryString((b & 0xFF) + 0x100).substring(1 + unusedBits))
                            .collect(Collectors.joining()), unusedBits));
        }
    }

    @Override
    public String toString() {
        return ToString.builder(this).addAll()
                .map("value", v -> "0x" + HexDump.toHexString(value))
                .build();
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ASN1BitString other = (ASN1BitString) obj;

        return unusedBits == other.unusedBits && Arrays.equals(value, other.value);
    }

}
