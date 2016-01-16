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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.eskaton.asn4j.parser.ast.values.Tag;
import ch.eskaton.commons.CollectionUtils;
import ch.eskaton.commons.CollectionUtils.Mapper;
import ch.eskaton.commons.StringUtils;

public class JavaClass implements JavaStructure {

	private List<String> imports = new ArrayList<String>();

	private List<JavaField> fields = new ArrayList<JavaField>();

	private List<JavaMethod> methods = new ArrayList<JavaMethod>();

	private List<JavaClass> innerClasses = new ArrayList<JavaClass>();

	private List<JavaEnum> enums = new ArrayList<JavaEnum>();

	private Set<JavaModifier> modifiers = new HashSet<JavaModifier>();

	private List<JavaStaticInitializer> staticInitializers;

	private List<JavaInitializer> initializers;

	private String pkg;

	private String name;

	private String parent;

	private String interfaze;

	private Tag tag;

	private ch.eskaton.asn4j.runtime.annotations.ASN1Tag.Mode mode;

	private boolean constructed;

	private String typeParam;

	public JavaClass(String pkg, String name, Tag tag,
			ch.eskaton.asn4j.runtime.annotations.ASN1Tag.Mode mode,
			boolean constructed, String parent) {
		this.pkg = pkg;
		this.name = name;
		this.tag = tag;
		this.mode = mode;
		this.constructed = constructed;
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getParent() {
		return parent;
	}

	public void setInterfaze(String interfaze) {
		this.interfaze = interfaze;
	}

	public String getInterfaze() {
		return interfaze;
	}

	public void setTypeParam(String typeParam) {
		this.typeParam = typeParam;
	}

	public void addImport(Class<?> clazz) {
		addImport(clazz.getName());
	}

	public void addImport(String imp) {
		imports.add(imp);
	}

	public void addField(JavaField field) {
		fields.add(field);
	}

	public void addField(JavaDefinedField field) {
		addField(field, true, true);
	}

	public void addField(JavaDefinedField field, boolean hasGetter,
			boolean hasSetter) {
		String typeName = field.getTypeName();
		addMethod(new JavaSetter(typeName, field.getName()));
		addMethod(new JavaGetter(typeName, field.getName()));
		fields.add(field);
	}

	public void addMethod(JavaMethod method) {
		methods.add(method);
	}

	public void addInnerClass(JavaClass innerClass) {
		innerClasses.add(innerClass);
	}

	public void addEnum(JavaEnum anEnum) {
		enums.add(anEnum);
	}

	public void addModifier(JavaModifier modifier) {
		modifiers.add(modifier);
	}

	public void addStaticInitializer(JavaStaticInitializer staticInitializer) {
		if (this.staticInitializers == null) {
			this.staticInitializers = new ArrayList<JavaStaticInitializer>();
		}
		this.staticInitializers.add(staticInitializer);
	}

	public void addInitializer(JavaInitializer initializer) {
		if (this.initializers == null) {
			this.initializers = new ArrayList<JavaInitializer>();
		}
		this.initializers.add(initializer);
	}

	public List<JavaMethod> getMethods() {
		return methods;
	}

	public List<JavaConstructor> getConstructors() {
		List<JavaConstructor> constructors = new ArrayList<JavaConstructor>();

		for (JavaMethod method : methods) {
			if (method instanceof JavaConstructor) {
				constructors.add((JavaConstructor) method);
			}
		}

		return constructors;
	}

	public void save(String dir) throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(dir + File.separator
						+ pkg.replace('.', File.separatorChar) + File.separator
						+ name + ".java")));
		write(writer, "");
		writer.close();
	}

	public void write(BufferedWriter writer, String prefix) throws IOException {
		writeFileHeader(writer);
		writeClass(writer, prefix);
	}

	private void writeClass(BufferedWriter writer, String prefix)
			throws IOException {
		writeClassHeader(writer, prefix);

		for (JavaEnum theEnum : enums) {
			theEnum.write(writer, prefix + "\t");
		}

		for (JavaField field : fields) {
			field.write(writer, prefix);
		}

		if (staticInitializers != null) {
			for (JavaStaticInitializer jsi : staticInitializers) {
				writer.write("\tstatic {\n");
				writer.write(jsi.toString());
				writer.write("\n\t}\n\n");
			}
		}

		if (initializers != null) {
			for (JavaInitializer ji : initializers) {
				writer.write("\t{\n");
				writer.write(ji.toString());
				writer.write("\n\t}\n\n");
			}
		}

		for (JavaMethod method : methods) {
			method.write(writer, prefix);
		}

		writer.write("\n\n");

		for (JavaClass innerClass : innerClasses) {
			innerClass.writeClass(writer, prefix + "\t");
		}

		writeClassFooter(writer, prefix);
	}

	private void writeClassHeader(BufferedWriter writer, String prefix)
			throws IOException {

		if (tag != null) {
			writer.write(StringUtils.concat(prefix, "@ASN1Tag(clazz=",
					ch.eskaton.asn4j.runtime.annotations.ASN1Tag.Clazz.class
							.getCanonicalName(), "."));

			if (tag.getClazz() == null) {
				writer.write(ch.eskaton.asn4j.runtime.annotations.ASN1Tag.Clazz.ContextSpecific
						.toString());
			} else {
				switch (tag.getClazz()) {

					case APPLICATION:
						writer.write(ch.eskaton.asn4j.runtime.annotations.ASN1Tag.Clazz.Application
								.toString());
						break;
					case PRIVATE:
						writer.write(ch.eskaton.asn4j.runtime.annotations.ASN1Tag.Clazz.Private
								.toString());
						break;
					case UNIVERSAL:
						writer.write(ch.eskaton.asn4j.runtime.annotations.ASN1Tag.Clazz.Universal
								.toString());
						break;
				}
			}

			writer.write(StringUtils.concat(",tag=", tag.getClassNumber()
					.getClass(), ",mode=",
					ch.eskaton.asn4j.runtime.annotations.ASN1Tag.Mode.class
							.getCanonicalName(), ".", mode, ",constructed=",
					constructed, ")\n"));
		}

		writer.write(StringUtils.concat(
				prefix,
				"public ",
				StringUtils.join(CollectionUtils.map(modifiers,
						new Mapper<JavaModifier, String>() {
							public String map(JavaModifier value) {
								return value.toString().toLowerCase();
							}
						}), " "),
				" class ",
				name,
				(parent != null ? " extends " + parent
						+ (typeParam != null ? "<" + typeParam + ">" : "") : ""),
				(interfaze != null ? " implements " + interfaze : ""), " {\n\n"));
	}

	private void writeFileHeader(BufferedWriter writer) throws IOException {
		writer.write("/* AUTOMATICALLY GENERATED - DO NOT EDIT */\n");
		writer.write(StringUtils.concat("package ", pkg, ";\n"));
		writer.newLine();

		for (String imp : imports) {
			writer.write(StringUtils.concat("import ", imp, ";\n"));
		}

		writer.write("import ch.eskaton.asn4j.runtime.types.*;\n");
		writer.write("import ch.eskaton.asn4j.runtime.annotations.*;\n");
		writer.newLine();
	}

	private void writeClassFooter(BufferedWriter writer, String prefix)
			throws IOException {
		writer.write(prefix);
		writer.write("}");
		writer.newLine();
	}

	public void createEqualsAndHashCode() {
		ArrayList<String> fieldNames = new ArrayList<String>(fields.size());

		for (JavaField field : fields) {
			if (field instanceof JavaDefinedField) {
				fieldNames.add(((JavaDefinedField) field).getName());
			}
		}

		addMethod(new JavaEquals(getName(), fieldNames));
		addMethod(new JavaHashCode(fieldNames));
	}

}
