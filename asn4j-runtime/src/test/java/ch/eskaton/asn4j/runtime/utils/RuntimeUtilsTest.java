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

        assertTagEquals(tags.get(0), Clazz.PRIVATE, ASN1Tag.Mode.EXPLICIT, 3);
        assertTagEquals(tags.get(1), Clazz.CONTEXT_SPECIFIC, ASN1Tag.Mode.IMPLICIT, 2);
        assertTagEquals(tags.get(2), Clazz.UNIVERSAL, ASN1Tag.Mode.EXPLICIT, 26);

        tags = RuntimeUtils.getTags(TaggedType.class, null);

        assertNotNull(tags);
        assertEquals(3, tags.size());

        assertTagEquals(tags.get(0), Clazz.PRIVATE, ASN1Tag.Mode.EXPLICIT, 3);
        assertTagEquals(tags.get(1), Clazz.CONTEXT_SPECIFIC, ASN1Tag.Mode.IMPLICIT, 2);
        assertTagEquals(tags.get(2), Clazz.UNIVERSAL, ASN1Tag.Mode.EXPLICIT, 26);

        var tag = ExplicitTag.class.getAnnotation(ASN1Tags.class).tags()[0];

        tags = RuntimeUtils.getTags(TaggedType.class, tag);

        assertNotNull(tags);
        assertEquals(4, tags.size());

        assertTagEquals(tags.get(0), Clazz.PRIVATE, ASN1Tag.Mode.EXPLICIT, 1);
        assertTagEquals(tags.get(1), Clazz.PRIVATE, ASN1Tag.Mode.EXPLICIT, 3);
        assertTagEquals(tags.get(2), Clazz.CONTEXT_SPECIFIC, ASN1Tag.Mode.IMPLICIT, 2);
        assertTagEquals(tags.get(3), Clazz.UNIVERSAL, ASN1Tag.Mode.EXPLICIT, 26);

        tag = ImplicitTag.class.getAnnotation(ASN1Tags.class).tags()[0];

        tags = RuntimeUtils.getTags(TaggedType.class, tag);

        assertNotNull(tags);
        assertEquals(3, tags.size());

        assertTagEquals(tags.get(0), Clazz.PRIVATE, ASN1Tag.Mode.IMPLICIT, 1);
        assertTagEquals(tags.get(1), Clazz.CONTEXT_SPECIFIC, ASN1Tag.Mode.IMPLICIT, 2);
        assertTagEquals(tags.get(2), Clazz.UNIVERSAL, ASN1Tag.Mode.EXPLICIT, 26);
    }

    private void assertTagEquals(ASN1Tag annotation, Clazz clazz, ASN1Tag.Mode mode, int tag) {
        assertEquals(clazz, annotation.clazz());
        assertEquals(mode, annotation.mode());
        assertEquals(tag, annotation.tag());
    }

    @ASN1Tags(tags = {
            @ASN1Tag(clazz = Clazz.PRIVATE, mode = ASN1Tag.Mode.EXPLICIT, tag = 3),
            @ASN1Tag(clazz = Clazz.CONTEXT_SPECIFIC, mode = ASN1Tag.Mode.IMPLICIT, tag = 2),
            @ASN1Tag(clazz = Clazz.APPLICATION, mode = ASN1Tag.Mode.EXPLICIT, tag = 3),
    })
    private static class TaggedType extends ASN1VisibleString {

    }

    @ASN1Tags(tags = @ASN1Tag(clazz = Clazz.PRIVATE, mode = ASN1Tag.Mode.EXPLICIT, tag = 1))
    private static class ExplicitTag {

    }

    @ASN1Tags(tags = @ASN1Tag(clazz = Clazz.PRIVATE, mode = ASN1Tag.Mode.IMPLICIT, tag = 1))
    private static class ImplicitTag {

    }

}
