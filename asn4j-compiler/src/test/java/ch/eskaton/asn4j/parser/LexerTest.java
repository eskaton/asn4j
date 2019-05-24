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

import static ch.eskaton.asn4j.parser.NoPosition.NO_POSITION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;

import ch.eskaton.asn4j.parser.Lexer.Context;
import ch.eskaton.asn4j.parser.ast.values.StringValue;
import ch.eskaton.asn4j.parser.ast.values.TimeValue;

public class LexerTest {

    @Test
    public void testTypeReference() throws IOException, ParserException {
    	Lexer lexer = new Lexer(new ByteArrayInputStream(
    			"T Type Test-Type".getBytes()));

    	assertEquals(Token.TokenType.TYPE_REFERENCE,
    			lexer.nextToken(Context.Normal).getType());

    	assertEquals(Token.TokenType.TYPE_REFERENCE,
    			lexer.nextToken(Context.Normal).getType());

    	assertEquals(Token.TokenType.TYPE_REFERENCE,
    			lexer.nextToken(Context.Normal).getType());

    	assertNull(lexer.nextToken(Context.Normal));
    	assertTrue(lexer.isEOF());
    }

    @Test
    public void testInvalidTypeReference() throws IOException, ParserException {
    	Lexer lexer = new Lexer(new ByteArrayInputStream(
    			"Test--Type".getBytes()));
    	assertEquals("Test", lexer.nextToken(Context.Normal).getText());

    	lexer = new Lexer(new ByteArrayInputStream("Type-".getBytes()));

    	try {
    		lexer.nextToken(Context.Normal);
    		fail("ASN1ParserException expected");
    	} catch (ParserException e) {
    	}
    }

    @Test
    public void testTypeIdentifier() throws IOException, ParserException {
    	Lexer lexer = new Lexer(new ByteArrayInputStream(
    			"t type test-type test-TYPE".getBytes()));

    	assertEquals(Token.TokenType.IDENTIFIER, lexer
    			.nextToken(Context.Normal).getType());

    	assertEquals(Token.TokenType.IDENTIFIER, lexer
    			.nextToken(Context.Normal).getType());

    	assertEquals(Token.TokenType.IDENTIFIER, lexer
    			.nextToken(Context.Normal).getType());

    	assertEquals(Token.TokenType.IDENTIFIER, lexer
    			.nextToken(Context.Normal).getType());

    	assertNull(lexer.nextToken(Context.Normal));
    	assertTrue(lexer.isEOF());
    }

    @Test
    public void testInvalidIdentifier() throws IOException, ParserException {
    	Lexer lexer = new Lexer(new ByteArrayInputStream(
    			"test--type".getBytes()));
    	assertEquals("test", lexer.nextToken(Context.Normal).getText());

    	lexer = new Lexer(new ByteArrayInputStream("type-".getBytes()));

    	try {
    		lexer.nextToken(Context.Normal);
    		fail("ASN1ParserException expected");
    	} catch (ParserException e) {
    	}
    }

    @Test
    public void testObjectClassReference() throws IOException, ParserException {
    	Lexer lexer = new Lexer(new ByteArrayInputStream(
    			"T OBJECT-CLASS".getBytes()));

    	assertEquals(Token.TokenType.OBJECT_CLASS_REFERENCE,
    			lexer.nextToken(Context.ObjectClass).getType());

    	assertEquals(Token.TokenType.OBJECT_CLASS_REFERENCE,
    			lexer.nextToken(Context.ObjectClass).getType());

    	assertNull(lexer.nextToken(Context.Normal));
    	assertTrue(lexer.isEOF());
    }

    @Test
    public void testInvalidObjectClassReference() throws IOException,
    		ParserException {
    	Lexer lexer = new Lexer(new ByteArrayInputStream("Invalid".getBytes()));

    	try {
    		lexer.nextToken(Context.ObjectClass).getType();
    		fail("ASN1ParserException expected");
    	} catch (ParserException e) {
    	}
    }

    @Test
    public void testTypeFieldReference() throws IOException, ParserException {
    	Lexer lexer = new Lexer(new ByteArrayInputStream(
    			"&T &Type-Field".getBytes()));

    	assertEquals(Token.TokenType.TYPE_FIELD_REFERENCE,
    			lexer.nextToken(Context.TypeField).getType());

    	assertEquals(Token.TokenType.TYPE_FIELD_REFERENCE,
    			lexer.nextToken(Context.TypeField).getType());

    	assertNull(lexer.nextToken(Context.Normal));
    	assertTrue(lexer.isEOF());
    }

    @Test
    public void testValueFieldReference() throws IOException, ParserException {
    	Lexer lexer = new Lexer(new ByteArrayInputStream(
    			"&value-field".getBytes()));

    	assertEquals(Token.TokenType.VALUE_FIELD_REFERENCE,
    			lexer.nextToken(Context.ValueField).getType());

    	assertNull(lexer.nextToken(Context.Normal));
    	assertTrue(lexer.isEOF());
    }

    @Test
    public void testWord() throws IOException, ParserException {
    	Lexer lexer = new Lexer(new ByteArrayInputStream("A-WORD".getBytes()));

    	assertEquals(Token.TokenType.WORD, lexer.nextToken(Context.Syntax)
    			.getType());

    	assertNull(lexer.nextToken(Context.Normal));
    	assertTrue(lexer.isEOF());
    }

    @Test
    public void testNumber() throws IOException, ParserException {
    	Lexer lexer = new Lexer(new ByteArrayInputStream(
    			"0 1 42 4711".getBytes()));
    	Token token;

    	assertEquals(Token.TokenType.NUMBER,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("0", token.getText());

    	assertEquals(Token.TokenType.NUMBER,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("1", token.getText());

    	assertEquals(Token.TokenType.NUMBER,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("42", token.getText());

    	assertEquals(Token.TokenType.NUMBER,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("4711", token.getText());

    	assertNull(lexer.nextToken(Context.Normal));
    	assertTrue(lexer.isEOF());
    }

    @Test
    public void testRealNumber() throws IOException, ParserException {
    	Lexer lexer = new Lexer(new ByteArrayInputStream(
    			"01 2. 3.41 3e0 3e12 3e-12 42.e-0 42.e5 42.e-5 77.35E0 77.35e+6 77.35e-6"
    					.getBytes()));
    	Token token;

    	assertEquals(Token.TokenType.REAL_NUMBER,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("01", token.getText());

    	assertEquals(Token.TokenType.REAL_NUMBER,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("2.", token.getText());

    	assertEquals(Token.TokenType.REAL_NUMBER,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("3.41", token.getText());

    	assertEquals(Token.TokenType.REAL_NUMBER,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("3e0", token.getText());

    	assertEquals(Token.TokenType.REAL_NUMBER,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("3e12", token.getText());

    	assertEquals(Token.TokenType.REAL_NUMBER,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("3e-12", token.getText());

    	assertEquals(Token.TokenType.REAL_NUMBER,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("42.e-0", token.getText());

    	assertEquals(Token.TokenType.REAL_NUMBER,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("42.e5", token.getText());

    	assertEquals(Token.TokenType.REAL_NUMBER,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("42.e-5", token.getText());

    	assertEquals(Token.TokenType.REAL_NUMBER,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("77.35E0", token.getText());

    	assertEquals(Token.TokenType.REAL_NUMBER,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("77.35e+6", token.getText());

    	assertEquals(Token.TokenType.REAL_NUMBER,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("77.35e-6", token.getText());

    	assertNull(lexer.nextToken(Context.Normal));
    	assertTrue(lexer.isEOF());
    }

    @Test
    public void testInvalidRealNumber() throws IOException {
    	Lexer lexer = new Lexer(new ByteArrayInputStream("5e".getBytes()));

    	try {
    		lexer.nextToken(Context.Normal);
    		fail("ASN1ParserException expected");
    	} catch (ParserException e) {
    	}

    	lexer = new Lexer(new ByteArrayInputStream("5e01".getBytes()));

    	try {
    		lexer.nextToken(Context.Normal);
    		fail("ASN1ParserException expected");
    	} catch (ParserException e) {
    	}
    }

    @Test
    public void testBString() throws IOException, ParserException {
    	Lexer lexer = new Lexer(new ByteArrayInputStream(
    			"''B '0'B '1'B '0101'B '1 01 1'B".getBytes()));
    	Token token;

    	assertEquals(Token.TokenType.B_STRING,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("", token.getText());

    	assertEquals(Token.TokenType.B_STRING,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("0", token.getText());

    	assertEquals(Token.TokenType.B_STRING,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("1", token.getText());

    	assertEquals(Token.TokenType.B_STRING,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("0101", token.getText());

    	assertEquals(Token.TokenType.B_STRING,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("1011", token.getText());

    	assertNull(lexer.nextToken(Context.Normal));
    	assertTrue(lexer.isEOF());
    }

    @Test
    public void testHString() throws IOException, ParserException {
    	Lexer lexer = new Lexer(new ByteArrayInputStream(
    			"''H '0'H '0B0F'H 'F AD 5'H".getBytes()));
    	Token token;

    	assertEquals(Token.TokenType.H_STRING,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("", token.getText());

    	assertEquals(Token.TokenType.H_STRING,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("0", token.getText());

    	assertEquals(Token.TokenType.H_STRING,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("0B0F", token.getText());

    	assertEquals(Token.TokenType.H_STRING,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("FAD5", token.getText());

    	assertNull(lexer.nextToken(Context.Normal));
    	assertTrue(lexer.isEOF());
    }

    @Test
    public void testCString() throws IOException, ParserException {
    	Lexer lexer = new Lexer(
    			new ByteArrayInputStream(
    					"\"\" \"\"\"\" \" \" \"a\"\"b\" \"abc def\" \"abc   \t\r\n   def\nghi\" \"\n\nabc\""
    							.getBytes()));
    	Token token;

    	assertEquals(Token.TokenType.C_STRING,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("", getCString(token));

    	assertEquals(Token.TokenType.C_STRING,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("\"", getCString(token));

    	assertEquals(Token.TokenType.C_STRING,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals(" ", getCString(token));

    	assertEquals(Token.TokenType.C_STRING,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("a\"b", getCString(token));

    	assertEquals(Token.TokenType.C_STRING,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("abc def", getCString(token));

    	assertEquals(Token.TokenType.C_STRING,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("abcdefghi", getCString(token));

    	assertEquals(Token.TokenType.C_STRING,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("abc", getCString(token));

    	assertNull(lexer.nextToken(Context.Normal));
    	assertTrue(lexer.isEOF());
    }

    private static String getCString(Token token) {
    	return new StringValue(NO_POSITION, token.getText(), ((StringToken) token).getFlags()).getCString();
    }

    @Test
    public void testSimpleString() throws IOException, ParserException {
    	Lexer lexer = new Lexer(new ByteArrayInputStream(
    			"\"a\" \"abc def\" \"abc\n\ndef\"".getBytes()));
    	Token token;

    	assertEquals(Token.TokenType.C_STRING,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("a", getSimpleString(token));

    	assertEquals(Token.TokenType.C_STRING,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("abc def", getSimpleString(token));

    	assertEquals(Token.TokenType.C_STRING,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals("abc  def", getSimpleString(token));

    	assertNull(lexer.nextToken(Context.Normal));
    	assertTrue(lexer.isEOF());
    }

    @Test
    public void testInvalidSimpleString() throws IOException {
    	Lexer lexer = new Lexer(new ByteArrayInputStream("\"\"".getBytes()));
    	Token token;

    	try {
    		assertEquals(Token.TokenType.C_STRING,
    				(token = lexer.nextToken(Context.Normal)).getType());
    		getSimpleString(token);
    		fail("ASN1ParserException expected");
    	} catch (ParserException e) {
    	}

    	lexer = new Lexer(new ByteArrayInputStream("\"\"\"\"".getBytes()));

    	try {
    		assertEquals(Token.TokenType.C_STRING,
    				(token = lexer.nextToken(Context.Normal)).getType());
    		getSimpleString(token);
    		fail("ASN1ParserException expected");
    	} catch (ParserException e) {
    	}

    	lexer = new Lexer(new ByteArrayInputStream("\"ü\"".getBytes()));

    	try {
    		assertEquals(Token.TokenType.C_STRING,
    				(token = lexer.nextToken(Context.Normal)).getType());
    		getSimpleString(token);
    		fail("ASN1ParserException expected");
    	} catch (ParserException e) {
    	}
    }

    private static String getSimpleString(Token token) throws ParserException {
    	return new StringValue(NO_POSITION, token.getText(), ((StringToken) token).getFlags()).getSimpleString();
    }

    @Test
    public void testTString() throws IOException, ParserException {
    	Lexer lexer = new Lexer(new ByteArrayInputStream(
    			"\".\" \"0123456789+-:.,/CDHMRPSTWYZ\"".getBytes()));
    	Token token;

    	assertEquals(Token.TokenType.C_STRING,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals(new TimeValue(NO_POSITION, "."), getTimeValue(token));

    	assertEquals(Token.TokenType.C_STRING,
    			(token = lexer.nextToken(Context.Normal)).getType());
    	assertEquals(new TimeValue(NO_POSITION, "0123456789+-:.,/CDHMRPSTWYZ"),
    			getTimeValue(token));

    	assertNull(lexer.nextToken(Context.Normal));
    	assertTrue(lexer.isEOF());
    }

    @Test
    public void testInvalidTString() throws IOException {
    	Lexer lexer = new Lexer(new ByteArrayInputStream("\"\"".getBytes()));
    	Token token;

    	try {
    		assertEquals(Token.TokenType.C_STRING,
    				(token = lexer.nextToken(Context.Normal)).getType());
    		getTimeValue(token);
    		fail("ASN1ParserException expected");
    	} catch (ParserException e) {
    	}

    	lexer = new Lexer(new ByteArrayInputStream("\"A\"".getBytes()));

    	try {
    		assertEquals(Token.TokenType.C_STRING,
    				(token = lexer.nextToken(Context.Normal)).getType());
    		getTimeValue(token);
    		fail("ASN1ParserException expected");
    	} catch (ParserException e) {
    	}

    	lexer = new Lexer(new ByteArrayInputStream("\"0 1\"".getBytes()));

    	try {
    		assertEquals(Token.TokenType.C_STRING,
    				(token = lexer.nextToken(Context.Normal)).getType());
    		getTimeValue(token);
    		fail("ASN1ParserException expected");
    	} catch (ParserException e) {
    	}
    }

    private static TimeValue getTimeValue(Token token) throws ParserException {
    	return new StringValue(NO_POSITION, token.getText(), ((StringToken) token).getFlags()).getTimeValue();
    }

    @Test
    public void testTokens() throws IOException, ParserException {
    	testToken("::=", Token.TokenType.ASSIGN);
    	testToken("..", Token.TokenType.RANGE);
    	testToken("...", Token.TokenType.ELLIPSIS);
    	testToken("[[", Token.TokenType.L_VERSION_BRACKETS);
    	testToken("]]", Token.TokenType.R_VERSION_BRACKETS);
    	testToken("{", Token.TokenType.L_BRACE);
    	testToken("}", Token.TokenType.R_BRACE);
    	testToken("<", Token.TokenType.LT);
    	testToken(">", Token.TokenType.GT);
    	testToken(",", Token.TokenType.COMMA);
    	testToken(".", Token.TokenType.DOT);
    	testToken("/", Token.TokenType.SOLIDUS);
    	testToken("(", Token.TokenType.L_PAREN);
    	testToken(")", Token.TokenType.R_PAREN);
    	testToken("[", Token.TokenType.L_BRACKET);
    	testToken("]", Token.TokenType.R_BRACKET);
    	testToken("-", Token.TokenType.MINUS);
    	testToken(":", Token.TokenType.COLON);
    	testToken("=", Token.TokenType.EQUALS);
    	testToken("\"", Token.TokenType.QUOTATION);
    	testToken("'", Token.TokenType.APOSTROPHE);
    	testToken(";", Token.TokenType.SEMICOLON);
    	testToken("@", Token.TokenType.AT);
    	testToken("|", Token.TokenType.PIPE);
    	testToken("!", Token.TokenType.EXCLAMATION);
    	testToken("^", Token.TokenType.CIRCUMFLEX);
    }

    @Test
    public void testKeywords() throws IOException, ParserException {
    	testToken("ABSENT", Token.TokenType.ABSENT_KW);
    	testToken("ABSTRACT-SYNTAX", Token.TokenType.ABSTRACT_SYNTAX_KW);
    	testToken("ALL", Token.TokenType.ALL_KW);
    	testToken("APPLICATION", Token.TokenType.APPLICATION_KW);
    	testToken("AUTOMATIC", Token.TokenType.AUTOMATIC_KW);
    	testToken("BEGIN", Token.TokenType.BEGIN_KW);
    	testToken("BIT", Token.TokenType.BIT_KW);
    	testToken("BMPString", Token.TokenType.BMP_STRING_KW);
    	testToken("BOOLEAN", Token.TokenType.BOOLEAN_KW);
    	testToken("BY", Token.TokenType.BY_KW);
    	testToken("CHARACTER", Token.TokenType.CHARACTER_KW);
    	testToken("CHOICE", Token.TokenType.CHOICE_KW);
    	testToken("CLASS", Token.TokenType.CLASS_KW);
    	testToken("COMPONENT", Token.TokenType.COMPONENT_KW);
    	testToken("COMPONENTS", Token.TokenType.COMPONENTS_KW);
    	testToken("CONSTRAINED", Token.TokenType.CONSTRAINED_KW);
    	testToken("CONTAINING", Token.TokenType.CONTAINING_KW);
    	testToken("DATE", Token.TokenType.DATE_KW);
    	testToken("DATE-TIME", Token.TokenType.DATE_TIME_KW);
    	testToken("DEFAULT", Token.TokenType.DEFAULT_KW);
    	testToken("DEFINITIONS", Token.TokenType.DEFINITIONS_KW);
    	testToken("DURATION", Token.TokenType.DURATION_KW);
    	testToken("EMBEDDED", Token.TokenType.EMBEDDED_KW);
    	testToken("ENCODED", Token.TokenType.ENCODED_KW);
    	testToken("ENCODING-CONTROL", Token.TokenType.ENCODING_CONTROL_KW);
    	testToken("END", Token.TokenType.END_KW);
    	testToken("ENUMERATED", Token.TokenType.ENUMERATED_KW);
    	testToken("EXCEPT", Token.TokenType.EXCEPT_KW);
    	testToken("EXPLICIT", Token.TokenType.EXPLICIT_KW);
    	testToken("EXPORTS", Token.TokenType.EXPORTS_KW);
    	testToken("EXTENSIBILITY", Token.TokenType.EXTENSIBILITY_KW);
    	testToken("EXTERNAL", Token.TokenType.EXTERNAL_KW);
    	testToken("FALSE", Token.TokenType.FALSE_KW);
    	testToken("FROM", Token.TokenType.FROM_KW);
    	testToken("GeneralizedTime", Token.TokenType.GENERALIZED_TIME_KW);
    	testToken("GeneralString", Token.TokenType.GENERAL_STRING_KW);
    	testToken("GraphicString", Token.TokenType.GRAPHIC_STRING_KW);
    	testToken("IA5String", Token.TokenType.IA5_STRING_KW);
    	testToken("IDENTIFIER", Token.TokenType.IDENTIFIER_KW);
    	testToken("IMPLICIT", Token.TokenType.IMPLICIT_KW);
    	testToken("IMPLIED", Token.TokenType.IMPLIED_KW);
    	testToken("IMPORTS", Token.TokenType.IMPORTS_KW);
    	testToken("INCLUDES", Token.TokenType.INCLUDES_KW);
    	testToken("INSTANCE", Token.TokenType.INSTANCE_KW);
    	testToken("INSTRUCTIONS", Token.TokenType.INSTRUCTIONS_KW);
    	testToken("INTEGER", Token.TokenType.INTEGER_KW);
    	testToken("INTERSECTION", Token.TokenType.INTERSECTION_KW);
    	testToken("ISO646String", Token.TokenType.ISO646_STRING_KW);
    	testToken("MAX", Token.TokenType.MAX_KW);
    	testToken("MIN", Token.TokenType.MIN_KW);
    	testToken("MINUS-INFINITY", Token.TokenType.MINUS_INFINITY_KW);
    	testToken("NOT-A-NUMBER", Token.TokenType.NOT_A_NUMBER_KW);
    	testToken("NULL", Token.TokenType.NULL_KW);
    	testToken("NumericString", Token.TokenType.NUMERIC_STRING_KW);
    	testToken("OBJECT", Token.TokenType.OBJECT_KW);
    	testToken("ObjectDescriptor", Token.TokenType.OBJECT_DESCRIPTOR_KW);
    	testToken("OCTET", Token.TokenType.OCTET_KW);
    	testToken("OF", Token.TokenType.OF_KW);
    	testToken("OID-IRI", Token.TokenType.OID_IRI_KW);
    	testToken("OPTIONAL", Token.TokenType.OPTIONAL_KW);
    	testToken("PATTERN", Token.TokenType.PATTERN_KW);
    	testToken("PDV", Token.TokenType.PDV_KW);
    	testToken("PLUS-INFINITY", Token.TokenType.PLUS_INFINITY_KW);
    	testToken("PRESENT", Token.TokenType.PRESENT_KW);
    	testToken("PrintableString", Token.TokenType.PRINTABLE_STRING_KW);
    	testToken("PRIVATE", Token.TokenType.PRIVATE_KW);
    	testToken("REAL", Token.TokenType.REAL_KW);
    	testToken("RELATIVE-OID", Token.TokenType.RELATIVE_OID_KW);
    	testToken("RELATIVE-OID-IRI", Token.TokenType.RELATIVE_OID_IRI_KW);
    	testToken("SEQUENCE", Token.TokenType.SEQUENCE_KW);
    	testToken("SET", Token.TokenType.SET_KW);
    	testToken("SETTINGS", Token.TokenType.SETTINGS_KW);
    	testToken("SIZE", Token.TokenType.SIZE_KW);
    	testToken("STRING", Token.TokenType.STRING_KW);
    	testToken("SYNTAX", Token.TokenType.SYNTAX_KW);
    	testToken("T61String", Token.TokenType.T61_STRING_KW);
    	testToken("TAGS", Token.TokenType.TAGS_KW);
    	testToken("TeletexString", Token.TokenType.TELETEX_STRING_KW);
    	testToken("TIME", Token.TokenType.TIME_KW);
    	testToken("TIME-OF-DAY", Token.TokenType.TIME_OF_DAY_KW);
    	testToken("TRUE", Token.TokenType.TRUE_KW);
    	testToken("TYPE-IDENTIFIER", Token.TokenType.TYPE_IDENTIFIER_KW);
    	testToken("UNION", Token.TokenType.UNION_KW);
    	testToken("UNIQUE", Token.TokenType.UNIQUE_KW);
    	testToken("UNIVERSAL", Token.TokenType.UNIVERSAL_KW);
    	testToken("UniversalString", Token.TokenType.UNIVERSAL_STRING_KW);
    	testToken("UTCTime", Token.TokenType.UTC_TIME_KW);
    	testToken("UTF8String", Token.TokenType.UTF8_STRING_KW);
    	testToken("VideotexString", Token.TokenType.VIDEOTEX_STRING_KW);
    	testToken("VisibleString", Token.TokenType.VISIBLE_STRING_KW);
    	testToken("WITH", Token.TokenType.WITH_KW);
    }

    @Test
    public void testEncodingReference() throws IOException, ParserException {
    	Lexer lexer = new Lexer(new ByteArrayInputStream(
    			"TAG XER PER".getBytes()));
    	Token token;

    	assertEquals(Token.TokenType.ENCODING_REFERENCE,
    			(token = lexer.nextToken(Context.Encoding)).getType());
    	assertEquals("TAG", token.getText());

    	assertEquals(Token.TokenType.ENCODING_REFERENCE,
    			(token = lexer.nextToken(Context.Encoding)).getType());
    	assertEquals("XER", token.getText());

    	assertEquals(Token.TokenType.ENCODING_REFERENCE,
    			(token = lexer.nextToken(Context.Encoding)).getType());
    	assertEquals("PER", token.getText());

    	assertNull(lexer.nextToken(Context.Normal));
    	assertTrue(lexer.isEOF());
    }

    @Test
    public void testInvalidEncodingReference() throws IOException {
    	Lexer lexer = new Lexer(new ByteArrayInputStream("ABC".getBytes()));

    	try {
    		lexer.nextToken(Context.Encoding);
    		fail("ASN1ParserException expected");
    	} catch (ParserException e) {
    	}

    	lexer = new Lexer(new ByteArrayInputStream("xer".getBytes()));

    	try {
    		lexer.nextToken(Context.Encoding);
    		fail("ASN1ParserException expected");
    	} catch (ParserException e) {
    	}
    }

    private void testToken(String token, Token.TokenType type)
    		throws IOException, ParserException {
    	Lexer lexer = new Lexer(new ByteArrayInputStream(token.getBytes()));

    	assertEquals(type, lexer.nextToken(Context.Normal).getType());
    }

}
