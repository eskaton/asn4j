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

package ch.eskaton.asn4j.runtime.utils;

import ch.eskaton.asn4j.runtime.Clazz;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tags;
import ch.eskaton.asn4j.runtime.objects.TestSetA;
import ch.eskaton.asn4j.runtime.types.ASN1VisibleString;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RuntimeUtilsTest {

    @Test
    void testGetComponent() {
        Field field = RuntimeUtils.getComponent(new TestSetA(), "a");

        assertNotNull(field);

        assertEquals("a", field.getName());
    }

    @Test
    void testGetTags() {
        var tags = RuntimeUtils.getTags(TaggedType.class);

        assertNotNull(tags);
        assertEquals(3, tags.size());

        assertTagEquals(tags.get(0), Clazz.PRIVATE, 3);
        assertTagEquals(tags.get(1), Clazz.CONTEXT_SPECIFIC, 2);
        assertTagEquals(tags.get(2), Clazz.UNIVERSAL, 26);

        tags = RuntimeUtils.getTags(TaggedType.class, null);

        assertNotNull(tags);
        assertEquals(3, tags.size());

        assertTagEquals(tags.get(0), Clazz.PRIVATE, 3);
        assertTagEquals(tags.get(1), Clazz.CONTEXT_SPECIFIC, 2);
        assertTagEquals(tags.get(2), Clazz.UNIVERSAL, 26);

        var tag = Arrays.asList(Tag.class.getAnnotation(ASN1Tags.class).tags());

        tags = RuntimeUtils.getTags(TaggedType.class, tag);

        assertNotNull(tags);
        assertEquals(4, tags.size());

        assertTagEquals(tags.get(0), Clazz.PRIVATE, 1);
        assertTagEquals(tags.get(1), Clazz.PRIVATE, 3);
        assertTagEquals(tags.get(2), Clazz.CONTEXT_SPECIFIC, 2);
        assertTagEquals(tags.get(3), Clazz.UNIVERSAL, 26);
    }

    private void assertTagEquals(ASN1Tag annotation, Clazz clazz, int tag) {
        assertEquals(clazz, annotation.clazz());
        assertEquals(tag, annotation.tag());
    }

    @ASN1Tags(tags = {
            @ASN1Tag(clazz = Clazz.PRIVATE, tag = 3),
            @ASN1Tag(clazz = Clazz.CONTEXT_SPECIFIC, tag = 2),
            @ASN1Tag(clazz = Clazz.UNIVERSAL, tag = 26),
    })
    private static class TaggedType extends ASN1VisibleString {

    }

    @ASN1Tags(tags = @ASN1Tag(clazz = Clazz.PRIVATE, tag = 1))
    private static class Tag {

    }


}
