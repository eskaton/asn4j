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
import ch.eskaton.asn4j.compiler.constraints.ast.BitStringValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.SizeNode;
import ch.eskaton.asn4j.compiler.constraints.elements.ContainedSubtypeCompiler;
import ch.eskaton.asn4j.compiler.constraints.elements.SingleValueCompiler;
import ch.eskaton.asn4j.compiler.constraints.elements.SizeCompiler;
import ch.eskaton.asn4j.compiler.constraints.expr.BitStringStringSizeExpressionBuilder;
import ch.eskaton.asn4j.compiler.constraints.optimizer.BitStringConstraintOptimizingVisitor;
import ch.eskaton.asn4j.compiler.constraints.optimizer.SizeBoundsVisitor;
import ch.eskaton.asn4j.compiler.il.BinaryBooleanExpression;
import ch.eskaton.asn4j.compiler.il.BinaryOperator;
import ch.eskaton.asn4j.compiler.il.BooleanExpression;
import ch.eskaton.asn4j.compiler.il.FunctionCall;
import ch.eskaton.asn4j.compiler.il.ILType;
import ch.eskaton.asn4j.compiler.il.ILValue;
import ch.eskaton.asn4j.compiler.il.Module;
import ch.eskaton.asn4j.compiler.il.Parameter;
import ch.eskaton.asn4j.compiler.il.Variable;
import ch.eskaton.asn4j.compiler.il.builder.FunctionBuilder;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.constraints.ContainedSubtype;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SizeConstraint;
import ch.eskaton.asn4j.parser.ast.values.BitStringValue;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.constraints.Constants.FUNC_CHECK_CONSTRAINT_VALUE;
import static ch.eskaton.asn4j.compiler.constraints.Constants.GET_UNUSED_BITS;
import static ch.eskaton.asn4j.compiler.constraints.Constants.GET_VALUE;
import static ch.eskaton.asn4j.compiler.constraints.Constants.VAR_UNUSED_BITS;
import static ch.eskaton.asn4j.compiler.constraints.Constants.VAR_VALUE;
import static ch.eskaton.asn4j.compiler.constraints.ast.IntegerRange.getLowerBound;
import static ch.eskaton.asn4j.compiler.constraints.ast.IntegerRange.getUpperBound;
import static ch.eskaton.asn4j.compiler.il.BooleanFunctionCall.ArrayEquals;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.BYTE_ARRAY;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.INTEGER;
import static java.util.Collections.emptyList;
import static java.util.Optional.of;

public class BitStringConstraintCompiler extends AbstractConstraintCompiler {

    private static final SizeBoundsVisitor BOUNDS_VISITOR = new SizeBoundsVisitor();

    public BitStringConstraintCompiler(CompilerContext ctx) {
        super(ctx);

        addConstraintHandler(SingleValueConstraint.class,
                new SingleValueCompiler(ctx, BitStringValue.class, BitStringValueNode.class, getTypeName(),
                        List.class)::compile);
        addConstraintHandler(ContainedSubtype.class, new ContainedSubtypeCompiler(ctx)::compile);
        addConstraintHandler(SizeConstraint.class,
                new SizeCompiler(ctx, new IntegerConstraintCompiler(ctx).getDispatcher())::compile);
    }

    @Override
    protected Optional<Bounds> getBounds(Optional<ConstraintDefinition> constraint) {
        return constraint.map(c ->
                new StringSizeBounds(getLowerBound(BOUNDS_VISITOR.visit(c.getRoots()).orElse(emptyList())),
                        getUpperBound(BOUNDS_VISITOR.visit(c.getRoots()).orElse(emptyList()))));
    }

    @Override
    protected TypeName getTypeName() {
        return TypeName.BIT_STRING;
    }

    @Override
    public void addConstraint(CompiledType type, Module module, ConstraintDefinition definition) {
        generateDoCheckConstraint(module);

        FunctionBuilder builder = generateCheckConstraintValue(module,
                new Parameter(ILType.of(BYTE_ARRAY), VAR_VALUE), new Parameter(ILType.of(INTEGER), VAR_UNUSED_BITS));

        addConstraintCondition(type, definition, builder);

        builder.build();
    }

    @Override
    protected FunctionCall generateCheckConstraintCall() {
        return new FunctionCall(of(FUNC_CHECK_CONSTRAINT_VALUE),
                new FunctionCall(of(GET_VALUE)), new FunctionCall(of(GET_UNUSED_BITS)));
    }

    @Override
    protected Node optimize(Node node) {
        return new BitStringConstraintOptimizingVisitor().visit(node);
    }

    @Override
    protected Optional<BooleanExpression> buildExpression(Module module, CompiledType compiledType, Node node) {
        switch (node.getType()) {
            case VALUE:
                return getValueExpression((BitStringValueNode) node);
            case ALL_VALUES:
                return Optional.empty();
            case SIZE:
                return new BitStringStringSizeExpressionBuilder().build(((SizeNode) node).getSize());
            default:
                return super.buildExpression(module, compiledType, node);
        }
    }

    private Optional<BooleanExpression> getValueExpression(BitStringValueNode node) {
        List<BitStringValue> values = node.getValue();
        List<BooleanExpression> valueArguments = values.stream()
                .map(this::buildExpression)
                .collect(Collectors.toList());

        return of(new BinaryBooleanExpression(BinaryOperator.OR, valueArguments));
    }

    private BooleanExpression buildExpression(BitStringValue value) {
        return new BinaryBooleanExpression(BinaryOperator.AND,
                new ArrayEquals(new ILValue(value.getByteValue()), new Variable(VAR_VALUE)),
                new BinaryBooleanExpression(BinaryOperator.EQ, new ILValue(value.getUnusedBits()),
                        new Variable(VAR_UNUSED_BITS)));
    }

}
