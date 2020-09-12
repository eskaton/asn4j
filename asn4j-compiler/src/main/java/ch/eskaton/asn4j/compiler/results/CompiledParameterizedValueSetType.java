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

import ch.eskaton.asn4j.parser.ast.ElementSetSpecsNode;
import ch.eskaton.asn4j.parser.ast.ParameterNode;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.runtime.utils.ToString;

import java.util.List;
import java.util.Objects;

public class CompiledParameterizedValueSetType implements CompilationResult {

    private final String name;

    private final Type type;

    private final ElementSetSpecsNode elementSet;

    private final List<ParameterNode> parameters;

    public CompiledParameterizedValueSetType(String name, Type type, ElementSetSpecsNode elementSet,
            List<ParameterNode> parameters) {
        this.name = name;
        this.type = type;
        this.elementSet = elementSet;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public ElementSetSpecsNode getElementSet() {
        return elementSet;
    }

    public List<ParameterNode> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return ToString.get(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CompiledParameterizedValueSetType that = (CompiledParameterizedValueSetType) o;

        return Objects.equals(name, that.name) &&
                Objects.equals(type, that.type) &&
                Objects.equals(elementSet, that.elementSet) &&
                Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, elementSet, parameters);
    }

}
