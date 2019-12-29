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

import ch.eskaton.asn4j.compiler.il.Declaration;
import ch.eskaton.asn4j.compiler.il.Foreach;
import ch.eskaton.asn4j.compiler.il.ILType;
import ch.eskaton.asn4j.compiler.il.Statement;
import ch.eskaton.asn4j.compiler.il.Variable;
import ch.eskaton.asn4j.runtime.utils.ToString;

import java.util.LinkedList;
import java.util.List;

public class ForeachBuilder<B extends Builder & HasStatements> implements Builder<B>, HasStatements {

    private B builder;

    private ILType type;

    private Variable variable;

    private Variable value;

    private List<Statement> statements = new LinkedList<>();


    public ForeachBuilder(B builder, ILType type, Variable variable, Variable value) {
        this.builder = builder;
        this.type = type;
        this.variable = variable;
        this.value = value;
    }

    public StatementBuilder<ForeachBuilder<B>> statements() {
        return new StatementBuilder<>(this);
    }

    @Override
    public void addStatement(Statement statement) {
        statements.add(statement);
    }

    @Override
    public B build() {
        builder.addStatement(new Foreach(new Declaration(type, variable), value, statements));

        return builder;
    }

    @Override
    public String toString() {
        return ToString.get(this);
    }

}
