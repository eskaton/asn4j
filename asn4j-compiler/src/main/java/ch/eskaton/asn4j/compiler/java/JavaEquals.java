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
import java.util.List;

import ch.eskaton.commons.utils.StringUtils;

public class JavaEquals extends JavaMethod {

    private List<String> fieldNames;

    private String className;

    public JavaEquals(String className, List<String> fieldNames) {
    	this.className = className;
    	this.fieldNames = fieldNames;
    }

    public void write(BufferedWriter writer, String prefix) throws IOException {
    	writer.write(prefix);
    	writer.write("\tpublic boolean equals(Object obj) {\n");

    	writer.write(prefix);
    	writer.write("\t\tif (this == obj)\n");
    	writer.write(prefix);
    	writer.write("\t\t\treturn true;\n");
    	writer.write("\t\tif (obj == null)\n");
    	writer.write(prefix);
    	writer.write("\t\t\treturn false;\n");
    	writer.write(prefix);
    	writer.write(StringUtils
    			.concat("\t\tif (getClass() != obj.getClass())\n"));
    	writer.write(prefix);
    	writer.write("\t\t\treturn false;\n");

    	if (!fieldNames.isEmpty()) {
    		writer.write(prefix);
    		writer.write(StringUtils.concat("\t\t", className, " other = (",
    				className, ") obj;\n"));

    		for (String fieldName : fieldNames) {
    			writer.write(prefix);
    			writer.write(StringUtils.concat("\t\tif (", fieldName,
    					" == null) {\n"));
    			writer.write(prefix);
    			writer.write(StringUtils.concat("\t\t\tif (other.", fieldName,
    					" != null)\n"));
    			writer.write(prefix);
    			writer.write("\t\t\t\treturn false;\n");
    			writer.write(prefix);
    			writer.write(StringUtils.concat("\t\t} else if (!", fieldName,
    					".equals(other.", fieldName, "))\n"));
    			writer.write(prefix);
    			writer.write("\t\t\treturn false;\n");
    		}
    	}

    	writer.write(prefix);
    	writer.write("\t\treturn true;\n");
    	writer.write(prefix);
    	writer.write("\t}\n");
    }

}
