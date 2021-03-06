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
import ch.eskaton.asn4j.compiler.CompilerUtils;
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.compiler.java.objs.JavaClass;
import ch.eskaton.asn4j.compiler.java.objs.JavaConstructor;
import ch.eskaton.asn4j.compiler.java.objs.JavaParameter;
import ch.eskaton.asn4j.compiler.java.objs.JavaStructure;
import ch.eskaton.asn4j.compiler.java.objs.JavaVisibility;
import ch.eskaton.asn4j.runtime.utils.ToString;
import ch.eskaton.commons.MutableInteger;
import ch.eskaton.commons.utils.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Thread.currentThread;

public class JavaDefaultCtorBuilder {

    public void build(Map<String, JavaStructure> structs) {
        var hierarchy = buildClassHierarchy(structs);

        createDefaultConstructors(null, hierarchy.getJavaClass(), hierarchy.getSubclasses());
    }

    private void createDefaultConstructors(JavaClass parentJavaClass, JavaClass javaClass,
            Set<ClassHierarchy> subclasses) {
        if (javaClass != null) {
            createDefaultConstructors(parentJavaClass, javaClass);
        }

        for (var subclass : subclasses) {
            createDefaultConstructors(javaClass, subclass.getJavaClass(), subclass.getSubclasses());
        }
    }

    private void createDefaultConstructors(JavaClass parentJavaClass, JavaClass javaClass) {
        if (javaClass.getParent().startsWith("ASN1")) {
            generateParentConstructors(javaClass);
        } else {
            createDefaultConstructor(parentJavaClass, javaClass);
        }
    }

    public <T> void generateParentConstructors(JavaClass javaClass) {
        try {
            var parentClazz = (Class<? super T>) currentThread()
                    .getContextClassLoader().loadClass("ch.eskaton.asn4j.runtime.types." + javaClass.getParent());

            Arrays.stream(parentClazz.getConstructors())
                    .filter(this::isRelevantConstructor)
                    .forEach(ctor -> generateParentConstructor(javaClass, ctor));

        } catch (ClassNotFoundException e) {
            throw new CompilerException("Failed to resolve builtin type: %s", javaClass.getParent());
        }
    }

    private boolean isRelevantConstructor(Constructor<?> ctor) {
        return !Modifier.isPrivate(ctor.getModifiers()) && !Modifier.isFinal(ctor.getModifiers());
    }

    private static void collectClasses(Set<Class<?>> classes, Type type) {
        if (type instanceof Class) {
            var clazz = (Class<?>) type;

            if (!type.equals(Object.class) && !clazz.isPrimitive()) {
                if (clazz.isArray()) {
                    Class<?> componentType = clazz.getComponentType();

                    if (!componentType.isPrimitive()) {
                        classes.add(componentType);
                    }
                } else {
                    classes.add(clazz);
                }
            }
        } else if (type instanceof ParameterizedType) {
            collectClasses(classes, ((ParameterizedType) type).getRawType());

            for (var actualType : ((ParameterizedType) type).getActualTypeArguments()) {
                collectClasses(classes, actualType);
            }
        } else if (type instanceof GenericArrayType) {
            collectClasses(classes, ((GenericArrayType) type).getGenericComponentType());
        } else if (type instanceof WildcardType) {
            for (var lowerBound : ((WildcardType) type).getLowerBounds()) {
                collectClasses(classes, lowerBound);
            }

            for (var upperBound : ((WildcardType) type).getUpperBounds()) {
                collectClasses(classes, upperBound);
            }
        }
    }

    private void generateParentConstructor(JavaClass javaClass, Constructor<?> ctor) {
        var javaCtor = new JavaConstructor(getVisibility(ctor), javaClass.getName());
        var classes = new HashSet<Class<?>>();

        Arrays.stream(ctor.getGenericParameterTypes()).forEach(type -> collectClasses(classes, type));

        classes.stream().forEach(javaClass::addImport);

        var parameters = getParameters(ctor, javaClass.getTypeParameter());

        javaCtor.getParameters().addAll(parameters);

        if (!isConstructorAvailable(javaClass, javaCtor)) {
            javaCtor.setBody(Optional.of("super(" + getParametersString(parameters) + ");"));
            javaClass.addMethod(javaCtor);
        }
    }

    private List<JavaParameter> getParameters(Constructor<?> ctor, Optional<List<String>> typeParameter) {
        var parameterClasses = ctor.getParameterTypes();
        var parameterTypes = ctor.getGenericParameterTypes();
        var parameterCount = parameterTypes.length;
        var parameters = new ArrayList<JavaParameter>(parameterCount);
        var n = new MutableInteger(0);

        for (var i = 0; i < parameterCount; i++) {
            var isVarArgs = i == parameterCount - 1 && ctor.isVarArgs();
            var clazz = parameterClasses[i];
            var type = parameterTypes[i];
            var typeName = getTypeName(type, typeParameter, isVarArgs);

            parameters.add(new JavaParameter(typeName, "arg" + n.increment().getValue(), clazz));
        }

        return parameters;
    }

    private String getTypeName(Type type, Optional<List<String>> typeParameter, boolean isVarArgs) {
        return getTypeNameAux(type, CompilerUtils.getTypeParameterString(typeParameter), isVarArgs, 1);
    }

    private String getTypeNameAux(Type type, String typeParameter, boolean isVarArgs, int level) {
        if (type instanceof GenericArrayType) {
            var componentType = ((GenericArrayType) type).getGenericComponentType();
            var typeName = getTypeNameAux(componentType, typeParameter, isVarArgs, level + 1);

            return getArrayTypeName(isVarArgs, level, typeName);
        } else if (type instanceof TypeVariable) {
            if ("T".equals(((TypeVariable) type).getName()) && !StringUtils.isEmpty(typeParameter)) {
                return typeParameter;
            }

            return ((TypeVariable) type).getName();
        } else if (type instanceof ParameterizedType) {
            var typeParameters = Arrays.stream(((ParameterizedType) type).getActualTypeArguments())
                    .map(t -> getTypeNameAux(t, typeParameter, isVarArgs, level + 1)).collect(Collectors.joining(", "));

            return ((ParameterizedType) type).getRawType().getTypeName() + "<" + typeParameters + ">";
        } else if (type instanceof Class && ((Class) type).isArray()) {
            var typeName = getTypeNameAux(((Class) type).getComponentType(), typeParameter, isVarArgs, level + 1);

            return getArrayTypeName(isVarArgs, level, typeName);
        }

        return type.getTypeName().replace("$", ".");
    }

    private String getArrayTypeName(boolean isVarArgs, int level, String typeName) {
        if (level == 1 && isVarArgs) {
            return typeName + "...";
        } else {
            return typeName + "[]";
        }
    }

    private String getParametersString(List<JavaParameter> parameters) {
        return parameters.stream().map(JavaParameter::getName).collect(Collectors.joining(", "));
    }

    private boolean isConstructorAvailable(JavaClass javaClass, JavaConstructor javaCtor) {
        return javaClass.getConstructors().stream()
                .anyMatch(c -> c.getClazz().equals(javaCtor.getClazz()) &&
                        getParameterTypes(c).equals(getParameterTypes(javaCtor)));
    }

    private List<String> getParameterTypes(JavaConstructor javaCtor) {
        return javaCtor.getParameters().stream().map(JavaParameter::getType).collect(Collectors.toList());
    }

    private JavaVisibility getVisibility(Executable executable) {
        var visibility = JavaVisibility.PACKAGE_PRIVATE;

        if (Modifier.isPublic(executable.getModifiers())) {
            visibility = JavaVisibility.PUBLIC;
        } else if (Modifier.isProtected(executable.getModifiers())) {
            visibility = JavaVisibility.PROTECTED;
        }

        return visibility;
    }

    private void createDefaultConstructor(JavaClass parentJavaClass, JavaClass javaClass) {
        var parentCtors = parentJavaClass.getConstructors().stream()
                .filter(ctor -> !ctor.getVisibility().equals(JavaVisibility.PRIVATE))
                .collect(Collectors.toList());
        var childCtors = javaClass.getConstructors();

        for (var parentCtor : parentCtors) {
            var ctor = new JavaConstructor(parentCtor.getVisibility(), javaClass.getName(),
                    parentCtor.getParameters());

            if (!constructorDefined(childCtors, ctor)) {
                var body = new StringBuilder("\t\tsuper(");

                body.append(getParametersString(ctor.getParameters()));

                body.append(");\n");
                ctor.setBody(Optional.of(body.toString()));
                javaClass.addMethod(ctor);
            }
        }
    }

    private boolean constructorDefined(List<JavaConstructor> childCtors, JavaConstructor childCtor) {
        return childCtors.stream().anyMatch(ctor ->
                ctor.getClazz().equals(childCtor.getClazz())
                        && ctor.getParameters().equals(childCtor.getParameters())
                        && ctor.getVisibility().equals(childCtor.getVisibility()));
    }

    private ClassHierarchy buildClassHierarchy(Map<String, JavaStructure> structs) {
        var root = new ClassHierarchy(null);

        for (var struct : structs.values()) {
            if (!(struct instanceof JavaClass)) {
                throw new IllegalCompilerStateException("Support for %s not implemented.", struct);
            }

            var javaClass = (JavaClass) struct;
            var clazzHierarchy = new LinkedList<JavaClass>();

            clazzHierarchy.push(javaClass);

            var parent = javaClass.getParent();

            while (parent != null && !parent.startsWith("ASN1")) {
                var parentStruct = structs.get(parent);

                if (parentStruct instanceof JavaClass) {
                    javaClass = (JavaClass) parentStruct;
                    parent = javaClass.getParent();

                    clazzHierarchy.push(javaClass);
                } else {
                    throw new IllegalCompilerStateException("Support for %s not implemented.", struct);
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

            for (var subclass : subclasses) {
                if (subclass.getJavaClass().equals(javaClass)) {
                    addClassToHierarchy(clazzHierarchy, subclass.getSubclasses());

                    continue subclasses;
                }
            }

            var subclass = new ClassHierarchy(javaClass);

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

        @Override
        public String toString() {
            return ToString.get(this);
        }
    }

}
