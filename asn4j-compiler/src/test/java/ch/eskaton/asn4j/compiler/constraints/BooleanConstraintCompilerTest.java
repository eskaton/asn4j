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

import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.values.BooleanValue;
import ch.eskaton.commons.utils.ReflectionUtils;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static ch.eskaton.asn4j.parser.NoPosition.NO_POSITION;
import static ch.eskaton.commons.utils.CollectionUtils.asHashSet;
import static java.util.Collections.emptySet;
import static org.junit.Assert.assertEquals;

public class BooleanConstraintCompilerTest {

    @Test
    public void testCalculateUnion() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        BooleanConstraintCompiler compiler = new BooleanConstraintCompiler(null);

        assertEquals(emptySet(), invokeCalculateUnion(compiler));
        assertEquals(asHashSet(true), invokeCalculateUnion(compiler, true));
        assertEquals(asHashSet(false), invokeCalculateUnion(compiler, false));
        assertEquals(asHashSet(true, false), invokeCalculateUnion(compiler, false, true));
        assertEquals(asHashSet(true, false), invokeCalculateUnion(compiler, false, true, false));
    }

    @Test
    public void testCalculateIntersection() throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        BooleanConstraintCompiler compiler = new BooleanConstraintCompiler(null);

        assertEquals(emptySet(), invokeCalculateIntersection(compiler));
        assertEquals(asHashSet(true), invokeCalculateIntersection(compiler, true));
        assertEquals(asHashSet(false), invokeCalculateIntersection(compiler, false));
        assertEquals(asHashSet(true), invokeCalculateIntersection(compiler, true, true));
        assertEquals(asHashSet(false), invokeCalculateIntersection(compiler, false, false));
        assertEquals(emptySet(), invokeCalculateIntersection(compiler, true, false));
        assertEquals(emptySet(), invokeCalculateIntersection(compiler, false, true));
    }

    @Test
    public void testCalculateExclude() throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        BooleanConstraintCompiler compiler = new BooleanConstraintCompiler(null);

        assertEquals(asHashSet(true, false), invokeCalculateExclude(compiler, asHashSet(true, false), asHashSet()));
        assertEquals(asHashSet(false), invokeCalculateExclude(compiler, asHashSet(true, false), asHashSet(true)));
        assertEquals(asHashSet(true), invokeCalculateExclude(compiler, asHashSet(true, false), asHashSet(false)));
        assertEquals(emptySet(), invokeCalculateExclude(compiler, asHashSet(true, false), asHashSet(false, true)));
    }

    private Set<Boolean> invokeCalculateExclude(BooleanConstraintCompiler compiler, Set<Boolean> a, Set<Boolean> b)
            throws InvocationTargetException, IllegalAccessException {
        return ((BooleanConstraintValues) ReflectionUtils.invokePrivateMethod(compiler, "calculateExclude",
                new Object[]{new BooleanConstraintValues(a), new BooleanConstraintValues(b)})).getValues();
    }

    private Set<Boolean> invokeCalculate(BooleanConstraintCompiler compiler, String method, boolean... values)
            throws InvocationTargetException, IllegalAccessException {
        List<SingleValueConstraint> elements = new ArrayList<>();

        for (boolean value : values) {
            elements.add(new SingleValueConstraint(NO_POSITION, new BooleanValue(NO_POSITION, value)));
        }

        return ((BooleanConstraintValues) ReflectionUtils.invokePrivateMethod(compiler, method,
                new Object[]{elements})).getValues();
    }

    private Set<Boolean> invokeCalculateUnion(BooleanConstraintCompiler compiler, boolean... values)
            throws InvocationTargetException, IllegalAccessException {
        return invokeCalculate(compiler, "calculateUnion", values);
    }

    private Set<Boolean> invokeCalculateIntersection(BooleanConstraintCompiler compiler, boolean... values)
            throws InvocationTargetException, IllegalAccessException {
        return invokeCalculate(compiler, "calculateIntersection", values);
    }

}
