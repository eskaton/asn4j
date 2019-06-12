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

package ch.eskaton.asn4j.compiler.defaults;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.parser.ast.OIDComponentNode;
import ch.eskaton.asn4j.parser.ast.values.AbstractOIDValue;
import ch.eskaton.asn4j.parser.ast.values.ObjectIdentifierValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.verifiers.ObjectIdentifierVerifier;

import java.util.List;

public class ObjectIdentifierDefaultCompiler extends AbstractOIDDefaultCompiler<ObjectIdentifierValue> {

    @Override
    public Class<ObjectIdentifierValue> getValueClass() {
        return ObjectIdentifierValue.class;
    }

    @Override
    public void resolveComponents(CompilerContext ctx, Value value, List<Integer> ids) {
        resolveComponents(ctx, resolveValue(ctx, value, ObjectIdentifierValue.class), ids);
    }

    @Override
    public void verifyObjectIds(List<Integer> ids) {
        ObjectIdentifierVerifier.verifyComponents(ids);
    }

    public void resolveComponents(CompilerContext ctx, AbstractOIDValue value, List<Integer> ids) {
        int componentNum = 1;

        for (OIDComponentNode component : value.getComponents()) {
            try {
                try {
                    ids.add(getComponentId(ctx, component));
                } catch (CompilerException e) {
                    if (componentNum == 1) {
                        Integer id = resolveRootArc(component.getName());

                        if (id != null) {
                            ids.add(id);
                        } else {
                            resolveOIDReference(ctx, ids, component);
                        }
                    } else {
                        throw e;
                    }
                }
            } catch (CompilerException e) {
                throw new CompilerException("Failed to resolve component of object identifier value", e);
            }

            componentNum++;
        }
    }

    private Integer resolveRootArc(String name) {
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
