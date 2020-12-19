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
import ch.eskaton.asn4j.compiler.results.CompiledEnumeratedType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.compiler.results.EnumerationItems;
import ch.eskaton.asn4j.parser.ast.EnumerationItemNode;
import ch.eskaton.asn4j.parser.ast.types.EnumeratedType;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.commons.MutableInteger;
import ch.eskaton.commons.collections.Tuple2;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EnumeratedTypeCompiler implements NamedCompiler<EnumeratedType, CompiledType> {

    @Override
    public CompiledType compile(CompilerContext ctx, String name, EnumeratedType node,
            Optional<Parameters> maybeParameters) {
        if (node.hasExceptionSpec()) {
            // TODO: figure out what to do
        }

        var tags = CompilerUtils.getTagIds(ctx, node);
        var compiledType = createCompiledType(ctx, name, node);

        compiledType.setTags(tags);

        ctx.compileConstraintAndModule(name, compiledType, maybeParameters).ifPresent(constraintAndModule -> {
            compiledType.setConstraintDefinition(constraintAndModule.get_1());
            compiledType.setModule(constraintAndModule.get_2());
        });

        return compiledType;
    }

    public CompiledEnumeratedType createCompiledType(CompilerContext ctx, String name, EnumeratedType node) {
        var compiledType = ctx.createCompiledType(CompiledEnumeratedType.class, node, name);
        var rootItems = getRootItems(ctx, name, node.getRootEnum());
        var additionalItems = getAdditionalItems(ctx, name, rootItems, node.getAdditionalEnum());

        compiledType.setRoots(rootItems);
        compiledType.setAdditions(additionalItems);

        return compiledType;
    }

    public EnumerationItems getRootItems(CompilerContext ctx, String typeName, List<EnumerationItemNode> nodes) {
        EnumerationItems items = getEnumerationItems(ctx, typeName, nodes);
        MutableInteger n = MutableInteger.of(0);

        items.getItems().forEach(item -> {
            if (item.get_2() == null) {
                while (items.contains(n)) {
                    n.increment();
                }

                item.set_2(n.getValue());
            }
        });

        return items;
    }

    public EnumerationItems getAdditionalItems(CompilerContext ctx, String typeName, EnumerationItems rootItems,
            List<EnumerationItemNode> nodes) {
        var additionalItems = getEnumerationItems(ctx, typeName, nodes);
        var allItems = rootItems.copy();

        allItems.addAll(additionalItems.getItems());

        if (nodes != null) {
            var i = rootItems.getItems().size();
            var n = MutableInteger.of(0);

            for (; i < allItems.getItems().size(); i++) {
                if (allItems.getNumber(i) == null) {
                    n.setValue(getNextNumber(allItems.getItems(), i));

                    if (allItems.contains(n)) {
                        throw new CompilerException("Duplicate enumeration value %s(%s) in %s",
                                allItems.getName(i), n, typeName);
                    }

                    allItems.setNumber(i, n.getValue());
                }
            }
        }

        return additionalItems;
    }

    private EnumerationItems getEnumerationItems(CompilerContext ctx, String typeName, List<EnumerationItemNode> nodes) {
        if (nodes == null) {
            return new EnumerationItems();
        }

        return new EnumerationItems().addAll(nodes.stream()
                .map(node -> Tuple2.of(node.getName(), getNumber(ctx, typeName, node)))
                .collect(Collectors.toList()));
    }

    private Integer getNumber(CompilerContext ctx, String name, EnumerationItemNode item) {
        Integer number;

        if (item.getRef() != null) {
            var compiledValue = ctx.<IntegerValue>getCompiledValue(IntegerValue.class, item.getRef());
            var bigValue = compiledValue.getValue().getValue();

            if (bigValue.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
                throw new CompilerException(item.getPosition(), "Value %s too large in type %s", bigValue, name);
            }

            number = bigValue.intValue();
        } else {
            number = item.getNumber();
        }

        return number;
    }

    public int getNextNumber(List<Tuple2<String, Integer>> numbers, int last) {
        return numbers.stream()
                .map(Tuple2::get_2)
                .limit(last)
                .collect(Collectors.maxBy(Integer::compare))
                .orElse(-1) + 1;
    }

}
