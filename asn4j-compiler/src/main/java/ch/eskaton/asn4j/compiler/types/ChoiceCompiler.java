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
import ch.eskaton.asn4j.compiler.Parameters;
import ch.eskaton.asn4j.compiler.results.CompiledChoiceType;
import ch.eskaton.asn4j.compiler.results.CompiledComponent;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.types.Choice;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChoiceCompiler implements NamedCompiler<Choice, CompiledType> {

    @Override
    public CompiledType compile(CompilerContext ctx, String name, Choice node, Optional<Parameters> maybeParameters) {
        var tags = CompilerUtils.getTagIds(ctx, node);
        var componentVerifiers = List.of(
                new NameUniquenessVerifier(TypeName.CHOICE),
                new TagUniquenessVerifier(TypeName.CHOICE),
                new UntaggedOpenTypeVerifier(TypeName.CHOICE));
        var compiledType = ctx.createCompiledType(CompiledChoiceType.class, node, name);
        var components = new ArrayList<CompiledComponent>();

        compiledType.setTags(tags);

        for (var namedType : node.getRootAlternatives()) {
            var compiledComponent = compileChoiceNamedType(ctx, namedType, maybeParameters);
            var component = compiledComponent.getCompiledType();

            component.setParent(compiledType);
            componentVerifiers.forEach(v -> v.verify(compiledComponent));
            components.add(compiledComponent);
        }

        compiledType.getComponents().addAll(components);

        CompilerUtils.compileComponentConstraints(ctx, compiledType, maybeParameters);

        ctx.compileConstraintAndModule(name, compiledType, maybeParameters).ifPresent(constraintAndModule -> {
            compiledType.setConstraintDefinition(constraintAndModule.get_1());
            compiledType.setModule(constraintAndModule.get_2());
        });

        return compiledType;
    }

    private CompiledComponent compileChoiceNamedType(CompilerContext ctx, NamedType namedType,
            Optional<Parameters> maybeParameters) {
        var name = namedType.getName();
        var compiledType = ctx.<NamedType, NamedTypeCompiler>getCompiler(NamedType.class).compile(ctx, namedType, maybeParameters);

        return new CompiledComponent(name, compiledType);
    }

}
