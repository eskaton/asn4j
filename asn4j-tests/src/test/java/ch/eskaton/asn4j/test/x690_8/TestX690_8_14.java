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

package ch.eskaton.asn4j.test.x690_8;

import ch.eskaton.asn4j.runtime.BEREncoder;
import ch.eskaton.asn4j.runtime.exceptions.ASN1RuntimeException;
import ch.eskaton.asn4j.runtime.types.ASN1VisibleString;
import ch.eskaton.asn4j.test.modules.X690_8.SetType1;
import ch.eskaton.asn4j.test.modules.X690_8.SetType2;
import ch.eskaton.asn4j.test.modules.X690_8.SetType3;
import ch.eskaton.asn4j.test.modules.X690_8.Type2;
import ch.eskaton.asn4j.test.modules.X690_8.Type3;
import ch.eskaton.asn4j.test.modules.X690_8.Type4;
import ch.eskaton.asn4j.test.modules.X690_8.Type5;
import ch.eskaton.asn4j.test.modules.X690_8.Type6;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class TestX690_8_14 {

    @Test
    void testEncodeVisibleString() throws ASN1RuntimeException {
        BEREncoder encoder = new BEREncoder();

        Type2 type2 = new Type2();

        type2.setValue("test");

        assertArrayEquals(new byte[] { 0x43, 0x04, 0x74, 0x65, 0x73, 0x74 }, encoder.encode(type2));

        Type3 type3 = new Type3();

        type3.setValue("test");

        assertArrayEquals(new byte[] { (byte) 0xa2, 0x06, 0x43, 0x04, 0x74, 0x65, 0x73, 0x74 }, encoder.encode(type3));

        Type4 type4 = new Type4();

        type4.setValue("test");

        assertArrayEquals(new byte[] { 0x47, 0x04, 0x74, 0x65, 0x73, 0x74 }, encoder.encode(type4));

        Type5 type5 = new Type5();

        type5.setValue("test");

        assertArrayEquals(new byte[] { (byte) 0xa4, 0x06, 0x43, 0x04, 0x74, 0x65, 0x73, 0x74 }, encoder.encode(type5));

        Type6 type6 = new Type6();

        type6.setValue("test");

        assertArrayEquals(new byte[] { (byte) 0x6b, 0x08, (byte) 0xa2, 0x06, 0x43, 0x04, 0x74, 0x65, 0x73, 0x74 },
                encoder.encode(type6));
    }

    @Test
    void testEncodeSetOf() throws ASN1RuntimeException {
        BEREncoder encoder = new BEREncoder();

        SetType1 type1 = new SetType1();

        type1.setValues(ASN1VisibleString.fromString("test"));

        assertArrayEquals(new byte[] { 0x31, 0x06, 0x1a, 0x04, 0x74, 0x65, 0x73, 0x74 }, encoder.encode(type1));

        SetType2 type2 = new SetType2();

        type2.setValues(ASN1VisibleString.fromString("test"));

        assertArrayEquals(new byte[] { 0x61, 0x06, 0x1a, 0x04, 0x74, 0x65, 0x73, 0x74 }, encoder.encode(type2));

        SetType3 type3 = new SetType3();

        type3.setValues(ASN1VisibleString.fromString("test"));

        assertArrayEquals(new byte[] { (byte) 0xa0, 0x08, 0x61, 0x06, 0x1a, 0x04, 0x74, 0x65, 0x73, 0x74 },
                encoder.encode(type3));
    }

}
