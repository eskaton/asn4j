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

import ch.eskaton.asn4j.compiler.constraints.ast.Visitor;
import org.junit.Test;

import static ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils.complement;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils.oidValue;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils.intersection;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils.not;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintTestUtils.union;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class ObjectIdentifierConstraintOptimizingVisitorTest {

    @Test
    public void testVisitBinOpNodeUnion() {
        Visitor visitor = new ObjectIdentifierConstraintOptimizingVisitor();

        assertThat(visitor.visit(union(oidValue(), oidValue())), equalTo(oidValue()));
        assertThat(visitor.visit(union(oidValue(asList(1), asList(2)), oidValue())),
                equalTo(oidValue(asList(1), asList(2))));
        assertThat(visitor.visit(union(oidValue(), oidValue(asList(1), asList(2)))),
                equalTo(oidValue(asList(1), asList(2))));
        assertThat(visitor.visit(union(oidValue(asList(1), asList(2)), oidValue(asList(2), asList(3)))),
                equalTo(oidValue(asList(1), asList(2), asList(3))));
        assertThat(visitor.visit(union(oidValue(asList(1), asList(2)), oidValue(asList(3), asList(4)))),
                equalTo(oidValue(asList(1), asList(2), asList(3), asList(4))));
    }

    @Test
    public void testVisitBinOpNodeIntersection() {
        Visitor visitor = new ObjectIdentifierConstraintOptimizingVisitor();

        assertThat(visitor.visit(intersection(oidValue(), oidValue())), equalTo(oidValue()));
        assertThat(visitor.visit(intersection(oidValue(asList(1), asList(2)), oidValue())), equalTo(oidValue()));
        assertThat(visitor.visit(intersection(oidValue(), oidValue(asList(1), asList(2)))), equalTo(oidValue()));
        assertThat(visitor.visit(intersection(oidValue(asList(1), asList(2)), oidValue(asList(2), asList(3)))),
                equalTo(oidValue(asList(2))));
        assertThat(visitor.visit(intersection(oidValue(asList(1), asList(2)), oidValue(asList(3), asList(4)))),
                equalTo(oidValue()));
    }

    @Test
    public void testVisitBinOpNodeComplement() {
        Visitor visitor = new ObjectIdentifierConstraintOptimizingVisitor();

        assertThat(visitor.visit(complement(oidValue(), oidValue())), equalTo(oidValue()));
        assertThat(visitor.visit(complement(oidValue(asList(1), asList(2)), oidValue())), equalTo(oidValue(asList(1),
                asList(2))));
        assertThat(visitor.visit(complement(oidValue(), oidValue(asList(1), asList(2)))), equalTo(oidValue()));
        assertThat(visitor.visit(complement(oidValue(asList(1), asList(2), asList(3)), oidValue(asList(3), asList(4)))),
                equalTo(oidValue(asList(1), asList(2))));
    }

    @Test
    public void testVisitBinOpNodeNot() {
        Visitor visitor = new ObjectIdentifierConstraintOptimizingVisitor();

        assertThat(visitor.visit(intersection(not(oidValue(asList(3), asList(4), asList(5))),
                oidValue(asList(1), asList(2), asList(3)))), equalTo(oidValue(asList(1), asList(2))));
        assertThat(visitor.visit(intersection(oidValue(asList(1), asList(2), asList(3)),
                not(oidValue(asList(3), asList(4), asList(5))))), equalTo(oidValue(asList(1), asList(2))));
        assertThat(visitor.visit(complement(oidValue(asList(1), asList(2), asList(3)),
                not(oidValue(asList(3), asList(4), asList(5))))), equalTo(oidValue(asList(3))));
        assertThat(visitor.visit(complement(not(oidValue(asList(1), asList(2), asList(3))),
                oidValue(asList(3), asList(4), asList(5)))),
                equalTo(not(oidValue(asList(1), asList(2), asList(3), asList(4), asList(5)))));
    }

}