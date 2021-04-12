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
import ch.eskaton.asn4j.parser.accessor.ListAccessor;
import ch.eskaton.asn4j.parser.accessor.SequenceListAccessor;
import ch.eskaton.asn4j.parser.ast.AbstractFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.AbstractSyntaxObjectClassReferenceNode;
import ch.eskaton.asn4j.parser.ast.AssignmentNode;
import ch.eskaton.asn4j.parser.ast.AtNotationNode;
import ch.eskaton.asn4j.parser.ast.ComponentIdListNode;
import ch.eskaton.asn4j.parser.ast.ComponentTypeListsNode;
import ch.eskaton.asn4j.parser.ast.DefaultObjectSetSpecNode;
import ch.eskaton.asn4j.parser.ast.DefaultObjectSpecNode;
import ch.eskaton.asn4j.parser.ast.DefaultSyntaxNode;
import ch.eskaton.asn4j.parser.ast.DefaultTypeSpecNode;
import ch.eskaton.asn4j.parser.ast.DefaultValueSetSpecNode;
import ch.eskaton.asn4j.parser.ast.DefaultValueSpecNode;
import ch.eskaton.asn4j.parser.ast.DefinedSyntaxNode;
import ch.eskaton.asn4j.parser.ast.DefinitiveIdentificationNode;
import ch.eskaton.asn4j.parser.ast.DummyGovernor;
import ch.eskaton.asn4j.parser.ast.ElementSetSpecsNode;
import ch.eskaton.asn4j.parser.ast.EncodingControlSectionNode;
import ch.eskaton.asn4j.parser.ast.EncodingPrefixNode;
import ch.eskaton.asn4j.parser.ast.EnumerationItemNode;
import ch.eskaton.asn4j.parser.ast.ExceptionIdentificationNode;
import ch.eskaton.asn4j.parser.ast.ExportsNode;
import ch.eskaton.asn4j.parser.ast.ExtensionAdditionAlternativeNode;
import ch.eskaton.asn4j.parser.ast.ExtensionAndExceptionNode;
import ch.eskaton.asn4j.parser.ast.ExternalObjectClassReference;
import ch.eskaton.asn4j.parser.ast.ExternalObjectReference;
import ch.eskaton.asn4j.parser.ast.ExternalObjectSetReference;
import ch.eskaton.asn4j.parser.ast.FieldNameNode;
import ch.eskaton.asn4j.parser.ast.FieldSettingNode;
import ch.eskaton.asn4j.parser.ast.FixedTypeValueFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.FixedTypeValueOrObjectFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.FixedTypeValueSetFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.FixedTypeValueSetOrObjectSetFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.Governor;
import ch.eskaton.asn4j.parser.ast.Group;
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
import ch.eskaton.asn4j.parser.ast.ObjectClassDefn;
import ch.eskaton.asn4j.parser.ast.Setting;
import ch.eskaton.asn4j.parser.ast.ActualParameter;
import ch.eskaton.asn4j.parser.ast.types.ObjectClassFieldType;
import ch.eskaton.asn4j.parser.ast.ObjectClassNode;
import ch.eskaton.asn4j.parser.ast.ObjectClassReference;
import ch.eskaton.asn4j.parser.ast.ObjectDefnNode;
import ch.eskaton.asn4j.parser.ast.ObjectFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.ObjectFromObjectNode;
import ch.eskaton.asn4j.parser.ast.ObjectNode;
import ch.eskaton.asn4j.parser.ast.ObjectReference;
import ch.eskaton.asn4j.parser.ast.ObjectSetAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ObjectSetElements;
import ch.eskaton.asn4j.parser.ast.ObjectSetFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.ObjectSetReference;
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
import ch.eskaton.asn4j.parser.ast.SetSpecsNode;
import ch.eskaton.asn4j.parser.ast.SimpleTableConstraint;
import ch.eskaton.asn4j.parser.ast.TokenOrGroup;
import ch.eskaton.asn4j.parser.ast.TypeAssignmentNode;
import ch.eskaton.asn4j.parser.ast.TypeFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.TypeIdentifierObjectClassReferenceNode;
import ch.eskaton.asn4j.parser.ast.TypeOrObjectClassAssignmentNode;
import ch.eskaton.asn4j.parser.ast.UpperEndpointNode;
import ch.eskaton.asn4j.parser.ast.UserDefinedConstraint;
import ch.eskaton.asn4j.parser.ast.UserDefinedConstraintParam;
import ch.eskaton.asn4j.parser.ast.ValueAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ValueOrObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ValueSetTypeAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ValueSetTypeOrObjectSetAssignmentNode;
import ch.eskaton.asn4j.parser.ast.VariableTypeValueFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.VariableTypeValueSetFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.constraints.AbstractConstraint;
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
import ch.eskaton.asn4j.parser.ast.constraints.SingleTypeConstraint;
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
import ch.eskaton.asn4j.parser.ast.types.TypeFromObject;
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
import ch.eskaton.commons.collections.Maps;
import ch.eskaton.commons.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.parser.NoPosition.NO_POSITION;
import static ch.eskaton.asn4j.parser.ParserUtils.getPosition;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableMap;

public class Parser {

    private Lexer lexer;

    private Deque<Context> lexerContext = new LinkedList<>();

    private Deque<Integer> marks = new LinkedList<>();

    private Deque<Token> tokens = new LinkedList<>();

    private TokenType lastExpectedToken;

    private Token lastErrorToken;

    private int lastErrorOffset = 0;

    private ParserException lastException;

    private String moduleName;

    // Parsers
    private ActualParameterListParser actualParameterListParser = new ActualParameterListParser();
    private ActualParameterParser actualParameterParser = new ActualParameterParser();
    private AlternativeTypeListParser alternativeTypeListParser = new AlternativeTypeListParser();
    private AlternativeTypeListsParser alternativeTypeListsParser = new AlternativeTypeListsParser();
    private AssignedIdentifierParser assignedIdentifierParser = new AssignedIdentifierParser();
    private AssignmentListParser assignmentListParser = new AssignmentListParser();
    private AssignmentParser assignmentParser = new AssignmentParser();
    private AtNotationParser atNotationParser = new AtNotationParser();
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
    private ObjectElementSetSpecParser objectElementSetSpecParser = new ObjectElementSetSpecParser();
    private ElementsParser elementsParser = new ElementsParser();
    private ObjectElementsParser objectElementsParser = new ObjectElementsParser();
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
    private ObjectExclusionsParser objectExclusionsParser = new ObjectExclusionsParser();
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
    private GlobalModuleReferenceParser globalModuleReferenceParser = new GlobalModuleReferenceParser();
    private GovernorParser governorParser = new GovernorParser();
    private IRIValueParser iriValueParser = new IRIValueParser();
    private IdentifierListParser identifierListParser = new IdentifierListParser();
    private ImportsParser importsParser = new ImportsParser();
    private InformationFromObjectsParser informationFromObjectsParser = new InformationFromObjectsParser();
    private InnerTypeConstraintsParser innerTypeConstraintsParser = new InnerTypeConstraintsParser();
    private IntegerValueParser integerValueParser = new IntegerValueParser();
    private IntersectionElementsParser intersectionElementsParser = new IntersectionElementsParser();
    private ObjectIntersectionElementsParser objectIntersectionElementsParser = new ObjectIntersectionElementsParser();
    private IntersectionsParser intersectionsParser = new IntersectionsParser();
    private ObjectIntersectionsParser objectIntersectionsParser = new ObjectIntersectionsParser();
    private LevelParser levelParser = new LevelParser();
    private LiteralParser literalParser = new LiteralParser();
    private LiteralDefinitionParser literalDefinitionParser = new LiteralDefinitionParser();
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
    private ParamGovernorParser paramGovernorParser = new ParamGovernorParser();
    private ParameterListParser parameterListParser = new ParameterListParser();
    private ParameterParser parameterParser = new ParameterParser();
    private ParameterizedAssignmentParser parameterizedAssignmentParser = new ParameterizedAssignmentParser();
    private ParameterizedObjectClassParser parameterizedObjectClassParser = new ParameterizedObjectClassParser();
    private ParameterizedObjectParser parameterizedObjectParser = new ParameterizedObjectParser();
    private ParameterizedObjectSetAssignmentParser parameterizedObjectSetAssignmentParser = new ParameterizedObjectSetAssignmentParser();
    private ParameterizedObjectSetParser parameterizedObjectSetParser = new ParameterizedObjectSetParser();
    private ParameterizedTypeOrObjectClassAssignmentParser parameterizedTypeOrObjectClassAssignmentParser = new ParameterizedTypeOrObjectClassAssignmentParser();
    private ParameterizedTypeParser parameterizedTypeParser = new ParameterizedTypeParser();
    private ParameterizedValueOrObjectAssignmentParser parameterizedValueOrObjectAssignmentParser = new ParameterizedValueOrObjectAssignmentParser();
    private ParameterizedValueParser parameterizedValueParser = new ParameterizedValueParser();
    private ParameterizedValueSetTypeAssignmentParser parameterizedValueSetTypeAssignmentParser = new ParameterizedValueSetTypeAssignmentParser();
    private PartialSpecificationParser partialSpecificationParser = new PartialSpecificationParser();
    private PatternConstraintParser patternConstraintParser = new PatternConstraintParser();
    private PermittedAlphabetParser permittedAlphabetParser = new PermittedAlphabetParser();
    private PrefixedTypeParser prefixedTypeParser = new PrefixedTypeParser();
    private PresenceConstraintParser presenceConstraintParser = new PresenceConstraintParser();
    private PrimitiveFieldNameParser primitiveFieldNameParser = new PrimitiveFieldNameParser();
    private ValuePrimitiveFieldNameParser valuePrimitiveFieldNameParser = new ValuePrimitiveFieldNameParser();
    private TypePrimitiveFieldNameParser typePrimitiveFieldNameParser = new TypePrimitiveFieldNameParser();
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
    private FixedTypeValueSetOrObjectSetFieldSpecParser fixedTypeValueSetOrObjectSetFieldSpecParser = new FixedTypeValueSetOrObjectSetFieldSpecParser();
    private ElementSetSpecsParser elementSetSpecsParser = new ElementSetSpecsParser();
    private ObjectSetSpecParser objectSetSpecParser = new ObjectSetSpecParser();
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
    private TableConstraintParser tableConstraintParser = new TableConstraintParser();
    private TagDefaultParser tagDefaultParser = new TagDefaultParser();
    private TagParser tagParser = new TagParser();
    private TaggedTypeParser taggedTypeParser = new TaggedTypeParser();
    private TimePointRangeParser timePointRangeParser = new TimePointRangeParser();
    private TokenOrGroupSpecParser tokenOrGroupSpecParser = new TokenOrGroupSpecParser();
    private TypeAssignmentParser typeAssignmentParser = new TypeAssignmentParser();
    private TypeConstraintParser typeConstraintParser = new TypeConstraintParser();
    private TypeConstraintsParser typeConstraintsParser = new TypeConstraintsParser();
    private TypeFieldSpecParser typeFieldSpecParser = new TypeFieldSpecParser();
    private TypeFromObjectParser typeFromObjectParser = new TypeFromObjectParser();
    private TypeOptionalitySpecParser typeOptionalitySpecParser = new TypeOptionalitySpecParser();
    private TypeOrNamedTypeParser typeOrNamedTypeParser = new TypeOrNamedTypeParser();
    private TypeParser typeParser = new TypeParser();
    private TypeReferenceParser typeReferenceParser = new TypeReferenceParser();
    private TypeWithConstraintParser typeWithConstraintParser = new TypeWithConstraintParser();
    private UnionsParser unionsParser = new UnionsParser();
    private ObjectUnionsParser objectUnionsParser = new ObjectUnionsParser();
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
    private ObjectOptionalitySpecParser objectOptionalitySpecParser = new ObjectOptionalitySpecParser();
    private ValueParser valueParser = new ValueParser();
    private ValueRangeParser valueRangeParser = new ValueRangeParser();
    private ValueReferenceParser valueReferenceParser = new ValueReferenceParser();
    private ValueSetOptionalitySpecParser valueSetOptionalitySpecParser = new ValueSetOptionalitySpecParser();
    private ObjectSetOptionalitySpecParser objectSetOptionalitySpecParser = new ObjectSetOptionalitySpecParser();
    private ValueSetParser valueSetParser = new ValueSetParser();
    private ValueSetTypeAssignmentParser valueSetTypeAssignmentParser = new ValueSetTypeAssignmentParser();
    private VariableTypeValueFieldSpecParser variableTypeValueFieldSpecParser = new VariableTypeValueFieldSpecParser();
    private VariableTypeValueSetFieldSpecParser variableTypeValueSetFieldSpecParser = new VariableTypeValueSetFieldSpecParser();
    private VersionNumberParser versionNumberParser = new VersionNumberParser();
    private WithSyntaxSpecParser withSyntaxSpecParser = new WithSyntaxSpecParser();
    private RuleParser<Type> bmpStringParser = () -> parseToken(TokenType.BMP_STRING_KW, BMPString::new);
    private RuleParser<Type> generalStringParser = () -> parseToken(TokenType.GENERAL_STRING_KW, GeneralString::new);
    private RuleParser<Type> graphicStringParser = () -> parseToken(TokenType.GRAPHIC_STRING_KW, GraphicString::new);
    private RuleParser<Type> ia5StringParser = () -> parseToken(TokenType.IA5_STRING_KW, IA5String::new);
    private RuleParser<Type> iso646StringParser = () -> parseToken(TokenType.ISO646_STRING_KW, ISO646String::new);
    private RuleParser<Type> numericStringParser = () -> parseToken(TokenType.NUMERIC_STRING_KW, NumericString::new);
    private RuleParser<Type> printableStringParser = () -> parseToken(TokenType.PRINTABLE_STRING_KW, PrintableString::new);
    private RuleParser<Type> teletexStringParser = () -> parseToken(TokenType.TELETEX_STRING_KW, TeletexString::new);
    private RuleParser<Type> t61StringParser = () -> parseToken(TokenType.T61_STRING_KW, T61String::new);
    private RuleParser<Type> universalStringParser = () -> parseToken(TokenType.UNIVERSAL_STRING_KW, UniversalString::new);
    private RuleParser<Type> utf8StringParser = () -> parseToken(TokenType.UTF8_STRING_KW, UTF8String::new);
    private RuleParser<Type> videotexStringParser = () -> parseToken(TokenType.VIDEOTEX_STRING_KW, VideotexString::new);
    private RuleParser<Type> visibleStringParser = () -> parseToken(TokenType.VISIBLE_STRING_KW, VisibleString::new);

    public Parser(InputStream is) throws IOException {
        this.lexer = new Lexer(is);

        lexerContext.push(Context.NORMAL);
    }

    public Parser(String moduleFile) throws IOException {
        this.lexer = new Lexer(moduleFile);

        lexerContext.push(Context.NORMAL);
    }

    private Token getToken() throws ParserException {
        Token token = lexer.nextToken(lexerContext.peek());

        if (!marks.isEmpty() && token != null) {
            tokens.push(token);
        }

        return token;
    }

    private void setException(String message, Token token) {
        Position position = token.getPosition();

        lastException = new ParserException(String.format(
                "File %s, Line %d, position %d: %s", position.getFile(), position.getLine(), position.getPosition(),
                message));
    }

    private void clearError() {
        lastErrorOffset = 0;
        lastException = null;
        lastExpectedToken = null;
    }

    private void pushBack() {
        if (!tokens.isEmpty()) {
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

    private void resetToMark(int mark) {
        while (tokens.size() > mark) {
            lexer.pushBack(tokens.pop());
        }
    }

    private void resetToMark() {
        resetToMark(marks.pop());
    }

    private Token expect(TokenType type) {
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

    public Type parseToken(TokenType tokenType, Function<Position, Type> ctor) {
        Token token = expect(tokenType);

        if (token != null) {
            return ctor.apply(token.getPosition());
        }

        return null;
    }

    protected class AmbiguousChoiceParser<T> implements RuleParser<Set<T>> {

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

                        result.computeIfAbsent(matchLen, key -> new HashSet<>()).add(choice);
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

                    @SuppressWarnings("unchecked")
                    T token = (T) expect(type);

                    if (token != null) {
                        clearMark();
                        return token;
                    } else {
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
            List<T> result = new ArrayList<>();

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

            if (result.isEmpty()) {
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

        SequenceParser(boolean[] mandatoryRules, Object... objects) {
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
                    throw new ParserException("Invalid parameter to SequenceParser");
                }
            }

            clearMark();

            return result;
        }

    }

    protected class TokenSeparatedRuleParser<T> implements RuleParser<List<T>> {

        private RuleParser<? extends T> parser;

        private TokenType[] types;

        public TokenSeparatedRuleParser(RuleParser<? extends T> parser, TokenType... types) {
            this.parser = parser;
            this.types = types;
        }

        public List<T> parse() throws ParserException {
            List<T> list = new ArrayList<>();
            T obj = parser.parse();

            if (obj == null) {
                return null;
            }

            list.add(obj);

            List<T> moreObjs = new RepetitionParser<T>(new ValueExtractor<>(1,
                    new SequenceParser(new ChoiceParser<Token>(types), parser))).parse();

            if (moreObjs != null) {
                list.addAll(moreObjs);
            }

            return list;
        }

    }

    protected class CommaSeparatedRuleParser<T> extends TokenSeparatedRuleParser<T> {

        public CommaSeparatedRuleParser(RuleParser<T> parser) {
            super(parser, TokenType.COMMA);
        }

    }


    public ModuleNode parse() throws ParserException {
        return new ModuleDefinitionParser().parse();
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
                    tagDefaultParser, extensionDefaultParser, TokenType.ASSIGN,
                    TokenType.BEGIN_KW).parse();
            ModuleBodyNode body = moduleBodyParser.parse();
            List<EncodingControlSectionNode> encCtrl = encodingControlSectionsParser
                    .parse();
            Token end = expect(TokenType.END_KW);

            if (rule != null && end != null) {
                ModuleIdentifierNode moduleIdentifierNode = (ModuleIdentifierNode) rule.get(0);

                return new ModuleNode(moduleIdentifierNode.getPosition(), moduleIdentifierNode,
                        (ModuleNode.Encoding) rule.get(2), (ModuleNode.TagMode) rule.get(3),
                        (Boolean) rule.get(4), body, encCtrl);
            } else {
                if (lastException == null) {
                    Position position = lastErrorToken.getPosition();

                    throw new ParserException(StringUtils.concat("Token '", lastExpectedToken,
                            "' expected, but found '",  lastErrorToken.getType(),
                            (lastErrorToken.getText() != null ? "(" + lastErrorToken.getText() + ")" : ""),
                            "' at line ", position.getLine(), " position ", position.getPosition()));
                } else {
                    throw lastException;
                }
            }
        }

    }

    // EncodingControlSections ::=
    // EncodingControlSection EncodingControlSections
    // |empty
    protected class EncodingControlSectionsParser extends ListRuleParser<List<EncodingControlSectionNode>> {

        public List<EncodingControlSectionNode> parse() throws ParserException {
            return new RepetitionParser<>(encodingControlSectionParser).parse();
        }

    }

    // EncodingControlSection ::=
    // ENCODING-CONTROL
    // encodingreference
    // EncodingInstructionAssignmentList
    protected class EncodingControlSectionParser implements RuleParser<EncodingControlSectionNode> {

        public EncodingControlSectionNode parse() throws ParserException {

            List<Object> rule = new SequenceParser(
                    TokenType.ENCODING_CONTROL_KW, new SingleTokenParser(
                            TokenType.ENCODING_REFERENCE, Context.ENCODING))
                    .parse();

            List<Token> encodingInstruction = new ArrayList<>();

            if (rule != null) {
                while (true) {
                    mark();

                    Token token = getToken();

                    if (token == null
                            || token.getType() == TokenType.ENCODING_CONTROL_KW
                            || token.getType() == TokenType.END_KW) {
                        pushBack();
                        break;
                    }

                    clearMark();
                    encodingInstruction.add(token);
                }

                Token token = (Token) rule.get(1);

                return new EncodingControlSectionNode(token.getPosition(), token.getText(), encodingInstruction);
            }

            return null;
        }

    }

    // ModuleIdentifier ::=
    // modulereference
    // DefinitiveIdentification
    protected class ModuleIdentifierParser extends ListRuleParser<ModuleIdentifierNode> {

        public ModuleIdentifierNode parse() throws ParserException {
            ModuleIdentifierNode node = super.parse(new SequenceParser(new boolean[] { true, false },
                    TokenType.TYPE_REFERENCE,definitiveIdentificationParser),
                    a -> new ModuleIdentifierNode(a.P0(), a.s0(), a.n1()));

            if (node != null) {
                moduleName = node.getModuleName();
            }

            return node;
        }

    }

    // EncodingReferenceDefault ::=
    // encodingreference INSTRUCTIONS
    // | empty
    protected class EncodingReferenceDefaultParser extends ListRuleParser<ModuleNode.Encoding> {

        public Encoding parse() throws ParserException {
            lexerContext.push(Context.ENCODING);

            try {
                super.parse(new SequenceParser(TokenType.ENCODING_REFERENCE, TokenType.INSTRUCTIONS_KW));

                if (matched()) {
                    return ModuleNode.getEncoding(s0());
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
    protected class TagDefaultParser extends ListRuleParser<ModuleNode.TagMode> {

        @SuppressWarnings("unchecked")
        public ModuleNode.TagMode parse() throws ParserException {
            super.parse(new ChoiceParser<>(new SequenceParser(new ChoiceParser<Token>(TokenType.EXPLICIT_KW,
                    TokenType.IMPLICIT_KW, TokenType.AUTOMATIC_KW), TokenType.TAGS_KW)));

            if (matched()) {
                switch ($0()) {
                    case EXPLICIT_KW:
                        return ModuleNode.TagMode.EXPLICIT;
                    case IMPLICIT_KW:
                        return ModuleNode.TagMode.IMPLICIT;
                    case AUTOMATIC_KW:
                        return ModuleNode.TagMode.AUTOMATIC;
                    default:
                        // fall through
                }
            }

            return ModuleNode.TagMode.EXPLICIT;
        }

    }

    // ExtensionDefault ::=
    // EXTENSIBILITY IMPLIED
    // | empty
    protected class ExtensionDefaultParser extends ListRuleParser<Boolean> {

        public Boolean parse() throws ParserException {
            return match(new SequenceParser(TokenType.EXTENSIBILITY_KW, TokenType.IMPLIED_KW));
        }

    }

    // DefinitiveIdentification ::=
    // | DefinitiveOID
    // | DefinitiveOIDandIRI
    // | empty
    // DefinitiveOIDandIRI ::=
    // DefinitiveOID
    // IRIValue
    protected class DefinitiveIdentificationParser extends ListRuleParser<DefinitiveIdentificationNode> {

        public DefinitiveIdentificationNode parse() throws ParserException {
            return super.parse(new SequenceParser(new boolean[] { true, false }, definitiveOIDParser, iriValueParser),
                    a -> new DefinitiveIdentificationNode(a.P0(), a.n0(), a.n1()));
        }

    }

    // DefinitiveNameAndNumberForm ::= identifier "(" DefinitiveNumberForm ")"
    protected class DefinitiveNameAndNumberFormParser extends ListRuleParser<OIDComponentNode> {

        public OIDComponentNode parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.IDENTIFIER, TokenType.L_PAREN,
                    definitiveNumberFormParser, TokenType.R_PAREN), a -> ((OIDComponentNode) a.n2()).name(a.s0()));
        }

    }

    // DefinitiveNumberForm ::= number
    protected class DefinitiveNumberFormParser extends ListRuleParser<OIDComponentNode> {

        public OIDComponentNode parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.NUMBER),
                    a -> new OIDComponentNode(a.P0(), Integer.parseInt(a.s0())));
        }

    }

    // NameForm ::= identifier
    protected class NameFormParser extends ListRuleParser<OIDComponentNode> {

        public OIDComponentNode parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.IDENTIFIER), a -> new OIDComponentNode(a.P0(), a.s0()));
        }

    }

    // DefinitiveObjIdComponent ::=
    // NameForm
    // | DefinitiveNumberForm
    // | DefinitiveNameAndNumberForm
    protected class DefinitiveObjIdComponentParser implements RuleParser<OIDComponentNode> {

        @SuppressWarnings("unchecked")
        public OIDComponentNode parse() throws ParserException {
            return new ChoiceParser<>(definitiveNumberFormParser, definitiveNameAndNumberFormParser,
                    nameFormParser).parse();
        }

    }

    // DefinitiveObjIdComponentList ::=
    // DefinitiveObjIdComponent
    // | DefinitiveObjIdComponent DefinitiveObjIdComponentList
    protected class DefinitiveObjIdComponentListParser extends ListRuleParser<OIDNode> {

        public OIDNode parse() throws ParserException {
            return super.parse(new RepetitionParser<>(definitiveObjIdComponentParser), a -> new OIDNode(a.P0(), a.p()));
        }

    }

    // DefinitiveOID ::=
    // "{" DefinitiveObjIdComponentList "}"
    protected class DefinitiveOIDParser extends ListRuleParser<OIDNode> {

        public OIDNode parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.L_BRACE, definitiveObjIdComponentListParser, TokenType.R_BRACE),
                    SequenceListAccessor::n1);
        }

    }

    // ModuleBody ::= Exports Imports AssignmentList | empty
    protected class ModuleBodyParser extends ListRuleParser<ModuleBodyNode> {

        @SuppressWarnings("unchecked")
        public ModuleBodyNode parse() throws ParserException {
            return super.parse(new SequenceParser(new boolean[] { true, false }, exportsParser, importsParser), a -> {
                        clearError();
                        return new ModuleBodyNode(a.P0(), a.n0(), a.n1(), assignmentListParser.parse());
                    }
            );
        }

    }

    // EXPORTS SymbolsExported ";"
    // | EXPORTS ALL ";"
    // | empty
    // SymbolsExported ::=
    // SymbolList | empty
    protected class ExportsParser extends ObjectRuleParser<ExportsNode> {

        @SuppressWarnings("unchecked")
        public ExportsNode parse() throws ParserException {
            super.parse(new ChoiceParser<Object>(
                    new SequenceParser(TokenType.EXPORTS_KW, TokenType.ALL_KW, TokenType.SEMICOLON),
                    new SequenceParser(TokenType.EXPORTS_KW, symbolListParser, TokenType.SEMICOLON)));

            if (matched()) {
                Object maybeSymbols = ((List<Object>) p()).get(1);

                if (maybeSymbols instanceof List) {
                    List<ReferenceNode> referenceNodeList = (List<ReferenceNode>) maybeSymbols;

                    return new ExportsNode(getPosition(referenceNodeList), ExportsNode.Mode.SPECIFIC, referenceNodeList);
                }
            }

            return new ExportsNode(NO_POSITION, ExportsNode.Mode.ALL);
        }

    }

    // SymbolList ::=
    // Symbol
    // | SymbolList "," Symbol
    protected class SymbolListParser implements RuleParser<List<ReferenceNode>> {

        public List<ReferenceNode> parse() throws ParserException {
            return new CommaSeparatedRuleParser<>(symbolParser).parse();
        }

    }

    // Symbol ::=
    // Reference
    // | ParameterizedReference
    // ParameterizedReference ::=
    // Reference | Reference "{" "}"
    protected class SymbolParser extends ObjectRuleParser<ReferenceNode> {

        @SuppressWarnings("unchecked")
        public ReferenceNode parse() throws ParserException {
            return super.parse(new ChoiceParser<>(new SequenceParser(referenceParser, TokenType.L_BRACE, TokenType.R_BRACE),
                    referenceParser), a -> a.p() instanceof List ?
                    ((ReferenceNode) ((List<Object>) a.p()).get(0)).parameterized(true) : (ReferenceNode) a.p());
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
            Token rule = new ChoiceParser<Token>(TokenType.TYPE_REFERENCE, TokenType.IDENTIFIER).parse();

            if (rule != null) {
                return new ReferenceNode(rule.getPosition(), rule.getText());
            }

            return null;
        }

    }

    // Imports ::=
    // IMPORTS SymbolsImported ";"
    // | empty
    protected class ImportsParser extends ListRuleParser<List<ImportNode>> {

        @SuppressWarnings("unchecked")
        public List<ImportNode> parse() throws ParserException {
            return super.parse(new SequenceParser(new boolean[] { true, false, true }, TokenType.IMPORTS_KW,
                    symbolsImportedParser, TokenType.SEMICOLON), a -> a.n1() == null ? new ArrayList<>() : a.n1());
        }

    }

    // SymbolsImported ::=
    // SymbolsFromModuleList
    // | empty
    protected class SymbolsImportedParser implements RuleParser<List<ImportNode>> {

        public List<ImportNode> parse() throws ParserException {
            return symbolsFromModuleListParser.parse();
        }

    }

    // SymbolsFromModuleList ::=
    // SymbolsFromModule
    // | SymbolsFromModuleList SymbolsFromModule
    protected class SymbolsFromModuleListParser implements RuleParser<List<ImportNode>> {

        public List<ImportNode> parse() throws ParserException {
            return new RepetitionParser<>(symbolsFromModuleParser).parse();
        }

    }

    // SymbolsFromModule ::=
    // SymbolList FROM GlobalModuleReference
    protected class SymbolsFromModuleParser extends ListRuleParser<ImportNode> {

        @SuppressWarnings("unchecked")
        public ImportNode parse() throws ParserException {
            return super.parse(new SequenceParser(symbolListParser, TokenType.FROM_KW, globalModuleReferenceParser),
                    a -> new ImportNode(a.P0(), a.n0(), a.n2()));
        }

    }

    // GlobalModuleReference ::=
    // modulereference AssignedIdentifier
    protected class GlobalModuleReferenceParser extends ListRuleParser<ModuleRefNode> {

        public ModuleRefNode parse() throws ParserException {
            return super.parse(new SequenceParser(new boolean[] { true, false }, TokenType.TYPE_REFERENCE,
                            assignedIdentifierParser),
                    a -> a.p().size() == 1 ? new ModuleRefNode(a.P0(), a.s0())
                            : new ModuleRefNode(a.P0(), a.s0(), a.n1()));
        }

    }

    // AssignedIdentifier ::=
    // ObjectIdentifierValue
    // | DefinedValue
    // | empty
    protected class AssignedIdentifierParser extends ObjectRuleParser<ObjectIdentifierValue> {

        @SuppressWarnings("unchecked")
        public ObjectIdentifierValue parse() throws ParserException {
            return super.parse(new ChoiceParser<>(objectIdentifierValueParser,
                            new ValueExtractor<Node>(0, new SequenceParser(definedValueParser,
                                    new NegativeLookaheadParser(TokenType.COMMA, TokenType.FROM_KW)))),
                    a -> a.n() instanceof ObjectIdentifierValue ? (ObjectIdentifierValue) a.n() :
                            new ObjectIdentifierValue(a.P(), (DefinedValue) a.n()));
        }

    }

    // ObjectIdentifierValue ::=
    // "{" ObjIdComponentsList "}"
    // | "{" DefinedValue ObjIdComponentsList "}"
    protected class ObjectIdentifierValueParser implements RuleParser<ObjectIdentifierValue> {

        public ObjectIdentifierValue parse() throws ParserException {
            List<OIDComponentNode> rule = new ValueExtractor<List<OIDComponentNode>>(1,
                    new SequenceParser(TokenType.L_BRACE, objIdComponentsListParser, TokenType.R_BRACE)).parse();

            if (rule != null) {
                return new ObjectIdentifierValue(getPosition(rule), rule);
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
    protected class NumberFormParser extends ObjectRuleParser<OIDComponentNode> {

        @SuppressWarnings("unchecked")
        public OIDComponentNode parse() throws ParserException {
            try {
                return super.parse(new ChoiceParser<>(new SingleTokenParser(TokenType.NUMBER), definedValueParser),
                        a -> a.p() instanceof Token ? new OIDComponentNode(a.P(), Integer.parseInt(a.s())) :
                                new OIDComponentNode(a.P(), (DefinedValue) a.n()));
            } catch (NumberFormatException e) {
                return null;
            }
        }

    }

    // NameAndNumberForm ::=
    // identifier "(" NumberForm ")"
    protected class NameAndNumberFormParser extends ListRuleParser<OIDComponentNode> {

        public OIDComponentNode parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.IDENTIFIER, TokenType.L_PAREN, numberFormParser,
                            TokenType.R_PAREN), a -> ((OIDComponentNode)a.n2()).name(a.s0()));
        }

    }

    // DefinedValue ::=
    // ExternalValueReference
    // | valuereference
    // | ParameterizedValue
    protected class DefinedValueParser implements RuleParser<DefinedValue> {

        @SuppressWarnings("unchecked")
        public DefinedValue parse() throws ParserException {
            return new ChoiceParser<>(parameterizedValueParser, externalValueReferenceParser,
                    valueReferenceParser).parse();
        }

    }

    // valuereference
    protected class ValueReferenceParser extends ObjectRuleParser<DefinedValue> {

        public DefinedValue parse() throws ParserException {
            return super.parse(new SingleTokenParser(TokenType.IDENTIFIER), a -> new SimpleDefinedValue(a.P(), a.s()));
        }

    }

    // ExternalValueReference ::=
    // modulereference
    // "."
    // valuereference
    protected class ExternalValueReferenceParser extends ListRuleParser<SimpleDefinedValue> {

        public SimpleDefinedValue parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.TYPE_REFERENCE, TokenType.DOT, TokenType.IDENTIFIER),
                    a -> new ExternalValueReference(a.P0(), a.s0(), a.s2()));
        }

    }

    // AssignmentList ::=
    // Assignment
    // | AssignmentList Assignment
    protected class AssignmentListParser implements RuleParser<List<AssignmentNode>> {

        public List<AssignmentNode> parse() throws ParserException {
            return new RepetitionParser<>(assignmentParser).parse();
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
            return new ChoiceParser<>(typeAssignmentParser, valueAssignmentParser, valueSetTypeAssignmentParser,
                    parameterizedAssignmentParser).parse();
        }

    }

    // TypeAssignment ::= typereference "::=" Type
    // ObjectClassAssignment ::= objectclassreference "::=" ObjectClass
    protected class TypeAssignmentParser extends ListRuleParser<TypeOrObjectClassAssignmentNode> {

        public TypeOrObjectClassAssignmentNode parse() throws ParserException {
            Set<List<Object>> rules = new AmbiguousChoiceParser<>(
                    new SequenceParser(TokenType.TYPE_REFERENCE, TokenType.ASSIGN, typeParser),
                    new SequenceParser(new SingleTokenParser(TokenType.OBJECT_CLASS_REFERENCE, Context.OBJECT_CLASS),
                            TokenType.ASSIGN, objectClassParser)).parse();

            Optional<List<Object>> first = rules.stream().findFirst();

            if (first.isPresent()) {
                Token token = (Token) first.get().get(0);
                TypeOrObjectClassAssignmentNode rule =
                        new TypeOrObjectClassAssignmentNode(token.getPosition(), token.getText());

                rules.stream().forEach(r -> {
                    TokenType tokenType = ((Token) r.get(0)).getType();

                    switch (tokenType) {
                        case TYPE_REFERENCE:
                            rule.setTypeAssignment(new TypeAssignmentNode(rule.getPosition(),
                                    rule.getReference(), (Type) r.get(2)));
                            break;
                        case OBJECT_CLASS_REFERENCE:
                            rule.setObjectClassAssignment(new ObjectClassAssignmentNode(rule.getPosition(),
                                    rule.getReference(), (ObjectClassNode) r.get(2)));
                            break;
                        default:
                            throw new IllegalStateException("Unexpected token type: " + tokenType.toString());
                    }
                });

                return rule;
            }

            return null;
        }

    }

    // Type ::= BuiltinType | ReferencedType | ConstrainedType
    // ConstrainedType ::=
    // Type Constraint
    // | TypeWithConstraint
    protected class TypeParser extends ObjectRuleParser<Type> {

        @SuppressWarnings("unchecked")
        public Type parse() throws ParserException {
            return super.parse(new ChoiceParser<>(typeWithConstraintParser,
                            new SequenceParser(new boolean[] { true, false },
                                    new ChoiceParser<Object>(builtinTypeParser, referencedTypeParser),
                                    new RepetitionParser<>(constraintParser))),
                    a -> {
                        if (a.n() instanceof Type) {
                            return (Type) a.n();
                        } else if (a.n() instanceof List) {
                            List<Object> ruleList = (List<Object>) a.n();
                            Type type = (Type) ruleList.get(0);

                            if (ruleList.get(1) != null) {
                                type.setConstraints((List<Constraint>) ruleList.get(1));
                            }

                            return type;
                        }

                        return null;
                    });
        }

    }

    // NamedType ::= identifier Type
    protected class NamedTypeParser extends ListRuleParser<NamedType> {

        public NamedType parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.IDENTIFIER, typeParser),
                    a -> new NamedType(a.P0(), a.s0(), a.n1()));
        }

    }

    protected class TypeOrNamedTypeParser extends ListRuleParser<Type> {

        public Type parse() throws ParserException {
            return super.parse(new SequenceParser(new boolean[] { false, true }, TokenType.IDENTIFIER, typeParser),
                    a -> a.n0() == null ? a.n1() : new NamedType(a.P0(), a.s0(), a.n1()));
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
            return new ChoiceParser<>(prefixedTypeParser, builtinTypeParserAux, characterStringTypeParser,
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
    public class BuiltinTypeParserAux implements RuleParser<Type> {

        @SuppressWarnings("unchecked")
        public Type parse() throws ParserException {
            mark();

            Token token = getToken();

            if (token == null) {
                resetToMark();
                return null;
            }

            Position position = token.getPosition();
            Type type = null;

            switch (token.getType()) {

                case BOOLEAN_KW:
                    type = new BooleanType(position);
                    break;

                case DATE_KW:
                    type = new DateType(position);
                    break;

                case DATE_TIME_KW:
                    type = new DateTime(position);
                    break;

                case DURATION_KW:
                    type = new Duration(position);
                    break;

                case EMBEDDED_KW:
                    if (expect(TokenType.PDV_KW) != null) {
                        type = new EmbeddedPDV(position);
                    } else {
                        resetToMark();
                        return null;
                    }
                    break;

                case BIT_KW:
                    List<Object> bitRule = new SequenceParser(new boolean[] { true, false }, TokenType.STRING_KW,
                            new ValueExtractor<List<NamedBitNode>>(1, new SequenceParser(TokenType.L_BRACE,
                                            namedBitListParser, TokenType.R_BRACE))).parse();

                    if (bitRule != null) {
                        if (bitRule.get(1) != null) {
                            type = new BitString(position, (List<NamedBitNode>) bitRule.get(1));
                        } else {
                            type = new BitString(position);
                        }
                    }
                    break;

                case NULL_KW:
                    type = new Null(position);
                    break;

                case REAL_KW:
                    type = new Real(position);
                    break;

                case INTEGER_KW:
                    List<NamedNumber> intRule = new ValueExtractor<List<NamedNumber>>(1,
                            new SequenceParser(TokenType.L_BRACE, namedNumberListParser, TokenType.R_BRACE)).parse();

                    if (intRule != null) {
                        type = new IntegerType(position, intRule);
                    } else {
                        type = new IntegerType(position);
                    }

                    break;

                case RELATIVE_OID_KW:
                    type = new RelativeOID(position);
                    break;

                case OID_IRI_KW:
                    type = new IRI(position);
                    break;

                case RELATIVE_OID_IRI_KW:
                    type = new RelativeIRI(position);
                    break;

                case OBJECT_KW:
                    if (expect(TokenType.IDENTIFIER_KW) != null) {
                        type = new ObjectIdentifier(position);
                    } else {
                        resetToMark();
                        return null;
                    }
                    break;

                case OCTET_KW:
                    if (expect(TokenType.STRING_KW) != null) {
                        type = new OctetString(position);
                    } else {
                        resetToMark();
                        return null;
                    }
                    break;

                case EXTERNAL_KW:
                    type = new External(position);
                    break;

                case CHOICE_KW:
                    List<Object> choiceRule = new SequenceParser(TokenType.L_BRACE,
                            alternativeTypeListsParser, TokenType.R_BRACE).parse();

                    if (choiceRule != null) {
                        type = new Choice(position, moduleName, (AlternativeTypeLists) choiceRule.get(1));
                    }
                    break;

                case ENUMERATED_KW:
                    List<Object> enumRule = new SequenceParser(TokenType.L_BRACE,
                            enumerationsParser, TokenType.R_BRACE).parse();

                    if (enumRule != null) {
                        type = (EnumeratedType) enumRule.get(1);
                    }
                    break;

                case TIME_KW:
                    type = new Time(position);
                    break;

                case TIME_OF_DAY_KW:
                    type = new TimeOfDay(position);
                    break;

                case INSTANCE_KW:
                    List<Object> instRule = new SequenceParser(TokenType.OF_KW,
                            definedObjectClassParser).parse();

                    if (instRule != null) {
                        type = new InstanceOfType(position, (ObjectClassReference) instRule.get(1));
                    }
                    break;

                case SEQUENCE_KW:
                case SET_KW:
                    if (expect(TokenType.OF_KW) != null) {
                        Type rule = typeOrNamedTypeParser.parse();

                        if (rule != null) {
                            type = token.getType() == TokenType.SEQUENCE_KW ?
                                    new SequenceOfType(position, moduleName, rule) :
                                    new SetOfType(position, moduleName, rule);
                        }
                    } else {
                        List<Object> rule = new SequenceParser(new boolean[] { true, false, true }, TokenType.L_BRACE,
                                new ChoiceParser<>(new SequenceParser(componentTypeListsParser),
                                        new SequenceParser(new boolean[] { true, false },
                                                extensionAndExceptionParser, optionalExtensionMarkerParser)),
                                TokenType.R_BRACE).parse();

                        if (rule != null) {
                            if (rule.get(1) == null) {
                                type = token.getType() == TokenType.SEQUENCE_KW ?
                                        new SequenceType(position, moduleName) :
                                        new SetType(position, moduleName);
                            } else {
                                List<Object> ruleList = (List<Object>) rule.get(1);

                                if (ruleList.size() == 1) {
                                    type = token.getType() == TokenType.SEQUENCE_KW ?
                                            new SequenceType(position, moduleName, (ComponentTypeListsNode) ruleList.get(0)) :
                                            new SetType(position, moduleName, (ComponentTypeListsNode) ruleList.get(0));
                                } else {
                                    type = token.getType() == TokenType.SEQUENCE_KW ?
                                            new SequenceType(position, moduleName, (ExtensionAndExceptionNode) ruleList.get(0), (Boolean) ruleList.get(1)):
                                            new SetType(position, moduleName, (ExtensionAndExceptionNode) ruleList.get(0), (Boolean) ruleList.get(1));
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
    protected class NamedBitListParser implements RuleParser<List<NamedBitNode>> {

        public List<NamedBitNode> parse() throws ParserException {
            return new CommaSeparatedRuleParser<>(namedBitParser).parse();
        }

    }

    // NamedBit ::=
    // identifier "(" number ")"
    // | identifier "(" DefinedValue ")"
    protected class NamedBitParser extends ListRuleParser<NamedBitNode> {

        @SuppressWarnings("unchecked")
        public NamedBitNode parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.IDENTIFIER, TokenType.L_PAREN,
                            new ChoiceParser<>(new SingleTokenParser(TokenType.NUMBER), definedValueParser),
                            TokenType.R_PAREN),
                    a -> a.n2() instanceof Token ? new NamedBitNode(a.P0(), a.s0(), Integer.parseInt(a.s2())) :
                            new NamedBitNode(a.P0(), a.s0(), a.n2()));
        }

    }

    // CharacterStringType ::=
    // RestrictedCharacterStringType
    // | UnrestrictedCharacterStringType
    protected class CharacterStringTypeParser implements RuleParser<Type> {

        @SuppressWarnings("unchecked")
        public Type parse() throws ParserException {
            return new ChoiceParser<>(restrictedCharacterStringTypeParser, unrestrictedCharacterStringTypeParser).parse();
        }

    }

    // Enumerations ::=
    // RootEnumeration
    // | RootEnumeration "," "..." ExceptionSpec
    // | RootEnumeration "," "..." ExceptionSpec "," AdditionalEnumeration
    protected class EnumerationsParser extends ListRuleParser<EnumeratedType> {

        @SuppressWarnings("unchecked")
        public EnumeratedType parse() throws ParserException {
            return super.parse(new SequenceParser(
                            new boolean[] { true, false }, enumerationParser,
                            new SequenceParser(new boolean[] { true, true, false, false },
                                    TokenType.COMMA, TokenType.ELLIPSIS, exceptionSpecParser,
                                    new SequenceParser(TokenType.COMMA, enumerationParser))),
                    a -> {
                        if (a.n1() == null) {
                            return new EnumeratedType(a.P0(), a.n0());
                        } else {
                            List<Object> ruleList = a.n1();
                            Object exceptionSpec = ruleList.get(2);

                            if (ruleList.get(3) == null) {
                                return new EnumeratedType(a.P0(), a.n0(),
                                        exceptionSpec != null ? (ExceptionIdentificationNode) exceptionSpec : null);
                            } else {
                                return new EnumeratedType(a.P0(), a.n0(),
                                        exceptionSpec != null ? (ExceptionIdentificationNode) exceptionSpec : null,
                                        (List<EnumerationItemNode>) ((List<Object>) ruleList.get(3)).get(1));
                            }
                        }
                    });
        }

    }

    // Enumeration ::= EnumerationItem | EnumerationItem "," Enumeration
    // RootEnumeration ::= Enumeration
    // AdditionalEnumeration ::= Enumeration
    protected class EnumerationParser implements RuleParser<List<EnumerationItemNode>> {

        public List<EnumerationItemNode> parse() throws ParserException {
            return new CommaSeparatedRuleParser<>(enumerationItemParser).parse();
        }

    }

    // EnumerationItem ::= identifier | NamedNumber
    protected class EnumerationItemParser implements RuleParser<EnumerationItemNode> {

        @SuppressWarnings("unchecked")
        public EnumerationItemNode parse() throws ParserException {
            Object rule = new ChoiceParser<>(namedNumberParser,    new SingleTokenParser(TokenType.IDENTIFIER)).parse();

            if (rule == null) {
                return null;
            }

            EnumerationItemNode item;

            if (rule instanceof Token) {
                item = new EnumerationItemNode(((Token) rule).getPosition());
                item.setName(((Token) rule).getText());
            } else {
                NamedNumber namedNumber = ((NamedNumber) rule);

                item = new EnumerationItemNode(namedNumber.getPosition());
                item.setName(((NamedNumber) rule).getId());

                // TODO: check for >= 0
                if (namedNumber.getValue() != null) {
                    BigInteger bigNumber = namedNumber.getValue().getNumber();

                    if (bigNumber.bitLength() > 31) {
                        throw new ParserException("Enumeration value too long: " + bigNumber.toString());
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
    protected class NamedNumberListParser implements RuleParser<List<NamedNumber>> {

        public List<NamedNumber> parse() throws ParserException {
            return new CommaSeparatedRuleParser<>(namedNumberParser).parse();
        }

    }

    // NamedNumber ::=
    // identifier "(" SignedNumber ")"
    // | identifier "(" DefinedValue ")"
    protected class NamedNumberParser extends ListRuleParser<NamedNumber> {

        @SuppressWarnings("unchecked")
        public NamedNumber parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.IDENTIFIER, TokenType.L_PAREN,
                            new ChoiceParser<Object>(signedNumberParser, definedValueParser), TokenType.R_PAREN),
                    a -> a.n2() instanceof DefinedValue ? new NamedNumber(a.P0(), a.s0(), (DefinedValue) a.n2()) :
                            new NamedNumber(a.P0(), a.s0(), (SignedNumber) a.n2()));
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
    protected class ComponentTypeListsParser implements RuleParser<ComponentTypeListsNode> {

        @SuppressWarnings("unchecked")
        public ComponentTypeListsNode parse() throws ParserException {
            List<Object> rule = new ChoiceParser<>(
                    new SequenceParser(new boolean[] { true, false },
                            componentTypeListParser, new SequenceParser(
                                    new boolean[] { true, true, false, false },
                                    TokenType.COMMA,
                                    extensionAndExceptionParser,
                                    extensionAdditionsParser,
                                    new ChoiceParser<>(
                                            new SequenceParser(
                                                    extensionEndMarkerParser,
                                                    TokenType.COMMA,
                                                    componentTypeListParser),
                                            optionalExtensionMarkerParser))),
                    new SequenceParser(new boolean[] { true, false, false },
                            extensionAndExceptionParser,
                            extensionAdditionsParser, new ChoiceParser<>(
                                    new SequenceParser(
                                            extensionEndMarkerParser,
                                            TokenType.COMMA,
                                            componentTypeListParser),
                                    optionalExtensionMarkerParser))).parse();

            if (rule != null) {
                if (rule.size() == 2) {
                    List<Object> extList = (List<Object>) rule.get(1);

                    List<ComponentType> componentTypes = (List<ComponentType>) rule.get(0);
                    Position position = getPosition(componentTypes);

                    if (extList != null) {
                        if (extList.get(3) instanceof Boolean) {
                            // RootComponentTypeList "," ExtensionAndException
                            // ExtensionAdditions OptionalExtensionMarker
                            return new ComponentTypeListsNode(position, componentTypes,
                                    (ExtensionAndExceptionNode) extList.get(1),
                                    toExtensionAdditionGroup(extList.get(2)), (Boolean) extList.get(3));
                        } else {
                            // RootComponentTypeList "," ExtensionAndException
                            // ExtensionAdditions ExtensionEndMarker ","
                            // RootComponentTypeList
                            return new ComponentTypeListsNode(position, componentTypes,
                                    (ExtensionAndExceptionNode) extList.get(1),
                                    toExtensionAdditionGroup(extList.get(2)), false,
                                    (List<ComponentType>) ((List<Object>) extList.get(3)).get(2));
                        }
                    } else {
                        // RootComponentTypeList
                        return new ComponentTypeListsNode(position, componentTypes);
                    }
                } else {
                    Object obj = rule.get(2);
                    ExtensionAndExceptionNode extensionAndExceptionNode = (ExtensionAndExceptionNode) rule.get(0);
                    Position position = extensionAndExceptionNode.getPosition();

                    if (obj instanceof Boolean) {
                        // ExtensionAndException ExtensionAdditions
                        // OptionalExtensionMarker
                        return new ComponentTypeListsNode(position, null,extensionAndExceptionNode,
                                toExtensionAdditionGroup(rule.get(1)), (Boolean) obj);
                    } else {
                        // ExtensionAndException ExtensionAdditions
                        // ExtensionEndMarker "," RootComponentTypeList
                        return new ComponentTypeListsNode(position, null, extensionAndExceptionNode,
                                toExtensionAdditionGroup(rule.get(1)), true,
                                (List<ComponentType>) ((List<Object>) obj).get(2));
                    }
                }
            }

            return null;
        }

        private List<ExtensionAdditionGroup> toExtensionAdditionGroup(Object rule) {
            if (rule == null) {
                return null;
            }

            if (rule instanceof List) {
                return (List<ExtensionAdditionGroup>) ((List) rule).stream().map(element -> {
                    if (element instanceof ComponentType) {
                        return new ExtensionAdditionGroup(((ComponentType) element).getPosition(),
                                List.of((ComponentType)element));
                    } else if (element instanceof ExtensionAdditionGroup) {
                        return element;
                    }

                    throw new IllegalStateException("Unhandled element: " + element);
                }).collect(Collectors.toList());
            }

            throw new IllegalStateException("Unhandled rule: " + rule);
        }

    }

    // ExtensionEndMarker ::= "," "..."
    protected class ExtensionEndMarkerParser implements RuleParser<Object> {

        public Object parse() throws ParserException {
            return new SequenceParser(TokenType.COMMA, TokenType.ELLIPSIS).parse();
        }

    }

    // ExtensionAdditions ::=
    // "," ExtensionAdditionList
    // | empty
    protected class ExtensionAdditionsParser implements RuleParser<Object> {

        public List<Object> parse() throws ParserException {
            return new ValueExtractor<List<Object>>(1, new SequenceParser(
                    TokenType.COMMA, extensionAdditionListParser)).parse();
        }

    }

    // ExtensionAdditionList ::=
    // ExtensionAddition
    // | ExtensionAdditionList "," ExtensionAddition
    protected class ExtensionAdditionListParser implements RuleParser<List<Object>> {

        public List<Object> parse() throws ParserException {
            return new CommaSeparatedRuleParser<>(extensionAdditionParser).parse();
        }

    }

    // ExtensionAddition ::=
    // ComponentType
    // | ExtensionAdditionGroup
    protected class ExtensionAdditionParser implements RuleParser<Object> {

        @SuppressWarnings("unchecked")
        public Object parse() throws ParserException {
            return new ChoiceParser<Object>(componentTypeParser, extensionAdditionGroupParser).parse();
        }

    }

    // ExtensionAdditionGroup ::= "[[" VersionNumber ComponentTypeList "]]"
    protected class ExtensionAdditionGroupParser extends ListRuleParser<ExtensionAdditionGroup> {

        @SuppressWarnings("unchecked")
        public ExtensionAdditionGroup parse() throws ParserException {
            return super.parse(new SequenceParser(new boolean[] { true, false, true, true },
                    TokenType.L_VERSION_BRACKETS, versionNumberParser, componentTypeListParser,
                    TokenType.R_VERSION_BRACKETS), a -> new ExtensionAdditionGroup(a.P0(), a.n1(), a.n2()));
        }

    }

    // ComponentTypeList ::=
    // ComponentType
    // | ComponentTypeList "," ComponentType
    protected class ComponentTypeListParser implements RuleParser<List<ComponentType>> {

        public List<ComponentType> parse() throws ParserException {
            return new CommaSeparatedRuleParser<>(componentTypeParser).parse();
        }

    }

    // ComponentType ::=
    // NamedType
    // | NamedType OPTIONAL
    // | NamedType DEFAULT Value
    // | COMPONENTS OF Type
    protected class ComponentTypeParser extends ObjectRuleParser<ComponentType> {

        @SuppressWarnings("unchecked")
        public ComponentType parse() throws ParserException {
            super.parse(new ChoiceParser<>(
                    new SequenceParser(namedTypeParser, TokenType.OPTIONAL_KW),
                    new SequenceParser(namedTypeParser, TokenType.DEFAULT_KW, valueParser),
                    new SequenceParser(namedTypeParser),
                    new SequenceParser(TokenType.COMPONENTS_KW, TokenType.OF_KW, typeParser)));

            List<Object> rule = (List<Object>) p();

            if (rule != null) {
                switch (rule.size()) {
                    case 1:
                        return new ComponentType(P(), ComponentType.CompType.NAMED_TYPE, (NamedType) rule.get(0));
                    case 2:
                        return new ComponentType(P(), ComponentType.CompType.NAMED_TYPE_OPT, (NamedType) rule.get(0));
                    case 3:
                        if (rule.get(0) instanceof Token) {
                            return new ComponentType(P(), ComponentType.CompType.TYPE, (Type) rule.get(2));
                        } else {
                            return new ComponentType(P(), ComponentType.CompType.NAMED_TYPE_DEF, (NamedType) rule.get(0),
                                    (Value) rule.get(2));
                        }
                    default:
                        throw new ParserException("Invalid rule");
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
            return new ChoiceParser<>(taggedTypeParser, encodingPrefixedTypeParser).parse();
        }

    }

    // EncodingPrefixedType ::=
    // EncodingPrefix Type
    protected class EncodingPrefixedTypeParser extends ListRuleParser<Type> {

        public Type parse() throws ParserException {
            return super.parse(new SequenceParser(encodingPrefixParser, typeParser), a -> {
                Type type = a.n1();
                type.setEncodingPrefix(a.n0());
                return type;
            });
        }

    }

    // EncodingPrefix ::=
    // "[" EncodingReference EncodingInstruction "]"
    protected class EncodingPrefixParser extends ListRuleParser<EncodingPrefixNode> {

        public EncodingPrefixNode parse() throws ParserException {
            return super.parse(new SequenceParser(new boolean[] { true, false }, TokenType.L_BRACKET,
                            encodingReferenceParser),
                    a -> {
                        List<Token> encodingInstruction = new ArrayList<>();

                        while (true) {
                            mark();

                            Token token = getToken();

                            if (token == null) {
                                throw new ParserException("Premature EOF");
                            } else if (token.getType() == TokenType.R_BRACKET) {
                                break;
                            } else if (token.getType() == TokenType.L_BRACKET) {
                                throw new ParserException("Invalid token " + token + " in EncodingInstruction");
                            }

                            clearMark();
                            encodingInstruction.add(token);
                        }

                        return new EncodingPrefixNode(a.P(), a.n1(), encodingInstruction);
                    });
        }

    }

    // TaggedType ::=
    // Tag Type
    // | Tag IMPLICIT Type
    // | Tag EXPLICIT Type
    protected class TaggedTypeParser extends ListRuleParser<Type> {

        public Type parse() throws ParserException {
            return super.parse(new SequenceParser(new boolean[] { true, false, true }, tagParser,
                    new ChoiceParser<Token>(TokenType.IMPLICIT_KW, TokenType.EXPLICIT_KW), typeParser), a -> {
                Type type = a.n2();
                type.addTag(a.n0());
                Token token = a.t1();

                if (token == null) {
                    type.addTaggingMode(Optional.empty());
                } else {
                    TaggingMode taggingMode = a.$1() == TokenType.IMPLICIT_KW ? TaggingMode.IMPLICIT : TaggingMode.EXPLICIT;

                    type.addTaggingMode(Optional.of(taggingMode));
                }

                return type;
            });
        }

    }

    // Tag ::= "[" EncodingReference Class ClassNumber "]"
    protected class TagParser extends ListRuleParser<Tag> {

        public Tag parse() throws ParserException {
            return super.parse(new SequenceParser(new boolean[] { true, false, false, true, true },
                            TokenType.L_BRACKET, encodingReferenceParser, classParser, classNumberParser,
                            TokenType.R_BRACKET), a -> new Tag(a.P(), a.n1(), a.n2(), a.n3()));
        }

    }

    // EncodingReference ::=
    // encodingreference ":"
    // | empty
    protected class EncodingReferenceParser extends ListRuleParser<String> {

        public String parse() throws ParserException {
            return super.parse(new SequenceParser(
                            new SingleTokenParser(TokenType.ENCODING_REFERENCE, Context.ENCODING), TokenType.COLON),
                    a -> a.t0() != null ? a.s0() : null);
        }
    }

    // ClassNumber ::=
    // number
    // | DefinedValue
    protected class ClassNumberParser extends ObjectRuleParser<ClassNumber> {

        @SuppressWarnings("unchecked")
        public ClassNumber parse() throws ParserException {
            return super.parse(new ChoiceParser<>(new SingleTokenParser(TokenType.NUMBER), definedValueParser), a ->
                    a.p() instanceof Token ? new ClassNumber(a.P(), Integer.parseInt(a.s())) :
                            new ClassNumber(a.P(), (DefinedValue) a.n())
            );
        }

    }

    // Class ::=
    // UNIVERSAL
    // | APPLICATION
    // | PRIVATE
    // | empty
    protected class ClassParser extends ObjectRuleParser<ClassType> {

        public ClassType parse() throws ParserException {
            return super.parse(new ChoiceParser<Token>(TokenType.UNIVERSAL_KW,
                    TokenType.APPLICATION_KW, TokenType.PRIVATE_KW), a -> {
                switch (a.$()) {
                    case UNIVERSAL_KW:
                        return ClassType.UNIVERSAL;
                    case APPLICATION_KW:
                        return ClassType.APPLICATION;
                    case PRIVATE_KW:
                        return ClassType.PRIVATE;
                    default:
                        return null;
                }
            });
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
    protected class RestrictedCharacterStringTypeParser implements RuleParser<Type> {

        @SuppressWarnings("unchecked")
        public Type parse() throws ParserException {
            return new ChoiceParser<>(bmpStringParser, generalStringParser, graphicStringParser, ia5StringParser,
                    iso646StringParser, numericStringParser, printableStringParser, teletexStringParser,
                    t61StringParser, universalStringParser, utf8StringParser, videotexStringParser,
                    visibleStringParser).parse();
        }


    }

    // AlternativeTypeLists ::=
    // RootAlternativeTypeList
    // | RootAlternativeTypeList ","
    // ExtensionAndException ExtensionAdditionAlternatives
    // OptionalExtensionMarker
    protected class AlternativeTypeListsParser implements RuleParser<AlternativeTypeLists> {

        @SuppressWarnings("unchecked")
        public AlternativeTypeLists parse() throws ParserException {
            List<Object> rule = new ChoiceParser<>(
                    new SequenceParser(new boolean[] { true, true, true, false,    false },
                            rootAlternativeTypeListParser, TokenType.COMMA, extensionAndExceptionParser,
                            extensionAdditionAlternativesParser, optionalExtensionMarkerParser),
                    new SequenceParser(rootAlternativeTypeListParser)).parse();

            if (rule != null) {
                List<NamedType> namedTypeList = (List<NamedType>) rule.get(0);
                Position position = getPosition(namedTypeList);

                if (rule.size() == 1) {
                    return new AlternativeTypeLists(position, namedTypeList);
                } else {
                    return new AlternativeTypeLists(position, namedTypeList,
                            (ExtensionAndExceptionNode) rule.get(2),
                            (List<ExtensionAdditionAlternativeNode>) rule.get(3), (Boolean) rule.get(4));
                }
            }

            return null;
        }

    }

    // OptionalExtensionMarker ::= "," "..." | empty
    protected class OptionalExtensionMarkerParser extends ListRuleParser<Boolean> {

        public Boolean parse() throws ParserException {
            return match(new SequenceParser(TokenType.COMMA, TokenType.ELLIPSIS));
        }

    }

    // RootAlternativeTypeList ::= AlternativeTypeList
    protected class RootAlternativeTypeListParser implements RuleParser<List<NamedType>> {

        public List<NamedType> parse() throws ParserException {
            return alternativeTypeListParser.parse();
        }

    }

    // ExtensionAdditionAlternatives ::=
    // "," ExtensionAdditionAlternativesList
    // | empty
    protected class ExtensionAdditionAlternativesParser extends ListRuleParser<List<ExtensionAdditionAlternativeNode>> {

        @SuppressWarnings("unchecked")
        public List<ExtensionAdditionAlternativeNode> parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.COMMA, extensionAdditionAlternativesListParser),
                    ListAccessor::n1);
        }

    }

    // ExtensionAdditionAlternativesList ::=
    // ExtensionAdditionAlternative
    // | ExtensionAdditionAlternativesList "," ExtensionAdditionAlternative
    protected class ExtensionAdditionAlternativesListParser
            implements RuleParser<List<ExtensionAdditionAlternativeNode>> {

        public List<ExtensionAdditionAlternativeNode> parse() throws ParserException {
            return new CommaSeparatedRuleParser<>(extensionAdditionAlternativeParser).parse();
        }

    }

    // ExtensionAdditionAlternative ::=
    // ExtensionAdditionAlternativesGroup
    // | NamedType
    protected class ExtensionAdditionAlternativeParser extends ObjectRuleParser<ExtensionAdditionAlternativeNode> {

        @SuppressWarnings("unchecked")
        public ExtensionAdditionAlternativeNode parse() throws ParserException {
            return super.parse(new ChoiceParser<Object>(namedTypeParser, extensionAdditionAlternativesGroupParser),
                    a -> a.p() instanceof NamedType ?
                            new ExtensionAdditionAlternativeNode(a.P(), (NamedType) a.p()) :
                            new ExtensionAdditionAlternativeNode(a.P(), (ExtensionAdditionAlternativesGroup) a.p()));
        }

    }

    // ExtensionAdditionAlternativesGroup ::=
    // "[[" VersionNumber AlternativeTypeList "]]"
    protected class ExtensionAdditionAlternativesGroupParser
            extends ListRuleParser<ExtensionAdditionAlternativesGroup> {

        @SuppressWarnings("unchecked")
        public ExtensionAdditionAlternativesGroup parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.L_VERSION_BRACKETS, versionNumberParser,
                    alternativeTypeListParser, TokenType.R_VERSION_BRACKETS),
                    a -> new ExtensionAdditionAlternativesGroup(a.P(), a.n1(), a.n2()));
        }

    }

    // VersionNumber ::= empty | number ":"
    protected class VersionNumberParser extends ListRuleParser<Integer> {

        public Integer parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.NUMBER, TokenType.COLON), a -> Integer.valueOf(a.s0()));
        }

    }

    // AlternativeTypeList ::=
    // NamedType
    // | AlternativeTypeList "," NamedType
    protected class AlternativeTypeListParser implements RuleParser<List<NamedType>> {

        public List<NamedType> parse() throws ParserException {
            return new CommaSeparatedRuleParser<>(namedTypeParser).parse();
        }

    }

    // ExtensionAndException ::= "..." | "..." ExceptionSpec
    protected class ExtensionAndExceptionParser extends ListRuleParser<ExtensionAndExceptionNode> {

        public ExtensionAndExceptionNode parse() throws ParserException {
            return super.parse(new SequenceParser(new boolean[] { true, false }, TokenType.ELLIPSIS,
                            exceptionSpecParser), a -> a.p().size() > 1 ? new ExtensionAndExceptionNode(a.P(), a.n1()) :
                            new ExtensionAndExceptionNode(a.P()));
        }

    }

    // ExceptionSpec ::= "!" ExceptionIdentification | empty
    protected class ExceptionSpecParser implements RuleParser<ExceptionIdentificationNode> {

        public ExceptionIdentificationNode parse() throws ParserException {
            return new ValueExtractor<ExceptionIdentificationNode>(1,
                    new SequenceParser(TokenType.EXCLAMATION, exceptionIdentificationParser)).parse();
        }

    }

    // ExceptionIdentification ::=
    // SignedNumber
    // | DefinedValue
    // | Type ":" Value
    protected class ExceptionIdentificationParser extends ObjectRuleParser<ExceptionIdentificationNode> {

        @SuppressWarnings("unchecked")
        public ExceptionIdentificationNode parse() throws ParserException {
            return super.parse(new ChoiceParser<>(
                    new SequenceParser(signedNumberParser),
                    new SequenceParser(definedValueParser),
                    new SequenceParser(typeParser, TokenType.COLON, valueParser)),
                    a -> {
                        List<Object> rule = (List<Object>) a.p();

                        if (rule.size() == 1) {
                            return new ExceptionIdentificationNode(a.P(), (Value) rule.get(0));
                        } else {
                            return new ExceptionIdentificationNode(a.P(), (Type) rule.get(0), (Value) rule.get(2));
                        }
                    });
        }

    }

    // SignedNumber ::=
    // number
    // | "-" number
    protected class SignedNumberParser extends ListRuleParser<SignedNumber> {

        public SignedNumber parse() throws ParserException {
            return super.parse(new SequenceParser(new boolean[] { false, true }, TokenType.MINUS, TokenType.NUMBER),
                    a -> a.t0() != null ? new SignedNumber(a.P(), new BigInteger("-" + a.s1())) :
                            new SignedNumber(a.P1(), new BigInteger(a.s1())));
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
            Set<Value> rules = new AmbiguousChoiceParser<>(bitStringValueParser, booleanValueParser, realValueParser,
                    characterStringValueParser, choiceValueParser, valueFromObjectParser, definedValueParser,
                    embeddedPDVValueParser, integerValueParser, iriValueParser, nullValueParser,
                    objectIdentifierValueParser, relativeIRIValueParser, relativeOIDValueParser,
                    collectionValueParser, emptyValueParser).parse();

            if (rules.size() == 1) {
                return rules.iterator().next();
            } else if (rules.size() > 1) {
                return new AmbiguousValue(rules.iterator().next().getPosition(), rules);
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
    protected class BitOrOctetStringValueParser extends ObjectRuleParser<Value> {

        @SuppressWarnings("unchecked")
        public Value parse() throws ParserException {
            return super.parse(new ChoiceParser<>(new SingleTokenParser(TokenType.B_STRING),
                    new SingleTokenParser(TokenType.H_STRING),
                    new SequenceParser(TokenType.L_BRACE, identifierListParser,
                            TokenType.R_BRACE), new SequenceParser(TokenType.CONTAINING_KW, valueParser)),
                    a -> {
                        if (a.p() instanceof List) {
                            List<Object> list = (List<Object>) a.p();

                            if (list.size() == 3) {
                                return new BitStringValue(a.P(), (List<String>) list.get(1));
                            } else if (TokenType.CONTAINING_KW == ((Token) list.get(0)).getType()) {
                                return new ContainingStringValue(a.P(), (Value) ((List<Object>) a.p()).get(1));
                            } else {
                                return new EmptyValue(a.P());
                            }
                        } else {
                            String value = a.s();

                            if (a.$() == TokenType.B_STRING) {
                                return new BinaryStringValue(a.P(), value);
                            } else {
                                return new HexStringValue(a.P(), value);
                            }
                        }
                    });
        }

    }

    // IdentifierList ::=
    // identifier
    // | IdentifierList "," identifier
    protected class IdentifierListParser extends ListRuleParser<List<String>> {

        public List<String> parse() throws ParserException {
            return super.parse(new CommaSeparatedRuleParser<>(new SingleTokenParser(TokenType.IDENTIFIER)),
                    a -> a.p().stream().map(t -> ((Token) t).getText()).collect(Collectors.toList()));
        }

    }

    // BooleanValue ::= TRUE | FALSE
    protected class BooleanValueParser extends ObjectRuleParser<Value> {

        public Value parse() throws ParserException {
            return super.parse(new ChoiceParser<Token>(TokenType.TRUE_KW, TokenType.FALSE_KW), a ->
                new BooleanValue(a.P(), a.$() == TokenType.TRUE_KW));
        }

    }

    // CharacterStringValue ::=
    // RestrictedCharacterStringValue
    // | UnrestrictedCharacterStringValue
    protected class CharacterStringValueParser implements RuleParser<Value> {

        @SuppressWarnings("unchecked")
        public Value parse() throws ParserException {
            return new ChoiceParser<>(restrictedCharacterStringValueParser, unrestrictedCharacterStringValue).parse();
        }

    }

    // RestrictedCharacterStringValue ::=
    // cstring
    // | CharacterStringList
    // | Quadruple
    // | Tuple
    // TimeValue ::= tstring
    protected class RestrictedCharacterStringValueParser extends ObjectRuleParser<Value> {

        @SuppressWarnings("unchecked")
        public Value parse() throws ParserException {
            super.parse(new ChoiceParser<>(new SingleTokenParser(TokenType.C_STRING), characterStringListParser,
                    collectionValueParser));

            if (matched()) {
                if (p() instanceof StringToken) {
                    StringToken stringToken = (StringToken) p();
                    StringValue string = new StringValue(stringToken.getPosition(), stringToken.getText(),
                            stringToken.getFlags());

                    if (!string.isCString() && !string.isTString()) {
                        return null;
                    }

                    return string;
                } else {
                    return (Value) p();
                }
            }

            return null;
        }

    }

    // CharacterStringList ::= "{" CharSyms "}"
    protected class CharacterStringListParser extends ListRuleParser<CharacterStringList> {

        @SuppressWarnings("unchecked")
        public CharacterStringList parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.L_BRACE, charSymsParser, TokenType.R_BRACE),
                    a -> new CharacterStringList(a.P(), a.n1()));
        }

    }

    // CharSyms ::=
    // CharsDefn
    // | CharSyms "," CharsDefn
    protected class CharSymsParser implements RuleParser<List<Value>> {

        public List<Value> parse() throws ParserException {
            return new CommaSeparatedRuleParser<>(charsDefnParser).parse();
        }

    }

    // CharsDefn ::=
    // cstring
    // | Quadruple
    // | Tuple
    // | DefinedValue
    protected class CharsDefnParser extends ObjectRuleParser<Value> {

        @SuppressWarnings("unchecked")
        public Value parse() throws ParserException {
            super.parse(new ChoiceParser<>(new SingleTokenParser(TokenType.C_STRING), collectionValueParser,
                    definedValueParser));

            if (matched()) {
                if (p() instanceof StringToken) {
                    StringToken stringToken = (StringToken) p();
                    StringValue string = new StringValue(stringToken.getPosition(), stringToken.getText(),
                            stringToken.getFlags());

                    if (!string.isCString()) {
                        throw new ParserException("Invalid cstring: " + stringToken.getText());
                    }

                    return string;
                } else if (p() instanceof Value) {
                    return (Value) p();
                }
            }

            return null;
        }

    }

    // UnrestrictedCharacterStringValue ::= SequenceValue
    protected class UnrestrictedCharacterStringValue implements RuleParser<Value> {

        public Value parse() throws ParserException {
            return collectionValueParser.parse();
        }

    }

    // ChoiceValue ::= identifier ":" Value
    protected class ChoiceValueParser extends ListRuleParser<ChoiceValue> {

        public ChoiceValue parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.IDENTIFIER, TokenType.COLON, valueParser),
                    a -> new ChoiceValue(a.P0(), a.s0(), a.n2()));
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
    protected class IntegerValueParser extends ObjectRuleParser<IntegerValue> {

        @SuppressWarnings({ "unchecked" })
        public IntegerValue parse() throws ParserException {
            // Ignore identifier. Will be parsed as SimpleDefinedValue
            return super.parse(signedNumberParser, a -> new IntegerValue(a.P(), ((SignedNumber) a.n()).getNumber()));
        }

    }

    private List<IRIToken> parseArcIdSequence(String iriValue, boolean relative) throws ParserException {
        IRILexer iriLexer = new IRILexer(iriValue);
        List<IRIToken> iriTokens = new ArrayList<>();
        IRIToken iriToken;
        boolean idExpected = relative;

        do {
            iriToken = iriLexer.nextToken();

            if (iriToken == null) {
                break;
            }

            if (idExpected) {
                if (iriToken.getType() == IRIToken.Type.SOLIDUS) {
                    throw new ParserException("(non-)integerUnicodeLabel expected");
                }

                iriTokens.add(iriToken);
            } else {
                if (iriToken.getType() != IRIToken.Type.SOLIDUS) {
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
    protected class IRIValueParser extends ObjectRuleParser<Value> {

        public Value parse() throws ParserException {
            return super.parse(new SingleTokenParser(TokenType.C_STRING), a -> {
                List<IRIToken> iriTokens = parseArcIdSequence(a.s(),false);

                if (iriTokens.isEmpty()) {
                    throw new ParserException("Empty IRIValue");
                }

                return new IRIValue(a.P(), iriTokens);
            });
        }

    }

    // NullValue ::= NULL
    protected class NullValueParser implements RuleParser<Value> {

        public Value parse() throws ParserException {
            Token token = expect(TokenType.NULL_KW);

            if (token != null) {
                return new NullValue(token.getPosition());
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
    protected class NumericRealValueParser extends ObjectRuleParser<RealValue> {

        @SuppressWarnings("unchecked")
        public RealValue parse() throws ParserException {
            return super.parse(new ChoiceParser<>(new SingleTokenParser(TokenType.REAL_NUMBER),
                    new SequenceParser(TokenType.MINUS, TokenType.REAL_NUMBER)), a -> {
                Object rule = a.p();

                if (rule instanceof Token) {
                    return new RealValue(a.P(), new BigDecimal(a.s()));
                } else if (rule instanceof List) {
                    return new RealValue(a.l().P1(), new BigDecimal("-" + a.l().s1()));
                }

                return null;
            });
        }

    }

    // SpecialRealValue ::=
    // PLUS-INFINITY
    // | MINUS-INFINITY
    // | NOT-A-NUMBER
    protected class SpecialRealValueParser extends ObjectRuleParser<RealValue> {

        public RealValue parse() throws ParserException {
            return super.parse(new ChoiceParser<Token>(TokenType.PLUS_INFINITY_KW,
                    TokenType.MINUS_INFINITY_KW, TokenType.NOT_A_NUMBER_KW), a -> {
                switch (a.$()) {
                    case PLUS_INFINITY_KW:
                        return new RealValue(a.P(), RealValue.RealType.POSITIVE_INF);
                    case MINUS_INFINITY_KW:
                        return new RealValue(a.P(), RealValue.RealType.NEGATIVE_INF);
                    case NOT_A_NUMBER_KW:
                        return new RealValue(a.P(), RealValue.RealType.NAN);
                    default:
                        return null;
                }
            });
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
    protected class RelativeIRIValueParser extends ObjectRuleParser<Value> {

        public Value parse() throws ParserException {
            return super.parse(new SingleTokenParser(TokenType.C_STRING), a -> {
                List<IRIToken> iriTokens = parseArcIdSequence(a.s(), true);

                if (iriTokens.isEmpty()) {
                    throw new ParserException("Empty RelativeIRIValue");
                }

                return new RelativeIRIValue(a.P(), iriTokens);
            });
        }

    }

    // RelativeOIDValue ::=
    // "{" RelativeOIDComponentsList "}"
    protected class RelativeOIDValueParser implements RuleParser<RelativeOIDValue> {

        public RelativeOIDValue parse() throws ParserException {
            List<OIDComponentNode> rule = new ValueExtractor<List<OIDComponentNode>>(1,
                    new SequenceParser(TokenType.L_BRACE, relativeOIDComponentsListParser, TokenType.R_BRACE)).parse();

            if (rule != null) {
                return new RelativeOIDValue(getPosition(rule), rule);
            }

            return null;
        }

    }

    // RelativeOIDComponentsList ::=
    // RelativeOIDComponents
    // | RelativeOIDComponents RelativeOIDComponentsList
    protected class RelativeOIDComponentsListParser implements RuleParser<List<OIDComponentNode>> {

        public List<OIDComponentNode> parse() throws ParserException {
            return new RepetitionParser<>(relativeOIDComponentsParser).parse();
        }

    }

    // RelativeOIDComponents ::=
    // NumberForm
    // | NameAndNumberForm
    // | DefinedValue
    protected class RelativeOIDComponentsParser extends ObjectRuleParser<OIDComponentNode> {

        @SuppressWarnings("unchecked")
        public OIDComponentNode parse() throws ParserException {
            return super.parse(new ChoiceParser<>(nameAndNumberFormParser, numberFormParser, definedValueParser),
                    a -> a.n() instanceof OIDComponentNode ? (OIDComponentNode) a.n() :
                            new OIDComponentNode(a.P(), (DefinedValue) a.n()));
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
    protected class CollectionValueParser extends ObjectRuleParser<Value> {

        @SuppressWarnings("unchecked")
        public Value parse() throws ParserException {
            return super.parse(new ChoiceParser<>(
                    new SequenceParser(TokenType.L_BRACE, namedValueListParser, TokenType.R_BRACE),
                    new SequenceParser(TokenType.L_BRACE, valueListParser, TokenType.R_BRACE)), a ->
                    ((List) a.l().n1()).stream().anyMatch(e -> e instanceof NamedValue) ?
                            new CollectionValue(a.P(), (List<NamedValue>) a.l().n1()) :
                            new CollectionOfValue(a.P(), (List<Value>) a.l().n1())
            );
        }

    }

    // SequenceValue ::= "{" "}"
    protected class EmptyValueParser extends ListRuleParser<Value> {

        public Value parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.L_BRACE, TokenType.R_BRACE), a -> new EmptyValue(a.P()));
        }

    }

    // NamedValue ::= identifier Value
    protected class NamedValueParser extends ListRuleParser<NamedValue> {

        public NamedValue parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.IDENTIFIER, valueParser),
                    a -> new NamedValue(a.P(), a.s0(), a.n1()));
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
    protected class NamedValueListParser implements RuleParser<List<NamedValue>> {

        public List<NamedValue> parse() throws ParserException {
            return new CommaSeparatedRuleParser<>(namedValueParser).parse();
        }

    }

    // UnrestrictedCharacterStringType ::= CHARACTER STRING
    protected class UnrestrictedCharacterStringTypeParser extends ListRuleParser<Type> {

        public Type parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.CHARACTER_KW, TokenType.STRING_KW),
                    a -> new CharacterString(a.P()));
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
            return new ChoiceParser<>(usefulTypeParser,
                    typeFromObjectParser /* incl. ValueSetFromObjects */,
                    parameterizedTypeParser, externalTypeReferenceParser
                    /* incl. ParameterizedValueSetType */, typeReferenceParser,
                    selectionTypeParser).parse();
        }

    }

    // typereference
    protected class TypeReferenceParser implements RuleParser<TypeReference> {

        public TypeReference parse() throws ParserException {
            Token token = expect(TokenType.TYPE_REFERENCE);

            if (token != null) {
                return new TypeReference(token.getPosition(), moduleName, token.getText());
            }

            return null;
        }

    }

    // ExternalTypeReference ::=
    // modulereference
    // "."
    // typereference
    protected class ExternalTypeReferenceParser extends ListRuleParser<ExternalTypeReference> {

        public ExternalTypeReference parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.TYPE_REFERENCE, TokenType.DOT, TokenType.TYPE_REFERENCE),
                    a -> new ExternalTypeReference(a.P0(), a.s0(), a.s2()));
        }

    }

    // UsefulType ::= typereference
    protected class UsefulTypeParser extends ObjectRuleParser<UsefulType> {

        public UsefulType parse() throws ParserException {
            return super.parse(new ChoiceParser<Token>(TokenType.UTC_TIME_KW, TokenType.GENERALIZED_TIME_KW,
                    TokenType.OBJECT_DESCRIPTOR_KW), a -> {
                switch (a.$()) {
                    case UTC_TIME_KW:
                        return new UTCTime(a.P(), UTCTime.class.getSimpleName());
                    case GENERALIZED_TIME_KW:
                        return new GeneralizedTime(a.P(), GeneralizedTime.class.getSimpleName());
                    case OBJECT_DESCRIPTOR_KW:
                        return new ObjectDescriptor(a.P(), ObjectDescriptor.class.getSimpleName());
                    default:
                        return null;
                }
            });
        }

    }

    // SelectionType ::= identifier "<" Type
    protected class SelectionTypeParser extends ListRuleParser<SelectionType> {

        public SelectionType parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.IDENTIFIER, TokenType.LT, typeParser),
                    a -> new SelectionType(a.P0(), a.s0(), a.n2()));
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
    protected class TypeWithConstraintParser extends ObjectRuleParser<Type> {

        @SuppressWarnings("unchecked")
        public Type parse() throws ParserException {
            return super.parse(new ChoiceParser<>(
                    new SequenceParser(new ChoiceParser<Token>(TokenType.SET_KW, TokenType.SEQUENCE_KW),
                            sizeConstraintParser, TokenType.OF_KW, typeOrNamedTypeParser),
                    new SequenceParser(new ChoiceParser<Token>(TokenType.SET_KW,TokenType.SEQUENCE_KW),
                            constraintParser, TokenType.OF_KW, typeOrNamedTypeParser)), a -> {
                Type type;

                switch (a.l().$0()) {
                    case SET_KW:
                        type = new SetOfType(a.l().P0(), moduleName, (Type) a.l().n3());
                        break;
                    case SEQUENCE_KW:
                        type = new SequenceOfType(a.l().P0(), moduleName, (Type) a.l().n3());
                        break;
                    default:
                        throw new ParserException("Implementation error");
                }

                type.setConstraints(Arrays.asList((Constraint) a.l().n1()));

                return type;
            });
        }

    }

    // ValueAssignment ::= valuereference Type "::=" Value
    // ObjectAssignment ::= objectreference DefinedObjectClass "::=" Object
    protected class ValueAssignmentParser implements RuleParser<ValueOrObjectAssignmentNode> {

        @SuppressWarnings("unchecked")
        public ValueOrObjectAssignmentNode parse() throws ParserException {
            Set<List<Object>> rules = new AmbiguousChoiceParser<>(
                    new SequenceParser(TokenType.IDENTIFIER, typeParser, TokenType.ASSIGN, valueParser),
                    new SequenceParser(TokenType.IDENTIFIER, definedObjectClassParser, TokenType.ASSIGN, objectParser)).parse();

            Optional<List<Object>> first = rules.stream().findFirst();

            if (first.isPresent()) {
                Token token = (Token) first.get().get(0);

                ValueOrObjectAssignmentNode rule =
                        new ValueOrObjectAssignmentNode(token.getPosition(), token.getText());

                rules.stream().forEach(r -> {
                    Node node = ((Node) r.get(1));

                    if (node instanceof Type) {
                        rule.setValueAssignment(new ValueAssignmentNode(rule.getPosition(),
                                rule.getReference(), (Type) r.get(1), (Value) r.get(3)));
                    } else if (node instanceof ObjectClassReference){
                        rule.setObjectAssignment(new ObjectAssignmentNode(rule.getPosition(),
                                rule.getReference(), (ObjectClassReference) r.get(1), (ObjectNode) r.get(3)));
                    } else {
                        throw new IllegalStateException("Unexpected type: " + node.getClass().getSimpleName());
                    }
                });

                return rule;
            }

            return null;
        }

    }

    // ValueSetTypeAssignment ::= typereference Type "::=" ValueSet
    // ObjectSetAssignment ::= objectsetreference DefinedObjectClass "::=" ObjectSet
    protected class ValueSetTypeAssignmentParser implements RuleParser<ValueSetTypeOrObjectSetAssignmentNode> {

        @SuppressWarnings("unchecked")
        public ValueSetTypeOrObjectSetAssignmentNode parse() throws ParserException {
            Set<List<Object>> rules = new AmbiguousChoiceParser<>(
                    new SequenceParser(TokenType.TYPE_REFERENCE, typeParser, TokenType.ASSIGN, valueSetParser),
                    new SequenceParser(new SingleTokenParser(TokenType.OBJECT_SET_REFERENCE, Context.OBJECT_SET),
                            definedObjectClassParser, TokenType.ASSIGN, objectSetParser)).parse();

            Optional<List<Object>> first = rules.stream().findFirst();

            if (first.isPresent()) {
                Token token = (Token) first.get().get(0);
                ValueSetTypeOrObjectSetAssignmentNode rule =
                        new ValueSetTypeOrObjectSetAssignmentNode(token.getPosition(), token.getText());

                rules.stream().forEach(r -> {
                    TokenType tokenType = ((Token) r.get(0)).getType();

                    switch (tokenType) {
                        case TYPE_REFERENCE:
                            rule.setValueSetTypeAssignment(new ValueSetTypeAssignmentNode(rule.getPosition(),
                                    rule.getReference(), (Type) r.get(1), (ElementSetSpecsNode) r.get(3)));
                            break;
                        case OBJECT_SET_REFERENCE:
                            rule.setObjectSetAssignment(new ObjectSetAssignmentNode(rule.getPosition(),
                                    rule.getReference(), (ObjectClassReference) r.get(1), (ObjectSetSpecNode) r.get(3)));
                            break;
                        default:
                            throw new IllegalStateException("Unexpected token type: " + tokenType.toString());
                    }
                });

                return rule;
            }

            return null;
        }

    }

    // ValueSet ::= "{" ElementSetSpecs "}"
    protected class ValueSetParser extends ListRuleParser<ElementSetSpecsNode> {

        public ElementSetSpecsNode parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.L_BRACE, elementSetSpecsParser, TokenType.R_BRACE),
                    ListAccessor::n1);
        }

    }

    // ObjectSet ::= "{" ObjectSetSpec "}"
    protected class ObjectSetParser extends ListRuleParser<ObjectSetSpecNode> {

        public ObjectSetSpecNode parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.L_BRACE, objectSetSpecParser, TokenType.R_BRACE),
                    ListAccessor::n1);
        }

    }

    // ObjectSetSpec ::=
    // RootElementSetSpec
    // | RootElementSetSpec "," "..."
    // | "..."
    // | "..." "," AdditionalElementSetSpec
    // | RootElementSetSpec "," "..." "," AdditionalElementSetSpec
    // RootElementSetSpec ::= ElementSetSpec
    // AdditionalElementSetSpec ::= ElementSetSpec
    protected class ObjectSetSpecParser extends ObjectRuleParser<ObjectSetSpecNode> {

        @SuppressWarnings("unchecked")
        public ObjectSetSpecNode parse() throws ParserException {
              return super.parse(new ChoiceParser<>(
                    new SequenceParser(objectElementSetSpecParser, TokenType.COMMA, TokenType.ELLIPSIS, TokenType.COMMA,
                            objectElementSetSpecParser),
                    new SequenceParser(objectElementSetSpecParser, TokenType.COMMA, TokenType.ELLIPSIS),
                      objectElementSetSpecParser,
                    new SequenceParser(TokenType.ELLIPSIS, TokenType.COMMA, objectElementSetSpecParser),
                    new SingleTokenParser(TokenType.ELLIPSIS)), a -> {
                if (a.p() instanceof List) {
                    switch (a.l().size()) {
                        case 3:
                            if (a.l().n2() instanceof Token) {
                                return new ObjectSetSpecNode((ElementSet) a.l().n0(), true);
                            } else {
                                return new ObjectSetSpecNode(a.l().P2(), null, true, (ElementSet) a.l().n2());
                            }

                        case 5:
                            return new ObjectSetSpecNode(a.l().P0(), (ElementSet) a.l().n0(), true,
                                    (ElementSet) a.l().n4());

                        default:
                            throw new ParserException("Invalid rule");
                    }
                } else {
                    if (a.p() instanceof Token) {
                        return new ObjectSetSpecNode(a.t().getPosition(), true);
                    } else {
                        return new ObjectSetSpecNode((ElementSet) a.n());
                    }
                }
            });
        }

    }

    // ElementSetSpecs ::=
    // RootElementSetSpec
    // | RootElementSetSpec "," "..."
    // | RootElementSetSpec "," "..." "," AdditionalElementSetSpec
    // RootElementSetSpec ::= ElementSetSpec
    // AdditionalElementSetSpec ::= ElementSetSpec
    protected class ElementSetSpecsParser extends ObjectRuleParser<ElementSetSpecsNode> {

        @SuppressWarnings("unchecked")
        public ElementSetSpecsNode parse() throws ParserException {
             return super.parse(new ChoiceParser<>(
                    new SequenceParser(elementSetSpecParser, TokenType.COMMA, TokenType.ELLIPSIS, TokenType.COMMA,
                            elementSetSpecParser),
                    new SequenceParser(elementSetSpecParser, TokenType.COMMA, TokenType.ELLIPSIS),
                    elementSetSpecParser), a -> {
                if (a.p() instanceof List) {
                    switch (a.l().size()) {
                        case 3:
                            return new ElementSetSpecsNode((ElementSet) a.l().n0(), true);

                        case 5:
                            return new ElementSetSpecsNode(a.l().P0(), (ElementSet) a.l().n0(), true,
                                    (ElementSet) a.l().n4());

                        default:
                            throw new ParserException("Invalid rule");
                    }
                } else if (a.n() instanceof ElementSet) {
                    return new ElementSetSpecsNode((ElementSet) a.n());
                }

                return null;
            });
        }

    }

    // ElementSetSpec ::= Unions
    // | ALL Exclusions
    protected class ElementSetSpecParser extends ObjectRuleParser<ElementSet> {

        @SuppressWarnings("unchecked")
        public ElementSet parse() throws ParserException {
            return super.parse(new ChoiceParser<>(new SequenceParser(TokenType.ALL_KW, exclusionsParser),
                    unionsParser), a -> a.p() instanceof List ?
                         new ElementSet(a.l().P1(), OpType.ALL, (ElementSet) a.l().n1()) : (ElementSet) a.p());
        }

    }

    // ElementSetSpec ::= Unions
    // | ALL Exclusions
    protected class ObjectElementSetSpecParser extends ObjectRuleParser<ElementSet> {

        @SuppressWarnings("unchecked")
        public ElementSet parse() throws ParserException {
            return super.parse(new ChoiceParser<>(new SequenceParser(TokenType.ALL_KW, objectExclusionsParser),
                    objectUnionsParser), a -> a.p() instanceof List ?
                    new ElementSet(a.l().P1(), OpType.ALL, (ElementSet) a.l().n1()) : (ElementSet) a.p());
        }

    }

    // Unions ::= Intersections
    // | UElems UnionMark Intersections
    // UElems ::= Unions
    protected class UnionsParser implements RuleParser<ElementSet> {

        public ElementSet parse() throws ParserException {
            List<Elements> elements = new TokenSeparatedRuleParser<Elements>(
                    intersectionsParser, TokenType.PIPE, TokenType.UNION_KW).parse();

            if (elements != null) {
                return new ElementSet(getPosition(elements), OpType.UNION, elements.toArray(new Elements[] {}));
            }

            return null;
        }

    }

    // Unions ::= Intersections
    // | UElems UnionMark Intersections
    // UElems ::= Unions
    protected class ObjectUnionsParser implements RuleParser<ElementSet> {

        public ElementSet parse() throws ParserException {
            List<Elements> elements = new TokenSeparatedRuleParser<Elements>(
                    objectIntersectionsParser, TokenType.PIPE, TokenType.UNION_KW).parse();

            if (elements != null) {
                return new ElementSet(getPosition(elements), OpType.UNION, elements.toArray(new Elements[] {}));
            }

            return null;
        }

    }

    // Intersections ::= IntersectionElements
    // | IElems IntersectionMark IntersectionElements
    // IElems ::= Intersections
    protected class IntersectionsParser extends ListRuleParser<ElementSet> {

        public ElementSet parse() throws ParserException {
            List<Elements> elements = new TokenSeparatedRuleParser<>(intersectionElementsParser, TokenType.CIRCUMFLEX,
                    TokenType.INTERSECTION_KW).parse();

            if (elements != null) {
                return new ElementSet(getPosition(elements), OpType.INTERSECTION, elements.toArray(new Elements[] {}));
            }

            return null;
        }

    }

    // Intersections ::= IntersectionElements
    // | IElems IntersectionMark IntersectionElements
    // IElems ::= Intersections
    protected class ObjectIntersectionsParser extends ListRuleParser<ElementSet> {

        public ElementSet parse() throws ParserException {
            List<Elements> elements = new TokenSeparatedRuleParser<>(objectIntersectionElementsParser,
                    TokenType.CIRCUMFLEX, TokenType.INTERSECTION_KW).parse();

            if (elements != null) {
                return new ElementSet(getPosition(elements), OpType.INTERSECTION, elements.toArray(new Elements[] {}));
            }

            return null;
        }

    }

    // IntersectionElements ::= Elements | Elems Exclusions
    // Elems ::= Elements
    protected class IntersectionElementsParser extends ListRuleParser<Elements> {

        public Elements parse() throws ParserException {
            return super.parse(new SequenceParser(new boolean[] { true, false }, elementsParser,
                    exclusionsParser), a -> a.n1() == null ? a.n0() :
                    new ElementSet(a.P(), OpType.EXCLUDE, a.n0(), ((ElementSet) a.n1()).getOperands().get(0)));
        }

    }

    // IntersectionElements ::= Elements | Elems Exclusions
    // Elems ::= Elements
    protected class ObjectIntersectionElementsParser extends ListRuleParser<Elements> {

        public Elements parse() throws ParserException {
            return super.parse(new SequenceParser(new boolean[] { true, false }, objectElementsParser,
                    objectExclusionsParser), a -> a.n1() == null ? a.n0() :
                    new ElementSet(a.P(), OpType.EXCLUDE, a.n0(), ((ElementSet) a.n1()).getOperands().get(0)));
        }

    }

    // Exclusions ::= EXCEPT Elements
    protected class ExclusionsParser extends ListRuleParser<ElementSet> {

        public ElementSet parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.EXCEPT_KW, elementsParser),
                    a -> new ElementSet(a.P(), OpType.EXCLUDE, (Elements) a.n1()));
        }

    }

    // Exclusions ::= EXCEPT Elements
    protected class ObjectExclusionsParser extends ListRuleParser<ElementSet> {

        public ElementSet parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.EXCEPT_KW, objectElementsParser),
                    a -> new ElementSet(a.P(), OpType.EXCLUDE, (Elements) a.n1()));
        }

    }

    // Elements ::=
    // SubtypeElements
    // | "(" ElementSetSpec ")"
    protected class ElementsParser extends ObjectRuleParser<Elements> {

        @SuppressWarnings("unchecked")
        public Elements parse() throws ParserException {
            return super.parse(new ChoiceParser<>(subtypeElementsParser,
                            new SequenceParser(TokenType.L_PAREN, elementSetSpecParser, TokenType.R_PAREN)),
                    a -> a.p() instanceof List ? (ElementSet) a.l().n1() : (Elements) a.p());
        }

    }

    // Elements ::=
    // | ObjectSetElements
    // | "(" ElementSetSpec ")"
    protected class ObjectElementsParser extends ObjectRuleParser<Elements> {

        @SuppressWarnings("unchecked")
        public Elements parse() throws ParserException {
            return super.parse(new ChoiceParser<>(objectSetElementsParser,
                            new SequenceParser(TokenType.L_PAREN, objectElementSetSpecParser, TokenType.R_PAREN)),
                    a -> a.p() instanceof List ? (ElementSet) a.l().n1() : (Elements) a.p());
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
            return new ChoiceParser<>(containedSubtypeParser, valueRangeParser, permittedAlphabetParser,
                    sizeConstraintParser, innerTypeConstraintsParser, patternConstraintParser,
                    propertySettingsParser, durationRangeParser, timePointRangeParser, recurrenceRangeParser,
                    singleValueParser, typeConstraintParser).parse();
        }

    }

    // SingleValue ::= Value
    protected class SingleValueParser extends ObjectRuleParser<SingleValueConstraint> {

        public SingleValueConstraint parse() throws ParserException {
            return super.parse(valueParser, a -> new SingleValueConstraint(a.P(), (Value) a.n()));
        }

    }

    // ContainedSubtype ::= Includes Type
    // Includes ::= INCLUDES | empty
    protected class ContainedSubtypeParser extends ListRuleParser<ContainedSubtype> {

        public ContainedSubtype parse() throws ParserException {
            return super.parse(new SequenceParser(new boolean[] { false, true }, TokenType.INCLUDES_KW, typeParser),
                    a -> {
                if (a.n0() == null) {
                    if (a.n1() instanceof Null) {
                        throw new ParserException("NULL not allowed");
                    }

                    return new ContainedSubtype(a.P1(), a.n1(), false);
                } else {
                    return new ContainedSubtype(a.P0(), a.n1(), true);
                }
            });
        }

    }

    // ValueRange ::= LowerEndpoint ".." UpperEndpoint
    protected class ValueRangeParser extends ListRuleParser<RangeNode> {

        public RangeNode parse() throws ParserException {
            return super.parse(new SequenceParser(lowerEndpointParser, TokenType.RANGE, upperEndpointParser),
                    a -> new RangeNode(a.P(), a.n0(), a.n2()));
        }

    }

    // LowerEndpoint ::= LowerEndValue | LowerEndValue "<"
    protected class LowerEndpointParser extends ListRuleParser<LowerEndpointNode> {

        public LowerEndpointNode parse() throws ParserException {
            return super.parse(new SequenceParser(new boolean[] { true, false }, lowerEndValueParser,
                    TokenType.LT), a -> new LowerEndpointNode(a.P(), a.n0(), a.n1() == null));
        }

    }

    // UpperEndpoint ::= UpperEndValue | "<" UpperEndValue
    protected class UpperEndpointParser extends ListRuleParser<UpperEndpointNode> {

        public UpperEndpointNode parse() throws ParserException {
            return super.parse(new SequenceParser(new boolean[] { false, true }, TokenType.LT, upperEndValueParser),
                    a -> new UpperEndpointNode(a.P(), a.n1(), a.n0() == null));
        }

    }

    // LowerEndValue ::= Value | MIN
    protected class LowerEndValueParser extends ObjectRuleParser<Value> {

        @SuppressWarnings("unchecked")
        public Value parse() throws ParserException {
            return super.parse(new ChoiceParser<>(valueParser, new SingleTokenParser(TokenType.MIN_KW)),
                    a -> a.p() instanceof Token ? Value.MIN : (Value) a.n());
        }

    }

    // UpperEndValue ::= Value | MAX
    protected class UpperEndValueParser extends ObjectRuleParser<Value> {

        @SuppressWarnings("unchecked")
        public Value parse() throws ParserException {
            return super.parse(new ChoiceParser<>(valueParser, new SingleTokenParser(TokenType.MAX_KW)),
                    a -> a.p() instanceof Token ? Value.MAX : (Value) a.n());
        }

    }

    // SizeConstraint ::= SIZE Constraint
    protected class SizeConstraintParser extends ListRuleParser<SizeConstraint> {

        public SizeConstraint parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.SIZE_KW, constraintParser),
                    a -> new SizeConstraint(a.P0(), a.n1()));
        }

    }

    // TypeConstraint ::= Type
    protected class TypeConstraintParser extends ObjectRuleParser<TypeConstraint> {

        public TypeConstraint parse() throws ParserException {
            return super.parse(typeParser,
                    // TODO: only applicable to ObjectClassFieldType (X.680 3.8.57 NOTE 3)
                    a -> new TypeConstraint(((Type) a.n()).getPosition(), (Type) a.n()));
        }

    }

    // PermittedAlphabet ::= FROM Constraint
    protected class PermittedAlphabetParser extends ListRuleParser<PermittedAlphabetConstraint> {

        public PermittedAlphabetConstraint parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.FROM_KW, constraintParser),
                    a -> new PermittedAlphabetConstraint(a.P(), a.n1()) );
        }

    }

    // InnerTypeConstraints ::=
    // WITH COMPONENT SingleTypeConstraint
    // | WITH COMPONENTS MultipleTypeConstraints
    // SingleTypeConstraint ::= Constraint
    protected class InnerTypeConstraintsParser extends ListRuleParser<Constraint> {

        @SuppressWarnings("unchecked")
        public Constraint parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.WITH_KW,
                            new ChoiceParser<Object>(new SequenceParser(TokenType.COMPONENT_KW, constraintParser),
                                    new SequenceParser(TokenType.COMPONENTS_KW, multipleTypeConstraintsParser))),
                    a -> {
                        Constraint constraint = (Constraint) ((List<Object>) a.n1()).get(1);

                        if (constraint instanceof MultipleTypeConstraints) {
                            return constraint;
                        }

                        return new SingleTypeConstraint(a.P(), constraint);
                    });
        }

    }

    // MultipleTypeConstraints ::=
    // FullSpecification
    // | PartialSpecification
    protected class MultipleTypeConstraintsParser implements RuleParser<MultipleTypeConstraints> {

        @SuppressWarnings("unchecked")
        public MultipleTypeConstraints parse() throws ParserException {
            return new ChoiceParser<>(fullSpecificationParser, partialSpecificationParser).parse();
        }

    }

    // FullSpecification ::= "{" TypeConstraints "}"
    protected class FullSpecificationParser extends ListRuleParser<MultipleTypeConstraints> {

        @SuppressWarnings("unchecked")
        public MultipleTypeConstraints parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.L_BRACE, typeConstraintsParser, TokenType.R_BRACE),
                    a -> new MultipleTypeConstraints(a.P(), a.n1(), false));
        }

    }

    // PartialSpecification ::= "{" "..." "," TypeConstraints "}"
    protected class PartialSpecificationParser extends ListRuleParser<MultipleTypeConstraints> {

        @SuppressWarnings("unchecked")
        public MultipleTypeConstraints parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.L_BRACE, TokenType.ELLIPSIS, TokenType.COMMA,
                    typeConstraintsParser, TokenType.R_BRACE), a -> new MultipleTypeConstraints(a.P(), a.n3(), true));
        }

    }

    // TypeConstraints ::=
    // NamedConstraint
    // | NamedConstraint "," TypeConstraints
    protected class TypeConstraintsParser implements RuleParser<List<NamedConstraint>> {

        public List<NamedConstraint> parse() throws ParserException {
            return new CommaSeparatedRuleParser<>(namedConstraintParser).parse();
        }

    }

    // NamedConstraint ::=
    // identifier ComponentConstraint
    protected class NamedConstraintParser extends ListRuleParser<NamedConstraint> {

        public NamedConstraint parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.IDENTIFIER, componentConstraintParser),
                    a -> new NamedConstraint(a.P(), a.s0(), a.n1()));
        }

    }

    // ComponentConstraint ::= ValueConstraint PresenceConstraint
    protected class ComponentConstraintParser extends ListRuleParser<ComponentConstraint> {

        public ComponentConstraint parse() throws ParserException {
            return super.parse(new SequenceParser(new boolean[] { false, false }, valueConstraintParser,
                            presenceConstraintParser),
                    a -> new ComponentConstraint(Position.of(a.n0(), a.n1()), a.n0(), a.n1()));
        }

    }

    // ValueConstraint ::= Constraint | empty
    protected class ValueConstraintParser extends ObjectRuleParser<Constraint> {

        public Constraint parse() throws ParserException {
            return super.parse(constraintParser, a -> new ValueConstraint(a.P(), (Constraint) a.n()));
        }

    }

    // PresenceConstraint ::= PRESENT | ABSENT | OPTIONAL | empty
    protected class PresenceConstraintParser extends ObjectRuleParser<PresenceConstraint> {

        public PresenceConstraint parse() throws ParserException {
            return super.parse(new ChoiceParser<Token>(TokenType.PRESENT_KW, TokenType.ABSENT_KW, TokenType.OPTIONAL_KW),
                    a -> {
                        switch (a.$()) {
                            case PRESENT_KW:
                                return new PresenceConstraint(a.P(), PresenceConstraint.PresenceType.PRESENT);
                            case ABSENT_KW:
                                return new PresenceConstraint(a.P(), PresenceConstraint.PresenceType.ABSENT);
                            case OPTIONAL_KW:
                                return new PresenceConstraint(a.P(), PresenceConstraint.PresenceType.OPTIONAL);
                        }

                        return null;
                    });
        }

    }

    // PatternConstraint ::= PATTERN Value
    protected class PatternConstraintParser extends ListRuleParser<PatternConstraint> {

        public PatternConstraint parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.PATTERN_KW, valueParser), a -> {
                Value value = a.n1();

                if (value instanceof AmbiguousValue && ((AmbiguousValue) value).getValue(DefinedValue.class) != null) {
                    value = ((AmbiguousValue) value).getValue(DefinedValue.class);
                }

                return new PatternConstraint(a.P(), value);
            });
        }

    }

    // PropertySettings ::= SETTINGS simplestring
    // simplestring ::= "\"" PropertySettingsList "\""
    // PropertySettingsList ::=
    // PropertyAndSettingPair
    // | PropertySettingsList PropertyAndSettingPair
    protected class PropertySettingsParser extends ListRuleParser<PropertySettingsConstraint> {

        @SuppressWarnings("unchecked")
        public PropertySettingsConstraint parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.SETTINGS_KW,
                            new SingleTokenParser(TokenType.QUOTATION, Context.PROPERTY_SETTINGS),
                            new RepetitionParser<>(propertyAndSettingPairParser),
                            new SingleTokenParser(TokenType.QUOTATION, Context.PROPERTY_SETTINGS)),
                    a -> new PropertySettingsConstraint(a.P(), a.n2()));
        }

    }

    // PropertyAndSettingPair ::= PropertyName "=" SettingName
    // PropertyName ::= psname
    // SettingName ::= psname
    protected class PropertyAndSettingPairParser extends ListRuleParser<PropertyAndSettingNode> {

        @SuppressWarnings("serial")
        private final Map<String, Set<String>> properties = unmodifiableMap(Maps.<String, Set<String>>builder()
                .put("Basic", new HashSet<>(asList("Date", "Time", "Date-Time", "Interval", "Rec-Interval")))
                .put("Date", new HashSet<>(asList("C", "Y", "YM", "YMD", "YD", "YW", "YWD")))
                .put("Year", new HashSet<>(asList("Basic", "Proleptic", "Negative")))
                .put("Time", new HashSet<>(asList("H", "HM", "HMS")))
                .put("Local-or-UTC", new HashSet<>(asList("L", "Z", "LD")))
                .put("Interval-type", new HashSet<>(asList("SE", "D", "SD", "DE")))
                .put("SE-point", new HashSet<>(asList("Date", "Time", "Date-Time")))
                .put("Recurrence", new HashSet<>(asList("Unlimited")))
                .put("Midnight", new HashSet<>(asList("Start", "End")))
                .build());

        public PropertyAndSettingNode parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.TYPE_REFERENCE, TokenType.EQUALS, TokenType.TYPE_REFERENCE),
                    a -> {
                Token propertyToken = a.t0();
                Token settingToken = a.t2();
                String property = a.s0();
                String setting = a.s2();

                Set<String> settings = properties.get(property);

                if (settings == null) {
                    setException(String.format("Invalid property '%s'", property), propertyToken);
                    return null;
                }

                if (!settings.contains(setting)
                        && (!"Year".equals(property) || !Pattern.matches("L[567][0-9]*", setting))
                        && (!"Time".equals(property) || !Pattern.matches("HM?S?F[1-9][0-9]*", setting))
                        && (!"Recurrence".equals(property) || !Pattern.matches("R[1-9][0-9]*", setting))) {
                    setException(String.format("Invalid setting '%s' for property '%s'", setting, property),
                            settingToken);
                    return null;
                }

                return new PropertyAndSettingNode(a.P0(), property, setting);
            });
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
    protected class ConstraintParser extends ListRuleParser<Constraint> {

        public Constraint parse() throws ParserException {
            return super.parse(new SequenceParser(new boolean[] { true, true, false, true }, TokenType.L_PAREN,
                    constraintSpecParser, exceptionSpecParser, TokenType.R_PAREN), a -> {
                AbstractConstraint constraint = a.n1();

                if (a.n2() != null) {
                    constraint.setExceptionSpec(a.n2());
                }

                return constraint;
            });
        }

    }

    // ConstraintSpec ::= SubtypeConstraint
    // | GeneralConstraint
    protected class ConstraintSpecParser implements RuleParser<Constraint> {

        @SuppressWarnings("unchecked")
        public Constraint parse() throws ParserException {
            return new ChoiceParser<>(subtypeConstraintParser, generalConstraintParser).parse();
        }

    }

    // SubtypeConstraint ::= ElementSetSpecs
    protected class SubtypeConstraintParser extends ObjectRuleParser<SubtypeConstraint> {

        public SubtypeConstraint parse() throws ParserException {
            ElementSetSpecsNode rule = elementSetSpecsParser.parse();

            if (rule != null) {
                return new SubtypeConstraint(rule.getPosition(), rule);
            }

            return null;
        }

    }

    // DefinedObjectClass ::=
    // ExternalObjectClassReference | objectclassreference | UsefulObjectClassReference
    protected class DefinedObjectClassParser extends ObjectRuleParser<ObjectClassReference> {

        @SuppressWarnings("unchecked")
        public ObjectClassReference parse() throws ParserException {
            return super.parse(new ChoiceParser<>(externalObjectClassReferenceParser,
                    new SingleTokenParser(TokenType.OBJECT_CLASS_REFERENCE, Context.OBJECT_CLASS),
                    usefulObjectClassReferenceParser),
                    a -> a.n() instanceof  Token ? new ObjectClassReference(a.P(), a.s())
                            : (ObjectClassReference) a.n());
        }

    }

    // ExternalObjectClassReference ::= modulereference "." objectclassreference
    protected class ExternalObjectClassReferenceParser extends ListRuleParser<ExternalObjectClassReference> {

        public ExternalObjectClassReference parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.TYPE_REFERENCE, TokenType.DOT,
                    new SingleTokenParser(TokenType.OBJECT_CLASS_REFERENCE, Context.OBJECT_CLASS)),
                    a -> new ExternalObjectClassReference(a.P0(), a.s0(),a.s2()));
        }

    }

    // UsefulObjectClassReference ::=
    // TYPE-IDENTIFIER
    // | ABSTRACT-SYNTAX
    protected class UsefulObjectClassReferenceParser extends ObjectRuleParser<ObjectClassReference> {

        public ObjectClassReference parse() throws ParserException {
            return super.parse(new ChoiceParser<Token>(TokenType.TYPE_IDENTIFIER_KW, TokenType.ABSTRACT_SYNTAX_KW),
                    a -> {
                        if (a.$() == TokenType.TYPE_IDENTIFIER_KW) {
                            return new TypeIdentifierObjectClassReferenceNode(a.P());
                        } else {
                            return new AbstractSyntaxObjectClassReferenceNode(a.P());
                        }
                    });
        }

    }

    // ObjectClass ::= DefinedObjectClass | ObjectClassDefn |
    // ParameterizedObjectClass
    public class ObjectClassParser implements RuleParser<ObjectClassNode> {

        @SuppressWarnings("unchecked")
        public ObjectClassNode parse() throws ParserException {
            return new ChoiceParser<>(parameterizedObjectClassParser, definedObjectClassParser,
                    objectClassDefnParser).parse();
        }

    }

    // ObjectClassDefn ::= CLASS "{" FieldSpec "," + "}" WithSyntaxSpec?
    protected class ObjectClassDefnParser extends ListRuleParser<ObjectClassDefn> {

        @SuppressWarnings("unchecked")
        public ObjectClassDefn parse() throws ParserException {
            return super.parse(new SequenceParser(new boolean[] { true, true, true, true, false },
                    TokenType.CLASS_KW, TokenType.L_BRACE, new CommaSeparatedRuleParser<>(fieldSpecParser),
                    TokenType.R_BRACE, withSyntaxSpecParser),
                    a -> new ObjectClassDefn(a.P(), a.n2(), a.n4()));
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
    protected class FieldSpecParser implements RuleParser<AbstractFieldSpecNode> {

        @SuppressWarnings("unchecked")
        public AbstractFieldSpecNode parse() throws ParserException {
            return new ChoiceParser<>(fixedTypeValueFieldSpecParser,
                    variableTypeValueFieldSpecParser,
                    fixedTypeValueSetOrObjectSetFieldSpecParser,
                    variableTypeValueSetFieldSpecParser,
                    typeFieldSpecParser).parse();
        }

    }

    // PrimitiveFieldName ::=
    // typefieldreference
    // | valuefieldreference
    // | valuesetfieldreference
    // | objectfieldreference
    // | objectsetfieldreference
    protected class PrimitiveFieldNameParser implements RuleParser<PrimitiveFieldNameNode> {

        @SuppressWarnings("unchecked")
        public PrimitiveFieldNameNode parse() throws ParserException {
            Token rule = new ChoiceParser<>(new SingleTokenParser(TokenType.TYPE_FIELD_REFERENCE, Context.TYPE_FIELD),
                    new SingleTokenParser(TokenType.VALUE_FIELD_REFERENCE, Context.VALUE_FIELD)).parse();

            if (rule != null) {
                String reference = rule.getText().substring(1);

                switch (rule.getType()) {
                    case TYPE_FIELD_REFERENCE:
                        return new PrimitiveFieldNameNode(rule.getPosition(), reference, TokenType.TYPE_FIELD_REFERENCE);
                    case VALUE_FIELD_REFERENCE:
                        return new PrimitiveFieldNameNode(rule.getPosition(), reference, TokenType.VALUE_FIELD_REFERENCE);
                    default:
                        throw new ParserException("Unexpected type: " + rule.getType());
                }
            }

            return null;
        }

    }

    // PrimitiveFieldName ::=
    // typefieldreference
    // | valuesetfieldreference
    // | objectsetfieldreference
    protected class TypePrimitiveFieldNameParser implements RuleParser<PrimitiveFieldNameNode> {

        @SuppressWarnings("unchecked")
        public PrimitiveFieldNameNode parse() throws ParserException {
            Token rule = new SingleTokenParser(TokenType.TYPE_FIELD_REFERENCE, Context.TYPE_FIELD).parse();

            if (rule != null) {
                String reference = rule.getText().substring(1);

                return new PrimitiveFieldNameNode(rule.getPosition(), reference, TokenType.TYPE_FIELD_REFERENCE);
            }

            return null;
        }

    }

    // PrimitiveFieldName ::=
    // valuefieldreference
    // | objectfieldreference
    protected class ValuePrimitiveFieldNameParser implements RuleParser<PrimitiveFieldNameNode> {

        @SuppressWarnings("unchecked")
        public PrimitiveFieldNameNode parse() throws ParserException {
            Token rule = new SingleTokenParser(TokenType.VALUE_FIELD_REFERENCE, Context.VALUE_FIELD).parse();

            if (rule != null) {
                String reference = rule.getText().substring(1);

                return new PrimitiveFieldNameNode(rule.getPosition(), reference, TokenType.VALUE_FIELD_REFERENCE);
            }

            return null;
        }

    }

    // FieldName ::= PrimitiveFieldName "." +
    protected class FieldNameParser implements RuleParser<FieldNameNode> {

        public FieldNameNode parse() throws ParserException {
            List<PrimitiveFieldNameNode> rule = new TokenSeparatedRuleParser<>(primitiveFieldNameParser,
                    TokenType.DOT).parse();

            if (rule != null) {
                return new FieldNameNode(getPosition(rule), rule);
            }

            return null;
        }

    }

    // TypeFieldSpec ::= typefieldreference TypeOptionalitySpec?
    protected class TypeFieldSpecParser extends ListRuleParser<TypeFieldSpecNode> {

        public TypeFieldSpecNode parse() throws ParserException {
            return super.parse(new SequenceParser(new boolean[] { true, true, false },
                    TokenType.AMPERSAND, TokenType.TYPE_REFERENCE, typeOptionalitySpecParser),
                    a -> new TypeFieldSpecNode(a.P0(), a.s1(),a.n2()));
        }

    }

    // TypeOptionalitySpec ::= OPTIONAL | DEFAULT Type
    protected class TypeOptionalitySpecParser implements RuleParser<OptionalitySpecNode> {

        @SuppressWarnings("unchecked")
        public OptionalitySpecNode parse() throws ParserException {
            Object rule = new ChoiceParser<>(new SingleTokenParser(TokenType.OPTIONAL_KW),
                    new SequenceParser(TokenType.DEFAULT_KW, typeParser)).parse();

            if (rule != null) {
                if (rule instanceof List) {
                    List<?> list = (List<?>) rule;

                    return new DefaultTypeSpecNode(((Token) list.get(0)).getPosition(), (Type) list.get(1));
                } else {
                    return new OptionalSpecNode(((Token) rule).getPosition());
                }
            }

            return null;
        }

    }

    // FixedTypeValueFieldSpec ::= valuefieldreference Type UNIQUE ?
    // ValueOptionalitySpec ?
    // ObjectFieldSpec ::= objectfieldreference DefinedObjectClass
    // ObjectOptionalitySpec?
    protected class FixedTypeValueOrObjectFieldSpecParser implements RuleParser<FixedTypeValueOrObjectFieldSpecNode> {

        @SuppressWarnings("unchecked")
        public FixedTypeValueOrObjectFieldSpecNode parse() throws ParserException {
            Set<List<Object>> rules = new AmbiguousChoiceParser<>(
                    new SequenceParser(new boolean[] { true, true, true, false, false },
                            TokenType.AMPERSAND,
                            TokenType.IDENTIFIER,
                            typeParser,
                            TokenType.UNIQUE_KW,
                            valueOptionalitySpecParser),
                    new SequenceParser(new boolean[] { true, true, true, false },
                            TokenType.AMPERSAND,
                            TokenType.IDENTIFIER,
                            definedObjectClassParser,
                            objectOptionalitySpecParser)).parse();

            Optional<List<Object>> maybeFirst = rules.stream().findFirst();

            if (maybeFirst.isPresent()) {
                List<Object> first = maybeFirst.get();
                Token token = (Token) first.get(1);

                FixedTypeValueOrObjectFieldSpecNode rule =
                        new FixedTypeValueOrObjectFieldSpecNode(token.getPosition(), token.getText(),
                                (OptionalitySpecNode) first.get(first.size() - 1));

                rules.stream().forEach(r -> {
                    Node node = ((Node) r.get(2));
                    OptionalitySpecNode optionalitySpec = (OptionalitySpecNode) r.get(r.size() - 1);
                    boolean isNotDefault = optionalitySpec == null || optionalitySpec instanceof OptionalSpecNode;

                    if (node instanceof Type && (isNotDefault || optionalitySpec instanceof DefaultValueSpecNode)) {
                        rule.setFixedTypeValueFieldSpec(new FixedTypeValueFieldSpecNode(rule.getPosition(),
                                rule.getReference(), (Type) r.get(2), r.get(3) != null, optionalitySpec));
                    } else if (node instanceof ObjectClassReference
                            && (isNotDefault || optionalitySpec instanceof DefaultObjectSpecNode)) {
                        rule.setObjectFieldSpec(new ObjectFieldSpecNode(rule.getPosition(), rule.getReference(),
                                (ObjectClassReference) r.get(2), optionalitySpec));
                    }
                });

                return rule;
            }

            return null;
        }

    }

    // ValueOptionalitySpec ::= OPTIONAL | DEFAULT Value
    protected class ValueOptionalitySpecParser implements RuleParser<OptionalitySpecNode> {

        @SuppressWarnings("unchecked")
        public OptionalitySpecNode parse() throws ParserException {
            Object rule = new ChoiceParser<>(new SingleTokenParser(TokenType.OPTIONAL_KW),
                    new SequenceParser(TokenType.DEFAULT_KW, valueParser)).parse();

            if (rule != null) {
                if (rule instanceof List) {
                    List<Object> list = (List<Object>) rule;

                    return new DefaultValueSpecNode(getPosition(list), (Value) list.get(1));
                } else {
                    return new OptionalSpecNode(((Token)rule).getPosition());
                }
            }

            return null;
        }

    }

    // ObjectOptionalitySpec ::= OPTIONAL | DEFAULT Object
    protected class ObjectOptionalitySpecParser implements RuleParser<OptionalitySpecNode> {

        @SuppressWarnings("unchecked")
        public OptionalitySpecNode parse() throws ParserException {
            Object rule = new ChoiceParser<>(new SingleTokenParser(TokenType.OPTIONAL_KW),
                    new SequenceParser(TokenType.DEFAULT_KW, objectParser)).parse();

            if (rule != null) {
                if (rule instanceof List) {
                    List<Object> list = (List<Object>) rule;

                    return new DefaultObjectSpecNode(getPosition(list), (ObjectNode) list.get(1));
                } else {
                    return new OptionalSpecNode(((Token)rule).getPosition());
                }
            }

            return null;
        }

    }

    // VariableTypeValueFieldSpec ::= valuefieldreference FieldName
    // ValueOptionalitySpec ?
    protected class VariableTypeValueFieldSpecParser extends ListRuleParser<VariableTypeValueFieldSpecNode> {

        public VariableTypeValueFieldSpecNode parse() throws ParserException {
            return super.parse(new SequenceParser(new boolean[] { true, true, true, false },
                    TokenType.AMPERSAND, TokenType.IDENTIFIER, fieldNameParser, valueOptionalitySpecParser),
                    a -> new VariableTypeValueFieldSpecNode(a.P(), a.s1(),a.n2(), a.n3()));
        }

    }

    // FixedTypeValueSetFieldSpec ::= valuesetfieldreference Type
    // ValueSetOptionalitySpec ?
    // ObjectSetFieldSpec ::= objectsetfieldreference DefinedObjectClass
    // ObjectSetOptionalitySpec ?
    protected class FixedTypeValueSetOrObjectSetFieldSpecParser
            implements RuleParser<FixedTypeValueSetOrObjectSetFieldSpecNode> {

        @SuppressWarnings("unchecked")
        public FixedTypeValueSetOrObjectSetFieldSpecNode parse() throws ParserException {
            Set<List<Object>> rules = new AmbiguousChoiceParser<>(
                    new SequenceParser(new boolean[] { true, true, true, false },
                            TokenType.AMPERSAND,
                            TokenType.TYPE_REFERENCE,
                            typeParser,
                            valueSetOptionalitySpecParser),
                    new SequenceParser(new boolean[] { true, true, true, false },
                            TokenType.AMPERSAND,
                            TokenType.TYPE_REFERENCE,
                            definedObjectClassParser,
                            objectSetOptionalitySpecParser)).parse();

            Optional<List<Object>> maybeFirst = rules.stream().findFirst();

            if (maybeFirst.isPresent()) {
                List<Object> first = maybeFirst.get();
                Token token = (Token) first.get(1);

                FixedTypeValueSetOrObjectSetFieldSpecNode rule =
                        new FixedTypeValueSetOrObjectSetFieldSpecNode(token.getPosition(), token.getText(),
                                (OptionalitySpecNode) first.get(first.size() - 1));

                rules.stream().forEach(r -> {
                    Node node = ((Node) r.get(2));
                    OptionalitySpecNode optionalitySpec = (OptionalitySpecNode) r.get(3);
                    boolean isNotDefault = optionalitySpec == null || optionalitySpec instanceof OptionalSpecNode;

                    if (node instanceof Type && (isNotDefault || optionalitySpec instanceof DefaultValueSetSpecNode)) {
                        rule.setFixedTypeValueSetFieldSpec(new FixedTypeValueSetFieldSpecNode(rule.getPosition(),
                                rule.getReference(), (Type) r.get(2), optionalitySpec));
                    } else if (node instanceof ObjectClassReference
                            && (isNotDefault || optionalitySpec instanceof DefaultObjectSetSpecNode)) {
                        rule.setObjectSetFieldSpec(new ObjectSetFieldSpecNode(rule.getPosition(), rule.getReference(),
                                (ObjectClassReference) r.get(2), optionalitySpec));
                    }
                });

                return rule;
            }

            return null;
        }

    }

    // ValueSetOptionalitySpec ::= OPTIONAL | DEFAULT ValueSet
    protected class ValueSetOptionalitySpecParser implements RuleParser<OptionalitySpecNode> {

        public OptionalitySpecNode parse() throws ParserException {
            var rule = new ChoiceParser<>(new SingleTokenParser(TokenType.OPTIONAL_KW),
                    new SequenceParser(TokenType.DEFAULT_KW, valueSetParser)).parse();

            if (rule != null) {
                if (rule instanceof List) {
                    var list = (List<Object>) rule;

                    return new DefaultValueSetSpecNode(getPosition(list), (ElementSetSpecsNode) list.get(1));
                } else {
                    return new OptionalSpecNode(((Token) rule).getPosition());
                }
            }

            return null;
        }

    }

    // ObjectSetOptionalitySpec ::= OPTIONAL | DEFAULT ObjectSet
    protected class ObjectSetOptionalitySpecParser implements RuleParser<OptionalitySpecNode> {

        public OptionalitySpecNode parse() throws ParserException {
            var rule = new ChoiceParser<>(new SingleTokenParser(TokenType.OPTIONAL_KW),
                    new SequenceParser(TokenType.DEFAULT_KW, objectSetParser)).parse();

            if (rule != null) {
                if (rule instanceof List) {
                    var list = (List<Object>) rule;

                    return new DefaultObjectSetSpecNode(getPosition(list), (ObjectSetSpecNode) list.get(1));
                } else {
                    return new OptionalSpecNode(((Token) rule).getPosition());
                }
            }

            return null;
        }

    }

    // VariableTypeValueSetFieldSpec ::= valuesetfieldreference FieldName
    // ValueSetOptionalitySpec?
    protected class VariableTypeValueSetFieldSpecParser extends ListRuleParser<VariableTypeValueSetFieldSpecNode> {

        public VariableTypeValueSetFieldSpecNode parse() throws ParserException {
            return super.parse(new SequenceParser(new boolean[] { true, true, true, false },
                    TokenType.AMPERSAND, TokenType.TYPE_REFERENCE, fieldNameParser, valueSetOptionalitySpecParser),
                    a -> new VariableTypeValueSetFieldSpecNode(a.P(), a.s1(), a.n2(), a.n3()));
        }

    }

    // WithSyntaxSpec ::= WITH SYNTAX SyntaxList
    protected class WithSyntaxSpecParser implements RuleParser<List<TokenOrGroup>> {

        public List<TokenOrGroup> parse() throws ParserException {
            return new ValueExtractor<List<TokenOrGroup>>(2,
                    new SequenceParser(TokenType.WITH_KW, TokenType.SYNTAX_KW, syntaxListParser)).parse();
        }

    }

    // SyntaxList ::= "{" TokenOrGroupSpec empty + "}"
    protected class SyntaxListParser implements RuleParser<List<TokenOrGroup>> {

        public List<TokenOrGroup> parse() throws ParserException {
            return new ValueExtractor<List<TokenOrGroup>>(1, new SequenceParser(TokenType.L_BRACE,
                    new RepetitionParser<>(tokenOrGroupSpecParser), TokenType.R_BRACE)).parse();
        }

    }

    // TokenOrGroupSpec ::= RequiredToken | OptionalGroup
    protected class TokenOrGroupSpecParser implements RuleParser<TokenOrGroup> {

        @SuppressWarnings("unchecked")
        public TokenOrGroup parse() throws ParserException {
            return new ChoiceParser<>(requiredTokenParser, optionalGroupParser).parse();
        }

    }

    // OptionalGroup ::= "[" TokenOrGroupSpec empty + "]"
    protected class OptionalGroupParser implements RuleParser<Group> {

        public Group parse() throws ParserException {
            List<TokenOrGroup> rule = new ValueExtractor<List<TokenOrGroup>>(1,
                    new SequenceParser(new SingleTokenParser(
                            TokenType.L_BRACKET, Context.SYNTAX),
                            new RepetitionParser<>(tokenOrGroupSpecParser),
                            new SingleTokenParser(TokenType.R_BRACKET, Context.SYNTAX))).parse();

            if (rule != null) {
                return new Group(getPosition(rule), rule);
            }

            return null;
        }

    }

    // RequiredToken ::= Literal | PrimitiveFieldName
    protected class RequiredTokenParser implements RuleParser<RequiredToken> {

        @SuppressWarnings("unchecked")
        public RequiredToken parse() throws ParserException {
            Node rule = new ChoiceParser<>(primitiveFieldNameParser, literalDefinitionParser).parse();

            if (rule != null) {
                return new RequiredToken(rule.getPosition(), rule);
            }

            return null;
        }

    }

    // Literal ::= word | ","
    protected class LiteralDefinitionParser implements RuleParser<LiteralNode> {

        @SuppressWarnings("unchecked")
        public LiteralNode parse() throws ParserException {
            Token token = new ChoiceParser<>(new ValueExtractor<>(0,
                    new SequenceParser(new SingleTokenParser(TokenType.WORD, Context.SYNTAX),
                            new NegativeLookaheadParser(TokenType.DOT))),
                    new SingleTokenParser(TokenType.COMMA)).parse();

            if (token != null) {
                if (TokenType.WORD == token.getType()) {
                    return new LiteralNode(token.getPosition(), token.getText());
                } else {
                    return new LiteralNode(token.getPosition(), ",");
                }
            }

            return null;
        }

    }

    // Literal ::= word | ","
    protected class LiteralParser implements RuleParser<LiteralNode> {

        private Set<String> invalidWords = new HashSet<>(Arrays.asList(
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
            Token token = new ChoiceParser<>(new ValueExtractor<>(0,
                    new SequenceParser(new SingleTokenParser(TokenType.WORD, Context.SYNTAX),
                            new NegativeLookaheadParser(TokenType.DOT))),
                    new SingleTokenParser(TokenType.COMMA)).parse();

            if (token != null) {
                if (TokenType.WORD == token.getType()) {
                    if (invalidWords.contains(token.getText())) {
                        return null;
                    }

                    return new LiteralNode(token.getPosition(), token.getText());
                } else {
                    return new LiteralNode(token.getPosition(), ",");
                }
            }

            return null;
        }

    }

    // DefinedObject ::= ExternalObjectReference | objectreference
    protected class DefinedObjectParser implements RuleParser<ObjectNode> {

        @SuppressWarnings("unchecked")
        public ObjectReference parse() throws ParserException {
            Object rule = new ChoiceParser<>(externalObjectReferenceParser,
                    new SingleTokenParser(TokenType.IDENTIFIER)).parse();

            if (rule != null) {
                if (rule instanceof ExternalObjectReference) {
                    return (ObjectReference) rule;
                } else {
                    Token token = (Token) rule;

                    return new ObjectReference(token.getPosition(), token.getText());
                }
            }

            return null;
        }

    }

    // ExternalObjectReference ::= modulereference "." objectreference
    protected class ExternalObjectReferenceParser extends ListRuleParser<ExternalObjectReference> {

        public ExternalObjectReference parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.TYPE_REFERENCE, TokenType.DOT,
                    new SingleTokenParser(TokenType.IDENTIFIER)),
                    a -> new ExternalObjectReference(a.P(), a.s0(), a.s2())
            );
        }

    }

    // Object ::= DefinedObject | ObjectDefn | ObjectFromObject |
    // ParameterizedObject
    protected class ObjectParser implements RuleParser<ObjectNode> {

        @SuppressWarnings("unchecked")
        public ObjectNode parse() throws ParserException {
            Node rule = new ChoiceParser<>(parameterizedObjectParser, informationFromObjectsParser,
                    definedObjectParser, objectDefnParser).parse();

            if (rule != null) {
                if (rule instanceof InformationFromObjects) {
                    return new ObjectFromObjectNode(rule.getPosition(), (InformationFromObjects) rule);
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
            ObjectSyntaxNode rule = new ChoiceParser<>(defaultSyntaxParser, definedSyntaxParser).parse();

            if (rule != null) {
                return new ObjectDefnNode(rule.getPosition(), rule);
            }

            return null;
        }

    }

    // DefaultSyntax ::= "{" FieldSetting "," * "}"
    protected class DefaultSyntaxParser extends ListRuleParser<DefaultSyntaxNode> {

        @SuppressWarnings("unchecked")
        public DefaultSyntaxNode parse() throws ParserException {
            return super.parse(new SequenceParser(new boolean[] { true, false,    true },
                    new SingleTokenParser(TokenType.L_BRACE),
                    new CommaSeparatedRuleParser<>(fieldSettingParser),
                    new SingleTokenParser(TokenType.R_BRACE)),
                    a -> new DefaultSyntaxNode(a.P(), a.n1()));
        }

    }

    // FieldSetting ::= PrimitiveFieldName Setting
    protected class FieldSettingParser extends ListRuleParser<FieldSettingNode> {

        public FieldSettingNode parse() throws ParserException {
            List<Object> rule = new ChoiceParser<>(
                    new SequenceParser(valuePrimitiveFieldNameParser, settingParser),
                    new SequenceParser(typePrimitiveFieldNameParser, settingParser)).parse();

            if (rule != null) {
                PrimitiveFieldNameNode fieldName = (PrimitiveFieldNameNode) rule.get(0);

                return new FieldSettingNode(fieldName.getPosition(), fieldName, (Setting) rule.get(1));
            }

            return null;
        }

    }

    // DefinedSyntax ::= "{" DefinedSyntaxToken empty * "}"
    protected class DefinedSyntaxParser extends ListRuleParser<DefinedSyntaxNode> {

        @SuppressWarnings("unchecked")
        public DefinedSyntaxNode parse() throws ParserException {
            return super.parse(new SequenceParser(new boolean[] { true, false, true },
                    TokenType.L_BRACE, new RepetitionParser<>(definedSyntaxTokenParser), TokenType.R_BRACE),
                    a -> new DefinedSyntaxNode(a.P(), a.n1()));
        }

    }

    // DefinedSyntaxToken ::= Literal | Setting
    protected class DefinedSyntaxTokenParser implements RuleParser<Node> {

        @SuppressWarnings("unchecked")
        public Node parse() throws ParserException {
            return new ChoiceParser<>(literalParser, settingParser).parse();
        }

    }

    // Setting ::= Type | ValueSet | ObjectSet | Value | Object
    protected class SettingParser implements RuleParser<Node> {

        public Node parse() throws ParserException {
            Set<Node> rules = new AmbiguousChoiceParser<>(typeParser, valueSetParser, objectSetParser, valueParser,
                    objectParser).parse();

            Optional<Node> maybeFirst = rules.stream().findFirst();

            if (maybeFirst.isPresent()) {
                Node first = maybeFirst.get();

                Setting setting = new Setting(first.getPosition());

                rules.stream().forEach(rule -> {
                    if (rule instanceof Type type) {
                        setting.setType(type);
                    } else if (rule instanceof ElementSetSpecsNode valueSet) {
                        setting.setValueSet(valueSet);
                    } else if (rule instanceof ObjectSetSpecNode objectSet) {
                        setting.setObjectSet(objectSet);
                    } else if (rule instanceof Value value) {
                        setting.setValue(value);
                    } else if (rule instanceof ObjectNode object) {
                        setting.setObject(object);
                    }
                });

                return setting;
            }

            return null;
        }

    }

    // DefinedObjectSet ::= ExternalObjectSetReference | objectsetreference
    protected class DefinedObjectSetParser implements RuleParser<ObjectSetReference> {

        @SuppressWarnings("unchecked")
        public ObjectSetReference parse() throws ParserException {
            Object rule = new ChoiceParser<>(externalObjectSetReferenceParser,
                    new SingleTokenParser(TokenType.TYPE_REFERENCE)).parse();

            if (rule != null) {
                if (rule instanceof ExternalObjectSetReference) {
                    return (ObjectSetReference) rule;
                } else {
                    Token token = (Token) rule;

                    return new ObjectSetReference(token.getPosition(), token.getText());
                }
            }

            return null;
        }

    }

    // ExternalObjectSetReference ::= modulereference "." objectsetreference
    protected class ExternalObjectSetReferenceParser extends ListRuleParser<ExternalObjectSetReference> {

        public ExternalObjectSetReference parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.TYPE_REFERENCE, TokenType.DOT, TokenType.TYPE_REFERENCE),
                    a -> new ExternalObjectSetReference(a.P0(), a.s0(), a.s2()));
        }

    }

    // ObjectSetElements ::=
    // Object | DefinedObjectSet | ObjectSetFromObjects | ParameterizedObjectSet
    protected class ObjectSetElementsParser implements RuleParser<ObjectSetElements> {

        @SuppressWarnings("unchecked")
        public ObjectSetElements parse() throws ParserException {
            Node rule = new ChoiceParser<>(informationFromObjectsParser, parameterizedObjectSetParser, objectParser,
                    definedObjectSetParser).parse();

            if (rule != null) {
                return new ObjectSetElements(rule.getPosition(), rule);
            }

            return null;
        }

    }

    // ObjectClassFieldType ::= DefinedObjectClass "." FieldName
    protected class ObjectClassFieldTypeParser extends ListRuleParser<ObjectClassFieldType> {

        public ObjectClassFieldType parse() throws ParserException {
            return super.parse(new SequenceParser(definedObjectClassParser, TokenType.DOT, fieldNameParser),
                    a -> new ObjectClassFieldType(a.P(), a.n0(), a.n2()));
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
    protected class OpenTypeFieldValParser extends ListRuleParser<OpenTypeFieldValue> {

        public OpenTypeFieldValue parse() throws ParserException {
            return super.parse (new SequenceParser(typeParser, TokenType.COLON, valueParser),
                    a -> new OpenTypeFieldValue(a.P(), a.n0(), a.n2()));
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
            return new ChoiceParser<>(parameterizedObjectParser, definedObjectParser, parameterizedObjectSetParser,
                    definedObjectSetParser).parse();
        }

    }

    // InformationFromObjects ::=
    // ValueFromObject
    // | ValueSetFromObjects
    // | TypeFromObject
    // | ObjectFromObject
    // | ObjectSetFromObjects
    protected class InformationFromObjectsParser implements RuleParser<InformationFromObjects> {

        @SuppressWarnings("unchecked")
        public InformationFromObjects parse() throws ParserException {
            return new ChoiceParser<>(typeFromObjectParser, valueFromObjectParser).parse();
        }

    }

    // ValueSetFromObjects ::= ReferencedObjects "." FieldName
    // TypeFromObject ::= ReferencedObjects "." FieldName
    // ObjectFromObject ::= ReferencedObjects "." FieldName
    // ObjectSetFromObjects ::= ReferencedObjects "." FieldName
    protected class TypeFromObjectParser extends ListRuleParser<TypeFromObject> {

        public TypeFromObject parse() throws ParserException {
            return super.parse(new SequenceParser(referencedObjectsParser, TokenType.DOT, fieldNameParser),
                    a -> {
                        PrimitiveFieldNameNode lastField = a.<FieldNameNode>n2().getPrimitiveFieldNames()
                                .get(a.<FieldNameNode>n2().getPrimitiveFieldNames().size() - 1);

                        if (!(lastField.isValueFieldReference())) {
                            return new TypeFromObject(a.P(), a.n0(), a.n2());
                        }

                        return null;
                    });
        }

    }

    // ValueFromObject ::= ReferencedObjects "." FieldName
    protected class ValueFromObjectParser extends ListRuleParser<ValueFromObject> {

        public ValueFromObject parse() throws ParserException {
            return super.parse(new SequenceParser(referencedObjectsParser, TokenType.DOT, fieldNameParser),
                    a -> {
                        PrimitiveFieldNameNode lastField = a.<FieldNameNode>n2().getPrimitiveFieldNames()
                                .get(a.<FieldNameNode>n2().getPrimitiveFieldNames().size() - 1);

                        if (lastField.isValueFieldReference()) {
                            return new ValueFromObject(a.P(), a.n0(), a.n2());
                        }

                        return null;
                    });
        }

    }

    // GeneralConstraint ::= UserDefinedConstraint | TableConstraint |
    // ContentsConstraint
    protected class GeneralConstraintParser implements RuleParser<Constraint> {

        @SuppressWarnings("unchecked")
        public Constraint parse() throws ParserException {
            return new ChoiceParser<Constraint>(userDefinedConstraintParser, tableConstraintParser,
                    contentsConstraintParser).parse();
        }

    }

    // UserDefinedConstraint ::= CONSTRAINED BY "{"
    // UserDefinedConstraintParameter "," * "}"
    protected class UserDefinedConstraintParser extends ListRuleParser<UserDefinedConstraint> {

        @SuppressWarnings("unchecked")
        public UserDefinedConstraint parse() throws ParserException {
            return super.parse(new SequenceParser(
                    new boolean[] { true, true, true, false, true },
                    TokenType.CONSTRAINED_KW, TokenType.BY_KW, TokenType.L_BRACE,
                    new CommaSeparatedRuleParser<>(userDefinedConstraintParameterParser), TokenType.R_BRACE),
                    a -> new UserDefinedConstraint(a.P(), a.n3()));
        }

    }

    // UserDefinedConstraintParameter ::=
    // Governor ":" Value
    // | Governor ":" Object
    // | DefinedObjectSet
    // | Type
    // | DefinedObjectClass
    protected class UserDefinedConstraintParameterParser implements RuleParser<UserDefinedConstraintParam> {

        @SuppressWarnings("unchecked")
        public UserDefinedConstraintParam parse() throws ParserException {
            Object rule = new ChoiceParser<>(new SequenceParser(governorParser, TokenType.COLON,
                    new ChoiceParser<>(valueParser, objectParser)), typeParser, usefulObjectClassReferenceParser).parse();

            if (rule != null) {
                if (rule instanceof List) {
                    List<Object> ruleList = (List<Object>) rule;

                    return new UserDefinedConstraintParam(getPosition(ruleList), (Governor) ruleList.get(0),
                            (Node) ruleList.get(2));
                } else {
                    Node node = (Node) rule;

                    return new UserDefinedConstraintParam(node.getPosition(), node);
                }
            }

            return null;
        }

    }

    // TableConstraint ::= SimpleTableConstraint | ComponentRelationConstraint
    protected class TableConstraintParser implements RuleParser<TableConstraint> {

        @SuppressWarnings("unchecked")
        public TableConstraint parse() throws ParserException {
            return new ChoiceParser<>(componentRelationConstraintParser, simpleTableConstraintParser).parse();
        }

    }

    // SimpleTableConstraint ::= ObjectSet
    protected class SimpleTableConstraintParser implements RuleParser<SimpleTableConstraint> {

        public SimpleTableConstraint parse() throws ParserException {
            Object rule = objectSetParser.parse();

            if (rule != null) {
                ObjectSetSpecNode node = (ObjectSetSpecNode) rule;

                return new SimpleTableConstraint(node.getPosition(), node);
            }

            return null;
        }

    }

    // ComponentRelationConstraint ::= "{" DefinedObjectSet "}" "{" AtNotation
    // "," + "}"
    protected class ComponentRelationConstraintParser extends ListRuleParser<ComponentRelationConstraint> {

        @SuppressWarnings("unchecked")
        public ComponentRelationConstraint parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.L_BRACE, definedObjectSetParser, TokenType.R_BRACE,
                    TokenType.L_BRACE, new CommaSeparatedRuleParser<>(atNotationParser), TokenType.R_BRACE),
                    a -> new ComponentRelationConstraint(a.P(), a.n1(), a.n4()));
        }

    }

    // AtNotation ::= "@" ComponentIdList | "@." Level ComponentIdList
    protected class AtNotationParser implements RuleParser<AtNotationNode> {

        @SuppressWarnings("unchecked")
        public AtNotationNode parse() throws ParserException {
            List<Object> rule = new ChoiceParser<>(
                    new SequenceParser(TokenType.AT, componentIdListParser),
                    new SequenceParser(new boolean[] { true, true, false, true },
                            TokenType.AT, new SingleTokenParser(TokenType.DOT, Context.LEVEL), levelParser,
                            componentIdListParser)).parse();

            if (rule != null) {
                if (rule.size() == 2) {
                    return new AtNotationNode(getPosition(rule), (ComponentIdListNode) rule.get(1));
                } else {
                    return new AtNotationNode(getPosition(rule), (ComponentIdListNode) rule.get(3),
                            rule.get(1) != null ? ((Integer) rule.get(2)) + 1 : 0);
                }
            }

            return null;
        }

    }

    // Level ::= "." Level | empty
    protected class LevelParser extends ListRuleParser<Integer> {

        public Integer parse() throws ParserException {
            return super.parse(new RepetitionParser<>(new SingleTokenParser(TokenType.DOT, Context.LEVEL)),
                    ListAccessor::size, 0);
        }

    }

    // ComponentIdList ::= identifier "." +
    protected class ComponentIdListParser extends ListRuleParser<ComponentIdListNode> {

        public ComponentIdListNode parse() throws ParserException {
            List<Token> rule = new TokenSeparatedRuleParser<>(
                    new SingleTokenParser(TokenType.IDENTIFIER), TokenType.DOT).parse();

            if (rule != null) {
                return new ComponentIdListNode(getPosition(rule), rule);
            }

            return null;
        }

    }

    // ContentsConstraint ::=
    // CONTAINING Type
    // | ENCODED BY Value
    // | CONTAINING Type ENCODED BY Value
    protected class ContentsConstraintParser implements RuleParser<ContentsConstraint> {

        @SuppressWarnings("unchecked")
        public ContentsConstraint parse() throws ParserException {
            List<Object> rule = new ChoiceParser<>(
                    new SequenceParser(TokenType.CONTAINING_KW, typeParser, TokenType.ENCODED_KW, TokenType.BY_KW,
                            valueParser),
                    new SequenceParser(TokenType.CONTAINING_KW, typeParser),
                    new SequenceParser(TokenType.ENCODED_KW, TokenType.BY_KW, valueParser)).parse();

            if (rule != null) {
                Position position = getPosition(rule);

                switch (rule.size()) {
                    case 2:
                        return new ContentsConstraint(position, (Type) rule.get(1));

                    case 3:
                        return new ContentsConstraint(position, (Value) rule.get(2));

                    case 5:
                        return new ContentsConstraint(position, (Type) rule.get(1), (Value) rule.get(4));
                    default:
                        throw new ParserException("Invalid rule");
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
    protected class ParameterizedAssignmentParser implements RuleParser<ParameterizedAssignmentNode> {

        @SuppressWarnings("unchecked")
        public ParameterizedAssignmentNode parse() throws ParserException {
            return new ChoiceParser<>(parameterizedTypeOrObjectClassAssignmentParser, parameterizedValueOrObjectAssignmentParser,
                    parameterizedValueSetTypeAssignmentParser, parameterizedObjectSetAssignmentParser).parse();
        }

    }

    // ParameterizedTypeAssignment ::= typereference ParameterList "::=" Type
    // ParameterizedObjectClassAssignment ::= objectclassreference ParameterList "::=" ObjectClass
    protected class ParameterizedTypeOrObjectClassAssignmentParser extends
            ListRuleParser<ParameterizedTypeOrObjectClassAssignmentNode> {

        @SuppressWarnings("unchecked")
        public ParameterizedTypeOrObjectClassAssignmentNode parse() throws ParserException {
            Set<List<Object>> rules = new AmbiguousChoiceParser<>(
                    new SequenceParser(TokenType.TYPE_REFERENCE, parameterListParser, TokenType.ASSIGN, typeParser),
                    new SequenceParser(new SingleTokenParser(TokenType.OBJECT_CLASS_REFERENCE, Context.OBJECT_CLASS),
                            parameterListParser, TokenType.ASSIGN, objectClassParser)).parse();

            Optional<List<Object>> first = rules.stream().findFirst();

            if (first.isPresent()) {
                Token token = (Token) first.get().get(0);
                List<ParameterNode> parameters = (List<ParameterNode>) first.get().get(1);
                ParameterizedTypeOrObjectClassAssignmentNode rule =
                        new ParameterizedTypeOrObjectClassAssignmentNode(token.getPosition(), token.getText(),
                                parameters);

                rules.stream().forEach(r -> {
                    TokenType tokenType = ((Token) r.get(0)).getType();

                    switch (tokenType) {
                        case TYPE_REFERENCE:
                            var parameterizedTypeAssignmentNode = new ParameterizedTypeAssignmentNode(rule.getPosition(),
                                    rule.getReference(), parameters, (Type) r.get(3));
                            rule.setParameterizedTypeAssignment(parameterizedTypeAssignmentNode);
                            break;
                        case OBJECT_CLASS_REFERENCE:
                            var parameterizedObjectClassAssignmentNode = new ParameterizedObjectClassAssignmentNode(rule.getPosition(),
                                    rule.getReference(), parameters, (ObjectClassNode) r.get(3));
                            rule.setParameterizedObjectClassAssignment(parameterizedObjectClassAssignmentNode);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected token type: " + tokenType.toString());
                    }
                });

                return rule;
            }

            return null;
        }

    }

    // ParameterizedValueAssignment ::= valuereference ParameterList Type "::=" Value
    // ParameterizedObjectAssignment ::= objectreference ParameterList DefinedObjectClass "::=" Object
    protected class ParameterizedValueOrObjectAssignmentParser implements
            RuleParser<ParameterizedValueOrObjectAssignmentNode> {

        public ParameterizedValueOrObjectAssignmentNode parse() throws ParserException {
            Set<List<Object>> rules = new AmbiguousChoiceParser<>(
                    new SequenceParser(new SingleTokenParser(TokenType.VALUE_REFERENCE, Context.VALUE),
                            parameterListParser, typeParser, TokenType.ASSIGN, valueParser),
                    new SequenceParser(new SingleTokenParser(TokenType.OBJECT_REFERENCE, Context.OBJECT),
                            parameterListParser, definedObjectClassParser, TokenType.ASSIGN, objectParser )).parse();

            Optional<List<Object>> first = rules.stream().findFirst();

            if (first.isPresent()) {
                Token token = (Token) first.get().get(0);
                List<ParameterNode> parameters = (List<ParameterNode>) first.get().get(1);
                ParameterizedValueOrObjectAssignmentNode rule =
                        new ParameterizedValueOrObjectAssignmentNode(token.getPosition(), token.getText(),
                                parameters);

                rules.stream().forEach(r -> {
                    TokenType tokenType = ((Token) r.get(0)).getType();

                    switch (tokenType) {
                        case VALUE_REFERENCE:
                            var parameterizedValueAssignmentNode = new ParameterizedValueAssignmentNode(rule.getPosition(),
                                    rule.getReference(), parameters, (Type) r.get(2), (Value) r.get(4));
                            rule.setParameterizedValueAssignmentNode(parameterizedValueAssignmentNode);
                            break;
                        case OBJECT_REFERENCE:
                            var parameterizedObjectAssignmentNode = new ParameterizedObjectAssignmentNode(rule.getPosition(),
                                    rule.getReference(), parameters, (ObjectClassReference) r.get(2), (ObjectNode) r.get(4));
                            rule.setParameterizedObjectAssignmentNode(parameterizedObjectAssignmentNode);
                            break;
                        default:
                            throw new IllegalStateException("Unexpected token type: " + tokenType.toString());
                    }
                });

                return rule;
            }

            return null;
        }

    }

    // ParameterizedValueSetTypeAssignment ::=
    // typereference ParameterList Type "::=" ValueSet
    protected class ParameterizedValueSetTypeAssignmentParser extends
            ListRuleParser<ParameterizedValueSetTypeAssignmentNode> {

        @SuppressWarnings("unchecked")
        public ParameterizedValueSetTypeAssignmentNode parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.TYPE_REFERENCE, parameterListParser, typeParser,
                    TokenType.ASSIGN, valueSetParser),
                    a -> new ParameterizedValueSetTypeAssignmentNode(a.P0(), a.s0(), a.n1(), a.n2(), a.n4()));
        }

    }

    // ParameterizedObjectSetAssignment ::=
    // objectsetreference ParameterList DefinedObjectClass "::=" ObjectSet
    protected class ParameterizedObjectSetAssignmentParser extends ListRuleParser<ParameterizedObjectSetAssignmentNode> {

        @SuppressWarnings("unchecked")
        public ParameterizedObjectSetAssignmentNode parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.TYPE_REFERENCE, parameterListParser,
                    definedObjectClassParser, TokenType.ASSIGN, objectSetParser),
                    a -> new ParameterizedObjectSetAssignmentNode(a.P0(), a.s0(), a.n1(), a.n2(), a.n4()));
        }

    }

    // ParameterList ::= "{" Parameter "," + "}"
    protected class ParameterListParser implements RuleParser<List<ParameterNode>> {

        public List<ParameterNode> parse() throws ParserException {
            return new ValueExtractor<List<ParameterNode>>(1,
                    new SequenceParser(TokenType.L_BRACE,
                            new CommaSeparatedRuleParser<>(parameterParser), TokenType.R_BRACE)).parse();
        }

    }

    // Parameter ::= ParamGovernor ":" DummyReference | DummyReference
    protected class ParameterParser implements RuleParser<ParameterNode> {

        @SuppressWarnings("unchecked")
        public ParameterNode parse() throws ParserException {
            List<Object> rule = new ChoiceParser<>(
                    new SequenceParser(paramGovernorParser, TokenType.COLON, dummyReferenceParser),
                    new SequenceParser(dummyReferenceParser)).parse();

            if (rule != null) {
                if (rule.size() == 3) {
                    return new ParameterNode(getPosition(rule), (ParamGovernorNode) rule.get(0),
                            (ReferenceNode) rule.get(2));
                } else {
                    return new ParameterNode(getPosition(rule), (ReferenceNode) rule.get(0));
                }
            }

            return null;
        }

    }

    // ParamGovernor ::= Governor | DummyGovernor
    protected class ParamGovernorParser implements RuleParser<ParamGovernorNode> {

        @SuppressWarnings("unchecked")
        public ParamGovernorNode parse() throws ParserException {
            return new ChoiceParser<>(dummyGovernorParser, governorParser).parse();
        }

    }

    // Governor ::= Type | DefinedObjectClass
    protected class GovernorParser implements RuleParser<Governor> {

        @SuppressWarnings("unchecked")
        public Governor parse() throws ParserException {
            Node rule = new ChoiceParser<>(typeParser, definedObjectClassParser).parse();

            if (rule != null) {
                return new Governor(rule.getPosition(), rule);
            }

            return null;
        }

    }

    // DummyGovernor ::= DummyReference
    protected class DummyGovernorParser implements RuleParser<DummyGovernor> {

        public DummyGovernor parse() throws ParserException {
            ReferenceNode rule = dummyReferenceParser.parse();

            if (rule != null) {
                return new DummyGovernor(rule.getPosition(), rule);
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
    protected class SimpleDefinedTypeParser implements RuleParser<SimpleDefinedType> {

        @SuppressWarnings("unchecked")
        public SimpleDefinedType parse() throws ParserException {
            return new ChoiceParser<>(externalTypeReferenceParser, typeReferenceParser).parse();
        }

    }

    // SimpleDefinedValue ::= ExternalValueReference | valuereference
    // EnumeratedValue ::= identifier
    protected class SimpleDefinedValueParser implements RuleParser<DefinedValue> {

        @SuppressWarnings("unchecked")
        public SimpleDefinedValue parse() throws ParserException {
            Object rule = new ChoiceParser<>(
                    externalValueReferenceParser, new SingleTokenParser(TokenType.IDENTIFIER)).parse();

            if (rule != null) {
                if (rule instanceof Token) {
                    Token token = (Token) rule;

                    return new SimpleDefinedValue(token.getPosition(), (token).getText());
                } else {
                    return (SimpleDefinedValue) rule;
                }
            }

            return null;
        }

    }

    // ParameterizedType ::= SimpleDefinedType ActualParameterList
    // ParameterizedValueSetType ::= SimpleDefinedType ActualParameterList
    protected class ParameterizedTypeParser extends ListRuleParser<SimpleDefinedType> {

        @SuppressWarnings("unchecked")
        public SimpleDefinedType parse() throws ParserException {
            return super.parse(new SequenceParser(simpleDefinedTypeParser, actualParameterListParser),
                    a -> a.<SimpleDefinedType>n0().parameters(a.n1()));
        }

    }

    // ParameterizedValue ::= SimpleDefinedValue ActualParameterList
    protected class ParameterizedValueParser extends ListRuleParser<SimpleDefinedValue> {

        @SuppressWarnings("unchecked")
        public SimpleDefinedValue parse() throws ParserException {
            return super.parse(new SequenceParser(simpleDefinedValueParser, actualParameterListParser),
                    a -> a.<SimpleDefinedValue>n0().parameters(a.n1()));
        }

    }

    // ParameterizedObjectClass ::= DefinedObjectClass ActualParameterList
    protected class ParameterizedObjectClassParser extends ListRuleParser<ObjectClassReference> {

        @SuppressWarnings("unchecked")
        public ObjectClassReference parse() throws ParserException {
            return super.parse(new SequenceParser(definedObjectClassParser, actualParameterListParser),
                    a -> (a.<ObjectClassReference>n0()).parameters(a.n1()));
        }

    }

    // ParameterizedObjectSet ::= DefinedObjectSet ActualParameterList
    protected class ParameterizedObjectSetParser extends ListRuleParser<ObjectSetReference> {

        @SuppressWarnings("unchecked")
        public ObjectSetReference parse() throws ParserException {
            return super.parse(new SequenceParser(definedObjectSetParser, actualParameterListParser),
                    a -> (a.<ObjectSetReference>n0()).parameters(a.n1()));
        }

    }

    // ParameterizedObject ::= DefinedObject ActualParameterList
    protected class ParameterizedObjectParser extends ListRuleParser<ObjectNode> {

        @SuppressWarnings("unchecked")
        public ObjectReference parse() throws ParserException {
            return (ObjectReference) super.parse(new SequenceParser(definedObjectParser, actualParameterListParser),
                    a -> (a.<ObjectReference>n0()).parameters(a.n1()));
        }

    }

    // ActualParameterList ::= "{" ActualParameter "," + "}"
    protected class ActualParameterListParser extends ListRuleParser<List<Node>> {

        @SuppressWarnings("unchecked")
        public List<Node> parse() throws ParserException {
            return super.parse(new SequenceParser(TokenType.L_BRACE,
                    new CommaSeparatedRuleParser<>(actualParameterParser), TokenType.R_BRACE),
                    ListAccessor::n1);
        }

    }

    // ActualParameter ::= Type | Value | ValueSet | DefinedObjectClass | Object
    // | ObjectSet
    protected class ActualParameterParser implements RuleParser<Node> {

        @SuppressWarnings("unchecked")
        public ActualParameter parse() throws ParserException {
            Set<Node> rules = new AmbiguousChoiceParser<>(typeParser, valueParser, objectParser, valueSetParser,
                    objectSetParser, definedObjectClassParser).parse();
            Optional<Node> first = rules.stream().findFirst();

            if (first.isPresent()) {
                ActualParameter rule = new ActualParameter(first.get().getPosition());

                for (Node node : rules) {
                    if (node instanceof Type type) {
                        rule.setType(type);
                    } else if (node instanceof Value value) {
                        rule.setValue(value);
                    } else if (node instanceof ObjectNode object) {
                        rule.setObject(object);
                    } else if (node instanceof ElementSetSpecsNode elementSetSpecs) {
                        rule.setElementSetSpecs(elementSetSpecs);
                    } else if (node instanceof ObjectSetSpecNode objectSetSpec) {
                        rule.setObjectSetSpec(objectSetSpec);
                    } else if (node instanceof ObjectClassReference objectClassReference) {
                        rule.setObjectClassReference(objectClassReference);
                    }
                }

                return rule;
            }

            return null;
        }

    }

}
