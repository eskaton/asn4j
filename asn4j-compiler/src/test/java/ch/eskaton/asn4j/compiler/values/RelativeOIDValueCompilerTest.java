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
import ch.eskaton.asn4j.parser.ast.values.RelativeOIDValue;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static ch.eskaton.asn4j.compiler.CompilerTestUtils.getCompiledValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class RelativeOIDValueCompilerTest {

    @Test
    void testResolveNumberForm() throws IOException, ParserException {
        var body = """
                testRelativeObjectIdentifier1 RELATIVE-OID ::= { 3 6 1 }
                 """;

        var compiledValue = getCompiledValue(body, RelativeOIDValue.class, "testRelativeObjectIdentifier1");
        var value = (RelativeOIDValue) compiledValue.getValue();

        assertThat(value.getComponents().size(), equalTo(3));
        assertThat(value.getComponents().get(0).getId(), equalTo(3));
        assertThat(value.getComponents().get(1).getId(), equalTo(6));
        assertThat(value.getComponents().get(2).getId(), equalTo(1));
    }

    @Test
    void testResolveNameAndNumberForm() throws IOException, ParserException {
        var body = """
                testRelativeObjectIdentifier1 RELATIVE-OID ::= { identified-organization(3) dod(6) internet(1) }
                 """;

        var compiledValue = getCompiledValue(body, RelativeOIDValue.class, "testRelativeObjectIdentifier1");
        var value = (RelativeOIDValue) compiledValue.getValue();

        assertThat(value.getComponents().size(), equalTo(3));
        assertThat(value.getComponents().get(0).getId(), equalTo(3));
        assertThat(value.getComponents().get(0).getName(), equalTo("identified-organization"));
        assertThat(value.getComponents().get(1).getId(), equalTo(6));
        assertThat(value.getComponents().get(1).getName(), equalTo("dod"));
        assertThat(value.getComponents().get(2).getId(), equalTo(1));
        assertThat(value.getComponents().get(2).getName(), equalTo("internet"));
    }

    @Test
    void testResolveNameAndNumberFormWithDefinedValues() throws IOException, ParserException {
        var body = """
                dod INTEGER ::= 6
                testRelativeObjectIdentifier1 RELATIVE-OID ::= { identified-organization(3) dod(dod) internet(1) }
                 """;

        var compiledValue = getCompiledValue(body, RelativeOIDValue.class, "testRelativeObjectIdentifier1");
        var value = (RelativeOIDValue) compiledValue.getValue();

        assertThat(value.getComponents().size(), equalTo(3));
        assertThat(value.getComponents().get(0).getId(), equalTo(3));
        assertThat(value.getComponents().get(1).getId(), equalTo(6));
        assertThat(value.getComponents().get(2).getId(), equalTo(1));
    }

    @Test
    void testResolveNumberFormWithDefinedValues() throws IOException, ParserException {
        var body = """
                identified-organization INTEGER ::= 3                                                                              
                dod INTEGER ::= 6
                internet INTEGER ::= 1
                testRelativeObjectIdentifier1 RELATIVE-OID ::= { identified-organization dod internet }
                 """;

        var compiledValue = getCompiledValue(body, RelativeOIDValue.class, "testRelativeObjectIdentifier1");
        var value = (RelativeOIDValue) compiledValue.getValue();

        assertThat(value.getComponents().size(), equalTo(3));
        assertThat(value.getComponents().get(0).getId(), equalTo(3));
        assertThat(value.getComponents().get(1).getId(), equalTo(6));
        assertThat(value.getComponents().get(2).getId(), equalTo(1));
    }

    @Test
    void testResolveRelativeOIDDefinedValue() throws IOException, ParserException {
        var body = """
                testRelativeObjectIdentifier1 RELATIVE-OID ::= { 3 6 1 }
                testRelativeObjectIdentifier2 RELATIVE-OID ::= { testRelativeObjectIdentifier1 5 }
                 """;

        var compiledValue = getCompiledValue(body, RelativeOIDValue.class, "testRelativeObjectIdentifier2");
        var value = (RelativeOIDValue) compiledValue.getValue();

        assertThat(value.getComponents().size(), equalTo(4));
        assertThat(value.getComponents().get(0).getId(), equalTo(3));
        assertThat(value.getComponents().get(1).getId(), equalTo(6));
        assertThat(value.getComponents().get(2).getId(), equalTo(1));
        assertThat(value.getComponents().get(3).getId(), equalTo(5));
    }

    @Test
    void testResolveReference() throws IOException, ParserException {
        var body = """
                testRelativeObjectIdentifier1 RELATIVE-OID ::= { 3 6 1 }
                testRelativeObjectIdentifier2 RELATIVE-OID ::= testRelativeObjectIdentifier1
                 """;

        var compiledValue = getCompiledValue(body, RelativeOIDValue.class, "testRelativeObjectIdentifier2");
        var value = (RelativeOIDValue) compiledValue.getValue();

        assertThat(value.getComponents().size(), equalTo(3));
        assertThat(value.getComponents().get(0).getId(), equalTo(3));
        assertThat(value.getComponents().get(1).getId(), equalTo(6));
        assertThat(value.getComponents().get(2).getId(), equalTo(1));
    }

}
