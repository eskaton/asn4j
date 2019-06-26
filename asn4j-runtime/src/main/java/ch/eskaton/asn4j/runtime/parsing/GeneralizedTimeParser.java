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

public class GeneralizedTimeParser extends AbstractDateTimeParser {

    public static final BigDecimal SIXTY = BigDecimal.valueOf(60);

    public DateTime parse(String s) {
        Context ctx = new Context(s);

        while (true) {
            try {
                switch (ctx.getState()) {
                    case INITIAL:
                        ctx.year = parseYear(ctx);
                        break;
                    case YEAR:
                        ctx.month = parseMonth(ctx);
                        break;
                    case MONTH:
                        ctx.day = parseDay(ctx);
                        break;
                    case DAY:
                        ctx.hour = parseHour(ctx);

                        if (ctx.hour == null) {
                            ctx.hour = ZERO;
                            ctx.setState(State.TIME_ZONE);
                        }

                        break;
                    case HOUR:
                        ctx.minute = parseMinute(ctx);

                        if (ctx.minute == null) {
                            ctx.minute = ZERO;
                            ctx.setState(State.HOUR_FRACTION);
                        }
                        break;
                    case MINUTE:
                        ctx.second = parseSecond(ctx);

                        if (ctx.second == null) {
                            ctx.second = ZERO;
                            ctx.setState(State.MINUTE_FRACTION);
                        }
                        break;
                    case HOUR_FRACTION:
                    case MINUTE_FRACTION:
                    case SECOND_FRACTION:
                        State currentState = ctx.state;
                        String fraction = parseFraction(ctx);

                        if (fraction == null) {
                            break;
                        }

                        processFraction(ctx, currentState, fraction);
                        break;
                    case TIME_ZONE:
                        ctx.timeZone = parseTimeZone(ctx);
                        break;
                    case ACCEPT:
                        if (ctx.available()) {
                            throw new ASN1RuntimeException("Failed to parse: " + s);
                        }

                        DateTime dateTime = new DateTime(ctx.year, ctx.month, ctx.day, ctx.hour, ctx.minute, ctx.second)
                                .nanos(ctx.nanos);

                        if (ctx.timeZone != null) {
                            if ("Z".equals(ctx.timeZone)) {
                                return dateTime.zulu(true);
                            } else {
                                return dateTime.offset(ctx.timeZone);
                            }
                        }

                        return dateTime;
                    case ERROR:
                        throw new ASN1RuntimeException("Failed to parse: " + s);
                    default:
                        throw new ASN1RuntimeException("Unimplemented state: " + ctx.getState());
                }
            } catch (IOException e) {
                throw new ASN1RuntimeException(e);
            }
        }
    }

    protected Integer parseSecond(Context ctx) throws IOException {
        return parseComponent(ctx, State.SECOND_FRACTION, 2, second ->
                new LessEqualVerifiyer("second", 59).verify(second));
    }

    private void processFraction(Context ctx, State state, String fractionStr) {
        BigDecimal fraction = new BigDecimal("0." + fractionStr);
        BigDecimal componentWithFraction;

        switch (state) {
            case HOUR_FRACTION:
                componentWithFraction = fraction.multiply(SIXTY);
                fraction = componentWithFraction.remainder(BigDecimal.ONE);
                ctx.minute = componentWithFraction.subtract(fraction).setScale(0).intValue();
                // fall through
            case MINUTE_FRACTION:
                componentWithFraction = fraction.multiply(SIXTY);
                fraction = componentWithFraction.remainder(BigDecimal.ONE);
                ctx.second = componentWithFraction.subtract(fraction).setScale(0).intValue();
                // fall through
            case SECOND_FRACTION:
                ctx.nanos = fraction.multiply(BigDecimal.valueOf(1_000_000_000)).setScale(0).intValue();
                break;
            default:
                throw new IllegalStateException(ctx.getState().toString());
        }

    }

    private String parseFraction(Context ctx) throws IOException {
        ctx.setState(State.TIME_ZONE);

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

        ctx.setState(State.ACCEPT);

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

        int intMinute = Integer.parseInt(minute);

        if (Integer.parseInt(hour) * 100 + intMinute > 1800 || intMinute > 59) {
            throw new ASN1RuntimeException("Invalid offset");
        }

        sb.append(minute);

        return sb.toString();
    }

}
