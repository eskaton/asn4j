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
import ch.eskaton.asn4j.parser.ast.values.BMPStringValue;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static ch.eskaton.asn4j.compiler.CompilerTestUtils.getCompiledValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BMPStringValueCompilerTest {

    @Test
    void testResolveCStringValue() throws IOException, ParserException {
        var body = """
                testBMPString1 BMPString ::= "test"
                """;

        var compiledValue = getCompiledValue(body, BMPStringValue.class, "testBMPString1");
        var value = (BMPStringValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("test"));
    }

    @Test
    void testResolveReference() throws IOException, ParserException {
        var body = """
                testBMPString1 BMPString ::= "test"
                testBMPString2 BMPString ::= testBMPString1
                """;

        var compiledValue = getCompiledValue(body, BMPStringValue.class, "testBMPString2");
        var value = (BMPStringValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("test"));
    }

    @Test
    void testResolveCStringValueWithTypeReference() throws IOException, ParserException {
        var body = """
                TestBMPString1 ::= BMPString
                TestBMPString2 ::= TestBMPString1
                testBMPString1 TestBMPString2 ::= "test"
                """;

        var compiledValue = getCompiledValue(body, BMPStringValue.class, "testBMPString1");
        var value = (BMPStringValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("test"));
    }

    @Test
    void testResolveReferenceWithTypeReference() throws IOException, ParserException {
        var body = """
                TestBMPString1 ::= BMPString
                TestBMPString2 ::= TestBMPString1
                testBMPString1 TestBMPString2 ::= "test"
                testBMPString2 BMPString ::= testBMPString1
                """;

        var compiledValue = getCompiledValue(body, BMPStringValue.class, "testBMPString2");
        var value = (BMPStringValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("test"));
    }

    @Test
    void testResolveReferenceWithInvalidTypeReference() {
        var body = """
                TestBMPString1 ::= NumericString
                testBMPString1 TestBMPString1 ::= "test"
                testBMPString2 BMPString ::= testBMPString1
                """;

        assertThrows(CompilerException.class, () -> getCompiledValue(body, BMPStringValue.class, "testBMPString2"));
    }

    @Test
    void testResolveCharacterStringList() throws IOException, ParserException {
        var body = """
                testBMPString1 BMPString ::= {"abc", "def"}
                """;

        var compiledValue = getCompiledValue(body, BMPStringValue.class, "testBMPString1");
        var value = (BMPStringValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("abcdef"));
    }

    @Test
    void testResolveCharacterStringListWithReference() throws IOException, ParserException {
        var body = """
                testBMPString1 BMPString ::= "abc"
                testBMPString2 BMPString ::= {testBMPString1, "def"}
                testBMPString3 BMPString ::= {testBMPString2, "ghi"}
                """;

        var compiledValue = getCompiledValue(body, BMPStringValue.class, "testBMPString3");
        var value = (BMPStringValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("abcdefghi"));
    }

    @Test
    void testResolveCharacterStringListWithMissingReference() {
        var body = """
                testBMPString2 BMPString ::= {testBMPString1, "def"}
                """;
        assertThrows(CompilerException.class, () -> getCompiledValue(body, BMPStringValue.class, "testBMPString2"));
    }

    @Test
    void testResolveCharacterStringListWithInvalidReference() {
        var body = """
                testNumericString1 NumericString ::= "123"
                testBMPString2 BMPString ::= {testNumericString1, "def"}
                """;
        assertThrows(CompilerException.class, () -> getCompiledValue(body, BMPStringValue.class, "testBMPString2"));
    }

    @Test
    void testResolveInvalidTupleValue() {
        var body = """
                testBMPString1 BMPString ::= {1, 1}
                """;
        var exception = assertThrows(CompilerException.class,
                () -> getCompiledValue(body, BMPStringValue.class, "testBMPString1"));

        assertThat(exception.getMessage(), matchesPattern(".*Tuple values not allowed for type BMPString.*"));
    }

    @Test
    void testResolveInvalidQuadrupleValue() {
        var body = """
                testBMPString1 BMPString ::= {0, 1, 212, 175}
                """;
        var exception = assertThrows(CompilerException.class,
                () -> getCompiledValue(body, BMPStringValue.class, "testBMPString1"));

        assertThat(exception.getMessage(), matchesPattern(".*BMPString contains invalid characters.*"));
    }

    @Test
    void testResolveQuadrupleValue() throws IOException, ParserException {
        var body = """
                testBMPString1 BMPString ::= {0, 0, 32, 172}
                """;

        var compiledValue = getCompiledValue(body, BMPStringValue.class, "testBMPString1");
        var value = (BMPStringValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("€"));
    }

}
