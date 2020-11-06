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

package ch.eskaton.asn4j.compiler.values;

import ch.eskaton.asn4j.compiler.Compiler;
import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.compiler.Parameters;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.compiler.results.CompiledValue;
import ch.eskaton.asn4j.compiler.types.formatters.TypeFormatter;
import ch.eskaton.asn4j.compiler.values.formatters.ValueFormatter;
import ch.eskaton.asn4j.parser.ast.ObjectReference;
import ch.eskaton.asn4j.parser.ast.values.DefinedValue;
import ch.eskaton.asn4j.parser.ast.values.ExternalValueReference;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.parser.ast.values.ValueFromObject;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.util.Map;
import java.util.Optional;

public abstract class AbstractValueCompiler<V extends Value> implements Compiler<V> {

    private TypeName typeName;

    private Class<V> valueClass;

    protected AbstractValueCompiler(TypeName typeName, Class<V> valueClass) {
        this.typeName = typeName;
        this.valueClass = valueClass;
    }

    public TypeName getTypeName() {
        return typeName;
    }

    public Class<V> getValueClass() {
        return valueClass;
    }

    public final V compile(CompilerContext ctx, CompiledType compiledType, Value value,
            Optional<Parameters> maybeParameters) {
        var resolvedCompiledType = ctx.getCompiledBaseType(compiledType);

        if (value instanceof DefinedValue definedValue) {
            return compileDefinedValue(ctx, resolvedCompiledType, definedValue, maybeParameters);
        }

        return doCompile(ctx, resolvedCompiledType, value, maybeParameters);
    }

    protected V compileDefinedValue(CompilerContext ctx, CompiledType compiledType, DefinedValue definedValue,
            Optional<Parameters> maybeParameters) {
        var reference = definedValue.getReference();
        CompiledValue<?> resolvedValue = null;

        if (definedValue instanceof ExternalValueReference externalValueReference) {
            var moduleName = externalValueReference.getModule();

            resolvedValue = ctx.getCompiledValue(moduleName, reference);
        } else if (definedValue instanceof SimpleDefinedValue simpleDefinedValue) {
            if (maybeParameters.isPresent()) {
                var maybeValue = ctx.getValueParameter(maybeParameters.get(), simpleDefinedValue);

                if (maybeValue.isPresent()) {
                    resolvedValue = ctx.getCompiledValue(compiledType.getType(), maybeValue.get());
                }
            }

            if (resolvedValue == null) {
                resolvedValue = compileSimpleDefinedValue(ctx, compiledType, reference);
            }
        } else {
            throw new IllegalCompilerStateException(definedValue.getPosition(), "Unsupported value reference: %s",
                    definedValue);
        }

        var expectedType = compiledType.getType();
        var resolvedType = ctx.getCompiledBaseType(resolvedValue.getCompiledType()).getType();

        if (!resolvedType.getClass().isAssignableFrom(expectedType.getClass())) {
            var formattedExpectedType = TypeFormatter.formatType(ctx, expectedType);
            var formattedResolvedType = TypeFormatter.formatType(ctx, resolvedType);
            var formattedDefinedValue = ValueFormatter.formatValue(definedValue);

            throw new ValueResolutionException(definedValue.getPosition(),
                    "Expected a value of type %s but '%s' refers to a value of type %s",
                    formattedExpectedType, formattedDefinedValue, formattedResolvedType);
        }

        return (V) resolvedValue.getValue();
    }

    @SuppressWarnings("unused")
    protected CompiledValue compileSimpleDefinedValue(CompilerContext ctx, CompiledType compiledType,
            String reference) {
        return ctx.getCompiledValue(reference);
    }

    @SuppressWarnings("unused")
    public V doCompile(CompilerContext ctx, CompiledType compiledType, Value value,
            Optional<Parameters> maybeParameters) {
        value = resolveValueFromObject(ctx, value);

        if (value.getClass().isAssignableFrom(valueClass)) {
            return (V) value;
        }

        throw invalidValueError(value);
    }

    protected Value resolveValueFromObject(CompilerContext ctx, Value value) {
        if (!(value instanceof ValueFromObject)) {
            return value;
        }

        var valueFromObject = (ValueFromObject) value;
        var referencedObjects = valueFromObject.getReference();

        if (!(referencedObjects instanceof ObjectReference)) {
            var formattedValue = ValueFormatter.formatValue(value);

            throw new IllegalCompilerStateException(value.getPosition(), "%s contains an invalid object reference",
                    formattedValue);
        }

        var objectReference = (ObjectReference) referencedObjects;
        var compiledObject = ctx.getCompiledObject(objectReference);
        var objectDefinition = compiledObject.getObjectDefinition();
        var fieldNames = valueFromObject.getField().getPrimitiveFieldNames();
        Object resolvedValue = null;

        for (var fieldName : fieldNames) {
            var fieldReference = fieldName.getReference();

            if (!fieldName.isValueFieldReference() || objectDefinition == null ||
                    !objectDefinition.containsKey(fieldReference)) {
                var formattedValue = ValueFormatter.formatValue(valueFromObject);

                throw new CompilerException(fieldName.getPosition(), "%s in %s doesn't refer to a value",
                        fieldName.getReference(), formattedValue);
            }

            resolvedValue = objectDefinition.get(fieldReference);

            if (resolvedValue instanceof Map) {
                objectDefinition = (Map<String, Object>) resolvedValue;
            } else {
                objectDefinition = null;
            }
        }

        if (!(value instanceof Value)) {
            var formattedValue = ValueFormatter.formatValue(valueFromObject);

            throw new CompilerException(valueFromObject.getPosition(), "%s doesn't refer to a value", formattedValue);
        }

        return (Value) resolvedValue;
    }

    protected CompilerException invalidValueError(Value value) {
        return new ValueResolutionException(value.getPosition(), "Invalid %s value: %s", typeName,
                ValueFormatter.formatValue(value));
    }

}
