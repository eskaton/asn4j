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

package ch.eskaton.asn4j.parser.ast.types;

import ch.eskaton.asn4j.parser.Position;
import ch.eskaton.commons.utils.StreamsUtils;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class CollectionOfType extends AbstractType implements HasModuleName {

    private String moduleName;

    protected Type type;

    protected CollectionOfType() {
    }

    public CollectionOfType(Position position, String moduleName, Type type) {
        super(position);

        this.moduleName = moduleName;
        this.type = type;
    }

    @Override
    public String getModuleName() {
        return moduleName;
    }

    public Type getType() {
        return type;
    }

    public boolean hasAnyConstraint() {
        return StreamsUtils.of(new ElementTypeIterator(this)).anyMatch(Type::hasConstraint);
    }

    public boolean hasElementConstraint() {
        return StreamsUtils.of(new ElementTypeIterator(getType())).anyMatch(Type::hasConstraint);
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

        CollectionOfType that = (CollectionOfType) o;

        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type);
    }

    public static class ElementTypeIterator implements Iterator<Type> {

        private Type type;

        public ElementTypeIterator(Type type) {
            this.type = type;
        }

        @Override
        public boolean hasNext() {
            return type != null;
        }

        @Override
        public Type next() {
            if (type == null) {
                throw new NoSuchElementException();
            }

            Type oldType = type;

            type = type instanceof CollectionOfType ? ((CollectionOfType) type).getType() : null;

            return oldType;
        }

    }

}
