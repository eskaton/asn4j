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
import ch.eskaton.commons.collections.Tuple2;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class CompiledEnumeratedType extends CompiledType {

    private EnumerationItems roots;

    private EnumerationItems additions;

    public CompiledEnumeratedType(Type type, String name, EnumerationItems roots, EnumerationItems additions) {
        super(type, name);

        this.roots = roots;
        this.additions = additions;
    }

    public EnumerationItems getRoots() {
        return roots;
    }

    public EnumerationItems getAdditions() {
        return additions;
    }

    public Optional<Tuple2<String, Integer>> getElementById(String id) {
        Optional<Tuple2<String, Integer>> element = findElementById(getRoots(), id);

        if (!element.isPresent()) {
            element = findElementById(getAdditions(), id);
        }

        return element;
    }

    private Optional<Tuple2<String, Integer>> findElementById(EnumerationItems additions, String id) {
        return additions.getItems().stream().filter(idMatcher(id)).findFirst();
    }

    private Predicate<Tuple2<String, Integer>> idMatcher(String id) {
        return t -> t.get_1().equals(id);
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

        CompiledEnumeratedType that = (CompiledEnumeratedType) obj;

        return Objects.equals(roots, that.roots) &&
                Objects.equals(additions, that.additions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), roots, additions);
    }

}
