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
import ch.eskaton.asn4j.compiler.java.objs.JavaClass;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.BooleanType;
import ch.eskaton.asn4j.parser.ast.types.EnumeratedType;
import ch.eskaton.asn4j.parser.ast.types.IRI;
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
import ch.eskaton.asn4j.parser.ast.types.ObjectIdentifier;
import ch.eskaton.asn4j.parser.ast.types.OctetString;
import ch.eskaton.asn4j.parser.ast.types.RelativeIRI;
import ch.eskaton.asn4j.parser.ast.types.RelativeOID;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.commons.collections.Maps;

import java.util.Map;

import static ch.eskaton.asn4j.compiler.CompilerUtils.formatTypeName;
import static ch.eskaton.asn4j.compiler.CompilerUtils.getTypeName;

public class DefaultsCompiler {

    private Map<Class<? extends Type>, DefaultCompiler> compilers =
            Maps.<Class<? extends Type>, DefaultCompiler>builder()
                    .put(BooleanType.class, new BooleanDefaultCompiler())
                    .put(IntegerType.class, new IntegerDefaultCompiler())
                    .put(EnumeratedType.class, new EnumeratedDefaultCompiler())
                    .put(BitString.class, new BitStringDefaultCompiler())
                    .put(OctetString.class, new OctetStringDefaultCompiler())
                    .put(ObjectIdentifier.class, new ObjectIdentifierDefaultCompiler())
                    .put(RelativeOID.class, new RelativeOIDDefaultCompiler())
                    .put(IRI.class, new IRIDefaultCompiler())
                    .put(RelativeIRI.class, new RelativeIRIDefaultCompiler())
                    .build();

    private CompilerContext ctx;

    @SuppressWarnings("serial")
    public DefaultsCompiler(CompilerContext ctx) {
        this.ctx = ctx;
    }

    public void compileDefault(JavaClass clazz, String field, String typeName, Type type, Value value) {
        Type base;

        if (type instanceof TypeReference) {
            base = ctx.getBase((TypeReference) type);
        } else {
            base = type;
        }

        if (!compilers.containsKey(base.getClass())) {
            throw new CompilerException("Defaults for type %s not yet supported", getTypeName(base));
        }

        DefaultCompiler compiler = compilers.get(base.getClass());

        try {
            compiler.compileDefault(ctx, clazz, field, typeName, type, value);
        } catch (CompilerException e) {
            throw new CompilerException("Error in default for type " + formatTypeName(type) + ": " + e.getMessage(), e);
        }
    }

}
