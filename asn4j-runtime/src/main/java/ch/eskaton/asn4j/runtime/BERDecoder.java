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
import ch.eskaton.asn4j.runtime.decoders.BitStringDecoder;
import ch.eskaton.asn4j.runtime.decoders.BooleanDecoder;
import ch.eskaton.asn4j.runtime.decoders.ChoiceDecoder;
import ch.eskaton.asn4j.runtime.decoders.EnumeratedTypeDecoder;
import ch.eskaton.asn4j.runtime.decoders.IntegerDecoder;
import ch.eskaton.asn4j.runtime.decoders.OIDDecoder;
import ch.eskaton.asn4j.runtime.decoders.OctetStringDecoder;
import ch.eskaton.asn4j.runtime.decoders.RealDecoder;
import ch.eskaton.asn4j.runtime.decoders.SequenceDecoder;
import ch.eskaton.asn4j.runtime.decoders.SequenceOfDecoder;
import ch.eskaton.asn4j.runtime.decoders.SetDecoder;
import ch.eskaton.asn4j.runtime.decoders.VisibleStringDecoder;
import ch.eskaton.asn4j.runtime.exceptions.DecodingException;
import ch.eskaton.asn4j.runtime.exceptions.PrematureEndOfInputException;
import ch.eskaton.asn4j.runtime.exceptions.UnexpectedTagException;
import ch.eskaton.asn4j.runtime.types.ASN1BitString;
import ch.eskaton.asn4j.runtime.types.ASN1Boolean;
import ch.eskaton.asn4j.runtime.types.ASN1Choice;
import ch.eskaton.asn4j.runtime.types.ASN1EnumeratedType;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1NamedInteger;
import ch.eskaton.asn4j.runtime.types.ASN1Null;
import ch.eskaton.asn4j.runtime.types.ASN1ObjectIdentifier;
import ch.eskaton.asn4j.runtime.types.ASN1OctetString;
import ch.eskaton.asn4j.runtime.types.ASN1Real;
import ch.eskaton.asn4j.runtime.types.ASN1Sequence;
import ch.eskaton.asn4j.runtime.types.ASN1SequenceOf;
import ch.eskaton.asn4j.runtime.types.ASN1Set;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.asn4j.runtime.types.ASN1VisibleString;
import ch.eskaton.commons.utils.CollectionUtils;
import ch.eskaton.commons.utils.ReflectionUtils;
import ch.eskaton.commons.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static ch.eskaton.asn4j.runtime.Assert.notEmpty;
import static ch.eskaton.asn4j.runtime.Assert.notEmptyCollection;

public class BERDecoder implements Decoder {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(BERDecoder.class);

    private BooleanDecoder booleanDecoder = new BooleanDecoder();

    private BitStringDecoder bitStringDecoder = new BitStringDecoder();

    private ChoiceDecoder choiceDecoder = new ChoiceDecoder();

    private EnumeratedTypeDecoder enumeratedTypeDecoder = new EnumeratedTypeDecoder();

    private IntegerDecoder integerDecoder = new IntegerDecoder();

    private RealDecoder realDecoder = new RealDecoder();

    private OIDDecoder oidDecoder = new OIDDecoder();

    private OctetStringDecoder octetStringDecoder = new OctetStringDecoder();

    private SequenceDecoder sequenceDecoder = new SequenceDecoder();

    private SequenceOfDecoder sequenceOfDecoder = new SequenceOfDecoder();

    private SetDecoder setDecoder = new SetDecoder();

    private VisibleStringDecoder visibleStringDecoder = new VisibleStringDecoder();

    public <T extends ASN1Type> T decode(Class<T> type, byte[] buf)
            throws DecodingException {
        DecoderStates states = new DecoderStates();
        DecoderState state = new DecoderState(0, buf.length);
        states.buf = buf;
        states.push(state);
        return decode(type, states);
    }

    public <T extends ASN1Type> T decode(Class<T> type, DecoderStates states)
            throws DecodingException {
        return decode(type, states, null, false);
    }

    public ASN1Type decode(DecoderStates states,
                           Map<List<ASN1Tag>, Class<? extends ASN1Type>> tags)
            throws DecodingException {
        MultipleTagsMatcher matcher = new MultipleTagsMatcher(tags.keySet());
        DecoderState state = consumeMultipleTags(states, matcher);

        List<ASN1Tag> match = matcher.getMatch();

        Class<? extends ASN1Type> type = tags.remove(match);

        return decodeState(type, states, state);
    }

    public <T extends ASN1Type> T decode(Class<T> type, DecoderStates states, ASN1Tag tag, boolean optional)
            throws DecodingException {
        List<ASN1Tag> tags;

        if (ReflectionUtils.extendsClazz(type, ASN1Choice.class)) {
            return choiceDecoder.decode(this, type, states);
        }

        tags = Utils.getTags(type, tag);

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(StringUtils.concat("Expecting: javatype=", type
                    .getSimpleName(), ", tags=(", StringUtils.join(
                    CollectionUtils.map(tags, value -> StringUtils.concat("tag=", value.tag(),
                            ", class=", value.clazz())), ", "), ")"));
        }

        return decodeState(type, states, consumeTags(states, tags, optional));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> T decodeState(Class<T> type, DecoderStates states,
                              DecoderState state) {
        if (state == null) {
            return null;
        }

        T obj;

        try {
            if (ReflectionUtils.extendsClazz(type, ASN1EnumeratedType.class)) {
                obj = (T) enumeratedTypeDecoder.decode(this, states, state,
                        (Class<ASN1EnumeratedType>) type);
            } else {
                obj = type.newInstance();

                if (obj instanceof ASN1Boolean) {
                    booleanDecoder.decode(states, state, (ASN1Boolean) obj);
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
                } else if (obj instanceof ASN1Integer
                        || ReflectionUtils
                        .extendsClazz(type, ASN1NamedInteger.class)) {
                    integerDecoder.decode(states, state, (ASN1Integer) obj);
                } else if (obj instanceof ASN1Real) {
                    realDecoder.decode(states, state, (ASN1Real) obj);
                } else if (obj instanceof ASN1OctetString) {
                    octetStringDecoder.decode(states, state,
                            (ASN1OctetString) obj);
                } else if (obj instanceof ASN1VisibleString) {
                    visibleStringDecoder.decode(states, state,
                            (ASN1VisibleString) obj);
                } else if (obj instanceof ASN1ObjectIdentifier) {
                    oidDecoder
                            .decode(states, state, (ASN1ObjectIdentifier) obj);
                } else {
                    throw new DecodingException("Decoding of object "
                            + obj.getClass().getName() + " not supported");
                }

            }
        } catch (Throwable th) {
            if (DecodingException.class.isAssignableFrom(th.getClass())) {
                throw (DecodingException) th;
            }

            throw new DecodingException(th);
        } finally {
            state = states.pop();

            if (states.size() > 0 && state.tlv.nextTlv != -1) {
                states.peek().length -= state.tlv.nextTlv - states.peek().pos;
                states.peek().pos = state.tlv.nextTlv;
            }
        }

        return obj;
    }

    private DecoderState consumeMultipleTags(DecoderStates states, MultipleTagsMatcher matcher)
            throws DecodingException {
        DecoderState state = consumeTags(states, matcher);

        if (state == null) {
            DecoderState lastState = states.peek();

            if (lastState.length == 0) {
                throw new PrematureEndOfInputException();
            } else {
                throw new UnexpectedTagException();
            }
        }

        return state;
    }

    private DecoderState consumeTags(DecoderStates states, List<ASN1Tag> tags,
                                     boolean optional) throws DecodingException {
        TagsMatcher matcher = new SingleTagsMatcher(tags);
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

    private DecoderState consumeTags(DecoderStates states, TagsMatcher tags)
            throws DecodingException {
        if (!tags.hasNext()) {
            throw new DecodingException("Empty tag list");
        }

        TLV tlv = null;
        DecoderState lastState = states.peek();
        int pos = lastState.pos;
        int length = lastState.length;

        try {

            while (tags.hasNext()) {
                if (length == 0) {
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
        } catch (Throwable th) {
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

            if (!tag.clazz().equals(tlv.clazz) || tag.tag() != tlv.tag) {
                return false;
            }

            return true;
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

        public boolean hasChilds() {
            return !childs.isEmpty();
        }

        public ASN1Tag getTag() {
            return null;
        }

    }

    private static class TagNodeImpl implements TagNode {

        protected ASN1Tag tag;

        protected Set<TagNode> childs = new HashSet<TagNode>();

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

                if (tag.clazz().equals(tlv.clazz) && tag.tag() == tlv.tag) {
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
