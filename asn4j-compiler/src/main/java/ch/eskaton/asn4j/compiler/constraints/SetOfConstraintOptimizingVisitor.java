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
import ch.eskaton.asn4j.compiler.constraints.ast.CollectionOfValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.IntegerRange;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.NodeType;
import ch.eskaton.asn4j.compiler.constraints.ast.OpNode;
import ch.eskaton.asn4j.compiler.constraints.ast.SizeNode;
import ch.eskaton.asn4j.parser.ast.values.CollectionOfValue;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static ch.eskaton.asn4j.compiler.constraints.ConstraintUtils.throwUnimplementedNodeType;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.INTERSECTION;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.UNION;
import static java.util.stream.Collectors.toSet;

public class SetOfConstraintOptimizingVisitor
        extends AbstractConstraintOptimizingVisitor<CollectionOfValue, Set<CollectionOfValue>, CollectionOfValueNode> {

    public SetOfConstraintOptimizingVisitor() {
        super(new DefaultSetOperationsStrategy());
    }

    @Override
    public Node visit(BinOpNode node) {
        Node left = node.getLeft().accept(this);
        Node right = node.getRight().accept(this);

        switch (BinOpType.of(left.getType(), right.getType())) {
            case VALUE_VALUE:
                return transformValueValue(node, (CollectionOfValueNode) left, (CollectionOfValueNode) right);

            case VALUE_NEGATION:
                return transformValueNegation(node, (CollectionOfValueNode) left, right, false);

            case NEGATION_VALUE:
                return transformValueNegation(node, (CollectionOfValueNode) right, left, true);

            case SIZE_SIZE:
                return transformSizeSize(node, (SizeNode) left, (SizeNode) right);

            case SIZE_NEGATION:
                return transformSizeNegation(node, (SizeNode) left, right);

            case NEGATION_SIZE:
                return transformSizeNegation(node, (SizeNode) right, left);

            case VALUE_SIZE:
                return transformValueSize(node, (CollectionOfValueNode) left, (SizeNode) right);

            case SIZE_VALUE:
                return transformSizeValue(node, (SizeNode) left, (CollectionOfValueNode) right);

            default:
                return node;
        }
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

    private Node transformValueSize(BinOpNode node, CollectionOfValueNode left, SizeNode right) {
        Set<CollectionOfValue> values = left.getValue();
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

    private Node transformSizeValue(BinOpNode node, SizeNode left, CollectionOfValueNode right) {
        List<IntegerRange> sizes = left.getSize();
        Set<CollectionOfValue> values = right.getValue();

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

    private Node transformValueSizeUnion(Set<CollectionOfValue> values, List<IntegerRange> sizes) {
        if (sizes.isEmpty()) {
            return createValueNode(values);
        }

        values = values.stream()
                .filter(value -> sizes.stream().noneMatch(inRangePredicate(value)))
                .collect(toSet());

        if (values.isEmpty()) {
            return new SizeNode(sizes);
        }

        return new BinOpNode(UNION, createValueNode(values), new SizeNode(sizes));
    }

    private Node transformValueSizeIntersection(Set<CollectionOfValue> values, List<IntegerRange> sizes) {
        values = values.stream()
                .filter(value -> sizes.stream().anyMatch(inRangePredicate(value)))
                .collect(toSet());

        return createValueNode(values);
    }

    private Node transformValueSizeComplement(Set<CollectionOfValue> values, List<IntegerRange> sizes) {
        values = values.stream()
                .filter(value -> sizes.stream().noneMatch(inRangePredicate(value))).collect(toSet());

        return createValueNode(values);
    }

    @Override
    protected CollectionOfValueNode createNode(Set<CollectionOfValue> value) {
        return new CollectionOfValueNode(value);
    }

    protected CollectionOfValueNode createValueNode(Set<CollectionOfValue> values) {
        return new CollectionOfValueNode(values);
    }


    private Predicate<IntegerRange> inRangePredicate(CollectionOfValue value) {
        return size -> size.getLower() <= getSize(value) && size.getUpper() >= getSize(value);
    }

    protected int getSize(CollectionOfValue value) {
        return value.getValues().size();
    }

}
