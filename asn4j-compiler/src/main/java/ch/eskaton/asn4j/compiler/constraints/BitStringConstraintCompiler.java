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
import ch.eskaton.asn4j.compiler.java.JavaClass;
import ch.eskaton.asn4j.compiler.java.JavaClass.BodyBuilder;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.compiler.utils.BitStringUtils;
import ch.eskaton.asn4j.parser.ast.EndpointNode;
import ch.eskaton.asn4j.parser.ast.RangeNode;
import ch.eskaton.asn4j.parser.ast.SetSpecsNode;
import ch.eskaton.asn4j.parser.ast.constraints.Constraint;
import ch.eskaton.asn4j.parser.ast.constraints.ContainedSubtype;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SubtypeConstraint;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.BitStringValue;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;
import ch.eskaton.commons.utils.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.constraints.RangeNodes.getLowerBound;
import static ch.eskaton.asn4j.compiler.constraints.RangeNodes.getUpperBound;
import static ch.eskaton.asn4j.compiler.java.JavaType.BYTE_ARRAY;
import static ch.eskaton.asn4j.compiler.java.JavaType.INT;
import static ch.eskaton.asn4j.compiler.java.JavaVisibility.Protected;

public class BitStringConstraintCompiler extends AbstractConstraintCompiler<BitStringValue, Set<BitStringValue>,
        BitStringConstraint, BitStringConstraintDefinition> {

    public BitStringConstraintCompiler(CompilerContext ctx) {
        super(ctx);
    }

    @Override
    Optional<Bounds> getBounds(Optional<BitStringConstraintDefinition> constraint) {
        return constraint.map(c ->
                new BitStringBounds(getLowerBound(c.getRoots().getSizes().getSizes()),
                        getUpperBound(c.getRoots().getSizes().getSizes())));
    }

    @Override
    protected BitStringConstraintDefinition createDefinition(BitStringConstraint root, BitStringConstraint extension) {
        return new BitStringConstraintDefinition(root, extension);
    }

    @Override
    protected BitStringConstraint createConstraint() {
        return new BitStringConstraint();
    }

    @Override
    protected void addConstraint(JavaClass javaClass, ConstraintDefinition definition) throws CompilerException {
        BitStringConstraintDefinition bitStringConstraintDefinition = (BitStringConstraintDefinition) definition;

        BodyBuilder builder = javaClass.method().annotation("@Override").modifier(Protected)
                .returnType(boolean.class).name("checkConstraint").parameter(BYTE_ARRAY, "value")
                .parameter(INT, "unusedBits")
                .exception(ConstraintViolatedException.class).body();

        if (bitStringConstraintDefinition.isExtensible()) {
            builder.append("return true;");
        } else {
            BitStringConstraint roots = bitStringConstraintDefinition.getRoots();

            addValueConstraint(javaClass, builder, roots.getValues());
            addSizeConstraint(builder, roots.getSizes());

            builder.append("return true;");
        }

        builder.finish().build();
    }

    private void addValueConstraint(JavaClass javaClass, BodyBuilder builder, BitStringValueConstraint constraint) {
        Set<BitStringValue> values = constraint.getValues();
        boolean inverted = constraint.isInverted();

        javaClass.addImport(Arrays.class);

        if (!values.isEmpty()) {
            builder.append("if (" + (inverted ? "" : "!") + "(" + values.stream().map(value ->
                    "Arrays.equals(" + BitStringUtils.getInitializerString(value.getByteValue()) + ", value) && " +
                            value.getUnusedBits() + " == unusedBits").collect(Collectors.joining(" || "))
                    + ")) {");
            builder.append("\treturn false;");
            builder.append("}").nl();
        }
    }

    private void addSizeConstraint(BodyBuilder builder, BitStringSizeConstraint constraint) {
        List<RangeNode> sizes = constraint.getSizes();
        boolean inverted = constraint.isInverted();

        if (!sizes.isEmpty()) {
            builder.append("if (" + (inverted ? "" : "!") + "(" + sizes.stream().map(size ->
                    toLong(size.getLower().getValue()) + "L <= getSize() && " +
                            toLong(size.getUpper().getValue()) + "L >= getSize()")
                    .collect(Collectors.joining(" || ")) + ")) {");
            builder.append("\treturn false;");
            builder.append("}").nl();
        }
    }

    private long toLong(Value value) {
        if (value instanceof IntegerValue) {
            return ((IntegerValue) value).getValue().longValue();
        }

        throw new IllegalStateException("Unresolved");
    }

    @Override
    protected BitStringConstraint calculateElements(Type base, Elements elements, Optional<Bounds> bounds)
            throws CompilerException {
        if (elements instanceof ElementSet) {
            return compileConstraint(base, (ElementSet) elements, bounds);
        } else if (elements instanceof SingleValueConstraint) {
            Value value = ((SingleValueConstraint) elements).getValue();

            try {
                // TODO: implement a more convenient resolver
                BitStringValue bitStringValue = ctx.resolveGenericValue(BitStringValue.class, base, value);

                return new BitStringConstraint(new BitStringValueConstraint(CollectionUtils
                        .asHashSet(bitStringValue)));
            } catch (Exception e) {
                throw new CompilerException("Invalid single-value constraint %s for BIT STRING type", e,
                        value.getClass().getSimpleName());
            }
        } else if (elements instanceof ContainedSubtype) {
            Type type = ((ContainedSubtype) elements).getType();
            Optional<CompiledType> maybeCompiledType = ctx.getCompiledType(type);

            if (maybeCompiledType.isPresent()) {
                CompiledType compiledType = maybeCompiledType.get();
                BitStringConstraintDefinition constraintDefinition =
                        (BitStringConstraintDefinition) compiledType.getConstraintDefinition();

                return constraintDefinition.getRoots();
            } else if (type.equals(base)) {
                return new BitStringConstraint(new BitStringValueConstraint().invert());
            }

            throw new CompilerException("Failed to resolve contained subtype %s", type);
        } else if (elements instanceof ch.eskaton.asn4j.parser.ast.constraints.SizeConstraint) {
            Constraint constraint = ((ch.eskaton.asn4j.parser.ast.constraints.SizeConstraint) elements)
                    .getConstraint();

            if (constraint instanceof SubtypeConstraint) {
                SetSpecsNode setSpecs = ((SubtypeConstraint) constraint).getElementSetSpecs();

                return compileConstraint(base, setSpecs.getRootElements(), bounds);
            } else {
                throw new CompilerException("Constraints of type %s not yet supported",
                        constraint.getClass().getSimpleName());
            }
        } else if (elements instanceof RangeNode) {
            long min = bounds.map(b -> ((BitStringBounds) b).getMinSize()).orElse(Long.MIN_VALUE);
            long max = bounds.map(b -> ((BitStringBounds) b).getMaxSize()).orElse(Long.MAX_VALUE);

            EndpointNode lower = RangeNodes.canonicalizeLowerEndpoint(((RangeNode) elements).getLower(), min);
            EndpointNode upper = RangeNodes.canonicalizeUpperEndpoint(((RangeNode) elements).getUpper(), max);

            return new BitStringConstraint(new BitStringSizeConstraint(new RangeNode(lower, upper)));
        } else {
            throw new CompilerException("Invalid constraint %s for BIT STRING type",
                    elements.getClass().getSimpleName());
        }
    }

}
