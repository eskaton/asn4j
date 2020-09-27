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
import ch.eskaton.asn4j.compiler.results.CompiledIntegerType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.NamedNumber;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Optional;

public class IntegerCompiler extends BuiltinTypeCompiler<IntegerType> {

    @Override
    public CompiledType compile(CompilerContext ctx, String name, IntegerType node,
            Optional<Parameters> maybeParameters) {
        var tags = CompilerUtils.getTagIds(ctx, node);
        var namedNumbers = node.getNamedNumbers();
        var uniquenessChecker = new IdentifierUniquenessChecker<>(name);
        var compiledNamedNumbers = new HashMap<String, Long>();

        if (namedNumbers != null && !namedNumbers.isEmpty()) {
            for (var namedNumber : namedNumbers) {
                var value = getValue(ctx, namedNumber);
                var id = namedNumber.getId();

                uniquenessChecker.add(id, value);

                compiledNamedNumbers.put(id, value);
            }
        }

        var compiledType = ctx.createCompiledType(CompiledIntegerType.class, node, name);

        compiledType.setNamedNumbers(compiledNamedNumbers);
        compiledType.setTags(tags);

        ctx.compileConstraintAndModule(name, compiledType).ifPresent(constraintAndModule -> {
            compiledType.setConstraintDefinition(constraintAndModule.get_1());
            compiledType.setModule(constraintAndModule.get_2());
        });

        return compiledType;
    }

    private long getValue(CompilerContext ctx, NamedNumber namedNumber) {
        BigInteger bigValue;

        if (namedNumber.getRef() != null) {
            var compiledValue = ctx.<IntegerValue>getCompiledValue(IntegerValue.class, namedNumber.getRef());

            bigValue = compiledValue.getValue().getValue();
        } else {
            bigValue = namedNumber.getValue().getNumber();
        }

        if (bigValue.bitLength() > 63) {
            throw new CompilerException("Named number '%s' too long: %s", namedNumber.getId(), bigValue);
        }

        return bigValue.longValue();
    }

}
