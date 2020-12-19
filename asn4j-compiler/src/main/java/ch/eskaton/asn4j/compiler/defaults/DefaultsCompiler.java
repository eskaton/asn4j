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
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.parameters.Parameters;
import ch.eskaton.asn4j.compiler.java.objs.JavaClass;
import ch.eskaton.asn4j.compiler.results.CompiledValue;
import ch.eskaton.asn4j.compiler.types.formatters.TypeFormatter;
import ch.eskaton.asn4j.compiler.values.formatters.ValueFormatter;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.Value;

import java.util.Optional;

public class DefaultsCompiler {

    private CompilerContext ctx;

    @SuppressWarnings("serial")
    public DefaultsCompiler(CompilerContext ctx) {
        this.ctx = ctx;
    }

    public <V extends Value> CompiledValue<Value> compileDefault(Type type, V value,
            Optional<Parameters> maybeParameters) {
        var compiler = getDefaultCompiler(type);
        try {
            return compiler.compileDefault(ctx, type, value, maybeParameters);
        } catch (CompilerException e) {
            var formattedType = TypeFormatter.formatType(ctx, type);
            var formattedValue = ValueFormatter.formatValue(value);

            throw new CompilerException(value.getPosition(), "Failed to compile default value '%s' for type %s: %s",
                    e, formattedValue, formattedType, e.getMessage());
        }
    }

    private AbstractDefaultCompiler<Value> getDefaultCompiler(Type type) {
        var compiledBaseType = ctx.getCompiledBaseType(type);

        return ctx.getDefaultCompiler(compiledBaseType.getType().getClass());
    }

    public void addDefaultField(CompilerContext ctx, JavaClass javaClass, String field, String typeName,
            CompiledValue compiledValue) {
        var compiledBaseType = ctx.findCompiledBaseType(compiledValue.getCompiledType());
        var compiler = ctx.getDefaultCompiler(compiledBaseType.getType().getClass());

        compiler.addDefaultField(ctx, javaClass, field, typeName, compiledValue);
    }

}
