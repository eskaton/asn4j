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

import ch.eskaton.asn4j.test.TestHelper;

import ch.eskaton.asn4jtest.x680_51_3.TestBitString2;
import ch.eskaton.asn4jtest.x680_51_3.TestBitString3;
import ch.eskaton.asn4jtest.x680_51_3.TestBitString4;
import ch.eskaton.asn4jtest.x680_51_3.TestBitString5;
import ch.eskaton.asn4jtest.x680_51_3.TestBitString6;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestX680_51_3 {

    @Test
    public void testBitString2() {
        TestHelper.testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x00, 5);
        TestHelper.testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x01, 5);
        TestHelper.testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x02, 5);

        TestHelper.testBitStringFailure(new TestBitString2(), 0x03, 5);
        TestHelper.testBitStringFailure(new TestBitString2(), 0x00, 4);
        TestHelper.testBitStringFailure(new TestBitString2(), 0x00, 6);
    }

    @Test
    public void testBitString3() {
        TestHelper.testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x00, 5);
        TestHelper.testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x01, 5);

        TestHelper.testBitStringFailure(new TestBitString3(), 0x02, 5);
    }

    @Test
    public void testBitString4() {
        TestHelper.testBitStringSuccess(TestBitString4.class, new TestBitString4(), 0x00, 5);
        TestHelper.testBitStringSuccess(TestBitString4.class, new TestBitString4(), 0x01, 5);
        TestHelper.testBitStringSuccess(TestBitString4.class, new TestBitString4(), 0x02, 5);
        TestHelper.testBitStringSuccess(TestBitString4.class, new TestBitString4(), 0x03, 6);
        TestHelper.testBitStringSuccess(TestBitString4.class, new TestBitString4(), 0x03, 4);
    }

    @Test
    public void testBitString5() {
        TestHelper.testBitStringSuccess(TestBitString5.class, new TestBitString5(), 0x00, 0);
        TestHelper.testBitStringSuccess(TestBitString5.class, new TestBitString5(), 0xffee, 1);
    }

    @Test
    public void testBitString6() {
        TestHelper.testBitStringSuccess(TestBitString6.class, new TestBitString6(), 0x01, 0);
        TestHelper.testBitStringSuccess(TestBitString6.class, new TestBitString6(), 0x02, 4);
        TestHelper.testBitStringSuccess(TestBitString6.class, new TestBitString6(), 0x02, 6);
        TestHelper.testBitStringSuccess(TestBitString6.class, new TestBitString6(), 0xffee, 1);

        TestHelper.testBitStringFailure(new TestBitString6(), 0x02, 5);
    }

}
