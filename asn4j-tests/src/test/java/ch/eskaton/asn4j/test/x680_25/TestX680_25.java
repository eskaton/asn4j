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

import ch.eskaton.asn4j.runtime.types.ASN1Boolean;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1OctetString;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static ch.eskaton.asn4j.test.TestHelper.assertDecodable;
import static ch.eskaton.asn4j.test.TestHelper.assertDecodableVerifyAfter;
import static ch.eskaton.commons.utils.Utils.with;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("X.680 25 - SEQUENCE tests")
public class TestX680_25 {

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
                value -> assertEquals(ASN1OctetString.valueOf(new byte[] { (byte) 0xab, (byte) 0xc0 }), value.getC()));
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

}
