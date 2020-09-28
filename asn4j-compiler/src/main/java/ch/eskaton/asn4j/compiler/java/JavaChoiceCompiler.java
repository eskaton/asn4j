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

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerUtils;
import ch.eskaton.asn4j.compiler.java.objs.JavaAnnotation;
import ch.eskaton.asn4j.compiler.java.objs.JavaClass;
import ch.eskaton.asn4j.compiler.java.objs.JavaDefinedField;
import ch.eskaton.asn4j.compiler.java.objs.JavaEnum;
import ch.eskaton.asn4j.compiler.java.objs.JavaGetter;
import ch.eskaton.asn4j.compiler.java.objs.JavaStructure;
import ch.eskaton.asn4j.compiler.java.objs.JavaTypedSetter;
import ch.eskaton.asn4j.compiler.results.CompiledChoiceType;
import ch.eskaton.asn4j.compiler.results.CompiledComponent;
import ch.eskaton.asn4j.runtime.annotations.ASN1Alternative;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;
import ch.eskaton.asn4j.runtime.types.ASN1Type;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.CompilerUtils.formatName;
import static ch.eskaton.asn4j.compiler.java.objs.JavaVisibility.PRIVATE;
import static ch.eskaton.asn4j.compiler.java.objs.JavaVisibility.PUBLIC;

public class JavaChoiceCompiler implements JavaTypeCompiler<CompiledChoiceType> {

    public static final String CLEAR_FIELDS = "clearFields";

    private static final String CHOICE_ENUM = "Choice";

    private static final String CHOICE_FIELD = "choice";

    @Override
    public void compile(JavaCompiler compiler, CompilerContext ctx, Deque<JavaClass> classStack,
            Map<String, JavaStructure> compiledClasses, String pkg, CompiledChoiceType compiledType) {
        var name = compiledType.getName();
        var className = formatName(name);
        var tags = compiledType.getTags().orElse(List.of());
        var type = compiledType.getType();
        var javaClass = createClass(ctx, classStack, pkg, className, type, tags);

        generateChoiceJavaClass(ctx, javaClass, compiledType);

        compiledType.getComponents().forEach(component -> {
            var compiledComponent = component.getCompiledType();

            if (compiledComponent.isSubtype()) {
                compiler.compileType(ctx, classStack, compiledClasses, pkg, compiledComponent);
            }
        });

        finishClass(classStack, compiledClasses, true);
    }

    private void generateChoiceJavaClass(CompilerContext ctx, JavaClass javaClass, CompiledChoiceType compiledType) {
        var fieldNames = new ArrayList<String>();
        var typeEnum = new JavaEnum(CHOICE_ENUM);

        var bodyBuilder = javaClass.method().modifier(PUBLIC).annotation("@Override")
                .returnType(ASN1Type.class.getSimpleName()).name("getValue").body();
        var clearFields = "\t\t" + CLEAR_FIELDS + "();\n";

        bodyBuilder.append("switch(" + CHOICE_FIELD + ") {");

        for (var component : compiledType.getComponents()) {
            var fieldName = CompilerUtils.formatName(component.getName());
            var typeConstant = CompilerUtils.formatConstant(component.getName());

            fieldNames.add(fieldName);
            typeEnum.addEnumConstant(typeConstant);
            bodyBuilder.append("\tcase " + typeConstant + ":").append("\t\treturn " + fieldName + ";");

            addJavaField(javaClass, typeConstant, clearFields, component);
        }

        bodyBuilder.append("}").append("").append("return null;");

        bodyBuilder.finish().build();

        javaClass.addEnum(typeEnum);
        javaClass.addField(new JavaDefinedField(CHOICE_ENUM, CHOICE_FIELD), true, false);

        addClearFieldsMethod(javaClass, fieldNames);

        if (compiledType.getModule().isPresent()) {
            javaClass.addModule(ctx, compiledType.getModule().get());
            javaClass.addImport(ConstraintViolatedException.class);
        }
    }

    private void addJavaField(JavaClass javaClass, String typeConstant, String beforeCode,
            CompiledComponent compiledComponent) {
        var compiledType = compiledComponent.getCompiledType();
        var tags = compiledType.getTags();
        var javaTypeName = compiledType.getName();
        var javaFieldName = compiledComponent.getName();
        var qualifiedConstant = CHOICE_ENUM + "." + typeConstant;
        var field = new JavaDefinedField(javaTypeName, javaFieldName);
        var javaSetter = new JavaTypedSetter(javaTypeName, javaFieldName, CHOICE_FIELD, qualifiedConstant, beforeCode);
        var javaGetter = new JavaGetter(javaTypeName, javaFieldName, field.hasDefault());
        var annotation = new JavaAnnotation(ASN1Alternative.class).
                addParameter("name", '"' + typeConstant + '"');

        field.addAnnotation(annotation);

        if (tags.isPresent() && !tags.get().isEmpty()) {
            field.addAnnotation(CompilerUtils.getTagsAnnotation(tags.get()));
        }

        javaClass.addField(field, false, false);
        javaClass.addMethod(javaSetter);
        javaClass.addMethod(javaGetter);
    }

    private void addClearFieldsMethod(JavaClass javaClass, List<String> fieldNames) {
        var body = fieldNames.stream().map(f -> f + " = null;").collect(Collectors.toList());

        javaClass.method()
                .modifier(PRIVATE)
                .name(CLEAR_FIELDS)
                .body().append(body)
                .finish()
                .build();
    }

}
