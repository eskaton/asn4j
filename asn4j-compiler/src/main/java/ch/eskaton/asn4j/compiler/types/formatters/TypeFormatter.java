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

package ch.eskaton.asn4j.compiler.types.formatters;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.parser.ast.Node;
import ch.eskaton.asn4j.parser.ast.types.BMPString;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.BooleanType;
import ch.eskaton.asn4j.parser.ast.types.Choice;
import ch.eskaton.asn4j.parser.ast.types.CollectionOfType;
import ch.eskaton.asn4j.parser.ast.types.EnumeratedType;
import ch.eskaton.asn4j.parser.ast.types.GeneralString;
import ch.eskaton.asn4j.parser.ast.types.GraphicString;
import ch.eskaton.asn4j.parser.ast.types.IA5String;
import ch.eskaton.asn4j.parser.ast.types.IRI;
import ch.eskaton.asn4j.parser.ast.types.ISO646String;
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
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

import static ch.eskaton.asn4j.runtime.types.TypeName.BMP_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.GENERAL_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.GRAPHIC_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.IA5_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.ISO646_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.NULL;
import static ch.eskaton.asn4j.runtime.types.TypeName.NUMERIC_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.PRINTABLE_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.T61_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.TELETEX_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.UNIVERSAL_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.UTF8_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.VIDEOTEX_STRING;
import static ch.eskaton.asn4j.runtime.types.TypeName.VISIBLE_STRING;

public class TypeFormatter<S extends CollectionOfType> {

    private TypeFormatter() {
    }

    public static String formatType(CompilerContext ctx, Node node) {
        if (node instanceof SequenceType type) {
            return new SequenceFormatter().format(ctx, type);
        } else if (node instanceof SetType type) {
            return new SetTypeFormatter().format(ctx, type);
        } else if (node instanceof SequenceOfType type) {
            return new SequenceOfFormatter().format(ctx, type);
        } else if (node instanceof SetOfType type) {
            return new SetOfFormatter().format(ctx, type);
        } else if (node instanceof Choice type) {
            return new ChoiceFormatter().format(ctx, type);
        } else if (node instanceof BooleanType type) {
            return new BooleanFormatter().format(ctx, type);
        } else if (node instanceof IntegerType type) {
            return new IntegerFormatter().format(ctx, type);
        } else if (node instanceof EnumeratedType type) {
            return new EnumeratedFormatter().format(ctx, type);
        } else if (node instanceof BitString type) {
            return new BitStringFormatter().format(ctx, type);
        } else if (node instanceof OctetString type) {
            return new OctetStringFormatter().format(ctx, type);
        } else if (node instanceof Null type) {
            return new DefaultTypeFormatter<Null>(NULL).format(ctx, type);
        } else if (node instanceof ObjectIdentifier type) {
            return new ObjectIdentifierFormatter().format(ctx, type);
        } else if (node instanceof RelativeOID type) {
            return new RelativeOIDFormatter().format(ctx, type);
        } else if (node instanceof IRI type) {
            return new IRIFormatter().format(ctx, type);
        } else if (node instanceof RelativeIRI type) {
            return new RelativeIRIFormatter().format(ctx, type);
        } else if (node instanceof VisibleString type) {
            return new DefaultTypeFormatter<VisibleString>(VISIBLE_STRING).format(ctx, type);
        } else if (node instanceof ISO646String type) {
            return new DefaultTypeFormatter<ISO646String>(ISO646_STRING).format(ctx, type);
        } else if (node instanceof NumericString type) {
            return new DefaultTypeFormatter<NumericString>(NUMERIC_STRING).format(ctx, type);
        } else if (node instanceof PrintableString type) {
            return new DefaultTypeFormatter<PrintableString>(PRINTABLE_STRING).format(ctx, type);
        } else if (node instanceof IA5String type) {
            return new DefaultTypeFormatter<IA5String>(IA5_STRING).format(ctx, type);
        } else if (node instanceof GraphicString type) {
            return new DefaultTypeFormatter<GraphicString>(GRAPHIC_STRING).format(ctx, type);
        } else if (node instanceof GeneralString type) {
            return new DefaultTypeFormatter<GeneralString>(GENERAL_STRING).format(ctx, type);
        } else if (node instanceof TeletexString type) {
            return new DefaultTypeFormatter<TeletexString>(TELETEX_STRING).format(ctx, type);
        } else if (node instanceof T61String type) {
            return new DefaultTypeFormatter<T61String>(T61_STRING).format(ctx, type);
        } else if (node instanceof VideotexString type) {
            return new DefaultTypeFormatter<VideotexString>(VIDEOTEX_STRING).format(ctx, type);
        } else if (node instanceof UTF8String type) {
            return new DefaultTypeFormatter<UTF8String>(UTF8_STRING).format(ctx, type);
        } else if (node instanceof UniversalString type) {
            return new DefaultTypeFormatter<UniversalString>(UNIVERSAL_STRING).format(ctx, type);
        } else if (node instanceof BMPString type) {
            return new DefaultTypeFormatter<BMPString>(BMP_STRING).format(ctx, type);
        } else if (node instanceof TypeReference type) {
            return new TypeReferenceFormatter().format(ctx, type);
        }

        throw new IllegalCompilerStateException("Formatter for node %s not defined", node.getClass());
    }

}
