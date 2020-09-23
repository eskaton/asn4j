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

package ch.eskaton.asn4j.compiler.types;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.CompilerUtils;
import ch.eskaton.asn4j.compiler.NamedCompiler;
import ch.eskaton.asn4j.compiler.Parameters;
import ch.eskaton.asn4j.compiler.results.CompiledParameterizedType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.ParameterNode;
import ch.eskaton.asn4j.parser.ast.ReferenceNode;
import ch.eskaton.asn4j.parser.ast.types.SimpleDefinedType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;

import java.util.Optional;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.ParameterUsageVerifier.checkUnusedParameters;

public abstract class AbstractTypeReferenceCompiler<T extends SimpleDefinedType>
        implements NamedCompiler<T, CompiledType> {

    @Override
    public CompiledType compile(CompilerContext ctx, String name, T node, Optional<Parameters> maybeParameters) {
        if (node.getParameters().isEmpty()) {
            // ensure the type is resolvable
            ctx.resolveTypeReference(node);
        } else {
            var typeName = node.getType();
            var compiledParameterizedType = ctx.getCompiledParameterizedType(typeName);
            var parameters = createParameters(node, name, compiledParameterizedType);
            var type = compiledParameterizedType.getType();
            var compiler = ctx.<Type, NamedCompiler<Type, CompiledType>>getCompiler((Class<Type>) type.getClass());
            var compiledType = compiler.compile(ctx, name, type, parameters);

            checkUnusedParameters(parameters);

            return compiledType;
        }

        var tags = CompilerUtils.getTagIds(ctx, node);
        var javaClass = ctx.createClass(name, node, tags);
        var compiledType = ctx.createCompiledType(node, name);

        compiledType.setTags(tags);

        if (node.hasConstraint()) {
            var constraintDef = ctx.compileConstraintAndModule(name, compiledType);

            compiledType.setConstraintDefinition(constraintDef.get_1());

            javaClass.addModule(ctx, constraintDef.get_2());
            javaClass.addImport(ConstraintViolatedException.class);
        }

        ctx.finishClass();

        return compiledType;
    }

    private Optional<Parameters> createParameters(T node, String typeName,
            CompiledParameterizedType compiledParameterizedType) {
        var parameterizedTypeName = compiledParameterizedType.getName();
        var parameterValues = node.getParameters().get();
        var parameterValueCount = parameterValues.size();
        var parameterDefinitions = compiledParameterizedType.getParameters();

        if (parameterValueCount != parameterDefinitions.size()) {
            var parameterNames = parameterDefinitions.stream()
                    .map(ParameterNode::getReference)
                    .map(ReferenceNode::getName)
                    .collect(Collectors.joining(", "));

            throw new CompilerException(node.getPosition(), "'%s' passes %d parameters but '%s' expects: %s",
                    typeName, parameterValueCount, parameterizedTypeName, parameterNames);
        }

        return Optional.of(new Parameters(parameterizedTypeName, parameterDefinitions, parameterValues));
    }

}
