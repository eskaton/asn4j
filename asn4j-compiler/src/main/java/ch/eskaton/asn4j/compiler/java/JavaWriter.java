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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class JavaWriter {

    public void write(Map<String, JavaStructure> structs, String outputDir) {
        Set<JavaStructure> processed = new HashSet<>();

        for (JavaStructure struct : structs.values()) {
            if (processed.contains(struct)) {
                continue;
            }

            if (struct instanceof JavaClass) {
                JavaClass clazz = (JavaClass) struct;
                Deque<JavaClass> clazzHierarchy = getClassHierarchy(structs, clazz);

                List<JavaConstructor> constructors = new ArrayList<>();

                while (!clazzHierarchy.isEmpty()) {
                    clazz = clazzHierarchy.pop();

                    if (!constructors.isEmpty()) {
                        List<JavaConstructor> availableConstructors = clazz.getConstructors();

                        for (JavaConstructor constructor : constructors) {
                            JavaConstructor childConstructor = new JavaConstructor(constructor.getVisibility(),
                                    clazz.getName(), constructor.getParameters(), null);

                            boolean notAvailable = availableConstructors.stream().noneMatch(c ->
                                    c.getClazz().equals(childConstructor.getClazz())
                                            && c.getParameters().equals(childConstructor.getParameters())
                                            && c.getVisibility().equals(childConstructor.getVisibility()));

                            if (notAvailable) {
                                StringBuilder body = new StringBuilder("\t\tsuper(");

                                body.append(childConstructor.getParameters().stream().map(JavaParameter::getName)
                                        .collect(Collectors.joining(", ")));

                                body.append(");\n");
                                childConstructor.setBody(Optional.of(body.toString()));
                                clazz.addMethod(childConstructor);
                            }
                        }
                    }

                    constructors.clear();
                    constructors.addAll(clazz.getConstructors());
                    processed.add(clazz);
                }
            }
        }

        // write classes
        for (JavaStructure struct : structs.values()) {
            try {
                struct.save(outputDir);
            } catch (IOException e) {
                throw new CompilerException(e);
            }
        }

    }

    protected Deque<JavaClass> getClassHierarchy(Map<String, JavaStructure> structs, JavaClass clazz) {
        Deque<JavaClass> clazzHierarchy = new LinkedList<>();

        clazzHierarchy.push(clazz);

        String parent = clazz.getParent();

        while (parent != null && !parent.startsWith("ASN1")) {
            JavaStructure parentStruct = structs.get(parent);

            if (parentStruct instanceof JavaClass) {
                clazz = (JavaClass) parentStruct;
                parent = clazz.getParent();

                clazzHierarchy.push(clazz);
            } else if (parentStruct instanceof JavaInterface) {
                clazz.setParent(null);
                clazz.setInterface(parent);
                parent = null;
            } else {
                parent = null;
            }
        }

        return clazzHierarchy;
    }

}
