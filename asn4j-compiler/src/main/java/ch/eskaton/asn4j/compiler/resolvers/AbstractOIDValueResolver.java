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

package ch.eskaton.asn4j.compiler.resolvers;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.utils.ValueFormatter;
import ch.eskaton.asn4j.parser.ast.OIDComponentNode;
import ch.eskaton.asn4j.parser.ast.ValueOrObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.types.AbstractOID;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.AbstractOIDValue;
import ch.eskaton.asn4j.parser.ast.values.DefinedValue;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractOIDValueResolver<T extends AbstractOID, V extends AbstractOIDValue>
        extends AbstractOIDOrIRIValueResolver<T, V> {

    public AbstractOIDValueResolver(CompilerContext ctx, Class<T> typeClass, Class<V> valueClass) {
        super(ctx, typeClass, valueClass);
    }

    @Override
    protected V resolve(ValueOrObjectAssignmentNode<?, ?> valueAssignment) {
        return resolveComponents(super.resolve(valueAssignment), ctx);
    }

    @Override
    public V resolve(Optional<Type> type, V value) {
        return resolveComponents(value, ctx);
    }

    @Override
    public V resolveValue(CompilerContext ctx, Value value, Class<V> valueClass) {
        return resolveComponents(super.resolveValue(ctx, value, valueClass), ctx);
    }

    protected List<OIDComponentNode> resolveComponents(CompilerContext ctx, V value) {
        var components = new ArrayList<OIDComponentNode>();
        var componentPos = 1;

        for (OIDComponentNode component : value.getComponents()) {
            try {
                try {
                    components.add(resolveComponentId(ctx, component));
                } catch (CompilerException e) {
                    components.addAll(resolveComponent(ctx, component, componentPos).orElseThrow(() -> e));
                }
            } catch (CompilerException e) {
                throw new CompilerException(value.getPosition(), "Failed to resolve component of %s value: %s", e,
                        getTypeName(), ValueFormatter.formatValue(value));
            }

            componentPos++;
        }

        return components;
    }

    protected abstract Optional<List<OIDComponentNode>> resolveComponent(CompilerContext ctx,
            OIDComponentNode component, int componentPos);

    protected List<OIDComponentNode> resolveOIDReference(CompilerContext ctx, OIDComponentNode component,
            Class<V> valueClass) {
        V referencedOidValue;

        try {
            referencedOidValue = ctx.resolveValue(valueClass, component.getName());
        } catch (CompilerException e2) {
            referencedOidValue = ctx.resolveValue(valueClass, component.getDefinedValue());
        }

        return resolveComponents(ctx, referencedOidValue);
    }

    protected OIDComponentNode resolveComponentId(CompilerContext ctx, OIDComponentNode component) {
        Integer id = component.getId();

        if (id != null) {
            return component;
        }

        DefinedValue definedValue = component.getDefinedValue();

        int componentId;

        if (definedValue != null) {
            componentId = ctx.resolveValue(IntegerValue.class, definedValue).getValue().intValue();
        } else {
            componentId = ctx.resolveValue(IntegerValue.class, component.getName()).getValue().intValue();
        }

        return new OIDComponentNode(component.getPosition(), componentId, component.getName());
    }

    private V resolveComponents(V resolvedValue, CompilerContext ctx) {
        var components = resolveComponents(ctx, resolvedValue);

        resolvedValue.setComponents(components);

        return resolvedValue;
    }

}
