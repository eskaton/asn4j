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
import ch.eskaton.asn4j.compiler.CompilerUtils;
import ch.eskaton.asn4j.compiler.java.JavaClass;
import ch.eskaton.asn4j.compiler.java.JavaInitializer;
import ch.eskaton.asn4j.parser.ast.OIDComponentNode;
import ch.eskaton.asn4j.parser.ast.values.DefinedValue;
import ch.eskaton.asn4j.parser.ast.values.ObjectIdentifierValue;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.verifiers.ObjectIdentifierVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.CompilerUtils.resolveAmbiguousValue;

public class ObjectIdentifierDefaultCompiler implements DefaultCompiler {

    @Override
    public void compileDefault(CompilerContext ctx, JavaClass clazz, String typeName, String field,
            Value value) throws CompilerException {
        ObjectIdentifierValue oidValue;

        if (value instanceof ObjectIdentifierValue) {
            oidValue = (ObjectIdentifierValue) value;
        } else if (resolveAmbiguousValue(value, SimpleDefinedValue.class) != null) {
            value = resolveAmbiguousValue(value, SimpleDefinedValue.class);
            oidValue = ctx.resolveObjectIdentifierValue(((SimpleDefinedValue) value));
        } else {
            throw new CompilerException("Invalid default value");
        }

        List<Integer> ids = new ArrayList<>();

        resolveComponents(ctx, field, oidValue, ids);

        ObjectIdentifierVerifier.verifyComponents(ids);

        String defaultField = addDefaultField(clazz, typeName, field);
        String idsString = ids.stream().map(Object::toString).collect(Collectors.joining(", "));

        clazz.addInitializer(new JavaInitializer("\t\t" + defaultField + " = new " + typeName + "();\n"
                + "\t\t" + defaultField + ".setValue(" + idsString + ");"));
    }

    public void resolveComponents(CompilerContext ctx, String field, ObjectIdentifierValue oidValue, List<Integer> ids) {
        int componentNum = 1;

        for (OIDComponentNode component : oidValue.getComponents()) {
            try {
                try {
                    ids.add(getComponentId(ctx, component));
                } catch(CompilerException e) {
                    if (componentNum == 1) {
                        ObjectIdentifierValue referencedOidValue = ctx.resolveObjectIdentifierValue(component.getName());
                        resolveComponents(ctx, field, referencedOidValue, ids);
                    } else {
                        throw e;
                    }
                }
            } catch (CompilerException e) {
                throw new CompilerException("Failed to resolve component of object identifier value " + field, e);
            }

            componentNum++;
        }
    }

    private Integer getComponentId(CompilerContext ctx, OIDComponentNode component) {
        Integer id = component.getId();

        if (id != null) {
            return id;
        }

        DefinedValue definedValue = component.getDefinedValue();

        if (definedValue != null) {
            return ctx.resolveIntegerValue(definedValue).intValue();
        }

        return ctx.resolveIntegerValue(component.getName()).intValue();
    }

}
