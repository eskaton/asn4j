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

import ch.eskaton.asn4j.runtime.exceptions.DecodingException;
import ch.eskaton.asn4j.runtime.exceptions.PrematureEndOfInputException;
import org.junit.Test;

import ch.eskaton.asn4j.runtime.BERDecoder;
import ch.eskaton.asn4j.runtime.BEREncoder;
import ch.eskaton.asn4j.runtime.exceptions.ASN1RuntimeException;
import ch.eskaton.asn4j.runtime.types.ASN1BitString;

public class TestX690_8_6 {

    @Test
    public void testEncode() {
        BEREncoder encoder = new BEREncoder();

        assertArrayEquals(new byte[]{0x03, 0x01, 0x00},
                encoder.encode(ASN1BitString.of(new byte[]{})));
        assertArrayEquals(new byte[]{0x03, 0x02, 0x07, (byte) 0x80},
                encoder.encode(ASN1BitString.of(new byte[]{(byte) 0x80}, 7)));
        assertArrayEquals(new byte[]{0x03, 0x03, 0x07, (byte) 0xAA, (byte) 0x80},
                encoder.encode(ASN1BitString.of(new byte[]{(byte) 0xAA, (byte) 0x80}, 7)));
    }

    @Test
    public void testDecode() {
        BERDecoder decoder = new BERDecoder();

        assertEquals(
                ASN1BitString.of(new byte[]{}),
                decoder.decode(ASN1BitString.class, new byte[]{0x03, 0x01,
                        0x00}));
        assertEquals(
                ASN1BitString.of(new byte[]{(byte) 0x80}, 7),
                decoder.decode(ASN1BitString.class, new byte[]{0x03, 0x02,
                        0x07, (byte) 0x80}));
        assertEquals(
                ASN1BitString.of(new byte[]{0x55, 0x00}, 7),
                decoder.decode(ASN1BitString.class, new byte[]{0x03, 0x03,
                        0x07, 0x55, 0x00}));
    }

    @Test(expected = DecodingException.class)
    public void testDecodeUnusedBitsFailure() {
        new BERDecoder().decode(ASN1BitString.class, new byte[]{0x03, 0x01,
                0x01});
    }

    @Test(expected = PrematureEndOfInputException.class)
    public void testDecodeLengthFailure() {
        new BERDecoder().decode(ASN1BitString.class, new byte[]{0x03, 0x02,
                0x00});
    }

}
