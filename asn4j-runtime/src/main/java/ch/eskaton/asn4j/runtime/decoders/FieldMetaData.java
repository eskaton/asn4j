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

import ch.eskaton.asn4j.runtime.TagId;
import ch.eskaton.asn4j.runtime.annotations.ASN1Alternative;
import ch.eskaton.asn4j.runtime.annotations.ASN1Component;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.exceptions.DecodingException;
import ch.eskaton.asn4j.runtime.types.ASN1Choice;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.asn4j.runtime.utils.RuntimeUtils;
import ch.eskaton.asn4j.runtime.utils.ToString;
import ch.eskaton.commons.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class FieldMetaData {

    private Map<List<ASN1Tag>, Class<? extends ASN1Type>> tagsToTypes = new HashMap<>();

    private Map<List<TagId>, Field> tagsToFields = new HashMap<>();

    private Map<List<TagId>, Consumer<ASN1Type>> tagsToSetters = new HashMap<>();

    public FieldMetaData(ASN1Type type, Class<? extends Annotation> annotationClass) {
        for (Field field : RuntimeUtils.getComponents(type)) {
            Annotation annotation = field.getAnnotation(annotationClass);

            if (annotation != null) {
                Class<ASN1Type> fieldType = (Class<ASN1Type>) field.getType();

                if (ASN1Choice.class.isAssignableFrom(fieldType)) {
                    try {
                        ASN1Type obj = fieldType.getDeclaredConstructor().newInstance();
                        FieldMetaData metaData = new FieldMetaData(obj, ASN1Alternative.class);

                        tagsToFields.putAll(metaData.tagsToFields);
                        tagsToTypes.putAll(metaData.tagsToTypes);

                        metaData.tagsToSetters.entrySet().stream().forEach(e -> {
                            Consumer<ASN1Type> setter = (result) -> {
                                getSetter(field, type).accept(obj);
                                e.getValue().accept(result);
                            };
                            tagsToSetters.put(e.getKey(), setter);
                        });

                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                        throw new DecodingException(e);
                    }
                } else {
                    List<ASN1Tag> tags = RuntimeUtils.getTags(fieldType, field.getAnnotation(ASN1Tag.class));
                    List<TagId> tagIds = tags.stream().map(TagId::fromTag).collect(Collectors.toList());

                    tagsToFields.put(tagIds, field);
                    tagsToTypes.put(tags, fieldType);
                    tagsToSetters.put(tagIds, getSetter(field, type));
                }
            }
        }
    }

    private Consumer<ASN1Type> getSetter(Field field, ASN1Type obj) {
        return (result) -> {
            String setterName = "set" + StringUtils.initCap(field.getName());

            try {
                Method setter = obj.getClass().getDeclaredMethod(setterName, result.getClass());

                setter.invoke(obj, result);
            } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
                throw new DecodingException(e);
            } catch (NoSuchMethodException e) {
                throw new DecodingException("Setter '" + setterName + "' missing on type: " +
                        obj.getClass().getSimpleName());
            }
        };
    }

    public Map<List<ASN1Tag>, Class<? extends ASN1Type>> getTagsToTypes() {
        return tagsToTypes;
    }

    public Set<List<TagId>> getMandatoryFields() {
        return tagsToTypes.keySet().stream().map(TagId::fromTags)
                .filter(t -> !tagsToFields.get(t).getAnnotation(ASN1Component.class).optional())
                .collect(Collectors.toSet());
    }

    protected String getFieldName(List<TagId> tags) {
        Field field = tagsToFields.get(tags);

        return field.getDeclaringClass().getSimpleName() + "." + field.getName();
    }

    public Consumer<ASN1Type> getSetter(List<TagId> tags) {
        return tagsToSetters.get(tags);
    }

    @Override
    public String toString() {
        return ToString.get(this);
    }

}
