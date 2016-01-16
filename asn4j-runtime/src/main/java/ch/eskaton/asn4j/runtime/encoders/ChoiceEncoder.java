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

package ch.eskaton.asn4j.runtime.encoders;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;

import ch.eskaton.asn4j.runtime.Encoder;
import ch.eskaton.asn4j.runtime.EncodingException;
import ch.eskaton.asn4j.runtime.TLVUtils;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.types.ASN1Choice;
import ch.eskaton.asn4j.runtime.types.ASN1Type;

public class ChoiceEncoder implements TypeEncoder<ASN1Choice> {

	public byte[] encode(Encoder encoder, ASN1Choice obj)
			throws EncodingException {
		// TODO: choice
		ByteArrayOutputStream content = new ByteArrayOutputStream();

		ASN1Type value = ((ASN1Choice) obj).getValue();
		ASN1Tag tag = null;

		// TODO: find field
		for (Field field : obj.getClass().getDeclaredFields()) {
			if (field.getType().equals(value.getClass())) {
				tag = field.getAnnotation(ASN1Tag.class);
				break;
			}
		}

		try {
			if (tag != null) {
				ByteArrayOutputStream fieldContent = new ByteArrayOutputStream();
				fieldContent.write(encoder.encode((ASN1Type) value,
						tag.mode() == ASN1Tag.Mode.Implicit));
				content.write(TLVUtils.getTag(tag));
				content.write(TLVUtils.getLength(fieldContent.size()));
				content.write(fieldContent.toByteArray());
			} else {
				content.write(encoder.encode((ASN1Type) value));
			}
		} catch (Throwable e) {
			throw new EncodingException(e);
		}

		// Since the choice itself doesn't need to be encoded, we return its
		// value here
		return content.toByteArray();
	}

}
