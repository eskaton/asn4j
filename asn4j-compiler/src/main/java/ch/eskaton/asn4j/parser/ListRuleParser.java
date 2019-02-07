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

import ch.eskaton.asn4j.parser.accessor.ListAccessor;
import ch.eskaton.asn4j.parser.accessor.RepetitionListAccessor;
import ch.eskaton.asn4j.parser.accessor.SequenceListAccessor;

import java.util.List;

public abstract class ListRuleParser<T> extends RuleParser<T> {

    protected ListAccessor A = null;

    protected <U extends List<Object>> void parse(RuleParser<U> parser) throws ParserException {
        A = new ListAccessor(parser.parse());
    }

    protected T parse(Parser.SequenceParser parser, ParserFunction<SequenceListAccessor, T> consumer) throws ParserException {
        SequenceListAccessor A = new SequenceListAccessor(parser.parse());

        if (!A.matched()) {
            return null;
        }

        return consumer.apply(A);
    }

    protected <T, U> T parse(Parser.RepetitionParser<U> parser, ParserFunction<RepetitionListAccessor, T> consumer) throws ParserException {
        RepetitionListAccessor A = new RepetitionListAccessor<U>(parser.parse());

        if (!A.matched()) {
            return null;
        }

        return consumer.apply(A);
    }

    protected boolean matched() {
        return A.matched();
    }

    protected <U, V extends List<U>> boolean match(RuleParser<V> parser) throws ParserException {
        A = new ListAccessor(parser.parse());

        return A.matched();
    }

    protected Token t(int i) {
        return A.t(i);
    }

    protected Token t0() {
        return t(0);
    }

    protected Token t1() {
        return t(1);
    }

    protected Token t2() {
        return t(2);
    }

    protected String s0() {
        return t0().getText();
    }

    protected Token.TokenType $0() {
        return t0().getType();
    }

    protected <T> T n(int i) {
        return (T) A.n(i);
    }

    protected <T> T n0() {
        return n(0);
    }

    protected <T> T n1() {
        return n(1);
    }

    protected <T> T n2() {
        return n(2);
    }

    protected Position P0() {
        return A.P(0);
    }

    protected Position P1() {
        return A.P(1);
    }

    protected Position P2() {
        return A.P(2);
    }

}
