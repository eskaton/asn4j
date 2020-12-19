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

package ch.eskaton.asn4j.compiler.parameters;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.compiler.objects.ObjectClassNodeCompiler;
import ch.eskaton.asn4j.compiler.results.CompiledObjectClass;
import ch.eskaton.asn4j.parser.ast.DummyGovernor;
import ch.eskaton.asn4j.parser.ast.Governor;
import ch.eskaton.asn4j.parser.ast.Node;
import ch.eskaton.asn4j.parser.ast.ObjectClassNode;
import ch.eskaton.asn4j.parser.ast.ParamGovernorNode;
import ch.eskaton.asn4j.parser.ast.types.Type;

import java.util.Optional;

import static ch.eskaton.asn4j.compiler.parameters.ParametersHelper.getObjectClassParameter;
import static ch.eskaton.asn4j.compiler.parameters.ParametersHelper.getTypeParameter;

public class ParameterGovernorHelper {

    private ParameterGovernorHelper() {
    }

    public static Type getParameterType(CompilerContext ctx, Parameters parameters, ParamGovernorNode paramGovernor) {
        Node type = null;

        if (paramGovernor instanceof Governor governor) {
            type = governor.getType();
        } else if (paramGovernor instanceof DummyGovernor dummyGovernor) {
            var reference = dummyGovernor.getReference();
            var typeReference = reference.getName();
            var firstChar = typeReference.substring(0, 1);

            if (firstChar.equals(firstChar.toUpperCase())) {
                var resolvedType = getTypeParameter(parameters, typeReference);

                if (resolvedType.isPresent()) {
                    type = resolvedType.get();
                } else {
                    try {
                        type = ctx.getCompiledType(typeReference).getType();
                    } catch (CompilerException e) {
                        throw new CompilerException(dummyGovernor.getPosition(),
                                "The Governor references the type %s which can't be resolved", typeReference);
                    }
                }
            } else {
                throw new CompilerException(dummyGovernor.getPosition(),
                        "The Governor '%s' is not a valid typereference", typeReference);
            }
        } else if (paramGovernor != null) {
            throw new IllegalCompilerStateException(paramGovernor.getPosition(), "Unexpected governor type %s",
                    paramGovernor.getClass().getSimpleName());
        }

        if (type instanceof Type) {
            return (Type) type;
        }

        return null;
    }

    public static CompiledObjectClass getParameterObjectClass(CompilerContext ctx, Parameters parameters,
            ParamGovernorNode paramGovernor) {
        Node objectClass = null;

        if (paramGovernor instanceof Governor governor) {
            objectClass = governor.getType();
        } else if (paramGovernor instanceof DummyGovernor dummyGovernor) {
            var reference = dummyGovernor.getReference();
            var objectClassReference = reference.getName();

            if (objectClassReference.equals(objectClassReference.toUpperCase())) {
                var resolvedObjectClass = getObjectClassParameter(parameters, objectClassReference);

                if (resolvedObjectClass.isPresent()) {
                    objectClass = resolvedObjectClass.get();
                } else {
                    try {
                        return ctx.getCompiledObjectClass(objectClassReference);
                    } catch (CompilerException e) {
                        throw new CompilerException(dummyGovernor.getPosition(),
                                "The Governor references the object class %s which can't be resolved",
                                objectClassReference);
                    }
                }
            } else {
                throw new CompilerException(dummyGovernor.getPosition(),
                        "The Governor '%s' is not a valid objectclassreference", objectClassReference);
            }
        } else if (paramGovernor != null) {
            throw new IllegalCompilerStateException(paramGovernor.getPosition(), "Unexpected governor type %s",
                    paramGovernor.getClass().getSimpleName());
        }

        if (objectClass instanceof ObjectClassNode) {
            return new ObjectClassNodeCompiler().compile(ctx, null, (ObjectClassNode) objectClass, Optional.empty());
        }

        return null;
    }

}
