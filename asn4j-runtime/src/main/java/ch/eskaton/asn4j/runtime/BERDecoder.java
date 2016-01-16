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

package ch.eskaton.asn4j.runtime;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.decoders.BitStringDecoder;
import ch.eskaton.asn4j.runtime.decoders.ChoiceDecoder;
import ch.eskaton.asn4j.runtime.decoders.EnumeratedTypeDecoder;
import ch.eskaton.asn4j.runtime.decoders.IntegerDecoder;
import ch.eskaton.asn4j.runtime.decoders.OIDDecoder;
import ch.eskaton.asn4j.runtime.decoders.OctetStringDecoder;
import ch.eskaton.asn4j.runtime.decoders.SequenceDecoder;
import ch.eskaton.asn4j.runtime.decoders.SequenceOfDecoder;
import ch.eskaton.asn4j.runtime.decoders.SetDecoder;
import ch.eskaton.asn4j.runtime.decoders.VisibleStringDecoder;
import ch.eskaton.asn4j.runtime.types.ASN1BitString;
import ch.eskaton.asn4j.runtime.types.ASN1Boolean;
import ch.eskaton.asn4j.runtime.types.ASN1Choice;
import ch.eskaton.asn4j.runtime.types.ASN1EnumeratedType;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1NamedInteger;
import ch.eskaton.asn4j.runtime.types.ASN1Null;
import ch.eskaton.asn4j.runtime.types.ASN1ObjectIdentifier;
import ch.eskaton.asn4j.runtime.types.ASN1OctetString;
import ch.eskaton.asn4j.runtime.types.ASN1Sequence;
import ch.eskaton.asn4j.runtime.types.ASN1SequenceOf;
import ch.eskaton.asn4j.runtime.types.ASN1Set;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.asn4j.runtime.types.ASN1VisibleString;
import ch.eskaton.commons.CollectionUtils;
import ch.eskaton.commons.CollectionUtils.Mapper;
import ch.eskaton.commons.Reflection;
import ch.eskaton.commons.StringUtils;

public class BERDecoder implements Decoder {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(BERDecoder.class);

	private BitStringDecoder bitStringDecoder = new BitStringDecoder();
	private ChoiceDecoder choiceDecoder = new ChoiceDecoder();
	private EnumeratedTypeDecoder enumeratedTypeDecoder = new EnumeratedTypeDecoder();
	private IntegerDecoder integerDecoder = new IntegerDecoder();
	private OIDDecoder oidDecoder = new OIDDecoder();
	private OctetStringDecoder octetStringDecoder = new OctetStringDecoder();
	private SequenceDecoder sequenceDecoder = new SequenceDecoder();
	private SequenceOfDecoder sequenceOfDecoder = new SequenceOfDecoder();
	private SetDecoder setDecoder = new SetDecoder();
	private VisibleStringDecoder visibleStringDecoder = new VisibleStringDecoder();

	private Utils berUtils = new Utils();

	public <T extends ASN1Type> T decode(Class<T> type, byte[] buf)
			throws DecodingException {
		DecoderStates states = new DecoderStates();
		DecoderState state = new DecoderState();
		states.buf = buf;
		state.pos = 0;
		state.length = buf.length;
		states.states.push(state);
		return decode(type, states);
	}

	public <T extends ASN1Type> T decode(Class<T> type, DecoderStates states)
			throws DecodingException {
		return decode(type, states, null, false);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T extends ASN1Type> T decode(Class<T> type, DecoderStates states,
			ASN1Tag tag, boolean optional) throws DecodingException {
		List<ASN1Tag> tags;

		if (Reflection.extendsClazz(type, ASN1Choice.class)) {
			return choiceDecoder.decode(this, type, states);
		}

		if (tag != null) {
			if (tag.mode() == ASN1Tag.Mode.Implicit) {
				tags = new LinkedList<ASN1Tag>();
				tags.add(0, tag);
			} else {
				tags = berUtils.getTags(type);
				tags.add(0, tag);
			}
		} else {
			tags = berUtils.getTags(type);
		}

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace(StringUtils.concat("Expecting: javatype=", type
					.getSimpleName(), ", tags=(", StringUtils.join(
					CollectionUtils.map(tags, new Mapper<ASN1Tag, String>() {
						public String map(ASN1Tag value) {
							return StringUtils.concat("tag=", value.tag(),
									", class=", value.clazz());
						}
					}), ", "), ")"));
		}

		if (tags.isEmpty()) {
			throw new DecodingException("Invalid type provided: "
					+ type.getClass().getSimpleName()
					+ ". No ASN1Tag annotation found");
		}

		DecoderState state = consumeTags(states, tags, optional);

		if (state == null) {
			return null;
		}

		T obj = null;

		try {
			if (Reflection.extendsClazz(type, ASN1EnumeratedType.class)) {
				obj = (T) enumeratedTypeDecoder.decode(this, states, state,
						(Class<ASN1EnumeratedType>) type);
			} else {
				obj = type.newInstance();

				if (obj instanceof ASN1Boolean) {
					((ASN1Boolean) obj)
							.setValue(states.buf[state.pos] == 0x00 ? false
									: true);
				} else if (obj instanceof ASN1BitString) {
					bitStringDecoder.decode(states, state, (ASN1BitString) obj);
				} else if (obj instanceof ASN1Null) {
					; // nothing to do
				} else if (obj instanceof ASN1Sequence) {
					sequenceDecoder.decode(this, states, (ASN1Sequence) obj);
				} else if (obj instanceof ASN1Set) {
					setDecoder.decode(this, states, (ASN1Set) obj);
				} else if (obj instanceof ASN1SequenceOf) {
					sequenceOfDecoder
							.decode(this, states, (ASN1SequenceOf) obj);
				} else {
					if (obj instanceof ASN1Integer
							|| Reflection.extendsClazz(type,
									ASN1NamedInteger.class)) {
						integerDecoder.decode(states, state, (ASN1Integer) obj);
					} else if (obj instanceof ASN1OctetString) {
						octetStringDecoder.decode(states, state,
								(ASN1OctetString) obj);
					} else if (obj instanceof ASN1VisibleString) {
						visibleStringDecoder.decode(states, state,
								(ASN1VisibleString) obj);
					} else if (obj instanceof ASN1ObjectIdentifier) {
						oidDecoder.decode(states, state,
								(ASN1ObjectIdentifier) obj);
					} else {
						throw new DecodingException("Decoding of object "
								+ obj.getClass().getName() + " not supported");
					}
				}
			}
		} catch (Throwable th) {
			throw new DecodingException(th);
		} finally {
			state = states.states.pop();

			if (states.states.size() > 0 && state.tlv.nextTlv != -1) {
				states.states.peek().length -= state.tlv.nextTlv
						- states.states.peek().pos;
				states.states.peek().pos = state.tlv.nextTlv;
			}
		}

		return obj;
	}

	private DecoderState consumeTags(DecoderStates states, List<ASN1Tag> tags,
			boolean optional) throws DecodingException {
		ASN1Tag tag;
		TLV tlv;
		DecoderState state = new DecoderState();
		DecoderState lastState = states.states.peek();
		int pos = lastState.pos;
		int length = lastState.length;

		try {

			int tagIdx = 0;

			do {

				if (length == 0) {
					// TODO: contemplate
					// return null;
					if (optional) {
						return null;
					}

					throw new DecodingException("Premature end of input");
				}

				tlv = TLV.getTLV(states.buf, pos, length);
				tag = tags.get(tagIdx++);

				if (!tag.clazz().equals(tlv.clazz) || tag.tag() != tlv.tag) {
					if (optional) {
						return null;
					}

					throw new DecodingException("Unexpected tag found");
				} else {
					pos = tlv.pos;
					length = tlv.length;
				}

				if (LOGGER.isTraceEnabled()) {
					LOGGER.trace(StringUtils.concat("Found: tag=", tlv.tag,
							", class=", tlv.clazz, ", length=", tlv.length));
				}

			} while (tagIdx < tags.size());

			state.tlv = tlv;
			state.pos = tlv.pos;
			state.length = tlv.length;
			states.states.push(state);
		} catch (DecodingException e) {
			throw e;
		} catch (Throwable th) {
			throw new DecodingException(th);
		}

		return state;
	}

}
