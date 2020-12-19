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

import ch.eskaton.asn4j.compiler.Compiler;
import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.parameters.Parameters;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.Choice;
import ch.eskaton.asn4j.parser.ast.types.EnumeratedType;
import ch.eskaton.asn4j.parser.ast.types.IRI;
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.asn4j.parser.ast.types.ObjectClassFieldType;
import ch.eskaton.asn4j.parser.ast.types.ObjectIdentifier;
import ch.eskaton.asn4j.parser.ast.types.RelativeIRI;
import ch.eskaton.asn4j.parser.ast.types.RelativeOID;
import ch.eskaton.asn4j.parser.ast.types.SequenceOfType;
import ch.eskaton.asn4j.parser.ast.types.SequenceType;
import ch.eskaton.asn4j.parser.ast.types.SetOfType;
import ch.eskaton.asn4j.parser.ast.types.SetType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;

import java.util.Optional;

public class NamedTypeCompiler implements Compiler<NamedType> {

    public CompiledType compile(CompilerContext ctx, NamedType namedType, Optional<Parameters> maybeParameters) {
        var name = namedType.getName();
        var type = namedType.getType();

        if (type instanceof TypeReference typeReference && maybeParameters.isPresent()) {
            type = ctx.getTypeParameter(maybeParameters.get(), typeReference).orElse(type);
        }

        return compile(ctx, type, name, maybeParameters);
    }

    public CompiledType compile(CompilerContext ctx, Type type, String name, Optional<Parameters> maybeParameters) {
        if (type instanceof EnumeratedType) {
            return compile(ctx, type, name, maybeParameters, ctx.isSubtypeNeeded(type));
        } else if (type instanceof IntegerType) {
            return compile(ctx, type, name, maybeParameters, ctx.isSubtypeNeeded(type));
        } else if (type instanceof BitString) {
            return compile(ctx, type, name, maybeParameters, ctx.isSubtypeNeeded(type));
        } else if (type instanceof SequenceType
                || type instanceof SequenceOfType
                || type instanceof SetType
                || type instanceof SetOfType
                || type instanceof Choice) {
            return compile(ctx, type, name, maybeParameters, true);
        } else if (type instanceof ObjectIdentifier
                || type instanceof RelativeOID
                || type instanceof IRI
                || type instanceof RelativeIRI) {
            return compile(ctx, type, name, maybeParameters, false);
        } else if (type instanceof ObjectClassFieldType objectClassFieldType) {
            var compiler = ctx.<ObjectClassFieldType, ObjectClassFieldTypeCompiler>getCompiler(ObjectClassFieldType.class);

            return compiler.compile(ctx, name, objectClassFieldType, maybeParameters);
        }

        return ctx.createCompiledType(type, ctx.getTypeName(type, name), true);
    }

    private CompiledType compile(CompilerContext ctx, Type type, String name, Optional<Parameters> maybeParameters,
            boolean newType) {
        if (newType && name != null) {
            var compiledType = compileType(ctx, type, ctx.getTypeName(type, name), maybeParameters);

            compiledType.setSubtype(true);

            return compiledType;
        }

        return ctx.createCompiledType(type, ctx.getTypeName(type, name), ctx.isBuiltin(type));
    }


    private CompiledType compileType(CompilerContext ctx, Type type, String typeName,
            Optional<Parameters> maybeParameters) {
        return ctx.<Type, TypeCompiler>getCompiler(Type.class).compile(ctx, typeName, type, maybeParameters);
    }

}
