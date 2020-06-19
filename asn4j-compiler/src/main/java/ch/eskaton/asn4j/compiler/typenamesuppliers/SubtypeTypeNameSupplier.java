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

package ch.eskaton.asn4j.compiler.typenamesuppliers;

import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.CompilerUtils;
import ch.eskaton.asn4j.compiler.TypeConfiguration;
import ch.eskaton.asn4j.parser.ast.types.Type;

public class SubtypeTypeNameSupplier<T extends Type> implements TypeNameSupplier<T> {

    private final TypeConfiguration config;

    private final boolean subtypeNeeded;

    public SubtypeTypeNameSupplier(TypeConfiguration config) {
        this.config = config;
        this.subtypeNeeded = false;
    }

    public SubtypeTypeNameSupplier(TypeConfiguration config, boolean subtypeNeeded) {
        this.config = config;
        this.subtypeNeeded = subtypeNeeded;
    }

    @Override
    public String getName(T type, String name) {
        String typeName;

        if (isSubtypeNeeded(type, name)) {
            typeName = CompilerUtils.formatTypeName(name);
        } else {
            typeName = config.getRuntimeTypeClass(type.getClass()).getSimpleName();

            if (typeName == null) {
                throw new CompilerException("No runtime class available for type: %s", type);
            }
        }

        return typeName;
    }

    protected boolean isSubtypeNeeded(T type, String name) {
        return subtypeNeeded && name != null;
    }

}
