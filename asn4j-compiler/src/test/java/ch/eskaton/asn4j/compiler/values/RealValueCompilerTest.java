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
import ch.eskaton.asn4j.parser.ast.values.RealValue;
import org.hamcrest.text.MatchesPattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.math.BigDecimal;

import static ch.eskaton.asn4j.compiler.CompilerTestUtils.getCompiledValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RealValueCompilerTest {

    @Test
    void testNumericValue() throws IOException, ParserException {
        var body = """
                testRealValue1 REAL ::= 12.78
                """;

        var compiledValue = getCompiledValue(body, RealValue.class, "testRealValue1");
        var value = (RealValue) compiledValue.getValue();

        assertThat(value.getRealType(), equalTo(RealValue.RealType.NORMAL));
        assertThat(value.getValue(), equalTo(new BigDecimal("12.78")));
    }

    @Test
    void testNumericValueReference() throws IOException, ParserException {
        var body = """
                testRealValue1 REAL ::= 12.78
                testRealValue2 REAL ::= testRealValue1
                """;

        var compiledValue = getCompiledValue(body, RealValue.class, "testRealValue1");
        var value = (RealValue) compiledValue.getValue();

        assertThat(value.getRealType(), equalTo(RealValue.RealType.NORMAL));
        assertThat(value.getValue(), equalTo(new BigDecimal("12.78")));
    }

    @Test
    void testNaN() throws IOException, ParserException {
        var body = """
                testRealValue1 REAL ::= NOT-A-NUMBER
                """;

        var compiledValue = getCompiledValue(body, RealValue.class, "testRealValue1");
        var value = (RealValue) compiledValue.getValue();

        assertThat(value.getRealType(), equalTo(RealValue.RealType.NAN));
    }

    @Test
    void testMinusInfinity() throws IOException, ParserException {
        var body = """
                testRealValue1 REAL ::= MINUS-INFINITY
                """;

        var compiledValue = getCompiledValue(body, RealValue.class, "testRealValue1");
        var value = (RealValue) compiledValue.getValue();

        assertThat(value.getRealType(), equalTo(RealValue.RealType.NEGATIVE_INF));
    }

    @Test
    void testPlusInfinity() throws IOException, ParserException {
        var body = """
                testRealValue1 REAL ::= PLUS-INFINITY
                """;

        var compiledValue = getCompiledValue(body, RealValue.class, "testRealValue1");
        var value = (RealValue) compiledValue.getValue();

        assertThat(value.getRealType(), equalTo(RealValue.RealType.POSITIVE_INF));
    }

    @Test
    void testSequenceValue() throws IOException, ParserException {
        var body = """
                testRealValue1 REAL ::= { mantissa 12, base 10, exponent 2 }
                """;

        var compiledValue = getCompiledValue(body, RealValue.class, "testRealValue1");
        var value = (RealValue) compiledValue.getValue();

        assertThat(value.getRealType(), equalTo(RealValue.RealType.SPECIAL));
        assertThat(value.getMantissa(), equalTo(12L));
        assertThat(value.getBase(), equalTo(10));
        assertThat(value.getExponent(), equalTo(2));
    }

    @Test
    void testSequenceValueReference() throws IOException, ParserException {
        var body = """
                testRealValue1 REAL ::= { mantissa 12, base 10, exponent 2 }
                testRealValue2 REAL ::= testRealValue1
                """;

        var compiledValue = getCompiledValue(body, RealValue.class, "testRealValue2");
        var value = (RealValue) compiledValue.getValue();

        assertThat(value.getRealType(), equalTo(RealValue.RealType.SPECIAL));
        assertThat(value.getMantissa(), equalTo(12L));
        assertThat(value.getBase(), equalTo(10));
        assertThat(value.getExponent(), equalTo(2));
    }

    @Test
    void testSequenceValueWithReference() throws IOException, ParserException {
        var body = """
                testMantissa INTEGER ::= 12
                testRealValue1 REAL ::= { mantissa testMantissa, base 10, exponent 2 }
                """;

        var compiledValue = getCompiledValue(body, RealValue.class, "testRealValue1");
        var value = (RealValue) compiledValue.getValue();

        assertThat(value.getRealType(), equalTo(RealValue.RealType.SPECIAL));
        assertThat(value.getMantissa(), equalTo(12L));
        assertThat(value.getBase(), equalTo(10));
        assertThat(value.getExponent(), equalTo(2));
    }

    @Test
    void testSequenceValueMantissaMissing() {
        var body = """
                testRealValue1 REAL ::= { base 10, exponent 2 }
                """;

        var exception = assertThrows(CompilerException.class,
                () -> getCompiledValue(body, RealValue.class, "testRealValue1"));

        assertThat(exception.getMessage(), MatchesPattern.matchesPattern(".*Incomplete REAL value.*"));
    }

    @Test
    void testSequenceValueBaseMissing() {
        var body = """
                testRealValue1 REAL ::= { mantissa 12, exponent 2 }
                """;

        var exception = assertThrows(CompilerException.class,
                () -> getCompiledValue(body, RealValue.class, "testRealValue1"));

        assertThat(exception.getMessage(), MatchesPattern.matchesPattern(".*Incomplete REAL value.*"));
    }

    @Test
    void testSequenceValueExponentMissing() {
        var body = """
                testRealValue1 REAL ::= { mantissa 12, base 10 }
                """;

        var exception = assertThrows(CompilerException.class,
                () -> getCompiledValue(body, RealValue.class, "testRealValue1"));

        assertThat(exception.getMessage(), MatchesPattern.matchesPattern(".*Incomplete REAL value.*"));
    }

    @Test
    void testSequenceValueInvalidComponent() {
        var body = """
                testRealValue1 REAL ::= { mantissa 12, base 2, abc 2 }
                """;

        var exception = assertThrows(CompilerException.class,
                () -> getCompiledValue(body, RealValue.class, "testRealValue1"));

        assertThat(exception.getMessage(), MatchesPattern.matchesPattern(".*Unknown component in REAL value.*"));
    }

    @Test
    void testSequenceValueInvalidBase() {
        var body = """
                testRealValue1 REAL ::= { mantissa 12, base 3, exponent 2 }
                """;

        var exception = assertThrows(CompilerException.class,
                () -> getCompiledValue(body, RealValue.class, "testRealValue1"));

        assertThat(exception.getMessage(), MatchesPattern.matchesPattern(".*Invalid base '3' in REAL value.*"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "-9223372036854775809", "9223372036854775808" })
    void testSequenceValueMantissaInvalid(String mantissa) {
        var body = """
                testRealValue1 REAL ::= { mantissa %s, base 2, exponent 2 }
                """.formatted(mantissa);

        var exception = assertThrows(CompilerException.class,
                () -> getCompiledValue(body, RealValue.class, "testRealValue1"));

        assertThat(exception.getMessage(), MatchesPattern.matchesPattern(".*Mantissa in REAL value is out of range.*"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "-9223372036854775808", "9223372036854775807" })
    void testSequenceValueMaxMantissaRange(String mantissa) throws IOException, ParserException {
        var body = """
                testRealValue1 REAL ::= { mantissa %s, base 2, exponent 2 }
                """.formatted(mantissa);

        var compiledValue = getCompiledValue(body, RealValue.class, "testRealValue1");
        var value = (RealValue) compiledValue.getValue();

        assertThat(value.getMantissa(), equalTo(Long.parseLong(mantissa)));
    }

    @ParameterizedTest
    @ValueSource(strings = { "-1000000000", "1000000000" })
    void testSequenceValueExponentInvalid(String exponent) {
        var body = """
                testRealValue1 REAL ::= { mantissa 12, base 2, exponent %s }
                """.formatted(exponent);

        var exception = assertThrows(CompilerException.class,
                () -> getCompiledValue(body, RealValue.class, "testRealValue1"));

        assertThat(exception.getMessage(), MatchesPattern.matchesPattern(".*Exponent in REAL value is out of range.*"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "-999999999", "999999999" })
    void testSequenceValueMaxExponentRange(String exponent) throws IOException, ParserException {
        var body = """
                testRealValue1 REAL ::= { mantissa 12, base 2, exponent %s }
                """.formatted(exponent);

        var compiledValue = getCompiledValue(body, RealValue.class, "testRealValue1");
        var value = (RealValue) compiledValue.getValue();

        assertThat(value.getExponent(), equalTo(Integer.parseInt(exponent)));
    }

}
