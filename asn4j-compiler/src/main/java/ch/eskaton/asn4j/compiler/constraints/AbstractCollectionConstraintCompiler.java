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
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.constraints.MultipleTypeConstraints;
import ch.eskaton.asn4j.parser.ast.constraints.NamedConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.PresenceConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.Collection;
import ch.eskaton.asn4j.parser.ast.types.CollectionOfType;
import ch.eskaton.asn4j.parser.ast.types.ComponentType;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.asn4j.parser.ast.types.SequenceType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.values.CollectionValue;
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
import ch.eskaton.asn4j.runtime.types.ASN1Sequence;
import ch.eskaton.asn4j.runtime.types.ASN1SequenceOf;
import ch.eskaton.asn4j.runtime.types.ASN1Set;
import ch.eskaton.asn4j.runtime.types.ASN1SetOf;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.asn4j.runtime.types.TypeName;
import ch.eskaton.commons.collections.Tuple2;
import ch.eskaton.commons.utils.StreamsUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
            return calculateSingleValueConstraint(baseType, (SingleValueConstraint) elements);
        } else if (elements instanceof ContainedSubtype) {
            return calculateContainedSubtype(baseType, ((ContainedSubtype) elements).getType());
        } else if (elements instanceof MultipleTypeConstraints) {
            return calculateMultipleTypeConstraint((CompiledCollectionType) baseType, (MultipleTypeConstraints) elements);
        } else {
            throw new CompilerException("Invalid constraint %s for %s type",
                    elements.getClass().getSimpleName(), typeName);
        }
    }

    private Node calculateMultipleTypeConstraint(CompiledCollectionType baseType, MultipleTypeConstraints elements) {
        var components = baseType.getComponents();
        var componentNodes = new HashSet<ComponentNode>();
        var lastIndex = -1;

        for (var constraint : elements.getConstraints()) {
            var name = constraint.getName();
            var index = StreamsUtils.indexOf(components, (c) -> Objects.equals(name, c.get_1()));

            if (index != -1 && index > lastIndex) {
                lastIndex = index;
            } else {
                throw new CompilerException("Component '%s' not found in type '%s'", name, baseType.getName());
            }

            componentNodes.add(calculateComponentConstraint(components.get(index).get_2(), constraint));
        }

        return new WithComponentsNode(componentNodes);
    }

    private ComponentNode calculateComponentConstraint(CompiledType compiledType, NamedConstraint namedConstraint) {
        var name = namedConstraint.getName();
        var constraint = namedConstraint.getConstraint();
        var presence = Optional.ofNullable(constraint.getPresence()).map(PresenceConstraint::getType).orElse(null);
        var valueConstraint = constraint.getValue().getConstraint();
        var definition = ctx.compileConstraint(compiledType);

        if (definition != null) {
            definition = definition.serialApplication(ctx.compileConstraint(compiledType, valueConstraint));
        } else {
            definition = ctx.compileConstraint(compiledType, valueConstraint);
        }

        return new ComponentNode(name, compiledType.getType(), definition.getRoots(), presence);
    }

    private Node calculateSingleValueConstraint(CompiledType baseType, SingleValueConstraint elements) {
        Value value = elements.getValue();

        try {
            CollectionValue collectionValue = ctx.resolveGenericValue(CollectionValue.class, baseType.getType(), value);

            return new CollectionValueNode(singleton(collectionValue));
        } catch (Exception e) {
            throw new CompilerException("Invalid single-value constraint %s for %s type", e,
                    value.getClass().getSimpleName(), typeName);
        }
    }

    @Override
    protected boolean isAssignable(CompiledType compiledType, CompiledType compiledParentType) {
        return super.isAssignable(compiledType, compiledParentType) && Objects
                .equals(getElementTypes(compiledType), getElementTypes(compiledParentType));
    }

    private Map<String, Class<? extends ch.eskaton.asn4j.parser.ast.Node>> getElementTypes(CompiledType compiledType) {
        return ((Collection) compiledType.getType()).getAllRootComponents().stream()
                .map(ComponentType::getNamedType)
                .collect(Collectors.toMap(NamedType::getName, nt -> ctx.resolveTypeReference(nt.getType()).getClass()));
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
                .name("doCheckConstraint")
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
                .map(t -> t.get_1())
                .map(n -> new Tuple2(ILValue.of(n), new FunctionCall(of("get" + initCap(n)))))
                .forEach(associations::add);

        return new FunctionCall(of("checkConstraintValue"),
                new ILMapValue(ILType.of(ILBuiltinType.STRING),
                        ILParameterizedType.of(CUSTOM, singletonList(ASN1Type.class.getSimpleName())), associations));
    }

    protected void addConstraintCondition(CompiledType compiledType, List<String> typeParameter, ConstraintDefinition definition,
            FunctionBuilder builder, Module module) {
        String functionName = "checkConstraintValue";

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
                        new FunctionCall(of(functionName), new FunctionCall(of(GET_VALUES), of(ILValue.of(OBJ)))));
            } else if (elementType instanceof BitString) {
                condition = new NegationExpression(
                        new FunctionCall(of(functionName),
                                new FunctionCall(of(GET_VALUE), of(ILValue.of(OBJ))),
                                new FunctionCall(of("getUnusedBits"), of(ILValue.of(OBJ)))));
            } else {
                condition = new NegationExpression(
                        new FunctionCall(of(functionName), new FunctionCall(of(GET_VALUE), of(ILValue.of(OBJ)))));
            }

            if (expression.isPresent()) {
                builder.statements().returnExpression(expression.get()).build();
            } else {
                // @formatter:off
                builder.statements()
                        .foreach(ILParameterizedType.of(CUSTOM, typeParameter), Variable.of(OBJ), Variable.of(VALUE))
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

    private BooleanExpression buildExpression(CompiledType compiledType, CollectionValue collectionValue) {
        var compiledBaseType = ctx.getCompiledBaseType(compiledType);
        var typeStream = ((SequenceType) compiledBaseType.getType()).getAllRootComponents().stream().map(componentType -> ctx.getTypeName(
                Optional.ofNullable(componentType.getType()).orElse(componentType.getNamedType().getType())));
        var valueStream = collectionValue.getValues().stream();
        var associations = new HashSet<Tuple2<Expression, Expression>>();

        StreamsUtils.zip(typeStream, valueStream)
                .map(t -> new Tuple2(ILValue.of(t.get_2().getName()), ILValue.of(t.get_1(), t.get_2().getValue())))
                .forEach(associations::add);

        return new BooleanFunctionCall.MapEquals(Variable.of(VALUES), new ILMapValue(ILType.of(ILBuiltinType.STRING),
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

        String checkSym = module.generateSymbol("_checkConstraint");

        // @formatter:off
        module.function()
                .returnType(ILType.of(BOOLEAN))
                .name(checkSym)
                .parameter(getMapParameter())
                .statements()
                    .returnExpression(new BinaryBooleanExpression(BinaryOperator.AND, expressionCalls))
                    .build()
                .build();
        // @formatter:on

        return Optional.of(new BooleanFunctionCall(Optional.of(checkSym), Variable.of(VALUES)));
    }

    private List<Expression> getParameters(ComponentNode component, String typeName, String runtimeType) {
        var accessor = new FunctionCall.GetMapValue(Variable.of(VALUES),
                ILValue.of(component.getName()),
                ILParameterizedType.of(CUSTOM, singletonList(typeName)));
        Function<String, FunctionCall> getCall = (String f) -> new FunctionCall(of(f), of(accessor));

        if (runtimeType.equals(ASN1Integer.class.getSimpleName())) {
            return Collections.singletonList(getCall.apply(GET_VALUE));
        } else if (runtimeType.equals(ASN1Boolean.class.getSimpleName())) {
            return Collections.singletonList(getCall.apply(GET_VALUE));
        } else if (runtimeType.equals(ASN1EnumeratedType.class.getSimpleName())) {
            return Collections.singletonList(getCall.apply(GET_VALUE));
        } else if (runtimeType.equals(ASN1Null.class.getSimpleName())) {
            return Collections.singletonList(getCall.apply(GET_VALUE));
        } else if (runtimeType.equals(ASN1ObjectIdentifier.class.getSimpleName())) {
            return Collections.singletonList(new FunctionCall.ToArray(ILType.of(INTEGER), getCall.apply(GET_VALUE)));
        } else if (runtimeType.equals(ASN1RelativeOID.class.getSimpleName())) {
            return Collections.singletonList(new FunctionCall.ToArray(ILType.of(INTEGER), getCall.apply(GET_VALUE)));
        } else if (runtimeType.equals(ASN1IRI.class.getSimpleName())) {
            return Collections.singletonList(new FunctionCall.ToArray(ILType.of(STRING), getCall.apply(GET_VALUE)));
        } else if (runtimeType.equals(ASN1RelativeIRI.class.getSimpleName())) {
            return Collections.singletonList(new FunctionCall.ToArray(ILType.of(STRING), getCall.apply(GET_VALUE)));
        } else if (runtimeType.equals(ASN1BitString.class.getSimpleName())) {
            return List.of(getCall.apply(GET_VALUE), getCall.apply("getUnusedBits"));
        } else if (runtimeType.equals(ASN1SequenceOf.class.getSimpleName()) ||
                runtimeType.equals(ASN1SetOf.class.getSimpleName())) {
            return Collections.singletonList(getCall.apply(GET_VALUES));
        } else if (runtimeType.equals(ASN1Sequence.class.getSimpleName()) ||
                runtimeType.equals(ASN1Set.class.getSimpleName())) {
            var associations = new HashSet<Tuple2<Expression, Expression>>();

            ((SequenceType) ctx.resolveTypeReference(component.getComponentType())).getAllComponents().stream()
                    .map(c -> c.getNamedType().getName())
                    .map(n -> new Tuple2(ILValue.of(n), getCall.apply("get" + initCap(n))))
                    .forEach(associations::add);

            return Collections.singletonList(new ILMapValue(ILType.of(ILBuiltinType.STRING),
                    ILParameterizedType.of(CUSTOM, singletonList(ASN1Type.class.getSimpleName())), associations));
        }

        throw new IllegalCompilerStateException("Unsupported type: " + runtimeType);
    }

    private Tuple2<String, String> buildExpressionFunction(Module module, CompiledType compiledType,
            BooleanExpression expression) {
        String expressionSym = module.generateSymbol("_expression");

        // @formatter:off
        module.function()
                .returnType(ILType.of(BOOLEAN))
                .name(expressionSym)
                .parameters(getParameterDefinition(compiledType))
                .statements()
                    .returnExpression(expression)
                    .build()
                .build();
        // @formatter:on

        return Tuple2.of(expressionSym, ctx.getRuntimeType(compiledType.getType()));
    }

    private List<Parameter> getParameterDefinition(CompiledType compiledType) {
        String runtimeType = ctx.getRuntimeType(compiledType.getType());
        List<Parameter> parameters;

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
            parameters = asList(Parameter.of(ILType.of(BYTE_ARRAY), VALUE),
                    Parameter.of(ILType.of(INTEGER), "unusedBits"));
        } else if (runtimeType.equals(ASN1OctetString.class.getSimpleName())) {
            parameters = getParameter(BYTE_ARRAY);
        } else if (runtimeType.equals(ASN1Sequence.class.getSimpleName()) ||
                runtimeType.equals(ASN1Set.class.getSimpleName())) {
            parameters = singletonList(getMapParameter());
        } else if (runtimeType.equals(ASN1SequenceOf.class.getSimpleName()) ||
                runtimeType.equals(ASN1SetOf.class.getSimpleName())) {
            var contentType = ((CompiledCollectionOfType) ctx.getCompiledBaseType(compiledType))
                    .getContentType().getType();
            List<String> typeParameter = ctx.getParameterizedType(contentType)
                    .stream()
                    .collect(Collectors.toList());

            parameters = singletonList(
                    new Parameter(new ILParameterizedType(ILBuiltinType.LIST, typeParameter), VALUES));
        } else {
            throw new CompilerException("Unsupported runtimeType %s", runtimeType);
        }

        return parameters;
    }

    private Parameter getMapParameter() {
        return Parameter.of(ILParameterizedType.of(ILBuiltinType.MAP,
                singletonList(String.class.getSimpleName()),
                singletonList(ASN1Type.class.getSimpleName())), VALUES);
    }

    private List<Parameter> getParameter(ILBuiltinType builtinType) {
        return singletonList(Parameter.of(ILType.of(builtinType), VALUE));
    }

}
