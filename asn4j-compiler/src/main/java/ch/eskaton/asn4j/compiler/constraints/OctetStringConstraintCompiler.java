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
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.OctetStringValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.SizeNode;
import ch.eskaton.asn4j.compiler.constraints.optimizer.OctetStringConstraintOptimizingVisitor;
import ch.eskaton.asn4j.compiler.constraints.optimizer.SizeBoundsVisitor;
import ch.eskaton.asn4j.compiler.il.BinaryBooleanExpression;
import ch.eskaton.asn4j.compiler.il.BinaryOperator;
import ch.eskaton.asn4j.compiler.il.BooleanExpression;
import ch.eskaton.asn4j.compiler.il.BooleanFunctionCall.ArrayEquals;
import ch.eskaton.asn4j.compiler.il.ILType;
import ch.eskaton.asn4j.compiler.il.ILValue;
import ch.eskaton.asn4j.compiler.il.Module;
import ch.eskaton.asn4j.compiler.il.Parameter;
import ch.eskaton.asn4j.compiler.il.Variable;
import ch.eskaton.asn4j.compiler.il.builder.FunctionBuilder;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.constraints.ContainedSubtype;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SizeConstraint;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.OctetStringValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.constraints.ast.IntegerRange.getLowerBound;
import static ch.eskaton.asn4j.compiler.constraints.ast.IntegerRange.getUpperBound;
import static ch.eskaton.asn4j.compiler.il.FunctionCall.ArrayLength;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.BYTE_ARRAY;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class OctetStringConstraintCompiler extends AbstractConstraintCompiler {

    private static final SizeBoundsVisitor BOUNDS_VISITOR = new SizeBoundsVisitor();

    private static final String VALUE = "value";

    public OctetStringConstraintCompiler(CompilerContext ctx) {
        super(ctx);
    }

    @Override
    protected Optional<Bounds> getBounds(Optional<ConstraintDefinition> constraint) {
        return constraint.map(c ->
                new StringSizeBounds(getLowerBound(BOUNDS_VISITOR.visit(c.getRoots()).orElse(emptyList())),
                        getUpperBound(BOUNDS_VISITOR.visit(c.getRoots()).orElse(emptyList()))));
    }

    @Override
    protected Node calculateElements(CompiledType baseType, Elements elements,
            Optional<Bounds> bounds) {
        if (elements instanceof ElementSet) {
            return compileConstraint(baseType, (ElementSet) elements, bounds);
        } else if (elements instanceof SingleValueConstraint) {
            return calculateSingleValueConstraints(baseType, (SingleValueConstraint) elements);
        } else if (elements instanceof ContainedSubtype) {
            return calculateContainedSubtype(baseType, ((ContainedSubtype) elements).getType());
        } else if (elements instanceof SizeConstraint) {
            return calculateSize(baseType, ((SizeConstraint) elements).getConstraint(), bounds);
        } else {
            throw new CompilerException("Invalid constraint %s for %s type",
                    elements.getClass().getSimpleName(), TypeName.OCTET_STRING);
        }
    }

    private Node calculateSingleValueConstraints(CompiledType baseType, SingleValueConstraint elements) {
        Value value = elements.getValue();

        try {
            OctetStringValue octetStringValue = ctx
                    .resolveGenericValue(OctetStringValue.class, baseType.getType(), value);

            return new OctetStringValueNode(singletonList(octetStringValue));
        } catch (Exception e) {
            throw new CompilerException("Invalid single-value constraint %s for %s type", e,
                    value.getClass().getSimpleName(), TypeName.OCTET_STRING);
        }
    }

    @Override
    public void addConstraint(CompiledType type, Module module, ConstraintDefinition definition) {
        generateDoCheckConstraint(module);

        FunctionBuilder builder = generateCheckConstraintValue(module, new Parameter(ILType.of(BYTE_ARRAY), VALUE));

        addConstraintCondition(type, definition, builder);

        builder.build();
    }

    @Override
    protected Node optimize(Node node) {
        return new OctetStringConstraintOptimizingVisitor().visit(node);
    }

    @Override
    protected Optional<BooleanExpression> buildExpression(Module module, CompiledType compiledType, Node node) {
        switch (node.getType()) {
            case VALUE:
                List<OctetStringValue> values = ((OctetStringValueNode) node).getValue();
                List<BooleanExpression> valueExpressions = values.stream().map(this::buildExpression2)
                        .collect(Collectors.toList());

                return Optional.of(new BinaryBooleanExpression(BinaryOperator.OR, valueExpressions));
            case ALL_VALUES:
                return Optional.empty();
            case SIZE:
                List<IntegerRange> sizes = ((SizeNode) node).getSize();
                List<BooleanExpression> sizeExpressions = sizes.stream().map(this::buildSizeExpression)
                        .collect(Collectors.toList());

                return Optional.of(new BinaryBooleanExpression(BinaryOperator.OR, sizeExpressions));
            default:
                return super.buildExpression(module, compiledType, node);
        }
    }

    private BooleanExpression buildExpression2(OctetStringValue value) {
        return new ArrayEquals(new ILValue(value.getByteValue()), new Variable(VALUE));
    }

    private BinaryBooleanExpression buildSizeExpression(IntegerRange range) {
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
        return new BinaryBooleanExpression(operator, new ArrayLength(new Variable(VALUE)), new ILValue(value));
    }

}
