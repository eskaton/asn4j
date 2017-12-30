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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.TypeResolver;
import ch.eskaton.asn4j.compiler.java.JavaClass;
import ch.eskaton.asn4j.compiler.java.JavaLiteralMethod;
import ch.eskaton.asn4j.compiler.java.JavaMethod;
import ch.eskaton.asn4j.parser.ast.constraints.ContainedSubtype;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.types.BooleanType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.values.BooleanValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;

public class BooleanConstraintCompiler extends
		AbstractConstraintCompiler<Boolean> {

	public BooleanConstraintCompiler(ConstraintCompiler constraintCompiler,
			TypeResolver typeResolver) {
		super(constraintCompiler, typeResolver);
	}

	private final static Set<Boolean> ALL = new HashSet<Boolean>(Arrays.asList(
			Boolean.TRUE, Boolean.FALSE));

	protected Set<Boolean> compileConstraint(ElementSet set)
			throws CompilerException {
		List<Elements> operands = set.getOperands();
		Set<Boolean> booleans = new HashSet<Boolean>();

		switch (set.getOperation()) {
			case All:
				booleans.addAll(ALL);
				booleans.removeAll(compileConstraint((ElementSet) operands
						.get(0)));
				return booleans;

			case Exclude:
				if (operands.size() == 1) {
					// ALL EXCEPT
					return calculateElements(operands.get(0));
				} else {
					return calculateExclude(operands);
				}

			case Intersection:
				return calculateIntersection(operands);

			case Union:
				return calculateUnion(operands);
		}

		return booleans;
	}

	private Set<Boolean> calculateExclude(List<Elements> operands)
			throws CompilerException {
		Set<Boolean> bools = calculateElements(operands.get(0));
		Set<Boolean> excludes = calculateElements(operands.get(1));

		for (Boolean exclude : excludes) {
			if (bools.contains(exclude)) {
				bools.remove(exclude);
			} else {
				throw new CompilerException(exclude
						+ " doesn't exist in parent type");
			}
		}

		return bools;
	}

	private Set<Boolean> calculateElements(Elements elements)
			throws CompilerException {
		if (elements instanceof ElementSet) {
			return compileConstraint((ElementSet) elements);
		} else {
			if (elements instanceof SingleValueConstraint) {
				Value value = ((SingleValueConstraint) elements).getValue();
				if (value instanceof BooleanValue) {
					return new HashSet<Boolean>(
							Arrays.asList(((BooleanValue) value).getValue()));
				} else {
					throw new CompilerException(
							"Invalid single-value constraint "
									+ value.getClass().getName()
									+ " for BOOLEAN type");
				}
			} else if (elements instanceof ContainedSubtype) {
				Type type = ((ContainedSubtype) elements).getType();
				return calculateContainedSubtype(type);
			} else {
				throw new CompilerException("Invalid constraint "
						+ elements.getClass().getName() + " for BOOLEAN type");
			}
		}
	}

	private Set<Boolean> calculateUnion(List<Elements> operands)
			throws CompilerException {
		Set<Boolean> union = new HashSet<Boolean>();

		for (Elements e : operands) {
			union.addAll(calculateElements(e));
		}

		return union;
	}

	private Set<Boolean> calculateIntersection(List<Elements> operands)
			throws CompilerException {
		Set<Boolean> intersection = new HashSet<Boolean>();

		for (Elements e : operands) {
			Set<Boolean> bools = calculateElements(e);

			if (intersection.isEmpty()) {
				intersection.addAll(bools);
			} else {
				intersection.retainAll(bools);
				if (intersection.isEmpty()) {
					return intersection;
				}
			}
		}

		return intersection;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Collection<Boolean> calculateIntersection(Collection<?> op1,
			Collection<?> op2) {
		Set<Boolean> result = new HashSet<Boolean>();
		result.addAll((Collection<? extends Boolean>) op1);
		result.retainAll(op2);
		return result;
	}

	private Set<Boolean> calculateContainedSubtype(Type type)
			throws CompilerException {

		if (type instanceof BooleanType) {
			Set<Boolean> cons = new HashSet<Boolean>();
			cons.addAll(ALL);
			return cons;
		} else if (type instanceof TypeReference) {
			return (Set<Boolean>) compileConstraints(type,
					typeResolver.getBase(((TypeReference) type).getType()));
		} else {
			throw new CompilerException("Invalid type " + type
					+ " in constraint for BOOLEAN type");
		}

	}

	@Override
	public void addConstraint(JavaClass clazz, Collection<?> values) {
		if (values.size() == 2) {
			return;
		}

		StringBuilder body = new StringBuilder();

		body.append("\t@Override\n");
		body.append("\tprotected boolean checkConstraint(Boolean v) throws ")
				.append(ConstraintViolatedException.class.getSimpleName())
				.append(" {\n");

		if (values.isEmpty()) {
			body.append("\t\treturn false;\n");
		} else {
			body.append("\t\tif(v == ").append(values.iterator().next())
					.append(") {\n");
			body.append("\t\t\treturn true;\n");
			body.append("\t\t} else {\n");
			body.append("\t\t\treturn false;\n");
			body.append("\t\t}\n");
		}

		body.append("\t}");

		JavaMethod constrMethod = new JavaLiteralMethod(body.toString());
		clazz.addMethod(constrMethod);
		clazz.addImport(ConstraintViolatedException.class);
	}

}
