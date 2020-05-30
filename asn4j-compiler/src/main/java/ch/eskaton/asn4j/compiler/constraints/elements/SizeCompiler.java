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
import ch.eskaton.asn4j.compiler.constraints.IntegerValueBounds;
import ch.eskaton.asn4j.compiler.constraints.SizeBounds;
import ch.eskaton.asn4j.compiler.constraints.SizeVisitor;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.constraints.SizeConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SubtypeConstraint;
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
import ch.eskaton.commons.collections.Tuple3;
import ch.eskaton.commons.utils.Dispatcher;

import java.util.Optional;

import static ch.eskaton.asn4j.parser.NoPosition.NO_POSITION;

public class SizeCompiler implements ElementsCompiler<SizeConstraint> {

    protected final CompilerContext ctx;

    protected final Dispatcher<Elements, Class<? extends Elements>, Tuple3<CompiledType,
            ? extends Elements, Optional<Bounds>>, Node> dispatcher;


    public SizeCompiler(CompilerContext ctx, Dispatcher<Elements, Class<? extends Elements>, Tuple3<CompiledType,
            ? extends Elements, Optional<Bounds>>, Node> dispatcher) {
        this.ctx = ctx;
        this.dispatcher = dispatcher;
    }

    @Override
    public Node compile(CompiledType compiledType, SizeConstraint sizeConstraint, Optional<Bounds> bounds) {
        var constraint = sizeConstraint.getConstraint();

        if (constraint instanceof SubtypeConstraint) {
            var setSpecs = ((SubtypeConstraint) constraint).getElementSetSpecs();
            var compiledBaseType = ctx.getCompiledBaseType(new IntegerType(NO_POSITION));
            var rootElements = setSpecs.getRootElements();

            var adjustedBounds = bounds.map(b -> new IntegerValueBounds(Math.max(0, ((SizeBounds) b).getMinSize()),
                    ((SizeBounds) b).getMaxSize()))
                    .orElse(new IntegerValueBounds(0L, Long.MAX_VALUE));

            var node = dispatcher.execute(rootElements, Tuple3.of(compiledBaseType, rootElements,
                    Optional.of(adjustedBounds)));

            var maybeSizes = new SizeVisitor().visit(node);

            if (!maybeSizes.isPresent() || maybeSizes.get().getSize().isEmpty()) {
                throw new CompilerException(setSpecs.getPosition(),
                        "Invalid SIZE constraint. It contains no restrictions.");
            }

            return maybeSizes.get();
        } else {
            throw new CompilerException("Constraints of type %s not yet supported",
                    constraint.getClass().getSimpleName());
        }
    }

}
