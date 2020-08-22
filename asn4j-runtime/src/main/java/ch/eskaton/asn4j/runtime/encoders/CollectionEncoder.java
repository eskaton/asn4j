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

package ch.eskaton.asn4j.runtime.encoders;

import ch.eskaton.asn4j.runtime.Encoder;
import ch.eskaton.asn4j.runtime.EncodingResult;
import ch.eskaton.asn4j.runtime.annotations.ASN1Component;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tags;
import ch.eskaton.asn4j.runtime.exceptions.EncodingException;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.asn4j.runtime.utils.RuntimeUtils;

import java.io.ByteArrayOutputStream;
import java.util.List;

public abstract class CollectionEncoder<T extends ASN1Type> implements TypeEncoder<T> {

    @Override
    @SuppressWarnings("squid:S3011")
    public EncodingResult encode(Encoder encoder, T obj) {
        var content = new ByteArrayOutputStream();
        var compFields = RuntimeUtils.getComponents(obj);

        if (!compFields.isEmpty()) {
            for (var compField : compFields) {
                var annotation = compField.getAnnotation(ASN1Component.class);

                if (annotation != null) {
                    compField.setAccessible(true);

                    try {
                        var value = compField.get(obj);

                        if (value != null) {
                            var tags = compField.getDeclaredAnnotation(ASN1Tags.class);

                            if (tags != null) {
                                content.write(encoder.encode((ASN1Type) value, List.of(tags.tags())));
                            } else {
                                content.write(encoder.encode((ASN1Type) value));
                            }
                        } else if (!(annotation.optional() || annotation.hasDefault())) {
                            throw new EncodingException("Value for %s.%s missing", obj.getClass().getSimpleName(),
                                    compField.getName());

                        }
                    } catch (EncodingException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new EncodingException(e);
                    }
                }
            }

            return EncodingResult.of(content.toByteArray(), true);
        } else {
            return EncodingResult.of(new byte[0], true);
        }
    }

}
