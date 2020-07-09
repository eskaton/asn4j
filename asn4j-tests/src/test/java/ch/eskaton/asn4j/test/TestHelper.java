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
import ch.eskaton.asn4j.runtime.types.ASN1BMPString;
import ch.eskaton.asn4j.runtime.types.ASN1BitString;
import ch.eskaton.asn4j.runtime.types.ASN1Boolean;
import ch.eskaton.asn4j.runtime.types.ASN1Choice;
import ch.eskaton.asn4j.runtime.types.ASN1EnumeratedType;
import ch.eskaton.asn4j.runtime.types.ASN1GeneralString;
import ch.eskaton.asn4j.runtime.types.ASN1GraphicString;
import ch.eskaton.asn4j.runtime.types.ASN1IA5String;
import ch.eskaton.asn4j.runtime.types.ASN1IRI;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1Null;
import ch.eskaton.asn4j.runtime.types.ASN1NumericString;
import ch.eskaton.asn4j.runtime.types.ASN1ObjectIdentifier;
import ch.eskaton.asn4j.runtime.types.ASN1OctetString;
import ch.eskaton.asn4j.runtime.types.ASN1PrintableString;
import ch.eskaton.asn4j.runtime.types.ASN1RelativeIRI;
import ch.eskaton.asn4j.runtime.types.ASN1RelativeOID;
import ch.eskaton.asn4j.runtime.types.ASN1Sequence;
import ch.eskaton.asn4j.runtime.types.ASN1SequenceOf;
import ch.eskaton.asn4j.runtime.types.ASN1Set;
import ch.eskaton.asn4j.runtime.types.ASN1SetOf;
import ch.eskaton.asn4j.runtime.types.ASN1TeletexString;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.asn4j.runtime.types.ASN1UTF8String;
import ch.eskaton.asn4j.runtime.types.ASN1UniversalString;
import ch.eskaton.asn4j.runtime.types.ASN1VideotexString;
import ch.eskaton.asn4j.runtime.types.ASN1VisibleString;
import ch.eskaton.asn4j.runtime.types.AbstractASN1IRI;
import ch.eskaton.asn4j.runtime.types.AbstractASN1OID;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.test.TestUtils.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TestHelper {

    private TestHelper() {
    }

    public static <T extends ASN1Type> void assertWithType(Class<T> type, Consumer<T> consumer) {
        try {
            T value = type.newInstance();
            consumer.accept(value);
        } catch (InstantiationException | IllegalAccessException e) {
            fail("Failed to instantiate type " + type.getSimpleName());
        }
    }

    public static <T extends ASN1Type> void assertDecodable(Class<T> type) {
        assertWithType(type, value -> assertValueDecodable(type, value));
    }

    public static <T extends ASN1Type> void assertDecodable(Class<T> type, Consumer<T> consumer) {
        assertWithType(type, value -> {
            consumer.accept(value);
            assertValueDecodable(type, value);
        });
    }

    public static <T extends ASN1Type> void assertDecodableVerifyAfter(Class<T> type, Consumer<T> consumer,
            Consumer<T> after) {
        assertWithType(type, value -> {
            consumer.accept(value);
            assertValueDecodable(type, value);
            after.accept(value);
        });
    }

    public static <T extends ASN1Type> void assertDecodableVerifyAround(Class<T> type, Consumer<T> consumer,
            Consumer<T> before, Consumer<T> after) {
        assertWithType(type, value -> {
            consumer.accept(value);
            before.accept(value);
            assertValueDecodable(type, value);
            after.accept(value);
        });
    }

    public static <T extends ASN1Type> void assertDecodableVerifyAfter(Class<T> type, Consumer<T> after) {
        assertWithType(type, value -> {
            assertValueDecodable(type, value);
            after.accept(value);
        });
    }

    public static <T extends ASN1Type> void assertDecodableVerifyAround(Class<T> type, Consumer<T> before,
            Consumer<T> after) {
        assertWithType(type, value -> {
            before.accept(value);
            assertValueDecodable(type, value);
            after.accept(value);
        });
    }

    public static <T extends ASN1Type> void assertValueDecodable(Class<T> type, ASN1Type value) {
        T decoded = new BERDecoder().decode(type, new BEREncoder().encode(value));

        assertEquals(value, decoded);
    }

    public static <T extends ASN1BitString> void testBitStringSuccess(Class<? extends T> clazz, T bitString, long value,
            int unusedBits) {
        setBitStringValue(bitString, value, unusedBits);

        assertValueDecodable(clazz, bitString);
    }

    public static <T extends ASN1BitString> void testBitStringFailure(Class<? extends T> clazz, T bitString, long value,
            int unusedBits) {
        setBitStringValue(bitString, value, unusedBits);

        assertThrows(() -> assertValueDecodable(clazz, bitString), ConstraintViolatedException.class);
    }

    private static <T extends ASN1BitString> void setBitStringValue(T bitString, long value, int unusedBits) {
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
    }

    public static <T extends ASN1Boolean> void testBooleanSuccess(Class<? extends T> clazz, T booleanValue,
            boolean value) {
        booleanValue.setValue(value);

        assertValueDecodable(clazz, booleanValue);
    }

    public static <T extends ASN1Boolean> void testBooleanFailure(Class<? extends T> clazz, T booleanValue,
            boolean value) {
        booleanValue.setValue(value);

        assertThrows(() -> assertValueDecodable(clazz, booleanValue), ConstraintViolatedException.class);
    }

    public static <T extends ASN1EnumeratedType> void testEnumeratedSuccess(Class<? extends T> clazz, T enumValue,
            T value) {
        enumValue.setValue(value);

        assertValueDecodable(clazz, enumValue);
    }

    public static <T extends ASN1EnumeratedType> void testEnumeratedFailure(Class<? extends T> clazz, T enumValue,
            T value) {
        enumValue.setValue(value);

        assertThrows(() -> assertValueDecodable(clazz, enumValue), ConstraintViolatedException.class);
    }

    public static <T extends ASN1Integer> void testIntegerSuccess(Class<? extends T> clazz, T intValue, long value) {
        intValue.setValue(BigInteger.valueOf(value));

        assertValueDecodable(clazz, intValue);
    }

    public static <T extends ASN1Integer> void testIntegerSuccess(Class<? extends T> clazz, long... values)
            throws IllegalAccessException, InstantiationException {
        for (long value : values) {
            testIntegerSuccess(clazz, clazz.newInstance(), value);
        }
    }

    public static <T extends ASN1Integer> void testIntegerFailure(Class<? extends T> clazz, T intValue, long value) {
        intValue.setValue(BigInteger.valueOf(value));

        assertThrows(() -> assertValueDecodable(clazz, intValue), ConstraintViolatedException.class,
                "Value: " + value);
    }

    public static <T extends ASN1Integer> void testIntegerFailure(Class<? extends T> clazz, long... values)
            throws IllegalAccessException, InstantiationException {
        for (long value : values) {
            testIntegerFailure(clazz, clazz.newInstance(), value);
        }
    }

    public static <T extends ASN1Null> void testNullSuccess(Class<? extends T> clazz, Supplier<T> nullSupplier) {
        T nullValue = nullSupplier.get();

        assertValueDecodable(clazz, nullValue);
    }

    public static <T extends ASN1Null> void testNullFailure(Class<? extends T> clazz, Supplier<T> nullSupplier) {
        T nullValue = nullSupplier.get();

        assertThrows(
                () -> assertValueDecodable(clazz, nullValue), ConstraintViolatedException.class);
    }

    public static <T extends ASN1ObjectIdentifier> void testObjectIdentifierSuccess(Class<? extends T> clazz,
            T oidValue, int... value) {
        oidValue.setValue(value);

        assertValueDecodable(clazz, oidValue);
    }

    public static <T extends ASN1ObjectIdentifier> void testObjectIdentifierFailure(Class<? extends T> clazz,
            T oidValue, int... value) {
        oidValue.setValue(value);

        assertThrows(() -> assertValueDecodable(clazz, oidValue), ConstraintViolatedException.class);
    }

    public static <T extends ASN1RelativeOID> void testRelativeOIDSuccess(Class<? extends T> clazz,
            T roidValue, int... value) {
        roidValue.setValue(value);

        assertValueDecodable(clazz, roidValue);
    }

    public static <T extends ASN1RelativeOID> void testRelativeOIDFailure(Class<? extends T> clazz, T roidValue,
            int... value) {
        roidValue.setValue(value);

        assertThrows(() -> assertValueDecodable(clazz, roidValue), ConstraintViolatedException.class);
    }

    public static <T extends ASN1IRI> void testIRISuccess(Class<? extends T> clazz, T iriValue, String... value) {
        iriValue.setValue(value);

        assertValueDecodable(clazz, iriValue);
    }

    public static <T extends ASN1IRI> void testIRIFailure(Class<? extends T> clazz, T iriValue, String... value) {
        iriValue.setValue(value);

        assertThrows(() -> assertValueDecodable(clazz, iriValue), ConstraintViolatedException.class);
    }

    public static <T extends ASN1RelativeIRI> void testRelativeIRISuccess(Class<? extends T> clazz, T ririValue,
            String... value) {
        ririValue.setValue(value);

        assertValueDecodable(clazz, ririValue);
    }

    public static <T extends ASN1RelativeIRI> void testRelativeIRIFailure(Class<? extends T> clazz, T ririValue,
            String... value) {
        ririValue.setValue(value);

        assertThrows(() -> assertValueDecodable(clazz, ririValue), ConstraintViolatedException.class);
    }

    public static <T extends ASN1OctetString> void testOctetStringSuccess(Class<? extends T> clazz, T octetStringValue,
            byte[] value) {
        octetStringValue.setValue(value);

        assertValueDecodable(clazz, octetStringValue);
    }

    public static <T extends ASN1OctetString> void testOctetStringFailure(Class<? extends T> clazz, T octetStringValue,
            byte[] value) {
        octetStringValue.setValue(value);

        assertThrows(() -> assertValueDecodable(clazz, octetStringValue), ConstraintViolatedException.class);
    }

    public static <V extends ASN1Type, T extends ASN1SetOf> void testSetOfSuccess(Class<? extends T> clazz, T setOfValue,
            V... values) {
        setOfValue.setValues(values);

        assertValueDecodable(clazz, setOfValue);
    }

    public static <V extends ASN1Type, T extends ASN1SetOf> void testSetOfFailure(Class<? extends T> clazz, T setOfValue,
            V... values) {
        setOfValue.setValues(values);

        assertThrows(() -> assertValueDecodable(clazz, setOfValue), ConstraintViolatedException.class);
    }

    public static <V extends ASN1Type, T extends ASN1SequenceOf> void testSequenceOfSuccess(Class<? extends T> clazz,
            T sequenceOfValue, V... values) {
        sequenceOfValue.setValues(values);

        assertValueDecodable(clazz, sequenceOfValue);
    }

    public static <V extends ASN1Type, T extends ASN1SequenceOf> void testSequenceOfFailure(Class<? extends T> clazz,
            T sequenceOfValue, V... values) {
        sequenceOfValue.setValues(values);

        assertThrows(() -> assertValueDecodable(clazz, sequenceOfValue), ConstraintViolatedException.class);
    }

    public static <T extends ASN1Sequence> void testSequenceSuccess(Class<? extends T> clazz, T sequenceValue,
            Consumer<T> consumer) {
        consumer.accept(sequenceValue);

        assertValueDecodable(clazz, sequenceValue);
    }

    public static <T extends ASN1Sequence> void testSequenceFailure(Class<? extends T> clazz, T sequenceValue,
            Consumer<T> consumer) {
        consumer.accept(sequenceValue);

        assertThrows(() -> assertValueDecodable(clazz, sequenceValue), ConstraintViolatedException.class);
    }

    public static <T extends ASN1Set> void testSetSuccess(Class<? extends T> clazz, T setValue,
            Consumer<T> consumer) {
        consumer.accept(setValue);

        assertValueDecodable(clazz, setValue);
    }

    public static <T extends ASN1Set> void testSetFailure(Class<? extends T> clazz, T setValue,
            Consumer<T> consumer) {
        consumer.accept(setValue);

        assertThrows(() -> assertValueDecodable(clazz, setValue), ConstraintViolatedException.class);
    }

    public static <T extends ASN1Choice> void testChoiceSuccess(Class<? extends T> clazz, T choiceValue,
            Consumer<T> consumer) {
        consumer.accept(choiceValue);

        assertValueDecodable(clazz, choiceValue);
    }

    public static <T extends ASN1Choice> void testChoiceFailure(Class<? extends T> clazz, T choiceValue,
            Consumer<T> consumer) {
        consumer.accept(choiceValue);

        assertThrows(() -> assertValueDecodable(clazz, choiceValue), ConstraintViolatedException.class);
    }

    public static <T extends ASN1VisibleString> void testVisibleStringSuccess(Class<? extends T> clazz, T stringValue) {
        assertValueDecodable(clazz, stringValue);
    }

    public static <T extends ASN1VisibleString> void testVisibleStringFailure(Class<? extends T> clazz, T stringValue) {
        assertThrows(() -> assertValueDecodable(clazz, stringValue), ConstraintViolatedException.class);
    }

    public static <T extends ASN1GeneralString> void testGeneralStringSuccess(Class<? extends T> clazz, T stringValue) {
        assertValueDecodable(clazz, stringValue);
    }

    public static <T extends ASN1GeneralString> void testGeneralStringFailure(Class<? extends T> clazz, T stringValue) {
        assertThrows(() -> assertValueDecodable(clazz, stringValue), ConstraintViolatedException.class);
    }

    public static <T extends ASN1GraphicString> void testGraphicStringSuccess(Class<? extends T> clazz, T stringValue) {
        assertValueDecodable(clazz, stringValue);
    }

    public static <T extends ASN1GraphicString> void testGraphicStringFailure(Class<? extends T> clazz, T stringValue) {
        assertThrows(() -> assertValueDecodable(clazz, stringValue), ConstraintViolatedException.class);
    }

    public static <T extends ASN1IA5String> void testIA5StringSuccess(Class<? extends T> clazz, T stringValue) {
        assertValueDecodable(clazz, stringValue);
    }

    public static <T extends ASN1IA5String> void testIA5StringFailure(Class<? extends T> clazz, T stringValue) {
        assertThrows(() -> assertValueDecodable(clazz, stringValue), ConstraintViolatedException.class);
    }

    public static <T extends ASN1VideotexString> void testVideotexStringSuccess(Class<? extends T> clazz, T stringValue) {
        assertValueDecodable(clazz, stringValue);
    }

    public static <T extends ASN1VideotexString> void testVideotexStringFailure(Class<? extends T> clazz, T stringValue) {
        assertThrows(() -> assertValueDecodable(clazz, stringValue), ConstraintViolatedException.class);
    }

    public static <T extends ASN1TeletexString> void testTeletexStringSuccess(Class<? extends T> clazz, T stringValue) {
        assertValueDecodable(clazz, stringValue);
    }

    public static <T extends ASN1TeletexString> void testTeletexStringFailure(Class<? extends T> clazz, T stringValue) {
        assertThrows(() -> assertValueDecodable(clazz, stringValue), ConstraintViolatedException.class);
    }

    public static <T extends ASN1PrintableString> void testPrintableStringSuccess(Class<? extends T> clazz, T stringValue) {
        assertValueDecodable(clazz, stringValue);
    }

    public static <T extends ASN1PrintableString> void testPrintableStringFailure(Class<? extends T> clazz, T stringValue) {
        assertThrows(() -> assertValueDecodable(clazz, stringValue), ConstraintViolatedException.class);
    }

    public static <T extends ASN1NumericString> void testNumericStringSuccess(Class<? extends T> clazz, T stringValue) {
        assertValueDecodable(clazz, stringValue);
    }

    public static <T extends ASN1NumericString> void testNumericStringFailure(Class<? extends T> clazz, T stringValue) {
        assertThrows(() -> assertValueDecodable(clazz, stringValue), ConstraintViolatedException.class);
    }

    public static <T extends ASN1UTF8String> void testUTF8StringSuccess(Class<? extends T> clazz, T stringValue) {
        assertValueDecodable(clazz, stringValue);
    }

    public static <T extends ASN1UTF8String> void testUTF8StringFailure(Class<? extends T> clazz, T stringValue) {
        assertThrows(() -> assertValueDecodable(clazz, stringValue), ConstraintViolatedException.class);
    }

    public static <T extends ASN1UniversalString> void testUniversalStringSuccess(Class<? extends T> clazz, T stringValue) {
        assertValueDecodable(clazz, stringValue);
    }

    public static <T extends ASN1UniversalString> void testUniversalStringFailure(Class<? extends T> clazz, T stringValue) {
        assertThrows(() -> assertValueDecodable(clazz, stringValue), ConstraintViolatedException.class);
    }

    public static <T extends ASN1BMPString> void testBMPStringSuccess(Class<? extends T> clazz, T stringValue) {
        assertValueDecodable(clazz, stringValue);
    }

    public static <T extends ASN1BMPString> void testBMPStringFailure(Class<? extends T> clazz, T stringValue) {
        assertThrows(() -> assertValueDecodable(clazz, stringValue), ConstraintViolatedException.class);
    }

    public static byte[] randomBytes(int length) {
        byte[] value = new byte[length];

        for (int i = 0; i < length; i++) {
            value[i] = (byte) ((byte) Math.random() * 256);
        }

        return value;
    }

    public static <T extends AbstractASN1OID> T createOID(T oid, int... components) {
        oid.setValue(components);

        return oid;
    }

    public static <T extends AbstractASN1IRI> T createIRI(T iri, String... components) {
        iri.setValue(components);

        return iri;
    }

    public static java.util.List<ASN1Integer> toInts(Integer... ints) {
        return Arrays.stream(ints).map(i -> ASN1Integer.valueOf(i)).collect(Collectors.toList());
    }

    public static java.util.List<ASN1Boolean> toBooleans(Boolean... booleans) {
        return Arrays.stream(booleans).map(b -> ASN1Boolean.of(b)).collect(Collectors.toList());
    }

}
