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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import ch.eskaton.asn4j.runtime.BERDecoder;
import ch.eskaton.asn4j.runtime.BEREncoder;
import ch.eskaton.asn4j.runtime.exceptions.ASN1RuntimeException;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;

public class TestX690_8_3 {

    @Test
    public void testEncode() {
        BEREncoder encoder = new BEREncoder();

        assertArrayEquals(new byte[] { 0x02, 0x01, 0x00 },
                encoder.encode(ASN1Integer.valueOf(0)));
        assertArrayEquals(new byte[] { 0x02, 0x01, 0x7F },
                encoder.encode(ASN1Integer.valueOf(127)));
        assertArrayEquals(new byte[] { 0x02, 0x02, 0x00, (byte) 0x80 },
                encoder.encode(ASN1Integer.valueOf(128)));
        assertArrayEquals(new byte[] { 0x02, 0x02, 0x00, (byte) 0xFF },
                encoder.encode(ASN1Integer.valueOf(255)));
        assertArrayEquals(new byte[] { 0x02, 0x02, 0x01, 0x00 },
                encoder.encode(ASN1Integer.valueOf(256)));
        assertArrayEquals(new byte[] { 0x02, 0x02, 0x12, 0x67 },
                encoder.encode(ASN1Integer.valueOf(4711)));
    }

    @Test
    public void testDecode() {
        BERDecoder decoder = new BERDecoder();

        assertEquals(ASN1Integer.valueOf(0), decoder.decode(ASN1Integer.class,
                new byte[] { 0x02, 0x01, 0x00 }));
        assertEquals(ASN1Integer.valueOf(127), decoder.decode(
                ASN1Integer.class, new byte[] { 0x02, 0x01, 0x7F }));
        assertEquals(
                ASN1Integer.valueOf(128),
                decoder.decode(ASN1Integer.class, new byte[] { 0x02, 0x02,
                        0x00, (byte) 0x80 }));
        assertEquals(
                ASN1Integer.valueOf(255),
                decoder.decode(ASN1Integer.class, new byte[] { 0x02, 0x02,
                        0x00, (byte) 0xFF }));
        assertEquals(
                ASN1Integer.valueOf(256),
                decoder.decode(ASN1Integer.class, new byte[] { 0x02, 0x02,
                        0x01, 0x00 }));
        assertEquals(
                ASN1Integer.valueOf(4711),
                decoder.decode(ASN1Integer.class, new byte[] { 0x02, 0x02,
                        0x12, 0x67 }));
    }

}
