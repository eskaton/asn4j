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

package ch.eskaton.asn4j.parser.ast;

import ch.eskaton.asn4j.parser.Position;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.utils.ToString;

import java.util.Objects;
import java.util.Optional;

public class ActualParameter extends AbstractNode {

    private Optional<Type> type = Optional.empty();

    private Optional<Value> value = Optional.empty();

    private Optional<ObjectNode> object = Optional.empty();

    private Optional<ElementSetSpecsNode> elementSetSpecs = Optional.empty();

    private Optional<ObjectSetSpecNode> objectSetSpec = Optional.empty();

    private Optional<ObjectClassReference> objectClassReference = Optional.empty();

    public ActualParameter(Position position) {
        super(position);
    }

    public Optional<Type> getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = Optional.ofNullable(type);
    }

    public void setValue(Value value) {
        this.value = Optional.ofNullable(value);
    }

    public Optional<Value> getValue() {
        return value;
    }

    public void setObject(ObjectNode object) {
        this.object = Optional.ofNullable(object);
    }

    public Optional<ObjectNode> getObject() {
        return object;
    }

    public void setElementSetSpecs(ElementSetSpecsNode elementSetSpecs) {
        this.elementSetSpecs = Optional.ofNullable(elementSetSpecs);
    }

    public Optional<ElementSetSpecsNode> getElementSetSpecs() {
        return elementSetSpecs;
    }

    public void setObjectSetSpec(ObjectSetSpecNode objectSetSpec) {
        this.objectSetSpec = Optional.ofNullable(objectSetSpec);
    }

    public Optional<ObjectSetSpecNode> getObjectSetSpec() {
        return objectSetSpec;
    }

    public Optional<ObjectClassReference> getObjectClassReference() {
        return objectClassReference;
    }

    public void setObjectClassReference(ObjectClassReference objectClassReference) {
        this.objectClassReference = Optional.ofNullable(objectClassReference);
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

        ActualParameter that = (ActualParameter) o;

        return Objects.equals(type, that.type) &&
                Objects.equals(value, that.value) &&
                Objects.equals(object, that.object) &&
                Objects.equals(elementSetSpecs, that.elementSetSpecs) &&
                Objects.equals(objectSetSpec, that.objectSetSpec) &&
                Objects.equals(objectClassReference, that.objectClassReference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value, object, elementSetSpecs, objectSetSpec, objectClassReference);
    }

}
