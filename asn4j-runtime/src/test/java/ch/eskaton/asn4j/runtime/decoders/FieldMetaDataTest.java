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

import ch.eskaton.asn4j.runtime.Clazz;
import ch.eskaton.asn4j.runtime.TagId;
import ch.eskaton.asn4j.runtime.annotations.ASN1Component;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.objects.TestSetA;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1OctetString;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.commons.utils.ReflectionUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FieldMetaDataTest {

    @Test
    public void testFillMetaData() throws NoSuchFieldException, IllegalAccessException {
        TestSetA testSet = new TestSetA();

        FieldMetaData fieldMetaData = new FieldMetaData(testSet, ASN1Component.class);

        Map<List<ASN1Tag>, Class<? extends ASN1Type>> tagsToTypes =
                ReflectionUtils.getPrivateFieldValue(fieldMetaData, "tagsToTypes");
        Map<List<TagId>, Field> tagsToFields =
                ReflectionUtils.getPrivateFieldValue(fieldMetaData, "tagsToFields");

        assertEquals(2, tagsToTypes.size());

        Map.Entry<List<ASN1Tag>, Class<? extends ASN1Type>> tagsToType = getTagsToType(tagsToTypes, ASN1Integer.class);

        checkTagsToTypeKey(tagsToType, 0, 0, Clazz.CONTEXT_SPECIFIC);
        checkTagsToTypeKey(tagsToType, 1, 2, Clazz.UNIVERSAL);

        assertTrue(tagsToType.getValue().isAssignableFrom(ASN1Integer.class));

        tagsToType = getTagsToType(tagsToTypes, ASN1OctetString.class);

        checkTagsToTypeKey(tagsToType, 0, 1, Clazz.CONTEXT_SPECIFIC);
        checkTagsToTypeKey(tagsToType, 1, 4, Clazz.UNIVERSAL);

        assertTrue(tagsToType.getValue().isAssignableFrom(ASN1OctetString.class));

        assertEquals(2, tagsToFields.size());

        Map.Entry<List<TagId>, Field> tagsToField = getTagsToField(tagsToFields, "a");

        checkTagsToFieldKey(tagsToField, 0, 0, Clazz.CONTEXT_SPECIFIC);
        checkTagsToFieldKey(tagsToField, 1, 2, Clazz.UNIVERSAL);

        assertEquals("a", tagsToField.getValue().getName());

        tagsToField = getTagsToField(tagsToFields, "b");

        checkTagsToFieldKey(tagsToField, 0, 1, Clazz.CONTEXT_SPECIFIC);
        checkTagsToFieldKey(tagsToField, 1, 4, Clazz.UNIVERSAL);

        assertEquals("b", tagsToField.getValue().getName());
    }

    @Test
    public void testGetMandatoryFieldsMissing() {
        TestSetA testSet = new TestSetA();

        FieldMetaData fieldMetaData = new FieldMetaData(testSet, ASN1Component.class);

        Set<List<TagId>> mandatoryFields = fieldMetaData.getMandatoryFields();

        assertEquals(1, mandatoryFields.size());

        List<TagId> tags = mandatoryFields.stream().findFirst().get();

        checkTagsToFieldKey(tags, 0, 0, Clazz.CONTEXT_SPECIFIC);
        checkTagsToFieldKey(tags, 1, 2, Clazz.UNIVERSAL);
    }

    @Test
    public void testGetMandatoryFieldsComplete() throws NoSuchFieldException, IllegalAccessException {
        TestSetA testSet = new TestSetA();

        FieldMetaData fieldMetaData = new FieldMetaData(testSet, ASN1Component.class);

        Map<List<ASN1Tag>, Class<? extends ASN1Type>> tagsToTypes =
                ReflectionUtils.getPrivateFieldValue(fieldMetaData, "tagsToTypes");

        List<ASN1Tag> mandatoryKey = tagsToTypes.entrySet().stream().filter(
                e -> e.getValue().isAssignableFrom(ASN1Integer.class)).collect(Collectors.toList()).get(0).getKey();

        tagsToTypes.remove(mandatoryKey);

        Set<List<TagId>> mandatoryFields = fieldMetaData.getMandatoryFields();

        assertEquals(0, mandatoryFields.size());
    }

    @Test
    public void testCheckMandatoryFieldsComplete() throws NoSuchFieldException, IllegalAccessException {
        TestSetA testSet = new TestSetA();

        FieldMetaData fieldMetaData = new FieldMetaData(testSet, ASN1Component.class);

        Map<List<ASN1Tag>, Class<? extends ASN1Type>> tagsToTypes =
                ReflectionUtils.getPrivateFieldValue(fieldMetaData, "tagsToTypes");

        List<ASN1Tag> mandatoryKey = tagsToTypes.entrySet().stream().filter(
                e -> e.getValue().isAssignableFrom(ASN1Integer.class)).collect(Collectors.toList()).get(0).getKey();

        tagsToTypes.remove(mandatoryKey);

        fieldMetaData.getMandatoryFields();
    }

    private Map.Entry<List<ASN1Tag>, Class<? extends ASN1Type>> getTagsToType(Map<List<ASN1Tag>,
            Class<? extends ASN1Type>> tagsToTypes, Class<? extends ASN1Type> type) {
        return tagsToTypes.entrySet().stream().filter(e -> e.getValue().isAssignableFrom(type)).findFirst().get();
    }

    private void checkTagsToTypeKey(Map.Entry<List<ASN1Tag>, Class<? extends ASN1Type>> tagsToType, int key, int tag,
            Clazz clazz) {
        ASN1Tag asn1Tag = tagsToType.getKey().get(key);

        assertEquals(tag, asn1Tag.tag());
        assertEquals(clazz, asn1Tag.clazz());
    }

    private Map.Entry<List<TagId>, Field> getTagsToField(Map<List<TagId>, Field> tagsToFields, String name) {
        return tagsToFields.entrySet().stream().filter(e -> e.getValue().getName().equals(name)).findFirst().get();
    }

    private void checkTagsToFieldKey(Map.Entry<List<TagId>, Field> tagsToField, int key, int tag, Clazz clazz) {
        checkTagsToFieldKey(tagsToField.getKey(), key, tag, clazz);
    }

    private void checkTagsToFieldKey(List<TagId> tags, int key, int tag, Clazz clazz) {
        assertEquals(tag, tags.get(key).getTag());
        assertEquals(clazz, tags.get(key).getClazz());
    }

}
