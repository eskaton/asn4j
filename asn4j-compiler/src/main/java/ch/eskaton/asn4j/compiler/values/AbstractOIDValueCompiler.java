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
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.Parameters;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.compiler.results.CompiledValue;
import ch.eskaton.asn4j.compiler.values.formatters.ValueFormatter;
import ch.eskaton.asn4j.parser.ast.OIDComponentNode;
import ch.eskaton.asn4j.parser.ast.values.AbstractOIDValue;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractOIDValueCompiler<V extends AbstractOIDValue>
        extends AbstractOIDOrIRIValueCompiler<V> {

    public AbstractOIDValueCompiler(TypeName typeName, Class<V> valueClass) {
        super(typeName, valueClass);
    }

    @Override
    public V doCompile(CompilerContext ctx, CompiledType compiledType, Value value,
            Optional<Parameters> maybeParameters) {
        return resolveComponents(super.doCompile(ctx, compiledType, value, maybeParameters), ctx);
    }

    protected List<OIDComponentNode> resolveComponents(CompilerContext ctx, V value) {
        var components = new ArrayList<OIDComponentNode>();
        var componentPos = 1;

        for (OIDComponentNode component : value.getComponents()) {
            try {
                try {
                    components.add(resolveComponentId(ctx, component));
                } catch (ValueResolutionException e) {
                    components.addAll(resolveComponent(ctx, component, componentPos).orElseThrow(() -> e));
                }
            } catch (ValueResolutionException e) {
                throw new ValueResolutionException(value.getPosition(), "Failed to resolve component of %s value: %s",
                        e, getTypeName(), ValueFormatter.formatValue(value));
            }

            componentPos++;
        }

        return components;
    }

    protected abstract Optional<List<OIDComponentNode>> resolveComponent(CompilerContext ctx,
            OIDComponentNode component, int componentPos);

    protected List<OIDComponentNode> resolveOIDReference(CompilerContext ctx, OIDComponentNode component) {
        V referencedOidValue;
        var valueClass = getValueClass();

        try {
            referencedOidValue = ctx.resolveValue(valueClass, component.getName());
        } catch (ValueResolutionException e2) {
            referencedOidValue = ctx.resolveValue(valueClass, component.getDefinedValue());
        }

        return resolveComponents(ctx, referencedOidValue);
    }

    protected OIDComponentNode resolveComponentId(CompilerContext ctx, OIDComponentNode component) {
        Integer id = component.getId();

        if (id != null) {
            return component;
        }

        var definedValue = component.getDefinedValue();
        var name = component.getName();
        CompiledValue compiledValue = null;

        try {
            if (definedValue != null) {
                compiledValue = ctx.getCompiledValue(definedValue);
            } else if (name != null) {
                compiledValue = ctx.getCompiledValue(name);
            }
        } catch (CompilerException e) {
            // ignore
        }

        if (compiledValue != null && compiledValue.getValue() instanceof IntegerValue integerValue) {
            var componentId = integerValue.getValue().intValue();

            return new OIDComponentNode(component.getPosition(), componentId, component.getName());
        }

        throw new ValueResolutionException(component.getPosition(), "Failed to resolve component of %s value: %s",
                getTypeName(), component);
    }

    private V resolveComponents(V resolvedValue, CompilerContext ctx) {
        var components = resolveComponents(ctx, resolvedValue);

        resolvedValue.setComponents(components);

        return resolvedValue;
    }

}
