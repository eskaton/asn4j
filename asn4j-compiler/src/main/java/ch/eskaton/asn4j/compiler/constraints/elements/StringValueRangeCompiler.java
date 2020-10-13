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
import ch.eskaton.asn4j.compiler.Parameters;
import ch.eskaton.asn4j.compiler.constraints.Bounds;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.StringRange;
import ch.eskaton.asn4j.compiler.constraints.ast.StringValueNode;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.compiler.types.formatters.TypeFormatter;
import ch.eskaton.asn4j.compiler.values.formatters.ValueFormatter;
import ch.eskaton.asn4j.parser.ast.EndpointNode;
import ch.eskaton.asn4j.parser.ast.RangeNode;
import ch.eskaton.asn4j.parser.ast.values.AbstractStringValue;
import ch.eskaton.asn4j.parser.ast.values.StringValue;

import java.util.Optional;

import static java.util.Collections.singletonList;

public class StringValueRangeCompiler implements ElementsCompiler<RangeNode> {

    protected final CompilerContext ctx;

    public StringValueRangeCompiler(CompilerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public Node compile(CompiledType compiledType, RangeNode elements, Optional<Bounds> bounds,
            Optional<Parameters> maybeParameters) {
        var lower = resolveEndpoint(compiledType, maybeParameters, elements.getLower(), 1);
        var upper = resolveEndpoint(compiledType, maybeParameters, elements.getUpper(), -1);

        return new StringValueNode(singletonList(new StringRange(lower, upper)));
    }

    private String resolveEndpoint(CompiledType compiledType, Optional<Parameters> maybeParameters,
            EndpointNode endpoint, int offset) {
        var type = compiledType.getType();
        var value = ctx.getCompiledValue(type, endpoint.getValue(), maybeParameters).getValue();

        if (!(value instanceof AbstractStringValue)) {
            var formattedValue = ValueFormatter.formatValue(value);
            var formattedType = TypeFormatter.formatType(ctx, type);

            throw new CompilerException(endpoint.getPosition(), "Unexpected value '%s', expected a value of type '%s'",
                    formattedValue, formattedType);
        }

        var stringValue = ((AbstractStringValue) value).getValue();

        if (stringValue.length() != 1) {
            var formattedValue = ValueFormatter.formatValue(value);

            throw new CompilerException(endpoint.getPosition(), "Invalid value in range: %s", formattedValue);
        }

        if (!endpoint.isInclusive()) {
            return String.valueOf(Character.toChars(stringValue.codePointAt(0) + offset));
        }

        return stringValue;
    }

}
