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

import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.values.BitStringValue;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.constraints.BitStringTestUtils.toBString;
import static ch.eskaton.asn4j.compiler.constraints.BitStringTestUtils.toBitString;
import static ch.eskaton.asn4j.compiler.constraints.BitStringTestUtils.toBitStringSet;
import static ch.eskaton.asn4j.parser.NoPosition.NO_POSITION;
import static ch.eskaton.commons.utils.CollectionUtils.asHashSet;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.junit.Assert.assertEquals;

public class BitStringConstraintCompilerTest {

    @Test
    public void testCalculateUnion() {
        BitStringConstraintCompiler compiler = new BitStringConstraintCompiler(null);

        assertEquals(emptySet(), invokeCalculateUnion(compiler));
        assertEquals(asHashSet(toBitString("0001")), invokeCalculateUnion(compiler, "0001"));
        assertEquals(asHashSet(toBitString("0001"), toBitString("0010")),
                invokeCalculateUnion(compiler, "0001", "0010"));
        assertEquals(asHashSet(toBitString("0001"), toBitString("0010")),
                invokeCalculateUnion(compiler, "0001", "0010", "0001"));
    }

    @Test
    public void testCalculateIntersection() {
        BitStringConstraintCompiler compiler = new BitStringConstraintCompiler(null);

        assertEquals(emptySet(), invokeCalculateIntersection(compiler));
        assertEquals(emptySet(), invokeCalculateIntersection(compiler, asList("0001"), asList()));
        assertEquals(toBitStringSet("0001"), invokeCalculateIntersection(compiler, asList("0001")));
        assertEquals(toBitStringSet("0010"), invokeCalculateIntersection(compiler, asList("0001", "0010"),
                asList("0010", "0011")));
        assertEquals(toBitStringSet("0010"), invokeCalculateIntersection(compiler, asList("0001", "0010"),
                asList("0010", "0011", "0010")));
        assertEquals(toBitStringSet("0010", "0100"), invokeCalculateIntersection(compiler,
                asList("0001", "0010", "0100"), asList("0010", "0011", "0100")));
    }

    private Set<BitStringValue> invokeCalculateUnion(BitStringConstraintCompiler compiler, String... elements) {
        return compiler.calculateUnion(toElements(elements)).getValues();
    }

    private Set<BitStringValue> invokeCalculateIntersection(BitStringConstraintCompiler compiler, List<String>... elements) {
        return compiler.calculateIntersection(toElements(elements)).getValues();
    }

    private List<Elements> toElements(String... values) {
        List<Elements> elements = new ArrayList<>();

        for (String value : values) {
            elements.add(new SingleValueConstraint(NO_POSITION, toBString(value)));
        }

        return elements;
    }

    private List<Elements> toElements(List<String>... values) {
        List<Elements> elements = new ArrayList<>();

        for (List<String> value : values) {
            elements.add(new ElementSet(NO_POSITION, ElementSet.OpType.Union, value.stream()
                    .map(BitStringTestUtils::toBString).map(
                            v -> new SingleValueConstraint(NO_POSITION, v)).collect(Collectors.toList())));
        }

        return elements;
    }

}
