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
import ch.eskaton.asn4j.runtime.TagId;
import ch.eskaton.asn4j.runtime.Utils;
import ch.eskaton.asn4j.runtime.annotations.ASN1Component;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.exceptions.DecodingException;
import ch.eskaton.asn4j.runtime.types.ASN1Set;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.commons.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SetDecoder implements CollectionDecoder<ASN1Set> {


    @SuppressWarnings("unchecked")
    public void decode(Decoder decoder, DecoderStates states, ASN1Set obj) throws DecodingException {
        Map<List<ASN1Tag>, Class<? extends ASN1Type>> tagsToTypes = new HashMap<>();
        Map<List<TagId>, Field> tagsToFields = new HashMap<>();

        fillMetaData(obj, tagsToTypes, tagsToFields);

        DecodingResult<? extends ASN1Type> result;

        do {
            result = decoder.decode(states, tagsToTypes);

            if (result == null) {
                checkMandatoryFields(tagsToTypes, tagsToFields);
                return;
            }

            Field compField = tagsToFields.get(result.getTags());

            if (compField == null) {
                throw new DecodingException("Failed to decode a value of the type " + result.getClass().getSimpleName());
            }

            compField.setAccessible(true);

            try {
                compField.set(obj, result.getObj());
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new DecodingException(e);
            }
        } while (!tagsToTypes.isEmpty());
    }

    protected void fillMetaData(ASN1Set obj, Map<List<ASN1Tag>, Class<? extends ASN1Type>> tagsToTypes,
            Map<List<TagId>, Field> tagsToFields) {
        for (Field compField : Utils.getComponents(obj)) {
            ASN1Component annotation = compField.getAnnotation(ASN1Component.class);

            if (annotation != null) {
                Class<? extends ASN1Type> type = (Class<? extends ASN1Type>) compField.getType();
                List<ASN1Tag> tags = Utils.getTags(type, compField.getAnnotation(ASN1Tag.class));
                tagsToFields.put(tags.stream().map(TagId::fromTag).collect(Collectors.toList()), compField);
                tagsToTypes.put(tags, type);
            }
        }
    }

    protected void checkMandatoryFields(Map<List<ASN1Tag>, Class<? extends ASN1Type>> tagsToTypes, Map<List<TagId>,
            Field> tagsToFields) {
        Set<List<TagId>> mandatoryFields = getMandatoryFields(tagsToTypes, tagsToFields);

        if (!mandatoryFields.isEmpty()) {
            throw new DecodingException("Mandatory fields missing: " +
                    StringUtils.join(mandatoryFields.stream().map(tag -> getFieldName(tagsToFields.get(tag)))
                            .collect(Collectors.toList()), ", "));
        }
    }

    protected Set<List<TagId>> getMandatoryFields(Map<List<ASN1Tag>, Class<? extends ASN1Type>> tagsToTypes,
            Map<List<TagId>, Field> tagsToFields) {
        return tagsToTypes.keySet().stream().map(TagId::fromTags)
                .filter(t -> !tagsToFields.get(t).getAnnotation(ASN1Component.class).optional())
                .collect(Collectors.toSet());
    }

    protected String getFieldName(Field field) {
        return field.getDeclaringClass().getSimpleName() + "." + field.getName();
    }

}
