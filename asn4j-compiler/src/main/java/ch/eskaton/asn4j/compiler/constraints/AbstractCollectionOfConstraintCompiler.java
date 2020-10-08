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
import ch.eskaton.asn4j.compiler.CompilerUtils;
import ch.eskaton.asn4j.compiler.constraints.ast.CollectionOfValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.SizeNode;
import ch.eskaton.asn4j.compiler.constraints.ast.WithComponentNode;
import ch.eskaton.asn4j.compiler.constraints.elements.CollectionOfContainedSubtypeCompiler;
import ch.eskaton.asn4j.compiler.constraints.elements.SingleTypeConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.elements.SingleValueCompiler;
import ch.eskaton.asn4j.compiler.constraints.elements.SizeCompiler;
import ch.eskaton.asn4j.compiler.constraints.expr.CollectionOfSizeExpressionBuilder;
import ch.eskaton.asn4j.compiler.constraints.expr.CollectionOfValueExpressionBuilder;
import ch.eskaton.asn4j.compiler.constraints.expr.WithComponentExpressionBuilder;
import ch.eskaton.asn4j.compiler.constraints.optimizer.CollectionOfConstraintOptimizingVisitor;
import ch.eskaton.asn4j.compiler.il.BooleanExpression;
import ch.eskaton.asn4j.compiler.il.FunctionCall;
import ch.eskaton.asn4j.compiler.il.ILBuiltinType;
import ch.eskaton.asn4j.compiler.il.ILParameterizedType;
import ch.eskaton.asn4j.compiler.il.ILValue;
import ch.eskaton.asn4j.compiler.il.Module;
import ch.eskaton.asn4j.compiler.il.NegationExpression;
import ch.eskaton.asn4j.compiler.il.Parameter;
import ch.eskaton.asn4j.compiler.il.Variable;
import ch.eskaton.asn4j.compiler.il.builder.FunctionBuilder;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.constraints.ContainedSubtype;
import ch.eskaton.asn4j.parser.ast.constraints.SingleTypeConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SizeConstraint;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.CollectionOfType;
import ch.eskaton.asn4j.parser.ast.types.SimpleDefinedType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.CollectionOfValue;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static ch.eskaton.asn4j.compiler.constraints.Constants.FUNC_CHECK_CONSTRAINT_VALUE;
import static ch.eskaton.asn4j.compiler.constraints.Constants.GET_UNUSED_BITS;
import static ch.eskaton.asn4j.compiler.constraints.Constants.GET_VALUE;
import static ch.eskaton.asn4j.compiler.constraints.Constants.GET_VALUES;
import static ch.eskaton.asn4j.compiler.constraints.Constants.VAR_OBJ;
import static ch.eskaton.asn4j.compiler.constraints.Constants.VAR_VALUE;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.CUSTOM;
import static java.util.Optional.of;

public abstract class AbstractCollectionOfConstraintCompiler extends AbstractConstraintCompiler {

    private final TypeName typeName;

    private final ILBuiltinType collectionType;

    protected AbstractCollectionOfConstraintCompiler(CompilerContext ctx, TypeName typeName, ILBuiltinType collectionType) {
        super(ctx);

        this.typeName = typeName;
        this.collectionType = collectionType;

        addConstraintHandler(SingleValueConstraint.class,
                new SingleValueCompiler<>(ctx, CollectionOfValue.class, CollectionOfValueNode.class, getTypeName(),
                        Set.class)::compile);
        addConstraintHandler(ContainedSubtype.class, new CollectionOfContainedSubtypeCompiler(ctx)::compile);
        addConstraintHandler(SizeConstraint.class,
                new SizeCompiler(ctx, new IntegerConstraintCompiler(ctx).getDispatcher())::compile);
        addConstraintHandler(SingleTypeConstraint.class, new SingleTypeConstraintCompiler(ctx)::compile);
    }

    @Override
    protected TypeName getTypeName() {
        return typeName;
    }

    @Override
    Optional<ConstraintDefinition> compileComponentConstraints(Type node, CompiledType compiledBaseType) {
        var maybeConstraintDefinition = super.compileComponentConstraints(node, compiledBaseType);
        var collectionOfType = (CollectionOfType) compiledBaseType.getType();

        if (collectionOfType.hasElementConstraint()) {
            var constraintDefinition = maybeConstraintDefinition.orElse(new ConstraintDefinition());
            var contentType = collectionOfType.getType();
            var compiledType = ctx.getCompiledType(contentType);
            var maybeComponentDefinition = ctx.compileConstraint(compiledType);

            if (maybeComponentDefinition.isPresent()) {
                var componentDefinition = maybeComponentDefinition.get();

                if (componentDefinition.getRoots() != null) {
                    var roots = new WithComponentNode(contentType, componentDefinition.getRoots());

                    componentDefinition.setRoots(roots);
                }

                if (componentDefinition.getExtensions() != null) {
                    var extensions = new WithComponentNode(contentType, componentDefinition.getExtensions());

                    componentDefinition.setExtensions(extensions);
                }

                return Optional.ofNullable(constraintDefinition.serialApplication(componentDefinition));
            }
        }

        return maybeConstraintDefinition;
    }

    @Override
    public void addConstraint(CompiledType compiledType, Module module, ConstraintDefinition definition) {
        generateDoCheckConstraint(module);

        var type = compiledType.getType();

        if (CompilerUtils.isAnyTypeReference(type)) {
            type = ctx.getCompiledType((SimpleDefinedType) type).getType();
        }

        var collectionOfType = (CollectionOfType) type;
        var typeParameter = ctx.getTypeParameter(collectionOfType);
        var functionBuilder = generateCheckConstraintValue(module,
                new Parameter(ILParameterizedType.of(collectionType, typeParameter), VAR_VALUE));

        addConstraintCondition(compiledType, typeParameter, definition, functionBuilder, module);

        functionBuilder.build();
    }

    protected void addConstraintCondition(CompiledType compiledType, List<String> typeParameter, ConstraintDefinition definition,
            FunctionBuilder builder, Module module) {
        String functionName = FUNC_CHECK_CONSTRAINT_VALUE;

        if (definition.isExtensible()) {
            builder.statements().returnValue(Boolean.TRUE);
        } else if (builder.getModule().getFunctions().stream().noneMatch(f -> f.getName().equals(functionName))) {
            addConstraintCondition(compiledType, definition, builder);
        } else {
            Node roots = definition.getRoots();
            Optional<BooleanExpression> expression = buildExpression(module, compiledType, roots);
            Type elementType = ((CollectionOfType) compiledType.getType()).getType();
            BooleanExpression condition;

            if (elementType instanceof CollectionOfType) {
                condition = new NegationExpression(
                        new FunctionCall(of(functionName), new FunctionCall(of(GET_VALUES), of(new ILValue(VAR_OBJ)))));
            } else if (elementType instanceof BitString) {
                condition = new NegationExpression(
                        new FunctionCall(of(functionName),
                                new FunctionCall(of(GET_VALUE), of(new ILValue(VAR_OBJ))),
                                new FunctionCall(of(GET_UNUSED_BITS), of(new ILValue(VAR_OBJ)))));
            } else {
                condition = new NegationExpression(
                        new FunctionCall(of(functionName), new FunctionCall(of(GET_VALUE), of(new ILValue(VAR_OBJ)))));
            }

            if (expression.isPresent()) {
                // @formatter:off
                builder.statements()
                        .conditions()
                            .condition(expression.get())
                                .statements()
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
                // @formatter:off
                builder.statements()
                        .foreach(new ILParameterizedType(CUSTOM, typeParameter), new Variable(VAR_OBJ), new Variable(VAR_VALUE))
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
    protected FunctionCall generateCheckConstraintCall() {
        return new FunctionCall(of(FUNC_CHECK_CONSTRAINT_VALUE), new FunctionCall(of(GET_VALUES)));
    }

    @Override
    protected Node optimize(Node node) {
        return new CollectionOfConstraintOptimizingVisitor().visit(node);
    }

    @Override
    protected Optional<BooleanExpression> buildExpression(Module module, CompiledType compiledType, Node node) {
        if (node == null) {
            return Optional.empty();
        }

        return switch (node.getType()) {
            case VALUE -> new CollectionOfValueExpressionBuilder(ctx).build(compiledType, (CollectionOfValueNode) node);
            case SIZE -> new CollectionOfSizeExpressionBuilder().build(((SizeNode) node).getSize());
            case WITH_COMPONENT -> new WithComponentExpressionBuilder(ctx)
                    .build(module, compiledType, (WithComponentNode) node);
            default -> super.buildExpression(module, compiledType, node);
        };
    }

}
