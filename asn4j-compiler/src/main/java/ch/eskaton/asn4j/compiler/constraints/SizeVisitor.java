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
import ch.eskaton.asn4j.compiler.constraints.ast.IntegerRange;
import ch.eskaton.asn4j.compiler.constraints.ast.OpNode;
import ch.eskaton.asn4j.compiler.constraints.ast.SizeNode;
import ch.eskaton.asn4j.compiler.constraints.ast.ValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Visitor;
import ch.eskaton.asn4j.compiler.constraints.ast.WithComponentNode;

import java.util.List;
import java.util.Optional;

import static ch.eskaton.asn4j.compiler.constraints.ConstraintUtils.throwUnimplementedNodeType;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.NEGATION;
import static ch.eskaton.commons.utils.OptionalUtils.combine;
import static java.util.stream.Collectors.toList;

public class SizeVisitor implements Visitor<Optional<SizeNode>, List<IntegerRange>> {

    @Override
    public Optional<SizeNode> visit(AllValuesNode node) {
        return Optional.empty();
    }

    @Override
    public Optional<SizeNode> visit(BinOpNode node) {
        Optional<List<IntegerRange>> left = node.getLeft().accept(this).map(SizeNode::getSize);
        Optional<List<IntegerRange>> right = node.getRight().accept(this).map(SizeNode::getSize);

        switch (node.getType()) {
            case UNION:
                return combine(left, right, IntegerRange::union).map(SizeNode::new);
            case INTERSECTION:
                return combine(left, right, IntegerRange::intersection).map(SizeNode::new);
            case COMPLEMENT:
                if (left.isPresent() && right.isPresent()) {
                    return combine(left, right, IntegerRange::complement).map(SizeNode::new);
                } else if (left.isPresent()) {
                    return left.map(SizeNode::new);
                } else {
                    return createInvertedSizeNode(right);
                }
            default:
                return throwUnimplementedNodeType(node);
        }
    }

    @Override
    public Optional<SizeNode> visit(OpNode node) {
        if (node.getType() == NEGATION) {
            return createInvertedSizeNode(node.getNode().accept(this).map(SizeNode::getSize));
        } else {
            return throwUnimplementedNodeType(node);
        }
    }

    @Override
    public Optional<SizeNode> visit(SizeNode node) {
        throw new IllegalStateException();
    }

    @Override
    public Optional<SizeNode> visit(ValueNode<List<IntegerRange>> node) {
        return Optional.of(new SizeNode(node.getValue()));
    }

    @Override
    public Optional<SizeNode> visit(WithComponentNode node) {
        return Optional.empty();
    }

    private Optional<SizeNode> createInvertedSizeNode(Optional<List<IntegerRange>> maybeRanges) {
        return maybeRanges.map(IntegerRange::invert)
                .map(ranges -> ranges.stream()
                        .filter(range -> range.getUpper() >= 0)
                        .map(range -> range.getLower() < 0 ? new IntegerRange(0, range.getUpper()) : range)
                        .collect(toList()))
                .map(SizeNode::new);
    }

}
