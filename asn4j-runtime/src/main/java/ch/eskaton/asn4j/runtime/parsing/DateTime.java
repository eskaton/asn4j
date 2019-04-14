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

import java.util.Objects;

public class DateTime {

    private int year;

    private int month;

    private int day;

    private int hour;

    private int minute;

    private int second;

    private int nanos;

    private String offset;

    private boolean isZulu;

    public DateTime(int year, int month, int day, int hour, int minute, int second) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public DateTime(int year, int month, int day, int hour, int minute, int second, String offset) {
        this(year, month, day, hour, minute, second);

        this.offset = offset;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public int getNanos() {
        return nanos;
    }

    public void setNanos(int nanos) {
        this.nanos = nanos;
    }

    public DateTime nanos(int nanos) {
        setNanos(nanos);

        return this;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getOffset() {
        return offset;
    }

    public DateTime offset(String offset) {
        setOffset(offset);

        return this;
    }

    public boolean isZulu() {
        return isZulu;
    }

    public void setZulu(boolean zulu) {
        isZulu = zulu;
    }

    public DateTime zulu(boolean isZulu) {
        setZulu(isZulu);

        return this;
    }

    public boolean hasOffset() {
        return isZulu || offset != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DateTime dateTime = (DateTime) o;

        return year == dateTime.year &&
                month == dateTime.month &&
                day == dateTime.day &&
                hour == dateTime.hour &&
                minute == dateTime.minute &&
                second == dateTime.second &&
                nanos == dateTime.nanos &&
                Objects.equals(offset, dateTime.offset) &&
                Objects.equals(isZulu, dateTime.isZulu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month, day, hour, minute, second, nanos, offset, isZulu);
    }

}
