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
import ch.eskaton.asn4j.compiler.constraints.ast.BinOpNode;
import ch.eskaton.asn4j.compiler.constraints.ast.BitStringValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Visitor;
import ch.eskaton.asn4j.parser.ast.values.BitStringValue;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils.complement;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils.intersection;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils.not;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils.range;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils.union;
import static ch.eskaton.asn4j.parser.NoPosition.NO_POSITION;
import static ch.eskaton.commons.utils.CollectionUtils.asLinkedList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class BitStringConstraintOptimizingVisitorTest {

    @Test
    void testVisitBinOpNodeValueUnion() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        assertThat(visitor.visit(union(value(), value())), equalTo(value()));
        assertThat(visitor.visit(union(value(bitString(0x01)), value())), equalTo(value(bitString(0x01))));
        assertThat(visitor.visit(union(value(), value(bitString(0x02)))), equalTo(value(bitString(0x02))));
        assertThat(visitor.visit(union(value(bitString(0x01)), value(bitString(0x02)))),
                equalTo(value(bitStrings(0x01, 0x02))));
        assertThat(visitor.visit(union(value(bitStrings(0x01, 0x02)), value(bitStrings(0x02, 0x03)))),
                equalTo(value(bitStrings(0x01, 0x02, 0x03))));
    }

    @Test
    void testVisitBinOpNodeValueIntersection() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        assertThat(visitor.visit(intersection(value(), value())), equalTo(value()));
        assertThat(visitor.visit(intersection(value(bitString(0x01)), value())), equalTo(value()));
        assertThat(visitor.visit(intersection(value(), value(bitString(0x02)))), equalTo(value()));
        assertThat(visitor.visit(intersection(value(bitString(0x01)), value(bitString(0x02)))), equalTo(value()));
        assertThat(visitor.visit(intersection(value(bitStrings(0x01, 0x02)), value(bitStrings(0x02, 0x03)))),
                equalTo(value(bitString(0x02))));
    }

    @Test
    void testVisitBinOpNodeValueComplement() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        assertThat(visitor.visit(complement(value(), value())), equalTo(value()));
        assertThat(visitor.visit(complement(value(bitString(0x01)), value())), equalTo(value(bitString(0x01))));
        assertThat(visitor.visit(complement(value(), value(bitString(0x02)))), equalTo(value()));
        assertThat(visitor.visit(complement(value(bitStrings(0x01, 0x02)), value(bitString(0x02)))),
                equalTo(value(bitString(0x01))));
    }

    @Test
    void testVisitBinOpNodeValueNot() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        assertThat(visitor.visit(intersection(value(bitStrings(0x01, 0x02)), not(value(bitString(0x01))))),
                equalTo(value(bitString(0x02))));
        assertThat(visitor.visit(intersection(not(value(bitString(0x01))), value(bitStrings(0x01, 0x02)))),
                equalTo(value(bitString(0x02))));
    }

    @Test
    void testVisitBinOpNodeSizeUnion() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        assertThat(visitor.visit(union(ConstraintTestUtils.size(), ConstraintTestUtils.size())), equalTo(ConstraintTestUtils.size()));
        assertThat(visitor.visit(union(ConstraintTestUtils.size(1, 2), ConstraintTestUtils.size())), equalTo(ConstraintTestUtils.size(1, 2)));
        assertThat(visitor.visit(union(ConstraintTestUtils.size(), ConstraintTestUtils.size(1, 2))), equalTo(ConstraintTestUtils.size(1, 2)));
        assertThat(visitor.visit(union(ConstraintTestUtils.size(1, 2), ConstraintTestUtils.size(2, 3))), equalTo(ConstraintTestUtils.size(1, 3)));
        assertThat(visitor.visit(union(ConstraintTestUtils.size(1, 2), ConstraintTestUtils.size(3, 4))), equalTo(ConstraintTestUtils.size(1, 4)));
        assertThat(visitor.visit(union(ConstraintTestUtils.size(1, 2), ConstraintTestUtils.size(4, 5))), equalTo(ConstraintTestUtils.size(range(1, 2),
                range(4, 5))));
        assertThat(visitor.visit(union(ConstraintTestUtils.size(1, 2), ConstraintTestUtils.size(range(3, 4), range(6, 7)))),
                equalTo(ConstraintTestUtils.size(range(1, 4), range(6, 7))));
    }

    @Test
    void testVisitBinOpNodeSizeIntersection() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        assertThat(visitor.visit(intersection(ConstraintTestUtils.size(), ConstraintTestUtils.size())), equalTo(ConstraintTestUtils.size()));
        assertThat(visitor.visit(intersection(ConstraintTestUtils.size(1, 2), ConstraintTestUtils.size())), equalTo(ConstraintTestUtils.size()));
        assertThat(visitor.visit(intersection(ConstraintTestUtils.size(), ConstraintTestUtils.size(1, 2))), equalTo(ConstraintTestUtils.size()));
        assertThat(visitor.visit(intersection(ConstraintTestUtils.size(1, 2), ConstraintTestUtils.size(2, 3))), equalTo(ConstraintTestUtils.size(2, 2)));
        assertThat(visitor.visit(intersection(ConstraintTestUtils.size(1, 2), ConstraintTestUtils.size(3, 4))), equalTo(ConstraintTestUtils.size()));
        assertThat(visitor.visit(intersection(ConstraintTestUtils.size(1, 6), ConstraintTestUtils.size(range(3, 4), range(6, 7)))),
                equalTo(ConstraintTestUtils.size(range(3, 4), range(6, 6))));
    }

    @Test
    void testVisitBinOpNodeSizeComplement() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        assertThat(visitor.visit(complement(ConstraintTestUtils.size(), ConstraintTestUtils.size())), equalTo(ConstraintTestUtils.size()));
        assertThat(visitor.visit(complement(ConstraintTestUtils.size(1, 2), ConstraintTestUtils.size())), equalTo(ConstraintTestUtils.size(1, 2)));
        assertThat(visitor.visit(complement(ConstraintTestUtils.size(), ConstraintTestUtils.size(1, 2))), equalTo(ConstraintTestUtils.size()));
        assertThat(visitor.visit(complement(ConstraintTestUtils.size(1, 4), ConstraintTestUtils.size(2, 3))), equalTo(ConstraintTestUtils.size(range(1, 1), range(4, 4))));
        assertThat(visitor.visit(complement(ConstraintTestUtils.size(1, 7), ConstraintTestUtils.size(range(3, 4), range(6, 7)))),
                equalTo(ConstraintTestUtils.size(range(1, 2), range(5, 5))));
    }

    @Test
    void testVisitBinOpNodeSizeNot() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        assertThat(visitor.visit(intersection(ConstraintTestUtils.size(1, 10), not(ConstraintTestUtils.size(3, 7)))),
                equalTo(ConstraintTestUtils.size(range(1, 2), range(8, 10))));
        assertThat(visitor.visit(intersection(not(ConstraintTestUtils.size(3, 7)), ConstraintTestUtils.size(1, 6))), equalTo(ConstraintTestUtils.size(1, 2)));
    }

    @Test
    void testVisitBinOpNodeValueSizeUnion() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        assertThat(visitor.visit(union(value(), ConstraintTestUtils.size())), equalTo(value()));
        assertThat(visitor.visit(union(value(bitStrings(0x01, 0x02)), ConstraintTestUtils.size())),
                equalTo(value(bitStrings(0x01, 0x02))));
        assertThat(visitor.visit(union(value(), ConstraintTestUtils.size(1, 2))), equalTo(ConstraintTestUtils.size(1, 2)));
        assertThat(visitor.visit(union(value(bitString(0x01, 7), bitString(0x02, 6), bitString(0x3, 6)), ConstraintTestUtils.size(2, 8))),
                equalTo(union(value(bitString(0x01, 7)), ConstraintTestUtils.size(2, 8))));
    }

    @Test
    void testVisitBinOpNodeSizeValueUnion() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        assertThat(visitor.visit(union(ConstraintTestUtils.size(), value())), equalTo(value()));
        assertThat(visitor.visit(union(ConstraintTestUtils.size(), value(bitStrings(0x01, 0x02)))),
                equalTo(value(bitStrings(0x01, 0x02))));
        assertThat(visitor.visit(union(ConstraintTestUtils.size(1, 2), value())), equalTo(ConstraintTestUtils.size(1, 2)));
        assertThat(visitor.visit(union(ConstraintTestUtils.size(2, 8), value(bitString(0x01, 7), bitString(0x02, 6), bitString(0x3, 6)))),
                equalTo(union(value(bitString(0x01, 7)), ConstraintTestUtils.size(2, 8))));
    }

    @Test
    void testVisitBinOpNodeValueSizeIntersection() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        assertThat(visitor.visit(intersection(value(), ConstraintTestUtils.size())), equalTo(value()));
        assertThat(visitor.visit(intersection(value(bitStrings(0x01, 0x02)), ConstraintTestUtils.size())), equalTo(value()));
        assertThat(visitor.visit(intersection(value(), ConstraintTestUtils.size(1, 2))), equalTo(value()));
        assertThat(visitor
                        .visit(intersection(value(bitString(0x01, 7), bitString(0x02, 6), bitString(0x3, 6)), ConstraintTestUtils.size(2, 8))),
                equalTo(value(bitString(0x02, 6), bitString(0x3, 6))));
    }

    @Test
    void testVisitBinOpNodeSizeValueIntersection() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        assertThat(visitor.visit(intersection(ConstraintTestUtils.size(), value())), equalTo(value()));
        assertThat(visitor.visit(intersection(ConstraintTestUtils.size(), value(bitStrings(0x01, 0x02)))), equalTo(value()));
        assertThat(visitor.visit(intersection(ConstraintTestUtils.size(1, 2), value())), equalTo(value()));
        assertThat(visitor
                        .visit(intersection(ConstraintTestUtils.size(2, 8), value(bitString(0x01, 7), bitString(0x02, 6), bitString(0x3, 6)))),
                equalTo(value(bitString(0x02, 6), bitString(0x3, 6))));
    }

    @Test
    void testVisitBinOpNodeValueSizeComplement() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        assertThat(visitor.visit(complement(value(), ConstraintTestUtils.size())), equalTo(value()));
        assertThat(visitor.visit(complement(value(bitStrings(0x01, 0x02)), ConstraintTestUtils.size())),
                equalTo(value(bitStrings(0x01, 0x02))));
        assertThat(visitor.visit(complement(value(), ConstraintTestUtils.size(1, 2))), equalTo(value()));
        assertThat(visitor
                        .visit(complement(value(bitString(0x01, 7), bitString(0x02, 6), bitString(0x3, 6)), ConstraintTestUtils.size(2, 8))),
                equalTo(value(bitString(0x01, 7))));
    }

    @Test
    void testVisitBinOpNodeSizeValueComplement() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        BinOpNode complement = complement(ConstraintTestUtils.size(), value());

        assertThat(visitor.visit(complement), equalTo(complement));

        complement = complement(ConstraintTestUtils.size(), value(bitStrings(0x01, 0x02)));

        assertThat(visitor.visit(complement), equalTo(complement));

        complement = complement(ConstraintTestUtils.size(1, 2), value());

        assertThat(visitor.visit(complement), equalTo(complement));

        complement = complement(ConstraintTestUtils.size(2, 8), value(bitString(0x01, 7), bitString(0x02, 6), bitString(0x3, 6)));

        assertThat(visitor.visit(complement), equalTo(complement));
    }

    private static BitStringValue bitString(int value) {
        return new BitStringValue(NO_POSITION, new byte[] { (byte) value }, 0);
    }

    private static BitStringValue bitString(int value, int unusedBits) {
        return new BitStringValue(NO_POSITION, new byte[] { (byte) value }, unusedBits);
    }

    private static BitStringValue[] bitStrings(int... values) {
        return IntStream.of(values).boxed()
                .map(value -> new BitStringValue(NO_POSITION, new byte[] { value.byteValue() }, 0))
                .collect(toList()).toArray(new BitStringValue[] {});
    }

    private static BitStringValueNode value() {
        return new BitStringValueNode(emptyList());
    }

    private static BitStringValueNode value(BitStringValue... values) {
        return new BitStringValueNode(asLinkedList(values));
    }

}
