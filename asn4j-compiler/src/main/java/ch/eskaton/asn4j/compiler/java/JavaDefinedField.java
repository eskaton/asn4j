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

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import ch.eskaton.commons.utils.StringUtils;

public class JavaDefinedField implements JavaField {

	private String typeName;

	private String name;

	private Set<JavaAnnotation> annotations = new HashSet<JavaAnnotation>();

	public JavaDefinedField(String typeName, String name, String params) {
		this.typeName = typeName;
		this.name = name;
	}

	public JavaDefinedField(String typeName, String name) {
		this.typeName = typeName;
		this.name = name;
	}

	public void addAnnotation(JavaAnnotation annotation) {
		annotations.add(annotation);
	}

	public String getName() {
		return name;
	}

	public String getTypeName() {
		return typeName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see asn4j.compiler.JavaField#write(java.io.BufferedWriter)
	 */
	public void write(BufferedWriter writer, String prefix) throws IOException {

		for (JavaAnnotation annotation : annotations) {
			annotation.write(writer, prefix);
		}

		writer.write(StringUtils.concat(prefix, "\tprivate ", typeName, " ",
				name, ";\n\n"));
	}

	@Override
	public String toString() {
		return "JavaDefinedField [typeName=" + typeName + ", name=" + name
				+ ", annotations=" + annotations + "]";
	}

}
