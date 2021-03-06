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

import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.java.objs.JavaClass;
import ch.eskaton.asn4j.compiler.java.objs.JavaStructure;

import java.io.IOException;
import java.util.Map;

public class JavaWriter {

    public static final JavaDefaultCtorBuilder JAVA_DEFAULT_CTOR_BUILDER = new JavaDefaultCtorBuilder();

    public void write(Map<String, JavaStructure> structs, String outputDir) {
        createConstructors(structs);
        writeClasses(structs, outputDir);
    }

    private void createConstructors(Map<String, JavaStructure> structs) {
        JAVA_DEFAULT_CTOR_BUILDER.build(structs);

        structs.values().stream().
                filter(JavaClass.class::isInstance)
                .forEach(struct -> ((JavaClass) struct).getInnerClasses()
                        .forEach(innerClass -> createConstructors(Map.of(innerClass.getName(), innerClass))));
    }

    private void writeClasses(Map<String, JavaStructure> structs, String outputDir) {
        structs.values().forEach(struct -> {
            try {
                struct.save(outputDir);
            } catch (IOException e) {
                throw new CompilerException(e);
            }
        });
    }

}
