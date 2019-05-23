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
import ch.eskaton.asn4j.compiler.constraints.ast.*;
import ch.eskaton.asn4j.compiler.java.JavaClass;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.SetSpecsNode;
import ch.eskaton.asn4j.parser.ast.TypeAssignmentNode;
import ch.eskaton.asn4j.parser.ast.constraints.Constraint;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.constraints.SubtypeConstraint;
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.types.UsefulType;
import ch.eskaton.commons.utils.OptionalUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.function.BiFunction;

import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.*;
import static ch.eskaton.asn4j.parser.NoPosition.NO_POSITION;

public abstract class AbstractConstraintCompiler {

    protected CompilerContext ctx;

    public AbstractConstraintCompiler(CompilerContext ctx) {
        this.ctx = ctx;
    }

    public ConstraintDefinition compileConstraint(Type base, SetSpecsNode setSpecs, Optional<Bounds> bounds)
            throws CompilerException {
        Node root = compileConstraint(base, setSpecs.getRootElements(), bounds);
        Node extension = null;

        if (setSpecs.hasExtensionElements()) {
            extension = compileConstraint(base, setSpecs.getExtensionElements(), bounds);
        }

        return new ConstraintDefinition(root, extension).extensible(setSpecs.hasExtensionMarker());
    }

    ConstraintDefinition compileConstraints(Type node, Type base) throws CompilerException {
        LinkedList<ConstraintDefinition> definitions = new LinkedList<>();
        Stack<List<Constraint>> constraints = new Stack<>();

        while (true) {
            if (node.hasConstraint()) {
                constraints.push(node.getConstraints());
            }

            if (base.equals(node)) {
                break;
            }

            if (node instanceof UsefulType) {
                break;
            } else if (node instanceof TypeReference) {
                TypeAssignmentNode type = ctx.getTypeAssignment((TypeReference) node);

                if (type == null) {
                    throw new CompilerException("Referenced type %s not found", ((TypeReference) node).getType());
                }

                node = type.getType();
            } else {
                throw new CompilerException("not yet supported");
            }
        }

        while (!constraints.isEmpty()) {
            Optional<Bounds> bounds = getBounds(Optional.ofNullable(definitions.peek()));
            definitions.addLast(compileConstraints(base, constraints.pop(), bounds));
        }

        if (definitions.size() == 1) {
            return definitions.pop();
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

            return op1;
        }

        return null;
    }

    abstract Optional<Bounds> getBounds(Optional<ConstraintDefinition> constraint);

    ConstraintDefinition compileConstraints(Type base, List<Constraint> constraints, Optional<Bounds> bounds)
            throws CompilerException {
        ConstraintDefinition constraintDef = null;

        for (Constraint constraint : constraints) {
            if (constraint instanceof SubtypeConstraint) {
                SetSpecsNode setSpecs = ((SubtypeConstraint) constraint).getElementSetSpecs();

                if (constraintDef == null) {
                    constraintDef = compileConstraint(base, setSpecs, bounds);
                } else {
                    constraintDef = constraintDef.serialApplication(compileConstraint(base, setSpecs,
                            getBounds(Optional.of(constraintDef))));
                }
            } else {
                throw new CompilerException("Constraints of type %s not yet supported",
                        constraint.getClass().getSimpleName());
            }
        }

        return constraintDef;
    }

    protected Node compileConstraint(Type base, ElementSet set, Optional<Bounds> bounds) throws CompilerException {
        List<Elements> operands = set.getOperands();

        switch (set.getOperation()) {
            case All:
                return calculateInversion(compileConstraint(base, (ElementSet) operands.get(0), bounds));

            case Exclude:
                if (operands.size() == 1) {
                    // ALL EXCEPT
                    return calculateElements(base, operands.get(0), bounds);
                } else {
                    return calculateExclude(calculateElements(base, operands.get(0), bounds),
                            calculateElements(base, operands.get(1), bounds));
                }

            case Intersection:
                return calculateIntersection(base, operands, bounds);

            case Union:
                return calculateUnion(base, operands, bounds);

            default:
                throw new IllegalStateException("Unimplemented node type " + set.getOperation());
        }
    }

    protected Node calculateIntersection(Type base, List<Elements> elements, Optional<Bounds> bounds)
            throws CompilerException {
        return calculateBinOp(base, elements, bounds, INTERSECTION);
    }

    protected Node calculateUnion(Type base, List<Elements> elements, Optional<Bounds> bounds) {
        return calculateBinOp(base, elements, bounds, UNION);
    }

    protected Node calculateBinOp(Type base, List<Elements> elements, Optional<Bounds> bounds, NodeType type) {
        Node node = null;

        for (Elements element : elements) {
            Node tmpNode = calculateElements(base, element, bounds);

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

    protected Node calculateExclude(Node values1, Node values2) throws CompilerException {
        return new BinOpNode(COMPLEMENT, values1, values2);
    }

    protected abstract Node calculateElements(Type base, Elements elements, Optional<Bounds> bounds)
            throws CompilerException;

    protected Node calculateContainedSubtype(Type base, Type type) throws CompilerException {
        if (type.equals(base)) {
            return new AllValuesNode();
        } else {
            Optional<CompiledType> maybeCompiledType = ctx.getCompiledType(type);

            if (maybeCompiledType.isPresent()) {
                CompiledType compiledType = maybeCompiledType.get();
                ConstraintDefinition constraintDefinition = compiledType.getConstraintDefinition();

                return constraintDefinition == null ? new AllValuesNode() : constraintDefinition.getRoots();
            }
        }

        throw new CompilerException("Failed to resolve contained subtype %s", type);
    }

    protected SizeNode calculateSize(Constraint constraint, Optional<Bounds> bounds) {
        if (constraint instanceof SubtypeConstraint) {
            SetSpecsNode setSpecs = ((SubtypeConstraint) constraint).getElementSetSpecs();

            Node node = new IntegerConstraintCompiler(ctx).calculateElements(new IntegerType(NO_POSITION),
                    setSpecs.getRootElements(), Optional.of(bounds.map(b ->
                            new IntegerValueBounds(((SizeBounds) b).getMinSize(), ((SizeBounds) b).getMaxSize()))
                            .orElse(new IntegerValueBounds(0L, Long.MAX_VALUE))));

            Optional<SizeNode> maybeSizes = new SizeVisitor().visit(node);

            if (!maybeSizes.isPresent() || maybeSizes.get().getSize().isEmpty()) {
                throw new CompilerException(setSpecs.getPosition(),
                        "Invalid SIZE constraint. It contains no restrications.");
            }

            return maybeSizes.get();
        } else {
            throw new CompilerException("Constraints of type %s not yet supported",
                    constraint.getClass().getSimpleName());
        }
    }

    protected abstract void addConstraint(JavaClass javaClass, ConstraintDefinition definition)
            throws CompilerException;

    protected void addConstraintCondition(ConstraintDefinition definition, JavaClass.BodyBuilder builder) {
        if (definition.isExtensible()) {
            builder.append("return true;");
        } else {
            Node roots = optimize(definition.getRoots());
            Optional<String> expression = buildExpression(roots);

            if (expression.isPresent()) {
                builder.append("if (" + expression.get() + ") {")
                        .append("\treturn true;")
                        .append("} else {")
                        .append("\treturn false;")
                        .append("}");
            } else {
                builder.append("return true;");
            }
        }
    }

    protected Node optimize(Node node) {
        return node;
    }

    protected Optional<String> buildExpression(Node node) {
        switch (node.getType()) {
            case ALL_VALUES:
                return Optional.empty();
            case UNION:
                return OptionalUtils.combine(buildExpression(((BinOpNode) node).getLeft()),
                        buildExpression(((BinOpNode) node).getRight()), getBinOperation("||"));
            case INTERSECTION:
                return OptionalUtils.combine(buildExpression(((BinOpNode) node).getLeft()),
                        buildExpression(((BinOpNode) node).getRight()), getBinOperation("&&"));
            case COMPLEMENT:
                return OptionalUtils.combine(buildExpression(((BinOpNode) node).getLeft()),
                        buildExpression(((BinOpNode) node).getRight()).map(this::negate),
                        getBinOperation("&&"));
            case NEGATION:
                return buildExpression(((OpNode) node).getNode()).map(this::negate);
            default:
                throw new IllegalStateException("Unimplemented node type: " + node.getType());
        }
    }

    private String negate(String expr) {
        return "(!" + expr + ")";
    }

    private BiFunction<String, String, String> getBinOperation(String operator) {
        return (String a, String b) -> "(" + a + " " + operator + " " + b + ")";
    }

}
