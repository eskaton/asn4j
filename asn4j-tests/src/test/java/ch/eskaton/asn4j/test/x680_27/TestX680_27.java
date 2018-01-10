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

package ch.eskaton.asn4j.test.x680_27;

import static org.junit.Assert.assertEquals;

import ch.eskaton.asn4jtest.x680_27_implicit.TestSet2;
import org.junit.Test;

import ch.eskaton.asn4j.runtime.BERDecoder;
import ch.eskaton.asn4j.runtime.BEREncoder;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1OctetString;
import ch.eskaton.asn4jtest.x680_27_implicit.TestSet1;

public class TestX680_27 {

    @Test
    public void testSet1() {
        TestSet1 a = new TestSet1();
        a.setA(ASN1Integer.valueOf(4711));
        a.setB(ASN1OctetString.valueOf("test"));
        a.setC(ASN1Integer.valueOf(23));

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        TestSet1 b = decoder.decode(TestSet1.class, encoder.encode(a));

        assertEquals(a, b);
    }

    @Test
    public void testSet2() {
        TestSet2 a = new TestSet2();
        a.setA(ASN1Integer.valueOf(4711));

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        TestSet2 b = decoder.decode(TestSet2.class, encoder.encode(a));

        assertEquals(a, b);
    }

}
