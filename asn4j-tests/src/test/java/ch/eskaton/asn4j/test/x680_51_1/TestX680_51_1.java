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
import ch.eskaton.asn4jtest.x680_51_1.TestBitString7;
import ch.eskaton.asn4jtest.x680_51_1.TestBoolean2;
import ch.eskaton.asn4jtest.x680_51_1.TestEnumeration1;
import ch.eskaton.asn4jtest.x680_51_1.TestEnumeration2;
import ch.eskaton.asn4jtest.x680_51_1.TestEnumeration3;
import ch.eskaton.asn4jtest.x680_51_1.TestEnumeration4;
import ch.eskaton.asn4jtest.x680_51_1.TestInteger1;
import ch.eskaton.asn4jtest.x680_51_1.TestInteger10;
import ch.eskaton.asn4jtest.x680_51_1.TestInteger11;
import ch.eskaton.asn4jtest.x680_51_1.TestInteger2;
import ch.eskaton.asn4jtest.x680_51_1.TestInteger3;
import ch.eskaton.asn4jtest.x680_51_1.TestInteger4;
import ch.eskaton.asn4jtest.x680_51_1.TestInteger5;
import ch.eskaton.asn4jtest.x680_51_1.TestInteger6;
import ch.eskaton.asn4jtest.x680_51_1.TestInteger7;
import ch.eskaton.asn4jtest.x680_51_1.TestInteger8;
import ch.eskaton.asn4jtest.x680_51_1.TestInteger9;
import ch.eskaton.asn4jtest.x680_51_1.TestNull1;
import ch.eskaton.asn4jtest.x680_51_1.TestNull2;
import org.junit.Test;

import static ch.eskaton.asn4j.test.TestHelper.testBitStringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testBitStringSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testBooleanFailure;
import static ch.eskaton.asn4j.test.TestHelper.testBooleanSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testEnumeratedFailure;
import static ch.eskaton.asn4j.test.TestHelper.testEnumeratedSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testIntegerFailure;
import static ch.eskaton.asn4j.test.TestHelper.testIntegerSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testNullFailure;
import static ch.eskaton.asn4j.test.TestHelper.testNullSuccess;

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
    public void testBitString7() {
        testBitStringSuccess(TestBitString7.class, new TestBitString7(), 0x00, 5);
        testBitStringSuccess(TestBitString7.class, new TestBitString7(), 0x02, 5);
        testBitStringSuccess(TestBitString7.class, new TestBitString7(), 0x01, 6);

        testBitStringFailure(new TestBitString7(), 0x01, 5);
    }

    @Test
    public void testBoolean2() {
        testBooleanSuccess(TestBoolean2.class, new TestBoolean2(), false);

        testBooleanFailure(new TestBoolean2(), true);
    }

    @Test
    public void testEnumeration2() {
        testEnumeratedSuccess(TestEnumeration2.class, new TestEnumeration2(), TestEnumeration1.D);
        testEnumeratedSuccess(TestEnumeration2.class, new TestEnumeration2(), TestEnumeration1.E);

        testEnumeratedFailure(new TestEnumeration2(), TestEnumeration1.A);
        testEnumeratedFailure(new TestEnumeration2(), TestEnumeration1.B);
        testEnumeratedFailure(new TestEnumeration2(), TestEnumeration1.C);
        testEnumeratedFailure(new TestEnumeration2(), TestEnumeration1.F);
    }

    @Test
    public void testEnumeration3() {
        testEnumeratedSuccess(TestEnumeration3.class, new TestEnumeration3(), TestEnumeration1.A);
        testEnumeratedSuccess(TestEnumeration3.class, new TestEnumeration3(), TestEnumeration1.B);
        testEnumeratedSuccess(TestEnumeration3.class, new TestEnumeration3(), TestEnumeration1.E);
        testEnumeratedSuccess(TestEnumeration3.class, new TestEnumeration3(), TestEnumeration1.F);

        testEnumeratedFailure(new TestEnumeration3(), TestEnumeration1.C);
        testEnumeratedFailure(new TestEnumeration3(), TestEnumeration1.D);
    }

    @Test
    public void testEnumeration4() {
        testEnumeratedSuccess(TestEnumeration4.class, new TestEnumeration4(), TestEnumeration1.D);

        testEnumeratedFailure(new TestEnumeration4(), TestEnumeration1.A);
        testEnumeratedFailure(new TestEnumeration4(), TestEnumeration1.B);
        testEnumeratedFailure(new TestEnumeration4(), TestEnumeration1.C);
        testEnumeratedFailure(new TestEnumeration4(), TestEnumeration1.E);
        testEnumeratedFailure(new TestEnumeration4(), TestEnumeration1.F);
    }

    @Test
    public void testInteger1() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger1.class, -1, 1);
        testIntegerFailure(TestInteger1.class, 0);
    }

    @Test
    public void testInteger2() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger2.class, -2, 2);
        testIntegerFailure(TestInteger2.class, -1, 0, 1);
    }

    @Test
    public void testInteger3() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger3.class, 0, 1, 2, 4, 5, 6, 9);
        testIntegerFailure(TestInteger3.class, -1, 3, 7, 8, 10);
    }

    @Test
    public void testInteger4() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger4.class, 1, 2, 4, 5);
        testIntegerFailure(TestInteger4.class, 0, 3, 6);
    }

    @Test
    public void testInteger5() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger5.class, 5, 6);
        testIntegerFailure(TestInteger5.class, 4, 7, 8, 9);
    }

    @Test
    public void testInteger6() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger6.class, 4, 5);
        testIntegerFailure(TestInteger6.class, 0, 1, 2, 3, 6);
    }

    @Test
    public void testInteger7() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger7.class, 5);
        testIntegerFailure(TestInteger7.class, 0, 1, 2, 3, 4, 6);
    }

    @Test
    public void testInteger8() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger8.class, 6);
        testIntegerFailure(TestInteger8.class, 4, 5, 7, 8, 9);
    }

    @Test
    public void testInteger9() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger9.class, 2, 4);
        testIntegerFailure(TestInteger9.class, 3);
    }

    @Test
    public void testInteger10() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger10.class, 0, 2);
        testIntegerFailure(TestInteger10.class, 1);
    }

    @Test
    public void testInteger11() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger11.class, 3, 5);
        testIntegerFailure(TestInteger11.class, 4);
    }

    @Test
    public void testNull1() {
        testNullSuccess(TestNull1.class, () -> new TestNull1());
    }

    @Test
    public void testNull2() {
        testNullFailure(() -> new TestNull2());
    }

}
