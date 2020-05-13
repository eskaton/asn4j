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

import ch.eskaton.asn4j.runtime.types.ASN1IRI;
import ch.eskaton.asn4j.test.modules.x680_34.TestOidIris;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static ch.eskaton.asn4j.test.TestHelper.assertDecodableVerifyAround;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestX680_34 {

    @Test
    public void testIRIsWithDefaults() {
        ASN1IRI testOidIri1 = createIRI(new ASN1IRI(), "ISO", "Registration-Authority", "19785.CBEFF", "Organizations");
        ASN1IRI testOidIri2 = createIRI(new ASN1IRI(), "ISO", "Registration-Authority", "Test");
        ASN1IRI testOidIri3 = createIRI(new ASN1IRI(), "ISO", "Registration-Authority", "19785.CBEFF");

        Consumer<TestOidIris> verifier = value -> {
            assertEquals(testOidIri1, value.getTestOidIri1());
            assertEquals(testOidIri2, value.getTestOidIri2());
            assertEquals(testOidIri3, value.getTestOidIri3());

        };

        assertDecodableVerifyAround(TestOidIris.class, verifier, verifier);
    }

    @Test
    public void testIRIsWithoutDefaults() {
        ASN1IRI testOidIri1 = createIRI(new ASN1IRI(), "ISO", "Registration-Authority", "123", "Test");
        ASN1IRI testOidIri2 = createIRI(new ASN1IRI(), "ISO", "Registration-Authority", "456");
        ASN1IRI testOidIri3 = createIRI(new ASN1IRI(), "ISO", "Registration-Authority", "Test");

        Consumer<TestOidIris> verifier = value -> {
            assertEquals(testOidIri1, value.getTestOidIri1());
            assertEquals(testOidIri2, value.getTestOidIri2());
            assertEquals(testOidIri3, value.getTestOidIri3());
        };

        assertDecodableVerifyAround(TestOidIris.class, value -> {
            value.setTestOidIri1(testOidIri1);
            value.setTestOidIri2(testOidIri2);
            value.setTestOidIri3(testOidIri3);
        }, verifier, verifier);
    }

    public <T extends ASN1IRI> T createIRI(ASN1IRI iri, String... components) {
        iri.setValue(components);

        return (T) iri;
    }

}

