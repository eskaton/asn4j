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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class IRILexerTest {

    @Test
    void testSolidus() throws ParserException {
    	IRILexer lexer = new IRILexer("/");

    	assertEquals(IRIToken.Type.SOLIDUS, lexer.nextToken().getType());
    }

    @Test
    void testIntegerUnicodeLabel() throws ParserException {
    	IRILexer lexer = new IRILexer("0");

    	IRIToken token = lexer.nextToken();

    	assertEquals(IRIToken.Type.INTEGER_UNICODE_LABEL, token.getType());
    	assertEquals("0", token.getText());

    	lexer = new IRILexer("1234567890");

    	token = lexer.nextToken();

    	assertEquals(IRIToken.Type.INTEGER_UNICODE_LABEL, token.getType());
    	assertEquals("1234567890", token.getText());

    	lexer = new IRILexer("01");

    	try {
    		token = lexer.nextToken();
    		fail("ASN1ParserException expected");
    	} catch (ParserException e) {
    	}
    }

    @Test
    void testNonIntegerUnicodeLabel() throws ParserException {
    	IRILexer lexer = new IRILexer("abc\ud841\udf0e");

    	IRIToken token = lexer.nextToken();

    	assertEquals(IRIToken.Type.NON_INTEGER_UNICODE_LABEL, token.getType());
    	assertEquals("abc\ud841\udf0e", token.getText());

    	lexer = new IRILexer("-invalid");

    	try {
    		lexer.nextToken();
    		fail("ASN1ParserException expected");
    	} catch (ParserException e) {
    	}

    	lexer = new IRILexer("invalid-");

    	try {
    		lexer.nextToken();
    		fail("ASN1ParserException expected");
    	} catch (ParserException e) {
    	}

    	lexer = new IRILexer("in--valid");

    	try {
    		lexer.nextToken();
    		fail("ASN1ParserException expected");
    	} catch (ParserException e) {
    	}

    	lexer = new IRILexer("\ud841\udf0ex--");

    	try {
    		lexer.nextToken();
    		fail("ASN1ParserException expected");
    	} catch (ParserException e) {
    	}
    }

}
