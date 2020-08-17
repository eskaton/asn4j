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
import ch.eskaton.asn4j.compiler.resolvers.BMPStringValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.BitStringValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.BooleanValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.ChoiceValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.CollectionOfValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.CollectionValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.EnumeratedValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.GeneralStringValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.GeneralizedTimeValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.GraphicStringValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.IA5StringValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.IRIValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.IntegerValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.NullValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.NumericStringValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.ObjectIdentifierValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.OctetStringValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.PrintableStringValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.RealValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.RelativeIRIValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.RelativeOIDValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.TeletexStringValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.UTCTimeValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.UTF8StringValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.UniversalStringValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.ValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.VideotexStringValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.VisibleStringValueResolver;
import ch.eskaton.asn4j.compiler.typenamesuppliers.BitStringTypeNameSupplier;
import ch.eskaton.asn4j.compiler.typenamesuppliers.DefaultTypeNameSupplier;
import ch.eskaton.asn4j.compiler.typenamesuppliers.ExternalTypeReferenceTypeNameSupplier;
import ch.eskaton.asn4j.compiler.typenamesuppliers.IntegerTypeNameSupplier;
import ch.eskaton.asn4j.compiler.typenamesuppliers.SelectionTypeTypeNameSupplier;
import ch.eskaton.asn4j.compiler.typenamesuppliers.SubtypeTypeNameSupplier;
import ch.eskaton.asn4j.compiler.typenamesuppliers.TypeNameSupplier;
import ch.eskaton.asn4j.compiler.typenamesuppliers.TypeReferenceTypeNameSupplier;
import ch.eskaton.asn4j.parser.ObjectClassDefn;
import ch.eskaton.asn4j.parser.ast.FixedTypeValueFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.Node;
import ch.eskaton.asn4j.parser.ast.ObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ObjectClassAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ObjectClassNode;
import ch.eskaton.asn4j.parser.ast.ObjectDefnNode;
import ch.eskaton.asn4j.parser.ast.ObjectFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.ObjectSetAssignmentNode;
import ch.eskaton.asn4j.parser.ast.TypeAssignmentNode;
import ch.eskaton.asn4j.parser.ast.TypeFieldSpecNode;
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
                ASN1BitString.class, new BitStringValueResolver(ctx), new BitStringTypeNameSupplier(this),
                new BitStringDefaultCompiler(), new BitStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(BooleanType.class, new BooleanCompiler(), BooleanValue.class,
                ASN1Boolean.class, new BooleanValueResolver(ctx), new DefaultTypeNameSupplier(this),
                new BooleanConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(Choice.class, new ChoiceCompiler(), ChoiceValue.class,
                ASN1Choice.class, new ChoiceValueResolver(ctx), new SubtypeTypeNameSupplier<>(this, true),
                new ChoiceConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(EnumeratedType.class, new EnumeratedTypeCompiler(), EnumeratedValue.class,
                ASN1EnumeratedType.class, new EnumeratedValueResolver(ctx), new SubtypeTypeNameSupplier<>(this, true),
                new EnumeratedTypeConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(IntegerType.class, new IntegerCompiler(), IntegerValue.class,
                ASN1Integer.class, new IntegerValueResolver(ctx), new IntegerTypeNameSupplier(this),
                new IntegerConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(Null.class, new NullCompiler(), NullValue.class,
                ASN1Null.class, new NullValueResolver(ctx), new DefaultTypeNameSupplier(this),
                new NullConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(OctetString.class, new OctetStringCompiler(), OctetStringValue.class,
                ASN1OctetString.class, new OctetStringValueResolver(ctx), new DefaultTypeNameSupplier(this),
                new OctetStringDefaultCompiler(), new OctetStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(Real.class, new RealCompiler(), RealValue.class,
                ASN1Real.class, new RealValueResolver(ctx), new SubtypeTypeNameSupplier<>(this),
                new RealDefaultCompiler(), null));
        types.add(new TypeDefinition<>(SequenceType.class, new SequenceCompiler(), CollectionValue.class,
                ASN1Sequence.class, new CollectionValueResolver(ctx), new SubtypeTypeNameSupplier<>(this, true),
                new SequenceConstraintCompiler(ctx), true));
        types.add(new TypeDefinition<>(SequenceOfType.class, new SequenceOfCompiler(), CollectionOfValue.class,
                ASN1SequenceOf.class, new CollectionOfValueResolver(ctx), new SubtypeTypeNameSupplier<>(this, true),
                new SequenceOfConstraintCompiler(ctx), true));
        types.add(new TypeDefinition<>(SetType.class, new SetCompiler(), CollectionValue.class,
                ASN1Set.class, new CollectionValueResolver(ctx), new SubtypeTypeNameSupplier<>(this, true),
                new SetConstraintCompiler(ctx), true));
        types.add(new TypeDefinition<>(SetOfType.class, new SetOfCompiler(), CollectionOfValue.class,
                ASN1SetOf.class, new CollectionOfValueResolver(ctx), new SubtypeTypeNameSupplier<>(this, true),
                new SetOfConstraintCompiler(ctx), true));
        types.add(new TypeDefinition<>(VisibleString.class, new VisibleStringCompiler(), VisibleStringValue.class,
                ASN1VisibleString.class, new VisibleStringValueResolver(ctx), new DefaultTypeNameSupplier(this),
                new VisibleStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(ISO646String.class, new ISO646StringCompiler(), VisibleStringValue.class,
                ASN1VisibleString.class, new VisibleStringValueResolver(ctx), new DefaultTypeNameSupplier(this),
                new VisibleStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(NumericString.class, new NumericStringCompiler(), NumericStringValue.class,
                ASN1NumericString.class, new NumericStringValueResolver(ctx), new DefaultTypeNameSupplier(this),
                new NumericStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(PrintableString.class, new PrintableStringCompiler(), PrintableStringValue.class,
                ASN1PrintableString.class, new PrintableStringValueResolver(ctx), new DefaultTypeNameSupplier(this),
                new PrintableStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(IA5String.class, new IA5StringCompiler(), IA5StringValue.class,
                ASN1IA5String.class, new IA5StringValueResolver(ctx), new DefaultTypeNameSupplier(this),
                new IA5StringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(GraphicString.class, new GraphicStringCompiler(), GraphicStringValue.class,
                ASN1GraphicString.class, new GraphicStringValueResolver(ctx), new DefaultTypeNameSupplier(this),
                new GraphicStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(GeneralString.class, new GeneralStringCompiler(), GeneralStringValue.class,
                ASN1GeneralString.class, new GeneralStringValueResolver(ctx), new DefaultTypeNameSupplier(this),
                new GeneralStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(TeletexString.class, new TeletexStringCompiler(), TeletexStringValue.class,
                ASN1TeletexString.class, new TeletexStringValueResolver(ctx), new DefaultTypeNameSupplier(this),
                new TeletexStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(T61String.class, new T61StringCompiler(), TeletexStringValue.class,
                ASN1TeletexString.class, new TeletexStringValueResolver(ctx), new DefaultTypeNameSupplier(this),
                new TeletexStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(VideotexString.class, new VideotexStringCompiler(), VideotexStringValue.class,
                ASN1VideotexString.class, new VideotexStringValueResolver(ctx), new DefaultTypeNameSupplier(this),
                new VideotexStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(UniversalString.class, new UniversalStringCompiler(), UniversalStringValue.class,
                ASN1UniversalString.class, new UniversalStringValueResolver(ctx), new DefaultTypeNameSupplier(this),
                new UniversalStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(UTF8String.class, new UTF8StringCompiler(), UTF8StringValue.class,
                ASN1UTF8String.class, new UTF8StringValueResolver(ctx), new DefaultTypeNameSupplier(this),
                new UTF8StringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(BMPString.class, new BMPStringCompiler(), BMPStringValue.class,
                ASN1BMPString.class, new BMPStringValueResolver(ctx), new DefaultTypeNameSupplier(this),
                new BMPStringConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(ObjectIdentifier.class, new ObjectIdentifierCompiler(), ObjectIdentifierValue.class,
                ASN1ObjectIdentifier.class, new ObjectIdentifierValueResolver(ctx), new SubtypeTypeNameSupplier<>(this),
                new ObjectIdentifierConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(RelativeOID.class, new RelativeOIDCompiler(), RelativeOIDValue.class,
                ASN1RelativeOID.class, new RelativeOIDValueResolver(ctx), new SubtypeTypeNameSupplier<>(this),
                new RelativeOIDConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(IRI.class, new IRICompiler(), IRIValue.class,
                ASN1IRI.class, new IRIValueResolver(ctx), new SubtypeTypeNameSupplier<>(this),
                new IRIConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(RelativeIRI.class, new RelativeIRICompiler(), RelativeIRIValue.class,
                ASN1RelativeIRI.class, new RelativeIRIValueResolver(ctx), new SubtypeTypeNameSupplier<>(this),
                new RelativeIRIConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(OpenType.class, new OpenTypeCompiler(), null, ASN1OpenType.class, null,
                new SubtypeTypeNameSupplier<>(this), new OpenTypeConstraintCompiler(ctx)));
        types.add(new TypeDefinition<>(GeneralizedTime.class, new GeneralizedTimeCompiler(), GeneralizedTimeValue.class,
                ASN1GeneralizedTime.class, new GeneralizedTimeValueResolver(ctx), new DefaultTypeNameSupplier(this),
                null));
        types.add(new TypeDefinition<>(UTCTime.class, new UTCTimeCompiler(), UTCTimeValue.class,
                ASN1UTCTime.class, new UTCTimeValueResolver(ctx), new DefaultTypeNameSupplier(this),
                null));
        // special types
        types.add(new TypeDefinition<>(ComponentType.class, new ComponentTypeCompiler()));
        types.add(new TypeDefinition<>(SelectionType.class, new SelectionTypeCompiler(),
                new SelectionTypeTypeNameSupplier(ctx, this)));
        types.add(new TypeDefinition<>(TypeAssignmentNode.class, new TypeAssignmentCompiler()));
        types.add(new TypeDefinition<>(Type.class, new TypeCompiler()));
        types.add(new TypeDefinition<>(TypeReference.class, new TypeReferenceCompiler(),
                new TypeReferenceTypeNameSupplier(this)));
        types.add(new TypeDefinition<>(ExternalTypeReference.class, new ExternalTypeReferenceCompiler(),
                new ExternalTypeReferenceTypeNameSupplier(this)));
        types.add(new TypeDefinition<>(ObjectClassAssignmentNode.class, new ObjectClassAssignmentCompiler()));
        types.add(new TypeDefinition<>(ObjectClassNode.class, new ObjectClassNodeCompiler()));
        types.add(new TypeDefinition<>(ObjectClassDefn.class, new ObjectClassDefnCompiler()));
        types.add(new TypeDefinition<>(TypeFieldSpecNode.class, new TypeFieldSpecNodeCompiler()));
        types.add(new TypeDefinition<>(FixedTypeValueFieldSpecNode.class, new FixedTypeValueFieldSpecNodeCompiler()));
        types.add(new TypeDefinition<>(ObjectFieldSpecNode.class, new ObjectFieldSpecNodeCompiler()));
        types.add(new TypeDefinition<>(ObjectSetAssignmentNode.class, new ObjectSetAssignmentCompiler(ctx)));
        types.add(new TypeDefinition<>(ObjectAssignmentNode.class, new ObjectAssignmentCompiler()));
        types.add(new TypeDefinition<>(ObjectDefnNode.class, new ObjectDefnCompiler(ctx)));
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

    public <T extends Type> boolean isConstructed(Class<T> typeClass) {
        return getConfigByType("isConstructed", typeClass, TypeDefinition::isConstructed);
    }

    public <T extends Type> boolean isBuiltin(Class<T> typeClass) {
        return getConfigByType("isBuiltin", typeClass, TypeDefinition::isBuiltin);
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

    public <T extends Type, CC extends AbstractConstraintCompiler> CC getConstraintCompiler(Class<T> typeClass) {
        return (CC) getConfigByType("getConstraintCompiler", typeClass, TypeDefinition::getConstraintCompiler);
    }

    public <V extends Value, S extends ValueResolver<V>> S getValueResolver(Class<V> valueClass) {
        return (S) getConfigByValue("getValueResolver", valueClass, TypeDefinition::getValueResolver);
    }

    private <T extends Node, R> R getConfigByType(String functionName, Class<T> typeClass,
            Function<TypeDefinition, R> accessor) {
        return memoizer.get(functionName, List.of(typeClass), type -> types.stream()
                .filter(td -> td.matchesType(typeClass))
                .map(accessor)
                .findFirst()).orElseThrow(() ->
                new IllegalCompilerStateException("Unexpected type: %s", typeClass.getSimpleName()));
    }

    private <V extends Value, R> R getConfigByValue(String functionName, Class<V> valueClass,
            Function<TypeDefinition, R> accessor) {
        return memoizer.get(functionName, List.of(valueClass), type -> types.stream()
                .filter(td -> td.matchesValue(valueClass))
                .map(accessor)
                .findFirst()).orElseThrow(() ->
                new IllegalCompilerStateException("Unexpected value: %s", valueClass.getSimpleName()));
    }

    private static class Memoizer {

        private Map<Tuple2<String, List<Object>>, Object> cache = new HashMap<>();

        public <T> T get(String method, List<Object> args, Function<List<Object>, T> function) {
            return (T) cache.computeIfAbsent(Tuple2.of(method, args), k -> function.apply(k.get_2()));
        }

    }

}
