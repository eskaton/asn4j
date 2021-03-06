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
import ch.eskaton.asn4j.compiler.constraints.ast.EnumeratedValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.IntegerRange;
import ch.eskaton.asn4j.compiler.constraints.ast.IntegerRangeValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.NodeType;
import ch.eskaton.asn4j.compiler.constraints.ast.ObjectIdentifierValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.OpNode;
import ch.eskaton.asn4j.compiler.constraints.ast.SizeNode;
import ch.eskaton.commons.collections.Sets;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;

public class ConstraintTestUtils {

    public static BinOpNode union(Node left, Node right) {
        return new BinOpNode(NodeType.UNION, left, right);
    }

    public static BinOpNode intersection(Node left, Node right) {
        return new BinOpNode(NodeType.INTERSECTION, left, right);
    }

    public static BinOpNode complement(Node left, Node right) {
        return new BinOpNode(NodeType.COMPLEMENT, left, right);
    }

    public static OpNode not(Node node) {
        return new OpNode(NodeType.NEGATION, node);
    }

    public static IntegerRange range(long lower, long upper) {
        return new IntegerRange(lower, upper);
    }

    public static SizeNode size() {
        return new SizeNode(emptyList());
    }

    public static SizeNode size(long lower, long upper) {
        return new SizeNode(singletonList(range(lower, upper)));
    }

    public static SizeNode size(IntegerRange... ranges) {
        return new SizeNode(asList(ranges));
    }

    public static Optional<SizeNode> optSize() {
        return Optional.of(new SizeNode(emptyList()));
    }

    public static Optional<SizeNode> optSize(long lower, long upper) {
        return Optional.of(new SizeNode(singletonList(range(lower, upper))));
    }

    public static Optional<SizeNode> optSize(IntegerRange... ranges) {
        return Optional.of(new SizeNode(asList(ranges)));
    }

    public static IntegerRangeValueNode intValue() {
        return new IntegerRangeValueNode(emptyList());
    }

    public static IntegerRangeValueNode intValue(long lower, long upper) {
        return new IntegerRangeValueNode(singletonList(range(lower, upper)));
    }

    public static IntegerRangeValueNode intValue(IntegerRange... ranges) {
        return new IntegerRangeValueNode(asList(ranges));
    }

    public static EnumeratedValueNode enumValue() {
        return new EnumeratedValueNode(emptySet());
    }

    public static EnumeratedValueNode enumValue(Integer value) {
        return new EnumeratedValueNode(singleton(value));
    }

    public static EnumeratedValueNode enumValue(Integer... values) {
        return new EnumeratedValueNode(Sets.<Integer>builder().addAll(asList(values)).build());
    }

    public static ObjectIdentifierValueNode oidValue() {
        return new ObjectIdentifierValueNode(emptySet());
    }

    public static ObjectIdentifierValueNode oidValue(Integer... value) {
        return new ObjectIdentifierValueNode(singleton(Arrays.asList(value)));
    }

    public static ObjectIdentifierValueNode oidValue(List<Integer>... value) {
        return new ObjectIdentifierValueNode(Sets.<List<Integer>>builder().addAll(Arrays.asList(value)).build());
    }

}
