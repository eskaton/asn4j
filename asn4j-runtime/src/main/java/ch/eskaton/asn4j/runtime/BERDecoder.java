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

import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.decoders.BMPStringDecoder;
import ch.eskaton.asn4j.runtime.decoders.BitStringDecoder;
import ch.eskaton.asn4j.runtime.decoders.BooleanDecoder;
import ch.eskaton.asn4j.runtime.decoders.ChoiceDecoder;
import ch.eskaton.asn4j.runtime.decoders.EnumeratedTypeDecoder;
import ch.eskaton.asn4j.runtime.decoders.GeneralStringDecoder;
import ch.eskaton.asn4j.runtime.decoders.GraphicStringDecoder;
import ch.eskaton.asn4j.runtime.decoders.IA5StringDecoder;
import ch.eskaton.asn4j.runtime.decoders.IRIDecoder;
import ch.eskaton.asn4j.runtime.decoders.IntegerDecoder;
import ch.eskaton.asn4j.runtime.decoders.NumericStringDecoder;
import ch.eskaton.asn4j.runtime.decoders.ObjectIdentifierDecoder;
import ch.eskaton.asn4j.runtime.decoders.OctetStringDecoder;
import ch.eskaton.asn4j.runtime.decoders.PrintableStringDecoder;
import ch.eskaton.asn4j.runtime.decoders.RealDecoder;
import ch.eskaton.asn4j.runtime.decoders.RelativeIRIDecoder;
import ch.eskaton.asn4j.runtime.decoders.RelativeOIDDecoder;
import ch.eskaton.asn4j.runtime.decoders.SequenceDecoder;
import ch.eskaton.asn4j.runtime.decoders.SequenceOfDecoder;
import ch.eskaton.asn4j.runtime.decoders.SetDecoder;
import ch.eskaton.asn4j.runtime.decoders.SetOfDecoder;
import ch.eskaton.asn4j.runtime.decoders.TeletexStringDecoder;
import ch.eskaton.asn4j.runtime.decoders.TypeDecoder;
import ch.eskaton.asn4j.runtime.decoders.UTF8StringDecoder;
import ch.eskaton.asn4j.runtime.decoders.UniversalStringDecoder;
import ch.eskaton.asn4j.runtime.decoders.VideotexStringDecoder;
import ch.eskaton.asn4j.runtime.decoders.VisibleStringDecoder;
import ch.eskaton.asn4j.runtime.exceptions.DecodingException;
import ch.eskaton.asn4j.runtime.exceptions.PrematureEndOfInputException;
import ch.eskaton.asn4j.runtime.exceptions.UnexpectedTagException;
import ch.eskaton.asn4j.runtime.types.ASN1BMPString;
import ch.eskaton.asn4j.runtime.types.ASN1BitString;
import ch.eskaton.asn4j.runtime.types.ASN1Boolean;
import ch.eskaton.asn4j.runtime.types.ASN1Choice;
import ch.eskaton.asn4j.runtime.types.ASN1EnumeratedType;
import ch.eskaton.asn4j.runtime.types.ASN1GeneralString;
import ch.eskaton.asn4j.runtime.types.ASN1GraphicString;
import ch.eskaton.asn4j.runtime.types.ASN1IA5String;
import ch.eskaton.asn4j.runtime.types.ASN1IRI;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1NamedInteger;
import ch.eskaton.asn4j.runtime.types.ASN1Null;
import ch.eskaton.asn4j.runtime.types.ASN1NumericString;
import ch.eskaton.asn4j.runtime.types.ASN1ObjectIdentifier;
import ch.eskaton.asn4j.runtime.types.ASN1OctetString;
import ch.eskaton.asn4j.runtime.types.ASN1PrintableString;
import ch.eskaton.asn4j.runtime.types.ASN1Real;
import ch.eskaton.asn4j.runtime.types.ASN1RelativeIRI;
import ch.eskaton.asn4j.runtime.types.ASN1RelativeOID;
import ch.eskaton.asn4j.runtime.types.ASN1Sequence;
import ch.eskaton.asn4j.runtime.types.ASN1SequenceOf;
import ch.eskaton.asn4j.runtime.types.ASN1Set;
import ch.eskaton.asn4j.runtime.types.ASN1SetOf;
import ch.eskaton.asn4j.runtime.types.ASN1TeletexString;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.asn4j.runtime.types.ASN1UTF8String;
import ch.eskaton.asn4j.runtime.types.ASN1UniversalString;
import ch.eskaton.asn4j.runtime.types.ASN1VideotexString;
import ch.eskaton.asn4j.runtime.types.ASN1VisibleString;
import ch.eskaton.asn4j.runtime.types.HasConstraint;
import ch.eskaton.asn4j.runtime.utils.RuntimeUtils;
import ch.eskaton.commons.collections.Maps;
import ch.eskaton.commons.utils.CollectionUtils;
import ch.eskaton.commons.utils.ReflectionUtils;
import ch.eskaton.commons.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.runtime.Assert.notEmpty;
import static ch.eskaton.asn4j.runtime.Assert.notEmptyCollection;

public class BERDecoder implements Decoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(BERDecoder.class);

    private Map<Class<? extends ASN1Type>, TypeDecoder<? extends ASN1Type>> decoders =
            Maps.<Class<? extends ASN1Type>, TypeDecoder<? extends ASN1Type>>builder()
                    .put(ASN1BitString.class, new BitStringDecoder())
                    .put(ASN1Boolean.class, new BooleanDecoder())
                    .put(ASN1EnumeratedType.class, new EnumeratedTypeDecoder())
                    .put(ASN1Integer.class, new IntegerDecoder())
                    .put(ASN1Real.class, new RealDecoder())
                    .put(ASN1ObjectIdentifier.class, new ObjectIdentifierDecoder())
                    .put(ASN1RelativeOID.class, new RelativeOIDDecoder())
                    .put(ASN1IRI.class, new IRIDecoder())
                    .put(ASN1RelativeIRI.class, new RelativeIRIDecoder())
                    .put(ASN1OctetString.class, new OctetStringDecoder())
                    .put(ASN1VisibleString.class, new VisibleStringDecoder())
                    .put(ASN1NumericString.class, new NumericStringDecoder())
                    .put(ASN1PrintableString.class, new PrintableStringDecoder())
                    .put(ASN1GraphicString.class, new GraphicStringDecoder())
                    .put(ASN1GeneralString.class, new GeneralStringDecoder())
                    .put(ASN1IA5String.class, new IA5StringDecoder())
                    .put(ASN1TeletexString.class, new TeletexStringDecoder())
                    .put(ASN1VideotexString.class, new VideotexStringDecoder())
                    .put(ASN1UniversalString.class, new UniversalStringDecoder())
                    .put(ASN1UTF8String.class, new UTF8StringDecoder())
                    .put(ASN1BMPString.class, new BMPStringDecoder())
                    .build();

    private ChoiceDecoder choiceDecoder = new ChoiceDecoder();

    private SequenceDecoder sequenceDecoder = new SequenceDecoder();

    private SequenceOfDecoder sequenceOfDecoder = new SequenceOfDecoder();

    private SetDecoder setDecoder = new SetDecoder();

    private SetOfDecoder setOfDecoder = new SetOfDecoder();

    public <T extends ASN1Type> T decode(Class<T> type, byte[] buf) {
        DecoderStates states = new DecoderStates();
        DecoderState state = new DecoderState(0, buf.length);
        states.buf = buf;
        states.push(state);
        DecodingResult<T> result = decode(type, states);
        return result.getObj();
    }

    public <T extends ASN1Type> DecodingResult<T> decode(Class<T> type, DecoderStates states) {
        return decode(type, states, null, false);
    }

    public DecodingResult<? extends ASN1Type> decode(DecoderStates states, Map<List<ASN1Tag>,
            Class<? extends ASN1Type>> tags) {
        MultipleTagsMatcher matcher = new MultipleTagsMatcher(tags.keySet());
        DecoderState state = consumeMultipleTags(states, matcher);

        if (state != null) {
            List<ASN1Tag> match = matcher.getMatch();

            Class<? extends ASN1Type> type = tags.remove(match);

            state.setTagIds(match.stream().map(TagId::fromTag).collect(Collectors.toList()));

            return decodeState(type, states, state);
        }

        return null;
    }

    public <T extends ASN1Type> DecodingResult<T> decode(Type type, DecoderStates states, ASN1Tag tag,
            boolean optional) {
        List<ASN1Tag> tags;

        Class clazz = toClass(type);

        if (ReflectionUtils.extendsClazz(clazz, ASN1Choice.class)) {
            return decodeChoice(clazz, states, optional);
        }

        tags = RuntimeUtils.getTags(clazz, tag);

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(StringUtils.concat("Expecting: javatype=", clazz.getSimpleName(), ", tags=(", StringUtils
                    .join(CollectionUtils.map(tags, value -> StringUtils
                            .concat("tag=", value.tag(), ", class=", value.clazz())), ", "), ")"));
        }

        return decodeState(type, states, consumeTags(states, tags, optional));
    }

    private Class toClass(Type type) {
        if (type instanceof ParameterizedType) {
            return (Class) ((ParameterizedType) type).getRawType();
        } else if (type instanceof Class) {
            return (Class) type;
        } else {
            throw new DecodingException("Unsupported type: " + type);
        }
    }

    private <T extends ASN1Choice> DecodingResult<T> decodeChoice(Class<T> type, DecoderStates states, boolean optional) {
        T obj;

        try {
            obj = type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new DecodingException(e);
        }

        obj = choiceDecoder.decode(this, states, obj, optional);

        return new DecodingResult<>(Collections.emptyList(), obj);
    }

    public <T extends ASN1Type, C extends TypeDecoder<T>> C getDecoder(Class<T> clazz) {
        TypeDecoder<?> decoder = decoders.get(clazz);

        if (decoder == null) {
            throw new DecodingException("No decoder for node-type " + clazz.getSimpleName());
        }

        return (C) decoder;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <T> DecodingResult<T> decodeState(Type type, DecoderStates states, DecoderState state) {
        if (state == null) {
            return null;
        }

        T obj;

        try {
            Class<T> clazz = toClass(type);

            obj = clazz.newInstance();

            if (obj instanceof ASN1Boolean) {
                getDecoder(ASN1Boolean.class).decode(states, state, (ASN1Boolean) obj);
            } else if (obj instanceof ASN1BitString) {
                getDecoder(ASN1BitString.class).decode(states, state, (ASN1BitString) obj);
            } else if (obj instanceof ASN1EnumeratedType) {
                getDecoder(ASN1EnumeratedType.class).decode(states, state, (ASN1EnumeratedType) obj);
            } else if (obj instanceof ASN1Null) {
                // nothing to do
            } else if (obj instanceof ASN1Sequence) {
                sequenceDecoder.decode(this, states, type, (ASN1Sequence) obj);
            } else if (obj instanceof ASN1SequenceOf) {
                sequenceOfDecoder.decode(this, states, type, (ASN1SequenceOf) obj);
            } else if (obj instanceof ASN1Set) {
                setDecoder.decode(this, states, type, (ASN1Set) obj);
            } else if (obj instanceof ASN1SetOf) {
                setOfDecoder.decode(this, states, type, (ASN1SetOf) obj);
            } else if (obj instanceof ASN1Integer || ReflectionUtils.extendsClazz(clazz, ASN1NamedInteger.class)) {
                getDecoder(ASN1Integer.class).decode(states, state, (ASN1Integer) obj);
            } else if (obj instanceof ASN1Real) {
                getDecoder(ASN1Real.class).decode(states, state, (ASN1Real) obj);
            } else if (obj instanceof ASN1OctetString) {
                getDecoder(ASN1OctetString.class).decode(states, state, (ASN1OctetString) obj);
            } else if (obj instanceof ASN1VisibleString) {
                getDecoder(ASN1VisibleString.class).decode(states, state, (ASN1VisibleString) obj);
            } else if (obj instanceof ASN1NumericString) {
                getDecoder(ASN1NumericString.class).decode(states, state, (ASN1NumericString) obj);
            } else if (obj instanceof ASN1GraphicString) {
                getDecoder(ASN1GraphicString.class).decode(states, state, (ASN1GraphicString) obj);
            } else if (obj instanceof ASN1GeneralString) {
                getDecoder(ASN1GeneralString.class).decode(states, state, (ASN1GeneralString) obj);
            } else if (obj instanceof ASN1PrintableString) {
                getDecoder(ASN1PrintableString.class).decode(states, state, (ASN1PrintableString) obj);
            } else if (obj instanceof ASN1IA5String) {
                getDecoder(ASN1IA5String.class).decode(states, state, (ASN1IA5String) obj);
            } else if (obj instanceof ASN1TeletexString) {
                getDecoder(ASN1TeletexString.class).decode(states, state, (ASN1TeletexString) obj);
            } else if (obj instanceof ASN1VideotexString) {
                getDecoder(ASN1VideotexString.class).decode(states, state, (ASN1VideotexString) obj);
            } else if (obj instanceof ASN1UniversalString) {
                getDecoder(ASN1UniversalString.class).decode(states, state, (ASN1UniversalString) obj);
            } else if (obj instanceof ASN1UTF8String) {
                getDecoder(ASN1UTF8String.class).decode(states, state, (ASN1UTF8String) obj);
            } else if (obj instanceof ASN1BMPString) {
                getDecoder(ASN1BMPString.class).decode(states, state, (ASN1BMPString) obj);
            } else if (obj instanceof ASN1ObjectIdentifier) {
                getDecoder(ASN1ObjectIdentifier.class).decode(states, state, (ASN1ObjectIdentifier) obj);
            } else if (obj instanceof ASN1RelativeOID) {
                getDecoder(ASN1RelativeOID.class).decode(states, state, (ASN1RelativeOID) obj);
            } else if (obj instanceof ASN1IRI) {
                getDecoder(ASN1IRI.class).decode(states, state, (ASN1IRI) obj);
            } else if (obj instanceof ASN1RelativeIRI) {
                getDecoder(ASN1RelativeIRI.class).decode(states, state, (ASN1RelativeIRI) obj);
            } else {
                throw new DecodingException("Decoding of object " + obj.getClass().getName() + " not supported");
            }
        } catch (Exception th) {
            if (DecodingException.class.isAssignableFrom(th.getClass())) {
                throw (DecodingException) th;
            }

            throw new DecodingException(th);
        } finally {
            state = states.back();
        }

        if (obj instanceof HasConstraint) {
            ((HasConstraint) obj).checkConstraint();
        }

        return new DecodingResult(state.getTagIds(), obj);
    }

    private DecoderState consumeMultipleTags(DecoderStates states, MultipleTagsMatcher matcher) {
        return consumeTags(states, matcher, true);
    }

    private DecoderState consumeTags(DecoderStates states, List<ASN1Tag> tags, boolean optional) {
        return consumeTags(states, new SingleTagsMatcher(tags), optional);
    }

    private DecoderState consumeTags(DecoderStates states, TagsMatcher matcher, boolean optional) {
        DecoderState state = consumeTags(states, matcher);

        if (state == null && !optional) {
            DecoderState lastState = states.peek();

            if (lastState.length == 0) {
                throw new PrematureEndOfInputException();
            } else {
                throw new UnexpectedTagException();
            }
        }

        return state;
    }

    private DecoderState consumeTags(DecoderStates states, TagsMatcher tags) {
        if (!tags.hasNext()) {
            throw new DecodingException("Empty tag list");
        }

        TLV tlv = null;
        DecoderState lastState = states.peek();
        int pos = lastState.pos;
        int length = lastState.length;

        try {
            while (tags.hasNext()) {
                if (length <= 0) {
                    return null;
                }

                tlv = TLV.getTLV(states.buf, pos, length);

                if (!tags.accept(tlv)) {
                    return null;
                } else {
                    pos = tlv.pos;
                    length = tlv.length;
                }

                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace(StringUtils.concat("Found: tag=", tlv.tag, ", class=", tlv.clazz,
                            ", length=", tlv.length));
                }
            }

            return states.push(new DecoderState(tlv, pos, length));
        } catch (DecodingException e) {
            throw e;
        } catch (Exception th) {
            throw new DecodingException(th);
        }
    }

    private interface TagsMatcher {

        boolean hasNext();

        boolean accept(TLV tlv);

    }

    static class SingleTagsMatcher implements TagsMatcher {

        private Iterator<ASN1Tag> tags;

        public SingleTagsMatcher(List<ASN1Tag> tags) {
            notEmpty(tags, "tags");

            this.tags = tags.iterator();
        }

        public boolean hasNext() {
            return tags.hasNext();
        }

        public boolean accept(TLV tlv) {
            ASN1Tag tag = tags.next();

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Trying: tag={}, class={}", tag.tag(), tag.clazz());
            }

            return tlv.getTagId().equalsASN1Tag(tag);
        }

    }

    static class MultipleTagsMatcher implements TagsMatcher {

        private TagNode tree;

        private List<ASN1Tag> match = new ArrayList<>();

        public MultipleTagsMatcher(Collection<List<ASN1Tag>> tags) {
            notEmptyCollection(tags, "tags");

            tree = new TagTree();

            for (List<ASN1Tag> list : tags) {
                ((TagTree) tree).addTags(list);
            }
        }

        public boolean hasNext() {
            return tree != null && tree.hasChilds();
        }

        public boolean accept(TLV tlv) {
            tree = tree.accept(tlv);

            if (tree != null) {
                match.add(tree.getTag());
                return true;
            }

            match.clear();
            return false;
        }

        public List<ASN1Tag> getMatch() {
            return hasNext() || match.isEmpty() ? null : match;
        }

    }

    private interface TagNode {

        boolean hasChilds();

        ASN1Tag getTag();

        TagNode add(TagNode node);

        TagNode accept(TLV tlv);

    }

    private static class TagTree extends TagNodeImpl {

        private void addTags(List<ASN1Tag> tags) {
            TagNode currentNode = null;

            for (ASN1Tag tag : tags) {
                if (currentNode == null) {
                    for (TagNode tagNode : childs) {
                        if (tag.equals(tagNode.getTag())) {
                            currentNode = tagNode;
                        }
                    }

                    if (currentNode == null) {
                        currentNode = new TagNodeImpl(tag);
                    }

                    childs.add(currentNode);
                } else {
                    currentNode = currentNode.add(new TagNodeImpl(tag));
                }
            }
        }

        @Override
        public boolean hasChilds() {
            return !childs.isEmpty();
        }

        @Override
        public ASN1Tag getTag() {
            return null;
        }

    }

    private static class TagNodeImpl implements TagNode {

        protected ASN1Tag tag;

        protected Set<TagNode> childs = new HashSet<>();

        public TagNodeImpl() {
        }

        public TagNodeImpl(ASN1Tag tag) {
            this.tag = tag;
        }

        public ASN1Tag getTag() {
            return tag;
        }

        public boolean hasChilds() {
            return !childs.isEmpty();
        }

        public TagNode accept(TLV tlv) {
            for (TagNode child : childs) {
                ASN1Tag tag = child.getTag();

                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Trying: tag={}, class={}", tag.tag(), tag.clazz());
                }

                if (tlv.getTagId().equalsASN1Tag(tag)) {
                    return child;
                }
            }

            return null;
        }

        public TagNode add(TagNode node) {
            if (!childs.contains(node)) {
                childs.add(node);
            }

            return node;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(tag);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null) {
                return false;
            }

            if (!(obj instanceof TagNodeImpl)) {
                return false;
            }

            TagNodeImpl other = (TagNodeImpl) obj;

            if (tag == null) {
                if (other.tag != null) {
                    return false;
                }
            } else if (!tag.equals(other.tag)) {
                return false;
            }

            return true;
        }

    }

}
