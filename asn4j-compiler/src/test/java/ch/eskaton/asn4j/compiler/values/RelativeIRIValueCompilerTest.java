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
import ch.eskaton.asn4j.parser.ast.values.RelativeIRIValue;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static ch.eskaton.asn4j.compiler.CompilerTestUtils.getCompiledValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class RelativeIRIValueCompilerTest {

    @Test
    void testResolveValue() throws IOException, ParserException {
        var body = """
                testRelativeOidIri1 RELATIVE-OID-IRI ::= "Registration-Authority/19785.CBEFF/Organizations"
                """;

        var compiledValue = getCompiledValue(body, RelativeIRIValue.class, "testRelativeOidIri1");
        var value = (RelativeIRIValue) compiledValue.getValue();

        assertThat(value.getArcIdentifiers().size(), equalTo(3));
        assertThat(value.getArcIdentifiers().get(0).getText(), equalTo("Registration-Authority"));
        assertThat(value.getArcIdentifiers().get(1).getText(), equalTo("19785.CBEFF"));
        assertThat(value.getArcIdentifiers().get(2).getText(), equalTo("Organizations"));
    }

    @Test
    void testResolveReference() throws IOException, ParserException {
        var body = """
                testRelativeOidIri1 RELATIVE-OID-IRI ::= "Registration-Authority/19785.CBEFF/Organizations"
                testRelativeOidIri2 RELATIVE-OID-IRI ::= testRelativeOidIri1
                """;

        var compiledValue = getCompiledValue(body, RelativeIRIValue.class, "testRelativeOidIri1");
        var value = (RelativeIRIValue) compiledValue.getValue();

        assertThat(value.getArcIdentifiers().size(), equalTo(3));
        assertThat(value.getArcIdentifiers().get(0).getText(), equalTo("Registration-Authority"));
        assertThat(value.getArcIdentifiers().get(1).getText(), equalTo("19785.CBEFF"));
        assertThat(value.getArcIdentifiers().get(2).getText(), equalTo("Organizations"));
    }

}
