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

package ch.eskaton.asn4j.compiler.resolvers;

import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.parser.ParserException;
import ch.eskaton.asn4j.parser.ast.values.TeletexStringValue;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static ch.eskaton.asn4j.compiler.resolvers.ResolverTestUtils.resolveValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class T61StringValueResolverTest {

    @Test
    void testResolveCStringValue() throws IOException, ParserException {
        var body = """
                testT61String1 T61String ::= "abcäöϕ"
                """;
        var value = resolveValue(body, TeletexStringValue.class, "testT61String1");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("abcäöϕ"));
    }

    @Test
    void testResolveReference() throws IOException, ParserException {
        var body = """
                testT61String1 T61String ::= "abcäöϕ"
                testT61String2 T61String ::= testT61String1
                """;
        var value = resolveValue(body, TeletexStringValue.class, "testT61String2");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("abcäöϕ"));
    }

    @Test
    void testResolveCStringValueWithTypeReference() throws IOException, ParserException {
        var body = """
                TestT61String1 ::= T61String
                TestT61String2 ::= TestT61String1
                testT61String1 TestT61String2 ::= "abcäöϕ"
                """;
        var value = resolveValue(body, TeletexStringValue.class, "testT61String1");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("abcäöϕ"));
    }

    @Test
    void testResolveReferenceWithTypeReference() throws IOException, ParserException {
        var body = """
                TestT61String1 ::= T61String
                TestT61String2 ::= TestT61String1
                testT61String1 TestT61String2 ::= "abcäöϕ"
                testT61String2 T61String ::= testT61String1
                """;
        var value = resolveValue(body, TeletexStringValue.class, "testT61String2");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("abcäöϕ"));
    }

    @Test
    void testResolveReferenceWithInvalidTypeReference() {
        var body = """
                TestT61String1 ::= VisibleString
                testT61String1 TestT61String1 ::= "abcäöϕ"
                testT61String2 T61String ::= testT61String1
                """;

        assertThrows(CompilerException.class, () -> resolveValue(body, TeletexStringValue.class, "testT61String2"));
    }

    @Test
    void testResolveCharacterStringList() throws IOException, ParserException {
        var body = """
                testT61String1 T61String ::= {"abc", "def"}
                """;
        var value = resolveValue(body, TeletexStringValue.class, "testT61String1");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("abcdef"));
    }

    @Test
    void testResolveCharacterStringListWithReference() throws IOException, ParserException {
        var body = """
                testT61String1 T61String ::= "123"
                testT61String2 T61String ::= {testT61String1, "456"}
                testT61String3 T61String ::= {testT61String2, "789"}
                """;
        var value = resolveValue(body, TeletexStringValue.class, "testT61String3");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("123456789"));
    }

    @Test
    void testResolveCharacterStringListWithMissingReference() {
        var body = """
                testT61String2 T61String ::= {testT61String1, "123"}
                """;
        assertThrows(CompilerException.class, () -> resolveValue(body, TeletexStringValue.class, "testT61String2"));
    }

    @Test
    void testResolveCharacterStringListWithInvalidReference() {
        var body = """
                testT61String1 VisibleString ::= "123"
                testT61String2 T61String ::= {testT61String1, "456"}
                """;
        assertThrows(CompilerException.class, () -> resolveValue(body, TeletexStringValue.class, "testT61String2"));
    }

    @Test
    void testResolveInvalidTupleValue() {
        var body = """
                testT61String1 T61String ::= {1, 1}
                """;
        var exception = assertThrows(CompilerException.class,
                () -> resolveValue(body, TeletexStringValue.class, "testT61String1"));

        assertThat(exception.getMessage(), matchesPattern(".*Tuple values not allowed for type TeletexString.*"));
    }

    @Test
    void testResolveInvalidQuadrupleValue() {
        var body = """
                testT61String1 T61String ::= {1, 1, 1, 1}
                """;
        var exception = assertThrows(CompilerException.class,
                () -> resolveValue(body, TeletexStringValue.class, "testT61String1"));

        assertThat(exception.getMessage(), matchesPattern(".*Quadruple values not allowed for type TeletexString.*"));
    }

}
