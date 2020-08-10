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
import ch.eskaton.asn4j.compiler.results.CompiledEnumeratedType;
import ch.eskaton.asn4j.compiler.utils.ValueFormatter;
import ch.eskaton.asn4j.parser.ast.ValueOrObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.types.EnumeratedType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.values.EnumeratedValue;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.types.TypeName;
import ch.eskaton.commons.collections.Tuple2;

import java.util.Optional;

import static ch.eskaton.asn4j.compiler.CompilerUtils.resolveAmbiguousValue;

public class EnumeratedValueResolver extends AbstractValueResolver<EnumeratedValue> {

    public EnumeratedValueResolver(CompilerContext ctx) {
        super(ctx);
    }

    @Override
    public EnumeratedValue resolveGeneric(Type type, Value value) {
        if ((type instanceof TypeReference || type instanceof EnumeratedType) && value instanceof SimpleDefinedValue) {
            CompiledEnumeratedType compiledType = (CompiledEnumeratedType) ctx.getCompiledType(type);

            if (compiledType != null) {
                Optional<ValueOrObjectAssignmentNode> assignmentNode =
                        ctx.tryResolveAllValueReferences((SimpleDefinedValue) value);

                if (assignmentNode.isPresent()) {
                    value = resolveAmbiguousValue(assignmentNode.get().getValue(), SimpleDefinedValue.class);
                }

                String ref = ((SimpleDefinedValue) value).getValue();

                Optional<Tuple2<String, Integer>> element = compiledType.getElementById(ref);

                if (element.isPresent()) {
                    return new EnumeratedValue(value.getPosition(), ref, element.get().get_2());
                }
            }
        } else if (value instanceof EnumeratedValue enumeratedValue) {
            return enumeratedValue;
        }

        throw new CompilerException(value.getPosition(), "Failed to resolve an %s value: %s", TypeName.ENUMERATED,
                ValueFormatter.formatValue(value));
    }

}
