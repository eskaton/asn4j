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
import ch.eskaton.asn4j.compiler.constraints.ast.ValueNode;
import ch.eskaton.asn4j.compiler.il.BooleanExpression;
import ch.eskaton.asn4j.compiler.il.BooleanFunctionCall;
import ch.eskaton.asn4j.compiler.il.Expression;
import ch.eskaton.asn4j.compiler.il.ILBuiltinType;
import ch.eskaton.asn4j.compiler.il.ILMapValue;
import ch.eskaton.asn4j.compiler.il.ILParameterizedType;
import ch.eskaton.asn4j.compiler.il.ILType;
import ch.eskaton.asn4j.compiler.il.ILValue;
import ch.eskaton.asn4j.compiler.il.Variable;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.types.Choice;
import ch.eskaton.asn4j.parser.ast.values.ChoiceValue;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.commons.collections.Tuple2;

import java.util.HashSet;
import java.util.Set;

import static ch.eskaton.asn4j.compiler.constraints.Constants.VAR_VALUES;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.CUSTOM;
import static java.util.Collections.singletonList;

public class ChoiceValueExpressionBuilder
        extends VectorValueExpressionBuilder<ChoiceValue, ValueNode<Set<ChoiceValue>>> {

    public ChoiceValueExpressionBuilder(CompilerContext ctx) {
        super(ctx);
    }

    @Override
    protected BooleanExpression buildExpression(CompiledType compiledType, ChoiceValue choiceValue) {
        var compiledBaseType = ctx.getCompiledBaseType(compiledType);
        var associations = new HashSet<Tuple2<Expression, Expression>>();

        ((Choice) compiledBaseType.getType()).getAllAlternatives().stream().forEach(a -> {
            var value = a.getName().equals(choiceValue.getId()) ? choiceValue.getValue() : null;

            associations.add(Tuple2.of(ILValue.of(a.getName()), ILValue.of(ctx.getTypeName(a.getType()), value)));
        });

        return new BooleanFunctionCall.MapEquals(Variable.of(VAR_VALUES), new ILMapValue(ILType.of(ILBuiltinType.STRING),
                ILParameterizedType.of(CUSTOM, singletonList(ASN1Type.class.getSimpleName())), associations));
    }

}
