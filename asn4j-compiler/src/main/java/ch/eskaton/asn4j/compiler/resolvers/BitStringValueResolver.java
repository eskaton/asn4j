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
import ch.eskaton.asn4j.parser.ast.NamedBitNode;
import ch.eskaton.asn4j.parser.ast.Node;
import ch.eskaton.asn4j.parser.ast.ValueOrObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.values.AbstractBaseXStringValue;
import ch.eskaton.asn4j.parser.ast.values.BitStringValue;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Value;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.eskaton.asn4j.compiler.CompilerUtils.getTypeName;
import static ch.eskaton.asn4j.compiler.CompilerUtils.resolveAmbiguousValue;

public class BitStringValueResolver extends AbstractValueResolver<BitStringValue> {

    public BitStringValueResolver(CompilerContext ctx) {
        super(ctx);
    }

    @Override
    protected BitStringValue resolve(ValueOrObjectAssignmentNode<?, ?> valueAssignment) throws CompilerException {
        Node type = valueAssignment.getType();
        Node value = valueAssignment.getValue();

        if (type instanceof BitString) {
            if (value instanceof AbstractBaseXStringValue) {
                return ((AbstractBaseXStringValue) value).toBitString();
            } else if (value instanceof BitStringValue) {
                return (BitStringValue) value;
            }

            throw new CompilerException("BIT STRING value expected");
        } else if (type instanceof TypeReference) {
            BitStringValue bitStringValue = resolveAmbiguousValue(value, BitStringValue.class);

            if (bitStringValue != null) {
                return resolve((Type) type, bitStringValue);
            }
        }

        throw new CompilerException("Failed to resolve a BIT STRING value");
    }

    public BitStringValue resolve(Type type, BitStringValue value) {
        Type base = ctx.getBase(type);

        if (base instanceof BitString) {
            BitString bitString = (BitString) base;
            Map<String, BigInteger> namedBits = getNamedBits(bitString);

            return resolveValue(getTypeName(type), namedBits, value);
        }

        throw new CompilerException("Failed to resolve a BIT STRING value");
    }

    @Override
    public BitStringValue resolveGeneric(Type type, Value value) throws CompilerException {
        BitStringValue bitStringValue = null;

        if (value instanceof AbstractBaseXStringValue) {
            bitStringValue = ((AbstractBaseXStringValue) value).toBitString();
        } else {
            if (resolveAmbiguousValue(value, SimpleDefinedValue.class) != null) {
                bitStringValue = ctx.resolveValue(BitStringValue.class,
                        resolveAmbiguousValue(value, SimpleDefinedValue.class));
            } else if (resolveAmbiguousValue(value, BitStringValue.class) != null) {
                bitStringValue = ctx.resolveValue(BitStringValue.class, type,
                        resolveAmbiguousValue(value, BitStringValue.class));
            }
        }

        return bitStringValue;
    }

    private BitStringValue resolveValue(String typeName, Map<String, BigInteger> namedBits,
            BitStringValue bitStringValue) {
        List<BigInteger> bits = new ArrayList<>(bitStringValue.getNamedValues().size());

        for (String namedValue : bitStringValue.getNamedValues()) {
            if (!namedBits.containsKey(namedValue)) {
                throw new CompilerException("%s has no component %s", typeName, namedValue);
            }

            bits.add(namedBits.get(namedValue));
        }

        bits.sort(Comparator.reverseOrder());
        long length = bits.get(0).intValue() / 8 + 1;

        byte[] byteValue = new byte[(int) length];

        for (BigInteger bit : bits) {
            int pos = bit.intValue() / 8;
            byteValue[pos] = byteValue[pos] |= 1 << 7 - (bit.intValue() % 8);
        }

        bitStringValue.setByteValue(byteValue);

        return bitStringValue;
    }

    private Map<String, BigInteger> getNamedBits(BitString bitString) {
        Map<String, BigInteger> namedBits = new HashMap<>();

        if (bitString.getNamedBits() != null) {
            for (NamedBitNode namedBit : bitString.getNamedBits()) {
                String id = namedBit.getId();
                BigInteger intValue;

                if (namedBit.getRef() != null) {
                    intValue = ctx.resolveValue(BigInteger.class, namedBit.getRef());
                } else {
                    intValue = BigInteger.valueOf(namedBit.getNum());
                }

                namedBits.put(id, intValue);
            }
        }

        return namedBits;
    }

}
