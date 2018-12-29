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

package ch.eskaton.asn4j.parser;

import ch.eskaton.asn4j.parser.Lexer.Context;
import ch.eskaton.asn4j.parser.Token.TokenType;
import ch.eskaton.asn4j.parser.ast.AbstractASN1FieldSpecNode;
import ch.eskaton.asn4j.parser.ast.AbstractSyntaxObjectClassReferenceNode;
import ch.eskaton.asn4j.parser.ast.AssignmentNode;
import ch.eskaton.asn4j.parser.ast.AtNotationNode;
import ch.eskaton.asn4j.parser.ast.ComponentIdListNode;
import ch.eskaton.asn4j.parser.ast.ComponentTypeListsNode;
import ch.eskaton.asn4j.parser.ast.DefaultObjectSetSpecNode;
import ch.eskaton.asn4j.parser.ast.DefaultSetSpecNode;
import ch.eskaton.asn4j.parser.ast.DefaultSpecNode;
import ch.eskaton.asn4j.parser.ast.DefaultSyntaxNode;
import ch.eskaton.asn4j.parser.ast.DefaultTypeSpecNode;
import ch.eskaton.asn4j.parser.ast.DefaultValueSetSpecNode;
import ch.eskaton.asn4j.parser.ast.DefaultValueSpecNode;
import ch.eskaton.asn4j.parser.ast.DefinedSyntaxNode;
import ch.eskaton.asn4j.parser.ast.DefinitiveIdentificationNode;
import ch.eskaton.asn4j.parser.ast.ElementSetSpecsNode;
import ch.eskaton.asn4j.parser.ast.EncodingControlSectionNode;
import ch.eskaton.asn4j.parser.ast.EncodingPrefixNode;
import ch.eskaton.asn4j.parser.ast.EnumerationItemNode;
import ch.eskaton.asn4j.parser.ast.ExceptionIdentificationNode;
import ch.eskaton.asn4j.parser.ast.ExportsNode;
import ch.eskaton.asn4j.parser.ast.ExtensionAdditionAlternativeNode;
import ch.eskaton.asn4j.parser.ast.ExtensionAndExceptionNode;
import ch.eskaton.asn4j.parser.ast.ExternalObjectClassReferenceNode;
import ch.eskaton.asn4j.parser.ast.ExternalObjectReferenceNode;
import ch.eskaton.asn4j.parser.ast.ExternalObjectSetReferenceNode;
import ch.eskaton.asn4j.parser.ast.FieldNameNode;
import ch.eskaton.asn4j.parser.ast.FieldSettingNode;
import ch.eskaton.asn4j.parser.ast.FieldSpecNode;
import ch.eskaton.asn4j.parser.ast.ImportNode;
import ch.eskaton.asn4j.parser.ast.LiteralNode;
import ch.eskaton.asn4j.parser.ast.LowerEndpointNode;
import ch.eskaton.asn4j.parser.ast.ModuleBodyNode;
import ch.eskaton.asn4j.parser.ast.ModuleIdentifierNode;
import ch.eskaton.asn4j.parser.ast.ModuleNode;
import ch.eskaton.asn4j.parser.ast.ModuleNode.Encoding;
import ch.eskaton.asn4j.parser.ast.ModuleRefNode;
import ch.eskaton.asn4j.parser.ast.NamedBitNode;
import ch.eskaton.asn4j.parser.ast.Node;
import ch.eskaton.asn4j.parser.ast.OIDComponentNode;
import ch.eskaton.asn4j.parser.ast.OIDNode;
import ch.eskaton.asn4j.parser.ast.ObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ObjectClassAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ObjectClassFieldTypeNode;
import ch.eskaton.asn4j.parser.ast.ObjectClassNode;
import ch.eskaton.asn4j.parser.ast.ObjectClassReferenceNode;
import ch.eskaton.asn4j.parser.ast.ObjectDefnNode;
import ch.eskaton.asn4j.parser.ast.ObjectFromObjectNode;
import ch.eskaton.asn4j.parser.ast.ObjectNode;
import ch.eskaton.asn4j.parser.ast.ObjectReferenceNode;
import ch.eskaton.asn4j.parser.ast.ObjectSetElementsNode;
import ch.eskaton.asn4j.parser.ast.ObjectSetReferenceNode;
import ch.eskaton.asn4j.parser.ast.ObjectSetSpecNode;
import ch.eskaton.asn4j.parser.ast.ObjectSyntaxNode;
import ch.eskaton.asn4j.parser.ast.OptionalSpecNode;
import ch.eskaton.asn4j.parser.ast.OptionalitySpecNode;
import ch.eskaton.asn4j.parser.ast.ParamGovernorNode;
import ch.eskaton.asn4j.parser.ast.ParameterNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedObjectClassAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedObjectSetAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedTypeAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedTypeOrObjectClassAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedValueAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedValueOrObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedValueSetTypeAssignmentNode;
import ch.eskaton.asn4j.parser.ast.PrimitiveFieldNameNode;
import ch.eskaton.asn4j.parser.ast.PropertyAndSettingNode;
import ch.eskaton.asn4j.parser.ast.RangeNode;
import ch.eskaton.asn4j.parser.ast.ReferenceNode;
import ch.eskaton.asn4j.parser.ast.ReferencedObjectsNode;
import ch.eskaton.asn4j.parser.ast.SetFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.SetSpecsNode;
import ch.eskaton.asn4j.parser.ast.SimpleTableConstraintNode;
import ch.eskaton.asn4j.parser.ast.TypeAssignmentNode;
import ch.eskaton.asn4j.parser.ast.TypeFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.TypeIdentifierObjectClassReferenceNode;
import ch.eskaton.asn4j.parser.ast.TypeOrObjectClassAssignmentNode;
import ch.eskaton.asn4j.parser.ast.UpperEndpointNode;
import ch.eskaton.asn4j.parser.ast.UserDefinedConstraintNode;
import ch.eskaton.asn4j.parser.ast.UserDefinedConstraintParamNode;
import ch.eskaton.asn4j.parser.ast.ValueAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ValueOrObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ValueSetTypeOrObjectSetAssignmentNode;
import ch.eskaton.asn4j.parser.ast.VariableTypeValueFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.VariableTypeValueSetFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.constraints.ComponentConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.ComponentRelationConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.Constraint;
import ch.eskaton.asn4j.parser.ast.constraints.ContainedSubtype;
import ch.eskaton.asn4j.parser.ast.constraints.ContentsConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet.OpType;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.constraints.MultipleTypeConstraints;
import ch.eskaton.asn4j.parser.ast.constraints.NamedConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.PatternConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.PermittedAlphabetConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.PresenceConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.PropertySettingsConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SizeConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SubtypeConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.TableConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.TypeConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.ValueConstraint;
import ch.eskaton.asn4j.parser.ast.types.AlternativeTypeLists;
import ch.eskaton.asn4j.parser.ast.types.BMPString;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.BooleanType;
import ch.eskaton.asn4j.parser.ast.types.CharacterString;
import ch.eskaton.asn4j.parser.ast.types.Choice;
import ch.eskaton.asn4j.parser.ast.types.ClassNumber;
import ch.eskaton.asn4j.parser.ast.types.ClassType;
import ch.eskaton.asn4j.parser.ast.types.ComponentType;
import ch.eskaton.asn4j.parser.ast.types.DateTime;
import ch.eskaton.asn4j.parser.ast.types.DateType;
import ch.eskaton.asn4j.parser.ast.types.Duration;
import ch.eskaton.asn4j.parser.ast.types.EmbeddedPDV;
import ch.eskaton.asn4j.parser.ast.types.EnumeratedType;
import ch.eskaton.asn4j.parser.ast.types.ExtensionAdditionAlternativesGroup;
import ch.eskaton.asn4j.parser.ast.types.ExtensionAdditionGroup;
import ch.eskaton.asn4j.parser.ast.types.External;
import ch.eskaton.asn4j.parser.ast.types.ExternalTypeReference;
import ch.eskaton.asn4j.parser.ast.types.GeneralString;
import ch.eskaton.asn4j.parser.ast.types.GeneralizedTime;
import ch.eskaton.asn4j.parser.ast.types.GraphicString;
import ch.eskaton.asn4j.parser.ast.types.IA5String;
import ch.eskaton.asn4j.parser.ast.types.IRI;
import ch.eskaton.asn4j.parser.ast.types.ISO646String;
import ch.eskaton.asn4j.parser.ast.types.InformationFromObjects;
import ch.eskaton.asn4j.parser.ast.types.InstanceOfType;
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.asn4j.parser.ast.types.Null;
import ch.eskaton.asn4j.parser.ast.types.NumericString;
import ch.eskaton.asn4j.parser.ast.types.ObjectDescriptor;
import ch.eskaton.asn4j.parser.ast.types.ObjectIdentifier;
import ch.eskaton.asn4j.parser.ast.types.OctetString;
import ch.eskaton.asn4j.parser.ast.types.PrintableString;
import ch.eskaton.asn4j.parser.ast.types.Real;
import ch.eskaton.asn4j.parser.ast.types.RelativeIRI;
import ch.eskaton.asn4j.parser.ast.types.RelativeOID;
import ch.eskaton.asn4j.parser.ast.types.SelectionType;
import ch.eskaton.asn4j.parser.ast.types.SequenceOfType;
import ch.eskaton.asn4j.parser.ast.types.SequenceType;
import ch.eskaton.asn4j.parser.ast.types.SetOfType;
import ch.eskaton.asn4j.parser.ast.types.SetType;
import ch.eskaton.asn4j.parser.ast.types.SimpleDefinedType;
import ch.eskaton.asn4j.parser.ast.types.T61String;
import ch.eskaton.asn4j.parser.ast.types.TeletexString;
import ch.eskaton.asn4j.parser.ast.types.Time;
import ch.eskaton.asn4j.parser.ast.types.TimeOfDay;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeFromObjects;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.types.UTCTime;
import ch.eskaton.asn4j.parser.ast.types.UTF8String;
import ch.eskaton.asn4j.parser.ast.types.UniversalString;
import ch.eskaton.asn4j.parser.ast.types.UsefulType;
import ch.eskaton.asn4j.parser.ast.types.VideotexString;
import ch.eskaton.asn4j.parser.ast.types.VisibleString;
import ch.eskaton.asn4j.parser.ast.values.AmbiguousValue;
import ch.eskaton.asn4j.parser.ast.values.BinaryStringValue;
import ch.eskaton.asn4j.parser.ast.values.BitStringValue;
import ch.eskaton.asn4j.parser.ast.values.BooleanValue;
import ch.eskaton.asn4j.parser.ast.values.CharacterStringList;
import ch.eskaton.asn4j.parser.ast.values.ChoiceValue;
import ch.eskaton.asn4j.parser.ast.values.CollectionOfValue;
import ch.eskaton.asn4j.parser.ast.values.CollectionValue;
import ch.eskaton.asn4j.parser.ast.values.ContainingStringValue;
import ch.eskaton.asn4j.parser.ast.values.DefinedValue;
import ch.eskaton.asn4j.parser.ast.values.EmptyValue;
import ch.eskaton.asn4j.parser.ast.values.ExternalValueReference;
import ch.eskaton.asn4j.parser.ast.values.HexStringValue;
import ch.eskaton.asn4j.parser.ast.values.IRIValue;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.NamedNumber;
import ch.eskaton.asn4j.parser.ast.values.NamedValue;
import ch.eskaton.asn4j.parser.ast.values.NullValue;
import ch.eskaton.asn4j.parser.ast.values.ObjectIdentifierValue;
import ch.eskaton.asn4j.parser.ast.values.OpenTypeFieldValue;
import ch.eskaton.asn4j.parser.ast.values.RealValue;
import ch.eskaton.asn4j.parser.ast.values.RelativeIRIValue;
import ch.eskaton.asn4j.parser.ast.values.RelativeOIDValue;
import ch.eskaton.asn4j.parser.ast.values.SignedNumber;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.StringValue;
import ch.eskaton.asn4j.parser.ast.values.Tag;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.parser.ast.values.ValueFromObject;
import ch.eskaton.asn4j.runtime.TaggingMode;
import ch.eskaton.commons.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

public class Parser {

    private Lexer lexer;

    private Stack<Context> lexerContext = new Stack<Context>();

    private LinkedList<Integer> marks = new LinkedList<Integer>();

    private LinkedList<Token> tokens = new LinkedList<Token>();

    private Token.TokenType lastExpectedToken;

    private Token lastErrorToken;

    private int lastErrorOffset = 0;

    private ParserException lastException;

    // Parsers
    private ActualParameterListParser actualParameterListParser = new ActualParameterListParser();
    private ActualParameterParser actualParameterParser = new ActualParameterParser();
    private AlternativeTypeListParser alternativeTypeListParser = new AlternativeTypeListParser();
    private AlternativeTypeListsParser alternativeTypeListsParser = new AlternativeTypeListsParser();
    private AssignedIdentifierParser assignedIdentifierParser = new AssignedIdentifierParser();
    private AssignmentListParser assignmentListParser = new AssignmentListParser();
    private AssignmentParser assignmentParser = new AssignmentParser();
    private AtNotationParser atNotationParser = new AtNotationParser();
    private BMPStringParser bmpStringParser = new BMPStringParser();
    private BitOrOctetStringValueParser bitStringValueParser = new BitOrOctetStringValueParser();
    private BooleanValueParser booleanValueParser = new BooleanValueParser();
    private BuiltinOrReferencedValueParser builtinOrReferencedValueParser = new BuiltinOrReferencedValueParser();
    private BuiltinTypeParser builtinTypeParser = new BuiltinTypeParser();
    private BuiltinTypeParserAux builtinTypeParserAux = new BuiltinTypeParserAux();
    private CharSymsParser charSymsParser = new CharSymsParser();
    private CharacterStringListParser characterStringListParser = new CharacterStringListParser();
    private CharacterStringTypeParser characterStringTypeParser = new CharacterStringTypeParser();
    private CharacterStringValueParser characterStringValueParser = new CharacterStringValueParser();
    private CharsDefnParser charsDefnParser = new CharsDefnParser();
    private ChoiceValueParser choiceValueParser = new ChoiceValueParser();
    private ClassNumberParser classNumberParser = new ClassNumberParser();
    private ClassParser classParser = new ClassParser();
    private CollectionValueParser collectionValueParser = new CollectionValueParser();
    private ComponentConstraintParser componentConstraintParser = new ComponentConstraintParser();
    private ComponentIdListParser componentIdListParser = new ComponentIdListParser();
    private ComponentRelationConstraintParser componentRelationConstraintParser = new ComponentRelationConstraintParser();
    private ComponentTypeListParser componentTypeListParser = new ComponentTypeListParser();
    private ComponentTypeListsParser componentTypeListsParser = new ComponentTypeListsParser();
    private ComponentTypeParser componentTypeParser = new ComponentTypeParser();
    private ConstraintParser constraintParser = new ConstraintParser();
    private ConstraintSpecParser constraintSpecParser = new ConstraintSpecParser();
    private ContainedSubtypeParser containedSubtypeParser = new ContainedSubtypeParser();
    private ContentsConstraintParser contentsConstraintParser = new ContentsConstraintParser();
    private DefaultSyntaxParser defaultSyntaxParser = new DefaultSyntaxParser();
    private DefinedObjectClassParser definedObjectClassParser = new DefinedObjectClassParser();
    private DefinedObjectParser definedObjectParser = new DefinedObjectParser();
    private DefinedObjectSetParser definedObjectSetParser = new DefinedObjectSetParser();
    private DefinedSyntaxParser definedSyntaxParser = new DefinedSyntaxParser();
    private DefinedSyntaxTokenParser definedSyntaxTokenParser = new DefinedSyntaxTokenParser();
    private DefinedValueParser definedValueParser = new DefinedValueParser();
    private DefinitiveIdentificationParser definitiveIdentificationParser = new DefinitiveIdentificationParser();
    private DefinitiveNameAndNumberFormParser definitiveNameAndNumberFormParser = new DefinitiveNameAndNumberFormParser();
    private DefinitiveNumberFormParser definitiveNumberFormParser = new DefinitiveNumberFormParser();
    private DefinitiveOIDParser definitiveOIDParser = new DefinitiveOIDParser();
    private DefinitiveObjIdComponentListParser definitiveObjIdComponentListParser = new DefinitiveObjIdComponentListParser();
    private DefinitiveObjIdComponentParser definitiveObjIdComponentParser = new DefinitiveObjIdComponentParser();
    private DummyGovernorParser dummyGovernorParser = new DummyGovernorParser();
    private DummyReferenceParser dummyReferenceParser = new DummyReferenceParser();
    private DurationRangeParser durationRangeParser = new DurationRangeParser();
    private ElementSetSpecParser elementSetSpecParser = new ElementSetSpecParser();
    private ElementsParser elementsParser = new ElementsParser();
    private EmbeddedPDVValueParser embeddedPDVValueParser = new EmbeddedPDVValueParser();
    private EmptyValueParser emptyValueParser = new EmptyValueParser();
    private EncodingControlSectionParser encodingControlSectionParser = new EncodingControlSectionParser();
    private EncodingControlSectionsParser encodingControlSectionsParser = new EncodingControlSectionsParser();
    private EncodingPrefixParser encodingPrefixParser = new EncodingPrefixParser();
    private EncodingPrefixedTypeParser encodingPrefixedTypeParser = new EncodingPrefixedTypeParser();
    private EncodingReferenceDefaultParser encodingReferenceDefaultParser = new EncodingReferenceDefaultParser();
    private EncodingReferenceParser encodingReferenceParser = new EncodingReferenceParser();
    private EnumerationItemParser enumerationItemParser = new EnumerationItemParser();
    private EnumerationParser enumerationParser = new EnumerationParser();
    private EnumerationsParser enumerationsParser = new EnumerationsParser();
    private ExceptionIdentificationParser exceptionIdentificationParser = new ExceptionIdentificationParser();
    private ExceptionSpecParser exceptionSpecParser = new ExceptionSpecParser();
    private ExclusionsParser exclusionsParser = new ExclusionsParser();
    private ExportsParser exportsParser = new ExportsParser();
    private ExtensionAdditionAlternativeParser extensionAdditionAlternativeParser = new ExtensionAdditionAlternativeParser();
    private ExtensionAdditionAlternativesGroupParser extensionAdditionAlternativesGroupParser = new ExtensionAdditionAlternativesGroupParser();
    private ExtensionAdditionAlternativesListParser extensionAdditionAlternativesListParser = new ExtensionAdditionAlternativesListParser();
    private ExtensionAdditionAlternativesParser extensionAdditionAlternativesParser = new ExtensionAdditionAlternativesParser();
    private ExtensionAdditionGroupParser extensionAdditionGroupParser = new ExtensionAdditionGroupParser();
    private ExtensionAdditionListParser extensionAdditionListParser = new ExtensionAdditionListParser();
    private ExtensionAdditionParser extensionAdditionParser = new ExtensionAdditionParser();
    private ExtensionAdditionsParser extensionAdditionsParser = new ExtensionAdditionsParser();
    private ExtensionAndExceptionParser extensionAndExceptionParser = new ExtensionAndExceptionParser();
    private ExtensionDefaultParser extensionDefaultParser = new ExtensionDefaultParser();
    private ExtensionEndMarkerParser extensionEndMarkerParser = new ExtensionEndMarkerParser();
    private ExternalObjectClassReferenceParser externalObjectClassReferenceParser = new ExternalObjectClassReferenceParser();
    private ExternalObjectReferenceParser externalObjectReferenceParser = new ExternalObjectReferenceParser();
    private ExternalObjectSetReferenceParser externalObjectSetReferenceParser = new ExternalObjectSetReferenceParser();
    private ExternalTypeReferenceParser externalTypeReferenceParser = new ExternalTypeReferenceParser();
    private ExternalValueReferenceParser externalValueReferenceParser = new ExternalValueReferenceParser();
    private FieldNameParser fieldNameParser = new FieldNameParser();
    private FieldSettingParser fieldSettingParser = new FieldSettingParser();
    private FieldSpecParser fieldSpecParser = new FieldSpecParser();
    private FixedTypeFieldValParser fixedTypeFieldValParser = new FixedTypeFieldValParser();
    private FixedTypeValueOrObjectFieldSpecParser fixedTypeValueFieldSpecParser = new FixedTypeValueOrObjectFieldSpecParser();
    private FullSpecificationParser fullSpecificationParser = new FullSpecificationParser();
    private GeneralConstraintParser generalConstraintParser = new GeneralConstraintParser();
    private GeneralStringParser generalStringParser = new GeneralStringParser();
    private GlobalModuleReferenceParser globalModuleReferenceParser = new GlobalModuleReferenceParser();
    private GovernorParser governorParser = new GovernorParser();
    private GraphicStringParser graphicStringParser = new GraphicStringParser();
    private IA5StringParser ia5StringParser = new IA5StringParser();
    private IRIValueParser iriValueParser = new IRIValueParser();
    private ISO646StringParser iso646StringParser = new ISO646StringParser();
    private IdentifierListParser identifierListParser = new IdentifierListParser();
    private ImportsParser importsParser = new ImportsParser();
    private InformationFromObjectsParser informationFromObjectsParser = new InformationFromObjectsParser();
    private InnerTypeConstraintsParser innerTypeConstraintsParser = new InnerTypeConstraintsParser();
    private IntegerValueParser integerValueParser = new IntegerValueParser();
    private IntersectionElementsParser intersectionElementsParser = new IntersectionElementsParser();
    private IntersectionsParser intersectionsParser = new IntersectionsParser();
    private LevelParser levelParser = new LevelParser();
    private LiteralParser literalParser = new LiteralParser();
    private LowerEndValueParser lowerEndValueParser = new LowerEndValueParser();
    private LowerEndpointParser lowerEndpointParser = new LowerEndpointParser();
    private ModuleBodyParser moduleBodyParser = new ModuleBodyParser();
    private ModuleIdentifierParser moduleIdentifierParser = new ModuleIdentifierParser();
    private MultipleTypeConstraintsParser multipleTypeConstraintsParser = new MultipleTypeConstraintsParser();
    private NameAndNumberFormParser nameAndNumberFormParser = new NameAndNumberFormParser();
    private NameFormParser nameFormParser = new NameFormParser();
    private NamedBitListParser namedBitListParser = new NamedBitListParser();
    private NamedBitParser namedBitParser = new NamedBitParser();
    private NamedConstraintParser namedConstraintParser = new NamedConstraintParser();
    private NamedNumberListParser namedNumberListParser = new NamedNumberListParser();
    private NamedNumberParser namedNumberParser = new NamedNumberParser();
    private NamedTypeParser namedTypeParser = new NamedTypeParser();
    private NamedValueListParser namedValueListParser = new NamedValueListParser();
    private NamedValueParser namedValueParser = new NamedValueParser();
    private NullValueParser nullValueParser = new NullValueParser();
    private NumberFormParser numberFormParser = new NumberFormParser();
    private NumericRealValueParser numericRealValueParser = new NumericRealValueParser();
    private NumericStringParser numericStringParser = new NumericStringParser();
    private ObjIdComponentsListParser objIdComponentsListParser = new ObjIdComponentsListParser();
    private ObjIdComponentsParser objIdComponentsParser = new ObjIdComponentsParser();
    private ObjectClassDefnParser objectClassDefnParser = new ObjectClassDefnParser();
    private ObjectClassFieldTypeParser objectClassFieldTypeParser = new ObjectClassFieldTypeParser();
    private ObjectClassFieldValueParser objectClassFieldValueParser = new ObjectClassFieldValueParser();
    private ObjectClassParser objectClassParser = new ObjectClassParser();
    private ObjectDefnParser objectDefnParser = new ObjectDefnParser();
    private ObjectIdentifierValueParser objectIdentifierValueParser = new ObjectIdentifierValueParser();
    private ObjectParser objectParser = new ObjectParser();
    private ObjectSetElementsParser objectSetElementsParser = new ObjectSetElementsParser();
    private ObjectSetParser objectSetParser = new ObjectSetParser();
    private OpenTypeFieldValParser openTypeFieldValParser = new OpenTypeFieldValParser();
    private OptionalExtensionMarkerParser optionalExtensionMarkerParser = new OptionalExtensionMarkerParser();
    private OptionalGroupParser optionalGroupParser = new OptionalGroupParser();
    private OptionalitySpecParser optionalitySpecParser = new OptionalitySpecParser();
    private ParamGovernorParser paramGovernorParser = new ParamGovernorParser();
    private ParameterListParser parameterListParser = new ParameterListParser();
    private ParameterParser parameterParser = new ParameterParser();
    private ParameterizedAssignmentParser parameterizedAssignmentParser = new ParameterizedAssignmentParser();
    private ParameterizedObjectClassParser parameterizedObjectClassParser = new ParameterizedObjectClassParser();
    private ParameterizedObjectParser parameterizedObjectParser = new ParameterizedObjectParser();
    private ParameterizedObjectSetAssignmentParser parameterizedObjectSetAssignmentParser = new ParameterizedObjectSetAssignmentParser();
    private ParameterizedObjectSetParser parameterizedObjectSetParser = new ParameterizedObjectSetParser();
    private ParameterizedTypeAssignmentParser parameterizedTypeAssignmentParser = new ParameterizedTypeAssignmentParser();
    private ParameterizedTypeParser parameterizedTypeParser = new ParameterizedTypeParser();
    private ParameterizedValueAssignmentParser parameterizedValueAssignmentParser = new ParameterizedValueAssignmentParser();
    private ParameterizedValueParser parameterizedValueParser = new ParameterizedValueParser();
    private ParameterizedValueSetTypeAssignmentParser parameterizedValueSetTypeAssignmentParser = new ParameterizedValueSetTypeAssignmentParser();
    private PartialSpecificationParser partialSpecificationParser = new PartialSpecificationParser();
    private PatternConstraintParser patternConstraintParser = new PatternConstraintParser();
    private PermittedAlphabetParser permittedAlphabetParser = new PermittedAlphabetParser();
    private PrefixedTypeParser prefixedTypeParser = new PrefixedTypeParser();
    private PresenceConstraintParser presenceConstraintParser = new PresenceConstraintParser();
    private PrimitiveFieldNameParser primitiveFieldNameParser = new PrimitiveFieldNameParser();
    private PrintableStringParser printableStringParser = new PrintableStringParser();
    private PropertyAndSettingPairParser propertyAndSettingPairParser = new PropertyAndSettingPairParser();
    private PropertySettingsParser propertySettingsParser = new PropertySettingsParser();
    private RealValueParser realValueParser = new RealValueParser();
    private RecurrenceRangeParser recurrenceRangeParser = new RecurrenceRangeParser();
    private ReferenceParser referenceParser = new ReferenceParser();
    private ReferencedObjectsParser referencedObjectsParser = new ReferencedObjectsParser();
    private ReferencedTypeParser referencedTypeParser = new ReferencedTypeParser();
    private RelativeIRIValueParser relativeIRIValueParser = new RelativeIRIValueParser();
    private RelativeOIDComponentsListParser relativeOIDComponentsListParser = new RelativeOIDComponentsListParser();
    private RelativeOIDComponentsParser relativeOIDComponentsParser = new RelativeOIDComponentsParser();
    private RelativeOIDValueParser relativeOIDValueParser = new RelativeOIDValueParser();
    private RequiredTokenParser requiredTokenParser = new RequiredTokenParser();
    private RestrictedCharacterStringTypeParser restrictedCharacterStringTypeParser = new RestrictedCharacterStringTypeParser();
    private RestrictedCharacterStringValueParser restrictedCharacterStringValueParser = new RestrictedCharacterStringValueParser();
    private RootAlternativeTypeListParser rootAlternativeTypeListParser = new RootAlternativeTypeListParser();
    private SelectionTypeParser selectionTypeParser = new SelectionTypeParser();
    private SetFieldSpecParser setFieldSpecParser = new SetFieldSpecParser();
    private SetOptionalitySpecParser setOptionalitySpecParser = new SetOptionalitySpecParser();
    private SetParser setParser = new SetParser();
    private SetSpecsParser setSpecsParser = new SetSpecsParser();
    private SettingParser settingParser = new SettingParser();
    private SignedNumberParser signedNumberParser = new SignedNumberParser();
    private SimpleDefinedTypeParser simpleDefinedTypeParser = new SimpleDefinedTypeParser();
    private SimpleDefinedValueParser simpleDefinedValueParser = new SimpleDefinedValueParser();
    private SimpleTableConstraintParser simpleTableConstraintParser = new SimpleTableConstraintParser();
    private SingleValueParser singleValueParser = new SingleValueParser();
    private SizeConstraintParser sizeConstraintParser = new SizeConstraintParser();
    private SpecialRealValueParser specialRealValueParser = new SpecialRealValueParser();
    private SubtypeConstraintParser subtypeConstraintParser = new SubtypeConstraintParser();
    private SubtypeElementsParser subtypeElementsParser = new SubtypeElementsParser();
    private SymbolListParser symbolListParser = new SymbolListParser();
    private SymbolParser symbolParser = new SymbolParser();
    private SymbolsFromModuleListParser symbolsFromModuleListParser = new SymbolsFromModuleListParser();
    private SymbolsFromModuleParser symbolsFromModuleParser = new SymbolsFromModuleParser();
    private SymbolsImportedParser symbolsImportedParser = new SymbolsImportedParser();
    private SyntaxListParser syntaxListParser = new SyntaxListParser();
    private T61StringParser t61StringParser = new T61StringParser();
    private TableConstraintParser tableConstraintParser = new TableConstraintParser();
    private TagDefaultParser tagDefaultParser = new TagDefaultParser();
    private TagParser tagParser = new TagParser();
    private TaggedTypeParser taggedTypeParser = new TaggedTypeParser();
    private TeletexStringParser teletexStringParser = new TeletexStringParser();
    private TimePointRangeParser timePointRangeParser = new TimePointRangeParser();
    private TokenOrGroupSpecParser tokenOrGroupSpecParser = new TokenOrGroupSpecParser();
    private TypeAssignmentParser typeAssignmentParser = new TypeAssignmentParser();
    private TypeConstraintParser typeConstraintParser = new TypeConstraintParser();
    private TypeConstraintsParser typeConstraintsParser = new TypeConstraintsParser();
    private TypeFieldSpecParser typeFieldSpecParser = new TypeFieldSpecParser();
    private TypeFromObjectsParser typeFromObjectsParser = new TypeFromObjectsParser();
    private TypeOptionalitySpecParser typeOptionalitySpecParser = new TypeOptionalitySpecParser();
    private TypeOrNamedTypeParser typeOrNamedTypeParser = new TypeOrNamedTypeParser();
    private TypeParser typeParser = new TypeParser();
    private TypeReferenceParser typeReferenceParser = new TypeReferenceParser();
    private TypeWithConstraintParser typeWithConstraintParser = new TypeWithConstraintParser();
    private UTF8StringParser utf8StringParser = new UTF8StringParser();
    private UnionsParser unionsParser = new UnionsParser();
    private UniversalStringParser universalStringParser = new UniversalStringParser();
    private UnrestrictedCharacterStringTypeParser unrestrictedCharacterStringTypeParser = new UnrestrictedCharacterStringTypeParser();
    private UnrestrictedCharacterStringValue unrestrictedCharacterStringValue = new UnrestrictedCharacterStringValue();
    private UpperEndValueParser upperEndValueParser = new UpperEndValueParser();
    private UpperEndpointParser upperEndpointParser = new UpperEndpointParser();
    private UsefulObjectClassReferenceParser usefulObjectClassReferenceParser = new UsefulObjectClassReferenceParser();
    private UsefulTypeParser usefulTypeParser = new UsefulTypeParser();
    private UserDefinedConstraintParameterParser userDefinedConstraintParameterParser = new UserDefinedConstraintParameterParser();
    private UserDefinedConstraintParser userDefinedConstraintParser = new UserDefinedConstraintParser();
    private ValueAssignmentParser valueAssignmentParser = new ValueAssignmentParser();
    private ValueConstraintParser valueConstraintParser = new ValueConstraintParser();
    private ValueFromObjectParser valueFromObjectParser = new ValueFromObjectParser();
    private ValueListParser valueListParser = new ValueListParser();
    private ValueOptionalitySpecParser valueOptionalitySpecParser = new ValueOptionalitySpecParser();
    private ValueParser valueParser = new ValueParser();
    private ValueRangeParser valueRangeParser = new ValueRangeParser();
    private ValueReferenceParser valueReferenceParser = new ValueReferenceParser();
    private ValueSetOptionalitySpecParser valueSetOptionalitySpecParser = new ValueSetOptionalitySpecParser();
    private ValueSetParser valueSetParser = new ValueSetParser();
    private ValueSetTypeAssignmentParser valueSetTypeAssignmentParser = new ValueSetTypeAssignmentParser();
    private VariableTypeValueFieldSpecParser variableTypeValueFieldSpecParser = new VariableTypeValueFieldSpecParser();
    private VariableTypeValueSetFieldSpecParser variableTypeValueSetFieldSpecParser = new VariableTypeValueSetFieldSpecParser();
    private VersionNumberParser versionNumberParser = new VersionNumberParser();
    private VideotexStringParser videotexStringParser = new VideotexStringParser();
    private VisibleStringParser visibleStringParser = new VisibleStringParser();
    private WithSyntaxSpecParser withSyntaxSpecParser = new WithSyntaxSpecParser();

    public Parser(InputStream is) throws IOException {
    	this.lexer = new Lexer(is);
    	lexerContext.push(Context.Normal);
    }

    private Token getToken() throws ParserException {
    	Token token = lexer.nextToken(lexerContext.peek());

    	if (!marks.isEmpty() && token != null) {
    		tokens.push(token);
    	}

    	return token;
    }

    private void setException(String message, Token token) {
    	lastException = new ParserException(String.format(
    			"Line %d, position %d: %s", token.line, token.pos, message));
    }

    private void clearError() {
    	lastErrorOffset = 0;
    	lastException = null;
    	lastExpectedToken = null;
    	lastExpectedToken = null;
    }

    private void pushBack() {
    	if (tokens.size() > 0) {
    		lexer.pushBack(tokens.pop());
    	}
    }

    private void mark() {
    	marks.push(tokens.size());
    }

    private void clearMark() {
    	marks.pop();

    	if (marks.isEmpty()) {
    		tokens.clear();
    	}
    }

    private int getMark() {
        return marks.peek();
    }

    private void resetToMark(int mark) {
        while (tokens.size() > mark) {
            lexer.pushBack(tokens.pop());
        }
    }

    private void resetToMark() {
    	resetToMark(marks.pop());
    }

    private Token expect(TokenType type) throws ParserException {
    	Token token;

    	try {
    		token = getToken();
    	} catch (ParserException e) {
    		if (lexer.getOffset() > lastErrorOffset) {
    			lastErrorOffset = lexer.getOffset();
    			lastErrorToken = null;
    			lastExpectedToken = type;
    			lastException = e;
    		}

    		return null;
    	}

    	if (token != null && token.getType() != type) {
    		if (token.getOffset() > lastErrorOffset) {
    			lastErrorOffset = token.getOffset();
    			lastErrorToken = token;
    			lastExpectedToken = type;
    			lastException = null;
    		}

    		pushBack();
    		return null;
    	}

    	return token;
    }

    public ModuleNode parse() throws ParserException {
    	return new ModuleDefinitionParser().parse();
    }

    private interface RuleParser<T> {
    	T parse() throws ParserException;
    }

    protected class AmbiguousChoiceParser<T> {

        private RuleParser<? extends T>[] choices;

        AmbiguousChoiceParser(RuleParser<? extends T>... objects) {
            this.choices = objects;
        }

        public Set<T> parse() throws ParserException {
            RuleParser<? extends T> matching = null;
            int longestMatch = 0;
            Map<Integer, Set<T>> result = new HashMap<>();

            for (RuleParser<? extends T> o : choices) {
                mark();

                try {
                    T choice = o.parse();
                    int matchLen = tokens.size();

                    if (choice != null) {
                        if (matchLen > longestMatch) {
                            matching = o;
                            longestMatch = matchLen;
                        }

                        result.computeIfAbsent(matchLen, (key) -> new HashSet<>()).add(choice);
                    }

                    resetToMark();
                } catch (ParserException e) {
                    resetToMark();
                }
            }

            if (matching != null) {
                matching.parse();
                return result.get(longestMatch);
            }

            return Collections.emptySet();
        }

    }

    protected class ChoiceParser<T> implements RuleParser<T> {

    	private RuleParser<? extends T>[] choices;

    	private TokenType[] types;

    	ChoiceParser(RuleParser<? extends T>... objects) {
    		this.choices = objects;
    	}

    	public ChoiceParser(TokenType... types) {
    		this.types = types;
    	}

    	public T parse() throws ParserException {
    		if (choices != null) {
    			for (RuleParser<? extends T> o : choices) {
    				mark();

    				try {
    					T choice = o.parse();

    					if (choice != null) {
    						clearMark();
                            return choice;
    					} else {
    						resetToMark();
    					}
    				} catch (ParserException e) {
    					resetToMark();
    				}
    			}
    		} else {
    			for (TokenType type : types) {
    				mark();

    				try {
    					@SuppressWarnings("unchecked")
    					T token = (T) expect(type);

    					if (token != null) {
    						clearMark();
    						return token;
    					} else {
    						resetToMark();
    					}
    				} catch (ParserException e) {
    					resetToMark();
    				}
    			}
    		}

    		return null;
    	}
    }

    protected class SingleTokenParser implements RuleParser<Token> {

    	private TokenType type;

    	private Context context;

    	SingleTokenParser(TokenType type) {
    		this.type = type;
    	}

    	SingleTokenParser(TokenType type, Context context) {
    		this.type = type;
    		this.context = context;
    	}

    	public Token parse() throws ParserException {

    		if (context != null) {
    			lexerContext.push(context);
    		}

    		try {
    			Token token = expect(type);

    			if (token == null) {
    				return null;
    			}

    			return token;
    		} finally {
    			if (context != null) {
    				lexerContext.pop();
    			}
    		}
    	}

    }

    protected class NegativeLookaheadParser implements RuleParser<Object> {

    	private TokenType[] types;

    	NegativeLookaheadParser(TokenType... types) {
    		this.types = types;
    	}

    	public Object parse() throws ParserException {
    		Token token = getToken();

    		try {
    			if (token != null) {
    				TokenType foundType = token.getType();

    				for (TokenType type : types) {
    					if (type == foundType) {
    						return null;
    					}
    				}
    			}

    			return new Object();
    		} finally {
    			pushBack();
    		}
    	}

    }

    protected class RepetitionParser<T> implements RuleParser<List<T>> {

    	private Object object;

    	RepetitionParser(RuleParser<T> object) {
    		this.object = object;
    	}

    	public List<T> parse() throws ParserException {
    		List<T> result = new ArrayList<T>();

    		if (object instanceof RuleParser) {
    			while (true) {
    				@SuppressWarnings("unchecked")
    				T rule = ((RuleParser<T>) object).parse();
    				if (rule != null) {
    					result.add(rule);
    				} else {
    					break;
    				}
    			}
    		}

    		if (result.size() == 0) {
    			return null;
    		}

    		return result;
    	}

    }

    protected class SequenceParser implements RuleParser<List<Object>> {

    	private Object[] sequence;

    	private boolean[] mandatoryRules;

    	SequenceParser(Object... objects) {
    		this.sequence = objects;
    		mandatoryRules = new boolean[sequence.length];

    		for (int i = 0; i < mandatoryRules.length; i++) {
    			mandatoryRules[i] = true;
    		}
    	}

    	SequenceParser(boolean mandatoryRules[], Object... objects) {
    		if (mandatoryRules.length != objects.length) {
    			throw new IllegalArgumentException();
    		}

    		this.mandatoryRules = mandatoryRules;
    		this.sequence = objects;
    	}

    	public List<Object> parse() throws ParserException {
    		ArrayList<Object> result = new ArrayList<>();
    		mark();

    		for (int i = 0; i < sequence.length; i++) {
    			Object o = sequence[i];
    			boolean mandatory = mandatoryRules[i];

    			if (o instanceof TokenType) {
    				Token token = expect((TokenType) o);

    				if (mandatory && token == null) {
    					resetToMark();
    					return null;
    				}

    				result.add(token);
    			} else if (o instanceof RuleParser) {
    				Object rule = ((RuleParser<?>) o).parse();

    				if (mandatory && rule == null) {
    					resetToMark();
    					return null;
    				}

    				result.add(rule);
    			} else {
    				throw new ParserException(
    						"Invalid parameter to SequenceParser");
    			}
    		}

    		clearMark();

    		return result;
    	}

    }

    protected class ValueExtractor<T> implements RuleParser<T> {

    	private int index;

    	private RuleParser<List<Object>> parser;

    	public ValueExtractor(int index, RuleParser<List<Object>> parser) {
    		this.index = index;
    		this.parser = parser;
    	}

    	@SuppressWarnings("unchecked")
    	public T parse() throws ParserException {
    		List<Object> objs = parser.parse();

    		if (objs != null) {
    			return (T) objs.get(index);
    		}

    		return null;
    	}

    }

    protected class TokenSeparatedRuleParser<T> implements RuleParser<List<T>> {

    	private RuleParser<? extends T> parser;

    	private Token.TokenType[] types;

    	public TokenSeparatedRuleParser(RuleParser<? extends T> parser,
    			Token.TokenType... types) {
    		this.parser = parser;
    		this.types = types;
    	}

    	public List<T> parse() throws ParserException {
    		List<T> list = new ArrayList<T>();
    		T obj = parser.parse();

    		if (obj == null) {
    			return null;
    		}

    		list.add(obj);

    		List<T> moreObjs = new RepetitionParser<T>(new ValueExtractor<T>(1,
    				new SequenceParser(new ChoiceParser<Token>(types), parser))).parse();

    		if (moreObjs != null) {
    			list.addAll(moreObjs);
    		}

    		return list;
    	}

    }

    protected class CommaSeparatedRuleParser<T> extends
    		TokenSeparatedRuleParser<T> {

    	public CommaSeparatedRuleParser(RuleParser<T> parser) {
    		super(parser, TokenType.Comma);
    	}

    }

    // ModuleDefinition ::=
    // ModuleIdentifier
    // DEFINITIONS
    // EncodingReferenceDefault
    // TagDefault
    // ExtensionDefault
    // "::="
    // BEGIN
    // ModuleBody
    // EncodingControlSections
    // END
    protected class ModuleDefinitionParser implements RuleParser<ModuleNode> {

    	public ModuleNode parse() throws ParserException {
    		List<Object> rule = new SequenceParser(new boolean[] { true, true,
    				false, false, false, true, true }, moduleIdentifierParser,
    				TokenType.DEFINITIONS_KW, encodingReferenceDefaultParser,
    				tagDefaultParser, extensionDefaultParser, TokenType.Assign,
    				TokenType.BEGIN_KW).parse();
    		ModuleBodyNode body = moduleBodyParser.parse();
    		List<EncodingControlSectionNode> encCtrl = encodingControlSectionsParser
    				.parse();
    		Token end = expect(TokenType.END_KW);

    		if (rule != null && end != null) {
    			return new ModuleNode((ModuleIdentifierNode) rule.get(0),
    					(ModuleNode.Encoding) rule.get(2),
    					(ModuleNode.TagMode) rule.get(3),
    					(Boolean) rule.get(4), body, encCtrl);
    		} else {
    			if (lastException == null) {
    				throw new ParserException(StringUtils.concat("Token '",
    						lastExpectedToken, "' expected, but found '",
    						lastErrorToken.getType(),
    						(lastErrorToken.getText() != null ? "("
    								+ lastErrorToken.getText() + ")" : ""),
    						"' at line ", lastErrorToken.getLine(),
    						" position ", lastErrorToken.getPos()));
    			} else {
    				throw lastException;
    			}
    		}

    	}

    }

    // EncodingControlSections ::=
    // EncodingControlSection EncodingControlSections
    // |empty
    protected class EncodingControlSectionsParser implements
    		RuleParser<List<EncodingControlSectionNode>> {

    	public List<EncodingControlSectionNode> parse() throws ParserException {
    		return new RepetitionParser<EncodingControlSectionNode>(
    				encodingControlSectionParser).parse();
    	}

    }

    // EncodingControlSection ::=
    // ENCODING-CONTROL
    // encodingreference
    // EncodingInstructionAssignmentList
    protected class EncodingControlSectionParser implements
    		RuleParser<EncodingControlSectionNode> {

    	public EncodingControlSectionNode parse() throws ParserException {

    		List<Object> rule = new SequenceParser(
    				TokenType.ENCODING_CONTROL_KW, new SingleTokenParser(
    						TokenType.EncodingReference, Context.Encoding))
    				.parse();

    		List<Token> encodingInstruction = new ArrayList<Token>();

    		if (rule != null) {
    			while (true) {
    				mark();

    				Token token = getToken();

    				if (token == null
    						|| token.getType() == Token.TokenType.ENCODING_CONTROL_KW
    						|| token.getType() == TokenType.END_KW) {
    					pushBack();
    					break;
    				}

    				clearMark();
    				encodingInstruction.add(token);
    			}

    			return new EncodingControlSectionNode(
    					((Token) rule.get(1)).getText(), encodingInstruction);
    		}

    		return null;
    	}

    }

    // ModuleIdentifier ::=
    // modulereference
    // DefinitiveIdentification
    protected class ModuleIdentifierParser implements
    		RuleParser<ModuleIdentifierNode> {

    	public ModuleIdentifierNode parse() throws ParserException {
    		List<Object> rule = new SequenceParser(
    				new boolean[] { true, false }, TokenType.TypeReference,
    				definitiveIdentificationParser).parse();

    		if (rule != null) {
    			return new ModuleIdentifierNode(
    					((Token) rule.get(0)).getText(),
    					(DefinitiveIdentificationNode) rule.get(1));
    		}

    		return null;
    	}

    }

    // EncodingReferenceDefault ::=
    // encodingreference INSTRUCTIONS
    // | empty
    protected class EncodingReferenceDefaultParser implements
    		RuleParser<ModuleNode.Encoding> {

    	public Encoding parse() throws ParserException {
    		lexerContext.push(Context.Encoding);

    		try {
    			List<Object> rule = new SequenceParser(
    					TokenType.EncodingReference, TokenType.INSTRUCTIONS_KW)
    					.parse();

    			if (rule != null) {
    				return ModuleNode.getEncoding(((Token) rule.get(0))
    						.getText());
    			}

    			return Encoding.TAG;

    		} finally {
    			lexerContext.pop();
    		}
    	}

    }

    // TagDefault ::=
    // EXPLICIT TAGS
    // | IMPLICIT TAGS
    // | AUTOMATIC TAGS
    // | empty
    protected class TagDefaultParser implements RuleParser<ModuleNode.TagMode> {

    	@SuppressWarnings("unchecked")
    	public ModuleNode.TagMode parse() throws ParserException {
    		List<Object> rule = new ChoiceParser<List<Object>>(
    				new SequenceParser(new ChoiceParser<Token>(
    						TokenType.EXPLICIT_KW, TokenType.IMPLICIT_KW,
    						TokenType.AUTOMATIC_KW), TokenType.TAGS_KW))
    				.parse();

    		if (rule != null) {
    			switch (((Token) rule.get(0)).getType()) {
    			case EXPLICIT_KW:
    				return ModuleNode.TagMode.Explicit;
    			case IMPLICIT_KW:
    				return ModuleNode.TagMode.Implicit;
    			case AUTOMATIC_KW:
    				return ModuleNode.TagMode.Automatic;
    			}
    		}

    		return ModuleNode.TagMode.Explicit;
    	}

    }

    // ExtensionDefault ::=
    // EXTENSIBILITY IMPLIED
    // | empty
    protected class ExtensionDefaultParser implements RuleParser<Boolean> {

    	public Boolean parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.EXTENSIBILITY_KW,
    				TokenType.IMPLIED_KW).parse();

    		if (rule != null) {
    			return true;
    		}

    		return false;
    	}

    }

    // DefinitiveIdentification ::=
    // | DefinitiveOID
    // | DefinitiveOIDandIRI
    // | empty
    // DefinitiveOIDandIRI ::=
    // DefinitiveOID
    // IRIValue
    protected class DefinitiveIdentificationParser implements
    		RuleParser<DefinitiveIdentificationNode> {

    	public DefinitiveIdentificationNode parse() throws ParserException {
    		List<Object> rule = (List<Object>) new SequenceParser(
    				new boolean[] { true, false }, definitiveOIDParser,
    				iriValueParser).parse();

    		if (rule != null) {
    			return new DefinitiveIdentificationNode((OIDNode) rule.get(0),
    					(IRIValue) rule.get(1));
    		}

    		return null;
    	}

    }

    // DefinitiveNameAndNumberForm ::= identifier "(" DefinitiveNumberForm ")"
    protected class DefinitiveNameAndNumberFormParser implements
    		RuleParser<OIDComponentNode> {

    	public OIDComponentNode parse() throws ParserException {
    		List<Object> rule = (List<Object>) new SequenceParser(
    				TokenType.Identifier, TokenType.LParen,
    				definitiveNumberFormParser, TokenType.RParen).parse();

    		if (rule != null) {
    			OIDComponentNode oid = (OIDComponentNode) (rule.get(2));
    			oid.setName(((Token) rule.get(0)).getText());
    			return oid;
    		}

    		return null;
    	}

    }

    // DefinitiveNumberForm ::= number
    protected class DefinitiveNumberFormParser implements
    		RuleParser<OIDComponentNode> {

    	public OIDComponentNode parse() throws ParserException {
    		List<Object> rule = (List<Object>) new SequenceParser(
    				TokenType.Number).parse();

    		if (rule != null) {
    			return new OIDComponentNode(Integer.parseInt(((Token) rule
    					.get(0)).getText()));
    		}

    		return null;
    	}

    }

    // NameForm ::= identifier
    protected class NameFormParser implements RuleParser<OIDComponentNode> {

    	public OIDComponentNode parse() throws ParserException {
    		List<Object> rule = (List<Object>) new SequenceParser(
    				TokenType.Identifier).parse();

    		if (rule != null) {
    			return new OIDComponentNode(((Token) rule.get(0)).getText());
    		}

    		return null;
    	}

    }

    // DefinitiveObjIdComponent ::=
    // NameForm
    // | DefinitiveNumberForm
    // | DefinitiveNameAndNumberForm
    protected class DefinitiveObjIdComponentParser implements
    		RuleParser<OIDComponentNode> {

    	@SuppressWarnings("unchecked")
    	public OIDComponentNode parse() throws ParserException {
    		return new ChoiceParser<OIDComponentNode>(
    				definitiveNumberFormParser,
    				definitiveNameAndNumberFormParser, nameFormParser).parse();
    	}

    }

    // DefinitiveObjIdComponentList ::=
    // DefinitiveObjIdComponent
    // | DefinitiveObjIdComponent DefinitiveObjIdComponentList
    protected class DefinitiveObjIdComponentListParser implements
    		RuleParser<OIDNode> {

    	public OIDNode parse() throws ParserException {
    		List<OIDComponentNode> rule = new RepetitionParser<OIDComponentNode>(
    				definitiveObjIdComponentParser).parse();

    		return new OIDNode(rule);
    	}

    }

    // DefinitiveOID ::=
    // "{" DefinitiveObjIdComponentList "}"
    protected class DefinitiveOIDParser implements RuleParser<OIDNode> {

    	public OIDNode parse() throws ParserException {
    		List<Object> rule = (List<Object>) new SequenceParser(
    				TokenType.LBrace, definitiveObjIdComponentListParser,
    				TokenType.RBrace).parse();

    		if (rule != null) {
    			return (OIDNode) rule.get(1);
    		}

    		return null;
    	}

    }

    // ModuleBody ::= Exports Imports AssignmentList | empty
    protected class ModuleBodyParser implements RuleParser<ModuleBodyNode> {

    	@SuppressWarnings("unchecked")
    	public ModuleBodyNode parse() throws ParserException {
    		List<Object> rule = (List<Object>) new SequenceParser(
    				new boolean[] { true, false }, exportsParser, importsParser)
    				.parse();

    		if (rule != null) {
    			clearError();

    			ExportsNode exports = (ExportsNode) rule.get(0);
    			List<ImportNode> imports = (List<ImportNode>) rule.get(1);
    			List<AssignmentNode> assignments = assignmentListParser.parse();

    			return new ModuleBodyNode(exports, imports, assignments);
    		}

    		return null;
    	}

    }

    // EXPORTS SymbolsExported ";"
    // | EXPORTS ALL ";"
    // | empty
    // SymbolsExported ::=
    // SymbolList | empty
    protected class ExportsParser implements RuleParser<ExportsNode> {

    	@SuppressWarnings("unchecked")
    	public ExportsNode parse() throws ParserException {
    		List<Object> rule = new ChoiceParser<List<Object>>(
    				new SequenceParser(TokenType.EXPORTS_KW, TokenType.ALL_KW,
    						TokenType.Semicolon), new SequenceParser(
    						TokenType.EXPORTS_KW, symbolListParser,
    						TokenType.Semicolon)).parse();

    		if (rule != null) {
    			Object maybeList = rule.get(1);
    			if (maybeList instanceof List) {
    				return new ExportsNode(ExportsNode.Mode.Specific,
    						(List<ReferenceNode>) maybeList);
    			}
    		}

    		return new ExportsNode(ExportsNode.Mode.All);
    	}

    }

    // SymbolList ::=
    // Symbol
    // | SymbolList "," Symbol
    protected class SymbolListParser implements RuleParser<List<ReferenceNode>> {

    	public List<ReferenceNode> parse() throws ParserException {
    		return new CommaSeparatedRuleParser<ReferenceNode>(symbolParser)
    				.parse();
    	}

    }

    // Symbol ::=
    // Reference
    // | ParameterizedReference
    // ParameterizedReference ::=
    // Reference | Reference "{" "}"
    protected class SymbolParser implements RuleParser<ReferenceNode> {

    	@SuppressWarnings("unchecked")
    	public ReferenceNode parse() throws ParserException {
    		Object rule = new ChoiceParser<Object>(new SequenceParser(
    				referenceParser, TokenType.LBrace, TokenType.RBrace),
    				referenceParser).parse();

    		if (rule != null) {
    			if (rule instanceof List) {
    				ReferenceNode reference = (ReferenceNode) ((List<Object>) rule)
    						.get(0);
    				reference.setIsParameterized();
    				return reference;
    			} else {
    				return (ReferenceNode) rule;
    			}
    		}

    		return null;
    	}

    }

    // Reference ::=
    // typereference
    // | valuereference
    // | objectclassreference
    // | objectreference
    // | objectsetreference
    protected class ReferenceParser implements RuleParser<ReferenceNode> {

    	public ReferenceNode parse() throws ParserException {
    		Token rule = new ChoiceParser<Token>(TokenType.TypeReference,
    				TokenType.Identifier).parse();

    		if (rule != null) {
    			return new ReferenceNode(rule.getText());
    		}

    		return null;
    	}

    }

    // Imports ::=
    // IMPORTS SymbolsImported ";"
    // | empty
    protected class ImportsParser implements RuleParser<List<ImportNode>> {

    	@SuppressWarnings("unchecked")
    	public List<ImportNode> parse() throws ParserException {
    		List<Object> rule = new SequenceParser(new boolean[] { true, false,
    				true }, TokenType.IMPORTS_KW, symbolsImportedParser,
    				TokenType.Semicolon).parse();

    		if (rule != null) {
    			if (rule.get(1) == null) {
    				return new ArrayList<ImportNode>();
    			}

    			return (List<ImportNode>) rule.get(1);
    		}

    		return null;

    	}
    }

    // SymbolsImported ::=
    // SymbolsFromModuleList
    // | empty
    protected class SymbolsImportedParser implements
    		RuleParser<List<ImportNode>> {

    	public List<ImportNode> parse() throws ParserException {

    		return symbolsFromModuleListParser.parse();

    	}

    }

    // SymbolsFromModuleList ::=
    // SymbolsFromModule
    // | SymbolsFromModuleList SymbolsFromModule
    protected class SymbolsFromModuleListParser implements
    		RuleParser<List<ImportNode>> {

    	public List<ImportNode> parse() throws ParserException {
    		return new RepetitionParser<ImportNode>(symbolsFromModuleParser)
    				.parse();
    	}

    }

    // SymbolsFromModule ::=
    // SymbolList FROM GlobalModuleReference
    protected class SymbolsFromModuleParser implements RuleParser<ImportNode> {

    	@SuppressWarnings("unchecked")
    	public ImportNode parse() throws ParserException {
    		List<Object> rule = new SequenceParser(symbolListParser,
    				TokenType.FROM_KW, globalModuleReferenceParser).parse();

    		if (rule != null) {
    			return new ImportNode((List<ReferenceNode>) rule.get(0),
    					(ModuleRefNode) rule.get(2));
    		}

    		return null;
    	}

    }

    // GlobalModuleReference ::=
    // modulereference AssignedIdentifier
    protected class GlobalModuleReferenceParser implements
    		RuleParser<ModuleRefNode> {

    	public ModuleRefNode parse() throws ParserException {
    		List<Object> rule = new SequenceParser(
    				new boolean[] { true, false }, TokenType.TypeReference,
    				assignedIdentifierParser).parse();

    		if (rule != null) {
    			if (rule.size() == 1) {
    				return new ModuleRefNode(((Token) rule.get(0)).getText());
    			} else {
    				return new ModuleRefNode(((Token) rule.get(0)).getText(),
    						(ObjectIdentifierValue) rule.get(1));
    			}
    		}

    		return null;
    	}

    }

    // AssignedIdentifier ::=
    // ObjectIdentifierValue
    // | DefinedValue
    // | empty
    protected class AssignedIdentifierParser implements
    		RuleParser<ObjectIdentifierValue> {

    	@SuppressWarnings("unchecked")
    	public ObjectIdentifierValue parse() throws ParserException {
    		Node rule = new ChoiceParser<Node>(objectIdentifierValueParser,
    				new ValueExtractor<Node>(0, new SequenceParser(
    						definedValueParser, new NegativeLookaheadParser(
    								TokenType.Comma, TokenType.FROM_KW))))
    				.parse();

    		if (rule != null) {
    			if (rule instanceof ObjectIdentifierValue) {
    				return (ObjectIdentifierValue) rule;
    			} else {
    				return new ObjectIdentifierValue((DefinedValue) rule);
    			}
    		}

    		return null;
    	}

    }

    // ObjectIdentifierValue ::=
    // "{" ObjIdComponentsList "}"
    // | "{" DefinedValue ObjIdComponentsList "}"
    protected class ObjectIdentifierValueParser implements
    		RuleParser<ObjectIdentifierValue> {

    	public ObjectIdentifierValue parse() throws ParserException {
    		List<OIDComponentNode> rule = new ValueExtractor<List<OIDComponentNode>>(
    				1, new SequenceParser(TokenType.LBrace, objIdComponentsListParser, TokenType.RBrace))
    				.parse();

    		if (rule != null) {
    			return new ObjectIdentifierValue(rule);
    		}

    		return null;
    	}

    }

    // ObjIdComponentsList ::=
    // ObjIdComponents
    // | ObjIdComponents ObjIdComponentsList
    protected class ObjIdComponentsListParser implements RuleParser<List<OIDComponentNode>> {

    	public List<OIDComponentNode> parse() throws ParserException {
    		return new RepetitionParser<>(objIdComponentsParser).parse();
    	}

    }

    // ObjIdComponents ::=
    // NameForm
    // | NumberForm
    // | NameAndNumberForm
    // | DefinedValue
    protected class ObjIdComponentsParser implements RuleParser<OIDComponentNode> {

    	@SuppressWarnings("unchecked")
    	public OIDComponentNode parse() throws ParserException {
    		return new ChoiceParser<>(nameAndNumberFormParser, nameFormParser, numberFormParser).parse();
    	}

    }

    // NumberForm ::= number | DefinedValue
    protected class NumberFormParser implements RuleParser<OIDComponentNode> {

    	@SuppressWarnings("unchecked")
    	public OIDComponentNode parse() throws ParserException {
    		Object rule = (Object) new ChoiceParser<Object>(
    				new SingleTokenParser(TokenType.Number), definedValueParser)
    				.parse();

    		if (rule != null) {
    			if (rule instanceof Token) {
    				return new OIDComponentNode(Integer.parseInt(((Token) rule)
    						.getText()));
    			} else {
    				return new OIDComponentNode((DefinedValue) rule);
    			}
    		}

    		return null;
    	}

    }

    // NameAndNumberForm ::=
    // identifier "(" NumberForm ")"
    protected class NameAndNumberFormParser implements
    		RuleParser<OIDComponentNode> {

    	public OIDComponentNode parse() throws ParserException {
    		List<Object> rule = (List<Object>) new SequenceParser(
    				TokenType.Identifier, TokenType.LParen, numberFormParser,
    				TokenType.RParen).parse();

    		if (rule != null) {
    			OIDComponentNode oid = (OIDComponentNode) (rule.get(2));
    			oid.setName(((Token) rule.get(0)).getText());
    			return oid;
    		}

    		return null;
    	}

    }

    // DefinedValue ::=
    // ExternalValueReference
    // | valuereference
    // | ParameterizedValue
    protected class DefinedValueParser implements RuleParser<DefinedValue> {

    	@SuppressWarnings("unchecked")
    	public DefinedValue parse() throws ParserException {
    		return new ChoiceParser<DefinedValue>(parameterizedValueParser,
    				externalValueReferenceParser, valueReferenceParser).parse();
    	}

    }

    // valuereference
    protected class ValueReferenceParser implements RuleParser<DefinedValue> {

    	public DefinedValue parse() throws ParserException {
    		Token value = new SingleTokenParser(TokenType.Identifier).parse();

    		if (value != null) {
    			return new SimpleDefinedValue(value.getText());
    		}

    		return null;
    	}

    }

    // ExternalValueReference ::=
    // modulereference
    // "."
    // valuereference
    protected class ExternalValueReferenceParser implements
    		RuleParser<SimpleDefinedValue> {

    	public SimpleDefinedValue parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.TypeReference,
    				TokenType.Dot, TokenType.Identifier).parse();
    		if (rule != null) {
    			return new ExternalValueReference(
    					((Token) rule.get(0)).getText(),
    					((Token) rule.get(2)).getText());
    		}

    		return null;
    	}

    }

    // AssignmentList ::=
    // Assignment
    // | AssignmentList Assignment
    protected class AssignmentListParser implements	RuleParser<List<AssignmentNode>> {

    	public List<AssignmentNode> parse() throws ParserException {
    		return new RepetitionParser<AssignmentNode>(assignmentParser).parse();
    	}

    }

    // Assignment ::=
    // TypeAssignment
    // | ValueAssignment
    // | ValueSetTypeAssignment
    // | ObjectClassAssignment
    // | ObjectAssignment
    // | ObjectSetAssignment
    // | ParameterizedAssignment
    protected class AssignmentParser implements RuleParser<AssignmentNode> {

    	@SuppressWarnings("unchecked")
    	public AssignmentNode parse() throws ParserException {
    		return new ChoiceParser<>(typeAssignmentParser,	valueAssignmentParser, valueSetTypeAssignmentParser,
    				parameterizedAssignmentParser).parse();
    	}

    }

    // TypeAssignment ::= typereference "::=" Type
    // ObjectClassAssignment ::= objectclassreference "::=" ObjectClass
    protected class TypeAssignmentParser implements
    		RuleParser<TypeOrObjectClassAssignmentNode<?>> {

    	@SuppressWarnings("unchecked")
    	public TypeOrObjectClassAssignmentNode<?> parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.TypeReference, TokenType.Assign,
                    new ChoiceParser<>(typeParser, objectClassParser)).parse();

    		if (rule != null) {
    			String reference = ((Token) rule.get(0)).getText();
    			Node type = (Node) rule.get(2);

    			if (type instanceof Type) {
    			    return new TypeAssignmentNode(reference, (Type) type);
    			} else if (type instanceof ObjectClassNode) {
    				return new ObjectClassAssignmentNode(reference, (ObjectClassNode) type);
    			}
    		}

    		return null;
    	}

    }

    // Type ::= BuiltinType | ReferencedType | ConstrainedType
    // ConstrainedType ::=
    // Type Constraint
    // | TypeWithConstraint
    protected class TypeParser implements RuleParser<Type> {

    	@SuppressWarnings("unchecked")
    	public Type parse() throws ParserException {
    		Object rule = new ChoiceParser<Object>(typeWithConstraintParser,
    				new SequenceParser(new boolean[] { true, false },
    						new ChoiceParser<Object>(builtinTypeParser,
    								referencedTypeParser),
    						new RepetitionParser<Constraint>(constraintParser)))
    				.parse();

    		if (rule != null) {
    			if (rule instanceof Type) {
    				return (Type) rule;
    			} else if (rule instanceof List) {
    				List<Object> ruleList = (List<Object>) rule;
    				Type type = (Type) ruleList.get(0);

    				if (ruleList.get(1) != null) {
    					type.setConstraints((List<Constraint>) ruleList.get(1));
    				}

    				return type;
    			}
    		}

    		return null;
    	}

    }

    // NamedType ::= identifier Type
    protected class NamedTypeParser implements RuleParser<NamedType> {

    	public NamedType parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.Identifier,
    				typeParser).parse();

    		if (rule != null) {
    			return new NamedType(((Token) rule.get(0)).getText(),
    					(Type) rule.get(1));
    		}

    		return null;
    	}

    }

    protected class TypeOrNamedTypeParser implements RuleParser<Type> {

    	public Type parse() throws ParserException {
    		List<Object> rule = new SequenceParser(
    				new boolean[] { false, true }, TokenType.Identifier,
    				typeParser).parse();

    		if (rule != null) {
    			if (rule.get(0) != null) {
    				return new NamedType(((Token) rule.get(0)).getText(),
    						(Type) rule.get(1));
    			} else {
    				return (Type) rule.get(1);
    			}
    		}

    		return null;
    	}

    }

    // BuiltinType ::=
    // BitStringType
    // | BooleanType
    // | CharacterStringType
    // | ChoiceType
    // | DateType
    // | DateTimeType
    // | DurationType
    // | EmbeddedPDVType
    // | EnumeratedType
    // | ExternalType
    // | InstanceOfType
    // | IntegerType
    // | IRIType
    // | NullType
    // | ObjectClassFieldType
    // | ObjectIdentifierType
    // | OctetStringType
    // | RealType
    // | RelativeIRIType
    // | RelativeOIDType
    // | SequenceType
    // | SequenceOfType
    // | SetType
    // | SetOfType
    // | PrefixedType
    // | TimeType
    // | TimeOfDayType
    protected class BuiltinTypeParser implements RuleParser<Type> {

    	@SuppressWarnings("unchecked")
    	public Type parse() throws ParserException {
    		return new ChoiceParser<Type>(prefixedTypeParser,
    				builtinTypeParserAux, characterStringTypeParser,
    				objectClassFieldTypeParser).parse();
    	}

    }

    // BooleanType ::= BOOLEAN
    // DateType ::= DATE
    // Date ::= DATE-TIME
    // DurationType ::= DURATION
    // EmbeddedPDVType ::= EMBEDDED PDV
    // BitStringType ::=
    // BIT STRING
    // | BIT STRING "{" NamedBitList "}"
    // NullType ::= NULL
    // RealType ::= REAL
    // RelativeOIDType ::= RELATIVE-OID
    // ObjectIdentifierType ::= OBJECT IDENTIFIER
    // OctetStringType ::= OCTET STRING
    // ExternalType ::= EXTERNAL
    // IntegerType ::=
    // INTEGER
    // | INTEGER "{" NamedNumberList "}"
    // ChoiceType ::= CHOICE "{" AlternativeTypeLists "}"
    // EnumeratedType ::=
    // ENUMERATED "{" Enumerations "}"
    // IRIType ::= OID-IRI
    // RelativeIRIType ::= RELATIVE-OID-IRI
    // TimeType ::= TIME
    // TimeOfDayType ::= TIME-OF-DAY
    // InstanceOfType ::= INSTANCE OF DefinedObjectClass
    // SequenceOfType ::= SEQUENCE OF Type | SEQUENCE OF NamedType
    // SequenceType ::=
    // SEQUENCE "{" "}"
    // | SEQUENCE "{" ExtensionAndException OptionalExtensionMarker "}"
    // | SEQUENCE "{" ComponentTypeLists "}"
    // SetOfType ::=
    // SET OF Type
    // | SET OF NamedType
    // SetType ::=
    // SET "{" "}"
    // | SET "{" ExtensionAndException OptionalExtensionMarker "}"
    // | SET "{" ComponentTypeLists "}"
    protected class BuiltinTypeParserAux implements RuleParser<Type> {

    	@SuppressWarnings("unchecked")
    	public Type parse() throws ParserException {
    		mark();

    		Token token = getToken();

    		if (token == null) {
    			resetToMark();
    			return null;
    		}

    		Type type = null;

    		switch (token.getType()) {

    		case BOOLEAN_KW:
    			type = new BooleanType();
    			break;

    		case DATE_KW:
    			type = new DateType();
    			break;

    		case DATE_TIME_KW:
    			type = new DateTime();
    			break;

    		case DURATION_KW:
    			type = new Duration();
    			break;

    		case EMBEDDED_KW:
    			if (expect(TokenType.PDV_KW) != null) {
    				type = new EmbeddedPDV();
    			} else {
    				resetToMark();
    				return null;
    			}
    			break;

    		case BIT_KW:
    			List<Object> bitRule = new SequenceParser(new boolean[] { true,
    					false }, TokenType.STRING_KW,
    					new ValueExtractor<List<NamedBitNode>>(1,
    							new SequenceParser(TokenType.LBrace,
    									namedBitListParser, TokenType.RBrace)))
    					.parse();

    			if (bitRule != null) {
    				if (bitRule.get(1) != null) {
    					type = new BitString(
    							(List<NamedBitNode>) bitRule.get(1));
    				} else {
    					type = new BitString();
    				}
    			}
    			break;

    		case NULL_KW:
    			type = new Null();
    			break;

    		case REAL_KW:
    			type = new Real();
    			break;

    		case INTEGER_KW:
    			List<NamedNumber> intRule = new ValueExtractor<List<NamedNumber>>(
    					1, new SequenceParser(TokenType.LBrace,
    							namedNumberListParser, TokenType.RBrace))
    					.parse();

    			if (intRule != null) {
    				type = new IntegerType(intRule);
    			} else {
    				type = new IntegerType();
    			}

    			break;

    		case RELATIVE_OID_KW:
    			type = new RelativeOID();
    			break;

    		case OID_IRI_KW:
    			type = new IRI();
    			break;

    		case RELATIVE_OID_IRI_KW:
    			type = new RelativeIRI();
    			break;

    		case OBJECT_KW:
    			if (expect(TokenType.IDENTIFIER_KW) != null) {
    				type = new ObjectIdentifier();
    			} else {
    				resetToMark();
    				return null;
    			}
    			break;

    		case OCTET_KW:
    			if (expect(TokenType.STRING_KW) != null) {
    				type = new OctetString();
    			} else {
    				resetToMark();
    				return null;
    			}
    			break;

    		case EXTERNAL_KW:
    			type = new External();
    			break;

    		case CHOICE_KW:
    			List<Object> choiceRule = new SequenceParser(TokenType.LBrace,
    					alternativeTypeListsParser, TokenType.RBrace).parse();

    			if (choiceRule != null) {
    				type = new Choice((AlternativeTypeLists) choiceRule.get(1));
    			}
    			break;

    		case ENUMERATED_KW:
    			List<Object> enumRule = new SequenceParser(TokenType.LBrace,
    					enumerationsParser, TokenType.RBrace).parse();

    			if (enumRule != null) {
    				type = (EnumeratedType) enumRule.get(1);
    			}
    			break;

    		case TIME_KW:
    			type = new Time();
    			break;

    		case TIME_OF_DAY_KW:
    			type = new TimeOfDay();
    			break;

    		case INSTANCE_KW:
    			List<Object> instRule = new SequenceParser(TokenType.OF_KW,
    					definedObjectClassParser).parse();

    			if (instRule != null) {
    				type = new InstanceOfType(
    						(ObjectClassReferenceNode) instRule.get(1));
    			}
    			break;

    		case SEQUENCE_KW:
    		case SET_KW:
    			if (expect(TokenType.OF_KW) != null) {
    				Type rule = typeOrNamedTypeParser.parse();

    				if (rule != null) {
    					type = token.getType() == TokenType.SEQUENCE_KW ? new SequenceOfType(
    							rule) : new SetOfType(rule);
    				}
    			} else {
    				List<Object> rule = new SequenceParser(new boolean[] {
    						true, false, true }, TokenType.LBrace,
    						new ChoiceParser<List<Object>>(new SequenceParser(
    								componentTypeListsParser),
    								new SequenceParser(new boolean[] { true,
    										false },
    										extensionAndExceptionParser,
    										optionalExtensionMarkerParser)),
    						TokenType.RBrace).parse();

    				if (rule != null) {
    					if (rule.get(1) == null) {
    						type = token.getType() == TokenType.SEQUENCE_KW ? new SequenceType()
    								: new SetType();
    					} else {
    						List<Object> ruleList = (List<Object>) rule.get(1);
    						if (ruleList.size() == 1) {
    							type = token.getType() == TokenType.SEQUENCE_KW ? new SequenceType(
    									(ComponentTypeListsNode) ruleList
    											.get(0)) : new SetType(
    									(ComponentTypeListsNode) ruleList
    											.get(0));
    						} else {
    							type = token.getType() == TokenType.SEQUENCE_KW ? new SequenceType(
    									ruleList.get(0), ruleList.get(1))
    									: new SetType(ruleList.get(0),
    											ruleList.get(1));
    						}
    					}
    				}
    			}
    			break;

    		default:
    			resetToMark();
    			return null;

    		}

    		clearMark();
    		return type;
    	}

    }

    // NamedBitList ::=
    // NamedBit
    // | NamedBitList "," NamedBit
    protected class NamedBitListParser implements
    		RuleParser<List<NamedBitNode>> {

    	public List<NamedBitNode> parse() throws ParserException {
    		return new CommaSeparatedRuleParser<NamedBitNode>(namedBitParser)
    				.parse();
    	}

    }

    // NamedBit ::=
    // identifier "(" number ")"
    // | identifier "(" DefinedValue ")"
    protected class NamedBitParser implements RuleParser<NamedBitNode> {

    	@SuppressWarnings("unchecked")
    	public NamedBitNode parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.Identifier,
    				TokenType.LParen, new ChoiceParser<Object>(
    						new SingleTokenParser(TokenType.Number),
    						definedValueParser), TokenType.RParen).parse();

    		if (rule != null) {
    			if (rule.get(2) instanceof Token) {
    				return new NamedBitNode(((Token) rule.get(0)).getText(),
    						Integer.parseInt(((Token) rule.get(2)).getText()));
    			} else {
    				return new NamedBitNode(((Token) rule.get(0)).getText(),
    						(DefinedValue) rule.get(2));
    			}
    		}

    		return null;
    	}

    }

    // CharacterStringType ::=
    // RestrictedCharacterStringType
    // | UnrestrictedCharacterStringType
    protected class CharacterStringTypeParser implements RuleParser<Type> {

    	@SuppressWarnings("unchecked")
    	public Type parse() throws ParserException {
    		return new ChoiceParser<Type>(restrictedCharacterStringTypeParser,
    				unrestrictedCharacterStringTypeParser).parse();
    	}

    }

    // Enumerations ::=
    // RootEnumeration
    // | RootEnumeration "," "..." ExceptionSpec
    // | RootEnumeration "," "..." ExceptionSpec "," AdditionalEnumeration
    protected class EnumerationsParser implements RuleParser<EnumeratedType> {

    	@SuppressWarnings("unchecked")
    	public EnumeratedType parse() throws ParserException {
    		List<Object> rule = new SequenceParser(
    				new boolean[] { true, false }, enumerationParser,
    				new SequenceParser(
    						new boolean[] { true, true, false, false },
    						TokenType.Comma, TokenType.Ellipsis,
    						exceptionSpecParser, new SequenceParser(
    								TokenType.Comma, enumerationParser)))
    				.parse();

    		if (rule != null) {
    			if (rule.get(1) == null) {
    				return new EnumeratedType(
    						(List<EnumerationItemNode>) rule.get(0));
    			} else {
    				List<Object> ruleList = (List<Object>) rule.get(1);
    				Object exceptionSpec = ruleList.get(2);

    				if (ruleList.get(3) == null) {
    					return new EnumeratedType(
    							(List<EnumerationItemNode>) rule.get(0),
    							exceptionSpec != null ? (ExceptionIdentificationNode) exceptionSpec
    									: null);
    				} else {
    					return new EnumeratedType(
    							(List<EnumerationItemNode>) rule.get(0),
    							exceptionSpec != null ? (ExceptionIdentificationNode) exceptionSpec
    									: null,
    							(List<EnumerationItemNode>) ((List<Object>) ruleList
    									.get(3)).get(1));
    				}
    			}
    		}

    		return null;
    	}

    }

    // Enumeration ::= EnumerationItem | EnumerationItem "," Enumeration
    // RootEnumeration ::= Enumeration
    // AdditionalEnumeration ::= Enumeration
    protected class EnumerationParser implements
    		RuleParser<List<EnumerationItemNode>> {

    	public List<EnumerationItemNode> parse() throws ParserException {
    		return new CommaSeparatedRuleParser<EnumerationItemNode>(
    				enumerationItemParser).parse();
    	}

    }

    // EnumerationItem ::= identifier | NamedNumber
    protected class EnumerationItemParser implements
    		RuleParser<EnumerationItemNode> {

    	@SuppressWarnings("unchecked")
    	public EnumerationItemNode parse() throws ParserException {
    		Object rule = new ChoiceParser<Object>(namedNumberParser,
    				new SingleTokenParser(TokenType.Identifier)).parse();

    		if (rule == null) {
    			return null;
    		}

    		EnumerationItemNode item = new EnumerationItemNode();

    		if (rule instanceof Token) {
    			item.setName(((Token) rule).getText());
    		} else {
    			NamedNumber namedNumber = ((NamedNumber) rule);
    			item.setName(((NamedNumber) rule).getId());

    			// TODO: check for >= 0
    			if (namedNumber.getValue() != null) {
    				BigInteger bigNumber = namedNumber.getValue().getNumber();

    				if (bigNumber.bitLength() > 31) {
    					throw new ParserException(
    							"Enumeration value too long: "
    									+ bigNumber.toString());
    				}

    				item.setNumber(bigNumber.intValue());
    			} else {
    				item.setRef(namedNumber.getRef());
    			}
    		}

    		return item;
    	}

    }

    // NamedNumberList ::=
    // NamedNumber
    // | NamedNumberList "," NamedNumber
    protected class NamedNumberListParser implements
    		RuleParser<List<NamedNumber>> {

    	public List<NamedNumber> parse() throws ParserException {
    		return new CommaSeparatedRuleParser<NamedNumber>(namedNumberParser)
    				.parse();
    	}

    }

    // NamedNumber ::=
    // identifier "(" SignedNumber ")"
    // | identifier "(" DefinedValue ")"
    protected class NamedNumberParser implements RuleParser<NamedNumber> {

    	@SuppressWarnings("unchecked")
    	public NamedNumber parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.Identifier,
    				TokenType.LParen, new ChoiceParser<Object>(
    						signedNumberParser, definedValueParser),
    				TokenType.RParen).parse();

    		if (rule != null) {
    			if (rule.get(2) instanceof DefinedValue) {
    				return new NamedNumber(((Token) rule.get(0)).getText(),
    						(DefinedValue) rule.get(2));
    			} else {
    				return new NamedNumber(((Token) rule.get(0)).getText(),
    						(SignedNumber) rule.get(2));
    			}
    		}

    		return null;
    	}

    }

    // ComponentTypeLists ::=
    // RootComponentTypeList
    // | RootComponentTypeList "," ExtensionAndException ExtensionAdditions
    // OptionalExtensionMarker
    // | RootComponentTypeList "," ExtensionAndException ExtensionAdditions
    // ExtensionEndMarker "," RootComponentTypeList
    // | ExtensionAndException ExtensionAdditions ExtensionEndMarker ","
    // RootComponentTypeList
    // | ExtensionAndException ExtensionAdditions OptionalExtensionMarker
    // RootComponentTypeList ::= ComponentTypeList
    protected class ComponentTypeListsParser implements
    		RuleParser<ComponentTypeListsNode> {

    	@SuppressWarnings("unchecked")
    	public ComponentTypeListsNode parse() throws ParserException {
    		List<Object> rule = new ChoiceParser<List<Object>>(
    				new SequenceParser(new boolean[] { true, false },
    						componentTypeListParser, new SequenceParser(
    								new boolean[] { true, true, false, false },
    								TokenType.Comma,
    								extensionAndExceptionParser,
    								extensionAdditionsParser,
    								new ChoiceParser<Object>(
    										new SequenceParser(
    												extensionEndMarkerParser,
    												TokenType.Comma,
    												componentTypeListParser),
    										optionalExtensionMarkerParser))),
    				new SequenceParser(new boolean[] { true, false, false },
    						extensionAndExceptionParser,
    						extensionAdditionsParser, new ChoiceParser<Object>(
    								new SequenceParser(
    										extensionEndMarkerParser,
    										TokenType.Comma,
    										componentTypeListParser),
    								optionalExtensionMarkerParser))).parse();

    		if (rule != null) {
    			if (rule.size() == 2) {
    				List<Object> extList = (List<Object>) rule.get(1);

    				if (extList != null) {
    					if (extList.get(3) instanceof Boolean) {
    						// RootComponentTypeList "," ExtensionAndException
    						// ExtensionAdditions OptionalExtensionMarker
    						return new ComponentTypeListsNode(
    								(List<ComponentType>) rule.get(0),
    								(ExtensionAndExceptionNode) extList.get(1),
    								extList.get(2), (Boolean) extList.get(3));
    					} else {
    						// RootComponentTypeList "," ExtensionAndException
    						// ExtensionAdditions ExtensionEndMarker ","
    						// RootComponentTypeList
    						return new ComponentTypeListsNode(
    								(List<ComponentType>) rule.get(0),
    								(ExtensionAndExceptionNode) extList.get(1),
    								extList.get(2),
    								false,
    								(List<ComponentType>) ((List<Object>) extList
    										.get(3)).get(2));
    					}
    				} else {
    					// RootComponentTypeList
    					return new ComponentTypeListsNode(
    							(List<ComponentType>) rule.get(0));
    				}
    			} else {
    				Object obj = rule.get(2);

    				if (obj instanceof Boolean) {
    					// ExtensionAndException ExtensionAdditions
    					// OptionalExtensionMarker
    					return new ComponentTypeListsNode(null,
    							(ExtensionAndExceptionNode) rule.get(0),
    							rule.get(1), (Boolean) obj);
    				} else {
    					// ExtensionAndException ExtensionAdditions
    					// ExtensionEndMarker "," RootComponentTypeList
    					return new ComponentTypeListsNode(null,
    							(ExtensionAndExceptionNode) rule.get(0),
    							rule.get(1), true,
    							(List<ComponentType>) ((List<Object>) obj)
    									.get(2));
    				}
    			}
    		}

    		return null;
    	}

    }

    // ExtensionEndMarker ::= "," "..."
    protected class ExtensionEndMarkerParser implements RuleParser<Object> {

    	public Object parse() throws ParserException {
    		return new SequenceParser(TokenType.Comma, TokenType.Ellipsis)
    				.parse();
    	}

    }

    // ExtensionAdditions ::=
    // "," ExtensionAdditionList
    // | empty
    protected class ExtensionAdditionsParser implements RuleParser<Object> {

    	public List<Object> parse() throws ParserException {
    		return new ValueExtractor<List<Object>>(1, new SequenceParser(
    				TokenType.Comma, extensionAdditionListParser)).parse();
    	}

    }

    // ExtensionAdditionList ::=
    // ExtensionAddition
    // | ExtensionAdditionList "," ExtensionAddition
    protected class ExtensionAdditionListParser implements
    		RuleParser<List<Object>> {

    	public List<Object> parse() throws ParserException {
    		return new CommaSeparatedRuleParser<Object>(extensionAdditionParser)
    				.parse();
    	}

    }

    // ExtensionAddition ::=
    // ComponentType
    // | ExtensionAdditionGroup
    protected class ExtensionAdditionParser implements RuleParser<Object> {

    	@SuppressWarnings("unchecked")
    	public Object parse() throws ParserException {
    		return new ChoiceParser<Object>(componentTypeParser,
    				extensionAdditionGroupParser).parse();
    	}

    }

    // ExtensionAdditionGroup ::= "[[" VersionNumber ComponentTypeList "]]"
    protected class ExtensionAdditionGroupParser implements
    		RuleParser<ExtensionAdditionGroup> {

    	@SuppressWarnings("unchecked")
    	public ExtensionAdditionGroup parse() throws ParserException {
    		List<Object> rule = new SequenceParser(new boolean[] { true, false,
    				true, true }, TokenType.LVersionBrackets,
    				versionNumberParser, componentTypeListParser,
    				TokenType.RVersionBrackets).parse();

    		if (rule != null) {
    			return new ExtensionAdditionGroup((Integer) rule.get(1),
    					(List<ComponentType>) rule.get(2));
    		}

    		return null;
    	}

    }

    // ComponentTypeList ::=
    // ComponentType
    // | ComponentTypeList "," ComponentType
    protected class ComponentTypeListParser implements
    		RuleParser<List<ComponentType>> {

    	public List<ComponentType> parse() throws ParserException {
    		return new CommaSeparatedRuleParser<ComponentType>(
    				componentTypeParser).parse();
    	}

    }

    // ComponentType ::=
    // NamedType
    // | NamedType OPTIONAL
    // | NamedType DEFAULT Value
    // | COMPONENTS OF Type
    protected class ComponentTypeParser implements RuleParser<ComponentType> {

    	@SuppressWarnings("unchecked")
    	public ComponentType parse() throws ParserException {
    		List<Object> rule = new ChoiceParser<>(
    				new SequenceParser(namedTypeParser, TokenType.OPTIONAL_KW),
    				new SequenceParser(namedTypeParser, TokenType.DEFAULT_KW,
    						valueParser), new SequenceParser(namedTypeParser),
    				new SequenceParser(TokenType.COMPONENTS_KW,
    						TokenType.OF_KW, typeParser)).parse();

    		if (rule != null) {
    			switch (rule.size()) {
    			case 1:
    				return new ComponentType(ComponentType.CompType.NamedType,
    						(NamedType) rule.get(0));
    			case 2:
    				return new ComponentType(
    						ComponentType.CompType.NamedTypeOpt,
    						(NamedType) rule.get(0));
    			case 3:
    				if (rule.get(0) instanceof Token) {
    					return new ComponentType(ComponentType.CompType.Type,
    							(Type) rule.get(2));
    				} else {
    					return new ComponentType(
    							ComponentType.CompType.NamedTypeDef,
    							(NamedType) rule.get(0), (Value) rule.get(2));
    				}
    			}
    		}

    		return null;
    	}

    }

    // PrefixedType ::=
    // TaggedType
    // | EncodingPrefixedType
    protected class PrefixedTypeParser implements RuleParser<Type> {

    	@SuppressWarnings("unchecked")
    	public Type parse() throws ParserException {
    		return new ChoiceParser<Type>(taggedTypeParser,
    				encodingPrefixedTypeParser).parse();
    	}

    }

    // EncodingPrefixedType ::=
    // EncodingPrefix Type
    protected class EncodingPrefixedTypeParser implements RuleParser<Type> {

    	public Type parse() throws ParserException {
    		List<Object> rule = new SequenceParser(encodingPrefixParser,
    				typeParser).parse();

    		if (rule != null) {
    			Type type = (Type) rule.get(1);
    			type.setEncodingPrefix((EncodingPrefixNode) rule.get(0));
    			return type;
    		}

    		return null;
    	}

    }

    // EncodingPrefix ::=
    // "[" EncodingReference EncodingInstruction "]"
    protected class EncodingPrefixParser implements RuleParser<EncodingPrefixNode> {

    	public EncodingPrefixNode parse() throws ParserException {
            List<Object> rule = new SequenceParser(new boolean[] { true, false }, TokenType.LBracket,
                    encodingReferenceParser).parse();
            List<Token> encodingInstruction = new ArrayList<>();

    		if (rule != null) {
    			while (true) {
    				mark();

    				Token token = getToken();

    				if (token == null) {
    					throw new ParserException("Premature EOF");
    				} else if (token.getType() == TokenType.RBracket) {
    					break;
    				} else if (token.getType() == TokenType.LBracket) {
    					throw new ParserException("Invalid token " + token + " in EncodingInstruction");
    				}

    				clearMark();
    				encodingInstruction.add(token);
    			}

    			return new EncodingPrefixNode((String)rule.get(1), encodingInstruction);
    		}

    		return null;
    	}

    }

    // TaggedType ::=
    // Tag Type
    // | Tag IMPLICIT Type
    // | Tag EXPLICIT Type
    protected class TaggedTypeParser implements RuleParser<Type> {

    	public Type parse() throws ParserException {
    		List<Object> rule = new SequenceParser(new boolean[] { true, false,
    				true }, tagParser, new ChoiceParser<Token>(
    				TokenType.IMPLICIT_KW, TokenType.EXPLICIT_KW), typeParser)
    				.parse();

    		if (rule != null) {
    			Type type = (Type) rule.get(2);
    			type.setTag((Tag) rule.get(0));

    			if (rule.get(1) != null) {
    				if (((Token) rule.get(1)).getType() == TokenType.IMPLICIT_KW) {
    					type.setTaggingMode(TaggingMode.Implicit);
    				} else {
    					type.setTaggingMode(TaggingMode.Explicit);
    				}
    			}

    			return type;
    		}

    		return null;
    	}

    }

    // Tag ::= "[" EncodingReference Class ClassNumber "]"
    protected class TagParser implements RuleParser<Tag> {

    	public Tag parse() throws ParserException {
    		List<Object> rule = new SequenceParser(new boolean[] { true, false,
    				false, true, true }, TokenType.LBracket,
    				encodingReferenceParser, classParser, classNumberParser,
    				TokenType.RBracket).parse();

    		if (rule != null) {
    			return new Tag((String) rule.get(1), (ClassType) rule.get(2),
    					(ClassNumber) rule.get(3));
    		}

    		return null;
    	}

    }

    // EncodingReference ::=
    // encodingreference ":"
    // | empty
    protected class EncodingReferenceParser implements RuleParser<String> {

    	public String parse() throws ParserException {
            List<Object> rule = new SequenceParser(
                    new SingleTokenParser(TokenType.EncodingReference, Context.Encoding), TokenType.Colon).parse();

    	    if (rule != null) {
    	        Token token = (Token) rule.get(0);

                if (token != null) {
                    return token.getText();
                }
    	    }

    		return null;
    	}

    }

    // ClassNumber ::=
    // number
    // | DefinedValue
    protected class ClassNumberParser implements RuleParser<ClassNumber> {

    	@SuppressWarnings("unchecked")
    	public ClassNumber parse() throws ParserException {
    		Object rule = new ChoiceParser<Object>(new SingleTokenParser(
    				TokenType.Number), definedValueParser).parse();

    		if (rule != null) {
    			if (rule instanceof Token) {
    				return new ClassNumber(Integer.parseInt(((Token) rule)
    						.getText()));
    			} else {
    				return new ClassNumber((DefinedValue) rule);
    			}
    		}

    		return null;
    	}

    }

    // Class ::=
    // UNIVERSAL
    // | APPLICATION
    // | PRIVATE
    // | empty
    protected class ClassParser implements RuleParser<ClassType> {

    	public ClassType parse() throws ParserException {
    		Token token = new ChoiceParser<Token>(TokenType.UNIVERSAL_KW,
    				TokenType.APPLICATION_KW, TokenType.PRIVATE_KW).parse();

    		if (token != null) {
    			switch (token.getType()) {
    			case UNIVERSAL_KW:
    				return ClassType.UNIVERSAL;
    			case APPLICATION_KW:
    				return ClassType.APPLICATION;
    			case PRIVATE_KW:
    				return ClassType.PRIVATE;
    			}
    		}

    		return null;
    	}

    }

    // RestrictedCharacterStringType ::=
    // BMPString
    // | GeneralString
    // | GraphicString
    // | IA5String
    // | ISO646String
    // | NumericString
    // | PrintableString
    // | TeletexString
    // | T61String
    // | UniversalString
    // | UTF8String
    // | VideotexString
    // | VisibleString
    protected class RestrictedCharacterStringTypeParser implements
    		RuleParser<Type> {

    	@SuppressWarnings("unchecked")
    	public Type parse() throws ParserException {
    		return new ChoiceParser<Type>(bmpStringParser, generalStringParser,
    				graphicStringParser, ia5StringParser, iso646StringParser,
    				numericStringParser, printableStringParser,
    				teletexStringParser, t61StringParser,
    				universalStringParser, utf8StringParser,
    				videotexStringParser, visibleStringParser).parse();
    	}

    }

    protected class BMPStringParser implements RuleParser<Type> {

    	public Type parse() throws ParserException {
    		if (expect(TokenType.BMPString_KW) != null) {
    			return new BMPString();
    		}
    		return null;
    	}

    }

    protected class GeneralStringParser implements RuleParser<Type> {

    	public Type parse() throws ParserException {
    		if (expect(TokenType.GeneralString_KW) != null) {
    			return new GeneralString();
    		}
    		return null;
    	}

    }

    protected class GraphicStringParser implements RuleParser<Type> {

    	public Type parse() throws ParserException {
    		if (expect(TokenType.GraphicString_KW) != null) {
    			return new GraphicString();
    		}
    		return null;
    	}

    }

    protected class IA5StringParser implements RuleParser<Type> {

    	public Type parse() throws ParserException {
    		if (expect(TokenType.IA5String_KW) != null) {
    			return new IA5String();
    		}
    		return null;
    	}

    }

    protected class ISO646StringParser implements RuleParser<Type> {

    	public Type parse() throws ParserException {
    		if (expect(TokenType.ISO646String_KW) != null) {
    			return new ISO646String();
    		}
    		return null;
    	}

    }

    protected class NumericStringParser implements RuleParser<Type> {

    	public Type parse() throws ParserException {
    		if (expect(TokenType.NumericString_KW) != null) {
    			return new NumericString();
    		}
    		return null;
    	}

    }

    protected class PrintableStringParser implements RuleParser<Type> {

    	public Type parse() throws ParserException {
    		if (expect(TokenType.PrintableString_KW) != null) {
    			return new PrintableString();
    		}
    		return null;
    	}

    }

    protected class TeletexStringParser implements RuleParser<Type> {

    	public Type parse() throws ParserException {
    		if (expect(TokenType.TeletexString_KW) != null) {
    			return new TeletexString();
    		}
    		return null;
    	}

    }

    protected class T61StringParser implements RuleParser<Type> {

    	public Type parse() throws ParserException {
    		if (expect(TokenType.T61String_KW) != null) {
    			return new T61String();
    		}
    		return null;
    	}

    }

    protected class UniversalStringParser implements RuleParser<Type> {

    	public Type parse() throws ParserException {
    		if (expect(TokenType.UniversalString_KW) != null) {
    			return new UniversalString();
    		}
    		return null;
    	}

    }

    protected class UTF8StringParser implements RuleParser<Type> {

    	public Type parse() throws ParserException {
    		if (expect(TokenType.UTF8String_KW) != null) {
    			return new UTF8String();
    		}
    		return null;
    	}

    }

    protected class VideotexStringParser implements RuleParser<Type> {

    	public Type parse() throws ParserException {
    		if (expect(TokenType.VideotexString_KW) != null) {
    			return new VideotexString();
    		}
    		return null;
    	}

    }

    protected class VisibleStringParser implements RuleParser<Type> {

    	public Type parse() throws ParserException {
    		if (expect(TokenType.VisibleString_KW) != null) {
    			return new VisibleString();
    		}
    		return null;
    	}

    }

    // AlternativeTypeLists ::=
    // RootAlternativeTypeList
    // | RootAlternativeTypeList ","
    // ExtensionAndException ExtensionAdditionAlternatives
    // OptionalExtensionMarker
    protected class AlternativeTypeListsParser implements
    		RuleParser<AlternativeTypeLists> {

    	@SuppressWarnings("unchecked")
    	public AlternativeTypeLists parse() throws ParserException {
    		List<Object> rule = new ChoiceParser<List<Object>>(
    				new SequenceParser(new boolean[] { true, true, true, false,
    						false }, rootAlternativeTypeListParser,
    						TokenType.Comma, extensionAndExceptionParser,
    						extensionAdditionAlternativesParser,
    						optionalExtensionMarkerParser), new SequenceParser(
    						rootAlternativeTypeListParser)).parse();

    		if (rule != null) {
    			if (rule.size() == 1) {
    				return new AlternativeTypeLists(
    						(List<NamedType>) rule.get(0));
    			} else {
    				return new AlternativeTypeLists(
    						(List<NamedType>) rule.get(0),
    						(ExtensionAndExceptionNode) rule.get(2),
    						(List<ExtensionAdditionAlternativeNode>) rule
    								.get(3), (Boolean) rule.get(4));
    			}
    		}

    		return null;
    	}

    }

    // OptionalExtensionMarker ::= "," "..." | empty
    protected class OptionalExtensionMarkerParser implements
    		RuleParser<Boolean> {

    	public Boolean parse() throws ParserException {
    		return new SequenceParser(TokenType.Comma, TokenType.Ellipsis)
    				.parse() != null;
    	}

    }

    // RootAlternativeTypeList ::= AlternativeTypeList
    protected class RootAlternativeTypeListParser implements
    		RuleParser<List<NamedType>> {

    	public List<NamedType> parse() throws ParserException {
    		return alternativeTypeListParser.parse();
    	}

    }

    // ExtensionAdditionAlternatives ::=
    // "," ExtensionAdditionAlternativesList
    // | empty
    protected class ExtensionAdditionAlternativesParser implements
    		RuleParser<List<ExtensionAdditionAlternativeNode>> {

    	@SuppressWarnings("unchecked")
    	public List<ExtensionAdditionAlternativeNode> parse()
    			throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.Comma,
    				extensionAdditionAlternativesListParser).parse();

    		if (rule != null) {
    			return (List<ExtensionAdditionAlternativeNode>) rule.get(1);
    		}

    		return null;
    	}

    }

    // ExtensionAdditionAlternativesList ::=
    // ExtensionAdditionAlternative
    // | ExtensionAdditionAlternativesList "," ExtensionAdditionAlternative
    protected class ExtensionAdditionAlternativesListParser implements
    		RuleParser<List<ExtensionAdditionAlternativeNode>> {

    	public List<ExtensionAdditionAlternativeNode> parse()
    			throws ParserException {
    		return new CommaSeparatedRuleParser<ExtensionAdditionAlternativeNode>(
    				extensionAdditionAlternativeParser).parse();
    	}

    }

    // ExtensionAdditionAlternative ::=
    // ExtensionAdditionAlternativesGroup
    // | NamedType
    protected class ExtensionAdditionAlternativeParser implements
    		RuleParser<ExtensionAdditionAlternativeNode> {

    	@SuppressWarnings("unchecked")
    	public ExtensionAdditionAlternativeNode parse() throws ParserException {
    		Object rule = new ChoiceParser<Object>(namedTypeParser,
    				extensionAdditionAlternativesGroupParser).parse();

    		if (rule != null) {
    			if (rule instanceof NamedType) {
    				return new ExtensionAdditionAlternativeNode(
    						(NamedType) rule);
    			} else {
    				return new ExtensionAdditionAlternativeNode(
    						(ExtensionAdditionAlternativesGroup) rule);
    			}
    		}

    		return null;
    	}
    }

    // ExtensionAdditionAlternativesGroup ::=
    // "[[" VersionNumber AlternativeTypeList "]]"
    protected class ExtensionAdditionAlternativesGroupParser implements
    		RuleParser<ExtensionAdditionAlternativesGroup> {

    	@SuppressWarnings("unchecked")
    	public ExtensionAdditionAlternativesGroup parse()
    			throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.LVersionBrackets,
    				versionNumberParser, alternativeTypeListParser,
    				TokenType.RVersionBrackets).parse();

    		if (rule != null) {
    			return new ExtensionAdditionAlternativesGroup(
    					(Integer) rule.get(1), (List<NamedType>) rule.get(2));
    		}

    		return null;
    	}

    }

    // VersionNumber ::= empty | number ":"
    protected class VersionNumberParser implements RuleParser<Integer> {

    	public Integer parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.Number,
    				TokenType.Colon).parse();

    		if (rule != null) {
    			return Integer.valueOf(((Token) rule.get(0)).getText());
    		}

    		return null;
    	}

    }

    // AlternativeTypeList ::=
    // NamedType
    // | AlternativeTypeList "," NamedType
    protected class AlternativeTypeListParser implements
    		RuleParser<List<NamedType>> {

    	public List<NamedType> parse() throws ParserException {
    		return new CommaSeparatedRuleParser<NamedType>(namedTypeParser)
    				.parse();
    	}

    }

    // ExtensionAndException ::= "..." | "..." ExceptionSpec
    protected class ExtensionAndExceptionParser implements
    		RuleParser<ExtensionAndExceptionNode> {

    	public ExtensionAndExceptionNode parse() throws ParserException {
    		List<Object> rule = new SequenceParser(
    				new boolean[] { true, false }, TokenType.Ellipsis,
    				exceptionSpecParser).parse();

    		if (rule != null) {
    			if (rule.size() > 1) {
    				return new ExtensionAndExceptionNode(
    						(ExceptionIdentificationNode) rule.get(1));
    			}

    			return new ExtensionAndExceptionNode();
    		}

    		return null;
    	}

    }

    // ExceptionSpec ::= "!" ExceptionIdentification | empty
    protected class ExceptionSpecParser implements
    		RuleParser<ExceptionIdentificationNode> {

    	public ExceptionIdentificationNode parse() throws ParserException {
    		return new ValueExtractor<ExceptionIdentificationNode>(1,
    				new SequenceParser(TokenType.Exclamation,
    						exceptionIdentificationParser)).parse();
    	}

    }

    // ExceptionIdentification ::=
    // SignedNumber
    // | DefinedValue
    // | Type ":" Value
    protected class ExceptionIdentificationParser implements
    		RuleParser<ExceptionIdentificationNode> {

    	@SuppressWarnings("unchecked")
    	public ExceptionIdentificationNode parse() throws ParserException {
    		List<Object> rule = new ChoiceParser<>(
    				new SequenceParser(signedNumberParser), new SequenceParser(
    						definedValueParser), new SequenceParser(typeParser,
    						TokenType.Colon, valueParser)).parse();

    		if (rule != null) {
    			if (rule.size() == 1) {
    				return new ExceptionIdentificationNode((Value) rule.get(0));
    			} else {
    				return new ExceptionIdentificationNode((Type) rule.get(0),
    						(Value) rule.get(2));
    			}
    		}

    		return null;
    	}

    }

    // SignedNumber ::=
    // number
    // | "-" number
    protected class SignedNumberParser implements RuleParser<SignedNumber> {

    	public SignedNumber parse() throws ParserException {
    		List<Object> rule = new SequenceParser(
    				new boolean[] { false, true }, TokenType.Minus,
    				TokenType.Number).parse();

    		if (rule != null) {
    			return new SignedNumber(new BigInteger(
    					(rule.get(0) != null ? "-" : "")
    							+ ((Token) rule.get(1)).getText()));
    		}

    		return null;
    	}

    }

    // Value ::=
    // BuiltinValue
    // | ReferencedValue
    // | ObjectClassFieldValue
    // PrefixedValue ::= Value
    protected class ValueParser implements RuleParser<Value> {

    	@SuppressWarnings("unchecked")
    	public Value parse() throws ParserException {
    		return new ChoiceParser<>(objectClassFieldValueParser, builtinOrReferencedValueParser).parse();
    	}

    }

    // BuiltinValue ::=
    // BitStringValue
    // | BooleanValue
    // | CharacterStringValue
    // | ChoiceValue
    // | EmbeddedPDVValue
    // | EnumeratedValue
    // | ExternalValue
    // | InstanceOfValue
    // | IntegerValue
    // | IRIValue
    // | NullValue
    // | ObjectIdentifierValue
    // | OctetStringValue
    // | RealValue
    // | RelativeIRIValue
    // | RelativeOIDValue
    // | SequenceValue
    // | SequenceOfValue
    // | SetValue
    // | SetOfValue
    // | PrefixedValue
    // | TimeValue
    // ReferencedValue ::=
    // DefinedValue
    // | ValueFromObject
    protected class BuiltinOrReferencedValueParser implements RuleParser<Value> {

    	@SuppressWarnings("unchecked")
    	public Value parse() throws ParserException {
    		Set<Value> rules = new AmbiguousChoiceParser<>(bitStringValueParser,
    				booleanValueParser, realValueParser,
    				characterStringValueParser, choiceValueParser,
    				valueFromObjectParser, definedValueParser,
    				embeddedPDVValueParser, integerValueParser, iriValueParser,
    				nullValueParser, objectIdentifierValueParser,
    				relativeIRIValueParser, relativeOIDValueParser,
    				collectionValueParser, emptyValueParser).parse();

    		if (rules.size() == 1) {
    		    return rules.iterator().next();
            } else if (rules.size() > 1) {
    		    return new AmbiguousValue(rules);
            }

            return null;
    	}

    }

    // BitStringValue ::=
    // bstring
    // | hstring
    // | "{" IdentifierList "}"
    // | CONTAINING Value
    // OctetStringValue ::=
    // bstring
    // | hstring
    // | CONTAINING Value
    protected class BitOrOctetStringValueParser implements RuleParser<Value> {

    	@SuppressWarnings("unchecked")
    	public Value parse() throws ParserException {
    		Object rule = new ChoiceParser<>(new SingleTokenParser(
    				TokenType.BString),
    				new SingleTokenParser(TokenType.HString),
    				new SequenceParser(TokenType.LBrace, identifierListParser,
    						TokenType.RBrace), new SequenceParser(
    						TokenType.CONTAINING_KW, valueParser)).parse();

    		if (rule != null) {
    			if (rule instanceof List) {
    				List<Object> list = (List<Object>) rule;

    				if (list.size() == 3) {
    					return new BitStringValue((List<String>) list.get(1));
    				} else if (TokenType.CONTAINING_KW == ((Token) list.get(0))
    						.getType()) {
    					return new ContainingStringValue(
    							(Value) ((List<Object>) rule).get(1));
    				} else {
    					return new EmptyValue();
    				}
    			} else {
    				Token token = (Token) rule;

    				if (token.getType() == TokenType.BString) {
    					return new BinaryStringValue(token.getText().substring(
    							0, token.getText().length()));
    				} else {
    					return new HexStringValue(token.getText().substring(0,
    							token.getText().length()));
    				}
    			}
    		}

    		return null;
    	}

    }

    // IdentifierList ::=
    // identifier
    // | IdentifierList "," identifier
    protected class IdentifierListParser implements RuleParser<List<String>> {

    	public List<String> parse() throws ParserException {
    		List<Token> tokens = new CommaSeparatedRuleParser<Token>(
    				new SingleTokenParser(TokenType.Identifier)).parse();

    		if (tokens != null) {
    			List<String> values = new ArrayList<String>(tokens.size());

    			for (Token token : tokens) {
    				values.add(token.getText());
    			}

    			return values;
    		}

    		return null;
    	}

    }

    // BooleanValue ::= TRUE | FALSE
    protected class BooleanValueParser implements RuleParser<Value> {

    	public Value parse() throws ParserException {
    		Token token = new ChoiceParser<Token>(TokenType.TRUE_KW,
    				TokenType.FALSE_KW).parse();
    		if (token != null) {
    			if (token.getType() == TokenType.TRUE_KW) {
    				return new BooleanValue(true);
    			} else {
    				return new BooleanValue(false);
    			}
    		}
    		return null;
    	}

    }

    // CharacterStringValue ::=
    // RestrictedCharacterStringValue
    // | UnrestrictedCharacterStringValue
    protected class CharacterStringValueParser implements RuleParser<Value> {

    	@SuppressWarnings("unchecked")
    	public Value parse() throws ParserException {
    		return new ChoiceParser<Value>(
    				restrictedCharacterStringValueParser,
    				unrestrictedCharacterStringValue).parse();
    	}

    }

    // RestrictedCharacterStringValue ::=
    // cstring
    // | CharacterStringList
    // | Quadruple
    // | Tuple
    // TimeValue ::= tstring
    protected class RestrictedCharacterStringValueParser implements
    		RuleParser<Value> {

    	@SuppressWarnings("unchecked")
    	public Value parse() throws ParserException {
    		Object rule = new ChoiceParser<>(new SingleTokenParser(TokenType.CString), characterStringListParser,
    				collectionValueParser).parse();

    		if (rule != null) {
    			if (rule instanceof StringToken) {
    				StringValue string = new StringValue(((Token) rule).getText(), ((StringToken) rule).getFlags());

    				if (!string.isCString() && !string.isTString()) {
    					return null;
    				}

    				return string;
    			} else {
    				if (rule instanceof CollectionOfValue) {
    					if (((CollectionOfValue) rule).isTuple() || ((CollectionOfValue) rule).isQuadruple()) {
    						return (Value) rule;
    					}

    					return null;
    				}

    				return (Value) rule;
    			}
    		}

    		return null;
    	}

    }

    // CharacterStringList ::= "{" CharSyms "}"
    protected class CharacterStringListParser implements
    		RuleParser<CharacterStringList> {

    	@SuppressWarnings("unchecked")
    	public CharacterStringList parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.LBrace,
    				charSymsParser, TokenType.RBrace).parse();

    		if (rule != null) {
    			return new CharacterStringList((List<Value>) rule.get(1));
    		}

    		return null;
    	}

    }

    // CharSyms ::=
    // CharsDefn
    // | CharSyms "," CharsDefn
    protected class CharSymsParser implements RuleParser<List<Value>> {

    	public List<Value> parse() throws ParserException {
    		return new CommaSeparatedRuleParser<Value>(charsDefnParser).parse();
    	}

    }

    // CharsDefn ::=
    // cstring
    // | Quadruple
    // | Tuple
    // | DefinedValue
    protected class CharsDefnParser implements RuleParser<Value> {

    	@SuppressWarnings("unchecked")
    	public Value parse() throws ParserException {
    		Object rule = new ChoiceParser<Object>(new SingleTokenParser(
    				TokenType.CString), collectionValueParser,
    				definedValueParser).parse();

    		if (rule != null) {
    			if (rule instanceof StringToken) {
    				StringValue string = new StringValue(
    						((Token) rule).getText(),
    						((StringToken) rule).getFlags());

    				if (!string.isCString()) {
    					throw new ParserException("Invalid cstring: "
    							+ ((Token) rule).getText());
    				}

    				return string;
    			} else if (rule instanceof Value) {
    				if (rule instanceof CollectionOfValue) {
    					if (((CollectionOfValue) rule).isTuple()
    							|| ((CollectionOfValue) rule).isQuadruple()) {
    						return (Value) rule;
    					}

    					return null;
    				}

    				return (Value) rule;
    			}
    		}

    		return null;
    	}

    }

    // UnrestrictedCharacterStringValue ::= SequenceValue
    protected class UnrestrictedCharacterStringValue implements
    		RuleParser<Value> {

    	public Value parse() throws ParserException {
    		return collectionValueParser.parse();
    	}

    }

    // ChoiceValue ::= identifier ":" Value
    protected class ChoiceValueParser implements RuleParser<ChoiceValue> {

    	public ChoiceValue parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.Identifier,
    				TokenType.Colon, valueParser).parse();

    		if (rule != null) {
    			return new ChoiceValue(((Token) rule.get(0)).getText(),
    					(Value) rule.get(2));
    		}

    		return null;
    	}

    }

    // EmbeddedPDVValue ::= SequenceValue
    protected class EmbeddedPDVValueParser implements RuleParser<Value> {

    	public Value parse() throws ParserException {
    		return collectionValueParser.parse();
    	}

    }

    // IntegerValue ::=
    // SignedNumber
    // | identifier
    protected class IntegerValueParser implements RuleParser<IntegerValue> {

    	@SuppressWarnings({ "unchecked" })
    	public IntegerValue parse() throws ParserException {
    		Object value = new ChoiceParser<Object>(signedNumberParser,
    				new SingleTokenParser(TokenType.Identifier)).parse();

    		if (value != null) {
    			if (value instanceof Token) {
    				return new IntegerValue(((Token) value).getText());
    			} else {
    				return new IntegerValue(((SignedNumber) value).getNumber());
    			}
    		}

    		return null;
    	}

    }

    private List<IRIToken> parseArcIdSequence(String iriValue, boolean realtive)
    		throws ParserException {
    	IRILexer lexer = new IRILexer(iriValue);
    	List<IRIToken> iriTokens = new ArrayList<IRIToken>();
    	IRIToken iriToken = null;
    	boolean idExpected = realtive;

    	do {
    		iriToken = lexer.nextToken();

    		if (iriToken == null) {
    			break;
    		}

    		if (idExpected) {
    			if (iriToken.getType() == IRIToken.Type.Solidus) {
    				throw new ParserException(
    						"(non-)integerUnicodeLabel expected");
    			}

    			iriTokens.add(iriToken);
    		} else {
    			if (iriToken.getType() != IRIToken.Type.Solidus) {
    				throw new ParserException("Solidus expected");
    			}
    		}

    		idExpected = !idExpected;

    	} while (true);

    	return iriTokens;
    }

    // IRIValue ::=
    // '"'
    // FirstArcIdentifier
    // SubsequentArcIdentifier
    // '"'
    // FirstArcIdentifier ::=
    // "/" ArcIdentifier
    // SubsequentArcIdentifier ::=
    // "/" ArcIdentifier SubsequentArcIdentifier
    // | empty
    // ArcIdentifier ::=
    // integerUnicodeLabel
    // | non-integerUnicodeLabel
    protected class IRIValueParser implements RuleParser<Value> {

    	public Value parse() throws ParserException {
    		Token token = new SingleTokenParser(Token.TokenType.CString)
    				.parse();

    		if (token != null) {
    			List<IRIToken> iriTokens = parseArcIdSequence(token.getText(),
    					false);

    			if (iriTokens.size() == 0) {
    				throw new ParserException("Empty IRIValue");
    			}

    			return new IRIValue(iriTokens);
    		}

    		return null;
    	}

    }

    // NullValue ::= NULL
    protected class NullValueParser implements RuleParser<Value> {

    	public Value parse() throws ParserException {
    		if (expect(TokenType.NULL_KW) != null) {
    			return new NullValue();
    		}

    		return null;
    	}

    }

    // RealValue ::=
    // NumericRealValue
    // | SpecialRealValue
    protected class RealValueParser implements RuleParser<RealValue> {

    	@SuppressWarnings("unchecked")
    	public RealValue parse() throws ParserException {
    		return new ChoiceParser<>(numericRealValueParser, specialRealValueParser).parse();
    	}

    }

    // NumericRealValue ::=
    // realnumber
    // | "-" realnumber
    // | SequenceValue
    protected class NumericRealValueParser implements RuleParser<RealValue> {

    	@SuppressWarnings("unchecked")
    	public RealValue parse() throws ParserException {
    		Object rule = new ChoiceParser<>(new SingleTokenParser(
    				TokenType.RealNumber), new SequenceParser(TokenType.Minus,
    				TokenType.RealNumber), collectionValueParser).parse();

    		if (rule != null) {
    			if (rule instanceof Token) {
    				return new RealValue(new BigDecimal(
    						((Token) rule).getText()));
    			} else if (rule instanceof CollectionValue) {
    				List<Value> values = ((CollectionValue) rule).getValues();

    				BigInteger mantissa, base, exponent;

    				if (values.size() != 3
    						|| !checkNamedValue(values.get(0), "mantissa")
    						|| !checkNamedValue(values.get(1), "base")
    						|| !checkNamedValue(values.get(2), "exponent")) {
    					throw new ParserException(
    							"Invalid sequence. It must contain values for 'mantissa', 'base' and 'exponent'");
    				} else {
    					mantissa = getValue(values.get(0));
    					base = getValue(values.get(1));
    					exponent = getValue(values.get(2));

    					byte[] baseBytes = base.toByteArray();

    					if (baseBytes.length != 1 || baseBytes[0] != 2
    							&& baseBytes[0] != 10) {
    						throw new ParserException("Invalid base: " + base);
    					}

    					if (mantissa.bitLength() > 63) {
    						throw new ParserException("Mantissa to long: "
    								+ mantissa.toString());
    					}

    					if (exponent.bitLength() > 63) {
    						throw new ParserException("Exponent to long: "
    								+ exponent.toString());
    					}
    				}

    				return new RealValue(mantissa.longValue(),
    						base.longValue(), exponent.longValue());
    			} else {
    				return new RealValue(new BigDecimal("-"
    						+ ((Token) ((List<Object>) rule).get(1)).getText()));
    			}
    		}

    		return null;
    	}

    	private boolean checkNamedValue(Value value, String name) {
    		return value instanceof NamedValue
    				&& name.equals(((NamedValue) value).getName())
    				&& ((NamedValue) value).getValue() instanceof IntegerValue
    				&& !((IntegerValue) ((NamedValue) value).getValue())
    						.isReference();
    	}

    	private BigInteger getValue(Value value) {
    		return ((IntegerValue) ((NamedValue) value).getValue()).getValue();
    	}

    }

    // SpecialRealValue ::=
    // PLUS-INFINITY
    // | MINUS-INFINITY
    // | NOT-A-NUMBER
    protected class SpecialRealValueParser implements RuleParser<RealValue> {

    	public RealValue parse() throws ParserException {
    		Token token = new ChoiceParser<Token>(TokenType.PLUS_INFINITY_KW,
    				TokenType.MINUS_INFINITY_KW, TokenType.NOT_A_NUMBER_KW)
    				.parse();

    		if (token != null) {
    			switch (token.getType()) {
    			case PLUS_INFINITY_KW:
    				return new RealValue(RealValue.Type.PositiveInf);
    			case MINUS_INFINITY_KW:
    				return new RealValue(RealValue.Type.NegativeInf);
    			case NOT_A_NUMBER_KW:
    				return new RealValue(RealValue.Type.NaN);
    			}
    		}

    		return null;
    	}

    }

    // RelativeIRIValue ::=
    // '"'
    // FirstRelativeArcIdentifier
    // SubsequentArcIdentifier
    // '"'
    // FirstRelativeArcIdentifier ::= ArcIdentifier
    // SubsequentArcIdentifier ::=
    // "/" ArcIdentifier SubsequentArcIdentifier
    // | empty
    // ArcIdentifier ::=
    // integerUnicodeLabel
    // | non-integerUnicodeLabel
    protected class RelativeIRIValueParser implements RuleParser<Value> {

    	public Value parse() throws ParserException {
    		Token token = new SingleTokenParser(Token.TokenType.CString)
    				.parse();

    		if (token != null) {
    			List<IRIToken> iriTokens = parseArcIdSequence(token.getText(),
    					true);

    			if (iriTokens.size() == 0) {
    				throw new ParserException("Empty RelativeIRIValue");
    			}

    			return new RelativeIRIValue(iriTokens);
    		}

    		return null;
    	}

    }

    // RelativeOIDValue ::=
    // "{" RelativeOIDComponentsList "}"
    protected class RelativeOIDValueParser implements
    		RuleParser<RelativeOIDValue> {

    	public RelativeOIDValue parse() throws ParserException {
    		List<OIDComponentNode> rule = new ValueExtractor<List<OIDComponentNode>>(
    				1, new SequenceParser(TokenType.LBrace,
    						relativeOIDComponentsListParser, TokenType.RBrace))
    				.parse();

    		if (rule != null) {
    			return new RelativeOIDValue(rule);
    		}

    		return null;
    	}

    }

    // RelativeOIDComponentsList ::=
    // RelativeOIDComponents
    // | RelativeOIDComponents RelativeOIDComponentsList
    protected class RelativeOIDComponentsListParser implements
    		RuleParser<List<OIDComponentNode>> {

    	public List<OIDComponentNode> parse() throws ParserException {
    		return new RepetitionParser<OIDComponentNode>(
    				relativeOIDComponentsParser).parse();
    	}

    }

    // RelativeOIDComponents ::=
    // NumberForm
    // | NameAndNumberForm
    // | DefinedValue
    protected class RelativeOIDComponentsParser implements
    		RuleParser<OIDComponentNode> {

    	@SuppressWarnings("unchecked")
    	public OIDComponentNode parse() throws ParserException {
    		Node rule = new ChoiceParser<Node>(nameAndNumberFormParser,
    				numberFormParser, definedValueParser).parse();

    		if (rule != null) {
    			if (rule instanceof OIDComponentNode) {
    				return (OIDComponentNode) rule;
    			} else {
    				return new OIDComponentNode((DefinedValue) rule);
    			}
    		}

    		return null;
    	}

    }

    // SequenceValue ::=
    // "{" ComponentValueList "}"
    // SetValue ::=
    // "{" ComponentValueList "}"
    // ExternalValue ::= SequenceValue
    // SequenceOfValue ::=
    // "{" ValueList "}"
    // | "{" NamedValueList "}"
    // SetOfValue ::=
    // "{" ValueList "}"
    // | "{" NamedValueList "}"
    // Tuple ::= "{" TableColumn "," TableRow "}"
    // TableColumn ::= number
    // TableRow ::= number
    // Quadruple ::= "{" Group "," Plane "," Row "," Cell "}"
    // Group ::= number
    // Plane ::= number
    // Row ::= number
    // Cell ::= number
    protected class CollectionValueParser implements RuleParser<Value> {

    	@SuppressWarnings("unchecked")
    	public Value parse() throws ParserException {
    		List<Object> rule = new ChoiceParser<>(new SequenceParser(TokenType.LBrace, namedValueListParser,
    						TokenType.RBrace),
    				new SequenceParser(TokenType.LBrace, valueListParser, TokenType.RBrace)).parse();

    		if (rule != null) {
    			List<Value> values = (List<Value>) rule.get(1);

    			if (values.get(0) instanceof NamedValue) {
    				return new CollectionValue(values);
    			} else {
    				return new CollectionOfValue(values);
    			}
    		}

    		return null;
    	}

    }

    // SequenceValue ::= "{" "}"
    protected class EmptyValueParser implements RuleParser<Value> {

    	public Value parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.LBrace,
    				TokenType.RBrace).parse();

    		if (rule != null) {
    			return new EmptyValue();

    		}

    		return null;
    	}

    }

    // NamedValue ::= identifier Value
    protected class NamedValueParser implements RuleParser<NamedValue> {

    	public NamedValue parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.Identifier, valueParser).parse();

    		if (rule != null) {
    			return new NamedValue(((Token) rule.get(0)).getText(),(Value) rule.get(1));
    		}

    		return null;
    	}

    }

    // ValueList ::=
    // Value
    // | ValueList "," Value
    protected class ValueListParser implements RuleParser<List<Value>> {

    	public List<Value> parse() throws ParserException {
    		return new CommaSeparatedRuleParser<>(valueParser).parse();
    	}

    }

    // NamedValueList ::=
    // NamedValue
    // | NamedValueList "," NamedValue
    // ComponentValueList ::=
    // NamedValue
    // | ComponentValueList "," NamedValue
    protected class NamedValueListParser implements	RuleParser<List<NamedValue>> {

    	public List<NamedValue> parse() throws ParserException {
    		return new CommaSeparatedRuleParser<>(namedValueParser).parse();
    	}

    }

    // UnrestrictedCharacterStringType ::= CHARACTER STRING
    protected class UnrestrictedCharacterStringTypeParser implements
    		RuleParser<Type> {

    	public Type parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.CHARACTER_KW,
    				TokenType.STRING_KW).parse();

    		if (rule != null) {
    			return new CharacterString();
    		}

    		return null;
    	}

    }

    // ReferencedType ::=
    // DefinedType
    // | UsefulType
    // | SelectionType
    // | TypeFromObject
    // | ValueSetFromObjects
    // DefinedType ::=
    // ExternalTypeReference
    // | typereference
    // | ParameterizedType
    // | ParameterizedValueSetType
    protected class ReferencedTypeParser implements RuleParser<Type> {

    	@SuppressWarnings("unchecked")
    	public Type parse() throws ParserException {
    		return new ChoiceParser<Type>(usefulTypeParser,
    				typeFromObjectsParser /* incl. ValueSetFromObjects */,
    				externalTypeReferenceParser, parameterizedTypeParser
    				/* incl. ParameterizedValueSetType */, typeReferenceParser,
    				selectionTypeParser).parse();
    	}

    }

    // typereference
    protected class TypeReferenceParser implements RuleParser<TypeReference> {

    	public TypeReference parse() throws ParserException {
    		Token token = expect(TokenType.TypeReference);

    		if (token != null) {
    			return new TypeReference(token.getText());
    		}

    		return null;
    	}

    }

    // ExternalTypeReference ::=
    // modulereference
    // "."
    // typereference
    protected class ExternalTypeReferenceParser implements
    		RuleParser<ExternalTypeReference> {

    	public ExternalTypeReference parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.TypeReference,
    				TokenType.Dot, TokenType.TypeReference).parse();

    		if (rule != null) {
    			return new ExternalTypeReference(
    					((Token) rule.get(0)).getText(),
    					((Token) rule.get(2)).getText());
    		}

    		return null;

    	}

    }

    // UsefulType ::= typereference
    protected class UsefulTypeParser implements RuleParser<UsefulType> {

    	public UsefulType parse() throws ParserException {
    		Token type = new ChoiceParser<Token>(TokenType.UTCTime_KW,
    				TokenType.GeneralizedTime_KW, TokenType.ObjectDescriptor_KW)
    				.parse();

    		if (type != null) {
    			String tmp = type.getType().toString();
    			String typeName = StringUtils.concat("ASN1",
    					tmp.substring(0, tmp.length() - 3));

    			switch (type.getType()) {
    			case UTCTime_KW:
    				return new UTCTime(typeName);
    			case GeneralizedTime_KW:
    				return new GeneralizedTime(typeName);
    			case ObjectDescriptor_KW:
    				return new ObjectDescriptor(typeName);
    			}
    		}

    		return null;
    	}

    }

    // SelectionType ::= identifier "<" Type
    protected class SelectionTypeParser implements RuleParser<SelectionType> {

    	public SelectionType parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.Identifier,
    				TokenType.LT, typeParser).parse();

    		if (rule != null) {
    			return new SelectionType(((Token) rule.get(0)).getText(),
    					(Type) rule.get(2));
    		}

    		return null;
    	}

    }

    // TypeWithConstraint ::=
    // SET Constraint OF Type
    // | SET SizeConstraint OF Type
    // | SEQUENCE Constraint OF Type
    // | SEQUENCE SizeConstraint OF Type
    // | SET Constraint OF NamedType
    // | SET SizeConstraint OF NamedType
    // | SEQUENCE Constraint OF NamedType
    // | SEQUENCE SizeConstraint OF NamedType
    protected class TypeWithConstraintParser implements RuleParser<Type> {

    	@SuppressWarnings("unchecked")
    	public Type parse() throws ParserException {
    		List<Object> rule = new ChoiceParser<List<Object>>(
    				new SequenceParser(new ChoiceParser<Token>(
    						TokenType.SET_KW, TokenType.SEQUENCE_KW),
    						sizeConstraintParser, TokenType.OF_KW,
    						typeOrNamedTypeParser), new SequenceParser(
    						new ChoiceParser<Token>(TokenType.SET_KW,
    								TokenType.SEQUENCE_KW), constraintParser,
    						TokenType.OF_KW, typeOrNamedTypeParser)).parse();

    		if (rule != null) {
    			Type type;

    			switch (((Token) rule.get(0)).getType()) {
    			case SET_KW:
    				type = new SetOfType((Type) rule.get(3));
    				break;
    			case SEQUENCE_KW:
    				type = new SequenceOfType((Type) rule.get(3));
    				break;
    			default:
    				throw new ParserException("Implementation error");
    			}

    			type.setConstraints(Arrays.asList((Constraint) rule.get(1)));

    			return type;
    		}

    		return null;
    	}

    }

    // ValueAssignment ::= valuereference Type "::=" Value
    // ObjectAssignment ::= objectreference DefinedObjectClass "::=" Object
    protected class ValueAssignmentParser implements
    		RuleParser<ValueOrObjectAssignmentNode<?, ?>> {

    	@SuppressWarnings("unchecked")
    	public ValueOrObjectAssignmentNode<?, ?> parse() throws ParserException {
    		List<Object> rule = new SequenceParser(new SingleTokenParser(
    				TokenType.Identifier), new ChoiceParser<Node>(typeParser,
    				usefulObjectClassReferenceParser), TokenType.Assign,
    				new ChoiceParser<Node>(valueParser, objectParser)).parse();

    		if (rule != null) {
    			String reference = ((Token) rule.get(0)).getText();
    			Node type = (Node) rule.get(1);
    			Node value = (Node) rule.get(3);

    			if (type instanceof Type) {
    				if (type instanceof SimpleDefinedType) {
    					if (type instanceof UsefulType) {
    						return new ValueAssignmentNode(reference,
    								(Type) type, (Value) value);
    					} else {
    						return new ValueOrObjectAssignmentNode<Node, Node>(
    								((Token) rule.get(0)).getText(), type,
    								value);
    					}
    				} else {
    					return new ValueAssignmentNode(reference, (Type) type,
    							(Value) value);
    				}
    			} else if (type instanceof ObjectClassReferenceNode) {
    				return new ObjectAssignmentNode(reference,
    						(ObjectClassReferenceNode) type, (ObjectNode) value);
    			}

    			return new ValueOrObjectAssignmentNode<Node, Node>(reference,
    					type, value);

    		}

    		return null;
    	}

    }

    // ValueSetTypeAssignment ::= typereference Type "::=" ValueSet
    // ObjectSetAssignment ::= objectsetreference DefinedObjectClass "::="
    // ObjectSet
    protected class ValueSetTypeAssignmentParser implements
    		RuleParser<ValueSetTypeOrObjectSetAssignmentNode> {

    	@SuppressWarnings("unchecked")
    	public ValueSetTypeOrObjectSetAssignmentNode parse()
    			throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.TypeReference,
    				new ChoiceParser<>(typeParser, usefulObjectClassReferenceParser),
    				TokenType.Assign, new ChoiceParser<Node>(valueSetParser, objectSetParser)).parse();

    		if (rule != null) {
    			return new ValueSetTypeOrObjectSetAssignmentNode(
    					((Token) rule.get(0)).getText(), (Node) rule.get(1), (SetSpecsNode) rule.get(3));
    		}

    		return null;
    	}

    }

    // ValueSet ::= "{" ElementSetSpecs "}"
    protected class ValueSetParser implements RuleParser<ElementSetSpecsNode> {

    	public ElementSetSpecsNode parse() throws ParserException {
    		mark();

    		try {
    			SetSpecsNode rule = setParser.parse();

    			if (rule != null) {
    				ElementSetSpecsNode specs = rule.toElementSetSpecs();

    				if (specs != null) {
    					clearMark();
    					return specs;
    				}
    			}

    			resetToMark();
    		} catch (ParserException e) {
    			resetToMark();
    		}

    		return null;
    	}

    }

    // ObjectSet ::= "{" ObjectSetSpec "}"
    protected class ObjectSetParser implements RuleParser<ObjectSetSpecNode> {

    	public ObjectSetSpecNode parse() throws ParserException {
    		mark();

    		try {
    			SetSpecsNode rule = setParser.parse();

    			if (rule != null) {
    				ObjectSetSpecNode specs = rule.toObjectSetSpec();

    				if (specs != null) {
    					clearMark();
    					return specs;
    				}
    			}

    			resetToMark();
    		} catch (ParserException e) {
    			resetToMark();
    		}

    		return null;
    	}

    }

    // SetSpec ::= ElementSetSpecs | ObjectSetSpec
    protected class SetParser implements RuleParser<SetSpecsNode> {

    	public SetSpecsNode parse() throws ParserException {
    		return new ValueExtractor<SetSpecsNode>(1, new SequenceParser(
    				TokenType.LBrace, setSpecsParser, TokenType.RBrace))
    				.parse();
    	}

    }

    // ElementSetSpecs ::=
    // RootElementSetSpec
    // | RootElementSetSpec "," "..."
    // | RootElementSetSpec "," "..." "," AdditionalElementSetSpec
    // ObjectSetSpec ::=
    // RootElementSetSpec
    // | RootElementSetSpec "," "..."
    // | "..."
    // | "..." "," AdditionalElementSetSpec
    // | RootElementSetSpec "," "..." "," AdditionalElementSetSpec
    // RootElementSetSpec ::= ElementSetSpec
    // AdditionalElementSetSpec ::= ElementSetSpec
    protected class SetSpecsParser implements RuleParser<SetSpecsNode> {

    	@SuppressWarnings("unchecked")
    	public SetSpecsNode parse() throws ParserException {
    		Object rule = new ChoiceParser<Object>(new SequenceParser(
    				elementSetSpecParser, TokenType.Comma, TokenType.Ellipsis,
    				TokenType.Comma, elementSetSpecParser), new SequenceParser(
    				elementSetSpecParser, TokenType.Comma, TokenType.Ellipsis),
    				elementSetSpecParser, new SequenceParser(
    						TokenType.Ellipsis, TokenType.Comma,
    						elementSetSpecParser), new SingleTokenParser(
    						TokenType.Ellipsis)).parse();

    		if (rule != null) {
    			if (rule instanceof List) {
    				List<Object> ruleList = (List<Object>) rule;
    				switch (ruleList.size()) {
    				case 3:
    					if (ruleList.get(2) instanceof Token) {
    						return new SetSpecsNode(
    								(ElementSet) ruleList.get(0), true);
    					} else {
    						return new SetSpecsNode(null, true,
    								(ElementSet) ruleList.get(2));
    					}

    				case 5:
    					return new SetSpecsNode((ElementSet) ruleList.get(0),
    							true, (ElementSet) ruleList.get(4));
    				}

    			} else {
    				if (rule instanceof Token) {
    					return new SetSpecsNode(true);
    				} else {
    					return new SetSpecsNode((ElementSet) rule);
    				}
    			}
    		}

    		return null;
    	}

    }

    // ElementSetSpec ::= Unions
    // | ALL Exclusions
    protected class ElementSetSpecParser implements RuleParser<ElementSet> {

    	@SuppressWarnings("unchecked")
    	public ElementSet parse() throws ParserException {
    		Object rule = new ChoiceParser<Object>(new SequenceParser(
    				TokenType.ALL_KW, exclusionsParser), unionsParser).parse();

    		if (rule != null) {
    			if (rule instanceof List) {
    				return new ElementSet(OpType.All,
    						((ElementSet) ((List<ElementSet>) rule).get(1)));
    			} else {
    				return (ElementSet) rule;
    			}
    		}

    		return null;
    	}

    }

    // Unions ::= Intersections
    // | UElems UnionMark Intersections
    // UElems ::= Unions
    protected class UnionsParser implements RuleParser<ElementSet> {

    	public ElementSet parse() throws ParserException {
    		List<Elements> elements = new TokenSeparatedRuleParser<Elements>(
    				intersectionsParser, TokenType.Pipe, TokenType.UNION_KW)
    				.parse();

    		if (elements != null) {
    			return new ElementSet(OpType.Union,
    					elements.toArray(new Elements[] {}));
    		}

    		return null;
    	}

    }

    // Intersections ::= IntersectionElements
    // | IElems IntersectionMark IntersectionElements
    // IElems ::= Intersections
    protected class IntersectionsParser implements RuleParser<ElementSet> {

    	public ElementSet parse() throws ParserException {
    		List<Elements> elements = new TokenSeparatedRuleParser<Elements>(
    				intersectionElementsParser, TokenType.Circumflex,
    				TokenType.INTERSECTION_KW).parse();

    		if (elements != null) {
    			return new ElementSet(OpType.Intersection,
    					elements.toArray(new Elements[] {}));
    		}

    		return null;
    	}

    }

    // IntersectionElements ::= Elements | Elems Exclusions
    // Elems ::= Elements
    protected class IntersectionElementsParser implements RuleParser<Elements> {

    	public Elements parse() throws ParserException {
    		List<Object> rule = new SequenceParser(
    				new boolean[] { true, false }, elementsParser,
    				exclusionsParser).parse();

    		if (rule != null) {
    			if (rule.get(1) == null) {
    				return (Elements) rule.get(0);
    			} else {
    				return new ElementSet(OpType.Exclude,
    						(Elements) rule.get(0), ((ElementSet) rule.get(1))
    								.getOperands().get(0));
    			}
    		}

    		return null;
    	}

    }

    // Exclusions ::= EXCEPT Elements
    protected class ExclusionsParser implements RuleParser<ElementSet> {

    	public ElementSet parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.EXCEPT_KW,
    				elementsParser).parse();

    		if (rule != null) {
    			return new ElementSet(OpType.Exclude, (Elements) rule.get(1));
    		}

    		return null;
    	}

    }

    // Elements ::=
    // SubtypeElements
    // | ObjectSetElements
    // | "(" ElementSetSpec ")"
    protected class ElementsParser implements RuleParser<Elements> {

    	@SuppressWarnings("unchecked")
    	public Elements parse() throws ParserException {
    		Object rule = new ChoiceParser<Object>(subtypeElementsParser,
    				objectSetElementsParser, new SequenceParser(
    						TokenType.LParen, elementSetSpecParser,
    						TokenType.RParen)).parse();

    		if (rule != null) {
    			if (rule instanceof List) {
    				return (ElementSet) ((List<Object>) rule).get(1);
    			} else {
    				return (Elements) rule;
    			}
    		}

    		return null;
    	}

    }

    // SubtypeElements ::=
    // SingleValue
    // | ContainedSubtype
    // | ValueRange
    // | PermittedAlphabet
    // | SizeConstraint
    // | TypeConstraint
    // | InnerTypeConstraints
    // | PatternConstraint
    // | PropertySettings
    // | DurationRange
    // | TimePointRange
    // | RecurrenceRange
    protected class SubtypeElementsParser implements RuleParser<Constraint> {

    	@SuppressWarnings("unchecked")
    	public Constraint parse() throws ParserException {
    		return new ChoiceParser<>(containedSubtypeParser,
    				valueRangeParser, permittedAlphabetParser,
    				sizeConstraintParser, typeConstraintParser,
    				innerTypeConstraintsParser, patternConstraintParser,
    				propertySettingsParser, durationRangeParser,
    				timePointRangeParser, recurrenceRangeParser,
    				singleValueParser).parse();
    	}

    }

    // SingleValue ::= Value
    protected class SingleValueParser implements
    		RuleParser<SingleValueConstraint> {

    	public SingleValueConstraint parse() throws ParserException {
    		Value value = valueParser.parse();

    		if (value != null) {
    			return new SingleValueConstraint(value);
    		}

    		return null;
    	}

    }

    // ContainedSubtype ::= Includes Type
    // Includes ::= INCLUDES | empty
    protected class ContainedSubtypeParser implements
    		RuleParser<ContainedSubtype> {

    	public ContainedSubtype parse() throws ParserException {
    		List<Object> rule = new SequenceParser(
    				new boolean[] { false, true }, TokenType.INCLUDES_KW,
    				typeParser).parse();

    		if (rule != null) {
    			if (rule.get(0) == null) {
    				if (rule.get(1) instanceof Null) {
    					throw new ParserException("NULL not allowed");
    				}
    				return new ContainedSubtype((Type) rule.get(1), false);
    			} else {
    				return new ContainedSubtype((Type) rule.get(1), true);
    			}
    		}

    		return null;
    	}

    }

    // ValueRange ::= LowerEndpoint ".." UpperEndpoint
    protected class ValueRangeParser implements RuleParser<RangeNode> {

    	public RangeNode parse() throws ParserException {
    		List<Object> rule = new SequenceParser(lowerEndpointParser,
    				TokenType.Range, upperEndpointParser).parse();

    		if (rule != null) {
    			return new RangeNode((LowerEndpointNode) rule.get(0),
    					(UpperEndpointNode) rule.get(2));
    		}

    		return null;
    	}

    }

    // LowerEndpoint ::= LowerEndValue | LowerEndValue "<"
    protected class LowerEndpointParser implements
    		RuleParser<LowerEndpointNode> {

    	public LowerEndpointNode parse() throws ParserException {
    		List<Object> rule = new SequenceParser(
    				new boolean[] { true, false }, lowerEndValueParser,
    				TokenType.LT).parse();

    		if (rule != null) {
    			if (rule.get(1) == null) {
    				return new LowerEndpointNode((Value) rule.get(0));
    			} else {
    				return new LowerEndpointNode((Value) rule.get(0), false);
    			}
    		}

    		return null;
    	}

    }

    // UpperEndpoint ::= UpperEndValue | "<" UpperEndValue
    protected class UpperEndpointParser implements
    		RuleParser<UpperEndpointNode> {

    	public UpperEndpointNode parse() throws ParserException {
    		List<Object> rule = new SequenceParser(
    				new boolean[] { false, true }, TokenType.LT,
    				upperEndValueParser).parse();

    		if (rule != null) {
    			if (rule.get(0) == null) {
    				return new UpperEndpointNode((Value) rule.get(1));
    			} else {
    				return new UpperEndpointNode((Value) rule.get(1), false);
    			}
    		}

    		return null;
    	}

    }

    // LowerEndValue ::= Value | MIN
    protected class LowerEndValueParser implements RuleParser<Value> {

    	@SuppressWarnings("unchecked")
    	public Value parse() throws ParserException {
    		Object rule = new ChoiceParser<>(valueParser, new SingleTokenParser(TokenType.MIN_KW)).parse();

    		if (rule != null) {
    			if (rule instanceof Token) {
    				return Value.MIN;
    			}
    			return (Value) rule;
    		}

    		return null;
    	}

    }

    // UpperEndValue ::= Value | MAX
    protected class UpperEndValueParser implements RuleParser<Value> {

    	@SuppressWarnings("unchecked")
    	public Value parse() throws ParserException {
    		Object rule = new ChoiceParser<>(valueParser, new SingleTokenParser(TokenType.MAX_KW)).parse();

    		if (rule != null) {
    			if (rule instanceof Token) {
    				return Value.MAX;
    			}
    			return (Value) rule;
    		}

    		return null;
    	}

    }

    // SizeConstraint ::= SIZE Constraint
    protected class SizeConstraintParser implements RuleParser<SizeConstraint> {

    	public SizeConstraint parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.SIZE_KW,
    				constraintParser).parse();

    		if (rule != null) {
    			return new SizeConstraint((Constraint) rule.get(1));
    		}

    		return null;
    	}

    }

    // TypeConstraint ::= Type
    protected class TypeConstraintParser implements RuleParser<TypeConstraint> {

    	public TypeConstraint parse() throws ParserException {
    		Object type = typeParser.parse();

    		// TODO: only applicable to ObjectClassFieldType (X.680 3.8.57 NOTE
    		// 3)

    		if (type != null) {
    			return new TypeConstraint((Type) type);
    		}

    		return null;
    	}

    }

    // PermittedAlphabet ::= FROM Constraint
    protected class PermittedAlphabetParser implements
    		RuleParser<PermittedAlphabetConstraint> {

    	public PermittedAlphabetConstraint parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.FROM_KW,
    				constraintParser).parse();

    		if (rule != null) {
    			return new PermittedAlphabetConstraint((Constraint) rule.get(1));
    		}

    		return null;
    	}

    }

    // InnerTypeConstraints ::=
    // WITH COMPONENT SingleTypeConstraint
    // | WITH COMPONENTS MultipleTypeConstraints
    // SingleTypeConstraint ::= Constraint
    protected class InnerTypeConstraintsParser implements
    		RuleParser<Constraint> {

    	@SuppressWarnings("unchecked")
    	public Constraint parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.WITH_KW,
    				new ChoiceParser<Object>(new SequenceParser(
    						TokenType.COMPONENT_KW, constraintParser),
    						new SequenceParser(TokenType.COMPONENTS_KW,
    								multipleTypeConstraintsParser))).parse();

    		if (rule != null) {
    			rule = (List<Object>) rule.get(1);

    			if (rule.get(0) instanceof Token
    					&& TokenType.COMPONENT_KW.equals(((Token) rule.get(0))
    							.getType())) {
    				return (Constraint) rule.get(1);
    			} else {
    				return (MultipleTypeConstraints) rule.get(1);
    			}
    		}

    		return null;
    	}

    }

    // MultipleTypeConstraints ::=
    // FullSpecification
    // | PartialSpecification
    protected class MultipleTypeConstraintsParser implements
    		RuleParser<MultipleTypeConstraints> {

    	@SuppressWarnings("unchecked")
    	public MultipleTypeConstraints parse() throws ParserException {
    		return new ChoiceParser<MultipleTypeConstraints>(
    				fullSpecificationParser, partialSpecificationParser)
    				.parse();
    	}

    }

    // FullSpecification ::= "{" TypeConstraints "}"
    protected class FullSpecificationParser implements
    		RuleParser<MultipleTypeConstraints> {

    	@SuppressWarnings("unchecked")
    	public MultipleTypeConstraints parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.LBrace,
    				typeConstraintsParser, TokenType.RBrace).parse();

    		if (rule != null) {
    			return new MultipleTypeConstraints(
    					(List<NamedConstraint>) rule.get(1), false);
    		}

    		return null;
    	}

    }

    // PartialSpecification ::= "{" "..." "," TypeConstraints "}"
    protected class PartialSpecificationParser implements
    		RuleParser<MultipleTypeConstraints> {

    	@SuppressWarnings("unchecked")
    	public MultipleTypeConstraints parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.LBrace,
    				TokenType.Ellipsis, TokenType.Comma, typeConstraintsParser,
    				TokenType.RBrace).parse();

    		if (rule != null) {
    			return new MultipleTypeConstraints(
    					(List<NamedConstraint>) rule.get(3), true);
    		}

    		return null;
    	}

    }

    // TypeConstraints ::=
    // NamedConstraint
    // | NamedConstraint "," TypeConstraints
    protected class TypeConstraintsParser implements
    		RuleParser<List<NamedConstraint>> {

    	public List<NamedConstraint> parse() throws ParserException {
    		return new CommaSeparatedRuleParser<NamedConstraint>(
    				namedConstraintParser).parse();
    	}

    }

    // NamedConstraint ::=
    // identifier ComponentConstraint
    protected class NamedConstraintParser implements
    		RuleParser<NamedConstraint> {

    	public NamedConstraint parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.Identifier,
    				componentConstraintParser).parse();

    		if (rule != null) {
    			return new NamedConstraint(((Token) rule.get(0)).getText(),
    					(Constraint) rule.get(1));
    		}

    		return null;
    	}

    }

    // ComponentConstraint ::= ValueConstraint PresenceConstraint
    protected class ComponentConstraintParser implements
    		RuleParser<ComponentConstraint> {

    	public ComponentConstraint parse() throws ParserException {
    		List<Object> rule = new SequenceParser(
    				new boolean[] { false, false }, valueConstraintParser,
    				presenceConstraintParser).parse();
    		return new ComponentConstraint((Constraint) rule.get(0),
    				(PresenceConstraint) rule.get(1));
    	}

    }

    // ValueConstraint ::= Constraint | empty
    protected class ValueConstraintParser implements RuleParser<Constraint> {

    	public Constraint parse() throws ParserException {
    		Constraint rule = constraintParser.parse();

    		if (rule != null) {
    			return new ValueConstraint(rule);
    		}

    		return null;
    	}

    }

    // PresenceConstraint ::= PRESENT | ABSENT | OPTIONAL | empty
    protected class PresenceConstraintParser implements
    		RuleParser<PresenceConstraint> {

    	public PresenceConstraint parse() throws ParserException {
    		Token rule = new ChoiceParser<Token>(TokenType.PRESENT_KW,
    				TokenType.ABSENT_KW, TokenType.OPTIONAL_KW).parse();

    		if (rule != null) {
    			switch (rule.type) {
    			case PRESENT_KW:
    				return new PresenceConstraint(
    						PresenceConstraint.Type.Present);
    			case ABSENT_KW:
    				return new PresenceConstraint(
    						PresenceConstraint.Type.Absent);
    			case OPTIONAL_KW:
    				return new PresenceConstraint(
    						PresenceConstraint.Type.Optional);
    			}
    		}

    		return null;
    	}

    }

    // PatternConstraint ::= PATTERN Value
    protected class PatternConstraintParser implements
    		RuleParser<PatternConstraint> {

    	public PatternConstraint parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.PATTERN_KW, valueParser).parse();

    		if (rule != null) {
    		    Value value = (Value) rule.get(1);

                if (value instanceof AmbiguousValue && ((AmbiguousValue) value).getValue(DefinedValue.class) != null) {
                    value = ((AmbiguousValue) value).getValue(DefinedValue.class);
                }

    			return new PatternConstraint(value);
    		}

    		return null;
    	}

    }

    // PropertySettings ::= SETTINGS simplestring
    // simplestring ::= "\"" PropertySettingsList "\""
    // PropertySettingsList ::=
    // PropertyAndSettingPair
    // | PropertySettingsList PropertyAndSettingPair
    protected class PropertySettingsParser implements
    		RuleParser<PropertySettingsConstraint> {

    	@SuppressWarnings("unchecked")
    	public PropertySettingsConstraint parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.SETTINGS_KW,
    				new SingleTokenParser(TokenType.Quotation,
    						Context.PropertySettings),
    				new RepetitionParser<PropertyAndSettingNode>(
    						propertyAndSettingPairParser),
    				new SingleTokenParser(TokenType.Quotation,
    						Context.PropertySettings)).parse();

    		if (rule != null) {
    			return new PropertySettingsConstraint(
    					((List<PropertyAndSettingNode>) rule.get(2)));
    		}

    		return null;
    	}

    }

    // PropertyAndSettingPair ::= PropertyName "=" SettingName
    // PropertyName ::= psname
    // SettingName ::= psname
    protected class PropertyAndSettingPairParser implements
    		RuleParser<PropertyAndSettingNode> {

    	@SuppressWarnings("serial")
    	private final Map<String, Set<String>> PROPERTIES = new HashMap<String, Set<String>>() {
    		{
    			put("Basic",
    					new HashSet<String>(Arrays.asList("Date", "Time",
    							"Date-Time", "Interval", "Rec-Interval")));
    			put("Date",
    					new HashSet<String>(Arrays.asList("C", "Y", "YM",
    							"YMD", "YD", "YW", "YWD")));
    			put("Year",
    					new HashSet<String>(Arrays.asList("Basic", "Proleptic",
    							"Negative")));
    			put("Time",
    					new HashSet<String>(Arrays.asList("H", "HM", "HMS")));
    			put("Local-or-UTC",
    					new HashSet<String>(Arrays.asList("L", "Z", "LD")));
    			put("Interval-type",
    					new HashSet<String>(Arrays
    							.asList("SE", "D", "SD", "DE")));
    			put("SE-point",
    					new HashSet<String>(Arrays.asList("Date", "Time",
    							"Date-Time")));
    			put("Recurrence",
    					new HashSet<String>(Arrays.asList("Unlimited")));
    			put("Midnight",
    					new HashSet<String>(Arrays.asList("Start", "End")));
    		}
    	};

    	public PropertyAndSettingNode parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.TypeReference,
    				TokenType.Equals, TokenType.TypeReference).parse();

    		if (rule != null) {
    			Token propertyToken = ((Token) rule.get(0));
    			Token settingToken = ((Token) rule.get(2));
    			String property = propertyToken.getText();
    			String setting = settingToken.getText();

    			Set<String> settings = PROPERTIES.get(property);

    			if (settings == null) {
    				setException(
    						String.format("Invalid property '%s'", property),
    						propertyToken);
    				return null;
    			}

    			if (!settings.contains(setting)) {
    				if ("Year".equals(property)
    						&& Pattern.matches("L[567][0-9]*", setting)) {
    					;
    				} else if ("Time".equals(property)
    						&& Pattern.matches("HM?S?F[1-9][0-9]*", setting)) {
    					;
    				} else if ("Recurrence".equals(property)
    						&& Pattern.matches("R[1-9][0-9]*", setting)) {
    					;
    				} else {
    					setException(String.format(
    							"Invalid setting '%s' for property '%s'",
    							setting, property), settingToken);
    					return null;
    				}
    			}

    			return new PropertyAndSettingNode(property, setting);
    		}

    		return null;
    	}

    }

    // DurationRange ::= ValueRange
    protected class DurationRangeParser implements RuleParser<RangeNode> {

    	public RangeNode parse() throws ParserException {
    		// TODO: check validity
    		return valueRangeParser.parse();
    	}

    }

    // TimePointRange ::= ValueRange
    protected class TimePointRangeParser implements RuleParser<RangeNode> {

    	public RangeNode parse() throws ParserException {
    		// TODO: check validity
    		return valueRangeParser.parse();
    	}

    }

    // RecurrenceRange ::= ValueRange
    protected class RecurrenceRangeParser implements RuleParser<RangeNode> {

    	public RangeNode parse() throws ParserException {
    		// TODO: check validity
    		return valueRangeParser.parse();
    	}

    }

    // Constraint ::= "(" ConstraintSpec ExceptionSpec ")"
    protected class ConstraintParser implements RuleParser<Constraint> {

    	public Constraint parse() throws ParserException {
    		List<Object> rule = new SequenceParser(new boolean[] { true, true,
    				false, true }, TokenType.LParen, constraintSpecParser,
    				exceptionSpecParser, TokenType.RParen).parse();

    		if (rule != null) {
    			Constraint constraint = (Constraint) rule.get(1);

    			if (rule.get(2) != null) {
    				constraint
    						.setExceptionSpec((ExceptionIdentificationNode) rule
    								.get(2));
    			}

    			return constraint;
    		}

    		return null;
    	}

    }

    // ConstraintSpec ::= SubtypeConstraint
    // | GeneralConstraint
    protected class ConstraintSpecParser implements RuleParser<Constraint> {

    	@SuppressWarnings("unchecked")
    	public Constraint parse() throws ParserException {
    		return new ChoiceParser<Constraint>(generalConstraintParser,
    				subtypeConstraintParser).parse();
    	}

    }

    // SubtypeConstraint ::= ElementSetSpecs
    protected class SubtypeConstraintParser implements
    		RuleParser<SubtypeConstraint> {

    	public SubtypeConstraint parse() throws ParserException {
    		SetSpecsNode rule = setSpecsParser.parse();

    		if (rule != null) {
    			ElementSetSpecsNode specs = rule.toElementSetSpecs();
    			return new SubtypeConstraint(specs);
    		}

    		return null;
    	}

    }

    // DefinedObjectClass ::=
    // ExternalObjectClassReference | objectclassreference |
    // UsefulObjectClassReference
    protected class DefinedObjectClassParser implements
    		RuleParser<ObjectClassReferenceNode> {

    	@SuppressWarnings("unchecked")
    	public ObjectClassReferenceNode parse() throws ParserException {
    		Object rule = new ChoiceParser<Object>(
    				externalObjectClassReferenceParser,
    				new SingleTokenParser(TokenType.ObjectClassReference,
    						Context.ObjectClass),
    				usefulObjectClassReferenceParser).parse();

    		if (rule != null) {
    			if (rule instanceof Token) {
    				return new ObjectClassReferenceNode(
    						((Token) rule).getText());
    			} else {
    				return (ObjectClassReferenceNode) rule;
    			}
    		}

    		return null;
    	}

    }

    // ExternalObjectClassReference ::= modulereference "." objectclassreference
    protected class ExternalObjectClassReferenceParser implements
    		RuleParser<ExternalObjectClassReferenceNode> {

    	public ExternalObjectClassReferenceNode parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.TypeReference,
    				TokenType.Dot,
    				new SingleTokenParser(TokenType.ObjectClassReference,
    						Context.ObjectClass)).parse();

    		if (rule != null) {
    			return new ExternalObjectClassReferenceNode(
    					((Token) rule.get(0)).getText(),
    					((Token) rule.get(2)).getText());
    		}

    		return null;
    	}

    }

    // UsefulObjectClassReference ::=
    // TYPE-IDENTIFIER
    // | ABSTRACT-SYNTAX
    protected class UsefulObjectClassReferenceParser implements
    		RuleParser<ObjectClassReferenceNode> {

    	public ObjectClassReferenceNode parse() throws ParserException {
    		Token rule = new ChoiceParser<Token>(TokenType.TYPE_IDENTIFIER_KW,
    				TokenType.ABSTRACT_SYNTAX_KW).parse();

    		if (rule != null) {
    			if (rule.type == TokenType.TYPE_IDENTIFIER_KW) {
    				return new TypeIdentifierObjectClassReferenceNode();
    			} else {
    				return new AbstractSyntaxObjectClassReferenceNode();
    			}
    		}

    		return null;
    	}

    }

    // ObjectClass ::= DefinedObjectClass | ObjectClassDefn |
    // ParameterizedObjectClass
    protected class ObjectClassParser implements RuleParser<ObjectClassNode> {

    	@SuppressWarnings("unchecked")
    	public ObjectClassNode parse() throws ParserException {
    		return new ChoiceParser<ObjectClassNode>(
    				parameterizedObjectClassParser, definedObjectClassParser,
    				objectClassDefnParser).parse();
    	}

    }

    // ObjectClassDefn ::= CLASS "{" FieldSpec "," + "}" WithSyntaxSpec?
    protected class ObjectClassDefnParser implements
    		RuleParser<ObjectClassDefn> {

    	@SuppressWarnings("unchecked")
    	public ObjectClassDefn parse() throws ParserException {
    		List<Object> rule = new SequenceParser(new boolean[] { true, true,
    				true, true, false }, TokenType.CLASS_KW, TokenType.LBrace,
    				new CommaSeparatedRuleParser<AbstractASN1FieldSpecNode>(
    						fieldSpecParser), TokenType.RBrace,
    				withSyntaxSpecParser).parse();

    		if (rule != null) {
    			return new ObjectClassDefn(
    					(List<AbstractASN1FieldSpecNode>) rule.get(2),
    					(List<Object>) rule.get(4));
    		}

    		return null;
    	}

    }

    // FieldSpec ::=
    // TypeFieldSpec
    // | FixedTypeValueFieldSpec
    // | VariableTypeValueFieldSpec
    // | FixedTypeValueSetFieldSpec
    // | VariableTypeValueSetFieldSpec
    // | ObjectFieldSpec
    // | ObjectSetFieldSpec
    protected class FieldSpecParser implements
    		RuleParser<AbstractASN1FieldSpecNode> {

    	@SuppressWarnings("unchecked")
    	public AbstractASN1FieldSpecNode parse() throws ParserException {
    		return new ChoiceParser<AbstractASN1FieldSpecNode>(
    				fixedTypeValueFieldSpecParser,
    				variableTypeValueFieldSpecParser, setFieldSpecParser,
    				variableTypeValueSetFieldSpecParser, typeFieldSpecParser)
    				.parse();
    	}

    }

    // PrimitiveFieldName ::=
    // typefieldreference
    // | valuefieldreference
    // | valuesetfieldreference
    // | objectfieldreference
    // | objectsetfieldreference
    protected class PrimitiveFieldNameParser implements
    		RuleParser<PrimitiveFieldNameNode> {

    	@SuppressWarnings("unchecked")
    	public PrimitiveFieldNameNode parse() throws ParserException {
    		Token rule = new ChoiceParser<Token>(new SingleTokenParser(
    				TokenType.TypeFieldReference, Context.TypeField),
    				new SingleTokenParser(TokenType.ValueFieldReference,
    						Context.ValueField)).parse();

    		if (rule != null) {
    			switch (rule.getType()) {
    			case TypeFieldReference:
    				return new PrimitiveFieldNameNode(rule.getText().substring(
    						1), TokenType.TypeFieldReference);
    			case ValueFieldReference:
    				return new PrimitiveFieldNameNode(rule.getText().substring(
    						1), TokenType.ValueFieldReference);
    			}
    		}

    		return null;
    	}

    }

    // FieldName ::= PrimitiveFieldName "." +
    protected class FieldNameParser implements RuleParser<FieldNameNode> {

    	public FieldNameNode parse() throws ParserException {
    		List<PrimitiveFieldNameNode> rule = new TokenSeparatedRuleParser<PrimitiveFieldNameNode>(
    				primitiveFieldNameParser, TokenType.Dot).parse();

    		if (rule != null) {
    			return new FieldNameNode(rule);
    		}

    		return null;
    	}

    }

    // TypeFieldSpec ::= typefieldreference TypeOptionalitySpec?
    protected class TypeFieldSpecParser implements
    		RuleParser<TypeFieldSpecNode> {

    	public TypeFieldSpecNode parse() throws ParserException {
    		List<Object> rule = new SequenceParser(new boolean[] { true, true,
    				false }, TokenType.Ampersand, TokenType.TypeReference,
    				typeOptionalitySpecParser).parse();

    		if (rule != null) {
    			return new TypeFieldSpecNode(((Token) rule.get(1)).getText(),
    					(OptionalitySpecNode) rule.get(2));
    		}

    		return null;
    	}

    }

    // TypeOptionalitySpec ::= OPTIONAL | DEFAULT Type
    protected class TypeOptionalitySpecParser implements
    		RuleParser<OptionalitySpecNode> {

    	@SuppressWarnings("unchecked")
    	public OptionalitySpecNode parse() throws ParserException {
    		Object rule = new ChoiceParser<Object>(new SingleTokenParser(
    				TokenType.OPTIONAL_KW), new SequenceParser(
    				TokenType.DEFAULT_KW, typeParser)).parse();

    		if (rule != null) {
    			if (rule instanceof List) {
    				return new DefaultTypeSpecNode(
    						(Type) ((List<?>) rule).get(1));
    			} else {
    				return new OptionalSpecNode();
    			}
    		}

    		return null;
    	}

    }

    // FixedTypeValueFieldSpec ::= valuefieldreference Type UNIQUE ?
    // ValueOptionalitySpec ?
    // ObjectFieldSpec ::= objectfieldreference DefinedObjectClass
    // ObjectOptionalitySpec?
    protected class FixedTypeValueOrObjectFieldSpecParser implements
    		RuleParser<FieldSpecNode> {

    	@SuppressWarnings("unchecked")
    	public FieldSpecNode parse() throws ParserException {
    		List<Object> rule = new SequenceParser(new boolean[] { true, true,
    				true, false, false }, TokenType.Ampersand,
    				TokenType.Identifier, new ChoiceParser<Node>(typeParser,
    						usefulObjectClassReferenceParser),
    				TokenType.UNIQUE_KW, optionalitySpecParser).parse();

    		if (rule != null) {
    			boolean unique = rule.get(3) != null;
    			OptionalitySpecNode spec = (OptionalitySpecNode) rule.get(4);

    			if (unique && spec != null
    					&& !(spec instanceof OptionalSpecNode)) {
    				return null;
    			}

    			return new FieldSpecNode(
    					(String) ((Token) rule.get(1)).getText(),
    					(Node) rule.get(2), unique, spec);
    		}

    		return null;
    	}

    }

    // ValueOptionalitySpec ::= OPTIONAL | DEFAULT Value
    protected class ValueOptionalitySpecParser implements
    		RuleParser<OptionalitySpecNode> {

    	public OptionalitySpecNode parse() throws ParserException {
    		mark();

    		try {
    			OptionalitySpecNode rule = optionalitySpecParser.parse();

    			if (rule != null) {
    				if (rule instanceof DefaultSpecNode) {
    					DefaultValueSpecNode spec = ((DefaultSpecNode) rule)
    							.toDefaultValueSpec();

    					if (spec != null) {
    						clearMark();
    						return spec;
    					}
    				} else {
    					clearMark();
    					return rule;
    				}
    			}

    			resetToMark();
    		} catch (ParserException e) {
    			resetToMark();
    		}

    		return null;
    	}

    }

    // OptionalitySpec :: =
    // OPTIONAL
    // | DEFAULT Value
    // | DEFAULT Object
    //
    // Merges the following productions:
    // ValueOptionalitySpec ::= OPTIONAL | DEFAULT Value
    // ObjectOptionalitySpec ::= OPTIONAL | DEFAULT Object
    protected class OptionalitySpecParser implements
    		RuleParser<OptionalitySpecNode> {

    	@SuppressWarnings("unchecked")
    	public OptionalitySpecNode parse() throws ParserException {
    		Object rule = new ChoiceParser<>(new SingleTokenParser(
    				TokenType.OPTIONAL_KW), new SequenceParser(
    				TokenType.DEFAULT_KW, new ChoiceParser<>(valueParser, objectParser))).parse();

    		if (rule != null) {
    			if (rule instanceof List) {
    				return new DefaultSpecNode((Node) ((List<?>) rule).get(1));
    			} else {
    				return new OptionalSpecNode();
    			}
    		}

    		return null;
    	}

    }

    // VariableTypeValueFieldSpec ::= valuefieldreference FieldName
    // ValueOptionalitySpec ?
    protected class VariableTypeValueFieldSpecParser implements
    		RuleParser<VariableTypeValueFieldSpecNode> {

    	public VariableTypeValueFieldSpecNode parse() throws ParserException {
    		List<Object> rule = new SequenceParser(new boolean[] { true, true,
    				true, false }, TokenType.Ampersand, TokenType.Identifier,
    				fieldNameParser, valueOptionalitySpecParser).parse();

    		if (rule != null) {
    			return new VariableTypeValueFieldSpecNode(
    					((Token) rule.get(1)).getText(),
    					(FieldNameNode) rule.get(2),
    					(OptionalitySpecNode) rule.get(3));
    		}

    		return null;
    	}

    }

    // FixedTypeValueSetFieldSpec ::= valuesetfieldreference Type
    // ValueSetOptionalitySpec ?
    // ObjectSetFieldSpec ::= objectsetfieldreference DefinedObjectClass
    // ObjectSetOptionalitySpec ?
    protected class SetFieldSpecParser implements RuleParser<SetFieldSpecNode> {

    	@SuppressWarnings("unchecked")
    	public SetFieldSpecNode parse() throws ParserException {
    		List<Object> rule = new SequenceParser(new boolean[] { true, true,
    				true, false }, TokenType.Ampersand,
    				TokenType.TypeReference, new ChoiceParser<Node>(typeParser,
    						usefulObjectClassReferenceParser),
    				setOptionalitySpecParser).parse();

    		if (rule != null) {
    			return new SetFieldSpecNode(((Token) rule.get(1)).getText(),
    					(Node) rule.get(2), (OptionalitySpecNode) rule.get(3));
    		}

    		return null;
    	}

    }

    // ValueSetOptionalitySpec ::= OPTIONAL | DEFAULT ValueSet
    protected class ValueSetOptionalitySpecParser implements
    		RuleParser<OptionalitySpecNode> {

    	public OptionalitySpecNode parse() throws ParserException {
    		mark();

    		try {
    			OptionalitySpecNode rule = setOptionalitySpecParser.parse();

    			if (rule != null) {
    				if (rule instanceof DefaultSetSpecNode) {
    					DefaultValueSetSpecNode spec = ((DefaultSetSpecNode) rule)
    							.toDefaultValueSetOptionalitySpec();

    					if (spec != null) {
    						clearMark();
    						return spec;
    					}
    				} else {
    					clearMark();
    					return rule;
    				}
    			}

    			resetToMark();
    		} catch (ParserException e) {
    			resetToMark();
    		}

    		return null;
    	}

    }

    // ObjectSetOptionalitySpec ::= OPTIONAL | DEFAULT ObjectSet
    protected class ObjectSetOptionalitySpecParser implements
    		RuleParser<OptionalitySpecNode> {

    	public OptionalitySpecNode parse() throws ParserException {
    		mark();

    		try {
    			OptionalitySpecNode rule = setOptionalitySpecParser.parse();

    			if (rule != null) {
    				if (rule instanceof DefaultSetSpecNode) {
    					DefaultObjectSetSpecNode spec = ((DefaultSetSpecNode) rule)
    							.toDefaultObjectSetOptionalitySpec();

    					if (spec != null) {
    						clearMark();
    						return spec;
    					}
    				} else {
    					clearMark();
    					return rule;
    				}
    			}

    			resetToMark();
    		} catch (ParserException e) {
    			resetToMark();
    		}

    		return null;
    	}

    }

    // SetOptionalitySpec :: =
    // OPTIONAL
    // | DEFAULT ValueSet
    // | DEFAULT ObjectSet
    protected class SetOptionalitySpecParser implements
    		RuleParser<OptionalitySpecNode> {

    	@SuppressWarnings("unchecked")
    	public OptionalitySpecNode parse() throws ParserException {
    		Object rule = new ChoiceParser<Object>(new SingleTokenParser(
    				TokenType.OPTIONAL_KW), new SequenceParser(
    				TokenType.DEFAULT_KW, setParser)).parse();

    		if (rule != null) {
    			if (rule instanceof List) {
    				return new DefaultSetSpecNode(
    						(SetSpecsNode) ((List<?>) rule).get(1));
    			} else {
    				return new OptionalSpecNode();
    			}
    		}

    		return null;
    	}

    }

    // VariableTypeValueSetFieldSpec ::= valuesetfieldreference FieldName
    // ValueSetOptionalitySpec?
    protected class VariableTypeValueSetFieldSpecParser implements
    		RuleParser<VariableTypeValueSetFieldSpecNode> {

    	public VariableTypeValueSetFieldSpecNode parse() throws ParserException {
    		List<Object> rule = new SequenceParser(new boolean[] { true, true,
    				true, false }, TokenType.Ampersand,
    				TokenType.TypeReference, fieldNameParser,
    				valueSetOptionalitySpecParser).parse();

    		if (rule != null) {
    			return new VariableTypeValueSetFieldSpecNode(
    					((Token) rule.get(1)).getText(),
    					(FieldNameNode) rule.get(2),
    					(OptionalitySpecNode) rule.get(3));
    		}

    		return null;
    	}

    }

    // WithSyntaxSpec ::= WITH SYNTAX SyntaxList
    protected class WithSyntaxSpecParser implements
    		RuleParser<List<TokenOrGroup>> {

    	public List<TokenOrGroup> parse() throws ParserException {
    		return new ValueExtractor<List<TokenOrGroup>>(2,
    				new SequenceParser(TokenType.WITH_KW, TokenType.SYNTAX_KW,
    						syntaxListParser)).parse();
    	}

    }

    // SyntaxList ::= "{" TokenOrGroupSpec empty + "}"
    protected class SyntaxListParser implements RuleParser<List<TokenOrGroup>> {

    	public List<TokenOrGroup> parse() throws ParserException {
    		return new ValueExtractor<List<TokenOrGroup>>(1,
    				new SequenceParser(TokenType.LBrace,
    						new RepetitionParser<TokenOrGroup>(
    								tokenOrGroupSpecParser), TokenType.RBrace))
    				.parse();
    	}

    }

    // TokenOrGroupSpec ::= RequiredToken | OptionalGroup
    protected class TokenOrGroupSpecParser implements RuleParser<TokenOrGroup> {

    	@SuppressWarnings("unchecked")
    	public TokenOrGroup parse() throws ParserException {
    		return new ChoiceParser<TokenOrGroup>(requiredTokenParser,
    				optionalGroupParser).parse();
    	}

    }

    // OptionalGroup ::= "[" TokenOrGroupSpec empty + "]"
    protected class OptionalGroupParser implements RuleParser<Group> {

    	public Group parse() throws ParserException {
    		List<TokenOrGroup> rule = new ValueExtractor<List<TokenOrGroup>>(1,
    				new SequenceParser(new SingleTokenParser(
    						TokenType.LBracket, Context.Syntax),
    						new RepetitionParser<TokenOrGroup>(
    								tokenOrGroupSpecParser),
    						new SingleTokenParser(TokenType.RBracket,
    								Context.Syntax))).parse();

    		if (rule != null) {
    			return new Group(rule);
    		}

    		return null;
    	}

    }

    // RequiredToken ::= Literal | PrimitiveFieldName
    protected class RequiredTokenParser implements RuleParser<RequiredToken> {

    	@SuppressWarnings("unchecked")
    	public RequiredToken parse() throws ParserException {
    		Node rule = new ChoiceParser<Node>(primitiveFieldNameParser,
    				literalParser).parse();

    		if (rule != null) {
    			return new RequiredToken(rule);
    		}

    		return null;
    	}

    }

    // Literal ::= word | ","
    protected class LiteralParser implements RuleParser<LiteralNode> {

    	Set<String> invalidWords = new HashSet<String>(Arrays.asList(
    			Lexer.BIT_LIT, Lexer.BOOLEAN_LIT, Lexer.CHARACTER_LIT,
    			Lexer.CHOICE_LIT, Lexer.DATE_LIT, Lexer.DATE_TIME_LIT,
    			Lexer.DURATION_LIT, Lexer.EMBEDDED_LIT, Lexer.END_LIT,
    			Lexer.ENUMERATED_LIT, Lexer.EXTERNAL_LIT, Lexer.FALSE_LIT,
    			Lexer.INSTANCE_LIT, Lexer.INTEGER_LIT, Lexer.INTERSECTION_LIT,
    			Lexer.MINUS_INFINITY_LIT, Lexer.NULL_LIT, Lexer.OBJECT_LIT,
    			Lexer.OCTET_LIT, Lexer.PLUS_INFINITY_LIT, Lexer.REAL_LIT,
    			Lexer.RELATIVE_OID_LIT, Lexer.SEQUENCE_LIT, Lexer.SET_LIT,
    			Lexer.TIME_LIT, Lexer.TIME_OF_DAY_LIT, Lexer.TRUE_LIT,
    			Lexer.UNION_LIT));

    	@SuppressWarnings("unchecked")
    	public LiteralNode parse() throws ParserException {
    		Token token = new ChoiceParser<Token>(new ValueExtractor<Token>(0,
    				new SequenceParser(new SingleTokenParser(TokenType.Word,
    						Context.Syntax), new NegativeLookaheadParser(
    						TokenType.Dot))), new SingleTokenParser(
    				TokenType.Comma)).parse();

    		if (token != null) {
    			if (Token.TokenType.Word == token.getType()) {
    				if (invalidWords.contains(token.getText())) {
    					return null;
    				}

    				return new LiteralNode(token.getText());
    			} else {
    				return new LiteralNode(",");
    			}
    		}

    		return null;
    	}

    }

    // DefinedObject ::= ExternalObjectReference | objectreference
    protected class DefinedObjectParser implements RuleParser<ObjectNode> {

    	@SuppressWarnings("unchecked")
    	public ObjectReferenceNode parse() throws ParserException {
    		Object rule = new ChoiceParser<Object>(
    				externalObjectReferenceParser, new SingleTokenParser(
    						TokenType.Identifier)).parse();

    		if (rule != null) {
    			if (rule instanceof ExternalObjectReferenceNode) {
    				return (ObjectReferenceNode) rule;
    			} else {
    				return new ObjectReferenceNode(((Token) rule).getText());
    			}
    		}

    		return null;
    	}

    }

    // ExternalObjectReference ::= modulereference "." objectreference
    protected class ExternalObjectReferenceParser implements
    		RuleParser<ExternalObjectReferenceNode> {

    	public ExternalObjectReferenceNode parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.TypeReference,
    				TokenType.Dot, new SingleTokenParser(TokenType.Identifier))
    				.parse();

    		if (rule != null) {
    			return new ExternalObjectReferenceNode(
    					((Token) rule.get(0)).getText(),
    					((Token) rule.get(2)).getText());
    		}

    		return null;
    	}

    }

    // Object ::= DefinedObject | ObjectDefn | ObjectFromObject |
    // ParameterizedObject
    protected class ObjectParser implements RuleParser<ObjectNode> {

    	@SuppressWarnings("unchecked")
    	public ObjectNode parse() throws ParserException {
    		Node rule = new ChoiceParser<Node>(parameterizedObjectParser,
    				informationFromObjectsParser, definedObjectParser,
    				objectDefnParser).parse();

    		if (rule != null) {
    			if (rule instanceof InformationFromObjects) {
    				return new ObjectFromObjectNode(
    						(InformationFromObjects) rule);
    			}
    			return (ObjectNode) rule;
    		}

    		return null;
    	}

    }

    // ObjectDefn ::= DefaultSyntax | DefinedSyntax
    protected class ObjectDefnParser implements RuleParser<ObjectDefnNode> {

    	@SuppressWarnings("unchecked")
    	public ObjectDefnNode parse() throws ParserException {
    		ObjectSyntaxNode rule = new ChoiceParser<ObjectSyntaxNode>(
    				defaultSyntaxParser, definedSyntaxParser).parse();

    		if (rule != null) {
    			return new ObjectDefnNode(rule);
    		}

    		return null;
    	}

    }

    // DefaultSyntax ::= "{" FieldSetting "," * "}"
    protected class DefaultSyntaxParser implements
    		RuleParser<DefaultSyntaxNode> {

    	@SuppressWarnings("unchecked")
    	public DefaultSyntaxNode parse() throws ParserException {
    		List<Object> rule = new SequenceParser(new boolean[] { true, false,
    				true }, new SingleTokenParser(TokenType.LBrace),
    				new CommaSeparatedRuleParser<FieldSettingNode>(
    						fieldSettingParser), new SingleTokenParser(
    						TokenType.RBrace)).parse();

    		if (rule != null) {
    			return new DefaultSyntaxNode(
    					(List<FieldSettingNode>) rule.get(1));
    		}

    		return null;
    	}

    }

    // FieldSetting ::= PrimitiveFieldName Setting
    protected class FieldSettingParser implements RuleParser<FieldSettingNode> {

    	public FieldSettingNode parse() throws ParserException {
    		List<Object> rule = new SequenceParser(primitiveFieldNameParser,
    				settingParser).parse();

    		if (rule != null) {
    			return new FieldSettingNode(
    					(PrimitiveFieldNameNode) rule.get(0),
    					(Node) rule.get(1));
    		}

    		return null;
    	}

    }

    // DefinedSyntax ::= "{" DefinedSyntaxToken empty * "}"
    protected class DefinedSyntaxParser implements
    		RuleParser<DefinedSyntaxNode> {

    	@SuppressWarnings("unchecked")
    	public DefinedSyntaxNode parse() throws ParserException {
    		List<Object> rule = new SequenceParser(new boolean[] { true, false,
    				true }, TokenType.LBrace, new RepetitionParser<Node>(
    				definedSyntaxTokenParser), TokenType.RBrace).parse();

    		if (rule != null) {
    			return new DefinedSyntaxNode((List<Node>) rule.get(1));
    		}

    		return null;
    	}

    }

    // DefinedSyntaxToken ::= Literal | Setting
    protected class DefinedSyntaxTokenParser implements RuleParser<Node> {

    	@SuppressWarnings("unchecked")
    	public Node parse() throws ParserException {
    		return new ChoiceParser<Node>(literalParser, settingParser).parse();
    	}

    }

    // Setting ::= Type | Value | ValueSet | Object | ObjectSet
    protected class SettingParser implements RuleParser<Node> {

    	@SuppressWarnings("unchecked")
    	public Node parse() throws ParserException {
    		return new ChoiceParser<>(typeParser, valueParser, valueSetParser, objectParser, objectSetParser).parse();
    	}

    }

    // DefinedObjectSet ::= ExternalObjectSetReference | objectsetreference
    protected class DefinedObjectSetParser implements
    		RuleParser<ObjectSetReferenceNode> {

    	@SuppressWarnings("unchecked")
    	public ObjectSetReferenceNode parse() throws ParserException {
    		Object rule = new ChoiceParser<Object>(
    				externalObjectSetReferenceParser, new SingleTokenParser(
    						TokenType.TypeReference)).parse();

    		if (rule != null) {
    			if (rule instanceof ExternalObjectSetReferenceNode) {
    				return (ObjectSetReferenceNode) rule;
    			} else {
    				return new ObjectSetReferenceNode(((Token) rule).getText());
    			}
    		}

    		return null;
    	}

    }

    // ExternalObjectSetReference ::= modulereference "." objectsetreference
    protected class ExternalObjectSetReferenceParser implements
    		RuleParser<ExternalObjectSetReferenceNode> {

    	public ExternalObjectSetReferenceNode parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.TypeReference,
    				TokenType.Dot, TokenType.TypeReference).parse();

    		if (rule != null) {
    			return new ExternalObjectSetReferenceNode(
    					((Token) rule.get(0)).getText(),
    					((Token) rule.get(2)).getText());
    		}

    		return null;
    	}

    }

    // ObjectSetElements ::=
    // Object | DefinedObjectSet | ObjectSetFromObjects | ParameterizedObjectSet
    protected class ObjectSetElementsParser implements
    		RuleParser<ObjectSetElementsNode> {

    	@SuppressWarnings("unchecked")
    	public ObjectSetElementsNode parse() throws ParserException {
    		Node rule = new ChoiceParser<Node>(informationFromObjectsParser,
    				parameterizedObjectSetParser, objectParser,
    				definedObjectSetParser).parse();

    		if (rule != null) {
    			return new ObjectSetElementsNode(rule);
    		}

    		return null;
    	}

    }

    // ObjectClassFieldType ::= DefinedObjectClass "." FieldName
    protected class ObjectClassFieldTypeParser implements
    		RuleParser<ObjectClassFieldTypeNode> {

    	public ObjectClassFieldTypeNode parse() throws ParserException {
    		List<Object> rule = new SequenceParser(definedObjectClassParser,
    				TokenType.Dot, fieldNameParser).parse();

    		if (rule != null) {
    			return new ObjectClassFieldTypeNode(
    					(ObjectClassReferenceNode) rule.get(0),
    					(FieldNameNode) rule.get(2));
    		}

    		return null;
    	}

    }

    // ObjectClassFieldValue ::= OpenTypeFieldVal | FixedTypeFieldVal
    protected class ObjectClassFieldValueParser implements RuleParser<Value> {

    	@SuppressWarnings("unchecked")
    	public Value parse() throws ParserException {
    		return new ChoiceParser<>(openTypeFieldValParser, fixedTypeFieldValParser).parse();
    	}

    }

    // OpenTypeFieldVal ::= Type ":" Value
    protected class OpenTypeFieldValParser implements
    		RuleParser<OpenTypeFieldValue> {

    	public OpenTypeFieldValue parse() throws ParserException {
    		List<Object> rule = new SequenceParser(typeParser, TokenType.Colon,	valueParser).parse();

    		if (rule != null) {
    			return new OpenTypeFieldValue((Type) rule.get(0), (Value) rule.get(2));
    		}

    		return null;
    	}

    }

    // FixedTypeFieldVal ::= BuiltinValue | ReferencedValue
    protected class FixedTypeFieldValParser implements RuleParser<Value> {

    	public Value parse() throws ParserException {
    		return builtinOrReferencedValueParser.parse();
    	}

    }

    // ReferencedObjects ::=
    // DefinedObject | ParameterizedObject |
    // DefinedObjectSet | ParameterizedObjectSet
    protected class ReferencedObjectsParser implements RuleParser<Node> {

    	@SuppressWarnings("unchecked")
    	public Node parse() throws ParserException {
    		return new ChoiceParser<Node>(parameterizedObjectParser,
    				definedObjectParser, parameterizedObjectSetParser,
    				definedObjectSetParser).parse();
    	}

    }

    // InformationFromObjects ::=
    // ValueFromObject
    // | ValueSetFromObjects
    // | TypeFromObject
    // | ObjectFromObject
    // | ObjectSetFromObjects
    protected class InformationFromObjectsParser implements
    		RuleParser<InformationFromObjects> {

    	@SuppressWarnings("unchecked")
    	public InformationFromObjects parse() throws ParserException {
    		return new ChoiceParser<InformationFromObjects>(
    				typeFromObjectsParser, valueFromObjectParser).parse();
    	}

    }

    // ValueSetFromObjects ::= ReferencedObjects "." FieldName
    // TypeFromObject ::= ReferencedObjects "." FieldName
    // ObjectFromObject ::= ReferencedObjects "." FieldName
    // ObjectSetFromObjects ::= ReferencedObjects "." FieldName
    protected class TypeFromObjectsParser implements
    		RuleParser<TypeFromObjects> {

    	public TypeFromObjects parse() throws ParserException {
    		List<Object> rule = new SequenceParser(referencedObjectsParser,
    				TokenType.Dot, fieldNameParser).parse();

    		if (rule != null) {
    			FieldNameNode field = (FieldNameNode) rule.get(2);
    			PrimitiveFieldNameNode lastField = field
    					.getPrimitiveFieldNames().get(
    							field.getPrimitiveFieldNames().size() - 1);

    			if (!(lastField.isValueFieldReference())) {
    				return new TypeFromObjects(
    						(ReferencedObjectsNode) rule.get(0), field);
    			}
    		}

    		return null;
    	}

    }

    // ValueFromObject ::= ReferencedObjects "." FieldName
    protected class ValueFromObjectParser implements
    		RuleParser<ValueFromObject> {

    	public ValueFromObject parse() throws ParserException {
    		List<Object> rule = new SequenceParser(referencedObjectsParser,
    				TokenType.Dot, fieldNameParser).parse();

    		if (rule != null) {
    			FieldNameNode field = (FieldNameNode) rule.get(2);
    			PrimitiveFieldNameNode lastField = field
    					.getPrimitiveFieldNames().get(
    							field.getPrimitiveFieldNames().size() - 1);

    			if (lastField.isValueFieldReference()) {
    				return new ValueFromObject(
    						(ReferencedObjectsNode) rule.get(0), field);
    			}
    		}

    		return null;
    	}

    }

    // GeneralConstraint ::= UserDefinedConstraint | TableConstraint |
    // ContentsConstraint
    protected class GeneralConstraintParser implements RuleParser<Constraint> {

    	@SuppressWarnings("unchecked")
    	public Constraint parse() throws ParserException {
    		return new ChoiceParser<Constraint>(userDefinedConstraintParser,
    				tableConstraintParser, contentsConstraintParser).parse();
    	}

    }

    // UserDefinedConstraint ::= CONSTRAINED BY "{"
    // UserDefinedConstraintParameter "," * "}"
    protected class UserDefinedConstraintParser implements
    		RuleParser<UserDefinedConstraintNode> {

    	@SuppressWarnings("unchecked")
    	public UserDefinedConstraintNode parse() throws ParserException {
    		List<Object> rule = new SequenceParser(
    				new boolean[] { true, true, true, false, true },
    				TokenType.CONSTRAINED_KW,
    				TokenType.BY_KW,
    				TokenType.LBrace,
    				new CommaSeparatedRuleParser<UserDefinedConstraintParamNode>(
    						userDefinedConstraintParameterParser),
    				TokenType.RBrace).parse();

    		if (rule != null) {
    			return new UserDefinedConstraintNode(
    					(List<UserDefinedConstraintParamNode>) rule.get(3));
    		}

    		return null;
    	}

    }

    // UserDefinedConstraintParameter ::=
    // Governor ":" Value
    // | Governor ":" Object
    // | DefinedObjectSet
    // | Type
    // | DefinedObjectClass
    protected class UserDefinedConstraintParameterParser implements
    		RuleParser<UserDefinedConstraintParamNode> {

    	@SuppressWarnings("unchecked")
    	public UserDefinedConstraintParamNode parse() throws ParserException {
    		Object rule = new ChoiceParser<>(new SequenceParser(
    				governorParser, TokenType.Colon, new ChoiceParser<>(valueParser, objectParser)), typeParser,
    				usefulObjectClassReferenceParser).parse();

    		if (rule != null) {
    			if (rule instanceof List) {
    				List<Object> ruleList = (List<Object>) rule;
    				return new UserDefinedConstraintParamNode((Governor) ruleList.get(0), (Node) ruleList.get(2));
    			} else {
    				return new UserDefinedConstraintParamNode((Node) rule);
    			}
    		}

    		return null;
    	}

    }

    // TableConstraint ::= SimpleTableConstraint | ComponentRelationConstraint
    protected class TableConstraintParser implements
    		RuleParser<TableConstraint> {

    	@SuppressWarnings("unchecked")
    	public TableConstraint parse() throws ParserException {
    		return new ChoiceParser<TableConstraint>(
    				componentRelationConstraintParser,
    				simpleTableConstraintParser).parse();
    	}

    }

    // SimpleTableConstraint ::= ObjectSet
    protected class SimpleTableConstraintParser implements
    		RuleParser<SimpleTableConstraintNode> {

    	public SimpleTableConstraintNode parse() throws ParserException {
    		Object rule = objectSetParser.parse();

    		if (rule != null) {
    			return new SimpleTableConstraintNode((SetSpecsNode) rule);
    		}

    		return null;
    	}

    }

    // ComponentRelationConstraint ::= "{" DefinedObjectSet "}" "{" AtNotation
    // "," + "}"
    protected class ComponentRelationConstraintParser implements
    		RuleParser<ComponentRelationConstraint> {

    	@SuppressWarnings("unchecked")
    	public ComponentRelationConstraint parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.LBrace,
    				definedObjectSetParser, TokenType.RBrace, TokenType.LBrace,
    				new CommaSeparatedRuleParser<AtNotationNode>(
    						atNotationParser), TokenType.RBrace).parse();

    		if (rule != null) {
    			return new ComponentRelationConstraint(
    					(ObjectSetReferenceNode) rule.get(1),
    					(List<AtNotationNode>) rule.get(4));
    		}

    		return null;
    	}

    }

    // AtNotation ::= "@" ComponentIdList | "@." Level ComponentIdList
    protected class AtNotationParser implements RuleParser<AtNotationNode> {

    	@SuppressWarnings("unchecked")
    	public AtNotationNode parse() throws ParserException {
    		List<Object> rule = new ChoiceParser<List<Object>>(
    				new SequenceParser(TokenType.AT, componentIdListParser),
    				new SequenceParser(
    						new boolean[] { true, true, false, true },
    						TokenType.AT, new SingleTokenParser(TokenType.Dot,
    								Context.Level), levelParser,
    						componentIdListParser)).parse();

    		if (rule != null) {
    			if (rule.size() == 2) {
    				return new AtNotationNode((ComponentIdListNode) rule.get(1));
    			} else {
    				return new AtNotationNode(
    						(ComponentIdListNode) rule.get(3),
    						rule.get(1) != null ? ((Integer) rule.get(2)) + 1
    								: 0);
    			}
    		}

    		return null;
    	}

    }

    // Level ::= "." Level | empty
    protected class LevelParser implements RuleParser<Integer> {

    	public Integer parse() throws ParserException {
    		List<Token> rule = new RepetitionParser<Token>(
    				new SingleTokenParser(TokenType.Dot, Context.Level))
    				.parse();

    		if (rule != null) {
    			return rule.size();
    		}

    		return 0;
    	}

    }

    // ComponentIdList ::= identifier "." +
    protected class ComponentIdListParser implements
    		RuleParser<ComponentIdListNode> {

    	public ComponentIdListNode parse() throws ParserException {
    		List<Token> rule = new TokenSeparatedRuleParser<Token>(
    				new SingleTokenParser(TokenType.Identifier), TokenType.Dot)
    				.parse();

    		if (rule != null) {
    			return new ComponentIdListNode(rule);
    		}

    		return null;
    	}

    }

    // ContentsConstraint ::=
    // CONTAINING Type
    // | ENCODED BY Value
    // | CONTAINING Type ENCODED BY Value
    protected class ContentsConstraintParser implements
    		RuleParser<ContentsConstraint> {

    	@SuppressWarnings("unchecked")
    	public ContentsConstraint parse() throws ParserException {
    		List<Object> rule = new ChoiceParser<>(
    				new SequenceParser(TokenType.CONTAINING_KW, typeParser,
    						TokenType.ENCODED_KW, TokenType.BY_KW, valueParser),
    				new SequenceParser(TokenType.CONTAINING_KW, typeParser),
    				new SequenceParser(TokenType.ENCODED_KW, TokenType.BY_KW, valueParser)).parse();

    		if (rule != null) {
    			switch (rule.size()) {
    			case 2:
    				return new ContentsConstraint((Type) rule.get(1));

    			case 3:
    				return new ContentsConstraint((Value) rule.get(2));

    			case 5:
    				return new ContentsConstraint((Type) rule.get(1), (Value) rule.get(4));
    			}
    		}

    		return null;
    	}

    }

    // ParameterizedAssignment ::=
    // ParameterizedTypeAssignment
    // | ParameterizedValueAssignment
    // | ParameterizedValueSetTypeAssignment
    // | ParameterizedObjectClassAssignment
    // | ParameterizedObjectAssignment
    // | ParameterizedObjectSetAssignment
    protected class ParameterizedAssignmentParser implements
    		RuleParser<ParameterizedAssignmentNode> {

    	@SuppressWarnings("unchecked")
    	public ParameterizedAssignmentNode parse() throws ParserException {
    		return new ChoiceParser<ParameterizedAssignmentNode>(
    				parameterizedTypeAssignmentParser,
    				parameterizedValueAssignmentParser,
    				parameterizedValueSetTypeAssignmentParser,
    				parameterizedObjectSetAssignmentParser).parse();
    	}

    }

    // ParameterizedTypeAssignment ::=
    // typereference ParameterList "::=" Type
    // ParameterizedObjectClassAssignment ::=
    // objectclassreference ParameterList "::=" ObjectClass
    protected class ParameterizedTypeAssignmentParser implements
    		RuleParser<ParameterizedTypeOrObjectClassAssignmentNode<?>> {

    	@SuppressWarnings("unchecked")
    	public ParameterizedTypeOrObjectClassAssignmentNode<?> parse()
    			throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.TypeReference,
    				parameterListParser, TokenType.Assign,
    				new ChoiceParser<Node>(typeParser,
    						usefulObjectClassReferenceParser,
    						objectClassDefnParser)).parse();

    		if (rule != null) {
    			Node typeNode = (Node) rule.get(3);

    			if (typeNode instanceof ObjectClassDefn
    					|| typeNode instanceof ObjectClassReferenceNode) {
    				return new ParameterizedObjectClassAssignmentNode(
    						((Token) rule.get(0)).getText(),
    						(List<ParameterNode>) rule.get(1),
    						(ObjectClassNode) typeNode);
    			} else if (typeNode instanceof TypeReference
    					&& !(typeNode instanceof UsefulType)
    					|| typeNode instanceof ExternalTypeReference) {
    				return new ParameterizedTypeOrObjectClassAssignmentNode<Node>(
    						((Token) rule.get(0)).getText(),
    						(List<ParameterNode>) rule.get(1), typeNode);
    			} else {
    				return new ParameterizedTypeAssignmentNode(
    						((Token) rule.get(0)).getText(),
    						(List<ParameterNode>) rule.get(1),
    						(Type) rule.get(3));
    			}
    		}

    		return null;
    	}

    }

    // ParameterizedValueAssignment ::=
    // valuereference ParameterList Type "::=" Value
    // ParameterizedObjectAssignment ::=
    // objectreference ParameterList DefinedObjectClass "::=" Object
    protected class ParameterizedValueAssignmentParser implements
    		RuleParser<ParameterizedValueOrObjectAssignmentNode<?, ?>> {

    	@SuppressWarnings("unchecked")
    	public ParameterizedValueOrObjectAssignmentNode<?, ?> parse()
    			throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.Identifier, parameterListParser,
                    new ChoiceParser<>(typeParser,usefulObjectClassReferenceParser), TokenType.Assign,
                    new ChoiceParser<>(informationFromObjectsParser, valueParser, objectDefnParser)).parse();

    		if (rule != null) {
    			Node typeNode = (Node) rule.get(2);
    			Node valueNode = (Node) rule.get(4);

    			if (valueNode instanceof ObjectDefnNode	|| valueNode instanceof ObjectFromObjectNode) {
    				if (typeNode instanceof TypeReference && !(typeNode instanceof UsefulType)) {
    					return new ParameterizedObjectAssignmentNode(
    							((Token) rule.get(0)).getText(),
    							(List<ParameterNode>) rule.get(1),
    							new ObjectClassReferenceNode(((TypeReference) typeNode).getType()),
                                (ObjectNode) valueNode);
    				} else if (typeNode instanceof ExternalTypeReference) {
    					return new ParameterizedObjectAssignmentNode(
    							((Token) rule.get(0)).getText(),
    							(List<ParameterNode>) rule.get(1),
    							new ExternalObjectClassReferenceNode(
    									((ExternalTypeReference) typeNode).getModule(),
    									((ExternalTypeReference) typeNode).getType()),
    							(ObjectNode) valueNode);
    				}
    				// TODO: error
    			} else if (typeNode instanceof ObjectClassReferenceNode) {
    			    Node definedValue = valueNode;

    			    if (valueNode instanceof AmbiguousValue) {
    			        definedValue = ((AmbiguousValue) valueNode).getValue(DefinedValue.class);
                    }

    				if (definedValue instanceof DefinedValue) {
                        valueNode = ((DefinedValue) definedValue).toObjectValue();
    				} else if (!(valueNode instanceof ObjectNode)) {
    					return null;
    				}

    				return new ParameterizedObjectAssignmentNode(
    						((Token) rule.get(0)).getText(),
    						(List<ParameterNode>) rule.get(1),
    						(ObjectClassReferenceNode) typeNode,
    						(ObjectNode) valueNode);
    			} else if ((typeNode instanceof TypeReference
    					&& !(typeNode instanceof UsefulType) || typeNode instanceof ExternalTypeReference)
    					&& valueNode instanceof DefinedValue
    					|| valueNode instanceof TypeFromObjects
    					|| valueNode instanceof ValueFromObject
                        || valueNode instanceof AmbiguousValue
                        && ((AmbiguousValue) valueNode).getValue(DefinedValue.class) != null) {
    			    if (valueNode instanceof AmbiguousValue) {
    			        valueNode = ((AmbiguousValue) valueNode).getValue(DefinedValue.class);
                    }

    				return new ParameterizedValueOrObjectAssignmentNode<>(
    						((Token) rule.get(0)).getText(),
    						(List<ParameterNode>) rule.get(1), typeNode,
    						valueNode);
    			} else if (valueNode instanceof Value) {
    			    if (valueNode instanceof AmbiguousValue) {
    			        Value collectionValue = ((AmbiguousValue) valueNode).getValue(CollectionValue.class);

    			        if (collectionValue != null) {
    			            valueNode = collectionValue;
                        }
                     }

    				return new ParameterizedValueAssignmentNode(
    						((Token) rule.get(0)).getText(),
    						(List<ParameterNode>) rule.get(1), (Type) typeNode,
    						(Value) valueNode);
    			}

    		}

    		return null;
    	}
    }

    // ParameterizedValueSetTypeAssignment ::=
    // typereference ParameterList Type "::=" ValueSet
    protected class ParameterizedValueSetTypeAssignmentParser implements
    		RuleParser<ParameterizedValueSetTypeAssignmentNode> {

    	@SuppressWarnings("unchecked")
    	public ParameterizedValueSetTypeAssignmentNode parse()
    			throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.TypeReference,
    				parameterListParser, typeParser, TokenType.Assign,
    				valueSetParser).parse();

    		if (rule != null) {
    			return new ParameterizedValueSetTypeAssignmentNode(
    					((Token) rule.get(0)).getText(),
    					(List<ParameterNode>) rule.get(1), (Type) rule.get(2),
    					(ElementSetSpecsNode) rule.get(4));
    		}

    		return null;
    	}

    }

    // ParameterizedObjectSetAssignment ::=
    // objectsetreference ParameterList DefinedObjectClass "::=" ObjectSet
    protected class ParameterizedObjectSetAssignmentParser implements
    		RuleParser<ParameterizedObjectSetAssignmentNode> {

    	@SuppressWarnings("unchecked")
    	public ParameterizedObjectSetAssignmentNode parse()
    			throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.TypeReference,
    				parameterListParser, definedObjectClassParser,
    				TokenType.Assign, objectSetParser).parse();

    		if (rule != null) {
    			return new ParameterizedObjectSetAssignmentNode(
    					((Token) rule.get(0)).getText(),
    					(List<ParameterNode>) rule.get(1),
    					(ObjectClassReferenceNode) rule.get(2),
    					(ObjectSetSpecNode) rule.get(4));
    		}

    		return null;
    	}

    }

    // ParameterList ::= "{" Parameter "," + "}"
    protected class ParameterListParser implements
    		RuleParser<List<ParameterNode>> {

    	public List<ParameterNode> parse() throws ParserException {
    		return new ValueExtractor<List<ParameterNode>>(1,
    				new SequenceParser(TokenType.LBrace,
    						new CommaSeparatedRuleParser<ParameterNode>(
    								parameterParser), TokenType.RBrace))
    				.parse();
    	}

    }

    // Parameter ::= ParamGovernor ":" DummyReference | DummyReference
    protected class ParameterParser implements RuleParser<ParameterNode> {

    	@SuppressWarnings("unchecked")
    	public ParameterNode parse() throws ParserException {
    		List<Object> rule = new ChoiceParser<List<Object>>(
    				new SequenceParser(paramGovernorParser, TokenType.Colon,
    						dummyReferenceParser), new SequenceParser(
    						dummyReferenceParser)).parse();

    		if (rule != null) {
    			if (rule.size() == 3) {
    				return new ParameterNode((ParamGovernorNode) rule.get(0),
    						(ReferenceNode) rule.get(2));
    			} else {
    				return new ParameterNode((ReferenceNode) rule.get(0));
    			}
    		}

    		return null;
    	}

    }

    // ParamGovernor ::= Governor | DummyGovernor
    protected class ParamGovernorParser implements
    		RuleParser<ParamGovernorNode> {

    	@SuppressWarnings("unchecked")
    	public ParamGovernorNode parse() throws ParserException {
    		return new ChoiceParser<ParamGovernorNode>(governorParser,
    				dummyGovernorParser).parse();
    	}

    }

    // Governor ::= Type | DefinedObjectClass
    protected class GovernorParser implements RuleParser<Governor> {

    	@SuppressWarnings("unchecked")
    	public Governor parse() throws ParserException {
    		Node rule = new ChoiceParser<Node>(typeParser,
    				definedObjectClassParser).parse();

    		if (rule != null) {
    			return new Governor(rule);
    		}

    		return null;
    	}

    }

    // DummyGovernor ::= DummyReference
    protected class DummyGovernorParser implements RuleParser<DummyGovernor> {

    	public DummyGovernor parse() throws ParserException {
    		ReferenceNode rule = dummyReferenceParser.parse();

    		if (rule != null) {
    			return new DummyGovernor(rule);
    		}

    		return null;
    	}

    }

    // DummyReference ::= Reference
    protected class DummyReferenceParser implements RuleParser<ReferenceNode> {

    	public ReferenceNode parse() throws ParserException {
    		return referenceParser.parse();
    	}

    }

    // SimpleDefinedType ::= ExternalTypeReference | typereference
    protected class SimpleDefinedTypeParser implements
    		RuleParser<SimpleDefinedType> {

    	@SuppressWarnings("unchecked")
    	public SimpleDefinedType parse() throws ParserException {
    		return new ChoiceParser<SimpleDefinedType>(
    				externalTypeReferenceParser, typeReferenceParser).parse();
    	}

    }

    // SimpleDefinedValue ::= ExternalValueReference | valuereference
    // EnumeratedValue ::= identifier
    protected class SimpleDefinedValueParser implements
    		RuleParser<DefinedValue> {

    	@SuppressWarnings("unchecked")
    	public SimpleDefinedValue parse() throws ParserException {
    		Object rule = new ChoiceParser<Object>(
    				externalValueReferenceParser, new SingleTokenParser(
    						TokenType.Identifier)).parse();

    		if (rule != null) {
    			if (rule instanceof Token) {
    				return new SimpleDefinedValue(((Token) rule).getText());
    			} else {
    				return (SimpleDefinedValue) rule;
    			}
    		}

    		return null;
    	}

    }

    // ParameterizedType ::= SimpleDefinedType ActualParameterList
    // ParameterizedValueSetType ::= SimpleDefinedType ActualParameterList
    protected class ParameterizedTypeParser implements
    		RuleParser<SimpleDefinedType> {

    	@SuppressWarnings("unchecked")
    	public SimpleDefinedType parse() throws ParserException {
    		List<Object> rule = new SequenceParser(simpleDefinedTypeParser,
    				actualParameterListParser).parse();

    		if (rule != null) {
    			SimpleDefinedType type = (SimpleDefinedType) rule.get(0);
    			type.setParameters((List<Node>) rule.get(1));

    			return type;
    		}

    		return null;
    	}

    }

    // ParameterizedValue ::= SimpleDefinedValue ActualParameterList
    protected class ParameterizedValueParser implements
    		RuleParser<SimpleDefinedValue> {

    	@SuppressWarnings("unchecked")
    	public SimpleDefinedValue parse() throws ParserException {
    		List<Object> rule = new SequenceParser(simpleDefinedValueParser,
    				actualParameterListParser).parse();

    		if (rule != null) {
    			SimpleDefinedValue ref = (SimpleDefinedValue) rule.get(0);
    			ref.setParameters((List<Node>) rule.get(1));
    			return ref;
    		}

    		return null;
    	}

    }

    // ParameterizedObjectClass ::= DefinedObjectClass ActualParameterList
    protected class ParameterizedObjectClassParser implements
    		RuleParser<ObjectClassReferenceNode> {

    	@SuppressWarnings("unchecked")
    	public ObjectClassReferenceNode parse() throws ParserException {
    		List<Object> rule = new SequenceParser(definedObjectClassParser,
    				actualParameterListParser).parse();

    		if (rule != null) {
    			ObjectClassReferenceNode ref = (ObjectClassReferenceNode) rule
    					.get(0);
    			ref.setParameters((List<Node>) rule.get(1));
    			return ref;
    		}

    		return null;
    	}

    }

    // ParameterizedObjectSet ::= DefinedObjectSet ActualParameterList
    protected class ParameterizedObjectSetParser implements
    		RuleParser<ObjectSetReferenceNode> {

    	@SuppressWarnings("unchecked")
    	public ObjectSetReferenceNode parse() throws ParserException {
    		List<Object> rule = new SequenceParser(definedObjectSetParser,
    				actualParameterListParser).parse();

    		if (rule != null) {
    			ObjectSetReferenceNode ref = (ObjectSetReferenceNode) rule
    					.get(0);
    			ref.setParameters((List<Node>) rule.get(1));
    			return ref;
    		}

    		return null;
    	}

    }

    // ParameterizedObject ::= DefinedObject ActualParameterList
    protected class ParameterizedObjectParser implements RuleParser<ObjectNode> {

    	@SuppressWarnings("unchecked")
    	public ObjectReferenceNode parse() throws ParserException {
    		List<Object> rule = new SequenceParser(definedObjectParser,
    				actualParameterListParser).parse();

    		if (rule != null) {
    			ObjectReferenceNode ref = (ObjectReferenceNode) rule.get(0);
    			ref.setParameters((List<Node>) rule.get(1));
    			return ref;
    		}

    		return null;
    	}

    }

    // ActualParameterList ::= "{" ActualParameter "," + "}"
    protected class ActualParameterListParser implements RuleParser<List<Node>> {

    	@SuppressWarnings("unchecked")
    	public List<Node> parse() throws ParserException {
    		List<Object> rule = new SequenceParser(TokenType.LBrace,
    				new CommaSeparatedRuleParser<Node>(actualParameterParser),
    				TokenType.RBrace).parse();

    		if (rule != null) {
    			return (List<Node>) rule.get(1);
    		}

    		return null;
    	}

    }

    // ActualParameter ::= Type | Value | ValueSet | DefinedObjectClass | Object
    // | ObjectSet
    protected class ActualParameterParser implements RuleParser<Node> {

    	@SuppressWarnings("unchecked")
    	public Node parse() throws ParserException {
    		return new ChoiceParser<>(typeParser, objectParser,	objectSetParser, valueParser, valueSetParser,
                    definedObjectClassParser).parse();
    	}

    }

}
