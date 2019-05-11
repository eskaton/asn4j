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
import ch.eskaton.asn4j.parser.ast.EndpointNode;
import ch.eskaton.asn4j.parser.ast.RangeNode;
import ch.eskaton.asn4j.parser.ast.constraints.ContainedSubtype;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static ch.eskaton.asn4j.compiler.constraints.RangeNodes.canonicalizeRanges;
import static ch.eskaton.asn4j.compiler.java.JavaVisibility.Protected;

public class IntegerConstraintCompiler extends AbstractConstraintCompiler<RangeNode, List<RangeNode>, IntegerConstraintValues, IntegerConstraintDefinition> {

    public IntegerConstraintCompiler(CompilerContext ctx) {
        super(ctx);
    }

    @Override
    protected IntegerConstraintDefinition createDefinition(IntegerConstraintValues root, IntegerConstraintValues extension) {
        return new IntegerConstraintDefinition(root, extension);
    }

    @Override
    protected IntegerConstraintValues createValues() {
        return new IntegerConstraintValues();
    }

    @Override
    protected IntegerConstraintValues calculateElements(Type base, Elements elements) throws CompilerException {
        if (elements instanceof ElementSet) {
            return compileConstraint(base, (ElementSet) elements);
        } else {
            if (elements instanceof SingleValueConstraint) {
                Value value = ((SingleValueConstraint) elements).getValue();
                if (value instanceof IntegerValue) {
                    if (((IntegerValue) value).isReference()) {
                        // TODO: resolve
                        throw new CompilerException("not yet supported");
                    } else {
                        return new IntegerConstraintValues(Arrays.asList(new RangeNode(
                                new EndpointNode(value, true),
                                new EndpointNode(value, true))));
                    }
                } else {
                    throw new CompilerException("Invalid single-value constraint %s for INTEGER type",
                            value.getClass().getSimpleName());
                }
            } else if (elements instanceof ContainedSubtype) {
                Type type = ((ContainedSubtype) elements).getType();
                return calculateContainedSubtype(type);
            } else if (elements instanceof RangeNode) {
                EndpointNode lower = RangeNodes
                        .canonicalizeEndpoint(((RangeNode) elements).getLower(), true);
                EndpointNode upper = RangeNodes
                        .canonicalizeEndpoint(((RangeNode) elements).getUpper(), false);
                return new IntegerConstraintValues(Arrays.asList(new RangeNode(lower, upper)));
            } else {
                throw new CompilerException("Invalid constraint %s for INTEGER type",
                        elements.getClass().getSimpleName());
            }
        }
    }

    private IntegerConstraintValues calculateContainedSubtype(Type type) throws CompilerException {
        IntegerConstraintValues values = new IntegerConstraintValues();

        if (type instanceof ASN1Integer) {
            // no restriction
            return values;
        } else if (type instanceof TypeReference) {
            return compileConstraints(type, ctx.getBase((TypeReference) type)).getRootValues();
        } else {
            throw new CompilerException("Invalid type %s in constraint for INTEGER type", type);
        }
    }

    /**
     * Calculates the union of a list of {@link Elements}.
     *
     * @param base Base type
     * @param elements A list of {@link Elements}s.
     * @return A list with the union of {@link RangeNode}s
     */
    protected IntegerConstraintValues calculateUnion(Type base, List<Elements> elements) throws CompilerException {
        return new IntegerConstraintValues(canonicalizeRanges(super.calculateUnion(base, elements).getValues()));
    }

    @Override
    public void addConstraint(JavaClass javaClass, ConstraintDefinition definition)
            throws CompilerException {
        BodyBuilder body = javaClass.method().annotation(Override.class).modifier(Protected).returnType(boolean.class)
                .name("checkConstraint").parameter("BigInteger", "v")
                .exception(ConstraintViolatedException.class).body();

        body.append("if(");

        boolean first = true;

        IntegerConstraintValues rootValues = ((IntegerConstraintDefinition) definition).getRootValues();
        IntegerConstraintValues extensionValues = ((IntegerConstraintDefinition) definition).getExtensionValues();
        IntegerConstraintValues union = new IntegerConstraintValues(canonicalizeRanges(rootValues
                .union(extensionValues).getValues()));

        for (RangeNode value : union.getValues()) {
            RangeNode range = value;
            BigInteger lower = ((IntegerValue) range.getLower().getValue()).getValue();
            BigInteger upper = ((IntegerValue) range.getUpper().getValue()).getValue();

            if (!first) {
                body.append(" || ");
            }

            // TODO: 64 Bit unsigned
            if (lower == upper) {
                body.append(String.format("(v.compareTo(BigInteger.valueOf(%dL)) == 0)", lower));
            } else {
                if (lower.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) == 0) {
                    body.append(String.format("(v.compareTo(BigInteger.valueOf(%dL)) <= 0)", upper));
                } else if (upper.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) == 0) {
                    body.append(String.format("(v.compareTo(BigInteger.valueOf(%dL)) >= 0)", lower));
                } else {
                    body.append(String
                            .format("(v.compareTo(BigInteger.valueOf(%dL)) >= 0 && v.compareTo(BigInteger.valueOf(%dL)) <= 0)",
                                    lower, upper));
                }
            }

            first = false;
        }

        body.append(") {");
        body.append("\treturn true;");
        body.append("} else {");
        body.append("\treturn false;");
        body.append("}");

        body.finish().build();

        javaClass.addImport(BigInteger.class);
    }

}
