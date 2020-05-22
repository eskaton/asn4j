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

package ch.eskaton.asn4j.test.x680_35;

import ch.eskaton.asn4j.runtime.types.ASN1RelativeIRI;
import ch.eskaton.asn4j.test.modules.x680_35.TestRelativeOidIris;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static ch.eskaton.asn4j.test.TestHelper.assertDecodableVerifyAround;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestX680_35 {

    @Test
    void testRelativeIRIsWithDefaults() {
        ASN1RelativeIRI testRelativeOidIri1 = createRelativeIRI(new ASN1RelativeIRI(), "Registration-Authority", "19785.CBEFF", "Organizations");
        ASN1RelativeIRI testRelativeOidIri2 = createRelativeIRI(new ASN1RelativeIRI(), "Registration-Authority", "Test");
        ASN1RelativeIRI testRelativeOidIri3 = createRelativeIRI(new ASN1RelativeIRI(), "19785.CBEFF");

        Consumer<TestRelativeOidIris> verifier = value -> {
            assertEquals(testRelativeOidIri1, value.getTestRelativeOidIri1());
            assertEquals(testRelativeOidIri2, value.getTestRelativeOidIri2());
            assertEquals(testRelativeOidIri3, value.getTestRelativeOidIri3());
        };

        assertDecodableVerifyAround(TestRelativeOidIris.class, verifier, verifier);
    }

    @Test
    void testRelativeIRIsWithoutDefaults() {
        ASN1RelativeIRI testRelativeOidIri1 = createRelativeIRI(new ASN1RelativeIRI(), "Registration-Authority", "19785.CBEFF", "Organizations");
        ASN1RelativeIRI testRelativeOidIri2 = createRelativeIRI(new ASN1RelativeIRI(), "Registration-Authority", "Test");
        ASN1RelativeIRI testRelativeOidIri3 = createRelativeIRI(new ASN1RelativeIRI(), "19785.CBEFF");

        Consumer<TestRelativeOidIris> verifier = value -> {
            assertEquals(testRelativeOidIri1, value.getTestRelativeOidIri1());
            assertEquals(testRelativeOidIri2, value.getTestRelativeOidIri2());
            assertEquals(testRelativeOidIri3, value.getTestRelativeOidIri3());
        };

        assertDecodableVerifyAround(TestRelativeOidIris.class, value -> {
                    value.setTestRelativeOidIri1(testRelativeOidIri1);
                    value.setTestRelativeOidIri2(testRelativeOidIri2);
                    value.setTestRelativeOidIri3(testRelativeOidIri3);
                }, verifier, verifier

        );
    }

    public <T extends ASN1RelativeIRI> T createRelativeIRI(ASN1RelativeIRI iri, String... components) {
        iri.setValue(components);

        return (T) iri;
    }

}

