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
import ch.eskaton.asn4j.compiler.Parameters;
import ch.eskaton.asn4j.compiler.results.CompiledBuiltinType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.compiler.values.formatters.ValueFormatter;
import ch.eskaton.asn4j.parser.ast.NamedBitNode;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.values.AbstractBaseXStringValue;
import ch.eskaton.asn4j.parser.ast.values.BitStringValue;
import ch.eskaton.asn4j.parser.ast.values.EmptyValue;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static ch.eskaton.asn4j.compiler.CompilerUtils.resolveAmbiguousValue;

public class BitStringValueCompiler extends AbstractValueCompiler<BitStringValue> {

    public BitStringValueCompiler() {
        super(TypeName.BIT_STRING, BitStringValue.class);
    }

    @Override
    public BitStringValue doCompile(CompilerContext ctx, CompiledType compiledType, Value value,
            Optional<Parameters> maybeParameters) {
        value = resolveValueFromObject(ctx, value);

        if (value instanceof AbstractBaseXStringValue) {
            return ((AbstractBaseXStringValue) value).toBitString();
        } else if (value instanceof EmptyValue) {
            return new BitStringValue(value.getPosition(), new byte[] {}, 0);
        }

        var resolvedValue = resolveAmbiguousValue(value, BitStringValue.class);

        if (resolvedValue != null) {
            return resolve(ctx, compiledType, resolvedValue);
        }

        throw invalidValueError(value);
    }

    public BitStringValue resolve(CompilerContext ctx, CompiledType compiledType, BitStringValue value) {
        var type = compiledType.getType();
        var typeName = getTypeName(ctx, compiledType);

        if (type instanceof BitString) {
            var bitString = (BitString) type;
            var namedBits = getNamedBits(ctx, bitString);

            return resolveValue(typeName, namedBits, value);
        }

        throw new ValueResolutionException(value.getPosition(), "Failed to resolve a %s value", TypeName.BIT_STRING,
                ValueFormatter.formatValue(value));
    }

    private String getTypeName(CompilerContext ctx, CompiledType compiledType) {
        if (compiledType instanceof CompiledBuiltinType) {
            return ctx.getTypeName(compiledType.getType());
        }

        return compiledType.getName();
    }

    private BitStringValue resolveValue(String typeName, Map<String, BigInteger> namedBits,
            BitStringValue bitStringValue) {
        var namedValues = bitStringValue.getNamedValues();

        if (namedValues.isEmpty()) {
            return bitStringValue;
        }

        var bits = new ArrayList<BigInteger>(namedValues.size());

        for (var namedValue : namedValues) {
            if (!namedBits.containsKey(namedValue)) {
                throw new ValueResolutionException(bitStringValue.getPosition(), "%s has no named bit '%s'",
                        typeName, namedValue);
            }

            bits.add(namedBits.get(namedValue));
        }

        bits.sort(Comparator.reverseOrder());

        var length = bits.get(0).intValue() / 8 + 1;
        var byteValue = new byte[length];
        var unusedBits = length * 8;

        for (var bit : bits) {
            var bitValue = bit.intValue();
            var pos = bitValue / 8;

            unusedBits = Math.min(unusedBits, length * 8 - (bitValue + 1));
            byteValue[pos] = byteValue[pos] |= 1 << 7 - (bitValue % 8);
        }

        bitStringValue.setByteValue(byteValue, unusedBits);

        return bitStringValue;
    }

    private Map<String, BigInteger> getNamedBits(CompilerContext ctx, BitString bitString) {
        var namedBits = new HashMap<String, BigInteger>();

        if (bitString.getNamedBits() != null) {
            for (var namedBit : bitString.getNamedBits()) {
                var id = namedBit.getId();
                var bit = getBit(ctx, namedBit);

                namedBits.put(id, bit);
            }
        }

        return namedBits;
    }

    private BigInteger getBit(CompilerContext ctx, NamedBitNode namedBit) {
        if (namedBit.getRef() != null) {
            var compiledValue = ctx.getCompiledValue(namedBit.getRef());

            return ((IntegerValue) compiledValue.getValue()).getValue();
        } else {
            return BigInteger.valueOf(namedBit.getNum());
        }
    }

}
