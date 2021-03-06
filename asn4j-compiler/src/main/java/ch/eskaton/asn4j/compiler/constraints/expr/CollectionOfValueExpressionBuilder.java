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
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.constraints.ast.CollectionOfValueNode;
import ch.eskaton.asn4j.compiler.il.BooleanExpression;
import ch.eskaton.asn4j.compiler.il.BooleanFunctionCall;
import ch.eskaton.asn4j.compiler.il.ILListValue;
import ch.eskaton.asn4j.compiler.il.ILValue;
import ch.eskaton.asn4j.compiler.il.Variable;
import ch.eskaton.asn4j.compiler.results.CompiledBuiltinType;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionOfType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.values.CollectionOfValue;

import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.constraints.Constants.VAR_VALUE;

public class CollectionOfValueExpressionBuilder
        extends VectorValueExpressionBuilder<CollectionOfValue, CollectionOfValueNode> {

    public CollectionOfValueExpressionBuilder(CompilerContext ctx) {
        super(ctx);
    }

    @Override
    protected BooleanExpression buildExpression(CompiledType compiledType, CollectionOfValue collectionOfValue) {
        var values = collectionOfValue.getValues().stream()
                .map(value -> new ILValue(getTypeName(compiledType), value))
                .collect(Collectors.toList());

        return new BooleanFunctionCall.SetEquals(new Variable(VAR_VALUE), new ILListValue(values));
    }

    @Override
    protected String getTypeName(CompiledType compiledType) {
        var type = compiledType.getType();

        if (type instanceof TypeReference) {
            compiledType = ctx.getCompiledBaseType(compiledType);
        }

        if (compiledType instanceof CompiledCollectionOfType compiledCollectionOfType) {
            var compiledContentType = compiledCollectionOfType.getContentType();

            if (!(compiledContentType instanceof CompiledBuiltinType)) {
                return compiledContentType.getName();
            }

            return super.getTypeName(compiledContentType);
        }

        throw new CompilerException(type.getPosition(), "Unexpected type: %s", type);
    }

}
