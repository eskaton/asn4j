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
import ch.eskaton.asn4j.runtime.types.ASN1IRI;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1Null;
import ch.eskaton.asn4j.runtime.types.ASN1ObjectIdentifier;
import ch.eskaton.asn4j.runtime.types.ASN1RelativeIRI;
import ch.eskaton.asn4j.runtime.types.ASN1RelativeOID;
import ch.eskaton.asn4j.runtime.types.ASN1Type;

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

        testEncodeDecodeValue((Class<T>) clazz, bitString);
    }

    public static <T extends ASN1BitString> void testBitStringFailure(T bitString, long value, int unusedBits) {
        TestUtils.assertThrows(() -> bitString.setValue(new byte[] { (byte) value }, unusedBits),
                ConstraintViolatedException.class);
    }

    public static <T extends ASN1Boolean> void testBooleanSuccess(Class<? extends T> clazz, T booleanValue,
            boolean value) {
        booleanValue.setValue(value);

        testEncodeDecodeValue((Class<T>) clazz, booleanValue);
    }

    public static <T extends ASN1Boolean> void testBooleanFailure(T booleanValue, boolean value) {
        TestUtils.assertThrows(() -> booleanValue.setValue(value), ConstraintViolatedException.class);
    }

    public static <T extends ASN1EnumeratedType> void testEnumeratedSuccess(Class<? extends T> clazz, T enumValue,
            T value) {
        enumValue.setValue(value);

        testEncodeDecodeValue((Class<T>) clazz, enumValue);
    }

    public static <T extends ASN1EnumeratedType> void testEnumeratedFailure(T enumValue, T value) {
        TestUtils.assertThrows(() -> enumValue.setValue(value), ConstraintViolatedException.class);
    }

    public static <T extends ASN1Integer> void testIntegerSuccess(Class<? extends T> clazz, T intValue, long value) {
        intValue.setValue(BigInteger.valueOf(value));

        testEncodeDecodeValue((Class<T>) clazz, intValue);
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

        testEncodeDecodeValue((Class<T>) clazz, nullValue);
    }

    public static <T extends ASN1Null> void testNullFailure(Supplier<T> nullSupplier) {
        TestUtils.assertThrows(() -> nullSupplier.get(), ConstraintViolatedException.class);
    }

    public static <T extends ASN1ObjectIdentifier> void testObjectIdentifierSuccess(Class<? extends T> clazz,
            T oidValue, int... value) {
        oidValue.setValue(value);

        testEncodeDecodeValue((Class<T>) clazz, oidValue);
    }

    public static <T extends ASN1ObjectIdentifier> void testObjectIdentifierFailure(T oidValue, int... value) {
        TestUtils.assertThrows(() -> oidValue.setValue(value), ConstraintViolatedException.class);
    }

    public static <T extends ASN1RelativeOID> void testRelativeOIDSuccess(Class<? extends T> clazz,
            T roidValue, int... value) {
        roidValue.setValue(value);

        testEncodeDecodeValue((Class<T>) clazz, roidValue);
    }

    public static <T extends ASN1RelativeOID> void testRelativeOIDFailure(T roidValue, int... value) {
        TestUtils.assertThrows(() -> roidValue.setValue(value), ConstraintViolatedException.class);
    }

    private static <T extends ASN1Type> void testEncodeDecodeValue(Class<T> clazz, T value) {
        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        T result = decoder.decode(clazz, encoder.encode(value));

        assertEquals(value, result);
    }

    public static <T extends ASN1IRI> void testIRISuccess(Class<? extends T> clazz, T iriValue, String... value) {
        iriValue.setValue(value);

        testEncodeDecodeValue((Class<T>) clazz, iriValue);
    }

    public static <T extends ASN1IRI> void testIRIFailure(T iriValue, String... value) {
        TestUtils.assertThrows(() -> iriValue.setValue(value), ConstraintViolatedException.class);
    }

    public static <T extends ASN1RelativeIRI> void testRelativeIRISuccess(Class<? extends T> clazz, T ririValue,
            String... value) {
        ririValue.setValue(value);

        testEncodeDecodeValue((Class<T>) clazz, ririValue);
    }

    public static <T extends ASN1RelativeIRI> void testRelativeIRIFailure(T ririValue, String... value) {
        TestUtils.assertThrows(() -> ririValue.setValue(value), ConstraintViolatedException.class);
    }

}
