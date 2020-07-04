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
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.compiler.constraints.Bounds;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.PermittedAlphabetNode;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.RangeNode;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.constraints.PermittedAlphabetConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SubtypeConstraint;
import ch.eskaton.asn4j.runtime.types.TypeName;
import ch.eskaton.commons.collections.Tuple3;
import ch.eskaton.commons.functional.TriFunction;
import ch.eskaton.commons.utils.Dispatcher;

import java.util.Optional;

public class PermittedAlphabetConstraintCompiler implements ElementsCompiler<PermittedAlphabetConstraint> {

    private Dispatcher<Elements, Class<? extends Elements>, Tuple3<CompiledType,
            ? extends Elements, Optional<Bounds>>, Node> dispatcher;

    private final TypeName typeName;

    public PermittedAlphabetConstraintCompiler(CompilerContext ctx, TypeName typeName) {
        this.typeName = typeName;
        this.dispatcher = new Dispatcher<Elements, Class<? extends Elements>, Tuple3<CompiledType,
                ? extends Elements, Optional<Bounds>>, Node>()
                .withComparator((t, c) -> c.isInstance(t))
                .withException(e -> new CompilerException("Invalid constraint %s for %s type",
                        e.getClass().getSimpleName(), typeName));

        addConstraintHandler(ElementSet.class, new ElementSetCompiler(dispatcher)::compile);
        addConstraintHandler(RangeNode.class, new StringValueRangeCompiler(ctx)::compile);
    }

    protected <T extends Elements> Node dispatchToCompiler(Class<T> clazz,
            TriFunction<CompiledType, T, Optional<Bounds>, Node> function,
            Optional<Tuple3<CompiledType, ? extends Elements, Optional<Bounds>>> maybeArgs) {
        var args = maybeArgs.orElseThrow(
                () -> new IllegalCompilerStateException("Arguments in dispatchToCompiler may not be null"));

        return function.apply(args.get_1(), clazz.cast(args.get_2()), args.get_3());
    }

    protected <T extends Elements> void addConstraintHandler(Class<T> clazz,
            TriFunction<CompiledType, T, Optional<Bounds>, Node> function) {
        dispatcher.withCase(clazz, a -> dispatchToCompiler(clazz, function, a));
    }

    public TypeName getTypeName() {
        return typeName;
    }

    @Override
    public Node compile(CompiledType baseType, PermittedAlphabetConstraint elements, Optional<Bounds> bounds) {
        var constraint = elements.getConstraint();

        if (!(constraint instanceof SubtypeConstraint)) {
            throw new CompilerException(constraint.getPosition(), "Invalid subtype constraint: %s",
                    constraint.getClass().getSimpleName());
        }

        var setSpecs = ((SubtypeConstraint) constraint).getElementSetSpecs();
        var rootElements = setSpecs.getRootElements();
        var root = dispatcher.execute(rootElements, Tuple3.of(baseType, rootElements, bounds));
        var visitor = new PermittedAlphabetVisitor();

        return new PermittedAlphabetNode(visitor.visit(root));
    }

}
