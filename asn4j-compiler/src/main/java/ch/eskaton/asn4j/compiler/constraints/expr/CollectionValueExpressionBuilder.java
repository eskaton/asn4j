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
import ch.eskaton.asn4j.compiler.constraints.ast.CollectionValueNode;
import ch.eskaton.asn4j.compiler.il.BooleanExpression;
import ch.eskaton.asn4j.compiler.il.BooleanFunctionCall;
import ch.eskaton.asn4j.compiler.il.Expression;
import ch.eskaton.asn4j.compiler.il.ILBuiltinType;
import ch.eskaton.asn4j.compiler.il.ILMapValue;
import ch.eskaton.asn4j.compiler.il.ILParameterizedType;
import ch.eskaton.asn4j.compiler.il.ILType;
import ch.eskaton.asn4j.compiler.il.ILValue;
import ch.eskaton.asn4j.compiler.il.Variable;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.values.CollectionValue;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.commons.collections.Tuple2;
import ch.eskaton.commons.utils.StreamsUtils;

import java.util.HashSet;

import static ch.eskaton.asn4j.compiler.constraints.Constants.VAR_VALUES;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.CUSTOM;
import static java.util.Collections.singletonList;

public class CollectionValueExpressionBuilder
        extends VectorValueExpressionBuilder<CollectionValue, CollectionValueNode> {

    public CollectionValueExpressionBuilder(CompilerContext ctx) {
        super(ctx);
    }

    @Override
    protected BooleanExpression buildExpression(CompiledType compiledType, CollectionValue collectionValue) {
        var compiledBaseType = (CompiledCollectionType) ctx.getCompiledBaseType(compiledType);
        var typeStream = compiledBaseType.getComponents().stream().map(c -> c.getCompiledType().getName());
        var valueStream = collectionValue.getValues().stream();
        var associations = new HashSet<Tuple2<Expression, Expression>>();

        StreamsUtils.zip(typeStream, valueStream)
                .map(t -> new Tuple2<Expression, Expression>(ILValue.of(t.get_2().getName()),
                        ILValue.of(t.get_1(), t.get_2().getValue())))
                .forEach(associations::add);

        return new BooleanFunctionCall.MapEquals(Variable.of(VAR_VALUES), new ILMapValue(ILType.of(ILBuiltinType.STRING),
                ILParameterizedType.of(CUSTOM, singletonList(ASN1Type.class.getSimpleName())), associations));
    }

}
