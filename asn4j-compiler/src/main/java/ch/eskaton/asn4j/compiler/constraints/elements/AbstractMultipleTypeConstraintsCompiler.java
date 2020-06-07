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
package ch.eskaton.asn4j.compiler.constraints.elements;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.constraints.Bounds;
import ch.eskaton.asn4j.compiler.constraints.ast.ComponentNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.WithComponentsNode;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.constraints.MultipleTypeConstraints;
import ch.eskaton.asn4j.parser.ast.constraints.NamedConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.PresenceConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.ValueConstraint;
import ch.eskaton.commons.utils.StreamsUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public abstract class AbstractMultipleTypeConstraintsCompiler implements ElementsCompiler<MultipleTypeConstraints> {

    protected final CompilerContext ctx;

    private final Supplier<? extends ComponentVerifier> verifierSupplier;

    public AbstractMultipleTypeConstraintsCompiler(CompilerContext ctx,
            Supplier<? extends ComponentVerifier> verifierSupplier) {
        this.ctx = ctx;
        this.verifierSupplier = verifierSupplier;
    }

    @Override
    public Node compile(CompiledType baseType, MultipleTypeConstraints elements, Optional<Bounds> bounds) {
        var compiledCollectionType = (CompiledCollectionType) baseType;
        var components = compiledCollectionType.getComponents();
        var componentNodes = new HashSet<ComponentNode>();
        var verifier = verifierSupplier.get();

        for (var constraint : elements.getConstraints()) {
            var name = constraint.getName();
            var index = StreamsUtils.indexOf(components, c -> Objects.equals(name, c.get_1()));

            verifier.verify(index, compiledCollectionType.getName(), name);

            componentNodes.add(compileComponentConstraint(components.get(index).get_2(), constraint));
        }

        return new WithComponentsNode(componentNodes);
    }

    protected ComponentNode compileComponentConstraint(CompiledType compiledType, NamedConstraint namedConstraint) {
        var name = namedConstraint.getName();
        var constraint = namedConstraint.getConstraint();
        var presence = Optional.ofNullable(constraint.getPresence()).map(PresenceConstraint::getType).orElse(null);
        var maybeValueConstraint = Optional.ofNullable(constraint.getValue()).map(ValueConstraint::getConstraint);
        var definition = ctx.compileConstraint(compiledType);
        Node roots = null;

        if (maybeValueConstraint.isPresent()) {
            var valueConstraint = maybeValueConstraint.get();

            if (definition != null) {
                definition = definition.serialApplication(ctx.compileConstraint(compiledType, valueConstraint));
            } else {
                definition = ctx.compileConstraint(compiledType, valueConstraint);
            }

            roots = definition.getRoots();
        }

        return new ComponentNode(name, compiledType.getType(), roots, presence);
    }

    protected static class ComponentVerifier {

        private Set<String> definedComponents = new HashSet<>();

        protected void verify(int index, String typeName, String componentName) {
            if (index == -1) {
                throwNotFound(typeName, componentName);
            }

            if (definedComponents.contains(componentName)) {
                throw new CompilerException("Duplicate element '%s' found in type '%s'", componentName, typeName);
            }

            definedComponents.add(componentName);
        }

        protected void throwNotFound(String typeName, String componentName) {
            throw new CompilerException("Component '%s' not found in type '%s'", componentName, typeName);
        }

    }

}
