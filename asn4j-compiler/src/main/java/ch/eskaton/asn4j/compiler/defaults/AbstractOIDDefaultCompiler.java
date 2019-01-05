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
import ch.eskaton.asn4j.parser.ast.OIDComponentNode;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.AbstractOIDValue;
import ch.eskaton.asn4j.parser.ast.values.DefinedValue;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Value;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.CompilerUtils.resolveAmbiguousValue;

public abstract class AbstractOIDDefaultCompiler<T extends AbstractOIDValue> implements DefaultCompiler {

    public void compileDefault(CompilerContext ctx, JavaClass clazz, String field, String typeName, Type type,
            Value value) throws CompilerException {
        List<Integer> ids = new ArrayList<>();

        resolveComponents(ctx, field, value, ids);

        verifyObjectIds(ids);

        String defaultField = addDefaultField(clazz, typeName, field);
        String idsString = ids.stream().map(Object::toString).collect(Collectors.joining(", "));

        clazz.addInitializer(new JavaInitializer("\t\t" + defaultField + " = new " + typeName + "();\n"
                + "\t\t" + defaultField + ".setValue(" + idsString + ");"));
    }

    protected T resolveValue(CompilerContext ctx, Value value, Class<T> valueClass) {
        T oidValue;

        if (valueClass.isAssignableFrom(value.getClass())) {
            oidValue = (T) value;
        } else if (resolveAmbiguousValue(value, SimpleDefinedValue.class) != null) {
            value = resolveAmbiguousValue(value, SimpleDefinedValue.class);
            oidValue = getDefinedValueResolver().apply(ctx, (SimpleDefinedValue) value);
        } else {
            throw new CompilerException("Invalid default value");
        }

        return oidValue;
    }

    public BiFunction<CompilerContext, DefinedValue, T> getDefinedValueResolver() {
        return (ctx, ref) -> ctx.resolveValue(getValueClass(), ref);
    }

    public BiFunction<CompilerContext, String, T> getReferenceResolver() {
        return (ctx, ref) -> ctx.resolveValue(getValueClass(), ref);
    }

    protected abstract Class<T> getValueClass();


    abstract public void resolveComponents(CompilerContext ctx, String field, Value value, List<Integer> ids);

    public void verifyObjectIds(List<Integer> ids) {
    }

    protected Integer getComponentId(CompilerContext ctx, OIDComponentNode component) {
        Integer id = component.getId();

        if (id != null) {
            return id;
        }

        DefinedValue definedValue = component.getDefinedValue();

        if (definedValue != null) {
            return ctx.resolveValue(BigInteger.class, definedValue).intValue();
        }

        return ctx.resolveValue(BigInteger.class, component.getName()).intValue();
    }

    public void resolveOIDReference(CompilerContext ctx, String field, List<Integer> ids, OIDComponentNode component) {
        T referencedOidValue;

        try {
            referencedOidValue = getReferenceResolver().apply(ctx, component.getName());
        } catch (CompilerException e2) {
            referencedOidValue = getDefinedValueResolver().apply(ctx, component.getDefinedValue());
        }

        resolveComponents(ctx, field, referencedOidValue, ids);
    }

}
