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

import ch.eskaton.asn4j.compiler.constraints.ast.Visitor;
import org.junit.Test;

import static ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils.complement;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils.intersection;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils.not;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils.range;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils.union;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils.intValue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class IntegerConstraintOptimizingVisitorTest {

    @Test
    public void testVisitBinOpNodeUnion() {
        Visitor visitor = new IntegerConstraintOptimizingVisitor();

        assertThat(visitor.visit(union(intValue(), intValue())), equalTo(intValue()));
        assertThat(visitor.visit(union(intValue(1, 2), intValue())), equalTo(intValue(1, 2)));
        assertThat(visitor.visit(union(intValue(), intValue(1, 2))), equalTo(intValue(1, 2)));
        assertThat(visitor.visit(union(intValue(1, 2), intValue(2, 3))), equalTo(intValue(1, 3)));
        assertThat(visitor.visit(union(intValue(1, 2), intValue(3, 4))), equalTo(intValue(1, 4)));
        assertThat(visitor.visit(union(intValue(1, 2), intValue(4, 5))), equalTo(intValue(range(1, 2), range(4, 5))));
        assertThat(visitor.visit(union(intValue(1, 2), intValue(range(3, 4), range(6, 7)))),
                equalTo(intValue(range(1, 4), range(6, 7))));
    }

    @Test
    public void testVisitBinOpNodeIntersection() {
        Visitor visitor = new IntegerConstraintOptimizingVisitor();

        assertThat(visitor.visit(intersection(intValue(), intValue())), equalTo(intValue()));
        assertThat(visitor.visit(intersection(intValue(1, 2), intValue())), equalTo(intValue()));
        assertThat(visitor.visit(intersection(intValue(), intValue(1, 2))), equalTo(intValue()));
        assertThat(visitor.visit(intersection(intValue(1, 2), intValue(2, 3))), equalTo(intValue(2, 2)));
        assertThat(visitor.visit(intersection(intValue(1, 2), intValue(3, 4))), equalTo(intValue()));
        assertThat(visitor.visit(intersection(intValue(1, 6), intValue(range(3, 4), range(6, 7)))),
                equalTo(intValue(range(3, 4), range(6, 6))));
    }

    @Test
    public void testVisitBinOpNodeComplement() {
        Visitor visitor = new IntegerConstraintOptimizingVisitor();

        assertThat(visitor.visit(complement(intValue(), intValue())), equalTo(intValue()));
        assertThat(visitor.visit(complement(intValue(1, 2), intValue())), equalTo(intValue(1, 2)));
        assertThat(visitor.visit(complement(intValue(), intValue(1, 2))), equalTo(intValue()));
        assertThat(visitor.visit(complement(intValue(1, 4), intValue(2, 3))), equalTo(intValue(range(1, 1), range(4, 4))));
        assertThat(visitor.visit(complement(intValue(1, 7), intValue(range(3, 4), range(6, 7)))),
                equalTo(intValue(range(1, 2), range(5, 5))));
    }

    @Test
    public void testVisitBinOpNodeNot() {
        Visitor visitor = new IntegerConstraintOptimizingVisitor();

        assertThat(visitor.visit(intersection(intValue(1, 10), not(intValue(3, 7)))),
                equalTo(intValue(range(1, 2), range(8, 10))));
        assertThat(visitor.visit(intersection(not(intValue(3, 7)), intValue(1, 6))), equalTo(intValue(1, 2)));
    }

}
