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
import ch.eskaton.asn4j.compiler.java.objs.JavaStructure;
import ch.eskaton.asn4j.compiler.results.CompiledBitStringType;
import ch.eskaton.asn4j.runtime.types.ASN1NamedBitString;

import java.util.Deque;
import java.util.Map;

import static ch.eskaton.asn4j.compiler.java.objs.JavaVisibility.PUBLIC;

public class JavaBitStringCompiler extends AbstractJavaTypeCompiler<CompiledBitStringType> {

    @Override
    protected boolean createEqualsAndHashCode() {
        return false;
    }

    @Override
    protected void configureJavaClass(JavaCompiler compiler, CompilerContext ctx, Deque<JavaClass> classStack,
            Map<String, JavaStructure> compiledClasses, CompiledBitStringType compiledType, JavaClass javaClass) {
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
    }

}
