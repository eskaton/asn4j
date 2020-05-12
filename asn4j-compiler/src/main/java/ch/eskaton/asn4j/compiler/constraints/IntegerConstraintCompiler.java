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
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.constraints.ast.IntegerRange;
import ch.eskaton.asn4j.compiler.constraints.ast.IntegerRangeValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.optimizer.IntegerConstraintOptimizingVisitor;
import ch.eskaton.asn4j.compiler.constraints.optimizer.IntegerValueBoundsVisitor;
import ch.eskaton.asn4j.compiler.il.BinaryBooleanExpression;
import ch.eskaton.asn4j.compiler.il.BinaryOperator;
import ch.eskaton.asn4j.compiler.il.BooleanExpression;
import ch.eskaton.asn4j.compiler.il.FunctionCall.BigIntegerCompare;
import ch.eskaton.asn4j.compiler.il.ILType;
import ch.eskaton.asn4j.compiler.il.ILValue;
import ch.eskaton.asn4j.compiler.il.Module;
import ch.eskaton.asn4j.compiler.il.Parameter;
import ch.eskaton.asn4j.compiler.il.Variable;
import ch.eskaton.asn4j.compiler.il.builder.FunctionBuilder;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.RangeNode;
import ch.eskaton.asn4j.parser.ast.constraints.ContainedSubtype;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.constraints.ast.IntegerRange.getLowerBound;
import static ch.eskaton.asn4j.compiler.constraints.ast.IntegerRange.getUpperBound;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.BIG_INTEGER;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class IntegerConstraintCompiler extends AbstractConstraintCompiler {

    private static final IntegerValueBoundsVisitor BOUNDS_VISITOR = new IntegerValueBoundsVisitor();

    public IntegerConstraintCompiler(CompilerContext ctx) {
        super(ctx);
    }

    @Override
    protected Optional<Bounds> getBounds(Optional<ConstraintDefinition> constraint) {
        return constraint.map(c ->
                new IntegerValueBounds(getLowerBound(BOUNDS_VISITOR.visit(c.getRoots()).orElse(emptyList())),
                        getUpperBound(BOUNDS_VISITOR.visit(c.getRoots()).orElse(emptyList()))));
    }

    @Override
    protected Node calculateElements(CompiledType baseType, Elements elements,
            Optional<Bounds> bounds) {
        if (elements instanceof ElementSet) {
            return compileConstraint(baseType, (ElementSet) elements, bounds);
        } else if (elements instanceof SingleValueConstraint) {
            return calculateSingleValueConstraint(baseType, (SingleValueConstraint) elements);
        } else if (elements instanceof ContainedSubtype) {
            return calculateContainedSubtype(baseType, ((ContainedSubtype) elements).getType());
        } else if (elements instanceof RangeNode) {
            return calculateRangeNode((RangeNode) elements, bounds);
        } else {
            throw new CompilerException("Invalid constraint %s for %s type", elements.getClass().getSimpleName(),
                    TypeName.INTEGER);
        }
    }

    private Node calculateSingleValueConstraint(CompiledType baseType, SingleValueConstraint elements) {
        Value value = elements.getValue();

        try {
            IntegerValue intValue = ctx.resolveGenericValue(IntegerValue.class, baseType.getType(), value);
            long longValue = intValue.getValue().longValue();

            return new IntegerRangeValueNode((singletonList(new IntegerRange(longValue, longValue))));
        } catch (Exception e) {
            throw new CompilerException("Invalid single-value constraint %s for %s type", e,
                    value.getClass().getSimpleName(), TypeName.INTEGER);
        }
    }

    private Node calculateRangeNode(RangeNode elements, Optional<Bounds> bounds) {
        long min = bounds.map(b -> ((IntegerValueBounds) b).getMinValue()).orElse(Long.MIN_VALUE);
        long max = bounds.map(b -> ((IntegerValueBounds) b).getMaxValue()).orElse(Long.MAX_VALUE);

        IntegerValue lower = elements.getLower().getLowerEndPointValue(min);
        IntegerValue upper = elements.getUpper().getUpperEndPointValue(max);

        return new IntegerRangeValueNode(singletonList(new IntegerRange(lower.getValue().longValue(), upper
                .getValue().longValue())));
    }

    @Override
    protected boolean isAssignable(CompiledType compiledType, CompiledType compiledParentType) {
        return compiledType.getType().getClass().isAssignableFrom(ctx.getCompiledBaseType(compiledParentType).getType().getClass());
    }

    @Override
    public void addConstraint(Type type, Module module, ConstraintDefinition definition) {
        generateDoCheckConstraint(module);

        FunctionBuilder builder = generateCheckConstraintValue(module, new Parameter(ILType.of(BIG_INTEGER), "value"));

        addConstraintCondition(type, definition, builder);

        builder.build();
    }

    @Override
    protected Node optimize(Node node) {
        return new IntegerConstraintOptimizingVisitor().visit(node);
    }

    @Override
    protected Optional<BooleanExpression> buildExpression(Module module, Type type, Node node) {
        switch (node.getType()) {
            case VALUE:
                List<IntegerRange> range = ((IntegerRangeValueNode) node).getValue();
                Optional<List<BooleanExpression>> maybeExpressions =
                        Optional.of(range.stream().map(this::buildExpression).collect(Collectors.toList()));

                if (!maybeExpressions.get().isEmpty()) {
                    return Optional.of(new BinaryBooleanExpression(BinaryOperator.OR, maybeExpressions.get()));
                }

                return Optional.empty();
            case ALL_VALUES:
                return Optional.empty();
            default:
                return super.buildExpression(module, type, node);
        }
    }

    private BinaryBooleanExpression buildExpression(IntegerRange range) {
        long lower = range.getLower();
        long upper = range.getUpper();

        if (lower == upper) {
            return buildExpression(lower, BinaryOperator.EQ);
        } else if (lower == Long.MIN_VALUE) {
            return buildExpression(upper, BinaryOperator.LE);
        } else if (upper == Long.MAX_VALUE) {
            return buildExpression(lower, BinaryOperator.GE);
        } else {
            BinaryBooleanExpression expr1 = buildExpression(lower, BinaryOperator.GE);
            BinaryBooleanExpression expr2 = buildExpression(upper, BinaryOperator.LE);

            return new BinaryBooleanExpression(BinaryOperator.AND, expr1, expr2);
        }
    }

    private BinaryBooleanExpression buildExpression(long value, BinaryOperator operator) {
        return new BinaryBooleanExpression(operator,
                new BigIntegerCompare(new Variable("value"), new ILValue(BigInteger.valueOf(value))),
                new ILValue(0));
    }

}
