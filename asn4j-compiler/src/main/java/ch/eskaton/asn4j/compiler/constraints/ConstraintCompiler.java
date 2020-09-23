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
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.constraints.Constraint;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.commons.collections.Tuple2;

import java.util.Optional;

import static java.util.Collections.singletonList;

public class ConstraintCompiler {

    private CompilerContext ctx;

    @SuppressWarnings("serial")
    public ConstraintCompiler(CompilerContext ctx) {
        this.ctx = ctx;
    }

    public Optional<Tuple2<ConstraintDefinition, Module>> compileConstraintAndModule(String name,
            CompiledType compiledType) {
        var definition = compileConstraint(name, compiledType);

        if (definition.isPresent()) {
            var module = new Module();

            addConstraint(compiledType, module, definition.get());

            return Optional.of(Tuple2.of(definition.get(), module));
        }

        return Optional.empty();
    }

    public Optional<ConstraintDefinition> compileConstraint(String name, CompiledType compiledType) {
        try {
            return compileConstraintAux(compiledType);
        } catch (CompilerException e) {
            throw new CompilerException("Error in constraints for type %s: %s", e, name, e.getMessage());
        }
    }

    private AbstractConstraintCompiler getCompiler(CompiledType compiledType) {
        return ctx.getConstraintCompiler(compiledType.getType().getClass());
    }

    public Optional<ConstraintDefinition> compileConstraint(CompiledType compiledType) {
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

    private Optional<ConstraintDefinition> compileConstraintAux(CompiledType compiledType) {
        var compilerAndType = getCompilerAndType(compiledType);

        return compilerAndType.get_1().compileComponentConstraints(compiledType.getType(), compilerAndType.get_2());
    }

    public void addConstraint(CompiledType compiledType, Module module, ConstraintDefinition definition) {
        getCompilerAndType(compiledType).get_1().addConstraint(compiledType, module, definition);
    }

    public ConstraintDefinition compileConstraint(Type type, Constraint constraint) {
        var compilerAndType = getCompilerAndType(ctx.getCompiledBaseType(type));

        return compilerAndType.get_1().compileComponentConstraints(compilerAndType.get_2(), singletonList(constraint),
                Optional.empty());
    }

    public ConstraintDefinition compileConstraint(CompiledType compiledType, Constraint constraint) {
        var compilerAndType = getCompilerAndType(compiledType);

        return compilerAndType.get_1().compileComponentConstraints(compilerAndType.get_2(), singletonList(constraint),
                Optional.empty());
    }

    public Optional<BooleanExpression> buildExpression(Module module, CompiledType compiledType, Node node) {
        var compilerAndType = getCompilerAndType(compiledType);

        return compilerAndType.get_1().buildExpression(module, compilerAndType.get_2(), node);
    }

}
