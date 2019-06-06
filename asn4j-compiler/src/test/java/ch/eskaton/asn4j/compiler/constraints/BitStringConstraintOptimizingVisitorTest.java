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
import ch.eskaton.asn4j.compiler.constraints.ast.BitStringValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Visitor;
import ch.eskaton.asn4j.parser.ast.values.BitStringValue;
import org.junit.Test;

import java.util.stream.IntStream;

import static ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils.complement;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils.intersection;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils.not;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils.range;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils.size;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils.union;
import static ch.eskaton.asn4j.parser.NoPosition.NO_POSITION;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class BitStringConstraintOptimizingVisitorTest {

    @Test
    public void testVisitBinOpNodeValueUnion() {
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
    public void testVisitBinOpNodeValueIntersection() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        assertThat(visitor.visit(intersection(value(), value())), equalTo(value()));
        assertThat(visitor.visit(intersection(value(bitString(0x01)), value())), equalTo(value()));
        assertThat(visitor.visit(intersection(value(), value(bitString(0x02)))), equalTo(value()));
        assertThat(visitor.visit(intersection(value(bitString(0x01)), value(bitString(0x02)))), equalTo(value()));
        assertThat(visitor.visit(intersection(value(bitStrings(0x01, 0x02)), value(bitStrings(0x02, 0x03)))),
                equalTo(value(bitString(0x02))));
    }

    @Test
    public void testVisitBinOpNodeValueComplement() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        assertThat(visitor.visit(complement(value(), value())), equalTo(value()));
        assertThat(visitor.visit(complement(value(bitString(0x01)), value())), equalTo(value(bitString(0x01))));
        assertThat(visitor.visit(complement(value(), value(bitString(0x02)))), equalTo(value()));
        assertThat(visitor.visit(complement(value(bitStrings(0x01, 0x02)), value(bitString(0x02)))),
                equalTo(value(bitString(0x01))));
    }

    @Test
    public void testVisitBinOpNodeValueNot() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        assertThat(visitor.visit(intersection(value(bitStrings(0x01, 0x02)), not(value(bitString(0x01))))),
                equalTo(value(bitString(0x02))));
        assertThat(visitor.visit(intersection(not(value(bitString(0x01))), value(bitStrings(0x01, 0x02)))),
                equalTo(value(bitString(0x02))));
    }

    @Test
    public void testVisitBinOpNodeSizeUnion() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        assertThat(visitor.visit(union(size(), size())), equalTo(size()));
        assertThat(visitor.visit(union(size(1, 2), size())), equalTo(size(1, 2)));
        assertThat(visitor.visit(union(size(), size(1, 2))), equalTo(size(1, 2)));
        assertThat(visitor.visit(union(size(1, 2), size(2, 3))), equalTo(size(1, 3)));
        assertThat(visitor.visit(union(size(1, 2), size(3, 4))), equalTo(size(1, 4)));
        assertThat(visitor.visit(union(size(1, 2), size(4, 5))), equalTo(size(range(1, 2),
                range(4, 5))));
        assertThat(visitor.visit(union(size(1, 2), size(range(3, 4), range(6, 7)))),
                equalTo(size(range(1, 4), range(6, 7))));
    }

    @Test
    public void testVisitBinOpNodeSizeIntersection() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        assertThat(visitor.visit(intersection(size(), size())), equalTo(size()));
        assertThat(visitor.visit(intersection(size(1, 2), size())), equalTo(size()));
        assertThat(visitor.visit(intersection(size(), size(1, 2))), equalTo(size()));
        assertThat(visitor.visit(intersection(size(1, 2), size(2, 3))), equalTo(size(2, 2)));
        assertThat(visitor.visit(intersection(size(1, 2), size(3, 4))), equalTo(size()));
        assertThat(visitor.visit(intersection(size(1, 6), size(range(3, 4), range(6, 7)))),
                equalTo(size(range(3, 4), range(6, 6))));
    }

    @Test
    public void testVisitBinOpNodeSizeComplement() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        assertThat(visitor.visit(complement(size(), size())), equalTo(size()));
        assertThat(visitor.visit(complement(size(1, 2), size())), equalTo(size(1, 2)));
        assertThat(visitor.visit(complement(size(), size(1, 2))), equalTo(size()));
        assertThat(visitor.visit(complement(size(1, 4), size(2, 3))), equalTo(size(range(1, 1), range(4, 4))));
        assertThat(visitor.visit(complement(size(1, 7), size(range(3, 4), range(6, 7)))),
                equalTo(size(range(1, 2), range(5, 5))));
    }

    @Test
    public void testVisitBinOpNodeSizeNot() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        assertThat(visitor.visit(intersection(size(1, 10), not(size(3, 7)))),
                equalTo(size(range(1, 2), range(8, 10))));
        assertThat(visitor.visit(intersection(not(size(3, 7)), size(1, 6))), equalTo(size(1, 2)));
    }

    @Test
    public void testVisitBinOpNodeValueSizeUnion() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        assertThat(visitor.visit(union(value(), size())), equalTo(value()));
        assertThat(visitor.visit(union(value(bitStrings(0x01, 0x02)), size())),
                equalTo(value(bitStrings(0x01, 0x02))));
        assertThat(visitor.visit(union(value(), size(1, 2))), equalTo(size(1, 2)));
        assertThat(visitor.visit(union(value(bitString(0x01, 7), bitString(0x02, 6), bitString(0x3, 6)), size(2, 8))),
                equalTo(union(value(bitString(0x01, 7)), size(2, 8))));
    }

    @Test
    public void testVisitBinOpNodeSizeValueUnion() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        assertThat(visitor.visit(union(size(), value())), equalTo(value()));
        assertThat(visitor.visit(union(size(), value(bitStrings(0x01, 0x02)))),
                equalTo(value(bitStrings(0x01, 0x02))));
        assertThat(visitor.visit(union(size(1, 2), value())), equalTo(size(1, 2)));
        assertThat(visitor.visit(union(size(2, 8), value(bitString(0x01, 7), bitString(0x02, 6), bitString(0x3, 6)))),
                equalTo(union(value(bitString(0x01, 7)), size(2, 8))));
    }

    @Test
    public void testVisitBinOpNodeValueSizeIntersection() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        assertThat(visitor.visit(intersection(value(), size())), equalTo(value()));
        assertThat(visitor.visit(intersection(value(bitStrings(0x01, 0x02)), size())), equalTo(value()));
        assertThat(visitor.visit(intersection(value(), size(1, 2))), equalTo(value()));
        assertThat(visitor
                        .visit(intersection(value(bitString(0x01, 7), bitString(0x02, 6), bitString(0x3, 6)), size(2, 8))),
                equalTo(value(bitString(0x02, 6), bitString(0x3, 6))));
    }

    @Test
    public void testVisitBinOpNodeSizeValueIntersection() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        assertThat(visitor.visit(intersection(size(), value())), equalTo(value()));
        assertThat(visitor.visit(intersection(size(), value(bitStrings(0x01, 0x02)))), equalTo(value()));
        assertThat(visitor.visit(intersection(size(1, 2), value())), equalTo(value()));
        assertThat(visitor
                        .visit(intersection(size(2, 8), value(bitString(0x01, 7), bitString(0x02, 6), bitString(0x3, 6)))),
                equalTo(value(bitString(0x02, 6), bitString(0x3, 6))));
    }

    @Test
    public void testVisitBinOpNodeValueSizeComplement() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        assertThat(visitor.visit(complement(value(), size())), equalTo(value()));
        assertThat(visitor.visit(complement(value(bitStrings(0x01, 0x02)), size())),
                equalTo(value(bitStrings(0x01, 0x02))));
        assertThat(visitor.visit(complement(value(), size(1, 2))), equalTo(value()));
        assertThat(visitor
                        .visit(complement(value(bitString(0x01, 7), bitString(0x02, 6), bitString(0x3, 6)), size(2, 8))),
                equalTo(value(bitString(0x01, 7))));
    }

    @Test
    public void testVisitBinOpNodeSizeValueComplement() {
        Visitor visitor = new BitStringConstraintOptimizingVisitor();

        BinOpNode complement = complement(size(), value());

        assertThat(visitor.visit(complement), equalTo(complement));

        complement = complement(size(), value(bitStrings(0x01, 0x02)));

        assertThat(visitor.visit(complement), equalTo(complement));

        complement = complement(size(1, 2), value());

        assertThat(visitor.visit(complement), equalTo(complement));

        complement = complement(size(2, 8), value(bitString(0x01, 7), bitString(0x02, 6), bitString(0x3, 6)));

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
        return new BitStringValueNode(asList(values));
    }

}