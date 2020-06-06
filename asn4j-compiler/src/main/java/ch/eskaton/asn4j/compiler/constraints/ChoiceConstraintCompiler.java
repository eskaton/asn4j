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
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.ValueNode;
import ch.eskaton.asn4j.compiler.constraints.elements.SingleValueCompiler;
import ch.eskaton.asn4j.compiler.constraints.expr.ChoiceValueExpressionBuilder;
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
import ch.eskaton.asn4j.compiler.il.builder.FunctionBuilder;
import ch.eskaton.asn4j.compiler.results.CompiledChoiceType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.values.ChoiceValue;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.asn4j.runtime.types.TypeName;
import ch.eskaton.commons.collections.Tuple2;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static ch.eskaton.asn4j.compiler.constraints.Constants.FUNC_CHECK_CONSTRAINT_VALUE;
import static ch.eskaton.asn4j.compiler.constraints.Constants.FUNC_DO_CHECK_CONSTRAINT;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintUtils.getMapParameter;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.BOOLEAN;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.CUSTOM;
import static ch.eskaton.commons.utils.StringUtils.initCap;
import static java.util.Collections.singletonList;
import static java.util.Optional.of;

public class ChoiceConstraintCompiler extends AbstractConstraintCompiler {

    public ChoiceConstraintCompiler(CompilerContext ctx) {
        super(ctx);

        addConstraintHandler(SingleValueConstraint.class,
                new SingleValueCompiler(ctx, ChoiceValue.class, ValueNode.class, getTypeName(),
                        Set.class)::compile);
    }

    @Override
    protected TypeName getTypeName() {
        return TypeName.CHOICE;
    }

    @Override
    protected void addConstraint(CompiledType compiledType, Module module, ConstraintDefinition definition) {
        generateDoCheckConstraint((CompiledChoiceType) compiledType, module);

        FunctionBuilder builder = generateCheckConstraintValue(module, getMapParameter());

        addConstraintCondition(compiledType, definition, builder, module);

        builder.build();
    }

    protected void generateDoCheckConstraint(CompiledChoiceType compiledType, Module module) {
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

    protected FunctionCall generateCheckConstraintCall(CompiledChoiceType compiledType) {
        Set<Tuple2<Expression, Expression>> associations = new HashSet<>();

        compiledType.getComponents().stream()
                .map(Tuple2::get_1)
                .map(n -> new Tuple2(ILValue.of(n), new FunctionCall(of("get" + initCap(n)))))
                .forEach(associations::add);

        return new FunctionCall(of(FUNC_CHECK_CONSTRAINT_VALUE),
                new ILMapValue(ILType.of(ILBuiltinType.STRING),
                        ILParameterizedType.of(CUSTOM, singletonList(ASN1Type.class.getSimpleName())), associations));
    }

    private void addConstraintCondition(CompiledType compiledType, ConstraintDefinition definition,
            FunctionBuilder builder, Module module) {
        if (definition.isExtensible()) {
            builder.statements().returnValue(Boolean.TRUE);
        } else {
            Node roots = definition.getRoots();
            Optional<BooleanExpression> expression = buildExpression(module, compiledType, roots);

            if (expression.isPresent()) {
                builder.statements().returnExpression(expression.get()).build();
            } else {
                new IllegalCompilerStateException("Expression is empty");
            }
        }
    }

    @Override
    protected Optional<BooleanExpression> buildExpression(Module module, CompiledType compiledType, Node node) {
        if (node == null) {
            return Optional.empty();
        }

        return switch (node.getType()) {
            case VALUE -> new ChoiceValueExpressionBuilder(ctx).build(compiledType, (ValueNode<Set<ChoiceValue>>) node);
            default -> super.buildExpression(module, compiledType, node);
        };
    }

}
