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

package ch.eskaton.asn4j.test.x680_20;

import ch.eskaton.asn4j.runtime.BERDecoder;
import ch.eskaton.asn4j.runtime.BEREncoder;
import ch.eskaton.asn4j.test.modules.x680_20.TestEnumeration;
import ch.eskaton.asn4j.test.modules.x680_20.TestEnumerations;
import org.junit.jupiter.api.Test;

import static ch.eskaton.asn4j.test.TestHelper.assertDecodableVerifyAfter;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestX680_20 {

    @Test
    void test1() {
        testEnumeration(TestEnumeration.M, -3);
    }

    @Test
    void test2() {
        testEnumeration(TestEnumeration.A, 1);
    }

    @Test
    void test3() {
        testEnumeration(TestEnumeration.B, 2);
    }

    @Test
    void test4() {
        testEnumeration(TestEnumeration.C, 0);
    }

    @Test
    void test5() {
        testEnumeration(TestEnumeration.D, 3);
    }

    @Test
    void test6() {
        testEnumeration(TestEnumeration.E, 5);
    }

    @Test
    void test7() {
        testEnumeration(TestEnumeration.F, 6);
    }

    @Test
    void test8() {
        testEnumeration(TestEnumeration.G, 9);
    }

    @Test
    void test9() {
        testEnumeration(TestEnumeration.H, 10);
    }

    @Test
    void testEnumerationDefault() {
        assertDecodableVerifyAfter(TestEnumerations.class, value -> {
            assertEquals(TestEnumeration.M, value.getTestEnumeration1());
            assertEquals(TestEnumeration.E, value.getTestEnumeration2());
            assertEquals(TestEnumeration.E, value.getTestEnumeration3());

        });
    }

    private void testEnumeration(TestEnumeration a, int enumValue) {
        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        TestEnumeration b = decoder.decode(TestEnumeration.class, encoder.encode(a));

        assertEquals(a, b);
        assertEquals(enumValue, b.getValue());
    }

}
