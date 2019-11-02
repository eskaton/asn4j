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

import ch.eskaton.asn4j.test.modules.x680_19.TestInteger;
import ch.eskaton.asn4j.test.modules.x680_19.TestNamedInteger;
import ch.eskaton.asn4j.test.modules.x680_19.TestNamedInteger3;
import ch.eskaton.asn4j.test.modules.x680_19.TestNamedInteger5;
import org.junit.Test;

import java.math.BigInteger;

import static ch.eskaton.asn4j.test.TestHelper.assertDecodable;
import static ch.eskaton.asn4j.test.TestHelper.assertValueDecodable;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestX680_19 {

    @Test
    public void test1() {
        assertDecodable(TestInteger.class, value -> value.setValue(BigInteger.valueOf(17)));
    }

    @Test
    public void test2() {
        assertValueDecodable(TestNamedInteger.class, TestNamedInteger.VALUE3);
    }

    @Test
    public void test3() {
        assertDecodable(TestNamedInteger.class, value -> value.setValue(BigInteger.valueOf(13)));
    }

    @Test
    public void testEquality() {
        assertEquals(new TestNamedInteger3(), new TestNamedInteger3());
    }

    @Test
    public void testInequality() {
        assertNotEquals(new TestNamedInteger3(), new TestNamedInteger5());
    }

}
