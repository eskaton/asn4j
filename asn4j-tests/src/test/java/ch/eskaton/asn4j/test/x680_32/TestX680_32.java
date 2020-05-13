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

package ch.eskaton.asn4j.test.x680_32;

import ch.eskaton.asn4j.runtime.BERDecoder;
import ch.eskaton.asn4j.runtime.BEREncoder;
import ch.eskaton.asn4j.runtime.types.ASN1ObjectIdentifier;
import ch.eskaton.asn4j.test.modules.x680_32.TestObjectIdentifierRootArcs;
import ch.eskaton.asn4j.test.modules.x680_32.TestObjectIdentifiers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestX680_32 {

    @Test
    public void testObjectIdentifiersWithDefaults() {
        TestObjectIdentifiers a = new TestObjectIdentifiers();
        ASN1ObjectIdentifier testObjectIdentifier1 = createOID(new ASN1ObjectIdentifier(), 1, 3, 6, 1);
        ASN1ObjectIdentifier testObjectIdentifier2 = createOID(new ASN1ObjectIdentifier(), 1, 3, 6, 1);
        ASN1ObjectIdentifier testObjectIdentifier3 = createOID(new ASN1ObjectIdentifier(), 1, 3, 6, 1);
        ASN1ObjectIdentifier testObjectIdentifier4 = createOID(new ASN1ObjectIdentifier(), 1, 3, 6, 1, 23);
        ASN1ObjectIdentifier testObjectIdentifier5 = createOID(new ASN1ObjectIdentifier(), 1, 3, 6, 1);
        ASN1ObjectIdentifier testObjectIdentifier6 = createOID(new ASN1ObjectIdentifier(), 1, 3, 6, 2);

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        TestObjectIdentifiers b = decoder.decode(TestObjectIdentifiers.class, encoder.encode(a));

        assertEquals(a, b);
        assertEquals(testObjectIdentifier1, a.getTestObjectIdentifier1());
        assertEquals(testObjectIdentifier2, a.getTestObjectIdentifier2());
        assertEquals(testObjectIdentifier3, a.getTestObjectIdentifier3());
        assertEquals(testObjectIdentifier4, a.getTestObjectIdentifier4());
        assertEquals(testObjectIdentifier5, a.getTestObjectIdentifier5());
        assertEquals(testObjectIdentifier6, a.getTestObjectIdentifier6());

        assertEquals(testObjectIdentifier1, b.getTestObjectIdentifier1());
        assertEquals(testObjectIdentifier2, b.getTestObjectIdentifier2());
        assertEquals(testObjectIdentifier3, b.getTestObjectIdentifier3());
        assertEquals(testObjectIdentifier4, b.getTestObjectIdentifier4());
        assertEquals(testObjectIdentifier5, b.getTestObjectIdentifier5());
        assertEquals(testObjectIdentifier6, b.getTestObjectIdentifier6());
    }

    @Test
    public void testObjectIdentifiersWithoutDefaults() {
        TestObjectIdentifiers a = new TestObjectIdentifiers();
        ASN1ObjectIdentifier testObjectIdentifier1 = createOID(new ASN1ObjectIdentifier(), 2, 3);
        ASN1ObjectIdentifier testObjectIdentifier2 = createOID(new ASN1ObjectIdentifier(), 0, 7, 1, 1);
        ASN1ObjectIdentifier testObjectIdentifier3 = createOID(new ASN1ObjectIdentifier(), 1, 9, 6, 7);
        ASN1ObjectIdentifier testObjectIdentifier4 = createOID(new ASN1ObjectIdentifier(), 1, 9, 6, 7, 17);
        ASN1ObjectIdentifier testObjectIdentifier5 = createOID(new ASN1ObjectIdentifier(), 1, 9, 6, 7, 13);
        ASN1ObjectIdentifier testObjectIdentifier6 = createOID(new ASN1ObjectIdentifier(), 1, 9, 6, 7, 11);

        a.setTestObjectIdentifier1(testObjectIdentifier1);
        a.setTestObjectIdentifier2(testObjectIdentifier2);
        a.setTestObjectIdentifier3(testObjectIdentifier3);
        a.setTestObjectIdentifier4(testObjectIdentifier4);
        a.setTestObjectIdentifier5(testObjectIdentifier5);
        a.setTestObjectIdentifier6(testObjectIdentifier6);

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        TestObjectIdentifiers b = decoder.decode(TestObjectIdentifiers.class, encoder.encode(a));

        assertEquals(a, b);
        assertEquals(testObjectIdentifier1, a.getTestObjectIdentifier1());
        assertEquals(testObjectIdentifier2, a.getTestObjectIdentifier2());
        assertEquals(testObjectIdentifier3, a.getTestObjectIdentifier3());
        assertEquals(testObjectIdentifier4, a.getTestObjectIdentifier4());
        assertEquals(testObjectIdentifier5, a.getTestObjectIdentifier5());
        assertEquals(testObjectIdentifier6, a.getTestObjectIdentifier6());

        assertEquals(testObjectIdentifier1, b.getTestObjectIdentifier1());
        assertEquals(testObjectIdentifier2, b.getTestObjectIdentifier2());
        assertEquals(testObjectIdentifier3, b.getTestObjectIdentifier3());
        assertEquals(testObjectIdentifier4, b.getTestObjectIdentifier4());
        assertEquals(testObjectIdentifier5, b.getTestObjectIdentifier5());
        assertEquals(testObjectIdentifier6, b.getTestObjectIdentifier6());
    }

    @Test
    public void testObjectIdentifiersRootArcs() {
        TestObjectIdentifierRootArcs a = new TestObjectIdentifierRootArcs();

        ASN1ObjectIdentifier oidItuT = createOID(new ASN1ObjectIdentifier(), 0, 12);
        ASN1ObjectIdentifier oidCcitt = createOID(new ASN1ObjectIdentifier(), 0, 12);
        ASN1ObjectIdentifier oidIso = createOID(new ASN1ObjectIdentifier(), 1, 12);
        ASN1ObjectIdentifier oidJointIsoItuT = createOID(new ASN1ObjectIdentifier(), 2, 12);
        ASN1ObjectIdentifier oidIsoCcitt = createOID(new ASN1ObjectIdentifier(), 2, 12);

        assertEquals(oidItuT, a.getTestOidItuT());
        assertEquals(oidCcitt, a.getTestOidCcitt());
        assertEquals(oidIso, a.getTestOidIso());
        assertEquals(oidJointIsoItuT, a.getTestOidJointIsoItuT());
        assertEquals(oidIsoCcitt, a.getTestOidJointIsoCcitt());
    }

    public <T extends ASN1ObjectIdentifier> T createOID(ASN1ObjectIdentifier oid, int... components) {
        oid.setValue(components);
        return (T) oid;
    }

}
