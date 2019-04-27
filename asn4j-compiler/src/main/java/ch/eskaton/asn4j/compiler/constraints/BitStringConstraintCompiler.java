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

package ch.eskaton.asn4j.compiler.constraints;

import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.TypeResolver;
import ch.eskaton.asn4j.compiler.java.JavaClass;
import ch.eskaton.asn4j.compiler.utils.BitStringUtils;
import ch.eskaton.asn4j.parser.ast.constraints.ContainedSubtype;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.AbstractBaseXStringValue;
import ch.eskaton.asn4j.parser.ast.values.BitStringValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;
import ch.eskaton.commons.utils.CollectionUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.java.JavaType.BYTE_ARRAY;
import static ch.eskaton.asn4j.compiler.java.JavaType.INT;
import static ch.eskaton.asn4j.compiler.java.JavaVisibility.Protected;

public class BitStringConstraintCompiler extends AbstractConstraintCompiler<BitStringValue, Set<BitStringValue>, BitStringConstraintValues, BitStringConstraintDefinition> {

    public BitStringConstraintCompiler(TypeResolver typeResolver) {
        super(typeResolver);
    }

    @Override
    protected BitStringConstraintDefinition createDefinition(BitStringConstraintValues root, BitStringConstraintValues extension) {
        return new BitStringConstraintDefinition(root, extension);
    }

    @Override
    protected BitStringConstraintValues createValues() {
        return new BitStringConstraintValues();
    }

    @Override
    protected BitStringConstraintValues calculateExclude(BitStringConstraintValues values1,
            BitStringConstraintValues values2) {
        for (BitStringValue value : values2.getValues()) {
            if (values1.getValues().contains(value)) {
                values1.getValues().remove(value);
            } else {
                throw new CompilerException(value + " doesn't exist in parent type");
            }
        }

        return values1;
    }

    @Override
    protected void addConstraint(JavaClass javaClass, ConstraintDefinition definition) throws CompilerException {
        BitStringConstraintValues rootValues = ((BitStringConstraintDefinition) definition).getRootValues();
        BitStringConstraintValues extensionValues = ((BitStringConstraintDefinition) definition).getExtensionValues();
        BitStringConstraintValues union = rootValues.union(extensionValues);
        Set<BitStringValue> values = union.getValues();
        boolean inverted = union.isInverted();

        javaClass.addImport(Arrays.class);

        JavaClass.BodyBuilder builder = javaClass.method().annotation("@Override").modifier(Protected)
                .returnType(boolean.class).name("checkConstraint").parameter(BYTE_ARRAY, "value")
                .parameter(INT, "unusedBits")
                .exception(ConstraintViolatedException.class).body();

        if (!values.isEmpty()) {
            builder.append("if (" + values.stream().map(value ->
                    "Arrays.equals(" + BitStringUtils.getInitializerString(value.getByteValue()) + ", value) && " +
                            value.getUnusedBits() + " == unusedBits").collect(Collectors.joining(" || "))
                    + ") {");
            builder.append("\treturn " + (inverted ? "false" : "true") + ";");
            builder.append("}").nl();
        }

        builder.append("return " + (inverted ? "true" : "false") + ";");

        builder.finish().build();
    }

    protected BitStringConstraintValues calculateElements(Elements elements) throws CompilerException {
        if (elements instanceof ElementSet) {
            return compileConstraint((ElementSet) elements);
        } else {
            if (elements instanceof SingleValueConstraint) {
                Value value = ((SingleValueConstraint) elements).getValue();

                if (value instanceof AbstractBaseXStringValue) {
                    return new BitStringConstraintValues(CollectionUtils.asHashSet(((AbstractBaseXStringValue) value).toBitString()));
                } else {
                    throw new CompilerException("Invalid single-value constraint %s for BIT STRING type",
                            value.getClass().getSimpleName());
                }
            } else if (elements instanceof ContainedSubtype) {
                Type type = ((ContainedSubtype) elements).getType();

                throw new CompilerException("Invalid constraint %s for BIT STRING type",
                        elements.getClass().getSimpleName());
            } else {
                throw new CompilerException("Invalid constraint %s for BIT STRING type",
                        elements.getClass().getSimpleName());
            }
        }
    }

}
