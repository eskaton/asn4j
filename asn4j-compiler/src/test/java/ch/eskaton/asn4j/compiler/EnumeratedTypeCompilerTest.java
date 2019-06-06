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

import ch.eskaton.asn4j.compiler.EnumeratedTypeCompiler.EnumerationItems;
import ch.eskaton.asn4j.parser.ast.EnumerationItemNode;
import org.junit.Test;

import java.util.List;

import static ch.eskaton.asn4j.parser.NoPosition.NO_POSITION;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.mockito.Mockito.mock;

public class EnumeratedTypeCompilerTest {

    @Test
    public void testAddRootItems() {
        CompilerContext context = mock(CompilerContext.class);
        EnumeratedTypeCompiler compiler = new EnumeratedTypeCompiler();
        EnumerationItems items = new EnumerationItems();
        List<EnumerationItemNode> nodes = asList(createNode("a", 0), createNode("b"), createNode("c", 3));

        compiler.addRootItems(context, "type", items, nodes);

        assertEquals(0, (int) items.getNumbers().get(0));
        assertEquals(1, (int) items.getNumbers().get(1));
        assertEquals(3, (int) items.getNumbers().get(2));

        items = new EnumerationItems();
        nodes = asList(createNode("a", -2), createNode("b"), createNode("c", 0));

        compiler.addRootItems(context, "type", items, nodes);

        assertEquals(-2, (int) items.getNumbers().get(0));
        assertEquals(1, (int) items.getNumbers().get(1));
        assertEquals(0, (int) items.getNumbers().get(2));
    }

    @Test
    public void testAdditionalItems() {
        CompilerContext context = mock(CompilerContext.class);
        EnumeratedTypeCompiler compiler = new EnumeratedTypeCompiler();
        EnumerationItems items = new EnumerationItems();
        List<EnumerationItemNode> rootNodes = asList(createNode("a", 0), createNode("b"), createNode("c", 3));

        compiler.addRootItems(context, "type", items, rootNodes);

        List<EnumerationItemNode> additionalNodes = asList(createNode("d"), createNode("e", 5), createNode("f", 7),
                createNode("g"));

        compiler.addAdditionalItems(context, "type", items, additionalNodes);

        assertEquals(4, (int) items.getNumbers().get(3));
        assertEquals(5, (int) items.getNumbers().get(4));
        assertEquals(7, (int) items.getNumbers().get(5));
        assertEquals(8, (int) items.getNumbers().get(6));

        items = new EnumerationItems();

        compiler.addRootItems(context, "type", items, rootNodes);

        additionalNodes = asList(createNode("d"), createNode("e", 4));

        try {
            compiler.addAdditionalItems(context, "type", items, additionalNodes);
            fail("CompilerException expected");
        } catch (CompilerException e) {
            assertThat(e.getMessage(), containsString("Duplicate enumeration value"));
        }
    }

    @Test
    public void testGetNextNumber() {
        EnumeratedTypeCompiler compiler = new EnumeratedTypeCompiler();

        assertEquals(4, (int) compiler.getNextNumber(asList(1, 2, 3), 3));
        assertEquals(3, (int) compiler.getNextNumber(asList(1, 2, 3), 2));
        assertEquals(10, (int) compiler.getNextNumber(asList(1, 9, 2, 5), 3));
    }

    private EnumerationItemNode createNode(String name) {
        return new EnumerationItemNode(NO_POSITION).name(name);
    }

    private EnumerationItemNode createNode(String name, Integer value) {
        return new EnumerationItemNode(NO_POSITION).name(name).number(value);
    }

}
