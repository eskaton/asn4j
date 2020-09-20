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

import ch.eskaton.asn4j.parser.ParserException;
import ch.eskaton.asn4j.parser.ast.values.ObjectIdentifierValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static ch.eskaton.asn4j.compiler.CompilerTestUtils.getCompiledValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class ObjectIdentifierValueCompilerTest {

    @Test
    void testResolveNumberForm() throws IOException, ParserException {
        var body = """
                testObjectIdentifier1 OBJECT IDENTIFIER ::= { 1 3 6 1 }
                 """;

        var compiledValue = getCompiledValue(body, ObjectIdentifierValue.class, "testObjectIdentifier1");
        var value = (ObjectIdentifierValue) compiledValue.getValue();

        assertThat(value.getComponents().size(), equalTo(4));
        assertThat(value.getComponents().get(0).getId(), equalTo(1));
        assertThat(value.getComponents().get(1).getId(), equalTo(3));
        assertThat(value.getComponents().get(2).getId(), equalTo(6));
        assertThat(value.getComponents().get(3).getId(), equalTo(1));
    }

    @Test
    void testResolveNameAndNumberForm() throws IOException, ParserException {
        var body = """
                testObjectIdentifier1 OBJECT IDENTIFIER ::= { iso(1) identified-organization(3) dod(6) internet(1) }
                 """;

        var compiledValue = getCompiledValue(body, ObjectIdentifierValue.class, "testObjectIdentifier1");
        var value = (ObjectIdentifierValue) compiledValue.getValue();

        assertThat(value.getComponents().size(), equalTo(4));
        assertThat(value.getComponents().get(0).getId(), equalTo(1));
        assertThat(value.getComponents().get(0).getName(), equalTo("iso"));
        assertThat(value.getComponents().get(1).getId(), equalTo(3));
        assertThat(value.getComponents().get(1).getName(), equalTo("identified-organization"));
        assertThat(value.getComponents().get(2).getId(), equalTo(6));
        assertThat(value.getComponents().get(2).getName(), equalTo("dod"));
        assertThat(value.getComponents().get(3).getId(), equalTo(1));
        assertThat(value.getComponents().get(3).getName(), equalTo("internet"));
    }

    @Test
    void testResolveNameAndNumberFormWithDefinedValues() throws IOException, ParserException {
        var body = """
                dod INTEGER ::= 6
                testObjectIdentifier1 OBJECT IDENTIFIER ::= { iso(1) identified-organization(3) dod(dod) internet(1) }
                 """;

        var compiledValue = getCompiledValue(body, ObjectIdentifierValue.class, "testObjectIdentifier1");
        var value = (ObjectIdentifierValue) compiledValue.getValue();

        assertThat(value.getComponents().size(), equalTo(4));
        assertThat(value.getComponents().get(0).getId(), equalTo(1));
        assertThat(value.getComponents().get(0).getName(), equalTo("iso"));
        assertThat(value.getComponents().get(1).getId(), equalTo(3));
        assertThat(value.getComponents().get(1).getName(), equalTo("identified-organization"));
        assertThat(value.getComponents().get(2).getId(), equalTo(6));
        assertThat(value.getComponents().get(2).getName(), equalTo("dod"));
        assertThat(value.getComponents().get(3).getId(), equalTo(1));
        assertThat(value.getComponents().get(3).getName(), equalTo("internet"));
    }

    @Test
    void testResolveNumberFormWithDefinedValues() throws IOException, ParserException {
        var body = """
                iso INTEGER ::= 1                                                                              
                identified-organization INTEGER ::= 3                                                                              
                dod INTEGER ::= 6
                internet INTEGER ::= 1
                testObjectIdentifier1 OBJECT IDENTIFIER ::= { iso identified-organization dod internet }
                 """;

        var compiledValue = getCompiledValue(body, ObjectIdentifierValue.class, "testObjectIdentifier1");
        var value = (ObjectIdentifierValue) compiledValue.getValue();

        assertThat(value.getComponents().size(), equalTo(4));
        assertThat(value.getComponents().get(0).getId(), equalTo(1));
        assertThat(value.getComponents().get(1).getId(), equalTo(3));
        assertThat(value.getComponents().get(2).getId(), equalTo(6));
        assertThat(value.getComponents().get(3).getId(), equalTo(1));
    }

    @Test
    void testResolveOIDDefinedValue() throws IOException, ParserException {
        var body = """
                testObjectIdentifier1 OBJECT IDENTIFIER ::= { 1 3 6 1 }
                testObjectIdentifier2 OBJECT IDENTIFIER ::= { testObjectIdentifier1 5 }
                 """;

        var compiledValue = getCompiledValue(body, ObjectIdentifierValue.class, "testObjectIdentifier2");
        var value = (ObjectIdentifierValue) compiledValue.getValue();

        assertThat(value.getComponents().size(), equalTo(5));
        assertThat(value.getComponents().get(0).getId(), equalTo(1));
        assertThat(value.getComponents().get(1).getId(), equalTo(3));
        assertThat(value.getComponents().get(2).getId(), equalTo(6));
        assertThat(value.getComponents().get(3).getId(), equalTo(1));
        assertThat(value.getComponents().get(4).getId(), equalTo(5));
    }

    @Test
    void testResolveReference() throws IOException, ParserException {
        var body = """
                testObjectIdentifier1 OBJECT IDENTIFIER ::= { 1 3 6 1 }
                testObjectIdentifier2 OBJECT IDENTIFIER ::= testObjectIdentifier1
                 """;

        var compiledValue = getCompiledValue(body, ObjectIdentifierValue.class, "testObjectIdentifier2");
        var value = (ObjectIdentifierValue) compiledValue.getValue();

        assertThat(value.getComponents().size(), equalTo(4));
        assertThat(value.getComponents().get(0).getId(), equalTo(1));
        assertThat(value.getComponents().get(1).getId(), equalTo(3));
        assertThat(value.getComponents().get(2).getId(), equalTo(6));
        assertThat(value.getComponents().get(3).getId(), equalTo(1));
    }

    @ParameterizedTest(name = "[{index}] {2}")
    @DisplayName("Test Root arcs according to X.660 A.2")
    @MethodSource("provideRootArcs")
    void testRootArcs(String body, int id, String description) throws IOException, ParserException {
        var compiledValue = getCompiledValue(body, ObjectIdentifierValue.class, "testObjectIdentifier");
        var value = (ObjectIdentifierValue) compiledValue.getValue();

        assertThat(value.getComponents().size(), equalTo(2));
        assertThat(value.getComponents().get(0).getId(), equalTo(id));
        assertThat(value.getComponents().get(1).getId(), equalTo(12));
    }

    private static Stream<Arguments> provideRootArcs() {
        return Stream.of(
                Arguments.of("testObjectIdentifier OBJECT IDENTIFIER ::= { itu-t test(12) }", 0, "itu-t"),
                Arguments.of("testObjectIdentifier OBJECT IDENTIFIER ::= { ccitt test(12) }", 0, "ccitt"),
                Arguments.of("testObjectIdentifier OBJECT IDENTIFIER ::= { iso test(12) }", 1, "iso"),
                Arguments.of("testObjectIdentifier OBJECT IDENTIFIER ::= { joint-iso-itu-t test(12) }", 2, "joint-iso-itu-t"),
                Arguments.of("testObjectIdentifier OBJECT IDENTIFIER ::= { joint-iso-ccitt test(12) }", 2, "joint-iso-ccitt"));
    }

}
