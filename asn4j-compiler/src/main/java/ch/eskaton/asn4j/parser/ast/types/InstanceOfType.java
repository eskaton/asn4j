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
import ch.eskaton.asn4j.parser.ast.EncodingPrefixNode;
import ch.eskaton.asn4j.parser.ast.ObjectClassReference;
import ch.eskaton.asn4j.parser.ast.constraints.Constraint;
import ch.eskaton.asn4j.parser.ast.values.Tag;
import ch.eskaton.asn4j.runtime.TaggingMode;

import java.util.List;
import java.util.Objects;

public class InstanceOfType extends AbstractType {

    private ObjectClassReference objectClass;

    public InstanceOfType(Position position, ObjectClassReference objectClass) {
        super(position);

        this.objectClass = objectClass;
    }

    @Override
    public Tag getTag() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setTag(Tag tag) {
        // TODO Auto-generated method stub

    }

    @Override
    public TaggingMode getTaggingMode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setTaggingMode(TaggingMode mode) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setConstraints(List<Constraint> constraints) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<Constraint> getConstraints() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasConstraint() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setEncodingPrefix(EncodingPrefixNode encodingPrefix) {
        // TODO Auto-generated method stub

    }

    @Override
    public EncodingPrefixNode getEncodingPrefix() {
        // TODO Auto-generated method stub
        return null;
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

        InstanceOfType that = (InstanceOfType) o;

        return Objects.equals(objectClass, that.objectClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), objectClass);
    }

}
