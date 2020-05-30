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
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.compiler.constraints.ast.CollectionOfValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.SizeNode;
import ch.eskaton.asn4j.compiler.constraints.ast.WithComponentNode;
import ch.eskaton.asn4j.compiler.constraints.elements.ContainedSubtypeCompiler;
import ch.eskaton.asn4j.compiler.constraints.elements.SingleTypeConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.elements.SingleValueCompiler;
import ch.eskaton.asn4j.compiler.constraints.elements.SizeCompiler;
import ch.eskaton.asn4j.compiler.constraints.expr.CollectionOfSizeExpressionBuilder;
import ch.eskaton.asn4j.compiler.constraints.optimizer.CollectionOfConstraintOptimizingVisitor;
import ch.eskaton.asn4j.compiler.il.BinaryBooleanExpression;
import ch.eskaton.asn4j.compiler.il.BinaryOperator;
import ch.eskaton.asn4j.compiler.il.BooleanExpression;
import ch.eskaton.asn4j.compiler.il.BooleanFunctionCall;
import ch.eskaton.asn4j.compiler.il.Expression;
import ch.eskaton.asn4j.compiler.il.FunctionCall;
import ch.eskaton.asn4j.compiler.il.ILBuiltinType;
import ch.eskaton.asn4j.compiler.il.ILListValue;
import ch.eskaton.asn4j.compiler.il.ILMapValue;
import ch.eskaton.asn4j.compiler.il.ILParameterizedType;
import ch.eskaton.asn4j.compiler.il.ILType;
import ch.eskaton.asn4j.compiler.il.ILValue;
import ch.eskaton.asn4j.compiler.il.Module;
import ch.eskaton.asn4j.compiler.il.NegationExpression;
import ch.eskaton.asn4j.compiler.il.Parameter;
import ch.eskaton.asn4j.compiler.il.Variable;
import ch.eskaton.asn4j.compiler.il.builder.FunctionBuilder;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionOfType;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.constraints.ContainedSubtype;
import ch.eskaton.asn4j.parser.ast.constraints.SingleTypeConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SizeConstraint;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.CollectionOfType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.values.CollectionOfValue;
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
import ch.eskaton.asn4j.runtime.types.ASN1Sequence;
import ch.eskaton.asn4j.runtime.types.ASN1SequenceOf;
import ch.eskaton.asn4j.runtime.types.ASN1Set;
import ch.eskaton.asn4j.runtime.types.ASN1SetOf;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.asn4j.runtime.types.TypeName;
import ch.eskaton.commons.collections.Tuple2;
import ch.eskaton.commons.utils.Dispatcher;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.constraints.Constants.FUNC_CHECK_CONSTRAINT;
import static ch.eskaton.asn4j.compiler.constraints.Constants.FUNC_CHECK_CONSTRAINT_VALUE;
import static ch.eskaton.asn4j.compiler.constraints.Constants.FUNC_EXPRESSION;
import static ch.eskaton.asn4j.compiler.constraints.Constants.GET_UNUSED_BITS;
import static ch.eskaton.asn4j.compiler.constraints.Constants.GET_VALUE;
import static ch.eskaton.asn4j.compiler.constraints.Constants.GET_VALUES;
import static ch.eskaton.asn4j.compiler.constraints.Constants.VAR_OBJ;
import static ch.eskaton.asn4j.compiler.constraints.Constants.VAR_UNUSED_BITS;
import static ch.eskaton.asn4j.compiler.constraints.Constants.VAR_VALUE;
import static ch.eskaton.asn4j.compiler.constraints.Constants.VAR_VALUES;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintUtils.getMapParameter;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintUtils.getValueParameter;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.BIG_INTEGER;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.BOOLEAN;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.BYTE_ARRAY;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.CUSTOM;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.INTEGER;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.INTEGER_ARRAY;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.NULL;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.STRING_ARRAY;
import static ch.eskaton.commons.utils.StringUtils.initCap;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Optional.of;

public abstract class AbstractCollectionOfConstraintCompiler extends AbstractConstraintCompiler {

    private final TypeName typeName;

    private final ILBuiltinType collectionType;

    private final Dispatcher<String, Class<? extends ASN1Type>, CompiledType, List<Parameter>> parameterDefinitionDispatcher =
            new Dispatcher<String, Class<? extends ASN1Type>, CompiledType, List<Parameter>>()
                    .withComparator((t, u) -> t.equals(u.getSimpleName()))
                    .withCase(ASN1Integer.class, () -> getValueParameter(BIG_INTEGER))
                    .withCase(ASN1Boolean.class, () -> getValueParameter(BOOLEAN))
                    .withCase(ASN1EnumeratedType.class, () -> getValueParameter(INTEGER))
                    .withCase(ASN1Null.class, () -> getValueParameter(NULL))
                    .withCase(ASN1ObjectIdentifier.class, () -> getValueParameter(INTEGER_ARRAY))
                    .withCase(ASN1RelativeOID.class, () -> getValueParameter(INTEGER_ARRAY))
                    .withCase(ASN1IRI.class, () -> getValueParameter(STRING_ARRAY))
                    .withCase(ASN1RelativeIRI.class, () -> getValueParameter(STRING_ARRAY))
                    .withCase(ASN1BitString.class, () -> asList(new Parameter(ILType.of(BYTE_ARRAY), VAR_VALUE),
                            new Parameter(ILType.of(INTEGER), VAR_UNUSED_BITS)))
                    .withCase(ASN1OctetString.class, () -> getValueParameter(BYTE_ARRAY))
                    .withCase(ASN1Sequence.class, () -> singletonList(getMapParameter()))
                    .withCase(ASN1Set.class, () -> singletonList(getMapParameter()))
                    .withCase(ASN1SequenceOf.class, args -> getCollectionOfParameter(args.get()))
                    .withCase(ASN1SetOf.class, args -> getCollectionOfParameter(args.get()));

    Dispatcher<String, Class<? extends ASN1Type>, CompiledType, List<Expression>> parametersDispatcher =
            new Dispatcher<String, Class<? extends ASN1Type>, CompiledType, List<Expression>>()
                    .withComparator((t, u) -> t.equals(u.getSimpleName()))
                    .withCase(ASN1Integer.class, () -> singletonList(getGetValueCall()))
                    .withCase(ASN1Boolean.class, () -> singletonList(getGetValueCall()))
                    .withCase(ASN1EnumeratedType.class, () -> singletonList(getGetValueCall()))
                    .withCase(ASN1Null.class, () -> singletonList(getGetValueCall()))
                    .withCase(ASN1ObjectIdentifier.class,
                            () -> singletonList(new FunctionCall.ToArray(ILType.of(INTEGER), getGetValueCall())))
                    .withCase(ASN1RelativeOID.class,
                            () -> singletonList(new FunctionCall.ToArray(ILType.of(INTEGER), getGetValueCall())))
                    .withCase(ASN1IRI.class,
                            () -> singletonList(new FunctionCall.ToArray(ILType.of(ILBuiltinType.STRING),
                                    getGetValueCall())))
                    .withCase(ASN1RelativeIRI.class,
                            () -> singletonList(new FunctionCall.ToArray(ILType.of(ILBuiltinType.STRING),
                                    getGetValueCall())))
                    .withCase(ASN1BitString.class,
                            () -> List.of(getGetValueCall(), getAccessorCall(GET_UNUSED_BITS, VAR_VALUE)))
                    .withCase(ASN1OctetString.class, () -> singletonList(getGetValueCall()))
                    .withCase(ASN1Sequence.class, args -> getCollectionParameters((CompiledCollectionType) args.get()))
                    .withCase(ASN1Set.class, args -> getCollectionParameters((CompiledCollectionType) args.get()))
                    .withCase(ASN1SequenceOf.class, () -> singletonList(getGetValuesCall()))
                    .withCase(ASN1SetOf.class, () -> singletonList(getGetValuesCall()));

    public AbstractCollectionOfConstraintCompiler(CompilerContext ctx, TypeName typeName, ILBuiltinType collectionType) {
        super(ctx);

        this.typeName = typeName;
        this.collectionType = collectionType;

        addConstraintHandler(SingleValueConstraint.class,
                new SingleValueCompiler(ctx, CollectionOfValue.class, CollectionOfValueNode.class, getTypeName(),
                        Set.class)::compile);
        addConstraintHandler(ContainedSubtype.class, new ContainedSubtypeCompiler(ctx)::compile);
        addConstraintHandler(SizeConstraint.class,
                new SizeCompiler(ctx, new IntegerConstraintCompiler(ctx).getDispatcher())::compile);
        addConstraintHandler(SingleTypeConstraint.class, new SingleTypeConstraintCompiler(ctx)::compile);
    }

    @Override
    protected TypeName getTypeName() {
        return typeName;
    }

    @Override
    ConstraintDefinition compileConstraints(Type node, CompiledType baseType) {
        ConstraintDefinition constraintDefinition = super.compileConstraints(node, baseType);
        CollectionOfType collectionOfType = (CollectionOfType) baseType.getType();

        if (collectionOfType.hasElementConstraint()) {
            if (constraintDefinition == null) {
                constraintDefinition = new ConstraintDefinition();
            }

            ConstraintDefinition componentDefinition = ctx
                    .compileConstraint(ctx.getCompiledType(((CollectionOfType) baseType.getType()).getType()));

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
    public void addConstraint(CompiledType compiledType, Module module, ConstraintDefinition definition) {
        generateDoCheckConstraint(module);

        CollectionOfType referencedType = (CollectionOfType) ctx.resolveTypeReference(compiledType.getType());
        List<String> typeParameter = ctx.getTypeParameter(referencedType);

        FunctionBuilder builder = generateCheckConstraintValue(module,
                new Parameter(ILParameterizedType.of(collectionType, typeParameter), VAR_VALUE));

        addConstraintCondition(compiledType, typeParameter, definition, builder, module);

        builder.build();
    }

    protected void addConstraintCondition(CompiledType compiledType, List<String> typeParameter, ConstraintDefinition definition,
            FunctionBuilder builder, Module module) {
        String functionName = FUNC_CHECK_CONSTRAINT_VALUE;

        if (definition.isExtensible()) {
            builder.statements().returnValue(Boolean.TRUE);
        } else if (builder.getModule().getFunctions().stream().noneMatch(f -> f.getName().equals(functionName))) {
            addConstraintCondition(compiledType, definition, builder);
        } else {
            Node roots = definition.getRoots();
            Optional<BooleanExpression> expression = buildExpression(module, compiledType, roots);
            Type elementType = ((CollectionOfType) compiledType.getType()).getType();
            BooleanExpression condition;

            if (elementType instanceof CollectionOfType) {
                condition = new NegationExpression(
                        new FunctionCall(of(functionName), new FunctionCall(of(GET_VALUES), of(new ILValue(VAR_OBJ)))));
            } else if (elementType instanceof BitString) {
                condition = new NegationExpression(
                        new FunctionCall(of(functionName),
                                new FunctionCall(of(GET_VALUE), of(new ILValue(VAR_OBJ))),
                                new FunctionCall(of(GET_UNUSED_BITS), of(new ILValue(VAR_OBJ)))));
            } else {
                condition = new NegationExpression(
                        new FunctionCall(of(functionName), new FunctionCall(of(GET_VALUE), of(new ILValue(VAR_OBJ)))));
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
                        .foreach(new ILParameterizedType(CUSTOM, typeParameter), new Variable(VAR_OBJ), new Variable(VAR_VALUE))
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
        return new FunctionCall(of(FUNC_CHECK_CONSTRAINT_VALUE), new FunctionCall(of(GET_VALUES)));
    }

    @Override
    protected Node optimize(Node node) {
        return new CollectionOfConstraintOptimizingVisitor().visit(node);
    }

    @Override
    protected Optional<BooleanExpression> buildExpression(Module module, CompiledType compiledType, Node node) {
        if (node == null) {
            return Optional.empty();
        }

        switch (node.getType()) {
            case VALUE:
                return getValueExpression(compiledType, (CollectionOfValueNode) node);
            case SIZE:
                return new CollectionOfSizeExpressionBuilder().build(((SizeNode) node).getSize());
            case WITH_COMPONENT:
                return getWithComponentExpression(module, compiledType, (WithComponentNode) node);
            default:
                return super.buildExpression(module, compiledType, node);
        }
    }

    private Optional<BooleanExpression> getValueExpression(CompiledType compiledType, CollectionOfValueNode node) {
        Set<CollectionOfValue> values = node.getValue();
        List<BooleanExpression> valueArguments = values.stream()
                .map(value -> buildExpression(compiledType, value))
                .collect(Collectors.toList());

        return Optional.of(new BinaryBooleanExpression(BinaryOperator.OR, valueArguments));
    }

    private Optional<BooleanExpression> getWithComponentExpression(Module module, CompiledType compiledType,
            WithComponentNode node) {
        var compiledBaseType = (CompiledCollectionOfType) ctx.getCompiledBaseType(compiledType);
        var compiledContentType = ctx.getCompiledBaseType(compiledBaseType.getContentType());
        var maybeExpression = ctx.buildExpression(module, compiledContentType, node.getConstraint());
        var expression = maybeExpression.orElseThrow(
                () -> new IllegalCompilerStateException("Expected expression in %s"));
        var expressionSym = module.generateSymbol(FUNC_EXPRESSION);
        var parameterizedType = ctx.getParameterizedType(node.getComponentType());
        var typeParameters = parameterizedType.stream().skip(1).collect(Collectors.toList());
        var parameterDefinition = getParameterDefinition(compiledBaseType, parameterizedType.get(0), typeParameters);

        ConstraintUtils.buildExpressionFunction(module, expression, expressionSym, parameterDefinition);

        var functionCall = new BooleanFunctionCall(Optional.of(expressionSym), getParameters(compiledContentType));

        String checkSym = module.generateSymbol(FUNC_CHECK_CONSTRAINT);

        // @formatter:off
        module.function()
                .returnType(ILType.of(BOOLEAN))
                .name(checkSym)
                .parameter(new Parameter(new ILParameterizedType(ILBuiltinType.LIST, parameterizedType), VAR_VALUES))
                .statements()
                    .foreach(new ILParameterizedType(CUSTOM, parameterizedType), new Variable(VAR_VALUE), new Variable(VAR_VALUES))
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

        return Optional.of(new BooleanFunctionCall(Optional.of(checkSym), new Variable(VAR_VALUES)));
    }

    private List<Expression> getParameters(CompiledType compiledContentType) {
        return parametersDispatcher.execute(ctx.getRuntimeType(compiledContentType.getType()), compiledContentType);
    }

    private List<Expression> getCollectionParameters(CompiledCollectionType compiledContentType) {
        var associations = new HashSet<Tuple2<Expression, Expression>>();

        compiledContentType.getComponents().stream()
                .map(Tuple2::get_1)
                .map(n -> new Tuple2<Expression, Expression>(ILValue.of(n),
                        new FunctionCall(of("get" + initCap(n)), of(Variable.of(VAR_VALUE)))))
                .forEach(associations::add);

        return Collections.singletonList(new ILMapValue(ILType.of(ILBuiltinType.STRING),
                ILParameterizedType.of(CUSTOM, singletonList(ASN1Type.class.getSimpleName())), associations));
    }

    private FunctionCall getGetValueCall() {
        return getAccessorCall(GET_VALUE, VAR_VALUE);
    }

    private FunctionCall getGetValuesCall() {
        return getAccessorCall(GET_VALUES, VAR_VALUE);
    }

    private FunctionCall getAccessorCall(String function, String object) {
        return new FunctionCall(Optional.of(function), Optional.of(new Variable(object)));
    }

    private List<Parameter> getParameterDefinition(CompiledCollectionOfType compiledBaseType, String type,
            List<String> typeParameters) {
        List<Parameter> parameters;

        if (typeParameters.isEmpty()) {
            parameters = getParameterDefinition(compiledBaseType, type);
        } else {
            parameters = singletonList(
                    new Parameter(new ILParameterizedType(ILBuiltinType.LIST, typeParameters), VAR_VALUES));
        }

        return parameters;
    }

    private List<Parameter> getParameterDefinition(CompiledType compiledType, String type) {
        return parameterDefinitionDispatcher.execute(ctx.getRuntimeType(type), compiledType);
    }

    private List<Parameter> getCollectionOfParameter(CompiledType compiledType) {
        var compiledBaseType = (CompiledCollectionOfType) ctx.getCompiledBaseType(compiledType);
        var contentType = compiledBaseType.getContentType().getType();

        List<String> typeParameter = ctx.getParameterizedType((Type) ctx.resolveTypeReference(contentType))
                .stream()
                .skip(1)
                .collect(Collectors.toList());

        return singletonList(new Parameter(new ILParameterizedType(ILBuiltinType.LIST, typeParameter), VAR_VALUES));
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

    private BooleanExpression buildExpression(CompiledType compiledType, CollectionOfValue collectionOfValue) {
        var values = collectionOfValue.getValues().stream()
                .map(value -> new ILValue(getTypeName(compiledType.getType()), value))
                .collect(Collectors.toList());

        return new BooleanFunctionCall.SetEquals(new Variable(VAR_VALUE), new ILListValue(values));
    }

}
