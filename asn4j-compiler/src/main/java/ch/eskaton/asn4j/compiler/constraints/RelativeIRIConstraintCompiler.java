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

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.RelativeIRIValueNode;
import ch.eskaton.asn4j.compiler.constraints.optimizer.RelativeIRIConstraintOptimizingVisitor;
import ch.eskaton.asn4j.compiler.resolvers.AbstractIRIValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.RelativeIRIValueResolver;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.values.AbstractIRIValue;
import ch.eskaton.asn4j.parser.ast.values.RelativeIRIValue;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.util.List;
import java.util.Set;

public class RelativeIRIConstraintCompiler extends AbstractIRIConstraintCompiler<RelativeIRIValueNode> {

    private final RelativeIRIValueResolver valueResolver;

    public RelativeIRIConstraintCompiler(CompilerContext ctx) {
        super(ctx);

        valueResolver = new RelativeIRIValueResolver(ctx);
    }

    @Override
    protected boolean isAssignable(CompiledType compiledType, CompiledType compiledParentType) {
        return compiledType.getType().getClass()
                .isAssignableFrom(ctx.getCompiledBaseType(compiledParentType).getType().getClass());
    }

    @Override
    protected Node optimize(Node node) {
        return new RelativeIRIConstraintOptimizingVisitor().visit(node);
    }

    @Override
    protected RelativeIRIValueNode createNode(Set<List<String>> value) {
        return new RelativeIRIValueNode(value);
    }

    @Override
    protected Class<? extends AbstractIRIValue> getValueClass() {
        return RelativeIRIValue.class;
    }

    @Override
    protected AbstractIRIValueResolver getValueResolver() {
        return valueResolver;
    }

    @Override
    protected TypeName getTypeName() {
        return TypeName.RELATIVE_OID_IRI;
    }

}
