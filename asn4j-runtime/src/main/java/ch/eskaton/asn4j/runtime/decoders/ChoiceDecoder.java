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

package ch.eskaton.asn4j.runtime.decoders;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import ch.eskaton.asn4j.runtime.Decoder;
import ch.eskaton.asn4j.runtime.DecoderState;
import ch.eskaton.asn4j.runtime.DecoderStates;
import ch.eskaton.asn4j.runtime.DecodingException;
import ch.eskaton.asn4j.runtime.TLV;
import ch.eskaton.asn4j.runtime.Utils;
import ch.eskaton.asn4j.runtime.annotations.ASN1Alternative;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.commons.StringUtils;

public class ChoiceDecoder {

	private Utils berUtils = new Utils();

	public <T extends ASN1Type> T decode(Decoder decoder, Class<T> type,
			DecoderStates states) throws DecodingException {
		DecoderState lastState = states.states.peek();
		int pos = lastState.pos;
		int length = lastState.length;

		try {
			TLV tlv = TLV.getTLV(states.buf, pos, length);

			T obj = type.newInstance();

			List<Field> altFields = berUtils.getComponents(obj);

			for (Field altField : altFields) {
				ASN1Alternative annotation = altField
						.getAnnotation(ASN1Alternative.class);
				if (annotation != null) {
					ASN1Tag tag = altField.getAnnotation(ASN1Tag.class);

					if (tag != null) {
						if (!tag.clazz().equals(tlv.clazz)
								|| tag.tag() != tlv.tag) {
							continue;
						}
					} else {
						@SuppressWarnings("unchecked")
						List<ASN1Tag> tags = berUtils
								.getTags((Class<? extends ASN1Type>) altField
										.getType());
						// TODO: Multiple tags? explicit encoding?

						if (tags.isEmpty()) {
							continue;
						} else {
							// ASN1Tag firstTag = tags.peek();
							ASN1Tag firstTag = tags.get(tags.size() - 1);

							if (firstTag.clazz().equals(tlv.clazz)
									|| firstTag.tag() != tlv.tag) {
								continue;
							}
						}
					}

					@SuppressWarnings("unchecked")
					ASN1Type value = decoder.decode(
							(Class<? extends ASN1Type>) altField.getType(),
							states, tag, false);

					try {
						Method m = obj.getClass()
								.getMethod(
										StringUtils.concat("set", StringUtils
												.initCap(altField.getName())),
										new Class<?>[] { value.getClass() });
						m.invoke(obj, value);
						return obj;
					} catch (SecurityException e) {
						throw new DecodingException(e);
					} catch (NoSuchMethodException e) {
						throw new DecodingException(e);
					} catch (IllegalArgumentException e) {
						throw new DecodingException(e);
					} catch (InvocationTargetException e) {
						throw new DecodingException(e);
					}
				}
			}

		} catch (IOException e) {
			throw new DecodingException(e);
		} catch (InstantiationException e) {
			throw new DecodingException(e);
		} catch (IllegalAccessException e) {
			throw new DecodingException(e);
		}

		return null;
	}

}
