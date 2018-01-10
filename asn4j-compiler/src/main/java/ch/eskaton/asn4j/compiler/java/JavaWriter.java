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

package ch.eskaton.asn4j.compiler.java;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import ch.eskaton.asn4j.compiler.CompilerException;

public class JavaWriter {

    public void write(Map<String, JavaStructure> structs, String outputDir)
    		throws CompilerException {
    	Set<JavaStructure> processed = new HashSet<JavaStructure>();

    	for (JavaStructure struct : structs.values()) {
    		if (processed.contains(struct)) {
    			continue;
    		}

    		if (struct instanceof JavaClass) {
    			JavaClass clazz = (JavaClass) struct;
    			String parent = clazz.getParent();
    			Stack<JavaClass> clazzHierarchy = new Stack<JavaClass>();
    			clazzHierarchy.push(clazz);

    			while (parent != null && !parent.startsWith("ASN1")) {
    				JavaStructure parentStruct = structs.get(parent);

    				if (parentStruct instanceof JavaClass) {
    					clazz = (JavaClass) parentStruct;
    					if (clazz != null) {
    						clazzHierarchy.push(clazz);
    						parent = clazz.getParent();
    					}
    				} else if (parentStruct instanceof JavaInterface) {
    					clazz.setParent(null);
    					clazz.setInterfaze(parent);
    					parent = null;
    				} else {
    					parent = null;
    				}
    			}

    			List<JavaConstructor> constructors = new ArrayList<JavaConstructor>();

    			while (!clazzHierarchy.empty()) {
    				clazz = clazzHierarchy.pop();
    				if (!constructors.isEmpty()) {
    					List<JavaConstructor> availableConstructors = clazz
    							.getConstructors();
    					for (JavaConstructor constructor : constructors) {
    						JavaConstructor childConstructor = new JavaConstructor(
    								constructor.getVisibility(),
    								clazz.getName(),
    								constructor.getParameters(), null);
    						if (!availableConstructors
    								.contains(childConstructor)) {
    							StringBuilder body = new StringBuilder();
    							body.append("\t\tsuper(");

    							int paramCount = 0;

    							for (JavaParameter parameter : childConstructor
    									.getParameters()) {
    								if (paramCount > 0) {
    									body.append(", ");
    								}
    								body.append(parameter.getName());
    								paramCount++;
    							}

    							body.append(");\n");
    							childConstructor.setBody(body.toString());
    							clazz.addMethod(childConstructor);
    						}
    					}
    				}
    				constructors.clear();
    				constructors.addAll(clazz.getConstructors());
    				processed.add(clazz);
    			}
    		}

    	}

    	// write classes
    	for (JavaStructure struct : structs.values()) {
    		try {
    			struct.save(outputDir);
    		} catch (IOException e) {
    			throw new CompilerException(e);
    		}
    	}

    }

}
