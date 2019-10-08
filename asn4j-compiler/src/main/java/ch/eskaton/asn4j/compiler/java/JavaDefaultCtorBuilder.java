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

import ch.eskaton.asn4j.compiler.java.objs.JavaClass;
import ch.eskaton.asn4j.compiler.java.objs.JavaConstructor;
import ch.eskaton.asn4j.compiler.java.objs.JavaParameter;
import ch.eskaton.asn4j.compiler.java.objs.JavaStructure;
import ch.eskaton.asn4j.compiler.java.objs.JavaVisibility;
import ch.eskaton.asn4j.runtime.exceptions.ASN1RuntimeException;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class JavaDefaultCtorBuilder {

    public void build(Map<String, JavaStructure> structs) {
        ClassHierarchy hierarchy = buildClassHierarchy(structs);

        createDefaultConstructors(null, hierarchy.getJavaClass(), hierarchy.getSubclasses());
    }

    private void createDefaultConstructors(JavaClass parentJavaClass, JavaClass javaClass, Set<ClassHierarchy> subclasses) {
        if (javaClass != null) {
            createDefaultConstructors(parentJavaClass, javaClass);
        }

        for (ClassHierarchy subclass : subclasses) {
            createDefaultConstructors(javaClass, subclass.getJavaClass(), subclass.getSubclasses());
        }
    }

    private void createDefaultConstructors(JavaClass parentJavaClass, JavaClass javaClass) {
        if (javaClass.getParent().startsWith("ASN1")) {
            javaClass.generateParentConstructors();
        } else {
            createDefaultConstructor(parentJavaClass, javaClass);
        }
    }

    private void createDefaultConstructor(JavaClass parentJavaClass, JavaClass javaClass) {
        List<JavaConstructor> parentCtors = parentJavaClass.getConstructors().stream()
                .filter(ctor -> !ctor.getVisibility().equals(JavaVisibility.Private))
                .collect(Collectors.toList());

        List<JavaConstructor> childCtors = javaClass.getConstructors();

        for (JavaConstructor parentCtor : parentCtors) {
            JavaConstructor ctor = new JavaConstructor(parentCtor.getVisibility(), javaClass.getName(),
                    parentCtor.getParameters());

            if (!constructorDefined(childCtors, ctor)) {
                StringBuilder body = new StringBuilder("\t\tsuper(");

                body.append(ctor.getParameters().stream().map(JavaParameter::getName)
                        .collect(Collectors.joining(", ")));

                body.append(");\n");
                ctor.setBody(Optional.of(body.toString()));
                javaClass.addMethod(ctor);
            }
        }
    }

    private boolean constructorDefined(List<JavaConstructor> childCtors, JavaConstructor childCtor) {
        return childCtors.stream().noneMatch(ctor ->
                ctor.getClazz().equals(childCtor.getClazz())
                        && ctor.getParameters().equals(childCtor.getParameters())
                        && ctor.getVisibility().equals(childCtor.getVisibility()));
    }

    private ClassHierarchy buildClassHierarchy(Map<String, JavaStructure> structs) {
        ClassHierarchy root = new ClassHierarchy(null);

        for (JavaStructure struct : structs.values()) {
            if (!(struct instanceof JavaClass)) {
                throw new ASN1RuntimeException("Support for " + struct + " not implemented.");
            }

            JavaClass javaClass = (JavaClass) struct;

            Deque<JavaClass> clazzHierarchy = new LinkedList<>();

            clazzHierarchy.push(javaClass);

            String parent = javaClass.getParent();

            while (parent != null && !parent.startsWith("ASN1")) {
                JavaStructure parentStruct = structs.get(parent);

                if (parentStruct instanceof JavaClass) {
                    javaClass = (JavaClass) parentStruct;
                    parent = javaClass.getParent();

                    clazzHierarchy.push(javaClass);
                } else {
                    throw new ASN1RuntimeException("Support for " + struct + " not implemented.");
                }
            }

            addClassToHierarchy(clazzHierarchy, root.getSubclasses());
        }

        return root;
    }

    private void addClassToHierarchy(Deque<JavaClass> clazzHierarchy, Set<ClassHierarchy> subclasses) {
        if (clazzHierarchy.isEmpty()) {
            return;
        }

        JavaClass javaClass;

        subclasses:
        while (!clazzHierarchy.isEmpty()) {
            javaClass = clazzHierarchy.pop();

            for (ClassHierarchy subclass : subclasses) {
                if (subclass.getJavaClass().equals(javaClass)) {
                    addClassToHierarchy(clazzHierarchy, subclass.getSubclasses());
                    continue subclasses;
                }
            }

            ClassHierarchy subclass = new ClassHierarchy(javaClass);

            subclasses.add(subclass);

            addClassToHierarchy(clazzHierarchy, subclass.getSubclasses());
        }
    }

    private static class ClassHierarchy {

        private JavaClass javaClass;

        private Set<ClassHierarchy> subclasses = new HashSet<>();

        public ClassHierarchy(JavaClass javaClass) {
            this.javaClass = javaClass;
        }

        public JavaClass getJavaClass() {
            return javaClass;
        }

        public Set<ClassHierarchy> getSubclasses() {
            return subclasses;
        }

    }

}
