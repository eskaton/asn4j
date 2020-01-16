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

package ch.eskaton.asn4j.compiler.il.builder;

import ch.eskaton.asn4j.compiler.il.Function;
import ch.eskaton.asn4j.compiler.il.ILType;
import ch.eskaton.asn4j.compiler.il.ILVisibility;
import ch.eskaton.asn4j.compiler.il.Module;
import ch.eskaton.asn4j.compiler.il.Parameter;
import ch.eskaton.asn4j.compiler.il.Statement;
import ch.eskaton.asn4j.runtime.utils.ToString;

import java.util.List;

public class FunctionBuilder implements Builder<Module>, HasStatements {

    private Module module;

    private Function function;

    public FunctionBuilder(Module module) {
        this.module = module;
        this.function = new Function();
    }

    public Module getModule() {
        return module;
    }

    public FunctionBuilder name(String name) {
        function.setName(name);

        return this;
    }

    public FunctionBuilder overriden(boolean overriden) {
        function.setOverriden(overriden);

        return this;
    }

    public FunctionBuilder visibility(ILVisibility visibility) {
        function.setVisibility(visibility);

        return this;
    }

    public FunctionBuilder returnType(ILType type) {
        function.setReturnType(type);

        return this;
    }

    public FunctionBuilder parameter(ILType type, String name) {
        function.addParameter(new Parameter(type, name));

        return this;
    }

    public FunctionBuilder parameter(Parameter parameter) {
        function.addParameter(parameter);

        return this;
    }

    public FunctionBuilder parameters(List<Parameter> parameters) {
        parameters.forEach(this::parameter);

        return this;
    }

    public StatementBuilder<FunctionBuilder> statements() {
        return new StatementBuilder(this);
    }

    @Override
    public void addStatement(Statement statement) {
        function.addStatement(statement);
    }

    @Override
    public Module build() {
        module.addFunction(function);

        return module;
    }

    @Override
    public String toString() {
        return ToString.get(this);
    }
}
