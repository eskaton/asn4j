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

import ch.eskaton.asn4j.parser.ast.types.ComponentType;
import ch.eskaton.asn4j.parser.ast.types.SetType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.runtime.TagId;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.util.HashMap;

public class SetCompiler extends AbstractCollectionCompiler<SetType> {

    public SetCompiler() {
        super(TypeName.SET, (c, t) -> new TagUniquenessVerifier(c, t));
    }

    private static class TagUniquenessVerifier implements ComponentVerifier {

        private final CompilerContext ctx;

        private final HashMap<TagId, ComponentType> seenTags = new HashMap<>();

        private final String typeName;

        public TagUniquenessVerifier(CompilerContext ctx, String typeName) {
            this.ctx = ctx;
            this.typeName = typeName;
        }

        public void verify(ComponentType component) {
            var tagId = getTagId(ctx, component);
            var seenComponent = seenTags.get(tagId);

            if (seenComponent != null) {
                throw new CompilerException("Duplicate tags in %s %s: %s and %s", TypeName.SET, typeName,
                        getName(seenComponent), getName(component));
            }

            seenTags.put(tagId, component);
        }

        private TagId getTagId(CompilerContext ctx, ComponentType component) {
            var type = getType(component);
            var tag = ctx.resolveType(type).getTag();

            if (tag != null) {
                return CompilerUtils.toTagId(tag);
            }

            return ctx.getTagId(type);
        }

        private Type getType(ComponentType component) {
            switch (component.getCompType()) {
                case NAMED_TYPE:
                    // fall through
                case NAMED_TYPE_DEF:
                    // fall through
                case NAMED_TYPE_OPT:
                    return component.getNamedType().getType();
                case TYPE:
                    return component.getType();
                default:
                    throw new IllegalCompilerStateException("Unexpected component type: %s", component.getCompType());
            }
        }

        private String getName(ComponentType seenComponent) {
            return getType(seenComponent).toString();
        }

    }


}
