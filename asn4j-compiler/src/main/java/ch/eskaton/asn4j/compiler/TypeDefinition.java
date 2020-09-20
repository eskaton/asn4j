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

package ch.eskaton.asn4j.compiler;

import ch.eskaton.asn4j.compiler.constraints.AbstractConstraintCompiler;
import ch.eskaton.asn4j.compiler.defaults.AbstractDefaultCompiler;
import ch.eskaton.asn4j.compiler.defaults.DefaultCompilerImpl;
import ch.eskaton.asn4j.compiler.typenamesuppliers.TypeNameSupplier;
import ch.eskaton.asn4j.compiler.values.AbstractValueCompiler;
import ch.eskaton.asn4j.parser.ast.Node;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.Value;

public class TypeDefinition<T extends Node, V extends Value, R, S extends AbstractValueCompiler<V>, C extends Compiler<T>,
        D extends AbstractDefaultCompiler<V>, K extends AbstractConstraintCompiler> {

    private Class<T> typeClass;

    private Class<V> valueClass;

    private S valueCompiler;

    private Class<R> runtimeTypeClass;

    private TypeNameSupplier<? extends Type> typeNameSupplier;

    private D defaultCompiler;

    private K constraintCompiler;

    private C compiler;

    public TypeDefinition(Class<T> typeClass, C compiler, Class<V> valueClass, Class<R> runtimeTypeClass,
            S valueCompiler, TypeNameSupplier<? extends Type> typeNameSupplier, K constraintCompiler) {
        this(typeClass, compiler, valueClass, runtimeTypeClass, valueCompiler, typeNameSupplier,
                (D) new DefaultCompilerImpl<V>(valueClass), constraintCompiler);
    }

    public TypeDefinition(Class<T> typeClass, C compiler, Class<V> valueClass, Class<R> runtimeTypeClass,
            S valueCompiler, TypeNameSupplier<? extends Type> typeNameSupplier, D defaultCompiler, K constraintCompiler) {
        this.typeClass = typeClass;
        this.compiler = compiler;
        this.valueClass = valueClass;
        this.runtimeTypeClass = runtimeTypeClass;
        this.valueCompiler = valueCompiler;
        this.typeNameSupplier = typeNameSupplier;
        this.defaultCompiler = defaultCompiler;
        this.constraintCompiler = constraintCompiler;
    }

    public TypeDefinition(Class<T> typeClass, C compiler, TypeNameSupplier<? extends Type> typeNameSupplier) {
        this(typeClass, compiler);

        this.typeNameSupplier = typeNameSupplier;
    }

    public TypeDefinition(Class<T> typeClass, C compiler) {
        this.typeClass = typeClass;
        this.compiler = compiler;
    }

    public Class<T> getTypeClass() {
        return typeClass;
    }

    public C getCompiler() {
        if (compiler == null) {
            throw new IllegalCompilerStateException("No compiler for type %s defined", typeClass.getSimpleName());
        }

        return compiler;
    }

    public Class<V> getValueClass() {
        if (valueClass == null) {
            throw new IllegalCompilerStateException("Type %s has no associated value", typeClass.getSimpleName());
        }

        return valueClass;
    }

    public Class<R> getRuntimeTypeClass() {
        if (runtimeTypeClass == null) {
            throw new IllegalCompilerStateException("Type %s has no associated runtime type",
                    typeClass.getSimpleName());
        }

        return runtimeTypeClass;
    }

    public S getValueCompiler() {
        if (valueCompiler == null) {
            throw new IllegalCompilerStateException("Type %s has no associated value resolver",
                    typeClass.getSimpleName());
        }

        return valueCompiler;
    }

    public TypeNameSupplier getTypeNameSupplier() {
        if (typeNameSupplier == null) {
            throw new IllegalCompilerStateException("Type %s has no associated type name supplier",
                    typeClass.getSimpleName());
        }

        return typeNameSupplier;
    }

    public D getDefaultCompiler() {
        if (defaultCompiler == null) {
            throw new IllegalCompilerStateException("Type %s has no associated default compiler",
                    typeClass.getSimpleName());
        }

        return defaultCompiler;
    }

    public K getConstraintCompiler() {
        if (constraintCompiler == null) {
            throw new IllegalCompilerStateException("Type %s has no associated constraint compiler",
                    typeClass.getSimpleName());
        }

        return constraintCompiler;
    }

    public boolean isBuiltin() {
        return valueClass != null;
    }

    public boolean matchesType(Class<T> typeClass) {
        return this.typeClass.equals(typeClass);
    }

    public boolean matchesValue(Class<V> valueClass) {
        return valueClass != null && this.valueClass != null && this.valueClass.equals(valueClass);
    }

    public boolean matchesRuntimeType(String runtimeTypeName) {
        return runtimeTypeClass != null && this.runtimeTypeClass.getSimpleName().equals(runtimeTypeName);
    }

}
