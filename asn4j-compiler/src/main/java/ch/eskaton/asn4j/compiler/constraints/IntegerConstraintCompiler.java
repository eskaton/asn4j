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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static ch.eskaton.asn4j.compiler.java.JavaVisibility.Protected;

public class IntegerConstraintCompiler extends AbstractConstraintCompiler<IntegerConstraintDefinition> {

    public IntegerConstraintCompiler(TypeResolver typeResolver) {
        super(typeResolver);
    }

    protected IntegerConstraintDefinition compileConstraint(ElementSet set) throws CompilerException {
        List<Elements> operands = set.getOperands();

        switch (set.getOperation()) {
            case All:
                return calculateInversion(compileConstraint((ElementSet) operands.get(0)));

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

        return new IntegerConstraintDefinition();
    }

    private IntegerConstraintDefinition calculateElements(Elements elements) throws CompilerException {
        if (elements instanceof ElementSet) {
            return compileConstraint((ElementSet) elements);
        } else {
            if (elements instanceof SingleValueConstraint) {
                Value value = ((SingleValueConstraint) elements).getValue();
                if (value instanceof IntegerValue) {
                    if (((IntegerValue) value).isReference()) {
                        // TODO: resolve
                        throw new CompilerException("not yet supported");
                    } else {
                        return new IntegerConstraintDefinition(Arrays.asList(new RangeNode(
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
                EndpointNode lower = canonicalizeEndpoint(((RangeNode) elements).getLower(), true);
                EndpointNode upper = canonicalizeEndpoint(((RangeNode) elements).getUpper(), false);
                return new IntegerConstraintDefinition(Arrays.asList(new RangeNode(lower, upper)));
            } else {
                throw new CompilerException("Invalid constraint %s for INTEGER type",
                        elements.getClass().getSimpleName());
            }
        }
    }

    private IntegerConstraintDefinition calculateInversion(IntegerConstraintDefinition constraintDef) {
        IntegerConstraintDefinition result = new IntegerConstraintDefinition();
        List<RangeNode> ranges = constraintDef.getRanges();
        RangeNode op1 = ranges.get(0);
        RangeNode op2 = op1;

        if (((IntegerValue) op1.getLower().getValue()).getValue().compareTo(
                BigInteger.valueOf(Long.MIN_VALUE)) > 0) {
            result.getRanges().add(new RangeNode(new EndpointNode(new IntegerValue(
                    Long.MIN_VALUE), true), decrement(op1.getLower())));
        }

        for (int i = 1; i < ranges.size(); i++) {
            op2 = ranges.get(i);
            result.getRanges().add(new RangeNode(increment(op1.getUpper()), decrement(op2.getLower())));
        }

        if (((IntegerValue) op2.getUpper().getValue()).getValue().compareTo(
                BigInteger.valueOf(Long.MAX_VALUE)) < 0) {
            result.getRanges().add(new RangeNode(increment(op2.getUpper()),
                    new EndpointNode(new IntegerValue(Long.MAX_VALUE), true)));
        }

        return result;
    }

    private IntegerConstraintDefinition calculateExclude(IntegerConstraintDefinition constraintDef1,
            IntegerConstraintDefinition constraintDef2) throws CompilerException {
        IntegerConstraintDefinition result = new IntegerConstraintDefinition();
        int excludeInd = 0;
        int rangeInd = 0;
        List<RangeNode> r1 = constraintDef1.getRanges();
        List<RangeNode> r2 = constraintDef2.getRanges();
        RangeNode exclude = r2.get(excludeInd++);
        RangeNode range = r1.get(rangeInd++);
        long lower, upper;

        while (true) {
            if (compareCanonicalEndpoint(exclude.getLower(), range.getLower()) < 0) {
                throw new CompilerException(((IntegerValue) exclude.getLower().getValue()).getValue()
                        + " doesn't exist in parent type");
            } else if (compareCanonicalEndpoint(exclude.getLower(), range.getUpper()) > 0) {
                result.getRanges().add(range);
                range = getRange(r1, rangeInd++);
            } else if ((lower = compareCanonicalEndpoint(exclude.getLower(), range.getLower())) >= 0
                    && compareCanonicalEndpoint(exclude.getLower(), range.getUpper()) <= 0) {

                if ((upper = compareCanonicalEndpoint(exclude.getUpper(), range.getUpper())) > 0) {
                    throw new CompilerException(((IntegerValue) exclude.getUpper().getValue()).getValue()
                            + " doesn't exist in parent type");
                }

                if (lower != 0) {
                    result.getRanges().add(new RangeNode(range.getLower(), decrement(exclude.getLower())));
                }

                if (upper != 0) {
                    result.getRanges().add(new RangeNode(increment(exclude.getUpper()), range.getUpper()));
                }

                exclude = getRange(r2, excludeInd++);
                range = getRange(r1, rangeInd++);
            } else {
                throw new IllegalStateException();
            }

            if (range == null && exclude != null) {
                throw new CompilerException(((IntegerValue) exclude.getLower().getValue()).getValue()
                        + " doesn't exist in parent type");
            }

            if (exclude == null) {
                while (range != null) {
                    result.getRanges().add(range);
                    range = getRange(r1, rangeInd++);
                }
                break;
            }

        }

        return result;
    }

    private RangeNode getRange(List<RangeNode> ranges, int ind) {
        if (ranges.size() > ind) {
            return ranges.get(ind);
        }

        return null;
    }

    private EndpointNode increment(EndpointNode endpoint) {
        return new EndpointNode(new IntegerValue(
                ((IntegerValue) endpoint.getValue()).getValue().add(
                        BigInteger.ONE)), true);
    }

    private EndpointNode decrement(EndpointNode endpoint) {
        return new EndpointNode(new IntegerValue(
                ((IntegerValue) endpoint.getValue()).getValue().subtract(
                        BigInteger.ONE)), true);
    }

    /**
     * Sorts the ranges in ascending order and joins adjacent or overlapping
     * ranges.
     *
     * @param constraintDef A constraint definition
     * @return A constraint definition containing {@link RangeNode}s in canonical form
     */
    private IntegerConstraintDefinition canonicalizeRanges(IntegerConstraintDefinition constraintDef) {
        if (constraintDef.getRanges().size() <= 1) {
            return constraintDef;
        }

        List<RangeNode> ranges = constraintDef.getRanges();

        Collections.sort(ranges, new ASN1RangeComparator());

        List<RangeNode> result = new ArrayList<>();
        RangeNode op1 = ranges.get(0);

        for (int i = 1; i < ranges.size(); i++) {
            RangeNode op2 = ranges.get(i);

            if (compareCanonicalEndpoint(op2.getLower(), op1.getUpper()) <= 1) {
                // join the two ranges
                if (compareCanonicalEndpoint(op2.getUpper(), op1.getUpper()) > 0) {
                    op1 = new RangeNode(op1.getLower(), op2.getUpper());
                }
            } else {
                result.add(op1);
                op1 = op2;
            }
        }

        result.add(op1);

        constraintDef.setRanges(result);

        return constraintDef;
    }

    private IntegerConstraintDefinition calculateContainedSubtype(Type type) throws CompilerException {
        IntegerConstraintDefinition constraintDef = new IntegerConstraintDefinition();

        if (type instanceof ASN1Integer) {
            // no restriction
            return constraintDef;
        } else if (type instanceof TypeReference) {
            return compileConstraints(type, typeResolver.getBase((TypeReference) type));
        } else {
            throw new CompilerException("Invalid type %s in constraint for INTEGER type", type);
        }
    }

    /**
     * Calculates the union of a list of {@link Elements}.
     *
     * @param elements A list of {@link Elements}s.
     * @return A list with the union of {@link RangeNode}s
     */
    private IntegerConstraintDefinition calculateUnion(List<Elements> elements) throws CompilerException {
        IntegerConstraintDefinition constraintDef = new IntegerConstraintDefinition();

        for (Elements e : elements) {
            constraintDef.union(calculateElements(e));
        }

        return canonicalizeRanges(constraintDef);
    }

    /**
     * Calculates the intersection of a list of {@link Elements}.
     *
     * @param elements A list of {@link Elements}s.
     * @return A list containing the intersections as {@link RangeNode}s or an
     * empty list, if there is no intersection
     */
    private IntegerConstraintDefinition calculateIntersection(List<Elements> elements) throws CompilerException {
        IntegerConstraintDefinition constraintDef = new IntegerConstraintDefinition();

        for (Elements e : elements) {
            IntegerConstraintDefinition values = calculateElements(e);

            if (constraintDef.getRanges().isEmpty()) {
                constraintDef.union(values);
            } else {
                constraintDef = calculateIntersection(constraintDef, values);
                if (constraintDef.getRanges().isEmpty()) {
                    return constraintDef;
                }
            }
        }

        return constraintDef;
    }

    /**
     * Calculates the intersections of two collections of {@link RangeNode}s.
     * The lists must have been prepared with
     * {@link IntegerConstraintCompiler#canonicalizeRanges}.
     *
     * @param op1 Collection of {@link RangeNode}s
     * @param constraintDef2 Collection of {@link RangeNode}s
     * @return A list containing the intersections as {@link RangeNode}s or an
     * empty list, if there is no intersection
     */
    @SuppressWarnings("unchecked")
    @Override
    protected IntegerConstraintDefinition calculateIntersection(IntegerConstraintDefinition op1,
            IntegerConstraintDefinition constraintDef2) throws CompilerException {
        IntegerConstraintDefinition result = new IntegerConstraintDefinition();
        int r1Ind = 0;
        int r2Ind = 0;
        List<RangeNode> l1 = op1.getRanges();
        List<RangeNode> l2 = constraintDef2.getRanges();
        RangeNode r1 = getRange(l1, r1Ind++);
        RangeNode r2 = getRange(l2, r2Ind++);

        while (true) {
            if (r1 == null || r2 == null) {
                break;
            }

            if (compareCanonicalEndpoint(r1.getUpper(), r2.getLower()) < 0) {
                // r1 < r2
                r1 = getRange(l1, r1Ind++);
            } else if (compareCanonicalEndpoint(r2.getUpper(), r1.getLower()) < 0) {
                // r2 < r1
                r2 = getRange(l2, r2Ind++);
            } else if (compareCanonicalEndpoint(r1.getLower(), r2.getLower()) >= 0
                    && compareCanonicalEndpoint(r1.getUpper(), r2.getUpper()) <= 0) {
                // r1 included in r2
                result.getRanges().add(r1);
                r2 = new RangeNode(increment(r1.getUpper()), r2.getUpper());
                r1 = getRange(l1, r1Ind++);
            } else if (compareCanonicalEndpoint(r2.getLower(), r1.getLower()) >= 0
                    && compareCanonicalEndpoint(r2.getUpper(), r1.getUpper()) <= 0) {
                // r2 included in r1
                result.getRanges().add(r2);
                r1 = new RangeNode(increment(r2.getUpper()), r1.getUpper());
                r2 = getRange(l2, r2Ind++);
            } else if (compareCanonicalEndpoint(r1.getUpper(), r2.getLower()) >= 0
                    && compareCanonicalEndpoint(r1.getUpper(), r2.getUpper()) <= 0) {
                // r1 < r2 with intersection
                result.getRanges().add(new RangeNode(r2.getLower(), r1.getUpper()));
                r2 = new RangeNode(increment(r1.getUpper()), r2.getUpper());
                r1 = getRange(l1, r1Ind++);
            } else if (compareCanonicalEndpoint(r2.getUpper(), r1.getLower()) >= 0
                    && compareCanonicalEndpoint(r2.getUpper(), r1.getUpper()) <= 0) {
                // r2 < r1 with intersection
                result.getRanges().add(new RangeNode(r1.getLower(), r2.getUpper()));
                r1 = new RangeNode(increment(r2.getUpper()), r1.getUpper());
                r2 = getRange(l2, r2Ind++);
            } else {
                throw new IllegalStateException();
            }
        }

        return result;
    }

    /**
     * Canonicalizes an {@link EndpointNode}, i.e. resolves MIN and MAX values
     * and converts the value to inclusive.
     *
     * @param e       An {@link EndpointNode}
     * @param isLower true, if it's a lower {@link EndpointNode}
     * @return a canonical {@link EndpointNode}
     */
    private EndpointNode canonicalizeEndpoint(EndpointNode e, boolean isLower) {
        Value v = e.getValue();
        boolean inclusive = e.isInclusive();

        if (Value.MAX.equals(v)) {
            return new EndpointNode(new IntegerValue(inclusive ? Long.MAX_VALUE
                    : Long.MAX_VALUE - 1), true);
        } else if (Value.MIN.equals(v)) {
            return new EndpointNode(new IntegerValue(inclusive ? Long.MIN_VALUE
                    : Long.MIN_VALUE + 1), true);
        } else {
            if (inclusive) {
                return new EndpointNode(v, true);
            }
            return new EndpointNode(new IntegerValue(
                    isLower ? ((IntegerValue) v).getValue().add(BigInteger.ONE)
                            : ((IntegerValue) v).getValue().subtract(
                            BigInteger.ONE)), true);
        }
    }

    /**
     * Compares canonical integer endpoints.
     *
     * @param e1 An {@link EndpointNode}
     * @param e2 An {@link EndpointNode}
     * @return 0, if they are equal, a positive int if e1 is bigger than e2, a
     * negative int otherwise. The int is 1/-1 if the numbers are
     * adjacent, 2/-2 otherwise.
     */
    private static int compareCanonicalEndpoint(EndpointNode e1, EndpointNode e2) {
        BigInteger v1 = ((IntegerValue) e1.getValue()).getValue();
        BigInteger v2 = ((IntegerValue) e2.getValue()).getValue();

        int ret = v1.compareTo(v2);

        if (ret == 0) {
            return ret;
        } else if (ret == -1) {
            if (v1.compareTo(v2.subtract(BigInteger.ONE)) == 0) {
                return -1;
            } else {
                return -2;
            }
        } else {
            if (v1.compareTo(v2.add(BigInteger.ONE)) == 0) {
                return 1;
            } else {
                return 2;
            }
        }
    }

    /**
     * Compares two canonical {@link RangeNode}s.
     *
     * @param r1 A range
     * @param r2 Another range
     * @return Returns 0 if the ranges are identical. Returns -1 if {@code r1}
     * has either a smaller lower endpoint than {@code r2} or if both
     * are identical {@code r1}'s upper endpoint is smaller. Returns 1
     * in the opposite case.
     */
    private static int compareCanonicalRange(RangeNode r1, RangeNode r2) {
        int result = compareCanonicalEndpoint(r1.getLower(), r2.getLower());

        if (result != 0) {
            return result;
        }

        return compareCanonicalEndpoint(r1.getUpper(), r2.getUpper());
    }

    @Override
    public void addConstraint(JavaClass clazz, ConstraintDefinition constraintDef)
            throws CompilerException {
        BodyBuilder body = clazz.method().annotation(Override.class).modifier(Protected).returnType(boolean.class)
                .name("checkConstraint").parameter("BigInteger", "v")
                .exception(ConstraintViolatedException.class).body();

        body.append("if(");

        boolean first = true;

        for (Object value : ((IntegerConstraintDefinition) constraintDef).getRanges()) {
            if (!(value instanceof RangeNode)) {
                throw new CompilerException("Invalid type %s in INTEGER constraint", value.getClass());
            }

            RangeNode range = (RangeNode) value;
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

        clazz.addImport(BigInteger.class);
    }

    private class ASN1RangeComparator implements Comparator<RangeNode> {

        public int compare(RangeNode r1, RangeNode r2) {
            return IntegerConstraintCompiler.compareCanonicalRange(r1, r2);
        }

    }

}
