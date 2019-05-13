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
import ch.eskaton.asn4j.parser.ast.EndpointNode;
import ch.eskaton.asn4j.parser.ast.RangeNode;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.Value;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RangeNodes {

    /**
     * Calculates the union of two collections of {@link RangeNode}s.
     *
     * @param l1 Collection of {@link RangeNode}s
     * @param l2 Collection of {@link RangeNode}s
     * @return A list containing the union as {@link RangeNode}s
     */
    @SuppressWarnings("unchecked")
    public static List<RangeNode> union(List<RangeNode> l1, List<RangeNode> l2) {
        List<RangeNode> result = new ArrayList<>(l1);

        result.addAll(l2);

        return canonicalizeRanges(result);
    }

    /**
     * Calculates the intersections of two collections of {@link RangeNode}s.
     *
     * @param l1 Collection of {@link RangeNode}s
     * @param l2 Collection of {@link RangeNode}s
     * @return A list containing the intersections as {@link RangeNode}s or an
     * empty list, if there is no intersection
     */
    @SuppressWarnings("unchecked")
    public static List<RangeNode> intersection(List<RangeNode> l1, List<RangeNode> l2) {
        l1 = canonicalizeRanges(l1);
        l2 = canonicalizeRanges(l2);

        int r1Ind = 0;
        int r2Ind = 0;
        RangeNode r1 = getRange(l1, r1Ind++);
        RangeNode r2 = getRange(l2, r2Ind++);
        List<RangeNode> result = new ArrayList<>();

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
                result.add(r1);
                r2 = new RangeNode(increment(r1.getUpper()), r2.getUpper());
                r1 = getRange(l1, r1Ind++);
            } else if (compareCanonicalEndpoint(r2.getLower(), r1.getLower()) >= 0
                    && compareCanonicalEndpoint(r2.getUpper(), r1.getUpper()) <= 0) {
                // r2 included in r1
                result.add(r2);
                r1 = new RangeNode(increment(r2.getUpper()), r1.getUpper());
                r2 = getRange(l2, r2Ind++);
            } else if (compareCanonicalEndpoint(r1.getUpper(), r2.getLower()) >= 0
                    && compareCanonicalEndpoint(r1.getUpper(), r2.getUpper()) <= 0) {
                // r1 < r2 with intersection
                result.add(new RangeNode(r2.getLower(), r1.getUpper()));
                r2 = new RangeNode(increment(r1.getUpper()), r2.getUpper());
                r1 = getRange(l1, r1Ind++);
            } else if (compareCanonicalEndpoint(r2.getUpper(), r1.getLower()) >= 0
                    && compareCanonicalEndpoint(r2.getUpper(), r1.getUpper()) <= 0) {
                // r2 < r1 with intersection
                result.add(new RangeNode(r1.getLower(), r2.getUpper()));
                r1 = new RangeNode(increment(r2.getUpper()), r1.getUpper());
                r2 = getRange(l2, r2Ind++);
            } else {
                throw new IllegalStateException();
            }
        }

        return result;
    }

    public static List<RangeNode> invert(List<RangeNode> ranges) {
        if (ranges.isEmpty()) {
            return ranges;
        }

        ranges = canonicalizeRanges(ranges);

        List<RangeNode> result = new ArrayList<>();
        RangeNode op1 = ranges.get(0);
        RangeNode op2 = op1;

        if (((IntegerValue) op1.getLower().getValue()).getValue()
                .compareTo(BigInteger.valueOf(Long.MIN_VALUE)) > 0) {
            result.add(new RangeNode(new EndpointNode(new IntegerValue(Long.MIN_VALUE), true),
                    decrement(op1.getLower())));
        }

        for (int i = 1; i < ranges.size(); i++) {
            op2 = ranges.get(i);
            result.add(new RangeNode(increment(op1.getUpper()), decrement(op2.getLower())));
        }

        if (((IntegerValue) op2.getUpper().getValue()).getValue()
                .compareTo(BigInteger.valueOf(Long.MAX_VALUE)) < 0) {
            result.add(new RangeNode(increment(op2.getUpper()),
                    new EndpointNode(new IntegerValue(Long.MAX_VALUE), true)));
        }

        return result;
    }

    public static List<RangeNode> exclude(List<RangeNode> r1, List<RangeNode> r2) {
        r1 = canonicalizeRanges(r1);
        r2 = canonicalizeRanges(r2);

        if (r2.isEmpty()) {
            return r1;
        }

        int excludeInd = 0;
        int rangeInd = 0;
        RangeNode exclude = r2.get(excludeInd++);
        RangeNode range = r1.get(rangeInd++);
        long lower, upper;

        List<RangeNode> result = new ArrayList<>();

        while (true) {
            if (compareCanonicalEndpoint(exclude.getLower(), range.getLower()) < 0) {
                throwParentTypeException(exclude.getLower());
            } else if (compareCanonicalEndpoint(exclude.getLower(), range.getUpper()) > 0) {
                result.add(range);
                range = getRange(r1, rangeInd++);
            } else if ((lower = compareCanonicalEndpoint(exclude.getLower(), range.getLower())) >= 0
                    && compareCanonicalEndpoint(exclude.getLower(), range.getUpper()) <= 0) {

                if ((upper = compareCanonicalEndpoint(exclude.getUpper(), range.getUpper())) > 0) {
                    throwParentTypeException(exclude.getUpper());
                }

                if (lower != 0) {
                    result.add(new RangeNode(range.getLower(), decrement(exclude.getLower())));
                }

                if (upper != 0) {
                    result.add(new RangeNode(increment(exclude.getUpper()), range.getUpper()));
                }

                exclude = getRange(r2, excludeInd++);
                range = getRange(r1, rangeInd++);
            } else {
                throw new IllegalStateException();
            }

            if (range == null && exclude != null) {
                throwParentTypeException(exclude.getLower());
            }

            if (exclude == null) {
                while (range != null) {
                    result.add(range);
                    range = getRange(r1, rangeInd++);
                }
                break;
            }
        }

        return result;
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
    static int compareCanonicalEndpoint(EndpointNode e1, EndpointNode e2) {
        BigInteger v1 = ((IntegerValue) e1.getValue()).getValue();
        BigInteger v2 = ((IntegerValue) e2.getValue()).getValue();

        int ret = v1.compareTo(v2);

        if (ret == 0) {
            return ret;
        } else if (ret < 0) {
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
    static int compareCanonicalRange(RangeNode r1, RangeNode r2) {
        int result = compareCanonicalEndpoint(r1.getLower(), r2.getLower());

        if (result != 0) {
            return result;
        }

        return compareCanonicalEndpoint(r1.getUpper(), r2.getUpper());
    }

    public static EndpointNode canonicalizeLowerEndpoint(EndpointNode node, long bound) {
        return canonicalizeEndpoint(node, true, bound);
    }

    public static EndpointNode canonicalizeUpperEndpoint(EndpointNode node, long bound) {
        return canonicalizeEndpoint(node, false, bound);
    }

    /**
     * Canonicalizes an {@link EndpointNode}, i.e. resolves MIN and MAX values
     * and converts the value to inclusive.
     *
     * @param node    An {@link EndpointNode}
     * @param isLower true, if it's a lower {@link EndpointNode}
     * @return a canonical {@link EndpointNode}
     */
    private static EndpointNode canonicalizeEndpoint(EndpointNode node, boolean isLower, long bound) {
        Value v = node.getValue();
        boolean inclusive = node.isInclusive();

        if (Value.MAX.equals(v)) {
            return new EndpointNode(new IntegerValue(inclusive ? bound : bound - 1), true);
        } else if (Value.MIN.equals(v)) {
            return new EndpointNode(new IntegerValue(inclusive ? bound : bound + 1), true);
        } else {
            if (inclusive) {
                return new EndpointNode(v, true);
            }

            return new EndpointNode(new IntegerValue(
                    isLower ? ((IntegerValue) v).getValue().add(BigInteger.ONE)
                            : ((IntegerValue) v).getValue().subtract(BigInteger.ONE)), true);
        }
    }

    /**
     * Sorts the ranges in ascending order and joins adjacent or overlapping
     * ranges.
     *
     * @param ranges Constraint values
     * @return Constraint values containing {@link RangeNode}s in canonical form
     */
    public static List<RangeNode> canonicalizeRanges(List<RangeNode> ranges) {
        if (ranges.size() <= 1) {
            return ranges;
        }

        Collections.sort(ranges, new RangeNodeComparator());

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

        return result;
    }

    private static RangeNode getRange(List<RangeNode> ranges, int ind) {
        if (ranges.size() > ind) {
            return ranges.get(ind);
        }

        return null;
    }

    private static EndpointNode increment(EndpointNode endpoint) {
        return new EndpointNode(new IntegerValue(((IntegerValue) endpoint.getValue()).getValue()
                .add(BigInteger.ONE)), true);
    }

    private static EndpointNode decrement(EndpointNode endpoint) {
        return new EndpointNode(new IntegerValue(((IntegerValue) endpoint.getValue()).getValue()
                .subtract(BigInteger.ONE)), true);
    }

    public static Long getLowerBound(List<RangeNode> ranges) {
        long min = Long.MAX_VALUE;

        for (RangeNode range : ranges) {
            min = Math.min(min, toLong(range.getLower().getValue()));
        }

        return min;
    }

    public static Long getUpperBound(List<RangeNode> ranges) {
        long max = Long.MIN_VALUE;

        for (RangeNode range : ranges) {
            max = Math.max(max, toLong(range.getUpper().getValue()));
        }

        return max;
    }

    private static long toLong(Value value) {
        long num;

        if (Value.MAX.equals(value)) {
            num = Long.MAX_VALUE;
        } else if (Value.MIN.equals(value)) {
            num = Long.MIN_VALUE;
        } else {
            num = ((IntegerValue) value).getValue().longValue();
        }

        return num;
    }

    private static void throwParentTypeException(EndpointNode endpointNode) {
        throw new CompilerException(((IntegerValue)endpointNode.getValue()).getValue()
                + " doesn't exist in parent type");
    }

    private static class RangeNodeComparator implements Comparator<RangeNode> {

        public int compare(RangeNode r1, RangeNode r2) {
            return compareCanonicalRange(r1, r2);
        }

    }

}
