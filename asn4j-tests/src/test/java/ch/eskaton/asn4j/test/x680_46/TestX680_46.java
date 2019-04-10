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

package ch.eskaton.asn4j.test.x680_46;

import ch.eskaton.asn4j.runtime.BERDecoder;
import ch.eskaton.asn4j.runtime.BEREncoder;
import ch.eskaton.asn4j.runtime.exceptions.ASN1RuntimeException;
import ch.eskaton.asn4jtest.x680_46.TestGeneralizedTime;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;

public class TestX680_46 {

    @Test
    public void testGeneralizedTimeLocal() throws ASN1RuntimeException {
        TestGeneralizedTime a = new TestGeneralizedTime();
        LocalDateTime now = LocalDateTime.now();

        a.setTime(now);

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        TestGeneralizedTime b = decoder.decode(TestGeneralizedTime.class, encoder.encode(a));

        assertEquals(a, b);
    }

    @Test
    public void testGeneralizedTimeOffset() throws ASN1RuntimeException {
        TestGeneralizedTime a = new TestGeneralizedTime();
        OffsetDateTime now = OffsetDateTime.now();

        a.setTime(now);

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        TestGeneralizedTime b = decoder.decode(TestGeneralizedTime.class, encoder.encode(a));

        assertEquals(a, b);
    }

    @Test
    public void testGeneralizedTimeFromString() throws ASN1RuntimeException {
        TestGeneralizedTime a = new TestGeneralizedTime();

        a.setValue("2016101923.1234567-0130");

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        TestGeneralizedTime b = decoder.decode(TestGeneralizedTime.class, encoder.encode(a));

        assertEquals(a, b);
    }

}
