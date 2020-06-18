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

import ch.eskaton.asn4j.compiler.resolvers.ValueResolver;
import ch.eskaton.asn4j.parser.ast.Node;
import ch.eskaton.asn4j.parser.ast.values.Value;

public class TypeDefinition<T extends Node, V extends Value, R, S extends ValueResolver<V>, C extends Compiler<T>> {

    private Class<T> typeClass;

    private Class<V> valueClass;

    private S valueResolver;

    private Class<R> runtimeTypeClass;

    private Boolean constructed;

    private C compiler;

    public TypeDefinition(Class<T> typeClass, C compiler, Class<V> valueClass, Class<R> runtimeTypeClass, S valueResolver) {
        this.typeClass = typeClass;
        this.valueClass = valueClass;
        this.runtimeTypeClass = runtimeTypeClass;
        this.valueResolver = valueResolver;
        this.constructed = false;
        this.compiler = compiler;
    }

    public TypeDefinition(Class<T> typeClass, C compiler, Class<V> valueClass, Class<R> runtimeTypeClass, S valueResolver, boolean constructed) {
        this.typeClass = typeClass;
        this.valueClass = valueClass;
        this.runtimeTypeClass = runtimeTypeClass;
        this.valueResolver = valueResolver;
        this.constructed = constructed;
        this.compiler = compiler;
    }

    public TypeDefinition(Class<T> typeClass, C compiler) {
        this.typeClass = typeClass;
        this.compiler = compiler;
    }

    public Class<T> getTypeClass() {
        return typeClass;
    }

    public Class<V> getValueClass() {
        if (valueClass == null) {
            throw new IllegalCompilerStateException("Type %s has no associated value", typeClass.getSimpleName());
        }

        return valueClass;
    }

    public Class<R> getRuntimeTypeClass() {
        if (runtimeTypeClass == null) {
            throw new IllegalCompilerStateException("Type %s has no associated runtime type", typeClass.getSimpleName());
        }

        return runtimeTypeClass;
    }

    public S getValueResolver() {
        if (valueResolver == null) {
            throw new IllegalCompilerStateException("Type %s has no associated value resolver", typeClass.getSimpleName());
        }

        return valueResolver;
    }

    public boolean isConstructed() {
        if (constructed == null) {
            throw new IllegalCompilerStateException("Constructed attribute for %s is undefined", typeClass.getSimpleName());
        }

        return constructed;
    }

    public C getCompiler() {
        if (compiler == null) {
            throw new IllegalCompilerStateException("No compiler for type %s defined", typeClass.getSimpleName());
        }

        return compiler;
    }

    public boolean matchesType(Class<T> typeClass) {
        return this.typeClass.equals(typeClass);
    }

    public boolean matchesValue(Class<V> valueClass) {
        return valueClass != null && this.valueClass.equals(valueClass);
    }

}
