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
import ch.eskaton.asn4j.runtime.annotations.ASN1Component;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.exceptions.DecodingException;
import ch.eskaton.asn4j.runtime.types.ASN1OpenType;
import ch.eskaton.asn4j.runtime.types.ASN1Set;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.commons.utils.StringUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SetDecoder implements CollectionDecoder<ASN1Set> {

    @SuppressWarnings("squid:S3011")
    public void decode(Decoder decoder, DecoderStates states, Type type, ASN1Set obj) {
        FieldMetaData metaData = new FieldMetaData(obj, ASN1Component.class);
        Map<List<ASN1Tag>, Class<? extends ASN1Type>> tagsToTypes = metaData.getTagsToTypes();
        DecodingResult<? extends ASN1Type> result;

        if (tagsToTypes.size() == 1 &&
                ASN1OpenType.class.isAssignableFrom(tagsToTypes.values().stream().findFirst().get())) {
            var value = decoder.decodeOpenType(states, states.peek(), metaData.getMandatoryFields().isEmpty());

            setValue(value, metaData.getTagData().get(0).getSetter());
        } else {
            do {
                result = decoder.decode(states, tagsToTypes);

                if (result == null) {
                    checkMandatoryFields(metaData, tagsToTypes);
                    return;
                }

                setValue(result.getObj(), metaData.getSetter(result.getTags()));
            } while (!tagsToTypes.isEmpty());
        }
    }

    private void setValue(ASN1Type value, Consumer<ASN1Type> setter) {
        if (setter == null) {
            throw new DecodingException("Failed to decode a value of the type " + value.getClass().getSimpleName());
        }

        setter.accept(value);
    }

    protected void checkMandatoryFields(FieldMetaData metaData, Map<List<ASN1Tag>, Class<? extends ASN1Type>> tagsToTypes) {
        var mandatoryFields = metaData.getMandatoryFields();
        var missingFields = tagsToTypes.entrySet().stream()
                .map(e -> metaData.getTagIds(e.getKey()))
                .collect(Collectors.toSet());

        mandatoryFields.retainAll(missingFields);

        if (!mandatoryFields.isEmpty()) {
            throw new DecodingException("Mandatory fields missing: " + StringUtils.join(mandatoryFields.stream()
                    .map(metaData::getFieldName)
                    .collect(Collectors.toList()), ", "));
        }
    }

}
