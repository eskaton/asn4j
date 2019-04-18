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
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.BooleanType;
import ch.eskaton.asn4j.parser.ast.types.IRI;
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
import ch.eskaton.asn4j.parser.ast.types.Null;
import ch.eskaton.asn4j.parser.ast.types.ObjectIdentifier;
import ch.eskaton.asn4j.parser.ast.types.OctetString;
import ch.eskaton.asn4j.parser.ast.types.RelativeIRI;
import ch.eskaton.asn4j.parser.ast.types.RelativeOID;
import ch.eskaton.asn4j.parser.ast.types.SetOfType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.types.VisibleString;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ConstraintCompiler {

    private Map<Class<? extends Type>, AbstractConstraintCompiler<?>> compilers;

    private TypeResolver typeResolver;

    @SuppressWarnings("serial")
    public ConstraintCompiler(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;

        compilers = new HashMap<Class<? extends Type>, AbstractConstraintCompiler<?>>() {
            {
                put(IntegerType.class, new IntegerConstraintCompiler(typeResolver));
                put(BooleanType.class, new BooleanConstraintCompiler(typeResolver));
                put(BitString.class, new BitStringConstraintCompiler(typeResolver));
                put(VisibleString.class, new VisibleStringConstraintCompiler(typeResolver));
                put(OctetString.class, new OctetStringConstraintCompiler(typeResolver));
                put(Null.class, new NullConstraintCompiler(typeResolver));
                put(SetOfType.class, new SetOfConstraintCompiler(typeResolver));
                put(ObjectIdentifier.class, new ObjectIdentifierConstraintCompiler(typeResolver));
                put(RelativeOID.class, new RelativeOIDConstraintCompiler(typeResolver));
                put(IRI.class, new IRIConstraintCompiler(typeResolver));
                put(RelativeIRI.class, new RelativeIRIConstraintCompiler(typeResolver));
            }
        };
    }

    public void compileConstraint(JavaClass javaClass, String name, Type node) throws CompilerException {
        Type base;

        if (node instanceof TypeReference) {
            base = typeResolver.getBase((TypeReference) node);
        } else {
            base = node;
        }

        if (!compilers.containsKey(base.getClass())) {
            throw new CompilerException("Constraints for type %s not yet supported", base.getClass().getSimpleName());
        }

        AbstractConstraintCompiler<?> compiler = compilers.get(base.getClass());

        ConstraintDefinition constraintDef;

        try {
            constraintDef = compiler.compileConstraints(node, base);
        } catch (CompilerException e) {
            throw new CompilerException("Error in constraints for type %s: %s", e, name, e.getMessage());
        }

        if (constraintDef != null) {
            if (constraintDef.isEmpty()) {
                throw new CompilerException("Constraints for type %s excludes all values", name);
            }

            compiler.addConstraint(javaClass, constraintDef);
        }

        javaClass.addImport(ConstraintViolatedException.class);

        return;
    }

}
