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
import ch.eskaton.asn4j.runtime.utils.ToString;

public class Token {

    public enum TokenType {
        MINUS, ELLIPSIS, DOT, RANGE, ASSIGN, COLON, AMPERSAND, AT, LT, GT,
        APOSTROPHE, ASTERISK, EXCLAMATION, PIPE, COMMA, SEMICOLON, L_PAREN,
        R_PAREN, L_BRACE, R_BRACE, L_BRACKET, R_BRACKET, L_VERSION_BRACKETS,
        R_VERSION_BRACKETS, EQUALS, IDENTIFIER, B_STRING, NUMBER, REAL_NUMBER,
        TYPE_REFERENCE, ABSENT_KW, ABSTRACT_SYNTAX_KW, ALL_KW, APPLICATION_KW,
        AUTOMATIC_KW, BEGIN_KW, BIT_KW, BMP_STRING_KW, BOOLEAN_KW, BY_KW,
        CHARACTER_KW, CHOICE_KW, CLASS_KW, COMPONENT_KW, COMPONENTS_KW,
        CONSTRAINED_KW, CONTAINING_KW, DATE_KW, DATE_TIME_KW, DEFAULT_KW,
        DEFINITIONS_KW, DURATION_KW, EMBEDDED_KW, ENCODED_KW,
        ENCODING_CONTROL_KW, END_KW, ENUMERATED_KW, EXCEPT_KW, EXPLICIT_KW,
        EXPORTS_KW, EXTENSIBILITY_KW, EXTERNAL_KW, FALSE_KW, FROM_KW,
        GENERALIZED_TIME_KW, GENERAL_STRING_KW, GRAPHIC_STRING_KW, IA5_STRING_KW,
        IDENTIFIER_KW, IMPLICIT_KW, IMPLIED_KW, IMPORTS_KW, INCLUDES_KW,
        INSTANCE_KW, INSTRUCTIONS_KW, INTEGER_KW, INTERSECTION_KW,
        ISO646_STRING_KW, MAX_KW, MIN_KW, MINUS_INFINITY_KW, NOT_A_NUMBER_KW,
        NULL_KW, NUMERIC_STRING_KW, OBJECT_KW, OBJECT_DESCRIPTOR_KW, OCTET_KW,
        OF_KW, OID_IRI_KW, OPTIONAL_KW, PATTERN_KW, PDV_KW, PLUS_INFINITY_KW,
        PRESENT_KW, PRINTABLE_STRING_KW, PRIVATE_KW, REAL_KW, RELATIVE_OID_KW,
        RELATIVE_OID_IRI_KW, SEQUENCE_KW, SET_KW, SETTINGS_KW, SIZE_KW,
        STRING_KW, SYNTAX_KW, T61_STRING_KW, TAGS_KW, TELETEX_STRING_KW, TIME_KW,
        TIME_OF_DAY_KW, TRUE_KW, TYPE_IDENTIFIER_KW, UNION_KW, UNIQUE_KW,
        UNIVERSAL_KW, UNIVERSAL_STRING_KW, UTC_TIME_KW, UTF8_STRING_KW,
        VIDEOTEX_STRING_KW, VISIBLE_STRING_KW, WITH_KW, QUOTATION, C_STRING,
        SOLIDUS, CIRCUMFLEX, H_STRING, ENCODING_REFERENCE, VALUE_REFERENCE,
        MODULE_REFERENCE, OBJECT_CLASS_REFERENCE, WORD, OBJECT_REFERENCE,
        OBJECT_SET_REFERENCE, VALUE_FIELD_REFERENCE, OBJECT_FIELD_REFERENCE,
        TYPE_FIELD_REFERENCE, VALUE_SET_FIELD_REFERENCE, OBJECT_SET_FIELD_REFERENCE
    }

    protected int offset;

    protected Position position;

    protected TokenType type;

    protected String text;

    protected Context context;

    public Token(Context context, TokenType type, int offset, Position position) {
        this(context, type, offset, position, null);
    }

    public Token(Context context, TokenType type, int offset, Position position, String text) {
        this.context = context;
        this.type = type;
        this.offset = offset;
        this.position = position;
        this.text = text;
    }

    public Context getContext() {
        return context;
    }

    public TokenType getType() {
        return type;
    }

    public int getOffset() {
        return offset;
    }

    public Position getPosition() {
        return position;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return ToString.get(this);
    }

}
