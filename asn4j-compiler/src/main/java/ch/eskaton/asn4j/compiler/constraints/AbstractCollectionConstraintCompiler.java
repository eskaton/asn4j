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
import ch.eskaton.asn4j.compiler.constraints.ast.CollectionValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.ComponentNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.WithComponentsNode;
import ch.eskaton.asn4j.compiler.constraints.elements.CollectionContainedSubtypeCompiler;
import ch.eskaton.asn4j.compiler.constraints.elements.MultipleTypeConstraintsCompiler;
import ch.eskaton.asn4j.compiler.constraints.elements.SingleValueCompiler;
import ch.eskaton.asn4j.compiler.constraints.optimizer.CollectionConstraintOptimizingVisitor;
import ch.eskaton.asn4j.compiler.il.BinaryBooleanExpression;
import ch.eskaton.asn4j.compiler.il.BinaryOperator;
import ch.eskaton.asn4j.compiler.il.BooleanExpression;
import ch.eskaton.asn4j.compiler.il.BooleanFunctionCall;
import ch.eskaton.asn4j.compiler.il.Expression;
import ch.eskaton.asn4j.compiler.il.FunctionCall;
import ch.eskaton.asn4j.compiler.il.ILBuiltinType;
import ch.eskaton.asn4j.compiler.il.ILMapValue;
import ch.eskaton.asn4j.compiler.il.ILParameterizedType;
import ch.eskaton.asn4j.compiler.il.ILType;
import ch.eskaton.asn4j.compiler.il.ILValue;
import ch.eskaton.asn4j.compiler.il.ILVisibility;
import ch.eskaton.asn4j.compiler.il.Module;
import ch.eskaton.asn4j.compiler.il.NegationExpression;
import ch.eskaton.asn4j.compiler.il.Parameter;
import ch.eskaton.asn4j.compiler.il.Variable;
import ch.eskaton.asn4j.compiler.il.builder.FunctionBuilder;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionOfType;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.constraints.ContainedSubtype;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.MultipleTypeConstraints;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.CollectionOfType;
import ch.eskaton.asn4j.parser.ast.types.SequenceType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.CollectionValue;
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
import ch.eskaton.commons.utils.StreamsUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.constraints.Constants.FUNC_CHECK_CONSTRAINT;
import static ch.eskaton.asn4j.compiler.constraints.Constants.FUNC_CHECK_CONSTRAINT_VALUE;
import static ch.eskaton.asn4j.compiler.constraints.Constants.FUNC_DO_CHECK_CONSTRAINT;
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
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.STRING;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.STRING_ARRAY;
import static ch.eskaton.commons.utils.StringUtils.initCap;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Optional.of;

public abstract class AbstractCollectionConstraintCompiler extends AbstractConstraintCompiler {

    private final TypeName typeName;

    private final Dispatcher<String, Class<? extends ASN1Type>, CompiledType, List<Parameter>> parameterDefinitionDispatcher =
            new Dispatcher<String, Class<? extends ASN1Type>, CompiledType, List<Parameter>>()
                    .withComparator((t, u) -> t.equals(u.getSimpleName()))
                    .withCase(ASN1Integer.class, args -> getValueParameter(BIG_INTEGER))
                    .withCase(ASN1Boolean.class, args -> getValueParameter(BOOLEAN))
                    .withCase(ASN1EnumeratedType.class, args -> getValueParameter(INTEGER))
                    .withCase(ASN1Null.class, args -> getValueParameter(NULL))
                    .withCase(ASN1ObjectIdentifier.class, args -> getValueParameter(INTEGER_ARRAY))
                    .withCase(ASN1RelativeOID.class, args -> getValueParameter(INTEGER_ARRAY))
                    .withCase(ASN1IRI.class, args -> getValueParameter(STRING_ARRAY))
                    .withCase(ASN1RelativeIRI.class, args -> getValueParameter(STRING_ARRAY))
                    .withCase(ASN1BitString.class, args -> asList(new Parameter(ILType.of(BYTE_ARRAY), VAR_VALUE),
                            new Parameter(ILType.of(INTEGER), VAR_UNUSED_BITS)))
                    .withCase(ASN1OctetString.class, args -> getValueParameter(BYTE_ARRAY))
                    .withCase(ASN1Sequence.class, args -> singletonList(getMapParameter()))
                    .withCase(ASN1Set.class, args -> singletonList(getMapParameter()))
                    .withCase(ASN1SequenceOf.class, args -> getCollectionOfParameterDefinition(args.get()))
                    .withCase(ASN1SetOf.class, args -> getCollectionOfParameterDefinition(args.get()));

    private final Dispatcher<String, Class<? extends ASN1Type>, Tuple2<ComponentNode, String>, List<Expression>> parametersDispatcher =
            new Dispatcher<String, Class<? extends ASN1Type>, Tuple2<ComponentNode, String>, List<Expression>>()
                    .withComparator((t, u) -> t.equals(u.getSimpleName()))
                    .withCase(ASN1Integer.class, args -> singletonList(getMapValueAccessor(args, GET_VALUE)))
                    .withCase(ASN1Boolean.class, args -> singletonList(getMapValueAccessor(args, GET_VALUE)))
                    .withCase(ASN1EnumeratedType.class, args -> singletonList(getMapValueAccessor(args, GET_VALUE)))
                    .withCase(ASN1Null.class, args -> singletonList(getMapValueAccessor(args, GET_VALUE)))
                    .withCase(ASN1ObjectIdentifier.class, args ->
                            singletonList(new FunctionCall.ToArray(ILType.of(INTEGER),
                                    getMapValueAccessor(args, GET_VALUE))))
                    .withCase(ASN1RelativeOID.class, args ->
                            singletonList(new FunctionCall.ToArray(ILType.of(INTEGER),
                                    getMapValueAccessor(args, GET_VALUE))))
                    .withCase(ASN1IRI.class, args ->
                            singletonList(new FunctionCall.ToArray(ILType.of(STRING),
                                    getMapValueAccessor(args, GET_VALUE))))
                    .withCase(ASN1RelativeIRI.class, args ->
                            singletonList(new FunctionCall.ToArray(ILType.of(STRING),
                                    getMapValueAccessor(args, GET_VALUE))))
                    .withCase(ASN1BitString.class, args -> List.of(getMapValueAccessor(args, GET_VALUE),
                            getMapValueAccessor(args, GET_UNUSED_BITS)))
                    .withCase(ASN1OctetString.class, args -> singletonList(getMapValueAccessor(args, GET_VALUE)))
                    .withCase(ASN1Sequence.class, args -> getCollectionParameters(args.get().get_1(),
                            (acc) -> getMapValueAccessor(args, acc)))
                    .withCase(ASN1Set.class, args -> getCollectionParameters(args.get().get_1(),
                            (acc) -> getMapValueAccessor(args, acc)))
                    .withCase(ASN1SequenceOf.class, args -> singletonList(getMapValueAccessor(args, GET_VALUES)))
                    .withCase(ASN1SetOf.class, args -> singletonList(getMapValueAccessor(args, GET_VALUES)));

    public AbstractCollectionConstraintCompiler(CompilerContext ctx, TypeName typeName) {
        super(ctx);

        this.typeName = typeName;

        addConstraintHandler(ElementSet.class, this::compileConstraint);
        addConstraintHandler(SingleValueConstraint.class,
                new SingleValueCompiler(ctx, CollectionValue.class, CollectionValueNode.class, getTypeName(),
                        Set.class)::compile);
        addConstraintHandler(ContainedSubtype.class, new CollectionContainedSubtypeCompiler(ctx)::compile);
        addConstraintHandler(MultipleTypeConstraints.class, new MultipleTypeConstraintsCompiler(ctx)::compile);
    }

    @Override
    protected TypeName getTypeName() {
        return typeName;
    }

    @Override
    public void addConstraint(CompiledType compiledType, Module module, ConstraintDefinition definition) {
        var compiledBaseType = getCompiledCollectionType(compiledType);

        generateDoCheckConstraint(compiledBaseType, module);

        List<String> typeParameter = ctx.getTypeParameter(compiledBaseType.getType());

        FunctionBuilder builder = generateCheckConstraintValue(module,
                getMapParameter());

        addConstraintCondition(compiledType, typeParameter, definition, builder, module);

        builder.build();
    }

    private CompiledCollectionType getCompiledCollectionType(CompiledType compiledType) {
        return Optional.ofNullable(ctx.getCompiledBaseType(compiledType))
                .filter(CompiledCollectionType.class::isInstance)
                .map(CompiledCollectionType.class::cast)
                .orElseThrow(() -> new CompilerException("Failed to resolve the type of %s", compiledType));
    }

    protected void generateDoCheckConstraint(CompiledCollectionType compiledType, Module module) {
        // @formatter:off
        module.function()
                .name(FUNC_DO_CHECK_CONSTRAINT)
                .overriden(true)
                .visibility(ILVisibility.PUBLIC)
                .returnType(ILType.of(BOOLEAN))
                .statements()
                    .returnExpression(generateCheckConstraintCall(compiledType))
                    .build()
                .build();
        // @formatter:on
    }

    protected FunctionCall generateCheckConstraintCall(CompiledCollectionType compiledType) {
        Set<Tuple2<Expression, Expression>> associations = new HashSet<>();

        compiledType.getComponents().stream()
                .map(Tuple2::get_1)
                .map(n -> new Tuple2(ILValue.of(n), new FunctionCall(of("get" + initCap(n)))))
                .forEach(associations::add);

        return new FunctionCall(of(FUNC_CHECK_CONSTRAINT_VALUE),
                new ILMapValue(ILType.of(ILBuiltinType.STRING),
                        ILParameterizedType.of(CUSTOM, singletonList(ASN1Type.class.getSimpleName())), associations));
    }

    protected void addConstraintCondition(CompiledType compiledType, List<String> typeParameter, ConstraintDefinition definition,
            FunctionBuilder builder, Module module) {
        if (definition.isExtensible()) {
            builder.statements().returnValue(Boolean.TRUE);
        } else if (builder.getModule().getFunctions().stream().noneMatch(f -> f.getName().equals(FUNC_CHECK_CONSTRAINT_VALUE))) {
            addConstraintCondition(compiledType, definition, builder);
        } else {
            Node roots = definition.getRoots();
            Optional<BooleanExpression> expression = buildExpression(module, compiledType, roots);
            Type elementType = ((CollectionOfType) compiledType.getType()).getType();
            BooleanExpression condition;

            if (elementType instanceof CollectionOfType) {
                condition = new NegationExpression(
                        new FunctionCall(of(FUNC_CHECK_CONSTRAINT_VALUE), new FunctionCall(of(GET_VALUES), of(ILValue.of(VAR_OBJ)))));
            } else if (elementType instanceof BitString) {
                condition = new NegationExpression(
                        new FunctionCall(of(FUNC_CHECK_CONSTRAINT_VALUE),
                                new FunctionCall(of(GET_VALUE), of(ILValue.of(VAR_OBJ))),
                                new FunctionCall(of(GET_UNUSED_BITS), of(ILValue.of(VAR_OBJ)))));
            } else {
                condition = new NegationExpression(
                        new FunctionCall(of(FUNC_CHECK_CONSTRAINT_VALUE), new FunctionCall(of(GET_VALUE), of(ILValue.of(VAR_OBJ)))));
            }

            if (expression.isPresent()) {
                builder.statements().returnExpression(expression.get()).build();
            } else {
                // @formatter:off
                builder.statements()
                        .foreach(ILParameterizedType.of(CUSTOM, typeParameter), Variable.of(VAR_OBJ), Variable.of(VAR_VALUE))
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
    protected Optional<BooleanExpression> buildExpression(Module module, CompiledType compiledType, Node node) {
        if (node == null) {
            return Optional.empty();
        }

        return switch (node.getType()) {
            case VALUE -> getValueExpression(compiledType, (CollectionValueNode) node);
            case WITH_COMPONENTS -> getWithComponentsExpression(module, compiledType, (WithComponentsNode) node);
            default -> super.buildExpression(module, compiledType, node);
        };
    }

    private Optional<BooleanExpression> getValueExpression(CompiledType compiledType, CollectionValueNode node) {
        Set<CollectionValue> values = node.getValue();
        List<BooleanExpression> valueArguments = values.stream()
                .map(value -> buildExpression(compiledType, value))
                .collect(Collectors.toList());

        return Optional.of(new BinaryBooleanExpression(BinaryOperator.OR, valueArguments));
    }

    private BooleanExpression buildExpression(CompiledType compiledType, CollectionValue collectionValue) {
        var compiledBaseType = ctx.getCompiledBaseType(compiledType);
        var typeStream = ((SequenceType) compiledBaseType.getType()).getAllRootComponents().stream()
                .map(componentType -> ctx.getTypeName(
                        Optional.ofNullable(componentType.getType())
                                .orElse(componentType.getNamedType().getType())));
        var valueStream = collectionValue.getValues().stream();
        var associations = new HashSet<Tuple2<Expression, Expression>>();

        StreamsUtils.zip(typeStream, valueStream)
                .map(t -> new Tuple2<Expression, Expression>(ILValue.of(t.get_2().getName()),
                        ILValue.of(t.get_1(), t.get_2().getValue())))
                .forEach(associations::add);

        return new BooleanFunctionCall.MapEquals(Variable.of(VAR_VALUES), new ILMapValue(ILType.of(ILBuiltinType.STRING),
                ILParameterizedType.of(CUSTOM, singletonList(ASN1Type.class.getSimpleName())), associations));
    }

    private Optional<BooleanExpression> getWithComponentsExpression(Module module, CompiledType compiledType,
            WithComponentsNode node) {
        var compiledComponentTypes = (getCompiledCollectionType(compiledType)).getComponents()
                .stream()
                .collect(Collectors.toMap(Tuple2::get_1, Tuple2::get_2));

        var expressionCalls = node.getComponents().stream().map(componentNode -> {
            var compiledComponent = compiledComponentTypes.get(componentNode.getName());
            var expression = ctx.buildExpression(module, compiledComponent, componentNode.getConstraint())
                    .orElseThrow(() -> new IllegalCompilerStateException("Expected expression"));
            var expressionFunction = buildExpressionFunction(module, compiledComponent, expression);

            return new BooleanFunctionCall(Optional.of(expressionFunction.get_1()),
                    getParameters(componentNode, compiledComponent.getName(), expressionFunction.get_2()));
        }).collect(Collectors.toList());

        var checkSym = module.generateSymbol(FUNC_CHECK_CONSTRAINT);
        var parameterDefinition = getMapParameter();
        var expression = new BinaryBooleanExpression(BinaryOperator.AND, expressionCalls);

        ConstraintUtils.buildExpressionFunction(module, expression, checkSym, singletonList(parameterDefinition));

        return Optional.of(new BooleanFunctionCall(Optional.of(checkSym), Variable.of(VAR_VALUES)));
    }

    private List<Expression> getParameters(ComponentNode component, String typeName, String runtimeType) {
        return parametersDispatcher.execute(runtimeType, Tuple2.of(component, typeName));
    }

    private FunctionCall getMapValueAccessor(Optional<Tuple2<ComponentNode, String>> args, String accessor) {
        return getMapValueAccessor(args.get().get_1(), args.get().get_2(), accessor);
    }

    private FunctionCall getMapValueAccessor(ComponentNode component, String typeName, String accessor) {
        return new FunctionCall(of(accessor),
                of(new FunctionCall.GetMapValue(Variable.of(VAR_VALUES),
                        ILValue.of(component.getName()),
                        ILParameterizedType.of(CUSTOM, singletonList(typeName)))));
    }

    private List<Expression> getCollectionParameters(ComponentNode component, Function<String, FunctionCall> getCall) {
        var associations = new HashSet<Tuple2<Expression, Expression>>();

        ((SequenceType) ctx.resolveTypeReference(component.getComponentType())).getAllComponents().stream()
                .map(c -> c.getNamedType().getName())
                .map(n -> new Tuple2(ILValue.of(n), getCall.apply("get" + initCap(n))))
                .forEach(associations::add);

        return singletonList(new ILMapValue(ILType.of(ILBuiltinType.STRING),
                ILParameterizedType.of(CUSTOM, singletonList(ASN1Type.class.getSimpleName())), associations));
    }

    private Tuple2<String, String> buildExpressionFunction(Module module, CompiledType compiledType,
            BooleanExpression expression) {
        var expressionSym = module.generateSymbol(FUNC_EXPRESSION);
        var parameterDefinition = getParameterDefinition(compiledType);

        ConstraintUtils.buildExpressionFunction(module, expression, expressionSym, parameterDefinition);

        return Tuple2.of(expressionSym, ctx.getRuntimeType(compiledType.getType()));
    }

    private List<Parameter> getParameterDefinition(CompiledType compiledType) {
        return parameterDefinitionDispatcher.execute(ctx.getRuntimeType(compiledType.getType()), compiledType);
    }

    private List<Parameter> getCollectionOfParameterDefinition(CompiledType compiledType) {
        var compiledBaseType = (CompiledCollectionOfType) ctx.getCompiledBaseType(compiledType);
        var contentType = compiledBaseType.getContentType().getType();

        List<String> typeParameter = ctx.getParameterizedType((Type) ctx.resolveTypeReference(contentType))
                .stream()
                .collect(Collectors.toList());

        return singletonList(new Parameter(new ILParameterizedType(ILBuiltinType.LIST, typeParameter), VAR_VALUES));
    }

}
