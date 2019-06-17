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

package ch.eskaton.asn4j.test.x680_22;

import ch.eskaton.asn4j.runtime.BERDecoder;
import ch.eskaton.asn4j.runtime.BEREncoder;
import ch.eskaton.asn4j.runtime.exceptions.ASN1RuntimeException;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;
import ch.eskaton.asn4j.runtime.exceptions.DecodingException;
import ch.eskaton.asn4j.runtime.exceptions.EncodingException;
import ch.eskaton.asn4j.test.modules.x680_22.TestBitString1;
import ch.eskaton.asn4j.test.modules.x680_22.TestBitString2;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestX680_22 {

    @Test
    public void testEncoding() throws ASN1RuntimeException, IOException {
    	BEREncoder encoder = new BEREncoder();
    	TestBitString1 a = new TestBitString1();
    	a.setValue(new byte[] { 0x00 });

    	assertArrayEquals(new byte[] { 0x03, 0x02, 0x00, 0x00 }, encoder.encode(a));

    	a.setBit(0);

    	assertArrayEquals(new byte[] { 0x03, 0x02, 0x00, (byte) 0x80 },
    			encoder.encode(a));

    	a.setBit(7);

    	assertArrayEquals(new byte[] { 0x03, 0x02, 0x00, (byte) 0x81 },
    			encoder.encode(a));
    }

    @Test
    public void test1() throws ASN1RuntimeException, IOException {
    	testBitString(TestBitString1.TEST_A, 0);
    }

    @Test
    public void test2() throws ASN1RuntimeException, IOException {
    	testBitString(TestBitString1.TEST_B, 1);
    }

    @Test
    public void test3() throws ASN1RuntimeException, IOException {
    	testBitString(TestBitString1.TEST_C, 2);
    }

    @Test
    public void test4() throws ASN1RuntimeException {
    	TestBitString2 a = new TestBitString2();
    	a.setValue(new byte[] { 0x00 });

    	a.setBit(5);

    	BEREncoder encoder = new BEREncoder();
    	BERDecoder decoder = new BERDecoder();

    	TestBitString2 b = decoder.decode(TestBitString2.class,
    			encoder.encode(a));

    	assertEquals(a, b);
    	assertTrue(b.testBit(5));

    }

    private void testBitString(int namedValue, int value)
    		throws ASN1RuntimeException, DecodingException, EncodingException,
    		ConstraintViolatedException {
    	TestBitString1 a = new TestBitString1();
    	a.setValue(new byte[] { 0x00 });

    	a.setBit(namedValue);

    	BEREncoder encoder = new BEREncoder();
    	BERDecoder decoder = new BERDecoder();

    	TestBitString1 b = decoder.decode(TestBitString1.class,
    			encoder.encode(a));

    	assertEquals(a, b);
    	assertTrue(b.testBit(value));
    }

}
