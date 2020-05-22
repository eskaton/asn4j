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
package ch.eskaton.asn4j.compiler.java;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.parser.ast.values.BitStringValue;
import ch.eskaton.asn4j.parser.ast.values.EmptyValue;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.OctetStringValue;
import ch.eskaton.asn4j.runtime.types.ASN1BitString;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1OctetString;
import org.junit.jupiter.api.Test;

import static ch.eskaton.asn4j.compiler.java.JavaUtils.getInitializerString;
import static ch.eskaton.asn4j.parser.NoPosition.NO_POSITION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class JavaUtilsTest {

    @Test
    void testBitStringValue() {
        assertEquals("new ASN1BitString(new byte[] { (byte) 0x01, (byte) 0x02, (byte) 0x03 }, 5)",
                JavaUtils.getInitializerString(mock(CompilerContext.class), ASN1BitString.class.getSimpleName(),
                        new BitStringValue(new byte[] { 0x01, 0x02, 0x03 }, 5)));
    }

    @Test
    void testOctetStringValue() {
        assertEquals("new ASN1OctetString(new byte[] { (byte) 0x01, (byte) 0x02, (byte) 0x03 })",
                JavaUtils.getInitializerString(mock(CompilerContext.class), ASN1OctetString.class.getSimpleName(),
                        new OctetStringValue(new byte[] { 0x01, 0x02, 0x03 })));
    }

    @Test
    void testIntegerValue() {
        assertEquals("new ASN1Integer(4711L)", JavaUtils.getInitializerString(mock(CompilerContext.class),
                ASN1Integer.class.getSimpleName(), new IntegerValue(4711)));
    }

    @Test
    void testUnsupportedValue() {
        assertThrows(CompilerException.class, () -> getInitializerString(null, null, new EmptyValue(NO_POSITION)));
    }

}
