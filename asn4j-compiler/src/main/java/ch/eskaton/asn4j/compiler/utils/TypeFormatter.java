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
import ch.eskaton.asn4j.parser.ast.types.BMPString;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.BooleanType;
import ch.eskaton.asn4j.parser.ast.types.Choice;
import ch.eskaton.asn4j.parser.ast.types.ComponentType;
import ch.eskaton.asn4j.parser.ast.types.EnumeratedType;
import ch.eskaton.asn4j.parser.ast.types.GeneralString;
import ch.eskaton.asn4j.parser.ast.types.GraphicString;
import ch.eskaton.asn4j.parser.ast.types.IA5String;
import ch.eskaton.asn4j.parser.ast.types.IRI;
import ch.eskaton.asn4j.parser.ast.types.ISO646String;
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.asn4j.parser.ast.types.Null;
import ch.eskaton.asn4j.parser.ast.types.NumericString;
import ch.eskaton.asn4j.parser.ast.types.ObjectIdentifier;
import ch.eskaton.asn4j.parser.ast.types.OctetString;
import ch.eskaton.asn4j.parser.ast.types.PrintableString;
import ch.eskaton.asn4j.parser.ast.types.RelativeIRI;
import ch.eskaton.asn4j.parser.ast.types.RelativeOID;
import ch.eskaton.asn4j.parser.ast.types.SequenceOfType;
import ch.eskaton.asn4j.parser.ast.types.SequenceType;
import ch.eskaton.asn4j.parser.ast.types.SetOfType;
import ch.eskaton.asn4j.parser.ast.types.SetType;
import ch.eskaton.asn4j.parser.ast.types.T61String;
import ch.eskaton.asn4j.parser.ast.types.TeletexString;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.types.UTF8String;
import ch.eskaton.asn4j.parser.ast.types.UniversalString;
import ch.eskaton.asn4j.parser.ast.types.VideotexString;
import ch.eskaton.asn4j.parser.ast.types.VisibleString;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.runtime.types.TypeName.BIT_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.BMP_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.BOOLEAN;
import static ch.eskaton.asn4j.runtime.types.TypeName.CHOICE;
import static ch.eskaton.asn4j.runtime.types.TypeName.ENUMERATED;
import static ch.eskaton.asn4j.runtime.types.TypeName.GENERAL_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.GRAPHIC_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.IA5_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.INTEGER;
import static ch.eskaton.asn4j.runtime.types.TypeName.ISO646_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.NULL;
import static ch.eskaton.asn4j.runtime.types.TypeName.NUMERIC_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.OCTET_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.OID;
import static ch.eskaton.asn4j.runtime.types.TypeName.OID_IRI;
import static ch.eskaton.asn4j.runtime.types.TypeName.PRINTABLE_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.RELATIVE_OID;
import static ch.eskaton.asn4j.runtime.types.TypeName.RELATIVE_OID_IRI;
import static ch.eskaton.asn4j.runtime.types.TypeName.SEQUENCE;
import static ch.eskaton.asn4j.runtime.types.TypeName.SEQUENCE_OF;
import static ch.eskaton.asn4j.runtime.types.TypeName.SET;
import static ch.eskaton.asn4j.runtime.types.TypeName.SET_OF;
import static ch.eskaton.asn4j.runtime.types.TypeName.T61_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.TELETEX_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.UNIVERSAL_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.UTF8_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.VIDEOTEX_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.VISIBLE_STRING;

public class TypeFormatter {

    private TypeFormatter() {
    }

    public static String formatType(CompilerContext ctx, Node type) {
        if (type instanceof SequenceType) {
            return SEQUENCE + "[" + ((SequenceType) type).getAllComponents().stream()
                    .map(t -> formatComponentType(ctx, t))
                    .collect(Collectors.joining(", ")) + "]";
        } else if (type instanceof SetType) {
            return SET + "[" + ((SetType) type).getAllComponents().stream()
                    .map(t -> formatComponentType(ctx, t))
                    .collect(Collectors.joining(", ")) + "]";
        } else if (type instanceof SequenceOfType) {
            return SEQUENCE_OF + "(" + formatType(ctx, ((SequenceOfType) type).getType()) + ")";
        } else if (type instanceof SetOfType) {
            return SET_OF + "(" + formatType(ctx, ((SetOfType) type).getType()) + ")";
        } else if (type instanceof Choice) {
            return CHOICE + "[" + ((Choice) type).getAllAlternatives().stream()
                    .map(a -> formatNamedType(ctx, a))
                    .collect(Collectors.joining(", ")) + "]";
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
        } else if (type instanceof VisibleString) {
            return VISIBLE_STRING.getName();
        } else if (type instanceof ISO646String) {
            return ISO646_STRING.getName();
        } else if (type instanceof NumericString) {
            return NUMERIC_STRING.getName();
        } else if (type instanceof PrintableString) {
            return PRINTABLE_STRING.getName();
        } else if (type instanceof IA5String) {
            return IA5_STRING.getName();
        } else if (type instanceof GraphicString) {
            return GRAPHIC_STRING.getName();
        } else if (type instanceof GeneralString) {
            return GENERAL_STRING.getName();
        } else if (type instanceof TeletexString) {
            return TELETEX_STRING.getName();
        } else if (type instanceof T61String) {
            return T61_STRING.getName();
        } else if (type instanceof VideotexString) {
            return VIDEOTEX_STRING.getName();
        } else if (type instanceof UTF8String) {
            return UTF8_STRING.getName();
        } else if (type instanceof UniversalString) {
            return UNIVERSAL_STRING.getName();
        } else if (type instanceof BMPString) {
            return BMP_STRING.getName();
        } else if (type instanceof TypeReference) {
            return formatType(ctx, ctx.resolveTypeReference((TypeReference) type));
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
            return formatNamedType(ctx, component.getNamedType());
        }

        throw new IllegalCompilerStateException("Component type %s not handled", component);
    }

    private static String formatNamedType(CompilerContext ctx, NamedType namedType) {
        return "[" + namedType.getName() + ": " + formatType(ctx, namedType.getType()) + "]";
    }

}
