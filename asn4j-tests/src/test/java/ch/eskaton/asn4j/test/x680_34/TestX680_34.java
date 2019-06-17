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

package ch.eskaton.asn4j.test.x680_34;

import ch.eskaton.asn4j.runtime.BERDecoder;
import ch.eskaton.asn4j.runtime.BEREncoder;
import ch.eskaton.asn4j.runtime.types.ASN1IRI;
import ch.eskaton.asn4j.test.modules.x680_34.TestOidIris;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestX680_34 {

    @Test
    public void testIRIsWithDefaults() {
        TestOidIris a = new TestOidIris();
        ASN1IRI testOidIri1 = createIRI(new ASN1IRI(), "ISO", "Registration-Authority", "19785.CBEFF", "Organizations");
        ASN1IRI testOidIri2 = createIRI(new ASN1IRI(), "ISO", "Registration-Authority", "Test");
        ASN1IRI testOidIri3 = createIRI(new ASN1IRI(), "ISO", "Registration-Authority", "19785.CBEFF");

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        TestOidIris b = decoder.decode(TestOidIris.class, encoder.encode(a));

        assertEquals(a, b);
        assertEquals(testOidIri1, a.getTestOidIri1());
        assertEquals(testOidIri2, a.getTestOidIri2());
        assertEquals(testOidIri3, a.getTestOidIri3());

        assertEquals(testOidIri1, b.getTestOidIri1());
        assertEquals(testOidIri2, b.getTestOidIri2());
        assertEquals(testOidIri3, b.getTestOidIri3());
    }

    @Test
    public void testIRIsWithoutDefaults() {
        TestOidIris a = new TestOidIris();
        ASN1IRI testOidIri1 = createIRI(new ASN1IRI(), "ISO", "Registration-Authority", "123", "Test");
        ASN1IRI testOidIri2 = createIRI(new ASN1IRI(), "ISO", "Registration-Authority", "456");
        ASN1IRI testOidIri3 = createIRI(new ASN1IRI(), "ISO", "Registration-Authority", "Test");

        a.setTestOidIri1(testOidIri1);
        a.setTestOidIri2(testOidIri2);
        a.setTestOidIri3(testOidIri3);

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        TestOidIris b = decoder.decode(TestOidIris.class, encoder.encode(a));

        assertEquals(a, b);
        assertEquals(testOidIri1, a.getTestOidIri1());
        assertEquals(testOidIri2, a.getTestOidIri2());
        assertEquals(testOidIri3, a.getTestOidIri3());

        assertEquals(testOidIri1, b.getTestOidIri1());
        assertEquals(testOidIri2, b.getTestOidIri2());
        assertEquals(testOidIri3, b.getTestOidIri3());
    }

    public <T extends ASN1IRI> T createIRI(ASN1IRI iri, String... components) {
        iri.setValue(components);
        return (T) iri;
    }

}

