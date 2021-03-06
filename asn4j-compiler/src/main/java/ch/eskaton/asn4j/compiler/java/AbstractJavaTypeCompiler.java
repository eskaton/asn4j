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
import ch.eskaton.asn4j.compiler.java.objs.JavaClass;
import ch.eskaton.asn4j.compiler.java.objs.JavaModifier;
import ch.eskaton.asn4j.compiler.java.objs.JavaStructure;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;

import java.util.Deque;
import java.util.List;
import java.util.Map;

import static ch.eskaton.asn4j.compiler.CompilerUtils.formatName;

public abstract class AbstractJavaTypeCompiler<T extends CompiledType> {

    protected void compile(JavaCompiler compiler, CompilerContext ctx, Deque<JavaClass> classStack,
            Map<String, JavaStructure> compiledClasses, String pkg, T compiledType) {
        var javaClass = createClass(ctx, classStack, pkg, compiledType);

        configureJavaClass(compiler, ctx, classStack, compiledClasses, compiledType, javaClass);

        finishClass(classStack, compiledClasses, createEqualsAndHashCode());
    }

    protected boolean createEqualsAndHashCode() {
        return true;
    }

    protected JavaClass createClass(CompilerContext ctx, Deque<JavaClass> classes, String pkg,
            CompiledType compiledType) {
        var name = compiledType.getName();
        var className = formatName(name);
        var tags = compiledType.getTags().orElse(List.of());
        var type = compiledType.getType();
        var javaClass = new JavaClass(pkg, className, tags, ctx.getTypeName(type));

        if (compiledType.getModule().isPresent()) {
            javaClass.addModule(ctx, compiledType.getModule().get());
            javaClass.addImport(ConstraintViolatedException.class);
        }

        classes.push(javaClass);

        return javaClass;
    }

    protected void finishClass(Deque<JavaClass> classes, Map<String, JavaStructure> compiledClasses,
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

    protected void configureJavaClass(JavaCompiler compiler, CompilerContext ctx, Deque<JavaClass> classStack,
            Map<String, JavaStructure> compiledClasses, T compiledType, JavaClass javaClass) {
    }

}
