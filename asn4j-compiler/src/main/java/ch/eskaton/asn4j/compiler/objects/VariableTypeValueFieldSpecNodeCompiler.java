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

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.compiler.NamedCompiler;
import ch.eskaton.asn4j.compiler.Parameters;
import ch.eskaton.asn4j.compiler.results.CompiledVariableTypeValueField;
import ch.eskaton.asn4j.parser.ast.DefaultValueSpecNode;
import ch.eskaton.asn4j.parser.ast.OptionalSpecNode;
import ch.eskaton.asn4j.parser.ast.VariableTypeValueFieldSpecNode;

import java.util.Optional;

public class VariableTypeValueFieldSpecNodeCompiler
        implements NamedCompiler<VariableTypeValueFieldSpecNode, CompiledVariableTypeValueField> {

    @Override
    public CompiledVariableTypeValueField compile(CompilerContext ctx, String name, VariableTypeValueFieldSpecNode node,
            Optional<Parameters> maybeParameters) {
        var optionalitySpec = node.getOptionalitySpec();
        var reference = node.getReference();
        var primitiveFieldNames = node.getFieldName().getPrimitiveFieldNames();

        if (primitiveFieldNames.size() != 1) {
            throw new IllegalCompilerStateException(node.getFieldName().getPosition(),
                    "Nested references not yet supported");
        }

        if (!primitiveFieldNames.get(0).isTypeFieldReference()) {
            throw new IllegalCompilerStateException(node.getFieldName().getPosition(),
                    "Only references to type fields are supported at the moment");
        }

        var fieldName = primitiveFieldNames.get(0).getReference();
        var compiledField = new CompiledVariableTypeValueField(reference, fieldName);

        if (optionalitySpec instanceof DefaultValueSpecNode) {
            var value = ((DefaultValueSpecNode) optionalitySpec).getSpec();

            compiledField.setDefaultValue(value);
        } else if (optionalitySpec instanceof OptionalSpecNode) {
            compiledField.setOptional(true);
        } else if (optionalitySpec != null) {
            throw new IllegalCompilerStateException("Invalid optionality spec for VariableTypeValueField");
        }

        return compiledField;
    }

}
