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

import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.parser.ast.EndpointNode;
import ch.eskaton.asn4j.parser.ast.RangeNode;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static ch.eskaton.asn4j.test.TestUtils.assertThrows;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IntegerConstraintValuesTest {

    @Test
    public void testCompareCanonicalEndpoint() {
        assertEquals(-1, invokeCompareCanonicalEndpoint(-2, -1));
        assertEquals(-2, invokeCompareCanonicalEndpoint(-5, -1));
        assertEquals(0, invokeCompareCanonicalEndpoint(5, 5));
        assertEquals(1, invokeCompareCanonicalEndpoint(5, 4));
        assertEquals(2, invokeCompareCanonicalEndpoint(5, -5));
        assertEquals(2, invokeCompareCanonicalEndpoint(Long.MAX_VALUE, Long.MIN_VALUE));
        assertEquals(-2, invokeCompareCanonicalEndpoint(Long.MIN_VALUE, Long.MAX_VALUE));
        assertEquals(-1, invokeCompareCanonicalEndpoint(Long.MIN_VALUE, Long.MIN_VALUE + 1));
    }

    @Test
    public void testCompareCanonicalRange() {
        assertTrue(-1 >= invokeCompareCanonicalRange(-2L, -1L, -1L, 0L));
        assertTrue(-1 >= invokeCompareCanonicalRange(-2L, -1L, -2L, 0L));
        assertEquals(0, invokeCompareCanonicalRange(-2L, -1L, -2L, -1L));
        assertTrue(1 <= invokeCompareCanonicalRange(0L, 5L, -5L, 5L));
        assertTrue(1 <= invokeCompareCanonicalRange(0L, 5L, 0L, 4L));
    }

    @Test
    public void testCanonicalizeRanges() {
        assertEquals(new ArrayList<RangeNode>(), invokeCanonicalizeRanges(emptyList()));

        // 2 ranges
        assertEquals(asList(createRange(0L, 5L), createRange(7L, 10L)), invokeCanonicalizeRanges(
                asList(createRange(0L, 5L), createRange(7L, 10L))));

        assertEquals(asList(createRange(0L, 10L)), invokeCanonicalizeRanges(
                asList(createRange(0L, 5L), createRange(6L, 10L))));

        assertEquals(asList(createRange(0L, 10L)), invokeCanonicalizeRanges(
                asList(createRange(0L, 5L), createRange(5L, 10L))));

        assertEquals(asList(createRange(0L, 10L)), invokeCanonicalizeRanges(
                asList(createRange(0L, 5L), createRange(3L, 10L))));

        assertEquals(asList(createRange(0L, 5L)), invokeCanonicalizeRanges(
                asList(createRange(0L, 5L), createRange(3L, 5L))));

        assertEquals(asList(createRange(0L, 5L)), invokeCanonicalizeRanges(
                asList(createRange(0L, 5L), createRange(3L, 4L))));

        // 3 ranges
        assertEquals(asList(createRange(0L, 5L), createRange(7L, 10L), createRange(15L, 20L)),
                invokeCanonicalizeRanges(asList(createRange(0L, 5L), createRange(7L, 10L), createRange(15L, 20L))));

        assertEquals(asList(createRange(0L, 5L), createRange(7L, 20L)),
                invokeCanonicalizeRanges(asList(createRange(0L, 5L), createRange(7L, 16L), createRange(15L, 20L))));

        assertEquals(asList(createRange(0L, 20L)),
                invokeCanonicalizeRanges(asList(createRange(0L, 6L), createRange(7L, 16L), createRange(15L, 20L))));

        assertEquals(asList(createRange(0L, 13L), createRange(15L, 20L)),
                invokeCanonicalizeRanges(asList(createRange(0L, 6L), createRange(7L, 13L), createRange(15L, 20L))));
    }

    @Test
    public void testCalculateExclude() {
        IntegerConstraintCompiler compiler = new IntegerConstraintCompiler(null);

        assertThrows(() -> invokeCalculateExclude(compiler, asList(createRange(5L, 10L)), asList(createRange(3L, 4L))),
                CompilerException.class);

        assertThrows(() -> invokeCalculateExclude(compiler, asList(createRange(5L, 10L)), asList(createRange(3L, 6L))),
                CompilerException.class);

        assertThrows(() -> invokeCalculateExclude(compiler, asList(createRange(5L, 10L)), asList(createRange(8L, 12L))),
                CompilerException.class);

        assertThrows(() -> invokeCalculateExclude(compiler, asList(createRange(5L, 10L)), asList(createRange(11L, 12L))),
                CompilerException.class);

        assertEquals(asList(),
                invokeCalculateExclude(compiler, asList(createRange(0L, 10L)), asList(createRange(0L, 10L))));

        assertEquals(asList(createRange(6L, 10L)),
                invokeCalculateExclude(compiler, asList(createRange(0L, 10L)), asList(createRange(0L, 5L))));

        assertEquals(asList(createRange(0L, 2L), createRange(9L, 10L)),
                invokeCalculateExclude(compiler, asList(createRange(0L, 10L)), asList(createRange(3L, 8L))));

        assertEquals(asList(createRange(0L, 5L)),
                invokeCalculateExclude(compiler, asList(createRange(0L, 10L)), asList(createRange(6L, 10L))));

        assertEquals(asList(createRange(0L, 5L), createRange(7L, 10L), createRange(20L, 30L)),
                invokeCalculateExclude(compiler, asList(createRange(0L, 10L), createRange(20L, 30L)),
                        asList(createRange(6L, 6L))));

        assertEquals(asList(createRange(0L, 10L), createRange(20L, 21L), createRange(23L, 30L)),
                invokeCalculateExclude(compiler, asList(createRange(0L, 10L), createRange(20L, 30L)),
                        asList(createRange(22L, 22L))));

        assertEquals(asList(createRange(0L, 10L), createRange(40L, 50L), createRange(80L, 90L)),
                invokeCalculateExclude(compiler,
                        asList(createRange(0L, 10L), createRange(20L, 30L), createRange(40L, 50L),
                                createRange(60L, 70L), createRange(80L, 90L)),
                        asList(createRange(20L, 30L), createRange(60L, 70L))));
    }

    private int invokeCompareCanonicalEndpoint(long a, long b) {
        return IntegerConstraintValues.compareCanonicalEndpoint(new EndpointNode(new IntegerValue(a), true),
                new EndpointNode(new IntegerValue(b), true));
    }

    private int invokeCompareCanonicalRange(long a1, long a2, long b1, long b2) {
        return IntegerConstraintValues.compareCanonicalRange(createRange(a1, a2), createRange(b1, b2));
    }

    private List<RangeNode> invokeCalculateExclude(IntegerConstraintCompiler compiler, List<RangeNode> a,
            List<RangeNode> b) {
        return compiler.calculateExclude(new IntegerConstraintValues(a), new IntegerConstraintValues(b)).getValues();
    }

    public List<RangeNode> invokeCanonicalizeRanges(List<RangeNode> ranges) {
        return IntegerConstraintValues.canonicalizeRanges(new IntegerConstraintValues(ranges)).getValues();
    }

    private RangeNode createRange(Long l1, Long l2) {
        return new RangeNode(new EndpointNode(new IntegerValue(l1), true),
                new EndpointNode(new IntegerValue(l2), true));
    }

}
