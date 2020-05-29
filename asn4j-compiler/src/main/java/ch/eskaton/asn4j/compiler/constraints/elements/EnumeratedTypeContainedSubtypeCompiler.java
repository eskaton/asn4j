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
package ch.eskaton.asn4j.compiler.constraints.elements;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.results.CompiledEnumeratedType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.compiler.results.EnumerationItems;
import ch.eskaton.asn4j.parser.ast.types.EnumeratedType;
import ch.eskaton.commons.collections.Tuple2;

import java.util.HashSet;
import java.util.Objects;

public class EnumeratedTypeContainedSubtypeCompiler extends ContainedSubtypeCompiler {

    public EnumeratedTypeContainedSubtypeCompiler(CompilerContext ctx) {
        super(ctx);
    }

    @Override
    protected boolean isAssignable(CompiledType compiledType, CompiledType compiledParentType) {
        return super.isAssignable(compiledType, compiledParentType) &&
                Objects.equals(getItems(compiledType), getItems(compiledParentType));
    }

    private Object getItems(CompiledType compiledType) {
        if (compiledType instanceof CompiledEnumeratedType) {
            return getItemsAux(compiledType);
        }

        return getItemsAux(ctx.getCompiledBaseType(compiledType.getType()));
    }

    private Object getItemsAux(CompiledType compiledType) {
        EnumerationItems roots = ((CompiledEnumeratedType) compiledType).getRoots();
        EnumerationItems additions = ((CompiledEnumeratedType) compiledType).getAdditions();

        return new Tuple2<>(((EnumeratedType) compiledType.getType()).isExtensible(),
                new HashSet<>(roots.copy().addAll(additions.getItems()).getItems()));
    }

}
