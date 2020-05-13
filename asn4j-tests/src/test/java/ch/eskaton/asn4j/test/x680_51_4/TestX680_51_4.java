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

package ch.eskaton.asn4j.test.x680_51_4;

import ch.eskaton.asn4j.test.modules.x680_51_4.TestInteger1;
import ch.eskaton.asn4j.test.modules.x680_51_4.TestInteger2;
import ch.eskaton.asn4j.test.modules.x680_51_4.TestInteger3;
import ch.eskaton.asn4j.test.modules.x680_51_4.TestInteger4;
import ch.eskaton.asn4j.test.modules.x680_51_4.TestInteger5;
import ch.eskaton.asn4j.test.modules.x680_51_4.TestInteger6;
import ch.eskaton.asn4j.test.modules.x680_51_4.TestInteger7;
import org.junit.jupiter.api.Test;

import static ch.eskaton.asn4j.test.TestHelper.testIntegerFailure;
import static ch.eskaton.asn4j.test.TestHelper.testIntegerSuccess;

public class TestX680_51_4 {

    @Test
    public void testInteger1() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger1.class, 1, 2, 3, 4);
        testIntegerFailure(TestInteger1.class, 0, 5);
    }

    @Test
    public void testInteger2() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger2.class, 2, 3);
        testIntegerFailure(TestInteger2.class, 1, 4);
    }

    @Test
    public void testInteger3() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger3.class, Long.MIN_VALUE, 0);
        testIntegerFailure(TestInteger3.class, 1);
    }

    @Test
    public void testInteger4() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger4.class, Long.MIN_VALUE + 1, 0);
        testIntegerFailure(TestInteger4.class, Long.MIN_VALUE, 1);
    }

    @Test
    public void testInteger5() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger5.class, 0, Long.MAX_VALUE);
        testIntegerFailure(TestInteger5.class, -1);
    }

    @Test
    public void testInteger6() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger6.class, 0, Long.MAX_VALUE - 1);
        testIntegerFailure(TestInteger6.class, -1, Long.MAX_VALUE);
    }

    @Test
    public void testInteger7() throws InstantiationException, IllegalAccessException {
        testIntegerSuccess(TestInteger7.class, -4, 0);
        testIntegerFailure(TestInteger7.class, -5, 1);
    }

}
