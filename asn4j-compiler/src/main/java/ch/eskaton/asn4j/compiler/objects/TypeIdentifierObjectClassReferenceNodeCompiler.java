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

package ch.eskaton.asn4j.compiler.objects;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.NamedCompiler;
import ch.eskaton.asn4j.compiler.parameters.Parameters;
import ch.eskaton.asn4j.compiler.results.CompiledObjectClass;
import ch.eskaton.asn4j.parser.Parser;
import ch.eskaton.asn4j.parser.ParserException;
import ch.eskaton.asn4j.parser.ast.ObjectClassNode;
import ch.eskaton.asn4j.parser.ast.TypeIdentifierObjectClassReferenceNode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

public class TypeIdentifierObjectClassReferenceNodeCompiler implements NamedCompiler<TypeIdentifierObjectClassReferenceNode, CompiledObjectClass> {

    private static final String TYPE_IDENTIFIER_SOURCE = """
              CLASS {
                  &id OBJECT IDENTIFIER UNIQUE,
                  &Type
              }
              WITH SYNTAX {&Type IDENTIFIED BY &id}
            """;

    private static final ObjectClassNode OBJECT_CLASS = parseObjectClass();

    @Override
    public CompiledObjectClass compile(CompilerContext ctx, String name, TypeIdentifierObjectClassReferenceNode node,
            Optional<Parameters> maybeParameters) {
        var compiler = ctx.<ObjectClassNode, ObjectClassNodeCompiler>getCompiler(ObjectClassNode.class);

        return compiler.compile(ctx, name, OBJECT_CLASS, maybeParameters);
    }

    private static ObjectClassNode parseObjectClass() {
        try {
            var is = new ByteArrayInputStream(TYPE_IDENTIFIER_SOURCE.getBytes());
            var parser = new Parser(is).new ObjectClassParser();

            return parser.parse();
        } catch (IOException | ParserException e) {
            throw new CompilerException("Failed to compile TYPE-IDENTIFIER", e);
        }
    }

}
