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
import ch.eskaton.asn4j.compiler.il.Module;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.runtime.TagId;
import ch.eskaton.asn4j.runtime.utils.ToString;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CompiledType implements CompilationResult {

    private CompiledType parent;

    private String moduleName;

    private Type type;

    private String name;

    private Optional<List<TagId>> tags = Optional.empty();

    private Optional<ConstraintDefinition> constraintDefinition = Optional.empty();

    private Optional<Module> module = Optional.empty();

    private boolean subtype;

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

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Optional<List<TagId>> getTags() {
        return tags;
    }

    public void setTags(List<TagId> tags) {
        this.tags = Optional.ofNullable(tags);
    }

    public Optional<ConstraintDefinition> getConstraintDefinition() {
        return constraintDefinition;
    }

    public void setConstraintDefinition(ConstraintDefinition constraintDefinition) {
        this.constraintDefinition = Optional.ofNullable(constraintDefinition);
    }

    public Optional<Module> getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = Optional.ofNullable(module);
    }

    public boolean isSubtype() {
        return subtype;
    }

    public void setSubtype(boolean subtype) {
        this.subtype = subtype;
    }

    public CompiledType copy() {
        var compiledType = new CompiledType(type, name);

        copyAttributes(compiledType);

        return compiledType;
    }

    protected void copyAttributes(CompiledType compiledType) {
        compiledType.constraintDefinition = constraintDefinition;
        compiledType.tags = tags;
        compiledType.module = module;
        compiledType.moduleName = moduleName;
        compiledType.parent = parent;
        compiledType.subtype = subtype;
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

        return Objects.equals(type, that.type) &&
                Objects.equals(name, that.name) &&
                Objects.equals(tags, that.tags) &&
                Objects.equals(constraintDefinition, that.constraintDefinition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, tags, constraintDefinition);
    }

    @Override
    public String toString() {
        return ToString.getExcept(this, "parent");
    }
}
