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

import java.io.IOException;

public class AbstractDateTimeParser {

    public static final Integer ZERO = Integer.valueOf(0);

    protected enum State {
        INITIAL, YEAR, MONTH, DAY, HOUR, MINUTE, SECOND, HOUR_FRACTION, MINUTE_FRACTION,
        SECOND_FRACTION, TIME_ZONE, ACCEPT, ERROR
    }

    protected Integer parseYear(Context ctx) throws IOException {
        return parseComponent(ctx, State.YEAR, 4, null);
    }

    protected Integer parseMonth(Context ctx) throws IOException {
        return parseComponent(ctx, State.MONTH, 2, month ->
                new LessEqualVerifiyer("month", 12).verify(month));
    }

    protected Integer parseDay(Context ctx) throws IOException {
        return parseComponent(ctx, State.DAY, 2, day ->
                new LessEqualVerifiyer("day", 31).verify(day));
    }

    protected Integer parseHour(Context ctx) throws IOException {
        return parseComponent(ctx, State.HOUR, 2, hour ->
                new LessEqualVerifiyer("hour", 23).verify(hour));
    }

    protected Integer parseMinute(Context ctx) throws IOException {
        return parseComponent(ctx, State.MINUTE, 2, minute ->
                new LessEqualVerifiyer("minute", 59).verify(minute));
    }

    protected Integer parseComponent(Context ctx, State targetState, int length, Verifier<Integer> verifier) throws IOException {
        String component = parseDigits(ctx, length);

        if (component != null) {
            if (verifier != null) {
                verifier.verify(Integer.valueOf(component));
            }

            ctx.setState(targetState);

            return Integer.valueOf(component);
        }

        ctx.setState(State.ERROR);

        return null;
    }

    protected String parseDigits(Context ctx, int count) throws IOException {
        StringBuilder sb = new StringBuilder();

        ctx.mark();

        for (int i = 0; i < count; i++) {
            int digit = parseDigit(ctx);

            if (digit == -1) {
                ctx.reset();
                return null;
            }

            sb.append((char) digit);
        }

        return sb.toString();
    }

    protected int parseDigit(Context ctx) throws IOException {
        int digit = ctx.read();

        if (Character.isDigit(digit)) {
            return digit;
        }

        ctx.unread();

        return -1;
    }

    protected static class Context {

        private LexerInputStream is;

        protected State state = State.INITIAL;

        Integer year = null;

        Integer month = null;

        Integer day = null;

        Integer hour = ZERO;

        Integer minute = ZERO;

        Integer second = ZERO;

        Integer nanos = ZERO;

        String timeZone;

        public Context(String s) {
            is = new LexerInputStream(s.toCharArray());
        }

        public State getState() {
            return state;
        }

        public void setState(State state) {
            this.state = state;
        }

        public int read() throws IOException {
            return is.read();
        }

        public void unread() throws IOException {
            is.unread();
        }

        public void mark() {
            is.mark();
        }

        public void reset() {
            is.reset();
        }

        public boolean available() throws IOException {
            return is.ready();
        }

    }

    protected interface Verifier<T> {

        void verify(T value);

    }

    protected class LessEqualVerifiyer implements Verifier<Integer> {

        private String valueName;

        private int threshold;

        public LessEqualVerifiyer(String valueName, int threshold) {
            this.valueName = valueName;
            this.threshold = threshold;
        }

        @Override
        public void verify(Integer value) {
            if (value > threshold) {
                throw new ASN1RuntimeException("Invalid " + valueName + ": " + value);
            }
        }
    }

}
