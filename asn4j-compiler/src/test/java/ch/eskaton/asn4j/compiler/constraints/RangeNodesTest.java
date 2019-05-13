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
import ch.eskaton.asn4j.parser.ast.values.Value;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static ch.eskaton.asn4j.compiler.constraints.RangeNodeTestUtils.createRange;
import static ch.eskaton.asn4j.compiler.constraints.RangeNodes.canonicalizeLowerEndpoint;
import static ch.eskaton.asn4j.compiler.constraints.RangeNodes.canonicalizeRanges;
import static ch.eskaton.asn4j.compiler.constraints.RangeNodes.canonicalizeUpperEndpoint;
import static ch.eskaton.asn4j.compiler.constraints.RangeNodes.compareCanonicalEndpoint;
import static ch.eskaton.asn4j.compiler.constraints.RangeNodes.compareCanonicalRange;
import static ch.eskaton.asn4j.compiler.constraints.RangeNodes.intersection;
import static ch.eskaton.asn4j.compiler.constraints.RangeNodes.invert;
import static ch.eskaton.asn4j.compiler.constraints.RangeNodes.union;
import static ch.eskaton.asn4j.test.TestUtils.assertThrows;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RangeNodesTest {

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
    public void testCanonicalizeEndpoints() {
        assertEquals(new EndpointNode(new IntegerValue(Long.MIN_VALUE), true),
                canonicalizeLowerEndpoint(new EndpointNode(Value.MIN, true), Long.MIN_VALUE));

        assertEquals(new EndpointNode(new IntegerValue(Long.MIN_VALUE + 1), true),
                canonicalizeLowerEndpoint(new EndpointNode(Value.MIN, false), Long.MIN_VALUE));

        assertEquals(new EndpointNode(new IntegerValue(-12), true),
                canonicalizeLowerEndpoint(new EndpointNode(Value.MIN, true), -12));

        assertEquals(new EndpointNode(new IntegerValue(-11), true),
                canonicalizeLowerEndpoint(new EndpointNode(Value.MIN, false), -12));

        assertEquals(new EndpointNode(new IntegerValue(Long.MAX_VALUE), true),
                canonicalizeUpperEndpoint(new EndpointNode(Value.MAX, true), Long.MAX_VALUE));

        assertEquals(new EndpointNode(new IntegerValue(Long.MAX_VALUE - 1), true),
                canonicalizeUpperEndpoint(new EndpointNode(Value.MAX, false), Long.MAX_VALUE));

        assertEquals(new EndpointNode(new IntegerValue(23), true),
                canonicalizeUpperEndpoint(new EndpointNode(Value.MAX, true), 23));

        assertEquals(new EndpointNode(new IntegerValue(22), true),
                canonicalizeUpperEndpoint(new EndpointNode(Value.MAX, false), 23));

        assertEquals(new EndpointNode(new IntegerValue(-15), true),
                canonicalizeLowerEndpoint(new EndpointNode(new IntegerValue(-15), true), Long.MIN_VALUE));

        assertEquals(new EndpointNode(new IntegerValue(-14), true),
                canonicalizeLowerEndpoint(new EndpointNode(new IntegerValue(-15), false), Long.MIN_VALUE));

        assertEquals(new EndpointNode(new IntegerValue(15), true),
                canonicalizeUpperEndpoint(new EndpointNode(new IntegerValue(15), true), Long.MAX_VALUE));

        assertEquals(new EndpointNode(new IntegerValue(14), true),
                canonicalizeUpperEndpoint(new EndpointNode(new IntegerValue(15), false), Long.MAX_VALUE));
    }

    @Test
    public void testCanonicalizeRanges() {
        assertEquals(new ArrayList<RangeNode>(), canonicalizeRanges(emptyList()));

        // 2 ranges
        assertEquals(asList(createRange(0L, 5L), createRange(7L, 10L)), canonicalizeRanges(asList(createRange(0L, 5L),
                createRange(7L, 10L))));

        assertEquals(asList(createRange(0L, 10L)), canonicalizeRanges(asList(createRange(0L, 5L),
                createRange(6L, 10L))));

        assertEquals(asList(createRange(0L, 10L)), canonicalizeRanges(asList(createRange(0L, 5L),
                createRange(5L, 10L))));

        assertEquals(asList(createRange(0L, 10L)), canonicalizeRanges(asList(createRange(0L, 5L),
                createRange(3L, 10L))));

        assertEquals(asList(createRange(0L, 5L)), canonicalizeRanges(asList(createRange(0L, 5L), createRange(3L, 5L))));

        assertEquals(asList(createRange(0L, 5L)), canonicalizeRanges(asList(createRange(0L, 5L), createRange(3L, 4L))));

        // 3 ranges
        assertEquals(asList(createRange(0L, 5L), createRange(7L, 10L), createRange(15L, 20L)),
                canonicalizeRanges(asList(createRange(0L, 5L), createRange(7L, 10L), createRange(15L, 20L))));

        assertEquals(asList(createRange(0L, 5L), createRange(7L, 20L)),
                canonicalizeRanges(asList(createRange(0L, 5L), createRange(7L, 16L), createRange(15L, 20L))));

        assertEquals(asList(createRange(0L, 20L)),
                canonicalizeRanges(asList(createRange(0L, 6L), createRange(7L, 16L), createRange(15L, 20L))));

        assertEquals(asList(createRange(0L, 13L), createRange(15L, 20L)),
                canonicalizeRanges(asList(createRange(0L, 6L), createRange(7L, 13L), createRange(15L, 20L))));
    }

    @Test
    public void testUnion() {
        assertEquals(asList(createRange(Long.MIN_VALUE, Long.MAX_VALUE)),
                union(asList(createRange(Long.MIN_VALUE, Long.MAX_VALUE)),
                        asList(createRange(Long.MIN_VALUE, Long.MAX_VALUE))));

        assertEquals(asList(createRange(5L, 30L)), union(asList(createRange(5L, 20L)), asList(createRange(18L, 30L))));

        assertEquals(asList(createRange(5L, 40L)),
                union(asList(createRange(5L, 20L)), asList(createRange(18L, 30L), createRange(29L, 40L))));

        assertEquals(asList(createRange(5L, 40L)),
                union(asList(createRange(5L, 20L), createRange(18L, 30L)), asList(createRange(29L, 40L))));
    }

    @Test
    public void testIntersect() {
        assertEquals(asList(createRange(Long.MIN_VALUE, Long.MAX_VALUE)),
                union(asList(createRange(Long.MIN_VALUE, Long.MAX_VALUE)),
                        asList(createRange(Long.MIN_VALUE, Long.MAX_VALUE))));
        assertEquals(emptyList(), intersection(asList(createRange(5L, 20L)), emptyList()));
        assertEquals(emptyList(), intersection(emptyList(), asList(createRange(5L, 20L))));

        assertEquals(emptyList(), intersection(asList(createRange(7L, 12L)), asList(createRange(15L, 30L))));

        assertEquals(emptyList(), intersection(asList(createRange(15L, 30L)), asList(createRange(7L, 12L))));

        assertEquals(asList(createRange(20L, 25L)),
                intersection(asList(createRange(15L, 30L)), asList(createRange(20L, 25L))));

        assertEquals(asList(createRange(20L, 25L)),
                intersection(asList(createRange(20L, 25L)), asList(createRange(15L, 30L))));

        assertEquals(asList(createRange(7L, 20L)),
                intersection(asList(createRange(5L, 20L)), asList(createRange(7L, 25L))));

        assertEquals(asList(createRange(7L, 15L), createRange(22L, 25L)),
                intersection(asList(createRange(5L, 15L), createRange(22L, 30L)), asList(createRange(7L, 25L))));

        assertEquals(asList(createRange(7L, 15L)),
                intersection(asList(createRange(5L, 15L)), asList(createRange(7L, 25L), createRange(22L, 30L))));
    }

    @Test
    public void testInvert() {
        assertEquals(asList(createRange(Long.MIN_VALUE, 4L), createRange(11L, Long.MAX_VALUE)),
                invert(asList(createRange(5L, 10L))));

        assertEquals(emptyList(), invert(asList(createRange(Long.MIN_VALUE, Long.MAX_VALUE))));

        assertEquals(asList(createRange(Long.MIN_VALUE, 4L), createRange(11L, 14L), createRange(21L, Long.MAX_VALUE)),
                invert(asList(createRange(5L, 10L), createRange(15L, 20L))));

        assertEquals(asList(createRange(11L, 14L)),
                invert(asList(createRange(Long.MIN_VALUE, 10L), createRange(15L, Long.MAX_VALUE))));

        assertEquals(asList(createRange(Long.MIN_VALUE, -1L), createRange(11L, 12L), createRange(21L, Long.MAX_VALUE)),
                invert(asList(createRange(0L, 10L), createRange(15L, 20L), createRange(13L, 17L))));
    }

    @Test
    public void testExclude() {
        assertThrows(() -> invokeCalculateExclude(asList(createRange(5L, 10L)), asList(createRange(3L, 4L))),
                CompilerException.class);

        assertThrows(() -> invokeCalculateExclude(asList(createRange(5L, 10L)), asList(createRange(3L, 6L))),
                CompilerException.class);

        assertThrows(() -> invokeCalculateExclude(asList(createRange(5L, 10L)), asList(createRange(8L, 12L))),
                CompilerException.class);

        assertThrows(() -> invokeCalculateExclude(asList(createRange(5L, 10L)), asList(createRange(11L, 12L))),
                CompilerException.class);

        assertEquals(asList(),
                invokeCalculateExclude(asList(createRange(0L, 10L)), asList(createRange(0L, 10L))));

        assertEquals(asList(createRange(6L, 10L)),
                invokeCalculateExclude(asList(createRange(0L, 10L)), asList(createRange(0L, 5L))));

        assertEquals(asList(createRange(0L, 2L), createRange(9L, 10L)),
                invokeCalculateExclude(asList(createRange(0L, 10L)), asList(createRange(3L, 8L))));

        assertEquals(asList(createRange(0L, 5L)),
                invokeCalculateExclude(asList(createRange(0L, 10L)), asList(createRange(6L, 10L))));

        assertEquals(asList(createRange(0L, 5L), createRange(7L, 10L), createRange(20L, 30L)),
                invokeCalculateExclude(asList(createRange(0L, 10L), createRange(20L, 30L)),
                        asList(createRange(6L, 6L))));

        assertEquals(asList(createRange(0L, 10L), createRange(20L, 21L), createRange(23L, 30L)),
                invokeCalculateExclude(asList(createRange(0L, 10L), createRange(20L, 30L)),
                        asList(createRange(22L, 22L))));

        assertEquals(asList(createRange(0L, 10L), createRange(40L, 50L), createRange(80L, 90L)),
                invokeCalculateExclude(
                        asList(createRange(0L, 10L), createRange(20L, 30L), createRange(40L, 50L),
                                createRange(60L, 70L), createRange(80L, 90L)),
                        asList(createRange(20L, 30L), createRange(60L, 70L))));
    }

    private List<RangeNode> invokeCalculateExclude(List<RangeNode> a, List<RangeNode> b) {
        return RangeNodes.exclude(a, b);
    }

    private int invokeCompareCanonicalEndpoint(long a, long b) {
        return compareCanonicalEndpoint(new EndpointNode(new IntegerValue(a), true),
                new EndpointNode(new IntegerValue(b), true));
    }

    private int invokeCompareCanonicalRange(long a1, long a2, long b1, long b2) {
        return compareCanonicalRange(createRange(a1, a2), createRange(b1, b2));
    }

}
