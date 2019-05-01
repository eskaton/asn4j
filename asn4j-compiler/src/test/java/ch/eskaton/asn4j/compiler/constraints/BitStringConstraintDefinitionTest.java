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

package ch.eskaton.asn4j.compiler.constraints;

import org.junit.Test;

import static ch.eskaton.asn4j.compiler.constraints.BitStringTestUtils.toBitStringSet;
import static ch.eskaton.asn4j.compiler.constraints.BitStringTestUtils.toBitStringValues;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BitStringConstraintDefinitionTest {

    @Test
    public void testUnionOnlyRoots() {
        BitStringConstraintDefinition a = new BitStringConstraintDefinition()
                .rootValues(toBitStringValues("1000", "1100"))
                .extensible(false);
        BitStringConstraintDefinition b = new BitStringConstraintDefinition()
                .rootValues(toBitStringValues("1100", "1110"))
                .extensible(false);

        BitStringConstraintDefinition union = a.union(b);

        assertFalse(union.isExtensible());
        assertTrue(union.getExtensionValues().isEmpty());
        assertEquals(new BitStringConstraintValues(toBitStringSet("1000", "1100", "1110")),
                union.getRootValues());
    }

    @Test
    public void testUnionOneExtension() {
        BitStringConstraintDefinition a = new BitStringConstraintDefinition()
                .rootValues(toBitStringValues("1000", "1100"))
                .extensible(false);
        BitStringConstraintDefinition b = new BitStringConstraintDefinition()
                .rootValues(toBitStringValues("1100", "1110"))
                .extensionValues(toBitStringValues("1111"))
                .extensible(false);

        BitStringConstraintDefinition union = a.union(b);

        assertTrue(union.isExtensible());
        assertEquals(toBitStringValues("1000", "1100", "1110"), union.getRootValues());
        assertEquals(toBitStringValues("1111"), union.getExtensionValues());
    }

    @Test
    public void testUnionTwoExtension() {
        BitStringConstraintDefinition a = new BitStringConstraintDefinition()
                .rootValues(toBitStringValues("1000", "1100"))
                .extensionValues(toBitStringValues("0000"))
                .extensible(false);
        BitStringConstraintDefinition b = new BitStringConstraintDefinition()
                .rootValues(toBitStringValues("1100", "1110"))
                .extensionValues(toBitStringValues("1111"))
                .extensible(false);

        BitStringConstraintDefinition union = a.union(b);

        assertTrue(union.isExtensible());
        assertEquals(toBitStringValues("1000", "1100", "1110"), union.getRootValues());
        assertEquals(toBitStringValues("0000", "1111"), union.getExtensionValues());
    }

    @Test
    public void testUnionTwoExtensionWithRootIntersection() {
        BitStringConstraintDefinition a = new BitStringConstraintDefinition()
                .rootValues(toBitStringValues("1000", "1100"))
                .extensionValues(toBitStringValues("0000"))
                .extensible(false);
        BitStringConstraintDefinition b = new BitStringConstraintDefinition()
                .rootValues(toBitStringValues("1100", "1110"))
                .extensionValues(toBitStringValues("1000"))
                .extensible(false);

        BitStringConstraintDefinition union = a.union(b);

        assertTrue(union.isExtensible());
        assertEquals(toBitStringValues("1000", "1100", "1110"), union.getRootValues());
        assertEquals(toBitStringValues("0000"), union.getExtensionValues());
    }

    @Test
    public void testIntersectionOnlyRoots() {
        BitStringConstraintDefinition a = new BitStringConstraintDefinition()
                .rootValues(toBitStringValues("1000", "1100"))
                .extensible(false);
        BitStringConstraintDefinition b = new BitStringConstraintDefinition()
                .rootValues(toBitStringValues("1100", "1110"))
                .extensible(false);

        BitStringConstraintDefinition intersection = a.intersection(b);

        assertTrue(intersection.getExtensionValues().isEmpty());
        assertEquals(new BitStringConstraintValues(toBitStringSet("1100")),
                intersection.getRootValues());
    }

    @Test
    public void testIntersectionOneExtension() {
        BitStringConstraintDefinition a = new BitStringConstraintDefinition()
                .rootValues(toBitStringValues("1000", "1100"))
                .extensionValues(toBitStringValues("0000", "1111"))
                .extensible(false);
        BitStringConstraintDefinition b = new BitStringConstraintDefinition()
                .rootValues(toBitStringValues("1100", "1111"))
                .extensible(false);

        BitStringConstraintDefinition intersection = a.intersection(b);

        assertTrue(intersection.isExtensible());
        assertEquals(toBitStringValues("1100"), intersection.getRootValues());
        assertEquals(toBitStringValues("1111"), intersection.getExtensionValues());
    }

    @Test
    public void testIntersectionTwoExtension() {
        BitStringConstraintDefinition a = new BitStringConstraintDefinition()
                .rootValues(toBitStringValues("1000", "1100"))
                .extensionValues(toBitStringValues("0000", "1111"))
                .extensible(false);
        BitStringConstraintDefinition b = new BitStringConstraintDefinition()
                .rootValues(toBitStringValues("1100", "1110"))
                .extensionValues(toBitStringValues("1000", "1111"))
                .extensible(false);

        BitStringConstraintDefinition intersection = a.intersection(b);

        assertTrue(intersection.isExtensible());
        assertEquals(toBitStringValues("1100"), intersection.getRootValues());
        assertEquals(toBitStringValues("1000", "1111"), intersection.getExtensionValues());
    }

}
