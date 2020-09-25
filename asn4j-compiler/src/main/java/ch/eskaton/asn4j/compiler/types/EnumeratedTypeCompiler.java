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
import ch.eskaton.asn4j.compiler.Parameters;
import ch.eskaton.asn4j.compiler.java.objs.JavaClass;
import ch.eskaton.asn4j.compiler.java.objs.JavaConstructor;
import ch.eskaton.asn4j.compiler.java.objs.JavaParameter;
import ch.eskaton.asn4j.compiler.java.objs.JavaVisibility;
import ch.eskaton.asn4j.compiler.results.CompiledEnumeratedType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.compiler.results.EnumerationItems;
import ch.eskaton.asn4j.parser.ast.EnumerationItemNode;
import ch.eskaton.asn4j.parser.ast.types.EnumeratedType;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.runtime.exceptions.ASN1RuntimeException;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;
import ch.eskaton.commons.MutableInteger;
import ch.eskaton.commons.collections.Tuple2;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.java.objs.JavaType.INT;
import static ch.eskaton.asn4j.compiler.java.objs.JavaVisibility.PUBLIC;
import static java.util.Arrays.asList;

public class EnumeratedTypeCompiler implements NamedCompiler<EnumeratedType, CompiledType> {

    public static final String VALUE_PARAMETER = "value";

    @Override
    public CompiledType compile(CompilerContext ctx, String name, EnumeratedType node,
            Optional<Parameters> maybeParameters) {
        if (node.hasExceptionSpec()) {
            // TODO: figure out what to do
        }

        var tags = CompilerUtils.getTagIds(ctx, node);
        var javaClass = ctx.createClass(name, node, tags);
        var compiledType = createCompiledType(ctx, name, node);

        compiledType.setTags(tags);

        ctx.compileConstraintAndModule(name, compiledType).ifPresent(constraintAndModule -> {
            compiledType.setConstraintDefinition(constraintAndModule.get_1());
            compiledType.setModule(constraintAndModule.get_2());
        });

        generateJavaClass(ctx, javaClass, compiledType);

        ctx.finishClass(false);

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

    private JavaClass generateJavaClass(CompilerContext ctx, JavaClass javaClass, CompiledEnumeratedType compiledType) {
        var name = compiledType.getName();
        var allItems = compiledType.getRoots().copy().addAll(compiledType.getAdditions().getItems());
        var cases = new HashMap<Integer, String>();

        allItems.getItems().forEach(item -> {
            var fieldName = CompilerUtils.formatConstant(item.get_1());
            var value = item.get_2();
            var initializer = "new %s(%s)".formatted(name, value);

            cases.put(value, fieldName);

            javaClass.field()
                    .modifier(PUBLIC)
                    .asStatic()
                    .asFinal()
                    .type(name)
                    .name(fieldName)
                    .initializer(initializer)
                    .build();
        });

        javaClass.addMethod(new JavaConstructor(JavaVisibility.PUBLIC, name));
        javaClass.addMethod(new JavaConstructor(JavaVisibility.PROTECTED, name,
                asList(new JavaParameter("int", VALUE_PARAMETER)), Optional.of("\t\tsuper.setValue(value);")));
        javaClass.addMethod(new JavaConstructor(JavaVisibility.PUBLIC, name,
                asList(new JavaParameter(name, VALUE_PARAMETER)), Optional.of("\t\tsuper.setValue(value.getValue());")));

        var bodyBuilder = javaClass.method()
                .asStatic()
                .returnType(name).name("valueOf")
                .parameter(INT, VALUE_PARAMETER)
                .exception(ASN1RuntimeException.class)
                .body();

        bodyBuilder.append("switch(value) {");

        for (var entry : cases.entrySet()) {
            bodyBuilder.append("\tcase " + entry.getKey() + ":");
            bodyBuilder.append("\t\treturn " + entry.getValue() + ";");
        }

        bodyBuilder.append("\tdefault:")
                .append("\t\tthrow new " + ASN1RuntimeException.class.getSimpleName() +
                        "(\"Undefined value: \" + value);").append("}");

        bodyBuilder.finish().build();

        javaClass.addImport(ASN1RuntimeException.class.getCanonicalName());

        if (compiledType.getModule().isPresent()) {
            javaClass.addModule(ctx, compiledType.getModule().get());
            javaClass.addImport(ConstraintViolatedException.class);
        }

        return javaClass;
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
