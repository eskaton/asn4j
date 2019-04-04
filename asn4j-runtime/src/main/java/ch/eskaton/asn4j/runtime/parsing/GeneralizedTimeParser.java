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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;


public class GeneralizedTimeParser {

    public static final Integer ZERO = Integer.valueOf(0);

    public static final BigDecimal SIXTY = BigDecimal.valueOf(60);

    private enum State {
        Initial, Year, Month, Day, Hour, Minute, Second, HourFraction, MinuteFraction, SecondFraction, TimeZone, Accept, Error
    }

    public GeneralizedTimeParser() {
    }

    public Temporal parse(String s) throws ASN1RuntimeException {
        Context ctx = new Context(s);

        while (true) {
            try {
                switch (ctx.getState()) {
                    case Initial:
                        ctx.year = parseYear(ctx);
                        break;
                    case Year:
                        ctx.month = parseMonth(ctx);
                        break;
                    case Month:
                        ctx.day = parseDay(ctx);
                        break;
                    case Day:
                        ctx.hour = parseHour(ctx);

                        if (ctx.hour == null) {
                            ctx.hour = ZERO;
                            ctx.setState(State.TimeZone);
                        }

                        break;
                    case Hour:
                        ctx.minute = parseMinute(ctx);

                        if (ctx.minute == null) {
                            ctx.minute = ZERO;
                            ctx.setState(State.HourFraction);
                        }
                        break;
                    case Minute:
                        ctx.second = parseSecond(ctx);

                        if (ctx.second == null) {
                            ctx.second = ZERO;
                            ctx.setState(State.MinuteFraction);
                        }
                        break;
                    case HourFraction:
                    case MinuteFraction:
                    case SecondFraction:
                        State currentState = ctx.state;
                        String fraction = parseFraction(ctx);

                        if (fraction == null) {
                            break;
                        }

                        processFraction(ctx, currentState, fraction);
                        break;
                    case TimeZone:
                        ctx.timeZone = parseTimeZone(ctx);
                        break;
                    case Accept:
                        if (ctx.available()) {
                            throw new ASN1RuntimeException("Failed to parse: " + s);
                        }

                        LocalDateTime dateTime = LocalDateTime.of(ctx.year, ctx.month, ctx.day, ctx.hour, ctx.minute, ctx.second, ctx.nanos);

                        if (ctx.timeZone != null) {
                            return ZonedDateTime.of(dateTime, ZoneId.of(ctx.timeZone));
                        }

                        return dateTime;
                    case Error:
                        throw new ASN1RuntimeException("Failed to parse: " + s);
                }
            } catch (IOException e) {
                throw new ASN1RuntimeException(e);
            }
        }
    }

    private void processFraction(Context ctx, State state, String fractionStr) {
        BigDecimal fraction = new BigDecimal("0." + fractionStr);
        BigDecimal componentWithFraction;

        switch (state) {
            case HourFraction:
                componentWithFraction = fraction.multiply(SIXTY);
                fraction = componentWithFraction.remainder(BigDecimal.ONE);
                ctx.minute = componentWithFraction.subtract(fraction).setScale(0).intValue();
            case MinuteFraction:
                componentWithFraction = fraction.multiply(SIXTY);
                fraction = componentWithFraction.remainder(BigDecimal.ONE);
                ctx.second = componentWithFraction.subtract(fraction).setScale(0).intValue();
            case SecondFraction:
                ctx.nanos = fraction.multiply(BigDecimal.valueOf(1_000_000_000)).setScale(0).intValue();
                break;
            default:
                throw new IllegalStateException(ctx.getState().toString());
        }

    }

    private Integer parseYear(Context ctx) throws IOException, ASN1RuntimeException {
        return parseComponent(ctx, State.Year, 4, null);
    }

    private Integer parseMonth(Context ctx) throws IOException, ASN1RuntimeException {
        return parseComponent(ctx, State.Month, 2, month ->
                new LessEqualVerifiyer("month", 12).verify(month));
    }

    private Integer parseDay(Context ctx) throws IOException, ASN1RuntimeException {
        return parseComponent(ctx, State.Day, 2, day ->
                new LessEqualVerifiyer("day", 31).verify(day));
    }

    private Integer parseHour(Context ctx) throws IOException, ASN1RuntimeException {
        return parseComponent(ctx, State.Hour, 2, hour ->
                new LessEqualVerifiyer("hour", 23).verify(hour));
    }

    private Integer parseMinute(Context ctx) throws IOException, ASN1RuntimeException {
        return parseComponent(ctx, State.Minute, 2, minute ->
                new LessEqualVerifiyer("minute", 59).verify(minute));
    }

    private Integer parseSecond(Context ctx) throws IOException, ASN1RuntimeException {
        return parseComponent(ctx, State.SecondFraction, 2, second ->
                new LessEqualVerifiyer("second", 59).verify(second));
    }

    private String parseFraction(Context ctx) throws IOException {
        ctx.setState(State.TimeZone);

        int comma = ctx.read();

        if (comma == '.' || comma == ',') {
            StringBuilder sb = new StringBuilder();

            int digit;

            while ((digit = parseDigit(ctx)) != -1) {
                sb.append((char) digit);
            }

            return sb.toString();
        }

        ctx.unread();

        return null;
    }

    private String parseTimeZone(Context ctx) throws IOException {
        StringBuilder sb = new StringBuilder();
        int chr = ctx.read();

        ctx.setState(State.Accept);

        if (chr == 'Z') {
            sb.append((char) chr);

            return sb.toString();
        }

        if (chr != '+' && chr != '-') {
            ctx.unread();

            return null;
        }

        sb.append((char) chr);

        ctx.mark();

        String hour = parseDigits(ctx, 2);

        if (hour == null) {
            ctx.reset();

            return null;
        }

        sb.append(hour);

        ctx.mark();

        String minute = parseDigits(ctx, 2);

        if (minute == null) {
            ctx.reset();

            return sb.toString();
        }

        sb.append(minute);

        return sb.toString();
    }

    private Integer parseComponent(Context ctx, State targetState, int length, Verifier<Integer> verifier) throws IOException, ASN1RuntimeException {
        String component = parseDigits(ctx, length);

        if (component != null) {
            if (verifier != null) {
                verifier.verify(Integer.valueOf(component));
            }

            ctx.setState(targetState);

            return Integer.valueOf(component);
        }

        ctx.setState(State.Error);

        return null;
    }

    private String parseDigits(Context ctx, int count) throws IOException {
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

    private int parseDigit(Context ctx) throws IOException {
        int digit = ctx.read();

        if (Character.isDigit(digit)) {
            return digit;
        }

        ctx.unread();

        return -1;
    }

    private static class Context {

        private LexerInputStream is;

        private State state = State.Initial;

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

    private interface Verifier<T> {

        void verify(T value) throws ASN1RuntimeException;

    }

    private class LessEqualVerifiyer implements Verifier<Integer> {

        private String valueName;

        private int threshold;

        public LessEqualVerifiyer(String valueName, int threshold) {
            this.valueName = valueName;
            this.threshold = threshold;
        }

        @Override
        public void verify(Integer value) throws ASN1RuntimeException {
            if (value > threshold) {
                throw new ASN1RuntimeException("Invalid " + valueName + ": " + value);
            }
        }
    }

}
