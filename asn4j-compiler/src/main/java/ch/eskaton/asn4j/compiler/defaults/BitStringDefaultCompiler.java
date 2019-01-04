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

package ch.eskaton.asn4j.compiler.defaults;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.java.JavaClass;
import ch.eskaton.asn4j.compiler.java.JavaInitializer;
import ch.eskaton.asn4j.compiler.resolvers.BitStringValueResolver;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.AbstractBaseXStringValue;
import ch.eskaton.asn4j.parser.ast.values.BitStringValue;
import ch.eskaton.asn4j.parser.ast.values.EmptyValue;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Value;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ch.eskaton.asn4j.compiler.CompilerUtils.resolveAmbiguousValue;

public class BitStringDefaultCompiler implements DefaultCompiler {

    public void compileDefault(CompilerContext ctx, JavaClass clazz, String field, String typeName, Type type,
            Value value) throws CompilerException {
        byte[] bytes;

        if (value instanceof AbstractBaseXStringValue) {
            bytes = ((AbstractBaseXStringValue) value).getBytes();
        } else if (value instanceof EmptyValue) {
            bytes = new byte[0];
        } else {
            BitStringValue bitStringValue = null;

            if (resolveAmbiguousValue(value, SimpleDefinedValue.class) != null) {
                value = resolveAmbiguousValue(value, SimpleDefinedValue.class);
                bitStringValue = ctx.resolveValue(BitStringValue.class, (SimpleDefinedValue) value);
            } else if (resolveAmbiguousValue(value, BitStringValue.class) != null) {
                value = resolveAmbiguousValue(value, BitStringValue.class);
                bitStringValue = new BitStringValueResolver(ctx).resolve(type, (BitStringValue) value);
            }

            bytes = bitStringValue.getByteValue();
        }

        String bytesStr = IntStream.range(0, bytes.length).boxed().map(
                i -> String.format("(byte) 0x%02x", bytes[i])).collect(Collectors.joining(", "));

        String strValue = "new byte[] { " + bytesStr + " }";

        String defaultField = addDefaultField(clazz, typeName, field);

        clazz.addInitializer(new JavaInitializer("\t\t" + defaultField + " = new " + typeName +
                "();\n\t\t" + defaultField + ".setValue(" + strValue + ");"));
    }

}
