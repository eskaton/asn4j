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
import ch.eskaton.asn4j.runtime.parsing.LexerInputStream;
import ch.eskaton.commons.collections.Maps;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Map;

public class Lexer {

    public enum Context {
        NORMAL, ENCODING, OBJECT_CLASS, SYNTAX, TYPE_FIELD, VALUE_FIELD, LEVEL, PROPERTY_SETTINGS
    }

    // @formatter:off
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
    // @formatter:on
    
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
    private static final Map<String, TokenType> keywords = Maps.<String, TokenType>builder()
            .put(ABSENT_LIT, TokenType.ABSENT_KW)
            .put(ABSTRACT_SYNTAX_LIT, TokenType.ABSTRACT_SYNTAX_KW)
            .put(ALL_LIT, TokenType.ALL_KW)
            .put(APPLICATION_LIT, TokenType.APPLICATION_KW)
            .put(AUTOMATIC_LIT, TokenType.AUTOMATIC_KW)
            .put(BEGIN_LIT, TokenType.BEGIN_KW)
            .put(BIT_LIT, TokenType.BIT_KW)
            .put(BMPSTRING_LIT, TokenType.BMP_STRING_KW)
            .put(BOOLEAN_LIT, TokenType.BOOLEAN_KW)
            .put(BY_LIT, TokenType.BY_KW)
            .put(CHARACTER_LIT, TokenType.CHARACTER_KW)
            .put(CHOICE_LIT, TokenType.CHOICE_KW)
            .put(CLASS_LIT, TokenType.CLASS_KW)
            .put(COMPONENT_LIT, TokenType.COMPONENT_KW)
            .put(COMPONENTS_LIT, TokenType.COMPONENTS_KW)
            .put(CONSTRAINED_LIT, TokenType.CONSTRAINED_KW)
            .put(CONTAINING_LIT, TokenType.CONTAINING_KW)
            .put(DATE_LIT, TokenType.DATE_KW)
            .put(DATE_TIME_LIT, TokenType.DATE_TIME_KW)
            .put(DEFAULT_LIT, TokenType.DEFAULT_KW)
            .put(DEFINITIONS_LIT, TokenType.DEFINITIONS_KW)
            .put(DURATION_LIT, TokenType.DURATION_KW)
            .put(EMBEDDED_LIT, TokenType.EMBEDDED_KW)
            .put(ENCODED_LIT, TokenType.ENCODED_KW)
            .put(ENCODING_CONTROL_LIT, TokenType.ENCODING_CONTROL_KW)
            .put(END_LIT, TokenType.END_KW)
            .put(ENUMERATED_LIT, TokenType.ENUMERATED_KW)
            .put(EXCEPT_LIT, TokenType.EXCEPT_KW)
            .put(EXPLICIT_LIT, TokenType.EXPLICIT_KW)
            .put(EXPORTS_LIT, TokenType.EXPORTS_KW)
            .put(EXTENSIBILITY_LIT, TokenType.EXTENSIBILITY_KW)
            .put(EXTERNAL_LIT, TokenType.EXTERNAL_KW)
            .put(FALSE_LIT, TokenType.FALSE_KW)
            .put(FROM_LIT, TokenType.FROM_KW)
            .put(GENERALIZEDTIME_LIT, TokenType.GENERALIZED_TIME_KW)
            .put(GENERALSTRING_LIT, TokenType.GENERAL_STRING_KW)
            .put(GRAPHICSTRING_LIT, TokenType.GRAPHIC_STRING_KW)
            .put(IA5STRING_LIT, TokenType.IA5_STRING_KW)
            .put(IDENTIFIER_LIT, TokenType.IDENTIFIER_KW)
            .put(IMPLICIT_LIT, TokenType.IMPLICIT_KW)
            .put(IMPLIED_LIT, TokenType.IMPLIED_KW)
            .put(IMPORTS_LIT, TokenType.IMPORTS_KW)
            .put(INCLUDES_LIT, TokenType.INCLUDES_KW)
            .put(INSTANCE_LIT, TokenType.INSTANCE_KW)
            .put(INSTRUCTIONS_LIT, TokenType.INSTRUCTIONS_KW)
            .put(INTEGER_LIT, TokenType.INTEGER_KW)
            .put(INTERSECTION_LIT, TokenType.INTERSECTION_KW)
            .put(ISO646STRING_LIT, TokenType.ISO646_STRING_KW)
            .put(MAX_LIT, TokenType.MAX_KW)
            .put(MIN_LIT, TokenType.MIN_KW)
            .put(MINUS_INFINITY_LIT, TokenType.MINUS_INFINITY_KW)
            .put(NOT_A_NUMBER_LIT, TokenType.NOT_A_NUMBER_KW)
            .put(NULL_LIT, TokenType.NULL_KW)
            .put(NUMERICSTRING_LIT, TokenType.NUMERIC_STRING_KW)
            .put(OBJECT_LIT, TokenType.OBJECT_KW)
            .put(OBJECTDESCRIPTOR_LIT, TokenType.OBJECT_DESCRIPTOR_KW)
            .put(OCTET_LIT, TokenType.OCTET_KW)
            .put(OF_LIT, TokenType.OF_KW)
            .put(OID_IRI_LIT, TokenType.OID_IRI_KW)
            .put(OPTIONAL_LIT, TokenType.OPTIONAL_KW)
            .put(PATTERN_LIT, TokenType.PATTERN_KW)
            .put(PDV_LIT, TokenType.PDV_KW)
            .put(PLUS_INFINITY_LIT, TokenType.PLUS_INFINITY_KW)
            .put(PRESENT_LIT, TokenType.PRESENT_KW)
            .put(PRINTABLESTRING_LIT, TokenType.PRINTABLE_STRING_KW)
            .put(PRIVATE_LIT, TokenType.PRIVATE_KW)
            .put(REAL_LIT, TokenType.REAL_KW)
            .put(RELATIVE_OID_LIT, TokenType.RELATIVE_OID_KW)
            .put(RELATIVE_OID_IRI_LIT, TokenType.RELATIVE_OID_IRI_KW)
            .put(SEQUENCE_LIT, TokenType.SEQUENCE_KW)
            .put(SET_LIT, TokenType.SET_KW)
            .put(SETTINGS_LIT, TokenType.SETTINGS_KW)
            .put(SIZE_LIT, TokenType.SIZE_KW)
            .put(STRING_LIT, TokenType.STRING_KW)
            .put(SYNTAX_LIT, TokenType.SYNTAX_KW)
            .put(T61STRING_LIT, TokenType.T61_STRING_KW)
            .put(TAGS_LIT, TokenType.TAGS_KW)
            .put(TELETEXSTRING_LIT, TokenType.TELETEX_STRING_KW)
            .put(TIME_LIT, TokenType.TIME_KW)
            .put(TIME_OF_DAY_LIT, TokenType.TIME_OF_DAY_KW)
            .put(TRUE_LIT, TokenType.TRUE_KW)
            .put(TYPE_IDENTIFIER_LIT, TokenType.TYPE_IDENTIFIER_KW)
            .put(UNION_LIT, TokenType.UNION_KW)
            .put(UNIQUE_LIT, TokenType.UNIQUE_KW)
            .put(UNIVERSAL_LIT, TokenType.UNIVERSAL_KW)
            .put(UNIVERSALSTRING_LIT, TokenType.UNIVERSAL_STRING_KW)
            .put(UTCTIME_LIT, TokenType.UTC_TIME_KW)
            .put(UTF8STRING_LIT, TokenType.UTF8_STRING_KW)
            .put(VIDEOTEXSTRING_LIT, TokenType.VIDEOTEX_STRING_KW)
            .put(VISIBLESTRING_LIT, TokenType.VISIBLE_STRING_KW)
            .put(WITH_LIT, TokenType.WITH_KW)
            .build();

    private LexerInputStream is;

    private String moduleFile = "<stream>";

    private int line = 1;

    private int pos = 0;

    private boolean eof = false;

    private LinkedList<Token> tokens = new LinkedList<>();

    public Lexer(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int n;

        while ((n = is.read(buf)) > 0) {
            baos.write(buf, 0, n);
        }

        this.is = new LexerInputStream(baos.toString(StandardCharsets.UTF_8).toCharArray());
    }

    public Lexer(String moduleFile) throws IOException {
        this(new FileInputStream(moduleFile));
        this.moduleFile = moduleFile;
    }

    public Token nextToken(Context ctx) throws ParserException {
        int c, offset;
        Token token;

        if (!tokens.isEmpty()) {
            if (tokens.peek().getContext() == ctx) {
                return tokens.pop();
            } else {
                token = tokens.pop();
                is.seek(token.getOffset());
                pos = token.getPosition().getPosition() - 1;
                line = token.getPosition().getLine();
                tokens.clear();
                eof = false;
            }
        }

        try {
            loop:
            while (!eof) {
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
                        return new Token(ctx, TokenType.L_PAREN, offset, position(line, pos));
                    case ')':
                        return new Token(ctx, TokenType.R_PAREN, offset, position(line, pos));
                    case '{':
                        return new Token(ctx, TokenType.L_BRACE, offset, position(line, pos));
                    case '}':
                        return new Token(ctx, TokenType.R_BRACE, offset, position(line, pos));
                    case '[':
                        if (ctx == Context.SYNTAX) {
                            return new Token(ctx, TokenType.L_BRACKET, offset, position(line, pos));
                        }

                        if (is.read() == '[') {
                            pos++;
                            return new Token(ctx, TokenType.L_VERSION_BRACKETS, offset, position(line, pos - 1));
                        } else {
                            is.unread();
                            return new Token(ctx, TokenType.L_BRACKET, offset, position(line, pos));
                        }
                    case ']':
                        if (ctx == Context.SYNTAX) {
                            return new Token(ctx, TokenType.R_BRACKET, offset, position(line, pos));
                        }

                        if (is.read() == ']') {
                            pos++;
                            return new Token(ctx, TokenType.R_VERSION_BRACKETS, offset, position(line, pos - 1));
                        } else {
                            is.unread();
                            return new Token(ctx, TokenType.R_BRACKET, offset, position(line, pos));
                        }
                    case ':':
                        switch (is.read()) {
                            case ':':
                                switch (is.read()) {
                                    case '=':
                                        pos += 2;
                                        return new Token(ctx, TokenType.ASSIGN, offset, position(line, pos - 2));
                                    default:
                                        is.unread();
                                } // fall through
                            default:
                                is.unread();
                        }
                        return new Token(ctx, TokenType.COLON, offset, position(line, pos));
                    case ';':
                        return new Token(ctx, TokenType.SEMICOLON, offset, position(line, pos));
                    case ',':
                        return new Token(ctx, TokenType.COMMA, offset, position(line, pos));
                    case '|':
                        return new Token(ctx, TokenType.PIPE, offset, position(line, pos));
                    case '!':
                        return new Token(ctx, TokenType.EXCLAMATION, offset, position(line, pos));
                    case '*':
                        return new Token(ctx, TokenType.ASTERISK, offset, position(line, pos));
                    case '-':
                        if (is.read() == '-') {
                            pos++;
                            skipComment();
                        } else {
                            is.unread();
                            return new Token(ctx, TokenType.MINUS, offset, position(line, pos));
                        }
                        break;
                    case '/':
                        if (is.read() == '*') {
                            pos++;
                            skipMLComment();
                        } else {
                            is.unread();
                            return new Token(ctx, TokenType.SOLIDUS, offset, position(line, pos));
                        }
                        break;
                    case '&':
                        switch (ctx) {
                            case TYPE_FIELD:
                                return parseFieldReference(Context.NORMAL, TokenType.TYPE_REFERENCE,
                                        TokenType.TYPE_FIELD_REFERENCE);
                            case VALUE_FIELD:
                                return parseFieldReference(Context.NORMAL, TokenType.IDENTIFIER,
                                        TokenType.VALUE_FIELD_REFERENCE);
                            default:
                                return new Token(ctx, TokenType.AMPERSAND, offset, position(line, pos));
                        }
                    case '^':
                        return new Token(ctx, TokenType.CIRCUMFLEX, offset, position(line, pos));
                    case '@':
                        return new Token(ctx, TokenType.AT, offset, position(line, pos));
                    case '<':
                        return new Token(ctx, TokenType.LT, offset, position(line, pos));
                    case '>':
                        return new Token(ctx, TokenType.GT, offset, position(line, pos));
                    case '=':
                        return new Token(ctx, TokenType.EQUALS, offset, position(line, pos));
                    case '\'':
                        if ((token = parseNumString(ctx)) != null) {
                            return token;
                        }

                        return new Token(ctx, TokenType.APOSTROPHE, offset, position(line, pos));
                    case '"':
                        if (ctx != Context.PROPERTY_SETTINGS && (token = parseCharString(ctx)) != null) {
                            return token;
                        }

                        return new Token(ctx, TokenType.QUOTATION, offset, position(line, pos));
                    case '.':
                        if (ctx != Context.LEVEL) {
                            if (is.read() == '.') {
                                pos++;
                                offset++;
                                if (is.read() == '.') {
                                    pos++;
                                    offset++;
                                    return new Token(ctx, TokenType.ELLIPSIS, offset - 2,
                                            position(line, pos - 2));
                                } else {
                                    is.unread();
                                    return new Token(ctx, TokenType.RANGE, offset - 1,
                                            position(line, pos - 1));
                                }
                            }

                            is.unread();
                        }

                        return new Token(ctx, TokenType.DOT, offset, position(line, pos));

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
            throw new ParserException("Error at line " + line + ", position " + pos, e);
        }

        if (!eof) {
            StringBuilder sb = new StringBuilder();

            is.mark();

            try {
                outer:
                while ((c = is.read()) != -1) {
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

                throw new ParserException("Invalid token '" + sb.toString() + "' at line " + line +
                        ", position " + (pos + 1));
            } catch (IOException e) {
                throw new ParserException("Invalid token at line " + line + ", position " + (pos + 1));
            } finally {
                is.reset();
            }
        }

        return null;
    }

    private Token parseFieldReference(Context parseCtx, TokenType expected, TokenType actual) throws IOException {
        Token token = parseOther(parseCtx);

        if (token != null) {
            token.offset--;
            token.getPosition().decrementPosition();
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
        int flags = StringToken.CSTRING | StringToken.TSTRING | StringToken.SIMPLE_STRING;
        is.mark();

        loop:
        while (!eof) {
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
                    return new StringToken(ctx, TokenType.C_STRING, beginOffset, position(beginLine, beginPos),
                            sb.toString(), flags);
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
        TokenType type = TokenType.NUMBER;
        int beginOffset = is.getPos() - 1;
        int beginPos = pos;
        int tmpPos = pos;
        int fractional = -1, exp = -1;
        int cn;
        is.mark();

        sb.append((char) c);

        loop:
        while (!eof) {
            c = is.read();
            tmpPos++;

            switch (c) {
                case 'e':
                case 'E':
                    if (exp != -1) {
                        break loop;
                    }

                    type = TokenType.REAL_NUMBER;
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

                        type = TokenType.REAL_NUMBER;
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

                        if (type == TokenType.NUMBER && sb.length() != 1 && sb.charAt(0) == '0') {
                            type = TokenType.REAL_NUMBER;
                        }

                        if (c != -1) {
                            is.unread();
                        }

                        pos = tmpPos - 1;

                        return new Token(ctx, type, beginOffset, position(line, beginPos), sb.toString());
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
        TokenType type = TokenType.B_STRING;

        is.mark();

        loop:
        while (!eof) {
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
                    if (cn == 'H' || cn == 'B' && type == TokenType.B_STRING) {
                        pos = tmpPos + 1;
                        return new Token(ctx, cn == 'H' ? TokenType.H_STRING : type, beginOffset,
                                position(line, beginPos), sb.toString());
                    }
                    break loop;
                default:
                    if (c >= 'A' && c <= 'F' || c >= '0' && c <= '9') {
                        if (c != '0' && c != '1') {
                            type = TokenType.H_STRING;
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
                throw new IOException(String.format("Unexpected token '%c'", is.read()));
            }

            int fc = sb.charAt(0);

            if (fc >= 'A' && fc <= 'Z') {
                type = TokenType.TYPE_REFERENCE;
            } else if (fc >= 'a' && fc <= 'z') {
                type = TokenType.IDENTIFIER;
            } else {
                break;
            }

            if (sb.charAt(sb.length() - 1) == '-') {
                break;
            }

            String str = sb.toString();

            if (ctx != Context.SYNTAX) {
                Token.TokenType kwType = keywords.get(str);

                if (kwType != null) {
                    pos = tmpPos - 1;
                    return new Token(ctx, kwType, beginOffset, position(line, beginPos));
                }
            }

            switch (ctx) {
                case ENCODING:
                    if (type == TokenType.TYPE_REFERENCE && ModuleNode.getEncoding(str) != null) {
                        type = TokenType.ENCODING_REFERENCE;
                        break;
                    }

                    is.reset();
                    return null;

                case OBJECT_CLASS:
                    if (type == TokenType.TYPE_REFERENCE && allUc) {
                        type = TokenType.OBJECT_CLASS_REFERENCE;
                        break;
                    }

                    is.reset();
                    return null;

                case SYNTAX:
                    if (type == TokenType.TYPE_REFERENCE && allUc && !digits) {
                        type = TokenType.WORD;
                        break;
                    }

                    is.reset();
                    return null;
            }

            String value = sb.toString();

            if (type == TokenType.TYPE_REFERENCE) {
                if (PLUS_INFINITY_LIT.equals(value)) {
                    type = Token.TokenType.PLUS_INFINITY_KW;
                } else if (MINUS_INFINITY_LIT.equals(value)) {
                    type = Token.TokenType.MINUS_INFINITY_KW;
                } else if (NOT_A_NUMBER_LIT.equals(value)) {
                    type = Token.TokenType.NOT_A_NUMBER_KW;
                }
            }

            pos = tmpPos - 1;
            return new Token(ctx, type, beginOffset, position(line, beginPos), value);
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
                    throw new IOException("Premature end-of-file in multi-line comment");
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
                default:
                    unexpectedTokenError(c);
                    return;
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

    private Position position(int line, int pos) {
        return new Position(moduleFile, line, pos);
    }

    private void unexpectedTokenError(int c) {
        throw new IllegalStateException("Unexpected token" + c);
    }

}
