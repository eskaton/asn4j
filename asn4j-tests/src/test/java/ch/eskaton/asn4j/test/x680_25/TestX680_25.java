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

package ch.eskaton.asn4j.test.x680_25;

import ch.eskaton.asn4j.runtime.types.ASN1Boolean;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1OctetString;
import ch.eskaton.asn4j.test.TestHelper;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence0;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence1;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence2;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence3;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence4;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence6;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence8;
import org.junit.Test;

import static ch.eskaton.asn4j.test.TestHelper.assertDecodable;
import static org.junit.Assert.assertEquals;

public class TestX680_25 {

    @Test
    public void testSequence0() {
        assertDecodable(TestSequence0.class, value -> value.setA(ASN1Boolean.TRUE));
    }

    @Test
    public void testSequence1() {
        assertDecodable(TestSequence1.class, value -> {
            value.setA(ASN1Integer.valueOf(4711));
            value.setB(ASN1Boolean.TRUE);
            value.setC(ASN1OctetString.valueOf(new byte[] { (byte) 0xff, (byte) 0x56 }));
        });
    }

    @Test
    public void testOptional() {
        assertDecodable(TestSequence2.class, value -> {
            value.setA(ASN1Integer.valueOf(4711));
            value.setC(ASN1OctetString.valueOf(new byte[] { (byte) 0xff, (byte) 0x56 }));
        });
    }

    @Test
    public void testDefaultAtEnd() {
        TestHelper.assertDecodableVerifyAfter(TestSequence3.class,
                value -> {
                    value.setA(ASN1Integer.valueOf(4711));
                    value.setB(ASN1Boolean.TRUE);
                },
                value -> assertEquals(ASN1OctetString.valueOf(new byte[] { (byte) 0xab, (byte) 0xc0 }), value.getC()));
    }

    @Test
    public void testDefaultAtStart() {
        TestHelper.assertDecodableVerifyAfter(TestSequence4.class,
                value -> {
                    value.setB(ASN1Boolean.TRUE);
                    value.setC(ASN1OctetString.valueOf(new byte[] { (byte) 0xff, (byte) 0x56 }));
                },
                value -> assertEquals(ASN1Integer.valueOf(4711), value.getA()));
    }

    @Test
    public void testComponentsOf() {
        assertDecodable(TestSequence6.class, value -> {
            value.setA(ASN1Integer.valueOf(4711));
            value.setB(ASN1Boolean.TRUE);
        });
    }

    @Test
    public void testComponentsOfDefault() {
        TestHelper.assertDecodableVerifyAfter(TestSequence6.class,
                value -> value.setB(ASN1Boolean.TRUE),
                value -> assertEquals(ASN1Integer.valueOf(23), value.getA()));
    }

    @Test
    public void testSequence8() {
        var childValue = new TestSequence8.ChildSequence();

        childValue.setA(ASN1Integer.valueOf(4711L));

        assertDecodable(TestSequence8.class, value -> value.setChildSequence(childValue));
    }

}
