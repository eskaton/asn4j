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

package ch.eskaton.asn4j.compiler.constraints.expr;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.compiler.constraints.ast.ComponentNode;
import ch.eskaton.asn4j.compiler.constraints.ast.WithComponentsNode;
import ch.eskaton.asn4j.compiler.il.BinaryBooleanExpression;
import ch.eskaton.asn4j.compiler.il.BinaryOperator;
import ch.eskaton.asn4j.compiler.il.BooleanExpression;
import ch.eskaton.asn4j.compiler.il.BooleanFunctionCall;
import ch.eskaton.asn4j.compiler.il.Expression;
import ch.eskaton.asn4j.compiler.il.ILValue;
import ch.eskaton.asn4j.compiler.il.Module;
import ch.eskaton.asn4j.compiler.il.Parameter;
import ch.eskaton.asn4j.compiler.il.Variable;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.constraints.PresenceConstraint.PresenceType;
import ch.eskaton.asn4j.parser.ast.types.Choice;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.commons.collections.Tuple2;
import ch.eskaton.commons.utils.OptionalUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.constraints.Constants.FUNC_CHECK_CONSTRAINT;
import static ch.eskaton.asn4j.compiler.constraints.Constants.FUNC_EXPRESSION;
import static ch.eskaton.asn4j.compiler.constraints.Constants.VAR_VALUE;
import static ch.eskaton.asn4j.compiler.constraints.Constants.VAR_VALUES;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintUtils.getMapParameter;
import static java.util.Collections.singletonList;
import static java.util.Optional.of;

public class ChoiceWithComponentsExpressionBuilder extends WithComponentsExpressionBuilder {

    public ChoiceWithComponentsExpressionBuilder(CompilerContext ctx) {
        super(ctx);
    }

    public Optional<BooleanExpression> build(Module module, CompiledType compiledType, WithComponentsNode node) {
        var compiledComponentTypes = (ctx.getCompiledChoiceType(compiledType)).getComponents()
                .stream()
                .collect(Collectors.toMap(Tuple2::get_1, Tuple2::get_2));

        var expressionCalls = node.getComponents().stream().map(componentNode -> {
            var compiledComponent = compiledComponentTypes.get(componentNode.getName());
            var maybeValueExpression = Optional.ofNullable(componentNode.getConstraint())
                    .map(constraint -> ctx.buildExpression(module, compiledComponent, constraint)
                            .orElseThrow(() -> new IllegalCompilerStateException("Expected maybeValueExpression")));
            var maybePresenceExpression = Optional.ofNullable(componentNode.getPresenceType())
                    .map(presenceType -> {
                        var choice = (Choice) compiledType.getType();
                        var componentType = choice.getAllAlternatives().stream()
                                .filter(c -> c.getName().equals(componentNode.getName()))
                                .findFirst();

                        return componentType.map(c -> buildPresenceExpression(c, presenceType)).orElse(null);
                    });
            var expression = OptionalUtils.combine(maybeValueExpression, maybePresenceExpression,
                    (l, r) -> new BinaryBooleanExpression(BinaryOperator.AND, l, r));

            return expression.map(e -> {
                var expressionFunction = buildExpressionFunction(module, compiledComponent, e);

                return new BooleanFunctionCall(of(expressionFunction.get_1()),
                        getParameters(componentNode, compiledComponent.getName(), expressionFunction.get_2()));
            });
        }).filter(Optional::isPresent)
                .map(Optional::get).collect(Collectors.toList());

        var checkSym = module.generateSymbol(FUNC_CHECK_CONSTRAINT);
        var parameterDefinition = getMapParameter();

        if (!expressionCalls.isEmpty()) {
            var expression = new BinaryBooleanExpression(BinaryOperator.OR, expressionCalls);

            buildExpressionFunction(module, expression, checkSym, singletonList(parameterDefinition));

            return Optional.of(new BooleanFunctionCall(Optional.of(checkSym), Variable.of(VAR_VALUES)));
        } else {
            return Optional.empty();
        }
    }

    private BooleanExpression buildPresenceExpression(NamedType componentType, PresenceType presenceType) {
        return switch (presenceType) {
            case PRESENT -> new BinaryBooleanExpression(BinaryOperator.NE, new Variable(VAR_VALUE), new ILValue(null));
            case ABSENT -> getAbsentExpression(componentType);
            case OPTIONAL -> null;
        };
    }

    private BooleanExpression getAbsentExpression(NamedType componentType) {
        return new BinaryBooleanExpression(BinaryOperator.EQ, new Variable(VAR_VALUE), new ILValue(null));
    }

    private List<Expression> getParameters(ComponentNode component, String typeName, String runtimeType) {
        return parametersDispatcher.execute(runtimeType, Tuple2.of(component, typeName));
    }

    private Tuple2<String, String> buildExpressionFunction(Module module, CompiledType compiledType,
            BooleanExpression expression) {
        var expressionSym = module.generateSymbol(FUNC_EXPRESSION);
        var parameterDefinition = getParameterDefinition(compiledType);

        buildExpressionFunction(module, expression, expressionSym, parameterDefinition);

        return Tuple2.of(expressionSym, ctx.getRuntimeTypeName(compiledType.getType()));
    }

    private List<Parameter> getParameterDefinition(CompiledType compiledType) {
        return parameterDefinitionDispatcher.execute(ctx.getRuntimeTypeName(compiledType.getType()), compiledType);
    }

}
