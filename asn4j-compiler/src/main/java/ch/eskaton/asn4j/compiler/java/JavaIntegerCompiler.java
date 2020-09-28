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
import ch.eskaton.asn4j.compiler.java.objs.JavaClass;
import ch.eskaton.asn4j.compiler.java.objs.JavaConstructor;
import ch.eskaton.asn4j.compiler.java.objs.JavaParameter;
import ch.eskaton.asn4j.compiler.java.objs.JavaStaticInitializer;
import ch.eskaton.asn4j.compiler.java.objs.JavaStructure;
import ch.eskaton.asn4j.compiler.java.objs.JavaVisibility;
import ch.eskaton.asn4j.compiler.results.CompiledIntegerType;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;

import java.math.BigInteger;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static ch.eskaton.asn4j.compiler.CompilerUtils.formatName;
import static ch.eskaton.asn4j.compiler.java.objs.JavaVisibility.PUBLIC;
import static java.util.Collections.singletonList;

public class JavaIntegerCompiler implements JavaTypeCompiler<CompiledIntegerType> {


    @Override
    public void compile(JavaCompiler compiler, CompilerContext ctx, Deque<JavaClass> classStack,
            Map<String, JavaStructure> compiledClasses, String pkg, CompiledIntegerType compiledType) {
        var name = compiledType.getName();
        var className = formatName(name);
        var tags = compiledType.getTags().orElse(List.of());
        var type = compiledType.getType();
        var javaClass = createClass(ctx, classStack, pkg, className, type, tags);

        generateIntegerJavaClass(ctx, javaClass, compiledType);

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


}
