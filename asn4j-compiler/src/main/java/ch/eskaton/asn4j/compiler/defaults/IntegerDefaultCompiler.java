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

package ch.eskaton.asn4j.compiler.defaults;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.java.JavaClass;
import ch.eskaton.asn4j.compiler.java.JavaInitializer;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;

public class IntegerDefaultCompiler implements DefaultCompiler {

    public void compileDefault(CompilerContext ctx, JavaClass clazz,
    		String field, Value value) throws CompilerException {
    	long intValue;

    	if (value instanceof IntegerValue) {
    		intValue = ((IntegerValue) value).getValue().longValue();

    		try {
    			ASN1Integer.valueOf(intValue);
    		} catch (ConstraintViolatedException e) {
    			throw new CompilerException(
    					"Default value doesn't satisfy constraints", e);
    		}
    	} else if (value instanceof SimpleDefinedValue) {
    		intValue = ctx.resolveIntegerValue(((SimpleDefinedValue) value))
    				.longValue();
    	} else {
    		throw new CompilerException("Invalid default value");
    	}

    	StringBuilder body = new StringBuilder();

    	body.append("\t\ttry {\n");
    	body.append("\t\t\t").append(field).append(" = ")
    			.append(ASN1Integer.class.getSimpleName()).append(".valueOf(")
    			.append(intValue).append(");\n");
    	body.append("\t\t} catch(")
    			.append(ConstraintViolatedException.class.getSimpleName())
    			.append(" e) {\n");
    	body.append("\t\t}");

    	clazz.addInitializer(new JavaInitializer(body.toString()));
    	clazz.addImport(ConstraintViolatedException.class);
    }

}
