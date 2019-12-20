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

package ch.eskaton.asn4j.compiler.il;

import ch.eskaton.asn4j.runtime.utils.ToString;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static ch.eskaton.asn4j.compiler.il.ILVisibility.PRIVATE;

public class Function {

    private String name;

    private boolean overriden;

    private Optional<ILVisibility> visibility = Optional.empty();

    private ILType returnType;

    private List<Statement> statements = new LinkedList<>();

    private List<Parameter> parameters = new LinkedList<>();

    public Function() {
    }

    public Function(String name, List<Statement> statements) {
        this.name = name;
        this.statements = statements;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setOverriden(boolean overriden) {
        this.overriden = overriden;
    }

    public boolean isOverriden() {
        return overriden;
    }

    public void setVisibility(ILVisibility visibility) {
        this.visibility = Optional.ofNullable(visibility);
    }

    public ILVisibility getVisibility() {
        return visibility.orElse(PRIVATE);
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public void addStatement(Statement statement) {
        statements.add(statement);
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void setReturnType(ILType returnType) {
        this.returnType = returnType;
    }

    public ILType getReturnType() {
        return returnType;
    }

    public void addParameter(Parameter parameter) {
        parameters.add(parameter);
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return ToString.get(this);
    }
}
