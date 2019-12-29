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
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.constraints.ast.AbstractOIDValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
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
import ch.eskaton.asn4j.compiler.il.builder.FunctionBuilder;
import ch.eskaton.asn4j.compiler.resolvers.AbstractOIDValueResolver;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.constraints.ContainedSubtype;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.AbstractOIDValue;
import ch.eskaton.asn4j.parser.ast.values.Value;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.INTEGER_ARRAY;
import static java.util.Collections.singleton;
import static java.util.Optional.of;

public abstract class AbstractOIDConstraintCompiler<N extends AbstractOIDValueNode>
        extends AbstractConstraintCompiler {

    public AbstractOIDConstraintCompiler(CompilerContext ctx) {
        super(ctx);
    }

    @Override
    Optional<Bounds> getBounds(Optional<ConstraintDefinition> constraint) {
        return Optional.empty();
    }

    protected Node calculateElements(CompiledType baseType, Elements elements, Optional<Bounds> bounds) {
        if (elements instanceof ElementSet) {
            return compileConstraint(baseType, (ElementSet) elements, bounds);
        } else if (elements instanceof SingleValueConstraint) {
            AbstractOIDValueResolver resolver = getValueResolver();
            Value value = ((SingleValueConstraint) elements).getValue();
            AbstractOIDValue oidValue = (AbstractOIDValue) resolver.resolveValue(ctx, value, getValueClass());

            if (oidValue != null) {
                return createNode(singleton(resolver.resolveComponents(ctx, oidValue)));
            } else {
                throw new CompilerException("Invalid single-value constraint %s for " + getTypeName() + " type",
                        value.getClass().getSimpleName());
            }
        } else if (elements instanceof ContainedSubtype) {
            return calculateContainedSubtype(((ContainedSubtype) elements).getType());
        } else {
            throw new CompilerException("Invalid constraint %s for " + getTypeName() + " type",
                    elements.getClass().getSimpleName());
        }
    }

    @Override
    public void addConstraint(Type type, Module module, ConstraintDefinition definition, int level) {
        generateDoCheckConstraint(module, level);

        FunctionBuilder builder = generateCheckConstraintValue(module, level,
                new Parameter(ILType.of(INTEGER_ARRAY), "value"));

        addConstraintCondition(type, definition, builder);

        builder.build();
    }

    @Override
    protected FunctionCall generateCheckConstraintCall(int level) {
        return new FunctionCall(of("checkConstraintValue_" + level),
                new FunctionCall.ToArray(ILType.of(ILBuiltinType.INTEGER), new FunctionCall(of("getValue"))));
    }

    @Override
    protected Optional<BooleanExpression> buildExpression(String typeName, Node node) {
        switch (node.getType()) {
            case VALUE:
                List<BooleanExpression> arguments = (((N) node).getValue()).stream()
                        .map(this::buildExpression2)
                        .collect(Collectors.toList());

                return Optional.of(new BinaryBooleanExpression(BinaryOperator.OR, arguments));
            default:
                return super.buildExpression(typeName, node);
        }
    }

    protected BooleanExpression buildExpression2(List<Integer> value) {
        return new BooleanFunctionCall.ArrayEquals(new Variable("value"),
                new ILValue(value.stream().mapToInt(Integer::intValue).toArray()));
    }

    protected abstract N createNode(Set<List<Integer>> value);

    protected abstract Class<? extends AbstractOIDValue> getValueClass();

    protected abstract AbstractOIDValueResolver getValueResolver();

    protected abstract String getTypeName();

}
