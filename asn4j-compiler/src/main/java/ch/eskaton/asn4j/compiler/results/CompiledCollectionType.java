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

package ch.eskaton.asn4j.compiler.results;

import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.runtime.utils.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CompiledCollectionType extends CompiledType implements HasComponents<CompiledCollectionComponent>,
        HasChildComponents {

    private List<CompiledCollectionComponent> components = new ArrayList<>();

    public CompiledCollectionType(Type type, String name) {
        super(type, name);
    }

    @Override
    public List<CompiledCollectionComponent> getComponents() {
        return components;
    }

    public void setComponents(List<CompiledCollectionComponent> components) {
        this.components = components;
    }

    @Override
    public List<CompiledType> getChildComponents() {
        return components.stream().map(CompiledComponent::getCompiledType).collect(Collectors.toList());
    }

    @Override
    public CompiledCollectionType copy() {
        var compiledType = new CompiledCollectionType(getType(), getName());

        copyAttributes(compiledType);

        return compiledType;
    }

    protected void copyAttributes(CompiledCollectionType compiledType) {
        super.copyAttributes(compiledType);

        compiledType.components = components;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        CompiledCollectionType that = (CompiledCollectionType) obj;

        return Objects.equals(components, that.components);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), components);
    }

    @Override
    public String toString() {
        return ToString.get(this);
    }

}
