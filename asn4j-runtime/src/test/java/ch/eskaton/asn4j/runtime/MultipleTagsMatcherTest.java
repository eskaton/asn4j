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
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class MultipleTagsMatcherTest extends AbstractTagsMatcherTest {

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyTags() {
        new BERDecoder.MultipleTagsMatcher(Collections.emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyTagsList() {
        new BERDecoder.MultipleTagsMatcher(asList(getTags(TestTag1a.class), Collections.emptyList()));
    }

    @Test
    public void testImplicit() {
        List<ASN1Tag> tags1 = getTags(TestTag1a.class);
        List<ASN1Tag> tags2 = getTags(TestTag2a.class);
        BERDecoder.MultipleTagsMatcher matcher = new BERDecoder.MultipleTagsMatcher(asList(tags1, tags2));

        assertTrue(matcher.hasNext());

        TLV tlv = getTlv(Clazz.Universal, 3, false);

        assertTrue(matcher.accept(tlv));
        assertFalse(matcher.hasNext());
        assertEquals(tags2, matcher.getMatch());

        matcher = new BERDecoder.MultipleTagsMatcher(asList(tags1, tags2));

        assertTrue(matcher.hasNext());

        tlv = getTlv(Clazz.Universal, 1, false);

        assertTrue(matcher.accept(tlv));
        assertFalse(matcher.hasNext());
        assertEquals(tags1, matcher.getMatch());
    }

    @Test
    public void testImplicitFailure() {
        BERDecoder.MultipleTagsMatcher matcher = new BERDecoder.MultipleTagsMatcher(asList(getTags(TestTag1a.class), getTags(TestTag2a.class)));

        assertTrue(matcher.hasNext());

        TLV tlv = getTlv(Clazz.Application, 2, false);

        assertFalse(matcher.accept(tlv));
        assertFalse(matcher.hasNext());
        assertNull(matcher.getMatch());
    }

    @Test
    public void testExplicit() {
        List<ASN1Tag> tags1 = getTags(TestTag1b.class);
        List<ASN1Tag> tags2 = getTags(TestTag2b.class);
        BERDecoder.MultipleTagsMatcher matcher = new BERDecoder.MultipleTagsMatcher(asList(tags1, tags2));

        assertTrue(matcher.hasNext());

        TLV tlv = getTlv(Clazz.Application, 4, false);

        assertTrue(matcher.accept(tlv));
        assertTrue(matcher.hasNext());
        assertNull(matcher.getMatch());

        tlv = getTlv(Clazz.Universal, 3, false);

        assertTrue(matcher.accept(tlv));
        assertFalse(matcher.hasNext());
        assertEquals(tags2, matcher.getMatch());

        matcher = new BERDecoder.MultipleTagsMatcher(asList(tags1, tags2));

        assertTrue(matcher.hasNext());

        tlv = getTlv(Clazz.Application, 2, false);

        assertTrue(matcher.accept(tlv));
        assertTrue(matcher.hasNext());
        assertNull(matcher.getMatch());

        tlv = getTlv(Clazz.Universal, 1, false);

        assertTrue(matcher.accept(tlv));
        assertFalse(matcher.hasNext());
        assertEquals(tags1, matcher.getMatch());
    }

    @Test
    public void testExplicitFailure() {
        BERDecoder.MultipleTagsMatcher matcher = new BERDecoder.MultipleTagsMatcher(asList(getTags(TestTag1b.class), getTags(TestTag2b.class)));

        assertTrue(matcher.hasNext());

        TLV tlv = getTlv(Clazz.Application, 4, false);

        assertTrue(matcher.accept(tlv));
        assertTrue(matcher.hasNext());
        assertNull(matcher.getMatch());

        tlv = getTlv(Clazz.Universal, 1, false);

        assertFalse(matcher.accept(tlv));
        assertFalse(matcher.hasNext());
        assertNull(matcher.getMatch());
    }

    @ASN1Tag(clazz = Clazz.Universal, tag = 1, mode = ASN1Tag.Mode.Explicit, constructed = false)
    private static class TestTag1a implements ASN1Type {
    }

    @ASN1Tag(clazz = Clazz.Application, tag = 2, mode = ASN1Tag.Mode.Explicit, constructed = false)
    private static class TestTag1b extends TestTag1a {
    }

    @ASN1Tag(clazz = Clazz.Universal, tag = 3, mode = ASN1Tag.Mode.Explicit, constructed = false)
    private static class TestTag2a implements ASN1Type {
    }

    @ASN1Tag(clazz = Clazz.Application, tag = 4, mode = ASN1Tag.Mode.Explicit, constructed = false)
    private static class TestTag2b extends TestTag2a {
    }

}
