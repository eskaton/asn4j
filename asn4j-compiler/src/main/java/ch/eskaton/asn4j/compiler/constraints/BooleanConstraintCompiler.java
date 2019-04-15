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
import ch.eskaton.asn4j.compiler.java.JavaClass.BodyBuilder;
import ch.eskaton.asn4j.parser.ast.constraints.ContainedSubtype;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.types.BooleanType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.values.BooleanValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.eskaton.asn4j.compiler.java.JavaType.BOOLEAN;
import static ch.eskaton.asn4j.compiler.java.JavaVisibility.Protected;

public class BooleanConstraintCompiler extends AbstractConstraintCompiler<BooleanConstraintDefinition> {

    public BooleanConstraintCompiler(TypeResolver typeResolver) {
        super(typeResolver);
    }

    private final static Set<Boolean> ALL = new HashSet<>(Arrays.asList(Boolean.TRUE, Boolean.FALSE));

    protected BooleanConstraintDefinition compileConstraint(ElementSet set) throws CompilerException {
        List<Elements> operands = set.getOperands();

        switch (set.getOperation()) {
            case All:
                BooleanConstraintDefinition constraintDef = new BooleanConstraintDefinition(ALL);
                constraintDef.getValues().removeAll(compileConstraint((ElementSet) operands.get(0)).getValues());
                return constraintDef;

            case Exclude:
                if (operands.size() == 1) {
                    // ALL EXCEPT
                    return calculateElements(operands.get(0));
                } else {
                    return calculateExclude(calculateElements(operands.get(0)), calculateElements(operands.get(1)));
                }

            case Intersection:
                return calculateIntersection(operands);

            case Union:
                return calculateUnion(operands);
        }

        return new BooleanConstraintDefinition();
    }

    private BooleanConstraintDefinition calculateExclude(BooleanConstraintDefinition constraintDef1,
            BooleanConstraintDefinition constraintDef2) throws CompilerException {
        for (Boolean value : constraintDef2.getValues()) {
            if (constraintDef1.getValues().contains(value)) {
                constraintDef1.getValues().remove(value);
            } else {
                throw new CompilerException(value + " doesn't exist in parent type");
            }
        }

        return constraintDef1;
    }

    private BooleanConstraintDefinition calculateElements(Elements elements) throws CompilerException {
        if (elements instanceof ElementSet) {
            return compileConstraint((ElementSet) elements);
        } else {
            if (elements instanceof SingleValueConstraint) {
                Value value = ((SingleValueConstraint) elements).getValue();
                if (value instanceof BooleanValue) {
                    return new BooleanConstraintDefinition(new HashSet<>(
                            Collections.singletonList(((BooleanValue) value).getValue())));
                } else {
                    throw new CompilerException("Invalid single-value constraint %s for BOOLEAN type",
                            value.getClass().getSimpleName());
                }
            } else if (elements instanceof ContainedSubtype) {
                Type type = ((ContainedSubtype) elements).getType();
                return calculateContainedSubtype(type);
            } else {
                throw new CompilerException("Invalid constraint %s for BOOLEAN type",
                        elements.getClass().getSimpleName());
            }
        }
    }

    private BooleanConstraintDefinition calculateUnion(List<Elements> elements) throws CompilerException {
        BooleanConstraintDefinition constraintDef = new BooleanConstraintDefinition();

        for (Elements e : elements) {
            constraintDef.union(calculateElements(e));
        }

        return constraintDef;
    }

    private BooleanConstraintDefinition calculateIntersection(List<Elements> operands) throws CompilerException {
        BooleanConstraintDefinition constraintDef = new BooleanConstraintDefinition();

        for (Elements e : operands) {
            BooleanConstraintDefinition values = calculateElements(e);

            if (constraintDef.getValues().isEmpty()) {
                constraintDef.union(values);
            } else {
                constraintDef.intersection(values);
                if (constraintDef.getValues().isEmpty()) {
                    return constraintDef;
                }
            }
        }

        return constraintDef;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected BooleanConstraintDefinition calculateIntersection(BooleanConstraintDefinition op1,
            BooleanConstraintDefinition op2) {
        op1.intersection(op2);

        return op1;
    }

    private BooleanConstraintDefinition calculateContainedSubtype(Type type) throws CompilerException {
        if (type instanceof BooleanType) {
            return new BooleanConstraintDefinition(ALL);
        } else if (type instanceof TypeReference) {
            return compileConstraints(type, typeResolver.getBase((TypeReference) type));
        } else {
            throw new CompilerException("Invalid type %s in constraint for BOOLEAN type", type);
        }
    }

    @Override
    public void addConstraint(JavaClass javaClass, ConstraintDefinition constraintDef) {
        Set values = ((BooleanConstraintDefinition) constraintDef).getValues();

        if (values.size() == 2) {
            return;
        }

        BodyBuilder builder = javaClass.method().annotation("@Override").modifier(Protected)
                .returnType(boolean.class).name("checkConstraint").parameter(BOOLEAN, "v")
                .exception(ConstraintViolatedException.class).body();

        if (values.isEmpty()) {
            builder.append("return false;");
        } else {
            builder.append("if(v == ")
                    .append(values.iterator().next())
                    .append(") {")
                    .append("\treturn true;")
                    .append("} else {")
                    .append("\treturn false;")
                    .append("}");
        }

        builder.finish().build();
    }

}
