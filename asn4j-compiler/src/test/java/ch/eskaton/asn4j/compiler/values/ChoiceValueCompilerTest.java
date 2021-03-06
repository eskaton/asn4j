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
import ch.eskaton.asn4j.parser.ast.values.BooleanValue;
import ch.eskaton.asn4j.parser.ast.values.ChoiceValue;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static ch.eskaton.asn4j.compiler.CompilerTestUtils.getCompiledValue;
import static ch.eskaton.asn4j.compiler.CompilerTestUtils.testModule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChoiceValueCompilerTest {

    private static final String MODULE_NAME = "TEST-MODULE";

    @Test
    void testChoiceValue() throws IOException, ParserException {
        var body = """
                TestChoice ::= CHOICE {a BOOLEAN, b INTEGER}
                choice TestChoice ::= a:TRUE
                """;

        var compiledValue = getCompiledValue(body, ChoiceValue.class, "choice");
        var value = (ChoiceValue) compiledValue.getValue();

        assertEquals("a", value.getId());
        assertTrue(value.getValue() instanceof BooleanValue);
        assertEquals(true, ((BooleanValue) value.getValue()).getValue());
    }

    @Test
    void testChoiceValueReference() throws IOException, ParserException {
        var body = """
                TestChoice ::= CHOICE {a BOOLEAN, b INTEGER}
                choice1 TestChoice ::= b:12
                choice2 TestChoice ::= choice1
                """;

        var compiledValue = getCompiledValue(body, ChoiceValue.class, "choice2");
        var value = (ChoiceValue) compiledValue.getValue();

        assertEquals("b", value.getId());
        assertTrue(value.getValue() instanceof IntegerValue);
        assertEquals(12, ((IntegerValue) value.getValue()).getValue().intValue());
    }

    @Test
    void testChoiceValueInvalid() {
        var body = """
                TestChoice ::= CHOICE {a BOOLEAN, b INTEGER}
                choice TestChoice ::= TRUE
                """;

        testModule(MODULE_NAME, body, CompilerException.class, ".*Invalid CHOICE value: TRUE.*");
    }

}
