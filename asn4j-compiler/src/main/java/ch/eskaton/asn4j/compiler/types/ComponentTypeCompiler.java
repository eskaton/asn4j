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
import ch.eskaton.asn4j.compiler.results.CompiledCollectionComponent;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.compiler.results.CompiledValue;
import ch.eskaton.asn4j.compiler.types.formatters.TypeFormatter;
import ch.eskaton.asn4j.parser.ast.types.ComponentType;
import ch.eskaton.asn4j.parser.ast.types.ComponentType.CompType;
import ch.eskaton.asn4j.parser.ast.types.ExternalTypeReference;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.types.UsefulType;
import ch.eskaton.asn4j.parser.ast.values.Value;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        // TODO: handle parameterized type
        CompiledType compiledComponent;

        if (CompilerUtils.isTypeReference(namedType.getType()) && !(CompilerUtils.isUsefulType(namedType.getType()))) {
            compiledComponent = CompilerUtils.compileTypeReference(ctx, (TypeReference) namedType.getType(),
                    maybeParameters);
        } else {
            compiledComponent = ctx.defineType(namedType, maybeParameters);
        }

        compiledComponent.setParent(compiledType);

        var compiledCollectionComponent = new CompiledCollectionComponent(namedType.getName(), compiledComponent,
                isOptional, isRoot);

        compiledType.getComponents().add(compiledCollectionComponent);

        var maybeDefaultValue = getDefaultValue(ctx, component, maybeParameters, compiledComponent);

        compiledCollectionComponent.setDefault(maybeDefaultValue);

        return List.of(compiledCollectionComponent);
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
        var compiledCollectionType = resolveCompiledCollectionType(ctx, compiledType, type, maybeParameters);
        var compiledCollectionComponents = getComponentTypes(compiledCollectionType);

        compiledType.getComponents().addAll(compiledCollectionComponents);

        return compiledCollectionComponents;
    }

    private CompiledCollectionType resolveCompiledCollectionType(CompilerContext ctx,
            CompiledCollectionType compiledType, Type type, Optional<Parameters> maybeParameters) {
        var collectionType = compiledType.getType();
        CompiledType compiledCollectionType = null;

        if (collectionType.getClass().isAssignableFrom(type.getClass())) {
            compiledCollectionType = ctx.<Type, TypeCompiler>getCompiler(Type.class).compile(ctx, null, type,
                    maybeParameters);
        } else if (type instanceof TypeReference typeReference) {
            compiledCollectionType = resolveTypeReference(ctx, typeReference, maybeParameters);
        } else if (type instanceof ExternalTypeReference typeReference) {
            compiledCollectionType = resolveExternalTypeReference(ctx, typeReference);
        }

        if (!(compiledCollectionType instanceof CompiledCollectionType)) {
            var formattedType = TypeFormatter.getTypeName(type);

            throw new CompilerException(type.getPosition(), "Invalid type '%s' in COMPONENTS OF '%s'",
                    formattedType, compiledType.getName());
        }

        return (CompiledCollectionType) compiledCollectionType;
    }

    private List<CompiledCollectionComponent> getComponentTypes(CompiledCollectionType compiledCollectionType) {
        return compiledCollectionType.getComponents().stream()
                .filter(CompiledCollectionComponent::isRoot)
                .collect(Collectors.toList());
    }

    private CompiledType resolveTypeReference(CompilerContext ctx, TypeReference typeReference,
            Optional<Parameters> maybeParameters) {
        var referencedTypeName = typeReference.getType();
        var compiledComponentType = CompilerUtils.compileTypeReference(ctx, typeReference, maybeParameters);

        if (compiledComponentType == null) {
            throw new CompilerException("Type %s referenced but not defined", referencedTypeName);
        }

        return compiledComponentType;
    }

    private CompiledType resolveExternalTypeReference(CompilerContext ctx, ExternalTypeReference typeReference) {
        var refTypeName = typeReference.getType();
        var refModuleName = typeReference.getModule();
        var compiledComponentType = ctx.getCompiledType(refModuleName, refTypeName);

        if (compiledComponentType == null) {
            throw new CompilerException(typeReference.getPosition(),
                    "Type %s from Module %s referenced but not defined or not exported",
                    refTypeName, refModuleName);
        }

        return compiledComponentType;
    }

}
