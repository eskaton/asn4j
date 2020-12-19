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
import ch.eskaton.asn4j.compiler.parameters.Parameters;
import ch.eskaton.asn4j.compiler.constraints.ast.EnumeratedValueNode;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.compiler.types.formatters.TypeFormatter;
import ch.eskaton.asn4j.compiler.values.formatters.ValueFormatter;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.values.EnumeratedValue;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.util.Optional;
import java.util.Set;

public class EnumeratedTypeSingleValueCompiler extends SingleValueCompiler<EnumeratedValue, EnumeratedValueNode> {

    public EnumeratedTypeSingleValueCompiler(CompilerContext ctx, TypeName typeName) {
        super(ctx, EnumeratedValue.class, EnumeratedValueNode.class, typeName, Set.class);
    }

    @Override
    protected Integer resolveValue(CompiledType compiledType, SingleValueConstraint elements, Parameters parameters) {
        var value = elements.getValue();
        var type = compiledType.getType();
        var compiledValue = ctx.getCompiledValue(type, value, Optional.ofNullable(parameters));

        if (compiledValue.getValue() instanceof EnumeratedValue enumeratedValue) {
            return enumeratedValue.getValue();
        } else {
            var position = value.getPosition();
            var formattedType = TypeFormatter.formatType(ctx, type);
            var formattedValue = ValueFormatter.formatValue(value);

            throw new CompilerException(position, "Failed to resolve value '%s' of type '%s'", formattedValue,
                    formattedType);
        }
    }

}
