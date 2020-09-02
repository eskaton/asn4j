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

package ch.eskaton.asn4j.runtime.utils;

import ch.eskaton.asn4j.runtime.DecoderState;
import ch.eskaton.asn4j.runtime.DecoderStates;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tags;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.commons.utils.StreamsUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class RuntimeUtils {

    private RuntimeUtils() {
    }

    public static List<ASN1Tag> getTags(Class<? extends ASN1Type> clazz) {
        var tags = clazz.getAnnotation(ASN1Tags.class);

        if (tags != null) {
            return Arrays.asList(tags.tags());
        }

        return Collections.emptyList();
    }

    public static <T extends ASN1Type> List<ASN1Tag> getTags(Class<T> type, List<ASN1Tag> tags) {
        var typeTags = getTags(type);
        var allTags = new ArrayList<ASN1Tag>();

        if (tags != null) {
            allTags.addAll(tags);
        }

        allTags.addAll(typeTags);

        return allTags;
    }

    public static List<Field> getComponents(ASN1Type type) {
        return StreamsUtils.of(FieldIterator.of(type.getClass())).collect(toList());
    }

    public static Field getComponent(ASN1Type type, String name) {
        return StreamsUtils.of(FieldIterator.of(type.getClass())).filter(f -> f.getName().equals(name)).findFirst()
                .orElse(null);
    }

    public static byte[] getValue(DecoderStates states, DecoderState state) {
        return getValue(states, state, 0);
    }

    public static byte[] getValue(DecoderStates states, DecoderState state, int offset) {
        byte[] buf = new byte[state.tlv.length - offset];
        System.arraycopy(states.buf, state.tlv.pos + offset, buf, 0, state.tlv.length - offset);
        return buf;
    }

}
