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

package ch.eskaton.asn4j.runtime.types;

import ch.eskaton.asn4j.runtime.Clazz;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.exceptions.ASN1RuntimeException;
import ch.eskaton.asn4j.runtime.parsing.DateTime;
import ch.eskaton.asn4j.runtime.parsing.UTCTimeParser;

import java.util.Objects;

@ASN1Tag(clazz = Clazz.UNIVERSAL, tag = 23, mode = ASN1Tag.Mode.IMPLICIT, constructed = false)
public class ASN1UTCTime extends ASN1VisibleString {

    private static UTCTimeParser utcTimeParser = new UTCTimeParser();

    private DateTime dateTime;

    public static ASN1UTCTime from(String dateTimeString) throws ASN1RuntimeException {
        ASN1UTCTime instance = new ASN1UTCTime();

        instance.setValue(dateTimeString);

        return instance;
    }

    @Override
    public void setValue(String value) throws ASN1RuntimeException {
        this.dateTime = utcTimeParser.parse(value);
    }

    @Override
    public String getValue() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%02d%02d%02d%02d%02d", dateTime.getYear(), dateTime.getMonth(), dateTime.getDay(),
                dateTime.getHour(), dateTime.getMinute()));

        if (dateTime.getSecond() != 0) {
            sb.append(String.format("%02d", dateTime.getSecond()));
        }

        if (dateTime.hasOffset()) {
            if (dateTime.isZulu()) {
                sb.append("Z");
            } else {
                sb.append(dateTime.getOffset());
            }
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        ASN1UTCTime that = (ASN1UTCTime) o;

        return Objects.equals(dateTime, that.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dateTime);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[dateTime=" + dateTime + ']';
    }

}
