/*
 *
 *  *  Copyright (c) 2015, Adrian Moser
 *  *  All rights reserved.
 *  *
 *  *  Redistribution and use in source and binary forms, with or without
 *  *  modification, are permitted provided that the following conditions are met:
 *  *  * Redistributions of source code must retain the above copyright
 *  *  notice, this list of conditions and the following disclaimer.
 *  *  * Redistributions in binary form must reproduce the above copyright
 *  *  notice, this list of conditions and the following disclaimer in the
 *  *  documentation and/or other materials provided with the distribution.
 *  *  * Neither the name of the author nor the
 *  *  names of its contributors may be used to endorse or promote products
 *  *  derived from this software without specific prior written permission.
 *  *
 *  *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  *  DISCLAIMED. IN NO EVENT SHALL AUTHOR BE LIABLE FOR ANY
 *  *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package ch.eskaton.asn4j.runtime;

import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.utils.ToString;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

public class TagId {

    private Clazz clazz;

    private int tag;

    public TagId(Clazz clazz, int tag) {
        this.clazz = clazz;
        this.tag = tag;
    }

    public static TagId fromTag(ASN1Tag tag) {
        return new TagId(tag.clazz(), tag.tag());
    }

    public static List<TagId> fromTags(List<ASN1Tag> tags) {
        return tags.stream().map(TagId::fromTag).collect(toList());
    }

    public Clazz getClazz() {
        return clazz;
    }

    public int getTag() {
        return tag;
    }

    public boolean equalsASN1Tag(ASN1Tag asn1Tag) {
        return asn1Tag.clazz().equals(clazz) && asn1Tag.tag() == tag;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, tag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TagId tagId = (TagId) o;

        return tag == tagId.tag && clazz == tagId.clazz;
    }

    @Override
    public String toString() {
        return ToString.get(this);
    }

}
