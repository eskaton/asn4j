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
import ch.eskaton.asn4j.parser.ast.values.UniversalStringValue;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static ch.eskaton.asn4j.compiler.resolvers.ResolverTestUtils.resolveValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UniversalStringValueResolverTest {

    @Test
    void testResolveCStringValue() throws IOException, ParserException {
        var body = """
                testUniversalString1 UniversalString ::= "test"
                """;
        var value = resolveValue(body, UniversalStringValue.class, "testUniversalString1");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("test"));
    }

    @Test
    void testResolveReference() throws IOException, ParserException {
        var body = """
                testUniversalString1 UniversalString ::= "test"
                testUniversalString2 UniversalString ::= testUniversalString1
                """;
        var value = resolveValue(body, UniversalStringValue.class, "testUniversalString2");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("test"));
    }

    @Test
    void testResolveCStringValueWithTypeReference() throws IOException, ParserException {
        var body = """
                TestUniversalString1 ::= UniversalString
                TestUniversalString2 ::= TestUniversalString1
                testUniversalString1 TestUniversalString2 ::= "test"
                """;
        var value = resolveValue(body, UniversalStringValue.class, "testUniversalString1");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("test"));
    }

    @Test
    void testResolveReferenceWithTypeReference() throws IOException, ParserException {
        var body = """
                TestUniversalString1 ::= UniversalString
                TestUniversalString2 ::= TestUniversalString1
                testUniversalString1 TestUniversalString2 ::= "test"
                testUniversalString2 UniversalString ::= testUniversalString1
                """;
        var value = resolveValue(body, UniversalStringValue.class, "testUniversalString2");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("test"));
    }

    @Test
    void testResolveReferenceWithInvalidTypeReference() {
        var body = """
                TestUniversalString1 ::= NumericString
                testUniversalString1 TestUniversalString1 ::= "test"
                testUniversalString2 UniversalString ::= testUniversalString1
                """;

        assertThrows(CompilerException.class, () -> resolveValue(body, UniversalStringValue.class, "testUniversalString2"));
    }

    @Test
    void testResolveCharacterStringList() throws IOException, ParserException {
        var body = """
                testUniversalString1 UniversalString ::= {"abc", "def"}
                """;
        var value = resolveValue(body, UniversalStringValue.class, "testUniversalString1");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("abcdef"));
    }

    @Test
    void testResolveCharacterStringListWithReference() throws IOException, ParserException {
        var body = """
                testUniversalString1 UniversalString ::= "abc"
                testUniversalString2 UniversalString ::= {testUniversalString1, "def"}
                testUniversalString3 UniversalString ::= {testUniversalString2, "ghi"}
                """;
        var value = resolveValue(body, UniversalStringValue.class, "testUniversalString3");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("abcdefghi"));
    }

    @Test
    void testResolveCharacterStringListWithMissingReference() {
        var body = """
                testUniversalString2 UniversalString ::= {testUniversalString1, "def"}
                """;
        assertThrows(CompilerException.class, () -> resolveValue(body, UniversalStringValue.class, "testUniversalString2"));
    }

    @Test
    void testResolveCharacterStringListWithInvalidReference() {
        var body = """
                testNumericString1 NumericString ::= "123"
                testUniversalString2 UniversalString ::= {testNumericString1, "def"}
                """;
        assertThrows(CompilerException.class, () -> resolveValue(body, UniversalStringValue.class, "testUniversalString2"));
    }

    @Test
    void testResolveInvalidTupleValue() {
        var body = """
                testUniversalString1 UniversalString ::= {1, 1}
                """;
        var exception = assertThrows(CompilerException.class,
                () -> resolveValue(body, UniversalStringValue.class, "testUniversalString1"));

        assertThat(exception.getMessage(), matchesPattern(".*Tuple values not allowed for type UniversalString.*"));
    }

    @Test
    void testResolveQuadrupleValue() throws IOException, ParserException {
        var body = """
                testUniversalString1 UniversalString ::= {0, 1, 212, 175}
                """;
        var value = resolveValue(body, UniversalStringValue.class, "testUniversalString1");

        assertThat(value.getValue(), equalTo("\uD835\uDCAF"));
    }

}
