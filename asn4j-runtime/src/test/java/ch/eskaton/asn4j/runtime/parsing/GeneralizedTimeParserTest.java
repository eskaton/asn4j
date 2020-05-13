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

package ch.eskaton.asn4j.runtime.parsing;

import ch.eskaton.asn4j.runtime.exceptions.ASN1RuntimeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

public class GeneralizedTimeParserTest {

    @Test
    public void testParseLocalDateTime() throws ASN1RuntimeException {
        verifyLocalDateTime(new GeneralizedTimeParser().parse("20191231"), 2019, 12, 31, 0, 0, 0, 0);
        verifyLocalDateTime(new GeneralizedTimeParser().parse("2019123105"), 2019, 12, 31, 5, 0, 0, 0);
        verifyLocalDateTime(new GeneralizedTimeParser().parse("2019123105.54321"), 2019, 12, 31, 5, 32, 35, 556000000);
        verifyLocalDateTime(new GeneralizedTimeParser().parse("201912310547"), 2019, 12, 31, 5, 47, 0, 0);
        verifyLocalDateTime(new GeneralizedTimeParser().parse("201912310547.54321"), 2019, 12, 31, 5, 47, 32, 592600000);
        verifyLocalDateTime(new GeneralizedTimeParser().parse("20191231054731"), 2019, 12, 31, 5, 47, 31, 0);
        verifyLocalDateTime(new GeneralizedTimeParser().parse("20191231054731.54321"), 2019, 12, 31, 5, 47, 31, 543210000);
    }

    @Test
    public void testParseOffsetDateTime() throws ASN1RuntimeException {
        verifyOffsetDateTime(new GeneralizedTimeParser().parse("20191231Z"), 2019, 12, 31, 0, 0, 0, 0, null, true);
        verifyOffsetDateTime(new GeneralizedTimeParser().parse("20191231+0000"), 2019, 12, 31, 0, 0, 0, 0, "+0000", null);
        verifyOffsetDateTime(new GeneralizedTimeParser().parse("20191231+1230"), 2019, 12, 31, 0, 0, 0, 0, "+1230", null);
        verifyOffsetDateTime(new GeneralizedTimeParser().parse("2019123105+1100"), 2019, 12, 31, 05, 0, 0, 0, "+1100", null);
        verifyOffsetDateTime(new GeneralizedTimeParser().parse("201912310547+0030"), 2019, 12, 31, 05, 47, 0, 0, "+0030", null);
        verifyOffsetDateTime(new GeneralizedTimeParser().parse("20191231054731+0815"), 2019, 12, 31, 05, 47, 31, 0, "+0815", null);
        verifyOffsetDateTime(new GeneralizedTimeParser().parse("2019123123.123456789Z"), 2019, 12, 31, 23, 7, 24, 444440400, null, true);
        verifyOffsetDateTime(new GeneralizedTimeParser().parse("2019123123.123456789+12"), 2019, 12, 31, 23, 7, 24, 444440400, "+12", null);
        verifyOffsetDateTime(new GeneralizedTimeParser().parse("2019123123.123456789-1230"), 2019, 12, 31, 23, 7, 24, 444440400, "-1230", null);
        verifyOffsetDateTime(new GeneralizedTimeParser().parse("20191231231507.123-1800"), 2019, 12, 31, 23, 15, 7, 123000000, "-1800", null);
        verifyOffsetDateTime(new GeneralizedTimeParser().parse("20191231231507.123+1800"), 2019, 12, 31, 23, 15, 7, 123000000, "+1800", null);
    }

    @Test
    public void testParseInvalidDateTime() {
        assertInvalid("2019");
        assertInvalid("201905");
        assertInvalid("2019053");
        assertInvalid("201905312");
        assertInvalid("20190531235");
        assertInvalid("2019053123595");
        assertInvalid("20190531.123");
        assertInvalid("20190531+1");
        assertInvalid("20190531+123");
        assertInvalid("20190531+1801");
        assertInvalid("20190531-1801");
    }

    private void assertInvalid(String dateTime) {
        try {
            new GeneralizedTimeParser().parse(dateTime);
            fail("Exception expected");
        } catch (ASN1RuntimeException e) {
        }
    }

    private void verifyLocalDateTime(DateTime t, int year, int month, int day, int hour, int minute, int second, int nanos) {
        assertEquals(year, t.getYear());
        assertEquals(month, t.getMonth());
        assertEquals(day, t.getDay());
        assertEquals(hour, t.getHour());
        assertEquals(minute, t.getMinute());
        assertEquals(second, t.getSecond());
        assertEquals(nanos, t.getNanos());
    }

    private void verifyOffsetDateTime(DateTime t, int year, int month, int day, int hour, int minute, int second, int nanos, String offset, Boolean isZulu) {
        assertEquals(year, t.getYear());
        assertEquals(month, t.getMonth());
        assertEquals(day, t.getDay());
        assertEquals(hour, t.getHour());
        assertEquals(minute, t.getMinute());
        assertEquals(second, t.getSecond());
        assertEquals(nanos, t.getNanos());

        if (isZulu != null) {
            assertEquals(isZulu, t.isZulu());
            assertNull(t.getOffset());
        } else {
            assertEquals(offset, t.getOffset());
        }
    }

}
