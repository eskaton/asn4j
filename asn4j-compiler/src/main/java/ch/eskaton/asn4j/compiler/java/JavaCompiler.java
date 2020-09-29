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
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.java.objs.JavaClass;
import ch.eskaton.asn4j.compiler.java.objs.JavaStructure;
import ch.eskaton.asn4j.compiler.results.CompiledBitStringType;
import ch.eskaton.asn4j.compiler.results.CompiledChoiceType;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionOfType;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionType;
import ch.eskaton.asn4j.compiler.results.CompiledEnumeratedType;
import ch.eskaton.asn4j.compiler.results.CompiledIntegerType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.commons.collections.Tuple6;
import ch.eskaton.commons.functional.HexaConsumer;
import ch.eskaton.commons.utils.Dispatcher;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static ch.eskaton.commons.utils.Utils.callWithConsumer;

public class JavaCompiler {

    public static final Dispatcher<CompiledType, Class<? extends CompiledType>, Tuple6<JavaCompiler, CompilerContext, Deque<JavaClass>, Map<String, JavaStructure>, String, ? extends CompiledType>, Boolean> DISPATCHER =
            new Dispatcher<CompiledType, Class<? extends CompiledType>, Tuple6<JavaCompiler, CompilerContext, Deque<JavaClass>, Map<String, JavaStructure>, String, ? extends CompiledType>, Boolean>()
                    .withComparator((t, u) -> u.isInstance(t))
                    .withException(t -> new CompilerException("JavaCompiler for compiled type %s not defined", t));

    static {
        addCase(CompiledCollectionType.class, new JavaCollectionCompiler()::compile);
        addCase(CompiledCollectionOfType.class, new JavaCollectionOfCompiler()::compile);
        addCase(CompiledChoiceType.class, new JavaChoiceCompiler()::compile);
        addCase(CompiledBitStringType.class, new JavaBitStringCompiler()::compile);
        addCase(CompiledEnumeratedType.class, new JavaEnumeratedCompiler()::compile);
        addCase(CompiledIntegerType.class, new JavaIntegerCompiler()::compile);
        addCase(CompiledType.class, new JavaDefaultTypeCompiler()::compile);
    }

    private static <T extends CompiledType> void addCase(Class<T> compiledTypeClass,
            HexaConsumer<JavaCompiler, CompilerContext, Deque<JavaClass>, Map<String, JavaStructure>, String, T> compiler) {
        DISPATCHER.withCase(compiledTypeClass,
                maybeArgs -> callWithConsumer(args -> compiler.accept(args.get_1(), args.get_2(), args.get_3(), args.get_4(), args.get_5(), (T) args.get_6()), maybeArgs.get(), true));
    }

    public Map<String, JavaStructure> compile(CompilerContext ctx, Map<String, CompiledType> compiledTypes,
            String pkg) {
        var compiledClasses = new HashMap<String, JavaStructure>();

        compile(ctx, new LinkedList<>(), compiledClasses, pkg, compiledTypes);

        return compiledClasses;
    }

    public void compile(CompilerContext ctx, Deque<JavaClass> classStack, Map<String, JavaStructure> compiledClasses,
            String pkg, Map<String, CompiledType> compiledTypes) {
        for (var compiledType : compiledTypes.values()) {
            compileType(ctx, classStack, compiledClasses, pkg, compiledType);
        }
    }

    protected void compileType(CompilerContext ctx, Deque<JavaClass> classStack, Map<String,
            JavaStructure> compiledClasses, String pkg, CompiledType compiledType) {
        DISPATCHER.execute(compiledType, Tuple6.of(this, ctx, classStack, compiledClasses, pkg, compiledType));
    }

}
