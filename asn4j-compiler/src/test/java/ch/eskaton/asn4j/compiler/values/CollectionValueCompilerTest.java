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
import ch.eskaton.asn4j.parser.ast.values.CollectionValue;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.NamedValue;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static ch.eskaton.asn4j.compiler.CompilerTestUtils.MODULE_NAME;
import static ch.eskaton.asn4j.compiler.CompilerTestUtils.getCompiledValue;
import static ch.eskaton.asn4j.compiler.CompilerTestUtils.testModule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CollectionValueCompilerTest {

    @Test
    void testCollectionValue() throws IOException, ParserException {
        var body = """
                Seq ::= SEQUENCE {a INTEGER, b BOOLEAN}
                seq Seq ::= {a 12, b FALSE}
                """;

        var compiledValue = getCompiledValue(body, CollectionValue.class, "seq");
        var value = (CollectionValue) compiledValue.getValue();

        var namedValue1 = getNamedValue(value, "a");

        assertTrue(namedValue1.isPresent());
        assertEquals(12, ((IntegerValue) namedValue1.get().getValue()).getValue().intValue());

        var namedValue2 = getNamedValue(value, "b");

        assertTrue(namedValue2.isPresent());
        assertEquals(false, ((BooleanValue) namedValue2.get().getValue()).getValue());
    }

    @Test
    void testCollectionValueEmpty() throws IOException, ParserException {
        var body = """
                Seq ::= SEQUENCE {a INTEGER OPTIONAL, b BOOLEAN OPTIONAL}
                seq Seq ::= {}
                """;

        var compiledValue = getCompiledValue(body, CollectionValue.class, "seq");
        var value = (CollectionValue) compiledValue.getValue();

        assertTrue(value.getValues().isEmpty());
    }

    @Test
    void testCollectionValueWithTypeReference() throws IOException, ParserException {
        var body = """
                Seq1 ::= SEQUENCE {a INTEGER, b BOOLEAN}
                Seq2 ::= Seq1
                seq Seq2 ::= {a 12, b FALSE}
                """;

        var compiledValue = getCompiledValue(body, CollectionValue.class, "seq");
        var value = (CollectionValue) compiledValue.getValue();

        var namedValue1 = getNamedValue(value, "a");

        assertTrue(namedValue1.isPresent());
        assertEquals(12, ((IntegerValue) namedValue1.get().getValue()).getValue().intValue());

        var namedValue2 = getNamedValue(value, "b");

        assertTrue(namedValue2.isPresent());
        assertEquals(false, ((BooleanValue) namedValue2.get().getValue()).getValue());
    }

    @Test
    void testCollectionValueAmbiguous() throws IOException, ParserException {
        var body = """
                Seq ::= SEQUENCE {mantissa INTEGER, base INTEGER, exponent INTEGER}
                seq Seq ::= {mantissa 12, base 10, exponent 2}
                """;

        var compiledValue = getCompiledValue(body, CollectionValue.class, "seq");
        var value = (CollectionValue) compiledValue.getValue();

        var namedValue1 = getNamedValue(value, "mantissa");

        assertTrue(namedValue1.isPresent());
        assertEquals(12, ((IntegerValue) namedValue1.get().getValue()).getValue().intValue());

        var namedValue2 = getNamedValue(value, "base");

        assertTrue(namedValue2.isPresent());
        assertEquals(10, ((IntegerValue) namedValue2.get().getValue()).getValue().intValue());

        var namedValue3 = getNamedValue(value, "exponent");

        assertTrue(namedValue3.isPresent());
        assertEquals(2, ((IntegerValue) namedValue3.get().getValue()).getValue().intValue());
    }

    @Test
    void testCollectionValueInvalid() {
        var body = """
                Seq ::= SEQUENCE {a INTEGER, b BOOLEAN}
                seq Seq ::= TRUE
                """;

        testModule(MODULE_NAME, body, CompilerException.class, ".*Invalid SEQUENCE value: TRUE.*");
    }

    @Test
    void testCollectionValueInvalidComponentName() {
        var body = """
                Seq ::= SEQUENCE {a INTEGER, b BOOLEAN}
                seq Seq ::= {a 12, c TRUE}
                """;

        testModule(MODULE_NAME, body, CompilerException.class,
                ".*SEQUENCE value contains a component 'c' which isn't defined.*");
    }

    @Test
    void testCollectionValueInvalidComponentValue() {
        var body = """
                Seq ::= SEQUENCE {a INTEGER, b BOOLEAN}
                seq Seq ::= {a 12, b 23}
                """;

        testModule(MODULE_NAME, body, CompilerException.class,
                ".*Invalid BOOLEAN value: 23.*");
    }

    private Optional<NamedValue> getNamedValue(CollectionValue value, String name) {
        return value.getValues().stream()
                .filter(namedValue -> namedValue.getName().equals(name))
                .findFirst();
    }

}
