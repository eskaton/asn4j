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
import ch.eskaton.asn4j.compiler.java.objs.JavaStructure;
import ch.eskaton.asn4j.compiler.java.objs.JavaVisibility;
import ch.eskaton.asn4j.compiler.results.CompiledEnumeratedType;
import ch.eskaton.asn4j.runtime.exceptions.ASN1RuntimeException;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static ch.eskaton.asn4j.compiler.java.objs.JavaType.INT;
import static ch.eskaton.asn4j.compiler.java.objs.JavaVisibility.PUBLIC;
import static java.util.Arrays.asList;

public class JavaEnumeratedCompiler extends AbstractJavaTypeCompiler<CompiledEnumeratedType> {

    @Override
    protected boolean createEqualsAndHashCode() {
        return false;
    }

    @Override
    protected void configureJavaClass(JavaCompiler compiler, CompilerContext ctx, Deque<JavaClass> classStack,
            Map<String, JavaStructure> compiledClasses, CompiledEnumeratedType compiledType, JavaClass javaClass) {
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
    }

}
