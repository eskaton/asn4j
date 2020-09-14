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

import ch.eskaton.asn4j.compiler.java.objs.JavaAnnotation;
import ch.eskaton.asn4j.compiler.java.objs.JavaDefinedField;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.TypeAssignmentNode;
import ch.eskaton.asn4j.parser.ast.types.Choice;
import ch.eskaton.asn4j.parser.ast.types.ComponentType;
import ch.eskaton.asn4j.parser.ast.types.ComponentType.CompType;
import ch.eskaton.asn4j.parser.ast.types.ExternalTypeReference;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.asn4j.parser.ast.types.SequenceType;
import ch.eskaton.asn4j.parser.ast.types.SetType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.runtime.annotations.ASN1Component;
import ch.eskaton.commons.collections.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ch.eskaton.asn4j.compiler.CompilerUtils.formatName;

public class ComponentTypeCompiler implements UnNamedCompiler<ComponentType> {

    public List<Tuple2<String, CompiledType>> compile(CompilerContext ctx, CompiledCollectionType compiledType,
            ComponentType node, Optional<Parameters> maybeParameters) {
        switch (node.getCompType()) {
            case NAMED_TYPE_OPT:
                // fall through
            case NAMED_TYPE_DEF:
                // fall through
            case NAMED_TYPE:
                return compileComponentNamedType(ctx, compiledType, node, node.getNamedType(), maybeParameters);
            case TYPE:
                return compileComponentType(ctx, compiledType, node.getType(), maybeParameters);
            default:
                throw new IllegalCompilerStateException(node.getPosition(), "Unsupported component type: %s",
                        node.getCompType());
        }
    }

    private List<Tuple2<String, CompiledType>> compileComponentNamedType(CompilerContext ctx,
            CompiledCollectionType compiledType, ComponentType component, NamedType namedType,
            Optional<Parameters> maybeParameters) {
        var javaClass = ctx.getCurrentClass();
        var type = ctx.resolveSelectedType(namedType.getType());
        var compAnnotation = new JavaAnnotation(ASN1Component.class);
        var hasDefault = component.getCompType() == CompType.NAMED_TYPE_DEF;
        var isOptional = component.getCompType() == CompType.NAMED_TYPE_OPT;
        var compiledComponent = ctx.defineType(namedType, maybeParameters);
        var typeName = compiledComponent.getName();
        var field = new JavaDefinedField(typeName, formatName(namedType.getName()), hasDefault);

        compiledComponent.setParent(compiledType);
        compiledType.getComponents().add(Tuple2.of(namedType.getName(), compiledComponent));

        if (isOptional) {
            compiledComponent.setOptional(true);
            compAnnotation.addParameter("optional", "true");
        } else if (hasDefault) {
            compAnnotation.addParameter("hasDefault", "true");

            if (type instanceof Choice) {
                javaClass.addStaticImport(ch.eskaton.commons.utils.Utils.class, "with");
            }

            ctx.compileDefault(javaClass, field.getName(), typeName, type, component.getValue());
        }

        field.addAnnotation(compAnnotation);

        var tags = compiledComponent.getTags();

        if (tags.isPresent() && !tags.get().isEmpty()) {
            field.addAnnotation(CompilerUtils.getTagsAnnotation(tags.get()));
        }

        javaClass.addField(field);

        return List.of(Tuple2.of(namedType.getName(), compiledComponent));
    }

    private List<Tuple2<String, CompiledType>> compileComponentType(CompilerContext ctx,
            CompiledCollectionType compiledType, Type type, Optional<Parameters> maybeParameters) {
        Optional<TypeAssignmentNode> assignment;
        Type referencedType;

        if (type instanceof TypeReference typeReference) {
            var refTypeName = typeReference.getType();

            if (maybeParameters.isPresent()) {
                refTypeName = ctx.getTypeParameter(maybeParameters.get(), typeReference)
                        .filter(TypeReference.class::isInstance)
                        .map(TypeReference.class::cast)
                        .map(TypeReference::getType)
                        .orElse(refTypeName);
            }

            assignment = ctx.getTypeAssignment(refTypeName);

            if (assignment.isEmpty()) {
                throw new CompilerException("Type %s referenced but not defined", refTypeName);
            }

            referencedType = assignment.get().getType();

            ctx.duplicateModule();
        } else if (type instanceof ExternalTypeReference typeReference) {
            var refTypeName = typeReference.getType();
            var refModuleName = typeReference.getModule();

            assignment = ctx.getTypeAssignment(refModuleName, refTypeName);

            ctx.resolveBaseType(refModuleName, refTypeName);

            if (assignment.isEmpty()) {
                throw new CompilerException(typeReference.getPosition(),
                        "Type %s from Module %s referenced but not defined or not exported",
                        refTypeName, refModuleName);
            }

            referencedType = assignment.get().getType();

            ctx.pushModule(refModuleName);
        } else {
            throw new CompilerException(type.getPosition(), "Unimplemented type %s", type);
        }

        List<ComponentType> componentTypes;

        if (referencedType instanceof SetType) {
            componentTypes = ((SetType) referencedType).getAllRootComponents();
        } else if (referencedType instanceof SequenceType) {
            componentTypes = ((SequenceType) referencedType).getAllRootComponents();
        } else {
            throw new CompilerException(referencedType.getPosition(), "Components of type %s not supported",
                    referencedType);
        }

        var components = new ArrayList<Tuple2<String, CompiledType>>();

        for (ComponentType referencedComponent : componentTypes) {
            components.addAll(ctx.<ComponentType, ComponentTypeCompiler>getCompiler(ComponentType.class)
                    .compile(ctx, compiledType, referencedComponent, Optional.empty()));
        }

        ctx.popModule();

        return components;
    }

}
