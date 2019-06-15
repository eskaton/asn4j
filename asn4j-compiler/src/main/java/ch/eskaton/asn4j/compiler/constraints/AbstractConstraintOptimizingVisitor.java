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
import ch.eskaton.asn4j.compiler.constraints.ast.OpNode;
import ch.eskaton.asn4j.compiler.constraints.ast.ValueNode;

import java.util.Collection;

import static ch.eskaton.asn4j.compiler.constraints.ConstraintUtils.throwUnimplementedNodeType;

public abstract class AbstractConstraintOptimizingVisitor<V, C extends Collection<V>, N extends ValueNode<C>>
        implements OptimizingVisitor<V> {

    private SetOperationsStrategy<V, C> setOperations;

    public AbstractConstraintOptimizingVisitor(SetOperationsStrategy<V, C> setOperations) {
        this.setOperations = setOperations;
    }

    @Override
    public Node visit(BinOpNode node) {
        Node left = node.getLeft().accept(this);
        Node right = node.getRight().accept(this);

        switch (BinOpType.of(left.getType(), right.getType())) {
            case VALUE_VALUE:
                return transformValueValue(node, (N) left, (N) right);
            case VALUE_NEGATION:
                return transformValueNegation(node, (N) left, right, false);
            case NEGATION_VALUE:
                return transformValueNegation(node, (N) right, left, true);
            default:
                return node;
        }
    }

    protected Node transformValueValue(BinOpNode node, N left, N right) {
        C leftValue = left.getValue();
        C rightValue = right.getValue();

        switch (node.getType()) {
            case UNION:
                return createNode(setOperations.union(leftValue, rightValue));
            case INTERSECTION:
                return createNode(setOperations.intersection(leftValue, rightValue));
            case COMPLEMENT:
                return createNode(setOperations.complement(leftValue, rightValue));
            default:
                return throwUnimplementedNodeType(node);
        }
    }

    protected Node transformValueNegation(BinOpNode node, N left, Node right, boolean opSwitched) {
        right = (((OpNode) right).getNode());

        if (right.getType() == NodeType.VALUE) {
            C leftValue = left.getValue();
            C rightValue = ((N) right).getValue();

            switch (node.getType()) {
                case INTERSECTION:
                    return createNode(setOperations.complement(leftValue, rightValue));
                case COMPLEMENT:
                    if (opSwitched) {
                        return new OpNode(NodeType.NEGATION, createNode(setOperations.union(leftValue, rightValue)));
                    } else {
                        return createNode(setOperations.intersection(leftValue, rightValue));
                    }
                default:
                    return throwUnimplementedNodeType(node);
            }
        }

        return node;
    }

    protected abstract N createNode(C value);

}
