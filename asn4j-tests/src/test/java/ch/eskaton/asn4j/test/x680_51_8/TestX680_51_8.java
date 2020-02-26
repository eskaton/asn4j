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

package ch.eskaton.asn4j.test.x680_51_8;

import ch.eskaton.asn4j.runtime.types.ASN1Boolean;
import ch.eskaton.asn4j.runtime.types.ASN1IRI;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1Null;
import ch.eskaton.asn4j.runtime.types.ASN1ObjectIdentifier;
import ch.eskaton.asn4j.runtime.types.ASN1OctetString;
import ch.eskaton.asn4j.runtime.types.ASN1RelativeIRI;
import ch.eskaton.asn4j.runtime.types.ASN1RelativeOID;
import ch.eskaton.asn4j.runtime.types.ASN1SetOf;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestEnumerated1;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSequenceOf1;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSequenceOf2;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf1;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf10;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf12;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf14;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf16;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf18;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf2;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf20;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf22;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf24;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf3;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf4;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf5;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf6;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf7;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf8;
import org.junit.Test;

import static ch.eskaton.asn4j.test.TestHelper.testSequenceOfFailure;
import static ch.eskaton.asn4j.test.TestHelper.testSequenceOfSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testSetOfFailure;
import static ch.eskaton.asn4j.test.TestHelper.testSetOfSuccess;

public class TestX680_51_8 {

    @Test
    public void testSetOf1() {
        testSetOfSuccess(TestSetOf1.class, new TestSetOf1(), ASN1Integer.valueOf(0));
        testSetOfSuccess(TestSetOf1.class, new TestSetOf1(), ASN1Integer.valueOf(4));

        testSetOfFailure(TestSetOf1.class, new TestSetOf1(), ASN1Integer.valueOf(-1));
        testSetOfFailure(TestSetOf1.class, new TestSetOf1(), ASN1Integer.valueOf(5));
    }

    @Test
    public void testSetOf2() {
        testSetOfSuccess(TestSetOf2.class, new TestSetOf2(), ASN1Integer.valueOf(1));
        testSetOfSuccess(TestSetOf2.class, new TestSetOf2(), ASN1Integer.valueOf(2));

        testSetOfFailure(TestSetOf2.class, new TestSetOf2(), ASN1Integer.valueOf(0));
        testSetOfFailure(TestSetOf2.class, new TestSetOf2(), ASN1Integer.valueOf(3));
    }

    @Test
    public void testSetOf3() {
        testSetOfSuccess(TestSetOf3.class, new TestSetOf3(), new ASN1SetOf<>(ASN1Integer.valueOf(0)));
        testSetOfSuccess(TestSetOf3.class, new TestSetOf3(), new ASN1SetOf<>(ASN1Integer.valueOf(4)));
        testSetOfSuccess(TestSetOf3.class, new TestSetOf3(),
                new ASN1SetOf<>(ASN1Integer.valueOf(0), ASN1Integer.valueOf(1), ASN1Integer.valueOf(2),
                        ASN1Integer.valueOf(3), ASN1Integer.valueOf(4)));

        testSetOfFailure(TestSetOf3.class, new TestSetOf3());
        testSetOfFailure(TestSetOf3.class, new TestSetOf3(), new ASN1SetOf<>(ASN1Integer.valueOf(0)),
                new ASN1SetOf<>(ASN1Integer.valueOf(1)));
    }

    @Test
    public void testSetOf4() {
        testSetOfSuccess(TestSetOf4.class, new TestSetOf4(),
                new ASN1SetOf<>(ASN1Integer.valueOf(0), ASN1Integer.valueOf(1)));
        testSetOfSuccess(TestSetOf4.class, new TestSetOf4(), new ASN1SetOf<>(ASN1Integer.valueOf(1),
                ASN1Integer.valueOf(4)));

        testSetOfFailure(TestSetOf4.class, new TestSetOf4());
        testSetOfFailure(TestSetOf4.class, new TestSetOf4(), new ASN1SetOf<>(ASN1Integer.valueOf(0)));
        testSetOfFailure(TestSetOf4.class, new TestSetOf4(),
                new ASN1SetOf<>(ASN1Integer.valueOf(0), ASN1Integer.valueOf(5)));
        testSetOfFailure(TestSetOf4.class, new TestSetOf4(),
                new ASN1SetOf<>(ASN1Integer.valueOf(0), ASN1Integer.valueOf(2), ASN1Integer.valueOf(4)));
    }

    @Test
    public void testSetOf5() {
        testSetOfSuccess(TestSetOf5.class, new TestSetOf5(), new ASN1SetOf<>(ASN1Integer.valueOf(1)));
        testSetOfSuccess(TestSetOf5.class, new TestSetOf5(), new ASN1SetOf<>(ASN1Integer.valueOf(2)));
        testSetOfSuccess(TestSetOf5.class, new TestSetOf5(),
                new ASN1SetOf<>(ASN1Integer.valueOf(1), ASN1Integer.valueOf(1), ASN1Integer.valueOf(1)));

        testSetOfFailure(TestSetOf5.class, new TestSetOf5());
        testSetOfFailure(TestSetOf5.class, new TestSetOf5(), new ASN1SetOf<>(ASN1Integer.valueOf(0)));
    }

    @Test
    public void testSetOf6() {
        testSetOfSuccess(TestSetOf6.class, new TestSetOf6(), new ASN1SetOf<>(ASN1Integer.valueOf(1)));
        testSetOfSuccess(TestSetOf6.class, new TestSetOf6(), new ASN1SetOf<>(ASN1Integer.valueOf(2)));
        testSetOfSuccess(TestSetOf6.class, new TestSetOf6(),
                new ASN1SetOf<>(ASN1Integer.valueOf(3), ASN1Integer.valueOf(4)));
        testSetOfSuccess(TestSetOf6.class, new TestSetOf6(),
                new ASN1SetOf<>(ASN1Integer.valueOf(1), ASN1Integer.valueOf(2), ASN1Integer.valueOf(1)));

        testSetOfFailure(TestSetOf6.class, new TestSetOf6());
        testSetOfFailure(TestSetOf6.class, new TestSetOf6(), new ASN1SetOf<>(ASN1Integer.valueOf(0)));
    }

    @Test
    public void testSetOf7() {
        testSetOfSuccess(TestSetOf7.class, new TestSetOf7(),
                new ASN1SetOf<>(ASN1Integer.valueOf(1), ASN1Integer.valueOf(2)));

        testSetOfFailure(TestSetOf7.class, new TestSetOf7());
        testSetOfFailure(TestSetOf7.class, new TestSetOf7(), new ASN1SetOf<>(ASN1Integer.valueOf(1)));
        testSetOfFailure(TestSetOf7.class, new TestSetOf7(),
                new ASN1SetOf<>(ASN1Integer.valueOf(3), ASN1Integer.valueOf(4)));
        testSetOfFailure(TestSetOf7.class, new TestSetOf7(),
                new ASN1SetOf<>(ASN1Integer.valueOf(1), ASN1Integer.valueOf(2), ASN1Integer.valueOf(1)));
    }

    @Test
    public void testSetOf8() {
        testSetOfSuccess(TestSetOf8.class, new TestSetOf8(),
                new ASN1SetOf<>(ASN1Integer.valueOf(0), ASN1Integer.valueOf(3)));

        testSetOfFailure(TestSetOf8.class, new TestSetOf8());
        testSetOfFailure(TestSetOf8.class, new TestSetOf8(), new ASN1SetOf<>(ASN1Integer.valueOf(1)));
        testSetOfFailure(TestSetOf8.class, new TestSetOf8(),
                new ASN1SetOf<>(ASN1Integer.valueOf(1), ASN1Integer.valueOf(2)));
        testSetOfFailure(TestSetOf8.class, new TestSetOf8(),
                new ASN1SetOf<>(ASN1Integer.valueOf(0), ASN1Integer.valueOf(3), ASN1Integer.valueOf(4)));
    }

    @Test
    public void testSetOf10() {
        testSetOfSuccess(TestSetOf10.class, new TestSetOf10());
        testSetOfSuccess(TestSetOf10.class, new TestSetOf10(), ASN1Boolean.TRUE);

        testSetOfFailure(TestSetOf10.class, new TestSetOf10(), ASN1Boolean.FALSE);
    }

    @Test
    public void testSetOf12() {
        testSetOfSuccess(TestSetOf12.class, new TestSetOf12());
        testSetOfSuccess(TestSetOf12.class, new TestSetOf12(), TestEnumerated1.A);

        testSetOfFailure(TestSetOf12.class, new TestSetOf12(), TestEnumerated1.B);
        testSetOfFailure(TestSetOf12.class, new TestSetOf12(), TestEnumerated1.C);
    }

    @Test
    public void testSetOf14() {
        testSetOfSuccess(TestSetOf14.class, new TestSetOf14());
        testSetOfSuccess(TestSetOf14.class, new TestSetOf14(), new ASN1Null());
    }

    @Test
    public void testSetOf16() {
        testSetOfSuccess(TestSetOf16.class, new TestSetOf16());
        testSetOfSuccess(TestSetOf16.class, new TestSetOf16(), ASN1ObjectIdentifier.from(0, 3, 6, 3));

        testSetOfFailure(TestSetOf16.class, new TestSetOf16(), ASN1ObjectIdentifier.from(0, 3, 6, 2));
    }

    @Test
    public void testSetOf18() {
        testSetOfSuccess(TestSetOf18.class, new TestSetOf18());
        testSetOfSuccess(TestSetOf18.class, new TestSetOf18(), ASN1RelativeOID.from(3, 6, 3));

        testSetOfFailure(TestSetOf18.class, new TestSetOf18(), ASN1RelativeOID.from(3, 6, 2));
    }

    @Test
    public void testSetOf20() {
        testSetOfSuccess(TestSetOf20.class, new TestSetOf20());
        testSetOfSuccess(TestSetOf20.class, new TestSetOf20(), ASN1IRI.from("ISO", "a", "b", "e"));

        testSetOfFailure(TestSetOf20.class, new TestSetOf20(), ASN1IRI.from("ISO", "a", "b", "f"));
    }

    @Test
    public void testSetOf22() {
        testSetOfSuccess(TestSetOf22.class, new TestSetOf22());
        testSetOfSuccess(TestSetOf22.class, new TestSetOf22(), ASN1RelativeIRI.from("a", "b", "e"));

        testSetOfFailure(TestSetOf22.class, new TestSetOf22(), ASN1RelativeIRI.from("a", "b", "f"));
    }

    @Test
    public void testSetOf24() {
        testSetOfSuccess(TestSetOf24.class, new TestSetOf24());
        testSetOfSuccess(TestSetOf24.class, new TestSetOf24(), ASN1OctetString.valueOf(new byte[] { 0x50 }));

        testSetOfFailure(TestSetOf24.class, new TestSetOf24(), ASN1OctetString.valueOf(new byte[] { 0x51 }));
    }

    @Test
    public void testSequenceOf1() {
        testSequenceOfSuccess(TestSequenceOf1.class, new TestSequenceOf1(), ASN1Integer.valueOf(0));
        testSequenceOfSuccess(TestSequenceOf1.class, new TestSequenceOf1(), ASN1Integer.valueOf(4));

        testSequenceOfFailure(TestSequenceOf1.class, new TestSequenceOf1(), ASN1Integer.valueOf(-1));
        testSequenceOfFailure(TestSequenceOf1.class, new TestSequenceOf1(), ASN1Integer.valueOf(5));
    }

    @Test
    public void testSequenceOf2() {
        testSequenceOfSuccess(TestSequenceOf2.class, new TestSequenceOf2(), ASN1Integer.valueOf(1));
        testSequenceOfSuccess(TestSequenceOf2.class, new TestSequenceOf2(), ASN1Integer.valueOf(2));

        testSequenceOfFailure(TestSequenceOf2.class, new TestSequenceOf2(), ASN1Integer.valueOf(0));
        testSequenceOfFailure(TestSequenceOf2.class, new TestSequenceOf2(), ASN1Integer.valueOf(3));
    }

    
}
