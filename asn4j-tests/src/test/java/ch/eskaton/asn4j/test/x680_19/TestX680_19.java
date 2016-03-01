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

package ch.eskaton.asn4j.test.x680_19;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.Test;

import ch.eskaton.asn4j.runtime.ASN1RuntimeException;
import ch.eskaton.asn4j.runtime.BERDecoder;
import ch.eskaton.asn4j.runtime.BEREncoder;
import ch.eskaton.asn4jtest.x680_19.TestInteger;
import ch.eskaton.asn4jtest.x680_19.TestNamedInteger;
import ch.eskaton.asn4jtest.x680_19.TestNamedInteger3;
import ch.eskaton.asn4jtest.x680_19.TestNamedInteger5;

public class TestX680_19 {

	@Test
	public void testEncoding() throws ASN1RuntimeException, IOException {
		BEREncoder encoder = new BEREncoder();
		TestInteger a = new TestInteger();

		a.setValue(BigInteger.valueOf(127));

		assertArrayEquals(new byte[] { 0x02, 0x01, 0x7f }, encoder.encode(a));

		a.setValue(BigInteger.valueOf(255));

		assertArrayEquals(new byte[] { 0x02, 0x02, 0x00, (byte) 0xff },
				encoder.encode(a));
	}

	@Test
	public void test1() throws ASN1RuntimeException, IOException {
		TestInteger a = new TestInteger();

		a.setValue(BigInteger.valueOf(17));

		BEREncoder encoder = new BEREncoder();
		BERDecoder decoder = new BERDecoder();

		TestInteger b = decoder.decode(TestInteger.class, encoder.encode(a));

		assertEquals(a, b);
	}

	@Test
	public void test2() throws ASN1RuntimeException, IOException {
		TestNamedInteger a = TestNamedInteger.VALUE3;

		BEREncoder encoder = new BEREncoder();
		BERDecoder decoder = new BERDecoder();

		TestNamedInteger b = decoder.decode(TestNamedInteger.class,
				encoder.encode(a));

		assertEquals(a, b);
	}

	@Test
	public void test3() throws ASN1RuntimeException, IOException {
		TestNamedInteger a = new TestNamedInteger();
		a.setValue(BigInteger.valueOf(13));

		BEREncoder encoder = new BEREncoder();
		BERDecoder decoder = new BERDecoder();

		TestNamedInteger b = decoder.decode(TestNamedInteger.class,
				encoder.encode(a));

		assertEquals(a, b);
	}

	@Test
	public void testEquality() throws ASN1RuntimeException, IOException {
		TestNamedInteger3 a = new TestNamedInteger3();
		TestNamedInteger3 b = new TestNamedInteger3();
		assertEquals(a, b);
	}

	@Test
	public void testInequality() throws ASN1RuntimeException, IOException {
		TestNamedInteger3 a = new TestNamedInteger3();
		TestNamedInteger5 b = new TestNamedInteger5();
		assertNotEquals(a, b);
	}

}
