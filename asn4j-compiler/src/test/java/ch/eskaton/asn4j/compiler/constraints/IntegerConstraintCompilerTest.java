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

import ch.eskaton.asn4j.parser.ast.EndpointNode;
import ch.eskaton.asn4j.parser.ast.RangeNode;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

public class IntegerConstraintCompilerTest {

    @Test
    public void testCalculateInversion() {
        IntegerConstraintCompiler compiler = new IntegerConstraintCompiler(null);

        assertEquals(asList(createRange(Long.MIN_VALUE, 4L), createRange(6L, Long.MAX_VALUE)),
                invokeCalculateInversion(compiler, createRange(5L, 5L)));

        assertEquals(asList(createRange(Long.MIN_VALUE, 4L), createRange(6L, 9L), createRange(21L, Long.MAX_VALUE)),
                invokeCalculateInversion(compiler, createRange(5L, 5L), createRange(10L, 20L)));
    }

    @Test
    public void testCalculateIntersection() {
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

    private List<RangeNode> invokeCalculateInversion(IntegerConstraintCompiler compiler, RangeNode... rangeNodes) {
        return compiler.calculateInversion(new IntegerConstraintValues(asList(rangeNodes))).getValues();
    }

    private List<RangeNode> invokeCalculateIntersection(IntegerConstraintCompiler compiler, List<RangeNode> a,
            List<RangeNode> b) {
        return compiler.calculateIntersection(new IntegerConstraintValues(a), new IntegerConstraintValues(b)).getValues();
    }

    private RangeNode createRange(Long l1, Long l2) {
        return new RangeNode(new EndpointNode(new IntegerValue(l1), true),
                new EndpointNode(new IntegerValue(l2), true));
    }

}
