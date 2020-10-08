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

import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.compiler.constraints.ast.IntegerRange;
import ch.eskaton.asn4j.compiler.il.BinaryBooleanExpression;
import ch.eskaton.asn4j.compiler.il.BinaryOperator;
import ch.eskaton.asn4j.compiler.il.BooleanExpression;
import ch.eskaton.asn4j.compiler.il.Expression;
import ch.eskaton.asn4j.compiler.il.ILValue;
import ch.eskaton.asn4j.compiler.il.Variable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.constraints.Constants.VAR_VALUE;
import static java.util.Optional.of;

public abstract class AbstractIntegerRangeExpressionBuilder {

    protected final Function<List<Expression>, BooleanExpression> checkMin;

    protected final Function<List<Expression>, BooleanExpression> checkMax;

    protected final Function<List<Expression>, BooleanExpression> checkEq;

    protected AbstractIntegerRangeExpressionBuilder(Function<List<Expression>, BooleanExpression> checkMin,
            Function<List<Expression>, BooleanExpression> checkMax,
            Function<List<Expression>, BooleanExpression> checkEq) {
        this.checkMin = checkMin;
        this.checkMax = checkMax;
        this.checkEq = checkEq;
    }

    public Optional<BooleanExpression> build(List<IntegerRange> ranges) {
        var expressions = ranges.stream()
                .map(this::buildExpression)
                .collect(Collectors.toList());

        return of(new BinaryBooleanExpression(BinaryOperator.OR, expressions));
    }

    private BooleanExpression buildExpression(IntegerRange range) {
        long lower = range.getLower();
        long upper = range.getUpper();

        if (lower == upper) {
            return buildExpression(lower, BinaryOperator.EQ);
        } else if (lower == Long.MIN_VALUE) {
            return buildExpression(upper, BinaryOperator.LE);
        } else if (upper == Long.MAX_VALUE) {
            return buildExpression(lower, BinaryOperator.GE);
        } else {
            BooleanExpression expr1 = buildExpression(lower, BinaryOperator.GE);
            BooleanExpression expr2 = buildExpression(upper, BinaryOperator.LE);

            return new BinaryBooleanExpression(BinaryOperator.AND, expr1, expr2);
        }
    }

    protected BooleanExpression buildExpression(long value, BinaryOperator operator) {
        return switch (operator) {
            case GE -> checkMin.apply(List.of(new Variable(VAR_VALUE), new ILValue(value)));
            case LE -> checkMax.apply(List.of(new Variable(VAR_VALUE), new ILValue(value)));
            case EQ -> checkEq.apply(List.of(new Variable(VAR_VALUE), new ILValue(value)));
            default -> throw new IllegalCompilerStateException("Illegal operator: %s", operator);
        };
    }

}
