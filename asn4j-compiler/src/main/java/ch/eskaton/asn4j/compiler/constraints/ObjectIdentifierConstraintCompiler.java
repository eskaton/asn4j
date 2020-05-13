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
import ch.eskaton.asn4j.compiler.constraints.ast.ObjectIdentifierValueNode;
import ch.eskaton.asn4j.compiler.constraints.optimizer.ObjectIdentifierConstraintOptimizingVisitor;
import ch.eskaton.asn4j.compiler.resolvers.AbstractOIDValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.ObjectIdentifierValueResolver;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.values.AbstractOIDValue;
import ch.eskaton.asn4j.parser.ast.values.ObjectIdentifierValue;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.util.List;
import java.util.Set;

public class ObjectIdentifierConstraintCompiler extends AbstractOIDConstraintCompiler<ObjectIdentifierValueNode> {

    private final ObjectIdentifierValueResolver valueResolver;

    public ObjectIdentifierConstraintCompiler(CompilerContext ctx) {
        super(ctx);

        valueResolver = new ObjectIdentifierValueResolver(ctx);
    }

    @Override
    protected boolean isAssignable(CompiledType compiledType, CompiledType compiledParentType) {
        return compiledType.getType().getClass()
                .isAssignableFrom(ctx.getCompiledBaseType(compiledParentType).getType().getClass());
    }

    @Override
    protected Node optimize(Node node) {
        return new ObjectIdentifierConstraintOptimizingVisitor().visit(node);
    }

    @Override
    protected ObjectIdentifierValueNode createNode(Set<List<Integer>> value) {
        return new ObjectIdentifierValueNode(value);
    }

    @Override
    protected Class<? extends AbstractOIDValue> getValueClass() {
        return ObjectIdentifierValue.class;
    }

    @Override
    protected AbstractOIDValueResolver getValueResolver() {
        return valueResolver;
    }

    @Override
    protected TypeName getTypeName() {
        return TypeName.OBJECT_IDENTIFIER;
    }

}
