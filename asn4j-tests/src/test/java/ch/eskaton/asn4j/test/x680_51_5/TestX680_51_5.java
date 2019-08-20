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

package ch.eskaton.asn4j.test.x680_51_5;

import ch.eskaton.asn4j.test.modules.x680_51_5.TestBitString1;
import ch.eskaton.asn4j.test.modules.x680_51_5.TestBitString10;
import ch.eskaton.asn4j.test.modules.x680_51_5.TestBitString11;
import ch.eskaton.asn4j.test.modules.x680_51_5.TestBitString12;
import ch.eskaton.asn4j.test.modules.x680_51_5.TestBitString2;
import ch.eskaton.asn4j.test.modules.x680_51_5.TestBitString3;
import ch.eskaton.asn4j.test.modules.x680_51_5.TestBitString4;
import ch.eskaton.asn4j.test.modules.x680_51_5.TestBitString5;
import ch.eskaton.asn4j.test.modules.x680_51_5.TestBitString6;
import ch.eskaton.asn4j.test.modules.x680_51_5.TestBitString7;
import ch.eskaton.asn4j.test.modules.x680_51_5.TestBitString8;
import ch.eskaton.asn4j.test.modules.x680_51_5.TestBitString9;
import ch.eskaton.asn4j.test.modules.x680_51_5.TestOctetString1;
import ch.eskaton.asn4j.test.modules.x680_51_5.TestOctetString10;
import ch.eskaton.asn4j.test.modules.x680_51_5.TestOctetString11;
import ch.eskaton.asn4j.test.modules.x680_51_5.TestOctetString12;
import ch.eskaton.asn4j.test.modules.x680_51_5.TestOctetString2;
import ch.eskaton.asn4j.test.modules.x680_51_5.TestOctetString3;
import ch.eskaton.asn4j.test.modules.x680_51_5.TestOctetString4;
import ch.eskaton.asn4j.test.modules.x680_51_5.TestOctetString5;
import ch.eskaton.asn4j.test.modules.x680_51_5.TestOctetString6;
import ch.eskaton.asn4j.test.modules.x680_51_5.TestOctetString7;
import ch.eskaton.asn4j.test.modules.x680_51_5.TestOctetString8;
import ch.eskaton.asn4j.test.modules.x680_51_5.TestOctetString9;
import org.junit.Test;

import static ch.eskaton.asn4j.test.TestHelper.randomBytes;
import static ch.eskaton.asn4j.test.TestHelper.testBitStringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testBitStringSuccess;
import static ch.eskaton.asn4j.test.TestHelper.testOctetStringFailure;
import static ch.eskaton.asn4j.test.TestHelper.testOctetStringSuccess;

public class TestX680_51_5 {

    @Test
    public void testBitString1() {
        testBitStringSuccess(TestBitString1.class, new TestBitString1(), 0x0, 0);
        testBitStringSuccess(TestBitString1.class, new TestBitString1(), 0xFFFFFFFFFFFFFFFFL, 0);
    }

    @Test
    public void testBitString2() {
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0x0F, 4);
        testBitStringSuccess(TestBitString2.class, new TestBitString2(), 0xFFFFFFFFFFFFFFFFL, 0);

        testBitStringFailure(new TestBitString2(), 0x07, 5);
    }

    @Test
    public void testBitString3() {
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x01, 7);
        testBitStringSuccess(TestBitString3.class, new TestBitString3(), 0x0F, 4);

        testBitStringFailure(new TestBitString3(), 0x1F, 3);
    }

    @Test
    public void testBitString4() {
        testBitStringSuccess(TestBitString4.class, new TestBitString4(), 0x02, 6);
        testBitStringSuccess(TestBitString4.class, new TestBitString4(), 0x3F, 2);

        testBitStringFailure(new TestBitString4(), 0x7F, 1);
    }

    @Test
    public void testBitString5() {
        testBitStringSuccess(TestBitString5.class, new TestBitString5(), 0x02, 6);
        testBitStringSuccess(TestBitString5.class, new TestBitString5(), 0xFF, 0);

        testBitStringFailure(new TestBitString5(), 0x01FF, 7);
    }

    @Test
    public void testBitString6() {
        testBitStringSuccess(TestBitString6.class, new TestBitString6(), 0x0F, 4);
        testBitStringSuccess(TestBitString6.class, new TestBitString6(), 0xFF, 0);

        testBitStringFailure(new TestBitString6(), 0x07, 5);
        testBitStringFailure(new TestBitString6(), 0x01FF, 7);
    }

    @Test
    public void testBitString7() {
        testBitStringSuccess(TestBitString7.class, new TestBitString7(), 0x0F, 4);
        testBitStringSuccess(TestBitString7.class, new TestBitString7(), 0x7F, 1);
        testBitStringSuccess(TestBitString7.class, new TestBitString7(), 0x01FFFFL, 7);

        testBitStringFailure(new TestBitString7(), 0xFF, 0);
        testBitStringFailure(new TestBitString7(), 0xFFFF, 0);
    }

    @Test
    public void testBitString8() {
        testBitStringSuccess(TestBitString8.class, new TestBitString8(), 0x0F, 4);
        testBitStringSuccess(TestBitString8.class, new TestBitString8(), 0xFF, 0);

        testBitStringFailure(new TestBitString8(), 0x07, 5);
        testBitStringFailure(new TestBitString8(), 0x01FF, 7);
    }

    @Test
    public void testBitString9() {
        testBitStringSuccess(TestBitString9.class, new TestBitString9(), 0x07, 5);
        testBitStringSuccess(TestBitString9.class, new TestBitString9(), 0x0F, 4);
        testBitStringSuccess(TestBitString9.class, new TestBitString9(), 0xFFF, 4);
        testBitStringSuccess(TestBitString9.class, new TestBitString9(), 0x1FFF, 3);
    }

    @Test
    public void testBitString10() {
        testBitStringSuccess(TestBitString10.class, new TestBitString10(), 0x00, 6);
        testBitStringSuccess(TestBitString10.class, new TestBitString10(), 0x01, 6);
        testBitStringSuccess(TestBitString10.class, new TestBitString10(), 0x02, 6);
        testBitStringSuccess(TestBitString10.class, new TestBitString10(), 0x03, 6);

        testBitStringFailure(new TestBitString10(), 0x01, 7);
        testBitStringFailure(new TestBitString10(), 0x04, 5);
    }

    @Test
    public void testBitString11() {
        testBitStringFailure(new TestBitString11(), 0x00, 7);
        testBitStringFailure(new TestBitString11(), 0x01, 7);

        testBitStringSuccess(TestBitString11.class, new TestBitString11(), 0x00, 8);
        testBitStringSuccess(TestBitString11.class, new TestBitString11(), 0x00, 6);
        testBitStringSuccess(TestBitString11.class, new TestBitString11(), 0x02, 6);
    }

    @Test
    public void testBitString12() {
        testBitStringFailure(new TestBitString12(), 0x00, 6);
        testBitStringFailure(new TestBitString12(), 0x01, 6);
        testBitStringFailure(new TestBitString12(), 0x02, 6);
        testBitStringFailure(new TestBitString12(), 0x03, 6);

        testBitStringSuccess(TestBitString12.class, new TestBitString12(), 0x00, 8);
        testBitStringSuccess(TestBitString12.class, new TestBitString12(), 0x00, 7);
        testBitStringSuccess(TestBitString12.class, new TestBitString12(), 0x02, 5);
    }


    @Test
    public void testOctetString1() {
        testOctetStringSuccess(TestOctetString1.class, new TestOctetString1(), new byte[] {});
        testOctetStringSuccess(TestOctetString1.class, new TestOctetString1(), randomBytes(255));
    }

    @Test
    public void testOctetString2() {
        testOctetStringSuccess(TestOctetString2.class, new TestOctetString2(), randomBytes(4));
        testOctetStringSuccess(TestOctetString2.class, new TestOctetString2(), randomBytes(255));

        testOctetStringFailure(new TestOctetString2(), randomBytes(3));
    }

    @Test
    public void testOctetString3() {
        testOctetStringSuccess(TestOctetString3.class, new TestOctetString3(), new byte[] {});
        testOctetStringSuccess(TestOctetString3.class, new TestOctetString3(), randomBytes(4));

        testOctetStringFailure(new TestOctetString3(), randomBytes(5));
    }

    @Test
    public void testOctetString4() {
        testOctetStringSuccess(TestOctetString4.class, new TestOctetString4(), randomBytes(2));
        testOctetStringSuccess(TestOctetString4.class, new TestOctetString4(), randomBytes(6));

        testOctetStringFailure(new TestOctetString4(), randomBytes(1));
        testOctetStringFailure(new TestOctetString4(), randomBytes(7));
    }

    @Test
    public void testOctetString5() {
        testOctetStringSuccess(TestOctetString5.class, new TestOctetString5(), randomBytes(2));
        testOctetStringSuccess(TestOctetString5.class, new TestOctetString5(), randomBytes(255));

        testOctetStringFailure(new TestOctetString5(), randomBytes(1));
    }

    @Test
    public void testOctetString6() {
        testOctetStringSuccess(TestOctetString6.class, new TestOctetString6(), randomBytes(4));
        testOctetStringSuccess(TestOctetString6.class, new TestOctetString6(), randomBytes(8));

        testOctetStringFailure(new TestOctetString6(), randomBytes(3));
        testOctetStringFailure(new TestOctetString6(), randomBytes(9));
    }

    @Test
    public void testOctetString7() {
        testOctetStringSuccess(TestOctetString7.class, new TestOctetString7(), randomBytes(4));
        testOctetStringSuccess(TestOctetString7.class, new TestOctetString7(), randomBytes(7));
        testOctetStringSuccess(TestOctetString7.class, new TestOctetString7(), randomBytes(17));

        testOctetStringFailure(new TestOctetString7(), randomBytes(3));
        testOctetStringFailure(new TestOctetString7(), randomBytes(8));
        testOctetStringFailure(new TestOctetString7(), randomBytes(16));
    }

    @Test
    public void testOctetString8() {
        testOctetStringSuccess(TestOctetString8.class, new TestOctetString8(), randomBytes(4));
        testOctetStringSuccess(TestOctetString8.class, new TestOctetString8(), randomBytes(8));

        testOctetStringFailure(new TestOctetString8(), randomBytes(3));
        testOctetStringFailure(new TestOctetString8(), randomBytes(9));
    }

    @Test
    public void testOctetString9() {
        testOctetStringSuccess(TestOctetString9.class, new TestOctetString9(), randomBytes(4));
        testOctetStringSuccess(TestOctetString9.class, new TestOctetString9(), randomBytes(16));

        testOctetStringFailure(new TestOctetString8(), randomBytes(3));
        testOctetStringFailure(new TestOctetString8(), randomBytes(17));
    }

    @Test
    public void testOctetString10() {
        testOctetStringSuccess(TestOctetString10.class, new TestOctetString10(), randomBytes(2));

        testOctetStringFailure(new TestOctetString10(), randomBytes(1));
        testOctetStringFailure(new TestOctetString10(), randomBytes(3));
    }

    @Test
    public void testOctetString11() {
        testOctetStringSuccess(TestOctetString11.class, new TestOctetString11(), new byte[] {});
        testOctetStringSuccess(TestOctetString11.class, new TestOctetString11(), randomBytes(2));
        testOctetStringSuccess(TestOctetString11.class, new TestOctetString11(), randomBytes(255));

        testOctetStringFailure(new TestOctetString11(), randomBytes(1));
    }

    @Test
    public void testOctetString12() {
        testOctetStringSuccess(TestOctetString12.class, new TestOctetString12(), new byte[] {});
        testOctetStringSuccess(TestOctetString12.class, new TestOctetString12(), randomBytes(1));
        testOctetStringSuccess(TestOctetString12.class, new TestOctetString12(), randomBytes(255));

        testOctetStringFailure(new TestOctetString12(), randomBytes(2));
    }

}
