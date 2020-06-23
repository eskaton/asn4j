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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ch.eskaton.commons.utils.ReflectionUtils.getInstance;

public class FieldMetaData {

    private List<TagData> tagData;

    public FieldMetaData(ASN1Type type, Class<? extends Annotation> annotationClass) {
        tagData = getTagData(type, annotationClass);
    }

    private ArrayList<TagData> getTagData(ASN1Type type, Class<? extends Annotation> annotationClass) {
        var tagData = new ArrayList<TagData>();

        for (Field field : RuntimeUtils.getComponents(type)) {
            var annotation = field.getAnnotation(annotationClass);

            if (annotation != null) {
                Class<ASN1Type> fieldType = (Class<ASN1Type>) field.getType();

                if (ASN1Choice.class.isAssignableFrom(fieldType)) {
                    var obj = getInstance(fieldType, (e) -> new DecodingException(e));
                    var componentsTagData = getTagData(obj, annotationClass);

                    tagData.addAll(componentsTagData.stream().map(t -> {
                        var oldSetter = t.setter;

                        t.setter = (result) -> {
                            getSetter(field, type).accept(obj);
                            oldSetter.accept(result);
                        };

                        return t;
                    }).collect(Collectors.toList()));

                } else {
                    var tags = RuntimeUtils.getTags(fieldType, field.getAnnotation(ASN1Tag.class));

                    tagData.add(new TagData(tags, field, getSetter(field, type)));
                }
            }
        }

        return tagData;
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
        return tagData.stream()
                .collect(Collectors.toMap(t -> t.getTags(), t -> (Class<? extends ASN1Type>) t.getField().getType()));
    }

    public Set<List<TagId>> getMandatoryFields() {
        return tagData.stream()
                .filter(t -> !t.getField().getAnnotation(ASN1Component.class).optional())
                .map(t -> t.getTagIds())
                .collect(Collectors.toSet());
    }

    protected String getFieldName(List<TagId> tags) {
        return tagData.stream()
                .filter(t -> t.getTagIds().equals(tags))
                .map(TagData::getField)
                .findFirst()
                .map(f -> f.getDeclaringClass().getSimpleName() + "." + f.getName())
                .orElseThrow(() -> new DecodingException("Couldn't find field for tags: " + tags));
    }

    public Consumer<ASN1Type> getSetter(List<TagId> tags) {
        return tagData.stream()
                .filter(t -> t.getTagIds().equals(tags))
                .map(TagData::getSetter)
                .findFirst()
                .orElseThrow(() -> new DecodingException("Couldn't find setter for tags: " + tags));
    }

    public List<TagId> getTagIds(List<ASN1Tag> tags) {
        return tagData.stream()
                .filter(t -> t.getTags().equals(tags))
                .map(t -> t.getTagIds())
                .findAny()
                .orElseThrow(() -> new DecodingException("Couldn't find tag ids for tags: " + tags));
    }

    @Override
    public String toString() {
        return ToString.get(this);
    }

    static class TagData {

        private List<ASN1Tag> tags;

        private List<TagId> tagIds;

        private Field field;

        private Consumer<ASN1Type> setter;

        public TagData(List<ASN1Tag> tags, Field field, Consumer<ASN1Type> setter) {
            this.tags = tags;
            this.tagIds = tags.stream().map(TagId::fromTag).collect(Collectors.toList());
            this.field = field;
            this.setter = setter;
        }

        public List<ASN1Tag> getTags() {
            return tags;
        }

        public List<TagId> getTagIds() {
            return tagIds;
        }

        public Field getField() {
            return field;
        }

        public Consumer<ASN1Type> getSetter() {
            return setter;
        }

    }

}
