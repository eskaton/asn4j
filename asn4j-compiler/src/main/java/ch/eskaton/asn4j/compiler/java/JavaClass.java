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

import ch.eskaton.asn4j.compiler.CompilerUtils;
import ch.eskaton.asn4j.parser.ast.values.Tag;
import ch.eskaton.asn4j.runtime.Clazz;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.commons.utils.CollectionUtils;
import ch.eskaton.commons.utils.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.eskaton.asn4j.compiler.java.JavaVisibility.Public;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class JavaClass implements JavaStructure {

    private List<String> imports = new ArrayList<>();

    private List<JavaField> fields = new ArrayList<>();

    private List<JavaMethod> methods = new ArrayList<>();

    private List<JavaClass> innerClasses = new ArrayList<>();

    private List<JavaEnum> enums = new ArrayList<>();

    private Set<JavaModifier> modifiers = new HashSet<>();

    private List<JavaStaticInitializer> staticInitializers;

    private List<JavaInitializer> initializers;

    private String pkg;

    private String name;

    private String parent;

    private String interf;

    private Tag tag;

    private ASN1Tag.Mode mode;

    private boolean constructed;

    private String typeParam;

    public JavaClass(String pkg, String name, Tag tag, ASN1Tag.Mode mode, boolean constructed, String parent) {
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

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getInterface() {
        return interf;
    }

    public void setInterface(String interf) {
        this.interf = interf;
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

    public void addField(JavaDefinedField field, boolean hasGetter, boolean hasSetter) {
        String typeName = field.getTypeName();

        if (hasSetter) {
            addMethod(new JavaSetter(typeName, field.getName()));
        }

        if (hasGetter) {
            addMethod(new JavaGetter(typeName, field.getName()));
        }

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
            this.staticInitializers = new ArrayList<>();
        }
        this.staticInitializers.add(staticInitializer);
    }

    public void addInitializer(JavaInitializer initializer) {
        if (this.initializers == null) {
            this.initializers = new ArrayList<>();
        }
        this.initializers.add(initializer);
    }

    public List<JavaMethod> getMethods() {
        return methods;
    }

    public List<JavaConstructor> getConstructors() {
        List<JavaConstructor> constructors = new ArrayList<>();

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
            writer.newLine();
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
            writer.newLine();
        }

        writer.write("\n\n");

        for (JavaClass innerClass : innerClasses) {
            innerClass.writeClass(writer, prefix + "\t");
        }

        writeClassFooter(writer, prefix);
    }

    private void writeClassHeader(BufferedWriter writer, String prefix) throws IOException {
        if (tag != null) {
            CompilerUtils.getTagAnnotation(tag, mode.toString(), constructed).write(writer, "");
        }

        writer.write(StringUtils.concat(prefix, "public ",
                StringUtils.join(CollectionUtils.map(modifiers, value -> value.toString().toLowerCase()), " "),
                " class ", name, (parent != null ? " extends " + parent +
                        (typeParam != null ? "<" + typeParam + ">" : "") : ""),
                (interf != null ? " implements " + interf : ""), " {\n\n"));
    }

    private void writeFileHeader(BufferedWriter writer) throws IOException {
        writer.write("/* AUTOMATICALLY GENERATED - DO NOT EDIT */\n");
        writer.write(StringUtils.concat("package ", pkg, ";\n"));
        writer.newLine();

        for (String imp : imports) {
            writer.write(StringUtils.concat("import ", imp, ";\n"));
        }

        String pkg = Clazz.class.getPackage().getName();

        writer.write("import " + pkg + ".Clazz;\n");
        writer.write("import " + pkg + ".types.*;\n");
        writer.write("import " + pkg + ".annotations.*;\n");
        writer.write("import java.util.Objects;\n");
        writer.newLine();
    }

    private void writeClassFooter(BufferedWriter writer, String prefix) throws IOException {
        writer.write(prefix);
        writer.write("}");
        writer.newLine();
    }

    public void createEqualsAndHashCode() {
        ArrayList<String> fieldNames = new ArrayList<>(fields.size());

        for (JavaField field : fields) {
            if (field instanceof JavaDefinedField) {
                fieldNames.add(((JavaDefinedField) field).getName());
            }
        }

        addMethod(new JavaEquals(getName(), fieldNames));
        addMethod(new JavaHashCode(fieldNames));
    }

    public MethodBuilder method() {
        return new MethodBuilder(this);
    }

    public FieldBuilder field() {
        return new FieldBuilder(this);
    }

    public static class AbstractJavaBuilder<T extends AbstractJavaBuilder> {

        protected final JavaClass javaClass;

        protected List<String> annotations = new ArrayList<>();

        protected JavaVisibility visibility = Public;

        protected boolean isStatic;

        protected boolean isFinal;

        protected String name;

        public AbstractJavaBuilder(JavaClass javaClass) {
            this.javaClass = javaClass;
        }

        public T annotation(Class<? extends Annotation> annotation) {
            if (!"java.lang".equals(annotation.getPackage().toString())) {
                javaClass.addImport(annotation);
            }

            annotations.add("@" + annotation.getClass().getSimpleName());
            return (T) this;
        }

        public T annotation(String annotation) {
            annotations.add(annotation);
            return (T) this;
        }

        public T modifier(JavaVisibility visibility) {
            this.visibility = visibility;
            return (T) this;
        }

        public T asStatic() {
            this.isStatic = true;
            return (T) this;
        }

        public T asFinal() {
            this.isFinal = true;
            return (T) this;
        }

        public T name(String name) {
            this.name = name;
            return (T) this;
        }

    }

    public static class MethodBuilder extends AbstractJavaBuilder<MethodBuilder> {

        private String returnType = "void";

        private List<JavaParameter> parameters = new ArrayList<>();

        private Set<String> exceptions = new HashSet<>();

        private List<String> body;

        private MethodBuilder(JavaClass javaClass) {
            super(javaClass);
        }

        public MethodBuilder returnType(String returnType) {
            this.returnType = returnType;
            return this;
        }

        public MethodBuilder returnType(Class<?> returnType) {
            return returnType(returnType.getClass().getSimpleName());
        }

        public MethodBuilder parameter(JavaParameter parameter) {
            parameters.add(parameter);
            return this;
        }

        public MethodBuilder parameter(String type, String name) {
            return parameter(new JavaParameter(type, name));
        }

        public MethodBuilder parameter(JavaType type, String name) {
            return parameter(type.toString(), name);
        }

        public MethodBuilder exception(Class<? extends Throwable> exception) {
            if (!"java.lang".equals(exception.getPackage().toString())) {
                javaClass.addImport(exception);
            }

            return exception(exception.getSimpleName());
        }

        public MethodBuilder exception(String exception) {
            exceptions.add(exception);
            return this;
        }

        public BodyBuilder body() {
            return new BodyBuilder(this);
        }

        private MethodBuilder body(String body) {
            return body(Arrays.asList(body.split("\n")));
        }

        private MethodBuilder body(List<String> body) {
            this.body = body.stream().map(b -> "\t\t" + b + "\n").collect(toList());
            return this;
        }

        public void build() {
            javaClass.addMethod(new JavaLiteralMethod(toString()));
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append(annotations.stream().map(a -> "\t" + a + "\n").collect(joining()));

            sb.append("\t");
            sb.append(visibility);

            if (isStatic) {
                sb.append(" static");
            }

            if (isFinal) {
                sb.append(" final");
            }

            sb.append(" ");
            sb.append(returnType);
            sb.append(" ");
            sb.append(name);
            sb.append("(");

            sb.append(parameters.stream().map(JavaParameter::toString).collect(joining(", ")));

            sb.append(") ");

            if (!exceptions.isEmpty()) {
                sb.append("throws ");
                sb.append(exceptions.stream().map(String::toString).collect(joining(", ")));
                sb.append(" ");
            }

            sb.append("{\n");

            if (body != null) {
                sb.append(body.stream().collect(joining()));
            }

            sb.append("\t}");

            return sb.toString();
        }

    }

    public static class BodyBuilder {

        private MethodBuilder methodBuilder;

        private List<String> body;

        private BodyBuilder(MethodBuilder methodBuilder) {
            this.methodBuilder = methodBuilder;
        }

        public BodyBuilder append(Object body) {
            return append(String.valueOf(body));
        }

        public BodyBuilder append(String body) {
            if (this.body == null) {
                this.body = new ArrayList<>();
            }

            this.body.add(body);

            return this;
        }

        public BodyBuilder append(List<String> body) {
            this.body = new ArrayList<>(body.size());

            this.body.addAll(body);

            return this;
        }

        public MethodBuilder finish() {
            return methodBuilder.body(body);
        }

    }

    public class FieldBuilder extends AbstractJavaBuilder<FieldBuilder> {

        private String initializer;

        private String type;

        public FieldBuilder(JavaClass javaClass) {
            super(javaClass);
        }

        public FieldBuilder type(String type) {
            this.type = type;
            return this;
        }

        public FieldBuilder type(Class<?> type) {
            return type(type.getClass().getSimpleName());
        }

        public FieldBuilder initializer(String initializer) {
            this.initializer = initializer;
            return this;
        }

        public void build() {
            javaClass.addField(new JavaLiteralField(toString()));
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append(annotations.stream().map(a -> "\t" + a + "\n").collect(joining()));

            sb.append("\t");
            sb.append(visibility);

            if (isStatic) {
                sb.append(" static");
            }

            if (isFinal) {
                sb.append(" final");
            }

            sb.append(" ");
            sb.append(type);
            sb.append(" ");
            sb.append(name);

            if (initializer != null) {
                sb.append(" = ");
                sb.append(initializer);
            }

            sb.append(";\n");

            return sb.toString();
        }

    }

}
