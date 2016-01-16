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

package ch.eskaton.asn4j.mvn;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import ch.eskaton.asn4j.compiler.CompilerImpl;

/**
 * Goal which compiles an ASN.1 module.
 * 
 * @goal asn4j
 * @requiresProject true
 */
public class ASN4JCompile extends AbstractMojo {

	/**
	 * Get the executed project from the forked life cycle.
	 * 
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * The name of the generated Java package.
	 * 
	 * @parameter
	 * @required
	 */
	private String pkg;

	/**
	 * The main ASN.1 module.
	 * 
	 * @parameter
	 * @required
	 */
	private String module;

	/**
	 * The output directory for generated sources.
	 * 
	 * @parameter property="outputDirectory"
	 *            default-value="${project.build.directory}/generated-sources/asn4j"
	 * @required
	 */
	private String outputDirectory;

	/**
	 * The directories for the files to include.
	 * 
	 * @parameter
	 * @required
	 */
	private List<String> includePaths;

	/**
	 * Executes the mojo.
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		StringBuilder paths = new StringBuilder();

		for (String path : includePaths) {
			if (paths.length() > 0) {
				paths.append(File.pathSeparator);
			}

			paths.append(project.getBasedir().getAbsolutePath())
					.append(File.separator).append(path);
		}

		getLog().info("Executing ASN.1 Compiler:");
		getLog().info("- ASN.1 module = " + module);
		getLog().info("- ASN.1 module include path = " + paths.toString());
		getLog().info("- Java package = " + pkg);
		getLog().info("- Output directory = " + outputDirectory);

		CompilerImpl compiler = new CompilerImpl(module, paths.toString(), pkg,
				outputDirectory);

		try {
			compiler.run();
			project.addCompileSourceRoot(outputDirectory);
		} catch (Exception e) {
			throw new MojoExecutionException("Asn4j compilation failed: "
					+ e.getMessage(), e);
		}
	}

}
