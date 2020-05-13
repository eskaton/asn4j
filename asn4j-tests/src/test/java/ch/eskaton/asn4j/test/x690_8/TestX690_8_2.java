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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestX690_8_2 {

    @Test
    public void testEncode() {
        BEREncoder encoder = new BEREncoder();

        assertArrayEquals(new byte[] { 0x01, 0x01, 0x00 }, encoder.encode(ASN1Boolean.FALSE));
        assertArrayEquals(new byte[] { 0x01, 0x01, (byte) 0xFF }, encoder.encode(ASN1Boolean.TRUE));
        assertArrayEquals(new byte[] { 0x01, 0x01, (byte) 0x00 }, encoder.encode(ASN1Boolean.FALSE));
    }

    @Test
    public void testDecode() {
        BERDecoder decoder = new BERDecoder();

        assertEquals(ASN1Boolean.FALSE, decoder.decode(ASN1Boolean.class, new byte[] { 0x01, 0x01, 0x00 }));
        assertEquals(ASN1Boolean.TRUE, decoder.decode(ASN1Boolean.class, new byte[] { 0x01, 0x01, 0x01 }));
        assertEquals(ASN1Boolean.TRUE, decoder.decode(ASN1Boolean.class, new byte[] { 0x01, 0x01, (byte) 0xFF }));
    }

}
