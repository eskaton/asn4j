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
import ch.eskaton.asn4j.runtime.exceptions.DecodingException;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1OctetString;
import ch.eskaton.asn4j.test.modules.x680_27.TestSet1;
import ch.eskaton.asn4j.test.modules.x680_27.TestSet2;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class TestX690_8_11 {

    @Test
    void testEncodeTestSet1() {
        BEREncoder encoder = new BEREncoder();

        TestSet1 a = new TestSet1();

        a.setA(ASN1Integer.valueOf(23));

        assertArrayEquals(new byte[] { 0x31, 0x05, (byte) 0xa0, 0x03, 0x02, 0x01, 0x17 }, encoder.encode(a));

        a.setB(ASN1Integer.valueOf(15));

        assertArrayEquals(
                new byte[] { 0x31, 0x0a, (byte) 0xa0, 0x03, 0x02, 0x01, 0x17, (byte) 0xa1, 0x03, 0x02, 0x01, 0x0f },
                encoder.encode(a));
    }

    @Test
    void testEncodeTestSet2() {
        BEREncoder encoder = new BEREncoder();

        TestSet2 a = new TestSet2();

        a.setA(ASN1Integer.valueOf(4711));
        a.setB(ASN1OctetString.valueOf(new byte[] { 0x74, 0x65, 0x73, 0x74 }));

        assertArrayEquals(new byte[] { 0x31, 0x0e, (byte) 0xa0, 0x04, 0x02, 0x02, 0x12, 0x67, (byte) 0xa1, 0x06, 0x04,
                0x04, 0x74, 0x65, 0x73, 0x74 }, encoder.encode(a));

        a.setC(ASN1Integer.valueOf(23));

        assertArrayEquals(new byte[] { 0x31, 0x11, (byte) 0xa0, 0x04, 0x02, 0x02, 0x12, 0x67, (byte) 0xa1, 0x06, 0x04,
                0x04, 0x74, 0x65, 0x73, 0x74, 0x02, 0x01, 0x17 }, encoder.encode(a));
    }

    @Test
    void testDecodeTestSet2() {
        BERDecoder decoder = new BERDecoder();

        TestSet2 a = new TestSet2();
        a.setA(ASN1Integer.valueOf(4711));
        a.setB(ASN1OctetString.valueOf(new byte[] { 0x74, 0x65, 0x73, 0x74 }));

        assertEquals(a, decoder.decode(TestSet2.class, new byte[] { 0x31, 0x0e, (byte) 0xa1, 0x06, 0x04,
                0x04, 0x74, 0x65, 0x73, 0x74, (byte) 0xa0, 0x04, 0x02, 0x02, 0x12, 0x67 }));
    }

    @Test
    void testDecodeTestSet2MissingField() {
        BERDecoder decoder = new BERDecoder();

        try {
            decoder.decode(TestSet2.class, new byte[] { 0x31, 0x06, (byte) 0xa0, 0x04, 0x02, 0x02, 0x12, 0x67 });
            fail("Decoding Exception expected");
        } catch (DecodingException e) {
            assertThat(e.getMessage(), containsString("Mandatory fields missing"));
        }
    }

}
