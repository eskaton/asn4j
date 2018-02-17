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

import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.TypeResolver;
import ch.eskaton.asn4j.compiler.java.JavaClass;
import ch.eskaton.asn4j.parser.ast.SetSpecsNode;
import ch.eskaton.asn4j.parser.ast.TypeAssignmentNode;
import ch.eskaton.asn4j.parser.ast.constraints.Constraint;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.SubtypeConstraint;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.types.UsefulType;

import java.util.Collection;
import java.util.List;
import java.util.Stack;

public abstract class AbstractConstraintCompiler<T> {

    // protected ConstraintCompiler constraintCompiler;

    protected TypeResolver typeResolver;

    public AbstractConstraintCompiler(ConstraintCompiler constraintCompiler, TypeResolver typeResolver) {
    	// this.constraintCompiler = constraintCompiler;
    	this.typeResolver = typeResolver;
    }

    public Collection<T> compileConstraint(SetSpecsNode setSpecs, boolean includeAddition) throws CompilerException {
    	Collection<T> root = compileConstraint(setSpecs.getRootElements());

    	if (includeAddition) {
    		ElementSet additionalElements = setSpecs.getAdditionalElements();

    		if (additionalElements != null) {
    			Collection<T> extension = compileConstraint(additionalElements);
    			root.addAll(extension);
    		}
    	}

    	return root;
    }

    Collection<T> compileConstraints(Type node, Type base) throws CompilerException {
    	Stack<Collection<T>> cons = new Stack<>();

    	boolean includeAdditions = true;

    	while (true) {
    		if (node.hasConstraint()) {
    			cons.push(compileConstraints(node.getConstraints(), includeAdditions));
    		}

    		includeAdditions = false;

    		if (base.equals(node)) {
    			break;
    		}

    		if (node instanceof UsefulType) {
    			break;
    		} else if (node instanceof TypeReference) {
    			TypeAssignmentNode type = typeResolver.getType(((TypeReference) node).getType());

    			if (type == null) {
    				throw new CompilerException("Referenced type " + ((TypeReference) node).getType() + " not found");
    			}

    			node = type.getType();
    		} else {
    			throw new CompilerException("not yet supported");
    		}

    	}

    	if (cons.size() == 1) {
    		return cons.pop();
    	} else if (cons.size() > 1) {
    		Collection<T> op1 = cons.pop();
    		Collection<T> op2 = cons.pop();

    		do {
    			op1 = calculateIntersection(op1, op2);

    			if (cons.isEmpty()) {
    				break;
    			}

    			op2 = cons.pop();
    		} while (true);

    		return op1;
    	}

    	return null;
    }

    Collection<T> compileConstraints(List<Constraint> constraints,
    		boolean includeAdditions) throws CompilerException {
    	Collection<T> intersection = null;

    	for (Constraint constraint : constraints) {
    		SetSpecsNode setSpecs = ((SubtypeConstraint) constraint).getElementSetSpecs();

    		if (constraint instanceof SubtypeConstraint) {
    			if (intersection == null) {
    				intersection = compileConstraint(setSpecs, includeAdditions);
    				includeAdditions = false;
    			} else {
    				intersection.retainAll(compileConstraint(setSpecs, includeAdditions));
    				if (intersection.isEmpty()) {
    					return intersection;
    				}
    			}
    		} else {
                throw new CompilerException("Constraints of type " + constraint.getClass().getName()
                                                    + " not yet supported");
    		}
    	}

    	return intersection;
    }

    protected abstract Collection<T> compileConstraint(ElementSet set)
    		throws CompilerException;

    protected abstract Collection<T> calculateIntersection(Collection<?> op1,
    		Collection<?> op2) throws CompilerException;

    protected abstract void addConstraint(JavaClass clazz, Collection<?> values)
    		throws CompilerException;

}
