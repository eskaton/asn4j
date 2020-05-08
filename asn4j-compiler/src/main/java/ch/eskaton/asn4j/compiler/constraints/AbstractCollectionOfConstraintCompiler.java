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
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.compiler.TypeName;
import ch.eskaton.asn4j.compiler.constraints.ast.CollectionOfValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.IntegerRange;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.SizeNode;
import ch.eskaton.asn4j.compiler.constraints.ast.WithComponentNode;
import ch.eskaton.asn4j.compiler.constraints.optimizer.CollectionOfConstraintOptimizingVisitor;
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
import ch.eskaton.asn4j.compiler.il.Module;
import ch.eskaton.asn4j.compiler.il.NegationExpression;
import ch.eskaton.asn4j.compiler.il.Parameter;
import ch.eskaton.asn4j.compiler.il.Variable;
import ch.eskaton.asn4j.compiler.il.builder.FunctionBuilder;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.constraints.ContainedSubtype;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.constraints.SingleTypeConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SizeConstraint;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.CollectionOfType;
import ch.eskaton.asn4j.parser.ast.types.SetOfType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.values.CollectionOfValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.types.ASN1BitString;
import ch.eskaton.asn4j.runtime.types.ASN1Boolean;
import ch.eskaton.asn4j.runtime.types.ASN1EnumeratedType;
import ch.eskaton.asn4j.runtime.types.ASN1IRI;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1Null;
import ch.eskaton.asn4j.runtime.types.ASN1ObjectIdentifier;
import ch.eskaton.asn4j.runtime.types.ASN1OctetString;
import ch.eskaton.asn4j.runtime.types.ASN1RelativeIRI;
import ch.eskaton.asn4j.runtime.types.ASN1RelativeOID;
import ch.eskaton.asn4j.runtime.types.ASN1SequenceOf;
import ch.eskaton.asn4j.runtime.types.ASN1SetOf;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.BIG_INTEGER;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.BOOLEAN;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.BYTE_ARRAY;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.CUSTOM;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.INTEGER;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.INTEGER_ARRAY;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.NULL;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.STRING_ARRAY;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.Optional.of;

public abstract class AbstractCollectionOfConstraintCompiler extends AbstractConstraintCompiler {

    private static final String VALUE = "value";

    private static final String VALUES = "values";

    public static final String GET_VALUES = "getValues";

    public static final String GET_VALUE = "getValue";

    private static final String OBJ = "obj";

    private final TypeName typeName;

    private final ILBuiltinType collectionType;

    public AbstractCollectionOfConstraintCompiler(CompilerContext ctx, TypeName typeName, ILBuiltinType collectionType) {
        super(ctx);

        this.typeName = typeName;
        this.collectionType = collectionType;
    }

    @Override
    ConstraintDefinition compileConstraints(Type node, CompiledType baseType) {
        ConstraintDefinition constraintDefinition = super.compileConstraints(node, baseType);
        CollectionOfType collectionOfType = (CollectionOfType) baseType.getType();

        if (collectionOfType.hasElementConstraint()) {
            if (constraintDefinition == null) {
                constraintDefinition = new ConstraintDefinition();
            }

            ConstraintDefinition componentDefinition = ctx.compileConstraint(collectionOfType.getType());

            if (componentDefinition.getRoots() != null) {
                componentDefinition
                        .setRoots(new WithComponentNode(collectionOfType.getType(), componentDefinition.getRoots()));
            }

            if (componentDefinition.getExtensions() != null) {
                componentDefinition.setExtensions(
                        new WithComponentNode(collectionOfType.getType(), componentDefinition.getRoots()));
            }

            constraintDefinition = constraintDefinition.serialApplication(componentDefinition);
        }

        return constraintDefinition;
    }

    @Override
    protected Node calculateElements(CompiledType baseType, Elements elements, Optional<Bounds> bounds) {
        if (elements instanceof ElementSet) {
            return compileConstraint(baseType, (ElementSet) elements, bounds);
        } else if (elements instanceof SingleValueConstraint) {
            return calculateSingleValueConstraint(baseType, (SingleValueConstraint) elements);
        } else if (elements instanceof ContainedSubtype) {
            return calculateContainedSubtype(baseType, ((ContainedSubtype) elements).getType());
        } else if (elements instanceof SizeConstraint) {
            return calculateSize(baseType, ((SizeConstraint) elements).getConstraint(), bounds);
        } else if (elements instanceof SingleTypeConstraint) {
            return calculateSingleTypeConstraint(baseType, (SingleTypeConstraint) elements);
        } else {
            throw new CompilerException("Invalid constraint %s for %s type",
                    elements.getClass().getSimpleName(), typeName);
        }
    }

    private Node calculateSingleTypeConstraint(CompiledType baseType, SingleTypeConstraint elements) {
        Type componentType = ((CollectionOfType) baseType.getType()).getType();

        ConstraintDefinition definition = ctx.compileConstraint(componentType);

        if (definition != null) {
            definition = definition.serialApplication(
                    ctx.compileConstraint(componentType, elements.getConstraint()));
        } else {
            definition = ctx.compileConstraint(componentType, elements.getConstraint());
        }

        return new WithComponentNode(componentType, definition.getRoots());
    }

    private Node calculateSingleValueConstraint(CompiledType baseType, SingleValueConstraint elements) {
        Value value = elements.getValue();

        try {
            CollectionOfValue collectionOfValue = ctx.resolveGenericValue(CollectionOfValue.class,
                    baseType.getType(), value);

            return new CollectionOfValueNode(singleton(collectionOfValue));
        } catch (Exception e) {
            throw new CompilerException("Invalid single-value constraint %s for %s type", e,
                    value.getClass().getSimpleName(), typeName);
        }
    }

    @Override
    protected boolean isAssignable(CompiledType compiledType, CompiledType compiledParentType) {
        if (!compiledType.getType().getClass().isAssignableFrom(compiledParentType.getType().getClass())) {
            return false;
        } else if (!compiledParentType.getType().getClass().isAssignableFrom(compiledParentType.getType().getClass())) {
            return false;
        }

        return true;
    }

    @Override
    public void addConstraint(Type type, Module module, ConstraintDefinition definition) {
        generateDoCheckConstraint(module);

        CollectionOfType referencedType = (CollectionOfType) ctx.resolveTypeReference(type);
        List<String> typeParameter = ctx.getTypeParameter(referencedType);

        FunctionBuilder builder = generateCheckConstraintValue(module,
                new Parameter(ILParameterizedType.of(collectionType, typeParameter), VALUE));

        addConstraintCondition(referencedType, typeParameter, definition, builder, module);

        builder.build();
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
            Optional<BooleanExpression> expression = buildExpression(module, type, roots);
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
    protected FunctionCall generateCheckConstraintCall() {
        return new FunctionCall(of("checkConstraintValue"), new FunctionCall(of(GET_VALUES)));
    }

    @Override
    protected Node optimize(Node node) {
        return new CollectionOfConstraintOptimizingVisitor().visit(node);
    }

    @Override
    protected Optional<BooleanExpression> buildExpression(Module module, Type type, Node node) {
        if (node == null) {
            return Optional.empty();
        }

        switch (node.getType()) {
            case VALUE:
                return getValueExpression(type, (CollectionOfValueNode) node);
            case SIZE:
                return getSizeExpression((SizeNode) node);
            case WITH_COMPONENT:
                return getWithComponentExpression(module, (WithComponentNode) node);
            default:
                return super.buildExpression(module, type, node);
        }
    }

    private Optional<BooleanExpression> getValueExpression(Type type, CollectionOfValueNode node) {
        Set<CollectionOfValue> values = node.getValue();
        List<BooleanExpression> valueArguments = values.stream()
                .map(value -> buildExpression(type, value))
                .collect(Collectors.toList());

        return Optional.of(new BinaryBooleanExpression(BinaryOperator.OR, valueArguments));
    }

    private Optional<BooleanExpression> getSizeExpression(SizeNode node) {
        List<IntegerRange> sizes = node.getSize();
        List<BooleanExpression> sizeArguments = sizes.stream().map(this::buildSizeExpression)
                .collect(Collectors.toList());

        return Optional.of(new BinaryBooleanExpression(BinaryOperator.OR, sizeArguments));
    }

    private Optional<BooleanExpression> getWithComponentExpression(Module module, WithComponentNode node) {
        WithComponentNode componentNode = node;
        Optional<BooleanExpression> expression = ctx
                .buildExpression(module, componentNode.getComponentType(), componentNode.getConstraint());

        String expressionSym = module.generateSymbol("_expression");

        List<String> parameterizedType = ctx.getParameterizedType(componentNode.getComponentType());
        List<String> typeParameters = parameterizedType.stream().skip(1).collect(Collectors.toList());
        List<Parameter> parameters = getParameters(parameterizedType, typeParameters);

        // @formatter:off
        module.function()
                .returnType(ILType.of(BOOLEAN))
                .name(expressionSym)
                .parameters(parameters)
                .statements()
                    .returnExpression(expression.orElseThrow(() -> new IllegalCompilerStateException("Expected expression in %s")))
                    .build()
                .build();
        // @formatter:on

        String checkSym = module.generateSymbol("_checkConstraint");
        BooleanFunctionCall functionCall = getExprFunctionCall(Optional.of(expressionSym), parameterizedType,
                typeParameters);

        // @formatter:off
        module.function()
                .returnType(ILType.of(BOOLEAN))
                .name(checkSym)
                .parameter(new Parameter(new ILParameterizedType(ILBuiltinType.LIST, parameterizedType), VALUES))
                .statements()
                    .foreach(new ILParameterizedType(CUSTOM, parameterizedType), new Variable(VALUE), new Variable(VALUES))
                        .statements()
                            .conditions()
                                .condition(new NegationExpression(functionCall))
                                    .statements()
                                        .returnValue(Boolean.FALSE)
                                        .build()
                                    .build()
                                .build()
                            .build()
                        .build()
                    .returnValue(Boolean.TRUE)
                    .build()
                .build();
        // @formatter:on

        return Optional.of(new BooleanFunctionCall(Optional.of(checkSym), new Variable(VALUES)));
    }

    private BooleanFunctionCall getExprFunctionCall(Optional<String> expressionSym, List<String> parameterizedType,
            List<String> typeParameters) {
        if (typeParameters.isEmpty()) {
            String runtimeType = ctx.getRuntimeType(parameterizedType.get(0));

            if (runtimeType.equals(ASN1BitString.class.getSimpleName())) {
                return new BooleanFunctionCall(expressionSym, getGetValueCall(),
                        new FunctionCall(Optional.of("getUnusedBits"), Optional.of(new Variable(VALUE))));
            } else if (runtimeType.equals(ASN1ObjectIdentifier.class.getSimpleName()) ||
                    runtimeType.equals(ASN1RelativeOID.class.getSimpleName())) {
                return new BooleanFunctionCall(expressionSym,
                        new FunctionCall.ToArray(ILType.of(INTEGER), getGetValueCall()));
            } else if (runtimeType.equals(ASN1IRI.class.getSimpleName()) ||
                    runtimeType.equals(ASN1RelativeIRI.class.getSimpleName())) {
                return new BooleanFunctionCall(expressionSym,
                        new FunctionCall.ToArray(ILType.of(ILBuiltinType.STRING), getGetValueCall()));
            } else if (runtimeType.equals(ASN1SequenceOf.class.getSimpleName()) ||
                    runtimeType.equals(ASN1SetOf.class.getSimpleName())) {
                return new BooleanFunctionCall(expressionSym, getGetValuesCall());
            } else {
                return new BooleanFunctionCall(expressionSym, getGetValueCall());
            }
        }

        return new BooleanFunctionCall(expressionSym,
                new FunctionCall(Optional.of(GET_VALUES), Optional.of(new Variable(VALUE))));
    }

    private FunctionCall getGetValueCall() {
        return new FunctionCall(Optional.of(GET_VALUE), Optional.of(new Variable(VALUE)));
    }


    private FunctionCall getGetValuesCall() {
        return new FunctionCall(Optional.of(GET_VALUES), Optional.of(new Variable(VALUE)));
    }

    private List<Parameter> getParameters(List<String> parameterizedType, List<String> typeParameters) {
        List<Parameter> parameters;

        if (typeParameters.isEmpty()) {
            String runtimeType = ctx.getRuntimeType(parameterizedType.get(0));

            if (runtimeType.equals(ASN1Integer.class.getSimpleName())) {
                parameters = getParameter(BIG_INTEGER);
            } else if (runtimeType.equals(ASN1Boolean.class.getSimpleName())) {
                parameters = getParameter(BOOLEAN);
            } else if (runtimeType.equals(ASN1EnumeratedType.class.getSimpleName())) {
                parameters = getParameter(INTEGER);
            } else if (runtimeType.equals(ASN1Null.class.getSimpleName())) {
                parameters = getParameter(NULL);
            } else if (runtimeType.equals(ASN1ObjectIdentifier.class.getSimpleName()) ||
                    runtimeType.equals(ASN1RelativeOID.class.getSimpleName())) {
                parameters = getParameter(INTEGER_ARRAY);
            } else if (runtimeType.equals(ASN1IRI.class.getSimpleName()) ||
                    runtimeType.equals(ASN1RelativeIRI.class.getSimpleName())) {
                parameters = getParameter(STRING_ARRAY);
            } else if (runtimeType.equals(ASN1BitString.class.getSimpleName())) {
                parameters = asList(new Parameter(ILType.of(BYTE_ARRAY), VALUE),
                        new Parameter(ILType.of(INTEGER), "unusedBits"));
            } else if (runtimeType.equals(ASN1OctetString.class.getSimpleName())) {
                parameters = getParameter(BYTE_ARRAY);
            } else if (runtimeType.equals(ASN1SequenceOf.class.getSimpleName()) ||
                    runtimeType.equals(ASN1SetOf.class.getSimpleName())) {
                List<String> typeParameter = ctx.getParameterizedType(ctx.getBase(parameterizedType.get(0))).stream()
                        .skip(1).collect(Collectors.toList());

                parameters = singletonList(
                        new Parameter(new ILParameterizedType(ILBuiltinType.LIST, typeParameter), VALUES));
            } else {
                throw new CompilerException("Unsupported runtimeType %s", runtimeType);
            }
        } else {
            parameters = singletonList(
                    new Parameter(new ILParameterizedType(ILBuiltinType.LIST, typeParameters), VALUES));
        }

        return parameters;
    }

    private List<Parameter> getParameter(ILBuiltinType builtinType) {
        return singletonList(new Parameter(ILType.of(builtinType), VALUE));
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

    private BooleanExpression buildExpression(Type type, CollectionOfValue collectionOfValue) {
        var values = collectionOfValue.getValues().stream().map(value -> new ILValue(getTypeName(type), value)).collect(Collectors.toList());

        return new BooleanFunctionCall.SetEquals(new Variable(VALUE), new ILListValue(values));
    }

    private BinaryBooleanExpression buildSizeExpression(IntegerRange range) {
        long lower = range.getLower();
        long upper = range.getUpper();

        if (lower == upper) {
            return buildSizeExpression(lower, BinaryOperator.EQ);
        } else if (lower == Long.MIN_VALUE) {
            return buildSizeExpression(upper, BinaryOperator.LE);
        } else if (upper == Long.MAX_VALUE) {
            return buildSizeExpression(lower, BinaryOperator.GE);
        } else {
            BinaryBooleanExpression expr1 = buildSizeExpression(lower, BinaryOperator.GE);
            BinaryBooleanExpression expr2 = buildSizeExpression(upper, BinaryOperator.LE);

            return new BinaryBooleanExpression(BinaryOperator.AND, expr1, expr2);
        }
    }

    private BinaryBooleanExpression buildSizeExpression(long value, BinaryOperator operator) {
        return new BinaryBooleanExpression(operator, new FunctionCall.GetSize(new Variable(VALUES)),
                new ILValue(value));
    }

}
