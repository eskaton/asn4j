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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static ch.eskaton.asn4j.compiler.java.JavaType.INT;
import static ch.eskaton.asn4j.compiler.java.JavaVisibility.Public;

public class EnumeratedTypeCompiler implements NamedCompiler<EnumeratedType, CompiledType> {

    @Override
    public CompiledType compile(CompilerContext ctx, String typeName, EnumeratedType node) {
        JavaClass javaClass = ctx.createClass(typeName, node, true);
        EnumerationItems items = new EnumerationItems();

        addRootItems(ctx, typeName, items, node.getRootEnum());
        addAdditionalItems(ctx, typeName, items, node.getAdditionalEnum());

        if (node.hasExceptionSpec()) {
            // TODO: figure out what to do
        }

        Map<Integer, String> cases = new HashMap<>();

        for (int j = 0; j < items.getNames().size(); j++) {
            String fieldName = CompilerUtils.formatConstant(items.getNames().get(j));
            int value = items.getNumbers().get(j);

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

    void addRootItems(CompilerContext ctx, String typeName, EnumerationItems items,
            List<EnumerationItemNode> nodes) {
        addEnumerationItems(ctx, typeName, items, nodes);

        List<Integer> numbers = items.getNumbers();

        int i;
        int n = 0;

        for (i = 0; i < numbers.size(); i++) {
            if (numbers.get(i) == null) {
                while (numbers.contains(n)) {
                    n++;
                }
                numbers.set(i, n);
            }
        }
    }

    void addAdditionalItems(CompilerContext ctx, String typeName, EnumerationItems items,
            List<EnumerationItemNode> nodes) {

        if (nodes != null) {
            int i = items.getNumbers().size();
            int n;

            addEnumerationItems(ctx, typeName, items, nodes);

            List<Integer> numbers = items.getNumbers();
            List<String> names = items.getNames();

            for (; i < numbers.size(); i++) {
                if (numbers.get(i) == null) {
                    n = getNextNumber(numbers, i);

                    if (numbers.contains(n)) {
                        throw new CompilerException("Duplicate enumeration value %s(%s) in %s", names.get(i),
                                n, typeName);
                    }

                    numbers.set(i, n);
                }
            }
        }
    }

    private void addEnumerationItems(CompilerContext ctx, String name, EnumerationItems items,
            List<EnumerationItemNode> rootEnum) {
        for (EnumerationItemNode item : rootEnum) {
            addEnumerationItem(name, items, item.getName(), getNumber(ctx, name, item));
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

    private Integer getNextNumber(List<Integer> numbers, int last) {
        int n = 0;

        for (int i = 0; i < last; i++) {
            n = Math.max(numbers.get(i), n);
        }

        return n + 1;
    }

    private void addEnumerationItem(String typeName, EnumerationItems items, String name, Integer value) {
        if (items.getNames().contains(name)) {
            throw new CompilerException("Duplicate enumeration item '%s' in %s", name, typeName);
        }

        if (value != null && items.getNumbers().contains(value)) {
            throw new CompilerException("Duplicate enumeration value %s(%s) in %s", name, value, typeName);
        }

        items.getNames().add(name);
        items.getNumbers().add(value);
    }

    static class EnumerationItems {

        List<String> names = new ArrayList<>();

        List<Integer> numbers = new ArrayList<>();

        public List<String> getNames() {
            return names;
        }

        public void setNames(List<String> names) {
            this.names = names;
        }

        public List<Integer> getNumbers() {
            return numbers;
        }

        public void setNumbers(List<Integer> numbers) {
            this.numbers = numbers;
        }

    }

}
