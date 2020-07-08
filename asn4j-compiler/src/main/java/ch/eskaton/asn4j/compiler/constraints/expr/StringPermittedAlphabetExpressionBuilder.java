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

import ch.eskaton.asn4j.compiler.constraints.ast.BinOpNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.OpNode;
import ch.eskaton.asn4j.compiler.constraints.ast.StringRange;
import ch.eskaton.asn4j.compiler.constraints.ast.StringValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.StringValueOrRange;
import ch.eskaton.asn4j.compiler.il.BinaryBooleanExpression;
import ch.eskaton.asn4j.compiler.il.BinaryOperator;
import ch.eskaton.asn4j.compiler.il.BooleanExpression;
import ch.eskaton.asn4j.compiler.il.BooleanFunctionCall;
import ch.eskaton.asn4j.compiler.il.FunctionCall;
import ch.eskaton.asn4j.compiler.il.ILBuiltinType;
import ch.eskaton.asn4j.compiler.il.ILType;
import ch.eskaton.asn4j.compiler.il.ILValue;
import ch.eskaton.asn4j.compiler.il.Module;
import ch.eskaton.asn4j.compiler.il.NegationExpression;
import ch.eskaton.asn4j.compiler.il.Variable;
import ch.eskaton.commons.utils.OptionalUtils;

import java.util.List;
import java.util.Optional;

import static ch.eskaton.asn4j.compiler.constraints.ConstraintUtils.throwUnimplementedNodeType;

public class StringPermittedAlphabetExpressionBuilder {

    private static final String FUNC_CHECK_ALPHABET = "checkPermittedAlphabet";

    private static final String FUNC_GET_CHARS = "getChars";

    private static final String VAR_VALUE = "value";

    private static final String VAR_CHR = "chr";

    public Optional<BooleanExpression> build(Module module, Node node) {
        var expression = buildExpression(node);
        var expressionSym = module.generateSymbol(FUNC_CHECK_ALPHABET);

        if (expression.isPresent()) {
            // @formatter:off
            module.function()
                    .name(expressionSym)
                    .returnType(ILType.of(ILBuiltinType.BOOLEAN))
                    .parameter(ILType.of(ILBuiltinType.INTEGER), VAR_CHR)
                    .statements()
                        .returnExpression(expression.get())
                        .build()
                    .build();

            module.function()
                    .name(expressionSym)
                    .returnType(ILType.of(ILBuiltinType.BOOLEAN))
                    .parameter(ILType.of(ILBuiltinType.STRING), VAR_VALUE)
                    .statements()
                        .foreach(ILType.of(ILBuiltinType.INTEGER), Variable.of(VAR_CHR),
                                new FunctionCall(Optional.of(FUNC_GET_CHARS), new Variable(VAR_VALUE)))
                            .statements()
                                .conditions()
                                    .condition(new NegationExpression(new BooleanFunctionCall(
                                            Optional.of(expressionSym), new Variable(VAR_CHR))))
                                        .statements()
                                            .returnValue(Boolean.FALSE)
                                            .build()
                                        .build()
                                    .build()
                                .build()
                            .build()
                            .returnValue(Boolean.TRUE)
                        .build()
                    .build();
            // @formatter:on

            return Optional.of(new BooleanFunctionCall(Optional.of(expressionSym), new Variable(VAR_VALUE)));
        }

        return Optional.empty();
    }

    protected Optional<BooleanExpression> buildExpression(Node node) {
        switch (node.getType()) {
            case UNION:
                return OptionalUtils.combine(
                        buildExpression(((BinOpNode) node).getLeft()),
                        buildExpression(((BinOpNode) node).getRight()),
                        getBinOperation(BinaryOperator.OR));
            case INTERSECTION:
                return OptionalUtils.combine(
                        buildExpression(((BinOpNode) node).getLeft()),
                        buildExpression(((BinOpNode) node).getRight()),
                        getBinOperation(BinaryOperator.AND));
            case COMPLEMENT:
                return OptionalUtils.combine(
                        buildExpression(((BinOpNode) node).getLeft()),
                        buildExpression(((BinOpNode) node).getRight()).map(this::negate),
                        getBinOperation(BinaryOperator.AND));
            case NEGATION:
                return buildExpression(((OpNode) node).getNode()).map(this::negate);
            case VALUE:
                return buildExpression(((StringValueNode) node).getValue());
            default:
                return throwUnimplementedNodeType(node);
        }
    }

    private Optional<BooleanExpression> buildExpression(List<StringValueOrRange> value) {
        return value.stream().map(this::buildExpression).reduce(union());
    }

    private java.util.function.BinaryOperator<BooleanExpression> union() {
        return getBinOperation(BinaryOperator.AND);
    }

    private BooleanExpression buildExpression(StringValueOrRange value) {
        return new BooleanFunctionCall(Optional.of(FUNC_CHECK_ALPHABET),
                new Variable(VAR_CHR),
                new ILValue(((StringRange) value).getLower()), new ILValue(((StringRange) value).getUpper()));
    }

    private BooleanExpression negate(BooleanExpression expr) {
        return new NegationExpression(expr);
    }

    private java.util.function.BinaryOperator<BooleanExpression> getBinOperation(BinaryOperator operator) {
        return (BooleanExpression a, BooleanExpression b) -> new BinaryBooleanExpression(operator, a, b);
    }

}
