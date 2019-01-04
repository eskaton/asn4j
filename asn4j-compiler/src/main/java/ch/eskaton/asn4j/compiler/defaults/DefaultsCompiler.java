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
import ch.eskaton.asn4j.compiler.TypeResolver;
import ch.eskaton.asn4j.compiler.java.JavaClass;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.ComponentType;
import ch.eskaton.asn4j.parser.ast.types.ComponentType.CompType;
import ch.eskaton.asn4j.parser.ast.types.IRI;
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.asn4j.parser.ast.types.ObjectIdentifier;
import ch.eskaton.asn4j.parser.ast.types.OctetString;
import ch.eskaton.asn4j.parser.ast.types.RelativeOID;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.values.Value;

import java.util.HashMap;
import java.util.Map;

import static ch.eskaton.asn4j.compiler.CompilerUtils.getTypeName;

public class DefaultsCompiler {

    private Map<Class<? extends Type>, DefaultCompiler> compilers =
            new HashMap<Class<? extends Type>, DefaultCompiler>() {
                {
                    put(IntegerType.class, new IntegerDefaultCompiler());
                    put(OctetString.class, new OctetStringDefaultCompiler());
                    put(BitString.class, new BitStringDefaultCompiler());
                    put(ObjectIdentifier.class, new ObjectIdentifierDefaultCompiler());
                    put(RelativeOID.class, new RelativeOIDDefaultCompiler());
                    put(IRI.class, new IRIDefaultCompiler());
                }
            };

    private CompilerContext ctx;

    private TypeResolver typeResolver;

    @SuppressWarnings("serial")
    public DefaultsCompiler(CompilerContext ctx, TypeResolver typeResolver) {
        this.ctx = ctx;
        this.typeResolver = typeResolver;
    }

    public void compileDefault(JavaClass clazz, String field, String typeName, Type type, Value value)
            throws CompilerException {
        Type base;

        if (type instanceof TypeReference) {
            base = typeResolver.getBase(((TypeReference) type).getType());
        } else {
            base = type;
        }

        if (!compilers.containsKey(base.getClass())) {
            throw new CompilerException("Defaults for type " + getTypeName(base) + " not yet supported");
        }

        DefaultCompiler compiler = compilers.get(base.getClass());

        try {
            compiler.compileDefault(ctx, clazz, field, typeName, type, value);
        } catch (CompilerException e) {
            throw new CompilerException("Error in default for type "
                    + (type instanceof NamedType ? ((NamedType) type).getName()
                    : getTypeName(type)) + ": "
                    + e.getMessage(), e);
        }

        return;
    }



}
