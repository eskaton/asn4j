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

import ch.eskaton.asn4j.compiler.constraints.ast.AbstractNode;
import ch.eskaton.asn4j.compiler.constraints.ast.AllValuesNode;
import ch.eskaton.asn4j.compiler.constraints.ast.BinOpNode;
import ch.eskaton.asn4j.compiler.constraints.ast.CollectionOfValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.CollectionValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.ComponentNode;
import ch.eskaton.asn4j.compiler.constraints.ast.EnumeratedValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.IRIValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.IntegerRange;
import ch.eskaton.asn4j.compiler.constraints.ast.IntegerRangeValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.ObjectIdentifierValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.PermittedAlphabetNode;
import ch.eskaton.asn4j.compiler.constraints.ast.RelativeIRIValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.RelativeOIDValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.SizeNode;
import ch.eskaton.asn4j.compiler.constraints.ast.StringRange;
import ch.eskaton.asn4j.compiler.constraints.ast.StringSingleValue;
import ch.eskaton.asn4j.compiler.constraints.ast.StringValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.ValueNode;
import ch.eskaton.asn4j.compiler.constraints.ast.WithComponentsNode;
import ch.eskaton.asn4j.compiler.results.AbstractCompiledField;
import ch.eskaton.asn4j.compiler.results.CompiledBitStringType;
import ch.eskaton.asn4j.compiler.results.CompiledChoiceType;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionComponent;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionOfType;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionType;
import ch.eskaton.asn4j.compiler.results.CompiledFixedTypeValueField;
import ch.eskaton.asn4j.compiler.results.CompiledFixedTypeValueSetField;
import ch.eskaton.asn4j.compiler.results.CompiledObject;
import ch.eskaton.asn4j.compiler.results.CompiledObjectClass;
import ch.eskaton.asn4j.compiler.results.CompiledObjectField;
import ch.eskaton.asn4j.compiler.results.CompiledObjectSet;
import ch.eskaton.asn4j.compiler.results.CompiledObjectSetField;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.compiler.results.CompiledTypeField;
import ch.eskaton.asn4j.compiler.results.CompiledVariableTypeValueField;
import ch.eskaton.asn4j.compiler.results.CompiledVariableTypeValueSetField;
import ch.eskaton.asn4j.parser.ParserException;
import ch.eskaton.asn4j.parser.ast.types.BMPString;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.BooleanType;
import ch.eskaton.asn4j.parser.ast.types.EnumeratedType;
import ch.eskaton.asn4j.parser.ast.types.ExternalTypeReference;
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
import ch.eskaton.asn4j.parser.ast.values.BMPStringValue;
import ch.eskaton.asn4j.parser.ast.values.BitStringValue;
import ch.eskaton.asn4j.parser.ast.values.BooleanValue;
import ch.eskaton.asn4j.parser.ast.values.ChoiceValue;
import ch.eskaton.asn4j.parser.ast.values.CollectionOfValue;
import ch.eskaton.asn4j.parser.ast.values.CollectionValue;
import ch.eskaton.asn4j.parser.ast.values.EnumeratedValue;
import ch.eskaton.asn4j.parser.ast.values.GeneralStringValue;
import ch.eskaton.asn4j.parser.ast.values.GeneralizedTimeValue;
import ch.eskaton.asn4j.parser.ast.values.GraphicStringValue;
import ch.eskaton.asn4j.parser.ast.values.IA5StringValue;
import ch.eskaton.asn4j.parser.ast.values.IRIValue;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.NullValue;
import ch.eskaton.asn4j.parser.ast.values.NumericStringValue;
import ch.eskaton.asn4j.parser.ast.values.ObjectIdentifierValue;
import ch.eskaton.asn4j.parser.ast.values.OctetStringValue;
import ch.eskaton.asn4j.parser.ast.values.PrintableStringValue;
import ch.eskaton.asn4j.parser.ast.values.RealValue;
import ch.eskaton.asn4j.parser.ast.values.RelativeIRIValue;
import ch.eskaton.asn4j.parser.ast.values.RelativeOIDValue;
import ch.eskaton.asn4j.parser.ast.values.TeletexStringValue;
import ch.eskaton.asn4j.parser.ast.values.UTCTimeValue;
import ch.eskaton.asn4j.parser.ast.values.UTF8StringValue;
import ch.eskaton.asn4j.parser.ast.values.UniversalStringValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.parser.ast.values.VideotexStringValue;
import ch.eskaton.asn4j.parser.ast.values.VisibleStringValue;
import ch.eskaton.asn4j.runtime.Clazz;
import ch.eskaton.asn4j.runtime.TagId;
import ch.eskaton.asn4j.runtime.types.ASN1Null;
import ch.eskaton.asn4j.runtime.types.TypeName;
import ch.eskaton.asn4j.test.CompilerTestBuilder;
import ch.eskaton.commons.collections.Tuple2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch.eskaton.asn4j.compiler.CompilerTestUtils.compilerConfig;
import static ch.eskaton.asn4j.compiler.CompilerTestUtils.getCompiledValue;
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
import static ch.eskaton.asn4j.test.TestUtils.module;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompilerImplTest {

    public static final String MODULE_NAME = "TEST-MODULE";

    @Test
    void testConstraintAndModuleAbsent() throws IOException, ParserException {
        var body = """
                Integer ::= INTEGER
                """;

        var compiledType = getCompiledType(body, MODULE_NAME, "Integer");

        assertTrue(compiledType.getType() instanceof IntegerType);
        assertTrue(compiledType.getConstraintDefinition().isEmpty());
        assertTrue(compiledType.getModule().isEmpty());
    }

    @Test
    void testConstraintAndModulePresent() throws IOException, ParserException {
        var body = """
                Integer ::= INTEGER (1..9)
                """;

        var compiledType = getCompiledType(body, MODULE_NAME, "Integer");

        assertTrue(compiledType.getType() instanceof IntegerType);
        assertTrue(compiledType.getConstraintDefinition().isPresent());
        assertTrue(compiledType.getModule().isPresent());
    }

    @Test
    void testCollectionConstraintAndModuleAbsent() throws IOException, ParserException {
        var body = """
                Seq ::= SEQUENCE {
                    a INTEGER
                }
                """;

        var compiledType = getCompiledCollectionType(body, MODULE_NAME, "Seq");
        var field = compiledType.getComponents().stream().filter(c -> c.getName().equals("a")).findFirst();

        assertTrue(field.isPresent());

        assertTrue(compiledType.getConstraintDefinition().isEmpty());
        assertTrue(compiledType.getModule().isEmpty());
    }

    @Test
    void testCollectionConstraintAndModulePresent() throws IOException, ParserException {
        var body = """
                Seq ::= SEQUENCE {
                    a INTEGER (1..9)
                }
                """;

        var compiledType = getCompiledCollectionType(body, MODULE_NAME, "Seq");

        assertTrue(compiledType.getConstraintDefinition().isPresent());
        assertTrue(compiledType.getModule().isPresent());
    }

    @Test
    void testCollectionOfNamedType() throws IOException, ParserException {
        var body = """
                SeqOf ::= SEQUENCE OF a INTEGER
                """;

        var compiledType = getCompiledCollectionOfType(body, MODULE_NAME, "SeqOf");

        assertTrue(compiledType instanceof CompiledCollectionOfType);

        var compiledCollectionOfType = (CompiledCollectionOfType) compiledType;

        assertEquals(Optional.of("a"), compiledCollectionOfType.getContentTypeName());
        assertTrue(compiledCollectionOfType.getContentType().getType() instanceof IntegerType);
    }

    @Test
    void testCollectionOfNamedTypeInvalidComponentInValue() {
        var body = """
                SeqOf ::= SEQUENCE OF a INTEGER
                                
                Seq ::= SEQUENCE {
                    b SeqOf DEFAULT {c 1, c 2}
                }
                """;

        testModule(body, CompilerException.class,
                ".*The value 'c 1' references a named component in 'SEQUENCE OF a INTEGER' that doesn't exist.*");
    }

    @Test
    void testCollectionOfTypeInvalidComponentInValue() {
        var body = """
                SeqOf ::= SEQUENCE OF INTEGER
                                
                Seq ::= SEQUENCE {
                    b SeqOf DEFAULT {c 1, c 2}
                }
                """;

        testModule(body, CompilerException.class,
                ".*The value 'c 1' references a named component in 'SEQUENCE OF INTEGER' that doesn't exist.*");
    }

    @Test
    void testCollectionOfConstraintAndModuleAbsent() throws IOException, ParserException {
        var body = """
                SeqOf ::= SEQUENCE OF INTEGER
                """;

        var compiledType = getCompiledCollectionOfType(body, MODULE_NAME, "SeqOf");

        assertTrue(compiledType.getConstraintDefinition().isEmpty());
        assertTrue(compiledType.getModule().isEmpty());
    }

    @Test
    void testCollectionOfConstraintAndModulePresent() throws IOException, ParserException {
        var body = """
                SeqOf ::= SEQUENCE OF INTEGER (1..9)
                """;

        var compiledType = getCompiledCollectionOfType(body, MODULE_NAME, "SeqOf");

        assertTrue(compiledType.getConstraintDefinition().isPresent());
        assertTrue(compiledType.getModule().isPresent());
    }

    @Test
    void testChoiceConstraintAndModuleAbsent() throws IOException, ParserException {
        var body = """
                Choice ::= CHOICE {
                    a INTEGER
                }
                """;

        var compiledType = getCompiledChoiceType(body, MODULE_NAME, "Choice");

        assertTrue(compiledType.getConstraintDefinition().isEmpty());
        assertTrue(compiledType.getModule().isEmpty());
    }

    @Test
    void testChoiceConstraintAndModulePresent() throws IOException, ParserException {
        var body = """
                Choice ::= CHOICE {
                    a INTEGER (1..9)
                }
                """;

        var compiledType = getCompiledChoiceType(body, MODULE_NAME, "Choice");

        assertTrue(compiledType.getConstraintDefinition().isPresent());
        assertTrue(compiledType.getModule().isPresent());
    }

    @Test
    void testValueReference() throws IOException, ParserException {
        var body = """
                null NULL ::= NULL
                nullRef NULL ::= null
                """;

        var module = module(MODULE_NAME, body);
        var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module));
        var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

        compiler.run();

        var ctx = compiler.getCompilerContext();
        var value = ctx.getCompiledModule(MODULE_NAME).getValues().get("nullRef");

        assertNotNull(value);
        assertTrue(value.getValue() instanceof NullValue);
    }

    @Test
    void testNullValueImportedReferences() throws IOException, ParserException {
        var body1 = """
                IMPORTS null FROM OTHER-MODULE;
                                
                nullRef NULL ::= null    
                """;
        var body2 = """
                EXPORTS null;
                                
                null NULL ::= NULL     
                """;

        var module1 = module(MODULE_NAME, body1);
        var module2 = module("OTHER-MODULE", body2);
        var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module1), Tuple2.of("OTHER-MODULE", module2));
        var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

        compiler.run();

        var ctx = compiler.getCompilerContext();
        var compiledValue = ctx.getCompiledModule(MODULE_NAME).getValues().get("nullRef");

        assertNotNull(compiledValue);
        assertTrue(compiledValue.getValue() instanceof NullValue);
    }

    @Test
    void testExternalValueReferences() throws IOException, ParserException {
        var body1 = """            
                nullRef NULL ::= OTHER-MODULE.null    
                """;
        var body2 = """
                EXPORTS null;
                                
                null NULL ::= NULL     
                """;

        var module1 = module(MODULE_NAME, body1);
        var module2 = module("OTHER-MODULE", body2);
        var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module1), Tuple2.of("OTHER-MODULE", module2));
        var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

        compiler.run();

        var ctx = compiler.getCompilerContext();
        var compiledValue = ctx.getCompiledModule(MODULE_NAME).getValues().get("nullRef");

        assertNotNull(compiledValue);
        assertTrue(compiledValue.getValue() instanceof NullValue);
    }

    @Test
    void testExternalTypeReference() throws IOException, ParserException {
        var body1 = """
                IMPORTS Integer FROM OTHER-MODULE;
                                
                MyInt1 ::= OTHER-MODULE.Integer     
                MyInt2 ::= [0] EXPLICIT OTHER-MODULE.Integer     
                """;
        var body2 = """
                EXPORTS Integer;
                                
                Integer ::= INTEGER     
                """;

        var module1 = module(MODULE_NAME, body1);
        var module2 = module("OTHER-MODULE", body2);
        var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module1), Tuple2.of("OTHER-MODULE", module2));
        var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

        compiler.run();

        var ctx = compiler.getCompilerContext();
        var value1 = ctx.getCompiledModule(MODULE_NAME).getTypes().get("MyInt1");

        assertNotNull(value1);
        assertTrue(value1.getType() instanceof ExternalTypeReference);
        assertTrue(value1.getTags().isPresent());
        assertEquals(1, value1.getTags().get().size());

        var value2 = ctx.getCompiledModule(MODULE_NAME).getTypes().get("MyInt2");

        assertNotNull(value2);
        assertTrue(value2.getType() instanceof ExternalTypeReference);
        assertTrue(value2.getTags().isPresent());
        assertEquals(2, value2.getTags().get().size());

        var value3 = ctx.getCompiledModule("OTHER-MODULE").getTypes().get("Integer");

        assertNotNull(value3);
        assertTrue(value3.getType() instanceof IntegerType);
    }

    @Test
    void testExternalTypeReferenceWithExportAll() throws IOException, ParserException {
        var body1 = """
                MyInt ::= OTHER-MODULE.Integer
                """;
        var body2 = """
                Integer ::= INTEGER
                """;

        var module1 = module(MODULE_NAME, body1);
        var module2 = module("OTHER-MODULE", body2);
        var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module1), Tuple2.of("OTHER-MODULE", module2));
        var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

        compiler.run();

        var ctx = compiler.getCompilerContext();
        var value = ctx.getCompiledModule(MODULE_NAME).getTypes().get("MyInt");

        assertNotNull(value);
        assertTrue(value.getType() instanceof ExternalTypeReference);
        assertTrue(value.getTags().isPresent());
        assertEquals(1, value.getTags().get().size());
    }

    @Test
    void testValueOfSelectedType() throws IOException, ParserException {
        var body = """
                TestChoice ::= CHOICE {a BOOLEAN, b INTEGER}
                selected b < TestChoice ::= 12
                """;

        var compiledValue = getCompiledValue(body, IntegerValue.class, "selected");
        var value = (IntegerValue) compiledValue.getValue();

        assertEquals(12, value.getValue().intValue());
    }

    @Test
    void testSequenceWithChoiceWithDefault() throws IOException, ParserException {
        var body = """
                Seq ::= SEQUENCE {
                    choice CHOICE {
                        a INTEGER,
                        b BOOLEAN
                    } DEFAULT a: 25
                }
                """;

        var compiledType = getCompiledType(body, MODULE_NAME, "Seq");

        assertTrue(compiledType instanceof CompiledCollectionType);

        var compiledCollectionType = (CompiledCollectionType) compiledType;
        var field = compiledCollectionType.getComponents().stream()
                .filter(c -> c.getName().equals("choice"))
                .findFirst();

        assertTrue(field.isPresent());

        var defaultValue = field.get().getDefaultValue();

        assertTrue(defaultValue.isPresent());

        var value = defaultValue.get().getValue();

        assertTrue(value instanceof ChoiceValue);

        var choiceValue = (ChoiceValue) value;

        assertEquals("a", choiceValue.getId());
        assertTrue(choiceValue.getValue() instanceof IntegerValue);
    }

    @Test
    void testSequenceWithParameterizedComponent() throws IOException, ParserException {
        var body = """
                Seq2{Type} ::= SEQUENCE {
                    field2 Type
                }

                Seq1 ::= SEQUENCE {
                    field1 Seq2{BOOLEAN}
                }
                """;

        var compiledType = getCompiledType(body, MODULE_NAME, "Seq1");

        assertTrue(compiledType instanceof CompiledCollectionType);

        var compiledCollectionType1 = (CompiledCollectionType) compiledType;
        var maybeField1 = compiledCollectionType1.getComponents().stream()
                .filter(c -> c.getName().equals("field1"))
                .findFirst();

        assertTrue(maybeField1.isPresent());

        var field1 = maybeField1.get();

        assertTrue(field1 instanceof CompiledCollectionComponent);

        var compiledCollectionComponent = (CompiledCollectionComponent) field1;

        assertTrue(compiledCollectionComponent.getCompiledType() instanceof CompiledCollectionType);

        var compiledCollectionType2 = (CompiledCollectionType) compiledCollectionComponent.getCompiledType();

        var maybeField2 = compiledCollectionType2.getComponents().stream()
                .filter(c -> c.getName().equals("field2"))
                .findFirst();

        assertTrue(maybeField2.isPresent());

        var field2 = maybeField2.get();

        assertTrue(field2.getCompiledType().getType() instanceof BooleanType);
    }

    @Test
    void testSequenceWithDefaultInvalidValue() {
        var body = """
                Seq ::= SEQUENCE {
                    field BOOLEAN DEFAULT 25
                }
                """;
        testModule(body, CompilerException.class, ".*Invalid BOOLEAN value: 25.*");
    }

    @Test
    void testSequenceWithDefaultInvalidReference() {
        var body = """
                int INTEGER ::= 25
                                
                Seq ::= SEQUENCE {
                    field BOOLEAN DEFAULT int
                }
                """;
        testModule(body, CompilerException.class,
                ".*Expected a value of type BOOLEAN but 'int' refers to a value of type INTEGER.*");
    }

    @SuppressWarnings("unused")
    @ParameterizedTest(name = "[{index}] {3}")
    @MethodSource("provideInvalidTypesInConstraintsArguments")
    @DisplayName("Test invalid types in constraints")
    void testInvalidTypesInConstraints(String body, Class<? extends Exception> expected, String message,
            String description) {
        testModule(body, expected, ".*" + message + ".*");
    }

    @SuppressWarnings("unused")
    @ParameterizedTest(name = "[{index}] {3}")
    @MethodSource("provideInvalidMultipleTypeConstraints")
    @DisplayName("Test invalid WITH COMPONENTS constraints")
    void testInvalidMultipleTypeConstraints(String body, Class<? extends Exception> expected, String message,
            String description) {
        testModule(body, expected, ".*" + message + ".*");
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideSingleTypeConstraint")
    @DisplayName("Test WITH COMPONENT constraints")
    void testSingleTypeConstraint(String typeName, String valueString) throws IOException, ParserException {
        var body = "SeqOf ::= SEQUENCE OF %s(%s)".formatted(typeName, valueString);

        getCompiledType(body, MODULE_NAME, "SeqOf");
    }

    @Test
    void testMultipleTypeConstraintOnSetReference() throws IOException, ParserException {
        var body = """
                 Set ::= SET {
                   field1 VisibleString(SIZE (1..9)),
                   field2 PrintableString
                 }

                 SetReference ::= Set (WITH COMPONENTS {field1 (SIZE(3..7)), field2 (SIZE(2..6))})
                """;

        var compiledType = getCompiledType(body, MODULE_NAME, "SetReference");

        assertNotNull(compiledType);

        var maybeConstraint = compiledType.getConstraintDefinition();

        assertTrue(maybeConstraint.isPresent());

        /* The following constraint is expected at the moment:
         * (field1 SIZE(1..9)) && ((field1 SIZE(1..9) && SIZE(3..7)) && (field2 SIZE(2..6)))
         *
         * This shall be optimised in the future.
         */

        var constraint = maybeConstraint.get();
        var roots = constraint.getRoots();

        assertTrue(roots instanceof BinOpNode);

        var node = (BinOpNode) roots;
        var leftNode = node.getLeft();

        assertTrue(leftNode instanceof WithComponentsNode);

        var leftComponentsNode = (WithComponentsNode) leftNode;

        assertEquals(1, leftComponentsNode.getComponents().size());

        var maybeField1 = leftComponentsNode.getComponents().stream()
                .filter(c -> c.getName().equals("field1")).findFirst();

        assertTrue(maybeField1.isPresent());

        checkSizeConstraint(maybeField1.get().getConstraint(), 1, 9);

        var rightNode = node.getRight();

        assertTrue(rightNode instanceof WithComponentsNode);

        var rightComponentsNode = (WithComponentsNode) rightNode;

        assertEquals(2, rightComponentsNode.getComponents().size());

        maybeField1 = rightComponentsNode.getComponents().stream()
                .filter(c -> c.getName().equals("field1")).findFirst();

        assertTrue(maybeField1.isPresent());

        var binOpNode = maybeField1.get().getConstraint();

        assertTrue(binOpNode instanceof BinOpNode);

        checkSizeConstraint(((BinOpNode) binOpNode).getLeft(), 1, 9);
        checkSizeConstraint(((BinOpNode) binOpNode).getRight(), 3, 7);

        var maybeField2 = rightComponentsNode.getComponents().stream()
                .filter(c -> c.getName().equals("field2")).findFirst();

        assertTrue(maybeField2.isPresent());

        checkSizeConstraint(maybeField2.get().getConstraint(), 2, 6);
    }

    @ParameterizedTest(name = "[{index}] {3}")
    @MethodSource("provideTagsOnComponents")
    @DisplayName("Test tags on SEQUENCE components")
    void testTagsOnComponents(Class<? extends Type> type, String typeName, TagId tag, String description)
            throws IOException, ParserException {
        var body = """
                Seq ::= SEQUENCE {
                    component %s
                }
                """.formatted(typeName);

        var compiledType = getCompiledType(body, MODULE_NAME, "Seq");

        assertTrue(compiledType instanceof CompiledCollectionType);

        var compiledSequence = (CompiledCollectionType) compiledType;

        assertEquals(1, compiledSequence.getComponents().size());

        var compiledComponent = compiledSequence.getComponents().get(0).getCompiledType();

        assertTrue(compiledComponent.getType().getClass().isAssignableFrom(type));
        assertTrue(compiledComponent.getTags().isPresent());
        assertEquals(1, compiledComponent.getTags().get().size());
        assertEquals(tag, compiledComponent.getTags().get().get(0));
    }

    @ParameterizedTest(name = "[{index}] {2}")
    @MethodSource("provideCustomTagsOnComponentsExplicit")
    @DisplayName("Test explicit custom tags on SEQUENCE components")
    void testCustomTagsOnComponentsExplicit(String prefixedType, List<TagId> tags, String description)
            throws IOException, ParserException {
        testCustomTagsOnComponents(prefixedType, tags, false);
    }

    @ParameterizedTest(name = "[{index}] {2}")
    @MethodSource("provideCustomTagsOnComponentsImplicit")
    @DisplayName("Test implicit custom tags on SEQUENCE components")
    void testCustomTagsOnComponentsImplicit(String prefixedType, List<TagId> tags, String description)
            throws IOException, ParserException {
        testCustomTagsOnComponents(prefixedType, tags, true);
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

    @Test
    void testDuplicateTypeWithTagsInSet() throws IOException, ParserException {
        var body = """
                String ::= VisibleString
                 
                Set ::= SET {
                    field-a  [0] String,
                    field-b  [1] String
                }
                """;

        var compiledCollectionType = getCompiledCollectionType(body, MODULE_NAME, "Set");
        var field = testCollectionField(compiledCollectionType, "field-a", VisibleString.class);
        var maybeTags = field.getTags();

        assertTrue(maybeTags.isPresent());
        assertEquals(2, maybeTags.get().size());

        field = testCollectionField(compiledCollectionType, "field-b", VisibleString.class);
        maybeTags = field.getTags();

        assertTrue(maybeTags.isPresent());
        assertEquals(2, maybeTags.get().size());
    }

    @Test
    void testDuplicateParameterizedTypeWithTagsInSet() throws IOException, ParserException {
        var body = """
                String{INTEGER:length} ::= VisibleString (SIZE(1..length))
                 
                Set ::= SET {
                    field-a  [0] String{10},
                    field-b  [1] String{15}
                }
                """;

        var compiledCollectionType = getCompiledCollectionType(body, MODULE_NAME, "Set");
        var field = testCollectionField(compiledCollectionType, "field-a", VisibleString.class);
        var maybeTags = field.getTags();

        assertTrue(maybeTags.isPresent());
        assertEquals(2, maybeTags.get().size());

        field = testCollectionField(compiledCollectionType, "field-b", VisibleString.class);
        maybeTags = field.getTags();

        assertTrue(maybeTags.isPresent());
        assertEquals(2, maybeTags.get().size());
    }

    @ParameterizedTest(name = "[{index}] {1}")
    @MethodSource("provideSetsWithDuplicateTags")
    @DisplayName("Test duplicate tags in SET")
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
    void testDuplicateNamesInChoice() {
        var body = """
                Choice ::= CHOICE {
                    a INTEGER,
                    a BOOLEAN
                }
                """;

        testModule(body, CompilerException.class, "Duplicate component name in CHOICE 'Choice': a.*");
    }

    @Test
    void testDuplicateNamesInSet() {
        var body = """
                Set ::= SET {
                    a INTEGER,
                    a BOOLEAN
                }
                """;

        testModule(body, CompilerException.class, "Duplicate component name in SET 'Set': a.*");
    }

    @Test
    void testDuplicateNamesInSequence() {
        var body = """
                Sequence ::= SEQUENCE {
                    a INTEGER,
                    a BOOLEAN
                }
                """;

        testModule(body, CompilerException.class, "Duplicate component name in SEQUENCE 'Sequence': a.*");
    }

    @Test
    void testSequenceWithComponentsOfReference() throws IOException, ParserException {
        var body = """
                Seq1 ::= SEQUENCE {a INTEGER, b BOOLEAN}
                Seq2 ::= SEQUENCE { 
                    COMPONENTS OF Seq1 
                }
                """;

        var compiledType = getCompiledType(body, MODULE_NAME, "Seq2");

        assertTrue(compiledType instanceof CompiledCollectionType);

        testCollectionField((CompiledCollectionType) compiledType, "a", IntegerType.class);
        testCollectionField((CompiledCollectionType) compiledType, "b", BooleanType.class);
    }

    @Test
    void testSequenceWithComponentsOf() throws IOException, ParserException {
        var body = """
                Seq ::= SEQUENCE { 
                    COMPONENTS OF SEQUENCE {a INTEGER, b BOOLEAN} 
                }
                """;

        var compiledType = getCompiledType(body, MODULE_NAME, "Seq");

        assertTrue(compiledType instanceof CompiledCollectionType);

        testCollectionField((CompiledCollectionType) compiledType, "a", IntegerType.class);
        testCollectionField((CompiledCollectionType) compiledType, "b", BooleanType.class);
    }

    @Test
    void testSequenceWithComponentsOfInvalidType() {
        var body = """
                Seq ::= SEQUENCE { 
                    COMPONENTS OF SET {a INTEGER, b BOOLEAN} 
                }
                """;

        testModule(body, CompilerException.class, ".*Invalid type 'SET' in COMPONENTS OF 'Seq'.*");
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
    void testTypeReferencesWithConstraints() throws IOException, ParserException {
        var body = """
                TestInteger1 ::= INTEGER ((0..2) | (4..6) | 9)
                TestInteger2 ::= TestInteger1 (MIN<..5)
                TestInteger3 ::= INTEGER (TestInteger2 ^ (4 | 5 | 6))
                """;

        var compiledType = getCompiledType(body, MODULE_NAME, "TestInteger3");

        var maybeConstraintDefinition = compiledType.getConstraintDefinition();

        assertTrue(maybeConstraintDefinition.isPresent());

        var constraintDefinition = maybeConstraintDefinition.get();
        var roots = constraintDefinition.getRoots();

        assertTrue(roots instanceof IntegerRangeValueNode);

        var integerRangeValueNode = (IntegerRangeValueNode) roots;

        checkIntegerRange(integerRangeValueNode.getValue(), 4, 5);
    }

    @Nested
    @DisplayName("Test object set")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ObjectSet {

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

            testModule(body, CompilerException.class, ".*Invalid INTEGER value.*");
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

            var module = module(MODULE_NAME, body);
            var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module));
            var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

            compiler.run();

            var ctx = compiler.getCompilerContext();
            var objectSet = ctx.getCompiledModule(MODULE_NAME).getObjectSets().get("TestSet");

            assertNotNull(objectSet);
            assertEquals(1, objectSet.getValues().size());

            var value = objectSet.getValues().stream().findFirst().get();

            assertTrue(value.containsKey("TypeField"));
            assertTrue(value.get("TypeField") instanceof CompiledType);
            assertTrue(((CompiledType) value.get("TypeField")).getType() instanceof BooleanType);
        }

        @Test
        void testObjectSetWithObjectSetReference() throws IOException, ParserException {
            var body = """
                    TEST ::= CLASS {
                        &id INTEGER UNIQUE
                    }

                    TestSet1 TEST ::= {
                        {&id 1234}
                    }
                                    
                    TestSet2 TEST ::= {
                        TestSet1 | {&id 5678}
                    }
                    """;

            var module = module(MODULE_NAME, body);
            var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module));
            var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

            compiler.run();

            var ctx = compiler.getCompilerContext();
            var objectSet = ctx.getCompiledModule(MODULE_NAME).getObjectSets().get("TestSet2");

            assertNotNull(objectSet);
            assertEquals(2, objectSet.getValues().size());
        }

        @Test
        void testObjectSetWithObjectReferences() throws IOException, ParserException {
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

            var module = module(MODULE_NAME, body);
            var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module));
            var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

            compiler.run();

            var ctx = compiler.getCompilerContext();
            var objectSet = ctx.getCompiledModule(MODULE_NAME).getObjectSets().get("TestSet");

            assertNotNull(objectSet);
            assertEquals(2, objectSet.getValues().size());
        }

    }

    @Nested
    @DisplayName("Test object")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class Object {

        @Test
        void testObjectWithReferenceInFixedTypeValueField() throws IOException, ParserException {
            var body = """
                    TEST ::= CLASS {
                        &intField INTEGER
                    }

                    intValue INTEGER ::= 47

                    testObject TEST ::= {
                        &intField intValue
                    }

                    """;

            var module = module(MODULE_NAME, body);
            var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module));
            var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

            compiler.run();

            var ctx = compiler.getCompilerContext();
            var object = ctx.getCompiledModule(MODULE_NAME).getObjects().get("testObject");

            assertNotNull(object);
            assertTrue(object.getObjectDefinition().containsKey("intField"));

            var value = object.getObjectDefinition().get("intField");

            assertTrue(value instanceof IntegerValue);
            assertEquals(47, ((IntegerValue) value).getValue().longValue());
        }

        @Test
        void testObjectWithReferenceInVariableTypeValueField() throws IOException, ParserException {
            var body = """
                    TEST ::= CLASS {
                        &field &Type,
                        &Type
                    }

                    intValue INTEGER ::= 47

                    testObject TEST ::= {
                        &field intValue,
                        &Type  INTEGER
                    }

                    """;

            var module = module(MODULE_NAME, body);
            var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module));
            var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

            compiler.run();

            var ctx = compiler.getCompilerContext();
            var object = ctx.getCompiledModule(MODULE_NAME).getObjects().get("testObject");

            assertNotNull(object);
            assertTrue(object.getObjectDefinition().containsKey("field"));

            var value = object.getObjectDefinition().get("field");

            assertTrue(value instanceof IntegerValue);
            assertEquals(47, ((IntegerValue) value).getValue().longValue());
        }

        @Test
        void testObjectWithObjectField() throws IOException, ParserException {
            var body = """
                    TEST1 ::= CLASS {
                        &field1 INTEGER
                    }
                                    
                    TEST2 ::= CLASS {
                        &field2 TEST1
                    }

                    object2 TEST2 ::= {
                        &field2 {&field1 47}
                    }
                    """;

            var module = module(MODULE_NAME, body);
            var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module));
            var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

            compiler.run();

            var ctx = compiler.getCompilerContext();
            var object2 = ctx.getCompiledModule(MODULE_NAME).getObjects().get("object2");

            assertNotNull(object2);
            assertTrue(object2.getObjectDefinition().containsKey("field2"));

            var object1 = object2.getObjectDefinition().get("field2");

            assertTrue(object1 instanceof Map);
            assertTrue(((Map) object1).containsKey("field1"));

            var value = ((Map) object1).get("field1");

            assertTrue(value instanceof IntegerValue);
            assertEquals(47, ((IntegerValue) value).getValue().longValue());
        }

        @Test
        void testObjectWithReferenceInObjectField() throws IOException, ParserException {
            var body = """
                    TEST1 ::= CLASS {
                        &field1 INTEGER
                    }
                                    
                    TEST2 ::= CLASS {
                        &field2 TEST1
                    }

                    object1 TEST1 ::= {&field1 47}

                    object2 TEST2 ::= {
                        &field2 object1
                    }
                    """;

            var module = module(MODULE_NAME, body);
            var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module));
            var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

            compiler.run();

            var ctx = compiler.getCompilerContext();
            var object2 = ctx.getCompiledModule(MODULE_NAME).getObjects().get("object2");

            assertNotNull(object2);
            assertTrue(object2.getObjectDefinition().containsKey("field2"));

            var object1 = object2.getObjectDefinition().get("field2");

            assertTrue(object1 instanceof Map);
            assertTrue(((Map) object1).containsKey("field1"));

            var value = ((Map) object1).get("field1");

            assertTrue(value instanceof IntegerValue);
            assertEquals(47, ((IntegerValue) value).getValue().longValue());
        }

        @Test
        void testObjectWithSequenceOf() throws IOException, ParserException {
            var body = """
                    TEST ::= CLASS {
                        &field SEQUENCE OF VisibleString
                    } WITH SYNTAX {
                        FIELD &field
                    }
                                    
                    object TEST ::= {
                        FIELD {"value"}
                    }
                    """;

            var module = module(MODULE_NAME, body);
            var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module));
            var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

            compiler.run();

            var ctx = compiler.getCompilerContext();
            var object = ctx.getCompiledModule(MODULE_NAME).getObjects().get("object");

            assertNotNull(object);
            assertTrue(object.getObjectDefinition().containsKey("field"));
        }

        @Test
        void testObjectWithSyntaxPrimitiveFieldNameFirst() throws IOException, ParserException {
            var body = """
                         TEST ::= CLASS {
                           &id    INTEGER UNIQUE,
                           &Type 
                         } WITH SYNTAX {
                           &Type IDENTIFIED BY &id
                         }
                        
                        compiledObject TEST ::= {
                           BOOLEAN IDENTIFIED BY 1 
                        }
                    """;

            var module = module(MODULE_NAME, body);
            var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module));
            var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

            compiler.run();

            var ctx = compiler.getCompilerContext();
            var compiledObject = ctx.getCompiledModule(MODULE_NAME).getObjects().get("compiledObject");

            assertNotNull(compiledObject);

            var object = compiledObject.getObjectDefinition();

            var value1 = object.get("id");

            assertTrue(value1 instanceof IntegerValue);

            var value2 = object.get("Type");

            assertTrue(value2 instanceof CompiledType);
            assertTrue(((CompiledType) value2).getType() instanceof BooleanType);
        }

        @Test
        void testObjectWithSyntaxPrimitiveFieldNameLast() throws IOException, ParserException {
            var body = """
                       TEST ::= CLASS {
                           &fixedTypeValueField1 BOOLEAN,
                           &fixedTypeValueField2 INTEGER
                       } WITH SYNTAX {
                           FIXED TYPE VALUE-A &fixedTypeValueField1
                           FIXED TYPE VALUE-B &fixedTypeValueField2
                       }
                       
                       testObject TEST ::= {
                           FIXED TYPE VALUE-A TRUE
                           FIXED TYPE VALUE-B 1
                       }
                       
                       TestSet TEST ::= { testObject }
                    """;

            var objectSet = getCompiledObjectSet(body, "TEST", "TestSet");

            assertEquals(1, objectSet.getValues().size());

            var object = objectSet.getValues().stream().findFirst().get();
            var value1 = object.get("fixedTypeValueField1");

            assertTrue(value1 instanceof BooleanValue);

            var value2 = object.get("fixedTypeValueField2");

            assertNotNull(value2);
            assertTrue(value2 instanceof IntegerValue);
        }

        @Test
        void testObjectWithSyntaxOptionalPresent() throws IOException, ParserException {
            var body = """
                       TEST ::= CLASS {
                           &fixedTypeValueField1 BOOLEAN OPTIONAL,
                           &fixedTypeValueField2 INTEGER
                       } WITH SYNTAX {
                           [VALUE-A FIELD &fixedTypeValueField1]
                           VALUE-B FIELD &fixedTypeValueField2
                       }
                       
                       testObject TEST ::= {
                           VALUE-A FIELD TRUE
                           VALUE-B FIELD 1
                       }
                       
                       TestSet TEST ::= { testObject }
                    """;

            var objectSet = getCompiledObjectSet(body, "TEST", "TestSet");

            assertEquals(1, objectSet.getValues().size());

            var object = objectSet.getValues().stream().findFirst().get();
            var value1 = object.get("fixedTypeValueField1");

            assertTrue(value1 instanceof BooleanValue);

            var value2 = object.get("fixedTypeValueField2");

            assertTrue(value2 instanceof IntegerValue);
        }

        @Test
        void testObjectWithSyntaxIncompleteOptionalGroup() {
            var body = """
                       TEST ::= CLASS {
                           &fixedTypeValueField1 BOOLEAN OPTIONAL,
                           &fixedTypeValueField2 INTEGER
                       } WITH SYNTAX {
                           [VALUE-A FIELD &fixedTypeValueField1]
                           VALUE-B FIELD &fixedTypeValueField2
                       }
                       
                       testObject TEST ::= {
                           VALUE-A TRUE
                           VALUE-B FIELD 1
                       }
                       
                       TestSet TEST ::= { testObject }
                    """;

            testModule(body, CompilerException.class, ".*Expected literal 'FIELD' but found 'TRUE'.*");
        }

        @Test
        void testObjectWithSyntaxMandatoryOptionalGroupMissing() {
            var body = """
                       TEST ::= CLASS {
                           &fixedTypeValueField1 BOOLEAN OPTIONAL,
                           &fixedTypeValueField2 INTEGER OPTIONAL
                       } WITH SYNTAX {
                           [LITERAL [A &fixedTypeValueField1] [B &fixedTypeValueField2]]
                       }
                       
                       testObject TEST ::= {
                           LITERAL
                       }
                       
                       TestSet TEST ::= { testObject }
                    """;

            testModule(body, CompilerException.class, ".*There must be at least one field in an optional group.*");
        }

        @Test
        void testObjectWithSyntaxMandatoryOptionalGroupMissingRecursively() {
            var body = """
                       TEST ::= CLASS {
                           &fixedTypeValueField1 BOOLEAN OPTIONAL,
                           &fixedTypeValueField2 INTEGER OPTIONAL
                       } WITH SYNTAX {
                           [LITERAL [LITERAL [A &fixedTypeValueField1] [B &fixedTypeValueField2]]]
                       }
                       
                       testObject TEST ::= {
                           LITERAL LITERAL
                       }
                       
                       TestSet TEST ::= { testObject }
                    """;

            testModule(body, CompilerException.class, ".*There must be at least one field in an optional group.*");
        }

        @Test
        void testObjectWithSyntaxMandatoryOptionalGroupPresent() throws IOException, ParserException {
            var body = """
                       TEST ::= CLASS {
                           &fixedTypeValueField1 BOOLEAN OPTIONAL,
                           &fixedTypeValueField2 INTEGER OPTIONAL
                       } WITH SYNTAX {
                           [LITERAL [A &fixedTypeValueField1] [B &fixedTypeValueField2]]
                       }
                       
                       testObject TEST ::= {
                           LITERAL B 12
                       }
                       
                       TestSet TEST ::= { testObject }
                    """;

            var objectSet = getCompiledObjectSet(body, "TEST", "TestSet");

            assertEquals(1, objectSet.getValues().size());

            var object = objectSet.getValues().stream().findFirst().get();
            var value1 = object.get("fixedTypeValueField1");

            assertNull(value1);

            var value2 = object.get("fixedTypeValueField2");

            assertTrue(value2 instanceof IntegerValue);
        }

        @Test
        void testObjectReference() throws IOException, ParserException {
            var body = """
                    TEST ::= CLASS {
                        &id   INTEGER
                    }
                    
                    object TEST ::= {&id 123}
                    
                    objectRef TEST ::= object
                """;

            var compiledObject = getCompiledObject(body, "objectRef");
            var value = compiledObject.getObjectDefinition().get("id");

            assert(value instanceof IntegerValue);

            assertEquals(123, ((IntegerValue)value).getValue().longValue());
        }

    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideValueFromObject")
    @DisplayName("Test value from object")
    <V extends Value> void testValueFromObject(String typeName, String valueString, Class<V> valueClass,
            Consumer<V> valueTester) throws IOException, ParserException {
        var body = """
                TEST ::= CLASS {
                    &field %s
                }
                              
                object TEST ::= {&field %s}

                value %s ::= object.&field
                """.formatted(typeName, valueString, typeName);

        var module = module(MODULE_NAME, body);
        var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module));
        var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

        compiler.run();

        var ctx = compiler.getCompilerContext();
        var compiledValue = ctx.getCompiledModule(MODULE_NAME).getValues().get("value");

        assertNotNull(compiledValue);

        var value = compiledValue.getValue();

        assertNotNull(value);
        assertTrue(valueClass.isInstance(value));

        valueTester.accept(valueClass.cast(value));
    }

    @Test
    void testValueFromObjectWithNestedObjects() throws IOException, ParserException {
        var body = """
                TEST1 ::= CLASS {
                    &field1 TEST2
                }

                TEST2 ::= CLASS {
                    &field2 BOOLEAN
                }
                              
                object TEST1 ::= {&field1 {&field2 TRUE}}

                value BOOLEAN ::= object.&field1.&field2
                """;

        var module = module(MODULE_NAME, body);
        var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module));
        var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

        compiler.run();

        var ctx = compiler.getCompilerContext();
        var compiledValue = ctx.getCompiledModule(MODULE_NAME).getValues().get("value");

        assertNotNull(compiledValue);
        assertNotNull(compiledValue.getValue());
        assertTrue(compiledValue.getValue() instanceof BooleanValue);
        assertEquals(true, ((BooleanValue) compiledValue.getValue()).getValue());
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

        var compiledType = getCompiledType(body, MODULE_NAME, "TestSequence");

        assertTrue(compiledType instanceof CompiledCollectionType);

        var compiledSequence = (CompiledCollectionType) compiledType;

        assertEquals(1, compiledSequence.getComponents().size());

        var compiledComponent = compiledSequence.getComponents().get(0).getCompiledType();

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

    @Nested
    @DisplayName("Test type fields")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TypeField {

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

            var compiledType = getCompiledType(body, MODULE_NAME, "TestSequence");

            assertTrue(compiledType instanceof CompiledCollectionType);

            var compiledSequence = (CompiledCollectionType) compiledType;

            assertEquals(1, compiledSequence.getComponents().size());

            var compiledComponent = compiledSequence.getComponents().get(0).getCompiledType();

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
                    ".*'TestSequence' contains the optional open type 'typeField' which is ambiguous.*");
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
                    ".*'TestSet' contains the open type 'typeField' which is ambiguous.*");

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
                    ".*'TestSet' contains the open type 'typeField' which is ambiguous.*");
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
                    ".*'TestChoice' contains the open type 'typeField' which is ambiguous.*");
        }

    }

    @Nested
    @DisplayName("Test variable type value field")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class VariableTypeValueField {

        @Test
        void testVariableTypeValueField() throws IOException, ParserException {
            var body = """
                    TEST ::= CLASS {
                        &variableTypeValueField &TypeField,
                        &TypeField
                    }
                    """;

            var variableTypeValueField =
                    getCompiledVariableTypeValueField(body, "TEST", "variableTypeValueField");

            assertEquals("TypeField", variableTypeValueField.getReference());
            assertFalse(variableTypeValueField.isOptional());
        }

        @Test
        void testVariableTypeValueFieldIsOptional() throws IOException, ParserException {
            var body = """
                    TEST ::= CLASS {
                        &variableTypeValueField &TypeField OPTIONAL,
                        &TypeField
                    }
                    """;

            var variableTypeValueField =
                    getCompiledVariableTypeValueField(body, "TEST", "variableTypeValueField");

            assertEquals("TypeField", variableTypeValueField.getReference());
            assertTrue(variableTypeValueField.isOptional());
        }

        @Test
        void testVariableTypeValueFieldHasDefault() throws IOException, ParserException {
            var body = """
                    TEST ::= CLASS {
                        &variableTypeValueField &TypeField DEFAULT TRUE,
                        &TypeField DEFAULT BOOLEAN
                    }
                    """;

            var variableTypeValueField =
                    getCompiledVariableTypeValueField(body, "TEST", "variableTypeValueField");

            assertEquals("TypeField", variableTypeValueField.getReference());

            var defaultValue = variableTypeValueField.getDefaultValue();

            assertTrue(defaultValue.isPresent());
            assertTrue(defaultValue.get() instanceof BooleanValue);
        }

        @Test
        void testVariableTypeValueFieldHasDisallowedDefault() {
            var body = """
                    TEST ::= CLASS {
                        &variableTypeValueField &TypeField DEFAULT TRUE,
                        &TypeField
                    }
                    """;

            testModule(body, CompilerException.class,
                    ".*'variableTypeValueField' in object class 'TEST' defines a default value, but the referenced type field 'TypeField' has no default.*");
        }

        @Test
        void testVariableTypeValueFieldHasInvalidDefault() {
            var body = """
                    TEST ::= CLASS {
                        &variableTypeValueField &TypeField DEFAULT 4711,
                        &TypeField DEFAULT VisibleString
                    }
                    """;

            testModule(body, CompilerException.class,
                    ".*'variableTypeValueField' in object class 'TEST' expects a default value of type VisibleString but found '4711'.*");
        }

        @Test
        void testVariableTypeValueFieldInvalidReference() {
            var body = """
                    TEST ::= CLASS {
                        &variableTypeValueField &InvalidTypeField,
                        &TypeField
                    }
                    """;

            testModule(body, CompilerException.class,
                    ".*'variableTypeValueField' in object class 'TEST' refers to the inexistent field 'InvalidTypeField'.*");
        }

        @Test
        void testObjectsWithVariableTypeValueField() throws IOException, ParserException {
            var body = """
                    TEST ::= CLASS {
                        &variableTypeValueField &TypeField DEFAULT "abc",
                        &TypeField DEFAULT VisibleString
                    }

                    testObject1 TEST ::= {
                        &variableTypeValueField  FALSE,
                        &TypeField          BOOLEAN
                    }

                    testObject2 TEST ::= {
                        &variableTypeValueField  4711,
                        &TypeField          INTEGER
                    }

                    TestSet TEST ::= {
                        testObject1 | testObject2
                    }
                    """;

            var module = module(MODULE_NAME, body);
            var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module));
            var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

            compiler.run();

            var ctx = compiler.getCompilerContext();
            var objectSet = ctx.getCompiledModule(MODULE_NAME).getObjectSets().get("TestSet");

            assertNotNull(objectSet);
            assertEquals(2, objectSet.getValues().size());
        }

        @Test
        void testObjectsWithVariableTypeValueFieldDefaults() throws IOException, ParserException {
            var body = """
                    TEST ::= CLASS {
                        &variableTypeValueField &TypeField DEFAULT "abc",
                        &TypeField DEFAULT VisibleString
                    }

                    TestSet TEST ::= {
                        {}
                    }
                    """;

            var module = module(MODULE_NAME, body);
            var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module));
            var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

            compiler.run();

            var ctx = compiler.getCompilerContext();
            var objectSet = ctx.getCompiledModule(MODULE_NAME).getObjectSets().get("TestSet");

            assertNotNull(objectSet);
            assertEquals(1, objectSet.getValues().size());

            var objectDefinition = objectSet.getValues().stream().findFirst().get();
            var compiledType = (CompiledType) objectDefinition.get("TypeField");

            assertEquals(VisibleString.class, compiledType.getType().getClass());

            var value = (Value) objectDefinition.get("variableTypeValueField");

            assertEquals(VisibleStringValue.class, value.getClass());
        }

        @Test
        void testObjectsWithVariableTypeValueFieldDefaultsSequence() throws IOException, ParserException {
            var body = """
                    Seq ::= SEQUENCE {
                        a BOOLEAN,
                        b INTEGER
                    }

                    TEST ::= CLASS {
                        &variableTypeValueField &TypeField DEFAULT {a TRUE, b 23},
                        &TypeField DEFAULT Seq
                    }

                    TestSet TEST ::= {
                        {}
                    }
                    """;

            var module = module(MODULE_NAME, body);
            var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module));
            var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

            compiler.run();

            var ctx = compiler.getCompilerContext();
            var objectSet = ctx.getCompiledModule(MODULE_NAME).getObjectSets().get("TestSet");

            assertNotNull(objectSet);
            assertEquals(1, objectSet.getValues().size());

            var objectDefinition = objectSet.getValues().stream().findFirst().get();
            var compiledType = (CompiledType) objectDefinition.get("TypeField");

            assertEquals(SequenceType.class, compiledType.getType().getClass());

            var value = (Value) objectDefinition.get("variableTypeValueField");

            assertEquals(CollectionValue.class, value.getClass());
        }

        @Test
        void testObjectWithVariableTypeValueFieldWithInvalidType() {
            var body = """
                    TEST ::= CLASS {
                        &variableTypeValueField &TypeField,
                        &TypeField
                    }

                    TestSet TEST ::= {
                        { &variableTypeValueField FALSE, &TypeField INTEGER}
                    }
                    """;

            testModule(body, CompilerException.class,
                    ".*The value for variableTypeValueField in the object definition for TEST must be of the type INTEGER but found the value.*");
        }

    }

    @Test
    void testFixedTypeValueSetField() throws IOException, ParserException {
        var body = """
                TEST ::= CLASS {
                    &FixedTypeValueSetField INTEGER
                }
                """;

        Optional<AbstractCompiledField> field = getCompiledField(body, "TEST", "FixedTypeValueSetField");
        assertTrue(field.get() instanceof CompiledFixedTypeValueSetField);

        var fixedTypeValueSetField = (CompiledFixedTypeValueSetField) field.get();

        assertTrue(fixedTypeValueSetField.getCompiledType().getType() instanceof IntegerType);
    }

    @Nested
    @DisplayName("Test variable type value set")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class VariableTypeValueSetField {

        @Test
        void testVariableTypeValueSetField() throws IOException, ParserException {
            var body = """
                    TEST ::= CLASS {
                        &VariableTypeValueSetField &TypeField,
                        &TypeField
                    }
                    """;

            var variableTypeValueSetField =
                    getCompiledVariableTypeValueSetField(body, "TEST", "VariableTypeValueSetField");

            assertEquals("TypeField", variableTypeValueSetField.getReference());
            assertFalse(variableTypeValueSetField.isOptional());
        }

        @Test
        void testVariableTypeValueSetFieldIsOptional() throws IOException, ParserException {
            var body = """
                    TEST ::= CLASS {
                        &VariableTypeValueSetField &TypeField OPTIONAL,
                        &TypeField
                    }
                    """;

            var variableTypeValueSetField =
                    getCompiledVariableTypeValueSetField(body, "TEST", "VariableTypeValueSetField");

            assertEquals("TypeField", variableTypeValueSetField.getReference());
            assertTrue(variableTypeValueSetField.isOptional());
        }

    }

    @Nested
    @DisplayName("Test object field")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ObjectField {

        @Test
        void testObjectField() throws IOException, ParserException {
            var body = """
                    TEST ::= CLASS {
                        &objectField TEST2
                    }
                                    
                    TEST2 ::= CLASS {
                        &TypeField
                    }
                    """;

            var objectField = getCompiledObjectField(body, "TEST", "objectField");

            assertNotNull(objectField.getObjectClass());

            var referencedObjectClass = objectField.getObjectClass();

            assertEquals("TEST2", referencedObjectClass.getName());
        }

        @Test
        void testObjectFieldRecursion() throws IOException, ParserException {
            var body = """
                    TEST ::= CLASS {
                        &objectField TEST OPTIONAL
                    }
                    """;

            var objectField = getCompiledObjectField(body, "TEST", "objectField");

            assertNotNull(objectField.getObjectClass());

            var referencedObjectClass = objectField.getObjectClass();

            assertEquals("TEST", referencedObjectClass.getName());
        }

        @Test
        void testObjectFieldInvalidRecursion() {
            var body = """
                    TEST ::= CLASS {
                        &objectField TEST
                    }
                    """;

            testModule(body, CompilerException.class,
                    ".*The object field 'objectField' that refers to its defining object class 'TEST' must be marked as OPTIONAL.*");
        }
    }

    @Nested
    @DisplayName("Test object set field")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ObjectSetField {

        @Test
        void testObjectSetField() throws IOException, ParserException {
            var body = """
                    TEST ::= CLASS {
                        &ObjectSetField TEST2
                    }

                    TEST2 ::= CLASS {
                        &fixedTypeValueField INTEGER
                    }
                    """;

            var objectSetField = getCompiledObjectSetField(body, "TEST", "ObjectSetField");

            assertNotNull(objectSetField.getObjectClass());

            var referencedObjectClass = objectSetField.getObjectClass();

            assertEquals("TEST2", referencedObjectClass.getName());
        }

        @Test
        void testObjectSetFieldIsOptional() throws IOException, ParserException {
            var body = """
                    TEST ::= CLASS {
                        &ObjectSetField TEST2 OPTIONAL
                    }
                                    
                    TEST2 ::= CLASS {
                        &fixedTypeValueField INTEGER
                    }
                    """;

            var objectSetField = getCompiledObjectSetField(body, "TEST", "ObjectSetField");

            assertNotNull(objectSetField.getObjectClass());

            var referencedObjectClass = objectSetField.getObjectClass();

            assertEquals("TEST2", referencedObjectClass.getName());
            assertTrue(objectSetField.isOptional());
        }

    }

    @Test
    void testInstanceOfType() throws IOException, ParserException {
        var body = """
                   TEST ::= TYPE-IDENTIFIER
                   Test ::= INSTANCE OF TEST
                """;

        var compiledType = getCompiledType(body, MODULE_NAME, "Test");

        assertTrue(compiledType instanceof CompiledCollectionType);

        var compiledCollectionType = (CompiledCollectionType) compiledType;
        var components = compiledCollectionType.getComponents();

        assertEquals(2, components.size());

        var maybeTypeId = components.stream().filter(component -> component.getName().equals("type-id")).findFirst();

        assertTrue(maybeTypeId.isPresent());

        var typeId = maybeTypeId.get();

        assertTrue(typeId.getCompiledType().getType() instanceof ObjectIdentifier);

        var maybeValue = components.stream().filter(component -> component.getName().equals("value")).findFirst();

        assertTrue(maybeValue.isPresent());

        var value = maybeValue.get();

        assertTrue(value.getCompiledType().getType() instanceof OpenType);

        var maybeTags = compiledCollectionType.getTags();

        assertTrue(maybeTags.isPresent());

        var tags = maybeTags.get();

        assertEquals(1, tags.size());

        var tag = tags.get(0);

        assertEquals(Clazz.UNIVERSAL, tag.getClazz());
        assertEquals(8, tag.getTag());
    }

    @Nested
    @DisplayName("Test object class")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ObjectClass {

        @Test
        void testObjectClassTypeIdentifier() throws IOException, ParserException {
            var body = """
                       TEST ::= TYPE-IDENTIFIER
                    """;

            var objectClass = getCompiledObjectClass(body, "TEST");

            assertEquals(2, objectClass.getFields().size());

            var maybeField1 = objectClass.getField("id");

            assertTrue(maybeField1.isPresent());

            var field1 = maybeField1.get();

            assertTrue(field1 instanceof CompiledFixedTypeValueField);

            var compiledFixedTypeValueField = (CompiledFixedTypeValueField) field1;

            assertTrue(compiledFixedTypeValueField.isUnique());
            assertTrue(compiledFixedTypeValueField.getCompiledType().getType() instanceof ObjectIdentifier);

            var maybeField2 = objectClass.getField("Type");

            assertTrue(maybeField2.isPresent());

            var field2 = maybeField2.get();

            assertTrue(field2 instanceof CompiledTypeField);

            assertTrue(objectClass.getSyntax().isPresent());
        }

        @Test
        void testObjectClassAbstractSyntax() throws IOException, ParserException {
            var body = """
                       TEST ::= ABSTRACT-SYNTAX
                    """;

            var objectClass = getCompiledObjectClass(body, "TEST");

            assertEquals(3, objectClass.getFields().size());

            var maybeField1 = objectClass.getField("id");

            assertTrue(maybeField1.isPresent());

            var field1 = maybeField1.get();

            assertTrue(field1 instanceof CompiledFixedTypeValueField);

            var compiledFixedTypeValueField1 = (CompiledFixedTypeValueField) field1;

            assertTrue(compiledFixedTypeValueField1.isUnique());
            assertTrue(compiledFixedTypeValueField1.getCompiledType().getType() instanceof ObjectIdentifier);

            var maybeField2 = objectClass.getField("Type");

            assertTrue(maybeField2.isPresent());

            var field2 = maybeField2.get();

            assertTrue(field2 instanceof CompiledTypeField);

            var maybeField3 = objectClass.getField("property");

            assertTrue(maybeField3.isPresent());

            var field3 = maybeField3.get();

            assertTrue(field3 instanceof CompiledFixedTypeValueField);

            var compiledFixedTypeValueField3 = (CompiledFixedTypeValueField) field3;

            assertFalse(compiledFixedTypeValueField3.isUnique());
            assertTrue(compiledFixedTypeValueField3.getCompiledType().getType() instanceof BitString);
            assertNotNull(compiledFixedTypeValueField3.getDefaultValue());

            assertTrue(objectClass.getSyntax().isPresent());
        }

        @Test
        void testObjectClassReference() throws IOException, ParserException {
            var body = """
                    TEST ::= CLASS {
                        &fixedTypeValueField INTEGER
                    }

                    TEST2 ::= TEST
                    """;

            var compiledObjectClass = getCompiledObjectClass(body, "TEST2");

            assertNotNull(compiledObjectClass);
            assertEquals(1, compiledObjectClass.getFields().size());

            var maybeField = compiledObjectClass.getField("fixedTypeValueField");

            assertTrue(maybeField.isPresent());

            var field = maybeField.get();

            assertTrue(field instanceof CompiledFixedTypeValueField);

            var fixedTypeValueField = (CompiledFixedTypeValueField) field;

            assertTrue(fixedTypeValueField.getCompiledType().getType() instanceof IntegerType);
        }

        @Test
        void testObjectClassExternalReference() throws IOException, ParserException {
            var body1 = """
                    TEST2 ::= OTHER-MODULE.TEST
                    """;
            var body2 = """
                    TEST ::= CLASS {
                        &fixedTypeValueField INTEGER
                    }
                    """;

            var module1 = module(MODULE_NAME, body1);
            var module2 = module("OTHER-MODULE", body2);
            var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module1), Tuple2.of("OTHER-MODULE", module2));
            var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

            compiler.run();

            var ctx = compiler.getCompilerContext();
            var compiledObjectClass = ctx.getCompiledModule(MODULE_NAME).getObjectClasses().get("TEST2");

            assertNotNull(compiledObjectClass);
            assertEquals(1, compiledObjectClass.getFields().size());

            var maybeField = compiledObjectClass.getField("fixedTypeValueField");

            assertTrue(maybeField.isPresent());

            var field = maybeField.get();

            assertTrue(field instanceof CompiledFixedTypeValueField);

            var fixedTypeValueField = (CompiledFixedTypeValueField) field;

            assertTrue(fixedTypeValueField.getCompiledType().getType() instanceof IntegerType);
        }

        @Test
        void testObjectClassWithObjectClassFieldType() throws IOException, ParserException {
            var body = """
                    TEST1 ::= CLASS {
                        &fixedTypeValueField INTEGER
                    }

                    TEST2 ::= CLASS {
                        &fixedTypeValueField TEST1.&fixedTypeValueField
                    }
                    """;


            var objectClass = getCompiledObjectClass(body, "TEST2");

            assertEquals(1, objectClass.getFields().size());

            var maybeField = objectClass.getField("fixedTypeValueField");

            assertTrue(maybeField.isPresent());

            var field = maybeField.get();

            assertTrue(field instanceof CompiledFixedTypeValueField);

            var fixedTypeValueField = (CompiledFixedTypeValueField) field;

            assertTrue(fixedTypeValueField.getCompiledType().getType() instanceof IntegerType);
        }

        @Test
        void testObjectClassWithSyntax() throws IOException, ParserException {
            var body = """
                       TEST ::= CLASS {
                           &fixedTypeValueField BOOLEAN
                       } WITH SYNTAX {
                           FIXED TYPE VALUE &fixedTypeValueField
                       }
                    """;

            var objectClass = getCompiledObjectClass(body, "TEST");

            assertTrue(objectClass.getSyntax().isPresent());
        }

        @Test
        void testObjectClassWithSyntaxInvalidField() {
            var body = """
                       TEST ::= CLASS {
                           &fixedTypeValueField BOOLEAN
                       } WITH SYNTAX {
                           FIXED TYPE VALUE &nonExistentField
                       }
                    """;

            testModule(body, CompilerException.class,
                    ".*Syntax of object class 'TEST' references the undefined field 'nonExistentField'.*");
        }

        @Test
        void testObjectClassWithSyntaxInvalidOptionalField() {
            var body = """
                       TEST ::= CLASS {
                           &fixedTypeValueField BOOLEAN
                       } WITH SYNTAX {
                           [FIXED TYPE VALUE &fixedTypeValueField]
                       }
                    """;

            testModule(body, CompilerException.class,
                    ".*'fixedTypeValueField' in object class 'TEST' is defined in an optional group but refers to a mandatory field.*");
        }

        @Test
        void testObjectClassWithSyntaxOptionalField() throws IOException, ParserException {
            var body = """
                       TEST ::= CLASS {
                           &fixedTypeValueField BOOLEAN OPTIONAL
                       } WITH SYNTAX {
                           [FIXED TYPE VALUE &fixedTypeValueField]
                       }
                    """;

            var objectClass = getCompiledObjectClass(body, "TEST");

            assertTrue(objectClass.getSyntax().isPresent());
        }

        @Test
        void testObjectClassWithSyntaxDefaultField() throws IOException, ParserException {
            var body = """
                       TEST ::= CLASS {
                           &fixedTypeValueField BOOLEAN DEFAULT TRUE
                       } WITH SYNTAX {
                           [FIXED TYPE VALUE &fixedTypeValueField]
                       }
                    """;

            var objectClass = getCompiledObjectClass(body, "TEST");

            assertTrue(objectClass.getSyntax().isPresent());
        }

        @Test
        void testObjectClassWithSyntaxInvalidLiteralAfterOptionalGroup() {
            var body = """
                       TEST ::= CLASS {
                           &fixedTypeValueField1 BOOLEAN OPTIONAL,
                           &fixedTypeValueField2 BOOLEAN OPTIONAL
                       } WITH SYNTAX {
                           [FIXED TYPE VALUE &fixedTypeValueField1]
                           [FIXED TYPE VALUE &fixedTypeValueField2]
                       }
                    """;

            testModule(body, CompilerException.class,
                    ".*Literal 'FIXED' in object class 'TEST' is illegal at this position because it's also used as the first literal of a preceding optional group.*");

            body = """
                       TEST ::= CLASS {
                           &fixedTypeValueField1 BOOLEAN OPTIONAL,
                           &fixedTypeValueField2 BOOLEAN OPTIONAL
                       } WITH SYNTAX {
                           [FIXED TYPE VALUE &fixedTypeValueField1]
                           FIXED TYPE VALUE &fixedTypeValueField2
                       }
                    """;

            testModule(body, CompilerException.class,
                    ".*Literal 'FIXED' in object class 'TEST' is illegal at this position because it's also used as the first literal of a preceding optional group.*");

            body = """
                       TEST ::= CLASS {
                           &fixedTypeValueField1 BOOLEAN OPTIONAL,
                           &fixedTypeValueField2 BOOLEAN OPTIONAL
                       } WITH SYNTAX {
                           [FIXED [TYPE [VALUE &fixedTypeValueField1] [VALUE &fixedTypeValueField2]]]
                       }
                    """;

            testModule(body, CompilerException.class,
                    ".*Literal 'VALUE' in object class 'TEST' is illegal at this position because it's also used as the first literal of a preceding optional group.*");
        }

        @Test
        void testObjectClassWithSyntaxDuplicateField() {
            var body = """
                       TEST ::= CLASS {
                           &fixedTypeValueField BOOLEAN
                       } WITH SYNTAX {
                           FIELD &fixedTypeValueField
                           FIELD &fixedTypeValueField 
                       }
                    """;

            testModule(body, CompilerException.class,
                    ".*Field 'fixedTypeValueField' already used in the syntax definition of object class 'TEST'.*");
        }

        @Test
        void testObjectClassWithSyntaxMissingMandatoryField() {
            var body = """
                       TEST ::= CLASS {
                           &fixedTypeValueField1 BOOLEAN,
                           &fixedTypeValueField2 BOOLEAN
                       } WITH SYNTAX {
                           FIELD &fixedTypeValueField1
                       }
                    """;

            testModule(body, CompilerException.class,
                    ".*Not all mandatory fields are defined in the syntax for object class 'TEST': fixedTypeValueField2.*");
        }

        @ParameterizedTest(name = "[{index}] {1}")
        @MethodSource("getObjectClassWithSyntaxForbiddenLiteralsArgument")
        @DisplayName("Test forbidden literals in syntax of object class")
        void testObjectClassWithSyntaxForbiddenLiterals(String reservedWord, String description) {
            var body = """
                       TEST ::= CLASS {
                           &fixedTypeValueField BOOLEAN
                       } WITH SYNTAX {
                           %s &fixedTypeValueField
                       }
                    """.formatted(reservedWord);

            testModule(body, CompilerException.class,
                    ".*Literal '%s' in object class 'TEST' is a reserved word and may not be used.*".formatted(reservedWord));
        }

        Stream<Arguments> getObjectClassWithSyntaxForbiddenLiteralsArgument() {
            return Stream.of(
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("BIT"),
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("BOOLEAN"),
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("CHARACTER"),
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("CHOICE"),
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("DATE"),
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("DATE-TIME"),
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("DURATION"),
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("EMBEDDED"),
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("END"),
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("ENUMERATED"),
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("EXTERNAL"),
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("FALSE"),
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("INSTANCE"),
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("INTEGER"),
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("INTERSECTION"),
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("MINUS-INFINITY"),
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("NULL"),
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("OBJECT"),
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("OCTET"),
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("PLUS-INFINITY"),
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("RELATIVE-OID"),
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("SEQUENCE"),
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("SET"),
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("TIME"),
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("TIME-OF-DAY"),
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("TRUE"),
                    getObjectClassWithSyntaxForbiddenLiteralsArgument("UNION")
            );
        }

        Arguments getObjectClassWithSyntaxForbiddenLiteralsArgument(String reservedWord) {
            return Arguments.of(reservedWord, "Test that '%s' is forbidden in DefinedSyntax".formatted(reservedWord));
        }

    }

    @Test
    void testObjectSetEmpty() throws IOException, ParserException {
        var body = """
                TEST ::= TYPE-IDENTIFIER
                                
                TestSet TEST ::= {...}
                """;

        var objectSet = getCompiledObjectSet(body, "TEST", "TestSet");

        assertEquals(0, objectSet.getValues().size());
    }

    @Nested
    @DisplayName("Test parameterized types")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ParameterizedTypes {

        @Test
        void testParameterizedTypeWithSequence() throws IOException, ParserException {
            var body = """
                       AbstractSeq {Type} ::= SEQUENCE {
                           field Type
                       }
                       
                       Seq ::= AbstractSeq {BOOLEAN}
                    """;

            var compiledType = getCompiledType(body, MODULE_NAME, "Seq");

            assertTrue(compiledType instanceof CompiledCollectionType);

            testCollectionField((CompiledCollectionType) compiledType, "field", BooleanType.class);
        }

        @Test
        void testParameterizedTypeWithSequenceComponentsOf() throws IOException, ParserException {
            var body = """
                       TestSequence ::= SEQUENCE {
                           field2 INTEGER,
                           field3 VisibleString
                       }
                       
                       AbstractSeq {Type, SequenceType} ::= SEQUENCE {
                           field1 Type,
                           COMPONENTS OF SequenceType
                       }
                       
                       Seq ::= AbstractSeq {BOOLEAN, TestSequence}
                    """;

            var compiledType = getCompiledType(body, MODULE_NAME, "Seq");

            assertTrue(compiledType instanceof CompiledCollectionType);

            testCollectionField((CompiledCollectionType) compiledType, "field1", BooleanType.class);
            testCollectionField((CompiledCollectionType) compiledType, "field2", IntegerType.class);
            testCollectionField((CompiledCollectionType) compiledType, "field3", VisibleString.class);
        }

        @Test
        void testParameterizedTypeWithSequenceComponentsOfInline() throws IOException, ParserException {
            var body = """
                    AbstractSeq {Type} ::= SEQUENCE { 
                        COMPONENTS OF SEQUENCE {field1 INTEGER, field2 Type} 
                    }
                                    
                    Seq ::= AbstractSeq {BOOLEAN}
                    """;

            var compiledType = getCompiledType(body, MODULE_NAME, "Seq");

            assertTrue(compiledType instanceof CompiledCollectionType);

            testCollectionField((CompiledCollectionType) compiledType, "field1", IntegerType.class);
            testCollectionField((CompiledCollectionType) compiledType, "field2", BooleanType.class);
        }

        @Test
        void testParameterizedTypeWithSequenceNested() throws IOException, ParserException {
            var body = """
                       AbstractSeq {Type1, Type2} ::= SEQUENCE {
                           field1 Type1,
                           field2 SEQUENCE {
                                      field3 Type2
                                  }
                       }
                       
                       Seq ::= AbstractSeq {BOOLEAN, INTEGER}
                    """;

            var compiledType = getCompiledType(body, MODULE_NAME, "Seq");

            assertTrue(compiledType instanceof CompiledCollectionType);

            var collection = (CompiledCollectionType) compiledType;

            testCollectionField(collection, "field1", BooleanType.class);

            var field2 = testCollectionField(collection, "field2", SequenceType.class);

            testCollectionField((CompiledCollectionType) field2, "field3", IntegerType.class);
        }

        @Test
        void testParameterizedTypeWithSequenceUnusedParameters() {
            var body = """
                       AbstractSeq {Type1, Type2, Type3} ::= SEQUENCE {
                           field Type2
                       }
                       
                       Seq ::= AbstractSeq {INTEGER, BOOLEAN, VisibleString}
                    """;

            testModule(body, CompilerException.class, ".*Unused parameters in type 'AbstractSeq': Type1, Type3.*");
        }

        @Test
        void testParameterizedTypeWithSequenceParameterCount() {
            var body = """
                       AbstractSeq {Type1, Type2, Type3} ::= SEQUENCE {
                           field Type2
                       }
                       
                       Seq ::= AbstractSeq {INTEGER}
                    """;

            testModule(body, CompilerException.class,
                    ".*'Seq' passes 1 parameters but 'AbstractSeq' expects: Type1, Type2, Type3.*");
        }

        @Test
        void testParameterizedTypeWithSetComponentsOf() throws IOException, ParserException {
            var body = """
                       TestSet ::= SET {
                           field2 INTEGER,
                           field3 VisibleString
                       }
                       
                       AbstractSet {Type, SetType} ::= SET {
                           field1 Type,
                           COMPONENTS OF SetType
                       }
                       
                       Set ::= AbstractSet {BOOLEAN, TestSet}
                    """;

            var compiledType = getCompiledType(body, MODULE_NAME, "Set");

            assertTrue(compiledType instanceof CompiledCollectionType);

            testCollectionField((CompiledCollectionType) compiledType, "field1", BooleanType.class);
            testCollectionField((CompiledCollectionType) compiledType, "field2", IntegerType.class);
            testCollectionField((CompiledCollectionType) compiledType, "field3", VisibleString.class);
        }

        @Test
        void testParameterizedTypeWithSetComponentsOfExternalTypeReference() throws IOException, ParserException {
            var body1 = """              
                    Set1 ::= SET {
                        COMPONENTS OF OTHER-MODULE.Set2
                    }
                    """;
            var body2 = """
                    EXPORTS Set2;
                                    
                    Set2 ::= SET {
                        field1 INTEGER,
                        field2 BOOLEAN
                    }     
                    """;

            var module1 = module(MODULE_NAME, body1);
            var module2 = module("OTHER-MODULE", body2);
            var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module1), Tuple2.of("OTHER-MODULE", module2));
            var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

            compiler.run();

            var ctx = compiler.getCompilerContext();
            var compiledType = ctx.getCompiledModule(MODULE_NAME).getTypes().get("Set1");

            assertNotNull(compiledType);
            assertTrue(compiledType instanceof CompiledCollectionType);

            testCollectionField((CompiledCollectionType) compiledType, "field1", IntegerType.class);
            testCollectionField((CompiledCollectionType) compiledType, "field2", BooleanType.class);
        }

        @Test
        void testParameterizedTypeWithSetComponentsOfImportedType() throws IOException, ParserException {
            var body1 = """
                    IMPORTS Set2 FROM OTHER-MODULE;
                                    
                    Set1 ::= SET {
                        COMPONENTS OF Set2
                    }
                    """;
            var body2 = """
                    EXPORTS Set2;
                                    
                    Set2 ::= SET {
                        field1 INTEGER,
                        field2 BOOLEAN
                    }    
                    """;

            var module1 = module(MODULE_NAME, body1);
            var module2 = module("OTHER-MODULE", body2);
            var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module1), Tuple2.of("OTHER-MODULE", module2));
            var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

            compiler.run();

            var ctx = compiler.getCompilerContext();
            var compiledType = ctx.getCompiledModule(MODULE_NAME).getTypes().get("Set1");

            assertNotNull(compiledType);
            assertTrue(compiledType instanceof CompiledCollectionType);

            testCollectionField((CompiledCollectionType) compiledType, "field1", IntegerType.class);
            testCollectionField((CompiledCollectionType) compiledType, "field2", BooleanType.class);
        }

        @Test
        void testParameterizedTypeWithSetComponentsOfDuplicateTags() {
            var body = """
                       TestSet ::= SET {
                           field2 INTEGER,
                           field3 VisibleString
                       }
                       
                       AbstractSet {Type, SetType} ::= SET {
                           field1 Type,
                           COMPONENTS OF SetType
                       }
                       
                       Set ::= AbstractSet {INTEGER, TestSet}
                    """;

            testModule(body, CompilerException.class, ".*Duplicate tags in SET 'Set': field1 and field2.*");
        }

        @Test
        void testParameterizedTypeWithChoice() throws IOException, ParserException {
            var body = """
                       AbstractChoice {Type} ::= CHOICE {
                           field1 Type,
                           field2 INTEGER
                       }
                       
                       Choice ::= AbstractChoice {BOOLEAN}
                    """;

            var compiledType = getCompiledType(body, MODULE_NAME, "Choice");

            assertTrue(compiledType instanceof CompiledChoiceType);

            testChoiceField((CompiledChoiceType) compiledType, "field1", BooleanType.class);
            testChoiceField((CompiledChoiceType) compiledType, "field2", IntegerType.class);
        }

        @Test
        void testParameterizedTypeWithChoiceDuplicateTags() {
            var body = """
                       AbstractChoice {Type} ::= CHOICE {
                           field1 Type,
                           field2 INTEGER
                       }
                       
                       Choice ::= AbstractChoice {INTEGER}
                    """;

            testModule(body, CompilerException.class, ".*Duplicate tags in CHOICE 'Choice': field1 and field2.*");
        }

        @Test
        void testParameterizedTypeWithChoiceUnusedParameters() {
            var body = """
                       AbstractChoice {Type1, Type2, Type3} ::= CHOICE {
                           field Type2
                       }
                       
                       Choice ::= AbstractChoice {INTEGER, BOOLEAN, VisibleString}
                    """;

            testModule(body, CompilerException.class, ".*Unused parameters in type 'AbstractChoice': Type1, Type3.*");
        }

        @Test
        void testParameterizedTypeWithSequenceOf() throws IOException, ParserException {
            var body = """
                       AbstractSequenceOf {Type} ::= SEQUENCE OF Type
                       
                       SequenceOf ::= AbstractSequenceOf {BOOLEAN}
                    """;

            var compiledType = getCompiledType(body, MODULE_NAME, "SequenceOf");

            assertTrue(compiledType instanceof CompiledCollectionOfType);

            var collectionOf = (CompiledCollectionOfType) compiledType;

            assertTrue(collectionOf.getContentType().getType() instanceof BooleanType);
        }

        @Test
        void testParameterizedTypeWithSequenceOfUnusedParameters() {
            var body = """
                       AbstractSequenceOf {Type1, Type2, Type3} ::= SEQUENCE OF Type2
                       
                       SequenceOf ::= AbstractSequenceOf {INTEGER, BOOLEAN, VisibleString}
                    """;

            testModule(body, CompilerException.class, ".*Unused parameters in type 'AbstractSequenceOf': Type1, Type3.*");
        }

        @Test
        void testParameterizedTypeWithSetOf() throws IOException, ParserException {
            var body = """
                       AbstractSetOf {Type} ::= SET OF Type
                       
                       SetOf ::= AbstractSetOf {BOOLEAN}
                    """;

            var compiledType = getCompiledType(body, MODULE_NAME, "SetOf");

            assertTrue(compiledType instanceof CompiledCollectionOfType);

            var collectionOf = (CompiledCollectionOfType) compiledType;

            assertTrue(collectionOf.getContentType().getType() instanceof BooleanType);
        }


        @Test
        void testParameterizedExternalTypeReference() throws IOException, ParserException {
            var body1 = """
                    String ::= OTHER-MODULE.AbstractType {VisibleString}
                    """;
            var body2 = """
                    AbstractType {Type} ::= Type
                    """;

            var module1 = module(MODULE_NAME, body1);
            var module2 = module("OTHER-MODULE", body2);
            var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module1), Tuple2.of("OTHER-MODULE", module2));
            var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

            compiler.run();

            var ctx = compiler.getCompilerContext();
            var compiledType = ctx.getCompiledModule(MODULE_NAME).getTypes().get("String");

            assertNotNull(compiledType);
            assertTrue(compiledType.getType() instanceof VisibleString);
        }

        @Test
        void testParameterizedTypeAndValueWithSequence() throws IOException, ParserException {
            var body = """
                       AbstractSequence {Type, Type:value} ::= SEQUENCE {
                           field Type DEFAULT value
                       }
                       
                       Sequence ::= AbstractSequence {INTEGER, 23}
                    """;

            var compiledType = getCompiledType(body, MODULE_NAME, "Sequence");

            assertTrue(compiledType instanceof CompiledCollectionType);

            var collection = (CompiledCollectionType) compiledType;

            testCollectionField(collection, "field", IntegerType.class);
        }

        @Test
        void testParameterizedTypeWithInheritedParameter() throws IOException, ParserException {
            var body = """
                        Set ::= AbstractSet2 {BOOLEAN}
                                    
                        AbstractSet1 {Type1} ::= SET {
                            field Type1
                        }
                                    
                        AbstractSet2 {Type2} ::= SET {
                            COMPONENTS OF AbstractSet1 {Type2}
                        }
                    """;

            var compiledType = getCompiledType(body, MODULE_NAME, "Set");

            assertTrue(compiledType instanceof CompiledCollectionType);

            var collection = (CompiledCollectionType) compiledType;

            testCollectionField(collection, "field", BooleanType.class);
        }

        @Test
        void testParameterizedTypeWithInheritedParameterInSet() throws IOException, ParserException {
            var body = """
                        Set ::= AbstractSet2 {BOOLEAN}
                                    
                        AbstractSet1 {Type1} ::= SET {
                            field2 Type1
                        }
                                    
                        AbstractSet2 {Type2} ::= SET {
                            field1 AbstractSet1 {Type2}
                        }
                    """;

            var compiledType = getCompiledType(body, MODULE_NAME, "Set");

            assertTrue(compiledType instanceof CompiledCollectionType);

            var set = (CompiledCollectionType) compiledType;

            assertEquals(1, set.getComponents().size());
            assertTrue(set.getComponents().get(0) instanceof CompiledCollectionComponent);

            var field1 = set.getComponents().get(0).getCompiledType();

            assertEquals(1, ((CompiledCollectionType) field1).getComponents().size());

            var field2 = ((CompiledCollectionType) field1).getComponents().get(0);

            assertTrue(field2.getCompiledType().getType() instanceof BooleanType);
        }

        @Test
        void testParameterizedTypeWithInheritedParameterInSetOf() throws IOException, ParserException {
            var body = """
                        SetOf ::= AbstractSetOf2 {BOOLEAN}
                                    
                        AbstractSetOf1 {Type1} ::= SET OF Type1
                                    
                        AbstractSetOf2 {Type2} ::= SET OF AbstractSetOf1 {Type2}
                    """;

            var compiledType = getCompiledType(body, MODULE_NAME, "SetOf");

            assertTrue(compiledType instanceof CompiledCollectionOfType);

            var collection = (CompiledCollectionOfType) compiledType;

            assertTrue(collection.getContentType() instanceof CompiledCollectionOfType);

            var contentType = (CompiledCollectionOfType) collection.getContentType();

            assertTrue(contentType.getContentType().getType() instanceof BooleanType);
        }

        @Test
        void testParameterizedObjectSetInConstraint() throws IOException, ParserException {
            var body = """
                        AbstractSeq{OBJ-CLASS:ObjectSetParam} ::= SEQUENCE {
                          field OBJ-CLASS.&id({ObjectSetParam})
                        }
                        
                        ObjectSet OBJ-CLASS ::= {
                            {&id 123}
                        }
                        
                        OBJ-CLASS ::= CLASS {
                            &id INTEGER UNIQUE
                        }

                        Seq ::= AbstractSeq{{ObjectSet}}
                    """;

            var compiledType = getCompiledType(body, MODULE_NAME, "Seq");

            assertTrue(compiledType instanceof CompiledCollectionType);
        }

        @Test
        void testUserDefinedConstraintWithType() throws IOException, ParserException {
            var body = """
                    AbstractBitString{Type} ::= BIT STRING
                        (CONSTRAINED BY {-- comment1 -- Type -- comment2 --})

                    BitString ::= AbstractBitString{BOOLEAN}
                    """;

            var compiledType = getCompiledType(body, MODULE_NAME, "BitString");

            assertTrue(compiledType instanceof CompiledBitStringType);
        }

    }

    @Nested
    @DisplayName("Test type parameters")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TypeParameters {

        @ParameterizedTest(name = "[{index}] {4}")
        @MethodSource("provideParameterizedTypeInConstraint")
        @DisplayName("Test type parameters in constraints")
        <T extends AbstractNode> void testParameterizedTypeInConstraint(String body, String typeName, Class<T> nodeClass,
                Consumer<T> verifier, String description) throws IOException, ParserException {
            testConstraint(MODULE_NAME, body, typeName, nodeClass, verifier);
        }

        Stream<Arguments> provideParameterizedTypeInConstraint() {
            // @formatter:off
            return Stream.of(
                    Arguments.of("""
                                AbstractChoice {Type} ::= CHOICE  {
                                    a INTEGER,
                                    b BOOLEAN
                                }  (INCLUDES Type)
                
                                Choice ::= AbstractChoice {CHOICE {a INTEGER, b BOOLEAN} (a: 2)}
                            """,
                            "Choice",
                            ValueNode.class,
                            (Consumer<ValueNode>) node -> {
                                var value = (Set) node.getValue();

                                assertEquals(1, value.size());
                                assertTrue(value.iterator().next() instanceof ChoiceValue);
                            },
                            "CHOICE contained subtype constraint"),
                    Arguments.of("""
                                AbstractSet {Type} ::= SET  { 
                                    a INTEGER, 
                                    b BOOLEAN 
                                }  (INCLUDES Type)
                
                                Set ::= AbstractSet {SET {a INTEGER, b BOOLEAN} ({a 1, b TRUE})}
                            """,
                            "Set",
                            CollectionValueNode.class,
                            (Consumer<CollectionValueNode>) node -> assertEquals(1, node.getValue().size()),
                            "SET contained subtype constraint"),
                    Arguments.of("""
                                AbstractSetOf {Type} ::= SET (INCLUDES Type) OF INTEGER
                
                                SetOf ::= AbstractSetOf {SET ({1} | {2} | {3}) OF INTEGER}
                            """,
                            "SetOf",
                            CollectionOfValueNode.class,
                            (Consumer<CollectionOfValueNode>) node -> assertEquals(3, node.getValue().size()),
                            "SET OF contained subtype constraint"),
                    Arguments.of("""
                                AbstractEnumeration {Type} ::= Type (INCLUDES Type)
    
                                Enumeration ::= AbstractEnumeration {ENUMERATED {a, b, c}}
                            """,
                            "Enumeration",
                            AllValuesNode.class,
                            (Consumer<AllValuesNode>) node -> {},
                            "ENUMERATED contained subtype constraint")
            );
            // @formatter:on
        }

    }

    @Nested
    @DisplayName("Test value parameters")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ValueParameters {

        @Test
        void testParameterizedValueWithSequence() throws IOException, ParserException {
            var body = """
                       AbstractSequence {INTEGER:value} ::= SEQUENCE {
                           field INTEGER DEFAULT value
                       }
                       
                       Sequence ::= AbstractSequence {23}
                    """;

            var compiledType = getCompiledType(body, MODULE_NAME, "Sequence");

            assertTrue(compiledType instanceof CompiledCollectionType);

            var collection = (CompiledCollectionType) compiledType;

            testCollectionField(collection, "field", IntegerType.class);
        }

        @Test
        void testParameterizedValueWithTypeReferenceWithSequence() throws IOException, ParserException {
            var body = """
                       Integer ::= INTEGER
                       
                       AbstractSequence {Integer:value} ::= SEQUENCE {
                           field INTEGER DEFAULT value
                       }
                       
                       Sequence ::= AbstractSequence {23}
                    """;

            var compiledType = getCompiledType(body, MODULE_NAME, "Sequence");

            assertTrue(compiledType instanceof CompiledCollectionType);

            var collection = (CompiledCollectionType) compiledType;

            testCollectionField(collection, "field", IntegerType.class);
        }

        @Test
        void testParameterizedValueWithSequenceUnresolvableTypeReference() {
            var body = """
                       AbstractSequence {Integer:value} ::= SEQUENCE {
                           field INTEGER DEFAULT value
                       }
                       
                       Sequence ::= AbstractSequence {TRUE}
                    """;

            testModule(body, CompilerException.class,
                    ".*The Governor references the type Integer which can't be resolved.*");
        }

        @Test
        void testParameterizedValueWithSequenceInvalidTypeReference() {
            var body = """
                       AbstractSequence {integer:value} ::= SEQUENCE {
                           field INTEGER DEFAULT value
                       }
                       
                       Sequence ::= AbstractSequence {23}
                    """;

            testModule(body, CompilerException.class,
                    ".*The Governor 'integer' is not a valid typereference.*");
        }

        @Test
        void testParameterizedValueWithSequenceWrongValueType() {
            var body = """
                       AbstractSequence {INTEGER:value} ::= SEQUENCE {
                           field INTEGER DEFAULT value
                       }
                       
                       Sequence ::= AbstractSequence {TRUE}
                    """;

            testModule(body, CompilerException.class, ".*Expected a value of type INTEGER but found: TRUE.*");
        }

        @ParameterizedTest(name = "[{index}] {4}")
        @MethodSource("provideParameterizedValueInConstraint")
        @DisplayName("Test value parameters in constraints")
        <T extends AbstractNode> void testParameterizedValueInConstraint(String body, String typeName, Class<T> nodeClass,
                Consumer<T> verifier, String description) throws IOException, ParserException {
            testConstraint(MODULE_NAME, body, typeName, nodeClass, verifier);
        }

        @Test
        void testParameterizedValueWithObjectWithDefaultSyntax() throws IOException, ParserException {
            var body = """
                    TEST ::= CLASS {
                        &id INTEGER
                    }
                    
                    object{INTEGER:value} TEST ::= {&id value}
                    
                    objectRef TEST ::= object{456}
                """;

            var compiledObject = getCompiledObject(body, "objectRef");
            var value = compiledObject.getObjectDefinition().get("id");

            assert(value instanceof IntegerValue);

            assertEquals(456, ((IntegerValue)value).getValue().longValue());
        }

        Stream<Arguments> provideParameterizedValueInConstraint() {
            // @formatter:off
            return Stream.of(
                    Arguments.of("""
                                AbstractString {VisibleString:value} ::= VisibleString (value)
        
                                String ::= AbstractString {"test-value"}
                            """,
                            "String",
                            StringValueNode.class,
                            (Consumer<StringValueNode>) node -> {
                                var stringSingleValues = node.getValue();

                                assertEquals(1, stringSingleValues.size());

                                var stringSingleValue = (StringSingleValue) stringSingleValues.get(0);

                                assertEquals("test-value", stringSingleValue.getValue());
                            },
                            "VisibleString single value constraint"),
                    Arguments.of("""
                               AbstractBString {INTEGER:max} ::= BIT STRING (SIZE (0..max))
            
                               BString ::= AbstractBString {3}
                            """,
                            "BString",
                            SizeNode.class,
                            (Consumer<SizeNode>) node -> checkIntegerRange(node.getSize(),0, 3),
                            "BIT STRING size constraint"),
                    Arguments.of("""
                               AbstractNull {Null:value} ::= NULL (value)
            
                               Null ::= AbstractNull {NULL}
                            """,
                            "Null",
                            ValueNode.class,
                            (Consumer<ValueNode>) node -> assertTrue(node.getValue() instanceof ASN1Null.Value),
                            "NULL single value constraint with type reference in governor"),
                    Arguments.of("""
                               AbstractNull {NULL:value} ::= NULL (value)
            
                               Null ::= AbstractNull {NULL}
                            """,
                            "Null",
                            ValueNode.class,
                            (Consumer<ValueNode>) node -> assertTrue(node.getValue() instanceof ASN1Null.Value),
                            "NULL single value constraint"),
                    Arguments.of("""
                                AbstractRelativeOIDIRI {RELATIVE-OID-IRI:value} ::= RELATIVE-OID-IRI (value)
    
                                RelativeOIDIRI ::= AbstractRelativeOIDIRI {"a/b/a"}
                            """,
                            "RelativeOIDIRI",
                            RelativeIRIValueNode.class,
                            (Consumer<RelativeIRIValueNode>) node -> {
                                var values = node.getValue();

                                assertEquals(1, values.size());
                                assertEquals(Arrays.asList("a", "b", "a"), values.stream().findFirst().get());
                            },
                            "RELATIVE-OID-IRI single value constraint"),
                    Arguments.of("""
                                AbstractOIDIRI {OID-IRI:value} ::= OID-IRI (value)
    
                                OIDIRI ::= AbstractOIDIRI {"/ISO/a/b/a"}
                            """,
                            "OIDIRI",
                            IRIValueNode.class,
                            (Consumer<IRIValueNode>) node -> {
                                var values = node.getValue();

                                assertEquals(1, values.size());
                                assertEquals(Arrays.asList("ISO", "a", "b", "a"), values.stream().findFirst().get());
                            },
                            "OID-IRI single value constraint"),
                    Arguments.of("""
                                AbstractRelativeOID {RELATIVE-OID:value} ::= RELATIVE-OID (value)
    
                                RelativeOID ::= AbstractRelativeOID {{2 1 3}}
                            """,
                            "RelativeOID",
                            RelativeOIDValueNode.class,
                            (Consumer<RelativeOIDValueNode>) node -> {
                                var values = node.getValue();

                                assertEquals(1, values.size());
                                assertEquals(Arrays.asList(2, 1, 3), values.stream().findFirst().get());
                            },
                            "RELATIVE-OID single value constraint"),
                    Arguments.of("""
                                AbstractObjIdentifier {OBJECT IDENTIFIER:value} ::= OBJECT IDENTIFIER (value)
            
                                ObjIdentifier ::= AbstractObjIdentifier {{1 2 1 3}}
                            """,
                            "ObjIdentifier",
                            ObjectIdentifierValueNode.class,
                            (Consumer<ObjectIdentifierValueNode>) node -> {
                                var values = node.getValue();

                                assertEquals(1, values.size());
                                assertEquals(Arrays.asList(1, 2, 1, 3), values.stream().findFirst().get());
                            },
                            "OBJECT IDENTIFIER single value constraint"),
                    Arguments.of("""
                                AbstractEnumerated {Enumerated:value} ::= ENUMERATED {a, b, c} (value)
            
                                Enumerated ::= AbstractEnumerated {b}
                            """,
                            "Enumerated",
                            EnumeratedValueNode.class,
                            (Consumer<EnumeratedValueNode>) node -> {
                                var values = node.getValue();

                                assertEquals(1, values.size());
                                assertEquals(1, values.stream().findFirst().get());
                            },
                            "ENUMERATED single value constraint"),
                    Arguments.of("""
                                AbstractBoolean {BOOLEAN:value} ::= BOOLEAN (value)
    
                                Boolean ::= AbstractBoolean {FALSE}
                            """,
                            "Boolean",
                            ValueNode.class,
                            (Consumer<ValueNode>) node -> assertEquals(false, node.getValue()),
                            "BOOLEAN single value constraint"),
                    Arguments.of("""
                                AbstractInt {INTEGER:value} ::= INTEGER (value)
    
                                Int ::= AbstractInt {7}
                            """,
                            "Int",
                            IntegerRangeValueNode.class,
                            (Consumer<IntegerRangeValueNode>) node -> checkIntegerRange(node.getValue(),7, 7),
                            "INTEGER single value constraint"),
                    Arguments.of("""
                                AbstractString {VisibleString:upper} ::= VisibleString (FROM ("a"..upper))
                     
                                String ::= AbstractString {"f"}
                            """,
                            "String",
                            PermittedAlphabetNode.class,
                            (Consumer<PermittedAlphabetNode>) node -> {
                                var stringRanges = (StringValueNode) node.getNode();

                                assertEquals(1, stringRanges.getValue().size());

                                var stringRange = (StringRange) stringRanges.getValue().get(0);

                                assertEquals("a", stringRange.getLower());
                                assertEquals("f", stringRange.getUpper());
                            },
                            "VisibleString permitted alphabet constraint"),
                    Arguments.of("""
                                AbstractInt {INTEGER:max} ::= INTEGER (0..max)
    
                                Int ::= AbstractInt {4}
                            """,
                            "Int",
                            IntegerRangeValueNode.class,
                            (Consumer<IntegerRangeValueNode>) node -> checkIntegerRange(node.getValue(), 0, 4),
                            "INTEGER value range constraint"),
                    Arguments.of("""
                                AbstractSequence {INTEGER:int, BOOLEAN:bool} ::= SEQUENCE {
                                    a INTEGER, 
                                    b BOOLEAN
                                } (WITH COMPONENTS {a (int), b (bool)})
    
                                Seq ::= AbstractSequence {23, FALSE}
                            """,
                            "Seq",
                            WithComponentsNode.class,
                            (Consumer<WithComponentsNode>) node -> {
                                var components = node.getComponents();

                                assertEquals(2, components.size());

                                var integerRange = getComponentNode(components, "a", IntegerRangeValueNode.class);

                                checkIntegerRange(integerRange.getValue(), 23, 23);

                                var value = getComponentNode(components, "b", ValueNode.class);

                                assertEquals(false, value.getValue());
                            },
                            "SEQUENCE inner subtyping constraint")
            );
            // @formatter:on
        }

    }

    <T extends AbstractNode> void testConstraint(String moduleName, String body, String typeName, Class<T> nodeClass,
            Consumer<T> verifier) throws IOException, ParserException {
        // @formatter:off
        new CompilerTestBuilder()
                .moduleBuilder()
                    .name(moduleName)
                    .body(body)
                    .build()
                .typeTestBuilder()
                    .typeName(typeName)
                    .constraintTestBuilder(nodeClass)
                        .verify(verifier::accept)
                        .build()
                    .build()
                .build()
                .run();
        // @formatter:on
    }

    private void testCustomTagsOnComponents(String prefixedType, List<TagId> tags, boolean implicit)
            throws IOException, ParserException {
        var body = """
                Seq ::= SEQUENCE {
                    component %s
                }
                """.formatted(prefixedType);

        var module = module(MODULE_NAME, body, implicit);
        var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module));
        var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

        compiler.run();

        var ctx = compiler.getCompilerContext();
        var compiledType = ctx.getCompiledModule(MODULE_NAME).getTypes().get("Seq");

        assertNotNull(compiledType);
        assertTrue(compiledType instanceof CompiledCollectionType);

        var compiledSequence = (CompiledCollectionType) compiledType;

        assertEquals(1, compiledSequence.getComponents().size());

        var compiledComponent = compiledSequence.getComponents().get(0).getCompiledType();

        assertTrue(compiledComponent.getTags().isPresent());
        assertEquals(tags.size(), compiledComponent.getTags().get().size());
        assertEquals(tags, compiledComponent.getTags().get());
    }

    private void testChoiceField(CompiledChoiceType choice, String fieldName,
            Class<? extends Type> fieldType) {
        var components = choice.getComponents();
        var maybeField = components.stream()
                .filter(tuple -> tuple.getName().equals(fieldName))
                .findAny();

        assertTrue(maybeField.isPresent());

        var field = maybeField.get();

        assertTrue(fieldType.isAssignableFrom(field.getCompiledType().getType().getClass()));
    }

    private CompiledType testCollectionField(CompiledCollectionType collection, String fieldName,
            Class<? extends Type> fieldType) {
        var components = collection.getComponents();
        var maybeField = components.stream()
                .filter(tuple -> tuple.getName().equals(fieldName))
                .findAny();

        assertTrue(maybeField.isPresent());

        var field = maybeField.get();

        assertTrue(fieldType.isAssignableFrom(field.getCompiledType().getType().getClass()));

        return field.getCompiledType();
    }

    private void testCompiledCollection(String body, String collectionName) throws IOException, ParserException {
        var compiledType = getCompiledType(body, MODULE_NAME, collectionName);

        assertTrue(compiledType instanceof CompiledCollectionType);
    }

    private void testCompiledChoice(String body) throws IOException, ParserException {
        var compiledType = getCompiledType(body, MODULE_NAME, "TestChoice");

        assertTrue(compiledType instanceof CompiledChoiceType);
    }

    private void testModule(String body, Class<? extends Exception> expected, String message) {
        CompilerTestUtils.testModule(MODULE_NAME, body, expected, message);
    }

    private CompiledType getCompiledType(String body, String moduleName, String typeName)
            throws IOException, ParserException {
        var module = module(moduleName, body);
        var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module));
        var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

        compiler.run();

        var ctx = compiler.getCompilerContext();
        var compiledType = ctx.getCompiledModule(moduleName).getTypes().get(typeName);

        assertNotNull(compiledType);

        return compiledType;
    }

    private CompiledCollectionType getCompiledCollectionType(String body, String moduleName, String typeName)
            throws IOException, ParserException {
        var compiledType = getCompiledType(body, moduleName, typeName);

        assertTrue(compiledType instanceof CompiledCollectionType);

        return (CompiledCollectionType) compiledType;
    }

    private CompiledCollectionOfType getCompiledCollectionOfType(String body, String moduleName, String typeName)
            throws IOException, ParserException {
        var compiledType = getCompiledType(body, moduleName, typeName);

        assertTrue(compiledType instanceof CompiledCollectionOfType);

        return (CompiledCollectionOfType) compiledType;
    }

    private CompiledChoiceType getCompiledChoiceType(String body, String moduleName, String typeName)
            throws IOException, ParserException {
        var compiledType = getCompiledType(body, moduleName, typeName);

        assertTrue(compiledType instanceof CompiledChoiceType);

        return (CompiledChoiceType) compiledType;
    }

    private CompiledObjectClass getCompiledObjectClass(String body, String objectClassName)
            throws IOException, ParserException {
        var module = module(MODULE_NAME, body);
        var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module));
        var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

        compiler.run();

        var ctx = compiler.getCompilerContext();

        return ctx.getCompiledModule(MODULE_NAME).getObjectClasses().get(objectClassName);
    }

    private CompiledVariableTypeValueField getCompiledVariableTypeValueField(String body, String objectClassName,
            String fieldName) throws IOException, ParserException {
        var field = getCompiledField(body, objectClassName, fieldName);

        assertTrue(field.get() instanceof CompiledVariableTypeValueField);

        return (CompiledVariableTypeValueField) field.get();
    }

    private CompiledVariableTypeValueSetField getCompiledVariableTypeValueSetField(String body, String objectClassName,
            String fieldName) throws IOException, ParserException {
        var field = getCompiledField(body, objectClassName, fieldName);

        assertTrue(field.get() instanceof CompiledVariableTypeValueSetField);

        return (CompiledVariableTypeValueSetField) field.get();
    }

    private CompiledObjectField getCompiledObjectField(String body, String objectClassName, String fieldName)
            throws IOException, ParserException {
        var field = getCompiledField(body, objectClassName, fieldName);

        assertTrue(field.get() instanceof CompiledObjectField);

        return (CompiledObjectField) field.get();
    }

    private CompiledObjectSetField getCompiledObjectSetField(String body, String objectClassName, String fieldName)
            throws IOException, ParserException {
        var field = getCompiledField(body, objectClassName, fieldName);

        assertTrue(field.get() instanceof CompiledObjectSetField);

        return (CompiledObjectSetField) field.get();
    }

    private Optional<AbstractCompiledField> getCompiledField(String body, String objectClassName, String fieldName)
            throws IOException, ParserException {
        var objectClass = getCompiledObjectClass(body, objectClassName);

        var field = objectClass.getField(fieldName);

        assertTrue(field.isPresent());

        return field;
    }

    private CompiledObject getCompiledObject(String body, String objectName)
            throws IOException, ParserException {
        var module = module(MODULE_NAME, body);
        var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module));
        var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

        compiler.run();

        var ctx = compiler.getCompilerContext();
        var object = ctx.getCompiledModule(MODULE_NAME).getObjects().get(objectName);

        assertNotNull(object);

        return object;
    }

    private CompiledObjectSet getCompiledObjectSet(String body, String objectClassName, String setName)
            throws IOException, ParserException {
        var module = module(MODULE_NAME, body);
        var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module));
        var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

        compiler.run();

        var ctx = compiler.getCompilerContext();
        var objectClass = ctx.getCompiledModule(MODULE_NAME).getObjectClasses().get(objectClassName);

        assertTrue(objectClass.getSyntax().isPresent());

        return ctx.getCompiledModule(MODULE_NAME).getObjectSets().get(setName);
    }

    private static Optional<ComponentNode> getComponent(Set<ComponentNode> components, String componentName) {
        return components.stream()
                .filter(component -> componentName.equals(component.getName()))
                .findAny();
    }

    private static void checkIntegerRange(List<IntegerRange> value, int lower, int upper) {
        var integerRanges = value;

        assertEquals(1, integerRanges.size());

        var integerRange = integerRanges.get(0);

        assertEquals(lower, integerRange.getLower());
        assertEquals(upper, integerRange.getUpper());
    }

    private static void checkSizeConstraint(Node node, int lower, int upper) {
        assertTrue(node instanceof SizeNode);

        checkIntegerRange(((SizeNode) node).getSize(), lower, upper);
    }


    private static <T extends AbstractNode> T getComponentNode(Set<ComponentNode> components, String componentName,
            Class<T> nodeClass) {
        var maybeComponent = getComponent(components, componentName);

        assertTrue(maybeComponent.isPresent());

        var component = maybeComponent.get();
        var constraint = component.getConstraint();

        assertTrue(nodeClass.isAssignableFrom(constraint.getClass()));

        return nodeClass.cast(constraint);
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

    private static Stream<Arguments> provideValueFromObject() {
        return Stream.of(
                getValueFromObjectArguments(BOOLEAN, "TRUE", BooleanValue.class,
                        value -> assertEquals(true, value.getValue())),
                getValueFromObjectArguments(INTEGER, "23", IntegerValue.class,
                        value -> assertEquals(23, value.getValue().longValue())),
                getValueFromObjectArguments(BIT_STRING, "'1011'B", BitStringValue.class,
                        value -> assertArrayEquals(new byte[] { 0x0b }, value.getByteValue())),
                getValueFromObjectArguments(OCTET_STRING, "'0A'H", OctetStringValue.class,
                        value -> assertArrayEquals(new byte[] { 0x0a }, value.getByteValue())),
                getValueFromObjectArguments(NULL, "NULL", NullValue.class, value -> {
                }),
                getValueFromObjectArguments(OBJECT_IDENTIFIER, "{1 2 3 27}", ObjectIdentifierValue.class,
                        value -> assertEquals(Arrays.asList(1, 2, 3, 27), value.getComponents().stream()
                                .map(c -> c.getId()).collect(Collectors.toList()))),
                getValueFromObjectArguments(REAL, "1.2e5", RealValue.class,
                        value -> assertEquals(120000, value.getValue().longValue())),
                getValueFromObjectArguments("ENUMERATED {a, b, c}", "c", EnumeratedValue.class,
                        value -> assertEquals(2, value.getValue())),
                getValueFromObjectArguments(UTF8_STRING, "\"äöü\"", UTF8StringValue.class,
                        value -> assertEquals("äöü", value.getValue())),
                getValueFromObjectArguments(RELATIVE_OID, "{2 3 27}", RelativeOIDValue.class,
                        value -> assertEquals(Arrays.asList(2, 3, 27), value.getComponents().stream()
                                .map(c -> c.getId()).collect(Collectors.toList()))),
                getValueFromObjectArguments("SEQUENCE {a BOOLEAN}", "{a TRUE}", CollectionValue.class,
                        value -> assertTrue(value.getValues().stream()
                                .filter(nv -> nv.getName().equals("a"))
                                .findAny()
                                .isPresent())),
                getValueFromObjectArguments("SEQUENCE OF BOOLEAN", "{TRUE}", CollectionOfValue.class,
                        value -> assertEquals(1, value.getSize())),
                getValueFromObjectArguments("SET {a BOOLEAN}", "{a TRUE}", CollectionValue.class,
                        value -> assertTrue(value.getValues().stream()
                                .filter(nv -> nv.getName().equals("a"))
                                .findAny()
                                .isPresent())),
                getValueFromObjectArguments("SET OF BOOLEAN", "{TRUE}", CollectionOfValue.class,
                        value -> assertEquals(1, value.getSize())),
                getValueFromObjectArguments(NUMERIC_STRING, "\"123\"", NumericStringValue.class,
                        value -> assertEquals("123", value.getValue())),
                getValueFromObjectArguments(PRINTABLE_STRING, "\"abc\"", PrintableStringValue.class,
                        value -> assertEquals("abc", value.getValue())),
                getValueFromObjectArguments(TELETEX_STRING, "\"abc\"", TeletexStringValue.class,
                        value -> assertEquals("abc", value.getValue())),
                getValueFromObjectArguments(T61_STRING, "\"abc\"", TeletexStringValue.class,
                        value -> assertEquals("abc", value.getValue())),
                getValueFromObjectArguments(VIDEOTEX_STRING, "\"abc\"", VideotexStringValue.class,
                        value -> assertEquals("abc", value.getValue())),
                getValueFromObjectArguments(IA5_STRING, "\"abc\"", IA5StringValue.class,
                        value -> assertEquals("abc", value.getValue())),
                getValueFromObjectArguments(UTC_TIME, "\"8201021200Z\"", UTCTimeValue.class,
                        value -> assertEquals("8201021200Z", value.getValue())),
                getValueFromObjectArguments(GENERALIZED_TIME, "\"19851106210627.3Z\"", GeneralizedTimeValue.class,
                        value -> assertEquals("19851106210627.3Z", value.getValue())),
                getValueFromObjectArguments(GRAPHIC_STRING, "\"abc\"", GraphicStringValue.class,
                        value -> assertEquals("abc", value.getValue())),
                getValueFromObjectArguments(VISIBLE_STRING, "\"abc\"", VisibleStringValue.class,
                        value -> assertEquals("abc", value.getValue())),
                getValueFromObjectArguments(ISO646_STRING, "\"abc\"", VisibleStringValue.class,
                        value -> assertEquals("abc", value.getValue())),
                getValueFromObjectArguments(GENERAL_STRING, "\"abc\"", GeneralStringValue.class,
                        value -> assertEquals("abc", value.getValue())),
                getValueFromObjectArguments(UNIVERSAL_STRING, "\"abc\"", UniversalStringValue.class,
                        value -> assertEquals("abc", value.getValue())),
                getValueFromObjectArguments(BMP_STRING, "\"äöü\"", BMPStringValue.class,
                        value -> assertEquals("äöü", value.getValue())),
                getValueFromObjectArguments(OID_IRI, "\"/iso/a/b/c\"", IRIValue.class,
                        value -> assertEquals(Arrays.asList("iso", "a", "b", "c"), value.getArcIdentifierTexts())),
                getValueFromObjectArguments(RELATIVE_OID_IRI, "\"a/b/c\"", RelativeIRIValue.class,
                        value -> assertEquals(Arrays.asList("a", "b", "c"), value.getArcIdentifierTexts())));
    }

    private static <V extends Value> Arguments getValueFromObjectArguments(TypeName typeName, String value,
            Class<V> valueClass, Consumer<V> valueTest) {
        return Arguments.of(typeName.toString(), value, valueClass, valueTest);
    }

    private static <V extends Value> Arguments getValueFromObjectArguments(String typeName, String value,
            Class<V> valueClass, Consumer<V> valueTest) {
        return Arguments.of(typeName, value, valueClass, valueTest);
    }

    private static Stream<Arguments> provideSingleTypeConstraint() {
        return Stream.of(
                getSingleTypeConstraintArguments(BOOLEAN, "TRUE"),
                getSingleTypeConstraintArguments(INTEGER, "23"),
                getSingleTypeConstraintArguments(BIT_STRING, "'1011'B"),
                getSingleTypeConstraintArguments(OCTET_STRING, "'0A'H"),
                getSingleTypeConstraintArguments(NULL, "NULL"),
                getSingleTypeConstraintArguments(OBJECT_IDENTIFIER, "{1 2 3 27}"),
                getSingleTypeConstraintArguments("ENUMERATED {a, b, c}", "c"),
                getSingleTypeConstraintArguments(UTF8_STRING, "\"äöü\""),
                getSingleTypeConstraintArguments(RELATIVE_OID, "{2 3 27}"),
                getSingleTypeConstraintArguments("SEQUENCE {a BOOLEAN}", "{a TRUE}"),
                getSingleTypeConstraintArguments("SEQUENCE OF BOOLEAN", "TRUE"),
                getSingleTypeConstraintArguments("SET {a BOOLEAN}", "{a TRUE}"),
                getSingleTypeConstraintArguments("SET OF BOOLEAN", "TRUE"),
                getSingleTypeConstraintArguments(NUMERIC_STRING, "\"123\""),
                getSingleTypeConstraintArguments(PRINTABLE_STRING, "\"abc\""),
                getSingleTypeConstraintArguments(TELETEX_STRING, "\"abc\""),
                getSingleTypeConstraintArguments(T61_STRING, "\"abc\""),
                getSingleTypeConstraintArguments(VIDEOTEX_STRING, "\"abc\""),
                getSingleTypeConstraintArguments(IA5_STRING, "\"abc\""),
                getSingleTypeConstraintArguments(GRAPHIC_STRING, "\"abc\""),
                getSingleTypeConstraintArguments(VISIBLE_STRING, "\"abc\""),
                getSingleTypeConstraintArguments(ISO646_STRING, "\"abc\""),
                getSingleTypeConstraintArguments(GENERAL_STRING, "\"abc\""),
                getSingleTypeConstraintArguments(UNIVERSAL_STRING, "\"abc\""),
                getSingleTypeConstraintArguments(BMP_STRING, "\"äöü\""),
                getSingleTypeConstraintArguments(OID_IRI, "\"/iso/a/b/c\""),
                getSingleTypeConstraintArguments(RELATIVE_OID_IRI, "\"a/b/c\""));
    }

    private static Arguments getSingleTypeConstraintArguments(TypeName typeName, String value) {
        return Arguments.of(typeName.toString(), value);
    }

    private static Arguments getSingleTypeConstraintArguments(String typeName, String value) {
        return Arguments.of(typeName, value);
    }

}
