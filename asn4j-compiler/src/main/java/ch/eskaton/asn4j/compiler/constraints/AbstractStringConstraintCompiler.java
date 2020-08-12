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
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.PermittedAlphabetNode;
import ch.eskaton.asn4j.compiler.constraints.ast.SizeNode;
import ch.eskaton.asn4j.compiler.constraints.ast.StringValueNode;
import ch.eskaton.asn4j.compiler.constraints.elements.ContainedSubtypeCompiler;
import ch.eskaton.asn4j.compiler.constraints.elements.SizeCompiler;
import ch.eskaton.asn4j.compiler.constraints.elements.StringPermittedAlphabetConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.elements.StringSingleValueCompiler;
import ch.eskaton.asn4j.compiler.constraints.expr.StringPermittedAlphabetExpressionBuilder;
import ch.eskaton.asn4j.compiler.constraints.expr.StringSizeExpressionBuilder;
import ch.eskaton.asn4j.compiler.constraints.expr.StringValueExpressionBuilder;
import ch.eskaton.asn4j.compiler.il.BooleanExpression;
import ch.eskaton.asn4j.compiler.il.ILType;
import ch.eskaton.asn4j.compiler.il.Module;
import ch.eskaton.asn4j.compiler.il.Parameter;
import ch.eskaton.asn4j.compiler.il.builder.FunctionBuilder;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.constraints.ContainedSubtype;
import ch.eskaton.asn4j.parser.ast.constraints.PermittedAlphabetConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SizeConstraint;
import ch.eskaton.asn4j.parser.ast.values.HasStringValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.util.Optional;

import static ch.eskaton.asn4j.compiler.constraints.Constants.VAR_VALUE;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.STRING;

public class AbstractStringConstraintCompiler<V extends HasStringValue & Value> extends AbstractConstraintCompiler {

    public AbstractStringConstraintCompiler(CompilerContext ctx, Class<V> valueClass) {
        super(ctx);

        addConstraintHandler(ContainedSubtype.class, new ContainedSubtypeCompiler(ctx)::compile);
        addConstraintHandler(SizeConstraint.class,
                new SizeCompiler(ctx, new IntegerConstraintCompiler(ctx).getDispatcher())::compile);
        addConstraintHandler(SingleValueConstraint.class,
                new StringSingleValueCompiler<>(ctx, valueClass, getTypeName())::compile);
        addConstraintHandler(PermittedAlphabetConstraint.class,
                new StringPermittedAlphabetConstraintCompiler<>(ctx, valueClass, getTypeName())::compile);
    }

    @Override
    protected TypeName getTypeName() {
        return TypeName.VISIBLE_STRING;
    }

    @Override
    protected void addConstraint(CompiledType type, Module module, ConstraintDefinition definition) {
        generateDoCheckConstraint(module);

        FunctionBuilder builder = generateCheckConstraintValue(module, new Parameter(ILType.of(STRING), VAR_VALUE));

        addConstraintCondition(type, definition, builder);

        builder.build();
    }

    @Override
    protected Optional<BooleanExpression> buildExpression(Module module, CompiledType compiledType, Node node) {
        return switch (node.getType()) {
            case VALUE -> new StringValueExpressionBuilder(ctx).build(compiledType, (StringValueNode) node);
            case SIZE -> new StringSizeExpressionBuilder().build(((SizeNode) node).getSize());
            case PERMITTED_ALPHABET -> new StringPermittedAlphabetExpressionBuilder()
                    .build(module, ((PermittedAlphabetNode) node).getNode());
            default -> super.buildExpression(module, compiledType, node);
        };
    }

}
