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

import ch.eskaton.asn4j.compiler.constraints.ast.BinOpNode;
import ch.eskaton.asn4j.compiler.constraints.ast.IntegerRange;
import ch.eskaton.asn4j.compiler.constraints.ast.IntegerRangeValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.NodeType;
import ch.eskaton.asn4j.compiler.constraints.ast.Visitor;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class IntegerConstraintOptimizingVisitorTest {

    @Test
    public void testVisitBinOpNodeUnion() {
        Visitor visitor = new IntegerConstraintOptimizingVisitor();

        assertThat(visitor.visit(union(value(), value())), equalTo(value()));
        assertThat(visitor.visit(union(value(1, 2), value())), equalTo(value(1, 2)));
        assertThat(visitor.visit(union(value(), value(1, 2))), equalTo(value(1, 2)));
        assertThat(visitor.visit(union(value(1, 2), value(2, 3))), equalTo(value(1, 3)));
        assertThat(visitor.visit(union(value(1, 2), value(3, 4))), equalTo(value(1, 4)));
        assertThat(visitor.visit(union(value(1, 2), value(4, 5))), equalTo(value(range(1, 2), range(4, 5))));
        assertThat(visitor.visit(union(value(1, 2), value(range(3, 4), range(6, 7)))),
                equalTo(value(range(1, 4), range(6, 7))));
    }

    @Test
    public void testVisitBinOpNodeIntersection() {
        Visitor visitor = new IntegerConstraintOptimizingVisitor();

        assertThat(visitor.visit(intersection(value(), value())), equalTo(value()));
        assertThat(visitor.visit(intersection(value(1, 2), value())), equalTo(value()));
        assertThat(visitor.visit(intersection(value(), value(1, 2))), equalTo(value()));
        assertThat(visitor.visit(intersection(value(1, 2), value(2, 3))), equalTo(value(2, 2)));
        assertThat(visitor.visit(intersection(value(1, 2), value(3, 4))), equalTo(value()));
        assertThat(visitor.visit(intersection(value(1, 6), value(range(3, 4), range(6, 7)))),
                equalTo(value(range(3, 4), range(6, 6))));
    }

    @Test
    public void testVisitBinOpNodeComplement() {
        Visitor visitor = new IntegerConstraintOptimizingVisitor();

        assertThat(visitor.visit(complement(value(), value())), equalTo(value()));
        assertThat(visitor.visit(complement(value(1, 2), value())), equalTo(value(1, 2)));
        assertThat(visitor.visit(complement(value(), value(1, 2))), equalTo(value()));
        assertThat(visitor.visit(complement(value(1, 4), value(2, 3))), equalTo(value(range(1, 1), range(4, 4))));
        assertThat(visitor.visit(complement(value(1, 7), value(range(3, 4), range(6, 7)))),
                equalTo(value(range(1, 2), range(5, 5))));
    }

    private static IntegerRangeValueNode value() {
        return new IntegerRangeValueNode(emptyList());
    }

    private static IntegerRangeValueNode value(long lower, long upper) {
        return new IntegerRangeValueNode(singletonList(range(lower, upper)));
    }

    private static IntegerRangeValueNode value(IntegerRange... ranges) {
        return new IntegerRangeValueNode(asList(ranges));
    }

    private static IntegerRange range(long lower, long upper) {
        return new IntegerRange(lower, upper);
    }

    private static BinOpNode union(IntegerRangeValueNode left, IntegerRangeValueNode right) {
        return new BinOpNode(NodeType.UNION, left, right);
    }

    private static BinOpNode intersection(IntegerRangeValueNode left, IntegerRangeValueNode right) {
        return new BinOpNode(NodeType.INTERSECTION, left, right);
    }

    private static BinOpNode complement(IntegerRangeValueNode left, IntegerRangeValueNode right) {
        return new BinOpNode(NodeType.COMPLEMENT, left, right);
    }

}
