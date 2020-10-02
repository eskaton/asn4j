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
import ch.eskaton.asn4j.compiler.CompilerUtils;
import ch.eskaton.asn4j.compiler.results.CompiledComponent;
import ch.eskaton.asn4j.runtime.TagId;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.util.HashMap;

class TagUniquenessVerifier <C extends CompiledComponent> implements ComponentVerifier<C> {

    private final TypeName typeName;

    private final HashMap<TagId, CompiledComponent> seenTags = new HashMap<>();

    public TagUniquenessVerifier(TypeName typeName) {
        this.typeName = typeName;
    }

    public void verify(CompiledComponent component) {
        var componentName = component.getName();
        var compiledType = component.getCompiledType();
        var tagIds = CompilerUtils.getLeadingTagId(compiledType);

        tagIds.forEach(tagId -> {
            var seenComponent = seenTags.get(tagId);

            if (seenComponent != null) {
                var parentTypeName = seenComponent.getCompiledType().getParent().getName();
                var collectionTypeName = typeName.getName();

                throw new CompilerException("Duplicate tags in %s '%s': %s and %s", collectionTypeName,
                        parentTypeName, seenComponent.getName(), componentName);
            }

            seenTags.put(tagId, component);
        });
    }

}
