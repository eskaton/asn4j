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

package ch.eskaton.asn4j.test.x681;

import ch.eskaton.asn4j.runtime.types.ASN1BitString;
import ch.eskaton.asn4j.runtime.types.ASN1Boolean;
import ch.eskaton.asn4j.runtime.types.ASN1Null;
import ch.eskaton.asn4j.runtime.types.ASN1OctetString;
import ch.eskaton.asn4j.test.modules.X681.TestSequence1;
import ch.eskaton.asn4j.test.modules.X681.TestSequence2;
import ch.eskaton.asn4j.test.modules.X681.TestSequence3;
import ch.eskaton.asn4j.test.modules.X681.TestSequence4;
import ch.eskaton.asn4j.test.modules.X681.TestSequence5;
import ch.eskaton.asn4j.test.modules.X681.TestSequence6;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static ch.eskaton.asn4j.test.TestHelper.testSequenceSuccess;

class TestX681 {

    @Test
    @DisplayName("Verify that the object class field type is resolved to a BIT STRING")
    void testSequence1() {
        testSequenceSuccess(TestSequence1.class, new TestSequence1(),
                s -> s.setBitStringField(ASN1BitString.of(new byte[] { 0x15 })));
    }

    @Test
    @DisplayName("Verify that the object class field type is resolved to a BOOLEAN")
    void testSequence2() {
        testSequenceSuccess(TestSequence2.class, new TestSequence2(), s -> s.setBooleanField(ASN1Boolean.TRUE));
    }

    @Test
    @DisplayName("Verify that the object class field type is resolved to an ENUMERATED type")
    void testSequence3() {
        testSequenceSuccess(TestSequence3.class, new TestSequence3(), s -> s.setEnumField(TestSequence3.EnumField.A));
    }

    @Test
    @DisplayName("Verify that the object class field type is resolved to an INTEGER")
    void testSequence4() {
        testSequenceSuccess(TestSequence4.class, new TestSequence4(), s -> s.setIntField(TestSequence4.IntField.A));
    }

    @Test
    @DisplayName("Verify that the object class field type is resolved to a NULL type")
    void testSequence5() {
        testSequenceSuccess(TestSequence5.class, new TestSequence5(), s -> s.setNullField(new ASN1Null()));
    }

    @Test
    @DisplayName("Verify that the object class field type is resolved to an OCTET STRING")
    void testSequence6() {
        testSequenceSuccess(TestSequence6.class, new TestSequence6(),
                s -> s.setOctetStringField(ASN1OctetString.valueOf(new byte[] { (byte) 0xab })));
    }

}
