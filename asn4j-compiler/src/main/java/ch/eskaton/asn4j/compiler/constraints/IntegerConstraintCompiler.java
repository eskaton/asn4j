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
import ch.eskaton.asn4j.compiler.constraints.ast.IntegerRange;
import ch.eskaton.asn4j.compiler.constraints.ast.IntegerRangeValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.java.JavaClass;
import ch.eskaton.asn4j.compiler.java.JavaClass.BodyBuilder;
import ch.eskaton.asn4j.parser.ast.RangeNode;
import ch.eskaton.asn4j.parser.ast.constraints.ContainedSubtype;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.constraints.ast.IntegerRange.getLowerBound;
import static ch.eskaton.asn4j.compiler.constraints.ast.IntegerRange.getUpperBound;
import static ch.eskaton.asn4j.compiler.java.JavaVisibility.Protected;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class IntegerConstraintCompiler extends AbstractConstraintCompiler {

    private static final IntegerValueBoundsVisitor BOUNDS_VISITOR = new IntegerValueBoundsVisitor();

    public IntegerConstraintCompiler(CompilerContext ctx) {
        super(ctx);
    }

    @Override
    protected Optional<Bounds> getBounds(Optional<ConstraintDefinition> constraint) {
        return constraint.map(c ->
                new IntegerValueBounds(getLowerBound(BOUNDS_VISITOR.visit(c.getRoots()).orElse(emptyList())),
                        getUpperBound(BOUNDS_VISITOR.visit(c.getRoots()).orElse(emptyList()))));
    }

    @Override
    protected Node calculateElements(Type base, Elements elements, Optional<Bounds> bounds) {
        if (elements instanceof ElementSet) {
            return compileConstraint(base, (ElementSet) elements, bounds);
        } else if (elements instanceof SingleValueConstraint) {
            Value value = ((SingleValueConstraint) elements).getValue();

            if (value instanceof IntegerValue) {
                if (((IntegerValue) value).isReference()) {
                    // TODO: resolve
                    throw new CompilerException("not yet supported");
                } else {
                    long intValue = ((IntegerValue) value).getValue().longValue();

                    return new IntegerRangeValueNode((singletonList(new IntegerRange(intValue, intValue))));
                }
            } else {
                throw new CompilerException("Invalid single-value constraint %s for INTEGER type",
                        value.getClass().getSimpleName());
            }
        } else if (elements instanceof ContainedSubtype) {
            Type type = ((ContainedSubtype) elements).getType();
            return calculateContainedSubtype(base, type);
        } else if (elements instanceof RangeNode) {
            long min = bounds.map(b -> ((IntegerValueBounds) b).getMinValue()).orElse(Long.MIN_VALUE);
            long max = bounds.map(b -> ((IntegerValueBounds) b).getMaxValue()).orElse(Long.MAX_VALUE);

            IntegerValue lower = ((RangeNode) elements).getLower().getLowerEndPointValue(min);
            IntegerValue upper = ((RangeNode) elements).getUpper().getUpperEndPointValue(max);

            return new IntegerRangeValueNode(singletonList(new IntegerRange(lower.getValue().longValue(), upper.getValue().longValue())));
        } else {
            throw new CompilerException("Invalid constraint %s for INTEGER type",
                    elements.getClass().getSimpleName());
        }
    }

    @Override
    public void addConstraint(JavaClass javaClass, ConstraintDefinition definition) {
        javaClass.addImport(BigInteger.class);

        BodyBuilder builder = javaClass.method().annotation(Override.class).modifier(Protected)
                .returnType(boolean.class).name("checkConstraint").parameter("BigInteger", "value")
                .exception(ConstraintViolatedException.class).body();

        addConstraintCondition(definition, builder);

        builder.finish().build();
    }

    @Override
    protected Node optimize(Node node) {
        return new IntegerConstraintOptimizingVisitor().visit(node);
    }

    @Override
    protected Optional<String> buildExpression(Node node) {
        switch (node.getType()) {
            case VALUE:
                List<IntegerRange> range = ((IntegerRangeValueNode) node).getValue();
                return Optional.of(range.stream().map(this::buildExpression).collect(Collectors.joining(" || ")));
            case ALL_VALUES:
                return Optional.empty();
            default:
                return super.buildExpression(node);
        }
    }

    private String buildExpression(IntegerRange range) {
        long lower = range.getLower();
        long upper = range.getUpper();

        if (lower == upper) {
            return String.format("(value.compareTo(BigInteger.valueOf(%dL)) == 0)", lower);
        } else if (lower == Long.MIN_VALUE) {
            return String.format("(value.compareTo(BigInteger.valueOf(%dL)) <= 0)", upper);
        } else if (upper == Long.MAX_VALUE) {
            return String.format("(value.compareTo(BigInteger.valueOf(%dL)) >= 0)", lower);
        } else {
            return String.format("(value.compareTo(BigInteger.valueOf(%dL)) >= 0 && " +
                    "value.compareTo(BigInteger.valueOf(%dL)) <= 0)", lower, upper);
        }
    }

}
