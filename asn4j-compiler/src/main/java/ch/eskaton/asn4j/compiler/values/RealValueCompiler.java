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

package ch.eskaton.asn4j.compiler.values;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerUtils;
import ch.eskaton.asn4j.compiler.parameters.Parameters;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
import ch.eskaton.asn4j.parser.ast.values.AmbiguousValue;
import ch.eskaton.asn4j.parser.ast.values.CollectionValue;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.RealValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

public class RealValueCompiler extends AbstractValueCompiler<RealValue> {

    public RealValueCompiler() {
        super(TypeName.REAL, RealValue.class);
    }

    @Override
    public RealValue doCompile(CompilerContext ctx, CompiledType compiledType, Value value,
            Optional<Parameters> maybeParameters) {
        value = resolveValueFromObject(ctx, value);

        if (value instanceof RealValue) {
            return (RealValue) value;
        } else if (value instanceof IntegerValue integerValue) {
            return new RealValue(integerValue.getPosition(), BigDecimal.valueOf(integerValue.getValue().longValue()));
        } else if (value instanceof CollectionValue) {
            return resolveCollectionValue(ctx, value);
        } else if (value instanceof AmbiguousValue) {
            return CompilerUtils.resolveAmbiguousValue(value, RealValue.class);
        }

        throw invalidValueError(value);
    }

    private RealValue resolveCollectionValue(CompilerContext ctx, Value value) {
        BigInteger mantissa = null;
        BigInteger base = null;
        BigInteger exponent = null;

        for (var namedValue : ((CollectionValue) value).getValues()) {
            var compiledType = ctx.getCompiledBuiltinType(IntegerType.class);
            var resolvedValue = new IntegerValueCompiler()
                    .compile(ctx, compiledType, namedValue.getValue(), Optional.empty());

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
                    throw new ValueResolutionException("Unknown component in %s value: %s", TypeName.REAL,
                            namedValue.getName());
            }
        }

        if (mantissa == null || base == null || exponent == null) {
            throw new ValueResolutionException(
                    "Incomplete %s value: %s. It must contain 'mantissa', 'base' and 'exponent'",
                    TypeName.REAL, value);
        }

        if (base.intValue() != 2 && base.intValue() != 10) {
            throw new ValueResolutionException(
                    "Invalid base '%s' in %s value: %s. Only 2 and 10 are allowed",
                    base, TypeName.REAL, value);
        }

        if (mantissa.bitLength() > 63) {
            throw new ValueResolutionException(
                    "Mantissa in %s value is out of range: %s. It must be between -2^63 and 2^63",
                    TypeName.REAL, mantissa);
        }

        if (exponent.compareTo(BigInteger.valueOf(-999999999L)) < 0 ||
                exponent.compareTo(BigInteger.valueOf(999999999L)) > 0) {
            throw new ValueResolutionException(
                    "Exponent in %s value is out of range: %s. It must be between -999999999L and 999999999L",
                    TypeName.REAL, exponent);
        }

        return new RealValue(value.getPosition(), mantissa.longValue(), base.intValue(), exponent.intValue());
    }

}
