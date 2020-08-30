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

package ch.eskaton.asn4j.test.x690_8;

import ch.eskaton.asn4j.runtime.BERDecoder;
import ch.eskaton.asn4j.runtime.BEREncoder;
import ch.eskaton.asn4j.runtime.types.ASN1Boolean;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1OctetString;
import ch.eskaton.asn4j.runtime.types.ASN1VisibleString;
import ch.eskaton.asn4j.test.modules.X690_8.TestSequence;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence1;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TestX690_8_9 {

    @Test
    void testEncodeTestSequence() {
        BEREncoder encoder = new BEREncoder();

        TestSequence a = new TestSequence();

        a.setA(new ASN1VisibleString("test"));

        assertArrayEquals(new byte[] { 0x30, 0x0a, (byte) 0xe3, 0x08, (byte) 0xa2, 0x06, 0x1a, 0x04, 0x74, 0x65,
                0x73, 0x74 }, encoder.encode(a));
    }

    @Test
    void testEncodeTestSequence1() {
        BEREncoder encoder = new BEREncoder();

        TestSequence1 a = new TestSequence1();

        a.setA(ASN1Integer.valueOf(4711));
        a.setB(ASN1Boolean.FALSE);
        a.setC(ASN1OctetString.valueOf(new byte[] { 0x74, 0x65, 0x73, 0x74 }));

        assertArrayEquals(new byte[] { 0x30, 0x0d, 0x02, 0x02, 0x12, 0x67, 0x01, 0x01, (byte) 0x00, 0x04,
                0x04, 0x74, 0x65, 0x73, 0x74 }, encoder.encode(a));
    }

    @Test
    void testDecodeTestSequence2() {
        BERDecoder decoder = new BERDecoder();

        TestSequence2 a = new TestSequence2();

        a.setA(ASN1Integer.valueOf(4711));
        a.setC(ASN1OctetString.valueOf(new byte[] { 0x74, 0x65, 0x73, 0x74 }));

        assertEquals(a, decoder.decode(TestSequence2.class,
                new byte[] { 0x30, 0x0a, 0x02, 0x02, 0x12, 0x67, 0x04, 0x04, 0x74, 0x65, 0x73, 0x74 }));
    }

}
