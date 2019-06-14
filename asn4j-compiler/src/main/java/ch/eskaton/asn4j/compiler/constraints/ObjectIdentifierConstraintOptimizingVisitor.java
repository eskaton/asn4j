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
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.NodeType;
import ch.eskaton.asn4j.compiler.constraints.ast.ObjectIdentifierValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.OpNode;
import ch.eskaton.commons.collections.Sets;

import java.util.List;
import java.util.Set;

import static ch.eskaton.asn4j.compiler.constraints.ConstraintUtils.throwUnimplementedNodeType;

public class ObjectIdentifierConstraintOptimizingVisitor implements OptimizingVisitor<Set<List<Integer>>> {

    @Override
    public Node visit(BinOpNode node) {
        Node left = node.getLeft().accept(this);
        Node right = node.getRight().accept(this);

        switch (BinOpType.of(left.getType(), right.getType())) {
            case VALUE_VALUE:
                return transformValueValue(node, (ObjectIdentifierValueNode) left, (ObjectIdentifierValueNode) right);

            case VALUE_NEGATION:
                return transformValueNegation(node, (ObjectIdentifierValueNode) left, right, false);

            case NEGATION_VALUE:
                return transformValueNegation(node, (ObjectIdentifierValueNode) right, left, true);

            default:
                return node;
        }
    }

    private Node transformValueValue(BinOpNode node, ObjectIdentifierValueNode left, ObjectIdentifierValueNode right) {
        Set<List<Integer>> leftValue = left.getValue();
        Set<List<Integer>> rightValue = right.getValue();

        switch (node.getType()) {
            case UNION:
                return new ObjectIdentifierValueNode(Sets.<List<Integer>>builder().addAll(leftValue).addAll(rightValue).build());
            case INTERSECTION:
                return new ObjectIdentifierValueNode(Sets.<List<Integer>>builder().addAll(leftValue).retainAll(rightValue).build());
            case COMPLEMENT:
                return new ObjectIdentifierValueNode(Sets.<List<Integer>>builder().addAll(leftValue).removeAll(rightValue).build());
            default:
                return throwUnimplementedNodeType(node);
        }
    }

    private Node transformValueNegation(BinOpNode node, ObjectIdentifierValueNode left, Node right, boolean opSwitched) {
        right = (((OpNode) right).getNode());

        if (right.getType() == NodeType.VALUE) {
            Set<List<Integer>> leftValue = left.getValue();
            Set<List<Integer>> rightValue = ((ObjectIdentifierValueNode) right).getValue();

            switch (node.getType()) {
                case INTERSECTION:
                    return new ObjectIdentifierValueNode(Sets.<List<Integer>>builder().addAll(leftValue).removeAll(rightValue).build());
                case COMPLEMENT:
                    Sets.Builder<List<Integer>> values = Sets.<List<Integer>>builder().addAll(leftValue);

                    if (opSwitched) {
                        return new OpNode(NodeType.NEGATION, new ObjectIdentifierValueNode(values.addAll(rightValue).build()));
                    } else {
                        return new ObjectIdentifierValueNode(values.retainAll(rightValue).build());
                    }
                default:
                    return throwUnimplementedNodeType(node);
            }
        }

        return node;
    }

}
