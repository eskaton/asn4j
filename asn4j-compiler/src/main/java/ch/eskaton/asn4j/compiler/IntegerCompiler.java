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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import ch.eskaton.asn4j.compiler.java.JavaClass;
import ch.eskaton.asn4j.compiler.java.JavaConstructor;
import ch.eskaton.asn4j.compiler.java.JavaLiteralField;
import ch.eskaton.asn4j.compiler.java.JavaParameter;
import ch.eskaton.asn4j.compiler.java.JavaStaticInitializer;
import ch.eskaton.asn4j.compiler.java.JavaVisibility;
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
import ch.eskaton.asn4j.parser.ast.values.NamedNumber;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;
import ch.eskaton.commons.utils.StringUtils;

public class IntegerCompiler extends BuiltinTypeCompiler<IntegerType> {

    @Override
    public void compile(CompilerContext ctx, String name, IntegerType node)
    		throws CompilerException {
    	JavaClass javaClass = ctx.createClass(name, node, false);
    	Collection<NamedNumber> namedNumbers = node.getNamedNumbers();
    	IdentifierUniquenessChecker<BigInteger> iuc = new IdentifierUniquenessChecker<BigInteger>(
    			name);

    	if (namedNumbers != null && !namedNumbers.isEmpty()) {
    		StringBuilder staticBody = new StringBuilder();
    		staticBody.append("\t\ttry {\n");

    		for (NamedNumber namedNumber : namedNumbers) {
    			String fieldName = CompilerUtils.formatConstant(namedNumber
    					.getId());
    			BigInteger bigValue;
    			long value;

    			if (namedNumber.getRef() != null) {
    				bigValue = ctx.resolveIntegerValue(namedNumber.getRef());
    			} else {
    				bigValue = namedNumber.getValue().getNumber();
    			}

    			if (bigValue.bitLength() > 63) {
    				throw new CompilerException("Named number '" + fieldName
    						+ "' too long: " + bigValue.toString());
    			}

    			iuc.add(namedNumber.getId(), bigValue);

    			value = bigValue.longValue();

    			javaClass.addField(new JavaLiteralField(StringUtils
    					.concat("\tpublic static final ", name, " ", fieldName,
    							";\n\n")));

    			staticBody.append(StringUtils.concat("\t\t\t", fieldName,
    					" = ", "new ", name, "(", value, ");\n"));
    		}

    		staticBody.append("\t\t} catch (")
    				.append(ConstraintViolatedException.class.getSimpleName())
    				.append(" e){\n");
    		staticBody.append("\t\t\tthrow new RuntimeException(e);\n");
    		staticBody.append("\t\t}");

    		javaClass.addStaticInitializer(new JavaStaticInitializer(staticBody
    				.toString()));
    	}

    	javaClass.addImport(BigInteger.class);
    	javaClass.addImport(ConstraintViolatedException.class);

    	javaClass.addMethod(new JavaConstructor(JavaVisibility.Public, name,
    			Collections.emptyList(), "\t\tsuper();",
    			Collections.emptyList()));

    	javaClass.addMethod(new JavaConstructor(JavaVisibility.Protected, name,
    			Arrays.asList(new JavaParameter("long", "value")),
    			"\t\tsuper.setValue(BigInteger.valueOf(value));", Arrays
    					.asList(ConstraintViolatedException.class.getName())));

    	if (node.hasConstraint()) {
    		ctx.compileConstraint(javaClass, name, node);
    	}

    	ctx.finishClass();
    }

}
