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

public class UTCTimeParser extends AbstractDateTimeParser {

    @Override
    public DateTime parse(String value) {
        Context ctx = new Context(value);

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
                        break;
                    case HOUR:
                        ctx.minute = parseMinute(ctx);
                        break;
                    case MINUTE:
                        ctx.second = parseSecond(ctx);

                        if (ctx.second == null) {
                            ctx.second = ZERO;
                            ctx.setState(State.TIME_ZONE);
                        }
                        break;
                    case TIME_ZONE:
                        ctx.timeZone = parseTimeZone(ctx);
                        break;
                    case ACCEPT:
                        if (ctx.available()) {
                            throw new ASN1RuntimeException("Failed to parse: %s", value);
                        }

                        DateTime dateTime = new DateTime(ctx.year, ctx.month, ctx.day, ctx.hour, ctx.minute, ctx.second);

                        if (ctx.timeZone != null) {
                            if ("Z".equals(ctx.timeZone)) {
                                return dateTime.zulu(true);
                            } else {
                                return dateTime.offset(ctx.timeZone);
                            }
                        }

                        return dateTime;
                    case ERROR:
                        throw new ASN1RuntimeException("Failed to parse: %s", value);
                    default:
                        throw new ASN1RuntimeException("Unimplemented state: %s", ctx.getState());
                }
            } catch (IOException e) {
                throw new ASN1RuntimeException(e);
            }
        }
    }

    @Override
    protected Integer parseYear(Context ctx) throws IOException {
        return parseComponent(ctx, State.YEAR, 2, null);
    }

    protected Integer parseSecond(Context ctx) throws IOException {
        return parseComponent(ctx, State.TIME_ZONE, 2, (c, second) ->
                new LessEqualVerifiyer("second", 59).verify(c, second));
    }

}
