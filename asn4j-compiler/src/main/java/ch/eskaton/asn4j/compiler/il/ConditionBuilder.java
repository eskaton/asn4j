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

public class ConditionBuilder implements Builder<ConditionsBuilder>, HasStatements {

    private ConditionsBuilder builder;

    private Condition condition;

    public ConditionBuilder(ConditionsBuilder builder, BooleanExpression expression) {
        this.builder = builder;
        this.condition = new Condition();

        if (expression != null) {
            condition.setExpression(expression);
        }
    }

    public ConditionBuilder(ConditionsBuilder builder) {
        this(builder, null);
    }

    public ConditionBuilder check(BooleanExpression expression) {
        condition.setExpression(expression);

        return this;
    }

    public StatementBuilder<ConditionBuilder> statements() {
        return new StatementBuilder<>(this);
    }

    @Override
    public ConditionBuilder addStatement(Statement statement) {
        condition.getStatements().add(statement);

        return this;
    }

    public ConditionsBuilder build() {
        builder.conditions.add(condition);

        return builder;
    }

    @Override
    public String toString() {
        return ToString.get(this);
    }

}
