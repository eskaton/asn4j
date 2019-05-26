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
import ch.eskaton.asn4j.compiler.constraints.ast.IntegerRange;
import ch.eskaton.asn4j.compiler.constraints.ast.IntegerRangeValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.NodeType;
import ch.eskaton.asn4j.compiler.constraints.ast.OpNode;
import ch.eskaton.asn4j.compiler.constraints.ast.SizeNode;

import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class ConstraintTestUtils {

    static BinOpNode union(Node left, Node right) {
        return new BinOpNode(NodeType.UNION, left, right);
    }

    static BinOpNode intersection(Node left, Node right) {
        return new BinOpNode(NodeType.INTERSECTION, left, right);
    }

    static BinOpNode complement(Node left, Node right) {
        return new BinOpNode(NodeType.COMPLEMENT, left, right);
    }

    static OpNode not(Node node) {
        return new OpNode(NodeType.NEGATION, node);
    }

    static IntegerRange range(long lower, long upper) {
        return new IntegerRange(lower, upper);
    }

    static SizeNode size() {
        return new SizeNode(emptyList());
    }

    static SizeNode size(long lower, long upper) {
        return new SizeNode(singletonList(range(lower, upper)));
    }

    static SizeNode size(IntegerRange... ranges) {
        return new SizeNode(asList(ranges));
    }

    static Optional<SizeNode> optSize() {
        return Optional.of(new SizeNode(emptyList()));
    }

    static Optional<SizeNode> optSize(long lower, long upper) {
        return Optional.of(new SizeNode(singletonList(range(lower, upper))));
    }

    static Optional<SizeNode> optSize(IntegerRange... ranges) {
        return Optional.of(new SizeNode(asList(ranges)));
    }

    static IntegerRangeValueNode value() {
        return new IntegerRangeValueNode(emptyList());
    }

    static IntegerRangeValueNode value(long lower, long upper) {
        return new IntegerRangeValueNode(singletonList(range(lower, upper)));
    }

    static IntegerRangeValueNode value(IntegerRange... ranges) {
        return new IntegerRangeValueNode(asList(ranges));
    }


}
