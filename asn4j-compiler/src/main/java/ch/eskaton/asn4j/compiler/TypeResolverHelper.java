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

import ch.eskaton.asn4j.parser.ast.ModuleNode;
import ch.eskaton.asn4j.parser.ast.TypeAssignmentNode;
import ch.eskaton.asn4j.parser.ast.types.Choice;
import ch.eskaton.asn4j.parser.ast.types.GeneralizedTime;
import ch.eskaton.asn4j.parser.ast.types.SelectionType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.types.UTCTime;

import java.util.Optional;

import static ch.eskaton.asn4j.parser.NoPosition.NO_POSITION;

public class TypeResolverHelper {

    private CompilerContext ctx;

    public TypeResolverHelper(CompilerContext ctx) {
        this.ctx = ctx;
    }

    private boolean isUsefulType(Type type) {
        return isUsefulType(type.getClass().getSimpleName());
    }

    boolean isUsefulType(String typeName) {
        if (GeneralizedTime.class.getSimpleName().equals(typeName)) {
            return true;
        } else if (UTCTime.class.getSimpleName().equals(typeName)) {
            return true;
        }

        return false;
    }

    private Type resolveUsefulType(String typeName) {
        if (GeneralizedTime.class.getSimpleName().equals(typeName)) {
            return new GeneralizedTime(NO_POSITION, typeName);
        } else if (UTCTime.class.getSimpleName().equals(typeName)) {
            return new UTCTime(NO_POSITION, typeName);
        }

        return null;
    }

    Type resolveType(ModuleNode module, String typeName) {
        if (isUsefulType(typeName)) {
            return resolveUsefulType(typeName);
        }

        Optional<TypeAssignmentNode> assignment = ctx.getTypeAssignment(module, typeName);

        if (assignment.isEmpty()) {
            var moduleName = ctx.findImport(typeName);

            if (moduleName.isPresent()) {
                return resolveBaseType(ctx.getModule(moduleName.get()), typeName);
            }

            throw new CompilerException("Failed to resolve a type: %s", typeName);
        }

        return assignment.get().getType();
    }

    Type resolveBaseType(ModuleNode module, String typeName) {
        var type = resolveType(module, typeName);

        while (true) {
            if (isUsefulType(type)) {
                return type;
            } else if (type instanceof TypeReference) {
                type = resolveType(module, ((TypeReference) type).getType());
            } else {
                return type;
            }
        }
    }

    Type resolveTypeReference(Type typeReference) {
        while (typeReference instanceof TypeReference) {
            if (isUsefulType(typeReference)) {
                return typeReference;
            }

            Optional<TypeAssignmentNode> assignment = ctx.getTypeAssignment(((TypeReference) typeReference).getType());

            if (assignment.isPresent()) {
                var node = assignment.get().getType();

                if (!(node instanceof Type)) {
                    throw new CompilerException("Invalid type: %s", node.getClass().getSimpleName());
                }

                typeReference = node;
            } else {
                Type type = resolveType(ctx.getModule(), ((TypeReference) typeReference).getType());

                if (!(type instanceof TypeReference)) {
                    return type;
                }
            }
        }

        return typeReference;
    }

    public Type resolveSelectedType(Type type) {
        if (type instanceof SelectionType) {
            String selectedId = ((SelectionType) type).getId();
            Type selectedType = ((SelectionType) type).getType();

            if (selectedType instanceof TypeReference) {
                var collectionType = resolveTypeReference(selectedType);

                if (collectionType instanceof Choice) {
                    return ((Choice) collectionType).getRootAlternatives().stream()
                            .filter(t -> t.getName().equals(selectedId))
                            .findFirst()
                            .orElseThrow(() -> new CompilerException("Selected type not found")).getType();
                }

            }

            throw new CompilerException("Selected type not found");
        }

        return type;
    }

}
