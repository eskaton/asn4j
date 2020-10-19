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

import ch.eskaton.asn4j.compiler.Clone;
import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerUtils;
import ch.eskaton.asn4j.compiler.NamedCompiler;
import ch.eskaton.asn4j.compiler.Parameters;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.types.SimpleDefinedType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;

import java.util.Optional;
import java.util.function.UnaryOperator;

import static ch.eskaton.asn4j.compiler.CompilerUtils.createParameters;
import static ch.eskaton.asn4j.compiler.CompilerUtils.isAnyTypeReference;
import static ch.eskaton.asn4j.compiler.CompilerUtils.updateParameters;
import static ch.eskaton.asn4j.compiler.ParameterUsageVerifier.checkUnusedParameters;

public class TypeReferenceCompiler extends AbstractTypeReferenceCompiler<TypeReference> {

    @Override
    public CompiledType compile(CompilerContext ctx, String name, TypeReference typeReference,
            Optional<Parameters> maybeParameters) {
        var maybeTypeRefParams = typeReference.getParameters();

        if (maybeTypeRefParams.isPresent()) {
            return compileParameterizedType(ctx, name, typeReference, maybeParameters);
        }

        if (maybeParameters.isPresent()) {
            var maybeResolvedType = ctx.getTypeParameter(maybeParameters.get(), typeReference);

            if (maybeResolvedType.isPresent()) {
                return compileTypeParameter(ctx, name, typeReference, maybeResolvedType.get(), maybeParameters);
            }
        }

        var compiledType = ctx.createCompiledType(typeReference, name);
        var tags = CompilerUtils.getTagIds(ctx, compiledType.getType());

        compiledType.setTags(tags);

        ctx.compileConstraintAndModule(name, compiledType, maybeParameters).ifPresent(constraintAndModule -> {
            compiledType.setConstraintDefinition(constraintAndModule.get_1());
            compiledType.setModule(constraintAndModule.get_2());
        });

        return compiledType;
    }

    private CompiledType compileTypeParameter(CompilerContext ctx, String name, TypeReference typeReference,
            Type resolvedType, Optional<Parameters> maybeParameters) {
        if (isAnyTypeReference(resolvedType)) {
            return ctx.getCompiledType((SimpleDefinedType) resolvedType);
        }

        if (typeReference.hasConstraint()) {
            resolvedType = Clone.clone(resolvedType);
            resolvedType.setConstraints(typeReference.getConstraints());
        }

        var compiler = ctx.<Type, NamedCompiler<Type, CompiledType>>getCompiler((Class<Type>) resolvedType.getClass());

        return compiler.compile(ctx, name, resolvedType, maybeParameters);
    }

    private CompiledType compileParameterizedType(CompilerContext ctx, String name, TypeReference typeReference,
            Optional<Parameters> maybeParameters) {
        var updateParameters = UnaryOperator.<Parameters>identity();

        if (maybeParameters.isPresent()) {
            updateParameters = parameters -> updateParameters(maybeParameters.get(), parameters);
        }

        return getCompiledParameterizedType(ctx, name, typeReference, updateParameters);
    }

    private CompiledType getCompiledParameterizedType(CompilerContext ctx, String name, TypeReference typeReference,
            UnaryOperator<Parameters> parametersProvider) {
        var typeName = typeReference.getType();
        var compiledParameterizedType = ctx.getCompiledParameterizedType(typeName);
        var parameters = createParameters(typeReference, name, compiledParameterizedType);
        var updatedParameters = parametersProvider.apply(parameters);
        var maybeUpdatedParameters = Optional.of(updatedParameters);
        var type = compiledParameterizedType.getType();
        var compiler = ctx.<Type, NamedCompiler<Type, CompiledType>>getCompiler((Class<Type>) type.getClass());
        var compiledType = compiler.compile(ctx, name, type, maybeUpdatedParameters);

        checkUnusedParameters(maybeUpdatedParameters);

        return compiledType;
    }

}
