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

package ch.eskaton.asn4j.test.x680_41;

import ch.eskaton.asn4j.test.modules.x680_41.TestGeneralString1;
import ch.eskaton.asn4j.test.modules.x680_41.TestGraphicString1;
import ch.eskaton.asn4j.test.modules.x680_41.TestIA5String1;
import ch.eskaton.asn4j.test.modules.x680_41.TestNumericString1;
import ch.eskaton.asn4j.test.modules.x680_41.TestPrintableString1;
import ch.eskaton.asn4j.test.modules.x680_41.TestTeletexString1;
import ch.eskaton.asn4j.test.modules.x680_41.TestVideotexString1;
import ch.eskaton.asn4j.test.modules.x680_41.TestVisibleString1;
import org.junit.jupiter.api.Test;

import static ch.eskaton.asn4j.test.TestHelper.assertDecodableVerifyAfter;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TestX680_41 {

    @Test
    void testVisibleString1() {
        assertDecodableVerifyAfter(TestVisibleString1.class, value -> value.setValue("abcd"),
                value -> assertEquals(new TestVisibleString1("abcd"), value));
    }

    @Test
    void testNumericString1() {
        assertDecodableVerifyAfter(TestNumericString1.class, value -> value.setValue("1234"),
                value -> assertEquals(new TestNumericString1("1234"), value));
    }

    @Test
    void testPrintableString1() {
        assertDecodableVerifyAfter(TestPrintableString1.class, value -> value.setValue("ab12"),
                value -> assertEquals(new TestPrintableString1("ab12"), value));
    }

    @Test
    void testTeletexString1() {
        assertDecodableVerifyAfter(TestTeletexString1.class, value -> value.setValue("ab12ö"),
                value -> assertEquals(new TestTeletexString1("ab12ö"), value));
    }

    @Test
    void testVideotexString1() {
        assertDecodableVerifyAfter(TestVideotexString1.class, value -> value.setValue("ab12ö"),
                value -> assertEquals(new TestVideotexString1("ab12ö"), value));
    }

    @Test
    void testIA5String1() {
        assertDecodableVerifyAfter(TestIA5String1.class, value -> value.setValue("ab12"),
                value -> assertEquals(new TestIA5String1("ab12"), value));
    }

    @Test
    void testGraphicString1() {
        assertDecodableVerifyAfter(TestGraphicString1.class, value -> value.setValue("ab12"),
                value -> assertEquals(new TestGraphicString1("ab12"), value));
    }

    @Test
    void testGeneralString1() {
        assertDecodableVerifyAfter(TestGeneralString1.class, value -> value.setValue("ab12"),
                value -> assertEquals(new TestGeneralString1("ab12"), value));
    }

}
