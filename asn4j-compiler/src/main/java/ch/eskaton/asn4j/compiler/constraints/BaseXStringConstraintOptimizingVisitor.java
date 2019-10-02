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

package ch.eskaton.asn4j.compiler.constraints;

import ch.eskaton.asn4j.compiler.constraints.ast.BinOpType;
import ch.eskaton.asn4j.compiler.constraints.ast.ValueNode;
import ch.eskaton.asn4j.parser.ast.values.AbstractValue;
import ch.eskaton.asn4j.parser.ast.values.HasSize;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public abstract class BaseXStringConstraintOptimizingVisitor<V extends AbstractValue & HasSize, C extends List<V>, N extends ValueNode<C>>
        extends AbstractConstraintOptimizingVisitor<V, C, N> {

    public <T extends Comparator<V>> BaseXStringConstraintOptimizingVisitor(T comparator, Function<C, N> createNode) {
        super.configureTransformation(BinOpType.VALUE_VALUE,
                new ValueValueTransformer<>(new OrderedSetOperationsStrategy(comparator), createNode));
        super.configureTransformation(BinOpType.VALUE_NEGATION,
                new ValueNegationTransformer<>(new OrderedSetOperationsStrategy(comparator), createNode, false));
        super.configureTransformation(BinOpType.NEGATION_VALUE,
                new ValueNegationTransformer<>(new OrderedSetOperationsStrategy(comparator), createNode, true));
        super.configureTransformation(BinOpType.SIZE_SIZE, new SizeSizeTransformer());
        super.configureTransformation(BinOpType.SIZE_NEGATION, new SizeNegationTransformer());
        super.configureTransformation(BinOpType.NEGATION_SIZE, new SizeNegationTransformer());
        super.configureTransformation(BinOpType.VALUE_SIZE, new ValueSizeTransformer<>(createNode));
        super.configureTransformation(BinOpType.SIZE_VALUE, new SizeValueTransformer<>(createNode));
    }

}
