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

import ch.eskaton.asn4j.compiler.java.JavaAnnotation;
import ch.eskaton.asn4j.compiler.java.JavaClass;
import ch.eskaton.asn4j.compiler.java.JavaDefinedField;
import ch.eskaton.asn4j.parser.ast.ModuleNode.TagMode;
import ch.eskaton.asn4j.parser.ast.TypeAssignmentNode;
import ch.eskaton.asn4j.parser.ast.types.ComponentType;
import ch.eskaton.asn4j.parser.ast.types.ComponentType.CompType;
import ch.eskaton.asn4j.parser.ast.types.ExternalTypeReference;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.asn4j.parser.ast.types.SequenceType;
import ch.eskaton.asn4j.parser.ast.types.SetType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.values.Tag;
import ch.eskaton.asn4j.runtime.TaggingMode;
import ch.eskaton.asn4j.runtime.annotations.ASN1Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ComponentTypeCompiler implements UnNamedCompiler<ComponentType> {

    public void compile(CompilerContext ctx, ComponentType node) throws CompilerException {

        TagMode mode = ctx.getModule().getTagMode();

        switch (node.getCompType()) {
            case NamedTypeOpt:
                // fall through
            case NamedTypeDef:
                // fall through
            case NamedType:
                compileComponentNamedType(ctx, node, node.getNamedType());
                break;
            case Type:
                compileComponentType(ctx, mode, node.getType());
                return;
            default:
                throw new CompilerException("Unsupported ComponentType: " + node.getCompType());
        }
    }

    private void compileComponentNamedType(CompilerContext ctx, ComponentType component, NamedType namedType)
            throws CompilerException {
        JavaClass javaClass = ctx.getCurrentClass();
        Type type = namedType.getType();
        TaggingMode taggingMode = type.getTaggingMode();
        Tag tag = ctx.resolveType(type).getTag();
        JavaDefinedField field = new JavaDefinedField(ctx.getTypeName(namedType), CompilerUtils
                .formatName(namedType.getName()));
        JavaAnnotation compAnnotation = new JavaAnnotation(ASN1Component.class);

        if (component.getCompType() == CompType.NamedTypeOpt) {
            compAnnotation.addParameter("optional", "true");
        } else if (component.getCompType() == CompType.NamedTypeDef) {
            compAnnotation.addParameter("hasDefault", "true");
            ctx.compileDefault(javaClass, field.getName(), component);
        }

        field.addAnnotation(compAnnotation);

        if (tag != null) {
            field.addAnnotation(CompilerUtils.getTagAnnotation(ctx.getModule(), tag, taggingMode));
        }

        javaClass.addField(field);
    }

    private Collection<String> compileComponentType(CompilerContext ctx, TagMode mode, Type type)
            throws CompilerException {
        TypeAssignmentNode assignment;

        if (type instanceof TypeReference) {
            TypeReference typeRef = (TypeReference) type;
            String refTypeName = typeRef.getType();

            assignment = ctx.getTypeAssignment(refTypeName, null);

            if (assignment == null) {
                throw new CompilerException("Type " + refTypeName + " referenced but not defined");
            }

            ctx.duplicateModule();
        } else if (type instanceof ExternalTypeReference) {
            ExternalTypeReference typeRef = (ExternalTypeReference) type;
            String refTypeName = typeRef.getType();
            String refModuleName = typeRef.getModule();

            assignment = ctx.getTypeAssignment(refTypeName, refModuleName);

            if (assignment == null) {
                throw new CompilerException("Type " + refTypeName + " from Module " + refModuleName +
                        " referenced but not defined or not exported");
            }

            ctx.pushModule(refModuleName);
        } else {
            throw new CompilerException("Unimplemented type " + type);
        }

        Type referencedType = assignment.getType();
        List<ComponentType> componentTypes;

        if (referencedType instanceof SetType) {
            componentTypes = ((SetType) referencedType).getAllComponents();
        } else if (referencedType instanceof SequenceType) {
            componentTypes = ((SequenceType) referencedType).getAllComponents();
        } else {
            throw new CompilerException("Components of type " + referencedType + " not supported");
        }

        List<String> fieldNames = new ArrayList<>();

        for (ComponentType referencedComponent : componentTypes) {
            ctx.<ComponentType, ComponentTypeCompiler>getCompiler(ComponentType.class).compile(ctx, referencedComponent);
        }

        ctx.popModule();

        return fieldNames;
    }

}
