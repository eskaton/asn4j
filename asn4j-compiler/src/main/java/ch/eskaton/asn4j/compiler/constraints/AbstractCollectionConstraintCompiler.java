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
import ch.eskaton.asn4j.compiler.TypeName;
import ch.eskaton.asn4j.compiler.constraints.ast.CollectionValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.optimizer.CollectionConstraintOptimizingVisitor;
import ch.eskaton.asn4j.compiler.il.BinaryBooleanExpression;
import ch.eskaton.asn4j.compiler.il.BinaryOperator;
import ch.eskaton.asn4j.compiler.il.BooleanExpression;
import ch.eskaton.asn4j.compiler.il.BooleanFunctionCall;
import ch.eskaton.asn4j.compiler.il.FunctionCall;
import ch.eskaton.asn4j.compiler.il.ILBuiltinType;
import ch.eskaton.asn4j.compiler.il.ILListValue;
import ch.eskaton.asn4j.compiler.il.ILParameterizedType;
import ch.eskaton.asn4j.compiler.il.ILType;
import ch.eskaton.asn4j.compiler.il.ILValue;
import ch.eskaton.asn4j.compiler.il.ILVisibility;
import ch.eskaton.asn4j.compiler.il.Module;
import ch.eskaton.asn4j.compiler.il.NegationExpression;
import ch.eskaton.asn4j.compiler.il.Parameter;
import ch.eskaton.asn4j.compiler.il.Variable;
import ch.eskaton.asn4j.compiler.il.builder.FunctionBuilder;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.CollectionOfType;
import ch.eskaton.asn4j.parser.ast.types.SequenceType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.values.CollectionValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.commons.utils.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.BOOLEAN;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.CUSTOM;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.Optional.of;

public abstract class AbstractCollectionConstraintCompiler extends AbstractConstraintCompiler {

    private static final String VALUE = "value";

    private static final String VALUES = "values";

    public static final String GET_VALUES = "getValues";

    public static final String GET_VALUE = "getValue";

    private static final String OBJ = "obj";

    private final TypeName typeName;

    public AbstractCollectionConstraintCompiler(CompilerContext ctx, TypeName typeName) {
        super(ctx);

        this.typeName = typeName;
    }

    @Override
    protected Node calculateElements(CompiledType baseType, Elements elements, Optional<Bounds> bounds) {
        if (elements instanceof ElementSet) {
            return compileConstraint(baseType, (ElementSet) elements, bounds);
        } else if (elements instanceof SingleValueConstraint) {
            Value value = ((SingleValueConstraint) elements).getValue();

            try {
                CollectionValue collectionValue = ctx.resolveGenericValue(CollectionValue.class,
                        baseType.getType(), value);

                return new CollectionValueNode(singleton(collectionValue));
            } catch (Exception e) {
                throw new CompilerException("Invalid single-value constraint %s for %s type", e,
                        value.getClass().getSimpleName(), typeName);
            }
        } else {
            throw new CompilerException("Invalid constraint %s for %s type",
                    elements.getClass().getSimpleName(), typeName);
        }
    }

    @Override
    public void addConstraint(Type type, Module module, ConstraintDefinition definition) {
        generateDoCheckConstraint(type, module);

        SequenceType referencedType = (SequenceType) ctx.resolveTypeReference(type);
        List<String> typeParameter = ctx.getTypeParameter(referencedType);

        FunctionBuilder builder = generateCheckConstraintValue(module,
                new Parameter(ILParameterizedType.of(ILBuiltinType.LIST, singletonList(ASN1Type.class.getSimpleName())),
                        VALUES));

        addConstraintCondition(referencedType, typeParameter, definition, builder, module);

        builder.build();
    }

    protected void generateDoCheckConstraint(Type type, Module module) {
        // @formatter:off
        module.function()
                .name("doCheckConstraint")
                .overriden(true)
                .visibility(ILVisibility.PUBLIC)
                .returnType(ILType.of(BOOLEAN))
                .statements()
                    .returnExpression(generateCheckConstraintCall(type))
                    .build()
                .build();
        // @formatter:on
    }

    protected FunctionCall generateCheckConstraintCall(Type type) {
        List<FunctionCall> getters = ((SequenceType) type).getRootComponents().stream()
                .map(c -> "get" + StringUtils.initCap(c.getNamedType().getName()))
                .map(f -> new FunctionCall(of(f)))
                .collect(Collectors.toList());

        return new FunctionCall(of("checkConstraintValue"), new ILListValue(getters));
    }

    protected void addConstraintCondition(Type type, List<String> typeParameter, ConstraintDefinition definition,
            FunctionBuilder builder, Module module) {
        String functionName = "checkConstraintValue";

        if (definition.isExtensible()) {
            builder.statements().returnValue(Boolean.TRUE);
        } else if (builder.getModule().getFunctions().stream().noneMatch(f -> f.getName().equals(functionName))) {
            addConstraintCondition(type, definition, builder);
        } else {
            Node roots = definition.getRoots();
            Optional<BooleanExpression> expression = buildExpression(module, getTypeName(type), roots);
            Type elementType = ((CollectionOfType) type).getType();
            BooleanExpression condition;

            if (elementType instanceof CollectionOfType) {
                condition = new NegationExpression(
                        new FunctionCall(of(functionName), new FunctionCall(of(GET_VALUES), of(new ILValue(OBJ)))));
            } else if (elementType instanceof BitString) {
                condition = new NegationExpression(
                        new FunctionCall(of(functionName),
                                new FunctionCall(of(GET_VALUE), of(new ILValue(OBJ))),
                                new FunctionCall(of("getUnusedBits"), of(new ILValue(OBJ)))));
            } else {
                condition = new NegationExpression(
                        new FunctionCall(of(functionName), new FunctionCall(of(GET_VALUE), of(new ILValue(OBJ)))));
            }

            if (expression.isPresent()) {
                // @formatter:off
                builder.statements()
                        .conditions()
                            .condition(expression.get())
                                .statements()
                                    .returnValue(Boolean.TRUE)
                                    .build()
                                .build()
                            .condition()
                                .statements()
                                    .returnValue(Boolean.FALSE)
                                    .build()
                                .build()
                            .build()
                        .build();
                // @formatter:on
            } else {
                // @formatter:off
                builder.statements()
                        .foreach(new ILParameterizedType(CUSTOM, typeParameter), new Variable(OBJ), new Variable(VALUE))
                            .statements()
                                .conditions()
                                    .condition(condition)
                                        .statements()
                                            .returnValue(Boolean.FALSE)
                                            .build()
                                        .build()
                                    .build()
                                .build()
                            .build()
                        .returnValue(Boolean.TRUE)
                        .build();
                // @formatter:on
            }
        }
    }

    @Override
    protected Node optimize(Node node) {
        return new CollectionConstraintOptimizingVisitor().visit(node);
    }

    @Override
    protected Optional<BooleanExpression> buildExpression(Module module, String typeName, Node node) {
        if (node == null) {
            return Optional.empty();
        }

        switch (node.getType()) {
            case VALUE:
                return getValueExpression(typeName, (CollectionValueNode) node);
            default:
                return super.buildExpression(module, typeName, node);
        }
    }

    private Optional<BooleanExpression> getValueExpression(String typeName, CollectionValueNode node) {
        Set<CollectionValue> values = node.getValue();
        List<BooleanExpression> valueArguments = values.stream()
                .map(value -> buildExpression(typeName, value))
                .collect(Collectors.toList());

        return Optional.of(new BinaryBooleanExpression(BinaryOperator.OR, valueArguments));
    }


    @Override
    protected String getTypeName(Type type) {
        if (type instanceof TypeReference) {
            type = (Type) ctx.resolveTypeReference(type);
        }

        Type elementType;

        if (type instanceof CollectionOfType) {
            elementType = ((CollectionOfType) type).getType();
        } else {
            elementType = type;
        }

        return super.getTypeName(elementType);
    }

    private BooleanExpression buildExpression(String typeName, CollectionValue value) {
        var values = value.getValues().stream().map(namedValue -> new ILValue(namedValue.getValue()))
                .collect(Collectors.toList());

        // return new BooleanFunctionCall.SetEquals(new Variable(VALUES), new ILListValue(values));
        return new BooleanFunctionCall.SetEquals(new Variable(VALUES), new ILValue(typeName, value));
    }

}