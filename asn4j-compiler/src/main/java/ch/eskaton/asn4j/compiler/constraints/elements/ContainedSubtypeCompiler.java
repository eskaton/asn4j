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

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.CompilerUtils;
import ch.eskaton.asn4j.compiler.Parameters;
import ch.eskaton.asn4j.compiler.constraints.Bounds;
import ch.eskaton.asn4j.compiler.constraints.ast.AllValuesNode;
import ch.eskaton.asn4j.compiler.constraints.ast.BinOpNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.NodeType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.constraints.ContainedSubtype;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;

import java.util.ArrayDeque;
import java.util.Optional;

import static ch.eskaton.asn4j.compiler.types.formatters.TypeFormatter.formatType;

public class ContainedSubtypeCompiler implements ElementsCompiler<ContainedSubtype> {

    protected final CompilerContext ctx;

    public ContainedSubtypeCompiler(CompilerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public Node compile(CompiledType compiledType, ContainedSubtype elements, Optional<Bounds> bounds,
            Optional<Parameters> maybeParameters) {
        var parent = elements.getType();
        var constraints = new ArrayDeque<Node>();

        do {
            var compiledParentType = getCompiledParentType(parent, maybeParameters);

            if (!isAssignable(compiledType, compiledParentType)) {
                throw new CompilerException("Type %s can't be used in INCLUDES constraint of type %s",
                        formatType(ctx, compiledParentType.getType()), formatType(ctx, compiledType.getType()));
            }

            var maybeConstraintDefinition = compiledParentType.getConstraintDefinition();

            if (maybeConstraintDefinition.isPresent()) {
                constraints.push(maybeConstraintDefinition.get().getRoots());
            }

            parent = compiledParentType.getType();
        } while (parent instanceof TypeReference);

        if (constraints.isEmpty()) {
            return new AllValuesNode();
        } else if (constraints.size() == 1) {
            return constraints.pop();
        } else {
            var node1 = constraints.pop();
            var node2 = constraints.pop();

            do {
                node1 = new BinOpNode(NodeType.INTERSECTION, node1, node2);

                if (constraints.isEmpty()) {
                    break;
                }

                node2 = constraints.pop();
            } while (true);

            return node1;
        }
    }

    private CompiledType getCompiledParentType(Type type, Optional<Parameters> maybeParameters) {
        if (type instanceof TypeReference typeReference) {
            return CompilerUtils.compileTypeReference(ctx, typeReference, maybeParameters);
        }

        return ctx.getCompiledType(type);
    }

    protected boolean isAssignable(CompiledType compiledType, CompiledType compiledParentType) {
        var compiledBaseType = ctx.getCompiledBaseType(compiledParentType);

        return compiledType.getType().getClass().isAssignableFrom(compiledBaseType.getType().getClass());
    }

}
