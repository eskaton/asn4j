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

package ch.eskaton.asn4j.test.x680_51_1;

import ch.eskaton.asn4j.runtime.types.ASN1BitString;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1SetOf;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestBitString1;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestBitString2;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestBitString3;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestBitString5;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestBitString6;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestBitString7;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestBoolean2;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestEnumeration1;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestEnumeration2;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestEnumeration3;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestEnumeration4;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestInteger1;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestInteger10;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestInteger11;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestInteger2;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestInteger3;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestInteger4;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestInteger5;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestInteger6;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestInteger7;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestInteger8;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestInteger9;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestNull1;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestNull2;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestObjectIdentifier2;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestOctetString1;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestOctetString2;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestOctetString3;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestOctetString5;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestOctetString6;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestOctetString7;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestOidIri2;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestRelativeOID2;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestRelativeOidIri2;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestSequenceOf1;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestSequenceOf2;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestSetOf1;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestSetOf10;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestSetOf2;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestSetOf3;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestSetOf4;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestSetOf5;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestSetOf6;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestSetOf7;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestSetOf8;
import ch.eskaton.asn4j.test.modules.x680_51_1.TestSetOf9;
import org.junit.jupiter.api.Test;

import static ch.eskaton.asn4j.test.TestHelper.randomBytes;
import static ch.eskaton.asn4j.test.TestHelper.testBitStringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testBitStringSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testBooleanFailure;
import static ch.eskaton.asn4j.test.TestHelper.testBooleanSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testEnumeratedFailure;
import static ch.eskaton.asn4j.test.TestHelper.testEnumeratedSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testIRIFailure;
import static ch.eskaton.asn4j.test.TestHelper.testIRISuccess;
import static ch.eskaton.asn4j.test.TestHelper.testIntegerFailure;
import static ch.eskaton.asn4j.test.TestHelper.testIntegerSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testNullFailure;
import static ch.eskaton.asn4j.test.TestHelper.testNullSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testObjectIdentifierFailure;
import static ch.eskaton.asn4j.test.TestHelper.testObjectIdentifierSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testOctetStringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testOctetStringSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testRelativeIRIFailure;
import static ch.eskaton.asn4j.test.TestHelper.testRelativeIRISuccess;
import static ch.eskaton.asn4j.test.TestHelper.testRelativeOIDFailure;
import static ch.eskaton.asn4j.test.TestHelper.testRelativeOIDSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testSequenceOfFailure;
import static ch.eskaton.asn4j.test.TestHelper.testSequenceOfSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testSetOfFailure;
import static ch.eskaton.asn4j.test.TestHelper.testSetOfSuccess;

class TestX680_51_1 {

    @Test
    void testBitString1() {
        testBitStringSuccess(TestBitString1.class, new TestBitString1(), 0x00, 5);
        testBitStringSuccess(TestBitString1.class, new TestBitString1(), 0x01, 5);
        testBitStringSuccess(TestBitString1.class, new TestBitString1(), 0x02, 5);
        testBitStringSuccess(TestBitString1.class, new TestBitString1(), 0x04, 5);
        testBitStringSuccess(TestBitString1.class, new TestBitString1(), 0x05, 5);
        testBitStringSuccess(TestBitString1.class, new TestBitString1(), 0x06, 5);
        testBitStringSuccess(TestBitString1.class, new TestBitString1(), 0x07, 5);

        testBitStringFailure(TestBitString1.class, new TestBitString1(), 0x02, 6);
        testBitStringFailure(TestBitString1.class, new TestBitString1(), 0x03, 5);
        testBitStringFailure(TestBitString1.class, new TestBitString1(), 0x06, 4);
    }

    @Test
    void testBitString2() {
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x00, 7);
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x01, 7);
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x00, 6);
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x01, 6);
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x02, 6);
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x03, 6);
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x07, 5);

        testBitStringFailure(TestBitString2.class, new TestBitString2(), 0x00, 5);
        testBitStringFailure(TestBitString2.class, new TestBitString2(), 0x06, 5);
        testBitStringFailure(TestBitString2.class, new TestBitString2(), 0x08, 4);
    }

    @Test
    void testBitString3() {
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x00, 7);
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x01, 7);
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x00, 6);
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x01, 6);
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x02, 6);
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x03, 6);
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x00, 5);
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x07, 5);

        testBitStringFailure(TestBitString3.class, new TestBitString3(), 0x06, 5);
        testBitStringFailure(TestBitString3.class, new TestBitString3(), 0x08, 4);
    }

    @Test
    void testBitString5() {
        testBitStringSuccess(TestBitString5.class, new TestBitString5(), 0x00, 5);
        testBitStringSuccess(TestBitString5.class, new TestBitString5(), 0x01, 5);

        testBitStringFailure(TestBitString5.class, new TestBitString5(), 0x02, 5);
    }

    @Test
    void testBitString6() {
        testBitStringSuccess(TestBitString6.class, new TestBitString6(), 0x01, 0);
        testBitStringSuccess(TestBitString6.class, new TestBitString6(), 0x02, 4);
        testBitStringSuccess(TestBitString6.class, new TestBitString6(), 0x02, 6);
        testBitStringSuccess(TestBitString6.class, new TestBitString6(), 0xffee, 1);

        testBitStringFailure(TestBitString6.class, new TestBitString6(), 0x02, 5);
    }

    @Test
    void testBitString7() {
        testBitStringSuccess(TestBitString7.class, new TestBitString7(), 0x00, 5);
        testBitStringSuccess(TestBitString7.class, new TestBitString7(), 0x02, 5);
        testBitStringSuccess(TestBitString7.class, new TestBitString7(), 0x01, 6);

        testBitStringFailure(TestBitString7.class, new TestBitString7(), 0x01, 5);
    }

    @Test
    void testBoolean2() {
        testBooleanSuccess(TestBoolean2.class, new TestBoolean2(), false);

        testBooleanFailure(TestBoolean2.class, new TestBoolean2(), true);
    }

    @Test
    void testEnumeration2() {
        testEnumeratedSuccess(TestEnumeration2.class, new TestEnumeration2(), TestEnumeration1.D);
        testEnumeratedSuccess(TestEnumeration2.class, new TestEnumeration2(), TestEnumeration1.E);

        testEnumeratedFailure(TestEnumeration2.class, new TestEnumeration2(), TestEnumeration1.A);
        testEnumeratedFailure(TestEnumeration2.class, new TestEnumeration2(), TestEnumeration1.B);
        testEnumeratedFailure(TestEnumeration2.class, new TestEnumeration2(), TestEnumeration1.C);
        testEnumeratedFailure(TestEnumeration2.class, new TestEnumeration2(), TestEnumeration1.F);
    }

    @Test
    void testEnumeration3() {
        testEnumeratedSuccess(TestEnumeration3.class, new TestEnumeration3(), TestEnumeration1.A);
        testEnumeratedSuccess(TestEnumeration3.class, new TestEnumeration3(), TestEnumeration1.B);
        testEnumeratedSuccess(TestEnumeration3.class, new TestEnumeration3(), TestEnumeration1.E);
        testEnumeratedSuccess(TestEnumeration3.class, new TestEnumeration3(), TestEnumeration1.F);

        testEnumeratedFailure(TestEnumeration3.class, new TestEnumeration3(), TestEnumeration1.C);
        testEnumeratedFailure(TestEnumeration3.class, new TestEnumeration3(), TestEnumeration1.D);
    }

    @Test
    void testEnumeration4() {
        testEnumeratedSuccess(TestEnumeration4.class, new TestEnumeration4(), TestEnumeration1.D);

        testEnumeratedFailure(TestEnumeration4.class, new TestEnumeration4(), TestEnumeration1.A);
        testEnumeratedFailure(TestEnumeration4.class, new TestEnumeration4(), TestEnumeration1.B);
        testEnumeratedFailure(TestEnumeration4.class, new TestEnumeration4(), TestEnumeration1.C);
        testEnumeratedFailure(TestEnumeration4.class, new TestEnumeration4(), TestEnumeration1.E);
        testEnumeratedFailure(TestEnumeration4.class, new TestEnumeration4(), TestEnumeration1.F);
    }

    @Test
    void testInteger1() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger1.class, -1, 1);
        testIntegerFailure(TestInteger1.class, 0);
    }

    @Test
    void testInteger2() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger2.class, -2, 2);
        testIntegerFailure(TestInteger2.class, -1, 0, 1);
    }

    @Test
    void testInteger3() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger3.class, 0, 1, 2, 4, 5, 6, 9);
        testIntegerFailure(TestInteger3.class, -1, 3, 7, 8, 10);
    }

    @Test
    void testInteger4() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger4.class, 1, 2, 4, 5);
        testIntegerFailure(TestInteger4.class, 0, 3, 6);
    }

    @Test
    void testInteger5() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger5.class, 5, 6);
        testIntegerFailure(TestInteger5.class, 4, 7, 8, 9);
    }

    @Test
    void testInteger6() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger6.class, 4, 5);
        testIntegerFailure(TestInteger6.class, 0, 1, 2, 3, 6);
    }

    @Test
    void testInteger7() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger7.class, 5);
        testIntegerFailure(TestInteger7.class, 0, 1, 2, 3, 4, 6);
    }

    @Test
    void testInteger8() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger8.class, 6);
        testIntegerFailure(TestInteger8.class, 4, 5, 7, 8, 9);
    }

    @Test
    void testInteger9() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger9.class, 2, 4);
        testIntegerFailure(TestInteger9.class, 3);
    }

    @Test
    void testInteger10() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger10.class, 0, 2);
        testIntegerFailure(TestInteger10.class, 1);
    }

    @Test
    void testInteger11() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger11.class, 3, 5);
        testIntegerFailure(TestInteger11.class, 4);
    }

    @Test
    void testNull1() {
        testNullSuccess(TestNull1.class, () -> new TestNull1());
    }

    @Test
    void testNull2() {
        testNullFailure(TestNull2.class, () -> new TestNull2());
    }

    @Test
    void testObjectIdentifier2() {
        testObjectIdentifierSuccess(TestObjectIdentifier2.class, new TestObjectIdentifier2(), 0, 3, 6, 3);

        testObjectIdentifierFailure(TestObjectIdentifier2.class, new TestObjectIdentifier2(), 0, 3, 6, 4);
    }

    @Test
    void testRelativeOID2() {
        testRelativeOIDSuccess(TestRelativeOID2.class, new TestRelativeOID2(), 3, 6, 3);

        testRelativeOIDFailure(TestRelativeOID2.class, new TestRelativeOID2(), 3, 6, 4);
    }

    @Test
    void testOIDIRI2() {
        testIRISuccess(TestOidIri2.class, new TestOidIri2(), "ISO", "a", "b", "b");

        testIRIFailure(TestOidIri2.class, new TestOidIri2(), "ISO", "a", "b", "a");
    }

    @Test
    void testRelativeOIDIRI2() {
        testRelativeIRISuccess(TestRelativeOidIri2.class, new TestRelativeOidIri2(), "a", "b", "b");

        testRelativeIRIFailure(TestRelativeOidIri2.class, new TestRelativeOidIri2(), "a", "b", "a");
    }

    @Test
    void testOctetString1() {
        testOctetStringSuccess(TestOctetString1.class, new TestOctetString1(), new byte[] { 0x01, 0x02, 0x02 });
        testOctetStringSuccess(TestOctetString1.class, new TestOctetString1(), new byte[] { 0x01, 0x02, 0x04 });

        testOctetStringFailure(TestOctetString1.class, new TestOctetString1(), randomBytes(2));
        testOctetStringFailure(TestOctetString1.class, new TestOctetString1(), randomBytes(4));
        testOctetStringFailure(TestOctetString1.class, new TestOctetString1(), new byte[] { 0x01, 0x02, 0x03 });
    }

    @Test
    void testOctetString2() {
        testOctetStringSuccess(TestOctetString2.class, new TestOctetString2(), new byte[] { 0x01, 0x02, 0x03 });
        testOctetStringSuccess(TestOctetString2.class, new TestOctetString2(), randomBytes(1));
        testOctetStringSuccess(TestOctetString2.class, new TestOctetString2(), randomBytes(2));

        testOctetStringFailure(TestOctetString2.class, new TestOctetString2(), new byte[] { 0x01, 0x02, 0x02 });
        testOctetStringFailure(TestOctetString2.class, new TestOctetString2(), new byte[] { 0x01, 0x02, 0x04 });
        testOctetStringFailure(TestOctetString2.class, new TestOctetString2(), new byte[] {});
        testOctetStringFailure(TestOctetString2.class, new TestOctetString2(), randomBytes(3));
    }

    @Test
    void testOctetString3() {
        testOctetStringSuccess(TestOctetString3.class, new TestOctetString3(), new byte[] { 0x01, 0x02, 0x03 });
        testOctetStringSuccess(TestOctetString3.class, new TestOctetString3(), new byte[] { 0x01, 0x02, 0x03 });
        testOctetStringSuccess(TestOctetString3.class, new TestOctetString3(), new byte[] { 0x0A, 0x0B, 0x0C });
        testOctetStringSuccess(TestOctetString3.class, new TestOctetString3(), randomBytes(1));
        testOctetStringSuccess(TestOctetString3.class, new TestOctetString3(), randomBytes(2));

        testOctetStringFailure(TestOctetString3.class, new TestOctetString3(), new byte[] { 0x01, 0x02, 0x02 });
        testOctetStringFailure(TestOctetString3.class, new TestOctetString3(), new byte[] { 0x01, 0x02, 0x04 });
        testOctetStringFailure(TestOctetString3.class, new TestOctetString3(), new byte[] {});
        testOctetStringFailure(TestOctetString3.class, new TestOctetString3(), randomBytes(3));
    }

    @Test
    void testOctetString5() {
        testOctetStringSuccess(TestOctetString5.class, new TestOctetString5(), new byte[] { 0x00 });
        testOctetStringSuccess(TestOctetString5.class, new TestOctetString5(), new byte[] { 0x0F });

        testOctetStringFailure(TestOctetString5.class, new TestOctetString5(), new byte[] { (byte) 0xF0 });
    }

    @Test
    void testOctetString6() {
        testOctetStringSuccess(TestOctetString6.class, new TestOctetString6(), new byte[] {});
        testOctetStringSuccess(TestOctetString6.class, new TestOctetString6(), new byte[] { (byte) 0xEF });
        testOctetStringSuccess(TestOctetString6.class, new TestOctetString6(), new byte[] { (byte) 0xF1 });

        testOctetStringFailure(TestOctetString6.class, new TestOctetString6(), new byte[] { (byte) 0xF0 });
    }

    @Test
    void testOctetString7() {
        testOctetStringSuccess(TestOctetString7.class, new TestOctetString7(), new byte[] {});
        testOctetStringSuccess(TestOctetString7.class, new TestOctetString7(), new byte[] { 0x0E });
        testOctetStringSuccess(TestOctetString7.class, new TestOctetString7(), new byte[] { 0x10 });

        testOctetStringFailure(TestOctetString7.class, new TestOctetString7(), new byte[] { (byte) 0x0F });
    }

    @Test
    void testSetOf1() {
        testSetOfSuccess(TestSetOf1.class, new TestSetOf1(), ASN1Integer.valueOf(0L));
        testSetOfSuccess(TestSetOf1.class, new TestSetOf1(), ASN1Integer.valueOf(2L));
        testSetOfSuccess(TestSetOf1.class, new TestSetOf1(), ASN1Integer.valueOf(1L), ASN1Integer.valueOf(2L));

        testSetOfFailure(TestSetOf1.class, new TestSetOf1(), ASN1Integer.valueOf(1));
    }

    @Test
    void testSetOf2() {
        testSetOfSuccess(TestSetOf2.class, new TestSetOf2(), ASN1Integer.valueOf(1L));
        testSetOfSuccess(TestSetOf2.class, new TestSetOf2(), ASN1Integer.valueOf(1L), ASN1Integer.valueOf(2L));
        testSetOfSuccess(TestSetOf2.class, new TestSetOf2(), ASN1Integer.valueOf(0L), ASN1Integer.valueOf(3L));

        testSetOfFailure(TestSetOf2.class, new TestSetOf2(), ASN1Integer.valueOf(0L));
        testSetOfFailure(TestSetOf2.class, new TestSetOf2(), ASN1Integer.valueOf(2L));
    }

    @Test
    void testSetOf3() {
        testSetOfSuccess(TestSetOf3.class, new TestSetOf3(), ASN1Integer.valueOf(1L), ASN1Integer.valueOf(2L));
        testSetOfSuccess(TestSetOf3.class, new TestSetOf3(), ASN1Integer.valueOf(0L), ASN1Integer.valueOf(3L));

        testSetOfFailure(TestSetOf3.class, new TestSetOf3(), ASN1Integer.valueOf(0L));
        testSetOfFailure(TestSetOf3.class, new TestSetOf3(), ASN1Integer.valueOf(1L));
        testSetOfFailure(TestSetOf3.class, new TestSetOf3(), ASN1Integer.valueOf(2L));
    }

    @Test
    void TestSetOf4() {
        testSetOfSuccess(TestSetOf4.class, new TestSetOf4(),
                new ASN1SetOf<>(ASN1Integer.valueOf(1L)),
                new ASN1SetOf<>(ASN1Integer.valueOf(2L)));
        testSetOfSuccess(TestSetOf4.class, new TestSetOf4(),
                new ASN1SetOf<>(ASN1Integer.valueOf(1L), ASN1Integer.valueOf(2L)),
                new ASN1SetOf<>(ASN1Integer.valueOf(3L), ASN1Integer.valueOf(4L)));
        testSetOfSuccess(TestSetOf4.class, new TestSetOf4(),
                new ASN1SetOf<>(ASN1Integer.valueOf(1L), ASN1Integer.valueOf(2L)),
                new ASN1SetOf<>(ASN1Integer.valueOf(3L)));

        testSetOfFailure(TestSetOf4.class, new TestSetOf4(), new ASN1SetOf<>(), new ASN1SetOf<>());
        testSetOfFailure(TestSetOf4.class, new TestSetOf4(),
                new ASN1SetOf<>(ASN1Integer.valueOf(4710L), ASN1Integer.valueOf(4711L)));
        testSetOfFailure(TestSetOf4.class, new TestSetOf4(),
                new ASN1SetOf<>(ASN1Integer.valueOf(1L), ASN1Integer.valueOf(2L)),
                new ASN1SetOf<>());
        testSetOfFailure(TestSetOf4.class, new TestSetOf4(),
                new ASN1SetOf<>(),
                new ASN1SetOf<>(ASN1Integer.valueOf(1L), ASN1Integer.valueOf(2L)));
    }

    @Test
    void testSetOf5() {
        testSetOfSuccess(TestSetOf5.class, new TestSetOf5(), ASN1Integer.valueOf(1L), ASN1Integer.valueOf(2L));

        testSetOfFailure(TestSetOf5.class, new TestSetOf5(), ASN1Integer.valueOf(1L));
        testSetOfFailure(TestSetOf5.class, new TestSetOf5(), ASN1Integer.valueOf(1L), ASN1Integer.valueOf(3L));
    }

    @Test
    void testSetOf6() {
        testSetOfSuccess(TestSetOf6.class, new TestSetOf6(), new ASN1BitString(new byte[] { 0x02 }, 5),
                new ASN1BitString(new byte[] { 0x03 }, 5));

        testSetOfFailure(TestSetOf6.class, new TestSetOf6(), new ASN1BitString(new byte[] { 0x02 }, 5));
        testSetOfFailure(TestSetOf6.class, new TestSetOf6(), new ASN1BitString(new byte[] { 0x04 }, 4),
                new ASN1BitString(new byte[] { 0x02 }, 5));
    }

    @Test
    void testSetOf7() {
        testSetOfSuccess(TestSetOf7.class, new TestSetOf7(), ASN1Integer.valueOf(1L));
        testSetOfSuccess(TestSetOf7.class, new TestSetOf7(), ASN1Integer.valueOf(2L));

        testSetOfFailure(TestSetOf7.class, new TestSetOf7());
        testSetOfFailure(TestSetOf7.class, new TestSetOf7(), ASN1Integer.valueOf(1L), ASN1Integer.valueOf(2L));
        testSetOfFailure(TestSetOf7.class, new TestSetOf7(), ASN1Integer.valueOf(0L));
        testSetOfFailure(TestSetOf7.class, new TestSetOf7(), ASN1Integer.valueOf(3L));
    }

    @Test
    void testSetOf8() {
        testSetOfSuccess(TestSetOf8.class, new TestSetOf8());
        testSetOfSuccess(TestSetOf8.class, new TestSetOf8(), ASN1Integer.valueOf(1L));
        testSetOfSuccess(TestSetOf8.class, new TestSetOf8(), ASN1Integer.valueOf(2L));
        testSetOfSuccess(TestSetOf8.class, new TestSetOf8(), ASN1Integer.valueOf(1L), ASN1Integer.valueOf(2L));

        testSetOfFailure(TestSetOf8.class, new TestSetOf8(), ASN1Integer.valueOf(0L));
        testSetOfFailure(TestSetOf8.class, new TestSetOf8(), ASN1Integer.valueOf(3L));
    }

    @Test
    void testSetOf9() {
        testSetOfSuccess(TestSetOf9.class, new TestSetOf9());
        testSetOfSuccess(TestSetOf9.class, new TestSetOf9(), ASN1Integer.valueOf(2L));
        testSetOfSuccess(TestSetOf9.class, new TestSetOf9(), ASN1Integer.valueOf(3L));
        testSetOfSuccess(TestSetOf9.class, new TestSetOf9(), ASN1Integer.valueOf(2L), ASN1Integer.valueOf(3L));

        testSetOfFailure(TestSetOf9.class, new TestSetOf9(), ASN1Integer.valueOf(1L));
        testSetOfFailure(TestSetOf9.class, new TestSetOf9(), ASN1Integer.valueOf(4L));
    }

    @Test
    void testSetOf10() {
        testSetOfSuccess(TestSetOf10.class, new TestSetOf10());
        testSetOfSuccess(TestSetOf10.class, new TestSetOf10(), new ASN1SetOf<>());
        testSetOfSuccess(TestSetOf10.class, new TestSetOf10(),
                new ASN1SetOf<>(ASN1Integer.valueOf(1L)),
                new ASN1SetOf<>(ASN1Integer.valueOf(2L)));
        testSetOfSuccess(TestSetOf10.class, new TestSetOf10(),
                new ASN1SetOf<>(ASN1Integer.valueOf(1L), ASN1Integer.valueOf(2L)),
                new ASN1SetOf<>());

        testSetOfFailure(TestSetOf10.class, new TestSetOf10(), new ASN1SetOf<>(ASN1Integer.valueOf(0)));
        testSetOfFailure(TestSetOf10.class, new TestSetOf10(), new ASN1SetOf<>(ASN1Integer.valueOf(3)));
    }

    @Test
    void testSequenceOf1() {
        testSequenceOfSuccess(TestSequenceOf1.class, new TestSequenceOf1(),
                new ASN1SetOf<>(ASN1Integer.valueOf(1L)),
                new ASN1SetOf<>(ASN1Integer.valueOf(2L)));
        testSequenceOfSuccess(TestSequenceOf1.class, new TestSequenceOf1(),
                new ASN1SetOf<>(ASN1Integer.valueOf(1L), ASN1Integer.valueOf(2L)),
                new ASN1SetOf<>(ASN1Integer.valueOf(3L), ASN1Integer.valueOf(4L)));
        testSequenceOfSuccess(TestSequenceOf1.class, new TestSequenceOf1(),
                new ASN1SetOf<>(ASN1Integer.valueOf(1L), ASN1Integer.valueOf(2L)),
                new ASN1SetOf<>(ASN1Integer.valueOf(3L)));

        testSequenceOfFailure(TestSequenceOf1.class, new TestSequenceOf1(), new ASN1SetOf<>(), new ASN1SetOf<>());
        testSequenceOfFailure(TestSequenceOf1.class, new TestSequenceOf1(),
                new ASN1SetOf<>(ASN1Integer.valueOf(4710L), ASN1Integer.valueOf(4711L)));
        testSequenceOfFailure(TestSequenceOf1.class, new TestSequenceOf1(),
                new ASN1SetOf<>(ASN1Integer.valueOf(1L), ASN1Integer.valueOf(2L)),
                new ASN1SetOf<>());
        testSequenceOfFailure(TestSequenceOf1.class, new TestSequenceOf1(),
                new ASN1SetOf<>(),
                new ASN1SetOf<>(ASN1Integer.valueOf(1L), ASN1Integer.valueOf(2L)));
    }

    @Test
    void testSequenceOf2() {
        testSequenceOfSuccess(TestSequenceOf2.class, new TestSequenceOf2(),
                new ASN1SetOf<>(ASN1Integer.valueOf(1L)),
                new ASN1SetOf<>(ASN1Integer.valueOf(2L)));
        testSequenceOfSuccess(TestSequenceOf2.class, new TestSequenceOf2(),
                new ASN1SetOf<>(ASN1Integer.valueOf(1L), ASN1Integer.valueOf(2L)),
                new ASN1SetOf<>(ASN1Integer.valueOf(3L), ASN1Integer.valueOf(4L)));
        testSequenceOfSuccess(TestSequenceOf2.class, new TestSequenceOf2(),
                new ASN1SetOf<>(ASN1Integer.valueOf(1L), ASN1Integer.valueOf(2L)),
                new ASN1SetOf<>(ASN1Integer.valueOf(3L)));

        testSequenceOfFailure(TestSequenceOf2.class, new TestSequenceOf2(), new ASN1SetOf<>(), new ASN1SetOf<>());
        testSequenceOfFailure(TestSequenceOf2.class, new TestSequenceOf2(),
                new ASN1SetOf<>(ASN1Integer.valueOf(4710L), ASN1Integer.valueOf(4711L)));
        testSequenceOfFailure(TestSequenceOf2.class, new TestSequenceOf2(),
                new ASN1SetOf<>(ASN1Integer.valueOf(1L), ASN1Integer.valueOf(2L)),
                new ASN1SetOf<>());
        testSequenceOfFailure(TestSequenceOf2.class, new TestSequenceOf2(),
                new ASN1SetOf<>(),
                new ASN1SetOf<>(ASN1Integer.valueOf(1L), ASN1Integer.valueOf(2L)));
    }

}
