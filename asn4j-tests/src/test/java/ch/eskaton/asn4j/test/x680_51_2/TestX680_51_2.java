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

package ch.eskaton.asn4j.test.x680_51_2;

import ch.eskaton.asn4j.runtime.types.ASN1BitString;
import ch.eskaton.asn4j.runtime.types.ASN1Boolean;
import ch.eskaton.asn4j.runtime.types.ASN1IRI;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1Null;
import ch.eskaton.asn4j.runtime.types.ASN1ObjectIdentifier;
import ch.eskaton.asn4j.runtime.types.ASN1OctetString;
import ch.eskaton.asn4j.runtime.types.ASN1RelativeIRI;
import ch.eskaton.asn4j.runtime.types.ASN1RelativeOID;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestBitString1;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestBitString2;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestBitString3;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestBitString4;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestBitString5;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestBitString6;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestBoolean1;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestBoolean2;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestBoolean3;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestBoolean4;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestBoolean5;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestBoolean6;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestEnumeration1;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestEnumeration2;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestEnumeration3;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestEnumeration4;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestInteger1;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestInteger2;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestInteger3;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestNull1;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestNull2;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestNull3;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestNull4;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestObjectIdentifier1;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestObjectIdentifier2;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestObjectIdentifier3;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestObjectIdentifier4;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestObjectIdentifier5;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestOctetString1;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestOctetString2;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestOctetString3;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestOctetString4;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestOidIri1;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestOidIri2;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestOidIri3;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestOidIri4;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestOidIri5;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestRelativeOID1;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestRelativeOID2;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestRelativeOID3;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestRelativeOID4;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestRelativeOID5;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestRelativeOidIri1;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestRelativeOidIri2;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestRelativeOidIri3;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestRelativeOidIri4;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestRelativeOidIri5;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestSetOf1;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestSetOf2;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestSetOf3;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestSetOf4;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestSetOf5;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestSetOf6;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestSetOfBitString1;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestSetOfBoolean1;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestSetOfEnumeration1;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestSetOfEnumeration2;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestSetOfNull1;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestSetOfNull2;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestSetOfObjectIdentifier1;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestSetOfObjectIdentifier2;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestSetOfOctetString1;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestSetOfOctetString2;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestSetOfOidIri1;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestSetOfOidIri2;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestSetOfRelativeOID1;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestSetOfRelativeOID2;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestSetOfRelativeOidIri1;
import ch.eskaton.asn4j.test.modules.x680_51_2.TestSetOfRelativeOidIri2;
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
import static ch.eskaton.asn4j.test.TestHelper.testSetOfFailure;
import static ch.eskaton.asn4j.test.TestHelper.testSetOfSuccess;

public class TestX680_51_2 {

    @Test
    public void testBitString1() {
        testBitStringSuccess(TestBitString1.class, new TestBitString1(), 0x03, 5);
        testBitStringSuccess(TestBitString1.class, new TestBitString1(), 0x05, 5);
        testBitStringSuccess(TestBitString1.class, new TestBitString1(), 0x03, 4);
        testBitStringSuccess(TestBitString1.class, new TestBitString1(), 0x06, 6);

        testBitStringFailure(TestBitString1.class, new TestBitString1(), 0x04, 5);
    }

    @Test
    public void testBitString2() {
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x00, 5);
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x01, 5);
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x03, 5);
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x04, 5);
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x05, 5);
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x06, 5);
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x07, 5);

        testBitStringFailure(TestBitString2.class, new TestBitString2(), 0x02, 5);
        testBitStringFailure(TestBitString2.class, new TestBitString2(), 0x08, 4);
        testBitStringFailure(TestBitString2.class, new TestBitString2(), 0x00, 4);
        testBitStringFailure(TestBitString2.class, new TestBitString2(), 0x00, 6);
    }

    @Test
    public void testBitString3() {
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x01, 5);
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x03, 5);

        testBitStringFailure(TestBitString3.class, new TestBitString3(), 0x00, 5);
        testBitStringFailure(TestBitString3.class, new TestBitString3(), 0x02, 5);
        testBitStringFailure(TestBitString3.class, new TestBitString3(), 0x04, 5);
        testBitStringFailure(TestBitString3.class, new TestBitString3(), 0x01, 4);
        testBitStringFailure(TestBitString3.class, new TestBitString3(), 0x01, 6);
    }

    @Test
    public void testBitString4() {
        testBitStringSuccess(TestBitString4.class, new TestBitString4(), 0x00, 5);
        testBitStringSuccess(TestBitString4.class, new TestBitString4(), 0x01, 5);
        testBitStringSuccess(TestBitString4.class, new TestBitString4(), 0x02, 5);
        testBitStringSuccess(TestBitString4.class, new TestBitString4(), 0x03, 6);
        testBitStringSuccess(TestBitString4.class, new TestBitString4(), 0x03, 4);
    }

    @Test
    public void testBitString5() {
        testBitStringSuccess(TestBitString5.class, new TestBitString5(), 0x00, 5);
        testBitStringSuccess(TestBitString5.class, new TestBitString5(), 0x01, 5);
        testBitStringSuccess(TestBitString5.class, new TestBitString5(), 0x02, 5);
        testBitStringSuccess(TestBitString5.class, new TestBitString5(), 0x03, 6);
        testBitStringSuccess(TestBitString5.class, new TestBitString5(), 0x03, 4);
    }

    @Test
    public void testBitString6() {
        testBitStringSuccess(TestBitString6.class, new TestBitString6(), 0x02, 5);

        testBitStringFailure(TestBitString6.class, new TestBitString6(), 0x01, 5);
        testBitStringFailure(TestBitString6.class, new TestBitString6(), 0x03, 5);
    }

    @Test
    public void testBoolean1() {
        testBooleanSuccess(TestBoolean1.class, new TestBoolean1(), false);
        testBooleanFailure(TestBoolean1.class, new TestBoolean1(), true);
    }

    @Test
    public void testBoolean2() {
        testBooleanSuccess(TestBoolean2.class, new TestBoolean2(), true);
        testBooleanFailure(TestBoolean2.class, new TestBoolean2(), false);
    }

    @Test
    public void testBoolean3() {
        testBooleanSuccess(TestBoolean3.class, new TestBoolean3(), false);
        testBooleanFailure(TestBoolean3.class, new TestBoolean3(), true);
    }

    @Test
    public void testBoolean4() {
        testBooleanSuccess(TestBoolean4.class, new TestBoolean4(), true);
        testBooleanSuccess(TestBoolean4.class, new TestBoolean4(), false);
    }

    @Test
    public void testBoolean5() {
        testBooleanSuccess(TestBoolean5.class, new TestBoolean5(), true);

        testBooleanFailure(TestBoolean5.class, new TestBoolean5(), false);
    }

    @Test
    public void testBoolean6() {
        testBooleanSuccess(TestBoolean6.class, new TestBoolean6(), true);

        testBooleanFailure(TestBoolean6.class, new TestBoolean6(), false);
    }

    @Test
    public void testEnumeration2() {
        testEnumeratedSuccess(TestEnumeration2.class, new TestEnumeration2(), TestEnumeration1.A);
        testEnumeratedSuccess(TestEnumeration2.class, new TestEnumeration2(), TestEnumeration1.B);

        testEnumeratedFailure(TestEnumeration2.class, new TestEnumeration2(), TestEnumeration1.C);
        testEnumeratedFailure(TestEnumeration2.class, new TestEnumeration2(), TestEnumeration1.D);
    }

    @Test
    public void testEnumeration3() {
        testEnumeratedSuccess(TestEnumeration3.class, new TestEnumeration3(), TestEnumeration1.B);
        testEnumeratedSuccess(TestEnumeration3.class, new TestEnumeration3(), TestEnumeration1.C);

        testEnumeratedFailure(TestEnumeration3.class, new TestEnumeration3(), TestEnumeration1.A);
        testEnumeratedFailure(TestEnumeration3.class, new TestEnumeration3(), TestEnumeration1.D);
    }

    @Test
    public void testEnumeration4() {
        testEnumeratedSuccess(TestEnumeration4.class, new TestEnumeration4(), TestEnumeration1.A);
        testEnumeratedSuccess(TestEnumeration4.class, new TestEnumeration4(), TestEnumeration1.B);
        testEnumeratedSuccess(TestEnumeration4.class, new TestEnumeration4(), TestEnumeration1.C);

        testEnumeratedFailure(TestEnumeration4.class, new TestEnumeration4(), TestEnumeration1.D);
    }

    @Test
    public void testInteger1() {
        testIntegerSuccess(TestInteger1.class, new TestInteger1(), 1);
        testIntegerSuccess(TestInteger1.class, new TestInteger1(), 2);

        testIntegerFailure(TestInteger1.class, new TestInteger1(), 0);
        testIntegerFailure(TestInteger1.class, new TestInteger1(), 3);
    }

    @Test
    public void testInteger2() {
        testIntegerSuccess(TestInteger2.class, new TestInteger2(), -1);
        testIntegerSuccess(TestInteger2.class, new TestInteger2(), 1);

        testIntegerFailure(TestInteger2.class, new TestInteger2(), 0);
    }

    @Test
    public void testInteger3() {
        testIntegerSuccess(TestInteger3.class, new TestInteger3(), 2);

        testIntegerFailure(TestInteger3.class, new TestInteger3(), 1);
        testIntegerFailure(TestInteger3.class, new TestInteger3(), 3);
    }

    @Test
    public void testNull1() {
        testNullSuccess(TestNull1.class, () -> new TestNull1());
    }

    @Test
    public void testNull2() {
        testNullFailure(TestNull2.class, () -> new TestNull2());
    }

    @Test
    public void testNull3() {
        testNullFailure(TestNull3.class, () -> new TestNull3());
    }

    @Test
    public void testObjectIdentifier1() {
        testObjectIdentifierSuccess(TestObjectIdentifier1.class, new TestObjectIdentifier1(), 1, 3, 6, 1);
        testObjectIdentifierSuccess(TestObjectIdentifier1.class, new TestObjectIdentifier1(), 1, 3, 6, 2);

        testObjectIdentifierFailure(TestObjectIdentifier1.class, new TestObjectIdentifier1(), 1, 3, 6, 0);
    }

    @Test
    public void testObjectIdentifier2() {
        testObjectIdentifierSuccess(TestObjectIdentifier2.class, new TestObjectIdentifier2(), 1, 3, 6, 1);

        testObjectIdentifierFailure(TestObjectIdentifier2.class, new TestObjectIdentifier2(), 1, 3, 6, 2);
    }

    @Test
    public void testObjectIdentifier3() {
        testObjectIdentifierSuccess(TestObjectIdentifier3.class, new TestObjectIdentifier3(), 1, 3, 6, 2);

        testObjectIdentifierFailure(TestObjectIdentifier3.class, new TestObjectIdentifier3(), 1, 3, 6, 1);
    }

    @Test
    public void testObjectIdentifier4() {
        testObjectIdentifierSuccess(TestObjectIdentifier4.class, new TestObjectIdentifier4(), 1, 3, 6, 1);

        testObjectIdentifierFailure(TestObjectIdentifier4.class, new TestObjectIdentifier4(), 1, 3, 6, 2);
    }

    @Test
    public void testObjectIdentifier5() {
        testObjectIdentifierSuccess(TestObjectIdentifier5.class, new TestObjectIdentifier5(), 0, 4, 11, 2);

        testObjectIdentifierFailure(TestObjectIdentifier5.class, new TestObjectIdentifier5(), 1, 3, 6, 2);
    }

    @Test
    public void testRelativeOID1() {
        testRelativeOIDSuccess(TestRelativeOID1.class, new TestRelativeOID1(), 3, 6, 1);
        testRelativeOIDSuccess(TestRelativeOID1.class, new TestRelativeOID1(), 3, 6, 2);

        testRelativeOIDFailure(TestRelativeOID1.class, new TestRelativeOID1(), 3, 6, 0);
    }

    @Test
    public void testRelativeOID2() {
        testRelativeOIDSuccess(TestRelativeOID2.class, new TestRelativeOID2(), 3, 6, 1);

        testRelativeOIDFailure(TestRelativeOID2.class, new TestRelativeOID2(), 3, 6, 2);
    }

    @Test
    public void testRelativeOID3() {
        testRelativeOIDSuccess(TestRelativeOID3.class, new TestRelativeOID3(), 3, 6, 2);

        testRelativeOIDFailure(TestRelativeOID3.class, new TestRelativeOID3(), 3, 6, 1);
    }

    @Test
    public void testRelativeOID4() {
        testRelativeOIDSuccess(TestRelativeOID4.class, new TestRelativeOID4(), 3, 6, 1);

        testRelativeOIDFailure(TestRelativeOID4.class, new TestRelativeOID4(), 3, 6, 2);
    }

    @Test
    public void testRelativeOID5() {
        testRelativeOIDSuccess(TestRelativeOID5.class, new TestRelativeOID5(), 4, 1, 11, 2);

        testRelativeOIDFailure(TestRelativeOID5.class, new TestRelativeOID5(), 4, 1, 11, 1);
    }

    @Test
    public void testOIDIRI1() {
        testIRISuccess(TestOidIri1.class, new TestOidIri1(), "ISO", "a", "b", "c");
        testIRISuccess(TestOidIri1.class, new TestOidIri1(), "ISO", "a", "b", "d");

        testIRIFailure(TestOidIri1.class, new TestOidIri1(), "ISO", "a", "b", "e");
    }

    @Test
    public void testOIDIRI2() {
        testIRISuccess(TestOidIri2.class, new TestOidIri2(), "ISO", "a", "b", "d");

        testIRIFailure(TestOidIri2.class, new TestOidIri2(), "ISO", "a", "b", "c");
    }

    @Test
    public void testOIDIRI3() {
        testIRISuccess(TestOidIri3.class, new TestOidIri3(), "ISO", "a", "b", "c");

        testIRIFailure(TestOidIri3.class, new TestOidIri3(), "ISO", "a", "b", "d");
    }

    @Test
    public void testOIDIRI4() {
        testIRISuccess(TestOidIri4.class, new TestOidIri4(), "ISO", "a", "b", "e");

        testIRIFailure(TestOidIri4.class, new TestOidIri4(), "ISO", "a", "b", "d");
    }

    @Test
    public void testOIDIRI5() {
        testIRISuccess(TestOidIri5.class, new TestOidIri5(), "ISO", "a", "b", "f");

        testIRIFailure(TestOidIri5.class, new TestOidIri5(), "ISO", "a", "b", "e");
    }

    @Test
    public void testRelativeOIDIRI1() {
        testRelativeIRISuccess(TestRelativeOidIri1.class, new TestRelativeOidIri1(), "a", "b", "c");
        testRelativeIRISuccess(TestRelativeOidIri1.class, new TestRelativeOidIri1(), "a", "b", "d");

        testRelativeIRIFailure(TestRelativeOidIri1.class, new TestRelativeOidIri1(), "a", "b", "e");
    }

    @Test
    public void testRelativeOIDIRI2() {
        testRelativeIRISuccess(TestRelativeOidIri2.class, new TestRelativeOidIri2(), "a", "b", "d");

        testRelativeIRIFailure(TestRelativeOidIri2.class, new TestRelativeOidIri2(), "a", "b", "c");
    }

    @Test
    public void testRelativeOIDIRI3() {
        testRelativeIRISuccess(TestRelativeOidIri3.class, new TestRelativeOidIri3(), "a", "b", "c");

        testRelativeIRIFailure(TestRelativeOidIri3.class, new TestRelativeOidIri3(), "a", "b", "d");
    }

    @Test
    public void testRelativeOIDIRI4() {
        testRelativeIRISuccess(TestRelativeOidIri4.class, new TestRelativeOidIri4(), "a", "b", "e");

        testRelativeIRIFailure(TestRelativeOidIri4.class, new TestRelativeOidIri4(), "a", "b", "d");
    }

    @Test
    public void testRelativeOIDIRI5() {
        testRelativeIRISuccess(TestRelativeOidIri5.class, new TestRelativeOidIri5(), "a", "b", "f");

        testRelativeIRIFailure(TestRelativeOidIri5.class, new TestRelativeOidIri5(), "a", "b", "e");
    }

    @Test
    public void testOctetString1() {
        testOctetStringSuccess(TestOctetString1.class, new TestOctetString1(), new byte[] { 0x50 });
        testOctetStringSuccess(TestOctetString1.class, new TestOctetString1(), new byte[] { 0x37 });

        testOctetStringFailure(TestOctetString1.class, new TestOctetString1(), new byte[] { 0x51 });
    }

    @Test
    public void testOctetString2() {
        testOctetStringSuccess(TestOctetString2.class, new TestOctetString2(), new byte[] { 0x50 });

        testOctetStringFailure(TestOctetString2.class, new TestOctetString2(), new byte[] { 0x37 });
    }

    @Test
    public void testOctetString3() {
        testOctetStringSuccess(TestOctetString3.class, new TestOctetString3(), new byte[] { 0x37 });

        testOctetStringFailure(TestOctetString3.class, new TestOctetString3(), new byte[] { 0x50 });
    }

    @Test
    public void testOctetString4() {
        testOctetStringSuccess(TestOctetString4.class, new TestOctetString4(), new byte[] { 0x37 });

        testOctetStringFailure(TestOctetString4.class, new TestOctetString4(), new byte[] { 0x38 });
    }

    @Test
    public void testSetOf1() {
        testSetOfSuccess(TestSetOf1.class, new TestSetOf1());

        testSetOfFailure(TestSetOf1.class, new TestSetOf1(), ASN1Integer.valueOf(22L));
    }

    @Test
    public void testSetOf2() {
        testSetOfSuccess(TestSetOf2.class, new TestSetOf2(), ASN1Integer.valueOf(23L));

        testSetOfFailure(TestSetOf2.class, new TestSetOf2(), ASN1Integer.valueOf(22L));
    }

    @Test
    public void testSetOf3() {
        testSetOfSuccess(TestSetOf3.class, new TestSetOf3(), ASN1Integer.valueOf(23L));
        testSetOfSuccess(TestSetOf3.class, new TestSetOf3(), ASN1Integer.valueOf(47L));

        testSetOfFailure(TestSetOf3.class, new TestSetOf3(), ASN1Integer.valueOf(21L));
        testSetOfFailure(TestSetOf3.class, new TestSetOf3(), ASN1Integer.valueOf(48L));
    }

    @Test
    public void testSetOf4() {
        testSetOfSuccess(TestSetOf4.class, new TestSetOf4(), ASN1Integer.valueOf(12L));
        testSetOfSuccess(TestSetOf4.class, new TestSetOf4(), ASN1Integer.valueOf(2L), ASN1Integer.valueOf(3L));
        // TODO: should be equal independent of the order of the elements
        // testSetOfSuccess(TestSetOf4.class, new TestSetOf4(), ASN1Integer.valueOf(3L), ASN1Integer.valueOf(2L));

        testSetOfFailure(TestSetOf4.class, new TestSetOf4());
        testSetOfFailure(TestSetOf4.class, new TestSetOf4(), ASN1Integer.valueOf(23L));
    }

    @Test
    public void testSetOf5() {
        testSetOfSuccess(TestSetOf5.class, new TestSetOf5(), ASN1Integer.valueOf(1L), ASN1Integer.valueOf(2L),
                ASN1Integer.valueOf(3L));

        testSetOfFailure(TestSetOf5.class, new TestSetOf4(), ASN1Integer.valueOf(1L), ASN1Integer.valueOf(2L));
    }

    @Test
    public void testSetOf6() {
        // TODO: should be equal independent of the order of the elements
        // testSetOfSuccess(TestSetOf6.class, new TestSetOf6(), ASN1Integer.valueOf(1L), ASN1Integer.valueOf(3L),
        //        ASN1Integer.valueOf(2L));
        testSetOfSuccess(TestSetOf6.class, new TestSetOf6(), ASN1Integer.valueOf(1L), ASN1Integer.valueOf(2L),
                ASN1Integer.valueOf(3L));

        testSetOfFailure(TestSetOf6.class, new TestSetOf6(), ASN1Integer.valueOf(1L), ASN1Integer.valueOf(2L));
    }

    @Test
    public void testSetOfBoolean1() {
        testSetOfSuccess(TestSetOfBoolean1.class, new TestSetOfBoolean1(),
                new ASN1Boolean(true));

        testSetOfFailure(TestSetOfBoolean1.class, new TestSetOfBoolean1(),
                new ASN1Boolean(false));
    }

    @Test
    public void testSetOfOctetString1() {
        testSetOfSuccess(TestSetOfOctetString1.class, new TestSetOfOctetString1(),
                new ASN1OctetString(new byte[] { 0x50 }));

        testSetOfFailure(TestSetOfOctetString1.class, new TestSetOfOctetString1(),
                new ASN1OctetString(new byte[] { 0x51 }));
    }

    @Test
    public void testSetOfOctetString2() {
        testSetOfSuccess(TestSetOfOctetString2.class, new TestSetOfOctetString2(),
                new TestOctetString1(new byte[] { 0x37 }));

        testSetOfFailure(TestSetOfOctetString2.class, new TestSetOfOctetString2(),
                new TestOctetString1(new byte[] { 0x38 }));
    }

    @Test
    public void testSetOfBitString1() {
        testSetOfSuccess(TestSetOfBitString1.class, new TestSetOfBitString1(),
                new ASN1BitString(new byte[] { 0x07 }, 4));

        testSetOfFailure(TestSetOfOctetString2.class, new TestSetOfOctetString2(),
                new TestOctetString1(new byte[] { 0x38 }));
    }

    @Test
    public void testSetOfEnumeration1() {
        testSetOfSuccess(TestSetOfEnumeration1.class, new TestSetOfEnumeration1(), TestEnumeration1.A);

        testSetOfFailure(TestSetOfEnumeration1.class, new TestSetOfEnumeration1(), TestEnumeration1.B);
    }

    @Test
    public void testSetOfEnumeration2() {
        testSetOfSuccess(TestSetOfEnumeration2.class, new TestSetOfEnumeration2(),
                TestEnumeration1.A, TestEnumeration1.C);

        testSetOfFailure(TestSetOfEnumeration2.class, new TestSetOfEnumeration2(),
                TestEnumeration1.A, TestEnumeration1.B);
    }

    @Test
    public void testSetOfNull1() {
        testSetOfSuccess(TestSetOfNull1.class, new TestSetOfNull1());

        testSetOfFailure(TestSetOfNull1.class, new TestSetOfNull1(), new ASN1Null());
    }

    @Test
    public void testSetOfNull2() {
        testSetOfSuccess(TestSetOfNull2.class, new TestSetOfNull2(), new TestNull4());

        testSetOfFailure(TestSetOfNull2.class, new TestSetOfNull2());
    }

    @Test
    public void testSetOfObjectIdentifier1() {
        testSetOfSuccess(TestSetOfObjectIdentifier1.class, new TestSetOfObjectIdentifier1(),
                new ASN1ObjectIdentifier(1, 3, 6, 1));

        testSetOfFailure(TestSetOfObjectIdentifier1.class, new TestSetOfObjectIdentifier1(),
                new ASN1ObjectIdentifier(1, 3, 6, 2));
    }

    @Test
    public void testSetOfObjectIdentifier2() {
        testSetOfSuccess(TestSetOfObjectIdentifier2.class, new TestSetOfObjectIdentifier2(),
                new TestObjectIdentifier1(1, 3, 6, 2));

        testSetOfFailure(TestSetOfObjectIdentifier2.class, new TestSetOfObjectIdentifier2(),
                new TestObjectIdentifier1(1, 3, 6, 1));
    }

    @Test
    public void testSetOfRelativeOID1() {
        testSetOfSuccess(TestSetOfRelativeOID1.class, new TestSetOfRelativeOID1(), new ASN1RelativeOID(4, 1));

        testSetOfFailure(TestSetOfRelativeOID1.class, new TestSetOfRelativeOID1(), new ASN1RelativeOID(4, 2));
    }

    @Test
    public void testSetOfRelativeOID2() {
        testSetOfSuccess(TestSetOfRelativeOID2.class, new TestSetOfRelativeOID2(), new TestRelativeOID1(3, 6, 2));

        testSetOfFailure(TestSetOfRelativeOID2.class, new TestSetOfRelativeOID2(), new TestRelativeOID1(3, 6, 1));
    }

    @Test
    public void testSetOfOidIri1() {
        testSetOfSuccess(TestSetOfOidIri1.class, new TestSetOfOidIri1(), new ASN1IRI("ISO", "a", "b", "f"));

        testSetOfFailure(TestSetOfOidIri1.class, new TestSetOfOidIri1(), new ASN1IRI("ISO", "a", "b", "e"));
    }

    @Test
    public void testSetOfOidIri2() {
        testSetOfSuccess(TestSetOfOidIri2.class, new TestSetOfOidIri2(), new TestOidIri1("ISO", "a", "b", "c"));

        testSetOfFailure(TestSetOfOidIri2.class, new TestSetOfOidIri2(), new TestOidIri1("ISO", "a", "b", "d"));
    }

    @Test
    public void testSetOfRelativeOidIri1() {
        testSetOfSuccess(TestSetOfRelativeOidIri1.class, new TestSetOfRelativeOidIri1(),
                new ASN1RelativeIRI("a", "b", "d"));

        testSetOfFailure(TestSetOfRelativeOidIri1.class, new TestSetOfRelativeOidIri1(),
                new ASN1RelativeIRI("a", "b", "c"));
    }

    @Test
    public void testSetOfRelativeOidIri2() {
        testSetOfSuccess(TestSetOfRelativeOidIri2.class, new TestSetOfRelativeOidIri2(),
                new TestRelativeOidIri1("a", "b", "c"));

        testSetOfFailure(TestSetOfRelativeOidIri2.class, new TestSetOfRelativeOidIri2(),
                new TestRelativeOidIri1("a", "b", "d"));
    }

}
