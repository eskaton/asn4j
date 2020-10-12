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
package ch.eskaton.asn4j.compiler.constraints.elements;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.compiler.Parameters;
import ch.eskaton.asn4j.compiler.constraints.Bounds;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.ValueNode;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;

public class SingleValueCompiler<V extends Value, N extends ValueNode>
        implements ElementsCompiler<SingleValueConstraint> {

    protected final CompilerContext ctx;

    protected final TypeName typeName;

    protected final Class<V> valueClazz;

    protected final Class<N> valueNodeClazz;

    protected final Optional<Class<? extends Collection>> collectionType;

    public SingleValueCompiler(CompilerContext ctx, Class<V> valueClazz, Class<N> valueNodeClazz, TypeName typeName) {
        this(ctx, valueClazz, valueNodeClazz, typeName, null);
    }

    public SingleValueCompiler(CompilerContext ctx, Class<V> valueClazz, Class<N> valueNodeClazz, TypeName typeName,
            Class<? extends java.util.Collection> collectionType) {
        this.ctx = ctx;
        this.typeName = typeName;
        this.valueClazz = valueClazz;
        this.valueNodeClazz = valueNodeClazz;
        this.collectionType = Optional.ofNullable(collectionType);
    }

    @Override
    public Node compile(CompiledType baseType, SingleValueConstraint elements, Optional<Bounds> bounds,
            Optional<Parameters> maybeParameters) {
        var value = resolveValue(baseType, elements, maybeParameters.orElse(null));

        try {
            var ctor = collectionType.map(ct -> getConstructor(valueNodeClazz, ct))
                    .orElseGet(() -> getConstructor(valueNodeClazz, valueClazz));

            var wrappedValue = switch (collectionType.map(Class::getSimpleName).orElse("")) {
                case "Set" -> singleton(value);
                case "List" -> singletonList(value);
                case "" -> value;
                default -> throw new IllegalCompilerStateException("Unsupported collection type: %s",
                        collectionType.get().getSimpleName());
            };

            return ctor.newInstance(wrappedValue);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new CompilerException("Invalid single-value constraint %s for %s type", e,
                    value.getClass().getSimpleName(), typeName);
        }
    }

    protected <T> T resolveValue(CompiledType baseType, SingleValueConstraint elements, Parameters parameters) {
        var maybeParameters = Optional.ofNullable(parameters);

        return (T) ctx.getCompiledValue(baseType.getType(), elements.getValue(), maybeParameters).getValue();
    }

    private <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... parameterTypes) {
        try {
            if (Objects.equals(clazz, ValueNode.class)) {
                return clazz.getDeclaredConstructor(Object.class);
            }

            return clazz.getDeclaredConstructor(parameterTypes);
        } catch (Exception e) {
            throw new CompilerException(e);
        }
    }

}
