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
import ch.eskaton.asn4j.compiler.constraints.ast.StringSingleValue;
import ch.eskaton.asn4j.compiler.constraints.ast.StringValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.StringValueOrRange;
import ch.eskaton.asn4j.compiler.il.BinaryBooleanExpression;
import ch.eskaton.asn4j.compiler.il.BinaryOperator;
import ch.eskaton.asn4j.compiler.il.BooleanExpression;
import ch.eskaton.asn4j.compiler.il.BooleanFunctionCall;
import ch.eskaton.asn4j.compiler.il.ILValue;
import ch.eskaton.asn4j.compiler.il.Variable;
import ch.eskaton.asn4j.compiler.results.CompiledType;

import java.util.Optional;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.constraints.Constants.VAR_VALUE;
import static java.util.Optional.of;

public class StringValueExpressionBuilder extends AbstractValueExpressionBuilder<StringValueNode> {

    public StringValueExpressionBuilder(CompilerContext ctx) {
        super(ctx);
    }

    @Override
    public Optional<BooleanExpression> build(CompiledType compiledType, StringValueNode node) {
        var expressions = node.getValue().stream()
                .map(v -> buildExpression(compiledType, v))
                .collect(Collectors.toList());

        return of(new BinaryBooleanExpression(BinaryOperator.OR, expressions));
    }

    private BooleanExpression buildExpression(CompiledType compiledType, StringValueOrRange value) {
        if (value instanceof StringSingleValue) {
            return new BooleanFunctionCall.StringEquals(new Variable(VAR_VALUE),
                    new ILValue(getTypeName(compiledType.getType()), ((StringSingleValue) value).getValue()));
        }

        throw new IllegalCompilerStateException("Unsupported value node: %s", value);
    }

}
