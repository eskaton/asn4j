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

package ch.eskaton.asn4j.compiler;

import org.hamcrest.text.MatchesPattern;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.util.stream.Stream;

import static ch.eskaton.asn4j.test.TestUtils.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;

public class CompilerImplTest {

    public static final String MODULE_NAME = "TEST-MODULE";

    @SuppressWarnings("unused")
    @ParameterizedTest(name = "[{index}] {3}")
    @MethodSource("provideInvalidTypesInConstraintsArguments")
    public void testInvalidTypesInConstraints(String body, Class<? extends Exception> expected, String message, String description) {
        var module = module("TEST-MODULE", body);
        var exception = assertThrows(() -> new CompilerImpl()
                        .loadAndCompileModule(MODULE_NAME, new ByteArrayInputStream(module.getBytes())),
                expected);

        exception.ifPresent(e -> assertThat(e.getMessage(), MatchesPattern.matchesPattern(".*" + message + ".*")));
    }

    private static Stream<Arguments> provideInvalidTypesInConstraintsArguments() {
        return Stream.of(
                Arguments.of("""
                            Integer ::= INTEGER (1 | 2)
                            Boolean ::= BOOLEAN (INCLUDES Integer)
                        """, CompilerException.class, "can't be used in INCLUDES constraint of type BOOLEAN",
                        "Contained subtype must be derived of the same built-in type as the parent type"),
                Arguments.of("""
                            InvalidSizeType ::= BIT STRING
                            BitString ::= BIT STRING (SIZE (InvalidSizeType))
                        """, CompilerException.class, "can't be used in INCLUDES constraint of type INTEGER",
                        "Contained subtype in SIZE constraint must be of type INTEGER")
        );
    }

    public String module(String name, String body) {
        return """
                %s DEFINITIONS ::=
                BEGIN
                    %s
                END
                """.formatted(name, body);
    }

}
