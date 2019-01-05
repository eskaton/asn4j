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

package ch.eskaton.asn4j.compiler.defaults;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.java.JavaClass;
import ch.eskaton.asn4j.compiler.java.JavaInitializer;
import ch.eskaton.asn4j.parser.IRIToken;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.IRIValue;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.commons.utils.StringUtils;

import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.CompilerUtils.resolveAmbiguousValue;

public class IRIDefaultCompiler implements DefaultCompiler {

    @Override
    public void compileDefault(CompilerContext ctx, JavaClass clazz, String field, String typeName, Type type,
            Value value) throws CompilerException {
        IRIValue iriValue;

        if (resolveAmbiguousValue(value, SimpleDefinedValue.class) != null) {
            value = resolveAmbiguousValue(value, SimpleDefinedValue.class);
            iriValue = ctx.resolveValue(IRIValue.class, (SimpleDefinedValue) value);
        } else if (resolveAmbiguousValue(value, IRIValue.class) != null) {
            iriValue = resolveAmbiguousValue(value, IRIValue.class);
        } else {
            throw new CompilerException("Invalid default value");
        }

        String defaultField = addDefaultField(clazz, typeName, field);

        String valueString = iriValue.getArcIdentifiers().stream().map(IRIToken::getText).map(StringUtils::dquote).collect(Collectors.joining(", "));

        clazz.addInitializer(new JavaInitializer("\t\t" + defaultField + " = new " + typeName + "();\n"
                + "\t\t" + defaultField + ".setValue(" + valueString + ");"));
    }

}
