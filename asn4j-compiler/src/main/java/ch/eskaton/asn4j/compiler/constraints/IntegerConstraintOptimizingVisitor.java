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

import ch.eskaton.asn4j.compiler.constraints.ast.*;

import java.util.List;

import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.INTERSECTION;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.Id.NEGATION;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.Id.VALUE;

public class IntegerConstraintOptimizingVisitor implements Visitor<Node, List<IntegerRange>> {

    @Override
    public Node visit(AllValuesNode node) {
        return node;
    }

    @Override
    public Node visit(BinOpNode node) {
        Node left = node.getLeft().accept(this);
        Node right = node.getRight().accept(this);

        List<IntegerRange> leftValue;
        List<IntegerRange> rightValue;
        Node temp;

        switch (left.getType().getId() << 16 | right.getType().getId()) {
            case VALUE << 16 | VALUE:
                leftValue = ((IntegerRangeValueNode) left).getValue();
                rightValue = ((IntegerRangeValueNode) right).getValue();

                switch (node.getType()) {
                    case UNION:
                        return new IntegerRangeValueNode(IntegerRange.union(leftValue, rightValue));
                    case INTERSECTION:
                        return new IntegerRangeValueNode(IntegerRange.intersection(leftValue, rightValue));
                    case COMPLEMENT:
                        return new IntegerRangeValueNode(IntegerRange.exclude(leftValue, rightValue));
                    default:
                        throw new IllegalStateException("Unimplemented node type: " + node.getType());
                }

            case NEGATION << 16 | VALUE:
                temp = right;
                right = left;
                left = temp;
                // fall through

            case VALUE << 16 | NEGATION:
                right = (((OpNode) right).getNode());

                if (right.getType() == NodeType.VALUE) {
                    leftValue = ((IntegerRangeValueNode) left).getValue();
                    rightValue = ((IntegerRangeValueNode) right).getValue();

                    if (node.getType() == INTERSECTION) {
                        return new IntegerRangeValueNode(IntegerRange.exclude(leftValue, rightValue));
                    } else {
                        throw new IllegalStateException("Unimplemented node type: " + node.getType());
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
    public Node visit(ValueNode<List<IntegerRange>> node) {
        return node;
    }

}
