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

package ch.eskaton.asn4j.runtime.utils;

import ch.eskaton.asn4j.runtime.Clazz;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class TLVUtilsTest {

    @Test
    void testGetTagClasses() {
        assertArrayEquals(new byte[] { (byte) 0x01 },
                TLVUtils.getTag(UniversalPrimitiveTest.class.getAnnotation(ASN1Tag.class), false));

        assertArrayEquals(new byte[] { (byte) 0x41 },
                TLVUtils.getTag(ApplicationPrimitiveTest.class.getAnnotation(ASN1Tag.class), false));

        assertArrayEquals(new byte[] { (byte) 0x81 }, TLVUtils
                .getTag(ContextSpecificPrimitiveTest.class.getAnnotation(ASN1Tag.class), false));

        assertArrayEquals(new byte[] { (byte) 0xC1 }, TLVUtils
                .getTag(PrivatePrimitiveTest.class.getAnnotation(ASN1Tag.class), false));

        assertArrayEquals(new byte[] { 0x21 }, TLVUtils
                .getTag(UniversalConstructedTest.class.getAnnotation(ASN1Tag.class), true));

        assertArrayEquals(new byte[] { 0x61 }, TLVUtils
                .getTag(ApplicationConstructedTest.class.getAnnotation(ASN1Tag.class), true));

        assertArrayEquals(new byte[] { (byte) 0xA1 }, TLVUtils
                .getTag(ContextSpecificConstructedTest.class.getAnnotation(ASN1Tag.class), true));

        assertArrayEquals(new byte[] { (byte) 0xE1 }, TLVUtils
                .getTag(PrivateConstructedTest.class.getAnnotation(ASN1Tag.class), true));
    }

    @Test
    void testGetTagNumber() {
        assertArrayEquals(new byte[] { (byte) 0xDE }, TLVUtils
                .getTag(PrivatePrimitive30Test.class.getAnnotation(ASN1Tag.class), false));

        assertArrayEquals(new byte[] { (byte) 0xDF, 0x1F }, TLVUtils
                .getTag(PrivatePrimitive31Test.class.getAnnotation(ASN1Tag.class), false));

        assertArrayEquals(new byte[] { (byte) 0xDF, (byte) 0x81, 0x00 }, TLVUtils
                .getTag(PrivatePrimitive128Test.class.getAnnotation(ASN1Tag.class), false));

        assertArrayEquals(new byte[] { (byte) 0xDF, (byte) 0xFF, 0x7F }, TLVUtils
                .getTag(PrivatePrimitive16383Test.class.getAnnotation(ASN1Tag.class), false));

        assertArrayEquals(new byte[] { (byte) 0xDF, (byte) 0x81, (byte) 0x80, 0x00 }, TLVUtils
                .getTag(PrivatePrimitive16384Test.class.getAnnotation(ASN1Tag.class), false));
    }

    @ASN1Tag(clazz = Clazz.UNIVERSAL, mode = ASN1Tag.Mode.IMPLICIT, tag = 1)
    private static class UniversalPrimitiveTest {

    }

    @ASN1Tag(clazz = Clazz.APPLICATION, mode = ASN1Tag.Mode.IMPLICIT, tag = 1)
    private static class ApplicationPrimitiveTest {

    }

    @ASN1Tag(clazz = Clazz.CONTEXT_SPECIFIC, mode = ASN1Tag.Mode.IMPLICIT, tag = 1)
    private static class ContextSpecificPrimitiveTest {

    }

    @ASN1Tag(clazz = Clazz.PRIVATE, mode = ASN1Tag.Mode.IMPLICIT, tag = 1)
    private static class PrivatePrimitiveTest {

    }

    @ASN1Tag(clazz = Clazz.UNIVERSAL, mode = ASN1Tag.Mode.IMPLICIT, tag = 1)
    private static class UniversalConstructedTest {

    }

    @ASN1Tag(clazz = Clazz.APPLICATION, mode = ASN1Tag.Mode.IMPLICIT, tag = 1)
    private static class ApplicationConstructedTest {

    }

    @ASN1Tag(clazz = Clazz.CONTEXT_SPECIFIC, mode = ASN1Tag.Mode.IMPLICIT, tag = 1)
    private static class ContextSpecificConstructedTest {

    }

    @ASN1Tag(clazz = Clazz.PRIVATE, mode = ASN1Tag.Mode.IMPLICIT, tag = 1)
    private static class PrivateConstructedTest {

    }

    @ASN1Tag(clazz = Clazz.PRIVATE, mode = ASN1Tag.Mode.IMPLICIT, tag = 30)
    private static class PrivatePrimitive30Test {

    }

    @ASN1Tag(clazz = Clazz.PRIVATE, mode = ASN1Tag.Mode.IMPLICIT, tag = 31)
    private static class PrivatePrimitive31Test {

    }

    @ASN1Tag(clazz = Clazz.PRIVATE, mode = ASN1Tag.Mode.IMPLICIT, tag = 128)
    private static class PrivatePrimitive128Test {

    }

    @ASN1Tag(clazz = Clazz.PRIVATE, mode = ASN1Tag.Mode.IMPLICIT, tag = 16383)
    private static class PrivatePrimitive16383Test {

    }

    @ASN1Tag(clazz = Clazz.PRIVATE, mode = ASN1Tag.Mode.IMPLICIT, tag = 16384)
    private static class PrivatePrimitive16384Test {

    }

}
