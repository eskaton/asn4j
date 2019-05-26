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

package ch.eskaton.asn4j.compiler.constraints.ast;

import static ch.eskaton.asn4j.compiler.constraints.ast.BinOpType.BinOpId.IGNORED_ID;
import static ch.eskaton.asn4j.compiler.constraints.ast.BinOpType.BinOpId.NEGATION_SIZE_ID;
import static ch.eskaton.asn4j.compiler.constraints.ast.BinOpType.BinOpId.NEGATION_VALUE_ID;
import static ch.eskaton.asn4j.compiler.constraints.ast.BinOpType.BinOpId.SIZE_NEGATION_ID;
import static ch.eskaton.asn4j.compiler.constraints.ast.BinOpType.BinOpId.SIZE_SIZE_ID;
import static ch.eskaton.asn4j.compiler.constraints.ast.BinOpType.BinOpId.SIZE_VALUE_ID;
import static ch.eskaton.asn4j.compiler.constraints.ast.BinOpType.BinOpId.VALUE_NEGATION_ID;
import static ch.eskaton.asn4j.compiler.constraints.ast.BinOpType.BinOpId.VALUE_SIZE_ID;
import static ch.eskaton.asn4j.compiler.constraints.ast.BinOpType.BinOpId.VALUE_VALUE_ID;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.Id.NEGATION_ID;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.Id.SIZE_ID;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.Id.VALUE_ID;
import static java.util.Arrays.asList;

public enum BinOpType {

    VALUE_VALUE(VALUE_VALUE_ID), NEGATION_VALUE(NEGATION_VALUE_ID), VALUE_NEGATION(VALUE_NEGATION_ID),
    SIZE_SIZE(SIZE_SIZE_ID), NEGATION_SIZE(NEGATION_SIZE_ID), SIZE_NEGATION(SIZE_NEGATION_ID),
    VALUE_SIZE(VALUE_SIZE_ID), SIZE_VALUE(SIZE_VALUE_ID), IGNORED(IGNORED_ID);

    private final int id;

    BinOpType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static BinOpType of(NodeType leftType, NodeType rightType) {
          return asList(BinOpType.values()).stream()
                .filter(value -> value.id == (leftType.getId() << 16 | rightType.getId()))
                .findFirst()
                .orElse(IGNORED);
    }

    static class BinOpId {
        public static final int VALUE_VALUE_ID = combine(VALUE_ID, VALUE_ID);
        public static final int NEGATION_VALUE_ID = combine(NEGATION_ID, VALUE_ID);
        public static final int VALUE_NEGATION_ID = combine(VALUE_ID, NEGATION_ID);
        public static final int SIZE_SIZE_ID = combine(SIZE_ID, SIZE_ID);
        public static final int NEGATION_SIZE_ID = combine(NEGATION_ID, SIZE_ID);
        public static final int SIZE_NEGATION_ID = combine(SIZE_ID, NEGATION_ID);
        public static final int VALUE_SIZE_ID = combine(VALUE_ID, SIZE_ID);
        public static final int SIZE_VALUE_ID = combine(SIZE_ID, VALUE_ID);
        public static final int IGNORED_ID = combine(0xff, 0xff);

        private BinOpId() {
        }

        static int combine(int leftNode, int rightNode) {
            return leftNode << 16 | rightNode;
        }

    }

}
