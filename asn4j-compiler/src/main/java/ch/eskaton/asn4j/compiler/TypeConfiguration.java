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

import ch.eskaton.asn4j.compiler.constraints.AbstractConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.BMPStringConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.BitStringConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.BooleanConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.ChoiceConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.EnumeratedTypeConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.GeneralStringConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.GraphicStringConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.IA5StringConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.IRIConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.IntegerConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.NullConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.NumericStringConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.ObjectIdentifierConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.OctetStringConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.OpenTypeConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.PrintableStringConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.RelativeIRIConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.RelativeOIDConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.SequenceConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.SequenceOfConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.SetConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.SetOfConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.TeletexStringConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.UTF8StringConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.UniversalStringConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.VideotexStringConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.VisibleStringConstraintCompiler;
import ch.eskaton.asn4j.compiler.defaults.AbstractDefaultCompiler;
import ch.eskaton.asn4j.compiler.defaults.BitStringDefaultCompiler;
import ch.eskaton.asn4j.compiler.defaults.OctetStringDefaultCompiler;
import ch.eskaton.asn4j.compiler.defaults.RealDefaultCompiler;
import ch.eskaton.asn4j.compiler.objects.AbstractSyntaxObjectClassReferenceNodeCompiler;
import ch.eskaton.asn4j.compiler.objects.ExternalObjectClassReferenceCompiler;
import ch.eskaton.asn4j.compiler.objects.FixedTypeValueFieldSpecNodeCompiler;
import ch.eskaton.asn4j.compiler.objects.FixedTypeValueSetFieldSpecNodeCompiler;
import ch.eskaton.asn4j.compiler.objects.ObjectClassDefnCompiler;
import ch.eskaton.asn4j.compiler.objects.ObjectClassNodeCompiler;
import ch.eskaton.asn4j.compiler.objects.ObjectClassReferenceCompiler;
import ch.eskaton.asn4j.compiler.objects.ObjectDefnCompiler;
import ch.eskaton.asn4j.compiler.objects.ObjectFieldSpecNodeCompiler;
import ch.eskaton.asn4j.compiler.objects.ObjectIdentifierCompiler;
import ch.eskaton.asn4j.compiler.objects.ObjectSetFieldSpecNodeCompiler;
import ch.eskaton.asn4j.compiler.objects.TypeFieldSpecNodeCompiler;
import ch.eskaton.asn4j.compiler.objects.TypeIdentifierObjectClassReferenceNodeCompiler;
import ch.eskaton.asn4j.compiler.objects.VariableTypeValueFieldSpecNodeCompiler;
import ch.eskaton.asn4j.compiler.objects.VariableTypeValueSetFieldSpecNodeCompiler;
import ch.eskaton.asn4j.compiler.typenamesuppliers.BitStringTypeNameSupplier;
import ch.eskaton.asn4j.compiler.typenamesuppliers.DefaultTypeNameSupplier;
import ch.eskaton.asn4j.compiler.typenamesuppliers.ExternalTypeReferenceTypeNameSupplier;
import ch.eskaton.asn4j.compiler.typenamesuppliers.IntegerTypeNameSupplier;
import ch.eskaton.asn4j.compiler.typenamesuppliers.SelectionTypeTypeNameSupplier;
import ch.eskaton.asn4j.compiler.typenamesuppliers.SubtypeTypeNameSupplier;
import ch.eskaton.asn4j.compiler.typenamesuppliers.TypeNameSupplier;
import ch.eskaton.asn4j.compiler.typenamesuppliers.TypeReferenceTypeNameSupplier;
import ch.eskaton.asn4j.compiler.types.BMPStringCompiler;
import ch.eskaton.asn4j.compiler.types.BitStringCompiler;
import ch.eskaton.asn4j.compiler.types.BooleanCompiler;
import ch.eskaton.asn4j.compiler.types.ChoiceCompiler;
import ch.eskaton.asn4j.compiler.types.ComponentTypeCompiler;
import ch.eskaton.asn4j.compiler.types.EnumeratedTypeCompiler;
import ch.eskaton.asn4j.compiler.types.ExternalTypeReferenceCompiler;
import ch.eskaton.asn4j.compiler.types.GeneralStringCompiler;
import ch.eskaton.asn4j.compiler.types.GeneralizedTimeCompiler;
import ch.eskaton.asn4j.compiler.types.GraphicStringCompiler;
import ch.eskaton.asn4j.compiler.types.IA5StringCompiler;
import ch.eskaton.asn4j.compiler.types.IRICompiler;
import ch.eskaton.asn4j.compiler.types.ISO646StringCompiler;
import ch.eskaton.asn4j.compiler.types.InstanceOfTypeCompiler;
import ch.eskaton.asn4j.compiler.types.IntegerCompiler;
import ch.eskaton.asn4j.compiler.types.NamedTypeCompiler;
import ch.eskaton.asn4j.compiler.types.NullCompiler;
import ch.eskaton.asn4j.compiler.types.NumericStringCompiler;
import ch.eskaton.asn4j.compiler.types.ObjectClassFieldTypeCompiler;
import ch.eskaton.asn4j.compiler.types.OctetStringCompiler;
import ch.eskaton.asn4j.compiler.types.OpenTypeCompiler;
import ch.eskaton.asn4j.compiler.types.PrintableStringCompiler;
import ch.eskaton.asn4j.compiler.types.RealCompiler;
import ch.eskaton.asn4j.compiler.types.RelativeIRICompiler;
import ch.eskaton.asn4j.compiler.types.RelativeOIDCompiler;
import ch.eskaton.asn4j.compiler.types.SelectionTypeCompiler;
import ch.eskaton.asn4j.compiler.types.SequenceCompiler;
import ch.eskaton.asn4j.compiler.types.SequenceOfCompiler;
import ch.eskaton.asn4j.compiler.types.SetCompiler;
import ch.eskaton.asn4j.compiler.types.SetOfCompiler;
import ch.eskaton.asn4j.compiler.types.T61StringCompiler;
import ch.eskaton.asn4j.compiler.types.TeletexStringCompiler;
import ch.eskaton.asn4j.compiler.types.TypeCompiler;
import ch.eskaton.asn4j.compiler.types.TypeReferenceCompiler;
import ch.eskaton.asn4j.compiler.types.UTCTimeCompiler;
import ch.eskaton.asn4j.compiler.types.UTF8StringCompiler;
import ch.eskaton.asn4j.compiler.types.UniversalStringCompiler;
import ch.eskaton.asn4j.compiler.types.VideotexStringCompiler;
import ch.eskaton.asn4j.compiler.types.VisibleStringCompiler;
import ch.eskaton.asn4j.compiler.values.AbstractValueCompiler;
import ch.eskaton.asn4j.compiler.values.BMPStringValueCompiler;
import ch.eskaton.asn4j.compiler.values.BitStringValueCompiler;
import ch.eskaton.asn4j.compiler.values.BooleanValueCompiler;
import ch.eskaton.asn4j.compiler.values.ChoiceValueCompiler;
import ch.eskaton.asn4j.compiler.values.EnumeratedValueCompiler;
import ch.eskaton.asn4j.compiler.values.GeneralStringValueCompiler;
import ch.eskaton.asn4j.compiler.values.GeneralizedTimeValueCompiler;
import ch.eskaton.asn4j.compiler.values.GraphicStringValueCompiler;
import ch.eskaton.asn4j.compiler.values.IA5StringValueCompiler;
import ch.eskaton.asn4j.compiler.values.IRIValueCompiler;
import ch.eskaton.asn4j.compiler.values.IntegerValueCompiler;
import ch.eskaton.asn4j.compiler.values.NullValueCompiler;
import ch.eskaton.asn4j.compiler.values.NumericStringValueCompiler;
import ch.eskaton.asn4j.compiler.values.ObjectIdentifierValueCompiler;
import ch.eskaton.asn4j.compiler.values.OctetStringValueCompiler;
import ch.eskaton.asn4j.compiler.values.PrintableStringValueCompiler;
import ch.eskaton.asn4j.compiler.values.RealValueCompiler;
import ch.eskaton.asn4j.compiler.values.RelativeIRIValueCompiler;
import ch.eskaton.asn4j.compiler.values.RelativeOIDValueCompiler;
import ch.eskaton.asn4j.compiler.values.SequenceOfValueCompiler;
import ch.eskaton.asn4j.compiler.values.SequenceValueCompiler;
import ch.eskaton.asn4j.compiler.values.SetOfValueCompiler;
import ch.eskaton.asn4j.compiler.values.SetValueCompiler;
import ch.eskaton.asn4j.compiler.values.TeletexStringValueCompiler;
import ch.eskaton.asn4j.compiler.values.UTCTimeValueCompiler;
import ch.eskaton.asn4j.compiler.values.UTF8StringValueCompiler;
import ch.eskaton.asn4j.compiler.values.UniversalStringValueCompiler;
import ch.eskaton.asn4j.compiler.values.ValueCompiler;
import ch.eskaton.asn4j.compiler.values.VideotexStringValueCompiler;
import ch.eskaton.asn4j.compiler.values.VisibleStringValueCompiler;
import ch.eskaton.asn4j.parser.ast.AbstractSyntaxObjectClassReferenceNode;
import ch.eskaton.asn4j.parser.ast.ExternalObjectClassReference;
import ch.eskaton.asn4j.parser.ast.FixedTypeValueFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.FixedTypeValueSetFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.Node;
import ch.eskaton.asn4j.parser.ast.ObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ObjectClassAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ObjectClassDefn;
import ch.eskaton.asn4j.parser.ast.ObjectClassNode;
import ch.eskaton.asn4j.parser.ast.ObjectClassReference;
import ch.eskaton.asn4j.parser.ast.ObjectDefnNode;
import ch.eskaton.asn4j.parser.ast.ObjectFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.ObjectSetAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ObjectSetFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.ObjectSetSpecNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedTypeAssignmentNode;
import ch.eskaton.asn4j.parser.ast.TypeAssignmentNode;
import ch.eskaton.asn4j.parser.ast.TypeFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.TypeIdentifierObjectClassReferenceNode;
import ch.eskaton.asn4j.parser.ast.ValueAssignmentNode;
import ch.eskaton.asn4j.parser.ast.VariableTypeValueFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.VariableTypeValueSetFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.types.BMPString;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.BooleanType;
import ch.eskaton.asn4j.parser.ast.types.Choice;
import ch.eskaton.asn4j.parser.ast.types.ComponentType;
import ch.eskaton.asn4j.parser.ast.types.EnumeratedType;
import ch.eskaton.asn4j.parser.ast.types.ExternalTypeReference;
import ch.eskaton.asn4j.parser.ast.types.GeneralString;
import ch.eskaton.asn4j.parser.ast.types.GeneralizedTime;
import ch.eskaton.asn4j.parser.ast.types.GraphicString;
import ch.eskaton.asn4j.parser.ast.types.IA5String;
import ch.eskaton.asn4j.parser.ast.types.IRI;
import ch.eskaton.asn4j.parser.ast.types.ISO646String;
import ch.eskaton.asn4j.parser.ast.types.InstanceOfType;
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.asn4j.parser.ast.types.Null;
import ch.eskaton.asn4j.parser.ast.types.NumericString;
import ch.eskaton.asn4j.parser.ast.types.ObjectClassFieldType;
import ch.eskaton.asn4j.parser.ast.types.ObjectIdentifier;
import ch.eskaton.asn4j.parser.ast.types.OctetString;
import ch.eskaton.asn4j.parser.ast.types.OpenType;
import ch.eskaton.asn4j.parser.ast.types.PrintableString;
import ch.eskaton.asn4j.parser.ast.types.Real;
import ch.eskaton.asn4j.parser.ast.types.RelativeIRI;
import ch.eskaton.asn4j.parser.ast.types.RelativeOID;
import ch.eskaton.asn4j.parser.ast.types.SelectionType;
import ch.eskaton.asn4j.parser.ast.types.SequenceOfType;
import ch.eskaton.asn4j.parser.ast.types.SequenceType;
import ch.eskaton.asn4j.parser.ast.types.SetOfType;
import ch.eskaton.asn4j.parser.ast.types.SetType;
import ch.eskaton.asn4j.parser.ast.types.T61String;
import ch.eskaton.asn4j.parser.ast.types.TeletexString;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
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
import ch.eskaton.asn4j.runtime.types.ASN1BMPString;
import ch.eskaton.asn4j.runtime.types.ASN1BitString;
import ch.eskaton.asn4j.runtime.types.ASN1Boolean;
import ch.eskaton.asn4j.runtime.types.ASN1Choice;
import ch.eskaton.asn4j.runtime.types.ASN1EnumeratedType;
import ch.eskaton.asn4j.runtime.types.ASN1GeneralString;
import ch.eskaton.asn4j.runtime.types.ASN1GeneralizedTime;
import ch.eskaton.asn4j.runtime.types.ASN1GraphicString;
import ch.eskaton.asn4j.runtime.types.ASN1IA5String;
import ch.eskaton.asn4j.runtime.types.ASN1IRI;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1Null;
import ch.eskaton.asn4j.runtime.types.ASN1NumericString;
import ch.eskaton.asn4j.runtime.types.ASN1ObjectIdentifier;
import ch.eskaton.asn4j.runtime.types.ASN1OctetString;
import ch.eskaton.asn4j.runtime.types.ASN1OpenType;
import ch.eskaton.asn4j.runtime.types.ASN1PrintableString;
import ch.eskaton.asn4j.runtime.types.ASN1Real;
import ch.eskaton.asn4j.runtime.types.ASN1RelativeIRI;
import ch.eskaton.asn4j.runtime.types.ASN1RelativeOID;
import ch.eskaton.asn4j.runtime.types.ASN1Sequence;
import ch.eskaton.asn4j.runtime.types.ASN1SequenceOf;
import ch.eskaton.asn4j.runtime.types.ASN1Set;
import ch.eskaton.asn4j.runtime.types.ASN1SetOf;
import ch.eskaton.asn4j.runtime.types.ASN1TeletexString;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.asn4j.runtime.types.ASN1UTCTime;
import ch.eskaton.asn4j.runtime.types.ASN1UTF8String;
import ch.eskaton.asn4j.runtime.types.ASN1UniversalString;
import ch.eskaton.asn4j.runtime.types.ASN1VideotexString;
import ch.eskaton.asn4j.runtime.types.ASN1VisibleString;
import ch.eskaton.commons.collections.Tuple2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TypeConfiguration {

    private Memoizer memoizer = new Memoizer();

    private List<TypeDefinition> types = new ArrayList<>();

    public TypeConfiguration(CompilerContext ctx) {
        types.add(new TypeDefinition<>(BitString.class, new BitStringCompiler(), BitStringValue.class,
                ASN1BitString.class, new BitStringValueCompiler(), new BitStringTypeNameSupplier(this),
                new BitStringDefaultCompiler(), new BitStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(BooleanType.class, new BooleanCompiler(), BooleanValue.class,
                ASN1Boolean.class, new BooleanValueCompiler(), new DefaultTypeNameSupplier(this),
                new BooleanConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(Choice.class, new ChoiceCompiler(), ChoiceValue.class,
                ASN1Choice.class, new ChoiceValueCompiler(), new SubtypeTypeNameSupplier<>(this, true),
                new ChoiceConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(EnumeratedType.class, new EnumeratedTypeCompiler(), EnumeratedValue.class,
                ASN1EnumeratedType.class, new EnumeratedValueCompiler(), new SubtypeTypeNameSupplier<>(this, true),
                new EnumeratedTypeConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(IntegerType.class, new IntegerCompiler(), IntegerValue.class,
                ASN1Integer.class, new IntegerValueCompiler(), new IntegerTypeNameSupplier(this),
                new IntegerConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(Null.class, new NullCompiler(), NullValue.class,
                ASN1Null.class, new NullValueCompiler(), new DefaultTypeNameSupplier(this),
                new NullConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(OctetString.class, new OctetStringCompiler(), OctetStringValue.class,
                ASN1OctetString.class, new OctetStringValueCompiler(), new DefaultTypeNameSupplier(this),
                new OctetStringDefaultCompiler(), new OctetStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(Real.class, new RealCompiler(), RealValue.class,
                ASN1Real.class, new RealValueCompiler(), new SubtypeTypeNameSupplier<>(this),
                new RealDefaultCompiler(), null));
        types.add(new TypeDefinition<>(SequenceType.class, new SequenceCompiler(), CollectionValue.class,
                ASN1Sequence.class, new SequenceValueCompiler(), new SubtypeTypeNameSupplier<>(this, true),
                new SequenceConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(SequenceOfType.class, new SequenceOfCompiler(), CollectionOfValue.class,
                ASN1SequenceOf.class, new SequenceOfValueCompiler(), new SubtypeTypeNameSupplier<>(this, true),
                new SequenceOfConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(SetType.class, new SetCompiler(), CollectionValue.class,
                ASN1Set.class, new SetValueCompiler(), new SubtypeTypeNameSupplier<>(this, true),
                new SetConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(SetOfType.class, new SetOfCompiler(), CollectionOfValue.class,
                ASN1SetOf.class, new SetOfValueCompiler(), new SubtypeTypeNameSupplier<>(this, true),
                new SetOfConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(VisibleString.class, new VisibleStringCompiler(), VisibleStringValue.class,
                ASN1VisibleString.class, new VisibleStringValueCompiler(), new DefaultTypeNameSupplier(this),
                new VisibleStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(ISO646String.class, new ISO646StringCompiler(), VisibleStringValue.class,
                ASN1VisibleString.class, new VisibleStringValueCompiler(), new DefaultTypeNameSupplier(this),
                new VisibleStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(NumericString.class, new NumericStringCompiler(), NumericStringValue.class,
                ASN1NumericString.class, new NumericStringValueCompiler(), new DefaultTypeNameSupplier(this),
                new NumericStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(PrintableString.class, new PrintableStringCompiler(), PrintableStringValue.class,
                ASN1PrintableString.class, new PrintableStringValueCompiler(), new DefaultTypeNameSupplier(this),
                new PrintableStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(IA5String.class, new IA5StringCompiler(), IA5StringValue.class,
                ASN1IA5String.class, new IA5StringValueCompiler(), new DefaultTypeNameSupplier(this),
                new IA5StringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(GraphicString.class, new GraphicStringCompiler(), GraphicStringValue.class,
                ASN1GraphicString.class, new GraphicStringValueCompiler(), new DefaultTypeNameSupplier(this),
                new GraphicStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(GeneralString.class, new GeneralStringCompiler(), GeneralStringValue.class,
                ASN1GeneralString.class, new GeneralStringValueCompiler(), new DefaultTypeNameSupplier(this),
                new GeneralStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(TeletexString.class, new TeletexStringCompiler(), TeletexStringValue.class,
                ASN1TeletexString.class, new TeletexStringValueCompiler(), new DefaultTypeNameSupplier(this),
                new TeletexStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(T61String.class, new T61StringCompiler(), TeletexStringValue.class,
                ASN1TeletexString.class, new TeletexStringValueCompiler(), new DefaultTypeNameSupplier(this),
                new TeletexStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(VideotexString.class, new VideotexStringCompiler(), VideotexStringValue.class,
                ASN1VideotexString.class, new VideotexStringValueCompiler(), new DefaultTypeNameSupplier(this),
                new VideotexStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(UniversalString.class, new UniversalStringCompiler(), UniversalStringValue.class,
                ASN1UniversalString.class, new UniversalStringValueCompiler(), new DefaultTypeNameSupplier(this),
                new UniversalStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(UTF8String.class, new UTF8StringCompiler(), UTF8StringValue.class,
                ASN1UTF8String.class, new UTF8StringValueCompiler(), new DefaultTypeNameSupplier(this),
                new UTF8StringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(BMPString.class, new BMPStringCompiler(), BMPStringValue.class,
                ASN1BMPString.class, new BMPStringValueCompiler(), new DefaultTypeNameSupplier(this),
                new BMPStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(ObjectIdentifier.class, new ObjectIdentifierCompiler(), ObjectIdentifierValue.class,
                ASN1ObjectIdentifier.class, new ObjectIdentifierValueCompiler(), new SubtypeTypeNameSupplier<>(this),
                new ObjectIdentifierConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(RelativeOID.class, new RelativeOIDCompiler(), RelativeOIDValue.class,
                ASN1RelativeOID.class, new RelativeOIDValueCompiler(), new SubtypeTypeNameSupplier<>(this),
                new RelativeOIDConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(IRI.class, new IRICompiler(), IRIValue.class,
                ASN1IRI.class, new IRIValueCompiler(), new SubtypeTypeNameSupplier<>(this),
                new IRIConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(RelativeIRI.class, new RelativeIRICompiler(), RelativeIRIValue.class,
                ASN1RelativeIRI.class, new RelativeIRIValueCompiler(), new SubtypeTypeNameSupplier<>(this),
                new RelativeIRIConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(OpenType.class, new OpenTypeCompiler(), null, ASN1OpenType.class, null,
                new SubtypeTypeNameSupplier<>(this), new OpenTypeConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(GeneralizedTime.class, new GeneralizedTimeCompiler(), GeneralizedTimeValue.class,
                ASN1GeneralizedTime.class, new GeneralizedTimeValueCompiler(), new DefaultTypeNameSupplier(this),
                new VisibleStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(UTCTime.class, new UTCTimeCompiler(), UTCTimeValue.class,
                ASN1UTCTime.class, new UTCTimeValueCompiler(), new DefaultTypeNameSupplier(this),
                new VisibleStringConstraintCompiler(ctx)));
        // special types
        types.add(new TypeDefinition<>(ComponentType.class, new ComponentTypeCompiler()));
        types.add(new TypeDefinition<>(SelectionType.class, new SelectionTypeCompiler(),
                new SelectionTypeTypeNameSupplier(ctx, this)));
        types.add(new TypeDefinition<>(Type.class, new TypeCompiler()));
        types.add(new TypeDefinition<>(NamedType.class, new NamedTypeCompiler()));
        types.add(new TypeDefinition<>(InstanceOfType.class, new InstanceOfTypeCompiler(), CollectionValue.class,
                ASN1Sequence.class, new DefaultTypeNameSupplier(this)));
        types.add(new TypeDefinition<>(TypeReference.class, new TypeReferenceCompiler(),
                new TypeReferenceTypeNameSupplier()));
        types.add(new TypeDefinition<>(ExternalTypeReference.class, new ExternalTypeReferenceCompiler(),
                new ExternalTypeReferenceTypeNameSupplier()));
        types.add(new TypeDefinition<>(ObjectClassFieldType.class, new ObjectClassFieldTypeCompiler()));
        types.add(new TypeDefinition<>(Value.class, new ValueCompiler()));
        types.add(new TypeDefinition<>(ObjectClassNode.class, new ObjectClassNodeCompiler()));
        types.add(new TypeDefinition<>(TypeIdentifierObjectClassReferenceNode.class, new TypeIdentifierObjectClassReferenceNodeCompiler()));
        types.add(new TypeDefinition<>(AbstractSyntaxObjectClassReferenceNode.class, new AbstractSyntaxObjectClassReferenceNodeCompiler()));
        types.add(new TypeDefinition<>(ObjectClassDefn.class, new ObjectClassDefnCompiler()));
        types.add(new TypeDefinition<>(ObjectClassReference.class, new ObjectClassReferenceCompiler()));
        types.add(new TypeDefinition<>(ExternalObjectClassReference.class, new ExternalObjectClassReferenceCompiler()));
        types.add(new TypeDefinition<>(ObjectSetSpecNode.class, new ObjectSetCompiler(ctx)));
        types.add(new TypeDefinition<>(TypeFieldSpecNode.class, new TypeFieldSpecNodeCompiler()));
        types.add(new TypeDefinition<>(FixedTypeValueFieldSpecNode.class, new FixedTypeValueFieldSpecNodeCompiler()));
        types.add(new TypeDefinition<>(VariableTypeValueFieldSpecNode.class, new VariableTypeValueFieldSpecNodeCompiler()));
        types.add(new TypeDefinition<>(FixedTypeValueSetFieldSpecNode.class, new FixedTypeValueSetFieldSpecNodeCompiler()));
        types.add(new TypeDefinition<>(VariableTypeValueSetFieldSpecNode.class, new VariableTypeValueSetFieldSpecNodeCompiler()));
        types.add(new TypeDefinition<>(ObjectFieldSpecNode.class, new ObjectFieldSpecNodeCompiler()));
        types.add(new TypeDefinition<>(ObjectSetFieldSpecNode.class, new ObjectSetFieldSpecNodeCompiler()));
        types.add(new TypeDefinition<>(ObjectDefnNode.class, new ObjectDefnCompiler(ctx)));
        // assignments
        types.add(new TypeDefinition<>(TypeAssignmentNode.class, new TypeAssignmentCompiler()));
        types.add(new TypeDefinition<>(ValueAssignmentNode.class, new ValueAssignmentCompiler()));
        types.add(new TypeDefinition<>(ObjectClassAssignmentNode.class, new ObjectClassAssignmentCompiler()));
        types.add(new TypeDefinition<>(ObjectAssignmentNode.class, new ObjectAssignmentCompiler()));
        types.add(new TypeDefinition<>(ObjectSetAssignmentNode.class, new ObjectSetAssignmentCompiler(ctx)));
        types.add(new TypeDefinition<>(ParameterizedTypeAssignmentNode.class, new ParameterizedTypeAssignmentCompiler()));
    }

    public <T extends Node, C extends Compiler<T>> C getCompiler(Class<T> typeClass) {
        return (C) getConfigByType("getCompiler", typeClass, TypeDefinition::getCompiler);
    }

    public <T extends Type, V extends Value> Class<V> getValueClass(Class<T> typeClass) {
        return getConfigByType("getValueClass", typeClass, TypeDefinition::getValueClass);
    }

    public <T extends Type, R extends ASN1Type> Class<R> getRuntimeTypeClass(Class<T> typeClass) {
        return getConfigByType("getRuntimeTypeClass", typeClass, TypeDefinition::getRuntimeTypeClass);
    }

    public <T extends Type> boolean isBuiltin(Class<T> typeClass) {
        try {
            return getConfigByType("isBuiltin", typeClass, TypeDefinition::isBuiltin);
        } catch (IllegalCompilerStateException e) {
            return false;
        }
    }

    public boolean isRuntimeType(String typeName) {
        return memoizer.get("isRuntimeType", List.of(typeName), type -> types.stream()
                .filter(td -> td.matchesRuntimeType(typeName))
                .findAny()).isPresent();
    }

    public <T extends Type> TypeNameSupplier<Type> getTypeNameSupplier(Class<T> typeClass) {
        return getConfigByType("getTypeNameSupplier", typeClass, TypeDefinition::getTypeNameSupplier);
    }

    public <V extends Value, T extends Type, D extends AbstractDefaultCompiler<V>> D getDefaultCompiler(
            Class<T> typeClass) {
        return (D) getConfigByType("getDefaultCompiler", typeClass, TypeDefinition::getDefaultCompiler);
    }

    public <T extends Type, C extends AbstractConstraintCompiler> C getConstraintCompiler(Class<T> typeClass) {
        return (C) getConfigByType("getConstraintCompiler", typeClass, TypeDefinition::getConstraintCompiler);
    }

    public <T extends Type, V extends Value, S extends AbstractValueCompiler<V>> S getValueCompiler(Class<T> typeClass) {
        return (S) getConfigByType("getValueCompiler", typeClass, TypeDefinition::getValueCompiler);
    }

    private <T extends Node, R> R getConfigByType(String functionName, Class<T> typeClass,
            Function<TypeDefinition, R> accessor) {
        return memoizer.get(functionName, List.of(typeClass), type -> types.stream()
                .filter(td -> td.matchesType(typeClass))
                .map(accessor)
                .findFirst()).orElseThrow(() ->
                new IllegalCompilerStateException("Unexpected type: %s", typeClass.getSimpleName()));
    }

    private static class Memoizer {

        private Map<Tuple2<String, List<Object>>, Object> cache = new HashMap<>();

        public <T> T get(String method, List<Object> args, Function<List<Object>, T> function) {
            return (T) cache.computeIfAbsent(Tuple2.of(method, args), k -> function.apply(k.get_2()));
        }

    }

}
