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

package ch.eskaton.asn4j.test.x680_13;

import ch.eskaton.asn4j.test.modules.x680_13.TestEnumeratedType1;
import ch.eskaton.asn4j.test.modules.x680_13.TestEnumeratedType2;
import ch.eskaton.asn4j.test.modules.x680_13.TestEnumeratedType3;
import ch.eskaton.asn4j.test.modules.x680_13.TestEnumeratedType4;
import ch.eskaton.asn4j.test.modules.x680_13.TestEnumeratedType5;
import ch.eskaton.asn4j.test.modules.x680_13.TestEnumeratedType6;
import ch.eskaton.asn4j.test.modules.x680_13.TestSequence1;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestX680_13 {

    @Test
    public void testImportsAndExtReferencesCompiled() {
        new TestEnumeratedType1();
        new TestEnumeratedType2();
        new TestEnumeratedType3();
        new TestEnumeratedType4();
        new TestEnumeratedType5();
        new TestEnumeratedType6();

        TestSequence1 sequence = new TestSequence1();

        assertEquals(4711, sequence.getA().getValue().intValue());
        assertEquals(4712, sequence.getB().getValue().intValue());
        assertEquals(4713, sequence.getC().getValue().intValue());
    }

}
