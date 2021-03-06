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
import ch.eskaton.asn4j.compiler.NamedCompiler;
import ch.eskaton.asn4j.compiler.parameters.Parameters;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionComponent;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.types.Collection;
import ch.eskaton.asn4j.parser.ast.types.ComponentType;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractCollectionCompiler<T extends Collection> implements NamedCompiler<T, CompiledType> {

    private final TypeName typeName;

    private final List<Function<TypeName, ComponentVerifier<CompiledCollectionComponent>>> componentVerifierSuppliers;

    protected AbstractCollectionCompiler(TypeName typeName,
            Function<TypeName, ComponentVerifier<CompiledCollectionComponent>>... componentVerifierSupplier) {
        this.typeName = typeName;
        this.componentVerifierSuppliers = new ArrayList<>(Arrays.asList(componentVerifierSupplier));
        this.componentVerifierSuppliers.add(NameUniquenessVerifier::new);
    }

    public CompiledType compile(CompilerContext ctx, String name, T node, Optional<Parameters> maybeParameters) {
        var tags = CompilerUtils.getTagIds(ctx, node);
        var componentVerifiers = componentVerifierSuppliers.stream()
                .map(s -> s.apply(typeName))
                .collect(Collectors.toList());
        var compiledType = ctx.createCompiledType(CompiledCollectionType.class, node, name);

        compiledType.setTags(tags);

        compileComponents(ctx, name, maybeParameters, componentVerifiers, compiledType,
                node.getAllRootComponents(), true);
        compileComponents(ctx, name, maybeParameters, componentVerifiers, compiledType,
                node.getExtensionAdditionComponents(), false);

        CompilerUtils.compileComponentConstraints(ctx, compiledType, maybeParameters);

        ctx.compileConstraintAndModule(name, compiledType, maybeParameters).ifPresent(constraintAndModule -> {
            compiledType.setConstraintDefinition(constraintAndModule.get_1());
            compiledType.setModule(constraintAndModule.get_2());
        });

        return compiledType;
    }

    private void compileComponents(CompilerContext ctx, String name, Optional<Parameters> maybeParameters,
            List<ComponentVerifier<CompiledCollectionComponent>> componentVerifiers,
            CompiledCollectionType compiledType, List<ComponentType> components, boolean isRoot) {
        for (var component : components) {
            try {
                var compiler = ctx.<ComponentType, ComponentTypeCompiler>getCompiler(ComponentType.class);
                var compiledComponents = compiler.compile(ctx, compiledType, component, isRoot, maybeParameters);

                compiledComponents.forEach(c -> componentVerifiers.forEach(v -> v.verify(c)));
            } catch (CompilerException e) {
                if (component.getNamedType() != null) {
                    throw new CompilerException(component.getPosition(),
                            "Failed to compile component '%s' in %s '%s'",
                            e, component.getNamedType().getName(), typeName, name);
                } else {
                    throw new CompilerException(component.getPosition(),
                            "Failed to compile a component in %s '%s'", e, typeName, name);
                }
            }
        }
    }

}
