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
import ch.eskaton.asn4j.compiler.constraints.ast.CollectionOfValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.IntegerRange;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.SizeNode;
import ch.eskaton.asn4j.compiler.constraints.optimizer.SetOfConstraintOptimizingVisitor;
import ch.eskaton.asn4j.compiler.il.BinaryBooleanExpression;
import ch.eskaton.asn4j.compiler.il.BinaryOperator;
import ch.eskaton.asn4j.compiler.il.BooleanExpression;
import ch.eskaton.asn4j.compiler.il.BooleanFunctionCall.SetEquals;
import ch.eskaton.asn4j.compiler.il.FunctionCall;
import ch.eskaton.asn4j.compiler.il.FunctionCall.SetSize;
import ch.eskaton.asn4j.compiler.il.ILParameterizedType;
import ch.eskaton.asn4j.compiler.il.ILValue;
import ch.eskaton.asn4j.compiler.il.Module;
import ch.eskaton.asn4j.compiler.il.NegationExpression;
import ch.eskaton.asn4j.compiler.il.Parameter;
import ch.eskaton.asn4j.compiler.il.Variable;
import ch.eskaton.asn4j.compiler.il.builder.FunctionBuilder;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.constraints.Constraint;
import ch.eskaton.asn4j.parser.ast.constraints.ContainedSubtype;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SizeConstraint;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.CollectionOfType;
import ch.eskaton.asn4j.parser.ast.types.SetOfType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.values.CollectionOfValue;
import ch.eskaton.asn4j.parser.ast.values.Value;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.CUSTOM;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.SET;
import static java.util.Collections.singleton;
import static java.util.Optional.of;

public class SetOfConstraintCompiler extends AbstractConstraintCompiler {

    public SetOfConstraintCompiler(CompilerContext ctx) {
        super(ctx);
    }

    ConstraintDefinition compileConstraints(CompiledType baseType, List<Constraint> constraints,
            Optional<Bounds> bounds) {
        ConstraintDefinition constraintDef = super.compileConstraints(baseType, constraints, bounds);

        Type elementType = ((CollectionOfType) baseType.getType()).getType();

        if (elementType.hasConstraint()) {
            constraintDef.setElementConstraint(ctx.compileConstraint(elementType));
        }

        return constraintDef;
    }

    @Override
    protected Node calculateElements(CompiledType baseType, Elements elements, Optional<Bounds> bounds) {
        if (elements instanceof ElementSet) {
            return compileConstraint(baseType, (ElementSet) elements, bounds);
        } else if (elements instanceof SingleValueConstraint) {
            Value value = ((SingleValueConstraint) elements).getValue();

            try {
                CollectionOfValue collectionOfValue = ctx.resolveGenericValue(CollectionOfValue.class,
                        baseType.getType(), value);

                return new CollectionOfValueNode(singleton(collectionOfValue));
            } catch (Exception e) {
                throw new CompilerException("Invalid single-value constraint %s for SET OF type", e,
                        value.getClass().getSimpleName());
            }
        } else if (elements instanceof ContainedSubtype) {
            return calculateContainedSubtype(((ContainedSubtype) elements).getType());
        } else if (elements instanceof SizeConstraint) {
            return calculateSize(baseType, ((SizeConstraint) elements).getConstraint(), bounds);
        } else {
            throw new CompilerException("Invalid constraint %s for SET OF type",
                    elements.getClass().getSimpleName());
        }
    }

    @Override
    public void addConstraint(Type type, Module module, ConstraintDefinition definition, int level) {
        generateDoCheckConstraint(module, level);

        ConstraintDefinition elementDefinition = definition.getElementConstraint();

        if (elementDefinition != null) {
            ctx.addConstraint(((SetOfType) type).getType(), module, elementDefinition, level + 1);
        }

        List<String> typeParameter = ctx.getTypeParameter((Type) ctx.resolveTypeReference(type));

        FunctionBuilder builder = generateCheckConstraintValue(module, level,
                new Parameter(ILParameterizedType.of(SET, typeParameter), "value"));

        addConstraintCondition(type, typeParameter, definition, builder, level + 1);

        builder.build();

    }

    protected void addConstraintCondition(Type type, List<String> typeParameter, ConstraintDefinition definition,
            FunctionBuilder builder, int level) {
        String functionName = "checkConstraintValue_" + level;

        if (definition.isExtensible()) {
            builder.statements().returnValue(Boolean.TRUE);
        } else if (!builder.getModule().getFunctions().stream().filter(f -> f.getName().equals(functionName)).findAny()
                .isPresent()) {
            addConstraintCondition(type, definition, builder);
        } else {
            Node roots = optimize(definition.getRoots());
            Optional<BooleanExpression> expression = buildExpression(getTypeName(type), roots);
            Type elementType = ((SetOfType) type).getType();
            BooleanExpression condition;

            if (elementType instanceof SetOfType) {
                condition = new NegationExpression(
                        new FunctionCall(of(functionName), new FunctionCall(of("getValues"), of(new ILValue("obj")))));
            } else if (elementType instanceof BitString) {
                condition = new NegationExpression(
                        new FunctionCall(of(functionName),
                                new FunctionCall(of("getValue"), of(new ILValue("obj"))),
                                new FunctionCall(of("getUnusedBits"), of(new ILValue("obj")))));
            } else {
                condition = new NegationExpression(
                        new FunctionCall(of(functionName), new FunctionCall(of("getValue"), of(new ILValue("obj")))));
            }

            if (expression.isPresent()) {
                // @formatter:off
                builder.statements()
                        .conditions()
                            .condition(expression.get())
                                .statements()
                                    .foreach(new ILParameterizedType(CUSTOM, typeParameter), new Variable("obj"), new Variable("value"))
                                        .statements()
                                            .conditions()
                                                .condition(condition)
                                                    .statements()
                                                        .returnValue(Boolean.FALSE)
                                                        .build()
                                                    .build()
                                                .build()
                                            .build()
                                        .build()
                                    .returnValue(Boolean.TRUE)
                                    .build()
                                .build()
                            .condition()
                                .statements()
                                    .returnValue(Boolean.FALSE)
                                    .build()
                                .build()
                        .build()
                    .build();
                // @formatter:on
            } else {
                builder.statements().returnValue(Boolean.TRUE);
            }
        }
    }

    protected FunctionCall generateCheckConstraintCall(int level) {
        return new FunctionCall(of("checkConstraintValue_" + level), new FunctionCall(of("getValues")));
    }

    @Override
    protected Node optimize(Node node) {
        return new SetOfConstraintOptimizingVisitor().visit(node);
    }

    @Override
    protected Optional<BooleanExpression> buildExpression(String typeName, Node node) {
        switch (node.getType()) {
            case VALUE:
                Set<CollectionOfValue> values = ((CollectionOfValueNode) node).getValue();
                List<BooleanExpression> valueArguments = values.stream()
                        .map(value -> buildExpression(typeName, value))
                        .collect(Collectors.toList());

                return Optional.of(new BinaryBooleanExpression(BinaryOperator.OR, valueArguments));
            case SIZE:
                List<IntegerRange> sizes = ((SizeNode) node).getSize();
                List<BooleanExpression> sizeArguments = sizes.stream().map(this::buildSizeExpression)
                        .collect(Collectors.toList());

                return Optional.of(new BinaryBooleanExpression(BinaryOperator.OR, sizeArguments));
            default:
                return super.buildExpression(typeName, node);
        }
    }

    @Override
    protected String getTypeName(Type type) {
        if (type instanceof TypeReference) {
            type = (Type) ctx.resolveTypeReference(type);
        }

        Type elementType;

        if (type instanceof SetOfType) {
            elementType = ((SetOfType) type).getType();
        } else {
            elementType = type;
        }

        return super.getTypeName(elementType);
    }

    private BooleanExpression buildExpression(String typeName, CollectionOfValue value) {
        return new SetEquals(new Variable("value"), new ILValue(typeName, value));
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
        return new BinaryBooleanExpression(operator, new SetSize(new Variable("value")), new ILValue(value));
    }

}
