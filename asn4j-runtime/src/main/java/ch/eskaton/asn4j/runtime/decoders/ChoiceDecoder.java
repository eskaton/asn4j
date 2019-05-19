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
import ch.eskaton.asn4j.runtime.utils.RuntimeUtils;
import ch.eskaton.asn4j.runtime.annotations.ASN1Alternative;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.exceptions.DecodingException;
import ch.eskaton.asn4j.runtime.types.ASN1Choice;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.commons.utils.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChoiceDecoder {

    public void decode(Decoder decoder, DecoderStates states, ASN1Choice obj) throws DecodingException {
        Map<List<ASN1Tag>, Class<? extends ASN1Type>> tagsToTypes = new HashMap<>();
        Map<List<TagId>, Field> tagsToFields = new HashMap<>();

        fillMetaData(obj, tagsToTypes, tagsToFields);

        DecodingResult<? extends ASN1Type> result;

        result = decoder.decode(states, tagsToTypes);

        if (result == null) {
            throw new DecodingException("Empty choice");
        }

        String typeName = result.getClass().getSimpleName();
        Field altField = tagsToFields.get(result.getTags());

        if (altField == null) {
            throw new DecodingException("Failed to decode a value of the type " + typeName);
        }

        String setterName = "set" + StringUtils.initCap(altField.getName());

        try {
            Method setter = obj.getClass().getDeclaredMethod(setterName, result.getObj().getClass());

            setter.invoke(obj, result.getObj());
        } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new DecodingException(e);
        } catch (NoSuchMethodException e) {
            throw new DecodingException("Setter '" + setterName + "' missing on type " + typeName);
        }
    }

    protected void fillMetaData(ASN1Choice obj, Map<List<ASN1Tag>, Class<? extends ASN1Type>> tagsToTypes,
            Map<List<TagId>, Field> tagsToFields) {
        for (Field altField : RuntimeUtils.getComponents(obj)) {
            ASN1Alternative annotation = altField.getAnnotation(ASN1Alternative.class);

            if (annotation != null) {
                Class<? extends ASN1Type> type = (Class<? extends ASN1Type>) altField.getType();
                List<ASN1Tag> tags = RuntimeUtils.getTags(type, altField.getAnnotation(ASN1Tag.class));
                tagsToFields.put(tags.stream().map(TagId::fromTag).collect(Collectors.toList()), altField);
                tagsToTypes.put(tags, type);
            }
        }
    }

}
