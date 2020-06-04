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
import ch.eskaton.asn4j.compiler.constraints.ast.CollectionValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.WithComponentsNode;
import ch.eskaton.asn4j.compiler.constraints.elements.CollectionContainedSubtypeCompiler;
import ch.eskaton.asn4j.compiler.constraints.elements.MultipleTypeConstraintsCompiler;
import ch.eskaton.asn4j.compiler.constraints.elements.SingleValueCompiler;
import ch.eskaton.asn4j.compiler.constraints.expr.CollectionValueExpressionBuilder;
import ch.eskaton.asn4j.compiler.constraints.expr.WithComponentsExpressionBuilder;
import ch.eskaton.asn4j.compiler.constraints.optimizer.CollectionConstraintOptimizingVisitor;
import ch.eskaton.asn4j.compiler.il.BooleanExpression;
import ch.eskaton.asn4j.compiler.il.Expression;
import ch.eskaton.asn4j.compiler.il.FunctionCall;
import ch.eskaton.asn4j.compiler.il.ILBuiltinType;
import ch.eskaton.asn4j.compiler.il.ILMapValue;
import ch.eskaton.asn4j.compiler.il.ILParameterizedType;
import ch.eskaton.asn4j.compiler.il.ILType;
import ch.eskaton.asn4j.compiler.il.ILValue;
import ch.eskaton.asn4j.compiler.il.ILVisibility;
import ch.eskaton.asn4j.compiler.il.Module;
import ch.eskaton.asn4j.compiler.il.NegationExpression;
import ch.eskaton.asn4j.compiler.il.Variable;
import ch.eskaton.asn4j.compiler.il.builder.FunctionBuilder;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.constraints.ContainedSubtype;
import ch.eskaton.asn4j.parser.ast.constraints.MultipleTypeConstraints;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.CollectionOfType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.CollectionValue;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.asn4j.runtime.types.TypeName;
import ch.eskaton.commons.collections.Tuple2;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static ch.eskaton.asn4j.compiler.constraints.Constants.FUNC_CHECK_CONSTRAINT_VALUE;
import static ch.eskaton.asn4j.compiler.constraints.Constants.FUNC_DO_CHECK_CONSTRAINT;
import static ch.eskaton.asn4j.compiler.constraints.Constants.GET_UNUSED_BITS;
import static ch.eskaton.asn4j.compiler.constraints.Constants.GET_VALUE;
import static ch.eskaton.asn4j.compiler.constraints.Constants.GET_VALUES;
import static ch.eskaton.asn4j.compiler.constraints.Constants.VAR_OBJ;
import static ch.eskaton.asn4j.compiler.constraints.Constants.VAR_VALUE;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintUtils.getMapParameter;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.BOOLEAN;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.CUSTOM;
import static ch.eskaton.commons.utils.StringUtils.initCap;
import static java.util.Collections.singletonList;
import static java.util.Optional.of;

public abstract class AbstractCollectionConstraintCompiler extends AbstractConstraintCompiler {

    private final TypeName typeName;

    public AbstractCollectionConstraintCompiler(CompilerContext ctx, TypeName typeName) {
        super(ctx);

        this.typeName = typeName;

        addConstraintHandler(SingleValueConstraint.class,
                new SingleValueCompiler(ctx, CollectionValue.class, CollectionValueNode.class, getTypeName(),
                        Set.class)::compile);
        addConstraintHandler(ContainedSubtype.class, new CollectionContainedSubtypeCompiler(ctx)::compile);
        addConstraintHandler(MultipleTypeConstraints.class, new MultipleTypeConstraintsCompiler(ctx)::compile);
    }

    @Override
    protected TypeName getTypeName() {
        return typeName;
    }

    @Override
    public void addConstraint(CompiledType compiledType, Module module, ConstraintDefinition definition) {
        var compiledBaseType = ctx.getCompiledCollectionType(compiledType);

        generateDoCheckConstraint(compiledBaseType, module);

        List<String> typeParameter = ctx.getTypeParameter(compiledBaseType.getType());

        FunctionBuilder builder = generateCheckConstraintValue(module, getMapParameter());

        addConstraintCondition(compiledType, typeParameter, definition, builder, module);

        builder.build();
    }

    protected void generateDoCheckConstraint(CompiledCollectionType compiledType, Module module) {
        // @formatter:off
        module.function()
                .name(FUNC_DO_CHECK_CONSTRAINT)
                .overriden(true)
                .visibility(ILVisibility.PUBLIC)
                .returnType(ILType.of(BOOLEAN))
                .statements()
                    .returnExpression(generateCheckConstraintCall(compiledType))
                    .build()
                .build();
        // @formatter:on
    }

    protected FunctionCall generateCheckConstraintCall(CompiledCollectionType compiledType) {
        Set<Tuple2<Expression, Expression>> associations = new HashSet<>();

        compiledType.getComponents().stream()
                .map(Tuple2::get_1)
                .map(n -> new Tuple2(ILValue.of(n), new FunctionCall(of("get" + initCap(n)))))
                .forEach(associations::add);

        return new FunctionCall(of(FUNC_CHECK_CONSTRAINT_VALUE),
                new ILMapValue(ILType.of(ILBuiltinType.STRING),
                        ILParameterizedType.of(CUSTOM, singletonList(ASN1Type.class.getSimpleName())), associations));
    }

    protected void addConstraintCondition(CompiledType compiledType, List<String> typeParameter, ConstraintDefinition definition,
            FunctionBuilder builder, Module module) {
        if (definition.isExtensible()) {
            builder.statements().returnValue(Boolean.TRUE);
        } else if (builder.getModule().getFunctions().stream().noneMatch(f -> f.getName().equals(FUNC_CHECK_CONSTRAINT_VALUE))) {
            addConstraintCondition(compiledType, definition, builder);
        } else {
            Node roots = definition.getRoots();
            Optional<BooleanExpression> expression = buildExpression(module, compiledType, roots);
            Type elementType = ((CollectionOfType) compiledType.getType()).getType();
            BooleanExpression condition;

            if (elementType instanceof CollectionOfType) {
                condition = new NegationExpression(
                        new FunctionCall(of(FUNC_CHECK_CONSTRAINT_VALUE), new FunctionCall(of(GET_VALUES), of(ILValue.of(VAR_OBJ)))));
            } else if (elementType instanceof BitString) {
                condition = new NegationExpression(
                        new FunctionCall(of(FUNC_CHECK_CONSTRAINT_VALUE),
                                new FunctionCall(of(GET_VALUE), of(ILValue.of(VAR_OBJ))),
                                new FunctionCall(of(GET_UNUSED_BITS), of(ILValue.of(VAR_OBJ)))));
            } else {
                condition = new NegationExpression(
                        new FunctionCall(of(FUNC_CHECK_CONSTRAINT_VALUE), new FunctionCall(of(GET_VALUE), of(ILValue.of(VAR_OBJ)))));
            }

            if (expression.isPresent()) {
                builder.statements().returnExpression(expression.get()).build();
            } else {
                // @formatter:off
                builder.statements()
                        .foreach(ILParameterizedType.of(CUSTOM, typeParameter), Variable.of(VAR_OBJ), Variable.of(VAR_VALUE))
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
                        .build();
                // @formatter:on
            }
        }
    }

    @Override
    protected Node optimize(Node node) {
        return new CollectionConstraintOptimizingVisitor().visit(node);
    }

    @Override
    protected Optional<BooleanExpression> buildExpression(Module module, CompiledType compiledType, Node node) {
        if (node == null) {
            return Optional.empty();
        }

        return switch (node.getType()) {
            case VALUE -> new CollectionValueExpressionBuilder(ctx).build(compiledType, (CollectionValueNode) node);
            case WITH_COMPONENTS -> new WithComponentsExpressionBuilder(ctx)
                    .build(module, compiledType, (WithComponentsNode) node);
            default -> super.buildExpression(module, compiledType, node);
        };
    }

}
