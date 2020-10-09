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

package ch.eskaton.asn4j.parser;

import ch.eskaton.asn4j.parser.Lexer.Context;
import ch.eskaton.asn4j.parser.ast.values.StringValue;
import ch.eskaton.asn4j.parser.ast.values.TimeValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.stream.Stream;

import static ch.eskaton.asn4j.parser.NoPosition.NO_POSITION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LexerTest {

    @ParameterizedTest
    @MethodSource("provideComments")
    void testComment(String body) throws IOException, ParserException {
        var lexer = new Lexer(new ByteArrayInputStream(body.getBytes()));

        assertNull(lexer.nextToken(Context.NORMAL));
        assertTrue(lexer.isEOF());
    }

    @ParameterizedTest
    @MethodSource("provideMultilineComments")
    void testMultilineComment(String body) throws IOException, ParserException {
        var lexer = new Lexer(new ByteArrayInputStream(body.getBytes()));

        assertNull(lexer.nextToken(Context.NORMAL));
        assertTrue(lexer.isEOF());
    }

    @ParameterizedTest
    @MethodSource("provideTypeReference")
    void testTypeReference(String body, String typeReference) throws IOException, ParserException {
        var lexer = new Lexer(new ByteArrayInputStream(body.getBytes()));
        var token = lexer.nextToken(Context.NORMAL);

        assertEquals(Token.TokenType.TYPE_REFERENCE, token.getType());
        assertEquals(typeReference, token.getText());
    }

    @Test
    void testInvalidTypeReference() throws IOException {
        Lexer lexer = new Lexer(new ByteArrayInputStream("Type-".getBytes()));

        assertThrows(ParserException.class, () -> lexer.nextToken(Context.NORMAL));
    }

    @ParameterizedTest
    @MethodSource("provideIdentifier")
    void testIdentifier(String body, String identifier) throws IOException, ParserException {
        var lexer = new Lexer(new ByteArrayInputStream(body.getBytes()));
        var token = lexer.nextToken(Context.NORMAL);

        assertEquals(Token.TokenType.IDENTIFIER, token.getType());
        assertEquals(identifier, token.getText());
    }

    @Test
    void testInvalidIdentifier() throws IOException {
        var lexer = new Lexer(new ByteArrayInputStream("type-".getBytes()));

        assertThrows(ParserException.class, () -> lexer.nextToken(Context.NORMAL));
    }

    @ParameterizedTest
    @ValueSource(strings = { "T", "OBJECT-CLASS" })
    void testObjectClassReference(String objectClassReference) throws IOException, ParserException {
        var lexer = new Lexer(new ByteArrayInputStream(objectClassReference.getBytes()));
        var token = lexer.nextToken(Context.OBJECT_CLASS);

        assertEquals(Token.TokenType.OBJECT_CLASS_REFERENCE, token.getType());
        assertEquals(objectClassReference, token.getText());
    }

    @Test
    void testInvalidObjectClassReference() throws IOException {
        var lexer = new Lexer(new ByteArrayInputStream("Invalid".getBytes()));

        assertThrows(ParserException.class, () -> lexer.nextToken(Context.OBJECT_CLASS).getType());
    }

    @ParameterizedTest
    @ValueSource(strings = { "&T", "&Type-Field" })
    void testTypeFieldReference(String typeFieldReference) throws IOException, ParserException {
        var lexer = new Lexer(new ByteArrayInputStream(typeFieldReference.getBytes()));
        var token = lexer.nextToken(Context.TYPE_FIELD);

        assertEquals(Token.TokenType.TYPE_FIELD_REFERENCE, token.getType());
        assertEquals(typeFieldReference, token.getText());
    }

    @Test
    void testValueFieldReference() throws IOException, ParserException {
        var lexer = new Lexer(new ByteArrayInputStream("&value-field".getBytes()));
        var token = lexer.nextToken(Context.VALUE_FIELD);

        assertEquals(Token.TokenType.VALUE_FIELD_REFERENCE, token.getType());
        assertEquals("&value-field", token.getText());
    }

    @Test
    void testWord() throws IOException, ParserException {
        var lexer = new Lexer(new ByteArrayInputStream("A-WORD".getBytes()));
        var token = lexer.nextToken(Context.SYNTAX);

        assertEquals(Token.TokenType.WORD, token.getType());
        assertEquals("A-WORD", token.getText());
    }

    @ParameterizedTest
    @ValueSource(strings = { "0", "1", "42", "4711" })
    void testNumber(String number) throws IOException, ParserException {
        var lexer = new Lexer(new ByteArrayInputStream(number.getBytes()));
        var token = lexer.nextToken(Context.NORMAL);

        assertEquals(Token.TokenType.NUMBER, token.getType());
        assertEquals(number, token.getText());
    }

    @ParameterizedTest
    @ValueSource(strings = { "01", "2.", "3.41", "3e0", "3e12", "3e-12", "42.e-0", "42.e5", "42.e-5", "77.35E0",
            "77.35e+6", "77.35e-6" })
    void testRealNumber(String realNumber) throws IOException, ParserException {
        var lexer = new Lexer(new ByteArrayInputStream(realNumber.getBytes()));
        var token = lexer.nextToken(Context.NORMAL);

        assertEquals(Token.TokenType.REAL_NUMBER, token.getType());
        assertEquals(realNumber, token.getText());
    }

    @ParameterizedTest
    @ValueSource(strings = { "5e", "5e01" })
    void testInvalidRealNumber(String input) throws IOException {
        Lexer lexer = new Lexer(new ByteArrayInputStream(input.getBytes()));

        assertThrows(ParserException.class, () -> lexer.nextToken(Context.NORMAL));
    }

    @ParameterizedTest
    @MethodSource("provideBString")
    void testBString(String body, String bString) throws IOException, ParserException {
        var lexer = new Lexer(new ByteArrayInputStream(body.getBytes()));
        var token = lexer.nextToken(Context.NORMAL);

        assertEquals(Token.TokenType.B_STRING, token.getType());
        assertEquals(bString, token.getText());
    }

    @ParameterizedTest
    @MethodSource("provideHString")
    void testHString(String body, String hString) throws IOException, ParserException {
        var lexer = new Lexer(new ByteArrayInputStream(body.getBytes()));
        var token = lexer.nextToken(Context.NORMAL);

        assertEquals(Token.TokenType.H_STRING, token.getType());
        assertEquals(hString, token.getText());
    }

    @ParameterizedTest
    @MethodSource("provideCString")
    void testCString(String body, String cString) throws IOException, ParserException {
        var lexer = new Lexer(new ByteArrayInputStream(body.getBytes()));
        var token = lexer.nextToken(Context.NORMAL);

        assertEquals(Token.TokenType.C_STRING, token.getType());
        assertEquals(cString, getCString(token));
    }

    private static String getCString(Token token) {
        return new StringValue(NO_POSITION, token.getText(), ((StringToken) token).getFlags()).getCString();
    }

    @ParameterizedTest
    @MethodSource("provideSimpleString")
    void testSimpleString(String body, String simpleString) throws IOException, ParserException {
        var lexer = new Lexer(new ByteArrayInputStream(body.getBytes()));
        var token = lexer.nextToken(Context.NORMAL);

        assertEquals(Token.TokenType.C_STRING, token.getType());
        assertEquals(simpleString.replace("\"", ""), getSimpleString(token));
    }

    @ParameterizedTest
    @ValueSource(strings = { "\"\"", "\"\"\"\"", "\"ü\"" })
    void testInvalidSimpleString(String input) throws IOException, ParserException {
        var lexer = new Lexer(new ByteArrayInputStream(input.getBytes()));
        var token = lexer.nextToken(Context.NORMAL);

        assertEquals(Token.TokenType.C_STRING, token.getType());
        assertThrows(ParserException.class, () -> getSimpleString(token));
    }

    private static String getSimpleString(Token token) throws ParserException {
        return new StringValue(NO_POSITION, token.getText(), ((StringToken) token).getFlags()).getSimpleString();
    }

    @ParameterizedTest
    @ValueSource(strings = { "\".\"", "\"0123456789+-:.,/CDHMRPSTWYZ\"" })
    void testTString(String tString) throws IOException, ParserException {
        var lexer = new Lexer(new ByteArrayInputStream(tString.getBytes()));
        var token = lexer.nextToken(Context.NORMAL);

        assertEquals(Token.TokenType.C_STRING, token.getType());
        assertEquals(new TimeValue(NO_POSITION, tString.replace("\"", "")), getTimeValue(token));

    }

    @ParameterizedTest
    @ValueSource(strings = { "\"\"", "\"A\"", "\"0 1\"" })
    void testInvalidTString(String input) throws IOException, ParserException {
        Lexer lexer = new Lexer(new ByteArrayInputStream(input.getBytes()));
        Token token = lexer.nextToken(Context.NORMAL);

        assertEquals(Token.TokenType.C_STRING, token.getType());
        assertThrows(ParserException.class, () -> getTimeValue(token));
    }

    private static TimeValue getTimeValue(Token token) throws ParserException {
        return new StringValue(NO_POSITION, token.getText(), ((StringToken) token).getFlags()).getTimeValue();
    }

    @ParameterizedTest
    @MethodSource("provideTokens")
    void testTokens(String token, Token.TokenType type) throws IOException, ParserException {
        var lexer = new Lexer(new ByteArrayInputStream(token.getBytes()));

        assertEquals(type, lexer.nextToken(Context.NORMAL).getType());
    }

    @ParameterizedTest
    @MethodSource("provideKeywords")
    void testKeywords(String token, Token.TokenType type) throws IOException, ParserException {
        var lexer = new Lexer(new ByteArrayInputStream(token.getBytes()));

        assertEquals(type, lexer.nextToken(Context.NORMAL).getType());
    }

    @ParameterizedTest
    @ValueSource(strings = { "TAG", "XER", "PER" })
    void testEncodingReference(String encodingReference) throws IOException, ParserException {
        var lexer = new Lexer(new ByteArrayInputStream(encodingReference.getBytes()));
        var token = lexer.nextToken(Context.ENCODING);

        assertEquals(Token.TokenType.ENCODING_REFERENCE, token.getType());
        assertEquals(encodingReference, token.getText());
    }

    @ParameterizedTest
    @ValueSource(strings = { "ABC", "xer" })
    void testInvalidEncodingReference(String input) throws IOException {
        Lexer lexer = new Lexer(new ByteArrayInputStream(input.getBytes()));

        assertThrows(ParserException.class, () -> lexer.nextToken(Context.ENCODING));
    }

    private static Stream<Arguments> provideBString() {
        return Stream.of(
                Arguments.of("''B", ""),
                Arguments.of("'0'B", "0"),
                Arguments.of("'1'B", "1"),
                Arguments.of("'0101'B", "0101"),
                Arguments.of("'1 01 1'B", "1011")
        );
    }

    private static Stream<Arguments> provideHString() {
        return Stream.of(
                Arguments.of("''H", ""),
                Arguments.of("'0'H", "0"),
                Arguments.of("'0B0F'H", "0B0F"),
                Arguments.of("'F AD 5'H", "FAD5")
        );
    }

    private static Stream<Arguments> provideCString() {
        return Stream.of(
                Arguments.of("\"\"", ""),
                Arguments.of("\"\"\"\"", "\""),
                Arguments.of("\" \"", " "),
                Arguments.of("\"a\"\"b\"", "a\"b"),
                Arguments.of("\"abc def\"", "abc def"),
                Arguments.of("\"abc   \t\r\n   def\nghi\"", "abcdefghi"),
                Arguments.of("\"\n\nabc\"", "abc")
        );
    }

    private static Stream<Arguments> provideSimpleString() {
        return Stream.of(
                Arguments.of("\"a\"", "a"),
                Arguments.of("\"abc def\"", "abc def"),
                Arguments.of("\"abc\n\ndef\"", "abc  def")
        );
    }

    private static Stream<Arguments> provideTypeReference() {
        return Stream.of(
                Arguments.of("T", "T"),
                Arguments.of("Type", "Type"),
                Arguments.of("Test-Type", "Test-Type"),
                Arguments.of("Test--Type", "Test")
        );
    }

    private static Stream<Arguments> provideIdentifier() {
        return Stream.of(
                Arguments.of("t", "t"),
                Arguments.of("type", "type"),
                Arguments.of("test-type", "test-type"),
                Arguments.of("test-TYPE", "test-TYPE"),
                Arguments.of("test--type", "test")
        );
    }

    private static Stream<Arguments> provideTokens() {
        return Stream.of(
                Arguments.of("::=", Token.TokenType.ASSIGN),
                Arguments.of("..", Token.TokenType.RANGE),
                Arguments.of("...", Token.TokenType.ELLIPSIS),
                Arguments.of("[[", Token.TokenType.L_VERSION_BRACKETS),
                Arguments.of("]]", Token.TokenType.R_VERSION_BRACKETS),
                Arguments.of("{", Token.TokenType.L_BRACE),
                Arguments.of("}", Token.TokenType.R_BRACE),
                Arguments.of("<", Token.TokenType.LT),
                Arguments.of(">", Token.TokenType.GT),
                Arguments.of(",", Token.TokenType.COMMA),
                Arguments.of(".", Token.TokenType.DOT),
                Arguments.of("/", Token.TokenType.SOLIDUS),
                Arguments.of("(", Token.TokenType.L_PAREN),
                Arguments.of(")", Token.TokenType.R_PAREN),
                Arguments.of("[", Token.TokenType.L_BRACKET),
                Arguments.of("]", Token.TokenType.R_BRACKET),
                Arguments.of("-", Token.TokenType.MINUS),
                Arguments.of(":", Token.TokenType.COLON),
                Arguments.of("=", Token.TokenType.EQUALS),
                Arguments.of("\"", Token.TokenType.QUOTATION),
                Arguments.of("'", Token.TokenType.APOSTROPHE),
                Arguments.of(";", Token.TokenType.SEMICOLON),
                Arguments.of("@", Token.TokenType.AT),
                Arguments.of("|", Token.TokenType.PIPE),
                Arguments.of("!", Token.TokenType.EXCLAMATION),
                Arguments.of("^", Token.TokenType.CIRCUMFLEX)
        );
    }

    private static Stream<Arguments> provideKeywords() {
        return Stream.of(
                Arguments.of("ABSENT", Token.TokenType.ABSENT_KW),
                Arguments.of("ABSTRACT-SYNTAX", Token.TokenType.ABSTRACT_SYNTAX_KW),
                Arguments.of("ALL", Token.TokenType.ALL_KW),
                Arguments.of("APPLICATION", Token.TokenType.APPLICATION_KW),
                Arguments.of("AUTOMATIC", Token.TokenType.AUTOMATIC_KW),
                Arguments.of("BEGIN", Token.TokenType.BEGIN_KW),
                Arguments.of("BIT", Token.TokenType.BIT_KW),
                Arguments.of("BMPString", Token.TokenType.BMP_STRING_KW),
                Arguments.of("BOOLEAN", Token.TokenType.BOOLEAN_KW),
                Arguments.of("BY", Token.TokenType.BY_KW),
                Arguments.of("CHARACTER", Token.TokenType.CHARACTER_KW),
                Arguments.of("CHOICE", Token.TokenType.CHOICE_KW),
                Arguments.of("CLASS", Token.TokenType.CLASS_KW),
                Arguments.of("COMPONENT", Token.TokenType.COMPONENT_KW),
                Arguments.of("COMPONENTS", Token.TokenType.COMPONENTS_KW),
                Arguments.of("CONSTRAINED", Token.TokenType.CONSTRAINED_KW),
                Arguments.of("CONTAINING", Token.TokenType.CONTAINING_KW),
                Arguments.of("DATE", Token.TokenType.DATE_KW),
                Arguments.of("DATE-TIME", Token.TokenType.DATE_TIME_KW),
                Arguments.of("DEFAULT", Token.TokenType.DEFAULT_KW),
                Arguments.of("DEFINITIONS", Token.TokenType.DEFINITIONS_KW),
                Arguments.of("DURATION", Token.TokenType.DURATION_KW),
                Arguments.of("EMBEDDED", Token.TokenType.EMBEDDED_KW),
                Arguments.of("ENCODED", Token.TokenType.ENCODED_KW),
                Arguments.of("ENCODING-CONTROL", Token.TokenType.ENCODING_CONTROL_KW),
                Arguments.of("END", Token.TokenType.END_KW),
                Arguments.of("ENUMERATED", Token.TokenType.ENUMERATED_KW),
                Arguments.of("EXCEPT", Token.TokenType.EXCEPT_KW),
                Arguments.of("EXPLICIT", Token.TokenType.EXPLICIT_KW),
                Arguments.of("EXPORTS", Token.TokenType.EXPORTS_KW),
                Arguments.of("EXTENSIBILITY", Token.TokenType.EXTENSIBILITY_KW),
                Arguments.of("EXTERNAL", Token.TokenType.EXTERNAL_KW),
                Arguments.of("FALSE", Token.TokenType.FALSE_KW),
                Arguments.of("FROM", Token.TokenType.FROM_KW),
                Arguments.of("GeneralizedTime", Token.TokenType.GENERALIZED_TIME_KW),
                Arguments.of("GeneralString", Token.TokenType.GENERAL_STRING_KW),
                Arguments.of("GraphicString", Token.TokenType.GRAPHIC_STRING_KW),
                Arguments.of("IA5String", Token.TokenType.IA5_STRING_KW),
                Arguments.of("IDENTIFIER", Token.TokenType.IDENTIFIER_KW),
                Arguments.of("IMPLICIT", Token.TokenType.IMPLICIT_KW),
                Arguments.of("IMPLIED", Token.TokenType.IMPLIED_KW),
                Arguments.of("IMPORTS", Token.TokenType.IMPORTS_KW),
                Arguments.of("INCLUDES", Token.TokenType.INCLUDES_KW),
                Arguments.of("INSTANCE", Token.TokenType.INSTANCE_KW),
                Arguments.of("INSTRUCTIONS", Token.TokenType.INSTRUCTIONS_KW),
                Arguments.of("INTEGER", Token.TokenType.INTEGER_KW),
                Arguments.of("INTERSECTION", Token.TokenType.INTERSECTION_KW),
                Arguments.of("ISO646String", Token.TokenType.ISO646_STRING_KW),
                Arguments.of("MAX", Token.TokenType.MAX_KW),
                Arguments.of("MIN", Token.TokenType.MIN_KW),
                Arguments.of("MINUS-INFINITY", Token.TokenType.MINUS_INFINITY_KW),
                Arguments.of("NOT-A-NUMBER", Token.TokenType.NOT_A_NUMBER_KW),
                Arguments.of("NULL", Token.TokenType.NULL_KW),
                Arguments.of("NumericString", Token.TokenType.NUMERIC_STRING_KW),
                Arguments.of("OBJECT", Token.TokenType.OBJECT_KW),
                Arguments.of("ObjectDescriptor", Token.TokenType.OBJECT_DESCRIPTOR_KW),
                Arguments.of("OCTET", Token.TokenType.OCTET_KW),
                Arguments.of("OF", Token.TokenType.OF_KW),
                Arguments.of("OID-IRI", Token.TokenType.OID_IRI_KW),
                Arguments.of("OPTIONAL", Token.TokenType.OPTIONAL_KW),
                Arguments.of("PATTERN", Token.TokenType.PATTERN_KW),
                Arguments.of("PDV", Token.TokenType.PDV_KW),
                Arguments.of("PLUS-INFINITY", Token.TokenType.PLUS_INFINITY_KW),
                Arguments.of("PRESENT", Token.TokenType.PRESENT_KW),
                Arguments.of("PrintableString", Token.TokenType.PRINTABLE_STRING_KW),
                Arguments.of("PRIVATE", Token.TokenType.PRIVATE_KW),
                Arguments.of("REAL", Token.TokenType.REAL_KW),
                Arguments.of("RELATIVE-OID", Token.TokenType.RELATIVE_OID_KW),
                Arguments.of("RELATIVE-OID-IRI", Token.TokenType.RELATIVE_OID_IRI_KW),
                Arguments.of("SEQUENCE", Token.TokenType.SEQUENCE_KW),
                Arguments.of("SET", Token.TokenType.SET_KW),
                Arguments.of("SETTINGS", Token.TokenType.SETTINGS_KW),
                Arguments.of("SIZE", Token.TokenType.SIZE_KW),
                Arguments.of("STRING", Token.TokenType.STRING_KW),
                Arguments.of("SYNTAX", Token.TokenType.SYNTAX_KW),
                Arguments.of("T61String", Token.TokenType.T61_STRING_KW),
                Arguments.of("TAGS", Token.TokenType.TAGS_KW),
                Arguments.of("TeletexString", Token.TokenType.TELETEX_STRING_KW),
                Arguments.of("TIME", Token.TokenType.TIME_KW),
                Arguments.of("TIME-OF-DAY", Token.TokenType.TIME_OF_DAY_KW),
                Arguments.of("TRUE", Token.TokenType.TRUE_KW),
                Arguments.of("TYPE-IDENTIFIER", Token.TokenType.TYPE_IDENTIFIER_KW),
                Arguments.of("UNION", Token.TokenType.UNION_KW),
                Arguments.of("UNIQUE", Token.TokenType.UNIQUE_KW),
                Arguments.of("UNIVERSAL", Token.TokenType.UNIVERSAL_KW),
                Arguments.of("UniversalString", Token.TokenType.UNIVERSAL_STRING_KW),
                Arguments.of("UTCTime", Token.TokenType.UTC_TIME_KW),
                Arguments.of("UTF8String", Token.TokenType.UTF8_STRING_KW),
                Arguments.of("VideotexString", Token.TokenType.VIDEOTEX_STRING_KW),
                Arguments.of("VisibleString", Token.TokenType.VISIBLE_STRING_KW),
                Arguments.of("WITH", Token.TokenType.WITH_KW)
        );
    }

    private static Stream<Arguments> provideComments() {
        return Stream.of(
                Arguments.of("--"),
                Arguments.of("-- This is a comment"),
                Arguments.of("-- This is a comment\r"),
                Arguments.of("-- This is a comment\r\n"),
                Arguments.of("-- This is a comment\n"),
                Arguments.of("-- This is a -comment"),
                Arguments.of("-- This is a -comment--")
        );
    }

    private static Stream<Arguments> provideMultilineComments() {
        return Stream.of(
                Arguments.of("/**/"),
                Arguments.of("/* This is a comment */"),
                Arguments.of("/* This is \ra comment */"),
                Arguments.of("/* This is \na comment */"),
                Arguments.of("/* This is \r\na comment */"),
                Arguments.of("/* This is / a comment */"),
                Arguments.of("/* This is * a comment */"),
                Arguments.of("/* This is a /* nested */ comment */")
        );
    }

}
