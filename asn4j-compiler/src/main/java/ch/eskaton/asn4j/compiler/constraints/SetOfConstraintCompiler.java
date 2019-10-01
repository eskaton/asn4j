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

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.constraints.ast.CollectionOfValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.IntegerRange;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.SizeNode;
import ch.eskaton.asn4j.compiler.java.JavaUtils;
import ch.eskaton.asn4j.compiler.java.objs.JavaClass;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.constraints.ContainedSubtype;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SizeConstraint;
import ch.eskaton.asn4j.parser.ast.values.CollectionOfValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;
import ch.eskaton.asn4j.runtime.types.ASN1SetOf;
import ch.eskaton.commons.utils.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.java.objs.JavaVisibility.Public;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;

public class SetOfConstraintCompiler extends AbstractConstraintCompiler {

    public SetOfConstraintCompiler(CompilerContext ctx) {
        super(ctx);
    }

    @Override
    protected Node calculateElements(CompiledType baseType, Elements elements, Optional<Bounds> bounds) {
        if (elements instanceof ElementSet) {
            return compileConstraint(baseType, (ElementSet) elements, bounds);
        } else if (elements instanceof SingleValueConstraint) {
            Value value = ((SingleValueConstraint) elements).getValue();

            try {
                CollectionOfValue collectionOfValue = ctx.resolveGenericValue(CollectionOfValue.class,
                        baseType.getType(), value);

                return new CollectionOfValueNode(singleton(collectionOfValue));
            } catch (Exception e) {
                throw new CompilerException("Invalid single-value constraint %s for SET OF type", e,
                        value.getClass().getSimpleName());
            }
        } else if (elements instanceof ContainedSubtype) {
            return calculateContainedSubtype(((ContainedSubtype) elements).getType());
        } else if (elements instanceof SizeConstraint) {
            return calculateSize(baseType, ((SizeConstraint) elements).getConstraint(), bounds);
        } else {
            throw new CompilerException("Invalid constraint %s for SET OF type",
                    elements.getClass().getSimpleName());
        }
    }

    @Override
    public void addConstraint(JavaClass javaClass, ConstraintDefinition definition) {
        javaClass.addStaticImport(CollectionUtils.class, "asHashSet");

        String baseName = ASN1SetOf.class.getSimpleName();
        JavaClass parentClass = javaClass;

        while (!parentClass.getParent().equals(baseName)) {
            final String parentName = parentClass.getParent();

            parentClass = ctx.getClass(parentName).orElseThrow(
                    () -> new CompilerException("Failed to resolve parent class: %s", parentName));
        }

        JavaClass.BodyBuilder builder = javaClass.method().annotation("@Override").modifier(Public)
                .returnType(boolean.class).name("doCheckConstraint")
                .exception(ConstraintViolatedException.class).body();

        addConstraintCondition(definition, builder);

        builder.finish().build();
    }

    @Override
    protected Node optimize(Node node) {
        return new SetOfConstraintOptimizingVisitor().visit(node);
    }

    @Override
    protected Optional<String> buildExpression(Node node) {
        switch (node.getType()) {
            case VALUE:
                Set<CollectionOfValue> values = ((CollectionOfValueNode) node).getValue();
                return Optional.of(values.stream().map(this::buildExpression).collect(Collectors.joining(" || ")));
            case SIZE:
                List<IntegerRange> sizes = ((SizeNode) node).getSize();
                return Optional.of(sizes.stream().map(this::buildSizeExpression).collect(Collectors.joining(" || ")));
            default:
                return super.buildExpression(node);
        }
    }

    private String buildExpression(CollectionOfValue value) {
        String initString = value.getValues().stream()
                .map(JavaUtils::getInitializerString)
                .collect(Collectors.joining(", "));

        return "(getValues().equals(asHashSet(" + initString + ")))";
    }

    private String buildSizeExpression(IntegerRange range) {
        long lower = range.getLower();
        long upper = range.getUpper();

        if (lower == upper) {
            return String.format("(getValues().size() == %dL)", lower);
        } else if (lower == 0) {
            return String.format("(getValues().size() <= %dL)", upper);
        } else if (upper == Long.MAX_VALUE) {
            return String.format("(getValues().size() >= %dL)", lower);
        } else {
            return String.format("(%dL <= getValues().size() && %dL >= getValues().size())", lower, upper);
        }
    }

}
