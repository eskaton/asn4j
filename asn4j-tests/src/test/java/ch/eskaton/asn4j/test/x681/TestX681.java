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

import ch.eskaton.asn4j.runtime.types.ASN1BMPString;
import ch.eskaton.asn4j.runtime.types.ASN1BitString;
import ch.eskaton.asn4j.runtime.types.ASN1Boolean;
import ch.eskaton.asn4j.runtime.types.ASN1GeneralString;
import ch.eskaton.asn4j.runtime.types.ASN1GraphicString;
import ch.eskaton.asn4j.runtime.types.ASN1IA5String;
import ch.eskaton.asn4j.runtime.types.ASN1IRI;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1Null;
import ch.eskaton.asn4j.runtime.types.ASN1NumericString;
import ch.eskaton.asn4j.runtime.types.ASN1ObjectIdentifier;
import ch.eskaton.asn4j.runtime.types.ASN1OctetString;
import ch.eskaton.asn4j.runtime.types.ASN1PrintableString;
import ch.eskaton.asn4j.runtime.types.ASN1RelativeIRI;
import ch.eskaton.asn4j.runtime.types.ASN1RelativeOID;
import ch.eskaton.asn4j.runtime.types.ASN1TeletexString;
import ch.eskaton.asn4j.runtime.types.ASN1UTF8String;
import ch.eskaton.asn4j.runtime.types.ASN1UniversalString;
import ch.eskaton.asn4j.runtime.types.ASN1VideotexString;
import ch.eskaton.asn4j.runtime.types.ASN1VisibleString;
import ch.eskaton.asn4j.test.modules.X681.TestSequence1;
import ch.eskaton.asn4j.test.modules.X681.TestSequence10;
import ch.eskaton.asn4j.test.modules.X681.TestSequence11;
import ch.eskaton.asn4j.test.modules.X681.TestSequence12;
import ch.eskaton.asn4j.test.modules.X681.TestSequence13;
import ch.eskaton.asn4j.test.modules.X681.TestSequence14;
import ch.eskaton.asn4j.test.modules.X681.TestSequence15;
import ch.eskaton.asn4j.test.modules.X681.TestSequence16;
import ch.eskaton.asn4j.test.modules.X681.TestSequence17;
import ch.eskaton.asn4j.test.modules.X681.TestSequence18;
import ch.eskaton.asn4j.test.modules.X681.TestSequence18.SequenceOfField;
import ch.eskaton.asn4j.test.modules.X681.TestSequence19;
import ch.eskaton.asn4j.test.modules.X681.TestSequence19.SetOfField;
import ch.eskaton.asn4j.test.modules.X681.TestSequence2;
import ch.eskaton.asn4j.test.modules.X681.TestSequence20;
import ch.eskaton.asn4j.test.modules.X681.TestSequence20.SequenceField;
import ch.eskaton.asn4j.test.modules.X681.TestSequence21;
import ch.eskaton.asn4j.test.modules.X681.TestSequence21.SetField;
import ch.eskaton.asn4j.test.modules.X681.TestSequence22;
import ch.eskaton.asn4j.test.modules.X681.TestSequence22.ChoiceField;
import ch.eskaton.asn4j.test.modules.X681.TestSequence23;
import ch.eskaton.asn4j.test.modules.X681.TestSequence24;
import ch.eskaton.asn4j.test.modules.X681.TestSequence25;
import ch.eskaton.asn4j.test.modules.X681.TestSequence26;
import ch.eskaton.asn4j.test.modules.X681.TestSequence3;
import ch.eskaton.asn4j.test.modules.X681.TestSequence4;
import ch.eskaton.asn4j.test.modules.X681.TestSequence5;
import ch.eskaton.asn4j.test.modules.X681.TestSequence6;
import ch.eskaton.asn4j.test.modules.X681.TestSequence7;
import ch.eskaton.asn4j.test.modules.X681.TestSequence8;
import ch.eskaton.asn4j.test.modules.X681.TestSequence9;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static ch.eskaton.asn4j.test.TestHelper.testSequenceSuccess;
import static ch.eskaton.commons.utils.Utils.with;

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

    @Test
    @DisplayName("Verify that the object class field type is resolved to a VisibleString")
    void testSequence7() {
        testSequenceSuccess(TestSequence7.class, new TestSequence7(),
                s -> s.setVisibleStringField(new ASN1VisibleString("abc")));
    }

    @Test
    @DisplayName("Verify that the object class field type is resolved to a GeneralString")
    void testSequence8() {
        testSequenceSuccess(TestSequence8.class, new TestSequence8(),
                s -> s.setGeneralStringField(new ASN1GeneralString("abc")));
    }

    @Test
    @DisplayName("Verify that the object class field type is resolved to a GraphicString")
    void testSequence9() {
        testSequenceSuccess(TestSequence9.class, new TestSequence9(),
                s -> s.setGraphicStringField(new ASN1GraphicString("abc")));
    }

    @Test
    @DisplayName("Verify that the object class field type is resolved to an IA5String")
    void testSequence10() {
        testSequenceSuccess(TestSequence10.class, new TestSequence10(),
                s -> s.setIa5StringField(new ASN1IA5String("abc")));
    }

    @Test
    @DisplayName("Verify that the object class field type is resolved to a VideotexString")
    void testSequence11() {
        testSequenceSuccess(TestSequence11.class, new TestSequence11(),
                s -> s.setVideotexStringField(new ASN1VideotexString("abc")));
    }

    @Test
    @DisplayName("Verify that the object class field type is resolved to a TeletexString")
    void testSequence12() {
        testSequenceSuccess(TestSequence12.class, new TestSequence12(),
                s -> s.setTeletexStringField(new ASN1TeletexString("abc")));
    }

    @Test
    @DisplayName("Verify that the object class field type is resolved to a PrintableString")
    void testSequence13() {
        testSequenceSuccess(TestSequence13.class, new TestSequence13(),
                s -> s.setPrintableStringField(new ASN1PrintableString("abc")));
    }

    @Test
    @DisplayName("Verify that the object class field type is resolved to a NumericString")
    void testSequence14() {
        testSequenceSuccess(TestSequence14.class, new TestSequence14(),
                s -> s.setNumericStringField(new ASN1NumericString("123")));
    }

    @Test
    @DisplayName("Verify that the object class field type is resolved to a UTF8String")
    void testSequence15() {
        testSequenceSuccess(TestSequence15.class, new TestSequence15(),
                s -> s.setUtf8StringField(new ASN1UTF8String("äöü")));
    }

    @Test
    @DisplayName("Verify that the object class field type is resolved to a UniversalString")
    void testSequence16() {
        testSequenceSuccess(TestSequence16.class, new TestSequence16(),
                s -> s.setUniversalStringField(new ASN1UniversalString("äöü")));
    }

    @Test
    @DisplayName("Verify that the object class field type is resolved to a BMPString")
    void testSequence17() {
        testSequenceSuccess(TestSequence17.class, new TestSequence17(),
                s -> s.setBmpStringField(new ASN1BMPString("äöü")));
    }

    @Test
    @DisplayName("Verify that the object class field type is resolved to a SEQUENCE OF type")
    void testSequence18() {
        testSequenceSuccess(TestSequence18.class, new TestSequence18(),
                s -> s.setSequenceOfField(new SequenceOfField(ASN1Integer.valueOf(1), ASN1Integer.valueOf(2))));
    }

    @Test
    @DisplayName("Verify that the object class field type is resolved to a SET OF type")
    void testSequence19() {
        testSequenceSuccess(TestSequence19.class, new TestSequence19(),
                s -> s.setSetOfField(new SetOfField(ASN1Integer.valueOf(1), ASN1Integer.valueOf(2))));
    }

    @Test
    @DisplayName("Verify that the object class field type is resolved to a SEQUENCE type")
    void testSequence20() {
        testSequenceSuccess(TestSequence20.class, new TestSequence20(),
                s -> s.setSequenceField(new SequenceField(ASN1Integer.valueOf(1), ASN1Boolean.TRUE)));
    }

    @Test
    @DisplayName("Verify that the object class field type is resolved to a SET type")
    void testSequence21() {
        testSequenceSuccess(TestSequence21.class, new TestSequence21(),
                s -> s.setSetField(new SetField(ASN1Integer.valueOf(1), ASN1Boolean.TRUE)));
    }

    @Test
    @DisplayName("Verify that the object class field type is resolved to a CHOICE type")
    void testSequence22() {
        testSequenceSuccess(TestSequence22.class, new TestSequence22(),
                s -> s.setChoiceField(with(new ChoiceField(), c -> c.setB(ASN1Boolean.TRUE))));
    }

    @Test
    @DisplayName("Verify that the object class field type is resolved to an OBJECT IDENTIFIER")
    void testSequence23() {
        testSequenceSuccess(TestSequence23.class, new TestSequence23(),
                s -> s.setObjectIdentifierField(ASN1ObjectIdentifier.from(1, 2, 5, 2)));
    }

    @Test
    @DisplayName("Verify that the object class field type is resolved to a RELATIVE-OID")
    void testSequence24() {
        testSequenceSuccess(TestSequence24.class, new TestSequence24(),
                s -> s.setRelativeOidField(ASN1RelativeOID.from(2, 5, 2)));
    }

    @Test
    @DisplayName("Verify that the object class field type is resolved to an OID-IRI type")
    void testSequence25() {
        testSequenceSuccess(TestSequence25.class, new TestSequence25(),
                s -> s.setOidIriField(ASN1IRI.from("ISO", "a", "b", "c")));
    }

    @Test
    @DisplayName("Verify that the object class field type is resolved to a RELATIVE-OID-IRI type")
    void testSequence26() {
        testSequenceSuccess(TestSequence26.class, new TestSequence26(),
                s -> s.setRelativeOidIriField(ASN1RelativeIRI.from("a", "b", "c")));
    }

}
