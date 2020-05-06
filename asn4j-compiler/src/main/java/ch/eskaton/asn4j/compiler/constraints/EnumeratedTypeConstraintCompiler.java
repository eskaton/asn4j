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
import ch.eskaton.asn4j.compiler.CompilerUtils;
import ch.eskaton.asn4j.compiler.TypeName;
import ch.eskaton.asn4j.compiler.constraints.ast.EnumeratedValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.optimizer.EnumeratedTypeConstraintOptimizingVisitor;
import ch.eskaton.asn4j.compiler.il.BinaryBooleanExpression;
import ch.eskaton.asn4j.compiler.il.BinaryOperator;
import ch.eskaton.asn4j.compiler.il.BooleanExpression;
import ch.eskaton.asn4j.compiler.il.ILType;
import ch.eskaton.asn4j.compiler.il.ILValue;
import ch.eskaton.asn4j.compiler.il.Module;
import ch.eskaton.asn4j.compiler.il.Parameter;
import ch.eskaton.asn4j.compiler.il.Variable;
import ch.eskaton.asn4j.compiler.il.builder.FunctionBuilder;
import ch.eskaton.asn4j.compiler.results.CompiledEnumeratedType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.compiler.results.EnumerationItems;
import ch.eskaton.asn4j.parser.ast.constraints.ContainedSubtype;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SizeConstraint;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.commons.collections.Sets;
import ch.eskaton.commons.collections.Tuple2;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.INTEGER;

public class EnumeratedTypeConstraintCompiler extends AbstractConstraintCompiler {

    public EnumeratedTypeConstraintCompiler(CompilerContext ctx) {
        super(ctx);
    }

    @Override
    protected Node calculateElements(CompiledType baseType, Elements elements, Optional<Bounds> bounds) {
        if (elements instanceof ElementSet) {
            return compileConstraint(baseType, (ElementSet) elements, bounds);
        } else if (elements instanceof SingleValueConstraint) {
            Value value = ((SingleValueConstraint) elements).getValue();

            try {
                SimpleDefinedValue definedValue = CompilerUtils.resolveAmbiguousValue(value, SimpleDefinedValue.class);
                Integer enumValue;

                if (definedValue != null) {
                    CompiledEnumeratedType compiledEnumeratedType = (CompiledEnumeratedType) baseType;
                    EnumerationItems allItems = compiledEnumeratedType.getRoots().copy()
                            .addAll(compiledEnumeratedType.getAdditions().getItems());
                    Optional<Tuple2<String, Integer>> enumItem = allItems.getItems().stream()
                            .filter(t -> t.get_1().equals(definedValue.getValue())).findAny();

                    if (enumItem.isPresent()) {
                        enumValue = enumItem.get().get_2();
                    } else {
                        throw new CompilerException("Failed to resolve enum value: " + definedValue.getValue());
                    }
                } else {
                    throw new CompilerException("Failed to resolve value: ", value.getClass().getSimpleName());
                }

                return new EnumeratedValueNode(Sets.<Integer>builder().add(enumValue).build());
            } catch (Exception e) {
                throw new CompilerException("Invalid single-value constraint %s for %s type", e,
                        value.getClass().getSimpleName(), TypeName.ENUMERATED);
            }
        } else if (elements instanceof ContainedSubtype) {
            Type type = ((ContainedSubtype) elements).getType();
            CompiledType compiledType = ctx.getCompiledBaseType(type);

            if (!baseType.getType().equals(compiledType.getType())) {
                throw new CompilerException("Invalid type in contained subtype constraint: " + type);
            }

            return calculateContainedSubtype(type);
        } else if (elements instanceof SizeConstraint) {
            return calculateSize(baseType, ((SizeConstraint) elements).getConstraint(), bounds);
        } else {
            throw new CompilerException("Invalid constraint %s for %s type",
                    elements.getClass().getSimpleName(), TypeName.ENUMERATED);
        }
    }

    @Override
    public void addConstraint(Type type, Module module, ConstraintDefinition definition) {
        generateDoCheckConstraint(module);

        FunctionBuilder builder = generateCheckConstraintValue(module, new Parameter(ILType.of(INTEGER), "value"));

        addConstraintCondition(type, definition, builder);

        builder.build();
    }

    @Override
    protected Node optimize(Node node) {
        return new EnumeratedTypeConstraintOptimizingVisitor().visit(node);
    }

    @Override
    protected Optional<BooleanExpression> buildExpression(Module module, Type type, Node node) {
        switch (node.getType()) {
            case VALUE:
                Set<Integer> values = ((EnumeratedValueNode) node).getValue();
                List<BinaryBooleanExpression> arguments = values.stream().map(this::buildExpression)
                        .collect(Collectors.toList());

                return Optional.of(new BinaryBooleanExpression(BinaryOperator.OR, arguments));
            default:
                return super.buildExpression(module, type, node);
        }
    }

    private BinaryBooleanExpression buildExpression(Integer enumValue) {
        return new BinaryBooleanExpression(BinaryOperator.EQ, new Variable("value"), new ILValue(enumValue));
    }

}
