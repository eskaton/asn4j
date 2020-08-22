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

import ch.eskaton.asn4j.runtime.Decoder;
import ch.eskaton.asn4j.runtime.DecoderStates;
import ch.eskaton.asn4j.runtime.DecodingResult;
import ch.eskaton.asn4j.runtime.annotations.ASN1Alternative;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.exceptions.DecodingException;
import ch.eskaton.asn4j.runtime.types.ASN1Choice;
import ch.eskaton.asn4j.runtime.types.ASN1OpenType;
import ch.eskaton.asn4j.runtime.types.ASN1Type;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ChoiceDecoder {

    public <T extends ASN1Choice> T decode(Decoder decoder, DecoderStates states, T obj, boolean optional) {
        FieldMetaData metaData = new FieldMetaData(obj, ASN1Alternative.class);
        Map<List<ASN1Tag>, Class<? extends ASN1Type>> tagsToTypes = metaData.getTagsToTypes();
        DecodingResult<? extends ASN1Type> result;

        if (tagsToTypes.size() == 1 &&
                ASN1OpenType.class.isAssignableFrom(tagsToTypes.values().stream().findFirst().get())) {
            var value = decoder.decodeOpenType(states, states.peek(), true);
            var setter = metaData.getTagData().get(0).getSetter();

            setValue(value, setter);
        } else {
            result = decoder.decode(states, tagsToTypes);

            if (result == null) {
                if (optional) {
                    return null;
                }

                throw new DecodingException("Empty choice");
            }

            var setter = metaData.getSetter(result.getTags());

            setValue(result.getObj(), setter);
        }

        return obj;
    }

    private void setValue(ASN1Type value, Consumer<ASN1Type> setter) {
        if (setter == null) {
            throw new DecodingException("Failed to decode a value of the type " + value.getClass().getSimpleName());
        }

        setter.accept(value);
    }

}
