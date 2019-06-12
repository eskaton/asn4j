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
import ch.eskaton.asn4j.runtime.types.ASN1Boolean;
import ch.eskaton.asn4j.runtime.types.ASN1EnumeratedType;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1Null;

import java.math.BigInteger;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

public class TestHelper {

    private TestHelper() {
    }


    public static <T extends ASN1BitString> void testBitStringSuccess(Class<? extends T> clazz, T bitString, long value,
            int unusedBits) {
        BigInteger intValue = BigInteger.valueOf(value);
        byte[] bytes = intValue.toByteArray();

        if (value > 0 && intValue.bitLength() % 8 == 0) {
            byte[] unsignedBytes = new byte[bytes.length - 1];
            System.arraycopy(bytes, 0, unsignedBytes, 0, unsignedBytes.length);
            bytes = unsignedBytes;
        } else if (value == 0 && unusedBits == 8) {
            bytes = new byte[] {};
            unusedBits = 0;
        }

        bitString.setValue(bytes, unusedBits);

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        T result = decoder.decode(clazz, encoder.encode(bitString));

        assertEquals(bitString, result);
    }

    public static <T extends ASN1BitString> void testBitStringFailure(T bitString, long value, int unusedBits) {
        TestUtils.assertThrows(() -> bitString.setValue(new byte[] { (byte) value }, unusedBits),
                ConstraintViolatedException.class);
    }

    public static <T extends ASN1Boolean> void testBooleanSuccess(Class<? extends T> clazz, T booleanValue,
            boolean value) {
        booleanValue.setValue(value);

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        T result = decoder.decode(clazz, encoder.encode(booleanValue));

        assertEquals(booleanValue, result);
    }

    public static <T extends ASN1Boolean> void testBooleanFailure(T booleanValue, boolean value) {
        TestUtils.assertThrows(() -> booleanValue.setValue(value), ConstraintViolatedException.class);
    }

    public static <T extends ASN1EnumeratedType> void testEnumeratedSuccess(Class<? extends T> clazz, T enumValue,
            T value) {
        enumValue.setValue(value);

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        T result = decoder.decode(clazz, encoder.encode(enumValue));

        assertEquals(enumValue, result);
    }

    public static <T extends ASN1EnumeratedType> void testEnumeratedFailure(T enumValue, T value) {
        TestUtils.assertThrows(() -> enumValue.setValue(value), ConstraintViolatedException.class);
    }

    public static <T extends ASN1Integer> void testIntegerSuccess(Class<? extends T> clazz, T intValue, long value) {
        intValue.setValue(BigInteger.valueOf(value));

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        T result = decoder.decode(clazz, encoder.encode(intValue));

        assertEquals(intValue, result);
    }

    public static <T extends ASN1Integer> void testIntegerSuccess(Class<? extends T> clazz, long... values)
            throws IllegalAccessException, InstantiationException {
        for (long value : values) {
            testIntegerSuccess(clazz, clazz.newInstance(), value);
        }
    }

    public static <T extends ASN1Integer> void testIntegerFailure(T intValue, long value) {
        TestUtils.assertThrows(() -> intValue.setValue(BigInteger.valueOf(value)), ConstraintViolatedException.class,
                "Value: " + value);
    }

    public static <T extends ASN1Integer> void testIntegerFailure(Class<? extends T> clazz, long... values)
            throws IllegalAccessException, InstantiationException {
        for (long value : values) {
            testIntegerFailure(clazz.newInstance(), value);
        }
    }

    public static <T extends ASN1Null> void testNullSuccess(Class<? extends T> clazz, Supplier<T> nullSupplier) {
        T nullValue = nullSupplier.get();

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        T result = decoder.decode(clazz, encoder.encode(nullValue));

        assertEquals(nullValue, result);
    }

    public static <T extends ASN1Null> void testNullFailure(Supplier<T> nullSupplier) {
        TestUtils.assertThrows(() -> nullSupplier.get(), ConstraintViolatedException.class);
    }

}
