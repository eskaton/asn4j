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

import java.util.ArrayList;
import java.util.List;

import ch.eskaton.commons.utils.StringUtils;

public class BitStringValue implements Value {

	private int stringValue;

	private List<String> namedValues;

	private Value value;

	public BitStringValue() {
		this.namedValues = new ArrayList<String>();
	}

	public BitStringValue(int stringValue) {
		this.stringValue = stringValue;
	}

	public BitStringValue(List<String> namedValues) {
		this.namedValues = namedValues;
	}

	public BitStringValue(Value value) {
		this.value = value;
	}

	public boolean isStringValue() {
		return namedValues == null && value == null;
	}

	public int getStringValue() {
		return stringValue;
	}

	public Value getValue() {
		return value;
	}

	public List<String> getNamedValues() {
		return namedValues;
	}

	@Override
	public String toString() {
		return StringUtils.concat(
				"BitStringValue[",
				isStringValue() ? Integer.toBinaryString(stringValue)
						: (namedValues != null ? StringUtils.join(namedValues,
								", ") : value), "]");
	}

}
