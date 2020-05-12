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

package ch.eskaton.asn4j.compiler.resolvers;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.ValueOrObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.NamedNumber;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.util.Optional;

public class IntegerValueResolver extends AbstractValueResolver<IntegerValue> {

    public IntegerValueResolver(CompilerContext ctx) {
        super(ctx);
    }

    @Override
    public IntegerValue resolveGeneric(Type type, Value value) {
        CompiledType compiledType = ctx.getCompiledType(type);

        if (value instanceof IntegerValue) {
            return (IntegerValue) value;
        }

        if (compiledType != null && compiledType.getType() instanceof IntegerType) {
            if (value instanceof IntegerValue) {
                return (IntegerValue) value;
            }

            Optional<ValueOrObjectAssignmentNode> assignmentNode =
                    ctx.tryResolveAllReferences((SimpleDefinedValue) value);

            if (assignmentNode.isPresent()) {
                value = (Value) assignmentNode.get().getValue();

                if (value instanceof IntegerValue) {
                    return (IntegerValue) value;
                }
            }

            return resolveNamedNumber((SimpleDefinedValue) value, compiledType);
        }

        throw new CompilerException("Failed to resolve an %s value", TypeName.INTEGER);
    }

    private IntegerValue resolveNamedNumber(SimpleDefinedValue value, CompiledType compiledType) {
        String reference = value.getValue();
        NamedNumber namedNumber = ((IntegerType) compiledType.getType()).getNamedNumber(reference);

        if (namedNumber.getValue() != null) {
            return new IntegerValue(namedNumber.getPosition(), namedNumber.getValue().getNumber());
        } else {
            return ctx.resolveValue(IntegerValue.class, namedNumber.getRef());
        }
    }

}

