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
import ch.eskaton.asn4j.runtime.annotations.ASN1Component;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.exceptions.EncodingException;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.asn4j.runtime.utils.RuntimeUtils;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.List;

public abstract class CollectionEncoder<T extends ASN1Type> implements TypeEncoder<T> {

    @Override
    @SuppressWarnings("squid:S3011")
    public byte[] encode(Encoder encoder, T obj) {
        ByteArrayOutputStream content = new ByteArrayOutputStream();

        List<Field> compFields = RuntimeUtils.getComponents(obj);

        if (!compFields.isEmpty()) {
            for (Field compField : compFields) {
                ASN1Component annotation = compField.getAnnotation(ASN1Component.class);

                if (annotation != null) {
                    compField.setAccessible(true);

                    ASN1Tag tag = compField.getDeclaredAnnotation(ASN1Tag.class);

                    try {
                        Object value = compField.get(obj);

                        if (value != null) {
                            if (tag != null) {
                                ByteArrayOutputStream fieldContent = new ByteArrayOutputStream();
                                fieldContent.write(encoder.encode((ASN1Type) value, tag));
                                content.write(fieldContent.toByteArray());
                            } else {
                                content.write(encoder.encode((ASN1Type) value));
                            }
                        } else if (!(annotation.optional() || annotation.hasDefault())) {
                            throw new EncodingException("Value for " + obj.getClass().getSimpleName() + "." +
                                    compField.getName() + " missing");

                        }
                    } catch (EncodingException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new EncodingException(e);
                    }
                }
            }

            return content.toByteArray();
        } else {
            return new byte[0];
        }
    }

}
