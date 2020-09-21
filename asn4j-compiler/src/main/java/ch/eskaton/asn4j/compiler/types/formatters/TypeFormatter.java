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
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.parser.ast.Node;
import ch.eskaton.asn4j.parser.ast.types.BMPString;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.BooleanType;
import ch.eskaton.asn4j.parser.ast.types.Choice;
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
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.types.UTF8String;
import ch.eskaton.asn4j.parser.ast.types.UniversalString;
import ch.eskaton.asn4j.parser.ast.types.VideotexString;
import ch.eskaton.asn4j.parser.ast.types.VisibleString;
import ch.eskaton.commons.collections.Tuple2;
import ch.eskaton.commons.utils.Dispatcher;

import java.util.function.BiFunction;

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
import static ch.eskaton.commons.utils.Utils.callWith;

public class TypeFormatter {

    public static final Dispatcher<Type, Class<? extends Type>, Tuple2<CompilerContext, ? extends Type>, String> DISPATCHER =
            new Dispatcher<Type, Class<? extends Type>, Tuple2<CompilerContext, ? extends Type>, String>()
                    .withComparator((t, u) -> u.isInstance(t))
                    .withException(t -> new CompilerException("Formatter for type %s not defined", t));

    static {
        addCase(BooleanType.class, new BooleanFormatter()::format);
        addCase(IntegerType.class, new IntegerFormatter()::format);
        addCase(EnumeratedType.class, new EnumeratedFormatter()::format);
        addCase(BitString.class, new BitStringFormatter()::format);
        addCase(OctetString.class, new OctetStringFormatter()::format);
        addCase(Null.class, new DefaultTypeFormatter<Null>(NULL)::format);
        addCase(ObjectIdentifier.class, new ObjectIdentifierFormatter()::format);
        addCase(RelativeOID.class, new RelativeOIDFormatter()::format);
        addCase(IRI.class, new IRIFormatter()::format);
        addCase(RelativeIRI.class, new RelativeIRIFormatter()::format);
        addCase(SequenceType.class, new SequenceFormatter()::format);
        addCase(SetType.class, new SetFormatter()::format);
        addCase(SequenceOfType.class, new SequenceOfFormatter()::format);
        addCase(SetOfType.class, new SetOfFormatter()::format);
        addCase(Choice.class, new ChoiceFormatter()::format);
        addCase(VisibleString.class, new DefaultTypeFormatter<VisibleString>(VISIBLE_STRING)::format);
        addCase(ISO646String.class, new DefaultTypeFormatter<ISO646String>(ISO646_STRING)::format);
        addCase(NumericString.class, new DefaultTypeFormatter<NumericString>(NUMERIC_STRING)::format);
        addCase(PrintableString.class, new DefaultTypeFormatter<PrintableString>(PRINTABLE_STRING)::format);
        addCase(IA5String.class, new DefaultTypeFormatter<IA5String>(IA5_STRING)::format);
        addCase(GraphicString.class, new DefaultTypeFormatter<GraphicString>(GRAPHIC_STRING)::format);
        addCase(GeneralString.class, new DefaultTypeFormatter<GeneralString>(GENERAL_STRING)::format);
        addCase(TeletexString.class, new DefaultTypeFormatter<TeletexString>(TELETEX_STRING)::format);
        addCase(T61String.class, new DefaultTypeFormatter<T61String>(T61_STRING)::format);
        addCase(VideotexString.class, new DefaultTypeFormatter<VideotexString>(VIDEOTEX_STRING)::format);
        addCase(UTF8String.class, new DefaultTypeFormatter<UTF8String>(UTF8_STRING)::format);
        addCase(UniversalString.class, new DefaultTypeFormatter<UniversalString>(UNIVERSAL_STRING)::format);
        addCase(BMPString.class, new DefaultTypeFormatter<BMPString>(BMP_STRING)::format);
        addCase(TypeReference.class, new TypeReferenceFormatter()::format);
    }

    private TypeFormatter() {
    }

    public static String formatType(CompilerContext ctx, Node node) {
        if (node instanceof Type type) {
            return DISPATCHER.execute(type, Tuple2.of(ctx, type));
        }

        throw new IllegalCompilerStateException("Invalid type: %s", node.getClass().getSimpleName());
    }

    private static <T extends Type> void addCase(Class<T> typeClass, BiFunction<CompilerContext, T, String> formatter) {
        DISPATCHER.withCase(typeClass,
                maybeArgs -> callWith(args -> formatter.apply(args.get_1(), (T) args.get_2()), maybeArgs.get()));
    }

}
