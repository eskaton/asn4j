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

import ch.eskaton.asn4j.compiler.constraints.ast.BinOpNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.NodeType;
import ch.eskaton.asn4j.compiler.constraints.ast.OpNode;
import ch.eskaton.asn4j.compiler.constraints.ast.ValueNode;

import java.util.Collection;
import java.util.function.Function;

import static ch.eskaton.asn4j.compiler.constraints.ConstraintUtils.throwUnimplementedNodeType;

public class ValueNegationTransformer<V, C extends Collection<V>, N extends Node>
        implements BinOpTransformer {

    private SetOperationsStrategy<V, C> setOperations;

    private Function<C, N> createNode;

    private boolean opSwitched;

    public ValueNegationTransformer(SetOperationsStrategy setOperations, Function<C, N> createNode, boolean opSwitched) {
        this.setOperations = setOperations;
        this.createNode = createNode;
        this.opSwitched = opSwitched;
    }

    @Override
    public N transform(BinOpNode node, Node left, Node right) {
        Node opNode = ((OpNode) right).getNode();

        if (opNode.getType() == NodeType.VALUE) {
            C leftValue = ((ValueNode<C>) left).getValue();
            C rightValue = ((ValueNode<C>) opNode).getValue();

            switch (node.getType()) {
                case INTERSECTION:
                    return createNode.apply(setOperations.complement(leftValue, rightValue));
                case COMPLEMENT:
                    if (opSwitched) {
                        return (N) new OpNode(NodeType.NEGATION, createNode.apply(setOperations.union(leftValue, rightValue)));
                    } else {
                        return createNode.apply(setOperations.intersection(leftValue, rightValue));
                    }
                default:
                    return throwUnimplementedNodeType(node);
            }
        }

        return (N) node;
    }

}
