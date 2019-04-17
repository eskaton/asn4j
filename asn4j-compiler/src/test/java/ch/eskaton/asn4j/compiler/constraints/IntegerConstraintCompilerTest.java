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
import ch.eskaton.commons.utils.ReflectionUtils;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class IntegerConstraintCompilerTest {

    private int invokeCompareCanonicalEndpoint(long a, long b) throws InvocationTargetException,
            IllegalAccessException {
        return (int) ReflectionUtils.invokeStaticPrivateMethod(
                IntegerConstraintCompiler.class, "compareCanonicalEndpoint",
                new EndpointNode[] {
                        new EndpointNode(new IntegerValue(a), true),
                        new EndpointNode(new IntegerValue(b), true) });
    }

    @Test
    public void testCompareCanonicalEndpoint() throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        assertEquals(-1, invokeCompareCanonicalEndpoint(-2, -1));
        assertEquals(-2, invokeCompareCanonicalEndpoint(-5, -1));
        assertEquals(0, invokeCompareCanonicalEndpoint(5, 5));
        assertEquals(1, invokeCompareCanonicalEndpoint(5, 4));
        assertEquals(2, invokeCompareCanonicalEndpoint(5, -5));
        assertEquals(2, invokeCompareCanonicalEndpoint(Long.MAX_VALUE, Long.MIN_VALUE));
        assertEquals(-2, invokeCompareCanonicalEndpoint(Long.MIN_VALUE, Long.MAX_VALUE));
        assertEquals(-1, invokeCompareCanonicalEndpoint(Long.MIN_VALUE, Long.MIN_VALUE + 1));
    }

    private int invokeCompareCanonicalRange(long a1, long a2, long b1, long b2) throws InvocationTargetException,
            IllegalAccessException {
        return (int) ReflectionUtils.invokeStaticPrivateMethod(IntegerConstraintCompiler.class, "compareCanonicalRange",
                new RangeNode[] { createRange(a1, a2), createRange(b1, b2) });
    }

    @Test
    public void testCompareCanonicalRange() throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        assertTrue(-1 >= invokeCompareCanonicalRange(-2L, -1L, -1L, 0L));
        assertTrue(-1 >= invokeCompareCanonicalRange(-2L, -1L, -2L, 0L));
        assertEquals(0, invokeCompareCanonicalRange(-2L, -1L, -2L, -1L));
        assertTrue(1 <= invokeCompareCanonicalRange(0L, 5L, -5L, 5L));
        assertTrue(1 <= invokeCompareCanonicalRange(0L, 5L, 0L, 4L));
    }

    public List<RangeNode> invokeCanonicalizeRanges(IntegerConstraintCompiler compiler, List<RangeNode> ranges)
            throws InvocationTargetException, IllegalAccessException {
        return ((IntegerConstraintDefinition) ReflectionUtils.invokePrivateMethod(compiler, "canonicalizeRanges",
                new Object[] { new IntegerConstraintDefinition(ranges) })).getValues();
    }

    @Test
    public void testCanonicalizeRanges() throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        IntegerConstraintCompiler compiler = new IntegerConstraintCompiler(null);

        assertEquals(new ArrayList<RangeNode>(), invokeCanonicalizeRanges(compiler, emptyList()));

        // 2 ranges
        assertEquals(asList(createRange(0L, 5L), createRange(7L, 10L)), invokeCanonicalizeRanges(compiler,
                asList(createRange(0L, 5L), createRange(7L, 10L))));

        assertEquals(asList(createRange(0L, 10L)), invokeCanonicalizeRanges(compiler,
                asList(createRange(0L, 5L), createRange(6L, 10L))));

        assertEquals(asList(createRange(0L, 10L)), invokeCanonicalizeRanges(compiler,
                asList(createRange(0L, 5L), createRange(5L, 10L))));

        assertEquals(asList(createRange(0L, 10L)), invokeCanonicalizeRanges(compiler,
                asList(createRange(0L, 5L), createRange(3L, 10L))));

        assertEquals(asList(createRange(0L, 5L)), invokeCanonicalizeRanges(compiler,
                asList(createRange(0L, 5L), createRange(3L, 5L))));

        assertEquals(asList(createRange(0L, 5L)), invokeCanonicalizeRanges(compiler,
                asList(createRange(0L, 5L), createRange(3L, 4L))));

        // 3 ranges
        assertEquals(asList(createRange(0L, 5L), createRange(7L, 10L), createRange(15L, 20L)),
                invokeCanonicalizeRanges(compiler,
                        asList(createRange(0L, 5L), createRange(7L, 10L), createRange(15L, 20L))));

        assertEquals(asList(createRange(0L, 5L), createRange(7L, 20L)),
                invokeCanonicalizeRanges(compiler,
                        asList(createRange(0L, 5L), createRange(7L, 16L), createRange(15L, 20L))));

        assertEquals(asList(createRange(0L, 20L)),
                invokeCanonicalizeRanges(compiler,
                        asList(createRange(0L, 6L), createRange(7L, 16L), createRange(15L, 20L))));

        assertEquals(asList(createRange(0L, 13L), createRange(15L, 20L)),
                invokeCanonicalizeRanges(compiler,
                        asList(createRange(0L, 6L), createRange(7L, 13L), createRange(15L, 20L))));
    }

    private List<RangeNode> invokeCalculateExclude(IntegerConstraintCompiler compiler, List<RangeNode> a,
            List<RangeNode> b) throws InvocationTargetException, IllegalAccessException {
        return ((IntegerConstraintDefinition) ReflectionUtils.invokePrivateMethod(compiler, "calculateExclude",
                new Object[] { new IntegerConstraintDefinition(a), new IntegerConstraintDefinition(b) })).getValues();
    }

    @Test
    public void testCalculateExclude() throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        IntegerConstraintCompiler compiler = new IntegerConstraintCompiler(null);

        try {
            invokeCalculateExclude(compiler, asList(createRange(5L, 10L)), asList(createRange(3L, 4L)));
            fail("ASN1CompilerException expected");
        } catch (InvocationTargetException e) {
            assertTrue(e.getCause() instanceof CompilerException);
        }

        try {
            invokeCalculateExclude(compiler, asList(createRange(5L, 10L)), asList(createRange(3L, 6L)));
            fail("ASN1CompilerException expected");
        } catch (InvocationTargetException e) {
            assertTrue(e.getCause() instanceof CompilerException);
        }

        try {
            invokeCalculateExclude(compiler, asList(createRange(5L, 10L)), asList(createRange(8L, 12L)));
            fail("ASN1CompilerException expected");
        } catch (InvocationTargetException e) {
            assertTrue(e.getCause() instanceof CompilerException);
        }

        try {
            invokeCalculateExclude(compiler, asList(createRange(5L, 10L)), asList(createRange(11L, 12L)));
            fail("ASN1CompilerException expected");
        } catch (InvocationTargetException e) {
            assertTrue(e.getCause() instanceof CompilerException);
        }

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

    @Test
    public void testCalculateInversion() throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        IntegerConstraintCompiler compiler = new IntegerConstraintCompiler(null);

        assertEquals(asList(createRange(Long.MIN_VALUE, 4L), createRange(6L, Long.MAX_VALUE)),
                ((IntegerConstraintDefinition) ReflectionUtils.invokePrivateMethod(compiler, "calculateInversion",
                        new Object[] { new IntegerConstraintDefinition(asList(createRange(5L, 5L))) })).getValues());

        assertEquals(asList(createRange(Long.MIN_VALUE, 4L), createRange(6L, 9L), createRange(21L, Long.MAX_VALUE)),
                ((IntegerConstraintDefinition) ReflectionUtils.invokePrivateMethod(compiler, "calculateInversion",
                        new Object[] { new IntegerConstraintDefinition(asList(createRange(5L, 5L),
                                createRange(10L, 20L))) })).getValues());
    }

    private List<RangeNode> invokeCalculateIntersection(IntegerConstraintCompiler compiler, List<RangeNode> a,
            List<RangeNode> b) throws InvocationTargetException, IllegalAccessException {
        return ((IntegerConstraintDefinition) ReflectionUtils.invokePrivateMethod(compiler, "calculateIntersection",
                new Object[] { new IntegerConstraintDefinition(a), new IntegerConstraintDefinition(b) })).getValues();
    }

    @Test
    public void testCalculateIntersection() throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        IntegerConstraintCompiler compiler = new IntegerConstraintCompiler(null);

        assertEquals(emptyList(), invokeCalculateIntersection(compiler, emptyList(), emptyList()));
        assertEquals(emptyList(), invokeCalculateIntersection(compiler, asList(createRange(1L, 1L)), emptyList()));
        assertEquals(emptyList(), invokeCalculateIntersection(compiler, emptyList(), asList(createRange(1L, 1L))));
        assertEquals(emptyList(), invokeCalculateIntersection(compiler, asList(createRange(0L, 10L)),
                asList(createRange(11L, 20L))));

        assertEquals(asList(createRange(0L, 10L)), invokeCalculateIntersection(compiler, asList(createRange(0L, 10L)),
                asList(createRange(0L, 10L))));

        assertEquals(asList(createRange(3L, 7L)), invokeCalculateIntersection(compiler, asList(createRange(0L, 10L)),
                asList(createRange(3L, 7L))));

        assertEquals(asList(createRange(3L, 7L)), invokeCalculateIntersection(compiler, asList(createRange(3L, 7L)),
                asList(createRange(0L, 10L))));

        assertEquals(asList(createRange(3L, 5L)), invokeCalculateIntersection(compiler, asList(createRange(3L, 7L)),
                asList(createRange(0L, 5L))));

        assertEquals(asList(createRange(3L, 5L)), invokeCalculateIntersection(compiler, asList(createRange(0L, 5L)),
                asList(createRange(3L, 7L))));


        assertEquals(asList(createRange(5L, 10L), createRange(20L, 25L), createRange(40L, 40L), createRange(45L, 50L),
                createRange(60L, 70L)),
                invokeCalculateIntersection(compiler,
                        asList(createRange(0L, 10L), createRange(20L, 50L), createRange(60L, 70L)),
                        asList(createRange(5L, 25L), createRange(40L, 40L), createRange(45L, 70L))));
    }

    private RangeNode createRange(Long l1, Long l2) {
        return new RangeNode(new EndpointNode(new IntegerValue(l1), true),
                new EndpointNode(new IntegerValue(l2), true));
    }

}
