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

package ch.eskaton.asn4j.compiler.values;

import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.parser.ParserException;
import ch.eskaton.asn4j.parser.ast.values.VisibleStringValue;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static ch.eskaton.asn4j.compiler.CompilerTestUtils.getCompiledValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VisibleStringValueCompilerTest {

    @Test
    void testResolveCStringValue() throws IOException, ParserException {
        var body = """
                testVisibleString1 VisibleString ::= "test"
                """;

        var compiledValue = getCompiledValue(body, VisibleStringValue.class, "testVisibleString1");
        var value = (VisibleStringValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("test"));
    }

    @Test
    void testResolveInvalidCStringValue() {
        var body = """
                testVisibleString1 VisibleString ::= "öäü"
                """;
        assertThrows(CompilerException.class,
                () -> getCompiledValue(body, VisibleStringValue.class, "testVisibleString1"));
    }

    @Test
    void testResolveReference() throws IOException, ParserException {
        var body = """
                testVisibleString1 VisibleString ::= "test"
                testVisibleString2 VisibleString ::= testVisibleString1
                """;

        var compiledValue = getCompiledValue(body, VisibleStringValue.class, "testVisibleString2");
        var value = (VisibleStringValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("test"));
    }

    @Test
    void testResolveCStringValueWithTypeReference() throws IOException, ParserException {
        var body = """
                TestVisibleString1 ::= VisibleString
                TestVisibleString2 ::= TestVisibleString1
                testVisibleString1 TestVisibleString2 ::= "test"
                """;

        var compiledValue = getCompiledValue(body, VisibleStringValue.class, "testVisibleString1");
        var value = (VisibleStringValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("test"));
    }

    @Test
    void testResolveReferenceWithTypeReference() throws IOException, ParserException {
        var body = """
                TestVisibleString1 ::= VisibleString
                TestVisibleString2 ::= TestVisibleString1
                testVisibleString1 TestVisibleString2 ::= "test"
                testVisibleString2 VisibleString ::= testVisibleString1
                """;

        var compiledValue = getCompiledValue(body, VisibleStringValue.class, "testVisibleString2");
        var value = (VisibleStringValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("test"));
    }

    @Test
    void testResolveReferenceWithInvalidTypeReference() {
        var body = """
                TestVisibleString1 ::= NumericString
                testVisibleString1 TestVisibleString1 ::= "test"
                testVisibleString2 VisibleString ::= testVisibleString1
                """;

        assertThrows(CompilerException.class,
                () -> getCompiledValue(body, VisibleStringValue.class, "testVisibleString2"));
    }

    @Test
    void testResolveCharacterStringList() throws IOException, ParserException {
        var body = """
                testVisibleString1 VisibleString ::= {"abc", "def"}
                """;

        var compiledValue = getCompiledValue(body, VisibleStringValue.class, "testVisibleString1");
        var value = (VisibleStringValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("abcdef"));
    }

    @Test
    void testResolveInvalidCharacterStringList() {
        var body = """
                testVisibleString1 VisibleString ::= {"abc", "öäü"}
                """;

        assertThrows(CompilerException.class,
                () -> getCompiledValue(body, VisibleStringValue.class, "testVisibleString1"));
    }

    @Test
    void testResolveCharacterStringListWithReference() throws IOException, ParserException {
        var body = """
                testVisibleString1 VisibleString ::= "abc"
                testVisibleString2 VisibleString ::= {testVisibleString1, "def"}
                testVisibleString3 VisibleString ::= {testVisibleString2, "ghi"}
                """;

        var compiledValue = getCompiledValue(body, VisibleStringValue.class, "testVisibleString3");
        var value = (VisibleStringValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("abcdefghi"));
    }

    @Test
    void testResolveCharacterStringListWithMissingReference() {
        var body = """
                testVisibleString2 VisibleString ::= {testVisibleString1, "def"}
                """;
        assertThrows(CompilerException.class,
                () -> getCompiledValue(body, VisibleStringValue.class, "testVisibleString2"));
    }

    @Test
    void testResolveCharacterStringListWithInvalidReference() {
        var body = """
                testNumericString1 NumericString ::= "123"
                testVisibleString2 VisibleString ::= {testNumericString1, "def"}
                """;
        assertThrows(CompilerException.class,
                () -> getCompiledValue(body, VisibleStringValue.class, "testVisibleString2"));
    }

    @Test
    void testResolveInvalidTupleValue() {
        var body = """
                testVisibleString1 VisibleString ::= {1, 1}
                """;
        var exception = assertThrows(CompilerException.class,
                () -> getCompiledValue(body, VisibleStringValue.class, "testVisibleString1"));

        assertThat(exception.getMessage(), matchesPattern(".*Tuple values not allowed for type VisibleString.*"));
    }

    @Test
    void testResolveInvalidQuadrupleValue() {
        var body = """
                testVisibleString1 VisibleString ::= {1, 1, 1, 1}
                """;
        var exception = assertThrows(CompilerException.class,
                () -> getCompiledValue(body, VisibleStringValue.class, "testVisibleString1"));

        assertThat(exception.getMessage(), matchesPattern(".*Quadruple values not allowed for type VisibleString.*"));
    }

}
