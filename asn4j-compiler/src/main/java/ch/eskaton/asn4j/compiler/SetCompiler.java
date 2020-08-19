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

import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.types.OpenType;
import ch.eskaton.asn4j.parser.ast.types.SetType;
import ch.eskaton.asn4j.runtime.TagId;
import ch.eskaton.asn4j.runtime.types.TypeName;
import ch.eskaton.commons.collections.Tuple2;

import java.util.HashMap;
import java.util.Set;

public class SetCompiler extends AbstractCollectionCompiler<SetType> {

    public SetCompiler() {
        super(TypeName.SET, TagUniquenessVerifier::new, OpenTypeVerifier::new);
    }

    private static class TagUniquenessVerifier implements ComponentVerifier {

        private final CompilerContext ctx;

        private final HashMap<TagId, Tuple2<String, CompiledType>> seenTags = new HashMap<>();

        public TagUniquenessVerifier(CompilerContext ctx) {
            this.ctx = ctx;
        }

        public void verify(String name, CompiledType component) {
            var tagIds = getTagId(ctx, component);

            tagIds.forEach(tagId -> {
                var seenComponent = seenTags.get(tagId);

                if (seenComponent != null) {
                    throw new CompilerException("Duplicate tags: %s and %s", seenComponent.get_1(), name);
                }
            });

            tagIds.stream().forEach(tagId -> seenTags.put(tagId, Tuple2.of(name, component)));
        }

        private Set<TagId> getTagId(CompilerContext ctx, CompiledType component) {
            var type = ctx.resolveSelectedType(component.getType());

            return ctx.getTagId(type);
        }

    }

    private static class OpenTypeVerifier implements ComponentVerifier {

        private int componentCount = 0;

        private Tuple2<String, CompiledType> untaggedOpenType;

        public OpenTypeVerifier(CompilerContext compilerContext) {
        }

        public void verify(String name, CompiledType component) {
            if (component.getType() instanceof OpenType openType) {
                if (untaggedOpenType == null && openType.getTag() == null) {
                    untaggedOpenType = Tuple2.of(name, component);
                }
            }

            if (componentCount >= 1 && untaggedOpenType != null) {
                throw new CompilerException("%s contains the open type %s which is ambiguous",
                        untaggedOpenType.get_2().getParent().getName(), untaggedOpenType.get_1());
            }

            componentCount++;
        }

    }

}
