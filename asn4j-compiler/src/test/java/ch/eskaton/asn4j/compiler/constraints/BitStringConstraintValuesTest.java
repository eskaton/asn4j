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
import static java.util.Collections.emptySet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BitStringConstraintValuesTest {

    @Test
    public void testCopy() {
        BitStringConstraintValues values = toBitStringValues("001", "010").inverted(true);

        assertEquals(values, values.copy());
    }

    @Test
    public void testInvert() {
        BitStringConstraintValues values = toBitStringValues("001", "010");

        assertEquals(false, values.isInverted());
        assertEquals(true, values.invert().isInverted());
        assertEquals(false, values.invert().invert().isInverted());
    }

    @Test
    public void testUnion() {
        BitStringConstraintValues values1 =  toBitStringValues("001", "010");
        BitStringConstraintValues values2 =  toBitStringValues("011", "100");

        BitStringConstraintValues union = values1.union(values2);

        assertEquals(toBitStringSet("001", "010", "011", "100"), union.getValues());

        values1 =  toBitStringValues("001", "010").invert();
        values2 =  toBitStringValues("011", "100").invert();

        union = values1.union(values2);

        assertEquals(emptySet(), union.getValues());
        assertTrue(union.isInverted());

        values1 =  toBitStringValues("001", "010", "011").invert();
        values2 =  toBitStringValues("010", "011", "100").invert();

        union = values1.union(values2);

        assertEquals(toBitStringSet("010", "011"), union.getValues());
        assertTrue(union.isInverted());

        values1 =  toBitStringValues("001", "010", "011").invert();
        values2 =  toBitStringValues("010", "011", "100");

        union = values1.union(values2);

        assertEquals(toBitStringSet("001"), union.getValues());
        assertTrue(union.isInverted());

        values1 =  toBitStringValues("001", "010", "011");
        values2 =  toBitStringValues("010", "011", "100").invert();

        union = values1.union(values2);

        assertEquals(toBitStringSet("100"), union.getValues());
        assertTrue(union.isInverted());
    }

    @Test
    public void testIntersection() {
        BitStringConstraintValues values1 = toBitStringValues("001", "010", "011");
        BitStringConstraintValues values2 = toBitStringValues("011", "100", "110");

        BitStringConstraintValues intersection = values1.intersection(values2);

        assertEquals(toBitStringSet("011"), intersection.getValues());

        values1 =  toBitStringValues("001", "010").invert();
        values2 =  toBitStringValues("011", "100").invert();

        intersection = values1.intersection(values2);

        assertEquals(toBitStringSet("001", "010", "011", "100"), intersection.getValues());

        values1 =  toBitStringValues("001", "010").invert();
        values2 =  toBitStringValues("001", "010").invert();

        intersection = values1.intersection(values2);

        assertEquals(toBitStringSet("001", "010"), intersection.getValues());
        assertTrue(intersection.isInverted());

        values1 =  toBitStringValues("001", "010").invert();
        values2 =  toBitStringValues("001", "010");

        intersection = values1.intersection(values2);

        assertEquals(emptySet(), intersection.getValues());
        assertFalse(intersection.isInverted());

        values1 =  toBitStringValues("001", "010", "011").invert();
        values2 =  toBitStringValues("011", "100", "110");

        intersection = values1.intersection(values2);

        assertEquals(toBitStringSet("100", "110"), intersection.getValues());
        assertFalse(intersection.isInverted());

        values1 =  toBitStringValues("001", "010");
        values2 =  toBitStringValues("001", "010").invert();

        intersection = values1.intersection(values2);

        assertEquals(emptySet(), intersection.getValues());
        assertFalse(intersection.isInverted());

        values1 =  toBitStringValues("001", "010", "011");
        values2 =  toBitStringValues("011", "100", "110").invert();

        intersection = values1.intersection(values2);

        assertEquals(toBitStringSet("001", "010"), intersection.getValues());
        assertFalse(intersection.isInverted());

    }

}
