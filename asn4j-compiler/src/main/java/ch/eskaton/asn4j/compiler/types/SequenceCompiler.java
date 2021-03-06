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

import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionComponent;
import ch.eskaton.asn4j.parser.ast.types.OpenType;
import ch.eskaton.asn4j.parser.ast.types.SequenceType;
import ch.eskaton.asn4j.runtime.types.TypeName;

public class SequenceCompiler extends AbstractCollectionCompiler<SequenceType> {

    public SequenceCompiler() {
        super(TypeName.SEQUENCE, OpenTypeVerifier::new);
    }

    private static class OpenTypeVerifier implements ComponentVerifier<CompiledCollectionComponent> {

        private final TypeName typeName;

        private CompiledCollectionComponent optionalOpenTypeComponent;

        public OpenTypeVerifier(TypeName typeName) {
            this.typeName = typeName;
        }

        public void verify(CompiledCollectionComponent component) {
            var compiledType = component.getCompiledType();

            if (compiledType.getType() instanceof OpenType && component.isOptional()) {
                optionalOpenTypeComponent = component;
            } else if (optionalOpenTypeComponent != null) {
                var collectionTypeName = this.typeName.getName();
                var parentTypeName = optionalOpenTypeComponent.getCompiledType().getParent().getName();
                var openTypeName = optionalOpenTypeComponent.getName();

                throw new CompilerException("%s '%s' contains the optional open type '%s' which is ambiguous",
                        collectionTypeName, parentTypeName, openTypeName);
            }
        }

    }

}
