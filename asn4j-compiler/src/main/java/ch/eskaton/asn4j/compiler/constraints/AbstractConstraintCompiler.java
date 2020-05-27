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
import ch.eskaton.asn4j.compiler.constraints.ast.AllValuesNode;
import ch.eskaton.asn4j.compiler.constraints.ast.BinOpNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.NodeType;
import ch.eskaton.asn4j.compiler.constraints.ast.OpNode;
import ch.eskaton.asn4j.compiler.constraints.ast.SizeNode;
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
import ch.eskaton.asn4j.parser.ast.ElementSetSpecsNode;
import ch.eskaton.asn4j.parser.ast.SetSpecsNode;
import ch.eskaton.asn4j.parser.ast.constraints.Constraint;
import ch.eskaton.asn4j.parser.ast.constraints.ContainedSubtype;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.constraints.SizeConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SubtypeConstraint;
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.commons.collections.Tuple3;
import ch.eskaton.commons.functional.TriFunction;
import ch.eskaton.commons.utils.Dispatcher;
import ch.eskaton.commons.utils.OptionalUtils;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static ch.eskaton.asn4j.compiler.constraints.Constants.FUNC_CHECK_CONSTRAINT_VALUE;
import static ch.eskaton.asn4j.compiler.constraints.Constants.GET_VALUE;
import static ch.eskaton.asn4j.compiler.constraints.ConstraintUtils.throwUnimplementedNodeType;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.COMPLEMENT;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.INTERSECTION;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.NEGATION;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.UNION;
import static ch.eskaton.asn4j.compiler.il.ILBuiltinType.BOOLEAN;
import static ch.eskaton.asn4j.compiler.utils.TypeFormatter.formatType;
import static ch.eskaton.asn4j.parser.NoPosition.NO_POSITION;
import static java.util.Optional.of;

public abstract class AbstractConstraintCompiler {

    private Dispatcher<Elements, Class<? extends Elements>, Tuple3<CompiledType,
            ? extends Elements, Optional<Bounds>>, Node> dispatcher;

    protected CompilerContext ctx;

    public AbstractConstraintCompiler(CompilerContext ctx) {
        this.ctx = ctx;
        this.dispatcher = new Dispatcher<Elements, Class<? extends Elements>, Tuple3<CompiledType,
                ? extends Elements, Optional<Bounds>>, Node>()
                .withException((e) -> new CompilerException("Invalid constraint %s for %s type",
                        e.getClass().getSimpleName(), getTypeName()));
    }

    protected abstract String getTypeName();

    public Dispatcher<Elements, Class<? extends Elements>, Tuple3<CompiledType, ? extends Elements, Optional<Bounds>>,
            Node> getDispatcher() {
        return dispatcher;
    }

    protected  <T extends Elements> Node dispatchToCalculate(Class<T> clazz,
            TriFunction<CompiledType, T, Optional<Bounds>, Node> function,
            Optional<Tuple3<CompiledType, ? extends Elements, Optional<Bounds>>> args) {
        return function.apply(args.get().get_1(), clazz.cast(args.get().get_2()), args.get().get_3());
    }

    protected Node calculateElements(CompiledType baseType, Elements elements, Optional<Bounds> bounds) {
        return getDispatcher().withComparator((t, c) -> c.isInstance(t))
                .execute(elements, new Tuple3<>(baseType, elements, bounds));
    }

    public ConstraintDefinition compileConstraint(CompiledType baseType, SubtypeConstraint subtypeConstraint,
            Optional<Bounds> bounds) {
        ElementSetSpecsNode setSpecs = subtypeConstraint.getElementSetSpecs();
        Node root = compileConstraint(baseType, setSpecs.getRootElements(), bounds);
        Node extension = null;

        if (setSpecs.hasExtensionElements()) {
            extension = compileConstraint(baseType, setSpecs.getExtensionElements(), bounds);
        }

        return new ConstraintDefinition(root, extension).extensible(setSpecs.hasExtensionMarker());
    }

    protected ConstraintDefinition compileConstraint(CompiledType baseType, SizeConstraint sizeConstraint,
            Optional<Bounds> bounds) {
        return new ConstraintDefinition(calculateElements(baseType, sizeConstraint, bounds));
    }

    ConstraintDefinition compileConstraints(Type node, CompiledType baseType) {
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

            if (compiledType.getConstraintDefinition() != null) {
                definitions.addLast(compiledType.getConstraintDefinition());
            }

            node = compiledType.getType();
        } while (!compiledType.equals(baseType));

        constraint.ifPresent(c -> {
            Optional<Bounds> bounds = getBounds(Optional.ofNullable(definitions.peek()));
            definitions.addLast(compileConstraints(baseType, c, bounds));
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

    @SuppressWarnings("squid:S1172")
    Optional<Bounds> getBounds(Optional<ConstraintDefinition> constraint) {
        return Optional.empty();
    }

    ConstraintDefinition compileConstraints(CompiledType baseType, List<Constraint> constraints,
            Optional<Bounds> bounds) {
        ConstraintDefinition constraintDef = null;

        for (Constraint constraint : constraints) {
            if (constraint instanceof SubtypeConstraint) {
                constraintDef = getConstraintDefinition(baseType, bounds, constraintDef, (SubtypeConstraint) constraint,
                        this::compileConstraint);
            } else if (constraint instanceof SizeConstraint) {
                constraintDef = getConstraintDefinition(baseType, bounds, constraintDef, (SizeConstraint) constraint,
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

    protected Node compileConstraint(CompiledType baseType, ElementSet set, Optional<Bounds> bounds) {
        List<Elements> operands = set.getOperands();

        switch (set.getOperation()) {
            case ALL:
                return calculateInversion(compileConstraint(baseType, (ElementSet) operands.get(0), bounds));

            case EXCLUDE:
                if (operands.size() == 1) {
                    // ALL EXCEPT
                    return calculateElements(baseType, operands.get(0), bounds);
                } else {
                    return calculateExclude(calculateElements(baseType, operands.get(0), bounds),
                            calculateElements(baseType, operands.get(1), bounds));
                }

            case INTERSECTION:
                return calculateIntersection(baseType, operands, bounds);

            case UNION:
                return calculateUnion(baseType, operands, bounds);

            default:
                throw new IllegalStateException("Unimplemented node type " + set.getOperation());
        }
    }

    protected Node calculateIntersection(CompiledType baseType, List<Elements> elements, Optional<Bounds> bounds) {
        return calculateBinOp(baseType, elements, bounds, INTERSECTION);
    }

    protected Node calculateUnion(CompiledType baseType, List<Elements> elements, Optional<Bounds> bounds) {
        return calculateBinOp(baseType, elements, bounds, UNION);
    }

    protected Node calculateBinOp(CompiledType baseType, List<Elements> elements, Optional<Bounds> bounds,
            NodeType type) {
        Node node = null;

        for (Elements element : elements) {
            Node tmpNode = calculateElements(baseType, element, bounds);

            if (node == null) {
                node = tmpNode;
            } else {
                node = new BinOpNode(type, node, tmpNode);
            }
        }

        return node;
    }

    protected Node calculateInversion(Node node) {
        return new OpNode(NEGATION, node);
    }

    protected Node calculateExclude(Node values1, Node values2) {
        return new BinOpNode(COMPLEMENT, values1, values2);
    }

    protected Node calculateContainedSubtype(CompiledType compiledType, ContainedSubtype elements,
            Optional<Bounds> bounds) {
        CompiledType compiledParentType;
        var parent = elements.getType();
        var constraints = new ArrayDeque<Node>();

        do {
            compiledParentType = ctx.getCompiledType(parent);

            if (!isAssignable(compiledType, compiledParentType)) {
                throw new CompilerException("Type %s can't be used in INCLUDES constraint of type %s",
                        formatType(ctx, compiledParentType.getType()), formatType(ctx, compiledType.getType()));
            }

            if (compiledParentType.getConstraintDefinition() != null) {
                constraints.push(compiledParentType.getConstraintDefinition().getRoots());
            }

            parent = compiledParentType.getType();
        } while (parent instanceof TypeReference);

        if (constraints.isEmpty()) {
            return new AllValuesNode();
        } else if (constraints.size() == 1) {
            return constraints.pop();
        } else {
            Node node1 = constraints.pop();
            Node node2 = constraints.pop();

            do {
                node1 = new BinOpNode(NodeType.INTERSECTION, node1, node2);

                if (constraints.isEmpty()) {
                    break;
                }

                node2 = constraints.pop();
            } while (true);

            return node1;
        }
    }

    protected boolean isAssignable(CompiledType compiledType, CompiledType compiledParentType) {
        return compiledType.getType().getClass()
                .isAssignableFrom(ctx.getCompiledBaseType(compiledParentType).getType().getClass());
    }

    protected SizeNode calculateSize(CompiledType baseType, SizeConstraint sizeConstraint, Optional<Bounds> bounds) {
        var constraint = sizeConstraint.getConstraint();

        if (constraint instanceof SubtypeConstraint) {
            SetSpecsNode setSpecs = ((SubtypeConstraint) constraint).getElementSetSpecs();

            Node node = new IntegerConstraintCompiler(ctx).calculateElements(
                    ctx.getCompiledBaseType(new IntegerType(NO_POSITION)), setSpecs.getRootElements(),
                    Optional.of(bounds.map(b -> new IntegerValueBounds(Math.max(0, ((SizeBounds) b).getMinSize()),
                            ((SizeBounds) b).getMaxSize()))
                            .orElse(new IntegerValueBounds(0L, Long.MAX_VALUE))));

            Optional<SizeNode> maybeSizes = new SizeVisitor().visit(node);

            if (!maybeSizes.isPresent() || maybeSizes.get().getSize().isEmpty()) {
                throw new CompilerException(setSpecs.getPosition(),
                        "Invalid SIZE constraint. It contains no restrictions.");
            }

            return maybeSizes.get();
        } else {
            throw new CompilerException("Constraints of type %s not yet supported",
                    constraint.getClass().getSimpleName());
        }
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

    protected String getTypeName(Type type) {
        if (type == null) {
            return "";
        } else if (type instanceof TypeReference) {
            return ((TypeReference) type).getType();
        } else {
            return ctx.getRuntimeType(type.getClass());
        }
    }

    protected FunctionBuilder generateCheckConstraintValue(Module module, Parameter... parameters) {
        FunctionBuilder builder = module.function()
                .name("checkConstraintValue")
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
                .name("doCheckConstraint")
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
