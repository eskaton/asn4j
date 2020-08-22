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
import ch.eskaton.asn4j.runtime.annotations.ASN1Tags;
import ch.eskaton.asn4j.runtime.exceptions.ASN1RuntimeException;
import ch.eskaton.asn4j.runtime.parsing.DateTime;
import ch.eskaton.asn4j.runtime.parsing.GeneralizedTimeParser;
import ch.eskaton.asn4j.runtime.utils.ToString;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Objects;

@ASN1Tags(tags = @ASN1Tag(clazz = Clazz.UNIVERSAL, tag = 24, mode = ASN1Tag.Mode.IMPLICIT))
public class ASN1GeneralizedTime extends ASN1VisibleString {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmss");

    private static final DateTimeFormatter NANOS_FORMATTER = DateTimeFormatter.ofPattern("SSSSSSSSS");

    private static final DateTimeFormatter OFFSET_FORMATTER = DateTimeFormatter.ofPattern("X");

    private static final GeneralizedTimeParser GENERALIZED_TIME_PARSER = new GeneralizedTimeParser();

    private Temporal dateTime;

    public ASN1GeneralizedTime() {
    }

    public ASN1GeneralizedTime(String value) {
        setValue(value);
    }

    public ASN1GeneralizedTime(LocalDateTime dateTime) {
        this.setTime(dateTime);
    }

    public ASN1GeneralizedTime(OffsetDateTime dateTime) {
        this.setTime(dateTime);
    }

    @Override
    public void setValue(String value) {
        DateTime dateTime = GENERALIZED_TIME_PARSER.parse(value);
        LocalDateTime localDateTime = LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay(),
                dateTime.getHour(), dateTime.getMinute(), dateTime.getSecond(), dateTime.getNanos());

        if (dateTime.hasOffset()) {
            if (dateTime.isZulu()) {
                setTime(OffsetDateTime.of(localDateTime, ZoneOffset.UTC));
            } else {
                setTime(OffsetDateTime.of(localDateTime, ZoneOffset.of(dateTime.getOffset())));
            }
        } else {
            setTime(localDateTime);
        }
    }

    @Override
    public String getValue() {
        String date = DATE_FORMATTER.format(dateTime);
        String time = TIME_FORMATTER.format(dateTime);
        String nanos = NANOS_FORMATTER.format(dateTime).replaceFirst("0+$", "");

        if (nanos.isEmpty()) {
            int timeLen;

            do {
                timeLen = time.length();
                time = time.replaceFirst("00$", "");
            } while (timeLen != time.length());
        } else {
            nanos = "." + nanos;
        }

        String offset = "";

        if (dateTime instanceof OffsetDateTime) {
            offset = OFFSET_FORMATTER.format(dateTime);
        }

        return date + time + nanos + offset;
    }

    public void setTime(Temporal dateTime) {
        if (!(dateTime instanceof LocalDateTime || dateTime instanceof OffsetDateTime)) {
            throw new ASN1RuntimeException("Invalid argument. Instance of " +
                    LocalDateTime.class.getSimpleName() + " or " +
                    OffsetDateTime.class.getSimpleName() + " expected.");
        }

        this.dateTime = dateTime;
    }

    public Temporal getTime() {
        return dateTime;
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

        ASN1GeneralizedTime that = (ASN1GeneralizedTime) o;

        return Objects.equals(dateTime, that.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dateTime);
    }

    @Override
    public String toString() {
        return ToString.getExcept(this, "value");
    }

}
