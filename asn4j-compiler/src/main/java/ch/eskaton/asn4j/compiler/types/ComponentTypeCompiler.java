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

package ch.eskaton.asn4j.compiler.types;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.CompilerUtils;
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.compiler.Parameters;
import ch.eskaton.asn4j.compiler.UnNamedCompiler;
import ch.eskaton.asn4j.compiler.java.objs.JavaAnnotation;
import ch.eskaton.asn4j.compiler.java.objs.JavaDefinedField;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionComponent;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.compiler.results.CompiledValue;
import ch.eskaton.asn4j.compiler.types.formatters.TypeFormatter;
import ch.eskaton.asn4j.parser.ast.types.Choice;
import ch.eskaton.asn4j.parser.ast.types.ComponentType;
import ch.eskaton.asn4j.parser.ast.types.ComponentType.CompType;
import ch.eskaton.asn4j.parser.ast.types.ExternalTypeReference;
import ch.eskaton.asn4j.parser.ast.types.SequenceType;
import ch.eskaton.asn4j.parser.ast.types.SetType;
import ch.eskaton.asn4j.parser.ast.types.SimpleDefinedType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.annotations.ASN1Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ch.eskaton.asn4j.compiler.CompilerUtils.formatName;

public class ComponentTypeCompiler implements UnNamedCompiler<ComponentType> {

    public List<CompiledCollectionComponent> compile(CompilerContext ctx, CompiledCollectionType compiledType,
            ComponentType node, boolean isRoot, Optional<Parameters> maybeParameters) {
        switch (node.getCompType()) {
            case NAMED_TYPE_OPT:
                // fall through
            case NAMED_TYPE_DEF:
                // fall through
            case NAMED_TYPE:
                return compileComponentNamedType(ctx, compiledType, node, isRoot, maybeParameters);
            case TYPE:
                return compileComponentType(ctx, compiledType, node, isRoot, maybeParameters);
            default:
                throw new IllegalCompilerStateException(node.getPosition(), "Unsupported component type: %s",
                        node.getCompType());
        }
    }

    private List<CompiledCollectionComponent> compileComponentNamedType(CompilerContext ctx,
            CompiledCollectionType compiledType, ComponentType component, boolean isRoot,
            Optional<Parameters> maybeParameters) {
        var isOptional = component.getCompType() == CompType.NAMED_TYPE_OPT;
        var namedType = component.getNamedType();
        var compiledComponent = ctx.defineType(namedType, maybeParameters);

        compiledComponent.setParent(compiledType);

        var compiledCollectionComponent = new CompiledCollectionComponent(namedType.getName(), compiledComponent,
                isOptional, isRoot);

        compiledType.getComponents().add(compiledCollectionComponent);

        var maybeDefaultValue = getDefaultValue(ctx, component, maybeParameters, compiledComponent);

        compiledCollectionComponent.setDefault(maybeDefaultValue);

        addField(ctx, compiledCollectionComponent);

        return List.of(compiledCollectionComponent);
    }

    private void addField(CompilerContext ctx, CompiledCollectionComponent compiledCollectionComponent) {
        var compiledType = compiledCollectionComponent.getCompiledType();
        var maybeDefaultValue = compiledCollectionComponent.getDefaultValue();
        var hasDefault = maybeDefaultValue.isPresent();
        var isOptional = compiledCollectionComponent.isOptional();
        var type = compiledType.getType();
        var javaClass = ctx.getCurrentClass();
        var javaTypeName = compiledType.getName();
        var javaFieldName = formatName(compiledCollectionComponent.getName());
        var field = new JavaDefinedField(javaTypeName, javaFieldName, hasDefault);
        var compAnnotation = new JavaAnnotation(ASN1Component.class);

        if (isOptional) {
            compAnnotation.addParameter("optional", "true");
        } else if (hasDefault) {
            compAnnotation.addParameter("hasDefault", "true");

            if (type instanceof Choice) {
                javaClass.addStaticImport(ch.eskaton.commons.utils.Utils.class, "with");
            }

            var defaultValue = maybeDefaultValue.get();

            ctx.addDefaultField(ctx, javaClass, field.getName(), javaTypeName, defaultValue);
        }

        field.addAnnotation(compAnnotation);

        var tags = compiledType.getTags();

        if (tags.isPresent() && !tags.get().isEmpty()) {
            field.addAnnotation(CompilerUtils.getTagsAnnotation(tags.get()));
        }

        javaClass.addField(field);
    }

    private Optional<CompiledValue<Value>> getDefaultValue(CompilerContext ctx, ComponentType component,
            Optional<Parameters> maybeParameters, CompiledType compiledComponent) {
        var hasDefault = component.getCompType() == CompType.NAMED_TYPE_DEF;

        if (hasDefault) {
            var defaultValue = ctx.compileDefault(compiledComponent.getType(), component.getValue(), maybeParameters);

            return Optional.of(defaultValue);
        }

        return Optional.empty();
    }

    private List<CompiledCollectionComponent> compileComponentType(CompilerContext ctx,
            CompiledCollectionType compiledType, ComponentType componentType, boolean isRoot,
            Optional<Parameters> maybeParameters) {
        var type = componentType.getType();
        var resolvedType = resolveType(ctx, compiledType, type, maybeParameters);
        var resolvedComponentTypes = getComponentTypes(resolvedType);
        var components = new ArrayList<CompiledCollectionComponent>();

        for (var resolvedComponentType : resolvedComponentTypes) {
            var componentTypeCompiler = ctx.<ComponentType, ComponentTypeCompiler>getCompiler(ComponentType.class);
            var compiledComponents = componentTypeCompiler.compile(ctx, compiledType, resolvedComponentType, isRoot,
                    Optional.empty());

            components.addAll(compiledComponents);
        }

        return components;
    }

    private Type resolveType(CompilerContext ctx, CompiledCollectionType compiledType, Type type,
            Optional<Parameters> maybeParameters) {
        var collectionType = compiledType.getType();

        if (collectionType.getClass().isAssignableFrom(type.getClass())) {
            return type;
        } else if (type instanceof TypeReference typeReference) {
            return resolveTypeReference(ctx, typeReference, maybeParameters);
        } else if (type instanceof ExternalTypeReference typeReference) {
            return resolveExternalTypeReference(ctx, typeReference);
        } else {
            var formattedType = TypeFormatter.getTypeName(type);

            throw new CompilerException(type.getPosition(), "Invalid type '%s' in COMPONENTS OF '%s'",
                    formattedType, compiledType.getName());
        }
    }

    private List<ComponentType> getComponentTypes(Type resolvedType) {
        if (resolvedType instanceof SetType) {
            return ((SetType) resolvedType).getAllRootComponents();
        } else if (resolvedType instanceof SequenceType) {
            return ((SequenceType) resolvedType).getAllRootComponents();
        } else {
            throw new CompilerException(resolvedType.getPosition(), "Components of type %s not supported",
                    resolvedType);
        }
    }

    private Type resolveTypeReference(CompilerContext ctx, TypeReference typeReference,
            Optional<Parameters> maybeParameters) {
        var referencedTypeName = typeReference.getType();
        var compiledComponentType = compileTypeReference(ctx, typeReference, maybeParameters);

        if (compiledComponentType == null) {
            throw new CompilerException("Type %s referenced but not defined", referencedTypeName);
        }

        return compiledComponentType.getType();
    }

    private CompiledType compileTypeReference(CompilerContext ctx, TypeReference typeReference,
            Optional<Parameters> maybeParameters) {
        var referencedTypeName = typeReference.getType();

        if (maybeParameters.isPresent()) {
            return ctx.getTypeParameter(maybeParameters.get(), typeReference)
                    .filter(SimpleDefinedType.class::isInstance)
                    .map(SimpleDefinedType.class::cast)
                    .map(ctx::getCompiledType)
                    .orElseGet(() -> ctx.getCompiledType(referencedTypeName));
        } else {
            return ctx.getCompiledType(referencedTypeName);
        }
    }

    private Type resolveExternalTypeReference(CompilerContext ctx, ExternalTypeReference typeReference) {
        var refTypeName = typeReference.getType();
        var refModuleName = typeReference.getModule();
        var compiledComponentType = ctx.getCompiledType(refModuleName, refTypeName);

        if (compiledComponentType == null) {
            throw new CompilerException(typeReference.getPosition(),
                    "Type %s from Module %s referenced but not defined or not exported",
                    refTypeName, refModuleName);
        }

        return compiledComponentType.getType();
    }

}
