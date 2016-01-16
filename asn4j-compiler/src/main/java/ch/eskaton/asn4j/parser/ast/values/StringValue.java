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

package ch.eskaton.asn4j.parser.ast.values;

import ch.eskaton.asn4j.parser.ParserException;
import ch.eskaton.asn4j.parser.StringToken;
import ch.eskaton.commons.StringUtils;

public class StringValue implements Value {

	private String cString;

	private String simpleString;

	private String tString;

	private int flags;

	public StringValue(String value, int flags) {
		this.cString = value;
		this.flags = flags;

		if ((flags & StringToken.SIMPLE_STRING) != 0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < value.length(); i++) {
				char c = value.charAt(i);
				switch (c) {
					case '\n':
					case '\r':
					case '\f':
					case 0x0b:
						sb.append(" ");
						break;
					default:
						sb.append(c);
				}
			}

			this.simpleString = sb.toString();
		}

		if ((flags & StringToken.TSTRING) != 0) {
			this.tString = value;
		}

		StringBuilder sb = new StringBuilder();
		boolean skipWS = false;

		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			switch (c) {
				case '\n':
				case '\r':
				case '\f':
				case 0x0b:
					int length = sb.length();
					char ws;

					for (; length > 0; length--) {
						ws = sb.charAt(length - 1);

						if (!(ws == ' ' || ws == '\t')) {
							break;
						}
					}

					sb.setLength(length);

					skipWS = true;
					break;
				case ' ':
				case '\t':
					if (!skipWS) {
						sb.append(c);
					}
					break;
				default:
					sb.append(c);
					skipWS = false;
			}
		}

		this.cString = sb.toString();

	}

	public boolean isCString() {
		return (flags & StringToken.CSTRING) != 0;
	}

	public boolean isSimpleString() {
		return (flags & StringToken.SIMPLE_STRING) != 0;
	}

	public boolean isTString() {
		return (flags & StringToken.TSTRING) != 0;
	}

	public String getCString() {
		return cString;
	}

	public String getSimpleString() throws ParserException {
		if (simpleString == null) {
			throw new ParserException(
					"simpleString contains invalid characters or is empty");
		}

		return simpleString;
	}

	public TimeValue getTimeValue() throws ParserException {
		if (tString == null) {
			throw new ParserException(
					"tstring contains invalid characters or is empty");
		}

		return new TimeValue(tString);
	}

	@Override
	public String toString() {
		return StringUtils.concat("String[", simpleString, "]");
	}

}
