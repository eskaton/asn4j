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

import ch.eskaton.asn4j.runtime.BERDecoder;
import ch.eskaton.asn4j.runtime.BEREncoder;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1SetOf;
import ch.eskaton.asn4j.test.modules.x680_28.TestSetOf1;
import ch.eskaton.asn4j.test.modules.x680_28.TestSetOf2;
import ch.eskaton.asn4j.test.modules.x680_28.TestSetOf3;
import ch.eskaton.asn4j.test.modules.x680_28.TestSetOf4;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestX680_28 {

    @Test
    public void testSetOf1() {
        TestSetOf1 a = new TestSetOf1();
        a.setValues(ASN1Integer.valueOf(4711), ASN1Integer.valueOf(23));

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        TestSetOf1 b = decoder.decode(TestSetOf1.class, encoder.encode(a));

        assertEquals(a, b);
    }

    @Test
    public void testSetOf2() {
        TestSetOf2 a = new TestSetOf2();

        a.setValues(new ASN1SetOf(ASN1Integer.valueOf(4711), ASN1Integer.valueOf(23)));

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        TestSetOf2 b = decoder.decode(TestSetOf2.class, encoder.encode(a));

        assertEquals(a, b);
    }

    @Test
    public void testSetOf3() {
        TestSetOf3 a = new TestSetOf3();

        a.setValues(new ASN1SetOf(new ASN1SetOf(ASN1Integer.valueOf(4711), ASN1Integer.valueOf(23)),
                new ASN1SetOf(ASN1Integer.valueOf(4478), ASN1Integer.valueOf(-13))));

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        TestSetOf3 b = decoder.decode(TestSetOf3.class, encoder.encode(a));

        assertEquals(a, b);
    }

    @Test
    public void testSetOf4() {
        TestSetOf4 a = new TestSetOf4();

        a.setValues(new TestSetOf1(ASN1Integer.valueOf(4711), ASN1Integer.valueOf(23)));

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        TestSetOf4 b = decoder.decode(TestSetOf4.class, encoder.encode(a));

        assertEquals(a, b);
    }

}
