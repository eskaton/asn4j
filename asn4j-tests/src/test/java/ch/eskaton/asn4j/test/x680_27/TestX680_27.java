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

import ch.eskaton.asn4j.runtime.types.ASN1Boolean;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1OctetString;
import ch.eskaton.asn4j.test.modules.x680_27.TestSet1;
import ch.eskaton.asn4j.test.modules.x680_27.TestSet2;
import ch.eskaton.asn4j.test.modules.x680_27.TestSet3;
import org.junit.jupiter.api.Test;

import static ch.eskaton.asn4j.test.TestHelper.assertDecodable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TestX680_27 {

    @Test
    void testSet1() {
        assertDecodable(TestSet1.class, value -> value.setA(ASN1Integer.valueOf(4711)));
    }

    @Test
    void testSet2() {
        assertDecodable(TestSet2.class, value -> {
            value.setA(ASN1Integer.valueOf(4711));
            value.setB(ASN1OctetString.valueOf(new byte[] { 0x74, 0x65, 0x73, 0x74 }));
            value.setC(ASN1Integer.valueOf(23));
        });
    }

    @Test
    void testSet3() {
        assertEquals(ASN1Integer.valueOf(1L), new TestSet3().getA().getB());
        assertEquals(ASN1Boolean.TRUE, new TestSet3().getA().getC());
        assertEquals(ASN1Integer.valueOf(1L), new TestSet3().getB().getA());
        assertEquals(ASN1OctetString.valueOf(new byte[] { (byte) 0xab, (byte) 0xc0 }), new TestSet3().getB().getB());
        assertNull(new TestSet3().getB().getC());
    }

}
