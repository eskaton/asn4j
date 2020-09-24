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

package ch.eskaton.asn4j.compiler.defaults;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerUtils;
import ch.eskaton.asn4j.compiler.Parameters;
import ch.eskaton.asn4j.compiler.java.JavaUtils;
import ch.eskaton.asn4j.compiler.java.objs.JavaClass;
import ch.eskaton.asn4j.compiler.java.objs.JavaDefinedField;
import ch.eskaton.asn4j.compiler.java.objs.JavaInitializer;
import ch.eskaton.asn4j.compiler.results.CompiledValue;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Value;

import java.util.Optional;

public class DefaultCompilerImpl<V extends Value> extends AbstractDefaultCompiler<V> {

    protected Class<V> valueClass;

    public DefaultCompilerImpl(Class<V> valueClass) {
        this.valueClass = valueClass;
    }

    @Override
    public CompiledValue<V> compileDefault(CompilerContext ctx, Type type, V value,
            Optional<Parameters> maybeParameters) {
        var valueToResolve = (Value) value;

        if (value instanceof SimpleDefinedValue simpleDefinedValue && maybeParameters.isPresent()) {
            valueToResolve = ctx.getValueParameter(maybeParameters.get(), simpleDefinedValue).orElse(value);
        }

        return ctx.getCompiledValue(type, valueToResolve);
    }

    @Override
    public String getInitializerString(CompilerContext ctx, String typeName, Type type, Value value) {
        var resolvedValue = ctx.getValue(type, value);

        return JavaUtils.getInitializerString(ctx, typeName, resolvedValue);
    }

    protected void addImports(JavaClass clazz) {
    }

    @Override
    public void addDefaultField(CompilerContext ctx, JavaClass javaClass, String fieldName, String typeName,
            CompiledValue compiledValue) {
        var initializerString = getInitializerString(ctx, typeName, compiledValue.getCompiledType().getType(),
                compiledValue.getValue());
        var defaultField = CompilerUtils.getDefaultFieldName(fieldName);

        javaClass.addField(new JavaDefinedField(typeName, defaultField), false, false);
        javaClass.addInitializer(new JavaInitializer(String.format("\t\t%s = %s;", defaultField, initializerString)));

        addImports(javaClass);
    }

}
