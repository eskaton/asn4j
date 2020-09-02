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
import ch.eskaton.asn4j.runtime.annotations.ASN1Component;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tags;
import ch.eskaton.asn4j.runtime.exceptions.DecodingException;
import ch.eskaton.asn4j.runtime.types.ASN1Sequence;
import ch.eskaton.asn4j.runtime.utils.RuntimeUtils;
import ch.eskaton.commons.utils.StringUtils;

import java.lang.reflect.Type;
import java.util.Arrays;

public class SequenceDecoder implements CollectionDecoder<ASN1Sequence> {

    @SuppressWarnings("squid:S3011")
    public void decode(Decoder decoder, DecoderStates states, Type type, ASN1Sequence obj) {
        var componentFields = RuntimeUtils.getComponents(obj);

        for (var componentField : componentFields) {
            var annotation = componentField.getAnnotation(ASN1Component.class);

            if (annotation != null) {
                var tagsAnnotation = componentField.getAnnotation(ASN1Tags.class);
                var tags = tagsAnnotation != null ? Arrays.asList(tagsAnnotation.tags()) : null;
                var optional = annotation.optional() || annotation.hasDefault();
                var result = decoder.decode(componentField.getType(), states, tags, optional);

                if (result != null) {
                    var component = result.getObj();

                    if (component != null) {
                        componentField.setAccessible(true);

                        try {
                            componentField.set(obj, component);
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            throw new DecodingException(e);
                        }
                    } else if (!(optional)) {
                        throw new DecodingException(StringUtils.concat("Invalid BER object ", obj.getClass()
                                .getSimpleName(), ". Component ", componentField.getName(), " may not be null"));
                    }
                }
            }
        }
    }

}
