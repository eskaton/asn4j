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

package ch.eskaton.asn4j.compiler.java;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.CompilerUtils;
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.compiler.results.CompiledChoiceType;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.AbstractValue;
import ch.eskaton.asn4j.parser.ast.values.BMPStringValue;
import ch.eskaton.asn4j.parser.ast.values.BinaryStringValue;
import ch.eskaton.asn4j.parser.ast.values.BitStringValue;
import ch.eskaton.asn4j.parser.ast.values.BooleanValue;
import ch.eskaton.asn4j.parser.ast.values.ChoiceValue;
import ch.eskaton.asn4j.parser.ast.values.CollectionOfValue;
import ch.eskaton.asn4j.parser.ast.values.CollectionValue;
import ch.eskaton.asn4j.parser.ast.values.EnumeratedValue;
import ch.eskaton.asn4j.parser.ast.values.GeneralStringValue;
import ch.eskaton.asn4j.parser.ast.values.GeneralizedTimeValue;
import ch.eskaton.asn4j.parser.ast.values.GraphicStringValue;
import ch.eskaton.asn4j.parser.ast.values.HexStringValue;
import ch.eskaton.asn4j.parser.ast.values.IA5StringValue;
import ch.eskaton.asn4j.parser.ast.values.IRIValue;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.NamedValue;
import ch.eskaton.asn4j.parser.ast.values.NullValue;
import ch.eskaton.asn4j.parser.ast.values.NumericStringValue;
import ch.eskaton.asn4j.parser.ast.values.ObjectIdentifierValue;
import ch.eskaton.asn4j.parser.ast.values.OctetStringValue;
import ch.eskaton.asn4j.parser.ast.values.PrintableStringValue;
import ch.eskaton.asn4j.parser.ast.values.RealValue;
import ch.eskaton.asn4j.parser.ast.values.RelativeIRIValue;
import ch.eskaton.asn4j.parser.ast.values.RelativeOIDValue;
import ch.eskaton.asn4j.parser.ast.values.TeletexStringValue;
import ch.eskaton.asn4j.parser.ast.values.UTCTimeValue;
import ch.eskaton.asn4j.parser.ast.values.UTF8StringValue;
import ch.eskaton.asn4j.parser.ast.values.UniversalStringValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.parser.ast.values.VideotexStringValue;
import ch.eskaton.asn4j.parser.ast.values.VisibleStringValue;
import ch.eskaton.commons.collections.Tuple3;
import ch.eskaton.commons.functional.TriFunction;
import ch.eskaton.commons.utils.Dispatcher;
import ch.eskaton.commons.utils.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ch.eskaton.asn4j.compiler.CompilerUtils.getComponentIds;
import static ch.eskaton.commons.utils.Utils.callWith;

public class JavaUtils {

    public static final Dispatcher<Value, Class<? extends Value>, Tuple3<CompilerContext, String, ? extends Value>, String> DISPATCHER =
            new Dispatcher<Value, Class<? extends Value>, Tuple3<CompilerContext, String, ? extends Value>, String>()
                    .withComparator((t, u) -> u.isInstance(t))
                    .withException(t -> new CompilerException("Failed to get initializer string for type %s", t));

    static {
        addCase(BooleanValue.class, JavaUtils::getBooleanInitializerString);
        addCase(BitStringValue.class, JavaUtils::getBitStringInitializerString);
        addCase(BinaryStringValue.class, JavaUtils::getBinaryStringInitializerString);
        addCase(HexStringValue.class, JavaUtils::getHexStringInitializerString);
        addCase(EnumeratedValue.class, JavaUtils::getEnumeratedInitializerString);
        addCase(IntegerValue.class, JavaUtils::getIntegerInitializerString);
        addCase(RealValue.class, JavaUtils::getRealInitializerString);
        addCase(IRIValue.class, JavaUtils::getIRIInitializerString);
        addCase(NullValue.class, JavaUtils::getNullInitializerString);
        addCase(ObjectIdentifierValue.class, JavaUtils::getObjectIdentifierInitializerString);
        addCase(OctetStringValue.class, JavaUtils::getOctetStringInitializerString);
        addCase(RelativeOIDValue.class, JavaUtils::getRelativeOIDInitializerString);
        addCase(RelativeIRIValue.class, JavaUtils::getRelativeIRIInitializerString);
        addCase(CollectionOfValue.class, JavaUtils::getCollectionOfInitializerString);
        addCase(CollectionValue.class, JavaUtils::getCollectionInitializerString);
        addCase(ChoiceValue.class, JavaUtils::getChoiceInitializerString);
        addCase(VisibleStringValue.class, JavaUtils::getVisibleStringInitializerString);
        addCase(UTCTimeValue.class, JavaUtils::getUTCTimeInitializerString);
        addCase(GeneralizedTimeValue.class, JavaUtils::getGeneralizedTimeInitializerString);
        addCase(NumericStringValue.class, JavaUtils::getNumericStringInitializerString);
        addCase(PrintableStringValue.class, JavaUtils::getPrintableStringInitializerString);
        addCase(IA5StringValue.class, JavaUtils::getIA5StringInitializerString);
        addCase(GraphicStringValue.class, JavaUtils::getGraphicStringInitializerString);
        addCase(GeneralStringValue.class, JavaUtils::getGeneralStringInitializerString);
        addCase(TeletexStringValue.class, JavaUtils::getTeletexStringInitializerString);
        addCase(VideotexStringValue.class, JavaUtils::getVideotexStringInitializerString);
        addCase(UniversalStringValue.class, JavaUtils::getUniversalStringInitializerString);
        addCase(UTF8StringValue.class, JavaUtils::getUTF8StringInitializerString);
        addCase(BMPStringValue.class, JavaUtils::getBMPStringInitializerString);
    }

    private JavaUtils() {
    }

    public static String getInitializerString(CompilerContext ctx, String typeName, Value value) {
        return DISPATCHER.execute(value, Tuple3.of(ctx, typeName, value));
    }

    private static <T extends Value> void addCase(Class<T> valueClazz,
            TriFunction<CompilerContext, String, T, String> initializer) {
        DISPATCHER.withCase(valueClazz,
                maybeArgs -> callWith(args ->
                        initializer.apply(args.get_1(), args.get_2(), valueClazz.cast(args.get_3())), maybeArgs.get()));
    }

    private static String getBooleanInitializerString(CompilerContext ctx, String typeName, BooleanValue value) {
        return "new %s(%s)".formatted(typeName, value.getValue());
    }

    private static String getBitStringInitializerString(CompilerContext ctx, String typeName, BitStringValue value) {
        var bytes = value.getByteValue();
        var bytesStr = IntStream.range(0, bytes.length).boxed()
                .map(i -> String.format("(byte) 0x%02x", bytes[i])).collect(Collectors.joining(", "));

        return "new %s(new byte[] { %s }, %s)".formatted(typeName, bytesStr, value.getUnusedBits());
    }

    private static String getBinaryStringInitializerString(CompilerContext ctx, String typeName,
            BinaryStringValue value) {
        return getBitStringInitializerString(ctx, typeName, value.toBitString());
    }

    private static String getHexStringInitializerString(CompilerContext ctx, String typeName, HexStringValue value) {
        return getBitStringInitializerString(ctx, typeName, value.toBitString());
    }

    private static String getEnumeratedInitializerString(CompilerContext ctx, String typeName, EnumeratedValue value) {
        return typeName + "." + value.getId().toUpperCase();
    }

    private static String getIntegerInitializerString(CompilerContext ctx, String typeName, IntegerValue value) {
        return "new %s(%sL)".formatted(typeName, value.getValue().longValue());
    }

    private static String getRealInitializerString(CompilerContext ctx, String typeName, RealValue value) {
        return switch (value.getRealType()) {
            case NORMAL -> String.format("new %s(new BigDecimal(\"%s\"))", typeName, value.getValue().toString());
            case SPECIAL -> String.format("new %s(%d, %d, %d)", typeName, value.getMantissa(), value.getBase(),
                    value.getExponent());
            case NEGATIVE_INF -> String.format("new %s(ASN1Real.Type.MINUS_INFINITY)", typeName);
            case POSITIVE_INF -> String.format("new %s(ASN1Real.Type.PLUS_INFINITY)", typeName);
            case NAN -> String.format("new %s(ASN1Real.Type.NOT_A_NUMBER)", typeName);
            default -> throw new IllegalCompilerStateException("Real type %s not handled", value.getRealType());
        };
    }

    private static String getIRIInitializerString(CompilerContext ctx, String typeName, IRIValue value) {
        return getIRIInitializerString(typeName, value.getArcIdentifierTexts());
    }

    private static String getRelativeIRIInitializerString(CompilerContext ctx, String typeName,
            RelativeIRIValue value) {
        return getIRIInitializerString(typeName, value.getArcIdentifierTexts());
    }

    private static String getIRIInitializerString(String typeName, List<String> components) {
        var idsString = components.stream().map(str -> StringUtils.wrap(str, "\"")).collect(Collectors.joining(", "));

        return "new %s(%s)".formatted(typeName, idsString);
    }

    private static String getNullInitializerString(CompilerContext ctx, String typeName, NullValue value) {
        return "new %s()".formatted(typeName);
    }

    private static String getObjectIdentifierInitializerString(CompilerContext ctx, String typeName,
            ObjectIdentifierValue value) {
        return getOIDInitializerString(typeName, getComponentIds(value.getComponents()));
    }

    private static String getRelativeOIDInitializerString(CompilerContext ctx, String typeName,
            RelativeOIDValue value) {
        return getOIDInitializerString(typeName, getComponentIds(value.getComponents()));
    }

    private static String getOIDInitializerString(String typeName, List<Integer> ids) {
        var idsString = ids.stream().map(Object::toString).collect(Collectors.joining(", "));

        return "new %s(new int[] {%s})".formatted(typeName, idsString);
    }

    private static String getOctetStringInitializerString(CompilerContext ctx, String typeName,
            OctetStringValue value) {
        var bytes = value.getByteValue();
        var bytesStr = IntStream.range(0, bytes.length).boxed().map(
                i -> String.format("(byte) 0x%02x", bytes[i])).collect(Collectors.joining(", "));

        return "new %s(new byte[] { %s })".formatted(typeName, bytesStr);
    }

    private static String getCollectionOfInitializerString(CompilerContext ctx, String typeName,
            CollectionOfValue value) {
        var initString = value.getValues().stream()
                .map(v -> getInitializerString(ctx, ctx.getTypeName(((AbstractValue) v).getType()), v))
                .collect(Collectors.joining(", "));

        return "new %s(%s)".formatted(typeName, initString);
    }

    private static String getCollectionInitializerString(CompilerContext ctx, String typeName, CollectionValue value) {
        var maybeCompiledType = ctx.findCompiledTypeRecursive(value.getType())
                .filter(CompiledCollectionType.class::isInstance)
                .map(CompiledCollectionType.class::cast);
        var values = value.getValues().stream().collect(Collectors.toMap(NamedValue::getName,
                NamedValue::getValue));

        return maybeCompiledType.map(compiledType -> {
            var initString = compiledType.getComponents().stream()
                    .map(c -> {
                        var componentTypeName = ctx.getTypeName(c.getCompiledType().getType());
                        var componentValue = values.get(c.getName());

                        return values.containsKey(c.getName()) ?
                                getInitializerString(ctx, componentTypeName, componentValue) :
                                "null";
                    })
                    .collect(Collectors.joining(", "));

            return "new %s(%s)".formatted(typeName, initString);
        }).orElseThrow(() -> new CompilerException("Failed to resolve type %s", typeName));
    }

    private static String getChoiceInitializerString(CompilerContext ctx, String typeName, ChoiceValue value) {
        var maybeCompiledType = ctx.findCompiledTypeRecursive(value.getType())
                .filter(CompiledChoiceType.class::isInstance)
                .map(CompiledChoiceType.class::cast);

        return maybeCompiledType.map(compiledType -> {
            var componentType = compiledType.getComponents().stream()
                    .filter(c -> c.getName().equals(value.getId()))
                    .map(c -> c.getCompiledType().getType())
                    .findFirst();
            var name = StringUtils.initCap(CompilerUtils.formatName(value.getId()));

            return componentType.map(t -> String.format("with(new %s(), v -> v.set%s(%s))", typeName, name,
                    getInitializerString(ctx, ctx.getTypeName(t), value.getValue())))
                    .orElseThrow(() -> new CompilerException("Invalid component '%s' in type %s",
                            value.getId(), typeName));
        }).orElseThrow(() -> new CompilerException("Failed to resolve type %s", typeName));
    }

    private static String getVisibleStringInitializerString(CompilerContext ctx, String typeName,
            VisibleStringValue value) {
        return getGenericStringInitializerString(ctx, typeName, value.getValue());
    }

    private static String getNumericStringInitializerString(CompilerContext ctx, String typeName,
            NumericStringValue value) {
        return getGenericStringInitializerString(ctx, typeName, value.getValue());
    }

    private static String getPrintableStringInitializerString(CompilerContext ctx, String typeName,
            PrintableStringValue value) {
        return getGenericStringInitializerString(ctx, typeName, value.getValue());
    }

    private static String getIA5StringInitializerString(CompilerContext ctx, String typeName,
            IA5StringValue value) {
        return getGenericStringInitializerString(ctx, typeName, value.getValue());
    }

    private static String getGraphicStringInitializerString(CompilerContext ctx, String typeName,
            GraphicStringValue value) {
        return getGenericStringInitializerString(ctx, typeName, value.getValue());
    }

    private static String getGeneralStringInitializerString(CompilerContext ctx, String typeName,
            GeneralStringValue value) {
        return getGenericStringInitializerString(ctx, typeName, value.getValue());
    }

    private static String getTeletexStringInitializerString(CompilerContext ctx, String typeName,
            TeletexStringValue value) {
        return getGenericStringInitializerString(ctx, typeName, value.getValue());
    }

    private static String getVideotexStringInitializerString(CompilerContext ctx, String typeName,
            VideotexStringValue value) {
        return getGenericStringInitializerString(ctx, typeName, value.getValue());
    }

    private static String getUniversalStringInitializerString(CompilerContext ctx, String typeName,
            UniversalStringValue value) {
        return getGenericStringInitializerString(ctx, typeName, value.getValue());
    }

    private static String getUTF8StringInitializerString(CompilerContext ctx, String typeName,
            UTF8StringValue value) {
        return getGenericStringInitializerString(ctx, typeName, value.getValue());
    }

    private static String getBMPStringInitializerString(CompilerContext ctx, String typeName,
            BMPStringValue value) {
        return getGenericStringInitializerString(ctx, typeName, value.getValue());
    }

    private static String getUTCTimeInitializerString(CompilerContext ctx, String typeName,
            UTCTimeValue value) {
        return getGenericStringInitializerString(ctx, typeName, value.getValue());
    }

    private static String getGeneralizedTimeInitializerString(CompilerContext ctx, String typeName,
            GeneralizedTimeValue value) {
        return getGenericStringInitializerString(ctx, typeName, value.getValue());
    }

    @SuppressWarnings("unused")
    private static String getGenericStringInitializerString(CompilerContext ctx, String typeName,
            String value) {
        var escaped = value.chars().boxed().map(JavaUtils::escapeCharacter).collect(Collectors.joining());

        return String.format("new %s(\"%s\")", typeName, escaped);
    }

    private static String escapeCharacter(Integer chr) {
        if (Character.isISOControl(chr)) {
            return escapeControl(chr);
        }

        return switch (chr.intValue()) {
            case '\'' -> "\\\'";
            case '\"' -> "\\\"";
            case '\\' -> "\\\\";
            default -> String.valueOf(Character.valueOf((char) chr.intValue()));
        };
    }

    private static String escapeControl(Integer chr) {
        return switch (chr.intValue()) {
            case '\t' -> "\\t";
            case '\b' -> "\\b";
            case '\n' -> "\\n";
            case '\r' -> "\\r";
            case '\f' -> "\\f";
            default -> String.format("\0%02d", Integer.toOctalString(chr));
        };
    }

}
