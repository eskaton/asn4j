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
import java.util.Collection;

import ch.eskaton.asn4j.compiler.java.JavaClass;
import ch.eskaton.asn4j.compiler.java.JavaLiteralField;
import ch.eskaton.asn4j.compiler.java.JavaLiteralMethod;
import ch.eskaton.asn4j.parser.ast.NamedBitNode;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.commons.utils.StringUtils;

public class BitStringCompiler extends BuiltinTypeCompiler<BitString> {

    @Override
    public void compile(CompilerContext ctx, String name, BitString node)
    		throws CompilerException {
    	JavaClass javaClass = ctx.createClass(name, node, false);
    	Collection<NamedBitNode> namedBits = node.getNamedBits();
    	IdentifierUniquenessChecker<Long> iuc = new IdentifierUniquenessChecker<Long>(
    			name);

    	javaClass
    			.setParent(ch.eskaton.asn4j.runtime.types.ASN1NamedBitString.class
    					.getSimpleName());
    	long msb = 0;

    	if (namedBits != null && !namedBits.isEmpty()) {
    		for (NamedBitNode namedBit : namedBits) {
    			String fieldName = CompilerUtils.formatConstant(namedBit
    					.getId());
    			long value;

    			if (namedBit.getRef() != null) {
    				BigInteger bigValue = ctx.resolveIntegerValue(namedBit
    						.getRef());

    				if (bigValue.bitLength() > 63) {
    					throw new CompilerException("Named bit '" + fieldName
    							+ "' too long: " + bigValue.toString());
    				}

    				value = bigValue.longValue();
    			} else {
    				value = namedBit.getNum();
    			}

    			iuc.add(namedBit.getId(), value);

    			msb = value > msb ? value : msb;

    			javaClass.addField(new JavaLiteralField(StringUtils.concat(
    					"\tpublic static final int ", fieldName, " = ", value,
    					";\n\n")));
    		}
    	}

    	javaClass.addMethod(new JavaLiteralMethod(StringUtils.concat(
    			"\tpublic ", name, "() {\n", "\t\tsuper();", "\n\t}\n")));

    	if (node.hasConstraint()) {
    		ctx.compileConstraint(javaClass, name, node);
    	}

    	ctx.finishClass(false);
    }

}
