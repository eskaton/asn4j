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

package ch.eskaton.asn4j.runtime.types;

import ch.eskaton.asn4j.runtime.exceptions.ASN1RuntimeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ASN1BitStringTest {

    @Test
    public void testSetValue() {
    	ASN1BitString bs = new ASN1BitString();

    	bs.setValue(new byte[] { 0x01 });

    	assertEquals(0, bs.getUnusedBits());
    	assertArrayEquals(new byte[] { 0x01 }, bs.getValue());

    	bs.setValue(new byte[] { 0x04 }, 2);

    	assertEquals(2, bs.getUnusedBits());
        assertArrayEquals(new byte[] { 0x04 }, bs.getValue());

    	bs.setValue(new byte[] { (byte) 0x80 }, 7);

    	assertEquals(7, bs.getUnusedBits());
        assertArrayEquals(new byte[] { (byte) 0x80 }, bs.getValue());

    	bs.setValue(new byte[] {});

    	assertEquals(0, bs.getUnusedBits());
        assertArrayEquals(new byte[] {}, bs.getValue());
    }

    @Test
    public void testSetBit0() throws ASN1RuntimeException {
    	ASN1BitString bs = ASN1BitString.of(new byte[] { 0x00 });
    	bs.setBit(0);
    	assertArrayEquals(new byte[] { (byte) 0x80 }, bs.getValue());
    }

    @Test
    public void testSetBit7() throws ASN1RuntimeException {
    	ASN1BitString bs = ASN1BitString.of(new byte[] { 0x00 });
    	bs.setBit(7);
    	assertArrayEquals(new byte[] { (byte) 0x01 }, bs.getValue());
    }

    @Test
    public void testSetBit15() throws ASN1RuntimeException {
    	ASN1BitString bs = ASN1BitString.of(new byte[] { 0x00, 0x00 });
    	bs.setBit(15);
    	assertArrayEquals(new byte[] { (byte) 0x00, (byte) 0x01 },
    			bs.getValue());
    }

    @Test
    public void testTestBit15() throws ASN1RuntimeException {
    	ASN1BitString bs = ASN1BitString.of(new byte[] { 0x00, 0x00 });
    	bs.setBit(15);
    	assertTrue(bs.testBit(15));
    }

    @Test
    public void testClearBit() throws ASN1RuntimeException {
    	ASN1BitString bs = ASN1BitString.of(new byte[] { 0x00, 0x00 });
    	bs.setBit(1);

        assertArrayEquals(new byte[] { (byte)0x40, 0x00 }, bs.getValue());

    	bs.setBit(14);

        assertArrayEquals(new byte[] { (byte)0x40, 0x02 }, bs.getValue());

    	bs.clearBit(14);

    	assertArrayEquals(new byte[] { (byte)0x40, 0x00 }, bs.getValue());

    	bs.clearBit(1);

    	assertArrayEquals(new byte[] { (byte)0x00, 0x00 }, bs.getValue());
    }

}
