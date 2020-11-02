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
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.compiler.Parameters;
import ch.eskaton.asn4j.compiler.constraints.Bounds;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.ObjectSetNode;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.ObjectSetElements;
import ch.eskaton.asn4j.parser.ast.ObjectSetReference;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.commons.collections.Tuple4;
import ch.eskaton.commons.utils.Dispatcher;

import java.util.Optional;

public class ObjectSetElementsCompiler implements ElementsCompiler<ObjectSetElements> {

    private CompilerContext ctx;

    public ObjectSetElementsCompiler(CompilerContext ctx, Dispatcher<Elements, Class<? extends Elements>,
            Tuple4<CompiledType, ? extends Elements, Optional<Bounds>, Optional<Parameters>>, Node> dispatcher) {
        this.ctx = ctx;
    }

    @Override
    public Node compile(CompiledType baseType, ObjectSetElements elements, Optional<Bounds> bounds,
            Optional<Parameters> maybeParameters) {
        var element = elements.getElement();

        if (element instanceof ObjectSetReference objectSetReference) {
            var compiledObjectSet = ctx.getCompiledObjectSet(objectSetReference, maybeParameters);

            return new ObjectSetNode(compiledObjectSet.getValues());
        }

        throw new IllegalCompilerStateException(elements.getPosition(), "Unsupported element type: %s", elements);
    }

}
