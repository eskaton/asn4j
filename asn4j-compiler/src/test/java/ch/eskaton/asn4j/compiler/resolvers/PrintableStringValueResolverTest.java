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
import ch.eskaton.asn4j.parser.ast.values.PrintableStringValue;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static ch.eskaton.asn4j.compiler.resolvers.ResolverTestUtils.resolveValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrintableStringValueResolverTest {

    @Test
    void testResolveCStringValue() throws IOException, ParserException {
        var body = """
                testPrintableString1 PrintableString ::= "abc"
                """;
        var value = resolveValue(body, PrintableStringValue.class, "testPrintableString1");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("abc"));
    }

    @Test
    void testResolveInvalidCStringValue() {
        var body = """
                testPrintableString1 PrintableString ::= "äöü"
                """;
        assertThrows(CompilerException.class, () -> resolveValue(body, PrintableStringValue.class, "testPrintableString1"));
    }

    @Test
    void testResolveReference() throws IOException, ParserException {
        var body = """
                testPrintableString1 PrintableString ::= "abc"
                testPrintableString2 PrintableString ::= testPrintableString1
                """;
        var value = resolveValue(body, PrintableStringValue.class, "testPrintableString2");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("abc"));
    }

    @Test
    void testResolveCStringValueWithTypeReference() throws IOException, ParserException {
        var body = """
                TestPrintableString1 ::= PrintableString
                TestPrintableString2 ::= TestPrintableString1
                testPrintableString1 TestPrintableString2 ::= "abc"
                """;
        var value = resolveValue(body, PrintableStringValue.class, "testPrintableString1");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("abc"));
    }

    @Test
    void testResolveReferenceWithTypeReference() throws IOException, ParserException {
        var body = """
                TestPrintableString1 ::= PrintableString
                TestPrintableString2 ::= TestPrintableString1
                testPrintableString1 TestPrintableString2 ::= "abc"
                testPrintableString2 PrintableString ::= testPrintableString1
                """;
        var value = resolveValue(body, PrintableStringValue.class, "testPrintableString2");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("abc"));
    }

    @Test
    void testResolveReferenceWithInvalidTypeReference() {
        var body = """
                TestPrintableString1 ::= VisibleString
                testPrintableString1 TestPrintableString1 ::= "abc"
                testPrintableString2 PrintableString ::= testPrintableString1
                """;

        assertThrows(CompilerException.class, () -> resolveValue(body, PrintableStringValue.class, "testPrintableString2"));
    }

    @Test
    void testResolveCharacterStringList() throws IOException, ParserException {
        var body = """
                testPrintableString1 PrintableString ::= {"abc", "def"}
                """;
        var value = resolveValue(body, PrintableStringValue.class, "testPrintableString1");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("abcdef"));
    }

    @Test
    void testResolveInvalidCharacterStringList() {
        var body = """
                testPrintableString1 PrintableString ::= {"abc", "äöü"}
                """;

        assertThrows(CompilerException.class, () -> resolveValue(body, PrintableStringValue.class, "testPrintableString1"));
    }

    @Test
    void testResolveCharacterStringListWithReference() throws IOException, ParserException {
        var body = """
                testPrintableString1 PrintableString ::= "123"
                testPrintableString2 PrintableString ::= {testPrintableString1, "456"}
                testPrintableString3 PrintableString ::= {testPrintableString2, "789"}
                """;
        var value = resolveValue(body, PrintableStringValue.class, "testPrintableString3");

        assertNotNull(value);
        assertThat(value.getValue(), equalTo("123456789"));
    }

    @Test
    void testResolveCharacterStringListWithMissingReference() {
        var body = """
                testPrintableString2 PrintableString ::= {testPrintableString1, "123"}
                """;
        assertThrows(CompilerException.class, () -> resolveValue(body, PrintableStringValue.class, "testPrintableString2"));
    }

    @Test
    void testResolveCharacterStringListWithInvalidReference() {
        var body = """
                testPrintableString1 VisibleString ::= "123"
                testPrintableString2 PrintableString ::= {testPrintableString1, "456"}
                """;
        assertThrows(CompilerException.class, () -> resolveValue(body, PrintableStringValue.class, "testPrintableString2"));
    }

    @Test
    void testResolveInvalidTupleValue() {
        var body = """
                testPrintableString1 PrintableString ::= {1, 1}
                """;
        var exception = assertThrows(CompilerException.class,
                () -> resolveValue(body, PrintableStringValue.class, "testPrintableString1"));

        assertThat(exception.getMessage(), matchesPattern(".*Tuple values not allowed for type PrintableString.*"));
    }

    @Test
    void testResolveInvalidQuadrupleValue() {
        var body = """
                testPrintableString1 PrintableString ::= {1, 1, 1, 1}
                """;
        var exception = assertThrows(CompilerException.class,
                () -> resolveValue(body, PrintableStringValue.class, "testPrintableString1"));

        assertThat(exception.getMessage(), matchesPattern(".*Quadruple values not allowed for type PrintableString.*"));
    }

}
