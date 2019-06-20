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

import ch.eskaton.asn4j.compiler.constraints.ConstraintDefinition;
import ch.eskaton.asn4j.compiler.java.JavaClass;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.TypeAssignmentNode;
import ch.eskaton.asn4j.parser.ast.types.SimpleDefinedType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;

public abstract class AbstractTypeReferenceCompiler<T extends SimpleDefinedType>
        implements NamedCompiler<T, CompiledType> {

    public CompiledType compile(CompilerContext ctx, String name, T node) {
        JavaClass javaClass = ctx.createClass(name, node, isConstructed(ctx, name));

        ConstraintDefinition constraintDef = ctx.compileConstraint(javaClass, name, node);
        ctx.finishClass();

        if (node.getParameters() != null) {
            throw new CompilerException("ParameterizedTypeReference not yet supported");
        }

        return new CompiledType(node, constraintDef);
    }

    protected boolean isConstructed(CompilerContext ctx, String type) {
        // TODO: what to do if the type isn't known in the current module
        TypeAssignmentNode assignment = (TypeAssignmentNode) ctx.getModule().getBody().getAssignments(type);
        Type base = assignment.getType();
        ASN1Tag.Mode mode = CompilerUtils.getTaggingMode(ctx.getModule(), base);

        if (ASN1Tag.Mode.EXPLICIT.equals(mode)) {
            return true;
        }

        return !ctx.isBuiltin(ctx.getBase(base).getClass().getSimpleName());
    }

}