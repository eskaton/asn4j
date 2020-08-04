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

package ch.eskaton.asn4j.compiler;

import ch.eskaton.commons.utils.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Clone {

    public static Object clone(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof String) {
            return object;
        } else if (object instanceof Boolean) {
            return object;
        } else if (object instanceof Integer) {
            return object;
        } else if (object instanceof BigInteger) {
            return object;
        }

        var clazz = object.getClass();
        Object copy;

        try {
            var constructor = clazz.getDeclaredConstructor();

            constructor.setAccessible(true);

            copy = constructor.newInstance();

            if (object instanceof Collection collection) {
                var list = (List) collection.stream()
                        .map(Clone::clone)
                        .collect(Collectors.toList());

                ((Collection) copy).addAll(list);
            } else {
                var properties = ReflectionUtils.getPropertiesSource(object);

                for (var property : properties) {
                    var source = property.get_1().get_1();
                    var name = property.get_1().get_2();
                    var value = property.get_2();
                    var field = source.getDeclaredField(name);

                    field.setAccessible(true);
                    field.set(copy, clone(value));
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                NoSuchFieldException e) {
            throw new CloneException("Failed to clone object of type " + clazz.getSimpleName(), e);
        }

        return copy;
    }

    public static class CloneException extends RuntimeException {

        public CloneException(String message, Throwable cause) {
            super(message, cause);
        }

    }

}
