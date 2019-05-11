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
import ch.eskaton.asn4j.parser.ast.RangeNode;
import org.junit.Test;

import java.util.List;

import static ch.eskaton.asn4j.compiler.constraints.IntegerTestUtils.createRange;
import static ch.eskaton.asn4j.test.TestUtils.assertThrows;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class IntegerConstraintValuesTest {

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



    private List<RangeNode> invokeCalculateExclude(IntegerConstraintCompiler compiler, List<RangeNode> a,
            List<RangeNode> b) {
        return compiler.calculateExclude(new IntegerConstraintValues(a), new IntegerConstraintValues(b)).getValues();
    }

}
