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
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.SizeNode;
import ch.eskaton.asn4j.compiler.constraints.ast.ValueNode;
import ch.eskaton.asn4j.compiler.java.JavaClass;
import ch.eskaton.asn4j.compiler.java.JavaClass.BodyBuilder;
import ch.eskaton.asn4j.compiler.utils.BitStringUtils;
import ch.eskaton.asn4j.parser.ast.EndpointNode;
import ch.eskaton.asn4j.parser.ast.RangeNode;
import ch.eskaton.asn4j.parser.ast.SetSpecsNode;
import ch.eskaton.asn4j.parser.ast.constraints.Constraint;
import ch.eskaton.asn4j.parser.ast.constraints.ContainedSubtype;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SizeConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SubtypeConstraint;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.BitStringValue;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;

import java.util.Arrays;
import java.util.Optional;

import static ch.eskaton.asn4j.compiler.constraints.RangeNodes.getLowerBound;
import static ch.eskaton.asn4j.compiler.constraints.RangeNodes.getUpperBound;
import static ch.eskaton.asn4j.compiler.java.JavaType.BYTE_ARRAY;
import static ch.eskaton.asn4j.compiler.java.JavaType.INT;
import static ch.eskaton.asn4j.compiler.java.JavaVisibility.Protected;
import static java.util.Collections.emptyList;

public class BitStringConstraintCompiler extends AbstractConstraintCompiler {

    private static final BoundsVisitor BOUNDS_VISITOR = new BoundsVisitor();

    public BitStringConstraintCompiler(CompilerContext ctx) {
        super(ctx);
    }

    @Override
    Optional<Bounds> getBounds(Optional<ConstraintDefinition> constraint) {
        return constraint.map(c ->
                new BitStringBounds(getLowerBound(BOUNDS_VISITOR.visit(c.getRoots()).orElse(emptyList())),
                        getUpperBound(BOUNDS_VISITOR.visit(c.getRoots()).orElse(emptyList()))));
    }

    private long toLong(Value value) {
        if (value instanceof IntegerValue) {
            return ((IntegerValue) value).getValue().longValue();
        }

        throw new IllegalStateException("Unresolved");
    }

    @Override
    protected Node calculateElements(Type base, Elements elements, Optional<Bounds> bounds)
            throws CompilerException {
        if (elements instanceof ElementSet) {
            return compileConstraint(base, (ElementSet) elements, bounds);
        } else if (elements instanceof SingleValueConstraint) {
            Value value = ((SingleValueConstraint) elements).getValue();

            try {
                // TODO: implement a more convenient resolver
                BitStringValue bitStringValue = ctx.resolveGenericValue(BitStringValue.class, base, value);

                return new ValueNode<>(bitStringValue);
            } catch (Exception e) {
                throw new CompilerException("Invalid single-value constraint %s for BIT STRING type", e,
                        value.getClass().getSimpleName());
            }
        } else if (elements instanceof ContainedSubtype) {
            return calculateContainedSubtype(base, ((ContainedSubtype) elements).getType());
        } else if (elements instanceof SizeConstraint) {
            Constraint constraint = ((SizeConstraint) elements).getConstraint();

            if (constraint instanceof SubtypeConstraint) {
                SetSpecsNode setSpecs = ((SubtypeConstraint) constraint).getElementSetSpecs();

                return compileConstraint(base, setSpecs.getRootElements(), bounds);
            } else {
                throw new CompilerException("Constraints of type %s not yet supported",
                        constraint.getClass().getSimpleName());
            }
        } else if (elements instanceof RangeNode) {
            long min = bounds.map(b -> ((BitStringBounds) b).getMinSize()).orElse(0L);
            long max = bounds.map(b -> ((BitStringBounds) b).getMaxSize()).orElse(Long.MAX_VALUE);

            EndpointNode lower = RangeNodes.canonicalizeLowerEndpoint(((RangeNode) elements).getLower(), min);
            EndpointNode upper = RangeNodes.canonicalizeUpperEndpoint(((RangeNode) elements).getUpper(), max);

            return new SizeNode(new RangeNode(lower, upper));
        } else {
            throw new CompilerException("Invalid constraint %s for BIT STRING type",
                    elements.getClass().getSimpleName());
        }
    }

    @Override
    public void addConstraint(JavaClass javaClass, ConstraintDefinition definition) {
        javaClass.addImport(Arrays.class);

        BodyBuilder builder = javaClass.method().annotation("@Override").modifier(Protected)
                .returnType(boolean.class).name("checkConstraint").parameter(BYTE_ARRAY, "value")
                .parameter(INT, "unusedBits")
                .exception(ConstraintViolatedException.class).body();

        addConstraintCondition(definition, builder);

        builder.finish().build();
    }

    protected Optional<String> buildExpression(Node node) {
        switch (node.getType()) {
            case VALUE:
                BitStringValue value = (BitStringValue) ((ValueNode) node).getValue();
                return Optional.of("(Arrays.equals(" + BitStringUtils.getInitializerString(value.getByteValue()) +
                        ", value) && " + value.getUnusedBits() + " == unusedBits)");
            case ALL_VALUES:
                return Optional.empty();
            case SIZE:
                RangeNode size = ((SizeNode) node).getSize();
                return Optional.of("(" + toLong(size.getLower().getValue()) +
                        "L <= ASN1BitString.getSize(value, unusedBits) && " +
                        toLong(size.getUpper().getValue()) + "L >= ASN1BitString.getSize(value, unusedBits))");
            default:
                return super.buildExpression(node);
        }
    }

}
