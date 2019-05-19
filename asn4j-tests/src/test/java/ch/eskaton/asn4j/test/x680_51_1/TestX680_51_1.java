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


import ch.eskaton.asn4jtest.x680_51_1.TestBitString1;
import ch.eskaton.asn4jtest.x680_51_1.TestBitString2;
import ch.eskaton.asn4jtest.x680_51_1.TestBitString3;
import ch.eskaton.asn4jtest.x680_51_1.TestBitString5;
import ch.eskaton.asn4jtest.x680_51_1.TestBitString6;
import ch.eskaton.asn4jtest.x680_51_1.TestBoolean2;
import ch.eskaton.asn4jtest.x680_51_3.TestBoolean4;
import org.junit.Test;

import static ch.eskaton.asn4j.test.TestHelper.testBitStringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testBitStringSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testBooleanFailure;
import static ch.eskaton.asn4j.test.TestHelper.testBooleanSuccess;

public class TestX680_51_1 {

    @Test
    public void testBitString1() {
        testBitStringSuccess(TestBitString1.class, new TestBitString1(), 0x00, 5);
        testBitStringSuccess(TestBitString1.class, new TestBitString1(), 0x01, 5);
        testBitStringSuccess(TestBitString1.class, new TestBitString1(), 0x02, 5);
        testBitStringSuccess(TestBitString1.class, new TestBitString1(), 0x04, 5);
        testBitStringSuccess(TestBitString1.class, new TestBitString1(), 0x05, 5);
        testBitStringSuccess(TestBitString1.class, new TestBitString1(), 0x06, 5);
        testBitStringSuccess(TestBitString1.class, new TestBitString1(), 0x07, 5);

        testBitStringFailure(new TestBitString1(), 0x02, 6);
        testBitStringFailure(new TestBitString1(), 0x03, 5);
        testBitStringFailure(new TestBitString1(), 0x06, 4);
    }

    @Test
    public void testBitString2() {
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x00, 7);
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x01, 7);
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x00, 6);
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x01, 6);
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x02, 6);
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x03, 6);
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x07, 5);

        testBitStringFailure(new TestBitString2(), 0x00, 5);
        testBitStringFailure(new TestBitString2(), 0x06, 5);
        testBitStringFailure(new TestBitString2(), 0x08, 4);
    }

    @Test
    public void testBitString3() {
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x00, 7);
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x01, 7);
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x00, 6);
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x01, 6);
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x02, 6);
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x03, 6);
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x00, 5);
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x07, 5);

        testBitStringFailure(new TestBitString3(), 0x06, 5);
        testBitStringFailure(new TestBitString3(), 0x08, 4);
    }


    @Test
    public void testBitString5() {
        testBitStringSuccess(TestBitString5.class, new TestBitString5(), 0x00, 5);
        testBitStringSuccess(TestBitString5.class, new TestBitString5(), 0x01, 5);

        testBitStringFailure(new TestBitString5(), 0x02, 5);
    }

    @Test
    public void testBitString6() {
        testBitStringSuccess(TestBitString6.class, new TestBitString6(), 0x01, 0);
        testBitStringSuccess(TestBitString6.class, new TestBitString6(), 0x02, 4);
        testBitStringSuccess(TestBitString6.class, new TestBitString6(), 0x02, 6);
        testBitStringSuccess(TestBitString6.class, new TestBitString6(), 0xffee, 1);

        testBitStringFailure(new TestBitString6(), 0x02, 5);
    }
    
    @Test
    public void testBoolean2() {
        testBooleanSuccess(TestBoolean2.class, new TestBoolean2(), false);

        testBooleanFailure(new TestBoolean2(), true);
    }

}
