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

package ch.eskaton.asn4j.compiler.constraints.elements;

import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.compiler.constraints.ast.AllValuesNode;
import ch.eskaton.asn4j.compiler.constraints.ast.BinOpNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.NodeType;
import ch.eskaton.asn4j.compiler.constraints.ast.OpNode;
import ch.eskaton.asn4j.compiler.constraints.ast.SizeNode;
import ch.eskaton.asn4j.compiler.constraints.ast.StringRange;
import ch.eskaton.asn4j.compiler.constraints.ast.StringSingleValue;
import ch.eskaton.asn4j.compiler.constraints.ast.StringValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.StringValueOrRange;
import ch.eskaton.asn4j.compiler.constraints.ast.ValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Visitor;
import ch.eskaton.asn4j.compiler.constraints.ast.WithComponentNode;
import ch.eskaton.asn4j.compiler.constraints.ast.WithComponentsNode;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class PermittedAlphabetVisitor implements Visitor<Node, List<StringValueOrRange>> {

    @Override
    public Node visit(AllValuesNode node) {
        return throwIllegalState(node);
    }

    @Override
    public Node visit(BinOpNode node) {
        return new BinOpNode(node.getType(), visit(node.getLeft()), visit(node.getRight()));
    }

    @Override
    public Node visit(OpNode node) {
        return new OpNode(node.getType(), visit(node.getNode()));
    }

    @Override
    public Node visit(SizeNode node) {
        return throwIllegalState(node);
    }

    @Override
    public Node visit(ValueNode<List<StringValueOrRange>> node) {
        List<Node> nodes1 = node.getValue().stream()
                .filter(StringSingleValue.class::isInstance)
                .map(StringSingleValue.class::cast)
                .map(v -> convertNode(node, v))
                .collect(Collectors.toList());

        var nodes2 = node.getValue().stream()
                .filter(StringRange.class::isInstance)
                .collect(Collectors.toList());

        if (!nodes2.isEmpty()) {
            nodes1.addAll(List.of(new StringValueNode(nodes2)));
        }

        return nodes1.stream().reduce(union())
                .orElseThrow(() -> new IllegalCompilerStateException("Result of transformation can't be empty. " +
                        "Input node was: %s", node));
    }

    private Node convertNode(ValueNode<List<StringValueOrRange>> node, StringSingleValue v) {
        return v.getValue().codePoints().boxed()
                .map(cp -> String.valueOf(Character.toChars(cp)))
                .map(s -> (Node) new StringValueNode(List.of(new StringRange(s, s))))
                .reduce(union())
                .orElseThrow(() -> new IllegalCompilerStateException("Result of transformation can't be empty. " +
                        "Input node was: %s", node));
    }

    private BinaryOperator<Node> union() {
        return (Node n1, Node n2) -> new BinOpNode(NodeType.UNION, n1, n2);
    }

    @Override
    public Node visit(WithComponentNode node) {
        return throwIllegalState(node);
    }

    @Override
    public Node visit(WithComponentsNode node) {
        return throwIllegalState(node);
    }

    private Node throwIllegalState(Node node) {
        throw new IllegalCompilerStateException("Invalid node type in permitted alphabet constraint: %s",
                node.getType());
    }

}
