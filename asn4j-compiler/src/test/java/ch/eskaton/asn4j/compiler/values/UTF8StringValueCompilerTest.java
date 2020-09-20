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
import ch.eskaton.asn4j.parser.ast.values.UTF8StringValue;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static ch.eskaton.asn4j.compiler.CompilerTestUtils.getCompiledValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UTF8StringValueCompilerTest {

    @Test
    void testResolveCStringValue() throws IOException, ParserException {
        var body = """
                testUTF8String1 UTF8String ::= "test"
                """;

        var compiledValue = getCompiledValue(body, UTF8StringValue.class, "testUTF8String1");
        var value = (UTF8StringValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("test"));
    }

    @Test
    void testResolveReference() throws IOException, ParserException {
        var body = """
                testUTF8String1 UTF8String ::= "test"
                testUTF8String2 UTF8String ::= testUTF8String1
                """;

        var compiledValue = getCompiledValue(body, UTF8StringValue.class, "testUTF8String1");
        var value = (UTF8StringValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("test"));
    }

    @Test
    void testResolveCStringValueWithTypeReference() throws IOException, ParserException {
        var body = """
                TestUTF8String1 ::= UTF8String
                TestUTF8String2 ::= TestUTF8String1
                testUTF8String1 TestUTF8String2 ::= "test"
                """;

        var compiledValue = getCompiledValue(body, UTF8StringValue.class, "testUTF8String1");
        var value = (UTF8StringValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("test"));
    }

    @Test
    void testResolveReferenceWithTypeReference() throws IOException, ParserException {
        var body = """
                TestUTF8String1 ::= UTF8String
                TestUTF8String2 ::= TestUTF8String1
                testUTF8String1 TestUTF8String2 ::= "test"
                testUTF8String2 UTF8String ::= testUTF8String1
                """;

        var compiledValue = getCompiledValue(body, UTF8StringValue.class, "testUTF8String2");
        var value = (UTF8StringValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("test"));
    }

    @Test
    void testResolveReferenceWithInvalidTypeReference() {
        var body = """
                TestUTF8String1 ::= NumericString
                testUTF8String1 TestUTF8String1 ::= "test"
                testUTF8String2 UTF8String ::= testUTF8String1
                """;

        assertThrows(CompilerException.class,
                () -> getCompiledValue(body, UTF8StringValue.class, "testUTF8String2"));
    }

    @Test
    void testResolveCharacterStringList() throws IOException, ParserException {
        var body = """
                testUTF8String1 UTF8String ::= {"abc", "def"}
                """;

        var compiledValue = getCompiledValue(body, UTF8StringValue.class, "testUTF8String1");
        var value = (UTF8StringValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("abcdef"));
    }

    @Test
    void testResolveCharacterStringListWithReference() throws IOException, ParserException {
        var body = """
                testUTF8String1 UTF8String ::= "abc"
                testUTF8String2 UTF8String ::= {testUTF8String1, "def"}
                testUTF8String3 UTF8String ::= {testUTF8String2, "ghi"}
                """;

        var compiledValue = getCompiledValue(body, UTF8StringValue.class, "testUTF8String3");
        var value = (UTF8StringValue) compiledValue.getValue();

        assertThat(value.getValue(), equalTo("abcdefghi"));
    }

    @Test
    void testResolveCharacterStringListWithMissingReference() {
        var body = """
                testUTF8String2 UTF8String ::= {testUTF8String1, "def"}
                """;
        assertThrows(CompilerException.class,
                () -> getCompiledValue(body, UTF8StringValue.class, "testUTF8String2"));
    }

    @Test
    void testResolveCharacterStringListWithInvalidReference() {
        var body = """
                testNumericString1 NumericString ::= "123"
                testUTF8String2 UTF8String ::= {testNumericString1, "def"}
                """;
        assertThrows(CompilerException.class,
                () -> getCompiledValue(body, UTF8StringValue.class, "testUTF8String2"));
    }

    @Test
    void testResolveInvalidTupleValue() {
        var body = """
                testUTF8String1 UTF8String ::= {1, 1}
                """;
        var exception = assertThrows(CompilerException.class,
                () -> getCompiledValue(body, UTF8StringValue.class, "testUTF8String1"));

        assertThat(exception.getMessage(), matchesPattern(".*Tuple values not allowed for type UTF8String.*"));
    }

    @Test
    void testResolveQuadrupleValue() throws IOException, ParserException {
        var body = """
                testUTF8String1 UTF8String ::= {0, 1, 212, 175}
                """;

        var compiledValue = getCompiledValue(body, UTF8StringValue.class, "testUTF8String1");

        var value = (UTF8StringValue) compiledValue.getValue();
        assertThat(value.getValue(), equalTo("\uD835\uDCAF"));
    }

}
