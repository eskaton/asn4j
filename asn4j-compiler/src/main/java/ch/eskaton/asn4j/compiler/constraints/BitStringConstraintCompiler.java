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

import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.TypeResolver;
import ch.eskaton.asn4j.compiler.java.JavaClass;
import ch.eskaton.asn4j.compiler.utils.BitStringUtils;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.values.AbstractBaseXStringValue;
import ch.eskaton.asn4j.parser.ast.values.BitStringValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.java.JavaType.BYTE_ARRAY;
import static ch.eskaton.asn4j.compiler.java.JavaType.INT;
import static ch.eskaton.asn4j.compiler.java.JavaVisibility.Protected;

public class BitStringConstraintCompiler extends AbstractConstraintCompiler<BitStringConstraintDefinition> {

    public BitStringConstraintCompiler(TypeResolver typeResolver) {
        super(typeResolver);
    }

    @Override
    protected BitStringConstraintDefinition compileConstraint(ElementSet set) throws CompilerException {
        List<Elements> operands = set.getOperands();

        switch (set.getOperation()) {
            case All:
                return compileConstraint((ElementSet) operands.get(0)).inverted(true);

            case Exclude:
                if (operands.size() == 1) {
                    // ALL EXCEPT
                    return calculateElements(operands.get(0));
                } else {
                    return calculateExclude(calculateElements(operands.get(0)), calculateElements(operands.get(1)));
                }

            case Intersection:
                if (operands.size() == 1) {
                    return calculateElements(operands.get(0));
                }

                return calculateIntersection(operands);
            case Union:
                return calculateUnion(operands);
        }

        return new BitStringConstraintDefinition();
    }

    private BitStringConstraintDefinition calculateExclude(BitStringConstraintDefinition constraintDef1,
            BitStringConstraintDefinition constraintDef2) {
        for (BitStringValue value : constraintDef2.getValues()) {
            if (constraintDef1.getValues().contains(value)) {
                constraintDef1.getValues().remove(value);
            } else {
                throw new CompilerException(value + " doesn't exist in parent type");
            }
        }

        return constraintDef1;
    }

    @Override
    protected BitStringConstraintDefinition calculateIntersection(BitStringConstraintDefinition constraintDef1,
            BitStringConstraintDefinition constraintDef2) throws CompilerException {
        return constraintDef1.intersection(constraintDef2);
    }

    @Override
    protected void addConstraint(JavaClass javaClass, ConstraintDefinition constraintDef) throws CompilerException {
        BitStringConstraintDefinition bitStringConstraintDef = (BitStringConstraintDefinition) constraintDef;
        Set<BitStringValue> values = bitStringConstraintDef.getValues();
        boolean inverted = bitStringConstraintDef.isInverted();

        javaClass.addImport(Arrays.class);

        JavaClass.BodyBuilder builder = javaClass.method().annotation("@Override").modifier(Protected)
                .returnType(boolean.class).name("checkConstraint").parameter(BYTE_ARRAY, "value")
                .parameter(INT, "unusedBits")
                .exception(ConstraintViolatedException.class).body();

        if (!values.isEmpty()) {
            builder.append("if (" + values.stream().map(value ->
                    "Arrays.equals(" + BitStringUtils.getInitializerString(value.getByteValue()) + ", value) && " +
                            value.getUnusedBits() + " == unusedBits").collect(Collectors.joining(" || "))
                    + ") {");
            builder.append("\treturn " + (inverted ? "false" : "true") + ";");
            builder.append("}").nl();
        }

        builder.append("return " + (inverted ? "true" : "false") + ";");

        builder.finish().build();
    }

    private BitStringConstraintDefinition calculateElements(Elements elements) throws CompilerException {
        if (elements instanceof ElementSet) {
            return compileConstraint((ElementSet) elements);
        } else {
            if (elements instanceof SingleValueConstraint) {
                Value value = ((SingleValueConstraint) elements).getValue();

                if (value instanceof AbstractBaseXStringValue) {
                    return new BitStringConstraintDefinition(Arrays.asList(((AbstractBaseXStringValue) value).toBitString()));
                } else {
                    throw new CompilerException("Invalid single-value constraint %s for BIT STRING type",
                            value.getClass().getSimpleName());
                }
            } else {
                throw new CompilerException("Invalid constraint %s for BIT STRING type",
                        elements.getClass().getSimpleName());
            }
        }
    }


    private BitStringConstraintDefinition calculateIntersection(List<Elements> elements) {
        BitStringConstraintDefinition constraintDef = new BitStringConstraintDefinition();

        for (Elements e : elements) {
            BitStringConstraintDefinition values = calculateElements(e);

            if (constraintDef.getValues().isEmpty()) {
                constraintDef.union(values);
            } else {
                constraintDef = calculateIntersection(constraintDef, values);

                if (constraintDef.getValues().isEmpty()) {
                    return constraintDef;
                }
            }
        }

        return constraintDef;
    }

    private BitStringConstraintDefinition calculateUnion(List<Elements> elements) throws CompilerException {
        BitStringConstraintDefinition constraintDef = new BitStringConstraintDefinition();

        for (Elements e : elements) {
            constraintDef = constraintDef.union(calculateElements(e));
        }

        return constraintDef;
    }

}
