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

import ch.eskaton.asn4jtest.x680_51_2.TestBitString1;
import ch.eskaton.asn4jtest.x680_51_2.TestBitString2;
import ch.eskaton.asn4jtest.x680_51_2.TestBitString3;
import ch.eskaton.asn4jtest.x680_51_2.TestBitString4;
import ch.eskaton.asn4jtest.x680_51_2.TestBitString5;
import ch.eskaton.asn4jtest.x680_51_2.TestBitString6;
import ch.eskaton.asn4jtest.x680_51_2.TestBoolean1;
import ch.eskaton.asn4jtest.x680_51_2.TestBoolean2;
import ch.eskaton.asn4jtest.x680_51_2.TestBoolean3;
import ch.eskaton.asn4jtest.x680_51_2.TestBoolean4;
import ch.eskaton.asn4jtest.x680_51_2.TestBoolean5;
import ch.eskaton.asn4jtest.x680_51_2.TestBoolean6;
import ch.eskaton.asn4jtest.x680_51_2.TestEnumeration1;
import ch.eskaton.asn4jtest.x680_51_2.TestEnumeration2;
import ch.eskaton.asn4jtest.x680_51_2.TestEnumeration3;
import ch.eskaton.asn4jtest.x680_51_2.TestEnumeration4;
import ch.eskaton.asn4jtest.x680_51_2.TestInteger1;
import ch.eskaton.asn4jtest.x680_51_2.TestInteger2;
import ch.eskaton.asn4jtest.x680_51_2.TestInteger3;
import ch.eskaton.asn4jtest.x680_51_2.TestNull1;
import ch.eskaton.asn4jtest.x680_51_2.TestNull2;
import ch.eskaton.asn4jtest.x680_51_2.TestNull3;
import ch.eskaton.asn4jtest.x680_51_2.TestObjectIdentifier1;
import ch.eskaton.asn4jtest.x680_51_2.TestObjectIdentifier2;
import ch.eskaton.asn4jtest.x680_51_2.TestObjectIdentifier3;
import ch.eskaton.asn4jtest.x680_51_2.TestObjectIdentifier4;
import ch.eskaton.asn4jtest.x680_51_2.TestObjectIdentifier5;
import ch.eskaton.asn4jtest.x680_51_2.TestOidIri1;
import ch.eskaton.asn4jtest.x680_51_2.TestOidIri2;
import ch.eskaton.asn4jtest.x680_51_2.TestOidIri3;
import ch.eskaton.asn4jtest.x680_51_2.TestOidIri4;
import ch.eskaton.asn4jtest.x680_51_2.TestOidIri5;
import ch.eskaton.asn4jtest.x680_51_2.TestRelativeOID1;
import ch.eskaton.asn4jtest.x680_51_2.TestRelativeOID2;
import ch.eskaton.asn4jtest.x680_51_2.TestRelativeOID3;
import ch.eskaton.asn4jtest.x680_51_2.TestRelativeOID4;
import ch.eskaton.asn4jtest.x680_51_2.TestRelativeOID5;
import ch.eskaton.asn4jtest.x680_51_2.TestRelativeOidIri1;
import ch.eskaton.asn4jtest.x680_51_2.TestRelativeOidIri2;
import ch.eskaton.asn4jtest.x680_51_2.TestRelativeOidIri3;
import ch.eskaton.asn4jtest.x680_51_2.TestRelativeOidIri4;
import ch.eskaton.asn4jtest.x680_51_2.TestRelativeOidIri5;
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
import static ch.eskaton.asn4j.test.TestHelper.testRelativeOIDFailure;
import static ch.eskaton.asn4j.test.TestHelper.testRelativeOIDSuccess;

public class TestX680_51_2 {

    @Test
    public void testBitString1() {
        testBitStringSuccess(TestBitString1.class, new TestBitString1(), 0x03, 5);
        testBitStringSuccess(TestBitString1.class, new TestBitString1(), 0x05, 5);
        testBitStringSuccess(TestBitString1.class, new TestBitString1(), 0x03, 4);
        testBitStringSuccess(TestBitString1.class, new TestBitString1(), 0x06, 6);

        testBitStringFailure(new TestBitString1(), 0x04, 5);
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

        testBitStringFailure(new TestBitString2(), 0x02, 5);
        testBitStringFailure(new TestBitString2(), 0x08, 4);
        testBitStringFailure(new TestBitString2(), 0x00, 4);
        testBitStringFailure(new TestBitString2(), 0x00, 6);
    }

    @Test
    public void testBitString3() {
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x01, 5);
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x03, 5);

        testBitStringFailure(new TestBitString3(), 0x00, 5);
        testBitStringFailure(new TestBitString3(), 0x02, 5);
        testBitStringFailure(new TestBitString3(), 0x04, 5);
        testBitStringFailure(new TestBitString3(), 0x01, 4);
        testBitStringFailure(new TestBitString3(), 0x01, 6);
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

        testBitStringFailure(new TestBitString6(), 0x01, 5);
        testBitStringFailure(new TestBitString6(), 0x03, 5);
    }

    @Test
    public void testBoolean1() {
        testBooleanSuccess(TestBoolean1.class, new TestBoolean1(), false);
        testBooleanFailure(new TestBoolean1(), true);
    }

    @Test
    public void testBoolean2() {
        testBooleanSuccess(TestBoolean2.class, new TestBoolean2(), true);
        testBooleanFailure(new TestBoolean2(), false);
    }

    @Test
    public void testBoolean3() {
        testBooleanSuccess(TestBoolean3.class, new TestBoolean3(), false);
        testBooleanFailure(new TestBoolean3(), true);
    }

    @Test
    public void testBoolean4() {
        testBooleanSuccess(TestBoolean4.class, new TestBoolean4(), true);
        testBooleanSuccess(TestBoolean4.class, new TestBoolean4(), false);
    }

    @Test
    public void testBoolean5() {
        testBooleanSuccess(TestBoolean5.class, new TestBoolean5(), true);

        testBooleanFailure(new TestBoolean5(), false);
    }

    @Test
    public void testBoolean6() {
        testBooleanSuccess(TestBoolean6.class, new TestBoolean6(), true);

        testBooleanFailure(new TestBoolean6(), false);
    }

    @Test
    public void testEnumeration2() {
        testEnumeratedSuccess(TestEnumeration2.class, new TestEnumeration2(), TestEnumeration1.A);
        testEnumeratedSuccess(TestEnumeration2.class, new TestEnumeration2(), TestEnumeration1.B);

        testEnumeratedFailure(new TestEnumeration2(), TestEnumeration1.C);
        testEnumeratedFailure(new TestEnumeration2(), TestEnumeration1.D);
    }

    @Test
    public void testEnumeration3() {
        testEnumeratedSuccess(TestEnumeration3.class, new TestEnumeration3(), TestEnumeration1.B);
        testEnumeratedSuccess(TestEnumeration3.class, new TestEnumeration3(), TestEnumeration1.C);

        testEnumeratedFailure(new TestEnumeration3(), TestEnumeration1.A);
        testEnumeratedFailure(new TestEnumeration3(), TestEnumeration1.D);
    }

    @Test
    public void testEnumeration4() {
        testEnumeratedSuccess(TestEnumeration4.class, new TestEnumeration4(), TestEnumeration1.A);
        testEnumeratedSuccess(TestEnumeration4.class, new TestEnumeration4(), TestEnumeration1.B);
        testEnumeratedSuccess(TestEnumeration4.class, new TestEnumeration4(), TestEnumeration1.C);

        testEnumeratedFailure(new TestEnumeration4(), TestEnumeration1.D);
    }

    @Test
    public void testInteger1() {
        testIntegerSuccess(TestInteger1.class, new TestInteger1(), 1);
        testIntegerSuccess(TestInteger1.class, new TestInteger1(), 2);

        testIntegerFailure(new TestInteger1(), 0);
        testIntegerFailure(new TestInteger1(), 3);
    }

    @Test
    public void testInteger2() {
        testIntegerSuccess(TestInteger2.class, new TestInteger2(), -1);
        testIntegerSuccess(TestInteger2.class, new TestInteger2(), 1);

        testIntegerFailure(new TestInteger2(), 0);
    }

    @Test
    public void testInteger3() {
        testIntegerSuccess(TestInteger3.class, new TestInteger3(), 2);

        testIntegerFailure(new TestInteger3(), 1);
        testIntegerFailure(new TestInteger3(), 3);
    }

    @Test
    public void testNull1() {
        testNullSuccess(TestNull1.class, () -> new TestNull1());
    }

    @Test
    public void testNull2() {
        testNullFailure(() -> new TestNull2());
    }

    @Test
    public void testNull3() {
        testNullFailure(() -> new TestNull3());
    }

    @Test
    public void testObjectIdentifier1() {
        testObjectIdentifierSuccess(TestObjectIdentifier1.class, new TestObjectIdentifier1(), 1, 3, 6, 1);
        testObjectIdentifierSuccess(TestObjectIdentifier1.class, new TestObjectIdentifier1(), 1, 3, 6, 2);

        testObjectIdentifierFailure(new TestObjectIdentifier1(), 1, 3, 6, 0);
    }

    @Test
    public void testObjectIdentifier2() {
        testObjectIdentifierSuccess(TestObjectIdentifier2.class, new TestObjectIdentifier2(), 1, 3, 6, 1);

        testObjectIdentifierFailure(new TestObjectIdentifier2(), 1, 3, 6, 2);
    }

    @Test
    public void testObjectIdentifier3() {
        testObjectIdentifierSuccess(TestObjectIdentifier3.class, new TestObjectIdentifier3(), 1, 3, 6, 2);

        testObjectIdentifierFailure(new TestObjectIdentifier3(), 1, 3, 6, 1);
    }

    @Test
    public void testObjectIdentifier4() {
        testObjectIdentifierSuccess(TestObjectIdentifier4.class, new TestObjectIdentifier4(), 1, 3, 6, 1);

        testObjectIdentifierFailure(new TestObjectIdentifier4(), 1, 3, 6, 2);
    }

    @Test
    public void testObjectIdentifier5() {
        testObjectIdentifierSuccess(TestObjectIdentifier5.class, new TestObjectIdentifier5(), 0, 4, 11, 2);

        testObjectIdentifierFailure(new TestObjectIdentifier5(), 1, 3, 6, 2);
    }

    @Test
    public void testRelativeOID1() {
        testRelativeOIDSuccess(TestRelativeOID1.class, new TestRelativeOID1(), 3, 6, 1);
        testRelativeOIDSuccess(TestRelativeOID1.class, new TestRelativeOID1(), 3, 6, 2);

        testRelativeOIDFailure(new TestRelativeOID1(), 3, 6, 0);
    }

    @Test
    public void testRelativeOID2() {
        testRelativeOIDSuccess(TestRelativeOID2.class, new TestRelativeOID2(), 3, 6, 1);

        testRelativeOIDFailure(new TestRelativeOID2(), 3, 6, 2);
    }

    @Test
    public void testRelativeOID3() {
        testRelativeOIDSuccess(TestRelativeOID3.class, new TestRelativeOID3(), 3, 6, 2);

        testRelativeOIDFailure(new TestRelativeOID3(), 3, 6, 1);
    }

    @Test
    public void testRelativeOID4() {
        testRelativeOIDSuccess(TestRelativeOID4.class, new TestRelativeOID4(), 3, 6, 1);

        testRelativeOIDFailure(new TestRelativeOID4(), 3, 6, 2);
    }

    @Test
    public void testRelativeOID5() {
        testRelativeOIDSuccess(TestRelativeOID5.class, new TestRelativeOID5(), 4, 1, 11, 2);

        testRelativeOIDFailure(new TestRelativeOID5(), 4, 1, 11, 1);
    }

    @Test
    public void testOIDIRI1() {
        testIRISuccess(TestOidIri1.class, new TestOidIri1(), "ISO", "a", "b", "c");
        testIRISuccess(TestOidIri1.class, new TestOidIri1(), "ISO", "a", "b", "d");

        testIRIFailure(new TestOidIri1(), "ISO", "a", "b", "e");
    }

    @Test
    public void testOIDIRI2() {
        testIRISuccess(TestOidIri2.class, new TestOidIri2(), "ISO", "a", "b", "d");

        testIRIFailure(new TestOidIri2(), "ISO", "a", "b", "c");
    }

    @Test
    public void testOIDIRI3() {
        testIRISuccess(TestOidIri3.class, new TestOidIri3(), "ISO", "a", "b", "c");

        testIRIFailure(new TestOidIri3(), "ISO", "a", "b", "d");
    }

    @Test
    public void testOIDIRI4() {
        testIRISuccess(TestOidIri4.class, new TestOidIri4(), "ISO", "a", "b", "e");

        testIRIFailure(new TestOidIri4(), "ISO", "a", "b", "d");
    }

    @Test
    public void testOIDIRI5() {
        testIRISuccess(TestOidIri5.class, new TestOidIri5(), "ISO", "a", "b", "f");

        testIRIFailure(new TestOidIri5(), "ISO", "a", "b", "e");
    }

    @Test
    public void testRelativeOIDIRI1() {
        testIRISuccess(TestRelativeOidIri1.class, new TestRelativeOidIri1(), "a", "b", "c");
        testIRISuccess(TestRelativeOidIri1.class, new TestRelativeOidIri1(), "a", "b", "d");

        testIRIFailure(new TestRelativeOidIri1(), "a", "b", "e");
    }

    @Test
    public void testRelativeOIDIRI2() {
        testIRISuccess(TestRelativeOidIri2.class, new TestRelativeOidIri2(), "a", "b", "d");

        testIRIFailure(new TestRelativeOidIri2(), "a", "b", "c");
    }

    @Test
    public void testRelativeOIDIRI3() {
        testIRISuccess(TestRelativeOidIri3.class, new TestRelativeOidIri3(), "a", "b", "c");

        testIRIFailure(new TestRelativeOidIri3(), "a", "b", "d");
    }

    @Test
    public void testRelativeOIDIRI4() {
        testIRISuccess(TestRelativeOidIri4.class, new TestRelativeOidIri4(), "a", "b", "e");

        testIRIFailure(new TestRelativeOidIri4(), "a", "b", "d");
    }

    @Test
    public void testRelativeOIDIRI5() {
        testIRISuccess(TestRelativeOidIri5.class, new TestRelativeOidIri5(), "a", "b", "f");

        testIRIFailure(new TestRelativeOidIri5(), "a", "b", "e");
    }

}
