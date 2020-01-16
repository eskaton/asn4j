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

import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1SetOf;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf1;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf2;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf3;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf4;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf5;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf6;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf7;
import ch.eskaton.asn4j.test.modules.x680_51_8.TestSetOf8;
import org.junit.Test;

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
        testSetOfSuccess(TestSetOf7.class, new TestSetOf7(), new ASN1SetOf<>(ASN1Integer.valueOf(1), ASN1Integer.valueOf(2)));

        testSetOfFailure(TestSetOf7.class, new TestSetOf7());
        testSetOfFailure(TestSetOf7.class, new TestSetOf7(), new ASN1SetOf<>(ASN1Integer.valueOf(1)));
        testSetOfFailure(TestSetOf7.class, new TestSetOf7(),
                new ASN1SetOf<>(ASN1Integer.valueOf(3), ASN1Integer.valueOf(4)));
        testSetOfFailure(TestSetOf7.class, new TestSetOf7(),
                new ASN1SetOf<>(ASN1Integer.valueOf(1), ASN1Integer.valueOf(2), ASN1Integer.valueOf(1)));
    }
    
    @Test
    public void testSetOf8() {
        testSetOfSuccess(TestSetOf8.class, new TestSetOf8(), new ASN1SetOf<>(ASN1Integer.valueOf(0), ASN1Integer.valueOf(3)));

        testSetOfFailure(TestSetOf8.class, new TestSetOf8());
        testSetOfFailure(TestSetOf8.class, new TestSetOf8(), new ASN1SetOf<>(ASN1Integer.valueOf(1)));
        testSetOfFailure(TestSetOf8.class, new TestSetOf8(),
                new ASN1SetOf<>(ASN1Integer.valueOf(1), ASN1Integer.valueOf(2)));
        testSetOfFailure(TestSetOf8.class, new TestSetOf8(),
                new ASN1SetOf<>(ASN1Integer.valueOf(0), ASN1Integer.valueOf(3), ASN1Integer.valueOf(4)));
    }
    
}
