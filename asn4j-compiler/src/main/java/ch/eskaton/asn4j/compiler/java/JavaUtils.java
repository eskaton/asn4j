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

package ch.eskaton.asn4j.compiler.java;

import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.parser.ast.values.BitStringValue;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.Value;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JavaUtils {

    private JavaUtils() {
    }

    public static String getInitializerString(Value value) {
        return typeSwitch(value,
                typeCase(BitStringValue.class, JavaUtils::getBitStringInitializerString),
                typeCase(IntegerValue.class, JavaUtils::getIntegerInitializerString)
        );
    }

    private static String getBitStringInitializerString(BitStringValue value) {
        byte[] bytes = value.getByteValue();
        String bytesStr = IntStream.range(0, bytes.length).boxed().map(
                i -> String.format("(byte) 0x%02x", bytes[i])).collect(Collectors.joining(", "));

        return "new ASN1BitString(new byte[] { " + bytesStr + " }, " + value.getUnusedBits() + ")";
    }

    private static String getIntegerInitializerString(IntegerValue value) {
        return "ASN1Integer.valueOf(" + value.getValue().longValue() + "L)";
    }

    public static String typeSwitch(Value value, Function<Object, Optional<String>>... functions) {
        return Arrays.stream(functions)
                .map(f -> f.apply(value))
                .filter(v -> v.isPresent())
                .findFirst()
                .orElseThrow(() -> new CompilerException("Failed to get initializer string for type %s",
                        value.getClass())).get();
    }

    private static <T extends Value> Function<Object, Optional<String>> typeCase(Class<T> cls, Function<T, String> fun) {
        return obj -> cls.isInstance(obj) ? Optional.of(fun.apply(cls.cast(obj))) : Optional.empty();
    }

}