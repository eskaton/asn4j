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

package ch.eskaton.asn4j.compiler;

import ch.eskaton.asn4j.compiler.java.objs.JavaConstructor;
import ch.eskaton.asn4j.compiler.java.objs.JavaParameter;
import ch.eskaton.asn4j.compiler.java.objs.JavaVisibility;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.ParameterNode;
import ch.eskaton.asn4j.parser.ast.ReferenceNode;
import ch.eskaton.asn4j.parser.ast.types.Collection;
import ch.eskaton.asn4j.parser.ast.types.ComponentType;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractCollectionCompiler<T extends Collection> implements NamedCompiler<T, CompiledType> {

    private final TypeName typeName;

    private final List<Function<TypeName, ComponentVerifier>> componentVerifierSuppliers;

    public AbstractCollectionCompiler(TypeName typeName,
            Function<TypeName, ComponentVerifier>... componentVerifierSupplier) {
        this.typeName = typeName;
        this.componentVerifierSuppliers = new ArrayList<>(Arrays.asList(componentVerifierSupplier));
        this.componentVerifierSuppliers.add(NameUniquenessVerifier::new);
    }

    public CompiledType compile(CompilerContext ctx, String name, T node, Optional<Parameters> maybeParameters) {
        var tags = CompilerUtils.getTagIds(ctx, node);
        var javaClass = ctx.createClass(name, node, tags);
        var componentVerifiers = componentVerifierSuppliers.stream()
                .map(s -> s.apply(typeName))
                .collect(Collectors.toList());
        var ctor = new JavaConstructor(JavaVisibility.PUBLIC, name);
        var ctorBody = new StringBuilder();
        var compiledType = ctx.createCompiledType(CompiledCollectionType.class, node, name);

        compiledType.setTags(tags);

        for (ComponentType component : node.getAllComponents()) {
            try {
                var compiler = ctx.<ComponentType, ComponentTypeCompiler>getCompiler(ComponentType.class);
                var compiledComponents = compiler.compile(ctx, compiledType, component, maybeParameters);

                compiledComponents.forEach(c -> {
                    var argType = c.get_2().getName();
                    var argName = CompilerUtils.formatName(c.get_1());

                    componentVerifiers.forEach(v -> v.verify(c.get_1(), c.get_2()));

                    ctor.getParameters().add(new JavaParameter(argType, argName));
                    ctorBody.append("\t\tthis." + argName + " = " + argName + ";\n");
                });
            } catch (CompilerException e) {
                if (component.getNamedType() != null) {
                    throw new CompilerException("Failed to compile component %s in %s %s", e,
                            component.getNamedType().getName(), typeName, name);
                } else {
                    throw new CompilerException("Failed to compile a component in %s %s", e, typeName, name);
                }
            }
        }

        ParameterUsageVerifier.checkUnusedParameters(maybeParameters);

        ctor.setBody(Optional.of(ctorBody.toString()));
        javaClass.addMethod(ctor);

        var hasComponentConstraint = CompilerUtils.compileComponentConstraints(ctx, compiledType);

        if (node.hasConstraint() || hasComponentConstraint) {
            var constraintDef = ctx.compileConstraint(javaClass, name, compiledType);

            compiledType.setConstraintDefinition(constraintDef);
        }

        ctx.finishClass();

        return compiledType;
    }

    private static class NameUniquenessVerifier implements ComponentVerifier {

        private final TypeName typeName;

        private final Set<String> seenNames = new HashSet<>();

        public NameUniquenessVerifier(TypeName typeName) {
            this.typeName = typeName;
        }

        public void verify(String name, CompiledType component) {
            if (seenNames.contains(name)) {
                throw new CompilerException("Duplicate component name in %s '%s': %s", typeName.getName(),
                        component.getParent().getName(), name);
            }

            seenNames.add(name);
        }

    }

}
