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

package ch.eskaton.asn4j.test.x680_31;

import ch.eskaton.asn4j.runtime.BERDecoder;
import ch.eskaton.asn4j.runtime.BEREncoder;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1VisibleString;
import ch.eskaton.asn4jtest.x680_30.TestSelectionSetType;
import ch.eskaton.asn4jtest.x680_31.TestPrefixedType1;
import ch.eskaton.asn4jtest.x680_31.TestPrefixedType2;
import ch.eskaton.asn4jtest.x680_31.TestPrefixedType3;
import ch.eskaton.asn4jtest.x680_31.TestPrefixedType4;
import ch.eskaton.asn4jtest.x680_31.TestPrefixedType5;
import ch.eskaton.asn4jtest.x680_31.TestPrefixedType6;
import org.junit.Test;

import java.util.function.Supplier;

import static ch.eskaton.asn4jtest.x680_30.TestSelectionSetType.SelectedType1;
import static org.junit.Assert.assertEquals;

public class TestX680_31 {

    private <T extends  ASN1VisibleString> void testPrefixedType(Supplier<T> prefixedType, String value) {
        T a = prefixedType.get();
        a.setValue(value);

        BEREncoder encoder = new BEREncoder();
        BERDecoder decoder = new BERDecoder();

        T b = decoder.decode((Class<T>) a.getClass(), encoder.encode(a));

        assertEquals(a, b);
    }

    @Test
    public void testTestPrefixedType1() {
        testPrefixedType(() -> new TestPrefixedType1(), "test1");
    }

    @Test
    public void testTestPrefixedType2() {
        testPrefixedType(() -> new TestPrefixedType2(), "test2");
    }

    @Test
    public void testTestPrefixedType3() {
        testPrefixedType(() -> new TestPrefixedType3(), "test3");
    }

    @Test
    public void testTestPrefixedType4() {
        testPrefixedType(() -> new TestPrefixedType4(), "test4");
    }

    @Test
    public void testTestPrefixedType5() {
        testPrefixedType(() -> new TestPrefixedType5(), "test5");
    }

    @Test
    public void testTestPrefixedType6() {
        testPrefixedType(() -> new TestPrefixedType6(), "test6");
    }

}