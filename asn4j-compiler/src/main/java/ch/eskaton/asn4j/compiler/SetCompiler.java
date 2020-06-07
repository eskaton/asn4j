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

import ch.eskaton.asn4j.compiler.constraints.ConstraintDefinition;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.types.ComponentType;
import ch.eskaton.asn4j.parser.ast.types.SetType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.Tag;
import ch.eskaton.asn4j.runtime.TagId;
import ch.eskaton.asn4j.runtime.types.TypeName;
import ch.eskaton.commons.collections.Tuple2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class SetCompiler extends CollectionCompiler<SetType> {

    @Override
    public CompiledType compile(CompilerContext ctx, String name, SetType node) {
        var javaClass = ctx.createClass(name, node, true);
        var components = new ArrayList<Tuple2<String, CompiledType>>();
        var seenTags = new HashMap<TagId, ComponentType>();

        for (ComponentType component : node.getAllComponents()) {
            TagId tagId = getTagId(ctx, component);
            ComponentType seenComponent = seenTags.get(tagId);

            if (seenComponent != null) {
                throw new CompilerException("Duplicate tags in set %s: %s and %s", name,
                        getName(seenComponent), getName(component));
            }

            try {
                components.addAll(ctx.<ComponentType, ComponentTypeCompiler>getCompiler(ComponentType.class)
                        .compile(ctx, component));
            } catch (CompilerException e) {
                if (component.getNamedType() != null) {
                    throw new CompilerException("Failed to compile component %s in %s %s", e,
                            component.getNamedType().getName(), TypeName.SEQUENCE, name);
                } else {
                    throw new CompilerException("Failed to compile a component in %s %s", e,
                            TypeName.SEQUENCE, name);
                }
            }

            seenTags.put(tagId, component);
        }

        CompiledType compiledType = new CompiledCollectionType(node, name, components);
        ConstraintDefinition constraintDef;

        if (node.hasConstraint()) {
            constraintDef = ctx.compileConstraint(javaClass, name, compiledType);

            compiledType.setConstraintDefinition(constraintDef);
        }

        ctx.finishClass();

        return compiledType;
    }

    private TagId getTagId(CompilerContext ctx, ComponentType component) {
        Type type = getAttribute(component, c -> c.getNamedType().getType(), ComponentType::getType);
        Tag tag = ctx.resolveType(type).getTag();

        if (tag != null) {
            return CompilerUtils.toTagId(tag);
        }

        return ctx.getTagId(type);
    }

    private <T> T getAttribute(ComponentType component, Function<ComponentType, T> namedFunction,
            Function<ComponentType, T> function) {
        switch (component.getCompType()) {
            case NAMED_TYPE:
                // fall through
            case NAMED_TYPE_DEF:
                // fall through
            case NAMED_TYPE_OPT:
                return namedFunction.apply(component);
            case TYPE:
                return function.apply(component);
            default:
                throw new CompilerException("Unexpected component type: " + component.getCompType());
        }
    }

    private String getName(ComponentType seenComponent) {
        return getAttribute(seenComponent, c -> c.getNamedType().getName(), c -> "n/a");
    }

}
