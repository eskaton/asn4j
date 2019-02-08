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

package ch.eskaton.asn4j.parser;

import ch.eskaton.asn4j.parser.accessor.ObjectAccessor;

public abstract class ObjectRuleParser<T> implements RuleParser<T> {

    protected ObjectAccessor A = null;

    protected <U> void parse(RuleParser<U> parser) throws ParserException {
        A = new ObjectAccessor(parser.parse());
    }

    protected <U> T parse(RuleParser<U> parser, ParserFunction<ObjectAccessor, T> consumer) throws ParserException {
        A = new ObjectAccessor(parser.parse());

        if (!A.matched()) {
            return null;
        }

        return consumer.apply(A);
    }

    protected <U> boolean match(RuleParser<U> parser) throws ParserException {
        A = new ObjectAccessor(parser.parse());

        return A.matched();
    }

    protected boolean matched() {
        return A.matched();
    }

    protected Object p() {
        return A.p();
    }

    protected <T> T n() {
        return (T) A.n();
    }

    protected Token t() {
        return A.t();
    }

    protected  Position P() {
        return A.P();
    }

}
