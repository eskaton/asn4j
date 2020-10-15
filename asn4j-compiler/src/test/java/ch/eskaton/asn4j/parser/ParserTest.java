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

import ch.eskaton.asn4j.parser.Parser.ActualParameterListParser;
import ch.eskaton.asn4j.parser.Parser.ActualParameterParser;
import ch.eskaton.asn4j.parser.Parser.AlternativeTypeListParser;
import ch.eskaton.asn4j.parser.Parser.AlternativeTypeListsParser;
import ch.eskaton.asn4j.parser.Parser.AssignedIdentifierParser;
import ch.eskaton.asn4j.parser.Parser.AssignmentListParser;
import ch.eskaton.asn4j.parser.Parser.AssignmentParser;
import ch.eskaton.asn4j.parser.Parser.AtNotationParser;
import ch.eskaton.asn4j.parser.Parser.BitOrOctetStringValueParser;
import ch.eskaton.asn4j.parser.Parser.BooleanValueParser;
import ch.eskaton.asn4j.parser.Parser.BuiltinOrReferencedValueParser;
import ch.eskaton.asn4j.parser.Parser.BuiltinTypeParser;
import ch.eskaton.asn4j.parser.Parser.BuiltinTypeParserAux;
import ch.eskaton.asn4j.parser.Parser.CharSymsParser;
import ch.eskaton.asn4j.parser.Parser.CharacterStringListParser;
import ch.eskaton.asn4j.parser.Parser.CharacterStringTypeParser;
import ch.eskaton.asn4j.parser.Parser.CharacterStringValueParser;
import ch.eskaton.asn4j.parser.Parser.CharsDefnParser;
import ch.eskaton.asn4j.parser.Parser.ChoiceValueParser;
import ch.eskaton.asn4j.parser.Parser.ClassNumberParser;
import ch.eskaton.asn4j.parser.Parser.ClassParser;
import ch.eskaton.asn4j.parser.Parser.CollectionValueParser;
import ch.eskaton.asn4j.parser.Parser.ComponentConstraintParser;
import ch.eskaton.asn4j.parser.Parser.ComponentIdListParser;
import ch.eskaton.asn4j.parser.Parser.ComponentRelationConstraintParser;
import ch.eskaton.asn4j.parser.Parser.ComponentTypeListParser;
import ch.eskaton.asn4j.parser.Parser.ComponentTypeListsParser;
import ch.eskaton.asn4j.parser.Parser.ComponentTypeParser;
import ch.eskaton.asn4j.parser.Parser.ConstraintParser;
import ch.eskaton.asn4j.parser.Parser.ConstraintSpecParser;
import ch.eskaton.asn4j.parser.Parser.ContainedSubtypeParser;
import ch.eskaton.asn4j.parser.Parser.ContentsConstraintParser;
import ch.eskaton.asn4j.parser.Parser.DefaultSyntaxParser;
import ch.eskaton.asn4j.parser.Parser.DefinedObjectClassParser;
import ch.eskaton.asn4j.parser.Parser.DefinedObjectParser;
import ch.eskaton.asn4j.parser.Parser.DefinedObjectSetParser;
import ch.eskaton.asn4j.parser.Parser.DefinedSyntaxParser;
import ch.eskaton.asn4j.parser.Parser.DefinedSyntaxTokenParser;
import ch.eskaton.asn4j.parser.Parser.DefinedValueParser;
import ch.eskaton.asn4j.parser.Parser.DefinitiveIdentificationParser;
import ch.eskaton.asn4j.parser.Parser.DefinitiveNameAndNumberFormParser;
import ch.eskaton.asn4j.parser.Parser.DefinitiveNumberFormParser;
import ch.eskaton.asn4j.parser.Parser.DefinitiveOIDParser;
import ch.eskaton.asn4j.parser.Parser.DefinitiveObjIdComponentListParser;
import ch.eskaton.asn4j.parser.Parser.DefinitiveObjIdComponentParser;
import ch.eskaton.asn4j.parser.Parser.DummyGovernorParser;
import ch.eskaton.asn4j.parser.Parser.DummyReferenceParser;
import ch.eskaton.asn4j.parser.Parser.DurationRangeParser;
import ch.eskaton.asn4j.parser.Parser.ElementSetSpecParser;
import ch.eskaton.asn4j.parser.Parser.ElementSetSpecsParser;
import ch.eskaton.asn4j.parser.Parser.ElementsParser;
import ch.eskaton.asn4j.parser.Parser.EmptyValueParser;
import ch.eskaton.asn4j.parser.Parser.EncodingControlSectionParser;
import ch.eskaton.asn4j.parser.Parser.EncodingControlSectionsParser;
import ch.eskaton.asn4j.parser.Parser.EncodingPrefixParser;
import ch.eskaton.asn4j.parser.Parser.EncodingPrefixedTypeParser;
import ch.eskaton.asn4j.parser.Parser.EncodingReferenceDefaultParser;
import ch.eskaton.asn4j.parser.Parser.EncodingReferenceParser;
import ch.eskaton.asn4j.parser.Parser.EnumerationItemParser;
import ch.eskaton.asn4j.parser.Parser.EnumerationParser;
import ch.eskaton.asn4j.parser.Parser.EnumerationsParser;
import ch.eskaton.asn4j.parser.Parser.ExceptionIdentificationParser;
import ch.eskaton.asn4j.parser.Parser.ExceptionSpecParser;
import ch.eskaton.asn4j.parser.Parser.ExclusionsParser;
import ch.eskaton.asn4j.parser.Parser.ExportsParser;
import ch.eskaton.asn4j.parser.Parser.ExtensionAdditionAlternativeParser;
import ch.eskaton.asn4j.parser.Parser.ExtensionAdditionAlternativesGroupParser;
import ch.eskaton.asn4j.parser.Parser.ExtensionAdditionAlternativesListParser;
import ch.eskaton.asn4j.parser.Parser.ExtensionAdditionAlternativesParser;
import ch.eskaton.asn4j.parser.Parser.ExtensionAdditionGroupParser;
import ch.eskaton.asn4j.parser.Parser.ExtensionAdditionListParser;
import ch.eskaton.asn4j.parser.Parser.ExtensionAdditionParser;
import ch.eskaton.asn4j.parser.Parser.ExtensionAdditionsParser;
import ch.eskaton.asn4j.parser.Parser.ExtensionAndExceptionParser;
import ch.eskaton.asn4j.parser.Parser.ExtensionDefaultParser;
import ch.eskaton.asn4j.parser.Parser.ExtensionEndMarkerParser;
import ch.eskaton.asn4j.parser.Parser.ExternalObjectClassReferenceParser;
import ch.eskaton.asn4j.parser.Parser.ExternalObjectReferenceParser;
import ch.eskaton.asn4j.parser.Parser.ExternalObjectSetReferenceParser;
import ch.eskaton.asn4j.parser.Parser.ExternalTypeReferenceParser;
import ch.eskaton.asn4j.parser.Parser.ExternalValueReferenceParser;
import ch.eskaton.asn4j.parser.Parser.FieldNameParser;
import ch.eskaton.asn4j.parser.Parser.FieldSettingParser;
import ch.eskaton.asn4j.parser.Parser.FieldSpecParser;
import ch.eskaton.asn4j.parser.Parser.FixedTypeFieldValParser;
import ch.eskaton.asn4j.parser.Parser.FixedTypeValueOrObjectFieldSpecParser;
import ch.eskaton.asn4j.parser.Parser.FixedTypeValueSetOrObjectSetFieldSpecParser;
import ch.eskaton.asn4j.parser.Parser.FullSpecificationParser;
import ch.eskaton.asn4j.parser.Parser.GeneralConstraintParser;
import ch.eskaton.asn4j.parser.Parser.GlobalModuleReferenceParser;
import ch.eskaton.asn4j.parser.Parser.GovernorParser;
import ch.eskaton.asn4j.parser.Parser.IRIValueParser;
import ch.eskaton.asn4j.parser.Parser.IdentifierListParser;
import ch.eskaton.asn4j.parser.Parser.ImportsParser;
import ch.eskaton.asn4j.parser.Parser.InformationFromObjectsParser;
import ch.eskaton.asn4j.parser.Parser.InnerTypeConstraintsParser;
import ch.eskaton.asn4j.parser.Parser.IntegerValueParser;
import ch.eskaton.asn4j.parser.Parser.IntersectionElementsParser;
import ch.eskaton.asn4j.parser.Parser.IntersectionsParser;
import ch.eskaton.asn4j.parser.Parser.LiteralDefinitionParser;
import ch.eskaton.asn4j.parser.Parser.LiteralParser;
import ch.eskaton.asn4j.parser.Parser.LowerEndValueParser;
import ch.eskaton.asn4j.parser.Parser.LowerEndpointParser;
import ch.eskaton.asn4j.parser.Parser.ModuleBodyParser;
import ch.eskaton.asn4j.parser.Parser.ModuleIdentifierParser;
import ch.eskaton.asn4j.parser.Parser.MultipleTypeConstraintsParser;
import ch.eskaton.asn4j.parser.Parser.NameAndNumberFormParser;
import ch.eskaton.asn4j.parser.Parser.NameFormParser;
import ch.eskaton.asn4j.parser.Parser.NamedBitListParser;
import ch.eskaton.asn4j.parser.Parser.NamedBitParser;
import ch.eskaton.asn4j.parser.Parser.NamedConstraintParser;
import ch.eskaton.asn4j.parser.Parser.NamedNumberListParser;
import ch.eskaton.asn4j.parser.Parser.NamedNumberParser;
import ch.eskaton.asn4j.parser.Parser.NamedTypeParser;
import ch.eskaton.asn4j.parser.Parser.NamedValueListParser;
import ch.eskaton.asn4j.parser.Parser.NamedValueParser;
import ch.eskaton.asn4j.parser.Parser.NullValueParser;
import ch.eskaton.asn4j.parser.Parser.NumberFormParser;
import ch.eskaton.asn4j.parser.Parser.NumericRealValueParser;
import ch.eskaton.asn4j.parser.Parser.ObjIdComponentsListParser;
import ch.eskaton.asn4j.parser.Parser.ObjIdComponentsParser;
import ch.eskaton.asn4j.parser.Parser.ObjectClassDefnParser;
import ch.eskaton.asn4j.parser.Parser.ObjectClassFieldTypeParser;
import ch.eskaton.asn4j.parser.Parser.ObjectClassFieldValueParser;
import ch.eskaton.asn4j.parser.Parser.ObjectClassParser;
import ch.eskaton.asn4j.parser.Parser.ObjectDefnParser;
import ch.eskaton.asn4j.parser.Parser.ObjectIdentifierValueParser;
import ch.eskaton.asn4j.parser.Parser.ObjectParser;
import ch.eskaton.asn4j.parser.Parser.ObjectSetElementsParser;
import ch.eskaton.asn4j.parser.Parser.ObjectSetOptionalitySpecParser;
import ch.eskaton.asn4j.parser.Parser.ObjectSetParser;
import ch.eskaton.asn4j.parser.Parser.OpenTypeFieldValParser;
import ch.eskaton.asn4j.parser.Parser.OptionalExtensionMarkerParser;
import ch.eskaton.asn4j.parser.Parser.OptionalGroupParser;
import ch.eskaton.asn4j.parser.Parser.ParamGovernorParser;
import ch.eskaton.asn4j.parser.Parser.ParameterListParser;
import ch.eskaton.asn4j.parser.Parser.ParameterParser;
import ch.eskaton.asn4j.parser.Parser.ParameterizedObjectClassParser;
import ch.eskaton.asn4j.parser.Parser.ParameterizedObjectParser;
import ch.eskaton.asn4j.parser.Parser.ParameterizedObjectSetAssignmentParser;
import ch.eskaton.asn4j.parser.Parser.ParameterizedObjectSetParser;
import ch.eskaton.asn4j.parser.Parser.ParameterizedTypeParser;
import ch.eskaton.asn4j.parser.Parser.ParameterizedValueParser;
import ch.eskaton.asn4j.parser.Parser.ParameterizedValueSetTypeAssignmentParser;
import ch.eskaton.asn4j.parser.Parser.PartialSpecificationParser;
import ch.eskaton.asn4j.parser.Parser.PatternConstraintParser;
import ch.eskaton.asn4j.parser.Parser.PermittedAlphabetParser;
import ch.eskaton.asn4j.parser.Parser.PrefixedTypeParser;
import ch.eskaton.asn4j.parser.Parser.PresenceConstraintParser;
import ch.eskaton.asn4j.parser.Parser.PrimitiveFieldNameParser;
import ch.eskaton.asn4j.parser.Parser.PropertyAndSettingPairParser;
import ch.eskaton.asn4j.parser.Parser.PropertySettingsParser;
import ch.eskaton.asn4j.parser.Parser.RealValueParser;
import ch.eskaton.asn4j.parser.Parser.ReferenceParser;
import ch.eskaton.asn4j.parser.Parser.ReferencedObjectsParser;
import ch.eskaton.asn4j.parser.Parser.ReferencedTypeParser;
import ch.eskaton.asn4j.parser.Parser.RelativeIRIValueParser;
import ch.eskaton.asn4j.parser.Parser.RelativeOIDComponentsListParser;
import ch.eskaton.asn4j.parser.Parser.RelativeOIDComponentsParser;
import ch.eskaton.asn4j.parser.Parser.RelativeOIDValueParser;
import ch.eskaton.asn4j.parser.Parser.RequiredTokenParser;
import ch.eskaton.asn4j.parser.Parser.RestrictedCharacterStringTypeParser;
import ch.eskaton.asn4j.parser.Parser.RestrictedCharacterStringValueParser;
import ch.eskaton.asn4j.parser.Parser.RootAlternativeTypeListParser;
import ch.eskaton.asn4j.parser.Parser.SelectionTypeParser;
import ch.eskaton.asn4j.parser.Parser.SettingParser;
import ch.eskaton.asn4j.parser.Parser.SignedNumberParser;
import ch.eskaton.asn4j.parser.Parser.SimpleDefinedTypeParser;
import ch.eskaton.asn4j.parser.Parser.SimpleDefinedValueParser;
import ch.eskaton.asn4j.parser.Parser.SimpleTableConstraintParser;
import ch.eskaton.asn4j.parser.Parser.SingleValueParser;
import ch.eskaton.asn4j.parser.Parser.SizeConstraintParser;
import ch.eskaton.asn4j.parser.Parser.SpecialRealValueParser;
import ch.eskaton.asn4j.parser.Parser.SubtypeConstraintParser;
import ch.eskaton.asn4j.parser.Parser.SubtypeElementsParser;
import ch.eskaton.asn4j.parser.Parser.SymbolListParser;
import ch.eskaton.asn4j.parser.Parser.SymbolParser;
import ch.eskaton.asn4j.parser.Parser.SymbolsFromModuleListParser;
import ch.eskaton.asn4j.parser.Parser.SymbolsFromModuleParser;
import ch.eskaton.asn4j.parser.Parser.SyntaxListParser;
import ch.eskaton.asn4j.parser.Parser.TableConstraintParser;
import ch.eskaton.asn4j.parser.Parser.TagDefaultParser;
import ch.eskaton.asn4j.parser.Parser.TagParser;
import ch.eskaton.asn4j.parser.Parser.TaggedTypeParser;
import ch.eskaton.asn4j.parser.Parser.TokenOrGroupSpecParser;
import ch.eskaton.asn4j.parser.Parser.TypeAssignmentParser;
import ch.eskaton.asn4j.parser.Parser.TypeConstraintParser;
import ch.eskaton.asn4j.parser.Parser.TypeConstraintsParser;
import ch.eskaton.asn4j.parser.Parser.TypeFieldSpecParser;
import ch.eskaton.asn4j.parser.Parser.TypeFromObjectsParser;
import ch.eskaton.asn4j.parser.Parser.TypeOptionalitySpecParser;
import ch.eskaton.asn4j.parser.Parser.TypeParser;
import ch.eskaton.asn4j.parser.Parser.TypeSettingParser;
import ch.eskaton.asn4j.parser.Parser.TypeWithConstraintParser;
import ch.eskaton.asn4j.parser.Parser.UnionsParser;
import ch.eskaton.asn4j.parser.Parser.UnrestrictedCharacterStringTypeParser;
import ch.eskaton.asn4j.parser.Parser.UpperEndValueParser;
import ch.eskaton.asn4j.parser.Parser.UpperEndpointParser;
import ch.eskaton.asn4j.parser.Parser.UsefulObjectClassReferenceParser;
import ch.eskaton.asn4j.parser.Parser.UsefulTypeParser;
import ch.eskaton.asn4j.parser.Parser.UserDefinedConstraintParameterParser;
import ch.eskaton.asn4j.parser.Parser.UserDefinedConstraintParser;
import ch.eskaton.asn4j.parser.Parser.ValueAssignmentParser;
import ch.eskaton.asn4j.parser.Parser.ValueConstraintParser;
import ch.eskaton.asn4j.parser.Parser.ValueFromObjectParser;
import ch.eskaton.asn4j.parser.Parser.ValueListParser;
import ch.eskaton.asn4j.parser.Parser.ValueOptionalitySpecParser;
import ch.eskaton.asn4j.parser.Parser.ValueParser;
import ch.eskaton.asn4j.parser.Parser.ValueRangeParser;
import ch.eskaton.asn4j.parser.Parser.ValueSetOptionalitySpecParser;
import ch.eskaton.asn4j.parser.Parser.ValueSetParser;
import ch.eskaton.asn4j.parser.Parser.ValueSetTypeAssignmentParser;
import ch.eskaton.asn4j.parser.Parser.VariableTypeValueFieldSpecParser;
import ch.eskaton.asn4j.parser.Parser.VariableTypeValueSetFieldSpecParser;
import ch.eskaton.asn4j.parser.Parser.VersionNumberParser;
import ch.eskaton.asn4j.parser.Parser.WithSyntaxSpecParser;
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
import ch.eskaton.asn4j.parser.ast.ExportsNode.Mode;
import ch.eskaton.asn4j.parser.ast.ExtensionAdditionAlternativeNode;
import ch.eskaton.asn4j.parser.ast.ExtensionAndExceptionNode;
import ch.eskaton.asn4j.parser.ast.ExternalObjectClassReference;
import ch.eskaton.asn4j.parser.ast.ExternalObjectReferenceNode;
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
import ch.eskaton.asn4j.parser.ast.ModuleNode.Encoding;
import ch.eskaton.asn4j.parser.ast.ModuleNode.TagMode;
import ch.eskaton.asn4j.parser.ast.ModuleRefNode;
import ch.eskaton.asn4j.parser.ast.NamedBitNode;
import ch.eskaton.asn4j.parser.ast.Node;
import ch.eskaton.asn4j.parser.ast.OIDComponentNode;
import ch.eskaton.asn4j.parser.ast.OIDNode;
import ch.eskaton.asn4j.parser.ast.ObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ObjectClassDefn;
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
import ch.eskaton.asn4j.parser.ast.OptionalSpecNode;
import ch.eskaton.asn4j.parser.ast.OptionalitySpecNode;
import ch.eskaton.asn4j.parser.ast.ParamGovernorNode;
import ch.eskaton.asn4j.parser.ast.ParameterNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedObjectSetAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedTypeOrObjectClassAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedValueAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedValueOrObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedValueSetTypeAssignmentNode;
import ch.eskaton.asn4j.parser.ast.PrimitiveFieldNameNode;
import ch.eskaton.asn4j.parser.ast.PropertyAndSettingNode;
import ch.eskaton.asn4j.parser.ast.Quadruple;
import ch.eskaton.asn4j.parser.ast.RangeNode;
import ch.eskaton.asn4j.parser.ast.ReferenceNode;
import ch.eskaton.asn4j.parser.ast.SetSpecsNode;
import ch.eskaton.asn4j.parser.ast.SimpleTableConstraint;
import ch.eskaton.asn4j.parser.ast.TokenOrGroup;
import ch.eskaton.asn4j.parser.ast.Tuple;
import ch.eskaton.asn4j.parser.ast.TypeFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.TypeIdentifierObjectClassReferenceNode;
import ch.eskaton.asn4j.parser.ast.TypeOrObjectClassAssignmentNode;
import ch.eskaton.asn4j.parser.ast.UpperEndpointNode;
import ch.eskaton.asn4j.parser.ast.UserDefinedConstraintNode;
import ch.eskaton.asn4j.parser.ast.UserDefinedConstraintParamNode;
import ch.eskaton.asn4j.parser.ast.ValueAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ValueOrObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ValueSetTypeAssignmentNode;
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
import ch.eskaton.asn4j.parser.ast.constraints.SingleTypeConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SizeConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.SubtypeConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.TableConstraint;
import ch.eskaton.asn4j.parser.ast.constraints.TypeConstraint;
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
import ch.eskaton.asn4j.parser.ast.types.ObjectClassFieldType;
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
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ParserTest {

    /**
     * ************************************************************************
     * X.680 13
     * ***********************************************************************
     */

    @Test
    void testModuleDefinitionParser() {
        // TODO: implement
    }

    @Test
    void testModuleIdentifierParser() throws IOException, ParserException {
        ModuleIdentifierParser parser = new Parser(new ByteArrayInputStream(
                "Module { test-a test-b (47) 21 }".getBytes())).new ModuleIdentifierParser();

        ModuleIdentifierNode result = parser.parse();

        assertNotNull(result);
        assertEquals("Module", result.getModuleName());
        assertNotNull(result.getId());
    }

    @Test
    void testDefinitiveIdentificationParser() throws IOException,
            ParserException {
        DefinitiveIdentificationParser parser = new Parser(
                new ByteArrayInputStream("{ test-a test-b (47) 21 }".getBytes())).new DefinitiveIdentificationParser();

        DefinitiveIdentificationNode result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getOid());
        assertNull(result.getIri());

        parser = new Parser(new ByteArrayInputStream(
                "{ test-a test-b (47) 21 } \"/ISO/Registration-Authority/19785.CBEFF\""
                        .getBytes())).new DefinitiveIdentificationParser();

        result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getOid());
        assertNotNull(result.getIri());
    }

    @Test
    void testDefinitiveOIDParser() throws IOException, ParserException {
        DefinitiveOIDParser parser = new Parser(new ByteArrayInputStream(
                "{ test-a test-b (47) 21 }".getBytes())).new DefinitiveOIDParser();

        OIDNode result = parser.parse();

        assertNotNull(result);
    }

    @Test
    void testDefinitiveObjIdComponentListParser() throws IOException,
            ParserException {
        DefinitiveObjIdComponentListParser parser = new Parser(
                new ByteArrayInputStream("test-a test-b (47) 21".getBytes())).new DefinitiveObjIdComponentListParser();

        OIDNode result = parser.parse();

        assertNotNull(result);

        assertEquals(3, result.getOidComponents().size());
    }

    @Test
    void testDefinitiveObjIdComponentParser() throws IOException,
            ParserException {
        DefinitiveObjIdComponentParser parser = new Parser(
                new ByteArrayInputStream("test".getBytes())).new DefinitiveObjIdComponentParser();

        OIDComponentNode result = parser.parse();

        assertNotNull(result);

        parser = new Parser(new ByteArrayInputStream("23".getBytes())).new DefinitiveObjIdComponentParser();

        result = parser.parse();

        assertNotNull(result);

        parser = new Parser(new ByteArrayInputStream("test (23)".getBytes())).new DefinitiveObjIdComponentParser();

        result = parser.parse();

        assertNotNull(result);
    }

    @Test
    void testDefinitiveNumberFormParser() throws IOException,
            ParserException {
        DefinitiveNumberFormParser parser = new Parser(
                new ByteArrayInputStream("23".getBytes())).new DefinitiveNumberFormParser();

        OIDComponentNode result = parser.parse();

        assertNotNull(result);
        assertEquals(23, (int) result.getId());
        assertNull(result.getName());
    }

    @Test
    void testDefinitiveNameAndNumberFormParser() throws IOException,
            ParserException {
        DefinitiveNameAndNumberFormParser parser = new Parser(
                new ByteArrayInputStream("test (23)".getBytes())).new DefinitiveNameAndNumberFormParser();

        OIDComponentNode result = parser.parse();

        assertNotNull(result);
        assertEquals(23, (int) result.getId());
        assertEquals("test", result.getName());
    }

    @Test
    void testEncodingReferenceDefault() throws IOException,
            ParserException {
        EncodingReferenceDefaultParser parser = new Parser(
                new ByteArrayInputStream("XER INSTRUCTIONS".getBytes())).new EncodingReferenceDefaultParser();
        assertEquals(Encoding.XER, parser.parse());

        parser = new Parser(new ByteArrayInputStream(
                "TAG INSTRUCTIONS".getBytes())).new EncodingReferenceDefaultParser();
        assertEquals(Encoding.TAG, parser.parse());

        parser = new Parser(new ByteArrayInputStream(
                "PER INSTRUCTIONS".getBytes())).new EncodingReferenceDefaultParser();
        assertEquals(Encoding.PER, parser.parse());
    }

    @Test
    void testTagDefaultParser() throws IOException, ParserException {
        TagDefaultParser parser = new Parser(new ByteArrayInputStream(
                "EXPLICIT TAGS".getBytes())).new TagDefaultParser();
        assertEquals(TagMode.EXPLICIT, parser.parse());

        parser = new Parser(
                new ByteArrayInputStream("IMPLICIT TAGS".getBytes())).new TagDefaultParser();
        assertEquals(TagMode.IMPLICIT, parser.parse());

        parser = new Parser(new ByteArrayInputStream(
                "AUTOMATIC TAGS".getBytes())).new TagDefaultParser();
        assertEquals(TagMode.AUTOMATIC, parser.parse());
    }

    @Test
    void testExtensionDefault() throws IOException, ParserException {
        ExtensionDefaultParser parser = new Parser(new ByteArrayInputStream(
                "EXTENSIBILITY IMPLIED".getBytes())).new ExtensionDefaultParser();
        assertTrue(parser.parse());

        parser = new Parser(new ByteArrayInputStream("".getBytes())).new ExtensionDefaultParser();
        assertFalse(parser.parse());
    }

    @Test
    void testModuleBodyParser() throws IOException, ParserException {
        ModuleBodyParser parser = new Parser(new ByteArrayInputStream(
                "EXPORTS ALL;".getBytes())).new ModuleBodyParser();

        ModuleBodyNode result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getExports());
        assertEquals(0, result.getImports().size());
        assertTrue(result.getAssignments().isEmpty());

        parser = new Parser(
                new ByteArrayInputStream(
                        "IMPORTS Type FROM Application-Context { iso standard 8571 application-context (1) };"
                                .getBytes())).new ModuleBodyParser();

        result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getExports());
        assertEquals(Mode.ALL, result.getExports().getMode());
        assertNotEquals(0, result.getImports().size());
        assertTrue(result.getAssignments().isEmpty());

        parser = new Parser(new ByteArrayInputStream(
                "value INTEGER ::= 23".getBytes())).new ModuleBodyParser();

        result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getExports());
        assertEquals(Mode.ALL, result.getExports().getMode());
        assertEquals(0, result.getImports().size());
        assertFalse(result.getAssignments().isEmpty());
        assertEquals(1, result.getAssignments().size());

        parser = new Parser(
                new ByteArrayInputStream(
                        "IMPORTS Type FROM Application-Context { iso standard 8571 application-context (1) }; value INTEGER ::= 23"
                                .getBytes())).new ModuleBodyParser();

        result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getExports());
        assertEquals(Mode.ALL, result.getExports().getMode());
        assertNotEquals(0, result.getImports().size());
        assertFalse(result.getAssignments().isEmpty());
        assertEquals(1, result.getAssignments().size());
    }

    @Test
    void testExportsParser() throws IOException, ParserException {
        ExportsParser parser = new Parser(new ByteArrayInputStream(
                "EXPORTS ALL;".getBytes())).new ExportsParser();

        ExportsNode result = parser.parse();

        assertNotNull(result);
        assertEquals(Mode.ALL, result.getMode());

        parser = new Parser(new ByteArrayInputStream(
                "EXPORTS value, Type{};".getBytes())).new ExportsParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(Mode.SPECIFIC, result.getMode());
        assertEquals(2, result.getSymbols().size());
    }

    @Test
    void testImportsParser() throws IOException, ParserException {
        ImportsParser parser = new Parser(
                new ByteArrayInputStream(
                        "IMPORTS OBJECT-CL FROM Module2 Type, value{} FROM Module1 { iso standard 12 module-1 (1) };"
                                .getBytes())).new ImportsParser();

        List<ImportNode> result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.size());

        parser = new Parser(new ByteArrayInputStream("IMPORTS ;".getBytes())).new ImportsParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testSymbolsFromModuleListParser() throws IOException,
            ParserException {
        SymbolsFromModuleListParser parser = new Parser(
                new ByteArrayInputStream(
                        "Type, value{} FROM Application-Context { iso standard 8571 application-context (1) } value2 FROM Module { iso standard 4711 module }"
                                .getBytes())).new SymbolsFromModuleListParser();

        List<ImportNode> result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.size());

        parser = new Parser(new ByteArrayInputStream(
                "value FROM Module1 value2 FROM Module2".getBytes())).new SymbolsFromModuleListParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testSymbolsFromModuleParser() throws IOException,
            ParserException {
        SymbolsFromModuleParser parser = new Parser(
                new ByteArrayInputStream(
                        "Type, value{} FROM Application-Context { iso standard 8571 application-context (1) }"
                                .getBytes())).new SymbolsFromModuleParser();

        ImportNode result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.getSymbols().size());
        assertEquals("Application-Context", result.getReference().getName());
    }

    @Test
    void testGlobalModuleReferenceParser() throws IOException,
            ParserException {
        GlobalModuleReferenceParser parser = new Parser(
                new ByteArrayInputStream(
                        "Application-Context { iso standard 8571 application-context (1) }"
                                .getBytes())).new GlobalModuleReferenceParser();

        ModuleRefNode result = parser.parse();

        assertNotNull(result);
        assertEquals("Application-Context", result.getName());
        assertNotNull(result.getValue().getComponents());

        parser = new Parser(new ByteArrayInputStream(
                "Application-Context oid-reference".getBytes())).new GlobalModuleReferenceParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals("Application-Context", result.getName());
        assertNotNull(result.getValue());
        assertNotNull(result.getValue().getReference());
    }

    @Test
    void testAssignedIdentifierParser() throws IOException, ParserException {
        AssignedIdentifierParser parser = new Parser(new ByteArrayInputStream(
                "value-reference".getBytes())).new AssignedIdentifierParser();

        ObjectIdentifierValue result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getReference());

        parser = new Parser(new ByteArrayInputStream(
                "{ iso standard 8571 application-context (1) }".getBytes())).new AssignedIdentifierParser();

        result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getComponents());
    }

    @Test
    void testSymbolListParser() throws IOException, ParserException {
        SymbolListParser parser = new Parser(new ByteArrayInputStream(
                "Type-Reference".getBytes())).new SymbolListParser();

        List<ReferenceNode> result = parser.parse();

        assertNotNull(result);
        assertEquals(1, result.size());

        parser = new Parser(new ByteArrayInputStream(
                "Type-Reference{}, value-reference".getBytes())).new SymbolListParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testSymbolParser() throws IOException, ParserException {
        SymbolParser parser = new Parser(new ByteArrayInputStream(
                "Type-Reference".getBytes())).new SymbolParser();

        ReferenceNode result = parser.parse();

        assertNotNull(result);
        assertFalse(result.isParameterized());

        parser = new Parser(new ByteArrayInputStream(
                "Type-Reference {}".getBytes())).new SymbolParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result.isParameterized());
    }

    @Test
    void testReferenceParser() throws IOException, ParserException {
        ReferenceParser parser = new Parser(new ByteArrayInputStream(
                "Type-Reference".getBytes())).new ReferenceParser();

        ReferenceNode result = parser.parse();

        assertNotNull(result);

        assertEquals("Type-Reference", result.getName());

        parser = new Parser(new ByteArrayInputStream(
                "value-reference".getBytes())).new ReferenceParser();

        result = parser.parse();

        assertNotNull(result);

        assertEquals("value-reference", result.getName());
    }

    @Test
    void testAssignmentListParser() throws IOException, ParserException {
        AssignmentListParser parser = new Parser(new ByteArrayInputStream(
                "Type-Reference ::= VisibleString value-reference VisibleString ::= \"string\""
                        .getBytes())).new AssignmentListParser();

        List<AssignmentNode> result = parser.parse();

        assertNotNull(result);
        assertTrue(result.get(0) instanceof TypeOrObjectClassAssignmentNode);
        assertTrue(result.get(1) instanceof ValueOrObjectAssignmentNode);
    }

    @Test
    void testAssignmentParser() throws IOException, ParserException {
        AssignmentParser parser = new Parser(new ByteArrayInputStream(
                "Type-Reference ::= VisibleString".getBytes())).new AssignmentParser();

        AssignmentNode result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof TypeOrObjectClassAssignmentNode);
        assertTrue(((TypeOrObjectClassAssignmentNode) result).getTypeAssignment().isPresent());

        parser = new Parser(new ByteArrayInputStream(
                "value-reference VisibleString ::= \"string\"".getBytes())).new AssignmentParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ValueOrObjectAssignmentNode);

        parser = new Parser(new ByteArrayInputStream(
                "Type-Reference INTEGER ::= { ALL EXCEPT (4..6) }".getBytes())).new AssignmentParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ValueSetTypeOrObjectSetAssignmentNode);

        parser = new Parser(
                new ByteArrayInputStream(
                        "OBJECT-CLASS ::= CLASS { &Type-Reference OPTIONAL } WITH SYNTAX { [ARGUMENT &ArgumentType] }"
                                .getBytes())).new AssignmentParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof TypeOrObjectClassAssignmentNode);
        assertTrue(((TypeOrObjectClassAssignmentNode) result).getObjectClassAssignment().isPresent());

        parser = new Parser(
                new ByteArrayInputStream(
                        "invertMatrix OPERATION ::= { &ArgumentType Matrix, &ResultType Matrix, &Errors {determinantIsZero}, &operationCode 7 }"
                                .getBytes())).new AssignmentParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ValueOrObjectAssignmentNode);

        parser = new Parser(new ByteArrayInputStream(
                "ObjectSet-Ref OBJ-CLASS ::= { (Object1 | Object2) }"
                        .getBytes())).new AssignmentParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ValueSetTypeOrObjectSetAssignmentNode);
    }

    /**
     * ************************************************************************
     * X.680 14
     * ***********************************************************************
     */

    @Test
    void testDefinedValueParser() throws IOException, ParserException {
        DefinedValueParser parser = new Parser(new ByteArrayInputStream(
                "Module.value".getBytes())).new DefinedValueParser();

        DefinedValue result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ExternalValueReference);

        parser = new Parser(new ByteArrayInputStream(
                "value-reference".getBytes())).new DefinedValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SimpleDefinedValue);

        parser = new Parser(new ByteArrayInputStream(
                "Module.value {4711}".getBytes())).new DefinedValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ExternalValueReference);
        assertTrue(((ExternalValueReference) result).getParameters().isPresent());
        assertEquals(1, ((ExternalValueReference) result).getParameters().get().size());
    }

    @Test
    void testExternalTypeReferenceParser() throws IOException,
            ParserException {
        ExternalTypeReferenceParser parser = new Parser(
                new ByteArrayInputStream("Module.Type".getBytes())).new ExternalTypeReferenceParser();

        ExternalTypeReference result = parser.parse();

        assertNotNull(result);

        assertEquals("Module", result.getModule());
        assertEquals("Type", result.getType());
    }

    @Test
    void testExternalValueReferenceParser() throws IOException,
            ParserException {
        ExternalValueReferenceParser parser = new Parser(
                new ByteArrayInputStream("Module.value".getBytes())).new ExternalValueReferenceParser();

        SimpleDefinedValue result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ExternalValueReference);

        assertEquals("Module", ((ExternalValueReference) result).getModule());
        assertEquals("value", result.getReference());
    }

    /**
     * ************************************************************************
     * X.680 16
     * ***********************************************************************
     */

    @Test
    void testTypeAssignmentParser() throws IOException, ParserException {
        TypeAssignmentParser parser = new Parser(new ByteArrayInputStream(
                "Type-Reference ::= [TAG: APPLICATION 23] INTEGER ((0..MAX))"
                        .getBytes())).new TypeAssignmentParser();

        TypeOrObjectClassAssignmentNode result = parser.parse();

        assertNotNull(result);
        assertEquals("Type-Reference", result.getReference());
        assertTrue(result.getTypeAssignment().isPresent());
        assertTrue(result.getTypeAssignment().get().getType() instanceof IntegerType);
    }

    @Test
    void testValueAssignmentParser() throws IOException, ParserException {
        ValueAssignmentParser parser = new Parser(new ByteArrayInputStream(
                "value-reference INTEGER ::= 4711".getBytes())).new ValueAssignmentParser();

        ValueOrObjectAssignmentNode result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getValueAssignment().isPresent());

        ValueAssignmentNode valueAssignment = result.getValueAssignment().get();

        assertEquals("value-reference", result.getReference());
        assertTrue(valueAssignment.getType() instanceof IntegerType);
        assertTrue(valueAssignment.getValue() instanceof IntegerValue);

        parser = new Parser(new ByteArrayInputStream(
                "oid OBJECT IDENTIFIER ::= { oid-reference 23 }".getBytes())).new ValueAssignmentParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getValueAssignment().isPresent());

        valueAssignment = result.getValueAssignment().get();

        assertEquals("oid", result.getReference());
        assertTrue(valueAssignment.getType() instanceof ObjectIdentifier);
        testAmbiguousValue(valueAssignment.getValue(), ObjectIdentifierValue.class);
    }

    @Test
    void testValueSetAssignmentParser() throws IOException, ParserException {
        ValueSetTypeAssignmentParser parser = new Parser(
                new ByteArrayInputStream(
                        "Type-Reference INTEGER ::= { ALL EXCEPT (4..6) }"
                                .getBytes())).new ValueSetTypeAssignmentParser();

        ValueSetTypeOrObjectSetAssignmentNode result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getValueSetTypeAssignment().isPresent());

        ValueSetTypeAssignmentNode valueSetTypeAssignment = result.getValueSetTypeAssignment().get();

        assertEquals("Type-Reference", valueSetTypeAssignment.getReference());
        assertTrue(valueSetTypeAssignment.getType() instanceof IntegerType);
        assertNotNull(valueSetTypeAssignment.getValueSet());
    }

    @Test
    void testValueSetParser() throws IOException, ParserException {
        ValueSetParser parser = new Parser(new ByteArrayInputStream(
                "{ ALL EXCEPT (4..6) }".getBytes())).new ValueSetParser();

        SetSpecsNode result = parser.parse();

        assertNotNull(result);
    }

    /**
     * ************************************************************************
     * X.680 17
     * ***********************************************************************
     */

    @Test
    void testBuiltinTypeParser() throws IOException, ParserException {
        BuiltinTypeParser parser = new Parser(new ByteArrayInputStream(
                "BIT STRING { fst-bit (1), snd-bit (2), trd-bit (val-ref) }"
                        .getBytes())).new BuiltinTypeParser();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof BitString);

        parser = new Parser(new ByteArrayInputStream("BOOLEAN".getBytes())).new BuiltinTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof BooleanType);

        parser = new Parser(new ByteArrayInputStream("BMPString".getBytes())).new BuiltinTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof BMPString);

        parser = new Parser(new ByteArrayInputStream(
                "CHOICE {aNumber INTEGER, aString OCTET STRING }".getBytes())).new BuiltinTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof Choice);

        parser = new Parser(new ByteArrayInputStream("DATE".getBytes())).new BuiltinTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof DateType);

        parser = new Parser(new ByteArrayInputStream("DATE-TIME".getBytes())).new BuiltinTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof DateTime);

        parser = new Parser(new ByteArrayInputStream("DURATION".getBytes())).new BuiltinTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof Duration);

        parser = new Parser(new ByteArrayInputStream("EMBEDDED PDV".getBytes())).new BuiltinTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof EmbeddedPDV);

        parser = new Parser(new ByteArrayInputStream(
                "ENUMERATED {root-enum, ..., additional-enum}".getBytes())).new BuiltinTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof EnumeratedType);

        parser = new Parser(new ByteArrayInputStream("EXTERNAL".getBytes())).new BuiltinTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof External);

        parser = new Parser(new ByteArrayInputStream(
                "INSTANCE OF OBJECT-CLASS".getBytes())).new BuiltinTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof InstanceOfType);

        parser = new Parser(new ByteArrayInputStream("INTEGER".getBytes())).new BuiltinTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof IntegerType);

        parser = new Parser(new ByteArrayInputStream("OID-IRI".getBytes())).new BuiltinTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof IRI);

        parser = new Parser(new ByteArrayInputStream("NULL".getBytes())).new BuiltinTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof Null);

        parser = new Parser(new ByteArrayInputStream(
                "OBJECT-CLASS.&ObjectSet-Reference".getBytes())).new BuiltinTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ObjectClassFieldType);

        parser = new Parser(new ByteArrayInputStream("OBJECT IDENTIFIER".getBytes())).new BuiltinTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ObjectIdentifier);

        parser = new Parser(new ByteArrayInputStream("OCTET STRING".getBytes())).new BuiltinTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof OctetString);

        parser = new Parser(new ByteArrayInputStream("REAL".getBytes())).new BuiltinTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof Real);

        parser = new Parser(new ByteArrayInputStream("RELATIVE-OID-IRI".getBytes())).new BuiltinTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof RelativeIRI);

        parser = new Parser(new ByteArrayInputStream("RELATIVE-OID".getBytes())).new BuiltinTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof RelativeOID);

        parser = new Parser(new ByteArrayInputStream("SEQUENCE {}".getBytes())).new BuiltinTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SequenceType);

        parser = new Parser(new ByteArrayInputStream(
                "SEQUENCE OF INTEGER".getBytes())).new BuiltinTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SequenceOfType);

        parser = new Parser(new ByteArrayInputStream("SET {}".getBytes())).new BuiltinTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SetType);

        parser = new Parser(new ByteArrayInputStream(
                "SET OF INTEGER".getBytes())).new BuiltinTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SetOfType);

        parser = new Parser(new ByteArrayInputStream(
                "[TAG: APPLICATION 23] INTEGER".getBytes())).new BuiltinTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof IntegerType);
        assertEquals(23, (int) result.getTags().getFirst().getClassNumber().getClazz());

        parser = new Parser(new ByteArrayInputStream("TIME".getBytes())).new BuiltinTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof Time);

        parser = new Parser(new ByteArrayInputStream("TIME-OF-DAY".getBytes())).new BuiltinTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof TimeOfDay);
    }

    @Test
    void testReferencedTypeParser() throws IOException, ParserException {
        ReferencedTypeParser parser = new Parser(new ByteArrayInputStream(
                "Type-Reference".getBytes())).new ReferencedTypeParser();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof TypeReference);

        parser = new Parser(new ByteArrayInputStream(
                "GeneralizedTime".getBytes())).new ReferencedTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof GeneralizedTime);

        parser = new Parser(new ByteArrayInputStream(
                "identifier < Type".getBytes())).new ReferencedTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SelectionType);

        parser = new Parser(new ByteArrayInputStream(
                "object-reference {Object}.&Type-Reference1.&Type-Reference2"
                        .getBytes())).new ReferencedTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof TypeFromObjects);
    }

    @Test
    void testNamedTypeParser() throws IOException, ParserException {
        NamedTypeParser parser = new Parser(new ByteArrayInputStream(
                "ident INTEGER".getBytes())).new NamedTypeParser();

        NamedType result = parser.parse();

        assertNotNull(result);
        assertEquals("ident", result.getName());
        assertTrue(result.getType() instanceof IntegerType);
    }

    @Test
    void testValueParser() throws IOException, ParserException {
        ValueParser parser = new Parser(new ByteArrayInputStream(
                "'1101'B".getBytes())).new ValueParser();

        Value result = parser.parse();
        assertTrue(result instanceof BinaryStringValue);

        parser = new Parser(new ByteArrayInputStream(
                "value-reference".getBytes())).new ValueParser();

        result = parser.parse();
        assertTrue(result instanceof SimpleDefinedValue);

        parser = new Parser(
                new ByteArrayInputStream("INTEGER: 4711".getBytes())).new ValueParser();

        result = parser.parse();
        assertTrue(result instanceof OpenTypeFieldValue);
    }

    @Test
    void testBuiltinOrReferencedValueParser() throws IOException, ParserException {
        BuiltinOrReferencedValueParser parser = new Parser(
                new ByteArrayInputStream("'1101'B".getBytes())).new BuiltinOrReferencedValueParser();

        Value result = parser.parse();

        assertTrue(result instanceof BinaryStringValue);

        parser = new Parser(new ByteArrayInputStream("'5AF'H".getBytes())).new BuiltinOrReferencedValueParser();

        result = parser.parse();

        assertTrue(result instanceof HexStringValue);

        parser = new Parser(new ByteArrayInputStream("{}".getBytes())).new BuiltinOrReferencedValueParser();

        result = parser.parse();

        assertTrue(result instanceof EmptyValue);

        parser = new Parser(
                new ByteArrayInputStream("CONTAINING 23".getBytes())).new BuiltinOrReferencedValueParser();

        result = parser.parse();

        assertTrue(result instanceof ContainingStringValue);

        parser = new Parser(new ByteArrayInputStream(
                "{a-value, b-value }".getBytes())).new BuiltinOrReferencedValueParser();

        result = parser.parse();

        testAmbiguousValue(result, BitStringValue.class);

        parser = new Parser(new ByteArrayInputStream("TRUE".getBytes())).new BuiltinOrReferencedValueParser();

        result = parser.parse();

        assertTrue(result instanceof BooleanValue);

        parser = new Parser(new ByteArrayInputStream("\"string\"".getBytes())).new BuiltinOrReferencedValueParser();

        result = parser.parse();

        testAmbiguousValue(result, StringValue.class);

        parser = new Parser(new ByteArrayInputStream(
                "aString: \"string\"".getBytes())).new BuiltinOrReferencedValueParser();

        result = parser.parse();

        assertTrue(result instanceof ChoiceValue);

        // TODO: EmbeddedPDVValue

        parser = new Parser(new ByteArrayInputStream("Module.enum-value".getBytes())).new BuiltinOrReferencedValueParser();

        result = parser.parse();

        assertTrue(result instanceof ExternalValueReference);
        assertEquals("Module", ((ExternalValueReference) result).getModule());
        assertEquals("enum-value", ((ExternalValueReference) result).getReference());

        parser = new Parser(new ByteArrayInputStream("4711".getBytes())).new BuiltinOrReferencedValueParser();

        result = parser.parse();

        assertTrue(result instanceof IntegerValue);

        parser = new Parser(new ByteArrayInputStream("NULL".getBytes())).new BuiltinOrReferencedValueParser();

        result = parser.parse();

        assertTrue(result instanceof NullValue);

        parser = new Parser(new ByteArrayInputStream(
                "{ oid-component1 Module.oid-component 4711 oid-comp (42)}"
                        .getBytes())).new BuiltinOrReferencedValueParser();

        result = parser.parse();

        testAmbiguousValue(result, ObjectIdentifierValue.class);

        parser = new Parser(new ByteArrayInputStream("3.14e2".getBytes())).new BuiltinOrReferencedValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof RealValue);

        parser = new Parser(new ByteArrayInputStream(
                "MINUS-INFINITY".getBytes())).new BuiltinOrReferencedValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof RealValue);

        parser = new Parser(new ByteArrayInputStream(
                "{ aString \"string\", anInteger 23 }".getBytes())).new BuiltinOrReferencedValueParser();

        result = parser.parse();

        testAmbiguousValue(result, CollectionValue.class);

        parser = new Parser(new ByteArrayInputStream("{}".getBytes())).new BuiltinOrReferencedValueParser();

        result = parser.parse();

        assertTrue(result instanceof EmptyValue);

        parser = new Parser(new ByteArrayInputStream("{ 1 }".getBytes())).new BuiltinOrReferencedValueParser();

        result = parser.parse();

        testAmbiguousValue(result, CollectionOfValue.class);

        parser = new Parser(new ByteArrayInputStream("{ 1, 2 }".getBytes())).new BuiltinOrReferencedValueParser();

        result = parser.parse();

        assertTrue(result instanceof CollectionOfValue);

        parser = new Parser(new ByteArrayInputStream(
                "\"P0Y29M0DT0H0.00M\"".getBytes())).new BuiltinOrReferencedValueParser();

        result = parser.parse();

        testAmbiguousValue(result, StringValue.class, value -> assertTrue(value.isTString()));

        parser = new Parser(new ByteArrayInputStream(
                "value-reference".getBytes())).new BuiltinOrReferencedValueParser();

        result = parser.parse();

        assertTrue(result instanceof SimpleDefinedValue);
        assertEquals("value-reference", ((SimpleDefinedValue) result).getReference());

        parser = new Parser(new ByteArrayInputStream(
                "object-reference {Object}.&value-reference1".getBytes())).new BuiltinOrReferencedValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ValueFromObject);
    }

    @Test
    void testNamedValueParser() throws IOException, ParserException {
        NamedValueParser parser = new Parser(new ByteArrayInputStream(
                "name 12".getBytes())).new NamedValueParser();

        Value result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof NamedValue);
        assertEquals("name", ((NamedValue) result).getName());
        assertTrue(((NamedValue) result).getValue() instanceof IntegerValue);
    }

    /**
     * ************************************************************************
     * X.680 18
     * ***********************************************************************
     */

    @Test
    void testBooleanTypeParser() throws IOException, ParserException {
        BuiltinTypeParserAux parser = new Parser(new ByteArrayInputStream(
                "BOOLEAN".getBytes())).new BuiltinTypeParserAux();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof BooleanType);
    }

    @Test
    void testBooleanValueParser() throws IOException, ParserException {
        BooleanValueParser parser = new Parser(new ByteArrayInputStream(
                "FALSE".getBytes())).new BooleanValueParser();

        Value result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof BooleanValue);
        assertFalse(((BooleanValue) result).getValue());

        parser = new Parser(new ByteArrayInputStream("TRUE".getBytes())).new BooleanValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof BooleanValue);
        assertTrue(((BooleanValue) result).getValue());
    }

    /**
     * ************************************************************************
     * X.680 19
     * ***********************************************************************
     */

    @Test
    void testIntegerTypeParser() throws IOException, ParserException {
        BuiltinTypeParserAux parser = new Parser(new ByteArrayInputStream(
                "INTEGER".getBytes())).new BuiltinTypeParserAux();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof IntegerType);
    }

    @Test
    void testNamedNumberListParser() throws IOException, ParserException {
        NamedNumberListParser parser = new Parser(new ByteArrayInputStream(
                "a-number (-12), another-number (23)".getBytes())).new NamedNumberListParser();

        List<NamedNumber> result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testNamedNumberParser() throws IOException, ParserException {
        NamedNumberParser parser = new Parser(new ByteArrayInputStream(
                "a-number (-12)".getBytes())).new NamedNumberParser();

        NamedNumber result = parser.parse();

        assertNotNull(result);
        assertEquals("a-number", result.getId());
        assertEquals(BigInteger.valueOf(-12), result.getValue().getNumber());

        parser = new Parser(new ByteArrayInputStream(
                "a-number (Module.ref-number)".getBytes())).new NamedNumberParser();

        result = parser.parse();

        assertEquals("a-number", result.getId());
        assertEquals("Module",
                ((ExternalValueReference) result.getRef()).getModule());
        assertEquals("ref-number",
                ((ExternalValueReference) result.getRef()).getReference());
    }

    @Test
    void testSignedNumberParser() throws IOException, ParserException {
        SignedNumberParser parser = new Parser(new ByteArrayInputStream(
                "18446744073709551615".getBytes())).new SignedNumberParser();

        SignedNumber result = parser.parse();

        assertNotNull(result);
        assertEquals(new BigInteger("18446744073709551615"), result.getNumber());

        parser = new Parser(new ByteArrayInputStream("-23".getBytes())).new SignedNumberParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(BigInteger.valueOf(-23), result.getNumber());
    }

    @Test
    void testIntegerValueParser() throws IOException, ParserException {
        IntegerValueParser parser = new Parser(new ByteArrayInputStream(
                "-12".getBytes())).new IntegerValueParser();

        IntegerValue result = parser.parse();

        assertNotNull(result);
        assertEquals(new BigInteger("-12"), result.getValue());
    }

    /**
     * ************************************************************************
     * X.680 20
     * ***********************************************************************
     */

    @Test
    void testEnumeratedTypeParser() throws IOException, ParserException {
        BuiltinTypeParserAux parser = new Parser(new ByteArrayInputStream(
                "ENUMERATED {root-enum, ..., additional-enum}".getBytes())).new BuiltinTypeParserAux();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof EnumeratedType);
    }

    @Test
    void testEnumerationsParser() throws IOException, ParserException {
        EnumerationsParser parser = new Parser(new ByteArrayInputStream(
                "root-enum, root-enum-b".getBytes())).new EnumerationsParser();

        EnumeratedType result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.getRootEnum().size());
        assertFalse(result.hasExceptionSpec());
        assertNull(result.getAdditionalEnum());

        parser = new Parser(new ByteArrayInputStream(
                "root-enum, root-enum-b, ...".getBytes())).new EnumerationsParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.getRootEnum().size());
        assertFalse(result.hasExceptionSpec());
        assertNull(result.getAdditionalEnum());

        parser = new Parser(new ByteArrayInputStream(
                "root-enum, root-enum-b, ... ! 23".getBytes())).new EnumerationsParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.getRootEnum().size());
        assertTrue(result.hasExceptionSpec());
        assertNull(result.getAdditionalEnum());

        parser = new Parser(new ByteArrayInputStream(
                "root-enum, root-enum-b, ..., add-enum-a".getBytes())).new EnumerationsParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.getRootEnum().size());
        assertFalse(result.hasExceptionSpec());
        assertEquals(1, result.getAdditionalEnum().size());

        parser = new Parser(new ByteArrayInputStream(
                "root-enum, root-enum-b, ... ! INTEGER: 12, add-enum-a"
                        .getBytes())).new EnumerationsParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.getRootEnum().size());
        assertTrue(result.hasExceptionSpec());
        assertEquals(1, result.getAdditionalEnum().size());
    }

    @Test
    void testEnumerationParser() throws IOException, ParserException {
        EnumerationParser parser = new Parser(new ByteArrayInputStream(
                "enum-ident".getBytes())).new EnumerationParser();

        List<EnumerationItemNode> result = parser.parse();

        assertNotNull(result);
        assertEquals(1, result.size());

        parser = new Parser(new ByteArrayInputStream(
                "enum-ident, a-name (ref), b-name (4)".getBytes())).new EnumerationParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testEnumerationItemParser() throws IOException, ParserException {
        EnumerationItemParser parser = new Parser(new ByteArrayInputStream(
                "enum-ident".getBytes())).new EnumerationItemParser();

        EnumerationItemNode result = parser.parse();

        assertNotNull(result);
        assertEquals("enum-ident", result.getName());

        parser = new Parser(new ByteArrayInputStream("name (5)".getBytes())).new EnumerationItemParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals("name", result.getName());
        assertEquals(Integer.valueOf(5), result.getNumber());

        parser = new Parser(new ByteArrayInputStream("name (ref)".getBytes())).new EnumerationItemParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals("name", result.getName());
        assertEquals("ref", ((SimpleDefinedValue) result.getRef()).getReference());
    }

    /**
     * ************************************************************************
     * X.680 21
     * ***********************************************************************
     */

    @Test
    void testRealTypeParser() throws IOException, ParserException {
        BuiltinTypeParserAux parser = new Parser(new ByteArrayInputStream(
                "REAL".getBytes())).new BuiltinTypeParserAux();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof Real);
    }

    @Test
    void testRealValueParser() throws IOException, ParserException {
        RealValueParser parser = new Parser(new ByteArrayInputStream(
                "3.14e2".getBytes())).new RealValueParser();

        RealValue result = parser.parse();

        assertNotNull(result);
        assertEquals(RealValue.RealType.NORMAL, result.getRealType());
        assertEquals(BigDecimal.valueOf(314), result.getValue());

        parser = new Parser(new ByteArrayInputStream(
                "MINUS-INFINITY".getBytes())).new RealValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(RealValue.RealType.NEGATIVE_INF, result.getRealType());
    }

    @Test
    void testNumericRealValueParser() throws IOException,
            ParserException {
        NumericRealValueParser parser = new Parser(new ByteArrayInputStream(
                "-1.5".getBytes())).new NumericRealValueParser();

        RealValue result = parser.parse();

        assertNotNull(result);
        assertEquals(RealValue.RealType.NORMAL, result.getRealType());
        assertEquals(new BigDecimal("-1.5"), result.getValue());
    }

    @Test
    void testSpecialRealValueParser() throws IOException,
            ParserException {
        SpecialRealValueParser parser = new Parser(new ByteArrayInputStream(
                "PLUS-INFINITY".getBytes())).new SpecialRealValueParser();

        RealValue result = parser.parse();

        assertNotNull(result);
        assertEquals(RealValue.RealType.POSITIVE_INF, result.getRealType());

        parser = new Parser(new ByteArrayInputStream(
                "MINUS-INFINITY".getBytes())).new SpecialRealValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(RealValue.RealType.NEGATIVE_INF, result.getRealType());

        parser = new Parser(new ByteArrayInputStream("NOT-A-NUMBER".getBytes())).new SpecialRealValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(RealValue.RealType.NAN, result.getRealType());
    }

    /**
     * ************************************************************************
     * X.680 22
     * ***********************************************************************
     */

    @Test
    void testBitStringTypeParser() throws IOException, ParserException {
        BuiltinTypeParserAux parser = new Parser(new ByteArrayInputStream(
                "BIT STRING".getBytes())).new BuiltinTypeParserAux();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof BitString);

        parser = new Parser(new ByteArrayInputStream(
                "BIT STRING { fst-bit (1), snd-bit (2), trd-bit (val-ref) }"
                        .getBytes())).new BuiltinTypeParserAux();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof BitString);
    }

    @Test
    void testNamedBitListParser() throws IOException, ParserException {
        NamedBitListParser parser = new Parser(new ByteArrayInputStream(
                "a-bit (3), another-bit (5)".getBytes())).new NamedBitListParser();

        List<NamedBitNode> result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testNamedBitParser() throws IOException, ParserException {
        NamedBitParser parser = new Parser(new ByteArrayInputStream(
                "a-bit (3)".getBytes())).new NamedBitParser();

        NamedBitNode result = parser.parse();

        assertNotNull(result);
        assertEquals("a-bit", result.getId());
        assertEquals(3, result.getNum());
        assertNull(result.getRef());

        parser = new Parser(new ByteArrayInputStream(
                "a-bit (value-ref)".getBytes())).new NamedBitParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals("a-bit", result.getId());
        assertEquals("value-ref",
                ((SimpleDefinedValue) result.getRef()).getReference());
    }

    @Test
    void testBitStringValueParser() throws IOException, ParserException {
        BitOrOctetStringValueParser parser = new Parser(
                new ByteArrayInputStream("'1101'B".getBytes())).new BitOrOctetStringValueParser();

        Value result = parser.parse();

        assertTrue(result instanceof BinaryStringValue);
        assertArrayEquals(new byte[] { 0x0d }, ((BinaryStringValue) result).toBitString().getByteValue());

        parser = new Parser(new ByteArrayInputStream("'AF'H".getBytes())).new BitOrOctetStringValueParser();

        result = parser.parse();

        assertTrue(result instanceof HexStringValue);
        assertArrayEquals(new byte[] { (byte) 0xaf }, ((HexStringValue) result).toBitString().getByteValue());

        parser = new Parser(new ByteArrayInputStream(
                "{a-value, b-value }".getBytes())).new BitOrOctetStringValueParser();

        result = parser.parse();

        assertTrue(result instanceof BitStringValue);
        assertNull(((BitStringValue) result).getByteValue());
        assertEquals(Arrays.asList("a-value", "b-value"), ((BitStringValue) result).getNamedValues());

        parser = new Parser(
                new ByteArrayInputStream("CONTAINING 23".getBytes())).new BitOrOctetStringValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ContainingStringValue);
        assertTrue(((ContainingStringValue) result).getValue() instanceof IntegerValue);
    }

    @Test
    void testIdentifierListParser() throws IOException, ParserException {
        IdentifierListParser parser = new Parser(new ByteArrayInputStream(
                "abc, def".getBytes())).new IdentifierListParser();

        List<String> result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("abc", result.get(0));
        assertEquals("def", result.get(1));
    }

    /**
     * ************************************************************************
     * X.680 23
     * ***********************************************************************
     */

    @Test
    void testOctetStringTypeParser() throws IOException, ParserException {
        BuiltinTypeParserAux parser = new Parser(new ByteArrayInputStream(
                "OCTET STRING".getBytes())).new BuiltinTypeParserAux();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof OctetString);
    }

    @Test
    void testOctetStringValueParser() throws IOException, ParserException {
        BitOrOctetStringValueParser parser = new Parser(
                new ByteArrayInputStream("'11010000111'B".getBytes())).new BitOrOctetStringValueParser();

        Value result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof BinaryStringValue);
        assertEquals((byte) 0xD0, ((BinaryStringValue) result).toOctetString().getByteValue()[0]);
        assertEquals((byte) 0xE0, ((BinaryStringValue) result).toOctetString().getByteValue()[1]);

        parser = new Parser(new ByteArrayInputStream("'1CF'H".getBytes())).new BitOrOctetStringValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof HexStringValue);
        assertEquals((byte) 0x1C, ((HexStringValue) result).toOctetString().getByteValue()[0]);
        assertEquals((byte) 0xF0, ((HexStringValue) result).toOctetString().getByteValue()[1]);

        parser = new Parser(new ByteArrayInputStream("CONTAINING 23".getBytes())).new BitOrOctetStringValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(((ContainingStringValue) result).getValue() instanceof IntegerValue);
    }

    /**
     * ************************************************************************
     * X.680 24
     * ***********************************************************************
     */
    @Test
    void testNullTypeParser() throws IOException, ParserException {
        BuiltinTypeParserAux parser = new Parser(new ByteArrayInputStream(
                "NULL".getBytes())).new BuiltinTypeParserAux();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof Null);
    }

    @Test
    void testNullValueParser() throws IOException, ParserException {
        NullValueParser parser = new Parser(new ByteArrayInputStream(
                "NULL".getBytes())).new NullValueParser();

        Value result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof NullValue);
    }

    /**
     * ************************************************************************
     * X.680 25
     * ***********************************************************************
     */

    @Test
    void testSequenceTypeParser() throws IOException, ParserException {
        // SEQUENCE "{" "}"
        BuiltinTypeParserAux parser = new Parser(new ByteArrayInputStream(
                "SEQUENCE {}".getBytes())).new BuiltinTypeParserAux();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SequenceType);

        // | SEQUENCE "{" ExtensionAndException OptionalExtensionMarker "}"
        parser = new Parser(new ByteArrayInputStream(
                "SEQUENCE { ... ! 34, ...}".getBytes())).new BuiltinTypeParserAux();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SequenceType);

        SequenceType seq = (SequenceType) result;

        assertNotNull(seq.getExtensionAndException());
        assertNotNull(seq.getOptionalExtensionMarker());

        // | SEQUENCE "{" ComponentTypeLists "}"
        parser = new Parser(new ByteArrayInputStream(
                "SEQUENCE {string VisibleString, ... ! 12, anInt INTEGER DEFAULT 47, ...}"
                        .getBytes())).new BuiltinTypeParserAux();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SequenceType);
    }

    @Test
    void testExtensionAndExceptionParser() throws IOException,
            ParserException {
        ExtensionAndExceptionParser parser = new Parser(
                new ByteArrayInputStream("...".getBytes())).new ExtensionAndExceptionParser();

        assertNotNull(parser.parse());

        parser = new Parser(new ByteArrayInputStream("... ! 23".getBytes())).new ExtensionAndExceptionParser();

        ExtensionAndExceptionNode result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getExceptionId());
    }

    @Test
    void testOptionalExtensionMarkerParser() throws IOException,
            ParserException {
        OptionalExtensionMarkerParser parser = new Parser(
                new ByteArrayInputStream(", ...".getBytes())).new OptionalExtensionMarkerParser();

        assertNotNull(parser.parse());
    }

    @Test
    void testComponentTypeListsParser() throws IOException,
            ParserException {
        // RootComponentTypeList
        ComponentTypeListsParser parser = new Parser(new ByteArrayInputStream(
                "string VisibleString".getBytes())).new ComponentTypeListsParser();

        ComponentTypeListsNode result = parser.parse();

        assertNotNull(result);

        assertNotNull(result.getRootComponents());
        assertEquals(1, result.getRootComponents().size());

        assertNull(result.getExtensionAndException());
        assertNull(result.getExtensionAdditions());
        assertFalse(result.getOptionalExtensionMarker());
        assertNull(result.getExtensionRootComponents());

        // RootComponentTypeList "," ExtensionAndException ExtensionAdditions
        // OptionalExtensionMarker
        parser = new Parser(new ByteArrayInputStream(
                "string VisibleString, ... ! 12, anInt INTEGER DEFAULT 47, ..."
                        .getBytes())).new ComponentTypeListsParser();

        result = parser.parse();

        assertNotNull(result);

        assertNotNull(result.getRootComponents());
        assertEquals(1, result.getRootComponents().size());

        assertNotNull(result.getExtensionAndException());
        assertNotNull(result.getExtensionAdditions());
        assertTrue(result.getOptionalExtensionMarker());
        assertNull(result.getExtensionRootComponents());

        // RootComponentTypeList "," ExtensionAndException ExtensionAdditions
        // OptionalExtensionMarker
        parser = new Parser(new ByteArrayInputStream(
                "string VisibleString, ... ! 12, anInt INTEGER DEFAULT 47"
                        .getBytes())).new ComponentTypeListsParser();

        result = parser.parse();

        assertNotNull(result);

        assertNotNull(result.getRootComponents());
        assertEquals(1, result.getRootComponents().size());

        assertNotNull(result.getExtensionAndException());
        assertNotNull(result.getExtensionAdditions());
        assertFalse(result.getOptionalExtensionMarker());
        assertNull(result.getExtensionRootComponents());

        // RootComponentTypeList "," ExtensionAndException ExtensionAdditions
        // ExtensionEndMarker "," RootComponentTypeList
        parser = new Parser(

                new ByteArrayInputStream(
                        "string VisibleString, ... ! 12, anInt INTEGER OPTIONAL, ..., anotherString OCTET STRING"
                                .getBytes())).new ComponentTypeListsParser();

        result = parser.parse();

        assertNotNull(result);

        assertNotNull(result.getRootComponents());
        assertEquals(1, result.getRootComponents().size());

        assertNotNull(result.getExtensionAndException());
        assertNotNull(result.getExtensionAdditions());
        assertFalse(result.getOptionalExtensionMarker());
        assertNotNull(result.getExtensionRootComponents());

        // ExtensionAndException ExtensionAdditions OptionalExtensionMarker
        parser = new Parser(new ByteArrayInputStream(
                "... ! 12, anInt INTEGER OPTIONAL, ...".getBytes())).new ComponentTypeListsParser();

        result = parser.parse();

        assertNotNull(result);

        assertNull(result.getRootComponents());
        assertNotNull(result.getExtensionAndException());
        assertNotNull(result.getExtensionAdditions());
        assertTrue(result.getOptionalExtensionMarker());
        assertNull(result.getExtensionRootComponents());

        // ExtensionAndException ExtensionAdditions ExtensionEndMarker ","
        // RootComponentTypeList
        parser = new Parser(new ByteArrayInputStream(
                "... ! 12, anInt INTEGER OPTIONAL, ..., anotherString OCTET STRING"
                        .getBytes())).new ComponentTypeListsParser();

        result = parser.parse();

        assertNotNull(result);

        assertNull(result.getRootComponents());
        assertNotNull(result.getExtensionAndException());
        assertNotNull(result.getExtensionAdditions());
        assertTrue(result.getOptionalExtensionMarker());
        assertNotNull(result.getExtensionRootComponents());
    }

    @Test
    void testExtensionEndMarkerParser() throws IOException,
            ParserException {
        ExtensionEndMarkerParser parser = new Parser(new ByteArrayInputStream(
                ", ...".getBytes())).new ExtensionEndMarkerParser();

        Object result = parser.parse();

        assertNotNull(result);
    }

    @Test
    void testExtensionAdditionsParser() throws IOException,
            ParserException {
        ExtensionAdditionsParser parser = new Parser(new ByteArrayInputStream(
                ", [[1: string VisibleString]]".getBytes())).new ExtensionAdditionsParser();

        List<Object> result = parser.parse();

        assertNotNull(result);
        assertEquals(1, result.size());

        assertTrue(result.get(0) instanceof ExtensionAdditionGroup);
    }

    @Test
    void testExtensionAdditionListParser() throws IOException,
            ParserException {
        ExtensionAdditionListParser parser = new Parser(
                new ByteArrayInputStream(
                        "[[1: string VisibleString]]".getBytes())).new ExtensionAdditionListParser();

        List<Object> result = parser.parse();

        assertNotNull(result);
        assertEquals(1, result.size());

        parser = new Parser(new ByteArrayInputStream(
                "int INTEGER, [[1: string VisibleString]]".getBytes())).new ExtensionAdditionListParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testExtensionAdditionParser() throws IOException,
            ParserException {
        ExtensionAdditionParser parser = new Parser(new ByteArrayInputStream(
                "[[1: string VisibleString]]".getBytes())).new ExtensionAdditionParser();

        Object result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ExtensionAdditionGroup);

        parser = new Parser(new ByteArrayInputStream("int INTEGER".getBytes())).new ExtensionAdditionParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ComponentType);
    }

    @Test
    void testExtensionAdditionGroupParser() throws IOException,
            ParserException {
        ExtensionAdditionGroupParser parser = new Parser(
                new ByteArrayInputStream(
                        "[[1: string VisibleString, int INTEGER]]".getBytes())).new ExtensionAdditionGroupParser();

        ExtensionAdditionGroup result = parser.parse();

        assertNotNull(result);
        assertEquals(1, result.getVersion().get());
        assertEquals(2, result.getComponents().size());

        parser = new Parser(new ByteArrayInputStream(
                "[[int INTEGER]]".getBytes())).new ExtensionAdditionGroupParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(Optional.empty(), result.getVersion());
        assertEquals(1, result.getComponents().size());
    }

    @Test
    void testVersionNumberParser() throws IOException, ParserException {
        VersionNumberParser parser = new Parser(new ByteArrayInputStream(
                "123:".getBytes())).new VersionNumberParser();

        Integer result = parser.parse();

        assertNotNull(result);
        assertEquals(123, (int) result);
    }

    @Test
    void testComponentTypeListParser() throws IOException,
            ParserException {
        ComponentTypeListParser parser = new Parser(new ByteArrayInputStream(
                "string VisibleString".getBytes())).new ComponentTypeListParser();

        List<ComponentType> result = parser.parse();

        assertNotNull(result);
        assertEquals(1, result.size());

        NamedType namedType = result.get(0).getNamedType();

        assertEquals("string", namedType.getName());
        assertTrue(namedType.getType() instanceof VisibleString);

        parser = new Parser(new ByteArrayInputStream(
                "string VisibleString, int INTEGER".getBytes())).new ComponentTypeListParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.size());

        namedType = result.get(1).getNamedType();

        assertEquals("int", namedType.getName());
        assertTrue(namedType.getType() instanceof IntegerType);
    }

    @Test
    void testComponentTypeParser() throws IOException, ParserException {
        ComponentTypeParser parser = new Parser(new ByteArrayInputStream(
                "string VisibleString".getBytes())).new ComponentTypeParser();

        ComponentType result = parser.parse();

        assertNotNull(result);

        assertEquals(ComponentType.CompType.NAMED_TYPE, result.getCompType());

        NamedType namedType = result.getNamedType();

        assertEquals("string", namedType.getName());
        assertTrue(namedType.getType() instanceof VisibleString);

        parser = new Parser(new ByteArrayInputStream(
                "real REAL OPTIONAL".getBytes())).new ComponentTypeParser();

        result = parser.parse();

        assertNotNull(result);

        assertEquals(ComponentType.CompType.NAMED_TYPE_OPT, result.getCompType());

        namedType = result.getNamedType();

        assertEquals("real", namedType.getName());
        assertTrue(namedType.getType() instanceof Real);

        parser = new Parser(new ByteArrayInputStream(
                "int INTEGER DEFAULT 12".getBytes())).new ComponentTypeParser();

        result = parser.parse();

        assertNotNull(result);

        assertEquals(ComponentType.CompType.NAMED_TYPE_DEF, result.getCompType());

        namedType = result.getNamedType();

        assertEquals("int", namedType.getName());
        assertTrue(namedType.getType() instanceof IntegerType);
        assertEquals(new IntegerValue(12), result.getValue());

        parser = new Parser(new ByteArrayInputStream(
                "COMPONENTS OF AType".getBytes())).new ComponentTypeParser();

        result = parser.parse();

        assertNotNull(result);

        assertEquals(ComponentType.CompType.TYPE, result.getCompType());

        Type type = result.getType();

        assertNotNull(type);

        assertTrue(type instanceof TypeReference);

        assertEquals("AType", ((TypeReference) type).getType());
    }

    @Test
    void testSequenceValueParser() throws IOException, ParserException {
        CollectionValueParser parser = new Parser(new ByteArrayInputStream(
                "{ string '0101'B, int 23 }".getBytes())).new CollectionValueParser();

        Value result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof CollectionValue);
        assertEquals(2, ((CollectionValue) result).getValues().size());

        EmptyValueParser emptyParser = new Parser(new ByteArrayInputStream(
                "{  }".getBytes())).new EmptyValueParser();

        result = emptyParser.parse();

        assertNotNull(result);
        assertTrue(result instanceof EmptyValue);
    }

    /**
     * ************************************************************************
     * X.680 26
     * ***********************************************************************
     */

    @Test
    void testSequenceOfTypeParser() throws IOException, ParserException {
        BuiltinTypeParserAux parser = new Parser(new ByteArrayInputStream(
                "SEQUENCE OF INTEGER".getBytes())).new BuiltinTypeParserAux();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SequenceOfType);
        assertTrue(((SequenceOfType) result).getType() instanceof IntegerType);

        parser = new Parser(new ByteArrayInputStream(
                "SEQUENCE OF IntType".getBytes())).new BuiltinTypeParserAux();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SequenceOfType);
        assertTrue(((SequenceOfType) result).getType() instanceof TypeReference);
    }

    @Test
    void testSequenceOfValueParser() throws IOException, ParserException {
        CollectionValueParser parser = new Parser(new ByteArrayInputStream(
                "{ aInt 1, bInt 2 }".getBytes())).new CollectionValueParser();

        Value result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof CollectionValue);
        assertEquals(2, ((CollectionValue) result).getValues().size());

        parser = new Parser(new ByteArrayInputStream(
                "{ '0101'B, '1100'B }".getBytes())).new CollectionValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof CollectionOfValue);
        assertEquals(2, ((CollectionOfValue) result).getValues().size());
    }

    @Test
    void testValueListParser() throws IOException, ParserException {
        ValueListParser parser = new Parser(new ByteArrayInputStream(
                "12, '0101'B".getBytes())).new ValueListParser();

        List<Value> result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0) instanceof IntegerValue);
        assertTrue(result.get(1) instanceof BinaryStringValue);
    }

    @Test
    void testNamedValueListParser() throws IOException, ParserException {
        NamedValueListParser parser = new Parser(new ByteArrayInputStream(
                "aNumber 12, aString '0101'B".getBytes())).new NamedValueListParser();

        List<NamedValue> result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getValue() instanceof IntegerValue);
        assertTrue(result.get(1).getValue() instanceof BinaryStringValue);
    }

    /**
     * ************************************************************************
     * X.680 27
     * ***********************************************************************
     */

    @Test
    void testSetTypeParser() throws IOException, ParserException {
        // SET "{" "}"
        BuiltinTypeParserAux parser = new Parser(new ByteArrayInputStream(
                "SET {}".getBytes())).new BuiltinTypeParserAux();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SetType);

        // | SET "{" ExtensionAndException OptionalExtensionMarker "}"
        parser = new Parser(new ByteArrayInputStream(
                "SET { ... ! 34, ...}".getBytes())).new BuiltinTypeParserAux();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SetType);

        SetType seq = (SetType) result;

        assertNotNull(seq.getExtensionAndException());
        assertNotNull(seq.getOptionalExtensionMarker());

        // | SET "{" ComponentTypeLists "}"
        parser = new Parser(new ByteArrayInputStream(
                "SET {anInt INTEGER, ... ! 12, anotherInt INTEGER DEFAULT 47, ...}"
                        .getBytes())).new BuiltinTypeParserAux();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SetType);
    }

    /**
     * ************************************************************************
     * X.680 28
     * ***********************************************************************
     */

    @Test
    void testSetOfTypeParser() throws IOException, ParserException {
        BuiltinTypeParserAux parser = new Parser(new ByteArrayInputStream(
                "SET OF INTEGER".getBytes())).new BuiltinTypeParserAux();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SetOfType);
        assertTrue(((SetOfType) result).getType() instanceof IntegerType);

        parser = new Parser(new ByteArrayInputStream(
                "SET OF ident INTEGER".getBytes())).new BuiltinTypeParserAux();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SetOfType);
        assertTrue(((SetOfType) result).getType() instanceof NamedType);
    }

    /**
     * ************************************************************************
     * X.680 29
     * ***********************************************************************
     */

    @Test
    void testChoiceTypeParser() throws IOException, ParserException {
        BuiltinTypeParserAux parser = new Parser(new ByteArrayInputStream(
                "CHOICE {aNumber INTEGER, aString OCTET STRING }".getBytes())).new BuiltinTypeParserAux();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof Choice);
        assertEquals(2, ((Choice) result).getRootAlternatives().size());
    }

    @Test
    void testAlternativeTypeListsParser() throws IOException,
            ParserException {
        AlternativeTypeListsParser parser = new Parser(
                new ByteArrayInputStream(
                        "aNumber INTEGER, aString OCTET STRING".getBytes())).new AlternativeTypeListsParser();

        AlternativeTypeLists result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.getRootAlternatives().size());
        assertEquals("aNumber", result.getRootAlternatives().get(0).getName());
        assertTrue(result.getRootAlternatives().get(0).getType() instanceof IntegerType);
        assertEquals("aString", result.getRootAlternatives().get(1).getName());
        assertTrue(result.getRootAlternatives().get(1).getType() instanceof OctetString);

        parser = new Parser(new ByteArrayInputStream(
                "aNumber INTEGER, ...".getBytes())).new AlternativeTypeListsParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(1, result.getRootAlternatives().size());
        assertNotNull(result.getExtensionAndException());
        assertNull(result.getExtensionAndException().getExceptionId());

        parser = new Parser(new ByteArrayInputStream(
                "aNumber INTEGER, ... ! 23".getBytes())).new AlternativeTypeListsParser();

        result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getExtensionAndException().getExceptionId());
        assertNotNull(result.getExtensionAndException().getExceptionId()
                .getValue());

        parser = new Parser(new ByteArrayInputStream(
                "aNumber INTEGER, ... ! 23, aString OCTET STRING".getBytes())).new AlternativeTypeListsParser();

        result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getExtensionAdditionAlternatives());
        assertEquals(1, result.getExtensionAdditionAlternatives().size());
        assertFalse(result.hasExtensionMarker());

        parser = new Parser(new ByteArrayInputStream(
                "aNumber INTEGER, ... ! 23, aString OCTET STRING, ..."
                        .getBytes())).new AlternativeTypeListsParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result.hasExtensionMarker());
    }

    @Test
    void testRootAlternativeTypeListParser() throws IOException,
            ParserException {
        RootAlternativeTypeListParser parser = new Parser(
                new ByteArrayInputStream(
                        "aNumber INTEGER, aString OCTET STRING".getBytes())).new RootAlternativeTypeListParser();

        List<NamedType> result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("aNumber", result.get(0).getName());
        assertTrue(result.get(0).getType() instanceof IntegerType);
        assertEquals("aString", result.get(1).getName());
        assertTrue(result.get(1).getType() instanceof OctetString);
    }

    @Test
    void testExtensionAdditionAlternativesParser() throws IOException,
            ParserException {
        ExtensionAdditionAlternativesParser parser = new Parser(
                new ByteArrayInputStream(", aString OCTET STRING".getBytes())).new ExtensionAdditionAlternativesParser();

        List<ExtensionAdditionAlternativeNode> result = parser.parse();

        assertNotNull(result);

        assertEquals("aString", result.get(0).getNamedType().getName());
    }

    @Test
    void testExtensionAdditionAlternativesListParser()
            throws IOException, ParserException {
        ExtensionAdditionAlternativesListParser parser = new Parser(
                new ByteArrayInputStream(
                        "[[12: aNumber INTEGER]], aString OCTET STRING"
                                .getBytes())).new ExtensionAdditionAlternativesListParser();

        List<ExtensionAdditionAlternativeNode> result = parser.parse();

        assertNotNull(result);

        assertEquals(12, (int) result.get(0).getExtensionAdditionAlternativesGroup().getVersion());
        assertEquals("aNumber", result.get(0).getExtensionAdditionAlternativesGroup().getAlternatives()
                .get(0).getName());

        assertEquals("aString", result.get(1).getNamedType().getName());
    }

    @Test
    void testExtensionAdditionAlternativeParser() throws IOException,
            ParserException {
        ExtensionAdditionAlternativeParser parser = new Parser(
                new ByteArrayInputStream("[[12: aNumber INTEGER]]".getBytes())).new ExtensionAdditionAlternativeParser();

        ExtensionAdditionAlternativeNode result = parser.parse();

        assertNotNull(result);
        assertEquals(12, (int) result.getExtensionAdditionAlternativesGroup().getVersion());
        assertEquals("aNumber", result.getExtensionAdditionAlternativesGroup().getAlternatives().get(0)
                .getName());

        result = parser.parse();

        parser = new Parser(new ByteArrayInputStream(
                "aNumber INTEGER".getBytes())).new ExtensionAdditionAlternativeParser();

        result = parser.parse();

        assertEquals("aNumber", result.getNamedType().getName());
    }

    @Test
    void testExtensionAdditionAlternativesGroupParser()
            throws IOException, ParserException {
        ExtensionAdditionAlternativesGroupParser parser = new Parser(
                new ByteArrayInputStream("[[12: aNumber INTEGER]]".getBytes())).new ExtensionAdditionAlternativesGroupParser();

        ExtensionAdditionAlternativesGroup result = parser.parse();

        assertNotNull(result);
        assertEquals(12, (int) result.getVersion());
        assertEquals("aNumber", result.getAlternatives().get(0).getName());
    }

    @Test
    void testAlternativesTypeListParser() throws IOException,
            ParserException {
        AlternativeTypeListParser parser = new Parser(new ByteArrayInputStream(
                "aNumber INTEGER, aString OCTET STRING".getBytes())).new AlternativeTypeListParser();

        List<NamedType> result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("aNumber", result.get(0).getName());
        assertTrue(result.get(0).getType() instanceof IntegerType);
        assertEquals("aString", result.get(1).getName());
        assertTrue(result.get(1).getType() instanceof OctetString);
    }

    @Test
    void testChoiceValueParser() throws IOException, ParserException {
        ChoiceValueParser parser = new Parser(new ByteArrayInputStream(
                "aNumber: 12".getBytes())).new ChoiceValueParser();

        ChoiceValue result = parser.parse();

        assertNotNull(result);
        assertEquals("aNumber", result.getId());
        assertTrue(result.getValue() instanceof IntegerValue);
    }

    /**
     * ************************************************************************
     * X.680 30
     * ***********************************************************************
     */

    @Test
    void testSelectionTypeParser() throws IOException, ParserException {
        SelectionTypeParser parser = new Parser(new ByteArrayInputStream(
                "identifier < Type".getBytes())).new SelectionTypeParser();

        SelectionType result = parser.parse();

        assertNotNull(result);
        assertEquals("identifier", result.getId());
        assertTrue(result.getType() instanceof TypeReference);
    }

    /**
     * ************************************************************************
     * X.680 31
     * ***********************************************************************
     */

    @Test
    void testPrefixedTypeParser() throws IOException, ParserException {
        PrefixedTypeParser parser = new Parser(new ByteArrayInputStream(
                "[TAG: APPLICATION 23] INTEGER".getBytes())).new PrefixedTypeParser();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof IntegerType);
        assertEquals("TAG", result.getTags().getFirst().getEncodingReference());
        assertEquals(ClassType.APPLICATION, result.getTags().getFirst().getClazz());
        assertEquals(23, (int) result.getTags().getFirst().getClassNumber().getClazz());

        parser = new Parser(new ByteArrayInputStream(
                "[TAG: 4711] INTEGER".getBytes())).new PrefixedTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof IntegerType);
        assertEquals("TAG", result.getTags().getFirst().getEncodingReference());
        assertNull(result.getTags().getFirst().getClazz());
        assertEquals(4711, (int) result.getTags().getFirst().getClassNumber().getClazz());

        parser = new Parser(new ByteArrayInputStream(
                "[4711] INTEGER".getBytes())).new PrefixedTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof IntegerType);
        assertNull(result.getTags().getFirst().getEncodingReference());
        assertNull(result.getTags().getFirst().getClazz());
        assertEquals(4711, (int) result.getTags().getFirst().getClassNumber().getClazz());

        parser = new Parser(new ByteArrayInputStream(
                "[PER: \"encoding instructions\"] INTEGER".getBytes())).new PrefixedTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof IntegerType);
        assertNotNull(result.getEncodingPrefix());
        assertEquals("PER", result.getEncodingPrefix().getEncodingReference());
        assertNotNull(result.getEncodingPrefix().getEncodingInstruction());
    }

    @Test
    void testTaggedTypeParser() throws IOException, ParserException {
        TaggedTypeParser parser = new Parser(new ByteArrayInputStream(
                "[TAG: APPLICATION 1] INTEGER".getBytes())).new TaggedTypeParser();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof IntegerType);
        assertEquals(ClassType.APPLICATION, result.getTags().getFirst().getClazz());
    }

    @Test
    void testTagParser() throws IOException, ParserException {
        TagParser parser = new Parser(new ByteArrayInputStream(
                "[TAG: UNIVERSAL 10]".getBytes())).new TagParser();

        Tag result = parser.parse();

        assertNotNull(result);
        assertEquals("TAG", result.getEncodingReference());
        assertEquals(ClassType.UNIVERSAL, result.getClazz());
        assertEquals(10, (int) result.getClassNumber().getClazz());
    }

    @Test
    void testEncodingReferenceParser() throws IOException,
            ParserException {
        EncodingReferenceParser parser = new Parser(new ByteArrayInputStream(
                "TAG:".getBytes())).new EncodingReferenceParser();

        String result = parser.parse();

        assertNotNull(result);
        assertEquals("TAG", result);
    }

    @Test
    void testClassNumberParser() throws IOException, ParserException {
        ClassNumberParser parser = new Parser(new ByteArrayInputStream(
                "4711".getBytes())).new ClassNumberParser();

        ClassNumber result = parser.parse();

        assertNotNull(result);
        assertEquals(4711, (int) result.getClazz());

        parser = new Parser(new ByteArrayInputStream("value-ref".getBytes())).new ClassNumberParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getRef() instanceof SimpleDefinedValue);
        assertEquals("value-ref",
                ((SimpleDefinedValue) result.getRef()).getReference());
    }

    @Test
    void testClassParser() throws IOException, ParserException {
        ClassParser parser = new Parser(new ByteArrayInputStream(
                "UNIVERSAL".getBytes())).new ClassParser();

        ClassType result = parser.parse();

        assertNotNull(result);
        assertEquals(ClassType.UNIVERSAL, result);

        parser = new Parser(new ByteArrayInputStream("APPLICATION".getBytes())).new ClassParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(ClassType.APPLICATION, result);

        parser = new Parser(new ByteArrayInputStream("PRIVATE".getBytes())).new ClassParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(ClassType.PRIVATE, result);
    }

    @Test
    void testEncodingPrefixedTypeParser() throws IOException,
            ParserException {
        EncodingPrefixedTypeParser parser = new Parser(
                new ByteArrayInputStream("[TAG: 21] VisibleString".getBytes())).new EncodingPrefixedTypeParser();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof VisibleString);
        assertNotNull(result.getEncodingPrefix());
    }

    @Test
    void testEncodingPrefixParser() throws IOException, ParserException {
        EncodingPrefixParser parser = new Parser(new ByteArrayInputStream(
                "[TAG: \"encoding instruction\"]".getBytes())).new EncodingPrefixParser();

        EncodingPrefixNode result = parser.parse();

        assertNotNull(result);
        assertEquals("TAG", result.getEncodingReference());
        assertEquals(1, result.getEncodingInstruction().size());

        parser = new Parser(new ByteArrayInputStream(
                "[\"encoding instruction\"]".getBytes())).new EncodingPrefixParser();

        result = parser.parse();

        assertNotNull(result);
        assertNull(result.getEncodingReference());
        assertEquals(1, result.getEncodingInstruction().size());
    }

    /**
     * ************************************************************************
     * X.680 32
     * ***********************************************************************
     */

    @Test
    void testObjectIdentifierTypeParser() throws IOException,
            ParserException {
        BuiltinTypeParserAux parser = new Parser(new ByteArrayInputStream(
                "OBJECT IDENTIFIER".getBytes())).new BuiltinTypeParserAux();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ObjectIdentifier);
    }

    @Test
    void testObjectIdentifierValueParser() throws IOException, ParserException {
        ObjectIdentifierValueParser parser = new Parser(
                new ByteArrayInputStream(
                        "{ oid-component1 Module.oid-component 4711 oid-comp (42)}"
                                .getBytes())).new ObjectIdentifierValueParser();

        ObjectIdentifierValue result = parser.parse();

        assertNotNull(result);
        assertEquals(4, result.getComponents().size());
    }

    @Test
    void testObjectIdComponentListParser() throws IOException, ParserException {
        ObjIdComponentsListParser parser = new Parser(new ByteArrayInputStream(
                "oid-component1 Module.oid-component 4711 oid-comp (42)"
                        .getBytes())).new ObjIdComponentsListParser();

        List<OIDComponentNode> result = parser.parse();

        assertNotNull(result);
        assertEquals(4, result.size());
    }

    @Test
    void testObjectIdComponentsParser() throws IOException,
            ParserException {
        ObjIdComponentsParser parser = new Parser(new ByteArrayInputStream(
                "oid-component".getBytes())).new ObjIdComponentsParser();

        OIDComponentNode result = parser.parse();

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals("oid-component", result.getName());

        parser = new Parser(new ByteArrayInputStream("4711".getBytes())).new ObjIdComponentsParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(4711, (int) result.getId());

        parser = new Parser(new ByteArrayInputStream(
                "Module.oid-component".getBytes())).new ObjIdComponentsParser();

        result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getDefinedValue());
        assertTrue(result.getDefinedValue() instanceof ExternalValueReference);
        assertEquals("Module",
                ((ExternalValueReference) result.getDefinedValue()).getModule());
        assertEquals("oid-component",
                ((ExternalValueReference) result.getDefinedValue()).getReference());

        parser = new Parser(new ByteArrayInputStream(
                "oid-component (Module.value)".getBytes())).new ObjIdComponentsParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals("oid-component", result.getName());
        assertNotNull(result.getDefinedValue());
        assertTrue(result.getDefinedValue() instanceof ExternalValueReference);
        assertEquals("Module",
                ((ExternalValueReference) result.getDefinedValue()).getModule());
        assertEquals("value",
                ((ExternalValueReference) result.getDefinedValue()).getReference());
    }

    @Test
    void testNameFormParser() throws IOException, ParserException {
        NameFormParser parser = new Parser(new ByteArrayInputStream(
                "oid-component".getBytes())).new NameFormParser();

        OIDComponentNode result = parser.parse();

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals("oid-component", result.getName());
    }

    @Test
    void testNumberFormParser() throws IOException, ParserException {
        NumberFormParser parser = new Parser(new ByteArrayInputStream(
                "4711".getBytes())).new NumberFormParser();

        OIDComponentNode result = parser.parse();

        assertNotNull(result);
        assertEquals(4711, (int) result.getId());

        parser = new Parser(new ByteArrayInputStream(
                "Module.oid-component".getBytes())).new NumberFormParser();

        result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getDefinedValue());
        assertTrue(result.getDefinedValue() instanceof ExternalValueReference);
        assertEquals("Module",
                ((ExternalValueReference) result.getDefinedValue()).getModule());
        assertEquals("oid-component",
                ((ExternalValueReference) result.getDefinedValue()).getReference());
    }

    @Test
    void testNameAndNumberFormParser() throws IOException,
            ParserException {
        NameAndNumberFormParser parser = new Parser(new ByteArrayInputStream(
                "oid-component (Module.value)".getBytes())).new NameAndNumberFormParser();

        OIDComponentNode result = parser.parse();

        assertNotNull(result);
        assertEquals("oid-component", result.getName());
        assertNotNull(result.getDefinedValue());
        assertTrue(result.getDefinedValue() instanceof ExternalValueReference);
        assertEquals("Module",
                ((ExternalValueReference) result.getDefinedValue()).getModule());
        assertEquals("value",
                ((ExternalValueReference) result.getDefinedValue()).getReference());

        parser = new Parser(new ByteArrayInputStream(
                "oid-component (4711)".getBytes())).new NameAndNumberFormParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals("oid-component", result.getName());
        assertNotNull(result.getId());
        assertEquals(4711, (int) result.getId());
    }

    /**
     * ************************************************************************
     * X.680 33
     * ***********************************************************************
     */

    @Test
    void testRelativeOIDTypeParser() throws IOException, ParserException {
        BuiltinTypeParserAux parser = new Parser(new ByteArrayInputStream(
                "RELATIVE-OID".getBytes())).new BuiltinTypeParserAux();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof RelativeOID);
    }

    @Test
    void testRelativeOIDValueParser() throws IOException,
            ParserException {
        RelativeOIDValueParser parser = new Parser(new ByteArrayInputStream(
                "{ 4711 test (4712) }".getBytes())).new RelativeOIDValueParser();

        RelativeOIDValue result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.getComponents().size());
    }

    @Test
    void testRelativeOIDComponentsListParser() throws IOException,
            ParserException {
        RelativeOIDComponentsListParser parser = new Parser(
                new ByteArrayInputStream("4711 test (4712)".getBytes())).new RelativeOIDComponentsListParser();

        List<OIDComponentNode> result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testRelativeOIDComponentsParser() throws IOException,
            ParserException {
        RelativeOIDComponentsParser parser = new Parser(
                new ByteArrayInputStream("4711".getBytes())).new RelativeOIDComponentsParser();

        Node result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof OIDComponentNode);

        parser = new Parser(new ByteArrayInputStream("test (4711)".getBytes())).new RelativeOIDComponentsParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof OIDComponentNode);

        parser = new Parser(new ByteArrayInputStream(
                "value-reference".getBytes())).new RelativeOIDComponentsParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof OIDComponentNode);
    }

    /**
     * ************************************************************************
     * X.680 34
     * ***********************************************************************
     */

    @Test
    void testIRITypeParser() throws IOException, ParserException {
        BuiltinTypeParserAux parser = new Parser(new ByteArrayInputStream(
                "OID-IRI".getBytes())).new BuiltinTypeParserAux();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof IRI);
    }

    @Test
    void testIRIValueParser() throws IOException, ParserException {
        IRIValueParser parser = new Parser(new ByteArrayInputStream(
                "\"/ISO/Registration-Authority/19785.CBEFF\"".getBytes())).new IRIValueParser();

        Value result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof IRIValue);
        assertEquals(3, ((IRIValue) result).getArcIdentifiers().size());

        parser = new Parser(new ByteArrayInputStream(
                "\"/0/Registration-Authority/19785.CBEFF\"".getBytes())).new IRIValueParser();

        result = parser.parse();
        assertTrue(result instanceof IRIValue);
        assertEquals(3, ((IRIValue) result).getArcIdentifiers().size());

        parser = new Parser(new ByteArrayInputStream(
                "\"/0/0/19785.CBEFF\"".getBytes())).new IRIValueParser();

        result = parser.parse();
        assertTrue(result instanceof IRIValue);
        assertEquals(3, ((IRIValue) result).getArcIdentifiers().size());

        parser = new Parser(new ByteArrayInputStream(
                "\"/0/01/19785.CBEFF\"".getBytes())).new IRIValueParser();

        try {
            result = parser.parse();
            fail("ASN1ParserException expected");
        } catch (ParserException e) {
        }
    }

    /**
     * ************************************************************************
     * X.680 35
     * ***********************************************************************
     */

    @Test
    void testRelativeIRITypeParser() throws IOException, ParserException {
        BuiltinTypeParserAux parser = new Parser(new ByteArrayInputStream(
                "RELATIVE-OID-IRI".getBytes())).new BuiltinTypeParserAux();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof RelativeIRI);
    }

    @Test
    void testRelativeIRIValueParser() throws IOException,
            ParserException {
        RelativeIRIValueParser parser = new Parser(

                new ByteArrayInputStream(
                        "\"ISO/Registration_Authority/19785.CBEFF\"".getBytes())).new RelativeIRIValueParser();

        Value result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof RelativeIRIValue);
        assertEquals(3, ((RelativeIRIValue) result).getArcIdentifiers().size());

        parser = new Parser(new ByteArrayInputStream(
                "\"0/Registration_Authority/19785.CBEFF\"".getBytes())).new RelativeIRIValueParser();

        result = parser.parse();
        assertTrue(result instanceof RelativeIRIValue);
        assertEquals(3, ((RelativeIRIValue) result).getArcIdentifiers().size());

        parser = new Parser(new ByteArrayInputStream(
                "\"0/0/19785.CBEFF\"".getBytes())).new RelativeIRIValueParser();

        result = parser.parse();
        assertTrue(result instanceof RelativeIRIValue);
        assertEquals(3, ((RelativeIRIValue) result).getArcIdentifiers().size());

        parser = new Parser(new ByteArrayInputStream(
                "\"0/01/19785.CBEFF\"".getBytes())).new RelativeIRIValueParser();

        try {
            parser.parse();
            fail("ASN1ParserException expected");
        } catch (ParserException e) {
        }
    }

    /**
     * ************************************************************************
     * X.680 36
     * ***********************************************************************
     */

    @Test
    void testEmbeddedPDVTypeParser() throws IOException, ParserException {
        BuiltinTypeParserAux parser = new Parser(new ByteArrayInputStream(
                "EMBEDDED PDV".getBytes())).new BuiltinTypeParserAux();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof EmbeddedPDV);
    }

    @Test
    void testEmbeddedPDVValueParser() {
        // TODO: implement
    }

    /**
     * ************************************************************************
     * X.680 37
     * ***********************************************************************
     */

    @Test
    void testExternalTypeParser() throws IOException, ParserException {
        BuiltinTypeParserAux parser = new Parser(new ByteArrayInputStream(
                "EXTERNAL".getBytes())).new BuiltinTypeParserAux();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof External);
    }

    @Test
    void testExternalValueParser() {
        // TODO: Test sequence content
    }

    /**
     * ************************************************************************
     * X.680 38
     * ***********************************************************************
     */

    @Test
    void testTimeTypeParser() throws IOException, ParserException {
        BuiltinTypeParserAux parser = new Parser(new ByteArrayInputStream(
                "TIME".getBytes())).new BuiltinTypeParserAux();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof Time);
    }

    @Test
    void testTimeValueParser() throws IOException, ParserException {
        RestrictedCharacterStringValueParser parser = new Parser(
                new ByteArrayInputStream("\"P0Y29M0DT0H0.00M\"".getBytes())).new RestrictedCharacterStringValueParser();

        Value result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof StringValue);
        assertTrue(((StringValue) result).isTString());
        assertNotNull(((StringValue) result).getTimeValue());
    }

    @Test
    void testDateTypeParser() throws IOException, ParserException {
        BuiltinTypeParserAux parser = new Parser(new ByteArrayInputStream(
                "DATE".getBytes())).new BuiltinTypeParserAux();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof DateType);
    }

    @Test
    void testTimeOfDayTypeParser() throws IOException, ParserException {
        BuiltinTypeParserAux parser = new Parser(new ByteArrayInputStream(
                "TIME-OF-DAY".getBytes())).new BuiltinTypeParserAux();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof TimeOfDay);
    }

    @Test
    void testDateTimeTypeParser() throws IOException, ParserException {
        BuiltinTypeParserAux parser = new Parser(new ByteArrayInputStream(
                "DATE-TIME".getBytes())).new BuiltinTypeParserAux();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof DateTime);
    }

    @Test
    void testDurationTypeParser() throws IOException, ParserException {
        BuiltinTypeParserAux parser = new Parser(new ByteArrayInputStream(
                "DURATION".getBytes())).new BuiltinTypeParserAux();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof Duration);
    }

    /**
     * ************************************************************************
     * X.680 40
     * ***********************************************************************
     */

    @Test
    void testCharacterStringTypeParser() throws IOException,
            ParserException {
        CharacterStringTypeParser parser = new Parser(new ByteArrayInputStream(
                "BMPString".getBytes())).new CharacterStringTypeParser();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof BMPString);

        parser = new Parser(new ByteArrayInputStream(
                "CHARACTER STRING".getBytes())).new CharacterStringTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof CharacterString);
    }

    @Test
    void testCharacterStringValueParser() throws IOException,
            ParserException {
        CharacterStringValueParser parser = new Parser(
                new ByteArrayInputStream("\"abc\"".getBytes())).new CharacterStringValueParser();

        Value result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof StringValue);

        // TODO: UnrestrictedCharacterStringValue
    }

    /**
     * ************************************************************************
     * X.680 41
     * ***********************************************************************
     */

    @Test
    void testRestrictedCharacterStringTypeParser() throws IOException,
            ParserException {
        RestrictedCharacterStringTypeParser parser = new Parser(
                new ByteArrayInputStream("BMPString".getBytes())).new RestrictedCharacterStringTypeParser();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof BMPString);

        parser = new Parser(
                new ByteArrayInputStream("GeneralString".getBytes())).new RestrictedCharacterStringTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof GeneralString);

        parser = new Parser(
                new ByteArrayInputStream("GraphicString".getBytes())).new RestrictedCharacterStringTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof GraphicString);

        parser = new Parser(new ByteArrayInputStream("IA5String".getBytes())).new RestrictedCharacterStringTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof IA5String);

        parser = new Parser(new ByteArrayInputStream("ISO646String".getBytes())).new RestrictedCharacterStringTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ISO646String);

        parser = new Parser(
                new ByteArrayInputStream("NumericString".getBytes())).new RestrictedCharacterStringTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof NumericString);

        parser = new Parser(new ByteArrayInputStream(
                "PrintableString".getBytes())).new RestrictedCharacterStringTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof PrintableString);

        parser = new Parser(
                new ByteArrayInputStream("TeletexString".getBytes())).new RestrictedCharacterStringTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof TeletexString);

        parser = new Parser(new ByteArrayInputStream("T61String".getBytes())).new RestrictedCharacterStringTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof T61String);

        parser = new Parser(new ByteArrayInputStream(
                "UniversalString".getBytes())).new RestrictedCharacterStringTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof UniversalString);

        parser = new Parser(new ByteArrayInputStream("UTF8String".getBytes())).new RestrictedCharacterStringTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof UTF8String);

        parser = new Parser(new ByteArrayInputStream(
                "VideotexString".getBytes())).new RestrictedCharacterStringTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof VideotexString);

        parser = new Parser(
                new ByteArrayInputStream("VisibleString".getBytes())).new RestrictedCharacterStringTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof VisibleString);
    }

    @Test
    void testRestrictedCharacterStringValueParser() throws IOException,
            ParserException {
        RestrictedCharacterStringValueParser parser = new Parser(
                new ByteArrayInputStream("\"abc\"".getBytes())).new RestrictedCharacterStringValueParser();

        Value result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof StringValue);
        assertEquals("abc", ((StringValue) result).getCString());

        parser = new Parser(new ByteArrayInputStream(
                "{\"abc\", {1, 2, 3, 4}}".getBytes())).new RestrictedCharacterStringValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof CharacterStringList);
        assertEquals(2, ((CharacterStringList) result).getValues().size());

        parser = new Parser(new ByteArrayInputStream("{1, 2, 3, 4}".getBytes())).new RestrictedCharacterStringValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof CollectionOfValue);
        assertTrue(((CollectionOfValue) result).isQuadruple());

        parser = new Parser(new ByteArrayInputStream("{1, 2}".getBytes())).new RestrictedCharacterStringValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof CollectionOfValue);
        assertTrue(((CollectionOfValue) result).isTuple());
    }

    @Test
    void testCharacterStringListParser() throws IOException,
            ParserException {
        CharacterStringListParser parser = new Parser(new ByteArrayInputStream(
                "{\"abc\", {1, 2, 3, 4}, value-ref, {1, 2}}".getBytes())).new CharacterStringListParser();

        CharacterStringList result = parser.parse();

        assertNotNull(result);
        assertEquals(4, result.getValues().size());
    }

    @Test
    void testCharSymsParser() throws IOException, ParserException {
        CharSymsParser parser = new Parser(new ByteArrayInputStream(
                "\"abc\", {1, 2, 3, 4}, value-ref, {1, 2}".getBytes())).new CharSymsParser();

        List<Value> result = parser.parse();

        assertNotNull(result);
        assertEquals(4, result.size());
    }

    @Test
    void testCharsDefnParser() throws IOException, ParserException {
        CharsDefnParser parser = new Parser(new ByteArrayInputStream(
                "\"abc\"".getBytes())).new CharsDefnParser();

        Value result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof StringValue);
        assertEquals("abc", ((StringValue) result).getCString());

        parser = new Parser(new ByteArrayInputStream("{1, 2, 3, 4}".getBytes())).new CharsDefnParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof CollectionOfValue);
        assertTrue(((CollectionOfValue) result).isQuadruple());

        parser = new Parser(new ByteArrayInputStream("{1, 2}".getBytes())).new CharsDefnParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof CollectionOfValue);
        assertTrue(((CollectionOfValue) result).isTuple());

        parser = new Parser(new ByteArrayInputStream("value-ref".getBytes())).new CharsDefnParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SimpleDefinedValue);
    }

    @Test
    void testQuadrupleParser() throws IOException, ParserException {
        CollectionValueParser parser = new Parser(new ByteArrayInputStream(
                "{0, 0, 0, 0}".getBytes())).new CollectionValueParser();

        Value result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof CollectionOfValue);
        assertTrue(((CollectionOfValue) result).isQuadruple());

        Quadruple quadrupel = ((CollectionOfValue) result).toQuadruple();

        assertEquals(0, quadrupel.getGroup());
        assertEquals(0, quadrupel.getPlane());
        assertEquals(0, quadrupel.getRow());
        assertEquals(0, quadrupel.getCell());

        parser = new Parser(new ByteArrayInputStream(
                "{127, 255, 255, 255}".getBytes())).new CollectionValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof CollectionOfValue);
        assertTrue(((CollectionOfValue) result).isQuadruple());

        quadrupel = ((CollectionOfValue) result).toQuadruple();

        assertEquals(127, quadrupel.getGroup());
        assertEquals(255, quadrupel.getPlane());
        assertEquals(255, quadrupel.getRow());
        assertEquals(255, quadrupel.getCell());

        parser = new Parser(new ByteArrayInputStream(
                "{127, 255, 255, 256}".getBytes())).new CollectionValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof CollectionOfValue);
        assertFalse(((CollectionOfValue) result).isQuadruple());

        parser = new Parser(new ByteArrayInputStream(
                "{127, 255, 256, 255}".getBytes())).new CollectionValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof CollectionOfValue);
        assertFalse(((CollectionOfValue) result).isQuadruple());

        parser = new Parser(new ByteArrayInputStream(
                "{127, 256, 255, 255}".getBytes())).new CollectionValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof CollectionOfValue);
        assertFalse(((CollectionOfValue) result).isQuadruple());

        parser = new Parser(new ByteArrayInputStream(
                "{128, 255, 252, 255}".getBytes())).new CollectionValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof CollectionOfValue);
        assertFalse(((CollectionOfValue) result).isQuadruple());

        parser = new Parser(
                new ByteArrayInputStream("{-1, 0, 0, 0}".getBytes())).new CollectionValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof CollectionOfValue);
        assertFalse(((CollectionOfValue) result).isQuadruple());

        parser = new Parser(
                new ByteArrayInputStream("{0, -1, 0, 0}".getBytes())).new CollectionValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof CollectionOfValue);
        assertFalse(((CollectionOfValue) result).isQuadruple());

        parser = new Parser(
                new ByteArrayInputStream("{0, 0, -1, 0}".getBytes())).new CollectionValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof CollectionOfValue);
        assertFalse(((CollectionOfValue) result).isQuadruple());

        parser = new Parser(
                new ByteArrayInputStream("{0, 0, 0, -1}".getBytes())).new CollectionValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof CollectionOfValue);
        assertFalse(((CollectionOfValue) result).isQuadruple());
    }

    @Test
    void testTupleParser() throws IOException, ParserException {
        CollectionValueParser parser = new Parser(new ByteArrayInputStream(
                "{7, 15}".getBytes())).new CollectionValueParser();

        Value result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof CollectionOfValue);
        assertTrue(((CollectionOfValue) result).isTuple());

        Tuple tuple = ((CollectionOfValue) result).toTuple();

        assertNotNull(result);
        assertEquals(7, tuple.getColumn());
        assertEquals(15, tuple.getRow());

        parser = new Parser(new ByteArrayInputStream("{7, -1}".getBytes())).new CollectionValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof CollectionOfValue);
        assertFalse(((CollectionOfValue) result).isTuple());

        parser = new Parser(new ByteArrayInputStream("{7, 16}".getBytes())).new CollectionValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof CollectionOfValue);
        assertFalse(((CollectionOfValue) result).isTuple());

        parser = new Parser(new ByteArrayInputStream("{-1, 15}".getBytes())).new CollectionValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof CollectionOfValue);
        assertFalse(((CollectionOfValue) result).isTuple());

        parser = new Parser(new ByteArrayInputStream("{8, 15}".getBytes())).new CollectionValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof CollectionOfValue);
        assertFalse(((CollectionOfValue) result).isTuple());
    }

    /**
     * ************************************************************************
     * X.680 44
     * ***********************************************************************
     */

    @Test
    void testUnrestrinctedCharacterStringTypeParser()
            throws IOException, ParserException {
        UnrestrictedCharacterStringTypeParser parser = new Parser(
                new ByteArrayInputStream("CHARACTER STRING".getBytes())).new UnrestrictedCharacterStringTypeParser();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof CharacterString);
    }

    @Test
    void testUnrestrinctedCharacterStringValueParser() {
        // TODO: implement
    }

    /**
     * ************************************************************************
     * X.680 46
     * ***********************************************************************
     */

    @Test
    void testGeneralizedTimeParser() throws IOException, ParserException {
        UsefulTypeParser parser = new Parser(new ByteArrayInputStream(
                "GeneralizedTime".getBytes())).new UsefulTypeParser();

        UsefulType result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof GeneralizedTime);
    }

    /**
     * ************************************************************************
     * X.680 47
     * ***********************************************************************
     */

    @Test
    void testUTCTimeParser() throws IOException, ParserException {
        UsefulTypeParser parser = new Parser(new ByteArrayInputStream(
                "UTCTime".getBytes())).new UsefulTypeParser();

        UsefulType result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof UTCTime);
    }

    /**
     * ************************************************************************
     * X.680 48
     * ***********************************************************************
     */

    @Test
    void testObjectDescriptorParser() throws IOException,
            ParserException {
        UsefulTypeParser parser = new Parser(new ByteArrayInputStream(
                "ObjectDescriptor".getBytes())).new UsefulTypeParser();

        UsefulType result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ObjectDescriptor);
    }

    /**
     * ************************************************************************
     * X.680 49
     * ***********************************************************************
     */

    @Test
    void testConstrainedTypeParser() throws IOException, ParserException {
        TypeParser parser = new Parser(new ByteArrayInputStream(
                "VisibleString (ENCODED BY value)".getBytes())).new TypeParser();

        Type result = parser.parse();

        assertTrue(result instanceof VisibleString);
        assertTrue(result.hasConstraint());
        assertTrue(result.getConstraints().get(0) instanceof ContentsConstraint);

        parser = new Parser(new ByteArrayInputStream(
                "VisibleString (SIZE (1..10)) (\"abc\" | \"def\")".getBytes())).new TypeParser();

        result = parser.parse();

        assertTrue(result instanceof VisibleString);
        assertTrue(result.hasConstraint());
        assertTrue(result.getConstraints().get(0) instanceof SubtypeConstraint);
        assertTrue(result.getConstraints().get(1) instanceof SubtypeConstraint);

        parser = new Parser(new ByteArrayInputStream(
                "OCTET STRING ('3AC49E70'H)".getBytes())).new TypeParser();

        result = parser.parse();

        assertTrue(result instanceof OctetString);
        assertTrue(result.hasConstraint());
        assertTrue(result.getConstraints().get(0) instanceof SubtypeConstraint);

        parser = new Parser(new ByteArrayInputStream(
                "SET SIZE (0 .. 2) OF VisibleString".getBytes())).new TypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SetOfType);
        assertTrue(((SetOfType) result).getType() instanceof VisibleString);
        assertTrue(result.hasConstraint());
        assertTrue(result.getConstraints().get(0) instanceof SizeConstraint);
    }

    @Test
    void testTypeWithConstraintParser() throws IOException,
            ParserException {
        TypeWithConstraintParser parser = new Parser(new ByteArrayInputStream(
                "SET SIZE (0 .. 2) OF VisibleString".getBytes())).new TypeWithConstraintParser();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SetOfType);
        assertTrue(((SetOfType) result).getType() instanceof VisibleString);
        assertTrue(result.hasConstraint());
        assertTrue(result.getConstraints().get(0) instanceof SizeConstraint);

        parser = new Parser(new ByteArrayInputStream(
                "SET SIZE (0 .. 2) OF aString VisibleString".getBytes())).new TypeWithConstraintParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SetOfType);
        assertTrue(((SetOfType) result).getType() instanceof NamedType);
        assertTrue(result.hasConstraint());
        assertTrue(result.getConstraints().get(0) instanceof SizeConstraint);

        parser = new Parser(new ByteArrayInputStream(
                "SEQUENCE SIZE (0 .. 2) OF VisibleString".getBytes())).new TypeWithConstraintParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SequenceOfType);
        assertTrue(((SequenceOfType) result).getType() instanceof VisibleString);
        assertTrue(result.hasConstraint());
        assertTrue(result.getConstraints().get(0) instanceof SizeConstraint);

        parser = new Parser(new ByteArrayInputStream(
                "SEQUENCE SIZE (0 .. 2) OF aString VisibleString".getBytes())).new TypeWithConstraintParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SequenceOfType);
        assertTrue(((SequenceOfType) result).getType() instanceof NamedType);
        assertTrue(result.hasConstraint());
        assertTrue(result.getConstraints().get(0) instanceof SizeConstraint);

        parser = new Parser(new ByteArrayInputStream(
                "SET (\"true\" | \"false\") OF VisibleString".getBytes())).new TypeWithConstraintParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SetOfType);
        assertTrue(((SetOfType) result).getType() instanceof VisibleString);
        assertTrue(result.hasConstraint());
        assertTrue(result.getConstraints().get(0) instanceof SubtypeConstraint);

        parser = new Parser(new ByteArrayInputStream(
                "SET (\"true\" | \"false\") OF aString VisibleString"
                        .getBytes())).new TypeWithConstraintParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SetOfType);
        assertTrue(((SetOfType) result).getType() instanceof NamedType);
        assertTrue(result.hasConstraint());
        assertTrue(result.getConstraints().get(0) instanceof SubtypeConstraint);

        parser = new Parser(new ByteArrayInputStream(
                "SEQUENCE (\"true\" | \"false\") OF VisibleString".getBytes())).new TypeWithConstraintParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SequenceOfType);
        assertTrue(((SequenceOfType) result).getType() instanceof VisibleString);
        assertTrue(result.hasConstraint());
        assertTrue(result.getConstraints().get(0) instanceof SubtypeConstraint);

        parser = new Parser(new ByteArrayInputStream(
                "SEQUENCE (\"true\" | \"false\") OF aString VisibleString"
                        .getBytes())).new TypeWithConstraintParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SequenceOfType);
        assertTrue(((SequenceOfType) result).getType() instanceof NamedType);
        assertTrue(result.hasConstraint());
        assertTrue(result.getConstraints().get(0) instanceof SubtypeConstraint);
    }

    @Test
    void testConstraintParser() throws IOException, ParserException {
        ConstraintParser parser = new Parser(new ByteArrayInputStream(
                "((1..10) EXCEPT (4..6))".getBytes())).new ConstraintParser();

        Constraint result = parser.parse();

        assertNotNull(result);
        assertNull(result.getExceptionSpec());

        parser = new Parser(new ByteArrayInputStream(
                "((1..10) EXCEPT (4..6) ! INTEGER: 21)".getBytes())).new ConstraintParser();

        result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getExceptionSpec());
    }

    @Test
    void testConstraintSpecParser() throws IOException, ParserException {
        ConstraintSpecParser parser = new Parser(new ByteArrayInputStream(
                "(1..10) EXCEPT (4..6)".getBytes())).new ConstraintSpecParser();

        Constraint result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SubtypeConstraint);

        parser = new Parser(new ByteArrayInputStream(
                "CONSTRAINED BY {}".getBytes())).new ConstraintSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof UserDefinedConstraintNode);
    }

    @Test
    void testSubtypeConstraintParser() throws IOException,
            ParserException {
        SubtypeConstraintParser parser = new Parser(new ByteArrayInputStream(
                "(1..10) EXCEPT (4..6)".getBytes())).new SubtypeConstraintParser();

        SubtypeConstraint result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getElementSetSpecs());
    }

    /**
     * ************************************************************************
     * X.680 50
     * ***********************************************************************
     */

    @Test
    void testElementSetSpecsParser() throws IOException, ParserException {
        ElementSetSpecsParser parser = new Parser(new ByteArrayInputStream(
                "(1..10) EXCEPT (4..6)".getBytes())).new ElementSetSpecsParser();

        SetSpecsNode result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getRootElements());
        assertNull(result.getExtensionElements());
        assertFalse(result.hasExtensionMarker());

        parser = new Parser(new ByteArrayInputStream(
                "ALL EXCEPT (4..6), ...".getBytes())).new ElementSetSpecsParser();

        result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getRootElements());
        assertNull(result.getExtensionElements());
        assertTrue(result.hasExtensionMarker());

        parser = new Parser(new ByteArrayInputStream(
                "(1..5) | (12<..<17), ..., ALL EXCEPT (7..77)".getBytes())).new ElementSetSpecsParser();

        result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getRootElements());
        assertNotNull(result.getExtensionElements());
        assertTrue(result.hasExtensionMarker());
    }

    @Test
    void testElementSetSpecParser() throws IOException, ParserException {
        ElementSetSpecParser parser = new Parser(new ByteArrayInputStream(
                "ALL EXCEPT 1".getBytes())).new ElementSetSpecParser();

        ElementSet result = parser.parse();

        assertNotNull(result);
        assertEquals(OpType.ALL, result.getOperation());

        result = (ElementSet) result.getOperands().get(0);

        assertEquals(OpType.EXCLUDE, result.getOperation());

        Constraint constraint = (Constraint) result.getOperands().get(0);

        assertTrue(constraint instanceof SingleValueConstraint);
        assertTrue(((SingleValueConstraint) constraint).getValue() instanceof IntegerValue);

        parser = new Parser(new ByteArrayInputStream(
                "TRUE UNION FALSE".getBytes())).new ElementSetSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(OpType.UNION, result.getOperation());

        result = (ElementSet) result.getOperands().get(0);

        assertEquals(OpType.INTERSECTION, result.getOperation());

        constraint = (Constraint) result.getOperands().get(0);

        assertTrue(constraint instanceof SingleValueConstraint);
        assertTrue(((SingleValueConstraint) constraint).getValue() instanceof BooleanValue);

        parser = new Parser(new ByteArrayInputStream(
                "ALL EXCEPT NULL".getBytes())).new ElementSetSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(OpType.ALL, result.getOperation());

        result = (ElementSet) result.getOperands().get(0);

        assertEquals(OpType.EXCLUDE, result.getOperation());

        constraint = (Constraint) result.getOperands().get(0);

        assertTrue(constraint instanceof SingleValueConstraint);
        assertTrue(((SingleValueConstraint) constraint).getValue() instanceof NullValue);
    }

    @Test
    void testUnionsParser() throws IOException, ParserException {
        UnionsParser parser = new Parser(new ByteArrayInputStream(
                "(1 .. 10) | (20..30)".getBytes())).new UnionsParser();

        Elements result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ElementSet);
        assertEquals(OpType.UNION, ((ElementSet) result).getOperation());
    }

    @Test
    void testIntersectionsParser() throws IOException, ParserException {
        IntersectionsParser parser = new Parser(new ByteArrayInputStream(
                "(1 .. 40) ^ (20..30)".getBytes())).new IntersectionsParser();

        Elements result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ElementSet);
        assertEquals(OpType.INTERSECTION, ((ElementSet) result).getOperation());

        parser = new Parser(new ByteArrayInputStream(
                "(1 .. 40) INTERSECTION (ALL EXCEPT (20..50))".getBytes())).new IntersectionsParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ElementSet);
        assertEquals(OpType.INTERSECTION, ((ElementSet) result).getOperation());
    }

    @Test
    void testIntersectionElementsParser() throws IOException,
            ParserException {
        IntersectionElementsParser parser = new Parser(
                new ByteArrayInputStream("(1 .. 40)".getBytes())).new IntersectionElementsParser();

        Elements result = parser.parse();

        assertNotNull(result);

        parser = new Parser(new ByteArrayInputStream(
                "(1 .. 40) EXCEPT 5".getBytes())).new IntersectionElementsParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ElementSet);
        assertEquals(OpType.EXCLUDE, ((ElementSet) result).getOperation());
    }

    @Test
    void testExclusionsParser() throws IOException, ParserException {
        ExclusionsParser parser = new Parser(new ByteArrayInputStream(
                "EXCEPT 1".getBytes())).new ExclusionsParser();

        Elements result = parser.parse();

        assertNotNull(result);
    }

    @Test
    void testElementsParser() throws IOException, ParserException {
        ElementsParser parser = new Parser(new ByteArrayInputStream(
                "12 .. 15".getBytes())).new ElementsParser();

        Elements result = parser.parse();

        assertNotNull(result);

        parser = new Parser(new ByteArrayInputStream(
                "(ALL EXCEPT 1)".getBytes())).new ElementsParser();

        result = parser.parse();

        assertNotNull(result);
    }

    @Test
    void testObjectElementsParser() throws IOException, ParserException {
        Parser.ObjectElementsParser parser = new Parser(new ByteArrayInputStream(
                "{ &int-field 4711, &Type-Field VisibleString }".getBytes())).new ObjectElementsParser();

        Elements result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ObjectSetElements);
    }

    /**
     * ************************************************************************
     * X.680 51
     * ***********************************************************************
     */

    @Test
    void testSubtypeElementsParser() throws IOException, ParserException {
        // SingleValue
        SubtypeElementsParser parser = new Parser(new ByteArrayInputStream(
                "4711".getBytes())).new SubtypeElementsParser();

        Constraint result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SingleValueConstraint);

        // ContainedSubtype
        parser = new Parser(new ByteArrayInputStream(
                "INCLUDES VisibleString (SIZE (1..255))".getBytes())).new SubtypeElementsParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ContainedSubtype);

        // ValueRange
        parser = new Parser(new ByteArrayInputStream(
                "{5, 65} .. {7, 120}".getBytes())).new SubtypeElementsParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof RangeNode);

        // SizeConstraint
        parser = new Parser(new ByteArrayInputStream("SIZE (16)".getBytes())).new SubtypeElementsParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SizeConstraint);

        // TypeConstraint TODO: ambiguous, returns ASN1ContainedSubtype
        parser = new Parser(new ByteArrayInputStream("INTEGER".getBytes())).new SubtypeElementsParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ContainedSubtype);

        // PermittedAlphabet
        parser = new Parser(new ByteArrayInputStream(
                "FROM (\"a\"..\"z\")".getBytes())).new SubtypeElementsParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof PermittedAlphabetConstraint);

        // InnerTypeConstraints
        parser = new Parser(new ByteArrayInputStream(
                "WITH COMPONENTS { identifier-1 (SIZE (1..10)) }".getBytes())).new SubtypeElementsParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof MultipleTypeConstraints);

        // PatternConstraint
        parser = new Parser(new ByteArrayInputStream(
                "PATTERN aPattern".getBytes())).new SubtypeElementsParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof PatternConstraint);

        // PropertySettings
        parser = new Parser(new ByteArrayInputStream(
                "SETTINGS \"Midnight=Start\"".getBytes())).new SubtypeElementsParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof PropertySettingsConstraint);

        // TODO: DurationRange

        // TODO: TimePointRange

        // TODO: RecurrenceRange
    }

    @Test
    void testSingleValueParser() throws IOException, ParserException {
        SingleValueParser parser = new Parser(new ByteArrayInputStream(
                "4711".getBytes())).new SingleValueParser();

        SingleValueConstraint result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getValue() instanceof IntegerValue);

        parser = new Parser(new ByteArrayInputStream(
                "{a 1, b TRUE}".getBytes())).new SingleValueParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getValue() instanceof CollectionValue);
        assertEquals(2, ((CollectionValue) result.getValue()).getSize());
    }

    @Test
    void testContainedSubtypeParser() throws IOException,
            ParserException {
        ContainedSubtypeParser parser = new Parser(new ByteArrayInputStream(
                "INCLUDES INTEGER".getBytes())).new ContainedSubtypeParser();

        ContainedSubtype result = parser.parse();

        assertNotNull(result);
        assertTrue(result.hasIncludes());
        assertTrue(result.getType() instanceof IntegerType);

        parser = new Parser(new ByteArrayInputStream("INTEGER".getBytes())).new ContainedSubtypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertFalse(result.hasIncludes());
        assertTrue(result.getType() instanceof IntegerType);
    }

    @Test
    void testValueRangeParser() throws IOException, ParserException {
        ValueRangeParser parser = new Parser(new ByteArrayInputStream(
                "12 .. < MAX".getBytes())).new ValueRangeParser();

        RangeNode result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getLower().getValue() instanceof IntegerValue);
        assertTrue(result.getLower().isInclusive());
        assertEquals(Value.MAX, result.getUpper().getValue());
        assertFalse(result.getUpper().isInclusive());

        parser = new Parser(new ByteArrayInputStream("12 .. 15".getBytes())).new ValueRangeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getLower().getValue() instanceof IntegerValue);
        assertTrue(result.getLower().isInclusive());
        assertTrue(result.getUpper().getValue() instanceof IntegerValue);
        assertTrue(result.getUpper().isInclusive());

        parser = new Parser(
                new ByteArrayInputStream("MIN < .. < 15".getBytes())).new ValueRangeParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(Value.MIN, result.getLower().getValue());
        assertFalse(result.getLower().isInclusive());
        assertTrue(result.getUpper().getValue() instanceof IntegerValue);
        assertFalse(result.getUpper().isInclusive());

        parser = new Parser(new ByteArrayInputStream("MIN < .. 15".getBytes())).new ValueRangeParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(Value.MIN, result.getLower().getValue());
        assertFalse(result.getLower().isInclusive());
        assertTrue(result.getUpper().getValue() instanceof IntegerValue);
        assertTrue(result.getUpper().isInclusive());

        parser = new Parser(new ByteArrayInputStream("12 .. 15".getBytes())).new ValueRangeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getLower().getValue() instanceof IntegerValue);
        assertTrue(result.getLower().isInclusive());
        assertTrue(result.getUpper().getValue() instanceof IntegerValue);
        assertTrue(result.getUpper().isInclusive());
    }

    @Test
    void testLowerEndpointParser() throws IOException, ParserException {
        LowerEndpointParser parser = new Parser(new ByteArrayInputStream(
                "MIN".getBytes())).new LowerEndpointParser();

        LowerEndpointNode result = parser.parse();

        assertNotNull(result);
        assertEquals(Value.MIN, result.getValue());
        assertTrue(result.isInclusive());

        parser = new Parser(new ByteArrayInputStream("MIN <".getBytes())).new LowerEndpointParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(Value.MIN, result.getValue());
        assertFalse(result.isInclusive());
    }

    @Test
    void testUpperEndpointParser() throws IOException, ParserException {
        UpperEndpointParser parser = new Parser(new ByteArrayInputStream(
                "MAX".getBytes())).new UpperEndpointParser();

        UpperEndpointNode result = parser.parse();

        assertNotNull(result);
        assertEquals(Value.MAX, result.getValue());
        assertTrue(result.isInclusive());

        parser = new Parser(new ByteArrayInputStream("< MAX".getBytes())).new UpperEndpointParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(Value.MAX, result.getValue());
        assertFalse(result.isInclusive());
    }

    @Test
    void testLowerValueParser() throws IOException, ParserException {
        LowerEndValueParser parser = new Parser(new ByteArrayInputStream(
                "MIN".getBytes())).new LowerEndValueParser();

        Value result = parser.parse();

        assertNotNull(result);
        assertEquals(Value.MIN, result);
    }

    @Test
    void testUpperValueParser() throws IOException, ParserException {
        UpperEndValueParser parser = new Parser(new ByteArrayInputStream(
                "MAX".getBytes())).new UpperEndValueParser();

        Value result = parser.parse();

        assertNotNull(result);
        assertEquals(Value.MAX, result);
    }

    @Test
    void testSizeConstraintParser() throws IOException, ParserException {
        SizeConstraintParser parser = new Parser(new ByteArrayInputStream(
                "SIZE (0..5)".getBytes())).new SizeConstraintParser();

        SizeConstraint result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getConstraint());
    }

    @Test
    void testTypeConstraintParser() throws IOException, ParserException {
        TypeConstraintParser parser = new Parser(new ByteArrayInputStream(
                "INTEGER".getBytes())).new TypeConstraintParser();

        TypeConstraint result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getType() instanceof IntegerType);
    }

    @Test
    void testPermittedAlphabetParser() throws IOException,
            ParserException {
        PermittedAlphabetParser parser = new Parser(new ByteArrayInputStream(
                "FROM (\"a\"..\"z\")".getBytes())).new PermittedAlphabetParser();

        PermittedAlphabetConstraint result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getConstraint());
    }

    @Test
    void testInnerTypeConstraintsParser() throws IOException,
            ParserException {
        InnerTypeConstraintsParser parser = new Parser(
                new ByteArrayInputStream(
                        "WITH COMPONENTS { identifier-1 (SIZE (1..10)) }"
                                .getBytes())).new InnerTypeConstraintsParser();

        Constraint result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof MultipleTypeConstraints);

        parser = new Parser(new ByteArrayInputStream(
                "WITH COMPONENT (SIZE (23))".getBytes())).new InnerTypeConstraintsParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SingleTypeConstraint);
    }

    @Test
    void testMultipleTypeConstraintsParser() throws IOException,
            ParserException {
        MultipleTypeConstraintsParser parser = new Parser(
                new ByteArrayInputStream(
                        "{ identifier-1 (SIZE (1..10)) }".getBytes())).new MultipleTypeConstraintsParser();

        MultipleTypeConstraints result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getConstraints());
        assertEquals(1, result.getConstraints().size());
        assertFalse(result.isPartial());

        parser = new Parser(new ByteArrayInputStream(
                "{..., identifier-1 (SIZE (1..10)) }".getBytes())).new MultipleTypeConstraintsParser();

        result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getConstraints());
        assertEquals(1, result.getConstraints().size());
        assertTrue(result.isPartial());
    }

    @Test
    void testFullSpecificationParser() throws IOException,
            ParserException {
        FullSpecificationParser parser = new Parser(new ByteArrayInputStream(
                "{ identifier-1 (SIZE (1..10)) }".getBytes())).new FullSpecificationParser();

        MultipleTypeConstraints result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getConstraints());
        assertEquals(1, result.getConstraints().size());
        assertFalse(result.isPartial());
    }

    @Test
    void testPartialSpecificationParser() throws IOException,
            ParserException {
        PartialSpecificationParser parser = new Parser(
                new ByteArrayInputStream(
                        "{..., identifier-1 (SIZE (1..10)) }".getBytes())).new PartialSpecificationParser();

        MultipleTypeConstraints result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getConstraints());
        assertEquals(1, result.getConstraints().size());
        assertTrue(result.isPartial());
    }

    @Test
    void testTypeConstraintsParser() throws IOException, ParserException {
        TypeConstraintsParser parser = new Parser(new ByteArrayInputStream(
                "identifier-1 (4711 | 4712) OPTIONAL, identifier-2 (SIZE (1..10)) DEFAULT 5"
                        .getBytes())).new TypeConstraintsParser();

        List<NamedConstraint> result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("identifier-1", result.get(0).getName());
        assertNotNull(result.get(0).getConstraint());
        assertEquals("identifier-2", result.get(1).getName());
        assertNotNull(result.get(1).getConstraint());
    }

    @Test
    void testNamedConstraintParser() throws IOException, ParserException {
        NamedConstraintParser parser = new Parser(new ByteArrayInputStream(
                "identifier (4711 | 4712) OPTIONAL".getBytes())).new NamedConstraintParser();

        NamedConstraint result = parser.parse();

        assertNotNull(result);
        assertEquals("identifier", result.getName());
        assertNotNull(result.getConstraint());
    }

    @Test
    void testComponentConstraintParser() throws IOException,
            ParserException {
        ComponentConstraintParser parser = new Parser(new ByteArrayInputStream(
                "(4711 | 4712) OPTIONAL".getBytes())).new ComponentConstraintParser();

        ComponentConstraint result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getValue());
        assertEquals(PresenceConstraint.PresenceType.OPTIONAL, result.getPresence()
                .getType());
    }

    @Test
    void testValueConstraintParser() throws IOException, ParserException {
        ValueConstraintParser parser = new Parser(new ByteArrayInputStream(
                "(4711 | 4712)".getBytes())).new ValueConstraintParser();

        Constraint result = parser.parse();

        assertNotNull(result);
    }

    @Test
    void testPresenceConstraintParser() throws IOException,
            ParserException {
        PresenceConstraintParser parser = new Parser(new ByteArrayInputStream(
                "PRESENT".getBytes())).new PresenceConstraintParser();

        PresenceConstraint result = parser.parse();

        assertNotNull(result);
        assertEquals(PresenceConstraint.PresenceType.PRESENT, result.getType());

        parser = new Parser(new ByteArrayInputStream("ABSENT".getBytes())).new PresenceConstraintParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(PresenceConstraint.PresenceType.ABSENT, result.getType());

        parser = new Parser(new ByteArrayInputStream("OPTIONAL".getBytes())).new PresenceConstraintParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(PresenceConstraint.PresenceType.OPTIONAL, result.getType());
    }

    @Test
    void testPatternConstraintParser() throws IOException,
            ParserException {
        // TODO: check pattern
        PatternConstraintParser parser = new Parser(new ByteArrayInputStream(
                "PATTERN aPattern".getBytes())).new PatternConstraintParser();

        PatternConstraint result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getPattern() instanceof DefinedValue);
    }

    @Test
    void testPropertySettingsParser() throws IOException,
            ParserException {
        PropertySettingsParser parser = new Parser(new ByteArrayInputStream(
                "SETTINGS \"Midnight=Start\"".getBytes())).new PropertySettingsParser();

        PropertySettingsConstraint result = parser.parse();

        assertNotNull(result);
        assertEquals(1, result.getSettings().size());

        parser = new Parser(new ByteArrayInputStream(
                "SETTINGS \"Midnight=Start Date=YMD\"".getBytes())).new PropertySettingsParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.getSettings().size());
    }

    @Test
    void testPropertyAndSettingPairParser() throws IOException,
            ParserException {
        PropertyAndSettingPairParser parser = new Parser(
                new ByteArrayInputStream("Midnight=Start".getBytes())).new PropertyAndSettingPairParser();

        PropertyAndSettingNode result = parser.parse();

        assertNotNull(result);
        assertEquals("Midnight", result.getProperty());
        assertEquals("Start", result.getSetting());

        parser = new Parser(new ByteArrayInputStream("Time=HMF27".getBytes())).new PropertyAndSettingPairParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals("Time", result.getProperty());
        assertEquals("HMF27", result.getSetting());

        parser = new Parser(new ByteArrayInputStream("Time=HF0".getBytes())).new PropertyAndSettingPairParser();

        result = parser.parse();

        assertNull(result);
    }

    @Test
    void testDurationRangeParser() throws IOException, ParserException {
        // TODO: implement
        DurationRangeParser parser = new Parser(new ByteArrayInputStream(
                "\"PT2M0.000S\"..\"PT2M59.000S\"".getBytes())).new DurationRangeParser();

        RangeNode result = parser.parse();

        assertNotNull(result);
    }

    @Test
    void testTimePointRangeParser() {
        // TODO: implement
    }

    @Test
    void testRecurrenceRangeParser() {
        // TODO: implement
    }

    /**
     * ************************************************************************
     * X.680 53
     * ***********************************************************************
     */

    @Test
    void testExceptionSpecParser() throws IOException, ParserException {
        ExceptionSpecParser parser = new Parser(new ByteArrayInputStream(
                "! 23".getBytes())).new ExceptionSpecParser();

        ExceptionIdentificationNode result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getValue() instanceof SignedNumber);
    }

    @Test
    void testExceptionIdentificationParser() throws IOException,
            ParserException {
        ExceptionIdentificationParser parser = new Parser(
                new ByteArrayInputStream("-4711".getBytes())).new ExceptionIdentificationParser();

        ExceptionIdentificationNode result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getValue() instanceof SignedNumber);
        assertNull(result.getType());

        parser = new Parser(new ByteArrayInputStream(
                "Module.valueref".getBytes())).new ExceptionIdentificationParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getValue() instanceof SimpleDefinedValue);
        assertNull(result.getType());

        parser = new Parser(new ByteArrayInputStream(
                "VisibleString: \"test\"".getBytes())).new ExceptionIdentificationParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getType() instanceof VisibleString);
        testAmbiguousValue(result.getValue(), StringValue.class);
    }

    /**
     * ************************************************************************
     * X.680 54
     * ***********************************************************************
     */

    @Test
    void testEncodingControlSectionsParser() throws IOException,
            ParserException {
        EncodingControlSectionsParser parser = new Parser(
                new ByteArrayInputStream(
                        "ENCODING-CONTROL XER ENCODING-CONTROL XER [NULL]"
                                .getBytes())).new EncodingControlSectionsParser();

        List<EncodingControlSectionNode> result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testEncodingControlSectionParser() throws IOException,
            ParserException {
        EncodingControlSectionParser parser = new Parser(
                new ByteArrayInputStream("ENCODING-CONTROL PER".getBytes())).new EncodingControlSectionParser();

        EncodingControlSectionNode result = parser.parse();

        assertNotNull(result);
        assertEquals("PER", result.getEncodingReference());
        assertEquals(0, result.getEncodingInstruction().size());

        parser = new Parser(new ByteArrayInputStream(
                "ENCODING-CONTROL PER [NULL] IA5String [SIZE 8] Body"
                        .getBytes())).new EncodingControlSectionParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals("PER", result.getEncodingReference());
        assertEquals(9, result.getEncodingInstruction().size());
    }

    /**
     * ************************************************************************
     * X.681 8
     * ***********************************************************************
     */

    @Test
    void testDefinedObjectClassParser() throws IOException,
            ParserException {
        DefinedObjectClassParser parser = new Parser(new ByteArrayInputStream(
                "OBJECT-CLASS".getBytes())).new DefinedObjectClassParser();

        ObjectClassReference result = parser.parse();

        assertNotNull(result);
        assertEquals("OBJECT-CLASS", result.getReference());

        parser = new Parser(new ByteArrayInputStream(
                "Module.OBJECT-CLASS".getBytes())).new DefinedObjectClassParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ExternalObjectClassReference);
        assertEquals("Module",
                ((ExternalObjectClassReference) result).getModule());
        assertEquals("OBJECT-CLASS", result.getReference());

        parser = new Parser(new ByteArrayInputStream(
                "TYPE-IDENTIFIER".getBytes())).new DefinedObjectClassParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof TypeIdentifierObjectClassReferenceNode);
    }

    @Test
    void testDefinedObjectParser() throws IOException, ParserException {
        DefinedObjectParser parser = new Parser(new ByteArrayInputStream(
                "object".getBytes())).new DefinedObjectParser();

        ObjectReference result = parser.parse();

        assertNotNull(result);
        assertEquals("object", result.getReference());

        parser = new Parser(
                new ByteArrayInputStream("Module.object".getBytes())).new DefinedObjectParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ExternalObjectReferenceNode);
        assertEquals("Module",
                ((ExternalObjectReferenceNode) result).getModule());
        assertEquals("object", result.getReference());
    }

    @Test
    void testDefinedObjectSetParser() throws IOException,
            ParserException {
        DefinedObjectSetParser parser = new Parser(new ByteArrayInputStream(
                "Object-Set".getBytes())).new DefinedObjectSetParser();

        ObjectSetReference result = parser.parse();

        assertNotNull(result);
        assertEquals("Object-Set", result.getReference());

        parser = new Parser(new ByteArrayInputStream(
                "Module.Object-Set".getBytes())).new DefinedObjectSetParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ExternalObjectSetReference);
        assertEquals("Module",
                ((ExternalObjectSetReference) result).getModule());
        assertEquals("Object-Set", result.getReference());
    }

    @Test
    void testExternalObjectClassReferenceParser() throws IOException,
            ParserException {
        ExternalObjectClassReferenceParser parser = new Parser(
                new ByteArrayInputStream("Module.OBJECT-CLASS".getBytes())).new ExternalObjectClassReferenceParser();

        ExternalObjectClassReference result = parser.parse();

        assertNotNull(result);
        assertEquals("Module", result.getModule());
        assertEquals("OBJECT-CLASS", result.getReference());
    }

    @Test
    void testExternalObjectReferenceParser() throws IOException,
            ParserException {
        ExternalObjectReferenceParser parser = new Parser(
                new ByteArrayInputStream("Module.object".getBytes())).new ExternalObjectReferenceParser();

        ExternalObjectReferenceNode result = parser.parse();

        assertNotNull(result);
        assertEquals("Module", result.getModule());
        assertEquals("object", result.getReference());
    }

    @Test
    void testExternalObjectSetReferenceParser() throws IOException,
            ParserException {
        ExternalObjectSetReferenceParser parser = new Parser(
                new ByteArrayInputStream("Module.ObjectSet".getBytes())).new ExternalObjectSetReferenceParser();

        ExternalObjectSetReference result = parser.parse();

        assertNotNull(result);
        assertEquals("Module", result.getModule());
        assertEquals("ObjectSet", result.getReference());
    }

    @Test
    void testUsefulObjectClassReferenceParser() throws IOException,
            ParserException {
        UsefulObjectClassReferenceParser parser = new Parser(
                new ByteArrayInputStream("TYPE-IDENTIFIER".getBytes())).new UsefulObjectClassReferenceParser();

        ObjectClassReference result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof TypeIdentifierObjectClassReferenceNode);

        parser = new Parser(new ByteArrayInputStream(
                "ABSTRACT-SYNTAX".getBytes())).new UsefulObjectClassReferenceParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof AbstractSyntaxObjectClassReferenceNode);
    }

    /**
     * ************************************************************************
     * X.681 9
     * ***********************************************************************
     */

    @Test
    void testObjectClassAssignmentParser() throws IOException, ParserException {
        TypeAssignmentParser parser = new Parser(
                new ByteArrayInputStream(
                        "OBJECT-CLASS ::= CLASS { &Type-Reference OPTIONAL } WITH SYNTAX { [ARGUMENT &ArgumentType] }"
                                .getBytes())).new TypeAssignmentParser();

        TypeOrObjectClassAssignmentNode result = parser.parse();

        assertNotNull(result);
        assertEquals("OBJECT-CLASS", result.getReference());
        assertTrue(result.getObjectClassAssignment().isPresent());
        assertTrue(result.getObjectClassAssignment().get().getObjectClass() instanceof ObjectClassDefn);
    }

    @Test
    void testObjectClassParser() throws IOException, ParserException {
        ObjectClassParser parser = new Parser(new ByteArrayInputStream(
                "CLASS { &Type-Reference OPTIONAL } WITH SYNTAX { [ARGUMENT &ArgumentType] }"
                        .getBytes())).new ObjectClassParser();

        ObjectClassNode result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ObjectClassDefn);

        parser = new Parser(new ByteArrayInputStream("OBJECT-CLASS".getBytes())).new ObjectClassParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ObjectClassReference);

        parser = new Parser(new ByteArrayInputStream(
                "OBJECT-CLASS {VisibleString}".getBytes())).new ObjectClassParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ObjectClassReference);
        assertNotNull(((ObjectClassReference) result).getParameters());
        assertTrue(((ObjectClassReference) result).getParameters().isPresent());
        assertEquals(1, ((ObjectClassReference) result).getParameters().get().size());
    }

    @Test
    void testObjectClassDefnParser() throws IOException, ParserException {
        ObjectClassDefnParser parser = new Parser(new ByteArrayInputStream(
                "CLASS { &Type-Reference OPTIONAL } WITH SYNTAX { [ARGUMENT &ArgumentType] }"
                        .getBytes())).new ObjectClassDefnParser();

        ObjectClassDefn result = parser.parse();

        assertNotNull(result);
        assertEquals(1, result.getFieldSpec().size());
        assertEquals(1, result.getSyntaxSpec().size());
    }

    @Test
    void testWithSyntaxSpecParser() throws IOException, ParserException {
        WithSyntaxSpecParser parser = new Parser(new ByteArrayInputStream(
                "WITH SYNTAX { [ARGUMENT &ArgumentType] }".getBytes())).new WithSyntaxSpecParser();

        List<TokenOrGroup> result = parser.parse();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testFieldSpecParser() throws IOException, ParserException {
        // TypeFieldSpec
        FieldSpecParser parser = new Parser(new ByteArrayInputStream(
                "&Type-Reference OPTIONAL".getBytes())).new FieldSpecParser();

        AbstractFieldSpecNode result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof TypeFieldSpecNode);
        assertNotNull(result.getOptionalitySpec());
        assertTrue(result.getOptionalitySpec() instanceof OptionalSpecNode);

        // FixedTypeValueFieldSpec
        parser = new Parser(new ByteArrayInputStream(
                "&value-reference INTEGER OPTIONAL".getBytes())).new FieldSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof FixedTypeValueOrObjectFieldSpecNode);
        assertTrue(((FixedTypeValueOrObjectFieldSpecNode) result).getFixedTypeValueFieldSpec().isPresent());
        assertTrue(((FixedTypeValueOrObjectFieldSpecNode) result).getFixedTypeValueFieldSpec().get()
                .getOptionalitySpec() instanceof OptionalSpecNode);

        // VariableTypeValueFieldSpec
        parser = new Parser(new ByteArrayInputStream(
                "&value-reference &ObjectSet-Reference OPTIONAL".getBytes())).new FieldSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof VariableTypeValueFieldSpecNode);

        // FixedTypeValueSetFieldSpec
        parser = new Parser(new ByteArrayInputStream(
                "&ValueSet-Reference INTEGER OPTIONAL".getBytes())).new FieldSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof FixedTypeValueSetOrObjectSetFieldSpecNode);
        assertTrue(((FixedTypeValueSetOrObjectSetFieldSpecNode) result).getFixedTypeValueSetFieldSpec().isPresent());
        assertTrue(((FixedTypeValueSetOrObjectSetFieldSpecNode) result).getFixedTypeValueSetFieldSpec().get()
                .getOptionalitySpec() instanceof OptionalSpecNode);

        // VariableTypeValueSetFieldSpec
        parser = new Parser(new ByteArrayInputStream(
                "&ValueSet-Field &Type-Reference1 OPTIONAL".getBytes())).new FieldSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof VariableTypeValueSetFieldSpecNode);

        // ObjectFieldSpec
        parser = new Parser(new ByteArrayInputStream(
                "&object-field ABSTRACT-SYNTAX".getBytes())).new FieldSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(((FixedTypeValueOrObjectFieldSpecNode) result).getObjectFieldSpec().isPresent());

        // ObjectSetFieldSpec
        parser = new Parser(new ByteArrayInputStream(
                "&ObjectSet-Field Module.OBJECT-CLASS DEFAULT {(Object1 | Object2)}"
                        .getBytes())).new FieldSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof FixedTypeValueSetOrObjectSetFieldSpecNode);
        assertTrue(((FixedTypeValueSetOrObjectSetFieldSpecNode) result).getObjectSetFieldSpec().isPresent());
        assertTrue(((FixedTypeValueSetOrObjectSetFieldSpecNode) result).getObjectSetFieldSpec().get()
                .getOptionalitySpec() instanceof DefaultObjectSetSpecNode);
    }

    @Test
    void testTypeFieldSpecParser() throws IOException, ParserException {
        TypeFieldSpecParser parser = new Parser(new ByteArrayInputStream(
                "&Type-Reference".getBytes())).new TypeFieldSpecParser();

        TypeFieldSpecNode result = parser.parse();

        assertNotNull(result);
        assertEquals("Type-Reference", result.getReference());
        assertNull(result.getOptionalitySpec());

        parser = new Parser(new ByteArrayInputStream(
                "&Type-Reference DEFAULT INTEGER".getBytes())).new TypeFieldSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals("Type-Reference", result.getReference());
        assertNotNull(result.getOptionalitySpec());

        parser = new Parser(new ByteArrayInputStream(
                "&Type-Reference OPTIONAL".getBytes())).new TypeFieldSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals("Type-Reference", result.getReference());
        assertNotNull(result.getOptionalitySpec());
    }

    @Test
    void testTypeOptionalitySpecParser() throws IOException,
            ParserException {
        TypeOptionalitySpecParser parser = new Parser(new ByteArrayInputStream(
                "OPTIONAL".getBytes())).new TypeOptionalitySpecParser();

        OptionalitySpecNode result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof OptionalSpecNode);

        parser = new Parser(new ByteArrayInputStream(
                "DEFAULT INTEGER".getBytes())).new TypeOptionalitySpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof DefaultTypeSpecNode);
        assertTrue(((DefaultTypeSpecNode) result).getSpec() instanceof IntegerType);
    }

    @Test
    void testFixedTypeValueFieldSpecParser() throws IOException,
            ParserException {
        FixedTypeValueOrObjectFieldSpecParser parser = new Parser(
                new ByteArrayInputStream(
                        "&value-reference INTEGER UNIQUE OPTIONAL".getBytes())).new FixedTypeValueOrObjectFieldSpecParser();

        FixedTypeValueOrObjectFieldSpecNode result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getFixedTypeValueFieldSpec().isPresent());

        FixedTypeValueFieldSpecNode fixedTypeValue = result.getFixedTypeValueFieldSpec().get();

        assertEquals("value-reference", fixedTypeValue.getReference());
        assertTrue(fixedTypeValue.getType() instanceof IntegerType);
        assertTrue(fixedTypeValue.isUnique());
        assertNotNull(fixedTypeValue.getOptionalitySpec());
        assertTrue(fixedTypeValue.getOptionalitySpec() instanceof OptionalSpecNode);

        parser = new Parser(new ByteArrayInputStream(
                "&value-reference INTEGER UNIQUE".getBytes())).new FixedTypeValueOrObjectFieldSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getFixedTypeValueFieldSpec().isPresent());

        fixedTypeValue = result.getFixedTypeValueFieldSpec().get();

        assertEquals("value-reference", fixedTypeValue.getReference());
        assertTrue(fixedTypeValue.getType() instanceof IntegerType);
        assertTrue(fixedTypeValue.isUnique());
        assertNull(fixedTypeValue.getOptionalitySpec());

        parser = new Parser(new ByteArrayInputStream(
                "&value-reference INTEGER DEFAULT 10".getBytes())).new FixedTypeValueOrObjectFieldSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getFixedTypeValueFieldSpec().isPresent());

        fixedTypeValue = result.getFixedTypeValueFieldSpec().get();

        assertEquals("value-reference", fixedTypeValue.getReference());
        assertTrue(fixedTypeValue.getType() instanceof IntegerType);
        assertFalse(fixedTypeValue.isUnique());
        assertNotNull(fixedTypeValue.getOptionalitySpec());
        assertTrue(fixedTypeValue.getOptionalitySpec() instanceof DefaultValueSpecNode);

        parser = new Parser(new ByteArrayInputStream(
                "&value-reference INTEGER OPTIONAL".getBytes())).new FixedTypeValueOrObjectFieldSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getFixedTypeValueFieldSpec().isPresent());

        fixedTypeValue = result.getFixedTypeValueFieldSpec().get();

        assertEquals("value-reference", fixedTypeValue.getReference());
        assertTrue(fixedTypeValue.getType() instanceof IntegerType);
        assertFalse(fixedTypeValue.isUnique());
        assertNotNull(fixedTypeValue.getOptionalitySpec());
        assertTrue(fixedTypeValue.getOptionalitySpec() instanceof OptionalSpecNode);

        parser = new Parser(new ByteArrayInputStream(
                "&value-reference INTEGER".getBytes())).new FixedTypeValueOrObjectFieldSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getFixedTypeValueFieldSpec().isPresent());

        fixedTypeValue = result.getFixedTypeValueFieldSpec().get();

        assertEquals("value-reference", fixedTypeValue.getReference());
        assertTrue(fixedTypeValue.getType() instanceof IntegerType);
        assertFalse(fixedTypeValue.isUnique());
        assertNull(fixedTypeValue.getOptionalitySpec());
    }

    @Test
    void testValueOptionalitySpecParser() throws IOException,
            ParserException {
        ValueOptionalitySpecParser parser = new Parser(
                new ByteArrayInputStream("OPTIONAL".getBytes())).new ValueOptionalitySpecParser();

        OptionalitySpecNode result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof OptionalSpecNode);

        parser = new Parser(new ByteArrayInputStream("DEFAULT 10".getBytes())).new ValueOptionalitySpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof DefaultValueSpecNode);
        assertTrue(((DefaultValueSpecNode) result).getSpec() instanceof IntegerValue);
    }

    @Test
    void testVariableTypeValueFieldSpecParser() throws IOException,
            ParserException {
        VariableTypeValueFieldSpecParser parser = new Parser(
                new ByteArrayInputStream(
                        "&value-reference &TypeReference OPTIONAL".getBytes())).new VariableTypeValueFieldSpecParser();

        VariableTypeValueFieldSpecNode result = parser.parse();

        assertNotNull(result);
        assertEquals("value-reference", result.getReference());
        assertTrue(result.getFieldName() instanceof FieldNameNode);
        assertNotNull(result.getOptionalitySpec());

        parser = new Parser(new ByteArrayInputStream(
                "&value-reference &TypeReference DEFAULT 23".getBytes())).new VariableTypeValueFieldSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals("value-reference", result.getReference());
        assertTrue(result.getFieldName() instanceof FieldNameNode);
        assertNotNull(result.getOptionalitySpec());
        assertTrue(result.getOptionalitySpec() instanceof DefaultValueSpecNode);

        parser = new Parser(new ByteArrayInputStream(
                "&value-reference &TypeReference".getBytes())).new VariableTypeValueFieldSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals("value-reference", result.getReference());
        assertTrue(result.getFieldName() instanceof FieldNameNode);
        assertNull(result.getOptionalitySpec());
    }

    @Test
    void testFixedTypeValueSetFieldSpecParser() throws IOException,
            ParserException {
        FixedTypeValueSetOrObjectSetFieldSpecParser parser = new Parser(new ByteArrayInputStream(
                "&ValueSet-Reference INTEGER OPTIONAL".getBytes())).new FixedTypeValueSetOrObjectSetFieldSpecParser();

        FixedTypeValueSetOrObjectSetFieldSpecNode result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getFixedTypeValueSetFieldSpec().isPresent());

        FixedTypeValueSetFieldSpecNode fixedTypeValueSetFieldSpecNode = result.getFixedTypeValueSetFieldSpec().get();

        assertEquals("ValueSet-Reference", fixedTypeValueSetFieldSpecNode.getReference());
        assertTrue(fixedTypeValueSetFieldSpecNode.getType() instanceof IntegerType);
        assertTrue(fixedTypeValueSetFieldSpecNode.getOptionalitySpec() instanceof OptionalSpecNode);

        parser = new Parser(new ByteArrayInputStream(
                "&ValueSet-Reference INTEGER DEFAULT {4711}".getBytes())).new FixedTypeValueSetOrObjectSetFieldSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getFixedTypeValueSetFieldSpec().isPresent());

        fixedTypeValueSetFieldSpecNode = result.getFixedTypeValueSetFieldSpec().get();

        assertEquals("ValueSet-Reference", fixedTypeValueSetFieldSpecNode.getReference());
        assertTrue(fixedTypeValueSetFieldSpecNode.getType() instanceof IntegerType);
        assertTrue(fixedTypeValueSetFieldSpecNode.getOptionalitySpec() instanceof DefaultValueSetSpecNode);
        assertNotNull((fixedTypeValueSetFieldSpecNode.getOptionalitySpec()));
    }

    @Test
    void testValueSetOptionalitySpecParser() throws IOException,
            ParserException {
        ValueSetOptionalitySpecParser parser = new Parser(
                new ByteArrayInputStream("OPTIONAL".getBytes())).new ValueSetOptionalitySpecParser();

        OptionalitySpecNode result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof OptionalSpecNode);

        parser = new Parser(new ByteArrayInputStream(
                "DEFAULT { (1..10) }".getBytes())).new ValueSetOptionalitySpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof DefaultValueSetSpecNode);
        assertTrue(((DefaultValueSetSpecNode) result).getSpec() instanceof SetSpecsNode);
    }

    @Test
    void testVariableTypeValueSetFieldSpecParser() throws IOException,
            ParserException {
        VariableTypeValueSetFieldSpecParser parser = new Parser(
                new ByteArrayInputStream(
                        "&ValueSet-Field &Type-Reference1.&Type-Reference2 OPTIONAL"
                                .getBytes())).new VariableTypeValueSetFieldSpecParser();

        VariableTypeValueSetFieldSpecNode result = parser.parse();

        assertNotNull(result);
        assertEquals("ValueSet-Field", result.getReference());
        assertEquals(2, result.getFieldName().getPrimitiveFieldNames().size());
        assertEquals("Type-Reference1", result.getFieldName()
                .getPrimitiveFieldNames().get(0).getReference());
        assertTrue(result.getOptionalitySpec() instanceof OptionalSpecNode);

        parser = new Parser(new ByteArrayInputStream(
                "&ValueSet-Field &Type-Reference1.&Type-Reference2 DEFAULT {(23 | 42)}"
                        .getBytes())).new VariableTypeValueSetFieldSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals("ValueSet-Field", result.getReference());
        assertEquals(2, result.getFieldName().getPrimitiveFieldNames().size());
        assertEquals("Type-Reference1", result.getFieldName()
                .getPrimitiveFieldNames().get(0).getReference());
        assertTrue(result.getOptionalitySpec() instanceof DefaultValueSetSpecNode);
    }

    @Test
    void testObjectFieldSpecParser() throws IOException, ParserException {
        FixedTypeValueOrObjectFieldSpecParser parser = new Parser(new ByteArrayInputStream(
                "&object-field OBJECT-CLASS OPTIONAL".getBytes())).new FixedTypeValueOrObjectFieldSpecParser();

        FixedTypeValueOrObjectFieldSpecNode result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof FixedTypeValueOrObjectFieldSpecNode);
        assertTrue(result.getObjectFieldSpec().isPresent());

        ObjectFieldSpecNode objectField = result.getObjectFieldSpec().get();

        assertEquals("object-field", objectField.getReference());
        assertEquals("OBJECT-CLASS", objectField.getObjectClassReference().getReference());
        assertTrue(objectField.getOptionalitySpec() instanceof OptionalSpecNode);

        parser = new Parser(new ByteArrayInputStream(
                "&object-field OBJECT-CLASS DEFAULT object-ref".getBytes())).new FixedTypeValueOrObjectFieldSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof FixedTypeValueOrObjectFieldSpecNode);
        assertTrue(result.getObjectFieldSpec().isPresent());

        objectField = result.getObjectFieldSpec().get();

        assertEquals("object-field", objectField.getReference());
        assertEquals("OBJECT-CLASS", objectField.getObjectClassReference().getReference());
        assertTrue(objectField.getOptionalitySpec() instanceof DefaultObjectSpecNode);
    }

    @Test
    void testObjectOptionalitySpecParser() throws IOException, ParserException {
        Parser.ObjectOptionalitySpecParser parser = new Parser(new ByteArrayInputStream(
                "OPTIONAL".getBytes())).new ObjectOptionalitySpecParser();

        OptionalitySpecNode result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof OptionalSpecNode);

        parser = new Parser(new ByteArrayInputStream(
                "DEFAULT object-ref".getBytes())).new ObjectOptionalitySpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof DefaultObjectSpecNode);
        assertTrue(((DefaultObjectSpecNode) result).getSpec() instanceof ObjectReference);
    }

    @Test
    void testObjectSetFieldSpecParser() throws IOException, ParserException {
        FixedTypeValueSetOrObjectSetFieldSpecParser parser = new Parser(new ByteArrayInputStream(
                "&ObjectSet-Field OBJECT-CLASS OPTIONAL".getBytes())).new FixedTypeValueSetOrObjectSetFieldSpecParser();

        FixedTypeValueSetOrObjectSetFieldSpecNode result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getObjectSetFieldSpec().isPresent());

        ObjectSetFieldSpecNode objectSetFieldSpecNode = result.getObjectSetFieldSpec().get();

        assertEquals("ObjectSet-Field", objectSetFieldSpecNode.getReference());
        assertTrue(objectSetFieldSpecNode.getObjectClassReference() instanceof ObjectClassReference);
        assertEquals("OBJECT-CLASS", objectSetFieldSpecNode.getObjectClassReference().getReference());
        assertTrue(objectSetFieldSpecNode.getOptionalitySpec() instanceof OptionalSpecNode);

        parser = new Parser(new ByteArrayInputStream(
                "&ObjectSet-Field OBJECT-CLASS DEFAULT {Object1}".getBytes())).new FixedTypeValueSetOrObjectSetFieldSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getObjectSetFieldSpec().isPresent());

        objectSetFieldSpecNode = result.getObjectSetFieldSpec().get();

        assertEquals("ObjectSet-Field", objectSetFieldSpecNode.getReference());
        assertTrue(objectSetFieldSpecNode.getObjectClassReference() instanceof ObjectClassReference);
        assertEquals("OBJECT-CLASS", objectSetFieldSpecNode.getObjectClassReference().getReference());
        assertTrue(objectSetFieldSpecNode.getOptionalitySpec() instanceof DefaultObjectSetSpecNode);
        assertNotNull(objectSetFieldSpecNode.getOptionalitySpec());
    }

    @Test
    void testObjectSetOptionalitySpecParser() throws IOException,
            ParserException {
        ObjectSetOptionalitySpecParser parser = new Parser(
                new ByteArrayInputStream("OPTIONAL".getBytes())).new ObjectSetOptionalitySpecParser();

        OptionalitySpecNode result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof OptionalSpecNode);

        parser = new Parser(new ByteArrayInputStream(
                "DEFAULT { (Object1 | Object2) }".getBytes())).new ObjectSetOptionalitySpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof DefaultObjectSetSpecNode);
    }

    @Test
    void testPrimitiveFieldNameParser() throws IOException,
            ParserException {
        PrimitiveFieldNameParser parser = new Parser(new ByteArrayInputStream(
                "&Type-Reference".getBytes())).new PrimitiveFieldNameParser();

        PrimitiveFieldNameNode result = parser.parse();

        assertNotNull(result);
        assertTrue(result.isTypeFieldReference());

        parser = new Parser(new ByteArrayInputStream("&value-field".getBytes())).new PrimitiveFieldNameParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result.isValueFieldReference());

        parser = new Parser(new ByteArrayInputStream(
                "&Value-Set-Reference".getBytes())).new PrimitiveFieldNameParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result.isValueSetFieldReference());

        parser = new Parser(new ByteArrayInputStream(
                "&object-Reference".getBytes())).new PrimitiveFieldNameParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result.isObjectFieldReference());

        parser = new Parser(new ByteArrayInputStream(
                "&ObjectSet-Reference".getBytes())).new PrimitiveFieldNameParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result.isObjectSetFieldReference());
    }

    @Test
    void testFieldNameParser() throws IOException, ParserException {
        FieldNameParser parser = new Parser(new ByteArrayInputStream(
                "&Type-Reference.&ObjectSet-Reference".getBytes())).new FieldNameParser();

        FieldNameNode result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.getPrimitiveFieldNames().size());
    }

    /**
     * ************************************************************************
     * X.681 10
     * ***********************************************************************
     */

    @Test
    void testSyntaxListParser() throws IOException, ParserException {
        SyntaxListParser parser = new Parser(new ByteArrayInputStream(
                "{ [RETURN RESULT &resultReturned] [ARGUMENT &ArgumentType] }"
                        .getBytes())).new SyntaxListParser();

        List<TokenOrGroup> result = parser.parse();

        assertNotNull(result);

        assertEquals(2, result.size());
        assertTrue(result.get(1) instanceof Group);
        assertEquals(2, ((Group) result.get(1)).getGroup().size());
        assertTrue(((Group) result.get(1)).getGroup().get(0) instanceof RequiredToken);
        assertTrue(((Group) result.get(1)).getGroup().get(1) instanceof RequiredToken);
    }

    @Test
    void testTokenOrGroupSpecParser() throws IOException,
            ParserException {
        TokenOrGroupSpecParser parser = new Parser(new ByteArrayInputStream(
                "A-WORD".getBytes())).new TokenOrGroupSpecParser();

        TokenOrGroup result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof RequiredToken);
        assertTrue(((RequiredToken) result).getToken() instanceof LiteralNode);
        assertEquals("A-WORD",
                ((LiteralNode) ((RequiredToken) result).getToken()).getText());

        parser = new Parser(new ByteArrayInputStream(
                "[ARGUMENT &ArgumentType]".getBytes())).new TokenOrGroupSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof Group);
        assertEquals(2, ((Group) result).getGroup().size());
        assertTrue(((Group) result).getGroup().get(0) instanceof RequiredToken);
        assertTrue(((RequiredToken) ((Group) result).getGroup().get(0))
                .getToken() instanceof LiteralNode);
        assertTrue(((Group) result).getGroup().get(1) instanceof RequiredToken);
        assertTrue(((RequiredToken) ((Group) result).getGroup().get(1))
                .getToken() instanceof PrimitiveFieldNameNode);

        parser = new Parser(new ByteArrayInputStream(
                "[LITERAL [A &field] [B &field]]".getBytes())).new TokenOrGroupSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof Group);
        assertEquals(3, ((Group) result).getGroup().size());
        assertTrue(((Group) result).getGroup().get(0) instanceof RequiredToken);
        assertTrue(((RequiredToken) ((Group) result).getGroup().get(0))
                .getToken() instanceof LiteralNode);
        assertTrue(((Group) result).getGroup().get(1) instanceof Group);
        assertEquals(2, ((Group) ((Group) result).getGroup().get(1)).getGroup()
                .size());
        assertTrue(((Group) result).getGroup().get(2) instanceof Group);
        assertEquals(2, ((Group) ((Group) result).getGroup().get(2)).getGroup()
                .size());
    }

    @Test
    void testOptionalGroupParser() throws IOException, ParserException {
        OptionalGroupParser parser = new Parser(new ByteArrayInputStream(
                "[ARGUMENT &ArgumentType]".getBytes())).new OptionalGroupParser();

        Group result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.getGroup().size());
        assertTrue(result.getGroup().get(0) instanceof RequiredToken);
        assertTrue(((RequiredToken) result.getGroup().get(0)).getToken() instanceof LiteralNode);
        assertEquals("ARGUMENT", ((LiteralNode) ((RequiredToken) result
                .getGroup().get(0)).getToken()).getText());
        assertTrue(result.getGroup().get(1) instanceof RequiredToken);
        assertTrue(((RequiredToken) result.getGroup().get(1)).getToken() instanceof PrimitiveFieldNameNode);
        assertEquals("ArgumentType",
                ((PrimitiveFieldNameNode) ((RequiredToken) result.getGroup()
                        .get(1)).getToken()).getReference());
    }

    @Test
    void testRequiredTokenParser() throws IOException, ParserException {
        RequiredTokenParser parser = new Parser(new ByteArrayInputStream(
                "&ObjectSet-Reference".getBytes())).new RequiredTokenParser();

        RequiredToken result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getToken() instanceof PrimitiveFieldNameNode);
        assertTrue(((PrimitiveFieldNameNode) result.getToken())
                .isObjectSetFieldReference());

        parser = new Parser(new ByteArrayInputStream(",".getBytes())).new RequiredTokenParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getToken() instanceof LiteralNode);
        assertEquals(",", ((LiteralNode) result.getToken()).getText());
    }

    @Test
    void testLiteralDefinitionParser() throws IOException, ParserException {
        assertEquals(",", getLiteralDefinition(","));
        assertEquals("A-WORD", getLiteralDefinition("A-WORD"));
    }

    private String getLiteralDefinition(String literal) throws ParserException, IOException {
        LiteralDefinitionParser parser = new Parser(new ByteArrayInputStream(literal.getBytes())).new LiteralDefinitionParser();

        LiteralNode result = parser.parse();

        if (result != null) {
            return result.getText();
        }

        return null;
    }

    @Test
    void testLiteralParser() throws IOException, ParserException {
        assertEquals(",", getLiteral(","));
        assertEquals("A-WORD", getLiteral("A-WORD"));

        assertNull(getLiteral("BIT"));
        assertNull(getLiteral("BOOLEAN"));
        assertNull(getLiteral("CHARACTER"));
        assertNull(getLiteral("CHOICE"));
        assertNull(getLiteral("DATE"));
        assertNull(getLiteral("DATE-TIME"));
        assertNull(getLiteral("DURATION"));
        assertNull(getLiteral("EMBEDDED"));
        assertNull(getLiteral("END"));
        assertNull(getLiteral("ENUMERATED"));
        assertNull(getLiteral("EXTERNAL"));
        assertNull(getLiteral("FALSE"));
        assertNull(getLiteral("INSTANCE"));
        assertNull(getLiteral("INTEGER"));
        assertNull(getLiteral("INTERSECTION"));
        assertNull(getLiteral("MINUS-INFINITY"));
        assertNull(getLiteral("NULL"));
        assertNull(getLiteral("OBJECT"));
        assertNull(getLiteral("OCTET"));
        assertNull(getLiteral("PLUS-INFINITY"));
        assertNull(getLiteral("REAL"));
        assertNull(getLiteral("RELATIVE-OID"));
        assertNull(getLiteral("SEQUENCE"));
        assertNull(getLiteral("SET"));
        assertNull(getLiteral("TIME"));
        assertNull(getLiteral("TIME-OF-DAY"));
        assertNull(getLiteral("TRUE"));
        assertNull(getLiteral("UNION"));
    }

    private String getLiteral(String literal) throws IOException, ParserException {
        LiteralParser parser = new Parser(new ByteArrayInputStream(literal.getBytes())).new LiteralParser();

        LiteralNode result = parser.parse();

        if (result != null) {
            return result.getText();
        }

        return null;
    }


    /**
     * ************************************************************************
     * X.681 11
     * ***********************************************************************
     */

    @Test
    void testObjectAssignmentParser() throws IOException,
            ParserException {
        ValueAssignmentParser parser = new Parser(
                new ByteArrayInputStream(
                        "invertMatrix OPERATION ::= { &ArgumentType Matrix, &ResultType Matrix, &Errors {determinantIsZero}, &operationCode 7 }"
                                .getBytes())).new ValueAssignmentParser();

        ValueOrObjectAssignmentNode result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getObjectAssignment().isPresent());

        ObjectAssignmentNode assignment = result.getObjectAssignment().get();

        assertEquals("invertMatrix", result.getReference());
        assertEquals("OPERATION", assignment.getObjectClassReference().getReference());
        assertNotNull(assignment.getObject());
    }

    @Test
    void testObjectParser() throws IOException, ParserException {
        ObjectParser parser = new Parser(new ByteArrayInputStream(
                "Module.object".getBytes())).new ObjectParser();

        Node result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ExternalObjectReferenceNode);

        parser = new Parser(new ByteArrayInputStream(
                "{ A-STRING VisibleString }".getBytes())).new ObjectParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ObjectDefnNode);

        parser = new Parser(new ByteArrayInputStream(
                "object.&Type-Reference".getBytes())).new ObjectParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ObjectFromObjectNode);
        assertNotNull(((ObjectFromObjectNode) result).getObject());

        parser = new Parser(new ByteArrayInputStream(
                "object {Parameter}".getBytes())).new ObjectParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ObjectReference);
        assertNotNull(((ObjectReference) result).getParameters());
    }

    @Test
    void testObjectDefnParser() throws IOException, ParserException {
        ObjectDefnParser parser = new Parser(new ByteArrayInputStream(
                "{ &int-field 4711, &Type-Field VisibleString }".getBytes())).new ObjectDefnParser();

        ObjectDefnNode result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getSyntax() instanceof DefaultSyntaxNode);

        parser = new Parser(new ByteArrayInputStream(
                "{ A-STRING VisibleString A-NUMBER INTEGER }".getBytes())).new ObjectDefnParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getSyntax() instanceof DefinedSyntaxNode);
    }

    @Test
    void testDefaultSyntaxParser() throws IOException, ParserException {
        DefaultSyntaxParser parser = new Parser(new ByteArrayInputStream(
                "{ &int-field 4711, &Type-Field VisibleString }".getBytes())).new DefaultSyntaxParser();

        DefaultSyntaxNode result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.getFieldSetting().size());

        parser = new Parser(new ByteArrayInputStream("{}".getBytes())).new DefaultSyntaxParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(0, result.getFieldSetting().size());
    }

    @Test
    void testFieldSettingParser() throws IOException, ParserException {
        FieldSettingParser parser = new Parser(new ByteArrayInputStream(
                "&value-field 4711".getBytes())).new FieldSettingParser();

        FieldSettingNode result = parser.parse();

        assertNotNull(result);
        assertEquals("value-field", result.getFieldName().getReference());
        assertTrue(result.getSetting() instanceof IntegerValue);
    }

    @Test
    void testDefinedSyntaxParser() throws IOException, ParserException {
        DefinedSyntaxParser parser = new Parser(new ByteArrayInputStream(
                "{ A-STRING VisibleString A-NUMBER INTEGER }".getBytes())).new DefinedSyntaxParser();

        DefinedSyntaxNode result = parser.parse();
        assertNotNull(result);
        assertEquals(4, result.getNodes().size());

        parser = new Parser(new ByteArrayInputStream("{ }".getBytes())).new DefinedSyntaxParser();

        result = parser.parse();
        assertNotNull(result);
        assertEquals(0, result.getNodes().size());
    }

    @Test
    void testDefinedSyntaxTokenParser() throws IOException,
            ParserException {
        DefinedSyntaxTokenParser parser = new Parser(new ByteArrayInputStream(
                "A-LITERAL".getBytes())).new DefinedSyntaxTokenParser();

        Node result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof LiteralNode);

        parser = new Parser(
                new ByteArrayInputStream("VisibleString".getBytes())).new DefinedSyntaxTokenParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof VisibleString);
    }

    @Test
    void testSettingParser() throws IOException, ParserException {
        // Type
        SettingParser parser = new Parser(new ByteArrayInputStream(
                "INTEGER".getBytes())).new SettingParser();

        Node result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof IntegerType);

        // Value
        parser = new Parser(new ByteArrayInputStream("12.5".getBytes())).new SettingParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof RealValue);

        // ValueSet
        parser = new Parser(new ByteArrayInputStream(
                "{ (12..24) ^ (30..42) }".getBytes())).new SettingParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ElementSetSpecsNode);

        // Object
        parser = new Parser(new ByteArrayInputStream(
                "{ A-STRING VisibleString }".getBytes())).new SettingParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ObjectDefnNode);

        // ObjectSet
        parser = new Parser(new ByteArrayInputStream(
                "{ ..., (Object1 | Object2) }".getBytes())).new SettingParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ObjectSetSpecNode);
    }

    @Test
    void testTypeSettingParser() throws IOException, ParserException {
        TypeSettingParser parser = new Parser(new ByteArrayInputStream(
                "NULL".getBytes())).new TypeSettingParser();

        Node result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof Null);
    }

    @Test
    void testValueSettingParser() throws IOException, ParserException {
        Parser.ValueSettingParser parser = new Parser(new ByteArrayInputStream(
                "NULL".getBytes())).new ValueSettingParser();

        Node result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof NullValue);
    }

    /**
     * ************************************************************************
     * X.681 12
     * ***********************************************************************
     */

    @Test
    void testObjectSetAssignmentParser() throws IOException,
            ParserException {
        ValueSetTypeAssignmentParser parser = new Parser(
                new ByteArrayInputStream(
                        "ObjectSet-Ref OBJ-CLASS ::= { (Object1 | Object2) }"
                                .getBytes())).new ValueSetTypeAssignmentParser();

        ValueSetTypeOrObjectSetAssignmentNode result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getObjectSetAssignment().isPresent());

        ObjectSetAssignmentNode assignment = result.getObjectSetAssignment().get();

        assertEquals("ObjectSet-Ref", assignment.getReference());
        assertTrue(assignment.getObjectClassReference() instanceof ObjectClassReference);
        assertEquals("OBJ-CLASS", assignment.getObjectClassReference().getReference());
        assertNotNull(assignment.getObjectSet());
    }

    @Test
    void testObjectSetParser() throws IOException, ParserException {
        ObjectSetParser parser = new Parser(new ByteArrayInputStream(
                "{ (Object1 | Object2) }".getBytes())).new ObjectSetParser();

        SetSpecsNode result = parser.parse();

        assertNotNull(result);
    }

    @Test
    void testObjectSetSpecParser() throws IOException, ParserException {
        Parser.ObjectSetSpecParser parser = new Parser(new ByteArrayInputStream(
                "(Object1 | Object2)".getBytes())).new ObjectSetSpecParser();

        SetSpecsNode result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getRootElements());
        assertNull(result.getExtensionElements());
        assertFalse(result.hasExtensionMarker());

        parser = new Parser(new ByteArrayInputStream(
                "(Object1 | Object2), ...".getBytes())).new ObjectSetSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getRootElements());
        assertNull(result.getExtensionElements());
        assertTrue(result.hasExtensionMarker());

        parser = new Parser(new ByteArrayInputStream("...".getBytes())).new ObjectSetSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertNull(result.getRootElements());
        assertNull(result.getExtensionElements());
        assertTrue(result.hasExtensionMarker());

        parser = new Parser(new ByteArrayInputStream(
                "..., (Object1 | Object2)".getBytes())).new ObjectSetSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertNull(result.getRootElements());
        assertNotNull(result.getExtensionElements());
        assertTrue(result.hasExtensionMarker());

        parser = new Parser(new ByteArrayInputStream(
                "(Object1 | Object2), ..., (Object3 | Object4)".getBytes())).new ObjectSetSpecParser();

        result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getRootElements());
        assertNotNull(result.getExtensionElements());
        assertTrue(result.hasExtensionMarker());
    }

    @Test
    void testObjectSetElementsParser() throws IOException,
            ParserException {
        ObjectSetElementsParser parser = new Parser(new ByteArrayInputStream(
                "object-reference".getBytes())).new ObjectSetElementsParser();

        ObjectSetElements result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getElement() instanceof ObjectNode);

        parser = new Parser(new ByteArrayInputStream(
                "Module.Object-Set".getBytes())).new ObjectSetElementsParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getElement() instanceof ExternalObjectSetReference);

        parser = new Parser(new ByteArrayInputStream(
                "object-reference.&Type-Reference1".getBytes())).new ObjectSetElementsParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getElement() instanceof TypeFromObjects);

        parser = new Parser(new ByteArrayInputStream(
                "ObjectSet-Reference {parameter}".getBytes())).new ObjectSetElementsParser();

        result = parser.parse();

        assertNotNull(result);
    }

    /**
     * ************************************************************************
     * X.681 14
     * ***********************************************************************
     */

    @Test
    void testObjectClassFieldTypeParser() throws IOException,
            ParserException {
        ObjectClassFieldTypeParser parser = new Parser(
                new ByteArrayInputStream(
                        "OBJECT-CLASS.&ObjectSet-Reference".getBytes())).new ObjectClassFieldTypeParser();

        ObjectClassFieldType result = parser.parse();

        assertNotNull(result);
        assertEquals("OBJECT-CLASS", result.getObjectClassReference().getReference());
        assertEquals("ObjectSet-Reference", result.getFieldName()
                .getPrimitiveFieldNames().get(0).getReference());
    }

    @Test
    void testObjectClassFieldValueParser() throws IOException, ParserException {
        ObjectClassFieldValueParser parser = new Parser(
                new ByteArrayInputStream("INTEGER: 4711".getBytes())).new ObjectClassFieldValueParser();

        Value result = parser.parse();
        assertTrue(result instanceof OpenTypeFieldValue);

        parser = new Parser(new ByteArrayInputStream("NULL:NULL".getBytes())).new ObjectClassFieldValueParser();

        result = parser.parse();
        assertTrue(result instanceof OpenTypeFieldValue);

        parser = new Parser(new ByteArrayInputStream(
                "object-reference.&value-reference".getBytes())).new ObjectClassFieldValueParser();

        result = parser.parse();
        assertTrue(result instanceof ValueFromObject);
    }

    @Test
    void testOpenTypeFieldValParser() throws IOException,
            ParserException {
        OpenTypeFieldValParser parser = new Parser(new ByteArrayInputStream(
                "INTEGER: 4711".getBytes())).new OpenTypeFieldValParser();

        OpenTypeFieldValue result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getType() instanceof IntegerType);
        assertTrue(result.getValue() instanceof IntegerValue);

        parser = new Parser(new ByteArrayInputStream(
                "VisibleString: value-ref".getBytes())).new OpenTypeFieldValParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getType() instanceof VisibleString);
        assertTrue(result.getValue() instanceof SimpleDefinedValue);
    }

    @Test
    void testFixedTypeFieldValParser() throws IOException,
            ParserException {
        FixedTypeFieldValParser parser = new Parser(new ByteArrayInputStream(
                "object-reference {Object}.&value-reference".getBytes())).new FixedTypeFieldValParser();

        Value result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ValueFromObject);

        parser = new Parser(new ByteArrayInputStream("\"string\"".getBytes())).new FixedTypeFieldValParser();

        result = parser.parse();

        testAmbiguousValue(result, StringValue.class);
    }

    /**
     * ************************************************************************
     * X.681 15
     * ***********************************************************************
     */

    @Test
    void testInformationFromObjectsParser() throws IOException,
            ParserException {
        InformationFromObjectsParser parser = new Parser(
                new ByteArrayInputStream(
                        "object-reference {Object}.&Type-Reference1.&Type-Reference2"
                                .getBytes())).new InformationFromObjectsParser();

        InformationFromObjects result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof TypeFromObjects);
        assertNotNull(result.getReference());
        assertNotNull(result.getField());

        parser = new Parser(new ByteArrayInputStream(
                "object-reference {Object}.&value-reference1.&value-reference2"
                        .getBytes())).new InformationFromObjectsParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ValueFromObject);
        assertNotNull(result.getReference());
        assertNotNull(result.getField());
    }

    @Test
    void testValueFromObjectParser() throws IOException, ParserException {
        ValueFromObjectParser parser = new Parser(new ByteArrayInputStream(
                "object-reference {Object}.&value-reference1.&value-reference2"
                        .getBytes())).new ValueFromObjectParser();

        ValueFromObject result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getReference());
        assertNotNull(result.getField());
    }

    @Test
    void testTypeFromObjectsParser() throws IOException, ParserException {
        TypeFromObjectsParser parser = new Parser(new ByteArrayInputStream(
                "object-reference {Object}.&Type-Reference1.&Type-Reference2"
                        .getBytes())).new TypeFromObjectsParser();

        TypeFromObjects result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getReference());
        assertNotNull(result.getField());
    }

    @Test
    void testReferencedObjectsParser() throws IOException,
            ParserException {
        ReferencedObjectsParser parser = new Parser(new ByteArrayInputStream(
                "object-reference".getBytes())).new ReferencedObjectsParser();

        Node result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ObjectReference);
        assertTrue(((ObjectReference) result).getParameters().isEmpty());

        parser = new Parser(new ByteArrayInputStream(
                "object-reference {Object}".getBytes())).new ReferencedObjectsParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ObjectReference);
        assertTrue(((ObjectReference) result).getParameters().isPresent());

        parser = new Parser(new ByteArrayInputStream(
                "ObjectSet-Reference".getBytes())).new ReferencedObjectsParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ObjectSetReference);
        assertTrue(((ObjectSetReference) result).getParameters().isEmpty());

        parser = new Parser(new ByteArrayInputStream(
                "ObjectSet-Reference {Object}".getBytes())).new ReferencedObjectsParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ObjectSetReference);
        assertTrue(((ObjectSetReference) result).getParameters().isPresent());
    }

    /**
     * ************************************************************************
     * X.681 C
     * ***********************************************************************
     */

    @Test
    void testInstanceOfTypeParser() throws IOException, ParserException {
        BuiltinTypeParserAux parser = new Parser(new ByteArrayInputStream(
                "INSTANCE OF OBJECT-CLASS".getBytes())).new BuiltinTypeParserAux();

        Type result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof InstanceOfType);
    }

    /**
     * ************************************************************************
     * X.682 8
     * ***********************************************************************
     */

    @Test
    void testGeneralConstraintParser() throws IOException,
            ParserException {
        GeneralConstraintParser parser = new Parser(new ByteArrayInputStream(
                "CONSTRAINED BY {}".getBytes())).new GeneralConstraintParser();

        Constraint result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof UserDefinedConstraintNode);

        parser = new Parser(new ByteArrayInputStream("{ErrorSet}".getBytes())).new GeneralConstraintParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof TableConstraint);

        parser = new Parser(new ByteArrayInputStream(
                "ENCODED BY {0 1 4711 2}".getBytes())).new GeneralConstraintParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ContentsConstraint);
    }

    /**
     * ************************************************************************
     * X.682 9
     * ***********************************************************************
     */

    @Test
    void testUserDefinedConstraintParser() throws IOException,
            ParserException {
        UserDefinedConstraintParser parser = new Parser(
                new ByteArrayInputStream("CONSTRAINED BY {}".getBytes())).new UserDefinedConstraintParser();

        UserDefinedConstraintNode result = parser.parse();

        assertNotNull(result);
        assertEquals(0, result.getParams().size());

        parser = new Parser(new ByteArrayInputStream(
                "CONSTRAINED BY { INTEGER: 4711 }".getBytes())).new UserDefinedConstraintParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(1, result.getParams().size());

        parser = new Parser(new ByteArrayInputStream(
                "CONSTRAINED BY { INTEGER: 4711, VisibleString: \"string\" }"
                        .getBytes())).new UserDefinedConstraintParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.getParams().size());
    }

    @Test
    void testUserDefinedConstraintParameterParser() throws IOException,
            ParserException {
        UserDefinedConstraintParameterParser parser = new Parser(
                new ByteArrayInputStream("INTEGER: 4711".getBytes())).new UserDefinedConstraintParameterParser();

        UserDefinedConstraintParamNode result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getGovernor());
        assertTrue(result.getValue() instanceof IntegerValue);

        parser = new Parser(new ByteArrayInputStream(
                "OBJECT-CLASS : { &int-field 4711, &Type-Field VisibleString }"
                        .getBytes())).new UserDefinedConstraintParameterParser();

        result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getGovernor());
        assertTrue(result.getValue() instanceof ObjectDefnNode);

        parser = new Parser(
                new ByteArrayInputStream("VisibleString".getBytes())).new UserDefinedConstraintParameterParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getValue() instanceof VisibleString);
    }

    /**
     * ************************************************************************
     * X.682 10
     * ***********************************************************************
     */

    @Test
    void testTableConstraintParser() throws IOException, ParserException {
        TableConstraintParser parser = new Parser(new ByteArrayInputStream(
                "{ErrorSet}".getBytes())).new TableConstraintParser();

        TableConstraint result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SimpleTableConstraint);

        parser = new Parser(new ByteArrayInputStream(
                "{ErrorSet} {@errorCategory, @.errorCode}".getBytes())).new TableConstraintParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ComponentRelationConstraint);
    }

    @Test
    void testSimpleTableConstraintParser() throws IOException,
            ParserException {
        SimpleTableConstraintParser parser = new Parser(
                new ByteArrayInputStream("{ErrorSet} ".getBytes())).new SimpleTableConstraintParser();

        SimpleTableConstraint result = parser.parse();

        assertNotNull(result);
    }

    @Test
    void testComponentRelationConstraintParser() throws IOException,
            ParserException {
        ComponentRelationConstraintParser parser = new Parser(
                new ByteArrayInputStream(
                        "{ErrorSet} {@errorCategory, @.errorCode}".getBytes())).new ComponentRelationConstraintParser();

        ComponentRelationConstraint result = parser.parse();

        assertNotNull(result);
        assertEquals("ErrorSet", result.getReference().getReference());
        assertEquals(2, result.getComponents().size());
    }

    @Test
    void testAtNotationParser() throws IOException, ParserException {
        AtNotationParser parser = new Parser(new ByteArrayInputStream(
                "@identifier-1".getBytes())).new AtNotationParser();

        AtNotationNode result = parser.parse();

        assertNotNull(result);
        assertEquals(1, result.getComponentIds().getIdentifiers().size());
        assertEquals("identifier-1", result.getComponentIds().getIdentifiers()
                .get(0));
        assertEquals(0, result.getLevel());

        parser = new Parser(new ByteArrayInputStream(
                "@identifier-1.identifier-2.identifier-3".getBytes())).new AtNotationParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(3, result.getComponentIds().getIdentifiers().size());
        assertEquals("identifier-1", result.getComponentIds().getIdentifiers()
                .get(0));
        assertEquals("identifier-2", result.getComponentIds().getIdentifiers()
                .get(1));
        assertEquals("identifier-3", result.getComponentIds().getIdentifiers()
                .get(2));
        assertEquals(0, result.getLevel());

        parser = new Parser(new ByteArrayInputStream(
                "@.identifier-1".getBytes())).new AtNotationParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(1, result.getComponentIds().getIdentifiers().size());
        assertEquals("identifier-1", result.getComponentIds().getIdentifiers()
                .get(0));
        assertEquals(1, result.getLevel());

        parser = new Parser(new ByteArrayInputStream(
                "@...identifier-1.identifier-2".getBytes())).new AtNotationParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.getComponentIds().getIdentifiers().size());
        assertEquals("identifier-1", result.getComponentIds().getIdentifiers()
                .get(0));
        assertEquals("identifier-2", result.getComponentIds().getIdentifiers()
                .get(1));
        assertEquals(3, result.getLevel());
    }

    @Test
    void testLevelParser() throws IOException, ParserException {
        assertEquals(0, (int) new Parser(
                new ByteArrayInputStream("".getBytes())).new LevelParser()
                .parse());
        assertEquals(
                1,
                (int) new Parser(new ByteArrayInputStream(".".getBytes())).new LevelParser()
                        .parse());
        assertEquals(
                5,
                (int) new Parser(new ByteArrayInputStream(".....".getBytes())).new LevelParser()
                        .parse());
    }

    @Test
    void testComponentIdListParser() throws IOException, ParserException {
        ComponentIdListParser parser = new Parser(new ByteArrayInputStream(
                "identifier-1".getBytes())).new ComponentIdListParser();

        ComponentIdListNode result = parser.parse();

        assertNotNull(result);
        assertEquals(Arrays.asList("identifier-1"), result.getIdentifiers());

        parser = new Parser(new ByteArrayInputStream(
                "identifier-1.identifier-2.identifier-3".getBytes())).new ComponentIdListParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(
                Arrays.asList("identifier-1", "identifier-2", "identifier-3"),
                result.getIdentifiers());
    }

    /**
     * ************************************************************************
     * X.682 11
     * ***********************************************************************
     */

    @Test
    void testContentsConstraintParser() throws IOException,
            ParserException {
        ContentsConstraintParser parser = new Parser(new ByteArrayInputStream(
                "CONTAINING VisibleString".getBytes())).new ContentsConstraintParser();

        ContentsConstraint result = parser.parse();

        assertNotNull(result);

        parser = new Parser(new ByteArrayInputStream(
                "ENCODED BY {0 1 4711 2}".getBytes())).new ContentsConstraintParser();

        result = parser.parse();

        assertNotNull(result);

        parser = new Parser(new ByteArrayInputStream(
                "CONTAINING VisibleString ENCODED BY {0 1 4711 2}".getBytes())).new ContentsConstraintParser();

        result = parser.parse();

        assertNotNull(result);
    }

    /**
     * ************************************************************************
     * X.683 8
     * ***********************************************************************
     */

    @Test
    void testParameterizedAssignmentParser() throws IOException, ParserException {
        var source = "TypeReference {VisibleString: String} ::= SEQUENCE { attribute String }";
        var parser = new Parser(new ByteArrayInputStream(source.getBytes())).new ParameterizedAssignmentParser();

        var result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ParameterizedTypeOrObjectClassAssignmentNode);

        var typeOrObjectClassAssignmentNode = (ParameterizedTypeOrObjectClassAssignmentNode) result;

        assertTrue(typeOrObjectClassAssignmentNode.getParameterizedTypeAssignment().isPresent());

        source = "valueReference {aValue} TypeReference ::= {attribute aValue}";
        parser = new Parser(new ByteArrayInputStream(source.getBytes())).new ParameterizedAssignmentParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ParameterizedValueOrObjectAssignmentNode);

        var valueOrObjectAssignmentNode = (ParameterizedValueOrObjectAssignmentNode) result;

        assertTrue(valueOrObjectAssignmentNode.getParameterizedValueAssignmentNode().isPresent());

        source = "TypeReference {aValue} INTEGER ::= {(4711 ^ aValue)}";
        parser = new Parser(new ByteArrayInputStream(source.getBytes())).new ParameterizedAssignmentParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ParameterizedValueSetTypeAssignmentNode);

        source = "OBJ-CLASS-REFERENCE {aValue} ::= ABSTRACT-SYNTAX";
        parser = new Parser(new ByteArrayInputStream(source.getBytes())).new ParameterizedAssignmentParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ParameterizedTypeOrObjectClassAssignmentNode);

        typeOrObjectClassAssignmentNode = (ParameterizedTypeOrObjectClassAssignmentNode) result;

        assertTrue(typeOrObjectClassAssignmentNode.getParameterizedObjectClassAssignment().isPresent());

        source = "objectReference {aValue} ABSTRACT-SYNTAX ::= object";
        parser = new Parser(new ByteArrayInputStream(source.getBytes())).new ParameterizedAssignmentParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ParameterizedValueOrObjectAssignmentNode);

        valueOrObjectAssignmentNode = (ParameterizedValueOrObjectAssignmentNode) result;

        assertTrue(valueOrObjectAssignmentNode.getParameterizedObjectAssignmentNode().isPresent());

        source = "ObjSetReference {aValue} ABSTRACT-SYNTAX ::= { object | { TYPE INTEGER } }";
        parser = new Parser(new ByteArrayInputStream(source.getBytes())).new ParameterizedAssignmentParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ParameterizedObjectSetAssignmentNode);
    }

    @Test
    void testParameterizedTypeAssignmentParser() throws IOException, ParserException {
        var source = "TypeReference {VisibleString: String} ::= SEQUENCE { attribute String }";
        var parser = new Parser(new ByteArrayInputStream(source.getBytes())).new ParameterizedTypeOrObjectClassAssignmentParser();
        var result = parser.parse();

        assertNotNull(result);
        assertEquals("TypeReference", result.getReference());
        assertEquals(1, result.getParameters().size());
        assertTrue(result instanceof ParameterizedTypeOrObjectClassAssignmentNode);

        var assignment = result.getParameterizedTypeAssignment();

        assertTrue(assignment.isPresent());
        assertTrue(assignment.get().getType() instanceof SequenceType);
    }

    @Test
    void testParameterizedValueAssignmentParser() throws IOException, ParserException {
        var source = "valueReference {aValue} TypeReference ::= {attribute aValue}";
        var parser = new Parser(new ByteArrayInputStream(source.getBytes())).new ParameterizedValueOrObjectAssignmentParser();

        var result = parser.parse();

        assertNotNull(result);
        assertEquals("valueReference", result.getReference());
        assertTrue(result instanceof ParameterizedValueOrObjectAssignmentNode);

        assertTrue(result.getParameterizedValueAssignmentNode().isPresent());

        var assignment = result.getParameterizedValueAssignmentNode().get();

        assertTrue(assignment.getType() instanceof TypeReference);
        assertEquals(1, result.getParameters().size());
        assertTrue(assignment.getValue() instanceof Value);
    }

    @Test
    void testParameterizedValueSetTypeAssignmentParser()
            throws IOException, ParserException {
        ParameterizedValueSetTypeAssignmentParser parser = new Parser(
                new ByteArrayInputStream(
                        "TypeReference {aValue} INTEGER ::= {(4711 ^ aValue)}"
                                .getBytes())).new ParameterizedValueSetTypeAssignmentParser();

        ParameterizedValueSetTypeAssignmentNode result = parser.parse();

        assertNotNull(result);
        assertEquals("TypeReference", result.getReference());
        assertTrue(result.getType() instanceof IntegerType);
        assertEquals(1, result.getParameters().size());
    }

    @Test
    void testParameterizedObjectClassAssignmentParser() throws IOException, ParserException {
        var source = "OBJ-CLASS-REFERENCE {aValue} ::= ABSTRACT-SYNTAX";
        var parser = new Parser(new ByteArrayInputStream(source.getBytes())).new ParameterizedTypeOrObjectClassAssignmentParser();
        var result = parser.parse();

        assertNotNull(result);
        assertEquals("OBJ-CLASS-REFERENCE", result.getReference());
        assertEquals(1, result.getParameters().size());
        assertTrue(result instanceof ParameterizedTypeOrObjectClassAssignmentNode);

        var assignment = result.getParameterizedObjectClassAssignment();

        assertTrue(assignment.isPresent());
        assertTrue(assignment.get().getObjectClass() instanceof ObjectClassReference);
    }

    @Test
    void testParameterizedObjectAssignmentParser() throws IOException, ParserException {
        var source = "objectReference {aValue} ABSTRACT-SYNTAX ::= object";
        var parser = new Parser(new ByteArrayInputStream(source.getBytes())).new ParameterizedValueOrObjectAssignmentParser();

        var result = parser.parse();

        assertTrue(result instanceof ParameterizedValueOrObjectAssignmentNode);
        assertEquals("objectReference", result.getReference());
        assertTrue(result.getParameterizedObjectAssignmentNode().isPresent());
        assertEquals(1, result.getParameters().size());

        source = "object{A-CLASS:class} A-CLASS ::= {ARGUMENT value.&Type}";
        parser = new Parser(new ByteArrayInputStream(source.getBytes())).new ParameterizedValueOrObjectAssignmentParser();

        result = parser.parse();

        assertTrue(result instanceof ParameterizedValueOrObjectAssignmentNode);
        assertTrue(result.getParameterizedObjectAssignmentNode().isPresent());
        assertTrue(result.getParameterizedObjectAssignmentNode().get().getObject() instanceof ObjectDefnNode);

        source = "object{A-CLASS:class} A-CLASS ::= object.&object-reference";
        parser = new Parser(new ByteArrayInputStream(source.getBytes())).new ParameterizedValueOrObjectAssignmentParser();

        result = parser.parse();

        assertTrue(result instanceof ParameterizedValueOrObjectAssignmentNode);
        assertTrue(result.getParameterizedObjectAssignmentNode().isPresent());

        assertTrue(result.getParameterizedObjectAssignmentNode().get().getObject() instanceof ObjectFromObjectNode);
    }

    @Test
    void testParameterizedObjectSetAssignmentParser() throws IOException, ParserException {
        ParameterizedObjectSetAssignmentParser parser = new Parser(
                new ByteArrayInputStream(
                        "ObjSetReference {aValue} OBJECT-CLASS ::= { object | { TYPE INTEGER } }"
                                .getBytes())).new ParameterizedObjectSetAssignmentParser();

        ParameterizedObjectSetAssignmentNode result = parser.parse();

        assertNotNull(result);
        assertEquals("ObjSetReference", result.getReference());
        assertTrue(result.getObjectClass() instanceof ObjectClassReference);
        assertEquals(1, result.getParameters().size());
    }

    @Test
    void testParameterListParser() throws IOException, ParserException {
        ParameterListParser parser = new Parser(new ByteArrayInputStream(
                "{VisibleString: aString, INTEGER: anInteger}".getBytes())).new ParameterListParser();

        List<ParameterNode> result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.size());

        parser = new Parser(new ByteArrayInputStream(
                "{VisibleString: aString}".getBytes())).new ParameterListParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(1, result.size());

        parser = new Parser(new ByteArrayInputStream(
                "{aString, anInteger}".getBytes())).new ParameterListParser();

        result = parser.parse();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testParameterParser() throws IOException, ParserException {
        ParameterParser parser = new Parser(new ByteArrayInputStream(
                "VisibleString: aString".getBytes())).new ParameterParser();

        ParameterNode result = parser.parse();

        assertNotNull(result);
        assertNotNull(result.getGovernor());
        assertEquals("aString", result.getReference().getName());

        parser = new Parser(new ByteArrayInputStream("aString".getBytes())).new ParameterParser();

        result = parser.parse();

        assertNotNull(result);
        assertNull(result.getGovernor());
        assertEquals("aString", result.getReference().getName());
    }

    @Test
    void testParamGovernorParser() throws IOException, ParserException {
        ParamGovernorParser parser = new Parser(new ByteArrayInputStream(
                "INTEGER".getBytes())).new ParamGovernorParser();

        ParamGovernorNode result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof Governor);
        assertTrue(((Governor) result).getType() instanceof IntegerType);
    }

    @Test
    void testGovernorParser() throws IOException, ParserException {
        GovernorParser parser = new Parser(new ByteArrayInputStream(
                "INTEGER".getBytes())).new GovernorParser();

        Governor result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getType() instanceof Type);

        parser = new Parser(new ByteArrayInputStream(
                "Module.OBJECT-CLASS".getBytes())).new GovernorParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result.getType() instanceof ExternalTypeReference);
    }

    @Test
    void testDummyGovernorParser() throws IOException, ParserException {
        DummyGovernorParser parser = new Parser(new ByteArrayInputStream(
                "Type-Reference".getBytes())).new DummyGovernorParser();

        DummyGovernor result = parser.parse();

        assertNotNull(result);
        assertEquals("Type-Reference", result.getReference().getName());
    }

    @Test
    void testDummyReferenceParser() throws IOException, ParserException {
        DummyReferenceParser parser = new Parser(new ByteArrayInputStream(
                "Type-Reference".getBytes())).new DummyReferenceParser();

        ReferenceNode result = parser.parse();

        assertNotNull(result);
        assertEquals("Type-Reference", result.getName());
    }

    /**
     * ************************************************************************
     * X.683 9
     * ***********************************************************************
     */

    @Test
    void testParameterizedTypeParser() throws IOException,
            ParserException {
        ParameterizedTypeParser parser = new Parser(new ByteArrayInputStream(
                "Module.Type {INTEGER}".getBytes())).new ParameterizedTypeParser();

        SimpleDefinedType result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ExternalTypeReference);

        assertEquals("Module", ((ExternalTypeReference) result).getModule());
        assertEquals("Type", result.getType());
        assertTrue(result.getParameters().isPresent());

        List<Node> parameters = result.getParameters().get();

        assertEquals(1, parameters.size());
        assertTrue(parameters.get(0) instanceof IntegerType);

        parser = new Parser(new ByteArrayInputStream("Type {4711}".getBytes())).new ParameterizedTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SimpleDefinedType);

        assertEquals("Type", result.getType());
        assertTrue(result.getParameters().isPresent());

        parameters = result.getParameters().get();

        assertEquals(1, parameters.size());
        assertTrue(parameters.get(0) instanceof IntegerValue);

        parser = new Parser(new ByteArrayInputStream("Type {a}".getBytes())).new ParameterizedTypeParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof SimpleDefinedType);

        assertEquals("Type", result.getType());
        assertTrue(result.getParameters().isPresent());

        parameters = result.getParameters().get();

        assertEquals(1, parameters.size());
        assertTrue(parameters.get(0) instanceof SimpleDefinedValue);
    }

    @Test
    void testSimpleDefinedTypeParser() throws IOException,
            ParserException {
        SimpleDefinedTypeParser parser = new Parser(new ByteArrayInputStream(
                "Module.TypeReference".getBytes())).new SimpleDefinedTypeParser();

        SimpleDefinedType result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ExternalTypeReference);

        assertEquals("Module", ((ExternalTypeReference) result).getModule());
        assertEquals("TypeReference", result.getType());

        parser = new Parser(
                new ByteArrayInputStream("TypeReference".getBytes())).new SimpleDefinedTypeParser();

        result = parser.parse();

        assertNotNull(result);

        assertEquals("TypeReference", result.getType());
    }

    @Test
    void testParameterizedValueParser() throws IOException,
            ParserException {
        ParameterizedValueParser parser = new Parser(new ByteArrayInputStream(
                "Module.value {4711}".getBytes())).new ParameterizedValueParser();

        SimpleDefinedValue result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ExternalValueReference);

        assertEquals("Module", ((ExternalValueReference) result).getModule());
        assertEquals("value", result.getReference());
        assertTrue(result.getParameters().isPresent());
        assertEquals(1, result.getParameters().get().size());
    }

    @Test
    void testSimpleDefinedValueParser() throws IOException,
            ParserException {
        SimpleDefinedValueParser parser = new Parser(new ByteArrayInputStream(
                "Module.value".getBytes())).new SimpleDefinedValueParser();

        SimpleDefinedValue result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof ExternalValueReference);

        assertEquals("Module", ((ExternalValueReference) result).getModule());
        assertEquals("value", result.getReference());

        parser = new Parser(new ByteArrayInputStream("valueref".getBytes())).new SimpleDefinedValueParser();

        result = parser.parse();

        assertNotNull(result);

        assertEquals("valueref", result.getReference());
    }

    @Test
    void testParameterizedValueSetTypeParser() throws IOException,
            ParserException {
        ParameterizedTypeParser parser = new Parser(new ByteArrayInputStream(
                "ValueSet {VisibleString}".getBytes())).new ParameterizedTypeParser();

        SimpleDefinedType result = parser.parse();

        assertNotNull(result);
        assertEquals("ValueSet", result.getType());
        assertTrue(result.getParameters().isPresent());
        assertEquals(1, result.getParameters().get().size());
    }

    @Test
    void testParameterizedObjectClassParser() throws IOException,
            ParserException {
        ParameterizedObjectClassParser parser = new Parser(
                new ByteArrayInputStream(
                        "OBJECT-CLASS {VisibleString}".getBytes())).new ParameterizedObjectClassParser();

        ObjectClassReference result = parser.parse();

        assertNotNull(result);
        assertEquals("OBJECT-CLASS", result.getReference());
        assertTrue(result.getParameters().isPresent());
        assertEquals(1, result.getParameters().get().size());
    }

    @Test
    void testParameterizedObjectSetParser() throws IOException,
            ParserException {
        ParameterizedObjectSetParser parser = new Parser(
                new ByteArrayInputStream("ObjectSet {INTEGER}".getBytes())).new ParameterizedObjectSetParser();

        ObjectSetReference result = parser.parse();

        assertNotNull(result);
        assertEquals("ObjectSet", result.getReference());
        assertTrue(result.getParameters().isPresent());
        assertEquals(1, result.getParameters().get().size());
    }

    @Test
    void testParameterizedObjectParser() throws IOException,
            ParserException {
        ParameterizedObjectParser parser = new Parser(new ByteArrayInputStream(
                "object {INTEGER}".getBytes())).new ParameterizedObjectParser();

        ObjectReference result = parser.parse();

        assertNotNull(result);
        assertEquals("object", result.getReference());
        assertTrue(result.getParameters().isPresent());
        assertEquals(1, result.getParameters().get().size());
    }

    @Test
    void testActualParameterListParser() throws IOException,
            ParserException {
        ActualParameterListParser parser = new Parser(new ByteArrayInputStream(
                "{CustomString {VisibleString}, INTEGER, object-reference}"
                        .getBytes())).new ActualParameterListParser();

        List<Node> result = parser.parse();

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testActualParameterParser() throws IOException, ParserException {
        ActualParameterParser parser = new Parser(new ByteArrayInputStream(
                "CustomString {VisibleString}".getBytes())).new ActualParameterParser();

        Node result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof TypeReference);
        assertNotNull(((TypeReference) result).getParameters());
        assertTrue(((TypeReference) result).getParameters().isPresent());
        assertEquals(1, ((TypeReference) result).getParameters().get().size());

        parser = new Parser(new ByteArrayInputStream(
                "Object {OBJECT-CLASS.&Type({ObjectSet})}".getBytes())).new ActualParameterParser();

        result = parser.parse();

        assertNotNull(result);
        assertTrue(result instanceof TypeReference);
        assertNotNull(((TypeReference) result).getParameters());
        assertTrue(((TypeReference) result).getParameters().isPresent());
        assertEquals(1, ((TypeReference) result).getParameters().get().size());
        assertTrue(((TypeReference) result).getParameters().get().get(0) instanceof ObjectClassFieldType);
        assertTrue(((ObjectClassFieldType) ((TypeReference) result)
                .getParameters().get().get(0)).hasConstraint());
    }

    private <T extends Value> void testAmbiguousValue(Object value, Class<T> valueClass) {
        testAmbiguousValue(value, valueClass, v -> {
        });
    }

    private <T extends Value> void testAmbiguousValue(Object value, Class<T> valueClass, Consumer<T> valueTest) {
        assertNotNull(value);
        assertTrue(value instanceof AmbiguousValue);
        assertNotNull(((AmbiguousValue) value).getValue(valueClass));
        valueTest.accept(((AmbiguousValue) value).getValue(valueClass));
    }

}
