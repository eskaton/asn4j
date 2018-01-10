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

import ch.eskaton.asn4j.parser.Token.TokenType;
import ch.eskaton.asn4j.parser.ast.ModuleNode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Lexer {

    public enum Context {
    	Normal, Encoding, ObjectClass, Syntax, TypeField, ValueField, Level,
    	PropertySettings
    };

    public static final String ABSENT_LIT = "ABSENT";
    public static final String ABSTRACT_SYNTAX_LIT = "ABSTRACT-SYNTAX";
    public static final String ALL_LIT = "ALL";
    public static final String APPLICATION_LIT = "APPLICATION";
    public static final String AUTOMATIC_LIT = "AUTOMATIC";
    public static final String BEGIN_LIT = "BEGIN";
    public static final String BIT_LIT = "BIT";
    public static final String BMPSTRING_LIT = "BMPString";
    public static final String BOOLEAN_LIT = "BOOLEAN";
    public static final String BY_LIT = "BY";
    public static final String CHARACTER_LIT = "CHARACTER";
    public static final String CHOICE_LIT = "CHOICE";
    public static final String CLASS_LIT = "CLASS";
    public static final String COMPONENT_LIT = "COMPONENT";
    public static final String COMPONENTS_LIT = "COMPONENTS";
    public static final String CONSTRAINED_LIT = "CONSTRAINED";
    public static final String CONTAINING_LIT = "CONTAINING";
    public static final String DATE_LIT = "DATE";
    public static final String DATE_TIME_LIT = "DATE-TIME";
    public static final String DEFAULT_LIT = "DEFAULT";
    public static final String DEFINITIONS_LIT = "DEFINITIONS";
    public static final String DURATION_LIT = "DURATION";
    public static final String EMBEDDED_LIT = "EMBEDDED";
    public static final String ENCODED_LIT = "ENCODED";
    public static final String ENCODING_CONTROL_LIT = "ENCODING-CONTROL";
    public static final String END_LIT = "END";
    public static final String ENUMERATED_LIT = "ENUMERATED";
    public static final String EXCEPT_LIT = "EXCEPT";
    public static final String EXPLICIT_LIT = "EXPLICIT";
    public static final String EXPORTS_LIT = "EXPORTS";
    public static final String EXTENSIBILITY_LIT = "EXTENSIBILITY";
    public static final String EXTERNAL_LIT = "EXTERNAL";
    public static final String FALSE_LIT = "FALSE";
    public static final String FROM_LIT = "FROM";
    public static final String GENERALIZEDTIME_LIT = "GeneralizedTime";
    public static final String GENERALSTRING_LIT = "GeneralString";
    public static final String GRAPHICSTRING_LIT = "GraphicString";
    public static final String IA5STRING_LIT = "IA5String";
    public static final String IDENTIFIER_LIT = "IDENTIFIER";
    public static final String IMPLICIT_LIT = "IMPLICIT";
    public static final String IMPLIED_LIT = "IMPLIED";
    public static final String IMPORTS_LIT = "IMPORTS";
    public static final String INCLUDES_LIT = "INCLUDES";
    public static final String INSTANCE_LIT = "INSTANCE";
    public static final String INSTRUCTIONS_LIT = "INSTRUCTIONS";
    public static final String INTEGER_LIT = "INTEGER";
    public static final String INTERSECTION_LIT = "INTERSECTION";
    public static final String ISO646STRING_LIT = "ISO646String";
    public static final String MAX_LIT = "MAX";
    public static final String MIN_LIT = "MIN";
    public static final String MINUS_INFINITY_LIT = "MINUS-INFINITY";
    public static final String NOT_A_NUMBER_LIT = "NOT-A-NUMBER";
    public static final String NULL_LIT = "NULL";
    public static final String NUMERICSTRING_LIT = "NumericString";
    public static final String OBJECT_LIT = "OBJECT";
    public static final String OBJECTDESCRIPTOR_LIT = "ObjectDescriptor";
    public static final String OCTET_LIT = "OCTET";
    public static final String OF_LIT = "OF";
    public static final String OID_IRI_LIT = "OID-IRI";
    public static final String OPTIONAL_LIT = "OPTIONAL";
    public static final String PATTERN_LIT = "PATTERN";
    public static final String PDV_LIT = "PDV";
    public static final String PLUS_INFINITY_LIT = "PLUS-INFINITY";
    public static final String PRESENT_LIT = "PRESENT";
    public static final String PRINTABLESTRING_LIT = "PrintableString";
    public static final String PRIVATE_LIT = "PRIVATE";
    public static final String REAL_LIT = "REAL";
    public static final String RELATIVE_OID_LIT = "RELATIVE-OID";
    public static final String RELATIVE_OID_IRI_LIT = "RELATIVE-OID-IRI";
    public static final String SEQUENCE_LIT = "SEQUENCE";
    public static final String SET_LIT = "SET";
    public static final String SETTINGS_LIT = "SETTINGS";
    public static final String SIZE_LIT = "SIZE";
    public static final String STRING_LIT = "STRING";
    public static final String SYNTAX_LIT = "SYNTAX";
    public static final String T61STRING_LIT = "T61String";
    public static final String TAGS_LIT = "TAGS";
    public static final String TELETEXSTRING_LIT = "TeletexString";
    public static final String TIME_LIT = "TIME";
    public static final String TIME_OF_DAY_LIT = "TIME-OF-DAY";
    public static final String TRUE_LIT = "TRUE";
    public static final String TYPE_IDENTIFIER_LIT = "TYPE-IDENTIFIER";
    public static final String UNION_LIT = "UNION";
    public static final String UNIQUE_LIT = "UNIQUE";
    public static final String UNIVERSAL_LIT = "UNIVERSAL";
    public static final String UNIVERSALSTRING_LIT = "UniversalString";
    public static final String UTCTIME_LIT = "UTCTime";
    public static final String UTF8STRING_LIT = "UTF8String";
    public static final String VIDEOTEXSTRING_LIT = "VideotexString";
    public static final String VISIBLESTRING_LIT = "VisibleString";
    public static final String WITH_LIT = "WITH";

    /*
     * @formatter:off 
     * typereference: 1-n / letters, digits, hyphens / InitCap / !-$ && !-- 
     * identifier: 1-n / letters, digits, hyphens / !InitCap / !-$ && !-- 
     * valuereference: identifier 
     * modulereference: typereference 
     * psname: typereference 
     * encodingreference: typereference with all caps
     * objectclassreference: typereference with all caps
     * objectreference: valuereference
     * objectsetreference: typereference
     * typefieldreference: &typereference
     * valuefieldreference: &valuereference
     * valuesetfieldreference: &typereference
     * objectfieldreference: &objectreference
     * objectsetfieldreference: &objectsetreference
     * word: typereference without lower-case letters and digits
     */

    // @formatter:on
    @SuppressWarnings("serial")
    private static final Map<String, Token.TokenType> keywords = new HashMap<String, Token.TokenType>() {
    	{
    		put(ABSENT_LIT, Token.TokenType.ABSENT_KW);
    		put(ABSTRACT_SYNTAX_LIT, Token.TokenType.ABSTRACT_SYNTAX_KW);
    		put(ALL_LIT, Token.TokenType.ALL_KW);
    		put(APPLICATION_LIT, Token.TokenType.APPLICATION_KW);
    		put(AUTOMATIC_LIT, Token.TokenType.AUTOMATIC_KW);
    		put(BEGIN_LIT, Token.TokenType.BEGIN_KW);
    		put(BIT_LIT, Token.TokenType.BIT_KW);
    		put(BMPSTRING_LIT, Token.TokenType.BMPString_KW);
    		put(BOOLEAN_LIT, Token.TokenType.BOOLEAN_KW);
    		put(BY_LIT, Token.TokenType.BY_KW);
    		put(CHARACTER_LIT, Token.TokenType.CHARACTER_KW);
    		put(CHOICE_LIT, Token.TokenType.CHOICE_KW);
    		put(CLASS_LIT, Token.TokenType.CLASS_KW);
    		put(COMPONENT_LIT, Token.TokenType.COMPONENT_KW);
    		put(COMPONENTS_LIT, Token.TokenType.COMPONENTS_KW);
    		put(CONSTRAINED_LIT, Token.TokenType.CONSTRAINED_KW);
    		put(CONTAINING_LIT, Token.TokenType.CONTAINING_KW);
    		put(DATE_LIT, Token.TokenType.DATE_KW);
    		put(DATE_TIME_LIT, Token.TokenType.DATE_TIME_KW);
    		put(DEFAULT_LIT, Token.TokenType.DEFAULT_KW);
    		put(DEFINITIONS_LIT, Token.TokenType.DEFINITIONS_KW);
    		put(DURATION_LIT, Token.TokenType.DURATION_KW);
    		put(EMBEDDED_LIT, Token.TokenType.EMBEDDED_KW);
    		put(ENCODED_LIT, Token.TokenType.ENCODED_KW);
    		put(ENCODING_CONTROL_LIT, Token.TokenType.ENCODING_CONTROL_KW);
    		put(END_LIT, Token.TokenType.END_KW);
    		put(ENUMERATED_LIT, Token.TokenType.ENUMERATED_KW);
    		put(EXCEPT_LIT, Token.TokenType.EXCEPT_KW);
    		put(EXPLICIT_LIT, Token.TokenType.EXPLICIT_KW);
    		put(EXPORTS_LIT, Token.TokenType.EXPORTS_KW);
    		put(EXTENSIBILITY_LIT, Token.TokenType.EXTENSIBILITY_KW);
    		put(EXTERNAL_LIT, Token.TokenType.EXTERNAL_KW);
    		put(FALSE_LIT, Token.TokenType.FALSE_KW);
    		put(FROM_LIT, Token.TokenType.FROM_KW);
    		put(GENERALIZEDTIME_LIT, Token.TokenType.GeneralizedTime_KW);
    		put(GENERALSTRING_LIT, Token.TokenType.GeneralString_KW);
    		put(GRAPHICSTRING_LIT, Token.TokenType.GraphicString_KW);
    		put(IA5STRING_LIT, Token.TokenType.IA5String_KW);
    		put(IDENTIFIER_LIT, Token.TokenType.IDENTIFIER_KW);
    		put(IMPLICIT_LIT, Token.TokenType.IMPLICIT_KW);
    		put(IMPLIED_LIT, Token.TokenType.IMPLIED_KW);
    		put(IMPORTS_LIT, Token.TokenType.IMPORTS_KW);
    		put(INCLUDES_LIT, Token.TokenType.INCLUDES_KW);
    		put(INSTANCE_LIT, Token.TokenType.INSTANCE_KW);
    		put(INSTRUCTIONS_LIT, Token.TokenType.INSTRUCTIONS_KW);
    		put(INTEGER_LIT, Token.TokenType.INTEGER_KW);
    		put(INTERSECTION_LIT, Token.TokenType.INTERSECTION_KW);
    		put(ISO646STRING_LIT, Token.TokenType.ISO646String_KW);
    		put(MAX_LIT, Token.TokenType.MAX_KW);
    		put(MIN_LIT, Token.TokenType.MIN_KW);
    		put(MINUS_INFINITY_LIT, Token.TokenType.MINUS_INFINITY_KW);
    		put(NOT_A_NUMBER_LIT, Token.TokenType.NOT_A_NUMBER_KW);
    		put(NULL_LIT, Token.TokenType.NULL_KW);
    		put(NUMERICSTRING_LIT, Token.TokenType.NumericString_KW);
    		put(OBJECT_LIT, Token.TokenType.OBJECT_KW);
    		put(OBJECTDESCRIPTOR_LIT, Token.TokenType.ObjectDescriptor_KW);
    		put(OCTET_LIT, Token.TokenType.OCTET_KW);
    		put(OF_LIT, Token.TokenType.OF_KW);
    		put(OID_IRI_LIT, Token.TokenType.OID_IRI_KW);
    		put(OPTIONAL_LIT, Token.TokenType.OPTIONAL_KW);
    		put(PATTERN_LIT, Token.TokenType.PATTERN_KW);
    		put(PDV_LIT, Token.TokenType.PDV_KW);
    		put(PLUS_INFINITY_LIT, Token.TokenType.PLUS_INFINITY_KW);
    		put(PRESENT_LIT, Token.TokenType.PRESENT_KW);
    		put(PRINTABLESTRING_LIT, Token.TokenType.PrintableString_KW);
    		put(PRIVATE_LIT, Token.TokenType.PRIVATE_KW);
    		put(REAL_LIT, Token.TokenType.REAL_KW);
    		put(RELATIVE_OID_LIT, Token.TokenType.RELATIVE_OID_KW);
    		put(RELATIVE_OID_IRI_LIT, Token.TokenType.RELATIVE_OID_IRI_KW);
    		put(SEQUENCE_LIT, Token.TokenType.SEQUENCE_KW);
    		put(SET_LIT, Token.TokenType.SET_KW);
    		put(SETTINGS_LIT, Token.TokenType.SETTINGS_KW);
    		put(SIZE_LIT, Token.TokenType.SIZE_KW);
    		put(STRING_LIT, Token.TokenType.STRING_KW);
    		put(SYNTAX_LIT, Token.TokenType.SYNTAX_KW);
    		put(T61STRING_LIT, Token.TokenType.T61String_KW);
    		put(TAGS_LIT, Token.TokenType.TAGS_KW);
    		put(TELETEXSTRING_LIT, Token.TokenType.TeletexString_KW);
    		put(TIME_LIT, Token.TokenType.TIME_KW);
    		put(TIME_OF_DAY_LIT, Token.TokenType.TIME_OF_DAY_KW);
    		put(TRUE_LIT, Token.TokenType.TRUE_KW);
    		put(TYPE_IDENTIFIER_LIT, Token.TokenType.TYPE_IDENTIFIER_KW);
    		put(UNION_LIT, Token.TokenType.UNION_KW);
    		put(UNIQUE_LIT, Token.TokenType.UNIQUE_KW);
    		put(UNIVERSAL_LIT, Token.TokenType.UNIVERSAL_KW);
    		put(UNIVERSALSTRING_LIT, Token.TokenType.UniversalString_KW);
    		put(UTCTIME_LIT, Token.TokenType.UTCTime_KW);
    		put(UTF8STRING_LIT, Token.TokenType.UTF8String_KW);
    		put(VIDEOTEXSTRING_LIT, Token.TokenType.VideotexString_KW);
    		put(VISIBLESTRING_LIT, Token.TokenType.VisibleString_KW);
    		put(WITH_LIT, Token.TokenType.WITH_KW);
    	}
    };

    private LexerInputStream is;

    private int line = 1;

    private int pos = 0;

    private boolean eof = false;

    private LinkedList<Token> tokens = new LinkedList<Token>();

    public Lexer(InputStream is) throws IOException {
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	byte[] buf = new byte[8192];
    	int n;

    	while ((n = is.read(buf)) > 0) {
    		baos.write(buf, 0, n);
    	}

    	this.is = new LexerInputStream(baos.toString("UTF-8").toCharArray());
    }

    public Token nextToken(Context ctx) throws ParserException {
    	int c, offset;
    	Token token = null;

    	if (!tokens.isEmpty()) {
    		if (tokens.peek().getContext() == ctx) {
    			return tokens.pop();
    		} else {
    			token = tokens.pop();
    			is.seek(token.getOffset());
    			pos = token.getPos() - 1;
    			line = token.getLine();
    			tokens.clear();
    			token = null;
    			eof = false;
    		}
    	}

    	try {
    		loop: while (!eof) {
    			offset = is.getPos();
    			c = is.read();
    			pos++;

    			switch (c) {
    				case -1:
    					eof = true;
    					return null;
    				case ' ': // fall through
    				case '\t':
    					break;
    				case '\r': // fall through
    					if (is.read() != '\n') {
    						is.unread();
    					}
    				case 0x0b: // vertical tab
    				case '\f':
    				case '\n':
    					line++;
    					pos = 0;
    					break;
    				case '(':
    					return new Token(ctx, TokenType.LParen, offset, line,
    							pos);
    				case ')':
    					return new Token(ctx, TokenType.RParen, offset, line,
    							pos);
    				case '{':
    					return new Token(ctx, TokenType.LBrace, offset, line,
    							pos);
    				case '}':
    					return new Token(ctx, TokenType.RBrace, offset, line,
    							pos);
    				case '[':
    					if (ctx == Context.Syntax) {
    						return new Token(ctx, TokenType.LBracket, offset,
    								line, pos);
    					}

    					if (is.read() == '[') {
    						pos++;
    						return new Token(ctx, TokenType.LVersionBrackets,
    								offset, line, pos - 1);
    					} else {
    						is.unread();
    						return new Token(ctx, TokenType.LBracket, offset,
    								line, pos);
    					}
    				case ']':
    					if (ctx == Context.Syntax) {
    						return new Token(ctx, TokenType.RBracket, offset,
    								line, pos);
    					}

    					if (is.read() == ']') {
    						pos++;
    						return new Token(ctx, TokenType.RVersionBrackets,
    								offset, line, pos - 1);
    					} else {
    						is.unread();
    						return new Token(ctx, TokenType.RBracket, offset,
    								line, pos);
    					}
    				case ':':
    					switch (is.read()) {
    						case ':':
    							switch (is.read()) {
    								case '=':
    									pos += 2;
    									return new Token(ctx, TokenType.Assign,
    											offset, line, pos - 2);
    								default:
    									is.unread();
    							} // fall through
    						default:
    							is.unread();
    					}
    					return new Token(ctx, TokenType.Colon, offset, line,
    							pos);
    				case ';':
    					return new Token(ctx, TokenType.Semicolon, offset,
    							line, pos);
    				case ',':
    					return new Token(ctx, TokenType.Comma, offset, line,
    							pos);
    				case '|':
    					return new Token(ctx, TokenType.Pipe, offset, line, pos);
    				case '!':
    					return new Token(ctx, TokenType.Exclamation, offset,
    							line, pos);
    				case '*':
    					return new Token(ctx, TokenType.Asterisk, offset, line,
    							pos);
    				case '-':
    					if (is.read() == '-') {
    						pos++;
    						offset++;
    						skipComment();
    					} else {
    						is.unread();
    						return new Token(ctx, TokenType.Minus, offset,
    								line, pos);
    					}
    					break;
    				case '/':
    					if (is.read() == '*') {
    						pos++;
    						offset++;
    						skipMLComment();
    					} else {
    						is.unread();
    						return new Token(ctx, TokenType.Solidus, offset,
    								line, pos);
    					}
    					break;
    				case '&':
    					switch (ctx) {
    						case TypeField:
    							return parseFieldReference(Context.Normal,
    									TokenType.TypeReference,
    									TokenType.TypeFieldReference);
    						case ValueField:
    							return parseFieldReference(Context.Normal,
    									TokenType.Identifier,
    									TokenType.ValueFieldReference);
    						default:
    							return new Token(ctx, TokenType.Ampersand,
    									offset, line, pos);
    					}
    				case '^':
    					return new Token(ctx, TokenType.Circumflex, offset,
    							line, pos);
    				case '@':
    					return new Token(ctx, TokenType.AT, offset, line, pos);
    				case '<':
    					return new Token(ctx, TokenType.LT, offset, line, pos);
    				case '>':
    					return new Token(ctx, TokenType.GT, offset, line, pos);
    				case '=':
    					return new Token(ctx, TokenType.Equals, offset, line,
    							pos);
    				case '\'':
    					if ((token = parseNumString(ctx)) != null) {
    						return token;
    					}

    					return new Token(ctx, TokenType.Apostrophe, offset,
    							line, pos);
    				case '"':
    					if (ctx != Context.PropertySettings
    							&& (token = parseCharString(ctx)) != null) {
    						return token;
    					}

    					return new Token(ctx, TokenType.Quotation, offset,
    							line, pos);
    				case '.':
    					if (ctx != Context.Level) {
    						if (is.read() == '.') {
    							pos++;
    							offset++;
    							if (is.read() == '.') {
    								pos++;
    								offset++;
    								return new Token(ctx, TokenType.Ellipsis,
    										offset - 2, line, pos - 2);
    							} else {
    								is.unread();
    								return new Token(ctx, TokenType.Range,
    										offset - 1, line, pos - 1);
    							}
    						}

    						is.unread();
    					}

    					return new Token(ctx, TokenType.Dot, offset, line, pos);

    				default:
    					if ('0' <= c && c <= '9') {
    						if ((token = parseNumber(ctx, c)) != null) {
    							return token;
    						}
    					}

    					is.unread();
    					pos--;

    					if ((token = parseOther(ctx)) != null) {
    						return token;
    					}

    					break loop;
    			}
    		}
    	} catch (IOException e) {
    		throw new ParserException("Error at line " + line + ", position "
    				+ pos, e);
    	}

    	if (!eof) {
    		StringBuilder sb = new StringBuilder();

    		is.mark();

    		try {
    			outer: while ((c = is.read()) != -1) {
    				switch (c) {
    					case '\r':
    					case '\n':
    					case '\t':
    					case 0x0b: // vertical tab
    					case '\f':
    					case ' ':
    						break outer;
    					default:
    						sb.append((char) c);
    				}

    			}

    			throw new ParserException("Invalid token '" + sb.toString()
    					+ "' at line " + line + ", position " + (pos + 1));
    		} catch (IOException e) {
    			throw new ParserException("Invalid token at line " + line
    					+ ", position " + (pos + 1));
    		} finally {
    			is.reset();
    		}
    	}

    	return null;
    }

    private Token parseFieldReference(Context parseCtx, TokenType expected,
    		TokenType actual) throws IOException {
        Token token = parseOther(parseCtx);

        if (token != null) {
            token.offset--;
            token.pos--;
            token.text = "&" + token.text;

            if (token.type == expected) {
                token.type = actual;
            }
        }

        return token;
    }

    private Token parseCharString(Context ctx) throws IOException {
    	StringBuilder sb = new StringBuilder();
    	int c, cn;
    	int beginOffset = is.getPos() - 1;
    	int beginPos = pos;
    	int beginLine = line;
    	int tmpPos = pos;
    	int tmpLine = line;
    	int flags = StringToken.CSTRING | StringToken.TSTRING
    			| StringToken.SIMPLE_STRING;
    	is.mark();

    	loop: while (!eof) {
    		c = is.read();
    		tmpPos++;

    		switch (c) {
    			case -1:
    				break loop;
    			case 0x0b: // vertical tab
    			case '\f':
    			case '\r':
    			case '\n':
    				sb.append((char) c);
    				tmpLine++;
    				tmpPos = 0;
    				flags &= ~StringToken.TSTRING;
    				break;
    			case ' ':
    			case '\t':
    				sb.append((char) c);
    				flags &= ~StringToken.TSTRING;
    				break;
    			case '"':
    				if ((cn = is.read()) == '"') {
    					sb.append('"');
    					tmpPos++;
    					flags = StringToken.CSTRING;
    					break;
    				} else if (cn != -1) {
    					is.unread();
    				}

    				if (sb.length() == 0) {
    					flags &= ~(StringToken.SIMPLE_STRING | StringToken.TSTRING);
    				}

    				pos = tmpPos;
    				line = tmpLine;
    				return new StringToken(ctx, TokenType.CString, beginOffset,
    						beginLine, beginPos, sb.toString(), flags);
    			default:
    				if (c < 32 || c > 126) {
    					flags &= ~StringToken.SIMPLE_STRING;
    				}

    				switch (c) {
    					default:
    						flags &= ~StringToken.TSTRING;
    						break;
    					case '0':
    					case '1':
    					case '2':
    					case '3':
    					case '4':
    					case '5':
    					case '6':
    					case '7':
    					case '8':
    					case '9':
    					case '+':
    					case '-':
    					case ':':
    					case '.':
    					case ',':
    					case '/':
    					case 'C':
    					case 'D':
    					case 'H':
    					case 'M':
    					case 'R':
    					case 'P':
    					case 'S':
    					case 'T':
    					case 'W':
    					case 'Y':
    					case 'Z':
    						break;
    				}

    				sb.append((char) c);
    				break;

    		}
    	}

    	is.reset();
    	return null;
    }

    private Token parseNumber(Context ctx, int c) throws IOException {
    	StringBuilder sb = new StringBuilder();
    	TokenType type = TokenType.Number;
    	int beginOffset = is.getPos() - 1;
    	int beginPos = pos;
    	int tmpPos = pos;
    	int fractional = -1, exp = -1;
    	int cn;
    	is.mark();

    	sb.append((char) c);

    	loop: while (!eof) {
    		c = is.read();
    		tmpPos++;

    		switch (c) {
    			case 'e':
    			case 'E':
    				if (exp != -1) {
    					break loop;
    				}
    				type = TokenType.RealNumber;
    				exp = 0;
    				sb.append((char) c);
    				cn = is.read();
    				switch (cn) {
    					case '-':
    					case '+':
    						tmpPos++;
    						sb.append((char) cn);
    						break;
    					default:
    						is.unread();
    						break;
    				}
    				break;
    			case '.':
    				cn = is.read();
    				is.unread();

    				if (cn == '.') {
    					// fall through
    				} else {
    					if (fractional != -1) {
    						break loop;
    					}
    					type = TokenType.RealNumber;
    					fractional = 0;
    					sb.append((char) c);
    					break;
    				}
    			default:
    				if (c >= '0' && c <= '9') {
    					if (exp >= 0) {
    						if (exp == 1 && sb.charAt(sb.length() - 1) == '0') {
    							break loop;
    						}
    						exp++;
    					}
    					sb.append((char) c);
    				} else {
    					if (exp == 0) {
    						break loop;
    					}

    					if (type == TokenType.Number && sb.length() != 1
    							&& sb.charAt(0) == '0') {
    						type = TokenType.RealNumber;
    					}

    					if (c != -1) {
    						is.unread();
    					}
    					pos = tmpPos - 1;
    					return new Token(ctx, type, beginOffset, line,
    							beginPos, sb.toString());
    				}
    		}
    	}

    	is.reset();
    	return null;
    }

    private Token parseNumString(Context ctx) throws IOException {
    	StringBuilder sb = new StringBuilder();
    	int c, cn;
    	int beginOffset = is.getPos() - 1;
    	int beginPos = pos;
    	int tmpPos = pos;
    	TokenType type = TokenType.BString;

    	is.mark();

    	loop: while (!eof) {
    		c = is.read();
    		tmpPos++;

    		switch (c) {
    			case -1:
    				break loop;
    			case ' ':
    			case '\t':
    			case '\r':
    			case '\n':
    			case '\f':
    			case 0x0b:
    				break;
    			case '\'':
    				cn = is.read();
    				if (cn == 'H' || cn == 'B' && type == TokenType.BString) {
    					pos = tmpPos + 1;
    					return new Token(ctx, cn == 'H' ? TokenType.HString
    							: type, beginOffset, line, beginPos,
    							sb.toString());
    				}
    				break loop;
    			default:
    				if (c >= 'A' && c <= 'F' || c >= '0' && c <= '9') {
    					if (c != '0' && c != '1') {
    						type = TokenType.HString;
    					}
    					sb.append((char) c);
    				} else {
    					break loop;
    				}
    		}

    	}

    	is.reset();
    	return null;
    }

    private Token parseOther(Context ctx) throws IOException {
    	StringBuilder sb = new StringBuilder();
    	TokenType type;
    	int beginOffset = is.getPos();
    	int beginPos = pos + 1;
    	int tmpPos = pos;
    	int c, cn;
    	boolean allUc = true;
    	boolean digits = false;
    	is.mark();

    	// is.unread();

    	while (!eof) {
    		c = is.read();
    		tmpPos++;

    		if (c >= 'a' && c <= 'z') {
    			allUc = false;
    			sb.append((char) c);
    			continue;
    		} else if (c >= 'A' && c <= 'Z') {
    			sb.append((char) c);
    			continue;
    		} else if (c >= '0' && c <= '9') {
    			digits = true;
    			sb.append((char) c);
    			continue;
    		} else if (c == '-') {
    			cn = is.read();
    			if (cn == '-') {
    				is.unread();
    				is.unread();
    			} else {
    				if (cn != -1) {
    					is.unread();
    				}
    				sb.append((char) c);
    				continue;
    			}
    		} else if (c != -1) {
    			is.unread();
    		}

    		if (sb.length() == 0) {
    			throw new IOException(String.format("Unexpected token '%c'",
    					is.read()));
    		}

    		int fc = sb.charAt(0);

    		if (fc >= 'A' && fc <= 'Z') {
    			type = TokenType.TypeReference;
    		} else if (fc >= 'a' && fc <= 'z') {
    			type = TokenType.Identifier;
    		} else {
    			break;
    		}

    		if (sb.charAt(sb.length() - 1) == '-') {
    			break;
    		}

    		String str = sb.toString();

    		if (ctx != Context.Syntax) {
    			Token.TokenType kwType = keywords.get(str);

    			if (kwType != null) {
    				pos = tmpPos - 1;
    				return new Token(ctx, kwType, beginOffset, line, beginPos);
    			}
    		}

    		switch (ctx) {
    			case Encoding:
    				if (type == TokenType.TypeReference
    						&& ModuleNode.getEncoding(str) != null) {
    					type = TokenType.EncodingReference;
    					break;
    				}

    				is.reset();
    				return null;

    			case ObjectClass:
    				if (type == TokenType.TypeReference && allUc) {
    					type = TokenType.ObjectClassReference;
    					break;
    				}

    				is.reset();
    				return null;

    			case Syntax:
    				if (type == TokenType.TypeReference && allUc && !digits) {
    					type = TokenType.Word;
    					break;
    				}

    				is.reset();
    				return null;
    		}

    		String value = sb.toString();

    		if (type == TokenType.TypeReference) {
    			if ("PLUS-INFINITY".equals(value)) {
    				type = Token.TokenType.PLUS_INFINITY_KW;
    			} else if ("MINUS-INFINITY".equals(value)) {
    				type = Token.TokenType.MINUS_INFINITY_KW;
    			} else if ("NOT-A-NUMBER".equals(value)) {
    				type = Token.TokenType.NOT_A_NUMBER_KW;
    			}
    		}

    		pos = tmpPos - 1;
    		return new Token(ctx, type, beginOffset, line, beginPos, value);
    	}

    	is.reset();
    	return null;
    }

    private void skipMLComment() throws IOException {
    	int c;
    	int level = 1;

    	while (!eof) {
    		c = is.read();
    		pos++;

    		switch (c) {
    			case -1:
    				eof = true;
    				throw new IOException(
    						"Premature end-of-file in multi-line comment");
    			case '\r':
    				if (is.read() != '\n') {
    					is.unread();
    				}
    				line++;
    				pos = 0;
    				break;
    			case 0x0b: // vertical tab
    			case '\f':
    			case '\n':
    				line++;
    				pos = 0;
    				break;
    			case '/':
    				if (is.read() == '*') {
    					pos++;
    					level++;
    				} else {
    					is.unread();
    				}
    				break;
    			case '*':
    				if (is.read() == '/') {
    					pos++;
    					level--;
    					if (level == 0) {
    						return;
    					}
    				} else {
    					is.unread();
    				}
    				break;
    		}
    	}
    }

    private void skipComment() throws IOException {
    	int c;

    	while (!eof) {
    		c = is.read();
    		pos++;

    		switch (c) {
    			case -1:
    				eof = true;
    				return;
    			case '-':
    				if (is.read() == '-') {
    					pos++;
    					return;
    				} else {
    					is.unread();
    				}
    				break;
    			case '\r':
    				if (is.read() != '\n') {
    					is.unread();
    				}
    				line++;
    				pos = 0;
    				return;
    			case 0x0b: // vertical tab
    			case '\f':
    			case '\n':
    				line++;
    				pos = 0;
    				return;
    		}
    	}
    }

    public void pushBack(Token token) {
    	tokens.push(token);
    }

    public boolean isEOF() {
    	return eof;
    }

    public int getOffset() {
    	return is.getPos();
    }

}
