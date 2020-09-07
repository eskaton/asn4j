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

import ch.eskaton.asn4j.runtime.utils.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CompiledObjectClass implements CompilationResult {

    private String name;

    private List<AbstractCompiledField> fields = new ArrayList<>();

    private Optional<List<Object>> syntax = Optional.empty();

    public CompiledObjectClass(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addField(AbstractCompiledField compiledField) {
        fields.add(compiledField);
    }

    public Optional<AbstractCompiledField> getField(String reference) {
        return fields.stream().filter(field -> Objects.equals(field.getName(), reference)).findAny();
    }

    public List<AbstractCompiledField> getFields() {
        return Collections.unmodifiableList(fields);
    }

    public void setSyntax(List<Object> syntax) {
        this.syntax = Optional.ofNullable(syntax);
    }

    public Optional<List<Object>> getSyntax() {
        return syntax;
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

        CompiledObjectClass that = (CompiledObjectClass) o;

        return Objects.equals(name, that.name) &&
                Objects.equals(fields, that.fields) &&
                Objects.equals(syntax, that.syntax);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, fields, syntax);
    }

}
