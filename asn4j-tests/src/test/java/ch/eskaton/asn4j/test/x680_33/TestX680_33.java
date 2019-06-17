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

package ch.eskaton.asn4j.test.x680_33;

import ch.eskaton.asn4j.runtime.BERDecoder;
import ch.eskaton.asn4j.runtime.BEREncoder;
import ch.eskaton.asn4j.runtime.types.ASN1RelativeOID;
import ch.eskaton.asn4j.test.modules.x680_33.TestRelativeObjectIdentifiers;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestX680_33 {

    @Test
    public void TestRelativeObjectIdentifiersWithDefaults() {
        TestRelativeObjectIdentifiers a = new TestRelativeObjectIdentifiers();
        ASN1RelativeOID testRelativeObjectIdentifier1 = createRelativeOID(new ASN1RelativeOID(), 3);
        ASN1RelativeOID testRelativeObjectIdentifier2 = createRelativeOID(new ASN1RelativeOID(), 3, 6, 1);

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        TestRelativeObjectIdentifiers b = decoder.decode(TestRelativeObjectIdentifiers.class, encoder.encode(a));

        assertEquals(a, b);
        assertEquals(testRelativeObjectIdentifier1, a.getTestRelativeObjectIdentifier1());
        assertEquals(testRelativeObjectIdentifier2, a.getTestRelativeObjectIdentifier2());

        assertEquals(testRelativeObjectIdentifier1, b.getTestRelativeObjectIdentifier1());
        assertEquals(testRelativeObjectIdentifier2, b.getTestRelativeObjectIdentifier2());
    }

    @Test
    public void TestRelativeObjectIdentifiersWithoutDefaults() {
        TestRelativeObjectIdentifiers a = new TestRelativeObjectIdentifiers();
        ASN1RelativeOID testObjectIdentifier1 = createRelativeOID(new ASN1RelativeOID(), 2, 3);
        ASN1RelativeOID testObjectIdentifier2 = createRelativeOID(new ASN1RelativeOID(), 0, 7, 1, 1);

        a.setTestRelativeObjectIdentifier1(testObjectIdentifier1);
        a.setTestRelativeObjectIdentifier2(testObjectIdentifier2);

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        TestRelativeObjectIdentifiers b = decoder.decode(TestRelativeObjectIdentifiers.class, encoder.encode(a));

        assertEquals(a, b);
        assertEquals(testObjectIdentifier1, a.getTestRelativeObjectIdentifier1());
        assertEquals(testObjectIdentifier2, a.getTestRelativeObjectIdentifier2());

        assertEquals(testObjectIdentifier1, b.getTestRelativeObjectIdentifier1());
        assertEquals(testObjectIdentifier2, b.getTestRelativeObjectIdentifier2());
    }

    public <T extends ASN1RelativeOID> T createRelativeOID(ASN1RelativeOID oid, int... components) {
        oid.setValue(components);
        return (T) oid;
    }

}
