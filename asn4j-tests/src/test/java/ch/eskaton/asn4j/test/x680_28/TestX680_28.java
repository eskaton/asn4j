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

package ch.eskaton.asn4j.test.x680_28;

import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1SequenceOf;
import ch.eskaton.asn4j.runtime.types.ASN1SetOf;
import ch.eskaton.asn4j.test.modules.x680_28.TestSetOf1;
import ch.eskaton.asn4j.test.modules.x680_28.TestSetOf2;
import ch.eskaton.asn4j.test.modules.x680_28.TestSetOf3;
import ch.eskaton.asn4j.test.modules.x680_28.TestSetOf4;
import ch.eskaton.asn4j.test.modules.x680_28.TestSetOf5;
import ch.eskaton.asn4j.test.modules.x680_28.TestSetOf6;
import ch.eskaton.asn4j.test.modules.x680_28.TestSetOf7;
import org.junit.Test;

import static ch.eskaton.asn4j.test.TestHelper.assertDecodable;

public class TestX680_28 {

    @Test
    public void testSetOf1() {
        assertDecodable(TestSetOf1.class,
                value -> value.setValues(ASN1Integer.valueOf(4711), ASN1Integer.valueOf(23)));
    }

    @Test
    public void testSetOf2() {
        assertDecodable(TestSetOf2.class,
                value -> value.setValues(new ASN1SetOf(ASN1Integer.valueOf(4711), ASN1Integer.valueOf(23))));
    }

    @Test
    public void testSetOf3() {
        assertDecodable(TestSetOf3.class,
                value -> value.setValues(new ASN1SetOf(
                        new ASN1SetOf(ASN1Integer.valueOf(4711), ASN1Integer.valueOf(23)),
                        new ASN1SetOf(ASN1Integer.valueOf(4478), ASN1Integer.valueOf(-13)))));
    }

    @Test
    public void testSetOf4() {
        assertDecodable(TestSetOf4.class,
                value -> value.setValues(new TestSetOf1(ASN1Integer.valueOf(4711), ASN1Integer.valueOf(23))));
    }

    @Test
    public void testSetOf5() {
        assertDecodable(TestSetOf5.class, value -> value.setValues(TestSetOf5.ContentType.A));
    }

    @Test
    public void testSetOf6() {
        assertDecodable(TestSetOf6.class, value -> value.setValues(new ASN1SetOf<>(TestSetOf6.ContentType.D)));
    }

    @Test
    public void testSetOf7() {
        assertDecodable(TestSetOf7.class, value -> value.setValues(new ASN1SequenceOf<>(TestSetOf7.ContentType.G)));
    }

}
