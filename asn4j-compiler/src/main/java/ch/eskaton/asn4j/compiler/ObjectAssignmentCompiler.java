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

package ch.eskaton.asn4j.compiler;

import ch.eskaton.asn4j.compiler.objects.ObjectDefnCompiler;
import ch.eskaton.asn4j.compiler.results.CompiledObject;
import ch.eskaton.asn4j.compiler.results.CompiledObjectClass;
import ch.eskaton.asn4j.parser.ast.ExternalObjectReference;
import ch.eskaton.asn4j.parser.ast.ObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ObjectDefnNode;
import ch.eskaton.asn4j.parser.ast.ObjectNode;
import ch.eskaton.asn4j.parser.ast.ObjectReference;

import java.util.Optional;
import java.util.function.UnaryOperator;

import static ch.eskaton.asn4j.compiler.CompilerUtils.updateParameters;
import static ch.eskaton.asn4j.compiler.ParameterUsageVerifier.checkUnusedParameters;

public class ObjectAssignmentCompiler implements Compiler<ObjectAssignmentNode> {

    public CompiledObject compile(CompilerContext ctx, ObjectAssignmentNode node) {
        String objectName = node.getReference();

        System.out.println("Compiling object " + objectName);

        var objectClass = ctx.getCompiledObjectClass(node.getObjectClassReference());
        var object = node.getObject();

        return compile(ctx, objectName, objectClass, object, Optional.empty());
    }

    private CompiledObject compile(CompilerContext ctx, String name, CompiledObjectClass objectClass,
            ObjectNode object, Optional<Parameters> maybeParameters) {
        if (object instanceof ObjectDefnNode objectDefnNode) {
            var compiler = ctx.<ObjectDefnNode, ObjectDefnCompiler>getCompiler(ObjectDefnNode.class);
            var objectDefinition = compiler.compile(objectClass, objectDefnNode, maybeParameters);

            return ctx.createCompiledObject(name, objectDefinition);
        } else if (object instanceof ObjectReference objectReference) {
            var maybeObjRefParameters = objectReference.getParameters();

            if (maybeObjRefParameters.isPresent()) {
                return compileParameterizedObject(ctx, name, objectClass, objectReference, maybeParameters);
            }

            var compiledObject = ctx.getCompiledObject(objectReference);

            return ctx.createCompiledObject(name, compiledObject.getObjectDefinition());
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
        var parameters = CompilerUtils.createParameters(objectReference, name, compiledParameterizedObject);
        var updatedParameters = parametersProvider.apply(parameters);
        var maybeUpdatedParameters = Optional.of(updatedParameters);
        var object = compiledParameterizedObject.getObject();
        var compiledObject = compile(ctx, name, objectClass, object, maybeUpdatedParameters);

        checkUnusedParameters(maybeUpdatedParameters);

        return compiledObject;
    }

}
