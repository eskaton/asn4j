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

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.java.objs.JavaClass;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.BooleanType;
import ch.eskaton.asn4j.parser.ast.types.EnumeratedType;
import ch.eskaton.asn4j.parser.ast.types.IRI;
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
import ch.eskaton.asn4j.parser.ast.types.Null;
import ch.eskaton.asn4j.parser.ast.types.ObjectIdentifier;
import ch.eskaton.asn4j.parser.ast.types.OctetString;
import ch.eskaton.asn4j.parser.ast.types.RelativeIRI;
import ch.eskaton.asn4j.parser.ast.types.RelativeOID;
import ch.eskaton.asn4j.parser.ast.types.SetOfType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;
import ch.eskaton.commons.collections.Maps;

import java.util.Map;
import java.util.Optional;

public class ConstraintCompiler {

    private Map<Class<? extends Type>, AbstractConstraintCompiler> compilers;

    private CompilerContext ctx;

    @SuppressWarnings("serial")
    public ConstraintCompiler(CompilerContext ctx) {
        this.ctx = ctx;

        compilers = Maps.<Class<? extends Type>, AbstractConstraintCompiler>builder()
                .put(BooleanType.class, new BooleanConstraintCompiler(ctx))
                .put(IntegerType.class, new IntegerConstraintCompiler(ctx))
                .put(EnumeratedType.class, new EnumeratedTypeConstraintCompiler(ctx))
                .put(BitString.class, new BitStringConstraintCompiler(ctx))
                .put(Null.class, new NullConstraintCompiler(ctx))
                .put(ObjectIdentifier.class, new ObjectIdentifierConstraintCompiler(ctx))
                .put(OctetString.class, new OctetStringConstraintCompiler(ctx))
                .put(RelativeOID.class, new RelativeOIDConstraintCompiler(ctx))
                .put(IRI.class, new IRIConstraintCompiler(ctx))
                .put(RelativeIRI.class, new RelativeIRIConstraintCompiler(ctx))
                .put(SetOfType.class, new SetOfConstraintCompiler(ctx))
//                .put(VisibleString.class, new VisibleStringConstraintCompiler(typeResolver))
                .build();
    }

    public ConstraintDefinition compileConstraint(JavaClass javaClass, String name, Type node) {
        CompiledType compiledType = ctx.getCompiledBaseType(node);
        Optional<AbstractConstraintCompiler> maybeCompiler = getCompiler(compiledType);
        ConstraintDefinition constraintDef;

        if (!maybeCompiler.isPresent()) {
            return null;
        }

        AbstractConstraintCompiler compiler = maybeCompiler.get();

        try {
            constraintDef = compiler.compileConstraints(node, compiledType);
        } catch (CompilerException e) {
            throw new CompilerException("Error in constraints for type %s: %s", e, name, e.getMessage());
        }

        if (constraintDef != null) {
            compiler.addConstraint(node, javaClass, constraintDef);
        }

        javaClass.addImport(ConstraintViolatedException.class);

        return constraintDef;
    }

    public ConstraintDefinition compileConstraint(Type node) {
        CompiledType compiledType = ctx.getCompiledBaseType(node);
        Optional<AbstractConstraintCompiler> compiler = getCompiler(compiledType);

        if (compiler.isPresent()) {
            try {
                return compiler.get().compileConstraints(node, compiledType);
            } catch (CompilerException e) {
                throw new CompilerException("Error in constraint: %s", e, e.getMessage());
            }
        }

        return null;
    }

    private Optional<AbstractConstraintCompiler> getCompiler(CompiledType compiledType) {
        if (!compilers.containsKey(compiledType.getType().getClass())) {
//            throw new CompilerException("Constraints for type %s not yet supported",
//                    compiledType.getType().getClass().getSimpleName());
            return Optional.empty();
        }

        return Optional.of(compilers.get(compiledType.getType().getClass()));
    }

    public void addConstraint(Type type, JavaClass javaClass, ConstraintDefinition definition) {
        CompiledType compiledType = ctx.getCompiledBaseType(type);
        Optional<AbstractConstraintCompiler> maybeCompiler = getCompiler(compiledType);

        if (!maybeCompiler.isPresent()) {
            return;
        }

        AbstractConstraintCompiler compiler = maybeCompiler.get();

        compiler.addConstraint(type, javaClass, definition);
    }

}
