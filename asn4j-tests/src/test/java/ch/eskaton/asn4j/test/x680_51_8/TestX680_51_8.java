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

import ch.eskaton.asn4j.runtime.types.ASN1BitString;
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
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSequence105;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSequence106;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSequence107;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSequence108;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSequence109;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSequence110;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSequence111;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSequence112;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSequence113;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSequence2;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSequence3;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSequence4;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSequence5;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSequenceOf1;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSequenceOf2;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf1;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf110;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf112;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf114;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf116;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf118;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf120;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf122;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf124;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf2;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf3;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf4;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf5;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf6;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf7;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf8;
import ch.eskaton.commons.functional.QuadConsumer;
import ch.eskaton.commons.functional.TriConsumer;
import org.junit.jupiter.api.Test;

import static ch.eskaton.asn4j.test.TestHelper.testSequenceFailure;
import static ch.eskaton.asn4j.test.TestHelper.testSequenceOfFailure;
import static ch.eskaton.asn4j.test.TestHelper.testSequenceOfSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testSequenceSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testSetOfFailure;
import static ch.eskaton.asn4j.test.TestHelper.testSetOfSuccess;
import static ch.eskaton.asn4j.test.modules.x680_51_8.TestSequence105.A.A;
import static ch.eskaton.asn4j.test.modules.x680_51_8.TestSequence105.A.B;
import static ch.eskaton.asn4j.test.modules.x680_51_8.TestSequence105.A.C;
import static ch.eskaton.commons.utils.Utils.with;

public class TestX680_51_8 {

    @Test
    void testSetOf1() {
        testSetOfSuccess(TestSetOf1.class, new TestSetOf1(), ASN1Integer.valueOf(0));
        testSetOfSuccess(TestSetOf1.class, new TestSetOf1(), ASN1Integer.valueOf(4));

        testSetOfFailure(TestSetOf1.class, new TestSetOf1(), ASN1Integer.valueOf(-1));
        testSetOfFailure(TestSetOf1.class, new TestSetOf1(), ASN1Integer.valueOf(5));
    }

    @Test
    void testSetOf2() {
        testSetOfSuccess(TestSetOf2.class, new TestSetOf2(), ASN1Integer.valueOf(1));
        testSetOfSuccess(TestSetOf2.class, new TestSetOf2(), ASN1Integer.valueOf(2));

        testSetOfFailure(TestSetOf2.class, new TestSetOf2(), ASN1Integer.valueOf(0));
        testSetOfFailure(TestSetOf2.class, new TestSetOf2(), ASN1Integer.valueOf(3));
    }

    @Test
    void testSetOf3() {
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
    void testSetOf4() {
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
    void testSetOf5() {
        testSetOfSuccess(TestSetOf5.class, new TestSetOf5(), new ASN1SetOf<>(ASN1Integer.valueOf(1)));
        testSetOfSuccess(TestSetOf5.class, new TestSetOf5(), new ASN1SetOf<>(ASN1Integer.valueOf(2)));
        testSetOfSuccess(TestSetOf5.class, new TestSetOf5(),
                new ASN1SetOf<>(ASN1Integer.valueOf(1), ASN1Integer.valueOf(1), ASN1Integer.valueOf(1)));

        testSetOfFailure(TestSetOf5.class, new TestSetOf5());
        testSetOfFailure(TestSetOf5.class, new TestSetOf5(), new ASN1SetOf<>(ASN1Integer.valueOf(0)));
    }

    @Test
    void testSetOf6() {
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
    void testSetOf7() {
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
    void testSetOf8() {
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
    void testSetOf110() {
        testSetOfSuccess(TestSetOf110.class, new TestSetOf110());
        testSetOfSuccess(TestSetOf110.class, new TestSetOf110(), ASN1Boolean.TRUE);

        testSetOfFailure(TestSetOf110.class, new TestSetOf110(), ASN1Boolean.FALSE);
    }

    @Test
    void testSetOf112() {
        testSetOfSuccess(TestSetOf112.class, new TestSetOf112());
        testSetOfSuccess(TestSetOf112.class, new TestSetOf112(), TestEnumerated1.A);

        testSetOfFailure(TestSetOf112.class, new TestSetOf112(), TestEnumerated1.B);
        testSetOfFailure(TestSetOf112.class, new TestSetOf112(), TestEnumerated1.C);
    }

    @Test
    void testSetOf114() {
        testSetOfSuccess(TestSetOf114.class, new TestSetOf114());
        testSetOfSuccess(TestSetOf114.class, new TestSetOf114(), new ASN1Null());
    }

    @Test
    void testSetOf116() {
        testSetOfSuccess(TestSetOf116.class, new TestSetOf116());
        testSetOfSuccess(TestSetOf116.class, new TestSetOf116(), ASN1ObjectIdentifier.from(0, 3, 6, 3));

        testSetOfFailure(TestSetOf116.class, new TestSetOf116(), ASN1ObjectIdentifier.from(0, 3, 6, 2));
    }

    @Test
    void testSetOf118() {
        testSetOfSuccess(TestSetOf118.class, new TestSetOf118());
        testSetOfSuccess(TestSetOf118.class, new TestSetOf118(), ASN1RelativeOID.from(3, 6, 3));

        testSetOfFailure(TestSetOf118.class, new TestSetOf118(), ASN1RelativeOID.from(3, 6, 2));
    }

    @Test
    void testSetOf120() {
        testSetOfSuccess(TestSetOf120.class, new TestSetOf120());
        testSetOfSuccess(TestSetOf120.class, new TestSetOf120(), ASN1IRI.from("ISO", "a", "b", "e"));

        testSetOfFailure(TestSetOf120.class, new TestSetOf120(), ASN1IRI.from("ISO", "a", "b", "f"));
    }

    @Test
    void testSetOf122() {
        testSetOfSuccess(TestSetOf122.class, new TestSetOf122());
        testSetOfSuccess(TestSetOf122.class, new TestSetOf122(), ASN1RelativeIRI.from("a", "b", "e"));

        testSetOfFailure(TestSetOf122.class, new TestSetOf122(), ASN1RelativeIRI.from("a", "b", "f"));
    }

    @Test
    void testSetOf124() {
        testSetOfSuccess(TestSetOf124.class, new TestSetOf124());
        testSetOfSuccess(TestSetOf124.class, new TestSetOf124(), ASN1OctetString.valueOf(new byte[] { 0x50 }));

        testSetOfFailure(TestSetOf124.class, new TestSetOf124(), ASN1OctetString.valueOf(new byte[] { 0x51 }));
    }

    @Test
    void testSequenceOf1() {
        testSequenceOfSuccess(TestSequenceOf1.class, new TestSequenceOf1(), ASN1Integer.valueOf(0));
        testSequenceOfSuccess(TestSequenceOf1.class, new TestSequenceOf1(), ASN1Integer.valueOf(4));

        testSequenceOfFailure(TestSequenceOf1.class, new TestSequenceOf1(), ASN1Integer.valueOf(-1));
        testSequenceOfFailure(TestSequenceOf1.class, new TestSequenceOf1(), ASN1Integer.valueOf(5));
    }

    @Test
    void testSequenceOf2() {
        testSequenceOfSuccess(TestSequenceOf2.class, new TestSequenceOf2(), ASN1Integer.valueOf(1));
        testSequenceOfSuccess(TestSequenceOf2.class, new TestSequenceOf2(), ASN1Integer.valueOf(2));

        testSequenceOfFailure(TestSequenceOf2.class, new TestSequenceOf2(), ASN1Integer.valueOf(0));
        testSequenceOfFailure(TestSequenceOf2.class, new TestSequenceOf2(), ASN1Integer.valueOf(3));
    }

    @Test
    void testSequence2() {
        TriConsumer<TestSequence2, ASN1Integer, ASN1Boolean> init = (s, a, b) -> {
            s.setA(a);
            s.setB(b);
        };

        testSequenceSuccess(TestSequence2.class, new TestSequence2(),
                s -> init.accept(s, ASN1Integer.valueOf(1), ASN1Boolean.TRUE));
        testSequenceSuccess(TestSequence2.class, new TestSequence2(),
                s -> init.accept(s, ASN1Integer.valueOf(1), ASN1Boolean.TRUE));
        testSequenceSuccess(TestSequence2.class, new TestSequence2(),
                s -> init.accept(s, ASN1Integer.valueOf(2), ASN1Boolean.TRUE));

        testSequenceFailure(TestSequence2.class, new TestSequence2(),
                s -> init.accept(s, ASN1Integer.valueOf(0), ASN1Boolean.TRUE));
        testSequenceFailure(TestSequence2.class, new TestSequence2(),
                s -> init.accept(s, ASN1Integer.valueOf(1), ASN1Boolean.FALSE));
    }

    @Test
    void testSequence3() {
        QuadConsumer<TestSequence3, ASN1Integer, ASN1Boolean, ASN1Boolean> init = (s, a, b, d) -> {
            s.setC(with(new TestSequence2(), s2 -> {
                s2.setA(a);
                s2.setB(b);
            }));
            s.setD(d);
        };

        testSequenceSuccess(TestSequence3.class, new TestSequence3(),
                s -> init.accept(s, ASN1Integer.valueOf(1), ASN1Boolean.TRUE, ASN1Boolean.FALSE));

        testSequenceFailure(TestSequence3.class, new TestSequence3(),
                s -> init.accept(s, ASN1Integer.valueOf(2), ASN1Boolean.TRUE, ASN1Boolean.FALSE));
        testSequenceFailure(TestSequence3.class, new TestSequence3(),
                s -> init.accept(s, ASN1Integer.valueOf(1), ASN1Boolean.FALSE, ASN1Boolean.FALSE));
        testSequenceFailure(TestSequence3.class, new TestSequence3(),
                s -> init.accept(s, ASN1Integer.valueOf(1), ASN1Boolean.TRUE, ASN1Boolean.TRUE));
    }

    @Test
    void testSequence4() {
        QuadConsumer<TestSequence4, ASN1Integer, ASN1Boolean, ASN1Boolean> init = (s, a, b, d) -> {
            s.setC(with(new TestSequence4.C(), s2 -> {
                s2.setA(a);
                s2.setB(b);
            }));
            s.setD(d);
        };

        testSequenceSuccess(TestSequence4.class, new TestSequence4(),
                s -> init.accept(s, ASN1Integer.valueOf(1), ASN1Boolean.TRUE, ASN1Boolean.FALSE));

        testSequenceFailure(TestSequence4.class, new TestSequence4(),
                s -> init.accept(s, ASN1Integer.valueOf(2), ASN1Boolean.TRUE, ASN1Boolean.FALSE));
        testSequenceFailure(TestSequence4.class, new TestSequence4(),
                s -> init.accept(s, ASN1Integer.valueOf(1), ASN1Boolean.FALSE, ASN1Boolean.FALSE));
        testSequenceFailure(TestSequence4.class, new TestSequence4(),
                s -> init.accept(s, ASN1Integer.valueOf(1), ASN1Boolean.TRUE, ASN1Boolean.TRUE));
    }

    @Test
    void testSequence5() {
        QuadConsumer<TestSequence5, ASN1Integer, ASN1Boolean, ASN1Boolean> init = (s, a, b, d) -> {
            s.setC(with(new TestSequence5.C(), s2 -> {
                s2.setA(a);
                s2.setB(b);
            }));
            s.setD(d);
        };

        testSequenceSuccess(TestSequence5.class, new TestSequence5(),
                s -> init.accept(s, ASN1Integer.valueOf(1), ASN1Boolean.TRUE, ASN1Boolean.TRUE));
        testSequenceSuccess(TestSequence5.class, new TestSequence5(),
                s -> init.accept(s, ASN1Integer.valueOf(1), ASN1Boolean.TRUE, ASN1Boolean.FALSE));
        testSequenceSuccess(TestSequence5.class, new TestSequence5(),
                s -> init.accept(s, ASN1Integer.valueOf(1), ASN1Boolean.FALSE, ASN1Boolean.TRUE));
        testSequenceSuccess(TestSequence5.class, new TestSequence5(),
                s -> init.accept(s, ASN1Integer.valueOf(1), ASN1Boolean.FALSE, ASN1Boolean.FALSE));

        testSequenceFailure(TestSequence5.class, new TestSequence5(),
                s -> init.accept(s, ASN1Integer.valueOf(2), ASN1Boolean.TRUE, ASN1Boolean.TRUE));
    }

    @Test
    void testSequence105() {
        testSequenceSuccess(TestSequence105.class, new TestSequence105(), s -> s.setA(A));
        testSequenceSuccess(TestSequence105.class, new TestSequence105(), s -> s.setA(B));

        testSequenceFailure(TestSequence105.class, new TestSequence105(), s -> s.setA(C));
    }

    @Test
    void testSequence106() {
        testSequenceSuccess(TestSequence106.class, new TestSequence106(), s -> s.setA(new ASN1Null()));
    }

    @Test
    void testSequence107() {
        testSequenceSuccess(TestSequence107.class, new TestSequence107(),
                s -> s.setA(new ASN1ObjectIdentifier(0, 3, 6, 3)));

        testSequenceFailure(TestSequence107.class, new TestSequence107(),
                s -> s.setA(new ASN1ObjectIdentifier(0, 3, 6, 4)));
    }

    @Test
    void testSequence108() {
        testSequenceSuccess(TestSequence108.class, new TestSequence108(), s -> s.setA(new ASN1RelativeOID(3, 6, 3)));

        testSequenceFailure(TestSequence108.class, new TestSequence108(), s -> s.setA(new ASN1RelativeOID(3, 6, 4)));
    }

    @Test
    void testSequence109() {
        testSequenceSuccess(TestSequence109.class, new TestSequence109(),
                s -> s.setA(new ASN1IRI("ISO", "a", "b", "e")));

        testSequenceFailure(TestSequence109.class, new TestSequence109(),
                s -> s.setA(new ASN1IRI("ISO", "a", "b", "c")));
    }

    @Test
    void testSequence110() {
        testSequenceSuccess(TestSequence110.class, new TestSequence110(),
                s -> s.setA(new ASN1RelativeIRI("a", "b", "e")));

        testSequenceFailure(TestSequence110.class, new TestSequence110(),
                s -> s.setA(new ASN1RelativeIRI("a", "b", "c")));
    }

    @Test
    void testSequence111() {
        testSequenceSuccess(TestSequence111.class, new TestSequence111(),
                s -> s.setA(new ASN1BitString(new byte[] { 0x05 }, 4)));

        testSequenceFailure(TestSequence111.class, new TestSequence111(),
                s -> s.setA(new ASN1BitString(new byte[] { 0x05 }, 5)));
    }

    @Test
    void testSequence112() {
        testSequenceSuccess(TestSequence112.class, new TestSequence112(),
                s -> s.setA(with(new TestSetOf1(), so -> so.setValues(ASN1Integer.valueOf(1L)))));
        testSequenceSuccess(TestSequence112.class, new TestSequence112(),
                s -> s.setA(with(new TestSetOf1(), so -> so.setValues(ASN1Integer.valueOf(2L)))));
        testSequenceSuccess(TestSequence112.class, new TestSequence112(),
                s -> s.setA(with(new TestSetOf1(), so -> so.setValues(ASN1Integer.valueOf(3L)))));

        testSequenceFailure(TestSequence112.class, new TestSequence112(),
                s -> s.setA(with(new TestSetOf1(), so -> so.setValues(ASN1Integer.valueOf(0L)))));
        testSequenceFailure(TestSequence112.class, new TestSequence112(),
                s -> s.setA(with(new TestSetOf1(), so -> so.setValues(ASN1Integer.valueOf(4L)))));
    }

    @Test
    void testSequence113() {
        testSequenceSuccess(TestSequence113.class, new TestSequence113(),
                s -> s.setA(with(new TestSetOf1(), so -> so.setValues(ASN1Integer.valueOf(1L)))));
        testSequenceSuccess(TestSequence113.class, new TestSequence113(),
                s -> s.setA(with(new TestSetOf1(), so -> so.setValues(ASN1Integer.valueOf(2L)))));

        testSequenceFailure(TestSequence113.class, new TestSequence113(),
                s -> s.setA(with(new TestSetOf1(), so -> so.setValues(ASN1Integer.valueOf(0L)))));
        testSequenceFailure(TestSequence113.class, new TestSequence113(),
                s -> s.setA(with(new TestSetOf1(), so -> so.setValues(ASN1Integer.valueOf(3L)))));
    }

}
