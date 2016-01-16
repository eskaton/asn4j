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

package ch.eskaton.asn4j.compiler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ch.eskaton.asn4j.compiler.java.JavaClass;
import ch.eskaton.asn4j.compiler.java.JavaConstructor;
import ch.eskaton.asn4j.compiler.java.JavaLiteralField;
import ch.eskaton.asn4j.compiler.java.JavaLiteralMethod;
import ch.eskaton.asn4j.compiler.java.JavaParameter;
import ch.eskaton.asn4j.compiler.java.JavaVisibility;
import ch.eskaton.asn4j.parser.ast.EnumerationItemNode;
import ch.eskaton.asn4j.parser.ast.types.EnumeratedType;
import ch.eskaton.asn4j.runtime.ASN1RuntimeException;
import ch.eskaton.commons.StringUtils;

public class EnumeratedTypeCompiler implements NamedCompiler<EnumeratedType> {

	public void compile(CompilerContext ctx, String name, EnumeratedType node)
			throws CompilerException {
		JavaClass javaClass = ctx.createClass(name, node, true);
		List<String> names = new ArrayList<String>();
		List<Integer> numbers = new ArrayList<Integer>();

		for (EnumerationItemNode item : node.getRootEnum()) {
			String eName = item.getName();
			Integer eNumber;

			if (item.getRef() != null) {
				BigInteger bigValue = ctx.resolveIntegerValue(item.getRef());

				if (bigValue.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
					throw new CompilerException("Value " + bigValue
							+ " too large in type " + name);
				}

				eNumber = bigValue.intValue();
			} else {
				eNumber = item.getNumber();
			}

			addEnumerationItem(name, names, numbers, eName, eNumber);
		}

		int i = 0, n = 0;

		for (i = 0; i < numbers.size(); i++) {
			if (numbers.get(i) == null) {
				while (numbers.contains(n)) {
					n++;
				}
				numbers.set(i, n);
			}
		}

		if (node.getAdditionalEnum() != null) {
			for (EnumerationItemNode item : node.getAdditionalEnum()) {
				String eName = item.getName();
				Integer eNumber;

				if (item.getRef() != null) {
					BigInteger bigValue = ctx
							.resolveIntegerValue(item.getRef());

					if (bigValue.compareTo(BigInteger
							.valueOf(Integer.MAX_VALUE)) > 0) {
						throw new CompilerException("Value " + bigValue
								+ " too large in type " + name);
					}

					eNumber = bigValue.intValue();
				} else {
					eNumber = item.getNumber();
				}

				addEnumerationItem(name, names, numbers, eName, eNumber);
			}

			for (; i < numbers.size(); i++) {
				if (numbers.get(i) == null) {
					n = getNextNumber(numbers, i);
					if (numbers.contains(n)) {
						throw new CompilerException(
								"Duplicate enumeration value " + names.get(i)
										+ "(" + n + ") in " + name);
					}
					numbers.set(i, n);
				}
			}
		}

		if (node.hasExceptionSpec()) {
			// TODO: figure out what to do
		}

		Map<Integer, String> cases = new HashMap<Integer, String>();

		for (int j = 0; j < names.size(); j++) {
			String fieldName = CompilerUtils.formatConstant(names.get(j));
			int value = numbers.get(j);

			cases.put(value, fieldName);

			javaClass.addField(new JavaLiteralField(StringUtils.concat(
					"\tpublic static final ", name, " ", fieldName, " = ",
					"new ", name, "(", value, ");\n\n")));
		}

		javaClass.addMethod(new JavaConstructor(JavaVisibility.Private, name,
				Arrays.asList(new JavaParameter("int", "value")),
				"\t\tsuper.setValue(value);\n"));

		StringBuilder body = new StringBuilder();
		body.append("\tpublic static ").append(name)
				.append(" valueOf(int value").append(") throws ")
				.append(ASN1RuntimeException.class.getSimpleName())
				.append(" {\n");
		body.append("\t\tswitch(value) {\n");

		for (Entry<Integer, String> entry : cases.entrySet()) {
			body.append("\t\tcase ").append(String.valueOf(entry.getKey()))
					.append(":\n");
			body.append("\t\t\treturn ").append(entry.getValue()).append(";\n");
		}

		body.append("\t\tdefault:\n");
		body.append("\t\t\tthrow new ")
				.append(ASN1RuntimeException.class.getSimpleName())
				.append("(\"Undefined value: \" + value);\n");
		body.append("\t\t}\n");
		body.append("\t}\n");

		javaClass.addImport(ASN1RuntimeException.class.getCanonicalName());
		javaClass.addMethod(new JavaLiteralMethod(body.toString()));

		ctx.finishClass();
	}

	private Integer getNextNumber(List<Integer> numbers, int last) {
		int n = 0;

		for (int i = 0; i < last; i++) {
			n = Math.max(numbers.get(i), n);
		}

		return n + 1;
	}

	private void addEnumerationItem(String typeName, List<String> names,
			List<Integer> values, String name, Integer value)
			throws CompilerException {
		if (names.contains(name)) {
			throw new CompilerException("Duplicate enumeration item '" + name
					+ "' in " + typeName);
		}

		if (value != null && values.contains(value)) {
			throw new CompilerException("Duplicate enumeration value " + name
					+ "(" + value + ") in " + typeName);
		}

		names.add(name);
		values.add(value);
	}

}
