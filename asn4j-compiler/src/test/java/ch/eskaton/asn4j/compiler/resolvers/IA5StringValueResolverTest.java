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
import ch.eskaton.asn4j.parser.ast.values.IA5StringValue;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static ch.eskaton.asn4j.compiler.resolvers.ResolverTestUtils.resolveValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IA5StringValueResolverTest {

    @Test
    void testResolveCStringValue() throws IOException, ParserException {
        var body = """
                testIA5String1 IA5String ::= "test"
                """;
        var value = resolveValue(body, IA5StringValue.class, "testIA5String1");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("test"));
    }

    @Test
    void testResolveInvalidCStringValue() {
        var body = """
                testIA5String1 IA5String ::= "öäü"
                """;
        assertThrows(CompilerException.class, () -> resolveValue(body, IA5StringValue.class, "testIA5String1"));
    }

    @Test
    void testResolveReference() throws IOException, ParserException {
        var body = """
                testIA5String1 IA5String ::= "test"
                testIA5String2 IA5String ::= testIA5String1
                """;
        var value = resolveValue(body, IA5StringValue.class, "testIA5String2");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("test"));
    }

    @Test
    void testResolveTupleValue() throws IOException, ParserException {
        var body = """
                testIA5String1 IA5String ::= {0, 10}
                """;
        var value = resolveValue(body, IA5StringValue.class, "testIA5String1");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("\n"));
    }

    @Test
    void testResolveInvalidTupleValue() {
        var body = """
                testIA5String1 IA5String ::= {10, 10}
                """;
        var exception = assertThrows(CompilerException.class,
                () -> resolveValue(body, IA5StringValue.class, "testIA5String1"));

        assertThat(exception.getMessage(), matchesPattern("Invalid tuple.*"));
    }

    @Test
    void testResolveInvalidQuadrupleValue() {
        var body = """
                testIA5String1 IA5String ::= {1, 1, 1, 1}
                """;
        var exception = assertThrows(CompilerException.class,
                () -> resolveValue(body, IA5StringValue.class, "testIA5String1"));

        assertThat(exception.getMessage(), matchesPattern(".*Quadruple values not allowed for type IA5String.*"));
    }

    @Test
    void testResolveCStringValueWithTypeReference() throws IOException, ParserException {
        var body = """
                TestIA5String1 ::= IA5String
                TestIA5String2 ::= TestIA5String1
                testIA5String1 TestIA5String2 ::= "test"
                """;
        var value = resolveValue(body, IA5StringValue.class, "testIA5String1");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("test"));
    }

    @Test
    void testResolveReferenceWithTypeReference() throws IOException, ParserException {
        var body = """
                TestIA5String1 ::= IA5String
                TestIA5String2 ::= TestIA5String1
                testIA5String1 TestIA5String2 ::= "test"
                testIA5String2 IA5String ::= testIA5String1
                """;
        var value = resolveValue(body, IA5StringValue.class, "testIA5String2");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("test"));
    }

    @Test
    void testResolveReferenceWithInvalidTypeReference() {
        var body = """
                TestIA5String1 ::= NumericString
                testIA5String1 TestIA5String1 ::= "test"
                testIA5String2 IA5String ::= testIA5String1
                """;

        assertThrows(CompilerException.class, () -> resolveValue(body, IA5StringValue.class, "testIA5String2"));
    }

    @Test
    void testResolveCharacterStringList() throws IOException, ParserException {
        var body = """
                testIA5String1 IA5String ::= {"abc", "def"}
                """;
        var value = resolveValue(body, IA5StringValue.class, "testIA5String1");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("abcdef"));
    }

    @Test
    void testResolveInvalidCharacterStringList() {
        var body = """
                testIA5String1 IA5String ::= {"abc", "öäü"}
                """;

        assertThrows(CompilerException.class, () -> resolveValue(body, IA5StringValue.class, "testIA5String1"));
    }

    @Test
    void testResolveCharacterStringListWithReference() throws IOException, ParserException {
        var body = """
                testIA5String1 IA5String ::= "abc"
                testIA5String2 IA5String ::= {testIA5String1, "def"}
                testIA5String3 IA5String ::= {testIA5String2, "ghi"}
                """;
        var value = resolveValue(body, IA5StringValue.class, "testIA5String3");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("abcdefghi"));
    }

    @Test
    void testResolveCharacterStringListWithMissingReference() {
        var body = """
                testIA5String2 IA5String ::= {testIA5String1, "def"}
                """;
        assertThrows(CompilerException.class, () -> resolveValue(body, IA5StringValue.class, "testIA5String2"));
    }

    @Test
    void testResolveCharacterStringListWithInvalidReference() {
        var body = """
                testNumericString1 NumericString ::= "123"
                testIA5String2 IA5String ::= {testNumericString1, "def"}
                """;
        assertThrows(CompilerException.class, () -> resolveValue(body, IA5StringValue.class, "testIA5String2"));
    }

    @Test
    void testResolveCharacterStringListWithTuples() throws IOException, ParserException {
        var body = """
                testIA5String1 IA5String ::= {4, 1}
                testIA5String2 IA5String ::= {testIA5String1, {0, 10}}
                """;
        var value = resolveValue(body, IA5StringValue.class, "testIA5String2");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("A\n"));
    }

}
