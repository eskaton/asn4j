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
import ch.eskaton.asn4j.compiler.CompilerImpl;
import ch.eskaton.asn4j.compiler.StringModuleSource;
import ch.eskaton.asn4j.parser.ParserException;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.commons.collections.Tuple2;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static ch.eskaton.asn4j.compiler.CompilerTestUtils.compilerConfig;
import static ch.eskaton.asn4j.compiler.CompilerTestUtils.getCompiledValue;
import static ch.eskaton.asn4j.compiler.CompilerTestUtils.testModule;
import static ch.eskaton.asn4j.test.TestUtils.module;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntegerValueCompilerTest {

    private static final String MODULE_NAME = "TEST-MODULE";

    @Test
    void testIntegerValue() throws IOException, ParserException {
        var body = """
                int INTEGER ::= 23
                """;

        var compiledValue = getCompiledValue(body, IntegerValue.class, "int");

        assertEquals(23, ((IntegerValue) compiledValue.getValue()).getValue().intValue());
    }


    @Test
    void testIntegerValueReference() throws IOException, ParserException {
        var body = """
                a INTEGER ::= 23
                int INTEGER ::= a
                """;

        var compiledValue = getCompiledValue(body, IntegerValue.class, "int");

        assertEquals(23, ((IntegerValue) compiledValue.getValue()).getValue().intValue());
    }

    @Test
    void testIntegerNamedValue() throws IOException, ParserException {
        var body = """
                Int ::= INTEGER { a(23) }
                int Int ::= a
                """;

        var compiledValue = getCompiledValue(body, IntegerValue.class, "int");

        assertEquals(23, ((IntegerValue) compiledValue.getValue()).getValue().intValue());
    }

    @Test
    void testIntegerNamedValueWithReference() throws IOException, ParserException {
        var body = """
                b INTEGER ::= 23
                Int ::= INTEGER { a(b) }
                int Int ::= a
                """;

        var compiledValue = getCompiledValue(body, IntegerValue.class, "int");

        assertEquals(23, ((IntegerValue) compiledValue.getValue()).getValue().intValue());
    }

    @Test
    void testIntegerNamedValueWithExternalReferences() throws IOException, ParserException {
        var body1 = """            
                Int ::= INTEGER { a(OTHER-MODULE.b) }
                int Int ::= a    
                """;
        var body2 = """
                EXPORTS b;
                                
                b INTEGER ::= 23
                """;

        var module1 = module(MODULE_NAME, body1);
        var module2 = module("OTHER-MODULE", body2);
        var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module1), Tuple2.of("OTHER-MODULE", module2));
        var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

        compiler.run();

        var ctx = compiler.getCompilerContext();
        var compiledValue = ctx.getCompiledModule(MODULE_NAME).getValues().get("int");

        assertNotNull(compiledValue);
        assertTrue(compiledValue.getValue() instanceof IntegerValue);
        assertEquals(23, ((IntegerValue) compiledValue.getValue()).getValue().intValue());
    }

    @Test
    void testIntegerValueInvalid() {
        var body = """
                int INTEGER ::= TRUE
                """;

        testModule(MODULE_NAME, body, CompilerException.class, ".*Invalid INTEGER value: TRUE.*");
    }

}
