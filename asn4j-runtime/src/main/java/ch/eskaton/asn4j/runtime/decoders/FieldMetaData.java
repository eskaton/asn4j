package ch.eskaton.asn4j.runtime.decoders;/*
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

import ch.eskaton.asn4j.runtime.TagId;
import ch.eskaton.asn4j.runtime.annotations.ASN1Component;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.asn4j.runtime.utils.RuntimeUtils;
import ch.eskaton.asn4j.runtime.utils.ToString;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FieldMetaData {

    private Map<List<ASN1Tag>, Class<? extends ASN1Type>> tagsToTypes = new HashMap<>();

    private Map<List<TagId>, Field> tagsToFields = new HashMap<>();

    public FieldMetaData(ASN1Type type, Class<? extends Annotation> annotationClass) {
        for (Field field : RuntimeUtils.getComponents(type)) {
            Annotation annotation = field.getAnnotation(annotationClass);

            if (annotation != null) {
                Class<? extends ASN1Type> fieldType = (Class<? extends ASN1Type>) field.getType();
                List<ASN1Tag> tags = RuntimeUtils.getTags(fieldType, field.getAnnotation(ASN1Tag.class));
                tagsToFields.put(tags.stream().map(TagId::fromTag).collect(Collectors.toList()), field);
                tagsToTypes.put(tags, fieldType);
            }
        }
    }

    public Map<List<ASN1Tag>, Class<? extends ASN1Type>> getTagsToTypes() {
        return tagsToTypes;
    }

    public Field getField(List<TagId> tags) {
        return tagsToFields.get(tags);
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

    @Override
    public String toString() {
        return ToString.get(this);
    }

}
