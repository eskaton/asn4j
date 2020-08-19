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

import ch.eskaton.asn4j.compiler.constraints.ConstraintDefinition;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.runtime.utils.ToString;

import java.util.Objects;

public class CompiledType implements CompilationResult {

    private CompiledType parent;

    private Type type;

    private String name;

    private ConstraintDefinition constraintDefinition;

    private CompiledObjectClass objectClass;

    private boolean optional;

    CompiledType(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public CompiledType getParent() {
        return parent;
    }

    public void setParent(CompiledType parent) {
        this.parent = parent;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public ConstraintDefinition getConstraintDefinition() {
        return constraintDefinition;
    }

    public void setConstraintDefinition(ConstraintDefinition constraintDefinition) {
        this.constraintDefinition = constraintDefinition;
    }

    public CompiledObjectClass getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(CompiledObjectClass objectClass) {
        this.objectClass = objectClass;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public boolean isOptional() {
        return optional;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CompiledType that = (CompiledType) o;

        return optional == that.optional &&
                Objects.equals(parent, that.parent) &&
                Objects.equals(type, that.type) &&
                Objects.equals(name, that.name) &&
                Objects.equals(constraintDefinition, that.constraintDefinition) &&
                Objects.equals(objectClass, that.objectClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, type, name, constraintDefinition, objectClass, optional);
    }

    @Override
    public String toString() {
        return ToString.getExcept(this, "parent");
    }

}
