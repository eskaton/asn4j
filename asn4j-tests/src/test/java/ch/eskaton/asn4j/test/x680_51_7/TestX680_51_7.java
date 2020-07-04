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

package ch.eskaton.asn4j.test.x680_51_7;

import ch.eskaton.asn4j.test.modules.x680_51_7.TestVisibleString1;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestVisibleString2;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestVisibleString3;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestVisibleString4;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestVisibleString5;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestVisibleString6;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestVisibleString7;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestVisibleString8;
import org.junit.jupiter.api.Test;

import static ch.eskaton.asn4j.test.TestHelper.testVisibleStringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testVisibleStringSuccess;

class TestX680_51_7 {

    @Test
    public void testVisibleString1() {
        testVisibleStringSuccess(TestVisibleString1.class, new TestVisibleString1(""));
        testVisibleStringSuccess(TestVisibleString1.class, new TestVisibleString1("aaa"));
        testVisibleStringSuccess(TestVisibleString1.class, new TestVisibleString1("abc"));

        testVisibleStringFailure(TestVisibleString1.class, new TestVisibleString1("ABC"));
        testVisibleStringFailure(TestVisibleString1.class, new TestVisibleString1("aadd"));
    }

    @Test
    public void testVisibleString2() {
        testVisibleStringSuccess(TestVisibleString2.class, new TestVisibleString2("aaa"));
        testVisibleStringSuccess(TestVisibleString2.class, new TestVisibleString2("abc"));

        testVisibleStringFailure(TestVisibleString2.class, new TestVisibleString2("ABC"));
        testVisibleStringFailure(TestVisibleString2.class, new TestVisibleString2("aadd"));
    }

    @Test
    public void testVisibleString3() {
        testVisibleStringSuccess(TestVisibleString3.class, new TestVisibleString3("a"));
        testVisibleStringSuccess(TestVisibleString3.class, new TestVisibleString3("aaa"));

        testVisibleStringFailure(TestVisibleString3.class, new TestVisibleString3("AAA"));
    }

    @Test
    public void testVisibleString4() {
        testVisibleStringSuccess(TestVisibleString4.class, new TestVisibleString4("abcxyz"));

        testVisibleStringFailure(TestVisibleString4.class, new TestVisibleString4("xyzABC"));
    }

    @Test
    public void testVisibleString5() {
        testVisibleStringSuccess(TestVisibleString5.class, new TestVisibleString5("abd"));

        testVisibleStringFailure(TestVisibleString5.class, new TestVisibleString5("abc"));
    }

    @Test
    public void testVisibleString6() {
        testVisibleStringSuccess(TestVisibleString6.class, new TestVisibleString6("abc"));
        testVisibleStringSuccess(TestVisibleString6.class, new TestVisibleString6("xyz"));

        testVisibleStringFailure(TestVisibleString6.class, new TestVisibleString6("lmn"));
    }

    @Test
    public void testVisibleString7() {
        testVisibleStringSuccess(TestVisibleString7.class, new TestVisibleString7("cd"));

        testVisibleStringFailure(TestVisibleString7.class, new TestVisibleString7("abc"));
        testVisibleStringFailure(TestVisibleString7.class, new TestVisibleString7("def"));
    }

    @Test
    public void testVisibleString8() {
        testVisibleStringSuccess(TestVisibleString8.class, new TestVisibleString8("bc"));

        testVisibleStringFailure(TestVisibleString8.class, new TestVisibleString8("ab"));
        testVisibleStringFailure(TestVisibleString8.class, new TestVisibleString8("cd"));
    }

}
