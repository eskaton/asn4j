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

import ch.eskaton.asn4j.test.modules.x680_51_7.TestBMPString1;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestBMPString2;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestGeneralString1;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestGeneralString2;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestGraphicString1;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestGraphicString2;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestIA5String1;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestIA5String2;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestISO646String1;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestISO646String2;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestNumericString1;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestNumericString2;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestPrintableString1;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestPrintableString2;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestT61String1;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestT61String2;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestTeletexString1;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestTeletexString2;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestUTF8String1;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestUTF8String2;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestUniversalString1;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestUniversalString2;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestVideotexString1;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestVideotexString2;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestVisibleString1;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestVisibleString2;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestVisibleString3;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestVisibleString4;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestVisibleString5;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestVisibleString6;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestVisibleString7;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestVisibleString8;
import ch.eskaton.asn4j.test.modules.x680_51_7.TestVisibleString9;
import org.junit.jupiter.api.Test;

import static ch.eskaton.asn4j.test.TestHelper.testBMPStringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testBMPStringSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testGeneralStringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testGeneralStringSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testGraphicStringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testGraphicStringSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testIA5StringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testIA5StringSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testNumericStringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testNumericStringSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testPrintableStringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testPrintableStringSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testTeletexStringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testTeletexStringSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testUTF8StringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testUTF8StringSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testUniversalStringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testUniversalStringSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testVideotexStringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testVideotexStringSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testVisibleStringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testVisibleStringSuccess;

class TestX680_51_7 {

    @Test
    void testVisibleString1() {
        testVisibleStringSuccess(TestVisibleString1.class, new TestVisibleString1(""));
        testVisibleStringSuccess(TestVisibleString1.class, new TestVisibleString1("aaa"));
        testVisibleStringSuccess(TestVisibleString1.class, new TestVisibleString1("abc"));

        testVisibleStringFailure(TestVisibleString1.class, new TestVisibleString1("ABC"));
        testVisibleStringFailure(TestVisibleString1.class, new TestVisibleString1("aadd"));
    }

    @Test
    void testVisibleString2() {
        testVisibleStringSuccess(TestVisibleString2.class, new TestVisibleString2("aaa"));
        testVisibleStringSuccess(TestVisibleString2.class, new TestVisibleString2("abc"));

        testVisibleStringFailure(TestVisibleString2.class, new TestVisibleString2("ABC"));
        testVisibleStringFailure(TestVisibleString2.class, new TestVisibleString2("aadd"));
    }

    @Test
    void testVisibleString3() {
        testVisibleStringSuccess(TestVisibleString3.class, new TestVisibleString3("a"));
        testVisibleStringSuccess(TestVisibleString3.class, new TestVisibleString3("aaa"));

        testVisibleStringFailure(TestVisibleString3.class, new TestVisibleString3("AAA"));
    }

    @Test
    void testVisibleString4() {
        testVisibleStringSuccess(TestVisibleString4.class, new TestVisibleString4("abcxyz"));

        testVisibleStringFailure(TestVisibleString4.class, new TestVisibleString4("xyzABC"));
    }

    @Test
    void testVisibleString5() {
        testVisibleStringSuccess(TestVisibleString5.class, new TestVisibleString5("abd"));

        testVisibleStringFailure(TestVisibleString5.class, new TestVisibleString5("abc"));
    }

    @Test
    void testVisibleString6() {
        testVisibleStringSuccess(TestVisibleString6.class, new TestVisibleString6("abc"));
        testVisibleStringSuccess(TestVisibleString6.class, new TestVisibleString6("xyz"));

        testVisibleStringFailure(TestVisibleString6.class, new TestVisibleString6("lmn"));
    }

    @Test
    void testVisibleString7() {
        testVisibleStringSuccess(TestVisibleString7.class, new TestVisibleString7("cd"));

        testVisibleStringFailure(TestVisibleString7.class, new TestVisibleString7("abc"));
        testVisibleStringFailure(TestVisibleString7.class, new TestVisibleString7("def"));
    }

    @Test
    void testVisibleString8() {
        testVisibleStringSuccess(TestVisibleString8.class, new TestVisibleString8("bccd"));
        testVisibleStringSuccess(TestVisibleString8.class, new TestVisibleString8("bf"));

        testVisibleStringFailure(TestVisibleString8.class, new TestVisibleString8("ab"));
        testVisibleStringFailure(TestVisibleString8.class, new TestVisibleString8("bff"));
        testVisibleStringFailure(TestVisibleString8.class, new TestVisibleString8("de"));
    }

    @Test
    void testVisibleString9() {
        testVisibleStringSuccess(TestVisibleString9.class, new TestVisibleString9("cdcd"));

        testVisibleStringFailure(TestVisibleString9.class, new TestVisibleString9("bc"));
        testVisibleStringFailure(TestVisibleString9.class, new TestVisibleString9("de"));
    }

    @Test
    void testISO646String1() {
        testVisibleStringSuccess(TestISO646String1.class, new TestISO646String1("bccd"));
        testVisibleStringSuccess(TestISO646String1.class, new TestISO646String1("bf"));

        testVisibleStringFailure(TestISO646String1.class, new TestISO646String1("ab"));
        testVisibleStringFailure(TestISO646String1.class, new TestISO646String1("bff"));
        testVisibleStringFailure(TestISO646String1.class, new TestISO646String1("de"));
    }

    @Test
    void testISO646String2() {
        testVisibleStringSuccess(TestISO646String2.class, new TestISO646String2("cdcd"));

        testVisibleStringFailure(TestISO646String2.class, new TestISO646String2("bc"));
        testVisibleStringFailure(TestISO646String2.class, new TestISO646String2("de"));
    }

    @Test
    void testGeneralString1() {
        testGeneralStringSuccess(TestGeneralString1.class, new TestGeneralString1("bccd"));
        testGeneralStringSuccess(TestGeneralString1.class, new TestGeneralString1("bf"));

        testGeneralStringFailure(TestGeneralString1.class, new TestGeneralString1("ab"));
        testGeneralStringFailure(TestGeneralString1.class, new TestGeneralString1("bff"));
        testGeneralStringFailure(TestGeneralString1.class, new TestGeneralString1("de"));
    }

    @Test
    void testGeneralString2() {
        testGeneralStringSuccess(TestGeneralString2.class, new TestGeneralString2("cdcd"));

        testGeneralStringFailure(TestGeneralString2.class, new TestGeneralString2("bc"));
        testGeneralStringFailure(TestGeneralString2.class, new TestGeneralString2("de"));
    }

    @Test
    void testGraphicString1() {
        testGraphicStringSuccess(TestGraphicString1.class, new TestGraphicString1("bccd"));
        testGraphicStringSuccess(TestGraphicString1.class, new TestGraphicString1("bf"));

        testGraphicStringFailure(TestGraphicString1.class, new TestGraphicString1("ab"));
        testGraphicStringFailure(TestGraphicString1.class, new TestGraphicString1("bff"));
        testGraphicStringFailure(TestGraphicString1.class, new TestGraphicString1("de"));
    }

    @Test
    void testGraphicString2() {
        testGraphicStringSuccess(TestGraphicString2.class, new TestGraphicString2("cdcd"));

        testGraphicStringFailure(TestGraphicString2.class, new TestGraphicString2("bc"));
        testGraphicStringFailure(TestGraphicString2.class, new TestGraphicString2("de"));
    }

    @Test
    void testIA5String1() {
        testIA5StringSuccess(TestIA5String1.class, new TestIA5String1("bccd"));
        testIA5StringSuccess(TestIA5String1.class, new TestIA5String1("bf"));

        testIA5StringFailure(TestIA5String1.class, new TestIA5String1("ab"));
        testIA5StringFailure(TestIA5String1.class, new TestIA5String1("bff"));
        testIA5StringFailure(TestIA5String1.class, new TestIA5String1("de"));
    }

    @Test
    void testIA5String2() {
        testIA5StringSuccess(TestIA5String2.class, new TestIA5String2("cdcd"));

        testIA5StringFailure(TestIA5String2.class, new TestIA5String2("bc"));
        testIA5StringFailure(TestIA5String2.class, new TestIA5String2("de"));
    }

    @Test
    void testVideotexString1() {
        testVideotexStringSuccess(TestVideotexString1.class, new TestVideotexString1("bccd"));
        testVideotexStringSuccess(TestVideotexString1.class, new TestVideotexString1("bf"));

        testVideotexStringFailure(TestVideotexString1.class, new TestVideotexString1("ab"));
        testVideotexStringFailure(TestVideotexString1.class, new TestVideotexString1("bff"));
        testVideotexStringFailure(TestVideotexString1.class, new TestVideotexString1("de"));
    }

    @Test
    void testVideotexString2() {
        testVideotexStringSuccess(TestVideotexString2.class, new TestVideotexString2("cdcd"));

        testVideotexStringFailure(TestVideotexString2.class, new TestVideotexString2("bc"));
        testVideotexStringFailure(TestVideotexString2.class, new TestVideotexString2("de"));
    }

    @Test
    void testTeletexString1() {
        testTeletexStringSuccess(TestTeletexString1.class, new TestTeletexString1("bccd"));
        testTeletexStringSuccess(TestTeletexString1.class, new TestTeletexString1("bf"));

        testTeletexStringFailure(TestTeletexString1.class, new TestTeletexString1("ab"));
        testTeletexStringFailure(TestTeletexString1.class, new TestTeletexString1("bff"));
        testTeletexStringFailure(TestTeletexString1.class, new TestTeletexString1("de"));
    }

    @Test
    void testTeletexString2() {
        testTeletexStringSuccess(TestTeletexString2.class, new TestTeletexString2("cdcd"));

        testTeletexStringFailure(TestTeletexString2.class, new TestTeletexString2("bc"));
        testTeletexStringFailure(TestTeletexString2.class, new TestTeletexString2("de"));
    }

    @Test
    void testT61String1() {
        testTeletexStringSuccess(TestT61String1.class, new TestT61String1("bccd"));
        testTeletexStringSuccess(TestT61String1.class, new TestT61String1("bf"));

        testTeletexStringFailure(TestT61String1.class, new TestT61String1("ab"));
        testTeletexStringFailure(TestT61String1.class, new TestT61String1("bff"));
        testTeletexStringFailure(TestT61String1.class, new TestT61String1("de"));
    }

    @Test
    void testT61String2() {
        testTeletexStringSuccess(TestT61String2.class, new TestT61String2("cdcd"));

        testTeletexStringFailure(TestT61String2.class, new TestT61String2("bc"));
        testTeletexStringFailure(TestT61String2.class, new TestT61String2("de"));
    }

    @Test
    void testPrintableString1() {
        testPrintableStringSuccess(TestPrintableString1.class, new TestPrintableString1("bccd"));
        testPrintableStringSuccess(TestPrintableString1.class, new TestPrintableString1("bf"));

        testPrintableStringFailure(TestPrintableString1.class, new TestPrintableString1("ab"));
        testPrintableStringFailure(TestPrintableString1.class, new TestPrintableString1("bff"));
        testPrintableStringFailure(TestPrintableString1.class, new TestPrintableString1("de"));
    }

    @Test
    void testPrintableString2() {
        testPrintableStringSuccess(TestPrintableString2.class, new TestPrintableString2("cdcd"));

        testPrintableStringFailure(TestPrintableString2.class, new TestPrintableString2("bc"));
        testPrintableStringFailure(TestPrintableString2.class, new TestPrintableString2("de"));
    }

    @Test
    void testNumericString1() {
        testNumericStringSuccess(TestNumericString1.class, new TestNumericString1("2334"));
        testNumericStringSuccess(TestNumericString1.class, new TestNumericString1("123"));

        testNumericStringFailure(TestNumericString1.class, new TestNumericString1("12"));
        testNumericStringFailure(TestNumericString1.class, new TestNumericString1("1234"));
        testNumericStringFailure(TestNumericString1.class, new TestNumericString1("45"));
    }

    @Test
    void testNumericString2() {
        testNumericStringSuccess(TestNumericString2.class, new TestNumericString2("3344"));

        testNumericStringFailure(TestNumericString2.class, new TestNumericString2("12"));
        testNumericStringFailure(TestNumericString2.class, new TestNumericString2("45"));
    }

    @Test
    void testUTF8String1() {
        testUTF8StringSuccess(TestUTF8String1.class, new TestUTF8String1("bccd"));
        testUTF8StringSuccess(TestUTF8String1.class, new TestUTF8String1("bf"));

        testUTF8StringFailure(TestUTF8String1.class, new TestUTF8String1("ab"));
        testUTF8StringFailure(TestUTF8String1.class, new TestUTF8String1("bff"));
        testUTF8StringFailure(TestUTF8String1.class, new TestUTF8String1("de"));
    }

    @Test
    void testUTF8String2() {
        testUTF8StringSuccess(TestUTF8String2.class, new TestUTF8String2("cdcd"));

        testUTF8StringFailure(TestUTF8String2.class, new TestUTF8String2("bc"));
        testUTF8StringFailure(TestUTF8String2.class, new TestUTF8String2("de"));
    }

    @Test
    void testUniversalString1() {
        testUniversalStringSuccess(TestUniversalString1.class, new TestUniversalString1("bccd"));
        testUniversalStringSuccess(TestUniversalString1.class, new TestUniversalString1("bf"));

        testUniversalStringFailure(TestUniversalString1.class, new TestUniversalString1("ab"));
        testUniversalStringFailure(TestUniversalString1.class, new TestUniversalString1("bff"));
        testUniversalStringFailure(TestUniversalString1.class, new TestUniversalString1("de"));
    }

    @Test
    void testUniversalString2() {
        testUniversalStringSuccess(TestUniversalString2.class, new TestUniversalString2("cdcd"));

        testUniversalStringFailure(TestUniversalString2.class, new TestUniversalString2("bc"));
        testUniversalStringFailure(TestUniversalString2.class, new TestUniversalString2("de"));
    }

    @Test
    void testBMPString1() {
        testBMPStringSuccess(TestBMPString1.class, new TestBMPString1("bccd"));
        testBMPStringSuccess(TestBMPString1.class, new TestBMPString1("bf"));

        testBMPStringFailure(TestBMPString1.class, new TestBMPString1("ab"));
        testBMPStringFailure(TestBMPString1.class, new TestBMPString1("bff"));
        testBMPStringFailure(TestBMPString1.class, new TestBMPString1("de"));
    }

    @Test
    void testBMPString2() {
        testBMPStringSuccess(TestBMPString2.class, new TestBMPString2("cdcd"));

        testBMPStringFailure(TestBMPString2.class, new TestBMPString2("bc"));
        testBMPStringFailure(TestBMPString2.class, new TestBMPString2("de"));
    }

}
