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

package ch.eskaton.asn4j.test.x680_25;

import ch.eskaton.asn4j.runtime.BERDecoder;
import ch.eskaton.asn4j.runtime.BEREncoder;
import ch.eskaton.asn4j.runtime.types.ASN1Boolean;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1OctetString;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence0;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence1;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence2;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence3;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence4;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence6;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestX680_25 {

    @Test
    public void testSequence0() {
        TestSequence0 a = new TestSequence0();
        a.setA(ASN1Boolean.TRUE);

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        TestSequence0 b = decoder.decode(TestSequence0.class, encoder.encode(a));

        assertEquals(a, b);
    }

    @Test
    public void testSequence1() {
        TestSequence1 a = new TestSequence1();
        a.setA(ASN1Integer.valueOf(4711));
        a.setB(ASN1Boolean.TRUE);
        a.setC(ASN1OctetString.valueOf("test".getBytes()));

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        TestSequence1 b = decoder.decode(TestSequence1.class, encoder.encode(a));

        assertEquals(a, b);
    }

    @Test
    public void testOptional() {
        TestSequence2 a = new TestSequence2();
        a.setA(ASN1Integer.valueOf(4711));
        a.setC(ASN1OctetString.valueOf("test".getBytes()));

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        TestSequence2 b = decoder.decode(TestSequence2.class, encoder.encode(a));

        assertEquals(a, b);
    }

    @Test
    public void testDefaultAtEnd() {
        TestSequence3 a = new TestSequence3();
        a.setA(ASN1Integer.valueOf(4711));
        a.setB(ASN1Boolean.TRUE);

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        TestSequence3 b = decoder.decode(TestSequence3.class, encoder.encode(a));

        assertEquals(a, b);
        assertEquals(ASN1OctetString.valueOf("test".getBytes()), b.getC());
    }

    @Test
    public void testDefaultAtStart() {
        TestSequence4 a = new TestSequence4();
        a.setB(ASN1Boolean.TRUE);
        a.setC(ASN1OctetString.valueOf("test".getBytes()));

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        TestSequence4 b = decoder.decode(TestSequence4.class, encoder.encode(a));

        assertEquals(a, b);
        assertEquals(ASN1Integer.valueOf(4711), b.getA());
    }

    @Test
    public void testComponentsOf() {
        TestSequence6 a = new TestSequence6();
        a.setA(ASN1Integer.valueOf(4711));
        a.setB(ASN1Boolean.TRUE);

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        TestSequence6 b = decoder.decode(TestSequence6.class, encoder.encode(a));

        assertEquals(a, b);
    }

    @Test
    public void testComponentsOfDefault() {
        TestSequence6 a = new TestSequence6();
        a.setB(ASN1Boolean.TRUE);

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        TestSequence6 b = decoder.decode(TestSequence6.class, encoder.encode(a));

        assertEquals(a, b);
        assertEquals(ASN1Integer.valueOf(23), a.getA());
    }

}
