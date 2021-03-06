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
import ch.eskaton.asn4j.parser.ast.values.GeneralizedTimeValue;
import ch.eskaton.asn4j.runtime.exceptions.ASN1RuntimeException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static ch.eskaton.asn4j.compiler.CompilerTestUtils.getCompiledValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GeneralizedTimeValueCompilerTest {

    @Test
    void testResolveCStringValue() throws IOException, ParserException {
        var body = """
                testGeneralizedTime1 GeneralizedTime ::= "19851106210627.3Z"
                """;

        var compiledValue = getCompiledValue(body, GeneralizedTimeValue.class, "testGeneralizedTime1");
        var value = (GeneralizedTimeValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("19851106210627.3Z"));
    }

    @Test
    void testResolveInvalidCStringValue() {
        var body = """
                testGeneralizedTime1 GeneralizedTime ::= "19851106210627,3Z"
                """;
        assertThrows(CompilerException.class,
                () -> getCompiledValue(body, GeneralizedTimeValue.class, "testGeneralizedTime1"));
    }

    @Test
    void testResolveInvalidGeneralizedTimeValue() {
        var body = """
                testGeneralizedTime1 GeneralizedTime ::= "19851106210627.3-1900"
                """;
        assertThrows(ASN1RuntimeException.class,
                () -> getCompiledValue(body, GeneralizedTimeValue.class, "testGeneralizedTime1"));
    }

    @Test
    void testResolveReference() throws IOException, ParserException {
        var body = """
                testGeneralizedTime1 GeneralizedTime ::= "19851106210627.3Z"
                testGeneralizedTime2 GeneralizedTime ::= testGeneralizedTime1
                """;

        var compiledValue = getCompiledValue(body, GeneralizedTimeValue.class, "testGeneralizedTime2");
        var value = (GeneralizedTimeValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("19851106210627.3Z"));
    }

    @Test
    void testResolveCStringValueWithTypeReference() throws IOException, ParserException {
        var body = """
                TestGeneralizedTime1 ::= GeneralizedTime
                TestGeneralizedTime2 ::= TestGeneralizedTime1
                testGeneralizedTime1 TestGeneralizedTime2 ::= "19851106210627.3Z"
                """;

        var compiledValue = getCompiledValue(body, GeneralizedTimeValue.class, "testGeneralizedTime1");
        var value = (GeneralizedTimeValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("19851106210627.3Z"));
    }

    @Test
    void testResolveReferenceWithTypeReference() throws IOException, ParserException {
        var body = """
                TestGeneralizedTime1 ::= GeneralizedTime
                TestGeneralizedTime2 ::= TestGeneralizedTime1
                testGeneralizedTime1 TestGeneralizedTime2 ::= "19851106210627.3Z"
                testGeneralizedTime2 GeneralizedTime ::= testGeneralizedTime1
                """;

        var compiledValue = getCompiledValue(body, GeneralizedTimeValue.class, "testGeneralizedTime2");
        var value = (GeneralizedTimeValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("19851106210627.3Z"));
    }

    @Test
    void testResolveReferenceWithInvalidTypeReference() {
        var body = """
                TestGeneralizedTime1 ::= NumericString
                testGeneralizedTime1 TestGeneralizedTime1 ::= "test"
                testGeneralizedTime2 GeneralizedTime ::= testGeneralizedTime1
                """;

        assertThrows(CompilerException.class,
                () -> getCompiledValue(body, GeneralizedTimeValue.class, "testGeneralizedTime2"));
    }

    @Test
    void testResolveUnsupportedCharacterStringList() {
        var body = """
                testGeneralizedTime1 GeneralizedTime ::= {"19851106210627.3Z"}
                """;

        assertThrows(CompilerException.class,
                () -> getCompiledValue(body, GeneralizedTimeValue.class, "testGeneralizedTime1"));
    }

    @Test
    void testResolveInvalidTupleValue() {
        var body = """
                testGeneralizedTime1 GeneralizedTime ::= {1, 1}
                """;
        var exception = assertThrows(CompilerException.class,
                () -> getCompiledValue(body, GeneralizedTimeValue.class, "testGeneralizedTime1"));

        assertThat(exception.getMessage(), matchesPattern(".*Tuple values not allowed for type GeneralizedTime.*"));
    }

    @Test
    void testResolveInvalidQuadrupleValue() {
        var body = """
                testGeneralizedTime1 GeneralizedTime ::= {1, 1, 1, 1}
                """;
        var exception = assertThrows(CompilerException.class,
                () -> getCompiledValue(body, GeneralizedTimeValue.class, "testGeneralizedTime1"));

        assertThat(exception.getMessage(), matchesPattern(".*Quadruple values not allowed for type GeneralizedTime.*"));
    }

}
