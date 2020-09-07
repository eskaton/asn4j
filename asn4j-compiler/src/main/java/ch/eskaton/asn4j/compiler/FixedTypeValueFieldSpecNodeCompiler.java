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

import ch.eskaton.asn4j.compiler.results.CompiledFixedTypeValueField;
import ch.eskaton.asn4j.parser.ast.DefaultValueSpecNode;
import ch.eskaton.asn4j.parser.ast.FixedTypeValueFieldSpecNode;
import ch.eskaton.asn4j.parser.ast.OptionalSpecNode;

public class FixedTypeValueFieldSpecNodeCompiler implements NamedCompiler<FixedTypeValueFieldSpecNode,
        CompiledFixedTypeValueField> {

    @Override
    public CompiledFixedTypeValueField compile(CompilerContext ctx, String name, FixedTypeValueFieldSpecNode node) {
        var type = node.getType();
        var compiledType = ctx.getCompiledType(type);
        var optionalitySpec = node.getOptionalitySpec();
        var compiledField = new CompiledFixedTypeValueField(node.getReference(), compiledType, node.isUnique());

        if (optionalitySpec instanceof DefaultValueSpecNode valueSpecNode) {
            if (node.isUnique()) {
                throw new CompilerException(optionalitySpec.getPosition(),
                        "Default value on field %s in object class %s not allowed because it's unique",
                        node.getReference(), name);
            }

            var value = valueSpecNode.getSpec();
            var valueClass = ctx.getValueType(type);
            var defaultValue = ctx.resolveGenericValue(valueClass, type, value);

            compiledField.setDefaultValue(defaultValue);
        } else if (optionalitySpec instanceof OptionalSpecNode) {
            compiledField.setOptional(true);
        } else if (optionalitySpec != null) {
            throw new IllegalCompilerStateException("Invalid optionality spec for FixedTypeValueField");
        }

        return compiledField;
    }

}
