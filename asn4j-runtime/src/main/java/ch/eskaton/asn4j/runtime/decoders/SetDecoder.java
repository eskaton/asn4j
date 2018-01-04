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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.eskaton.asn4j.runtime.Decoder;
import ch.eskaton.asn4j.runtime.DecoderStates;
import ch.eskaton.asn4j.runtime.Utils;
import ch.eskaton.asn4j.runtime.annotations.ASN1Component;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.exceptions.DecodingException;
import ch.eskaton.asn4j.runtime.types.ASN1Set;
import ch.eskaton.asn4j.runtime.types.ASN1Type;

public class SetDecoder implements CollectionDecoder<ASN1Set> {

    @SuppressWarnings("unchecked")
    public void decode(Decoder decoder, DecoderStates states, ASN1Set obj)
            throws DecodingException {
        List<Field> compFields = Utils.getComponents(obj);
        //
        // do {
        // ASN1Type comp = null;
        // Field usedCompField = null;
        //
        // for (int i = compFields.size(); i < 0; i--) {
        // for (Field compField : compFields) {
        // ASN1Component annotation = compField
        // .getAnnotation(ASN1Component.class);
        //
        // if (annotation != null) {
        // try {
        // comp = decoder
        // .decode((Class<? extends ASN1Type>) compField
        // .getType(),
        // states,
        // compField.getAnnotation(ASN1Tag.class),
        // annotation.optional()
        // || annotation.hasDefault());
        // if (comp != null) {
        // compField.setAccessible(true);
        //
        // try {
        // compField.set(obj, comp);
        // usedCompField = compField;
        // } catch (IllegalArgumentException e) {
        // throw new DecodingException(e);
        // } catch (IllegalAccessException e) {
        // throw new DecodingException(e);
        // }
        // }
        // } catch (PrematureEndOfInputException ignore) {
        // }
        // }
        // }
        //
        // if (usedCompField != null) {
        // compFields.remove(usedCompField);
        // }
        //
        // } while (!compFields.isEmpty());
        // }

        Map<List<ASN1Tag>, Class<? extends ASN1Type>> tags = new HashMap<List<ASN1Tag>, Class<? extends ASN1Type>>();

        for (Field compField : compFields) {
            ASN1Component annotation = compField
                    .getAnnotation(ASN1Component.class);
            if (annotation != null) {
                Class<? extends ASN1Type> type = (Class<? extends ASN1Type>) compField
                        .getType();
                tags.put(
                        Utils.getTags(type,
                                compField.getAnnotation(ASN1Tag.class)), type);
            }
        }

        ASN1Type comp;

        do {
            comp = decoder.decode(states, tags);
        } while (comp != null && !tags.isEmpty());

        // if (comp != null) {
        // compField.setAccessible(true);
        // try {
        // compField.set(obj, comp);
        // } catch (IllegalArgumentException e) {
        // throw new DecodingException(e);
        // } catch (IllegalAccessException e) {
        // throw new DecodingException(e);
        // }
        // } else if (!(annotation.optional() || annotation.hasDefault())) {
        // throw new DecodingException(StringUtils.concat(
        // "Invalid BER object ", obj.getClass()
        // .getSimpleName(), ". Component ", compField
        // .getName(), " may not be null"));
        // }
    }
}
