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

import ch.eskaton.asn4j.compiler.results.CompiledChoiceType;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.compiler.results.CompiledVariableTypeValueField;
import ch.eskaton.asn4j.parser.ParserException;
import ch.eskaton.asn4j.parser.ast.types.BMPString;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.BooleanType;
import ch.eskaton.asn4j.parser.ast.types.EnumeratedType;
import ch.eskaton.asn4j.parser.ast.types.GeneralString;
import ch.eskaton.asn4j.parser.ast.types.GeneralizedTime;
import ch.eskaton.asn4j.parser.ast.types.GraphicString;
import ch.eskaton.asn4j.parser.ast.types.IA5String;
import ch.eskaton.asn4j.parser.ast.types.IRI;
import ch.eskaton.asn4j.parser.ast.types.ISO646String;
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
import ch.eskaton.asn4j.parser.ast.types.Null;
import ch.eskaton.asn4j.parser.ast.types.NumericString;
import ch.eskaton.asn4j.parser.ast.types.ObjectIdentifier;
import ch.eskaton.asn4j.parser.ast.types.OctetString;
import ch.eskaton.asn4j.parser.ast.types.OpenType;
import ch.eskaton.asn4j.parser.ast.types.PrintableString;
import ch.eskaton.asn4j.parser.ast.types.Real;
import ch.eskaton.asn4j.parser.ast.types.RelativeIRI;
import ch.eskaton.asn4j.parser.ast.types.RelativeOID;
import ch.eskaton.asn4j.parser.ast.types.SequenceOfType;
import ch.eskaton.asn4j.parser.ast.types.SequenceType;
import ch.eskaton.asn4j.parser.ast.types.SetOfType;
import ch.eskaton.asn4j.parser.ast.types.SetType;
import ch.eskaton.asn4j.parser.ast.types.T61String;
import ch.eskaton.asn4j.parser.ast.types.TeletexString;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.UTCTime;
import ch.eskaton.asn4j.parser.ast.types.UTF8String;
import ch.eskaton.asn4j.parser.ast.types.UniversalString;
import ch.eskaton.asn4j.parser.ast.types.VideotexString;
import ch.eskaton.asn4j.parser.ast.types.VisibleString;
import ch.eskaton.asn4j.runtime.Clazz;
import ch.eskaton.asn4j.runtime.TagId;
import ch.eskaton.asn4j.runtime.types.TypeName;
import ch.eskaton.commons.utils.Utils;
import org.hamcrest.text.MatchesPattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static ch.eskaton.asn4j.runtime.types.TypeName.BIT_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.BMP_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.BOOLEAN;
import static ch.eskaton.asn4j.runtime.types.TypeName.ENUMERATED;
import static ch.eskaton.asn4j.runtime.types.TypeName.GENERALIZED_TIME;
import static ch.eskaton.asn4j.runtime.types.TypeName.GENERAL_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.GRAPHIC_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.IA5_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.INTEGER;
import static ch.eskaton.asn4j.runtime.types.TypeName.ISO646_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.NULL;
import static ch.eskaton.asn4j.runtime.types.TypeName.NUMERIC_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.OBJECT_IDENTIFIER;
import static ch.eskaton.asn4j.runtime.types.TypeName.OCTET_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.OID;
import static ch.eskaton.asn4j.runtime.types.TypeName.OID_IRI;
import static ch.eskaton.asn4j.runtime.types.TypeName.PRINTABLE_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.REAL;
import static ch.eskaton.asn4j.runtime.types.TypeName.RELATIVE_OID;
import static ch.eskaton.asn4j.runtime.types.TypeName.RELATIVE_OID_IRI;
import static ch.eskaton.asn4j.runtime.types.TypeName.SEQUENCE;
import static ch.eskaton.asn4j.runtime.types.TypeName.SEQUENCE_OF;
import static ch.eskaton.asn4j.runtime.types.TypeName.SET_OF;
import static ch.eskaton.asn4j.runtime.types.TypeName.T61_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.TELETEX_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.UNIVERSAL_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.UTC_TIME;
import static ch.eskaton.asn4j.runtime.types.TypeName.UTF8_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.VIDEOTEX_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.VISIBLE_STRING;
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

    @ParameterizedTest(name = "[{index}] {3}")
    @MethodSource("provideTagsOnComponents")
    void testTagsOnComponents(Class<? extends Type> type, String typeName, TagId tag, String description)
            throws IOException, ParserException {
        var body = """
                Seq ::= SEQUENCE {
                    component %s
                }
                """.formatted(typeName);

        var module = module("TEST-MODULE", body);
        var compiler = new CompilerImpl();

        compiler.loadAndCompileModule(MODULE_NAME, new ByteArrayInputStream(module.getBytes()));

        var ctx = compiler.getCompilerContext();
        var compiledType = ctx.getCompiledModule("TEST-MODULE").getTypes().get("Seq");

        assertNotNull(compiledType);
        assertTrue(compiledType instanceof CompiledCollectionType);

        var compiledSequence = (CompiledCollectionType) compiledType;

        assertEquals(1, compiledSequence.getComponents().size());

        var compiledComponent = compiledSequence.getComponents().get(0).get_2();

        assertTrue(compiledComponent.getType().getClass().isAssignableFrom(type));
        assertTrue(compiledComponent.getTags().isPresent());
        assertEquals(1, compiledComponent.getTags().get().size());
        assertEquals(tag, compiledComponent.getTags().get().get(0));
    }

    @ParameterizedTest(name = "[{index}] {2}")
    @MethodSource("provideCustomTagsOnComponentsExplicit")
    void testCustomTagsOnComponentsExplicit(String prefixedType, List<TagId> tags, String description)
            throws IOException, ParserException {
        testCustomTagsOnComponents(prefixedType, tags, false);
    }

    @ParameterizedTest(name = "[{index}] {2}")
    @MethodSource("provideCustomTagsOnComponentsImplicit")
    void testCustomTagsOnComponentsImplicit(String prefixedType, List<TagId> tags, String description)
            throws IOException, ParserException {
        testCustomTagsOnComponents(prefixedType, tags, true);
    }

    private void testCustomTagsOnComponents(String prefixedType, List<TagId> tags, boolean implicit)
            throws IOException, ParserException {
        var body = """
                Seq ::= SEQUENCE {
                    component %s
                }
                """.formatted(prefixedType);

        var module = module("TEST-MODULE", body, implicit);
        var compiler = new CompilerImpl();

        compiler.loadAndCompileModule(MODULE_NAME, new ByteArrayInputStream(module.getBytes()));

        var ctx = compiler.getCompilerContext();
        var compiledType = ctx.getCompiledModule("TEST-MODULE").getTypes().get("Seq");

        assertNotNull(compiledType);
        assertTrue(compiledType instanceof CompiledCollectionType);

        var compiledSequence = (CompiledCollectionType) compiledType;

        assertEquals(1, compiledSequence.getComponents().size());

        var compiledComponent = compiledSequence.getComponents().get(0).get_2();

        assertTrue(compiledComponent.getTags().isPresent());
        assertEquals(tags.size(), compiledComponent.getTags().get().size());
        assertEquals(tags, compiledComponent.getTags().get());
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
        testModule(body, CompilerException.class, "Duplicate tags in SET .*:.*");
    }

    @Test
    void testDuplicateTagsInChoice() {
        var body = """
                Choice ::= CHOICE {
                    a INTEGER,
                    b INTEGER
                }
                """;

        testModule(body, CompilerException.class, "Duplicate tags.*");
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
    void testObjectSetTypeValue() throws IOException, ParserException {
        var body = """
                TEST ::= CLASS {
                    &TypeField
                }

                TestSet TEST ::= {
                    {&TypeField BOOLEAN}
                }
                """;

        var module = module("TEST-MODULE", body);
        var compiler = new CompilerImpl();

        compiler.loadAndCompileModule(MODULE_NAME, new ByteArrayInputStream(module.getBytes()));

        var ctx = compiler.getCompilerContext();
        var objectSet = ctx.getCompiledModule("TEST-MODULE").getObjectSets().get("TestSet");

        assertNotNull(objectSet);
        assertEquals(1, objectSet.getValues().size());

        var value = objectSet.getValues().stream().findFirst().get();

        assertTrue(value.containsKey("TypeField"));
        assertTrue(value.get("TypeField") instanceof CompiledType);
        assertTrue(((CompiledType) value.get("TypeField")).getType() instanceof BooleanType);
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

    @Test
    void testTypeFieldInvalidOptionalInSequence() {
        var body = """
                TEST ::= CLASS {
                    &TypeField
                }
                                
                TestSequence ::= SEQUENCE {
                   typeField TEST.&TypeField OPTIONAL,
                   intField  INTEGER
                }
                """;

        testModule(body, CompilerException.class,
                ".*TestSequence contains the optional open type typeField which is ambiguous.*");
    }

    @Test
    void testTypeFieldValidOptionalInSequence() throws IOException, ParserException {
        var body = """
                TEST ::= CLASS {
                    &TypeField
                }
                                
                TestSequence ::= SEQUENCE {
                   intField  INTEGER,
                   typeField TEST.&TypeField OPTIONAL
                }
                """;

        testCompiledCollection(body, "TestSequence");
    }

    @Test
    void testTypeFieldInvalidInSet() {
        var body = """
                TEST ::= CLASS {
                    &TypeField
                }
                                
                TestSet ::= SET {
                   intField  INTEGER,
                   typeField TEST.&TypeField
                }
                """;

        testModule(body, CompilerException.class,
                ".*TestSet contains the open type typeField which is ambiguous.*");

        body = """
                TEST ::= CLASS {
                    &TypeField
                }
                                
                TestSet ::= SET {
                   typeField TEST.&TypeField,
                   intField  INTEGER
                }
                """;

        testModule(body, CompilerException.class,
                ".*TestSet contains the open type typeField which is ambiguous.*");
    }

    @Test
    void testTypeFieldValidInSet() throws IOException, ParserException {
        var body = """
                TEST ::= CLASS {
                    &TypeField
                }
                                
                TestSet ::= SET {
                   typeField TEST.&TypeField
                }
                """;

        testCompiledCollection(body, "TestSet");
    }

    @Test
    void testTypeFieldTaggedValidInSet() throws IOException, ParserException {
        var body = """
                TEST ::= CLASS {
                    &TypeField
                }
                                
                TestSet ::= SET {
                   intField        INTEGER,
                   typeField1 [23] TEST.&TypeField,
                   typeField2 [24] TEST.&TypeField
                }
                """;

        testCompiledCollection(body, "TestSet");
    }

    @Test
    void testTypeFieldValidInChoice() throws IOException, ParserException {
        var body = """
                TEST ::= CLASS {
                    &TypeField
                }

                TestChoice ::= CHOICE {
                   typeField TEST.&TypeField
                }
                """;

        testCompiledChoice(body);
    }

    @Test
    void testTypeFieldTaggedValidInChoice() throws IOException, ParserException {
        var body = """
                TEST ::= CLASS {
                    &TypeField
                }

                TestChoice ::= CHOICE {
                   typeField1 [23] TEST.&TypeField,
                   typeField2 [24] TEST.&TypeField
                }
                """;

        testCompiledChoice(body);
    }

    @Test
    void testTypeFieldInvalidInChoice() {
        var body = """
                TEST ::= CLASS {
                    &TypeField
                }

                TestChoice ::= CHOICE {
                   intField  INTEGER,
                   typeField TEST.&TypeField
                }
                """;

        testModule(body, CompilerException.class,
                ".*TestChoice contains the open type typeField which is ambiguous.*");
    }

    @Test
    void testVariableTypeField() throws IOException, ParserException {
        var body = """
                TEST ::= CLASS {
                    &variableTypeField &TypeField,
                    &TypeField
                }
                """;

        var module = module("TEST-MODULE", body);
        var compiler = new CompilerImpl();

        compiler.loadAndCompileModule(MODULE_NAME, new ByteArrayInputStream(module.getBytes()));

        var ctx = compiler.getCompilerContext();
        var objectClass = ctx.getCompiledModule("TEST-MODULE").getObjectClasses().get("TEST");

        var field = objectClass.getField("variableTypeField");

        assertTrue(field.isPresent());
        assertTrue(field.get() instanceof CompiledVariableTypeValueField);

        var variableTypeValueField = (CompiledVariableTypeValueField) field.get();

        assertEquals("TypeField", variableTypeValueField.getReference());
    }

    @Test
    void testVariableTypeFieldInvalidReference() {
        var body = """
                TEST ::= CLASS {
                    &variableTypeField &InvalidTypeField,
                    &TypeField
                }

                """;

        testModule(body, CompilerException.class,
                ".*variableTypeField in object class TEST refers to the inexistent field InvalidTypeField.*");
    }

    private void testCompiledCollection(String body, String collectionName) throws IOException, ParserException {
        var module = module("TEST-MODULE", body);
        var compiler = new CompilerImpl();

        compiler.loadAndCompileModule(MODULE_NAME, new ByteArrayInputStream(module.getBytes()));

        var ctx = compiler.getCompilerContext();
        var compiledType = ctx.getCompiledModule("TEST-MODULE").getTypes().get(collectionName);

        assertNotNull(compiledType);
        assertTrue(compiledType instanceof CompiledCollectionType);
    }

    private void testCompiledChoice(String body) throws IOException, ParserException {
        var module = module("TEST-MODULE", body);
        var compiler = new CompilerImpl();

        compiler.loadAndCompileModule(MODULE_NAME, new ByteArrayInputStream(module.getBytes()));

        var ctx = compiler.getCompilerContext();
        var compiledType = ctx.getCompiledModule("TEST-MODULE").getTypes().get("TestChoice");

        assertNotNull(compiledType);
        assertTrue(compiledType instanceof CompiledChoiceType);
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


    private static Stream<Arguments> provideTagsOnComponents() {
        return Stream.of(
                getTagsOnComponentsArguments(BooleanType.class, BOOLEAN, 1),
                getTagsOnComponentsArguments(IntegerType.class, INTEGER, 2),
                getTagsOnComponentsArguments(BitString.class, BIT_STRING, 3),
                getTagsOnComponentsArguments(OctetString.class, OCTET_STRING, 4),
                getTagsOnComponentsArguments(Null.class, NULL, 5),
                getTagsOnComponentsArguments(ObjectIdentifier.class, OBJECT_IDENTIFIER, 6),
                getTagsOnComponentsArguments(Real.class, REAL, 9),
                getTagsOnComponentsArguments(EnumeratedType.class, "ENUMERATED {a, b, c}", 10),
                getTagsOnComponentsArguments(UTF8String.class, UTF8_STRING, 12),
                getTagsOnComponentsArguments(RelativeOID.class, RELATIVE_OID, 13),
                getTagsOnComponentsArguments(SequenceType.class, "SEQUENCE {a NULL}", 16),
                getTagsOnComponentsArguments(SequenceOfType.class, "SEQUENCE OF BOOLEAN", 16),
                getTagsOnComponentsArguments(SetType.class, "SET {a NULL}", 17),
                getTagsOnComponentsArguments(SetOfType.class, "SET OF BOOLEAN", 17),
                getTagsOnComponentsArguments(NumericString.class, NUMERIC_STRING, 18),
                getTagsOnComponentsArguments(PrintableString.class, PRINTABLE_STRING, 19),
                getTagsOnComponentsArguments(TeletexString.class, TELETEX_STRING, 20),
                getTagsOnComponentsArguments(T61String.class, T61_STRING, 20),
                getTagsOnComponentsArguments(VideotexString.class, VIDEOTEX_STRING, 21),
                getTagsOnComponentsArguments(IA5String.class, IA5_STRING, 22),
                getTagsOnComponentsArguments(UTCTime.class, UTC_TIME, 23),
                getTagsOnComponentsArguments(GeneralizedTime.class, GENERALIZED_TIME, 24),
                getTagsOnComponentsArguments(GraphicString.class, GRAPHIC_STRING, 25),
                getTagsOnComponentsArguments(VisibleString.class, VISIBLE_STRING, 26),
                getTagsOnComponentsArguments(ISO646String.class, ISO646_STRING, 26),
                getTagsOnComponentsArguments(GeneralString.class, GENERAL_STRING, 27),
                getTagsOnComponentsArguments(UniversalString.class, UNIVERSAL_STRING, 28),
                getTagsOnComponentsArguments(BMPString.class, BMP_STRING, 30),
                getTagsOnComponentsArguments(IRI.class, OID_IRI, 35),
                getTagsOnComponentsArguments(RelativeIRI.class, RELATIVE_OID_IRI, 36)
        );
    }

    private static Arguments getTagsOnComponentsArguments(Class<? extends Type> type, String typeName, int tag) {
        return Arguments.of(type, typeName, new TagId(Clazz.UNIVERSAL, tag),
                "Test tag on %s component".formatted(typeName));
    }

    private static Arguments getTagsOnComponentsArguments(Class<? extends Type> type, TypeName typeName, int tag) {
        return getTagsOnComponentsArguments(type, typeName.toString(), tag);
    }

    private static Stream<Arguments> provideCustomTagsOnComponentsExplicit() {
        return Stream.of(
                getCustomTagsOnComponentsArguments("", List.of(new TagId(Clazz.UNIVERSAL, 1))),
                getCustomTagsOnComponentsArguments("[2]", List.of(new TagId(Clazz.CONTEXT_SPECIFIC, 2),
                        new TagId(Clazz.UNIVERSAL, 1))),
                getCustomTagsOnComponentsArguments("[2] EXPLICIT", List.of(new TagId(Clazz.CONTEXT_SPECIFIC, 2),
                        new TagId(Clazz.UNIVERSAL, 1))),
                getCustomTagsOnComponentsArguments("[2] IMPLICIT", List.of(new TagId(Clazz.CONTEXT_SPECIFIC, 2))),
                getCustomTagsOnComponentsArguments("[APPLICATION 2]", List.of(new TagId(Clazz.APPLICATION, 2),
                        new TagId(Clazz.UNIVERSAL, 1))),
                getCustomTagsOnComponentsArguments("[PRIVATE 2]", List.of(new TagId(Clazz.PRIVATE, 2),
                        new TagId(Clazz.UNIVERSAL, 1))),
                getCustomTagsOnComponentsArguments("[2] [3]", List.of(new TagId(Clazz.CONTEXT_SPECIFIC, 2),
                        new TagId(Clazz.CONTEXT_SPECIFIC, 3), new TagId(Clazz.UNIVERSAL, 1))),
                getCustomTagsOnComponentsArguments("[2] IMPLICIT [3]", List.of(new TagId(Clazz.CONTEXT_SPECIFIC, 2),
                        new TagId(Clazz.UNIVERSAL, 1))),
                getCustomTagsOnComponentsArguments("[2] EXPLICIT [3]", List.of(new TagId(Clazz.CONTEXT_SPECIFIC, 2),
                        new TagId(Clazz.CONTEXT_SPECIFIC, 3), new TagId(Clazz.UNIVERSAL, 1))),
                getCustomTagsOnComponentsArguments("[2] EXPLICIT [3] IMPLICIT",
                        List.of(new TagId(Clazz.CONTEXT_SPECIFIC, 2), new TagId(Clazz.CONTEXT_SPECIFIC, 3))),
                getCustomTagsOnComponentsArguments("[2] IMPLICIT [3] IMPLICIT",
                        List.of(new TagId(Clazz.CONTEXT_SPECIFIC, 2))),
                getCustomTagsOnComponentsArguments("[1] IMPLICIT [2] IMPLICIT [3] IMPLICIT",
                        List.of(new TagId(Clazz.CONTEXT_SPECIFIC, 1))),
                getCustomTagsOnComponentsArguments("[1] EXPLICIT [2] IMPLICIT [3] IMPLICIT",
                        List.of(new TagId(Clazz.CONTEXT_SPECIFIC, 1), new TagId(Clazz.CONTEXT_SPECIFIC, 2))),
                getCustomTagsOnComponentsArguments("[1] EXPLICIT [2] IMPLICIT [3] EXPLICIT",
                        List.of(new TagId(Clazz.CONTEXT_SPECIFIC, 1), new TagId(Clazz.CONTEXT_SPECIFIC, 2),
                                new TagId(Clazz.UNIVERSAL, 1)))
        );
    }

    private static Stream<Arguments> provideCustomTagsOnComponentsImplicit() {
        return Stream.of(
                getCustomTagsOnComponentsArguments("", List.of(new TagId(Clazz.UNIVERSAL, 1))),
                getCustomTagsOnComponentsArguments("[2]", List.of(new TagId(Clazz.CONTEXT_SPECIFIC, 2))),
                getCustomTagsOnComponentsArguments("[2] EXPLICIT", List.of(new TagId(Clazz.CONTEXT_SPECIFIC, 2),
                        new TagId(Clazz.UNIVERSAL, 1))),
                getCustomTagsOnComponentsArguments("[2] IMPLICIT", List.of(new TagId(Clazz.CONTEXT_SPECIFIC, 2))),
                getCustomTagsOnComponentsArguments("[APPLICATION 2]", List.of(new TagId(Clazz.APPLICATION, 2))),
                getCustomTagsOnComponentsArguments("[PRIVATE 2]", List.of(new TagId(Clazz.PRIVATE, 2))),
                getCustomTagsOnComponentsArguments("[2] [3]", List.of(new TagId(Clazz.CONTEXT_SPECIFIC, 2))),
                getCustomTagsOnComponentsArguments("[2] IMPLICIT [3]", List.of(new TagId(Clazz.CONTEXT_SPECIFIC, 2))),
                getCustomTagsOnComponentsArguments("[2] EXPLICIT [3]", List.of(new TagId(Clazz.CONTEXT_SPECIFIC, 2),
                        new TagId(Clazz.CONTEXT_SPECIFIC, 3))),
                getCustomTagsOnComponentsArguments("[2] EXPLICIT [3] IMPLICIT",
                        List.of(new TagId(Clazz.CONTEXT_SPECIFIC, 2), new TagId(Clazz.CONTEXT_SPECIFIC, 3))),
                getCustomTagsOnComponentsArguments("[2] IMPLICIT [3] IMPLICIT",
                        List.of(new TagId(Clazz.CONTEXT_SPECIFIC, 2))),
                getCustomTagsOnComponentsArguments("[1] IMPLICIT [2] IMPLICIT [3] IMPLICIT",
                        List.of(new TagId(Clazz.CONTEXT_SPECIFIC, 1))),
                getCustomTagsOnComponentsArguments("[1] EXPLICIT [2] IMPLICIT [3] IMPLICIT",
                        List.of(new TagId(Clazz.CONTEXT_SPECIFIC, 1), new TagId(Clazz.CONTEXT_SPECIFIC, 2))),
                getCustomTagsOnComponentsArguments("[1] EXPLICIT [2] IMPLICIT [3] EXPLICIT",
                        List.of(new TagId(Clazz.CONTEXT_SPECIFIC, 1), new TagId(Clazz.CONTEXT_SPECIFIC, 2),
                                new TagId(Clazz.UNIVERSAL, 1)))
        );
    }

    private static Arguments getCustomTagsOnComponentsArguments(String prefixedType, List<TagId> tags) {
        return Arguments.of(prefixedType + " BOOLEAN", tags,
                "Test custom tag '%s' on component".formatted(prefixedType));
    }

}
