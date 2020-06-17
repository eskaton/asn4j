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
import ch.eskaton.asn4j.parser.ast.values.GeneralStringValue;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static ch.eskaton.asn4j.compiler.resolvers.ResolverTestUtils.resolveValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GeneralStringValueResolverTest {

    @Test
    void testResolveCStringValue() throws IOException, ParserException {
        var body = """
                testGeneralString1 GeneralString ::= "abc"
                """;
        var value = resolveValue(body, GeneralStringValue.class, "testGeneralString1");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("abc"));
    }

    @Test
    void testResolveInvalidCStringValue() {
        var body = """
                testGeneralString1 GeneralString ::= "äöü"
                """;
        assertThrows(CompilerException.class, () -> resolveValue(body, GeneralStringValue.class, "testGeneralString1"));
    }

    @Test
    void testResolveReference() throws IOException, ParserException {
        var body = """
                testGeneralString1 GeneralString ::= "abc"
                testGeneralString2 GeneralString ::= testGeneralString1
                """;
        var value = resolveValue(body, GeneralStringValue.class, "testGeneralString2");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("abc"));
    }

    @Test
    void testResolveCStringValueWithTypeReference() throws IOException, ParserException {
        var body = """
                TestGeneralString1 ::= GeneralString
                TestGeneralString2 ::= TestGeneralString1
                testGeneralString1 TestGeneralString2 ::= "abc"
                """;
        var value = resolveValue(body, GeneralStringValue.class, "testGeneralString1");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("abc"));
    }

    @Test
    void testResolveReferenceWithTypeReference() throws IOException, ParserException {
        var body = """
                TestGeneralString1 ::= GeneralString
                TestGeneralString2 ::= TestGeneralString1
                testGeneralString1 TestGeneralString2 ::= "abc"
                testGeneralString2 GeneralString ::= testGeneralString1
                """;
        var value = resolveValue(body, GeneralStringValue.class, "testGeneralString2");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("abc"));
    }

    @Test
    void testResolveReferenceWithInvalidTypeReference() {
        var body = """
                TestGeneralString1 ::= VisibleString
                testGeneralString1 TestGeneralString1 ::= "abc"
                testGeneralString2 GeneralString ::= testGeneralString1
                """;

        assertThrows(CompilerException.class, () -> resolveValue(body, GeneralStringValue.class, "testGeneralString2"));
    }

    @Test
    void testResolveCharacterStringList() throws IOException, ParserException {
        var body = """
                testGeneralString1 GeneralString ::= {"abc", "def"}
                """;
        var value = resolveValue(body, GeneralStringValue.class, "testGeneralString1");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("abcdef"));
    }

    @Test
    void testResolveInvalidCharacterStringList() {
        var body = """
                testGeneralString1 GeneralString ::= {"abc", "äöü"}
                """;

        assertThrows(CompilerException.class, () -> resolveValue(body, GeneralStringValue.class, "testGeneralString1"));
    }

    @Test
    void testResolveCharacterStringListWithReference() throws IOException, ParserException {
        var body = """
                testGeneralString1 GeneralString ::= "123"
                testGeneralString2 GeneralString ::= {testGeneralString1, "456"}
                testGeneralString3 GeneralString ::= {testGeneralString2, "789"}
                """;
        var value = resolveValue(body, GeneralStringValue.class, "testGeneralString3");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("123456789"));
    }

    @Test
    void testResolveCharacterStringListWithMissingReference() {
        var body = """
                testGeneralString2 GeneralString ::= {testGeneralString1, "123"}
                """;
        assertThrows(CompilerException.class, () -> resolveValue(body, GeneralStringValue.class, "testGeneralString2"));
    }

    @Test
    void testResolveCharacterStringListWithInvalidReference() {
        var body = """
                testGeneralString1 VisibleString ::= "123"
                testGeneralString2 GeneralString ::= {testGeneralString1, "456"}
                """;
        assertThrows(CompilerException.class, () -> resolveValue(body, GeneralStringValue.class, "testGeneralString2"));
    }

    @Test
    void testResolveInvalidTupleValue() {
        var body = """
                testGeneralString1 GeneralString ::= {1, 1}
                """;
        var exception = assertThrows(CompilerException.class,
                () -> resolveValue(body, GeneralStringValue.class, "testGeneralString1"));

        assertThat(exception.getMessage(), matchesPattern("Tuple values not allowed for type GeneralString.*"));
    }

    @Test
    void testResolveInvalidQuadrupleValue() {
        var body = """
                testGeneralString1 GeneralString ::= {1, 1, 1, 1}
                """;
        var exception = assertThrows(CompilerException.class,
                () -> resolveValue(body, GeneralStringValue.class, "testGeneralString1"));

        assertThat(exception.getMessage(), matchesPattern("Quadruple values not allowed for type GeneralString.*"));
    }

}
