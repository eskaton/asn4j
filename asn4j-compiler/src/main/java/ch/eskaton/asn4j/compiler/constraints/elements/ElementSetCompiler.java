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

import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.compiler.parameters.Parameters;
import ch.eskaton.asn4j.compiler.constraints.Bounds;
import ch.eskaton.asn4j.compiler.constraints.ast.BinOpNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.NodeType;
import ch.eskaton.asn4j.compiler.constraints.ast.OpNode;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.commons.collections.Tuple4;
import ch.eskaton.commons.utils.Dispatcher;

import java.util.List;
import java.util.Optional;

import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.COMPLEMENT;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.INTERSECTION;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.NEGATION;
import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.UNION;

public class ElementSetCompiler implements ElementsCompiler<ElementSet> {

    private final Dispatcher<Elements, Class<? extends Elements>,
            Tuple4<CompiledType, ? extends Elements, Optional<Bounds>, Optional<Parameters>>, Node> dispatcher;

    public ElementSetCompiler(Dispatcher<Elements, Class<? extends Elements>,
            Tuple4<CompiledType, ? extends Elements, Optional<Bounds>, Optional<Parameters>>, Node> dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public Node compile(CompiledType baseType, ElementSet set, Optional<Bounds> bounds,
            Optional<Parameters> maybeParameters) {
        var operands = set.getOperands();

        switch (set.getOperation()) {
            case ALL:
                return calculateInversion(compile(baseType, (ElementSet) operands.get(0), bounds, maybeParameters));

            case EXCLUDE:
                var set1 = calculateElements(baseType, operands.get(0), bounds, maybeParameters);

                if (operands.size() == 1) {
                    // ALL EXCEPT
                    return set1;
                } else {
                    var set2 = calculateElements(baseType, operands.get(1), bounds, maybeParameters);

                    return calculateExclude(set1, set2);
                }

            case INTERSECTION:
                return calculateIntersection(baseType, operands, bounds, maybeParameters);

            case UNION:
                return calculateUnion(baseType, operands, bounds, maybeParameters);

            default:
                throw new IllegalCompilerStateException("Unimplemented node type: %s", set.getOperation());
        }
    }

    protected Node calculateElements(CompiledType baseType, Elements elements, Optional<Bounds> bounds,
            Optional<Parameters> maybeParameters) {
        return dispatcher.execute(elements, Tuple4.of(baseType, elements, bounds, maybeParameters));
    }

    protected Node calculateIntersection(CompiledType baseType, List<Elements> elements, Optional<Bounds> bounds,
            Optional<Parameters> maybeParameters) {
        return calculateBinOp(baseType, elements, bounds, maybeParameters, INTERSECTION);
    }

    protected Node calculateUnion(CompiledType baseType, List<Elements> elements, Optional<Bounds> bounds,
            Optional<Parameters> maybeParameters) {
        return calculateBinOp(baseType, elements, bounds, maybeParameters, UNION);
    }

    protected Node calculateInversion(Node node) {
        return new OpNode(NEGATION, node);
    }

    protected Node calculateExclude(Node values1, Node values2) {
        return new BinOpNode(COMPLEMENT, values1, values2);
    }

    protected Node calculateBinOp(CompiledType baseType, List<Elements> elements, Optional<Bounds> bounds,
            Optional<Parameters> maybeParameters, NodeType type) {
        Node node = null;

        for (var element : elements) {
            var tmpNode = calculateElements(baseType, element, bounds, maybeParameters);

            if (node == null) {
                node = tmpNode;
            } else {
                node = new BinOpNode(type, node, tmpNode);
            }
        }

        return node;
    }

}
