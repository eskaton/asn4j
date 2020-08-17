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

import ch.eskaton.asn4j.compiler.results.CompiledCollectionType;
import ch.eskaton.asn4j.parser.ParserException;
import ch.eskaton.asn4j.parser.ast.types.BooleanType;
import ch.eskaton.asn4j.parser.ast.types.OpenType;
import ch.eskaton.asn4j.runtime.types.TypeName;
import ch.eskaton.commons.utils.Utils;
import org.hamcrest.text.MatchesPattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.stream.Stream;

import static ch.eskaton.asn4j.runtime.types.TypeName.BIT_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.BOOLEAN;
import static ch.eskaton.asn4j.runtime.types.TypeName.ENUMERATED;
import static ch.eskaton.asn4j.runtime.types.TypeName.INTEGER;
import static ch.eskaton.asn4j.runtime.types.TypeName.NULL;
import static ch.eskaton.asn4j.runtime.types.TypeName.OCTET_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.OID;
import static ch.eskaton.asn4j.runtime.types.TypeName.OID_IRI;
import static ch.eskaton.asn4j.runtime.types.TypeName.RELATIVE_OID;
import static ch.eskaton.asn4j.runtime.types.TypeName.RELATIVE_OID_IRI;
import static ch.eskaton.asn4j.runtime.types.TypeName.SEQUENCE;
import static ch.eskaton.asn4j.runtime.types.TypeName.SEQUENCE_OF;
import static ch.eskaton.asn4j.runtime.types.TypeName.SET_OF;
import static ch.eskaton.asn4j.test.TestUtils.assertThrows;
import static ch.eskaton.asn4j.test.TestUtils.module;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompilerImplTest {

    public static final String MODULE_NAME = "TEST-MODULE";

    @SuppressWarnings("unused")
    @ParameterizedTest(name = "[{index}] {3}")
    @MethodSource("provideInvalidTypesInConstraintsArguments")
    void testInvalidTypesInConstraints(String body, Class<? extends Exception> expected, String message,
            String description) {
        testModule(body, expected, ".*" + message + ".*");
    }

    @SuppressWarnings("unused")
    @ParameterizedTest(name = "[{index}] {3}")
    @MethodSource("provideInvalidMultipleTypeConstraints")
    void testInvalidMultipleTypeConstraints(String body, Class<? extends Exception> expected, String message,
            String description) {
        testModule(body, expected, ".*" + message + ".*");
    }

    @Test
    void testForbiddenAbsentPresenceConstraint() {
        var body = """
                Seq ::= SEQUENCE {
                    a INTEGER
                } (WITH COMPONENTS {a ABSENT})
                """;

        testModule(body, CompilerException.class,
                ".*isn't optional and therefore can't have a presence constraint of ABSENT.*");
    }

    @ParameterizedTest(name = "[{index}] {1}")
    @MethodSource("provideSetsWithDuplicateTags")
    void testDuplicateTagsInSet(String body, String description) {
        testModule(body, CompilerException.class, "Duplicate tags:.*");
    }

    @Test
    void testDuplicateTagsInChoice() {
        var body = """
                Choice ::= CHOICE {
                    a INTEGER,
                    b INTEGER
                }
                """;

        testModule(body, CompilerException.class, "Duplicate tags in CHOICE.*");
    }

    @Test
    void testRedefinitionOfType() {
        var body = """
                Integer ::= INTEGER
                Integer ::= INTEGER
                """;

        testModule(body, CompilerException.class, "Type Integer is already defined");
    }

    @Test
    void testInvalidVisibleString() {
        var body = """
                Sequence ::= SEQUENCE {
                    a VisibleString DEFAULT "é"
                }
                """;

        testModule(body, CompilerException.class, ".*VisibleString contains invalid characters.*");
    }

    @Test
    void testInvalidNumericString() {
        var body = """
                Sequence ::= SEQUENCE {
                    a NumericString DEFAULT "a"
                }
                """;

        testModule(body, CompilerException.class, ".*NumericString contains invalid characters.*");
    }

    @Test
    void testObjectSetDuplicateValue() {
        var body = """
                TEST ::= CLASS {
                    &id   INTEGER UNIQUE,
                    &desc VisibleString
                }

                TestSet TEST ::= {
                      {&id 1, &desc "Desc 1"}
                    | {&id 2, &desc "Desc 2"}
                    | {&id 1, &desc "Desc 3"}
                }
                """;

        testModule(body, CompilerException.class, ".*Duplicate value in object set.*");
    }

    @Test
    void testObjectSetMissingValue() {
        var body = """
                TEST ::= CLASS {
                    &id   INTEGER UNIQUE,
                    &desc VisibleString
                }

                TestSet TEST ::= {
                      {&id 1}
                }
                """;

        testModule(body, CompilerException.class, ".*Field 'desc' is mandatory.*");
    }

    @Test
    void testObjectSetInvalidValue() {
        var body = """
                TEST ::= CLASS {
                    &id   INTEGER UNIQUE,
                    &desc VisibleString
                }

                TestSet TEST ::= {
                      {&id "abcd"}
                }
                """;

        testModule(body, CompilerException.class, ".*Failed to resolve an INTEGER value.*");
    }

    @Test
    void testObjects() throws IOException, ParserException {
        var body = """
                TEST ::= CLASS {
                    &booleanField  BOOLEAN,
                    &intField      INTEGER
                }

                testObject1 TEST ::= {
                    &booleanField  FALSE,
                    &intField      23
                }

                testObject2 TEST ::= {
                    &booleanField  TRUE,
                    &intField      47
                }

                TestSet TEST ::= {
                    testObject1 | testObject2
                }
                """;

        var module = module("TEST-MODULE", body);
        var compiler = new CompilerImpl();

        compiler.loadAndCompileModule(MODULE_NAME, new ByteArrayInputStream(module.getBytes()));

        var ctx = compiler.getCompilerContext();
        var objectSet = ctx.getCompiledModule("TEST-MODULE").getObjectSets().get("TestSet");

        assertNotNull(objectSet);
        assertEquals(2, objectSet.getValues().size());
    }

    @Test
    void testNestedFields() throws IOException, ParserException {
        var body = """
                TEST ::= CLASS {
                    &booleanField  BOOLEAN
                }
                                
                TEST2 ::= CLASS {
                    &test TEST
                }
                            
                TestSequence ::= SEQUENCE {
                    typeField TEST2.&test.&booleanField
                }
                """;

        var module = module("TEST-MODULE", body);
        var compiler = new CompilerImpl();

        compiler.loadAndCompileModule(MODULE_NAME, new ByteArrayInputStream(module.getBytes()));

        var ctx = compiler.getCompilerContext();
        var compiledType = ctx.getCompiledModule("TEST-MODULE").getTypes().get("TestSequence");

        assertNotNull(compiledType);
        assertTrue(compiledType instanceof CompiledCollectionType);

        var compiledSequence = (CompiledCollectionType) compiledType;

        assertEquals(1, compiledSequence.getComponents().size());

        var compiledComponent = compiledSequence.getComponents().get(0).get_2();

        assertTrue(compiledComponent.getType() instanceof BooleanType);
    }

    @Test
    void testNestedFieldsInvalidReference() {
        var body = """
                TEST ::= CLASS {
                    &test  BOOLEAN
                }
                 
                TestSequence ::= SEQUENCE {
                    typeField TEST.&test.&booleanField
                }
                """;

        testModule(body, CompilerException.class, ".*&test doesn't refer to an object class.*");
    }

    @Test
    void testFixedTypeValueFieldInvalidDefault() {
        var body = """
                TEST ::= CLASS {
                    &test  INTEGER UNIQUE DEFAULT 10
                }
                """;

        testModule(body, CompilerException.class,
                ".*Default value on field test in object class TEST not allowed because it's unique.*");
    }

    @Test
    void testTypeField() throws IOException, ParserException {
        var body = """
                TEST ::= CLASS {
                    &TypeField
                }

                TestSequence ::= SEQUENCE {
                    typeField TEST.&TypeField
                }
                """;

        var module = module("TEST-MODULE", body);
        var compiler = new CompilerImpl();

        compiler.loadAndCompileModule(MODULE_NAME, new ByteArrayInputStream(module.getBytes()));

        var ctx = compiler.getCompilerContext();
        var compiledType = ctx.getCompiledModule("TEST-MODULE").getTypes().get("TestSequence");

        assertNotNull(compiledType);
        assertTrue(compiledType instanceof CompiledCollectionType);

        var compiledSequence = (CompiledCollectionType) compiledType;

        assertEquals(1, compiledSequence.getComponents().size());

        var compiledComponent = compiledSequence.getComponents().get(0).get_2();

        assertTrue(compiledComponent.getType() instanceof OpenType);
    }

    private void testModule(String body, Class<? extends Exception> expected, String message) {
        var module = module("TEST-MODULE", body);
        var exception = assertThrows(() -> new CompilerImpl()
                        .loadAndCompileModule(MODULE_NAME, new ByteArrayInputStream(module.getBytes())),
                expected);

        exception.ifPresent(e -> assertThat(Utils.rootCause(e).getMessage(), MatchesPattern.matchesPattern(message)));
    }

    private static Stream<Arguments> provideInvalidTypesInConstraintsArguments() {
        // @formatter:off
        return Stream.of(
                Arguments.of("""
                            Integer ::= INTEGER (1 | 2)
                            Boolean ::= BOOLEAN (INCLUDES Integer)
                        """, CompilerException.class, getContainedSubtypeError(BOOLEAN),
                        getContainedSubtypeDescription(BOOLEAN)),
               Arguments.of("""
                            Integer ::= INTEGER (1 | 2)
                            BitString ::= BIT STRING (INCLUDES Integer)
                        """, CompilerException.class, getContainedSubtypeError(BIT_STRING),
                        getContainedSubtypeDescription(BIT_STRING)),
               Arguments.of("""
                            BitString ::= BIT STRING
                            Integer ::= INTEGER (INCLUDES BitString)
                        """, CompilerException.class, getContainedSubtypeError(INTEGER),
                        getContainedSubtypeDescription(INTEGER)),
               Arguments.of("""
                            Integer ::= INTEGER (1 | 2)
                            Enumeration ::= ENUMERATED { a, b } (INCLUDES Integer)
                        """, CompilerException.class, getContainedSubtypeError(ENUMERATED),
                        getContainedSubtypeDescription(ENUMERATED)),
               Arguments.of("""
                            Integer ::= INTEGER (1 | 2)
                            OctetString ::= OCTET STRING (INCLUDES Integer)
                        """, CompilerException.class, getContainedSubtypeError(OCTET_STRING),
                        getContainedSubtypeDescription(OCTET_STRING)),
               Arguments.of("""
                            Integer ::= INTEGER (1 | 2)
                            Null ::= NULL (INCLUDES Integer)
                        """, CompilerException.class, getContainedSubtypeError(NULL),
                        getContainedSubtypeDescription(NULL)),
               Arguments.of("""
                            Integer ::= INTEGER (1 | 2)
                            RelativeOID ::= RELATIVE-OID (INCLUDES Integer)
                        """, CompilerException.class, getContainedSubtypeError(RELATIVE_OID),
                        getContainedSubtypeDescription(RELATIVE_OID)),
               Arguments.of("""
                            Integer ::= INTEGER (1 | 2)
                            Oid ::= OBJECT IDENTIFIER (INCLUDES Integer)
                        """, CompilerException.class, getContainedSubtypeError(OID),
                        getContainedSubtypeDescription(OID)),
               Arguments.of("""
                            Integer ::= INTEGER (1 | 2)
                            RelativeOidIri ::= RELATIVE-OID-IRI (INCLUDES Integer)
                        """, CompilerException.class, getContainedSubtypeError(RELATIVE_OID_IRI),
                        getContainedSubtypeDescription(RELATIVE_OID_IRI)),
               Arguments.of("""
                            Integer ::= INTEGER (1 | 2)
                            OidIri ::= OID-IRI (INCLUDES Integer)
                        """, CompilerException.class, getContainedSubtypeError(OID_IRI),
                        getContainedSubtypeDescription(OID_IRI)),
               Arguments.of("""
                            Integer ::= INTEGER (1 | 2)
                            Sequence ::= SEQUENCE { a INTEGER } (INCLUDES Integer)
                        """, CompilerException.class, getContainedSubtypeError(SEQUENCE),
                        getContainedSubtypeDescription(SEQUENCE)),
               Arguments.of("""
                            Integer ::= INTEGER (1 | 2)
                            SequenceOf ::= SEQUENCE (INCLUDES Integer) OF INTEGER
                        """, CompilerException.class, getContainedSubtypeError(SEQUENCE_OF),
                        getContainedSubtypeDescription(SEQUENCE_OF)),
               Arguments.of("""
                            Integer ::= INTEGER (1 | 2)
                            Set ::= SET (INCLUDES Integer) OF INTEGER
                        """, CompilerException.class, getContainedSubtypeError(SET_OF),
                        getContainedSubtypeDescription(SET_OF)),

               Arguments.of("""
                            InvalidSizeType ::= BIT STRING
                            BitString ::= BIT STRING (SIZE (InvalidSizeType))
                        """, CompilerException.class, getContainedSubtypeError(INTEGER),
                        "Contained subtype in SIZE constraint must be of type INTEGER")
        );
        // @formatter:on
    }

    private static Stream<Arguments> provideInvalidMultipleTypeConstraints() {
        // @formatter:off
        return Stream.of(
                Arguments.of("""
                            Sequence ::= SEQUENCE {
                                a INTEGER,
                                b BOOLEAN
                            } (WITH COMPONENTS {b (TRUE), a (1)})
                        """, CompilerException.class, "Component 'a' not found in type 'Sequence'",
                        "Test invalid order of components in SEQUENCE"),
                Arguments.of("""
                            Sequence ::= SEQUENCE {
                                a INTEGER
                            } (WITH COMPONENTS {a (1), b (TRUE)})
                        """, CompilerException.class, "Component 'b' not found in type 'Sequence'",
                        "Test inexistent components in SEQUENCE"),
                Arguments.of("""
                            Sequence ::= SEQUENCE {
                                a INTEGER,
                                b BOOLEAN
                            } (WITH COMPONENTS {a (1), b (TRUE), a(2)})
                        """, CompilerException.class, "Duplicate element 'a' found in type 'Sequence'",
                        "Test duplicate components in SEQUENCE"),
                Arguments.of("""
                            Set ::= SET {
                                a INTEGER
                            } (WITH COMPONENTS {a (1), b (TRUE)})
                        """, CompilerException.class, "Component 'b' not found in type 'Set'",
                        "Test inexistent components in SET"),
                Arguments.of("""
                            Set ::= SET {
                                a INTEGER,
                                b BOOLEAN
                            } (WITH COMPONENTS {a (1), b (TRUE), a(2)})
                        """, CompilerException.class, "Duplicate element 'a' found in type 'Set'",
                        "Test duplicate components in SET")
        );
        // @formatter:on
    }

    private static Stream<Arguments> provideSetsWithDuplicateTags() {
        // @formatter:off
        return Stream.of(
                Arguments.of("""
                            Set ::= SET {
                                a INTEGER,
                                b INTEGER
                            }
                        """,
                        "Test duplicate tags with equal types in SET"),
                Arguments.of("""
                            Set1 ::= SET {
                                a INTEGER,
                                b NULL
                            }
                            Set2 ::= SET {
                                COMPONENTS OF Set1,
                                c INTEGER
                            }
                        """,
                        "Test duplicate tags with referenced components in SET"),
                Arguments.of("""
                            Integer ::= INTEGER
                            Set ::= SET {
                                a Integer,
                                b INTEGER
                            }
                        """,
                        "Test duplicate tags with user defined type in SET"),
                Arguments.of("""
                            Integer ::= [3] INTEGER
                            Set ::= SET {
                                a Integer,
                                b [3] NULL
                            }
                        """,
                        "Test duplicate tags with tagged type in SET")
        );
        // @formatter:on
    }

    private static String getContainedSubtypeError(TypeName type) {
        return "can't be used in INCLUDES constraint of type %s".formatted(type);
    }

    private static String getContainedSubtypeDescription(TypeName type) {
        return "Contained subtype for %s must be derived of the same built-in type as the parent type"
                .formatted(type);
    }

}
