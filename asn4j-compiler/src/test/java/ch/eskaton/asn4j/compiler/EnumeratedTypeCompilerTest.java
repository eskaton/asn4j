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

package ch.eskaton.asn4j.compiler;

import ch.eskaton.asn4j.compiler.results.EnumerationItems;
import ch.eskaton.asn4j.compiler.types.EnumeratedTypeCompiler;
import ch.eskaton.asn4j.parser.ast.EnumerationItemNode;
import ch.eskaton.commons.collections.Tuple2;
import ch.eskaton.commons.utils.StreamsUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static ch.eskaton.asn4j.parser.NoPosition.NO_POSITION;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

class EnumeratedTypeCompilerTest {

    @Test
    void testAddRootItems() {
        CompilerContext context = mock(CompilerContext.class);
        EnumeratedTypeCompiler compiler = new EnumeratedTypeCompiler();
        List<EnumerationItemNode> nodes = asList(createNode("a", 0), createNode("b"), createNode("c", 3));

        EnumerationItems items = compiler.getRootItems(context, "type", nodes);

        assertEquals(0, (int) items.getNumber(0));
        assertEquals(1, (int) items.getNumber(1));
        assertEquals(3, (int) items.getNumber(2));

        nodes = asList(createNode("a", -2), createNode("b"), createNode("c", 0));

        items = compiler.getRootItems(context, "type", nodes);

        assertEquals(-2, (int) items.getNumber(0));
        assertEquals(1, (int) items.getNumber(1));
        assertEquals(0, (int) items.getNumber(2));
    }

    @Test
    void testAdditionalItems() {
        CompilerContext context = mock(CompilerContext.class);
        EnumeratedTypeCompiler compiler = new EnumeratedTypeCompiler();
        List<EnumerationItemNode> rootNodes = asList(createNode("a", 0), createNode("b"), createNode("c", 3));

        EnumerationItems rootItems = compiler.getRootItems(context, "type", rootNodes);

        List<EnumerationItemNode> additionalNodes = asList(createNode("d"), createNode("e", 5), createNode("f", 7),
                createNode("g"));

        EnumerationItems additionalItems = compiler.getAdditionalItems(context, "type", rootItems, additionalNodes);

        assertEquals(4, (int) additionalItems.getNumber(0));
        assertEquals(5, (int) additionalItems.getNumber(1));
        assertEquals(7, (int) additionalItems.getNumber(2));
        assertEquals(8, (int) additionalItems.getNumber(3));

        rootItems = compiler.getRootItems(context, "type", rootNodes);

        additionalNodes = asList(createNode("d"), createNode("e", 4));

        try {
            compiler.getAdditionalItems(context, "type", rootItems, additionalNodes);
            fail("CompilerException expected");
        } catch (CompilerException e) {
            assertThat(e.getMessage(), containsString("Duplicate enumeration value"));
        }
    }

    @Test
    void testGetNextNumber() {
        EnumeratedTypeCompiler compiler = new EnumeratedTypeCompiler();

        assertEquals(0, compiler.getNextNumber(emptyList(), 0));
        assertEquals(0, compiler.getNextNumber(createTuples(1, 2, 3), 0));
        assertEquals(4, compiler.getNextNumber(createTuples(1, 2, 3), 3));
        assertEquals(3, compiler.getNextNumber(createTuples(1, 2, 3), 2));
        assertEquals(10, compiler.getNextNumber(createTuples(1, 9, 2, 5), 3));
    }

    private EnumerationItemNode createNode(String name) {
        return new EnumerationItemNode(NO_POSITION).name(name);
    }

    private EnumerationItemNode createNode(String name, Integer value) {
        return new EnumerationItemNode(NO_POSITION).name(name).number(value);
    }

    private List<Tuple2<String, Integer>> createTuples(int... values) {
        return StreamsUtils.zip(IntStream.rangeClosed('a', 'z').boxed().map(String::valueOf),
                Arrays.stream(values).boxed()).collect(toList());
    }

}
