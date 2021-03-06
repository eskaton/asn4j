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
import ch.eskaton.asn4j.parser.ast.HasPosition;
import ch.eskaton.asn4j.parser.ast.constraints.Constraint;
import ch.eskaton.asn4j.parser.ast.values.Tag;
import ch.eskaton.asn4j.runtime.TaggingMode;
import ch.eskaton.asn4j.runtime.utils.ToString;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractType implements Type, HasPosition {

    private Position position;

    private LinkedList<Tag> tags = new LinkedList<>();

    private LinkedList<Optional<TaggingMode>> taggingModes = new LinkedList<>();

    private List<Constraint> constraints;

    private EncodingPrefixNode encodingPrefix;

    protected AbstractType(Position position) {
        this.position = position;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public LinkedList<Tag> getTags() {
        return tags;
    }

    @Override
    public void setTags(LinkedList<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public void addTag(Tag tag) {
        this.tags.addFirst(tag);
    }

    @Override
    public LinkedList<Optional<TaggingMode>> getTaggingModes() {
        return taggingModes;
    }

    @Override
    public void setTaggingModes(LinkedList<Optional<TaggingMode>> taggingModes) {
        this.taggingModes = taggingModes;
    }

    @Override
    public void addTaggingMode(Optional<TaggingMode> taggingMode) {
        this.taggingModes.addFirst(taggingMode);
    }

    @Override
    public void setConstraints(List<Constraint> constraint) {
        this.constraints = constraint;
    }

    @Override
    public List<Constraint> getConstraints() {
        return constraints;
    }

    @Override
    public boolean hasConstraint() {
        return constraints != null && !constraints.isEmpty();
    }

    @Override
    public void setEncodingPrefix(EncodingPrefixNode encodingPrefix) {
        this.encodingPrefix = encodingPrefix;
    }

    @Override
    public EncodingPrefixNode getEncodingPrefix() {
        return encodingPrefix;
    }

    protected AbstractType() {
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

        AbstractType that = (AbstractType) o;

        return Objects.equals(position, that.position) &&
                Objects.equals(tags, that.tags) &&
                taggingModes == that.taggingModes &&
                Objects.equals(constraints, that.constraints) &&
                Objects.equals(encodingPrefix, that.encodingPrefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, tags, taggingModes, constraints, encodingPrefix);
    }

}
