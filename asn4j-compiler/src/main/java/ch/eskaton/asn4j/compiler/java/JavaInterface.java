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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import ch.eskaton.commons.StringUtils;

public class JavaInterface implements JavaStructure {

	private String pkg;

	private String name;

	public JavaInterface(String pkg, String name) {
		this.pkg = pkg;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void save(String dir) throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(dir + File.separator
						+ pkg.replace('.', File.separatorChar) + File.separator
						+ name + ".java")));
		write(writer, "");
	}

	public void write(BufferedWriter writer, String prefix) throws IOException {
		writeInterfaceHeader(writer);
		writeInterfaceFooter(writer);
	}

	private void writeInterfaceHeader(BufferedWriter writer) throws IOException {
		writer.write("/* AUTOMATICALLY GENERATED - DO NOT EDIT */\n");
		writer.write(StringUtils.concat("package ", pkg, ";\n"));
		writer.newLine();

		writer.write(StringUtils.concat("public interface ", name, " {\n\n"));
	}

	private void writeInterfaceFooter(BufferedWriter writer) throws IOException {
		writer.write("}");
		writer.newLine();
		writer.close();
	}

}
