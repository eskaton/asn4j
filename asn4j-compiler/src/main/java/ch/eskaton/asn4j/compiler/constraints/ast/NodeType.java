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

import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.Id.ALL_VALUES_ID;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.Id.COMPLEMENT_ID;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.Id.COMPONENT_ID;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.Id.INTERSECTION_ID;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.Id.NEGATION_ID;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.Id.OBJECT_SET_ID;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.Id.PERMITTED_ALPHABET_ID;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.Id.SIZE_ID;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.Id.UNION_ID;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.Id.VALUE_ID;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.Id.WITH_COMPONENTS_ID;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.Id.WITH_COMPONENT_ID;

public enum NodeType {

    UNION(UNION_ID), INTERSECTION(INTERSECTION_ID), COMPLEMENT(COMPLEMENT_ID), NEGATION(NEGATION_ID),
    VALUE(VALUE_ID), ALL_VALUES(ALL_VALUES_ID), SIZE(SIZE_ID), WITH_COMPONENT(WITH_COMPONENT_ID),
    WITH_COMPONENTS(WITH_COMPONENTS_ID), COMPONENT(COMPONENT_ID), PERMITTED_ALPHABET(PERMITTED_ALPHABET_ID),
    OBJECT_SET(OBJECT_SET_ID);

    private final int id;

    NodeType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static class Id {
        public static final int UNION_ID = 1;
        public static final int INTERSECTION_ID = 2;
        public static final int COMPLEMENT_ID = 3;
        public static final int NEGATION_ID = 4;
        public static final int VALUE_ID = 5;
        public static final int ALL_VALUES_ID = 6;
        public static final int SIZE_ID = 7;
        public static final int WITH_COMPONENT_ID = 8;
        public static final int WITH_COMPONENTS_ID = 9;
        public static final int COMPONENT_ID = 10;
        public static final int PERMITTED_ALPHABET_ID = 11;
        public static final int OBJECT_SET_ID = 12;

        private Id() {
        }

    }

}
