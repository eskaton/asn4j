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
import ch.eskaton.asn4j.compiler.constraints.ast.IntegerRangeValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.elements.ContainedSubtypeCompiler;
import ch.eskaton.asn4j.compiler.constraints.elements.IntegerSingleValueCompiler;
import ch.eskaton.asn4j.compiler.constraints.elements.ValueRangeCompiler;
import ch.eskaton.asn4j.compiler.constraints.expr.IntegerRangeExpressionBuilder;
import ch.eskaton.asn4j.compiler.constraints.optimizer.IntegerConstraintOptimizingVisitor;
import ch.eskaton.asn4j.compiler.constraints.optimizer.IntegerValueBoundsVisitor;
import ch.eskaton.asn4j.compiler.il.BooleanExpression;
import ch.eskaton.asn4j.compiler.il.ILType;
import ch.eskaton.asn4j.compiler.il.Module;
import ch.eskaton.asn4j.compiler.il.Parameter;
import ch.eskaton.asn4j.compiler.il.builder.FunctionBuilder;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.RangeNode;
import ch.eskaton.asn4j.parser.ast.constraints.ContainedSubtype;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.util.Optional;

import static ch.eskaton.asn4j.compiler.constraints.Constants.VAR_VALUE;
import static ch.eskaton.asn4j.compiler.constraints.ast.IntegerRange.getLowerBound;
import static ch.eskaton.asn4j.compiler.constraints.ast.IntegerRange.getUpperBound;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.BIG_INTEGER;
import static java.util.Collections.emptyList;

public class IntegerConstraintCompiler extends AbstractConstraintCompiler {

    private static final IntegerValueBoundsVisitor BOUNDS_VISITOR = new IntegerValueBoundsVisitor();

    public IntegerConstraintCompiler(CompilerContext ctx) {
        super(ctx);

        addConstraintHandler(SingleValueConstraint.class, new IntegerSingleValueCompiler(ctx, getTypeName())::compile);
        addConstraintHandler(ContainedSubtype.class, new ContainedSubtypeCompiler(ctx)::compile);
        addConstraintHandler(RangeNode.class, new ValueRangeCompiler(ctx)::compile);
    }

    @Override
    protected Optional<Bounds> getBounds(Optional<ConstraintDefinition> constraint) {
        return constraint.map(c ->
                new IntegerValueBounds(getLowerBound(BOUNDS_VISITOR.visit(c.getRoots()).orElse(emptyList())),
                        getUpperBound(BOUNDS_VISITOR.visit(c.getRoots()).orElse(emptyList()))));
    }

    @Override
    protected TypeName getTypeName() {
        return TypeName.INTEGER;
    }

    @Override
    public void addConstraint(CompiledType type, Module module, ConstraintDefinition definition) {
        generateDoCheckConstraint(module);

        FunctionBuilder builder = generateCheckConstraintValue(module, new Parameter(ILType.of(BIG_INTEGER), VAR_VALUE));

        addConstraintCondition(type, definition, builder);

        builder.build();
    }

    @Override
    protected Node optimize(Node node) {
        return new IntegerConstraintOptimizingVisitor().visit(node);
    }

    @Override
    protected Optional<BooleanExpression> buildExpression(Module module, CompiledType compiledType, Node node) {
        return switch (node.getType()) {
            case VALUE -> new IntegerRangeExpressionBuilder().build(((IntegerRangeValueNode) node).getValue());
            case ALL_VALUES -> Optional.empty();
            default -> super.buildExpression(module, compiledType, node);
        };
    }

}
