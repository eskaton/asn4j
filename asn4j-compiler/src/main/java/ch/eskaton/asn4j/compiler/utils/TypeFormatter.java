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

package ch.eskaton.asn4j.compiler.utils;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.parser.ast.EnumerationItemNode;
import ch.eskaton.asn4j.parser.ast.NamedBitNode;
import ch.eskaton.asn4j.parser.ast.Node;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.BooleanType;
import ch.eskaton.asn4j.parser.ast.types.ComponentType;
import ch.eskaton.asn4j.parser.ast.types.EnumeratedType;
import ch.eskaton.asn4j.parser.ast.types.IRI;
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.asn4j.parser.ast.types.Null;
import ch.eskaton.asn4j.parser.ast.types.ObjectIdentifier;
import ch.eskaton.asn4j.parser.ast.types.OctetString;
import ch.eskaton.asn4j.parser.ast.types.RelativeIRI;
import ch.eskaton.asn4j.parser.ast.types.RelativeOID;
import ch.eskaton.asn4j.parser.ast.types.SequenceOfType;
import ch.eskaton.asn4j.parser.ast.types.SequenceType;
import ch.eskaton.asn4j.parser.ast.types.SetOfType;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.runtime.types.TypeName.BIT_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.BOOLEAN;
import static ch.eskaton.asn4j.runtime.types.TypeName.ENUMERATED;
import static ch.eskaton.asn4j.runtime.types.TypeName.INTEGER;
import static ch.eskaton.asn4j.runtime.types.TypeName.NULL;
import static ch.eskaton.asn4j.runtime.types.TypeName.OCTET_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.OID;
import static ch.eskaton.asn4j.runtime.types.TypeName.OID_IRI;
import static ch.eskaton.asn4j.runtime.types.TypeName.RELATIVE_OID;
import static ch.eskaton.asn4j.runtime.types.TypeName.RELATIVE_OID_IRI;
import static ch.eskaton.asn4j.runtime.types.TypeName.SEQUENCE;
import static ch.eskaton.asn4j.runtime.types.TypeName.SEQUENCE_OF;
import static ch.eskaton.asn4j.runtime.types.TypeName.SET_OF;

public class TypeFormatter {

    private TypeFormatter() {
    }

    public static String formatType(CompilerContext ctx, Node type) {
        if (type instanceof SequenceType) {
            return SEQUENCE + "[" + ((SequenceType) type).getAllComponents().stream()
                    .map(t -> formatComponentType(ctx, t))
                    .collect(Collectors.joining(", ")) + "]";
        } else if (type instanceof SequenceOfType) {
            return SEQUENCE_OF + "(" + formatType(ctx, ((SequenceOfType) type).getType()) + ")";
        } else if (type instanceof SetOfType) {
            return SET_OF + "(" + formatType(ctx, ((SetOfType) type).getType()) + ")";
        } else if (type instanceof BooleanType) {
            return BOOLEAN.getName();
        } else if (type instanceof IntegerType) {
            return INTEGER.getName();
        } else if (type instanceof EnumeratedType) {
            return ENUMERATED.getName() + "(" + formatItems((EnumeratedType) type) + ")";
        } else if (type instanceof BitString) {
            return BIT_STRING.getName() + "(" + formatItems((BitString) type) + ")";
        } else if (type instanceof OctetString) {
            return OCTET_STRING.getName();
        } else if (type instanceof Null) {
            return NULL.getName();
        } else if (type instanceof ObjectIdentifier) {
            return OID.getName();
        } else if (type instanceof RelativeOID) {
            return RELATIVE_OID.getName();
        } else if (type instanceof IRI) {
            return OID_IRI.getName();
        } else if (type instanceof RelativeIRI) {
            return RELATIVE_OID_IRI.getName();
        } else if (type instanceof TypeReference) {
            return formatType(ctx, ctx.resolveTypeReference(type));
        }

        throw new IllegalCompilerStateException("Formatter for type %s not defined", type.getClass());
    }

    private static String formatItems(BitString type) {
        return Optional.ofNullable(type.getNamedBits())
                .map(items -> items.stream()
                        .map(NamedBitNode::getId)
                        .collect(Collectors.joining(", ")))
                .orElse("");
    }

    private static String formatItems(EnumeratedType type) {
        var rootItems = type.getRootEnum();
        var additionalItems = type.getAdditionalEnum();

        if (additionalItems != null && !additionalItems.isEmpty()) {
            return formatItems(rootItems) + ", " + formatItems(additionalItems);
        }

        return formatItems(rootItems);
    }

    private static String formatItems(List<EnumerationItemNode> enumerationItems) {
        return Optional.ofNullable(enumerationItems)
                .map(items -> items.stream()
                        .map(EnumerationItemNode::getName).
                                collect(Collectors.joining(", ")))
                .orElse("");
    }

    private static String formatComponentType(CompilerContext ctx, ComponentType component) {
        if (component.getType() != null) {
            return "[" + formatType(ctx, component.getType()) + "]";
        } else if (component.getNamedType() != null) {
            NamedType namedType = component.getNamedType();

            return "[" + namedType.getName() + ": " + formatType(ctx, namedType.getType()) + "]";
        }

        throw new IllegalCompilerStateException("Component type %s not handled", component);
    }

}
