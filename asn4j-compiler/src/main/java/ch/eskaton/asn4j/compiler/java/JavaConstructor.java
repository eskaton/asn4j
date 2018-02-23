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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.eskaton.commons.utils.StringUtils;

import static ch.eskaton.asn4j.compiler.java.JavaVisibility.PackagePrivate;

public class JavaConstructor extends JavaMethod {

    private JavaVisibility visibility;

    private String clazz;

    private List<JavaParameter> parameters;

    private String body;

    private List<String> exceptions;

    public JavaConstructor(JavaVisibility visibility, String clazz,
    		List<JavaParameter> parameters, String body) {
    	this(visibility, clazz, parameters, body, new ArrayList<String>());
    }

    public JavaConstructor(JavaVisibility visibility, String clazz,
    		List<JavaParameter> parameters, String body, List<String> exceptions) {
    	this.visibility = visibility;
    	this.clazz = clazz;
    	this.parameters = parameters;
    	this.body = body;
    	this.exceptions = exceptions;
    }

    public JavaVisibility getVisibility() {
    	return visibility;
    }

    public String getClazz() {
    	return clazz;
    }

    public List<JavaParameter> getParameters() {
    	return parameters;
    }

    public String getBody() {
    	return body;
    }

    public void setBody(String body) {
    	this.body = body;
    }

    public void write(BufferedWriter writer, String prefix) throws IOException {
    	int paramCount = 0;

    	writer.write(StringUtils.concat(prefix, "\t",
    			(visibility == PackagePrivate ? "" : visibility.toString().toLowerCase()), " ", clazz, "("));

    	for (JavaParameter parameter : parameters) {
    		if (paramCount != 0) {
    			writer.write(", ");
    		}
    		parameter.write(writer, "");
    	}

    	writer.write(prefix);
    	writer.write(") ");

    	if (!exceptions.isEmpty()) {
    		writer.write(" throws " + StringUtils.join(exceptions, ", "));
    	}

    	writer.write(" {\n");
    	writer.write(StringUtils.inject(body, "\n", prefix));
    	writer.write("\n");
    	writer.write(prefix);
    	writer.write("\t}\n");
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, parameters);
    }

    @Override
    public boolean equals(Object obj) {
    	if (this == obj)
    		return true;
    	if (obj == null)
    		return false;
    	if (getClass() != obj.getClass())
    		return false;
    	JavaConstructor other = (JavaConstructor) obj;
    	if (clazz == null) {
    		if (other.clazz != null)
    			return false;
    	} else if (!clazz.equals(other.clazz))
    		return false;
    	if (parameters == null) {
    		if (other.parameters != null)
    			return false;
    	} else if (!parameters.equals(other.parameters))
    		return false;
    	return true;
    }

}
