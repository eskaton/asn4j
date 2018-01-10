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

import java.io.IOException;

import ch.eskaton.asn4j.parser.IRIToken.Type;

public class IRILexer {

    private LexerInputStream is;

    private int pos = 0;

    private boolean eof = false;

    public IRILexer(String s) {
    	is = new LexerInputStream(s.toCharArray());
    }

    public IRIToken nextToken() throws ParserException {
    	boolean nonInteger = false;
    	int c;

    	if (eof) {
    		return null;
    	}

    	try {
    		c = is.read();
    		pos++;

    		if (c == -1) {
    			eof = true;
    		} else if (c == '/') {
    			return new IRIToken(Type.Solidus, pos);
    		} else if (c == '-') {
    			throw new ParserException(
    					"A non-integerUnicodeLabel must not start with a hyphen");
    		} else {
    			StringBuilder sb = new StringBuilder();

    			sb.append((char) c);

    			do {
    				if (c < '0' || c > '9') {
    					nonInteger = true;
    				}

    				c = is.read();
    				pos++;

    				if (c == -1) {
    					eof = true;
    				} else if (c == '/') {
    					is.unread();
    					pos--;
    					break;
    				} else {
    					switch (c) {
    						case '-':
    							if (pos == 3) {
    								int cn = is.read();

    								if (cn == '-') {
    									throw new ParserException(
    											"A non-integerUnicodeLabel must not contain two hyphens in the third and fourth position");
    								}
    							}

    						case '.':
    						case '_':
    						case '~':
    							sb.append((char) c);
    							break;
    						default:
    							if (c >= '0' && c <= '9' || c >= 'a'
    									&& c <= 'z' || c >= 'A' && c <= 'Z') {
    								sb.append((char) c);
    							} else {
    								int cp, cn;

    								if (Character.isHighSurrogate((char) c)) {
    									cn = is.read();
    									pos++;

    									if (cn == -1) {
    										eof = true;
    										throw new ParserException(
    												"Premature end of string: a low surrogate character was expected");
    									} else if (!Character
    											.isLowSurrogate((char) cn)) {
    										throw new ParserException(
    												"A low surrogate character was expected, but found "
    														+ (char) cn);
    									}

    									cp = Character.toCodePoint((char) c,
    											(char) cn);
    								} else {
    									cp = c;
    								}

    								if (cp >= 0x000a0 && cp <= 0x0dffe
    										|| cp >= 0x0f900 && cp <= 0x0fdcf
    										|| cp >= 0x0fdf0 && cp <= 0x0ffef
    										|| cp >= 0x10000 && cp <= 0x1fffd
    										|| cp >= 0x20000 && cp <= 0x2fffd
    										|| cp >= 0x30000 && cp <= 0x3fffd
    										|| cp >= 0x40000 && cp <= 0x4fffd
    										|| cp >= 0x50000 && cp <= 0x5fffd
    										|| cp >= 0x60000 && cp <= 0x6fffd
    										|| cp >= 0x70000 && cp <= 0x7fffd
    										|| cp >= 0x80000 && cp <= 0x8fffd
    										|| cp >= 0x90000 && cp <= 0x9fffd
    										|| cp >= 0xa0000 && cp <= 0xafffd
    										|| cp >= 0xb0000 && cp <= 0xbfffd
    										|| cp >= 0xc0000 && cp <= 0xcfffd
    										|| cp >= 0xd0000 && cp <= 0xdfffd
    										|| cp >= 0xe0000 && cp <= 0xefffd) {
    									sb.append(Character.toChars(cp));
    								} else {
    									throw new ParserException(
    											String.format(
    													"Invalid character in non-integerUnicodeLabel found: %c",
    													cp));
    								}
    							}
    					}
    				}

    			} while (!eof);

    			if (nonInteger) {
    				if (sb.charAt(sb.length() - 1) == '-') {
    					throw new ParserException(
    							"A non-integerUnicodeLabel must not end with a hyphen");
    				}

    				return new IRIToken(Type.NonIntegerUnicodeLabel, pos,
    						sb.toString());
    			} else {
    				if (sb.length() > 1 && sb.charAt(0) == '0') {
    					throw new ParserException(
    							"Invalid integerUnicodeLabel: " + sb.toString());
    				}

    				return new IRIToken(Type.IntegerUnicodeLabel, pos,
    						sb.toString());
    			}

    		}

    	} catch (IOException e) {
    		throw new ParserException(e);
    	}

    	return null;
    }

}
