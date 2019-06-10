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

import ch.eskaton.asn4jtest.x680_51_3.TestBitString2;
import ch.eskaton.asn4jtest.x680_51_3.TestBitString3;
import ch.eskaton.asn4jtest.x680_51_3.TestBitString4;
import ch.eskaton.asn4jtest.x680_51_3.TestBoolean2;
import ch.eskaton.asn4jtest.x680_51_3.TestEnumeration1;
import ch.eskaton.asn4jtest.x680_51_3.TestEnumeration2;
import ch.eskaton.asn4jtest.x680_51_3.TestInteger1;
import ch.eskaton.asn4jtest.x680_51_3.TestInteger2;
import org.junit.Test;

import static ch.eskaton.asn4j.test.TestHelper.testBitStringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testBitStringSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testBooleanFailure;
import static ch.eskaton.asn4j.test.TestHelper.testBooleanSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testEnumeratedFailure;
import static ch.eskaton.asn4j.test.TestHelper.testEnumeratedSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testIntegerFailure;
import static ch.eskaton.asn4j.test.TestHelper.testIntegerSuccess;

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

}
