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
import ch.eskaton.asn4j.runtime.types.ASN1Real;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TestX690_8_5 {

    @Test
    public void testEncode() {
        BEREncoder encoder = new BEREncoder();

        assertArrayEquals(new byte[] { 0x09, 0x01, 0x40 },
                encoder.encode(ASN1Real.PLUS_INFINITY));
        assertArrayEquals(new byte[] { 0x09, 0x01, 0x41 },
                encoder.encode(ASN1Real.MINUS_INFINITY));
        assertArrayEquals(new byte[] { 0x09, 0x01, 0x42 },
                encoder.encode(ASN1Real.NOT_A_NUMBER));
        assertArrayEquals(new byte[] { 0x09, 0x01, 0x43 },
                encoder.encode(ASN1Real.MINUS_ZERO));

        assertArrayEquals(new byte[] { 0x09, 0x00 },
                encoder.encode(ASN1Real.valueOf(0)));

        assertArrayEquals(new byte[] { 0x09, 0x03, 0x01, 0x20, 0x31 },
                encoder.encode(ASN1Real.valueOf(1)));

        assertArrayEquals(new byte[] { 0x09, 0x03, 0x01, 0x2D, 0x31 },
                encoder.encode(ASN1Real.valueOf(-1)));

        assertArrayEquals(new byte[] { 0x09, 0x06, 0x02, 0x20, 0x31, 0x2E,
                0x32, 0x35 }, encoder.encode(ASN1Real.valueOf(1.25)));
        assertArrayEquals(new byte[] { 0x09, 0x06, 0x02, 0x20, 0x30, 0x2E,
                0x32, 0x35 }, encoder.encode(ASN1Real.valueOf(0.25)));
    }

    @Test
    public void testDecode() {
        BERDecoder decoder = new BERDecoder();

        assertEquals(ASN1Real.PLUS_INFINITY,
                decoder.decode(ASN1Real.class, new byte[] { 0x09, 0x01, 0x40 }));
        assertEquals(ASN1Real.MINUS_INFINITY,
                decoder.decode(ASN1Real.class, new byte[] { 0x09, 0x01, 0x41 }));
        assertEquals(ASN1Real.NOT_A_NUMBER,
                decoder.decode(ASN1Real.class, new byte[] { 0x09, 0x01, 0x42 }));
        assertEquals(ASN1Real.MINUS_ZERO,
                decoder.decode(ASN1Real.class, new byte[] { 0x09, 0x01, 0x43 }));

        assertEquals(ASN1Real.valueOf(0),
                decoder.decode(ASN1Real.class, new byte[] { 0x09, 0x00 }));

        assertEquals(
                ASN1Real.valueOf(1),
                decoder.decode(ASN1Real.class, new byte[] { 0x09, 0x03, 0x01,
                        0x20, 0x31 }));

        assertEquals(
                ASN1Real.valueOf(-1),
                decoder.decode(ASN1Real.class, new byte[] { 0x09, 0x03, 0x01,
                        0x2D, 0x31 }));

        assertEquals(
                ASN1Real.valueOf(1.25),
                decoder.decode(ASN1Real.class, new byte[] { 0x09, 0x06, 0x02,
                        0x20, 0x31, 0x2E, 0x32, 0x35 }));
        assertEquals(
                ASN1Real.valueOf(0.25),
                decoder.decode(ASN1Real.class, new byte[] { 0x09, 0x06, 0x02,
                        0x20, 0x30, 0x2E, 0x32, 0x35 }));
    }

}
