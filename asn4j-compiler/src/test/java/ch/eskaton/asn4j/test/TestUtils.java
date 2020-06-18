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

package ch.eskaton.asn4j.test;

import ch.eskaton.commons.utils.CollectionUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.eskaton.commons.utils.CollectionUtils.asLinkedList;
import static ch.eskaton.commons.utils.Utils.rootCause;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class TestUtils {

    private TestUtils() {
    }

    public static Optional<Exception> assertThrows(ExceptionAction action, Class<? extends Exception> exception) {
        try {
            action.execute();
            fail(exception.getSimpleName() + " expected");
        } catch (Exception e) {
            assertTrue(exception.isAssignableFrom(rootCause(e).getClass()),
                    () -> String.format("Expected an exception of type %s but was %s",
                            exception.getSimpleName(), rootCause(e).getClass().getSimpleName()));

            return Optional.of(e);
        }

        return Optional.empty();
    }

    public static Optional<Exception> assertThrows(ExceptionAction action, Class<? extends Exception> exception, String message) {
        try {
            action.execute();
            fail(exception.getSimpleName() + " expected. " + message);
        } catch (Exception e) {
            assertTrue(exception.isAssignableFrom(e.getClass()));

            return Optional.of(e);
        }

        return Optional.empty();
    }

    public static void assertSetEncodingEquals(byte[] encoded, Byte[]... expectedValues) {
        byte[] header = new byte[2];
        Set<LinkedList<Byte>> values = new HashSet<>();

        System.arraycopy(encoded, 0, header, 0, 2);

        for (int i = 2; i < encoded.length; ) {
            int len = encoded[i + 1] + 2; // for simplicity we assume that test values are shorter than 128
            byte[] value = new byte[len];

            System.arraycopy(encoded, i, value, 0, len);

            values.add(asLinkedList(CollectionUtils.box(value)));

            i += len;
        }

        Set<LinkedList<Byte>> expected = CollectionUtils.asHashSet(expectedValues).stream()
                .map(e -> asLinkedList(e)).collect(Collectors.toSet());

        int expectedLen = expected.stream().map(e -> e.stream().collect(Collectors.counting()))
                .mapToInt(x -> x.intValue()).sum();

        assertArrayEquals(new byte[] { 0x31, (byte) expectedLen }, header);
        assertEquals(expected, values);
    }

    public static String module(String name, String body) {
        return """
                %s DEFINITIONS ::=
                BEGIN
                    %s
                END
                """.formatted(name, body);
    }

}
