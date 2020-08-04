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
import ch.eskaton.asn4j.compiler.CompilerUtils;
import ch.eskaton.asn4j.compiler.utils.ValueFormatter;
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.AmbiguousValue;
import ch.eskaton.asn4j.parser.ast.values.CollectionValue;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.RealValue;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.math.BigInteger;

import static ch.eskaton.asn4j.parser.NoPosition.NO_POSITION;

public class RealValueResolver extends AbstractValueResolver<RealValue> {

    public RealValueResolver(CompilerContext ctx) {
        super(ctx);
    }


    @Override
    public RealValue resolveGeneric(Type type, Value value) {
        if (value instanceof SimpleDefinedValue) {
            return ctx.tryResolveAllValueReferences((SimpleDefinedValue) value).map(this::resolve).orElse(null);
        } else if (value instanceof RealValue) {
            return (RealValue) value;
        } else if (value instanceof CollectionValue) {
            return resolveCollectionValue(value);
        } else if (value instanceof AmbiguousValue) {
            return CompilerUtils.resolveAmbiguousValue(value, RealValue.class);
        }

        throw new CompilerException(value.getPosition(), "Failed to resolve a %s value: %s", TypeName.REAL,
                ValueFormatter.formatValue(value));
    }

    private RealValue resolveCollectionValue(Value value) {
        BigInteger mantissa = null;
        BigInteger base = null;
        BigInteger exponent = null;

        for (var namedValue : ((CollectionValue) value).getValues()) {
            var resolvedValue = ctx.resolveGenericValue(IntegerValue.class, new IntegerType(NO_POSITION),
                    namedValue.getValue());

            switch (namedValue.getName()) {
                case "mantissa":
                    mantissa = resolvedValue.getValue();
                    break;
                case "base":
                    base = resolvedValue.getValue();
                    break;
                case "exponent":
                    exponent = resolvedValue.getValue();
                    break;
                default:
                    throw new CompilerException("Unknown component in %s value: %s", TypeName.REAL,
                            namedValue.getName());
            }
        }

        if (mantissa == null || base == null || exponent == null) {
            throw new CompilerException("Incomplete %s value: %s. It must contain 'mantissa', 'base' and 'exponent'",
                    TypeName.REAL, value);
        }

        if (base.intValue() != 2 && base.intValue() != 10) {
            throw new CompilerException("Invalid base '%s' in %s value: %s. Only 2 and 10 are allowed",
                    base, TypeName.REAL, value);
        }

        if (mantissa.bitLength() > 63) {
            throw new CompilerException("Mantissa in %s value is out of range: %s. It must be between -2^63 and 2^63",
                    TypeName.REAL, mantissa);
        }

        if (exponent.compareTo(BigInteger.valueOf(-999999999L)) < 0 ||
                exponent.compareTo(BigInteger.valueOf(999999999L)) > 0) {
            throw new CompilerException("Exponent in %s value is out of range: %s. It must be between -999999999L and 999999999L",
                    TypeName.REAL, exponent);
        }

        return new RealValue(value.getPosition(), mantissa.longValue(), base.intValue(), exponent.intValue());
    }

}
