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
import ch.eskaton.asn4j.compiler.java.objs.JavaClass;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.types.CollectionOfType;
import ch.eskaton.asn4j.parser.ast.types.SetOfType;
import ch.eskaton.asn4j.parser.ast.types.Type;

import java.util.LinkedList;

public class SetOfCompiler implements NamedCompiler<SetOfType, CompiledType> {

    public CompiledType compile(CompilerContext ctx, String name, SetOfType node) {
        JavaClass javaClass = ctx.createClass(name, node, true);

        String parameterizedTypeName = getTypeParameter(ctx, node);

        javaClass.setTypeParam(parameterizedTypeName);

        ConstraintDefinition constraintDef = null;

        if (node.hasConstraint()) {
            constraintDef = ctx.compileConstraint(javaClass, name, node);
        }

        ctx.finishClass();

        return new CompiledType(node, constraintDef);
    }

    private String getTypeParameter(CompilerContext ctx, SetOfType node) {
        LinkedList<String> typeNames = new LinkedList<>();
        Type type = node;

        while (type instanceof CollectionOfType) {
            type = ((CollectionOfType) type).getType();
            typeNames.push(ctx.getTypeName(type));
        }

        String parameterizedTypeName = null;

        while (!typeNames.isEmpty()) {
            if (parameterizedTypeName == null) {
                parameterizedTypeName = typeNames.pop();
            } else {
                parameterizedTypeName = typeNames.pop() + "<" + parameterizedTypeName + ">";
            }
        }

        return parameterizedTypeName;
    }

}
