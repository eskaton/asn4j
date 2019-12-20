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
import ch.eskaton.asn4j.compiler.java.objs.JavaConstructor;
import ch.eskaton.asn4j.compiler.java.objs.JavaParameter;
import ch.eskaton.asn4j.compiler.java.objs.JavaStructure;
import ch.eskaton.asn4j.compiler.java.objs.JavaVisibility;
import ch.eskaton.asn4j.runtime.exceptions.ASN1RuntimeException;
import ch.eskaton.commons.MutableInteger;
import ch.eskaton.commons.utils.StringUtils;
import sun.reflect.generics.reflectiveObjects.TypeVariableImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
            generateParentConstructors(javaClass);
        } else {
            createDefaultConstructor(parentJavaClass, javaClass);
        }
    }

    public <T> void generateParentConstructors(JavaClass javaClass) {
        try {
            Class<? super T> parentClazz = (Class<? super T>) currentThread()
                    .getContextClassLoader().loadClass("ch.eskaton.asn4j.runtime.types." + javaClass.getParent());

            Arrays.stream(parentClazz.getConstructors())
                    .filter(this::isRelevantConstructor)
                    .forEach(ctor -> generateParentConstructor(javaClass, ctor));

        } catch (ClassNotFoundException e) {
            throw new CompilerException("Failed to resolve builtin type: " + javaClass.getParent());
        }
    }

    private boolean isRelevantConstructor(Constructor<?> ctor) {
        return !Modifier.isPrivate(ctor.getModifiers()) && !Modifier.isFinal(ctor.getModifiers());
    }

    private static void collectClasses(Set<Class> classes, Type type) {
        if (type instanceof Class) {
            Class clazz = (Class) type;

            if (!type.equals(Object.class) && !clazz.isPrimitive()) {
                if (clazz.isArray()) {
                    Class componentType = clazz.getComponentType();

                    if (!componentType.isPrimitive()) {
                        classes.add(componentType);
                    }
                } else {
                    classes.add(clazz);
                }
            }
        } else if (type instanceof ParameterizedType) {
            collectClasses(classes, ((ParameterizedType) type).getRawType());

            for (Type actualType : ((ParameterizedType) type).getActualTypeArguments()) {
                collectClasses(classes, actualType);
            }
        } else if (type instanceof GenericArrayType) {
            collectClasses(classes, ((GenericArrayType) type).getGenericComponentType());
        } else if (type instanceof WildcardType) {
            for (Type lowerBound : ((WildcardType) type).getLowerBounds()) {
                collectClasses(classes, lowerBound);
            }

            for (Type upperBound : ((WildcardType) type).getUpperBounds()) {
                collectClasses(classes, upperBound);
            }
        }
    }

    private void generateParentConstructor(JavaClass javaClass, Constructor<?> ctor) {
        JavaConstructor javaCtor = new JavaConstructor(getVisibility(ctor), javaClass.getName());

        Set<Class> classes = new HashSet<>();

        Arrays.stream(ctor.getGenericParameterTypes()).forEach(type -> collectClasses(classes, type));

        classes.stream().forEach(javaClass::addImport);

        List<JavaParameter> parameters = getParameters(ctor, javaClass.getTypeParam());

        javaCtor.getParameters().addAll(parameters);

        if (!isConstructorAvailable(javaClass, javaCtor)) {
            javaCtor.setBody(Optional.of("super(" + getParametersString(parameters) + ");"));
            javaClass.addMethod(javaCtor);
        }
    }

    private List<JavaParameter> getParameters(Constructor<?> ctor, String typeParam) {
        MutableInteger n = new MutableInteger(0);
        Class<?>[] parameterClasses = ctor.getParameterTypes();
        Type[] parameterTypes = ctor.getGenericParameterTypes();
        int parameterCount = parameterTypes.length;
        List<JavaParameter> parameters = new ArrayList<>(parameterCount);

        for (int i = 0; i < parameterCount; i++) {
            boolean isVarArgs = i == parameterCount - 1 && ctor.isVarArgs();
            Class<?> clazz = parameterClasses[i];
            Type type = parameterTypes[i];
            String typeName = getTypeName(type, typeParam, isVarArgs);

            parameters.add(new JavaParameter(typeName, "arg" + n.increment().getValue(), clazz));
        }

        return parameters;
    }

    private String getTypeName(Type type, String typeParam, boolean isVarArgs) {
        return getTypeNameAux(type, typeParam, isVarArgs, 1);
    }

    private String getTypeNameAux(Type type, String typeParam, boolean isVarArgs, int level) {
        if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            String typeName = getTypeNameAux(componentType, typeParam, isVarArgs, level + 1);

            return getArrayTypeName(isVarArgs, level, typeName);
        } else if (type instanceof TypeVariableImpl) {
            if ("T".equals(((TypeVariableImpl) type).getName()) && !StringUtils.isEmpty(typeParam)) {
                return typeParam;
            }

            return ((TypeVariableImpl) type).getName();
        } else if (type instanceof ParameterizedType) {
            String typeParameters = Arrays.stream(((ParameterizedType) type).getActualTypeArguments())
                    .map(t -> getTypeNameAux(t, typeParam, isVarArgs, level + 1)).collect(Collectors.joining(", "));

            return ((ParameterizedType) type).getRawType().getTypeName() + "<" + typeParameters + ">";
        } else if (type instanceof Class && ((Class) type).isArray()) {
            String typeName = getTypeNameAux(((Class) type).getComponentType(), typeParam, isVarArgs, level + 1);

            return getArrayTypeName(isVarArgs, level, typeName);
        }

        return type.getTypeName();
    }

    private String getArrayTypeName(boolean isVarArgs, int level, String typeName) {
        if (level == 1 & isVarArgs) {
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
        return javaCtor.getParameters().stream().map(p -> p.getType()).collect(Collectors.toList());
    }

    private JavaVisibility getVisibility(Executable executable) {
        JavaVisibility visibility = JavaVisibility.PACKAGE_PRIVATE;

        if (Modifier.isPublic(executable.getModifiers())) {
            visibility = JavaVisibility.PUBLIC;
        } else if (Modifier.isProtected(executable.getModifiers())) {
            visibility = JavaVisibility.PROTECTED;
        }

        return visibility;
    }

    private void createDefaultConstructor(JavaClass parentJavaClass, JavaClass javaClass) {
        List<JavaConstructor> parentCtors = parentJavaClass.getConstructors().stream()
                .filter(ctor -> !ctor.getVisibility().equals(JavaVisibility.PRIVATE))
                .collect(Collectors.toList());

        List<JavaConstructor> childCtors = javaClass.getConstructors();

        for (JavaConstructor parentCtor : parentCtors) {
            JavaConstructor ctor = new JavaConstructor(parentCtor.getVisibility(), javaClass.getName(),
                    parentCtor.getParameters());

            if (!constructorDefined(childCtors, ctor)) {
                StringBuilder body = new StringBuilder("\t\tsuper(");

                body.append(getParametersString(ctor.getParameters()));

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
