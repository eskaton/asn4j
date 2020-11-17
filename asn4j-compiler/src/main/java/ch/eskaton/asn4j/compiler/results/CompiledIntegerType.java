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

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class CompiledIntegerType extends CompiledType {

    private Optional<Map<String, Long>> namedNumbers;

    CompiledIntegerType(Type type, String name) {
        super(type, name);
    }

    public void setNamedNumbers(Map<String, Long> namedNumbers) {
        this.namedNumbers = namedNumbers == null || namedNumbers.isEmpty() ?
                Optional.empty() :
                Optional.of(namedNumbers);
    }

    public Optional<Map<String, Long>> getNamedNumbers() {
        return namedNumbers;
    }

    @Override
    public CompiledIntegerType copy() {
        var compiledType = new CompiledIntegerType(getType(), getName());

        copyAttributes(compiledType);

        return compiledType;
    }

    protected void copyAttributes(CompiledIntegerType compiledType) {
        super.copyAttributes(compiledType);

        compiledType.namedNumbers = namedNumbers;
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

        if (!super.equals(o)) {
            return false;
        }

        CompiledIntegerType that = (CompiledIntegerType) o;

        return Objects.equals(namedNumbers, that.namedNumbers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), namedNumbers);
    }

}
