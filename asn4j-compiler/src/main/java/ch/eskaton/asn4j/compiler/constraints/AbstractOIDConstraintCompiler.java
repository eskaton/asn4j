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
import ch.eskaton.asn4j.compiler.constraints.ast.AbstractOIDValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.elements.ContainedSubtypeCompiler;
import ch.eskaton.asn4j.compiler.il.BinaryBooleanExpression;
import ch.eskaton.asn4j.compiler.il.BinaryOperator;
import ch.eskaton.asn4j.compiler.il.BooleanExpression;
import ch.eskaton.asn4j.compiler.il.BooleanFunctionCall;
import ch.eskaton.asn4j.compiler.il.FunctionCall;
import ch.eskaton.asn4j.compiler.il.ILBuiltinType;
import ch.eskaton.asn4j.compiler.il.ILType;
import ch.eskaton.asn4j.compiler.il.ILValue;
import ch.eskaton.asn4j.compiler.il.Module;
import ch.eskaton.asn4j.compiler.il.Parameter;
import ch.eskaton.asn4j.compiler.il.Variable;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.constraints.ContainedSubtype;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.constraints.Constants.FUNC_CHECK_CONSTRAINT_VALUE;
import static ch.eskaton.asn4j.compiler.constraints.Constants.GET_VALUE;
import static ch.eskaton.asn4j.compiler.constraints.Constants.VAR_VALUE;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.INTEGER_ARRAY;
import static java.util.Optional.of;

public abstract class AbstractOIDConstraintCompiler<N extends AbstractOIDValueNode>
        extends AbstractConstraintCompiler {

    public AbstractOIDConstraintCompiler(CompilerContext ctx) {
        super(ctx);

        addConstraintHandler(ContainedSubtype.class, new ContainedSubtypeCompiler(ctx)::compile);
    }

    @Override
    public void addConstraint(CompiledType type, Module module, ConstraintDefinition definition) {
        generateDoCheckConstraint(module);

        var builder = generateCheckConstraintValue(module, new Parameter(ILType.of(INTEGER_ARRAY), VAR_VALUE));

        addConstraintCondition(type, definition, builder);

        builder.build();
    }

    @Override
    protected FunctionCall generateCheckConstraintCall() {
        return new FunctionCall(of(FUNC_CHECK_CONSTRAINT_VALUE),
                new FunctionCall.ToArray(ILType.of(ILBuiltinType.INTEGER), new FunctionCall(of(GET_VALUE))));
    }

    @Override
    protected Optional<BooleanExpression> buildExpression(Module module, CompiledType compiledType, Node node) {
        return switch (node.getType()) {
            case VALUE -> getValueExpression((N) node);
            default -> super.buildExpression(module, compiledType, node);
        };
    }

    private Optional<BooleanExpression> getValueExpression(N node) {
        List<BooleanExpression> arguments = (node.getValue()).stream()
                .map(this::buildExpression)
                .collect(Collectors.toList());

        return Optional.of(new BinaryBooleanExpression(BinaryOperator.OR, arguments));
    }

    protected BooleanExpression buildExpression(List<Integer> value) {
        return new BooleanFunctionCall.ArrayEquals(new Variable(VAR_VALUE),
                new ILValue(value.stream().mapToInt(Integer::intValue).toArray()));
    }

}
