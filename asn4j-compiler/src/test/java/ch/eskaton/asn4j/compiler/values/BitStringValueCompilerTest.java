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
import ch.eskaton.asn4j.parser.ast.values.BitStringValue;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static ch.eskaton.asn4j.compiler.CompilerTestUtils.getCompiledValue;
import static ch.eskaton.asn4j.compiler.CompilerTestUtils.testModule;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BitStringValueCompilerTest {

    private static final String MODULE_NAME = "TEST-MODULE";

    @Test
    void testBitStringValueBinary() throws IOException, ParserException {
        var body = """
                bitString BIT STRING ::= '0101'B
                """;

        var compiledValue = getCompiledValue(body, BitStringValue.class, "bitString");
        var value = (BitStringValue) compiledValue.getValue();

        assertEquals(4, value.getUnusedBits());
        assertArrayEquals(new byte[] { 0x05 }, value.getByteValue());
    }

    @Test
    void testBitStringValueHex() throws IOException, ParserException {
        var body = """
                bitString BIT STRING ::= '05'H
                """;

        var compiledValue = getCompiledValue(body, BitStringValue.class, "bitString");
        var value = (BitStringValue) compiledValue.getValue();

        assertEquals(0, value.getUnusedBits());
        assertArrayEquals(new byte[] { 0x05 }, value.getByteValue());
    }

    @Test
    void testBitStringEmptyValue() throws IOException, ParserException {
        var body = """
                BitString ::= BIT STRING { a(1), b(2) }
                bitString BitString ::= {}
                """;

        var compiledValue = getCompiledValue(body, BitStringValue.class, "bitString");
        var value = (BitStringValue) compiledValue.getValue();

        assertEquals(0, value.getUnusedBits());
        assertArrayEquals(new byte[] {}, value.getByteValue());
    }

    @Test
    void testBitStringValueNamedBit() throws IOException, ParserException {
        var body = """
                BitString ::= BIT STRING { a(1), b(2) }
                bitString BitString ::= { a, b }
                """;

        var compiledValue = getCompiledValue(body, BitStringValue.class, "bitString");
        var value = (BitStringValue) compiledValue.getValue();

        assertEquals(5, value.getUnusedBits());
        assertArrayEquals(new byte[] { 0x60 }, value.getByteValue());
    }

    @Test
    void testBitStringValueNamedBitWithReference() throws IOException, ParserException {
        var body = """
                c INTEGER ::= 3
                BitString ::= BIT STRING { a(1), b(c) }
                bitString BitString ::= { a, b }
                """;

        var compiledValue = getCompiledValue(body, BitStringValue.class, "bitString");
        var value = (BitStringValue) compiledValue.getValue();

        assertEquals(4, value.getUnusedBits());
        assertArrayEquals(new byte[] { 0x50 }, value.getByteValue());
    }

    @Test
    void testBitStringValueInvalid() {
        var body = """
                bitString BIT STRING ::= 21
                """;

        testModule(MODULE_NAME, body, CompilerException.class, ".*Invalid BIT STRING value: 21.*");
    }

    @Test
    void testBitStringValueNamedBitWithInvalidReference() {
        var body = """
                BitString ::= BIT STRING { a(1), b(2) }
                bitString BitString ::= { a, c }
                """;

        testModule(MODULE_NAME, body, CompilerException.class, ".*BitString has no named bit 'c'.*");
    }

}
