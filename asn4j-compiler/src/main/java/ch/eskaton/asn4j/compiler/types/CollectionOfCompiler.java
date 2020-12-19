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
import ch.eskaton.asn4j.compiler.CompilerUtils;
import ch.eskaton.asn4j.compiler.NamedCompiler;
import ch.eskaton.asn4j.compiler.parameters.Parameters;
import ch.eskaton.asn4j.compiler.results.AnonymousCompiledCollectionOfType;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionOfType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.types.CollectionOfType;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;

import java.util.LinkedList;
import java.util.Optional;

public abstract class CollectionOfCompiler<T extends CollectionOfType> implements NamedCompiler<T, CompiledType> {

    @Override
    public CompiledType compile(CompilerContext ctx, String name, T node, Optional<Parameters> maybeParameters) {
        var tags = CompilerUtils.getTagIds(ctx, node);
        var compiledType = ctx.createCompiledType(CompiledCollectionOfType.class, node, name);

        compiledType.setTags(tags);
        compileContentType(ctx, compiledType, node, name, maybeParameters);

        ctx.compileConstraintAndModule(name, compiledType, maybeParameters).ifPresent(constraintAndModule -> {
            compiledType.setConstraintDefinition(constraintAndModule.get_1());
            compiledType.setModule(constraintAndModule.get_2());
        });

        return compiledType;
    }

    private void compileContentType(CompilerContext ctx, CompiledCollectionOfType compiledCollectionOfType,
            T node, String name, Optional<Parameters> maybeParameters) {
        var type = node.getType();
        var types = new LinkedList<Type>();

        while (type instanceof CollectionOfType) {
            types.push(type);
            type = ((CollectionOfType) type).getType();
        }

        var contentTypeName = name + "Content";

        if (type instanceof NamedType namedType) {
            contentTypeName = namedType.getName();
            compiledCollectionOfType.setContentTypeName(contentTypeName);
            type = namedType.getType();
        }

        if (maybeParameters.isPresent() && type instanceof TypeReference typeReference) {
            type = ctx.getTypeParameter(maybeParameters.get(), typeReference).orElse(type);
        }

        var compiledType = ctx.isSubtypeNeeded(type) ?
                ctx.<NamedType, NamedTypeCompiler>getCompiler(NamedType.class).compile(ctx, type, contentTypeName, maybeParameters) :
                getCompiledType(ctx, type, maybeParameters);

        while (!types.isEmpty()) {
            compiledType = new AnonymousCompiledCollectionOfType(types.pop(), compiledType);
        }

        compiledType.setParent(compiledCollectionOfType);
        compiledCollectionOfType.setContentType(compiledType);
    }

    private CompiledType getCompiledType(CompilerContext ctx, Type type, Optional<Parameters> maybeParameters) {
        if (type instanceof TypeReference typeReference) {
            return CompilerUtils.compileTypeReference(ctx, typeReference, maybeParameters);
        }

        return ctx.getCompiledType(type);
    }

}
