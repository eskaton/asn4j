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

package ch.eskaton.asn4j.compiler.resolvers;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.parser.ast.TypeAssignmentNode;
import ch.eskaton.asn4j.parser.ast.types.Choice;
import ch.eskaton.asn4j.parser.ast.types.ExternalTypeReference;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.values.ChoiceValue;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.util.Optional;

import static ch.eskaton.asn4j.compiler.utils.TypeFormatter.formatType;

public class ChoiceValueResolver extends AbstractValueResolver<ChoiceValue> {

    public ChoiceValueResolver(CompilerContext compilerContext) {
        super(compilerContext);
    }

    @Override
    public ChoiceValue resolveGeneric(Type type, Value value) {
        if (value instanceof SimpleDefinedValue) {
            return resolve((SimpleDefinedValue) value);
        }

        if (value instanceof ChoiceValue) {
            final var choiceValue = (ChoiceValue) value;
            var resolvedType = ctx.resolveType(type);

            Type valueType = ((ChoiceValue) value).getType();

            if (valueType != null) {
                // if the type is null, the value is defined in the context of the currently compiled CHOICE,
                // otherwise it must match the type of the latter
                checkTypes(resolvedType, ctx.resolveType(valueType));
            }

            Choice choiceType;

            if (type instanceof Choice) {
                choiceType = (Choice) type;
            } else {
                String typeName;
                Optional<TypeAssignmentNode> typeAssignment;

                if (resolvedType instanceof TypeReference) {
                    typeName = ((TypeReference) resolvedType).getType();
                    typeAssignment = ctx.getTypeAssignment(typeName);
                } else if (resolvedType instanceof ExternalTypeReference) {
                    typeName = ((ExternalTypeReference) resolvedType).getType();
                    typeAssignment = ctx.getTypeAssignment(((ExternalTypeReference) resolvedType).getModule());
                } else {
                    throw new IllegalCompilerStateException("Unsupported type: %s", formatType(ctx, resolvedType));
                }

                if (typeAssignment.isEmpty()) {
                    throw new CompilerException("Failed to resolve type: %s", typeName);
                }

                choiceType = (Choice) typeAssignment.get().getType();
            }

            var maybeNamedType = choiceType.getAllAlternatives().stream()
                    .filter(nt -> nt.getName().equals(choiceValue.getId())).findFirst();

            if (!maybeNamedType.isPresent()) {
                throw new CompilerException("Invalid component %s in %s", choiceValue.getId(), TypeName.CHOICE);
            }

            var namedType = maybeNamedType.get();
            var resolvedValue = ctx.resolveGenericValue(ctx.getValueType(namedType.getType()), namedType.getType(), choiceValue.getValue());

            choiceValue.setValue(resolvedValue);

            return choiceValue;
        }

        throw new CompilerException("Failed to resolve a %s value", TypeName.CHOICE);
    }

    private void checkTypes(Type type1, Type type2) {
        if (!type1.equals(type2)) {
            throw new CompilerException("Can't use a value of type %s where %s is expected",
                    formatType(ctx, type2), formatType(ctx, type1));
        }
    }

}
