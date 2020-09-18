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
import ch.eskaton.asn4j.compiler.constraints.ast.BinOpNode;
import ch.eskaton.asn4j.compiler.constraints.ast.ComponentNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.NodeType;
import ch.eskaton.asn4j.compiler.constraints.ast.OpNode;
import ch.eskaton.asn4j.compiler.constraints.ast.WithComponentsNode;
import ch.eskaton.asn4j.compiler.constraints.elements.ElementSetCompiler;
import ch.eskaton.asn4j.compiler.il.BinaryBooleanExpression;
import ch.eskaton.asn4j.compiler.il.BinaryOperator;
import ch.eskaton.asn4j.compiler.il.BooleanExpression;
import ch.eskaton.asn4j.compiler.il.FunctionCall;
import ch.eskaton.asn4j.compiler.il.ILType;
import ch.eskaton.asn4j.compiler.il.ILVisibility;
import ch.eskaton.asn4j.compiler.il.Module;
import ch.eskaton.asn4j.compiler.il.NegationExpression;
import ch.eskaton.asn4j.compiler.il.Parameter;
import ch.eskaton.asn4j.compiler.il.builder.FunctionBuilder;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.compiler.results.HasComponents;
import ch.eskaton.asn4j.parser.ast.SimpleTableConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.Constraint;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.constraints.PresenceConstraint.PresenceType;
import ch.eskaton.asn4j.parser.ast.constraints.SizeConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SubtypeConstraint;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.runtime.types.TypeName;
import ch.eskaton.commons.collections.Tuple3;
import ch.eskaton.commons.functional.TriFunction;
import ch.eskaton.commons.utils.Dispatcher;
import ch.eskaton.commons.utils.OptionalUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static ch.eskaton.asn4j.compiler.constraints.Constants.FUNC_CHECK_CONSTRAINT_VALUE;
import static ch.eskaton.asn4j.compiler.constraints.Constants.FUNC_DO_CHECK_CONSTRAINT;
import static ch.eskaton.asn4j.compiler.constraints.Constants.GET_VALUE;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintUtils.throwUnimplementedNodeType;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.INTERSECTION;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.BOOLEAN;
import static java.util.Optional.of;

public abstract class AbstractConstraintCompiler {

    private Dispatcher<Elements, Class<? extends Elements>, Tuple3<CompiledType,
            ? extends Elements, Optional<Bounds>>, Node> dispatcher;

    protected CompilerContext ctx;

    public AbstractConstraintCompiler(CompilerContext ctx) {
        this.ctx = ctx;
        this.dispatcher = new Dispatcher<Elements, Class<? extends Elements>, Tuple3<CompiledType,
                ? extends Elements, Optional<Bounds>>, Node>()
                .withComparator((t, c) -> c.isInstance(t))
                .withException(e -> new CompilerException("Invalid constraint %s for %s type",
                        e.getClass().getSimpleName(), getTypeName().getName()));

        addConstraintHandler(ElementSet.class, new ElementSetCompiler(dispatcher)::compile);
    }

    protected abstract TypeName getTypeName();

    public Dispatcher<Elements, Class<? extends Elements>, Tuple3<CompiledType, ? extends Elements, Optional<Bounds>>,
            Node> getDispatcher() {
        return dispatcher;
    }

    protected <T extends Elements> Node dispatchToCompiler(Class<T> clazz,
            TriFunction<CompiledType, T, Optional<Bounds>, Node> function,
            Optional<Tuple3<CompiledType, ? extends Elements, Optional<Bounds>>> maybeArgs) {
        var args = maybeArgs.orElseThrow(
                () -> new IllegalCompilerStateException("Arguments in dispatchToCompiler may not be null"));

        return function.apply(args.get_1(), clazz.cast(args.get_2()), args.get_3());
    }

    protected <T extends Elements> void addConstraintHandler(Class<T> clazz,
            TriFunction<CompiledType, T, Optional<Bounds>, Node> function) {
        getDispatcher().withCase(clazz, a -> dispatchToCompiler(clazz, function, a));
    }

    public ConstraintDefinition compileConstraint(CompiledType baseType, SubtypeConstraint subtypeConstraint,
            Optional<Bounds> bounds) {
        var setSpecs = subtypeConstraint.getElementSetSpecs();
        var rootElements = setSpecs.getRootElements();
        Node root = dispatcher.execute(rootElements, Tuple3.of(baseType, rootElements, bounds));
        Node extension = null;

        if (setSpecs.hasExtensionElements()) {
            var extensionElements = setSpecs.getExtensionElements();
            extension = dispatcher.execute(extensionElements, Tuple3.of(baseType, extensionElements, bounds));
        }

        return new ConstraintDefinition(root, extension).extensible(setSpecs.hasExtensionMarker());
    }

    protected ConstraintDefinition compileConstraint(CompiledType baseType, SizeConstraint sizeConstraint,
            Optional<Bounds> bounds) {
        var node = dispatcher.execute(sizeConstraint, Tuple3.of(baseType, sizeConstraint, bounds));

        return new ConstraintDefinition(node);
    }

    private ConstraintDefinition compileConstraint(CompiledType baseType,
            SimpleTableConstraint simpleTableConstraint, Optional<Bounds> bounds) {
        var setSpec = simpleTableConstraint.getObjectSetSpec();
        var rootElements = setSpec.getRootElements();
        Node root = dispatcher.execute(rootElements, Tuple3.of(baseType, rootElements, bounds));
        Node extension = null;

        if (setSpec.hasExtensionElements()) {
            var extensionElements = setSpec.getExtensionElements();

            extension = dispatcher.execute(extensionElements, Tuple3.of(baseType, extensionElements, bounds));
        }

        return new ConstraintDefinition(root, extension).extensible(setSpec.hasExtensionMarker());
    }

    ConstraintDefinition compileComponentConstraints(Type node, CompiledType baseType) {
        LinkedList<ConstraintDefinition> definitions = new LinkedList<>();
        Optional<List<Constraint>> constraint = Optional.ofNullable(node.getConstraints());
        ConstraintDefinition definition = null;
        CompiledType compiledType;

        do {
            if (node.equals(baseType.getType())) {
                compiledType = baseType;
            } else {
                compiledType = ctx.getCompiledType(node);
            }

            if (compiledType instanceof HasComponents) {
                compileComponentConstraints(compiledType).ifPresent(definitions::addLast);
            }

            var maybeConstraintDefinition = compiledType.getConstraintDefinition();

            if (maybeConstraintDefinition.isPresent()) {
                definitions.addLast(maybeConstraintDefinition.get());
            }

            node = compiledType.getType();
        } while (!compiledType.equals(baseType));

        constraint.ifPresent(c -> {
            Optional<Bounds> bounds = getBounds(Optional.ofNullable(definitions.peek()));
            definitions.addLast(compileComponentConstraints(baseType, c, bounds));
        });

        if (definitions.size() == 1) {
            definition = definitions.pop();
        } else if (definitions.size() > 1) {
            ConstraintDefinition op1 = definitions.pop();
            ConstraintDefinition op2 = definitions.pop();

            do {
                op1 = op1.serialApplication(op2);

                if (definitions.isEmpty()) {
                    break;
                }

                op2 = definitions.pop();
            } while (true);

            definition = op1;
        }

        if (definition != null) {
            definition.optimize(this::optimize);
        }

        return definition;
    }

    protected Optional<ConstraintDefinition> compileComponentConstraints(CompiledType compiledType) {
        return ((HasComponents) compiledType).getComponents().stream()
                .filter(t -> t.get_2().getConstraintDefinition().isPresent())
                .map(t -> {
                    var name = t.get_1();
                    var compiledComponent = t.get_2();
                    var constraintDefinition = compiledComponent.getConstraintDefinition().get();
                    var rootDefinitions = constraintDefinition.getRoots();
                    var extensionsDefinitions = constraintDefinition.getExtensions();
                    var roots = createComponentsNode(name, compiledComponent, rootDefinitions);
                    Node extensions = null;

                    if (extensionsDefinitions != null) {
                        extensions = createComponentsNode(name, compiledComponent, extensionsDefinitions);
                    }

                    return new ConstraintDefinition(roots, extensions, constraintDefinition.isExtensible());
                }).reduce((op1, op2) -> {
                    var roots = new BinOpNode(getComponentCombinationOp(), op1.getRoots(), op2.getRoots());
                    var extensions = op1.getExtensions();

                    if (extensions == null) {
                        extensions = op2.getExtensions();
                    } else {
                        if (op2.getExtensions() != null) {
                            extensions = new BinOpNode(getComponentCombinationOp(), extensions, op2.getExtensions());
                        }
                    }

                    var extensible = op1.isExtensible() || op2.isExtensible();

                    return new ConstraintDefinition(roots, extensions, extensible);
                });
    }

    private WithComponentsNode createComponentsNode(String name, CompiledType compiledComponent,
            Node constraintDefinition) {
        var type = compiledComponent.getType();
        var component = new ComponentNode(name, type, constraintDefinition, PresenceType.OPTIONAL);

        return new WithComponentsNode(Set.of(component));
    }

    protected NodeType getComponentCombinationOp() {
        return INTERSECTION;
    }

    @SuppressWarnings("squid:S1172")
    Optional<Bounds> getBounds(Optional<ConstraintDefinition> constraint) {
        return Optional.empty();
    }

    ConstraintDefinition compileComponentConstraints(CompiledType baseType, List<Constraint> constraints,
            Optional<Bounds> bounds) {
        ConstraintDefinition constraintDef = null;

        for (Constraint constraint : constraints) {
            if (constraint instanceof SubtypeConstraint) {
                constraintDef = getConstraintDefinition(baseType, bounds, constraintDef, (SubtypeConstraint) constraint,
                        this::compileConstraint);
            } else if (constraint instanceof SizeConstraint) {
                constraintDef = getConstraintDefinition(baseType, bounds, constraintDef, (SizeConstraint) constraint,
                        this::compileConstraint);
            } else if (constraint instanceof SimpleTableConstraint) {
                constraintDef = getConstraintDefinition(baseType, bounds, constraintDef, (SimpleTableConstraint) constraint,
                        this::compileConstraint);
            } else {
                throw new CompilerException("Constraints of type %s not yet supported",
                        constraint.getClass().getSimpleName());
            }
        }

        return constraintDef;
    }

    private <C extends Constraint> ConstraintDefinition getConstraintDefinition(CompiledType baseType,
            Optional<Bounds> bounds, ConstraintDefinition constraintDef, C constraint,
            TriFunction<CompiledType, C, Optional<Bounds>, ConstraintDefinition> compile) {
        if (constraintDef == null) {
            constraintDef = compile.apply(baseType, constraint, bounds);
        } else {
            constraintDef = constraintDef.serialApplication(compile.apply(baseType, constraint,
                    getBounds(Optional.of(constraintDef))));
        }

        return constraintDef;
    }

    protected abstract void addConstraint(CompiledType type, Module module, ConstraintDefinition definition);

    protected void addConstraintCondition(CompiledType compiledType, ConstraintDefinition definition, FunctionBuilder builder) {
        if (definition.isExtensible()) {
            builder.statements().returnValue(Boolean.TRUE);
        } else {
            buildExpression(builder.getModule(), compiledType, definition.getRoots()).ifPresentOrElse(
                    e -> builder.statements().returnExpression(e),
                    () -> builder.statements().returnValue(Boolean.TRUE));
        }
    }

    protected FunctionBuilder generateCheckConstraintValue(Module module, Parameter... parameters) {
        FunctionBuilder builder = module.function()
                .name(FUNC_CHECK_CONSTRAINT_VALUE)
                .returnType(ILType.of(BOOLEAN));

        Arrays.stream(parameters).forEach(builder::parameter);

        return builder;
    }

    protected Node optimize(Node node) {
        return node;
    }

    protected void generateDoCheckConstraint(Module module) {
        // @formatter:off
        module.function()
                .name(FUNC_DO_CHECK_CONSTRAINT)
                .overriden(true)
                .visibility(ILVisibility.PUBLIC)
                .returnType(ILType.of(BOOLEAN))
                .statements()
                    .returnExpression(generateCheckConstraintCall())
                    .build()
                .build();
        // @formatter:on
    }

    protected FunctionCall generateCheckConstraintCall() {
        return new FunctionCall(of(FUNC_CHECK_CONSTRAINT_VALUE), new FunctionCall(of(GET_VALUE)));
    }

    protected Optional<BooleanExpression> buildExpression(Module module, CompiledType compiledType, Node node) {
        switch (node.getType()) {
            case ALL_VALUES:
                return Optional.empty();
            case UNION:
                return OptionalUtils.combine(
                        buildExpression(module, compiledType, ((BinOpNode) node).getLeft()),
                        buildExpression(module, compiledType, ((BinOpNode) node).getRight()),
                        getBinOperation(BinaryOperator.OR));
            case INTERSECTION:
                return OptionalUtils.combine(
                        buildExpression(module, compiledType, ((BinOpNode) node).getLeft()),
                        buildExpression(module, compiledType, ((BinOpNode) node).getRight()),
                        getBinOperation(BinaryOperator.AND));
            case COMPLEMENT:
                return OptionalUtils.combine(
                        buildExpression(module, compiledType, ((BinOpNode) node).getLeft()),
                        buildExpression(module, compiledType, ((BinOpNode) node).getRight()).map(this::negate),
                        getBinOperation(BinaryOperator.AND));
            case NEGATION:
                return buildExpression(module, compiledType, ((OpNode) node).getNode()).map(this::negate);
            default:
                return throwUnimplementedNodeType(node);
        }
    }

    private BooleanExpression negate(BooleanExpression expr) {
        return new NegationExpression(expr);
    }

    private java.util.function.BinaryOperator<BooleanExpression> getBinOperation(BinaryOperator operator) {
        return (BooleanExpression a, BooleanExpression b) -> new BinaryBooleanExpression(operator, a, b);
    }

}
