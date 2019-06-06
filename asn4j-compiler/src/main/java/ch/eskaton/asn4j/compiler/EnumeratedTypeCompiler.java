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

import ch.eskaton.asn4j.compiler.constraints.ConstraintDefinition;
import ch.eskaton.asn4j.compiler.java.JavaClass;
import ch.eskaton.asn4j.compiler.java.JavaClass.BodyBuilder;
import ch.eskaton.asn4j.compiler.java.JavaConstructor;
import ch.eskaton.asn4j.compiler.java.JavaParameter;
import ch.eskaton.asn4j.compiler.java.JavaVisibility;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.EnumerationItemNode;
import ch.eskaton.asn4j.parser.ast.types.EnumeratedType;
import ch.eskaton.asn4j.runtime.exceptions.ASN1RuntimeException;
import ch.eskaton.commons.MutableInteger;
import ch.eskaton.commons.collections.Tuple2;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.java.JavaType.INT;
import static ch.eskaton.asn4j.compiler.java.JavaVisibility.Public;

public class EnumeratedTypeCompiler implements NamedCompiler<EnumeratedType, CompiledType> {

    @Override
    public CompiledType compile(CompilerContext ctx, String typeName, EnumeratedType node) {
        JavaClass javaClass = ctx.createClass(typeName, node, true);
        EnumerationItems rootItems = getRootItems(ctx, typeName, node.getRootEnum());

        addAdditionalItems(ctx, typeName, rootItems, node.getAdditionalEnum());

        if (node.hasExceptionSpec()) {
            // TODO: figure out what to do
        }

        Map<Integer, String> cases = new HashMap<>();

        for (int j = 0; j < rootItems.getItems().size(); j++) {
            String fieldName = CompilerUtils.formatConstant(rootItems.getName(j));
            int value = rootItems.getNumber(j);

            cases.put(value, fieldName);

            javaClass.field().modifier(Public).asStatic().asFinal().type(typeName).name(fieldName)
                    .initializer("new " + typeName + "(" + value + ")").build();
        }

        javaClass.addMethod(new JavaConstructor(JavaVisibility.Protected, typeName,
                Arrays.asList(new JavaParameter("int", "value")),
                "\t\tsuper.setValue(value);\n"));

        BodyBuilder builder = javaClass.method().asStatic().returnType(typeName).name("valueOf")
                .parameter(INT, "value").exception(ASN1RuntimeException.class).body();

        builder.append("switch(value) {");

        for (Entry<Integer, String> entry : cases.entrySet()) {
            builder.append("\tcase " + entry.getKey() + ":");
            builder.append("\t\treturn " + entry.getValue() + ";");
        }

        builder.append("\tdefault:")
                .append("\t\tthrow new " + ASN1RuntimeException.class.getSimpleName() +
                        "(\"Undefined value: \" + value);").append("}");

        builder.finish().build();

        javaClass.addImport(ASN1RuntimeException.class.getCanonicalName());

        ConstraintDefinition constraintDef = null;

        if (node.hasConstraint()) {
            constraintDef = ctx.compileConstraint(javaClass, typeName, node);
        }

        ctx.finishClass(false);

        return new CompiledType(node, constraintDef);
    }

    EnumerationItems getRootItems(CompilerContext ctx, String typeName, List<EnumerationItemNode> nodes) {
        EnumerationItems items = new EnumerationItems();

        addEnumerationItems(ctx, typeName, items, nodes);

        int i;
        MutableInteger n = MutableInteger.of(0);

        for (i = 0; i < items.getItems().size(); i++) {
            if (items.getNumber(i) == null) {
                while (items.contains(n)) {
                    n.increment();
                }

                items.setNumber(i, n.getValue());
            }
        }

        return items;
    }

    void addAdditionalItems(CompilerContext ctx, String typeName, EnumerationItems items,
            List<EnumerationItemNode> nodes) {

        if (nodes != null) {
            int i = items.getItems().size();
            MutableInteger n = MutableInteger.of(0);

            addEnumerationItems(ctx, typeName, items, nodes);

            for (; i < items.getItems().size(); i++) {
                if (items.getNumber(i) == null) {
                    n.setValue(getNextNumber(items.getItems(), i));

                    if (items.contains(n)) {
                        throw new CompilerException("Duplicate enumeration value %s(%s) in %s",
                                items.getName(i), n, typeName);
                    }

                    items.setNumber(i, n.getValue());
                }
            }
        }
    }

    private void addEnumerationItems(CompilerContext ctx, String typeName, EnumerationItems items,
            List<EnumerationItemNode> nodes) {
        for (EnumerationItemNode node : nodes) {
            items.add(node.getName(), getNumber(ctx, typeName, node));
        }
    }

    private Integer getNumber(CompilerContext ctx, String name, EnumerationItemNode item) {
        Integer number;

        if (item.getRef() != null) {
            BigInteger bigValue = ctx.resolveValue(BigInteger.class, item.getRef());

            if (bigValue.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
                throw new CompilerException("Value %s too large in type %s", bigValue, name);
            }

            number = bigValue.intValue();
        } else {
            number = item.getNumber();
        }

        return number;
    }

    int getNextNumber(List<Tuple2<String, Integer>> numbers, int last) {
        return numbers.stream().map(Tuple2::get_2).limit(last).collect(Collectors.maxBy(Integer::compare)).get() + 1;
    }

    static class EnumerationItems {

        private List<Tuple2<String, Integer>> items = new ArrayList<>();

        public List<Tuple2<String, Integer>> getItems() {
            return items;
        }

        public String getName(int index) {
            return items.get(index).get_1();
        }

        public Integer getNumber(int index) {
            return items.get(index).get_2();
        }

        public void setNumber(int index, int value) {
            items.get(index).set_2(value);
        }

        public boolean contains(MutableInteger n) {
            return getItems().stream().anyMatch(item -> Objects.equals(item.get_2(), n.getValue()));
        }

        public void add(String name, Integer value) {
            items.forEach(item -> {
                if (name.equals(item.get_1())) {
                    throw new CompilerException("Duplicate enumeration item '%s'", name);
                } else if (value != null && value.equals(item.get_2())) {
                    throw new CompilerException("Duplicate enumeration value %s(%s)", name, value);
                }
            });

            items.add(Tuple2.of(name, value));
        }

    }

}
