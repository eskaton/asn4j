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

package ch.eskaton.asn4j.runtime.utils;

import ch.eskaton.commons.collections.Tuple2;
import ch.eskaton.commons.utils.ReflectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch.eskaton.commons.utils.CollectionUtils.asHashSet;

public class ToString {

    private static ThreadLocal<Stack<Object>> visited = new ThreadLocal<>();

    private ToString() {
    }

    public static String get(Object object) {
        try {
            String properties = buildPropertiesString(ReflectionUtils.getProperties(object).stream());

            return buildString(object, properties);
        } catch (IllegalAccessException e) {
            return object.getClass().getSimpleName();
        }
    }

    public static String get(Object object, String... fields) {
        Set<String> fieldSet = asHashSet(fields);

        try {
            String properties = buildPropertiesString(ReflectionUtils.getProperties(object).stream()
                    .filter(property -> fieldSet.contains(property.get_1())));

            return buildString(object, properties);
        } catch (IllegalAccessException e) {
            return object.getClass().getSimpleName();
        }
    }

    public static String getExcept(Object object, String... fields) {
        Set<String> fieldSet = asHashSet(fields);

        try {
            String properties = buildPropertiesString(ReflectionUtils.getProperties(object).stream()
                    .filter(property -> !fieldSet.contains(property.get_1())));

            return buildString(object, properties);
        } catch (IllegalAccessException e) {
            return object.getClass().getSimpleName();
        }
    }

    private static String buildString(Object object, String properties) {
        return object.getClass().getSimpleName() + '[' + properties + ']';
    }

    private static String buildPropertiesString(Stream<Tuple2<String, Object>> stream) {
        return stream.filter(property -> Objects.nonNull(property.get_2()))
                .map(tuple -> tuple.get_1() + "=" + getValue(tuple.get_2()))
                .collect(Collectors.joining(", "));
    }

    private static String getValue(Object object) {
        var stack = visited.get();

        if (stack == null) {
            stack = new Stack<>();

            visited.set(stack);
        }

        if (stack.contains(object)) {
            return "...";
        }

        stack.push(object);

        try {
            return object.toString();
        } finally {
            stack.pop();

            if (stack.isEmpty()) {
                visited.remove();
            }
        }
    }

    public static Builder builder(Object object) {
        return new Builder(object);
    }

    public static class Builder {

        private Object object;

        private List<Tuple2<String, Object>> properties = new LinkedList<>();

        public Builder(Object object) {
            this.object = object;
        }

        public Builder add(String name, Object value) {
            properties.add(Tuple2.of(name, value));

            return this;
        }

        public Builder addAll() {
            try {
                properties.addAll(ReflectionUtils.getProperties(object));
            } catch (IllegalAccessException ignore) {
                // empty
            }

            return this;
        }

        public Builder map(String name, Function<Object, String> mapper) {
            properties.stream()
                    .filter(property -> name.equals(property.get_1()))
                    .forEach(property -> property.set_2(mapper.apply(property.get_2())));

            return this;
        }

        public String build() {
            return buildString(object, buildPropertiesString(properties.stream()));
        }

    }

}
