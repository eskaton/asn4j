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

package ch.eskaton.asn4j.test.x680_51_3;

import ch.eskaton.asn4j.runtime.types.ASN1Boolean;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestBitString2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestBitString3;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestBitString4;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestBoolean2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestChoice1;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestChoice2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestEnumeration2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestEnumeration3;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestGeneralString1;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestGeneralString2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestGraphicString1;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestGraphicString2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestIA5String1;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestIA5String2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestISO646String1;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestISO646String2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestInteger1;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestInteger2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestNull1;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestNull2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestNull3;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestNull4;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestNull5;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestNull6;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestNumericString1;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestNumericString2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestObjectIdentifier2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestOctetString1;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestOctetString2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestOctetString3;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestOidIri1;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestOidIri2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestPrintableString1;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestPrintableString2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestRelativeOID2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestRelativeOidIri1;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestRelativeOidIri2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestRelativeOidIri3;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestSequence1;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestSequence2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestSequenceOf1;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestSequenceOf2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestSet1;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestSet2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestSetOf1;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestSetOf2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestT61String1;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestT61String2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestTeletexString1;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestTeletexString2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestVideotexString1;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestVideotexString2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestVisibleString1;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestVisibleString2;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static ch.eskaton.asn4j.test.TestHelper.testBitStringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testBitStringSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testBooleanFailure;
import static ch.eskaton.asn4j.test.TestHelper.testBooleanSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testChoiceFailure;
import static ch.eskaton.asn4j.test.TestHelper.testChoiceSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testEnumeratedFailure;
import static ch.eskaton.asn4j.test.TestHelper.testEnumeratedSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testGeneralStringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testGeneralStringSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testGraphicStringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testGraphicStringSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testIA5StringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testIA5StringSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testIRIFailure;
import static ch.eskaton.asn4j.test.TestHelper.testIRISuccess;
import static ch.eskaton.asn4j.test.TestHelper.testIntegerFailure;
import static ch.eskaton.asn4j.test.TestHelper.testIntegerSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testNullFailure;
import static ch.eskaton.asn4j.test.TestHelper.testNullSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testNumericStringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testNumericStringSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testObjectIdentifierFailure;
import static ch.eskaton.asn4j.test.TestHelper.testObjectIdentifierSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testOctetStringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testOctetStringSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testPrintableStringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testPrintableStringSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testRelativeIRIFailure;
import static ch.eskaton.asn4j.test.TestHelper.testRelativeIRISuccess;
import static ch.eskaton.asn4j.test.TestHelper.testRelativeOIDFailure;
import static ch.eskaton.asn4j.test.TestHelper.testRelativeOIDSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testSequenceFailure;
import static ch.eskaton.asn4j.test.TestHelper.testSequenceOfFailure;
import static ch.eskaton.asn4j.test.TestHelper.testSequenceOfSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testSequenceSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testSetFailure;
import static ch.eskaton.asn4j.test.TestHelper.testSetOfFailure;
import static ch.eskaton.asn4j.test.TestHelper.testSetOfSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testSetSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testTeletexStringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testTeletexStringSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testVideotexStringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testVideotexStringSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testVisibleStringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testVisibleStringSuccess;
import static ch.eskaton.commons.utils.Utils.with;

class TestX680_51_3 {

    @Test
    void testBitString2() {
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x00, 5);
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x01, 5);
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x02, 5);

        testBitStringFailure(TestBitString2.class, new TestBitString2(), 0x03, 5);
        testBitStringFailure(TestBitString2.class, new TestBitString2(), 0x00, 4);
        testBitStringFailure(TestBitString2.class, new TestBitString2(), 0x00, 6);
    }

    @Test
    void testBitString3() {
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x00, 5);
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x01, 5);
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x02, 5);
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x03, 6);
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x03, 4);
    }

    @Test
    void testBitString4() {
        testBitStringSuccess(TestBitString4.class, new TestBitString4(), 0x00, 0);
        testBitStringSuccess(TestBitString4.class, new TestBitString4(), 0xffee, 1);
    }

    @Test
    void testBoolean2() {
        testBooleanSuccess(TestBoolean2.class, new TestBoolean2(), true);

        testBooleanFailure(TestBoolean2.class, new TestBoolean2(), false);
    }

    @Test
    void testEnumeration2() {
        testEnumeratedSuccess(TestEnumeration2.class, new TestEnumeration2(), TestEnumeration2.B);
        testEnumeratedSuccess(TestEnumeration2.class, new TestEnumeration2(), TestEnumeration2.C);
        testEnumeratedSuccess(TestEnumeration2.class, new TestEnumeration2(), TestEnumeration2.D);

        testEnumeratedFailure(TestEnumeration2.class, new TestEnumeration2(), TestEnumeration2.A);
    }

    @Test
    void testEnumeration3() {
        testEnumeratedSuccess(TestEnumeration3.class, new TestEnumeration3(), TestEnumeration3.A);
        testEnumeratedSuccess(TestEnumeration3.class, new TestEnumeration3(), TestEnumeration3.B);
        testEnumeratedSuccess(TestEnumeration3.class, new TestEnumeration3(), TestEnumeration3.C);
        testEnumeratedSuccess(TestEnumeration3.class, new TestEnumeration3(), TestEnumeration3.D);
    }

    @Test
    void testInteger1() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger1.class, 1, 2, 4);
        testIntegerFailure(TestInteger1.class, 0, 3, 5);
    }

    @Test
    void testInteger2() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger2.class, 1, 2, 4);
        testIntegerFailure(TestInteger2.class, 0, 3, 5);
    }

    @Test
    void testNull1() {
        testNullSuccess(TestNull1.class, () -> new TestNull1());
    }

    @Test
    void testNull2() {
        testNullSuccess(TestNull2.class, () -> new TestNull2());
    }

    @Test
    void testNull3() {
        testNullFailure(TestNull3.class, () -> new TestNull3());
    }

    @Test
    void testNull4() {
        testNullFailure(TestNull4.class, () -> new TestNull4());
    }

    @Test
    void testNull5() {
        testNullSuccess(TestNull5.class, () -> new TestNull5());
    }

    @Test
    void testNull6() {
        testNullSuccess(TestNull6.class, () -> new TestNull6());
    }

    @Test
    void testObjectIdentifier2() {
        testObjectIdentifierSuccess(TestObjectIdentifier2.class, new TestObjectIdentifier2(), 0, 3, 6, 1);
        testObjectIdentifierSuccess(TestObjectIdentifier2.class, new TestObjectIdentifier2(), 0, 3, 6, 2);

        testObjectIdentifierFailure(TestObjectIdentifier2.class, new TestObjectIdentifier2(), 0, 3, 6, 3);
    }

    @Test
    void testRelativeOID2() {
        testRelativeOIDSuccess(TestRelativeOID2.class, new TestRelativeOID2(), 7, 3, 6, 1);
        testRelativeOIDSuccess(TestRelativeOID2.class, new TestRelativeOID2(), 7, 3, 6, 2);

        testRelativeOIDFailure(TestRelativeOID2.class, new TestRelativeOID2(), 7, 3, 6, 3);
    }

    @Test
    void testOIDIRI1() {
        testIRISuccess(TestOidIri1.class, new TestOidIri1(), "ISO", "a", "b", "a");
        testIRISuccess(TestOidIri1.class, new TestOidIri1(), "ISO", "a", "b", "b");

        testIRIFailure(TestOidIri1.class, new TestOidIri1(), "ISO", "a", "b", "c");
    }

    @Test
    void testOIDIRI2() {
        testIRISuccess(TestOidIri2.class, new TestOidIri2(), "ISO", "a", "b", "a");
        testIRISuccess(TestOidIri2.class, new TestOidIri2(), "ISO", "a", "b", "b");

        testIRIFailure(TestOidIri2.class, new TestOidIri2(), "ISO", "a", "b", "c");
    }

    @Test
    void testRelativeOIDIRI1() {
        testRelativeIRISuccess(TestRelativeOidIri1.class, new TestRelativeOidIri1(), "a", "b", "a");
        testRelativeIRISuccess(TestRelativeOidIri1.class, new TestRelativeOidIri1(), "a", "b", "b");

        testRelativeIRIFailure(TestRelativeOidIri1.class, new TestRelativeOidIri1(), "a", "b", "c");
    }

    @Test
    void testRelativeOIDIRI2() {
        testRelativeIRISuccess(TestRelativeOidIri2.class, new TestRelativeOidIri2(), "a", "b", "a");
        testRelativeIRISuccess(TestRelativeOidIri2.class, new TestRelativeOidIri2(), "a", "b", "b");

        testRelativeIRIFailure(TestRelativeOidIri2.class, new TestRelativeOidIri2(), "a", "b", "c");
    }

    @Test
    void testRelativeOIDIRI3() {
        testRelativeIRISuccess(TestRelativeOidIri3.class, new TestRelativeOidIri3(), "a", "b", "b");

        testRelativeIRIFailure(TestRelativeOidIri3.class, new TestRelativeOidIri3(), "a", "b", "a");
    }

    @Test
    void testOctetString1() {
        testOctetStringSuccess(TestOctetString1.class, new TestOctetString1(), new byte[] { 0x50 });
        testOctetStringSuccess(TestOctetString1.class, new TestOctetString1(), new byte[] { 0x61, (byte) 0xAD });

        testOctetStringFailure(TestOctetString1.class, new TestOctetString1(), new byte[] { 0x51 });
    }

    @Test
    void testOctetString2() {
        testOctetStringSuccess(TestOctetString2.class, new TestOctetString2(), new byte[] { 0x50 });
        testOctetStringSuccess(TestOctetString2.class, new TestOctetString2(), new byte[] { 0x61, (byte) 0xAD });

        testOctetStringFailure(TestOctetString2.class, new TestOctetString2(), new byte[] { 0x51 });
    }

    @Test
    void testOctetString3() {
        testOctetStringSuccess(TestOctetString3.class, new TestOctetString3(), new byte[] { 0x61, (byte) 0xAD });

        testOctetStringFailure(TestOctetString3.class, new TestOctetString3(), new byte[] { 0x50 });
    }

    @Test
    void testSetOf1() {
        testSetOfSuccess(TestSetOf1.class, new TestSetOf1(), ASN1Integer.valueOf(1L));
        testSetOfSuccess(TestSetOf1.class, new TestSetOf1(), ASN1Integer.valueOf(2L));
        testSetOfSuccess(TestSetOf1.class, new TestSetOf1(), ASN1Integer.valueOf(3L));

        testSetOfFailure(TestSetOf1.class, new TestSetOf1(), ASN1Integer.valueOf(0));
        testSetOfFailure(TestSetOf1.class, new TestSetOf1(), ASN1Integer.valueOf(4));
    }

    @Test
    void testSetOf2() {
        testSetOfSuccess(TestSetOf2.class, new TestSetOf2(), ASN1Integer.valueOf(1L));
        testSetOfSuccess(TestSetOf2.class, new TestSetOf2(), ASN1Integer.valueOf(2L));
        testSetOfSuccess(TestSetOf2.class, new TestSetOf2(), ASN1Integer.valueOf(3L));

        testSetOfFailure(TestSetOf2.class, new TestSetOf2(), ASN1Integer.valueOf(0));
        testSetOfFailure(TestSetOf2.class, new TestSetOf2(), ASN1Integer.valueOf(4));
    }

    @Test
    void testSequenceOf1() {
        testSequenceOfSuccess(TestSequenceOf1.class, new TestSequenceOf1(), ASN1Integer.valueOf(1L));
        testSequenceOfSuccess(TestSequenceOf1.class, new TestSequenceOf1(), ASN1Integer.valueOf(2L));
        testSequenceOfSuccess(TestSequenceOf1.class, new TestSequenceOf1(), ASN1Integer.valueOf(3L));

        testSequenceOfFailure(TestSequenceOf1.class, new TestSequenceOf1(), ASN1Integer.valueOf(0));
        testSequenceOfFailure(TestSequenceOf1.class, new TestSequenceOf1(), ASN1Integer.valueOf(4));
    }

    @Test
    void testSequenceOf2() {
        testSequenceOfSuccess(TestSequenceOf2.class, new TestSequenceOf2(),
                with(new TestInteger1(), obj -> obj.setValue(BigInteger.ONE)));
        testSequenceOfSuccess(TestSequenceOf2.class, new TestSequenceOf2(),
                with(new TestInteger1(), obj -> obj.setValue(BigInteger.TWO)));

        testSequenceOfFailure(TestSequenceOf2.class, new TestSequenceOf2(),
                with(new TestInteger1(), obj -> obj.setValue(BigInteger.ZERO)));
        testSequenceOfFailure(TestSequenceOf2.class, new TestSequenceOf2(),
                with(new TestInteger1(), obj -> obj.setValue(BigInteger.valueOf(3L))));
        testSequenceOfFailure(TestSequenceOf2.class, new TestSequenceOf2(),
                with(new TestInteger1(), obj -> obj.setValue(BigInteger.valueOf(4L))));
    }

    @Test
    void testSet1() {
        testSetSuccess(TestSet1.class, new TestSet1(), set -> {
            set.setA(with(new TestInteger1(), obj -> obj.setValue(BigInteger.valueOf(4L))));
            set.setB(ASN1Boolean.TRUE);
        });

        testSetFailure(TestSet1.class, new TestSet1(), set -> {
            set.setA(with(new TestInteger1(), obj -> obj.setValue(BigInteger.TWO)));
            set.setB(ASN1Boolean.TRUE);
        });
        testSetFailure(TestSet1.class, new TestSet1(), set -> {
            set.setA(with(new TestInteger1(), obj -> obj.setValue(BigInteger.valueOf(4L))));
            set.setB(ASN1Boolean.FALSE);
        });
    }

    @Test
    void testSet2() {
        testSetSuccess(TestSet2.class, new TestSet2(), set -> {
            set.setA(ASN1Integer.valueOf(4L));
            set.setB(ASN1Boolean.TRUE);
        });

        testSetFailure(TestSet2.class, new TestSet2(), set -> {
            set.setA(ASN1Integer.valueOf(2L));
            set.setB(ASN1Boolean.TRUE);
        });
        testSetFailure(TestSet2.class, new TestSet2(), set -> {
            set.setA(ASN1Integer.valueOf(4L));
            set.setB(ASN1Boolean.FALSE);
        });
    }

    @Test
    void testSequence1() {
        testSequenceSuccess(TestSequence1.class, new TestSequence1(), seq -> {
            seq.setA(with(new TestInteger1(), obj -> obj.setValue(BigInteger.TWO)));
            seq.setB(ASN1Boolean.FALSE);
        });

        testSequenceFailure(TestSequence1.class, new TestSequence1(), seq -> {
            seq.setA(with(new TestInteger1(), obj -> obj.setValue(BigInteger.valueOf(32L))));
            seq.setB(ASN1Boolean.FALSE);
        });
        testSequenceFailure(TestSequence1.class, new TestSequence1(), seq -> {
            seq.setA(with(new TestInteger1(), obj -> obj.setValue(BigInteger.TWO)));
            seq.setB(ASN1Boolean.TRUE);
        });
    }

    @Test
    void testSequence2() {
        testSequenceSuccess(TestSequence2.class, new TestSequence2(), seq -> {
            seq.setA(ASN1Integer.valueOf(2));
            seq.setB(ASN1Boolean.FALSE);
        });

        testSequenceFailure(TestSequence2.class, new TestSequence2(), seq -> {
            seq.setA(ASN1Integer.valueOf(32));
            seq.setB(ASN1Boolean.FALSE);
        });
        testSequenceFailure(TestSequence2.class, new TestSequence2(), seq -> {
            seq.setA(ASN1Integer.valueOf(2));
            seq.setB(ASN1Boolean.TRUE);
        });
    }

    @Test
    void testChoice1() {
        testChoiceSuccess(TestChoice1.class, new TestChoice1(), choice -> {
            choice.setA(ASN1Integer.valueOf(12));
        });

        testChoiceFailure(TestChoice1.class, new TestChoice1(), choice -> {
            choice.setA(ASN1Integer.valueOf(4));
        });
        testChoiceFailure(TestChoice1.class, new TestChoice1(), choice -> {
            choice.setB(ASN1Boolean.FALSE);
        });
    }

    @Test
    void testChoice2() {
        testChoiceSuccess(TestChoice2.class, new TestChoice2(), choice -> {
            choice.setA(ASN1Integer.valueOf(12));
        });

        testChoiceFailure(TestChoice2.class, new TestChoice2(), choice -> {
            choice.setA(ASN1Integer.valueOf(4));
        });
        testChoiceFailure(TestChoice2.class, new TestChoice2(), choice -> {
            choice.setB(ASN1Boolean.TRUE);
        });
    }

    @Test
    void testVisibleString1() {
        testVisibleStringSuccess(TestVisibleString1.class, new TestVisibleString1("def"));

        testVisibleStringFailure(TestVisibleString1.class, new TestVisibleString1("abc"));
    }

    @Test
    void testVisibleString2() {
        testVisibleStringSuccess(TestVisibleString2.class, new TestVisibleString2("def"));

        testVisibleStringFailure(TestVisibleString2.class, new TestVisibleString2("abc"));
    }

    @Test
    void testISO646String1() {
        testVisibleStringSuccess(TestISO646String1.class, new TestISO646String1("def"));

        testVisibleStringFailure(TestISO646String1.class, new TestISO646String1("abc"));
    }

    @Test
    void testISO646String2() {
        testVisibleStringSuccess(TestISO646String2.class, new TestISO646String2("def"));

        testVisibleStringFailure(TestISO646String2.class, new TestISO646String2("abc"));
    }

    @Test
    void testGeneralString1() {
        testGeneralStringSuccess(TestGeneralString1.class, new TestGeneralString1("efg"));

        testGeneralStringFailure(TestGeneralString1.class, new TestGeneralString1("abc"));
    }

    @Test
    void testGeneralString2() {
        testGeneralStringSuccess(TestGeneralString2.class, new TestGeneralString2("efg"));

        testGeneralStringFailure(TestGeneralString2.class, new TestGeneralString2("abc"));
    }

    @Test
    void testGraphicString1() {
        testGraphicStringSuccess(TestGraphicString1.class, new TestGraphicString1("abc"));

        testGraphicStringFailure(TestGraphicString1.class, new TestGraphicString1("def"));
    }

    @Test
    void testGraphicString2() {
        testGraphicStringSuccess(TestGraphicString2.class, new TestGraphicString2("abc"));

        testGraphicStringFailure(TestGraphicString2.class, new TestGraphicString2("def"));
    }

    @Test
    void testIA5String1() {
        testIA5StringSuccess(TestIA5String1.class, new TestIA5String1("abc"));

        testIA5StringFailure(TestIA5String1.class, new TestIA5String1("def"));
    }

    @Test
    void testIA5String2() {
        testIA5StringSuccess(TestIA5String2.class, new TestIA5String2("abc"));

        testIA5StringFailure(TestIA5String2.class, new TestIA5String2("def"));
    }

    @Test
    void testVideotexString1() {
        testVideotexStringSuccess(TestVideotexString1.class, new TestVideotexString1("abc"));

        testVideotexStringFailure(TestVideotexString1.class, new TestVideotexString1("def"));
    }

    @Test
    void testVideotexString2() {
        testVideotexStringSuccess(TestVideotexString2.class, new TestVideotexString2("abc"));

        testVideotexStringFailure(TestVideotexString2.class, new TestVideotexString2("def"));
    }

    @Test
    void testTeletexString1() {
        testTeletexStringSuccess(TestTeletexString1.class, new TestTeletexString1("abc"));

        testTeletexStringFailure(TestTeletexString1.class, new TestTeletexString1("def"));
    }

    @Test
    void testTeletexString2() {
        testTeletexStringSuccess(TestTeletexString2.class, new TestTeletexString2("abc"));

        testTeletexStringFailure(TestTeletexString2.class, new TestTeletexString2("def"));
    }

    @Test
    void testT61String1() {
        testTeletexStringSuccess(TestT61String1.class, new TestT61String1("abc"));

        testTeletexStringFailure(TestT61String1.class, new TestT61String1("def"));
    }

    @Test
    void testT61String2() {
        testTeletexStringSuccess(TestT61String2.class, new TestT61String2("abc"));

        testTeletexStringFailure(TestT61String2.class, new TestT61String2("def"));
    }

    @Test
    void testPrintableString1() {
        testPrintableStringSuccess(TestPrintableString1.class, new TestPrintableString1("abc"));

        testPrintableStringFailure(TestPrintableString1.class, new TestPrintableString1("def"));
    }

    @Test
    void testPrintableString2() {
        testPrintableStringSuccess(TestPrintableString2.class, new TestPrintableString2("abc"));

        testPrintableStringFailure(TestPrintableString2.class, new TestPrintableString2("def"));
    }

    @Test
    void testNumericString1() {
        testNumericStringSuccess(TestNumericString1.class, new TestNumericString1("123"));

        testNumericStringFailure(TestNumericString1.class, new TestNumericString1("456"));
    }

    @Test
    void testNumericString2() {
        testNumericStringSuccess(TestNumericString2.class, new TestNumericString2("123"));

        testNumericStringFailure(TestNumericString2.class, new TestNumericString2("456"));
    }

}
