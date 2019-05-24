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

package ch.eskaton.asn4j.parser.accessor;

import ch.eskaton.asn4j.parser.Position;
import ch.eskaton.asn4j.parser.Token;
import ch.eskaton.asn4j.parser.Token.TokenType;

import java.util.List;

import static ch.eskaton.asn4j.parser.ParserUtils.getPosition;

public class ListAccessor<S> implements Accessor<List<S>> {

    private final List<S> rule;

    public ListAccessor(List<S> rule) {
        this.rule = rule;
    }

    public boolean matched() {
        return rule != null;
    }

    public int size() {
        return rule.size();
    }

    public Token t(int i) {
        return (Token) (rule.get(i));
    }

    public Token t0() {
        return t(0);
    }

    public Token t1() {
        return t(1);
    }

    public Token t2() {
        return t(2);
    }

    public String s0() {
        return t0().getText();
    }

    public String s1() {
        return t1().getText();
    }

    public String s2() {
        return t2().getText();
    }

    public TokenType $0() {
        return t0().getType();
    }

    public TokenType $1() {
        return t1().getType();
    }

    public <T> T n(int i) {
        return (T) rule.get(i);
    }

    public <T> T n0() {
        return n(0);
    }

    public <T> T n1() {
        return n(1);
    }

    public <T> T n2() {
        return n(2);
    }

    public <T> T n3() {
        return n(3);
    }

    public <T> T n4() {
        return n(4);
    }

    @Override
    public List<S> p() {
        return rule;
    }

    public Position P(int i) {
        return getPosition(rule.get(i));
    }

    public Position P() {
        return P0();
    }

    public Position P0() {
        return P(0);
    }

    public Position P1() {
        return P(1);
    }

    public Position P2() {
        return P(2);
    }

}

