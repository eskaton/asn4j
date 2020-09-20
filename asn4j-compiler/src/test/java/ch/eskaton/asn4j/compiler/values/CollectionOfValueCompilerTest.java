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
import ch.eskaton.asn4j.parser.ast.values.CollectionOfValue;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static ch.eskaton.asn4j.compiler.CompilerTestUtils.MODULE_NAME;
import static ch.eskaton.asn4j.compiler.CompilerTestUtils.getCompiledValue;
import static ch.eskaton.asn4j.compiler.CompilerTestUtils.testModule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CollectionOfValueCompilerTest {

    @Test
    void testCollectionOfValue() throws IOException, ParserException {
        var body = """
                SeqOf ::= SEQUENCE OF INTEGER
                seqOf SeqOf ::= {1, 2, 3}
                """;

        var compiledValue = getCompiledValue(body, CollectionOfValue.class, "seqOf");
        var value = (CollectionOfValue) compiledValue.getValue();

        assertEquals(3, value.getValues().size());
    }

    @Test
    void testCollectionOfValueEmpty() throws IOException, ParserException {
        var body = """
                SeqOf ::= SEQUENCE OF INTEGER
                seqOf SeqOf ::= {}
                """;

        var compiledValue = getCompiledValue(body, CollectionOfValue.class, "seqOf");
        var value = (CollectionOfValue) compiledValue.getValue();

        assertTrue(value.getValues().isEmpty());
    }

    @Test
    void testCollectionOfValueWithTypeReference() throws IOException, ParserException {
        var body = """
                SeqOf1 ::= SEQUENCE OF INTEGER
                SeqOf2 ::= SeqOf1
                seqOf SeqOf2 ::= {1, 2}
                """;

        var compiledValue = getCompiledValue(body, CollectionOfValue.class, "seqOf");
        var value = (CollectionOfValue) compiledValue.getValue();

        assertEquals(2, value.getValues().size());
    }


    @Test
    void testCollectionOfValueInvalid() {
        var body = """
                SeqOf ::= SEQUENCE OF INTEGER
                seqOf SeqOf ::= TRUE
                """;

        testModule(MODULE_NAME, body, CompilerException.class, ".*Invalid SEQUENCE OF value: TRUE.*");
    }

}
