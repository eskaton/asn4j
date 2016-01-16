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

package ch.eskaton.asn4j.test.x680_20;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ch.eskaton.asn4j.runtime.BERDecoder;
import ch.eskaton.asn4j.runtime.BEREncoder;
import ch.eskaton.asn4j.runtime.ConstraintViolatedException;
import ch.eskaton.asn4j.runtime.DecodingException;
import ch.eskaton.asn4j.runtime.EncodingException;
import ch.eskaton.asn4jtest.x680_20.TestEnumeration;

public class TestX680_20 {

	@Test
	public void test1() throws DecodingException, EncodingException,
			ConstraintViolatedException {
		testEnumeration(TestEnumeration.M, -3);
	}

	@Test
	public void test2() throws DecodingException, EncodingException,
			ConstraintViolatedException {
		testEnumeration(TestEnumeration.A, 1);
	}

	@Test
	public void test3() throws DecodingException, EncodingException,
			ConstraintViolatedException {
		testEnumeration(TestEnumeration.B, 2);
	}

	@Test
	public void test4() throws DecodingException, EncodingException,
			ConstraintViolatedException {
		testEnumeration(TestEnumeration.C, 0);
	}

	@Test
	public void test5() throws DecodingException, EncodingException,
			ConstraintViolatedException {
		testEnumeration(TestEnumeration.D, 3);
	}

	@Test
	public void test6() throws DecodingException, EncodingException,
			ConstraintViolatedException {
		testEnumeration(TestEnumeration.E, 5);
	}

	@Test
	public void test7() throws DecodingException, EncodingException,
			ConstraintViolatedException {
		testEnumeration(TestEnumeration.F, 6);
	}

	@Test
	public void test8() throws DecodingException, EncodingException,
			ConstraintViolatedException {
		testEnumeration(TestEnumeration.G, 9);
	}

	@Test
	public void test9() throws DecodingException, EncodingException,
			ConstraintViolatedException {
		testEnumeration(TestEnumeration.H, 10);
	}

	private void testEnumeration(TestEnumeration a, int value)
			throws DecodingException, EncodingException,
			ConstraintViolatedException {
		BEREncoder encoder = new BEREncoder();
		BERDecoder decoder = new BERDecoder();

		TestEnumeration b = decoder.decode(TestEnumeration.class,
				encoder.encode(a));

		assertEquals(a, b);
		assertEquals(value, b.getValue());
	}

}
