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
import ch.eskaton.asn4j.parser.ast.values.VideotexStringValue;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static ch.eskaton.asn4j.compiler.CompilerTestUtils.getCompiledValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VideotexStringValueCompilerTest {

    @Test
    void testResolveCStringValue() throws IOException, ParserException {
        var body = """
                testVideotexString1 VideotexString ::= "abcäöϕ"
                """;

        var compiledValue = getCompiledValue(body, VideotexStringValue.class, "testVideotexString1");
        var value = (VideotexStringValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("abcäöϕ"));
    }

    @Test
    void testResolveReference() throws IOException, ParserException {
        var body = """
                testVideotexString1 VideotexString ::= "abcäöϕ"
                testVideotexString2 VideotexString ::= testVideotexString1
                """;

        var compiledValue = getCompiledValue(body, VideotexStringValue.class, "testVideotexString2");
        var value = (VideotexStringValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("abcäöϕ"));
    }

    @Test
    void testResolveCStringValueWithTypeReference() throws IOException, ParserException {
        var body = """
                TestVideotexString1 ::= VideotexString
                TestVideotexString2 ::= TestVideotexString1
                testVideotexString1 TestVideotexString2 ::= "abcäöϕ"
                """;
        var compiledValue = getCompiledValue(body, VideotexStringValue.class, "testVideotexString1");
        var value = (VideotexStringValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("abcäöϕ"));
    }

    @Test
    void testResolveReferenceWithTypeReference() throws IOException, ParserException {
        var body = """
                TestVideotexString1 ::= VideotexString
                TestVideotexString2 ::= TestVideotexString1
                testVideotexString1 TestVideotexString2 ::= "abcäöϕ"
                testVideotexString2 VideotexString ::= testVideotexString1
                """;

        var compiledValue = getCompiledValue(body, VideotexStringValue.class, "testVideotexString2");
        var value = (VideotexStringValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("abcäöϕ"));
    }

    @Test
    void testResolveReferenceWithInvalidTypeReference() {
        var body = """
                TestVideotexString1 ::= VisibleString
                testVideotexString1 TestVideotexString1 ::= "abcäöϕ"
                testVideotexString2 VideotexString ::= testVideotexString1
                """;

        assertThrows(CompilerException.class,
                () -> getCompiledValue(body, VideotexStringValue.class, "testVideotexString2"));
    }

    @Test
    void testResolveCharacterStringList() throws IOException, ParserException {
        var body = """
                testVideotexString1 VideotexString ::= {"abc", "def"}
                """;

        var compiledValue = getCompiledValue(body, VideotexStringValue.class, "testVideotexString1");
        var value = (VideotexStringValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("abcdef"));
    }

    @Test
    void testResolveCharacterStringListWithReference() throws IOException, ParserException {
        var body = """
                testVideotexString1 VideotexString ::= "123"
                testVideotexString2 VideotexString ::= {testVideotexString1, "456"}
                testVideotexString3 VideotexString ::= {testVideotexString2, "789"}
                """;
        var compiledValue = getCompiledValue(body, VideotexStringValue.class, "testVideotexString3");
        var value = (VideotexStringValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("123456789"));
    }

    @Test
    void testResolveCharacterStringListWithMissingReference() {
        var body = """
                testVideotexString2 VideotexString ::= {testVideotexString1, "123"}
                """;
        assertThrows(CompilerException.class,
                () -> getCompiledValue(body, VideotexStringValue.class, "testVideotexString2"));
    }

    @Test
    void testResolveCharacterStringListWithInvalidReference() {
        var body = """
                testVideotexString1 VisibleString ::= "123"
                testVideotexString2 VideotexString ::= {testVideotexString1, "456"}
                """;
        assertThrows(CompilerException.class,
                () -> getCompiledValue(body, VideotexStringValue.class, "testVideotexString2"));
    }

    @Test
    void testResolveInvalidTupleValue() {
        var body = """
                testVideotexString1 VideotexString ::= {1, 1}
                """;
        var exception = assertThrows(CompilerException.class,
                () -> getCompiledValue(body, VideotexStringValue.class, "testVideotexString1"));

        assertThat(exception.getMessage(), matchesPattern(".*Tuple values not allowed for type VideotexString.*"));
    }

    @Test
    void testResolveInvalidQuadrupleValue() {
        var body = """
                testVideotexString1 VideotexString ::= {1, 1, 1, 1}
                """;
        var exception = assertThrows(CompilerException.class,
                () -> getCompiledValue(body, VideotexStringValue.class, "testVideotexString1"));

        assertThat(exception.getMessage(), matchesPattern(".*Quadruple values not allowed for type VideotexString.*"));
    }

}
