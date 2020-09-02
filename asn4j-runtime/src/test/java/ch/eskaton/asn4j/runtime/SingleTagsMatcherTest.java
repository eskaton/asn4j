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

package ch.eskaton.asn4j.runtime;

import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tags;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SingleTagsMatcherTest extends AbstractTagsMatcherTest {

    @Test
    void testEmptyTags() {
        List<ASN1Tag> emptyList = Collections.emptyList();

        assertThrows(IllegalArgumentException.class, () -> new BERDecoder.SingleTagsMatcher(emptyList));
    }

    @Test
    void testSingleTag() {
        BERDecoder.SingleTagsMatcher matcher = new BERDecoder.SingleTagsMatcher(getTags(TestTag1.class));

        assertTrue(matcher.hasNext());

        TLV tlv = getTlv(Clazz.UNIVERSAL, 1, false);

        assertTrue(matcher.accept(tlv));
        assertFalse(matcher.hasNext());
    }

    @Test
    void testSingleTagFailure() {
        BERDecoder.SingleTagsMatcher matcher = new BERDecoder.SingleTagsMatcher(getTags(TestTag1.class));

        assertTrue(matcher.hasNext());

        TLV tlv = getTlv(Clazz.UNIVERSAL, 7, false);

        assertFalse(matcher.accept(tlv));
        assertFalse(matcher.hasNext());
    }

    @Test
    void testMultipleTags() {
        BERDecoder.SingleTagsMatcher matcher = new BERDecoder.SingleTagsMatcher(getTags(TestTag2.class));

        assertTrue(matcher.hasNext());

        TLV tlv = getTlv(Clazz.APPLICATION, 2, false);

        assertTrue(matcher.accept(tlv));

        tlv = getTlv(Clazz.UNIVERSAL, 1, false);

        assertTrue(matcher.hasNext());
        assertTrue(matcher.accept(tlv));
        assertFalse(matcher.hasNext());
    }

    @Test
    void testMultipleTagsFailure() {
        BERDecoder.SingleTagsMatcher matcher = new BERDecoder.SingleTagsMatcher(getTags(TestTag2.class));

        assertTrue(matcher.hasNext());

        TLV tlv = getTlv(Clazz.UNIVERSAL, 7, false);

        assertFalse(matcher.accept(tlv));
        assertTrue(matcher.hasNext());
    }

    @ASN1Tags(tags = @ASN1Tag(clazz = Clazz.UNIVERSAL, tag = 1))
    private static class TestTag1 implements ASN1Type {

    }

    @ASN1Tags(tags = { @ASN1Tag(clazz = Clazz.APPLICATION, tag = 2), @ASN1Tag(clazz = Clazz.UNIVERSAL, tag = 1) })
    private static class TestTag2 extends TestTag1 {

    }

}
