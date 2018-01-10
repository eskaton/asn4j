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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.parser.ast.EndpointNode;
import ch.eskaton.asn4j.parser.ast.RangeNode;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.commons.utils.ReflectionUtils;
import org.junit.Test;

public class IntegerConstraintCompilerTest {

    @Test
    public void testCompareCanonicalEndpoint() throws IllegalArgumentException,
    		IllegalAccessException, InvocationTargetException {
    	assertTrue(-1 == (Integer) ReflectionUtils.invokeStaticPrivateMethod(
    			IntegerConstraintCompiler.class, "compareCanonicalEndpoint",
    			new EndpointNode[] {
    					new EndpointNode(new IntegerValue(-2), true),
    					new EndpointNode(new IntegerValue(-1), true) }));

    	assertTrue(-2 == (Integer) ReflectionUtils.invokeStaticPrivateMethod(
    			IntegerConstraintCompiler.class, "compareCanonicalEndpoint",
    			new EndpointNode[] {
    					new EndpointNode(new IntegerValue(-5), true),
    					new EndpointNode(new IntegerValue(-1), true) }));

    	assertEquals(0, ReflectionUtils.invokeStaticPrivateMethod(
    			IntegerConstraintCompiler.class, "compareCanonicalEndpoint",
    			new EndpointNode[] {
    					new EndpointNode(new IntegerValue(5), true),
    					new EndpointNode(new IntegerValue(5), true) }));

    	assertTrue(1 == (Integer) ReflectionUtils.invokeStaticPrivateMethod(
    			IntegerConstraintCompiler.class, "compareCanonicalEndpoint",
    			new EndpointNode[] {
    					new EndpointNode(new IntegerValue(5), true),
    					new EndpointNode(new IntegerValue(4), true) }));

    	assertTrue(2 == (Integer) ReflectionUtils.invokeStaticPrivateMethod(
    			IntegerConstraintCompiler.class, "compareCanonicalEndpoint",
    			new EndpointNode[] {
    					new EndpointNode(new IntegerValue(5), true),
    					new EndpointNode(new IntegerValue(-5), true) }));

    	assertTrue(2 == (Integer) ReflectionUtils
    			.invokeStaticPrivateMethod(IntegerConstraintCompiler.class,
    					"compareCanonicalEndpoint", new EndpointNode[] {
    							new EndpointNode(new IntegerValue(
    									Long.MAX_VALUE), true),
    							new EndpointNode(new IntegerValue(
    									Long.MIN_VALUE), true) }));

    	assertTrue(-2 == (Integer) ReflectionUtils
    			.invokeStaticPrivateMethod(IntegerConstraintCompiler.class,
    					"compareCanonicalEndpoint", new EndpointNode[] {
    							new EndpointNode(new IntegerValue(
    									Long.MIN_VALUE), true),
    							new EndpointNode(new IntegerValue(
    									Long.MAX_VALUE), true) }));

    	assertTrue(-1 == (Integer) ReflectionUtils
    			.invokeStaticPrivateMethod(IntegerConstraintCompiler.class,
    					"compareCanonicalEndpoint", new EndpointNode[] {
    							new EndpointNode(new IntegerValue(
    									Long.MIN_VALUE), true),
    							new EndpointNode(new IntegerValue(
    									Long.MIN_VALUE + 1), true) }));
    }

    @Test
    public void testCompareCanonicalRange() throws IllegalArgumentException,
    		IllegalAccessException, InvocationTargetException {
    	assertTrue(-1 >= (Integer) ReflectionUtils
    			.invokeStaticPrivateMethod(
    					IntegerConstraintCompiler.class,
    					"compareCanonicalRange",
    					new RangeNode[] { createRange(-2L, -1L),
    							createRange(-1L, 0L) }));

    	assertTrue(-1 >= (Integer) ReflectionUtils
    			.invokeStaticPrivateMethod(
    					IntegerConstraintCompiler.class,
    					"compareCanonicalRange",
    					new RangeNode[] { createRange(-2L, -1L),
    							createRange(-2L, 0L) }));

    	assertEquals(0,
    			ReflectionUtils.invokeStaticPrivateMethod(
    					IntegerConstraintCompiler.class,
    					"compareCanonicalRange",
    					new RangeNode[] { createRange(-2L, -1L),
    							createRange(-2L, -1L) }));

    	assertTrue(1 <= (Integer) ReflectionUtils.invokeStaticPrivateMethod(
    			IntegerConstraintCompiler.class, "compareCanonicalRange",
    			new RangeNode[] { createRange(0L, 5L), createRange(-5L, 5L) }));

    	assertTrue(1 <= (Integer) ReflectionUtils.invokeStaticPrivateMethod(
    			IntegerConstraintCompiler.class, "compareCanonicalRange",
    			new RangeNode[] { createRange(0L, 5L), createRange(0L, 4L) }));
    }

    @Test
    public void testCanonicalizeRanges() throws IllegalArgumentException,
    		IllegalAccessException, InvocationTargetException {
    	IntegerConstraintCompiler compiler = new IntegerConstraintCompiler(
    			null, null);

    	assertEquals(new ArrayList<RangeNode>(),
    			ReflectionUtils.invokePrivateMethod(compiler, "canonicalizeRanges",
    					new Object[] { new ArrayList<RangeNode>() }));

    	// 2 ranges
    	assertEquals(Arrays.asList(createRange(0L, 5L), createRange(7L, 10L)),
    			ReflectionUtils.invokePrivateMethod(compiler, "canonicalizeRanges",
    					new Object[] { Arrays.asList(createRange(0L, 5L),
    							createRange(7L, 10L)) }));

    	assertEquals(Arrays.asList(createRange(0L, 10L)),
    			ReflectionUtils.invokePrivateMethod(compiler, "canonicalizeRanges",
    					new Object[] { Arrays.asList(createRange(0L, 5L),
    							createRange(6L, 10L)) }));

    	assertEquals(Arrays.asList(createRange(0L, 10L)),
    			ReflectionUtils.invokePrivateMethod(compiler, "canonicalizeRanges",
    					new Object[] { Arrays.asList(createRange(0L, 5L),
    							createRange(5L, 10L)) }));

    	assertEquals(Arrays.asList(createRange(0L, 10L)),
    			ReflectionUtils.invokePrivateMethod(compiler, "canonicalizeRanges",
    					new Object[] { Arrays.asList(createRange(0L, 5L),
    							createRange(3L, 10L)) }));

    	assertEquals(Arrays.asList(createRange(0L, 5L)),
    			ReflectionUtils.invokePrivateMethod(compiler, "canonicalizeRanges",
    					new Object[] { Arrays.asList(createRange(0L, 5L),
    							createRange(3L, 5L)) }));

    	assertEquals(Arrays.asList(createRange(0L, 5L)),
    			ReflectionUtils.invokePrivateMethod(compiler, "canonicalizeRanges",
    					new Object[] { Arrays.asList(createRange(0L, 5L),
    							createRange(3L, 5L)) }));

    	assertEquals(Arrays.asList(createRange(0L, 5L)),
    			ReflectionUtils.invokePrivateMethod(compiler, "canonicalizeRanges",
    					new Object[] { Arrays.asList(createRange(0L, 5L),
    							createRange(3L, 4L)) }));

    	// 3 ranges
    	assertEquals(Arrays.asList(createRange(0L, 5L), createRange(7L, 10L),
    			createRange(15L, 20L)), ReflectionUtils.invokePrivateMethod(
    			compiler,
    			"canonicalizeRanges",
    			new Object[] { Arrays.asList(createRange(0L, 5L),
    					createRange(7L, 10L), createRange(15L, 20L)) }));

    	assertEquals(Arrays.asList(createRange(0L, 5L), createRange(7L, 20L)),
    			ReflectionUtils.invokePrivateMethod(compiler, "canonicalizeRanges",
    					new Object[] { Arrays.asList(createRange(0L, 5L),
    							createRange(7L, 16L), createRange(15L, 20L)) }));

    	assertEquals(Arrays.asList(createRange(0L, 20L)),
    			ReflectionUtils.invokePrivateMethod(compiler, "canonicalizeRanges",
    					new Object[] { Arrays.asList(createRange(0L, 6L),
    							createRange(7L, 16L), createRange(15L, 20L)) }));

    	assertEquals(
    			Arrays.asList(createRange(0L, 13L), createRange(15L, 20L)),
    			ReflectionUtils.invokePrivateMethod(compiler, "canonicalizeRanges",
    					new Object[] { Arrays.asList(createRange(0L, 6L),
    							createRange(7L, 13L), createRange(15L, 20L)) }));
    }

    @Test
    public void testCalculateExclude() throws IllegalArgumentException,
    		IllegalAccessException, InvocationTargetException {
    	IntegerConstraintCompiler compiler = new IntegerConstraintCompiler(
    			null, null);

    	try {
    		ReflectionUtils.invokePrivateMethod(
    				compiler,
    				"calculateExclude",
    				new Object[] { Arrays.asList(createRange(5L, 10L)),
    						Arrays.asList(createRange(3L, 4L)) });
    		fail("ASN1CompilerException expected");
    	} catch (InvocationTargetException e) {
    		assertTrue(e.getCause() instanceof CompilerException);
    	}

    	try {
    		ReflectionUtils.invokePrivateMethod(
    				compiler,
    				"calculateExclude",
    				new Object[] { Arrays.asList(createRange(5L, 10L)),
    						Arrays.asList(createRange(3L, 6L)) });
    		fail("ASN1CompilerException expected");
    	} catch (InvocationTargetException e) {
    		assertTrue(e.getCause() instanceof CompilerException);
    	}

    	try {
    		ReflectionUtils.invokePrivateMethod(
    				compiler,
    				"calculateExclude",
    				new Object[] { Arrays.asList(createRange(5L, 10L)),
    						Arrays.asList(createRange(8L, 12L)) });
    		fail("ASN1CompilerException expected");
    	} catch (InvocationTargetException e) {
    		assertTrue(e.getCause() instanceof CompilerException);
    	}

    	try {
    		ReflectionUtils.invokePrivateMethod(
    				compiler,
    				"calculateExclude",
    				new Object[] { Arrays.asList(createRange(5L, 10L)),
    						Arrays.asList(createRange(11L, 12L)) });
    		fail("ASN1CompilerException expected");
    	} catch (InvocationTargetException e) {
    		assertTrue(e.getCause() instanceof CompilerException);
    	}

    	assertEquals(Arrays.asList(), ReflectionUtils.invokePrivateMethod(
    			compiler,
    			"calculateExclude",
    			new Object[] { Arrays.asList(createRange(0L, 10L)),
    					Arrays.asList(createRange(0L, 10L)) }));

    	assertEquals(Arrays.asList(createRange(6L, 10L)),
    			ReflectionUtils.invokePrivateMethod(compiler, "calculateExclude",
    					new Object[] { Arrays.asList(createRange(0L, 10L)),
    							Arrays.asList(createRange(0L, 5L)) }));

    	assertEquals(Arrays.asList(createRange(0L, 2L), createRange(9L, 10L)),
    			ReflectionUtils.invokePrivateMethod(compiler, "calculateExclude",
    					new Object[] { Arrays.asList(createRange(0L, 10L)),
    							Arrays.asList(createRange(3L, 8L)) }));

    	assertEquals(Arrays.asList(createRange(0L, 5L)),
    			ReflectionUtils.invokePrivateMethod(compiler, "calculateExclude",
    					new Object[] { Arrays.asList(createRange(0L, 10L)),
    							Arrays.asList(createRange(6L, 10L)) }));

    	assertEquals(Arrays.asList(createRange(0L, 5L), createRange(7L, 10L),
    			createRange(20L, 30L)), ReflectionUtils.invokePrivateMethod(
    			compiler,
    			"calculateExclude",
    			new Object[] {
    					Arrays.asList(createRange(0L, 10L),
    							createRange(20L, 30L)),
    					Arrays.asList(createRange(6L, 6L)) }));

    	assertEquals(Arrays.asList(createRange(0L, 10L), createRange(20L, 21L),
    			createRange(23L, 30L)), ReflectionUtils.invokePrivateMethod(
    			compiler,
    			"calculateExclude",
    			new Object[] {
    					Arrays.asList(createRange(0L, 10L),
    							createRange(20L, 30L)),
    					Arrays.asList(createRange(22L, 22L)) }));

    	assertEquals(Arrays.asList(createRange(0L, 10L), createRange(40L, 50L),
    			createRange(80L, 90L)), ReflectionUtils.invokePrivateMethod(
    			compiler,
    			"calculateExclude",
    			new Object[] {
    					Arrays.asList(createRange(0L, 10L),
    							createRange(20L, 30L), createRange(40L, 50L),
    							createRange(60L, 70L), createRange(80L, 90L)),
    					Arrays.asList(createRange(20L, 30L),
    							createRange(60L, 70L)) }));
    }

    @Test
    public void testCalculateInversion() throws IllegalArgumentException,
    		IllegalAccessException, InvocationTargetException {
    	IntegerConstraintCompiler compiler = new IntegerConstraintCompiler(
    			null, null);

    	assertEquals(Arrays.asList(createRange(Long.MIN_VALUE, 4L),
    			createRange(6L, Long.MAX_VALUE)),
    			ReflectionUtils.invokePrivateMethod(compiler, "calculateInversion",
    					new Object[] { Arrays.asList(createRange(5L, 5L)) }));

    	assertEquals(Arrays.asList(createRange(Long.MIN_VALUE, 4L),
    			createRange(6L, 9L), createRange(21L, Long.MAX_VALUE)),
    			ReflectionUtils.invokePrivateMethod(compiler, "calculateInversion",
    					new Object[] { Arrays.asList(createRange(5L, 5L),
    							createRange(10L, 20L)) }));
    }

    @Test
    public void testCalculateIntersection() throws IllegalArgumentException,
    		IllegalAccessException, InvocationTargetException {
    	IntegerConstraintCompiler compiler = new IntegerConstraintCompiler(
    			null, null);

    	assertEquals(Arrays.asList(), ReflectionUtils.invokePrivateMethod(compiler,
    			"calculateIntersection",
    			new Object[] { Arrays.asList(), Arrays.asList() }));

    	assertEquals(Arrays.asList(), ReflectionUtils.invokePrivateMethod(
    			compiler,
    			"calculateIntersection",
    			new Object[] { Arrays.asList(createRange(1L, 1L)),
    					Arrays.asList() }));

    	assertEquals(Arrays.asList(), ReflectionUtils.invokePrivateMethod(
    			compiler,
    			"calculateIntersection",
    			new Object[] { Arrays.asList(),
    					Arrays.asList(createRange(1L, 1L)) }));

    	assertEquals(Arrays.asList(), ReflectionUtils.invokePrivateMethod(
    			compiler,
    			"calculateIntersection",
    			new Object[] { Arrays.asList(createRange(0L, 10L)),
    					Arrays.asList(createRange(11L, 20L)) }));

    	assertEquals(Arrays.asList(createRange(0L, 10L)),
    			ReflectionUtils.invokePrivateMethod(compiler,
    					"calculateIntersection",
    					new Object[] { Arrays.asList(createRange(0L, 10L)),
    							Arrays.asList(createRange(0L, 10L)) }));

    	assertEquals(Arrays.asList(createRange(3L, 7L)),
    			ReflectionUtils.invokePrivateMethod(compiler,
    					"calculateIntersection",
    					new Object[] { Arrays.asList(createRange(0L, 10L)),
    							Arrays.asList(createRange(3L, 7L)) }));

    	assertEquals(Arrays.asList(createRange(3L, 7L)),
    			ReflectionUtils.invokePrivateMethod(compiler,
    					"calculateIntersection",
    					new Object[] { Arrays.asList(createRange(3L, 7L)),
    							Arrays.asList(createRange(0L, 10L)) }));

    	assertEquals(Arrays.asList(createRange(3L, 5L)),
    			ReflectionUtils.invokePrivateMethod(compiler,
    					"calculateIntersection",
    					new Object[] { Arrays.asList(createRange(3L, 7L)),
    							Arrays.asList(createRange(0L, 5L)) }));

    	assertEquals(Arrays.asList(createRange(3L, 5L)),
    			ReflectionUtils.invokePrivateMethod(compiler,
    					"calculateIntersection",
    					new Object[] { Arrays.asList(createRange(0L, 5L)),
    							Arrays.asList(createRange(3L, 7L)) }));

    	assertEquals(Arrays.asList(createRange(5L, 10L), createRange(20L, 25L),
    			createRange(40L, 40L), createRange(45L, 50L),
    			createRange(60L, 70L)),
    			ReflectionUtils.invokePrivateMethod(
    					compiler,
    					"calculateIntersection",
    					new Object[] {
    							Arrays.asList(createRange(0L, 10L),
    									createRange(20L, 50L),
    									createRange(60L, 70L)),
    							Arrays.asList(createRange(5L, 25L),
    									createRange(40L, 40L),
    									createRange(45L, 70L)) }));
    }

    private RangeNode createRange(Long l1, Long l2) {
    	return new RangeNode(new EndpointNode(new IntegerValue(l1), true),
    			new EndpointNode(new IntegerValue(l2), true));
    }

}
