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
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TagIdTest {

    @Test
    public void testFromTag() {
        assertEquals(new TagId(Clazz.APPLICATION, 21), TagId.fromTag(getTag(TestA.class)));
    }

    @Test
    public void testFromTags() {
        assertEquals(Arrays.asList(new TagId(Clazz.PRIVATE, 37), new TagId(Clazz.APPLICATION, 21)),
                TagId.fromTags(Arrays.asList(getTag(TestB.class), getTag(TestA.class))));
    }

    @Test
    public void testEqualsASN1Tag() {
        assertTrue(new TagId(Clazz.APPLICATION, 21).equalsASN1Tag(getTag(TestA.class)));
    }

    private ASN1Tag getTag(Class<?> clazz) {
        return clazz.getAnnotation(ASN1Tag.class);
    }

    @ASN1Tag(clazz = Clazz.APPLICATION, tag = 21, mode = ASN1Tag.Mode.EXPLICIT, constructed = true)
    private static class TestA {

    }

    @ASN1Tag(clazz = Clazz.PRIVATE, tag = 37, mode = ASN1Tag.Mode.EXPLICIT, constructed = true)
    private static class TestB {

    }

}
