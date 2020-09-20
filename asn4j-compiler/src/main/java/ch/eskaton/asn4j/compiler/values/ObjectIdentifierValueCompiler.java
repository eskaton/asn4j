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

package ch.eskaton.asn4j.compiler.values;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerUtils;
import ch.eskaton.asn4j.parser.ast.OIDComponentNode;
import ch.eskaton.asn4j.parser.ast.values.ObjectIdentifierValue;
import ch.eskaton.asn4j.runtime.types.TypeName;
import ch.eskaton.asn4j.runtime.verifiers.ObjectIdentifierVerifier;

import java.util.List;
import java.util.Optional;

public class ObjectIdentifierValueCompiler extends AbstractOIDValueCompiler<ObjectIdentifierValue> {

    public ObjectIdentifierValueCompiler() {
        super(TypeName.OBJECT_IDENTIFIER, ObjectIdentifierValue.class);
    }

    @Override
    protected List<OIDComponentNode> resolveComponents(CompilerContext ctx, ObjectIdentifierValue value) {
        var components = super.resolveComponents(ctx, value);

        ObjectIdentifierVerifier.verifyComponents(CompilerUtils.getComponentIds(components));

        return components;
    }

    @Override
    protected Optional<List<OIDComponentNode>> resolveComponent(CompilerContext ctx, OIDComponentNode component,
            int componentPos) {
        if (componentPos == 1) {
            var name = component.getName();

            if (name != null) {
                var id = resolveRootArc(name);

                if (id != null) {
                    return Optional.of(List.of(new OIDComponentNode(component.getPosition(), id, name)));
                } else {
                    return Optional.of(resolveOIDReference(ctx, component));
                }
            }
        }

        return Optional.empty();
    }

    protected Integer resolveRootArc(String name) {
        switch (name) {
            case "itu-t":
            case "ccitt":
                return 0;
            case "iso":
                return 1;
            case "joint-iso-itu-t":
            case "joint-iso-ccitt":
                return 2;
            default:
                return null;
        }
    }

}
