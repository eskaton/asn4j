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

package ch.eskaton.asn4j.compiler.objects;

import ch.eskaton.asn4j.compiler.Compiler;
import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.CompilerUtils;
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.compiler.parameters.ParameterUsageVerifier;
import ch.eskaton.asn4j.compiler.parameters.Parameters;
import ch.eskaton.asn4j.compiler.results.CompiledObject;
import ch.eskaton.asn4j.compiler.results.CompiledObjectClass;
import ch.eskaton.asn4j.logging.Logger;
import ch.eskaton.asn4j.logging.LoggerFactory;
import ch.eskaton.asn4j.parser.ast.ExternalObjectReference;
import ch.eskaton.asn4j.parser.ast.ObjectDefnNode;
import ch.eskaton.asn4j.parser.ast.ObjectNode;
import ch.eskaton.asn4j.parser.ast.ObjectReference;

import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.function.UnaryOperator;

import static ch.eskaton.asn4j.compiler.parameters.ParameterUsageVerifier.checkUnusedParameters;
import static ch.eskaton.asn4j.compiler.parameters.ParametersHelper.createParameters;
import static ch.eskaton.asn4j.compiler.parameters.ParametersHelper.updateParameters;

public class ObjectNodeCompiler implements Compiler<ObjectNode> {

    private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public CompiledObject compile(CompilerContext ctx, String name, CompiledObjectClass objectClass,
            ObjectNode object, Optional<Parameters> maybeParameters) {
        if (object instanceof ObjectDefnNode objectDefnNode) {
            LOGGER.trace("Compiling object definition node %s", objectDefnNode);

            var compiler = ctx.<ObjectDefnNode, ObjectDefnCompiler>getCompiler(ObjectDefnNode.class);
            var objectDefinition = compiler.compile(objectClass, objectDefnNode, maybeParameters);

            return ctx.createCompiledObject(name, objectClass, objectDefinition);
        } else if (object instanceof ObjectReference objectReference) {
            LOGGER.trace("Compiling object reference %s", objectReference.getReference());

            var maybeObjRefParameters = objectReference.getParameters();

            if (maybeObjRefParameters.isPresent()) {
                return compileParameterizedObject(ctx, name, objectClass, objectReference, maybeParameters);
            }

            var compiledObject = ctx.getCompiledObject(objectReference);

            if (!objectClass.equals(compiledObject.getObjectClass())) {
                var formattedObjectReference = ObjectFormatter.formatObjectReference(objectReference);

                throw new CompilerException(object.getPosition(),
                        "Expected an object of class %s but %s refers to %s", objectClass.getName(),
                        formattedObjectReference, compiledObject.getObjectClass().getName());
            }

            return ctx.createCompiledObject(name, compiledObject.getObjectClass(), compiledObject.getObjectDefinition());
        } else {
            throw new IllegalCompilerStateException("Node type %s not yet supported", object.getClass().getSimpleName());
        }
    }

    protected CompiledObject compileParameterizedObject(CompilerContext ctx, String name,
            CompiledObjectClass objectClass, ObjectReference objectReference, Optional<Parameters> maybeParameters) {
        var updateParameters = UnaryOperator.<Parameters>identity();

        if (maybeParameters.isPresent()) {
            updateParameters = parameters -> updateParameters(maybeParameters.get(), parameters);
        }

        return getCompileParameterizedObject(ctx, name, objectClass, objectReference, updateParameters);
    }

    private CompiledObject getCompileParameterizedObject(CompilerContext ctx, String name,
            CompiledObjectClass objectClass, ObjectReference objectReference,
            UnaryOperator<Parameters> parametersProvider) {
        var reference = objectReference.getReference();
        var maybeModuleName = CompilerUtils.toExternalObjectReference(objectReference)
                .map(ExternalObjectReference::getModule);
        var compiledParameterizedObject = maybeModuleName
                .map(moduleName -> ctx.getCompiledParameterizedObject(moduleName, reference))
                .orElseGet(() -> ctx.getCompiledParameterizedObject(reference));
        var parameters = createParameters(objectReference, name, compiledParameterizedObject);
        var updatedParameters = parametersProvider.apply(parameters);
        var maybeUpdatedParameters = Optional.of(updatedParameters);
        var object = compiledParameterizedObject.getObject();
        var compiledObject = compile(ctx, name, objectClass, object, maybeUpdatedParameters);

        checkUnusedParameters(ParameterUsageVerifier.Kind.OBJECT, maybeUpdatedParameters);

        return compiledObject;
    }

}
