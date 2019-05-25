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

import ch.eskaton.asn4j.compiler.constraints.ast.AllValuesNode;
import ch.eskaton.asn4j.compiler.constraints.ast.BinOpNode;
import ch.eskaton.asn4j.compiler.constraints.ast.BitStringValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.IntegerRange;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.NodeType;
import ch.eskaton.asn4j.compiler.constraints.ast.OpNode;
import ch.eskaton.asn4j.compiler.constraints.ast.SizeNode;
import ch.eskaton.asn4j.compiler.constraints.ast.ValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Visitor;
import ch.eskaton.asn4j.parser.ast.values.BitStringValueComparator;
import ch.eskaton.asn4j.parser.ast.values.BitStringValue;
import ch.eskaton.commons.collections.Lists;
import ch.eskaton.commons.collections.Sets;

import java.util.List;

import static ch.eskaton.asn4j.compiler.constraints.ConstraintUtils.throwUnimplementedNodeType;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.INTERSECTION;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.Id.NEGATION;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.Id.SIZE;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.Id.VALUE;

public class BitStringConstraintOptimizingVisitor implements Visitor<Node, List<BitStringValue>> {

    @Override
    public Node visit(AllValuesNode node) {
        return node;
    }

    @Override
    public Node visit(BinOpNode node) {
        Node left = node.getLeft().accept(this);
        Node right = node.getRight().accept(this);

        List<BitStringValue> leftValue;
        List<BitStringValue> rightValue;
        List<IntegerRange> leftSize;
        List<IntegerRange> rightSize;

        Node temp;

        switch (left.getType().getId() << 16 | right.getType().getId()) {
            case VALUE << 16 | VALUE:
                leftValue = ((BitStringValueNode) left).getValue();
                rightValue = ((BitStringValueNode) right).getValue();

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

            case NEGATION << 16 | VALUE:
                temp = right;
                right = left;
                left = temp;
                // fall through

            case VALUE << 16 | NEGATION:
                right = (((OpNode) right).getNode());

                if (right.getType() == NodeType.VALUE) {
                    leftValue = ((BitStringValueNode) left).getValue();
                    rightValue = ((BitStringValueNode) right).getValue();

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

            case SIZE << 16 | SIZE:
                leftSize = ((SizeNode) left).getSize();
                rightSize = ((SizeNode) right).getSize();

                switch (node.getType()) {
                    case UNION:
                        return new SizeNode(IntegerRange.union(leftSize, rightSize));
                    case INTERSECTION:
                        return new SizeNode(IntegerRange.intersection(leftSize, rightSize));
                    case COMPLEMENT:
                        return new SizeNode(IntegerRange.exclude(leftSize, rightSize));
                    default:
                        return throwUnimplementedNodeType(node);
                }

            case NEGATION << 16 | SIZE:
                temp = right;
                right = left;
                left = temp;
                // fall through

            case SIZE << 16 | NEGATION:
                right = (((OpNode) right).getNode());

                if (right.getType() == NodeType.SIZE) {
                    leftSize = ((SizeNode) left).getSize();
                    rightSize = ((SizeNode) right).getSize();

                    if (node.getType() == INTERSECTION) {
                        return new SizeNode(IntegerRange.exclude(leftSize, rightSize));
                    } else {
                        return throwUnimplementedNodeType(node);
                    }
                }

                return node;

            default:
                return node;
        }
    }

    @Override
    public Node visit(OpNode node) {
        return node;
    }

    @Override
    public Node visit(SizeNode node) {
        return node;
    }

    @Override
    public Node visit(ValueNode<List<BitStringValue>> node) {
        return node;
    }

}
