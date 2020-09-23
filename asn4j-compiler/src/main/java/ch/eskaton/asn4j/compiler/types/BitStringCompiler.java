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

package ch.eskaton.asn4j.compiler.types;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.CompilerUtils;
import ch.eskaton.asn4j.compiler.Parameters;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.NamedBitNode;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;
import ch.eskaton.commons.collections.Tuple2;

import java.util.Optional;

import static ch.eskaton.asn4j.compiler.java.objs.JavaVisibility.PUBLIC;

public class BitStringCompiler extends BuiltinTypeCompiler<BitString> {

    @Override
    public CompiledType compile(CompilerContext ctx, String name, BitString node,
            Optional<Parameters> maybeParameters) {
        var tags = CompilerUtils.getTagIds(ctx, node);
        var javaClass = ctx.createClass(name, node, tags);
        var namedBits = node.getNamedBits();
        var uniquenessChecker = new IdentifierUniquenessChecker<>(name);
        long msb = 0;

        javaClass.setParent(ch.eskaton.asn4j.runtime.types.ASN1NamedBitString.class.getSimpleName());

        if (namedBits != null && !namedBits.isEmpty()) {
            for (NamedBitNode namedBit : namedBits) {
                var fieldName = CompilerUtils.formatConstant(namedBit.getId());
                long value;

                if (namedBit.getRef() != null) {
                    var compiledValue = ctx.<IntegerValue>getCompiledValue(IntegerValue.class, namedBit.getRef());
                    var bigValue = compiledValue.getValue().getValue();

                    if (bigValue.bitLength() > 63) {
                        throw new CompilerException("Named bit '%s' too long: %s", fieldName, bigValue.toString());
                    }

                    value = bigValue.longValue();
                } else {
                    value = namedBit.getNum();
                }

                uniquenessChecker.add(namedBit.getId(), value);

                msb = value > msb ? value : msb;

                javaClass.field().modifier(PUBLIC).asStatic().asFinal().type(int.class).name(fieldName)
                        .initializer(String.valueOf(value)).build();
            }
        }

        javaClass.method().modifier(PUBLIC).name(name).build();

        var compiledType = ctx.createCompiledType(node, name);

        compiledType.setTags(tags);

        var constraintDef = ctx.compileConstraintAndModule(name, compiledType);

        compiledType.setConstraintDefinition(constraintDef.map(Tuple2::get_1).orElse(null));

        if (constraintDef.isPresent()) {
            javaClass.addModule(ctx, constraintDef.get().get_2());
            javaClass.addImport(ConstraintViolatedException.class);
        }

        ctx.finishClass(false);

        return compiledType;
    }

}
