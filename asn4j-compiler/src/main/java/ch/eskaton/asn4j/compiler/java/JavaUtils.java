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
import ch.eskaton.asn4j.compiler.resolvers.IRIValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.ObjectIdentifierValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.RelativeIRIValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.RelativeOIDValueResolver;
import ch.eskaton.asn4j.parser.ast.values.AbstractValue;
import ch.eskaton.asn4j.parser.ast.values.BitStringValue;
import ch.eskaton.asn4j.parser.ast.values.BooleanValue;
import ch.eskaton.asn4j.parser.ast.values.CollectionOfValue;
import ch.eskaton.asn4j.parser.ast.values.EnumeratedValue;
import ch.eskaton.asn4j.parser.ast.values.IRIValue;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.NullValue;
import ch.eskaton.asn4j.parser.ast.values.ObjectIdentifierValue;
import ch.eskaton.asn4j.parser.ast.values.OctetStringValue;
import ch.eskaton.asn4j.parser.ast.values.RelativeIRIValue;
import ch.eskaton.asn4j.parser.ast.values.RelativeOIDValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.verifiers.ObjectIdentifierVerifier;
import ch.eskaton.commons.collections.Tuple3;
import ch.eskaton.commons.functional.TriFunction;
import ch.eskaton.commons.utils.Dispatcher;
import ch.eskaton.commons.utils.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ch.eskaton.commons.utils.Utils.callWith;

public class JavaUtils {

    private JavaUtils() {
    }

    public static String getInitializerString(CompilerContext ctx, String typeName, Value value) {
        var dispatcher = new Dispatcher<Value, Class<? extends Value>, Tuple3<CompilerContext, String, ? extends Value>, String>()
                .withComparator((t, u) -> u.isInstance(t))
                .withException(t -> new CompilerException("Failed to get initializer string for type %s", t));

        addCase(dispatcher, BooleanValue.class, JavaUtils::getBooleanInitializerString);
        addCase(dispatcher, BitStringValue.class, JavaUtils::getBitStringInitializerString);
        addCase(dispatcher, EnumeratedValue.class, JavaUtils::getEnumeratedInitializerString);
        addCase(dispatcher, IntegerValue.class, JavaUtils::getIntegerInitializerString);
        addCase(dispatcher, IRIValue.class, JavaUtils::getIRIInitializerString);
        addCase(dispatcher, NullValue.class, JavaUtils::getNullInitializerString);
        addCase(dispatcher, ObjectIdentifierValue.class, JavaUtils::getObjectIdentifierInitializerString);
        addCase(dispatcher, OctetStringValue.class, JavaUtils::getOctetStringInitializerString);
        addCase(dispatcher, RelativeOIDValue.class, JavaUtils::getRelativeOIDInitializerString);
        addCase(dispatcher, RelativeIRIValue.class, JavaUtils::getRelativeIRIInitializerString);
        addCase(dispatcher, CollectionOfValue.class, JavaUtils::getCollectionOfInitializerString);

        return dispatcher.execute(value, Tuple3.of(ctx, typeName, value));
    }

    private static <T extends Value> void addCase(
            Dispatcher<Value, Class<? extends Value>, Tuple3<CompilerContext, String, ? extends Value>, String> dispatcher,
            Class<T> valueClazz,
            TriFunction<CompilerContext, String, T, String> initializer) {
        dispatcher.withCase(valueClazz,
                maybeArgs -> callWith(args -> initializer.apply(args.get_1(), args.get_2(),
                        valueClazz.cast(args.get_3())), maybeArgs.get()));
    }

    private static String getBooleanInitializerString(CompilerContext ctx, String typeName, BooleanValue value) {
        return "new " + typeName + "(" + value.getValue() + ")";
    }

    private static String getBitStringInitializerString(CompilerContext ctx, String typeName, BitStringValue value) {
        byte[] bytes = value.getByteValue();
        String bytesStr = IntStream.range(0, bytes.length).boxed().map(
                i -> String.format("(byte) 0x%02x", bytes[i])).collect(Collectors.joining(", "));

        return "new " + typeName + "(new byte[] { " + bytesStr + " }, " + value.getUnusedBits() + ")";
    }

    private static String getEnumeratedInitializerString(CompilerContext ctx, String typeName, EnumeratedValue value) {
        return typeName + "." + value.getId().toUpperCase();
    }

    private static String getIntegerInitializerString(CompilerContext ctx, String typeName, IntegerValue value) {
        return "new " + typeName + "(" + value.getValue().longValue() + "L)";
    }

    private static String getIRIInitializerString(CompilerContext ctx, String typeName, IRIValue value) {
        IRIValueResolver resolver = new IRIValueResolver(ctx);

        List<String> components = resolver.resolveComponents(ctx, resolver.resolveValue(ctx, value, IRIValue.class));

        return getIRIInitializerString(typeName, components);
    }

    private static String getRelativeIRIInitializerString(CompilerContext ctx, String typeName, RelativeIRIValue value) {
        RelativeIRIValueResolver resolver = new RelativeIRIValueResolver(ctx);

        List<String> components = resolver.resolveComponents(ctx, resolver.resolveValue(ctx, value, RelativeIRIValue.class));

        return getIRIInitializerString(typeName, components);
    }

    private static String getIRIInitializerString(String typeName, List<String> components) {
        String idsString = components.stream().map(str -> StringUtils.wrap(str, "\"")).collect(Collectors.joining(", "));

        return "new " + typeName + "(" + idsString + ")";
    }

    private static String getNullInitializerString(CompilerContext ctx, String typeName, NullValue value) {
        return "new " + typeName + "()";
    }

    private static String getObjectIdentifierInitializerString(CompilerContext ctx, String typeName, ObjectIdentifierValue value) {
        ObjectIdentifierValueResolver resolver = new ObjectIdentifierValueResolver(ctx);

        List<Integer> ids = resolver.resolveComponents(ctx, resolver.resolveValue(ctx, value, ObjectIdentifierValue.class));

        ObjectIdentifierVerifier.verifyComponents(ids);

        return getOIDInitializerString(typeName, ids);
    }

    private static String getRelativeOIDInitializerString(CompilerContext ctx, String typeName, RelativeOIDValue value) {
        RelativeOIDValueResolver resolver = new RelativeOIDValueResolver(ctx);

        List<Integer> ids = resolver.resolveComponents(ctx, resolver.resolveValue(ctx, value, RelativeOIDValue.class));

        return getOIDInitializerString(typeName, ids);
    }

    private static String getOIDInitializerString(String typeName, List<Integer> ids) {
        String idsString = ids.stream().map(Object::toString).collect(Collectors.joining(", "));

        return "new " + typeName + "(new int[] { " + idsString + " })";
    }

    private static String getOctetStringInitializerString(CompilerContext ctx, String typeName, OctetStringValue value) {
        byte[] bytes = value.getByteValue();
        String bytesStr = IntStream.range(0, bytes.length).boxed().map(
                i -> String.format("(byte) 0x%02x", bytes[i])).collect(Collectors.joining(", "));

        return "new " + typeName + "(new byte[] { " + bytesStr + " })";
    }

    private static String getCollectionOfInitializerString(CompilerContext ctx, String typeName, CollectionOfValue value) {
        var initString = value.getValues().stream()
                .map(v -> getInitializerString(ctx, ctx.getTypeName(((AbstractValue) v).getType()), v))
                .collect(Collectors.joining(", "));

        return "new " + typeName + "(" + initString + ")";
    }

}
