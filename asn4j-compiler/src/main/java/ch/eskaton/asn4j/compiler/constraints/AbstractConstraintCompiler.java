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
import ch.eskaton.asn4j.compiler.java.JavaClass;
import ch.eskaton.asn4j.parser.ast.SetSpecsNode;
import ch.eskaton.asn4j.parser.ast.TypeAssignmentNode;
import ch.eskaton.asn4j.parser.ast.constraints.Constraint;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.constraints.SubtypeConstraint;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.types.UsefulType;

import java.util.Collection;
import java.util.List;
import java.util.Stack;

public abstract class AbstractConstraintCompiler<V, C extends Collection<V>, T extends ConstraintValues<V, C, T>,
        D extends ConstraintDefinition<V, C, T, D>> {

    protected CompilerContext ctx;

    public AbstractConstraintCompiler(CompilerContext ctx) {
        this.ctx = ctx;
    }

    public D compileConstraint(Type base, SetSpecsNode setSpecs) throws CompilerException {
        T root = compileConstraint(base, setSpecs.getRootElements());
        T extension = null;

        if (setSpecs.hasExtensionElements()) {
            extension = compileConstraint(base, setSpecs.getExtensionElements());
        }

        return createDefinition(root, extension).extensible(setSpecs.hasExtensionMarker());
    }

    D compileConstraints(Type node, Type base) throws CompilerException {
        Stack<D> cons = new Stack<>();

        while (true) {
            if (node.hasConstraint()) {
                cons.push(compileConstraints(base, node.getConstraints()));
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

        if (cons.size() == 1) {
            return cons.pop();
        } else if (cons.size() > 1) {
            D op1 = cons.pop();
            D op2 = cons.pop();

            do {
                op1 = op1.serialApplication(op2);

                if (cons.isEmpty()) {
                    break;
                }

                op2 = cons.pop();
            } while (true);

            return op1;
        }

        return null;
    }

    D compileConstraints(Type base, List<Constraint> constraints) throws CompilerException {
        D constraintDef = null;

        for (Constraint constraint : constraints) {
            if (constraint instanceof SubtypeConstraint) {
                SetSpecsNode setSpecs = ((SubtypeConstraint) constraint).getElementSetSpecs();

                if (constraintDef == null) {
                    constraintDef = compileConstraint(base, setSpecs);
                } else {
                    constraintDef.intersection(compileConstraint(base, setSpecs));
                }
            } else {
                throw new CompilerException("Constraints of type %s not yet supported",
                        constraint.getClass().getSimpleName());
            }
        }

        return constraintDef;
    }

    protected T compileConstraint(Type base, ElementSet set) throws CompilerException {
        List<Elements> operands = set.getOperands();

        switch (set.getOperation()) {
            case All:
                return calculateInversion(compileConstraint(base, (ElementSet) operands.get(0)));

            case Exclude:
                if (operands.size() == 1) {
                    // ALL EXCEPT
                    return calculateElements(base, operands.get(0));
                } else {
                    return calculateExclude(calculateElements(base, operands.get(0)),
                            calculateElements(base, operands.get(1)));
                }

            case Intersection:
                return calculateIntersection(base, operands);

            case Union:
                return calculateUnion(base, operands);
        }

        return createValues();
    }

    protected T calculateIntersection(Type base, List<Elements> elements) throws CompilerException {
        T values1 = createValues();

        for (Elements e : elements) {
            T values2 = calculateElements(base, e);

            if (values1.isEmpty()) {
                values1 = values1.union(values2);
            } else {
                values1 = calculateIntersection(values1, values2);

                if (values1.isEmpty()) {
                    return values1;
                }
            }
        }

        return values1;
    }

    protected T calculateUnion(Type base, List<Elements> elements) throws CompilerException {
        T values = createValues();

        for (Elements e : elements) {
            values = values.union(calculateElements(base, e));
        }

        return values;
    }

    protected T calculateInversion(T values) {
        return values.invert();
    }

    protected T calculateExclude(T values1, T values2) throws CompilerException {
        return values1.exclude(values2);
    }

    protected abstract D createDefinition(T root, T extension);

    protected abstract T createValues();

    protected abstract T calculateElements(Type base, Elements elements) throws CompilerException;

    protected T calculateIntersection(T values1, T values2) throws CompilerException {
        return values1.intersection(values2);
    }

    protected abstract void addConstraint(JavaClass javaClass, ConstraintDefinition definition) throws CompilerException;

}
