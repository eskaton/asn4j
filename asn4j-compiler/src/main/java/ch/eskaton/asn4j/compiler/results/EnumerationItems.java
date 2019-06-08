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

import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.runtime.utils.ToString;
import ch.eskaton.commons.MutableInteger;
import ch.eskaton.commons.collections.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EnumerationItems {

    private List<Tuple2<String, Integer>> items = new ArrayList<>();

    public List<Tuple2<String, Integer>> getItems() {
        return items;
    }

    public String getName(int index) {
        return items.get(index).get_1();
    }

    public Integer getNumber(int index) {
        return items.get(index).get_2();
    }

    public void setNumber(int index, int value) {
        items.get(index).set_2(value);
    }

    public boolean contains(MutableInteger n) {
        return getItems().stream().anyMatch(item -> Objects.equals(item.get_2(), n.getValue()));
    }

    public void add(String name, Integer value) {
        items.forEach(item -> {
            if (name.equals(item.get_1())) {
                throw new CompilerException("Duplicate enumeration item '%s'", name);
            } else if (value != null && value.equals(item.get_2())) {
                throw new CompilerException("Duplicate enumeration value %s(%s)", name, value);
            }
        });

        items.add(Tuple2.of(name, value));
    }

    public EnumerationItems addAll(List<Tuple2<String, Integer>> otherItems) {
        items.addAll(otherItems);

        return this;
    }

    public EnumerationItems copy() {
        EnumerationItems enumerationItems = new EnumerationItems();

        enumerationItems.getItems().addAll(items);

        return enumerationItems;
    }

    @Override
    public String toString() {
        return ToString.get(this);
    }

}
