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

package ch.eskaton.asn4j.parser.ast;

import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import org.junit.jupiter.api.Test;

import static ch.eskaton.asn4j.parser.ast.EndpointNode.canonicalizeLowerEndpoint;
import static ch.eskaton.asn4j.parser.ast.EndpointNode.canonicalizeUpperEndpoint;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EndpointNodeTest {

    @Test
    void testCanonicalizeEndpoints() {
        assertEquals(new IntegerValue(Long.MIN_VALUE),
                canonicalizeLowerEndpoint(new EndpointNode(Value.MIN, true), Long.MIN_VALUE));

        assertEquals(new IntegerValue(Long.MIN_VALUE + 1),
                canonicalizeLowerEndpoint(new EndpointNode(Value.MIN, false), Long.MIN_VALUE));

        assertEquals(new IntegerValue(-12),
                canonicalizeLowerEndpoint(new EndpointNode(Value.MIN, true), -12));

        assertEquals(new IntegerValue(-11),
                canonicalizeLowerEndpoint(new EndpointNode(Value.MIN, false), -12));

        assertEquals(new IntegerValue(Long.MAX_VALUE),
                canonicalizeUpperEndpoint(new EndpointNode(Value.MAX, true), Long.MAX_VALUE));

        assertEquals(new IntegerValue(Long.MAX_VALUE - 1),
                canonicalizeUpperEndpoint(new EndpointNode(Value.MAX, false), Long.MAX_VALUE));

        assertEquals(new IntegerValue(23),
                canonicalizeUpperEndpoint(new EndpointNode(Value.MAX, true), 23));

        assertEquals(new IntegerValue(22),
                canonicalizeUpperEndpoint(new EndpointNode(Value.MAX, false), 23));

        assertEquals(new IntegerValue(-15),
                canonicalizeLowerEndpoint(new EndpointNode(new IntegerValue(-15), true), Long.MIN_VALUE));

        assertEquals(new IntegerValue(-14),
                canonicalizeLowerEndpoint(new EndpointNode(new IntegerValue(-15), false), Long.MIN_VALUE));

        assertEquals(new IntegerValue(15),
                canonicalizeUpperEndpoint(new EndpointNode(new IntegerValue(15), true), Long.MAX_VALUE));

        assertEquals(new IntegerValue(14),
                canonicalizeUpperEndpoint(new EndpointNode(new IntegerValue(15), false), Long.MAX_VALUE));
    }

}
