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

package ch.eskaton.asn4j.compiler.constraints.ast;

import ch.eskaton.asn4j.parser.ast.EndpointNode;
import ch.eskaton.asn4j.runtime.utils.ToString;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class IntegerRange {

    private long lower;

    private long upper;

    public IntegerRange(long lower, long upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public long getLower() {
        return lower;
    }

    public long getUpper() {
        return upper;
    }

    /**
     * Calculates the union of two collections of {@link IntegerRange}s.
     *
     * @param l1 Collection of {@link IntegerRange}s
     * @param l2 Collection of {@link IntegerRange}s
     * @return A list containing the union as {@link IntegerRange}s
     */
    @SuppressWarnings("unchecked")
    public static List<IntegerRange> union(List<IntegerRange> l1, List<IntegerRange> l2) {
        List<IntegerRange> result = new ArrayList<>(l1);

        result.addAll(l2);

        return canonicalizeRanges(result);
    }

    public static List<IntegerRange> invert(List<IntegerRange> ranges) {
        if (ranges.isEmpty()) {
            return ranges;
        }

        ranges = canonicalizeRanges(ranges);

        List<IntegerRange> result = new ArrayList<>();
        IntegerRange op1 = ranges.get(0);
        IntegerRange op2 = op1;

        if (op1.getLower() > Long.MIN_VALUE) {
            result.add(new IntegerRange(Long.MIN_VALUE, op1.getLower() - 1));
        }

        for (int i = 1; i < ranges.size(); i++) {
            op2 = ranges.get(i);

            result.add(new IntegerRange(op1.getUpper() + 1, op2.getLower() - 1));
        }

        if (op2.getUpper() < Long.MAX_VALUE) {
            result.add(new IntegerRange(op2.getUpper() + 1, Long.MAX_VALUE));
        }

        return result;
    }

    /**
     * Calculates the intersections of two collections of {@link IntegerRange}s.
     *
     * @param l1 Collection of {@link IntegerRange}s
     * @param l2 Collection of {@link IntegerRange}s
     * @return A list containing the intersections as {@link IntegerRange}s or an
     * empty list, if there is no intersection
     */
    @SuppressWarnings("unchecked")
    public static List<IntegerRange> intersection(List<IntegerRange> l1, List<IntegerRange> l2) {
        l1 = canonicalizeRanges(l1);
        l2 = canonicalizeRanges(l2);

        int r1Ind = 0;
        int r2Ind = 0;
        IntegerRange r1 = getRange(l1, r1Ind++);
        IntegerRange r2 = getRange(l2, r2Ind++);
        List<IntegerRange> result = new ArrayList<>();

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
                r2 = new IntegerRange(r1.getUpper() + 1, r2.getUpper());
                r1 = getRange(l1, r1Ind++);
            } else if (compareCanonicalEndpoint(r2.getLower(), r1.getLower()) >= 0
                    && compareCanonicalEndpoint(r2.getUpper(), r1.getUpper()) <= 0) {
                // r2 included in r1
                result.add(r2);
                r1 = new IntegerRange(r2.getUpper() + 1, r1.getUpper());
                r2 = getRange(l2, r2Ind++);
            } else if (compareCanonicalEndpoint(r1.getUpper(), r2.getLower()) >= 0
                    && compareCanonicalEndpoint(r1.getUpper(), r2.getUpper()) <= 0) {
                // r1 < r2 with intersection
                result.add(new IntegerRange(r2.getLower(), r1.getUpper()));
                r2 = new IntegerRange(r1.getUpper() + 1, r2.getUpper());
                r1 = getRange(l1, r1Ind++);
            } else if (compareCanonicalEndpoint(r2.getUpper(), r1.getLower()) >= 0
                    && compareCanonicalEndpoint(r2.getUpper(), r1.getUpper()) <= 0) {
                // r2 < r1 with intersection
                result.add(new IntegerRange(r1.getLower(), r2.getUpper()));
                r1 = new IntegerRange(r2.getUpper() + 1, r1.getUpper());
                r2 = getRange(l2, r2Ind++);
            } else {
                throw new IllegalStateException();
            }
        }

        return result;
    }

    public static List<IntegerRange> exclude(List<IntegerRange> l1, List<IntegerRange> l2) {
        l1 = canonicalizeRanges(l1);
        l2 = canonicalizeRanges(l2);

        if (l1.isEmpty() || l2.isEmpty()) {
            return l1;
        }

        int excludeInd = 0;
        int rangeInd = 0;

        IntegerRange exclude = getRange(l2, excludeInd++);
        IntegerRange range = getRange(l1, rangeInd++);
        List<IntegerRange> result = new ArrayList<>();

        while (true) {
            if (range == null || exclude == null) {
                break;
            }

            if (compareCanonicalEndpoint(exclude.getUpper(), range.getLower()) < 0) {
                // exclude < range
                exclude = getRange(l2, excludeInd++);
            } else if (compareCanonicalEndpoint(range.getUpper(), exclude.getLower()) < 0) {
                // range < exclude
                result.add(range);
                range = getRange(l1, rangeInd++);
            } else if (compareCanonicalEndpoint(range.getLower(), exclude.getLower()) >= 0
                    && compareCanonicalEndpoint(range.getUpper(), exclude.getUpper()) <= 0) {
                // range included in exclude
                range = getRange(l1, rangeInd++);
            } else if (compareCanonicalEndpoint(exclude.getLower(), range.getLower()) >= 0
                    && compareCanonicalEndpoint(exclude.getUpper(), range.getUpper()) <= 0) {
                // exclude included in range
                if (compareCanonicalEndpoint(exclude.getLower(), range.getLower()) > 0) {
                    result.add(new IntegerRange(range.getLower(), exclude.getLower() - 1));
                }

                if (compareCanonicalEndpoint(range.getUpper(), exclude.getUpper()) > 0) {
                    range = new IntegerRange(exclude.getUpper() + 1, range.getUpper());
                } else {
                    range = getRange(l1, rangeInd++);
                }

                exclude = getRange(l2, excludeInd++);
            } else if (compareCanonicalEndpoint(range.getUpper(), exclude.getLower()) >= 0
                    && compareCanonicalEndpoint(range.getUpper(), exclude.getUpper()) <= 0) {
                // range < exclude with intersection
                result.add(new IntegerRange(range.getLower(), exclude.getLower() - 1));
                range = getRange(l1, rangeInd++);
            } else if (compareCanonicalEndpoint(exclude.getUpper(), range.getLower()) >= 0
                    && compareCanonicalEndpoint(exclude.getUpper(), range.getUpper()) <= 0) {
                // exclude < range with intersection
                range = new IntegerRange(exclude.getUpper() + 1, range.getUpper());
                exclude = getRange(l2, excludeInd++);
            } else {
                throw new IllegalStateException();
            }
        }

        while (range != null) {
            result.add(range);
            range = getRange(l1, rangeInd++);
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
    public static int compareCanonicalEndpoint(long e1, long e2) {
        BigInteger v1 = BigInteger.valueOf(e1);
        BigInteger v2 = BigInteger.valueOf(e2);

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
     * Sorts the ranges in ascending order and joins adjacent or overlapping
     * ranges.
     *
     * @param ranges Constraint values
     * @return Constraint values containing {@link IntegerRange}s in canonical form
     */
    public static List<IntegerRange> canonicalizeRanges(List<IntegerRange> ranges) {
        if (ranges.size() <= 1) {
            return ranges;
        }

        Collections.sort(ranges, new RangeComparator());

        List<IntegerRange> result = new ArrayList<>();
        IntegerRange op1 = ranges.get(0);

        for (int i = 1; i < ranges.size(); i++) {
            IntegerRange op2 = ranges.get(i);

            if (compareCanonicalEndpoint(op2.getLower(), op1.getUpper()) <= 1) {
                // join the two ranges
                if (compareCanonicalEndpoint(op2.getUpper(), op1.getUpper()) > 0) {
                    op1 = new IntegerRange(op1.getLower(), op2.getUpper());
                }
            } else {
                result.add(op1);
                op1 = op2;
            }
        }

        result.add(op1);

        return result;
    }

    /**
     * Compares two canonical {@link IntegerRange}s.
     *
     * @param r1 A range
     * @param r2 Another range
     * @return Returns 0 if the ranges are identical. Returns -1 if {@code r1}
     * has either a smaller lower endpoint than {@code r2} or if both
     * are identical {@code r1}'s upper endpoint is smaller. Returns 1
     * in the opposite case.
     */
    public static int compareCanonicalRange(IntegerRange r1, IntegerRange r2) {
        int result = compareCanonicalEndpoint(r1.getLower(), r2.getLower());

        if (result != 0) {
            return result;
        }

        return compareCanonicalEndpoint(r1.getUpper(), r2.getUpper());
    }

    private static IntegerRange getRange(List<IntegerRange> ranges, int ind) {
        if (ranges.size() > ind) {
            return ranges.get(ind);
        }

        return null;
    }

    public static long getLowerBound(List<IntegerRange> ranges) {
        if (ranges.isEmpty()) {
            return Long.MIN_VALUE;
        }

        long min = Long.MAX_VALUE;

        for (IntegerRange range : ranges) {
            min = Math.min(min, range.getLower());
        }

        return min;
    }

    public static long getUpperBound(List<IntegerRange> ranges) {
        if (ranges.isEmpty()) {
            return Long.MAX_VALUE;
        }

        long max = Long.MIN_VALUE;

        for (IntegerRange range : ranges) {
            max = Math.max(max, range.getUpper());
        }

        return max;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        IntegerRange range = (IntegerRange) other;

        return lower == range.lower && upper == range.upper;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lower, upper);
    }

    @Override
    public String toString() {
        return ToString.get(this);
    }

    private static class RangeComparator implements Comparator<IntegerRange> {

        public int compare(IntegerRange r1, IntegerRange r2) {
            return compareCanonicalRange(r1, r2);
        }

    }

}
