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
import ch.eskaton.asn4j.compiler.constraints.ast.BinOpType;
import ch.eskaton.asn4j.compiler.constraints.ast.BitStringValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.IntegerRange;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.NodeType;
import ch.eskaton.asn4j.compiler.constraints.ast.OpNode;
import ch.eskaton.asn4j.compiler.constraints.ast.SizeNode;
import ch.eskaton.asn4j.parser.ast.values.BitStringValue;
import ch.eskaton.asn4j.parser.ast.values.BitStringValueComparator;
import ch.eskaton.commons.collections.Lists;
import ch.eskaton.commons.collections.Sets;

import java.util.List;
import java.util.function.Predicate;

import static ch.eskaton.asn4j.compiler.constraints.ConstraintUtils.throwUnimplementedNodeType;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.INTERSECTION;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.UNION;
import static java.util.stream.Collectors.toList;

public class BitStringConstraintOptimizingVisitor implements OptimizingVisitor<List<BitStringValue>> {

    @Override
    public Node visit(BinOpNode node) {
        Node left = node.getLeft().accept(this);
        Node right = node.getRight().accept(this);

        switch (BinOpType.of(left.getType(), right.getType())) {
            case VALUE_VALUE:
                return transformValueValue(node, (BitStringValueNode) left, (BitStringValueNode) right);

            case VALUE_NEGATION:
                return transformValueNegation(node, (BitStringValueNode) left, right);

            case NEGATION_VALUE:
                return transformValueNegation(node, (BitStringValueNode) right, left);

            case SIZE_SIZE:
                return transformSizeSize(node, (SizeNode) left, (SizeNode) right);

            case SIZE_NEGATION:
                return transformSizeNegation(node, (SizeNode) left, right);

            case NEGATION_SIZE:
                return transformSizeNegation(node, (SizeNode) right, left);

            case VALUE_SIZE:
                return transformValueSize(node, (BitStringValueNode) left, (SizeNode) right);

            case SIZE_VALUE:
                return transformSizeValue(node, (SizeNode) left, (BitStringValueNode) right);

            default:
                return node;
        }
    }

    private Node transformValueValue(BinOpNode node, BitStringValueNode left, BitStringValueNode right) {
        List<BitStringValue> leftValue = left.getValue();
        List<BitStringValue> rightValue = right.getValue();

        switch (node.getType()) {
            case UNION:
                return new BitStringValueNode(Lists.<BitStringValue>builder()
                        .addAll(Sets.<BitStringValue>builder()
                                .addAll(leftValue)
                                .addAll(rightValue).build())
                        .sorted(new BitStringValueComparator())
                        .build());
            case INTERSECTION:
                return new BitStringValueNode(Lists.<BitStringValue>builder()
                        .addAll(Sets.<BitStringValue>builder()
                                .addAll(leftValue)
                                .retainAll(rightValue).build())
                        .sorted(new BitStringValueComparator())
                        .build());
            case COMPLEMENT:
                return new BitStringValueNode(Lists.<BitStringValue>builder()
                        .addAll(Sets.<BitStringValue>builder()
                                .addAll(leftValue)
                                .removeAll(rightValue).build())
                        .sorted(new BitStringValueComparator())
                        .build());
            default:
                return throwUnimplementedNodeType(node);
        }
    }

    private Node transformValueNegation(BinOpNode node, BitStringValueNode left, Node right) {
        right = (((OpNode) right).getNode());

        if (right.getType() == NodeType.VALUE) {
            List<BitStringValue> leftValue = left.getValue();
            List<BitStringValue> rightValue = ((BitStringValueNode) right).getValue();

            if (node.getType() == INTERSECTION) {
                return new BitStringValueNode(Lists.<BitStringValue>builder()
                        .addAll(Sets.<BitStringValue>builder()
                                .addAll(leftValue)
                                .removeAll(rightValue).build())
                        .sorted(new BitStringValueComparator())
                        .build());
            } else {
                return throwUnimplementedNodeType(node);
            }
        }

        return node;
    }

    private Node transformSizeSize(BinOpNode node, SizeNode left, SizeNode right) {
        List<IntegerRange> leftSize = left.getSize();
        List<IntegerRange> rightSize = right.getSize();

        switch (node.getType()) {
            case UNION:
                return new SizeNode(IntegerRange.union(leftSize, rightSize));
            case INTERSECTION:
                return new SizeNode(IntegerRange.intersection(leftSize, rightSize));
            case COMPLEMENT:
                return new SizeNode(IntegerRange.complement(leftSize, rightSize));
            default:
                return throwUnimplementedNodeType(node);
        }
    }

    private Node transformSizeNegation(BinOpNode node, SizeNode left, Node right) {
        right = (((OpNode) right).getNode());

        if (right.getType() == NodeType.SIZE) {
            List<IntegerRange> leftSize = left.getSize();
            List<IntegerRange> rightSize = ((SizeNode) right).getSize();

            if (node.getType() == INTERSECTION) {
                return new SizeNode(IntegerRange.complement(leftSize, rightSize));
            } else {
                return throwUnimplementedNodeType(node);
            }
        }

        return node;
    }

    private Node transformValueSize(BinOpNode node, BitStringValueNode left, SizeNode right) {
        List<BitStringValue> values = left.getValue();
        List<IntegerRange> sizes = right.getSize();

        switch (node.getType()) {
            case UNION:
                return transformValueSizeUnion(values, sizes);
            case INTERSECTION:
                return transformValueSizeIntersection(values, sizes);
            case COMPLEMENT:
                return transformValueSizeComplement(values, sizes);
            default:
                return throwUnimplementedNodeType(node);
        }
    }

    private Node transformSizeValue(BinOpNode node, SizeNode left, BitStringValueNode right) {
        List<IntegerRange> sizes = left.getSize();
        List<BitStringValue> values = right.getValue();

        switch (node.getType()) {
            case UNION:
                return transformValueSizeUnion(values, sizes);
            case INTERSECTION:
                return transformValueSizeIntersection(values, sizes);
            case COMPLEMENT:
                return node;
            default:
                return throwUnimplementedNodeType(node);
        }
    }

    private Node transformValueSizeUnion(List<BitStringValue> values, List<IntegerRange> sizes) {
        if (sizes.isEmpty()) {
            return new BitStringValueNode(values);
        }

        values = values.stream()
                .filter(value -> sizes.stream().noneMatch(inRangePredicate(value)))
                .collect(toList());

        if (values.isEmpty()) {
            return new SizeNode(sizes);
        }

        return new BinOpNode(UNION, new BitStringValueNode(values), new SizeNode(sizes));
    }

    private Node transformValueSizeIntersection(List<BitStringValue> values, List<IntegerRange> sizes) {
        values = values.stream()
                .filter(value -> sizes.stream().anyMatch(inRangePredicate(value)))
                .collect(toList());

        return new BitStringValueNode(values);
    }

    private Node transformValueSizeComplement(List<BitStringValue> values, List<IntegerRange> sizes) {
        values = values.stream()
                .filter(value -> sizes.stream().noneMatch(inRangePredicate(value))).collect(toList());

        return new BitStringValueNode(values);
    }

    private Predicate<IntegerRange> inRangePredicate(BitStringValue value) {
        return size -> size.getLower() <= value.getSize() && size.getUpper() >= value.getSize();
    }

}
