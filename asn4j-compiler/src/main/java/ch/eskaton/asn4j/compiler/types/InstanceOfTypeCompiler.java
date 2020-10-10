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
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.compiler.NamedCompiler;
import ch.eskaton.asn4j.compiler.Parameters;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.Parser;
import ch.eskaton.asn4j.parser.ParserException;
import ch.eskaton.asn4j.parser.ast.ObjectClassReference;
import ch.eskaton.asn4j.parser.ast.types.InstanceOfType;
import ch.eskaton.asn4j.parser.ast.types.SequenceType;
import ch.eskaton.asn4j.runtime.Clazz;
import ch.eskaton.asn4j.runtime.TagId;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class InstanceOfTypeCompiler implements NamedCompiler<InstanceOfType, CompiledType> {

    private static final String INSTANCE_OF_SOURCE = """
            SEQUENCE {
              type-id      %s.&id,
              value    [0] %s.&Type
            }
            """;


    @Override
    public CompiledType compile(CompilerContext ctx, String name, InstanceOfType node,
            Optional<Parameters> maybeParameters) {
        var sequence = parseSequence(node.getObjectClass());
        var compiler = ctx.<SequenceType, SequenceCompiler>getCompiler(SequenceType.class);
        var compiledType = compiler.compile(ctx, name, sequence, maybeParameters);

        compiledType.setTags(List.of(new TagId(Clazz.UNIVERSAL, 8)));

        return compiledType;
    }

    private SequenceType parseSequence(ObjectClassReference objectClassReference) {
        try {
            var reference = objectClassReference.getReference();
            var source = INSTANCE_OF_SOURCE.formatted(reference, reference);
            var is = new ByteArrayInputStream(source.getBytes());
            var parser = new Parser(is).new BuiltinTypeParserAux();

            var node = parser.parse();

            if (node instanceof SequenceType) {
                return (SequenceType) node;
            }

            throw new IllegalCompilerStateException("Expected a SEQUENCE");
        } catch (IOException | ParserException e) {
            throw new CompilerException(objectClassReference.getPosition(), "Failed to compile INSTANCE OF type", e);
        }
    }

}
