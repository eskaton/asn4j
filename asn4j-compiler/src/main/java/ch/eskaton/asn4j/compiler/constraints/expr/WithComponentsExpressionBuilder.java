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
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.compiler.constraints.ast.ComponentNode;
import ch.eskaton.asn4j.compiler.constraints.ast.WithComponentsNode;
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
import ch.eskaton.asn4j.compiler.il.Module;
import ch.eskaton.asn4j.compiler.il.Parameter;
import ch.eskaton.asn4j.compiler.il.Variable;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionComponent;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionOfType;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionType;
import ch.eskaton.asn4j.compiler.results.CompiledComponent;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.compiler.results.HasComponents;
import ch.eskaton.asn4j.parser.ast.constraints.PresenceConstraint.PresenceType;
import ch.eskaton.asn4j.runtime.types.ASN1BMPString;
import ch.eskaton.asn4j.runtime.types.ASN1BitString;
import ch.eskaton.asn4j.runtime.types.ASN1Boolean;
import ch.eskaton.asn4j.runtime.types.ASN1Choice;
import ch.eskaton.asn4j.runtime.types.ASN1EnumeratedType;
import ch.eskaton.asn4j.runtime.types.ASN1GeneralString;
import ch.eskaton.asn4j.runtime.types.ASN1GraphicString;
import ch.eskaton.asn4j.runtime.types.ASN1IA5String;
import ch.eskaton.asn4j.runtime.types.ASN1IRI;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1Null;
import ch.eskaton.asn4j.runtime.types.ASN1NumericString;
import ch.eskaton.asn4j.runtime.types.ASN1ObjectIdentifier;
import ch.eskaton.asn4j.runtime.types.ASN1OctetString;
import ch.eskaton.asn4j.runtime.types.ASN1PrintableString;
import ch.eskaton.asn4j.runtime.types.ASN1RelativeIRI;
import ch.eskaton.asn4j.runtime.types.ASN1RelativeOID;
import ch.eskaton.asn4j.runtime.types.ASN1Sequence;
import ch.eskaton.asn4j.runtime.types.ASN1SequenceOf;
import ch.eskaton.asn4j.runtime.types.ASN1Set;
import ch.eskaton.asn4j.runtime.types.ASN1SetOf;
import ch.eskaton.asn4j.runtime.types.ASN1TeletexString;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.asn4j.runtime.types.ASN1UTF8String;
import ch.eskaton.asn4j.runtime.types.ASN1UniversalString;
import ch.eskaton.asn4j.runtime.types.ASN1VideotexString;
import ch.eskaton.asn4j.runtime.types.ASN1VisibleString;
import ch.eskaton.commons.collections.Tuple2;
import ch.eskaton.commons.utils.Dispatcher;
import ch.eskaton.commons.utils.OptionalUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.constraints.Constants.FUNC_CHECK_CONSTRAINT;
import static ch.eskaton.asn4j.compiler.constraints.Constants.FUNC_EXPRESSION;
import static ch.eskaton.asn4j.compiler.constraints.Constants.GET_UNUSED_BITS;
import static ch.eskaton.asn4j.compiler.constraints.Constants.GET_VALUE;
import static ch.eskaton.asn4j.compiler.constraints.Constants.GET_VALUES;
import static ch.eskaton.asn4j.compiler.constraints.Constants.VAR_UNUSED_BITS;
import static ch.eskaton.asn4j.compiler.constraints.Constants.VAR_VALUE;
import static ch.eskaton.asn4j.compiler.constraints.Constants.VAR_VALUES;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintUtils.getMapParameter;
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

public class WithComponentsExpressionBuilder extends InnerTypeExpressionBuilder {

    protected final Dispatcher<String, Class<? extends ASN1Type>, CompiledType, List<Parameter>> parameterDefinitionDispatcher =
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
                    .withCase(ASN1VisibleString.class, args -> getValueParameter(STRING))
                    .withCase(ASN1NumericString.class, args -> getValueParameter(STRING))
                    .withCase(ASN1PrintableString.class, args -> getValueParameter(STRING))
                    .withCase(ASN1IA5String.class, args -> getValueParameter(STRING))
                    .withCase(ASN1GraphicString.class, args -> getValueParameter(STRING))
                    .withCase(ASN1GeneralString.class, args -> getValueParameter(STRING))
                    .withCase(ASN1TeletexString.class, args -> getValueParameter(STRING))
                    .withCase(ASN1VideotexString.class, args -> getValueParameter(STRING))
                    .withCase(ASN1UniversalString.class, args -> getValueParameter(STRING))
                    .withCase(ASN1UTF8String.class, args -> getValueParameter(STRING))
                    .withCase(ASN1BMPString.class, args -> getValueParameter(STRING))
                    .withCase(ASN1Sequence.class, args -> singletonList(getMapParameter()))
                    .withCase(ASN1Set.class, args -> singletonList(getMapParameter()))
                    .withCase(ASN1SequenceOf.class, args -> getCollectionOfParameterDefinition(args.get()))
                    .withCase(ASN1SetOf.class, args -> getCollectionOfParameterDefinition(args.get()))
                    .withCase(ASN1Choice.class, args -> singletonList(getMapParameter()));

    protected final Dispatcher<String, Class<? extends ASN1Type>, Tuple2<ComponentNode, CompiledType>, List<Expression>> parametersDispatcher =
            new Dispatcher<String, Class<? extends ASN1Type>, Tuple2<ComponentNode, CompiledType>, List<Expression>>()
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
                    .withCase(ASN1VisibleString.class, args -> singletonList(getMapValueAccessor(args, GET_VALUE)))
                    .withCase(ASN1NumericString.class, args -> singletonList(getMapValueAccessor(args, GET_VALUE)))
                    .withCase(ASN1PrintableString.class, args -> singletonList(getMapValueAccessor(args, GET_VALUE)))
                    .withCase(ASN1IA5String.class, args -> singletonList(getMapValueAccessor(args, GET_VALUE)))
                    .withCase(ASN1GraphicString.class, args -> singletonList(getMapValueAccessor(args, GET_VALUE)))
                    .withCase(ASN1GeneralString.class, args -> singletonList(getMapValueAccessor(args, GET_VALUE)))
                    .withCase(ASN1TeletexString.class, args -> singletonList(getMapValueAccessor(args, GET_VALUE)))
                    .withCase(ASN1VideotexString.class, args -> singletonList(getMapValueAccessor(args, GET_VALUE)))
                    .withCase(ASN1UniversalString.class, args -> singletonList(getMapValueAccessor(args, GET_VALUE)))
                    .withCase(ASN1UTF8String.class, args -> singletonList(getMapValueAccessor(args, GET_VALUE)))
                    .withCase(ASN1BMPString.class, args -> singletonList(getMapValueAccessor(args, GET_VALUE)))
                    .withCase(ASN1Sequence.class, args -> getComponentsParameters(args.get().get_2(),
                            acc -> getMapValueAccessor(args, acc)))
                    .withCase(ASN1Set.class, args -> getComponentsParameters(args.get().get_2(),
                            acc -> getMapValueAccessor(args, acc)))
                    .withCase(ASN1SequenceOf.class, args -> singletonList(getMapValueAccessor(args, GET_VALUES)))
                    .withCase(ASN1SetOf.class, args -> singletonList(getMapValueAccessor(args, GET_VALUES)))
                    .withCase(ASN1Choice.class, args -> getComponentsParameters(args.get().get_2(),
                            acc -> getMapValueAccessor(args, acc)));

    public WithComponentsExpressionBuilder(CompilerContext ctx) {
        super(ctx);
    }

    public Optional<BooleanExpression> build(Module module, CompiledType compiledType, WithComponentsNode node) {
        var compiledComponentTypes = getTypeWithComponents(compiledType).getComponents()
                .stream()
                .collect(Collectors.toMap(CompiledComponent::getName, CompiledComponent::getCompiledType));

        return getExpression(module, compiledType, node, compiledComponentTypes);
    }

    protected HasComponents<? extends CompiledComponent> getTypeWithComponents(CompiledType compiledType) {
        return ctx.getCompiledCollectionType(compiledType);
    }

    protected Optional<BooleanExpression> getExpression(Module module, CompiledType compiledType,
            WithComponentsNode node, Map<String, CompiledType> compiledComponentTypes) {
        var expressionCalls = node.getComponents().stream().map(componentNode -> {
            var compiledComponent = compiledComponentTypes.get(componentNode.getName());
            var maybeValueExpression = getValueExpression(module, componentNode, compiledComponent);
            var maybePresenceExpression = getPresenceExpression(compiledType, componentNode);
            var expression = OptionalUtils.combine(maybeValueExpression, maybePresenceExpression,
                    (l, r) -> new BinaryBooleanExpression(getOperator(), l, r));

            return expression.map(e -> {
                var expressionFunction = buildExpressionFunction(module, compiledComponent, e);

                return new BooleanFunctionCall(of(expressionFunction.get_1()),
                        getParameters(componentNode, compiledComponent, expressionFunction.get_2()));
            });
        }).filter(Optional::isPresent)
                .map(Optional::get).collect(Collectors.toList());

        var checkSym = module.generateSymbol(FUNC_CHECK_CONSTRAINT);
        var parameterDefinition = getMapParameter();

        if (!expressionCalls.isEmpty()) {
            var expression = new BinaryBooleanExpression(getOperator(), expressionCalls);

            buildExpressionFunction(module, expression, checkSym, singletonList(parameterDefinition));

            return Optional.of(new BooleanFunctionCall(Optional.of(checkSym), Variable.of(VAR_VALUES)));
        } else {
            return Optional.empty();
        }
    }

    protected BinaryOperator getOperator() {
        return BinaryOperator.AND;
    }

    protected Optional<BooleanExpression> getPresenceExpression(CompiledType compiledType,
            ComponentNode componentNode) {
        return Optional.ofNullable(componentNode.getPresenceType())
                .map(presenceType -> {
                    var compiledBaseType = ctx.getCompiledBaseType(compiledType);
                    var componentType = getComponentType((CompiledCollectionType) compiledBaseType, componentNode);

                    return componentType.map(c ->
                            buildPresenceExpression(c.getName(), isComponentOptional(c), presenceType))
                            .orElse(null);
                });
    }

    private Optional<CompiledCollectionComponent> getComponentType(CompiledCollectionType compiledCollectionType,
            ComponentNode componentNode) {
        return compiledCollectionType.getComponents().stream()
                .filter(c -> c.getName().equals(componentNode.getName()))
                .findFirst();
    }

    protected Optional<BooleanExpression> getValueExpression(Module module, ComponentNode componentNode,
            CompiledType compiledComponent) {
        return Optional.ofNullable(componentNode.getConstraint())
                .map(constraint -> ctx.buildExpression(module, compiledComponent, constraint)
                        .orElseThrow(() -> new IllegalCompilerStateException("Expected maybeValueExpression")));
    }

    protected BooleanExpression buildPresenceExpression(String componentName, boolean isOptional,
            PresenceType presenceType) {
        return switch (presenceType) {
            case PRESENT -> new BinaryBooleanExpression(BinaryOperator.NE, new Variable(VAR_VALUE), new ILValue(null));
            case ABSENT -> getAbsentExpression(isOptional, componentName);
            case OPTIONAL -> null;
        };
    }

    protected BooleanExpression getAbsentExpression(boolean componentOptional, String componentName) {
        if (!componentOptional) {
            throw new CompilerException("Component '%s' isn't optional and therefore can't have a presence constraint of ABSENT",
                    componentName);
        }

        return new BinaryBooleanExpression(BinaryOperator.EQ, new Variable(VAR_VALUE), new ILValue(null));
    }

    private boolean isComponentOptional(CompiledCollectionComponent compiledCollectionComponent) {
        return compiledCollectionComponent.isOptional();
    }

    protected List<Expression> getParameters(ComponentNode component, CompiledType compiledType, String runtimeType) {
        return parametersDispatcher.execute(runtimeType, Tuple2.of(component, compiledType));
    }

    private FunctionCall getMapValueAccessor(Optional<Tuple2<ComponentNode, CompiledType>> args, String accessor) {
        if (args.isEmpty()) {
            throw new IllegalCompilerStateException("Empty arguments not allowed");
        }

        return getMapValueAccessor(args.get().get_1(), args.get().get_2(), accessor);
    }

    private FunctionCall getMapValueAccessor(ComponentNode component, CompiledType compiledType, String accessor) {
        return new FunctionCall(of(accessor),
                of(new FunctionCall.GetMapValue(Variable.of(VAR_VALUES),
                        ILValue.of(component.getName()),
                        ILParameterizedType.of(CUSTOM, singletonList(getTypeName(compiledType))))));
    }

    private String getTypeName(CompiledType compiledType) {
        if (ctx.isRuntimeType(compiledType.getName())) {
            return compiledType.getName();
        } else {
            var parent = compiledType;
            var names = new LinkedList<String>();

            while (parent != null) {
                names.addFirst(parent.getName());
                parent = parent.getParent();
            }

            return names.stream().skip(1).collect(Collectors.joining("."));
        }
    }

    private List<Expression> getComponentsParameters(CompiledType compiledType,
            Function<String, FunctionCall> getCall) {
        var associations = new HashSet<Tuple2<Expression, Expression>>();
        var compiledBaseType = (HasComponents<? extends CompiledComponent>) ctx.getCompiledBaseType(compiledType);

        compiledBaseType.getComponents().stream().map(CompiledComponent::getName)
                .map(n -> new Tuple2(ILValue.of(n), getCall.apply("get" + initCap(n))))
                .forEach(associations::add);

        return singletonList(new ILMapValue(ILType.of(ILBuiltinType.STRING),
                ILParameterizedType.of(CUSTOM, singletonList(ASN1Type.class.getSimpleName())), associations));
    }

    protected Tuple2<String, String> buildExpressionFunction(Module module, CompiledType compiledType,
            BooleanExpression expression) {
        var expressionSym = module.generateSymbol(FUNC_EXPRESSION);
        var parameterDefinition = getParameterDefinition(compiledType);

        buildExpressionFunction(module, expression, expressionSym, parameterDefinition);

        return Tuple2.of(expressionSym, ctx.getRuntimeTypeName(compiledType.getType()));
    }

    protected List<Parameter> getParameterDefinition(CompiledType compiledType) {
        return parameterDefinitionDispatcher.execute(ctx.getRuntimeTypeName(compiledType.getType()), compiledType);
    }

    private List<Parameter> getCollectionOfParameterDefinition(CompiledType compiledType) {
        var compiledBaseType = (CompiledCollectionOfType) ctx.getCompiledBaseType(compiledType);
        var compiledContentType = compiledBaseType.getContentType();

        var typeParameter = ctx.getParameterizedType(compiledContentType.getType())
                .stream()
                .collect(Collectors.toList());

        return singletonList(new Parameter(new ILParameterizedType(ILBuiltinType.LIST, typeParameter), VAR_VALUES));
    }

}
