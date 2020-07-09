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
import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.il.BooleanExpression;
import ch.eskaton.asn4j.compiler.il.Module;
import ch.eskaton.asn4j.compiler.java.objs.JavaClass;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.constraints.Constraint;
import ch.eskaton.asn4j.parser.ast.types.BMPString;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.BooleanType;
import ch.eskaton.asn4j.parser.ast.types.Choice;
import ch.eskaton.asn4j.parser.ast.types.EnumeratedType;
import ch.eskaton.asn4j.parser.ast.types.GeneralString;
import ch.eskaton.asn4j.parser.ast.types.GraphicString;
import ch.eskaton.asn4j.parser.ast.types.IA5String;
import ch.eskaton.asn4j.parser.ast.types.IRI;
import ch.eskaton.asn4j.parser.ast.types.ISO646String;
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
import ch.eskaton.asn4j.parser.ast.types.Null;
import ch.eskaton.asn4j.parser.ast.types.NumericString;
import ch.eskaton.asn4j.parser.ast.types.ObjectIdentifier;
import ch.eskaton.asn4j.parser.ast.types.OctetString;
import ch.eskaton.asn4j.parser.ast.types.PrintableString;
import ch.eskaton.asn4j.parser.ast.types.RelativeIRI;
import ch.eskaton.asn4j.parser.ast.types.RelativeOID;
import ch.eskaton.asn4j.parser.ast.types.SequenceOfType;
import ch.eskaton.asn4j.parser.ast.types.SequenceType;
import ch.eskaton.asn4j.parser.ast.types.SetOfType;
import ch.eskaton.asn4j.parser.ast.types.SetType;
import ch.eskaton.asn4j.parser.ast.types.T61String;
import ch.eskaton.asn4j.parser.ast.types.TeletexString;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.UTF8String;
import ch.eskaton.asn4j.parser.ast.types.UniversalString;
import ch.eskaton.asn4j.parser.ast.types.VideotexString;
import ch.eskaton.asn4j.parser.ast.types.VisibleString;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;
import ch.eskaton.commons.collections.Maps;
import ch.eskaton.commons.collections.Tuple2;

import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonList;

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
                .put(SequenceOfType.class, new SequenceOfConstraintCompiler(ctx))
                .put(SetType.class, new SetConstraintCompiler(ctx))
                .put(SequenceType.class, new SequenceConstraintCompiler(ctx))
                .put(Choice.class, new ChoiceConstraintCompiler(ctx))
                .put(VisibleString.class, new VisibleStringConstraintCompiler(ctx))
                .put(ISO646String.class, new VisibleStringConstraintCompiler(ctx))
                .put(GeneralString.class, new GeneralStringConstraintCompiler(ctx))
                .put(GraphicString.class, new GraphicStringConstraintCompiler(ctx))
                .put(IA5String.class, new IA5StringConstraintCompiler(ctx))
                .put(VideotexString.class, new VideotexStringConstraintCompiler(ctx))
                .put(TeletexString.class, new TeletexStringConstraintCompiler(ctx))
                .put(T61String.class, new TeletexStringConstraintCompiler(ctx))
                .put(PrintableString.class, new PrintableStringConstraintCompiler(ctx))
                .put(NumericString.class, new NumericStringConstraintCompiler(ctx))
                .put(UTF8String.class, new UTF8StringConstraintCompiler(ctx))
                .put(UniversalString.class, new UniversalStringConstraintCompiler(ctx))
                .put(BMPString.class, new BMPStringConstraintCompiler(ctx))
                .build();
    }

    public ConstraintDefinition compileConstraint(JavaClass javaClass, String name, CompiledType compiledType) {
        ConstraintDefinition definition;

        try {
            definition = compileConstraintAux(compiledType);
        } catch (CompilerException e) {
            throw new CompilerException("Error in constraints for type %s: %s", e, name, e.getMessage());
        }

        if (definition != null) {
            Module module = new Module();

            addConstraint(compiledType, module, definition);

            javaClass.addModule(ctx, module);
        }

        javaClass.addImport(ConstraintViolatedException.class);

        return definition;
    }

    private AbstractConstraintCompiler getCompiler(CompiledType compiledType) {
        if (!compilers.containsKey(compiledType.getType().getClass())) {
            throw new CompilerException("Constraints for type %s not yet supported",
                    compiledType.getType().getClass().getSimpleName());
        }

        return compilers.get(compiledType.getType().getClass());
    }

    public ConstraintDefinition compileConstraint(CompiledType compiledType) {
        try {
            return compileConstraintAux(compiledType);
        } catch (CompilerException e) {
            throw new CompilerException("Error in constraint: %s", e, e.getMessage());
        }
    }

    private Tuple2<AbstractConstraintCompiler, CompiledType> getCompilerAndType(CompiledType compiledType) {
        var compiledBaseType = ctx.getCompiledBaseType(compiledType);
        var compiler = getCompiler(compiledBaseType);

        return Tuple2.of(compiler, compiledBaseType);
    }

    private ConstraintDefinition compileConstraintAux(CompiledType compiledType) {
        var compilerAndType = getCompilerAndType(compiledType);

        return compilerAndType.get_1().compileConstraints(compiledType.getType(), compilerAndType.get_2());
    }

    public void addConstraint(CompiledType compiledType, Module module, ConstraintDefinition definition) {
        getCompilerAndType(compiledType).get_1().addConstraint(compiledType, module, definition);
    }

    public ConstraintDefinition compileConstraint(Type type, Constraint constraint) {
        var compilerAndType = getCompilerAndType(ctx.getCompiledBaseType(type));

        return compilerAndType.get_1().compileConstraints(compilerAndType.get_2(), singletonList(constraint),
                Optional.empty());
    }

    public ConstraintDefinition compileConstraint(CompiledType compiledType, Constraint constraint) {
        var compilerAndType = getCompilerAndType(compiledType);

        return compilerAndType.get_1().compileConstraints(compilerAndType.get_2(), singletonList(constraint),
                Optional.empty());
    }

    public Optional<BooleanExpression> buildExpression(Module module, CompiledType compiledType, Node node) {
        var compilerAndType = getCompilerAndType(compiledType);

        return compilerAndType.get_1().buildExpression(module, compilerAndType.get_2(), node);
    }

}
