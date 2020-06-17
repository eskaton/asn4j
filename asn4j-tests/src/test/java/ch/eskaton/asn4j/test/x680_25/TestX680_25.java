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

package ch.eskaton.asn4j.test.x680_25;

import ch.eskaton.asn4j.runtime.types.ASN1BMPString;
import ch.eskaton.asn4j.runtime.types.ASN1Boolean;
import ch.eskaton.asn4j.runtime.types.ASN1GeneralString;
import ch.eskaton.asn4j.runtime.types.ASN1GeneralizedTime;
import ch.eskaton.asn4j.runtime.types.ASN1GraphicString;
import ch.eskaton.asn4j.runtime.types.ASN1IA5String;
import ch.eskaton.asn4j.runtime.types.ASN1IRI;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1Null;
import ch.eskaton.asn4j.runtime.types.ASN1NumericString;
import ch.eskaton.asn4j.runtime.types.ASN1ObjectIdentifier;
import ch.eskaton.asn4j.runtime.types.ASN1OctetString;
import ch.eskaton.asn4j.runtime.types.ASN1PrintableString;
import ch.eskaton.asn4j.runtime.types.ASN1Real;
import ch.eskaton.asn4j.runtime.types.ASN1RelativeIRI;
import ch.eskaton.asn4j.runtime.types.ASN1RelativeOID;
import ch.eskaton.asn4j.runtime.types.ASN1TeletexString;
import ch.eskaton.asn4j.runtime.types.ASN1UTCTime;
import ch.eskaton.asn4j.runtime.types.ASN1UTF8String;
import ch.eskaton.asn4j.runtime.types.ASN1UniversalString;
import ch.eskaton.asn4j.runtime.types.ASN1VideotexString;
import ch.eskaton.asn4j.runtime.types.ASN1VisibleString;
import ch.eskaton.asn4j.test.modules.x680_25.TestBMPString1;
import ch.eskaton.asn4j.test.modules.x680_25.TestBoolean;
import ch.eskaton.asn4j.test.modules.x680_25.TestChoice1;
import ch.eskaton.asn4j.test.modules.x680_25.TestEnumeration;
import ch.eskaton.asn4j.test.modules.x680_25.TestGeneralString1;
import ch.eskaton.asn4j.test.modules.x680_25.TestGeneralizedTime1;
import ch.eskaton.asn4j.test.modules.x680_25.TestGraphicString1;
import ch.eskaton.asn4j.test.modules.x680_25.TestIA5String1;
import ch.eskaton.asn4j.test.modules.x680_25.TestNull1;
import ch.eskaton.asn4j.test.modules.x680_25.TestNumericString1;
import ch.eskaton.asn4j.test.modules.x680_25.TestOctetString;
import ch.eskaton.asn4j.test.modules.x680_25.TestPrintableString1;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence0;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence1;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence10;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence11;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence2;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence3;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence4;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence5;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence6;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence7;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence8;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequence9;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults1;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults10;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults11;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults12;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults13;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults14;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults15;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults16;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults17;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults18;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults19;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults2;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults20;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults21;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults22;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults23;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults24;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults25;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults26;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults27;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults28;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults29;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults3;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults4;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults5;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults6;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults7;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults8;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceDefaults9;
import ch.eskaton.asn4j.test.modules.x680_25.TestSequenceOf1;
import ch.eskaton.asn4j.test.modules.x680_25.TestSetOf1;
import ch.eskaton.asn4j.test.modules.x680_25.TestT61String1;
import ch.eskaton.asn4j.test.modules.x680_25.TestTeletexString1;
import ch.eskaton.asn4j.test.modules.x680_25.TestUTCTime1;
import ch.eskaton.asn4j.test.modules.x680_25.TestUTF8String1;
import ch.eskaton.asn4j.test.modules.x680_25.TestUniversalString1;
import ch.eskaton.asn4j.test.modules.x680_25.TestVideotexString1;
import ch.eskaton.asn4j.test.modules.x680_25.TestVisibleString1;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static ch.eskaton.asn4j.test.TestHelper.assertDecodable;
import static ch.eskaton.asn4j.test.TestHelper.assertDecodableVerifyAfter;
import static ch.eskaton.asn4j.test.TestHelper.createIRI;
import static ch.eskaton.asn4j.test.TestHelper.createOID;
import static ch.eskaton.asn4j.test.TestHelper.toBooleans;
import static ch.eskaton.asn4j.test.TestHelper.toInts;
import static ch.eskaton.commons.utils.Utils.with;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("X.680 25 - SEQUENCE tests")
class TestX680_25 {

    @Test
    @DisplayName("Single component")
    void testSequence0() {
        assertDecodable(TestSequence0.class, value -> value.setA(ASN1Boolean.TRUE));
    }

    @Test
    @DisplayName("Multiple components")
    void testSequence1() {
        assertDecodable(TestSequence1.class, value -> {
            value.setA(ASN1Integer.valueOf(4711));
            value.setB(ASN1Boolean.TRUE);
            value.setC(ASN1OctetString.valueOf(new byte[] { (byte) 0xff, (byte) 0x56 }));
        });
    }

    @Test
    @DisplayName("Optional component")
    void testSequence2() {
        assertDecodable(TestSequence2.class, value -> {
            value.setA(ASN1Integer.valueOf(4711));
            value.setC(ASN1OctetString.valueOf(new byte[] { (byte) 0xff, (byte) 0x56 }));
        });
    }

    @Test
    @DisplayName("Default values at end")
    void testSequence3() {
        assertDecodableVerifyAfter(TestSequence3.class,
                value -> {
                    value.setA(ASN1Integer.valueOf(4711));
                    value.setB(ASN1Boolean.TRUE);
                },
                value -> {
                    assertEquals(ASN1OctetString.valueOf(new byte[] { (byte) 0xab, (byte) 0xc0 }), value.getC());
                });
    }

    @Test
    @DisplayName("Default values at beginning")
    void testSequence4() {
        assertDecodableVerifyAfter(TestSequence4.class,
                value -> {
                    value.setB(ASN1Boolean.TRUE);
                    value.setC(ASN1OctetString.valueOf(new byte[] { (byte) 0xff, (byte) 0x56 }));
                },
                value -> assertEquals(ASN1Integer.valueOf(4711), value.getA()));
    }

    @Test
    @DisplayName("Extension addition")
    void testSequence5() {
        assertDecodableVerifyAfter(TestSequence5.class,
                value -> {
                    value.setA(ASN1Integer.valueOf(4711));
                    value.setZ(ASN1Integer.valueOf(23));
                    value.setB(ASN1Boolean.TRUE);
                },
                value -> {
                    assertEquals(ASN1Integer.valueOf(4711), value.getA());
                    assertEquals(ASN1Integer.valueOf(23), value.getZ());
                    assertEquals(ASN1Boolean.TRUE, value.getB());
                });
    }

    @Test
    @DisplayName("Include components with COMPONENTS OF")
    void testSequence6() {
        assertDecodableVerifyAfter(TestSequence6.class, value -> {
                    value.setA(ASN1Integer.valueOf(4711));
                    value.setB(ASN1Boolean.TRUE);
                    value.setC(ASN1Boolean.FALSE);
                },
                value -> {
                    assertEquals(ASN1Integer.valueOf(4711), value.getA());
                    assertEquals(ASN1Boolean.TRUE, value.getB());
                    assertEquals(ASN1Boolean.FALSE, value.getC());
                });
    }

    @Test
    @DisplayName("Include components with COMPONENTS OF with default value")
    void testSequence6defaults() {
        assertDecodableVerifyAfter(TestSequence6.class,
                value -> {
                    value.setB(ASN1Boolean.FALSE);
                    value.setC(ASN1Boolean.TRUE);
                },
                value -> {
                    assertEquals(ASN1Integer.valueOf(23), value.getA());
                    assertEquals(ASN1Boolean.FALSE, value.getB());
                    assertEquals(ASN1Boolean.TRUE, value.getC());
                });
    }

    @Test
    @DisplayName("Extension groups with version")
    void testSequence7() {
        assertDecodableVerifyAfter(TestSequence7.class,
                value -> {
                    value.setA(ASN1Integer.valueOf(23));
                    value.setC(ASN1Integer.valueOf(97));
                    value.setD(ASN1Boolean.TRUE);
                    value.setB(ASN1Integer.valueOf(4711));
                },
                value -> {
                    assertEquals(ASN1Integer.valueOf(23), value.getA());
                    assertEquals(ASN1Integer.valueOf(97), value.getC());
                    assertEquals(ASN1Boolean.TRUE, value.getD());
                    assertEquals(ASN1Integer.valueOf(4711), value.getB());
                });
    }

    @Test
    @DisplayName("Nested sequence")
    void testSequence8() {
        assertDecodableVerifyAfter(TestSequence8.class,
                value -> value.setChildSequence(with(new TestSequence8.ChildSequence(),
                        childValue -> childValue.setA(ASN1Integer.valueOf(4711)))),
                value -> assertEquals(ASN1Integer.valueOf(4711), value.getChildSequence().getA()));
    }

    @Test
    @DisplayName("Enumerated component")
    void testSequence9() {
        assertDecodableVerifyAfter(TestSequence9.class,
                value -> value.setChildEnumerated(with(new TestSequence9.ChildEnumerated(),
                        childValue -> childValue.setValue(TestSequence9.ChildEnumerated.A))),
                value -> assertEquals(TestSequence9.ChildEnumerated.A, value.getChildEnumerated()));
    }

    @Test
    @DisplayName("Extension group without version")
    void testSequence10() {
        assertDecodableVerifyAfter(TestSequence10.class,
                value -> {
                    value.setA(ASN1Integer.valueOf(23));
                    value.setC(ASN1Integer.valueOf(97));
                    value.setD(ASN1Boolean.TRUE);
                    value.setB(ASN1Integer.valueOf(4712));
                },
                value -> {
                    assertEquals(ASN1Integer.valueOf(23), value.getA());
                    assertEquals(ASN1Integer.valueOf(97), value.getC());
                    assertEquals(ASN1Boolean.TRUE, value.getD());
                    assertEquals(ASN1Integer.valueOf(4712), value.getB());
                });
    }

    @Test
    @DisplayName("Nested sequence with extension addition")
    void testSequence11() {
        assertDecodableVerifyAfter(TestSequence11.class,
                value -> value.setChildSequence(with(new TestSequence11.ChildSequence(),
                        childValue -> {
                            childValue.setA(ASN1Integer.valueOf(4711));
                            childValue.setB(ASN1Boolean.TRUE);
                        })),
                value -> {
                    assertEquals(ASN1Integer.valueOf(4711), value.getChildSequence().getA());
                    assertEquals(ASN1Boolean.TRUE, value.getChildSequence().getB());
                });
    }

    @Test
    @DisplayName("Test defaults for BOOLEAN")
    void testSequenceDefaults1() {
        assertDecodableVerifyAfter(TestSequenceDefaults1.class,
                value -> {
                    assertEquals(ASN1Boolean.of(true), value.getTestBoolean1());
                    assertEquals(ASN1Boolean.of(false), value.getTestBoolean2());
                    assertEquals(new TestBoolean(true), value.getTestBoolean3());
                    assertEquals(ASN1Boolean.of(false), value.getTestBoolean4());
                });
    }

    @Test
    @DisplayName("Test defaults for ENUMERATED")
    void testSequenceDefaults2() {
        assertDecodableVerifyAfter(TestSequenceDefaults2.class, value -> {
            assertEquals(TestEnumeration.B, value.getTestEnumeration1());
            assertEquals(TestEnumeration.A, value.getTestEnumeration2());
            assertEquals(TestEnumeration.A, value.getTestEnumeration3());
        });
    }

    @Test
    @DisplayName("Test defaults for OCTET STRING")
    void testSequenceDefaults3() {
        assertDecodableVerifyAfter(TestSequenceDefaults3.class, value -> {
            assertEquals(new ASN1OctetString(new byte[] { 0x01, (byte) 0xAF }), value.getTestOctetString1());
            assertEquals(new TestOctetString(new byte[] { 0x01, (byte) 0xAF }), value.getTestOctetString2());
            assertEquals(new TestOctetString(new byte[] { 0x01, (byte) 0xAF }), value.getTestOctetString3());
            assertEquals(new TestOctetString(new byte[] { 0x50 }), value.getTestOctetString4());
        });
    }

    @Test
    @DisplayName("Test defaults for OBJECT IDENTIFIER")
    void testSequenceDefaults4() {
        assertDecodableVerifyAfter(TestSequenceDefaults4.class, value -> {
            assertEquals(createOID(new ASN1ObjectIdentifier(), 1, 3, 6, 1), value.getTestObjectIdentifier1());
        });
    }

    @Test
    @DisplayName("Test defaults for RELATIVE-OID")
    void testSequenceDefaults5() {
        assertDecodableVerifyAfter(TestSequenceDefaults5.class, value -> {
            assertEquals(createOID(new ASN1RelativeOID(), 3, 6, 1), value.getTestRelativeOID1());
        });
    }

    @Test
    @DisplayName("Test defaults for OID-IRI")
    void testSequenceDefaults6() {
        assertDecodableVerifyAfter(TestSequenceDefaults6.class, value -> {
            assertEquals(createIRI(new ASN1IRI(), "ISO", "Registration-Authority", "19785.CBEFF", "Organizations"),
                    value.getTestOidIri1());
            assertEquals(createIRI(new ASN1IRI(), "ISO", "Registration-Authority", "Test"), value.getTestOidIri2());

        });
    }

    @Test
    @DisplayName("Test defaults for RELATIVE-OID-IRI")
    void testSequenceDefaults7() {
        assertDecodableVerifyAfter(TestSequenceDefaults7.class, value -> {
            assertEquals(createIRI(new ASN1RelativeIRI(), "Registration-Authority", "19785.CBEFF", "Organizations"),
                    value.getTestRelativeOidIri1());
            assertEquals(createIRI(new ASN1RelativeIRI(), "Registration-Authority", "Test"),
                    value.getTestRelativeOidIri2());
        });
    }

    @Test
    @DisplayName("Test defaults for INTEGER")
    void testSequenceDefaults8() {
        assertDecodableVerifyAfter(TestSequenceDefaults8.class, value -> {
            assertEquals(ASN1Integer.valueOf(4711), value.getTestInteger1());
            assertEquals(ASN1Integer.valueOf(23), value.getTestInteger2());
        });
    }

    @Test
    @DisplayName("Test defaults for REAL")
    void testSequenceDefaults9() {
        assertDecodableVerifyAfter(TestSequenceDefaults9.class, value -> {
            assertEquals(new ASN1Real(new BigDecimal("12.78")), value.getTestReal1());
            assertEquals(new ASN1Real(5, 2, 3), value.getTestReal2());
            assertEquals(new ASN1Real(7, 2, 3), value.getTestReal3());
            assertEquals(new ASN1Real(new BigDecimal("12.78e-5")), value.getTestReal4());
            assertEquals(ASN1Real.MINUS_INFINITY, value.getTestReal5());
            assertEquals(ASN1Real.PLUS_INFINITY, value.getTestReal6());
            assertEquals(ASN1Real.NOT_A_NUMBER, value.getTestReal7());
        });
    }

    @Test
    @DisplayName("Test defaults for NULL")
    void testSequenceDefaults10() {
        assertDecodableVerifyAfter(TestSequenceDefaults10.class, value -> {
            assertEquals(new ASN1Null(), value.getTestNull1());
            assertEquals(new TestNull1(), value.getTestNull2());
        });
    }

    @Test
    @DisplayName("Test defaults for SEQUENCE")
    void testSequenceDefaults11() {
        assertDecodableVerifyAfter(TestSequenceDefaults11.class, value -> {
            assertEquals(ASN1Integer.valueOf(1), value.getA().getA());
            assertEquals(ASN1Boolean.FALSE, value.getA().getB());
            assertEquals(ASN1Integer.valueOf(1), value.getB().getA());
            assertEquals(ASN1OctetString.valueOf(new byte[] { (byte) 0xab, (byte) 0xc0 }), value.getB().getC());
        });
    }

    @Test
    @DisplayName("Test defaults for SET")
    void testSequenceDefaults12() {
        assertDecodableVerifyAfter(TestSequenceDefaults12.class, value -> {
            assertEquals(ASN1Integer.valueOf(2), value.getA().getA());
            assertEquals(ASN1Boolean.TRUE, value.getA().getB());
            assertEquals(ASN1Boolean.FALSE, value.getB().getA());
            assertEquals(ASN1Integer.valueOf(1), value.getB().getB());
        });
    }

    @Test
    @DisplayName("Test defaults for SEQUENCE OF")
    void testSequenceDefaults13() {
        assertDecodableVerifyAfter(TestSequenceDefaults13.class, value -> {
            assertEquals(new TestSequenceDefaults13.A(toBooleans(true, false)), value.getA());
            assertEquals(new TestSequenceOf1(toInts(1, 3, 5)), value.getB());
        });
    }

    @Test
    @DisplayName("Test defaults for SET OF")
    void testSequenceDefaults14() {
        assertDecodableVerifyAfter(TestSequenceDefaults14.class, value -> {
            assertEquals(new TestSequenceDefaults14.A(toBooleans(false, false)), value.getA());
            assertEquals(new TestSetOf1(toInts(2, 4, 6)), value.getB());
        });
    }

    @Test
    @DisplayName("Test defaults for CHOICE")
    void testSequenceDefaults15() {
        assertDecodableVerifyAfter(TestSequenceDefaults15.class, value -> {
            assertEquals(with(new TestSequenceDefaults15.A(), v -> v.setA(ASN1Integer.valueOf(25))), value.getA());
            assertEquals(with(new TestChoice1(), v -> v.setA(ASN1Boolean.TRUE)), value.getB());
        });
    }

    @Test
    @DisplayName("Test defaults for VisibleString")
    void testSequenceDefaults16() {
        assertDecodableVerifyAfter(TestSequenceDefaults16.class, value -> {
            assertEquals(new ASN1VisibleString("test1"), value.getA());
            assertEquals(new TestVisibleString1("test2"), value.getB());
        });
    }

    @Test
    @DisplayName("Test defaults for GeneralizedTime")
    void testSequenceDefaults17() {
        assertDecodableVerifyAfter(TestSequenceDefaults17.class, value -> {
            assertEquals(new ASN1GeneralizedTime("19851106210627.3Z"), value.getA());
            assertEquals(new TestGeneralizedTime1("19851106210627.3-0500"), value.getB());
        });
    }

    @Test
    @DisplayName("Test defaults for UTCTime")
    void testSequenceDefaults18() {
        assertDecodableVerifyAfter(TestSequenceDefaults18.class, value -> {
            assertEquals(new ASN1UTCTime("8201021200Z"), value.getA());
            assertEquals(new TestUTCTime1("8201020700-0500"), value.getB());
        });
    }

    @Test
    @DisplayName("Test defaults for NumericString")
    void testSequenceDefaults19() {
        assertDecodableVerifyAfter(TestSequenceDefaults19.class, value -> {
            assertEquals(new ASN1NumericString("1234"), value.getA());
            assertEquals(new TestNumericString1("5678"), value.getB());
        });
    }

    @Test
    @DisplayName("Test defaults for PrintableString")
    void testSequenceDefaults20() {
        assertDecodableVerifyAfter(TestSequenceDefaults20.class, value -> {
            assertEquals(new ASN1PrintableString("abCD"), value.getA());
            assertEquals(new TestPrintableString1("5678"), value.getB());
        });
    }

    @Test
    @DisplayName("Test defaults for IA5String")
    void testSequenceDefaults21() {
        assertDecodableVerifyAfter(TestSequenceDefaults21.class, value -> {
            assertEquals(new ASN1IA5String("abCD\"\'\\\n"), value.getA());
            assertEquals(new TestIA5String1("5678"), value.getB());
        });
    }

    @Test
    @DisplayName("Test defaults for GraphicString")
    void testSequenceDefaults22() {
        assertDecodableVerifyAfter(TestSequenceDefaults22.class, value -> {
            assertEquals(new ASN1GraphicString("abCD"), value.getA());
            assertEquals(new TestGraphicString1("5678"), value.getB());
        });
    }

    @Test
    @DisplayName("Test defaults for TeletexString")
    void testSequenceDefaults23() {
        assertDecodableVerifyAfter(TestSequenceDefaults23.class, value -> {
            assertEquals(new ASN1TeletexString("abCD"), value.getA());
            assertEquals(new TestTeletexString1("5678"), value.getB());
        });
    }

    @Test
    @DisplayName("Test defaults for T61String")
    void testSequenceDefaults24() {
        assertDecodableVerifyAfter(TestSequenceDefaults24.class, value -> {
            assertEquals(new ASN1TeletexString("abCD"), value.getA());
            assertEquals(new TestT61String1("5678"), value.getB());
        });
    }

    @Test
    @DisplayName("Test defaults for VideotexString")
    void testSequenceDefaults25() {
        assertDecodableVerifyAfter(TestSequenceDefaults25.class, value -> {
            assertEquals(new ASN1VideotexString("abCD"), value.getA());
            assertEquals(new TestVideotexString1("5678"), value.getB());
        });
    }

    @Test
    @DisplayName("Test defaults for GeneralString")
    void testSequenceDefaults26() {
        assertDecodableVerifyAfter(TestSequenceDefaults26.class, value -> {
            assertEquals(new ASN1GeneralString("abCD"), value.getA());
            assertEquals(new TestGeneralString1("5678"), value.getB());
        });
    }

    @Test
    @DisplayName("Test defaults for UniversalString")
    void testSequenceDefaults27() {
        assertDecodableVerifyAfter(TestSequenceDefaults27.class, value -> {
            assertEquals(new ASN1UniversalString("abCD\uD83D\uDE02"), value.getA());
            assertEquals(new TestUniversalString1("5678"), value.getB());
        });
    }

    @Test
    @DisplayName("Test defaults for UTF8String")
    void testSequenceDefaults28() {
        assertDecodableVerifyAfter(TestSequenceDefaults28.class, value -> {
            assertEquals(new ASN1UTF8String("abCD\uD83D\uDE02"), value.getA());
            assertEquals(new TestUTF8String1("5678"), value.getB());
        });
    }

    @Test
    @DisplayName("Test defaults for BMPString")
    void testSequenceDefaults29() {
        assertDecodableVerifyAfter(TestSequenceDefaults29.class, value -> {
            assertEquals(new ASN1BMPString("abCDü"), value.getA());
            assertEquals(new TestBMPString1("5678"), value.getB());
        });
    }

}
