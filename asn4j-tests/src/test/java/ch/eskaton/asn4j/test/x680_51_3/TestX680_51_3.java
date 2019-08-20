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

import ch.eskaton.asn4j.test.modules.x680_51_3.TestBitString2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestBitString3;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestBitString4;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestBoolean2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestEnumeration1;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestEnumeration2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestInteger1;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestInteger2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestNull1;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestNull2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestNull3;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestNull4;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestNull5;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestNull6;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestObjectIdentifier2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestOctetString1;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestOctetString2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestOctetString3;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestOidIri1;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestOidIri2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestRelativeOID2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestRelativeOidIri1;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestRelativeOidIri2;
import ch.eskaton.asn4j.test.modules.x680_51_3.TestRelativeOidIri3;
import org.junit.Test;

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

public class TestX680_51_3 {

    @Test
    public void testBitString2() {
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x00, 5);
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x01, 5);
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x02, 5);

        testBitStringFailure(new TestBitString2(), 0x03, 5);
        testBitStringFailure(new TestBitString2(), 0x00, 4);
        testBitStringFailure(new TestBitString2(), 0x00, 6);
    }

    @Test
    public void testBitString3() {
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x00, 5);
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x01, 5);
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x02, 5);
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x03, 6);
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x03, 4);
    }

    @Test
    public void testBitString4() {
        testBitStringSuccess(TestBitString4.class, new TestBitString4(), 0x00, 0);
        testBitStringSuccess(TestBitString4.class, new TestBitString4(), 0xffee, 1);
    }

    @Test
    public void testBoolean2() {
        testBooleanSuccess(TestBoolean2.class, new TestBoolean2(), true);

        testBooleanFailure(new TestBoolean2(), false);
    }

    @Test
    public void testEnumeration2() {
        testEnumeratedSuccess(TestEnumeration2.class, new TestEnumeration2(), TestEnumeration1.B);
        testEnumeratedSuccess(TestEnumeration2.class, new TestEnumeration2(), TestEnumeration1.C);
        testEnumeratedSuccess(TestEnumeration2.class, new TestEnumeration2(), TestEnumeration1.D);

        testEnumeratedFailure(new TestEnumeration2(), TestEnumeration1.A);
    }

    @Test
    public void testInteger1() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger1.class, 1, 2, 4);
        testIntegerFailure(TestInteger1.class, 0, 3, 5);
    }

    @Test
    public void testInteger2() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger2.class, 1, 2, 4);
        testIntegerFailure(TestInteger2.class, 0, 3, 5);
    }

    @Test
    public void testNull1() {
        testNullSuccess(TestNull1.class, () -> new TestNull1());
    }

    @Test
    public void testNull2() {
        testNullSuccess(TestNull2.class, () -> new TestNull2());
    }

    @Test
    public void testNull3() {
        testNullFailure(() -> new TestNull3());
    }

    @Test
    public void testNull4() {
        testNullFailure(() -> new TestNull4());
    }

    @Test
    public void testNull5() {
        testNullSuccess(TestNull5.class, () -> new TestNull5());
    }

    @Test
    public void testNull6() {
        testNullSuccess(TestNull6.class, () -> new TestNull6());
    }

    @Test
    public void testObjectIdentifier2() {
        testObjectIdentifierSuccess(TestObjectIdentifier2.class, new TestObjectIdentifier2(), 0, 3, 6, 1);
        testObjectIdentifierSuccess(TestObjectIdentifier2.class, new TestObjectIdentifier2(), 0, 3, 6, 2);

        testObjectIdentifierFailure(new TestObjectIdentifier2(), 0, 3, 6, 3);
    }

    @Test
    public void testRelativeOID2() {
        testRelativeOIDSuccess(TestRelativeOID2.class, new TestRelativeOID2(), 7, 3, 6, 1);
        testRelativeOIDSuccess(TestRelativeOID2.class, new TestRelativeOID2(), 7, 3, 6, 2);

        testRelativeOIDFailure(new TestRelativeOID2(), 7, 3, 6, 3);
    }

    @Test
    public void testOIDIRI1() {
        testIRISuccess(TestOidIri1.class, new TestOidIri1(), "ISO", "a", "b", "a");
        testIRISuccess(TestOidIri1.class, new TestOidIri1(), "ISO", "a", "b", "b");

        testIRIFailure(new TestOidIri1(), "ISO", "a", "b", "c");
    }

    @Test
    public void testOIDIRI2() {
        testIRISuccess(TestOidIri2.class, new TestOidIri2(), "ISO", "a", "b", "a");
        testIRISuccess(TestOidIri2.class, new TestOidIri2(), "ISO", "a", "b", "b");

        testIRIFailure(new TestOidIri2(), "ISO", "a", "b", "c");
    }

    @Test
    public void testRelativeOIDIRI1() {
        testRelativeIRISuccess(TestRelativeOidIri1.class, new TestRelativeOidIri1(), "a", "b", "a");
        testRelativeIRISuccess(TestRelativeOidIri1.class, new TestRelativeOidIri1(), "a", "b", "b");

        testRelativeIRIFailure(new TestRelativeOidIri1(), "a", "b", "c");
    }

    @Test
    public void testRelativeOIDIRI2() {
        testRelativeIRISuccess(TestRelativeOidIri2.class, new TestRelativeOidIri2(), "a", "b", "a");
        testRelativeIRISuccess(TestRelativeOidIri2.class, new TestRelativeOidIri2(), "a", "b", "b");

        testRelativeIRIFailure(new TestRelativeOidIri2(), "a", "b", "c");
    }

    @Test
    public void testRelativeOIDIRI3() {
        testRelativeIRISuccess(TestRelativeOidIri3.class, new TestRelativeOidIri3(), "a", "b", "b");

        testRelativeIRIFailure(new TestRelativeOidIri3(), "a", "b", "a");
    }

    @Test
    public void testOctetString1() {
        testOctetStringSuccess(TestOctetString1.class, new TestOctetString1(), new byte[] { 0x50 });
        testOctetStringSuccess(TestOctetString1.class, new TestOctetString1(), new byte[] { 0x61, (byte) 0xAD });

        testOctetStringFailure(new TestOctetString1(), new byte[] { 0x51 });
    }

    @Test
    public void testOctetString2() {
        testOctetStringSuccess(TestOctetString2.class, new TestOctetString2(), new byte[] { 0x50 });
        testOctetStringSuccess(TestOctetString2.class, new TestOctetString2(), new byte[] { 0x61, (byte) 0xAD });

        testOctetStringFailure(new TestOctetString2(), new byte[] { 0x51 });
    }

    @Test
    public void testOctetString3() {
        testOctetStringSuccess(TestOctetString3.class, new TestOctetString3(), new byte[] { 0x61, (byte) 0xAD });

        testOctetStringFailure(new TestOctetString3(), new byte[] { 0x50 });
    }

}
