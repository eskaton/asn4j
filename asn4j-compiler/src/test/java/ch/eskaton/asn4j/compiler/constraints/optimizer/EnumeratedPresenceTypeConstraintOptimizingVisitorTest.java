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

package ch.eskaton.asn4j.compiler.constraints.optimizer;

import ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils;
import ch.eskaton.asn4j.compiler.constraints.ast.Visitor;
import org.junit.jupiter.api.Test;

import static ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils.complement;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils.intersection;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils.not;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils.union;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class EnumeratedPresenceTypeConstraintOptimizingVisitorTest {

    @Test
    public void testVisitBinOpNodeUnion() {
        Visitor visitor = new EnumeratedTypeConstraintOptimizingVisitor();

        assertThat(visitor.visit(union(ConstraintTestUtils.enumValue(), ConstraintTestUtils.enumValue())), equalTo(ConstraintTestUtils.enumValue()));
        assertThat(visitor.visit(union(ConstraintTestUtils.enumValue(1, 2), ConstraintTestUtils.enumValue())), equalTo(ConstraintTestUtils.enumValue(1, 2)));
        assertThat(visitor.visit(union(ConstraintTestUtils.enumValue(), ConstraintTestUtils.enumValue(1, 2))), equalTo(ConstraintTestUtils.enumValue(1, 2)));
        assertThat(visitor.visit(union(ConstraintTestUtils.enumValue(1, 2), ConstraintTestUtils.enumValue(2, 3))), equalTo(ConstraintTestUtils.enumValue(1, 2, 3)));
        assertThat(visitor.visit(union(ConstraintTestUtils.enumValue(1, 2), ConstraintTestUtils.enumValue(3, 4))), equalTo(ConstraintTestUtils.enumValue(1, 2, 3, 4)));
    }

    @Test
    public void testVisitBinOpNodeIntersection() {
        Visitor visitor = new EnumeratedTypeConstraintOptimizingVisitor();

        assertThat(visitor.visit(intersection(ConstraintTestUtils.enumValue(), ConstraintTestUtils.enumValue())), equalTo(ConstraintTestUtils.enumValue()));
        assertThat(visitor.visit(intersection(ConstraintTestUtils.enumValue(1, 2), ConstraintTestUtils.enumValue())), equalTo(ConstraintTestUtils.enumValue()));
        assertThat(visitor.visit(intersection(ConstraintTestUtils.enumValue(), ConstraintTestUtils.enumValue(1, 2))), equalTo(ConstraintTestUtils.enumValue()));
        assertThat(visitor.visit(intersection(ConstraintTestUtils.enumValue(1, 2), ConstraintTestUtils.enumValue(2, 3))), equalTo(ConstraintTestUtils.enumValue(2)));
        assertThat(visitor.visit(intersection(ConstraintTestUtils.enumValue(1, 2), ConstraintTestUtils.enumValue(3, 4))), equalTo(ConstraintTestUtils.enumValue()));
    }

    @Test
    public void testVisitBinOpNodeComplement() {
        Visitor visitor = new EnumeratedTypeConstraintOptimizingVisitor();

        assertThat(visitor.visit(complement(ConstraintTestUtils.enumValue(), ConstraintTestUtils.enumValue())), equalTo(ConstraintTestUtils.enumValue()));
        assertThat(visitor.visit(complement(ConstraintTestUtils.enumValue(1, 2), ConstraintTestUtils.enumValue())), equalTo(ConstraintTestUtils.enumValue(1, 2)));
        assertThat(visitor.visit(complement(ConstraintTestUtils.enumValue(), ConstraintTestUtils.enumValue(1, 2))), equalTo(ConstraintTestUtils.enumValue()));
        assertThat(visitor.visit(complement(ConstraintTestUtils.enumValue(1, 2, 3), ConstraintTestUtils.enumValue(3, 4))), equalTo(ConstraintTestUtils.enumValue(1, 2)));
    }

    @Test
    public void testVisitBinOpNodeNot() {
        Visitor visitor = new EnumeratedTypeConstraintOptimizingVisitor();

        assertThat(visitor.visit(intersection(not(ConstraintTestUtils.enumValue(3, 4, 5)), ConstraintTestUtils.enumValue(1, 2, 3))), equalTo(ConstraintTestUtils.enumValue(1, 2)));
        assertThat(visitor.visit(intersection(ConstraintTestUtils.enumValue(1, 2, 3), not(ConstraintTestUtils.enumValue(3, 4, 5)))), equalTo(ConstraintTestUtils.enumValue(1, 2)));
        assertThat(visitor.visit(complement(ConstraintTestUtils.enumValue(1, 2, 3), not(ConstraintTestUtils.enumValue(3, 4, 5)))), equalTo(ConstraintTestUtils.enumValue(3)));
        assertThat(visitor.visit(complement(not(ConstraintTestUtils.enumValue(1, 2, 3)), ConstraintTestUtils.enumValue(3, 4, 5))),
                equalTo(not(ConstraintTestUtils.enumValue(1, 2, 3, 4, 5))));
    }

}
