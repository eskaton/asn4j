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

package ch.eskaton.asn4j.test.x680_26;

import ch.eskaton.asn4j.runtime.types.ASN1Boolean;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1SetOf;
import ch.eskaton.asn4j.test.modules.x680_26.TestSequenceOf1;
import ch.eskaton.asn4j.test.modules.x680_26.TestSequenceOf2;
import ch.eskaton.asn4j.test.modules.x680_26.TestSequenceOf2.TestSequenceOf2Content;
import ch.eskaton.asn4j.test.modules.x680_26.TestSequenceOf3;
import ch.eskaton.asn4j.test.modules.x680_26.TestSequenceOf3.TestSequenceOf3Content;
import ch.eskaton.asn4j.test.modules.x680_26.TestSequenceOf4;
import ch.eskaton.asn4j.test.modules.x680_26.TestSequenceOf4.TestSequenceOf4Content;
import ch.eskaton.asn4j.test.modules.x680_26.TestSequenceOf5;
import ch.eskaton.asn4j.test.modules.x680_26.TestSequenceOf5.TestSequenceOf5Content;
import ch.eskaton.asn4j.test.modules.x680_26.TestSequenceOf5.TestSequenceOf5Content.A;
import ch.eskaton.asn4j.test.modules.x680_26.TestSequenceOf5.TestSequenceOf5Content.A.AContent;
import ch.eskaton.asn4j.test.modules.x680_26.TestSequenceOf5.TestSequenceOf5Content.B;
import ch.eskaton.asn4j.test.modules.x680_26.TestSequenceOf5.TestSequenceOf5Content.B.BContent;
import org.junit.jupiter.api.Test;

import static ch.eskaton.asn4j.test.TestHelper.assertDecodable;
import static ch.eskaton.commons.utils.Utils.with;
import static java.util.Arrays.asList;

class TestX680_26 {

    @Test
    void testSequenceOf1() {
        assertDecodable(TestSequenceOf1.class,
                value -> value.setValues(ASN1Integer.valueOf(4711), ASN1Integer.valueOf(23)));
    }

    @Test
    void testSequenceOf2() {
        assertDecodable(TestSequenceOf2.class, value -> value.setValues(new ASN1SetOf<>(TestSequenceOf2Content.A)));
    }

    @Test
    void testSequenceOf3() {
        assertDecodable(TestSequenceOf3.class, value -> value.setValues(TestSequenceOf3Content.ONE));
    }

    @Test
    void testSequenceOf4() {
        assertDecodable(TestSequenceOf4.class, value -> {
            var bitString = new TestSequenceOf4Content(new byte[] { 0x00 }, 0);

            bitString.setBit(TestSequenceOf4Content.A);

            value.setValues(bitString);
        });
    }

    @Test
    void testSequenceOf5() {
        assertDecodable(TestSequenceOf5.class, value -> {
            value.setValues(with(new TestSequenceOf5Content(), (content) -> {
                content.setA(with(new A(), (a) ->
                        a.setValues(asList(with(new AContent(), (aContent) ->
                                aContent.setA(ASN1Integer.valueOf(12)))))));
                content.setB(with(new B(), (b) ->
                        b.setValues(asList(with(new BContent(), (bContent) ->
                                bContent.setB(ASN1Boolean.TRUE))))));
            }));
        });
    }

}
