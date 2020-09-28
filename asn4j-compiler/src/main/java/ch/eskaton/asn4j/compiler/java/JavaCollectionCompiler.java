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
import ch.eskaton.asn4j.compiler.java.objs.JavaParameter;
import ch.eskaton.asn4j.compiler.java.objs.JavaStructure;
import ch.eskaton.asn4j.compiler.java.objs.JavaVisibility;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionComponent;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionType;
import ch.eskaton.asn4j.parser.ast.types.Choice;
import ch.eskaton.asn4j.runtime.annotations.ASN1Component;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;

import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ch.eskaton.asn4j.compiler.CompilerUtils.formatName;

public class JavaCollectionCompiler implements JavaTypeCompiler<CompiledCollectionType> {

    @Override
    public void compile(JavaCompiler compiler, CompilerContext ctx, Deque<JavaClass> classStack,
            Map<String, JavaStructure> compiledClasses, String pkg, CompiledCollectionType compiledType) {
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
                compiler.compileType(ctx, classStack, compiledClasses, pkg, compiledComponent);
            }
        });

        finishClass(classStack, compiledClasses, true);
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

}
