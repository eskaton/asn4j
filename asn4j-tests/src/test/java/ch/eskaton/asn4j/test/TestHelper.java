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

package ch.eskaton.asn4j.test;

import ch.eskaton.asn4j.runtime.BERDecoder;
import ch.eskaton.asn4j.runtime.BEREncoder;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;
import ch.eskaton.asn4j.runtime.types.ASN1BitString;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class TestHelper {

    private TestHelper() {
    }


    public static <T extends ASN1BitString> void testBitStringSuccess(Class<? extends T> clazz, T bitString, long value,
            int unusedBits) {
        BigInteger intValue = BigInteger.valueOf(value);
        byte[] bytes = intValue.toByteArray();

        if (value > 0 && intValue.bitLength() % 8 == 0) {
            byte[] unsignedBytes = new byte[bytes.length-1];
            System.arraycopy(bytes, 0, unsignedBytes,0, unsignedBytes.length);
            bytes = unsignedBytes;
        }

        bitString.setValue(bytes, unusedBits);

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        T result = decoder.decode(clazz, encoder.encode(bitString));

        assertEquals(bitString, result);
    }

    public static <T extends ASN1BitString> void testBitStringFailure(final T bitString, long value, int unusedBits) {
        TestUtils.assertThrows(() -> bitString.setValue(new byte[] { (byte) value }, unusedBits),
                ConstraintViolatedException.class);
    }

}
