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
import ch.eskaton.asn4j.compiler.java.objs.JavaConstructor;
import ch.eskaton.asn4j.compiler.java.objs.JavaDefinedField;
import ch.eskaton.asn4j.compiler.java.objs.JavaEnum;
import ch.eskaton.asn4j.compiler.java.objs.JavaGetter;
import ch.eskaton.asn4j.compiler.java.objs.JavaModifier;
import ch.eskaton.asn4j.compiler.java.objs.JavaParameter;
import ch.eskaton.asn4j.compiler.java.objs.JavaStaticInitializer;
import ch.eskaton.asn4j.compiler.java.objs.JavaStructure;
import ch.eskaton.asn4j.compiler.java.objs.JavaTypedSetter;
import ch.eskaton.asn4j.compiler.java.objs.JavaVisibility;
import ch.eskaton.asn4j.compiler.results.AnonymousCompiledCollectionOfType;
import ch.eskaton.asn4j.compiler.results.CompiledBitStringType;
import ch.eskaton.asn4j.compiler.results.CompiledChoiceType;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionComponent;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionOfType;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionType;
import ch.eskaton.asn4j.compiler.results.CompiledComponent;
import ch.eskaton.asn4j.compiler.results.CompiledEnumeratedType;
import ch.eskaton.asn4j.compiler.results.CompiledIntegerType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.types.Choice;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.runtime.TagId;
import ch.eskaton.asn4j.runtime.annotations.ASN1Alternative;
import ch.eskaton.asn4j.runtime.annotations.ASN1Component;
import ch.eskaton.asn4j.runtime.exceptions.ASN1RuntimeException;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;
import ch.eskaton.asn4j.runtime.types.ASN1NamedBitString;
import ch.eskaton.asn4j.runtime.types.ASN1Type;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.CompilerUtils.formatName;
import static ch.eskaton.asn4j.compiler.java.objs.JavaType.INT;
import static ch.eskaton.asn4j.compiler.java.objs.JavaVisibility.PRIVATE;
import static ch.eskaton.asn4j.compiler.java.objs.JavaVisibility.PUBLIC;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class JavaCompiler {
    
    public static final String CLEAR_FIELDS = "clearFields";

    private static final String CHOICE_ENUM = "Choice";

    private static final String CHOICE_FIELD = "choice";

    public HashMap<String, JavaStructure> compile(CompilerContext ctx, Map<String, CompiledType> compiledTypes,
            String pkg) {
        var compiledClasses = new HashMap<String, JavaStructure>();

        compile(ctx, compiledTypes, pkg, new LinkedList<>(), compiledClasses);

        return compiledClasses;
    }

    public void compile(CompilerContext ctx, Map<String, CompiledType> compiledTypes, String pkg,
            Deque<JavaClass> classStack, HashMap<String, JavaStructure> compiledClasses) {
        for (var compiledType : compiledTypes.values()) {
            compile(ctx, pkg, classStack, compiledClasses, compiledType);
        }
    }

    private void compile(CompilerContext ctx, String pkg, Deque<JavaClass> classStack, HashMap<String,
            JavaStructure> compiledClasses, CompiledType compiledType) {
        if (compiledType instanceof CompiledCollectionType compiledCollectionType) {
            compileCollectionType(ctx, compiledCollectionType, pkg, classStack, compiledClasses);
        } else if (compiledType instanceof CompiledCollectionOfType compiledCollectionOfType) {
            compileCollectionOfType(ctx, compiledCollectionOfType, pkg, classStack, compiledClasses);
        } else if (compiledType instanceof CompiledChoiceType compiledChoiceType) {
            compileChoiceType(ctx, compiledChoiceType, pkg, classStack, compiledClasses);
        } else if (compiledType instanceof CompiledBitStringType compiledBitStringType) {
            compileBitStringType(ctx, compiledBitStringType, pkg, classStack, compiledClasses);
        } else if (compiledType instanceof CompiledEnumeratedType compiledEnumeratedType) {
            compileEnumeratedType(ctx, compiledEnumeratedType, pkg, classStack, compiledClasses);
        } else if (compiledType instanceof CompiledIntegerType compiledIntegerType) {
            compileIntegerType(ctx, compiledIntegerType, pkg, classStack, compiledClasses);
        } else {
            compileType(ctx, compiledType, pkg, classStack, compiledClasses);
        }
    }

    private void compileCollectionType(CompilerContext ctx, CompiledCollectionType compiledType, String pkg,
            Deque<JavaClass> classStack, HashMap<String, JavaStructure> compiledClasses) {
        var name = compiledType.getName();
        var className = formatName(name);
        var tags = compiledType.getTags().orElse(List.of());
        var type = compiledType.getType();
        var javaClass = createClass(ctx, classStack, pkg, className, type, tags);

        if (compiledType.getModule().isPresent()) {
            javaClass.addModule(ctx, compiledType.getModule().get());
            javaClass.addImport(ConstraintViolatedException.class);
        }

        createJavaConstructors(name, compiledType, javaClass);

        compiledType.getComponents().forEach(component -> {
            addJavaField(ctx, classStack, component);

            var compiledComponent = component.getCompiledType();

            if (compiledComponent.isSubtype()) {
                compile(ctx, pkg, classStack, compiledClasses, compiledComponent);
            }
        });

        finishClass(classStack, compiledClasses, true);
    }

    private void addJavaField(CompilerContext ctx, Deque<JavaClass> classStack,
            CompiledCollectionComponent compiledComponent) {
        var compiledType = compiledComponent.getCompiledType();
        var maybeDefaultValue = compiledComponent.getDefaultValue();
        var hasDefault = maybeDefaultValue.isPresent();
        var isOptional = compiledComponent.isOptional();
        var type = compiledType.getType();
        var javaClass = classStack.peek();
        var javaTypeName = compiledType.getName();
        var javaFieldName = formatName(compiledComponent.getName());
        var field = new JavaDefinedField(javaTypeName, javaFieldName, hasDefault);
        var compAnnotation = new JavaAnnotation(ASN1Component.class);

        if (isOptional) {
            compAnnotation.addParameter("optional", "true");
        } else if (hasDefault) {
            compAnnotation.addParameter("hasDefault", "true");

            if (type instanceof Choice) {
                javaClass.addStaticImport(ch.eskaton.commons.utils.Utils.class, "with");
            }

            var defaultValue = maybeDefaultValue.get();

            ctx.addDefaultField(ctx, javaClass, field.getName(), javaTypeName, defaultValue);
        }

        field.addAnnotation(compAnnotation);

        var tags = compiledType.getTags();

        if (tags.isPresent() && !tags.get().isEmpty()) {
            field.addAnnotation(CompilerUtils.getTagsAnnotation(tags.get()));
        }

        javaClass.addField(field);
    }

    private void createJavaConstructors(String name, CompiledCollectionType compiledType, JavaClass javaClass) {
        var ctor = new JavaConstructor(JavaVisibility.PUBLIC, name);
        var ctorBody = new StringBuilder();

        compiledType.getComponents().forEach(component -> {
            var argType = component.getCompiledType().getName();
            var argName = CompilerUtils.formatName(component.getName());

            ctor.getParameters().add(new JavaParameter(argType, argName));
            ctorBody.append("\t\tthis.%s = %s;\n".formatted(argName, argName));
        });

        ctor.setBody(Optional.of(ctorBody.toString()));
        javaClass.addMethod(ctor);
    }

    private void compileCollectionOfType(CompilerContext ctx, CompiledCollectionOfType compiledType, String pkg,
            Deque<JavaClass> classStack, HashMap<String, JavaStructure> compiledClasses) {
        var name = compiledType.getName();
        var className = formatName(name);
        var tags = compiledType.getTags().orElse(List.of());
        var type = compiledType.getType();
        var javaClass = createClass(ctx, classStack, pkg, className, type, tags);

        javaClass.typeParameter(ctx.getTypeParameter(type, Optional.of(name)));

        var contentType = compiledType.getContentType();

        while (contentType instanceof AnonymousCompiledCollectionOfType collectionOfType) {
            contentType = collectionOfType.getContentType();
        }

        if (contentType.isSubtype()) {
            compile(ctx, pkg, classStack, compiledClasses, contentType);
        }

        if (compiledType.getModule().isPresent()) {
            javaClass.addModule(ctx, compiledType.getModule().get());
            javaClass.addImport(ConstraintViolatedException.class);
        }

        finishClass(classStack, compiledClasses, true);
    }

    private void compileChoiceType(CompilerContext ctx, CompiledChoiceType compiledType, String pkg,
            Deque<JavaClass> classStack, HashMap<String, JavaStructure> compiledClasses) {
        var name = compiledType.getName();
        var className = formatName(name);
        var tags = compiledType.getTags().orElse(List.of());
        var type = compiledType.getType();
        var javaClass = createClass(ctx, classStack, pkg, className, type, tags);

        generateChoiceJavaClass(ctx, javaClass, compiledType);

        compiledType.getComponents().forEach(component -> {
            var compiledComponent = component.getCompiledType();

            if (compiledComponent.isSubtype()) {
                compile(ctx, pkg, classStack, compiledClasses, compiledComponent);
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

    private void compileBitStringType(CompilerContext ctx, CompiledBitStringType compiledType, String pkg,
            Deque<JavaClass> classStack, HashMap<String, JavaStructure> compiledClasses) {
        var name = compiledType.getName();
        var className = formatName(name);
        var tags = compiledType.getTags().orElse(List.of());
        var type = compiledType.getType();
        var javaClass = createClass(ctx, classStack, pkg, className, type, tags);

        generateBitStringJavaClass(ctx, javaClass, compiledType);

        finishClass(classStack, compiledClasses, false);
    }

    private void generateBitStringJavaClass(CompilerContext ctx, JavaClass javaClass,
            CompiledBitStringType compiledType) {
        var name = compiledType.getName();
        var namedBits = compiledType.getNamedBits();

        javaClass.setParent(ASN1NamedBitString.class.getSimpleName());

        if (namedBits.isPresent()) {
            for (var namedBit : namedBits.get().entrySet()) {
                var value = namedBit.getValue();
                var fieldName = CompilerUtils.formatConstant(namedBit.getKey());

                javaClass.field()
                        .modifier(PUBLIC)
                        .asStatic()
                        .asFinal()
                        .type(int.class)
                        .name(fieldName)
                        .initializer(String.valueOf(value))
                        .build();
            }
        }

        javaClass.method().modifier(PUBLIC).name(name).build();

        if (compiledType.getModule().isPresent()) {
            javaClass.addModule(ctx, compiledType.getModule().get());
            javaClass.addImport(ConstraintViolatedException.class);
        }
    }

    private void compileEnumeratedType(CompilerContext ctx, CompiledEnumeratedType compiledType, String pkg,
            Deque<JavaClass> classStack, HashMap<String, JavaStructure> compiledClasses) {
        var name = compiledType.getName();
        var className = formatName(name);
        var tags = compiledType.getTags().orElse(List.of());
        var type = compiledType.getType();
        var javaClass = createClass(ctx, classStack, pkg, className, type, tags);

        generateEnumeratedJavaClass(ctx, javaClass, compiledType);

        finishClass(classStack, compiledClasses, false);
    }

    private JavaClass generateEnumeratedJavaClass(CompilerContext ctx, JavaClass javaClass,
            CompiledEnumeratedType compiledType) {
        final String VALUE_PARAMETER = "value";

        var name = compiledType.getName();
        var allItems = compiledType.getRoots().copy().addAll(compiledType.getAdditions().getItems());
        var cases = new HashMap<Integer, String>();

        allItems.getItems().forEach(item -> {
            var fieldName = CompilerUtils.formatConstant(item.get_1());
            var value = item.get_2();
            var initializer = "new %s(%s)".formatted(name, value);

            cases.put(value, fieldName);

            javaClass.field()
                    .modifier(PUBLIC)
                    .asStatic()
                    .asFinal()
                    .type(name)
                    .name(fieldName)
                    .initializer(initializer)
                    .build();
        });

        javaClass.addMethod(new JavaConstructor(JavaVisibility.PUBLIC, name));
        javaClass.addMethod(new JavaConstructor(JavaVisibility.PROTECTED, name,
                asList(new JavaParameter("int", VALUE_PARAMETER)), Optional.of("\t\tsuper.setValue(value);")));
        javaClass.addMethod(new JavaConstructor(JavaVisibility.PUBLIC, name,
                asList(new JavaParameter(name, VALUE_PARAMETER)), Optional.of("\t\tsuper.setValue(value.getValue());")));

        var bodyBuilder = javaClass.method()
                .asStatic()
                .returnType(name).name("valueOf")
                .parameter(INT, VALUE_PARAMETER)
                .exception(ASN1RuntimeException.class)
                .body();

        bodyBuilder.append("switch(value) {");

        for (var entry : cases.entrySet()) {
            bodyBuilder.append("\tcase " + entry.getKey() + ":");
            bodyBuilder.append("\t\treturn " + entry.getValue() + ";");
        }

        bodyBuilder.append("\tdefault:")
                .append("\t\tthrow new " + ASN1RuntimeException.class.getSimpleName() +
                        "(\"Undefined value: \" + value);").append("}");

        bodyBuilder.finish().build();

        javaClass.addImport(ASN1RuntimeException.class.getCanonicalName());

        if (compiledType.getModule().isPresent()) {
            javaClass.addModule(ctx, compiledType.getModule().get());
            javaClass.addImport(ConstraintViolatedException.class);
        }

        return javaClass;
    }

    private void compileIntegerType(CompilerContext ctx, CompiledIntegerType compiledType, String pkg,
            Deque<JavaClass> classStack, HashMap<String, JavaStructure> compiledClasses) {
        var name = compiledType.getName();
        var className = formatName(name);
        var tags = compiledType.getTags().orElse(List.of());
        var type = compiledType.getType();
        var javaClass = createClass(ctx, classStack, pkg, className, type, tags);

        generateIntegerJavaClass(ctx, javaClass, compiledType);

        finishClass(classStack, compiledClasses, true);
    }

    private void compileType(CompilerContext ctx, CompiledType compiledType, String pkg, Deque<JavaClass> classStack,
            HashMap<String, JavaStructure> compiledClasses) {
        var name = compiledType.getName();
        var className = formatName(name);
        var tags = compiledType.getTags().orElse(List.of());
        var type = compiledType.getType();
        var javaClass = createClass(ctx, classStack, pkg, className, type, tags);

        if (compiledType.getModule().isPresent()) {
            javaClass.addModule(ctx, compiledType.getModule().get());
            javaClass.addImport(ConstraintViolatedException.class);
        }

        finishClass(classStack, compiledClasses, true);
    }

    private void generateIntegerJavaClass(CompilerContext ctx, JavaClass javaClass, CompiledIntegerType compiledType) {
        var name = compiledType.getName();
        var namedNumbers = compiledType.getNamedNumbers();

        if (namedNumbers.isPresent()) {
            var staticBody = new StringBuilder();

            staticBody.append("\t\ttry {\n");

            for (var namedNumber : namedNumbers.get().entrySet()) {
                var value = namedNumber.getValue();
                var fieldName = CompilerUtils.formatConstant(namedNumber.getKey());
                var initializer = "new %s(%s);\n".formatted(name, value);

                javaClass.field()
                        .modifier(PUBLIC)
                        .asStatic()
                        .asFinal()
                        .type(name)
                        .name(fieldName)
                        .initializer(initializer)
                        .build();
            }

            staticBody.append("\t\t} catch (")
                    .append(ConstraintViolatedException.class.getSimpleName())
                    .append(" e){\n");
            staticBody.append("\t\t\tthrow new RuntimeException(e);\n");
            staticBody.append("\t\t}");

            javaClass.addStaticInitializer(new JavaStaticInitializer(staticBody.toString()));
        }

        javaClass.addImport(BigInteger.class, ConstraintViolatedException.class);

        addJavaConstructor(javaClass, name);

        if (compiledType.getModule().isPresent()) {
            javaClass.addModule(ctx, compiledType.getModule().get());
        }
    }

    private void addJavaConstructor(JavaClass javaClass, String name) {
        var parameters = singletonList(new JavaParameter("long", "value"));
        var exceptions = singletonList(ConstraintViolatedException.class.getName());
        var body = Optional.of("\t\tsuper.setValue(BigInteger.valueOf(value));");
        var javaConstructor = new JavaConstructor(JavaVisibility.PROTECTED, name, parameters, body, exceptions);

        javaClass.addMethod(javaConstructor);
    }

    public JavaClass createClass(CompilerContext ctx, Deque<JavaClass> classes, String pkg, String name, Type type,
            List<TagId> tags) {
        var javaClass = new JavaClass(pkg, formatName(name), tags, ctx.getTypeName(type));

        classes.push(javaClass);

        return javaClass;
    }

    public void finishClass(Deque<JavaClass> classes, HashMap<String, JavaStructure> compiledClasses,
            boolean createEqualsAndHashCode) {
        var javaClass = classes.pop();

        if (createEqualsAndHashCode) {
            javaClass.createEqualsAndHashCode();
        }

        if (classes.isEmpty()) {
            compiledClasses.put(javaClass.getName(), javaClass);
        } else {
            classes.peek().addInnerClass(javaClass);
            javaClass.addModifier(JavaModifier.STATIC);
        }
    }

}
