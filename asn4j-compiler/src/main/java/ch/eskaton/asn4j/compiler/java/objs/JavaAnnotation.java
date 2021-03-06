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

package ch.eskaton.asn4j.compiler.java.objs;

import ch.eskaton.commons.utils.StringUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class JavaAnnotation implements JavaObject {

    private Class<?> annotation;

    private Map<String, Object> params = new HashMap<>();

    public JavaAnnotation(Class<?> annotation) {
        this.annotation = annotation;
    }

    public JavaAnnotation addParameter(String name, Object value) {
        params.put(name, value);

        return this;
    }

    public void write(BufferedWriter writer, String prefix) throws IOException {
        writer.write(StringUtils.concat(prefix, "@", annotation.getSimpleName(), "("));

        var first = true;

        for (var e : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                writer.write(", ");
            }

            writer.write(e.getKey());
            writer.write(" = ");

            if (e.getValue() instanceof Collection collectionValue) {
                writer.write("{ ");

                var collectionFirst = true;

                for (var value : collectionValue) {
                    if (collectionFirst) {
                        collectionFirst = false;
                    } else {
                        writer.write(", ");
                    }

                    if (value instanceof JavaObject javaObject) {
                        javaObject.write(writer, "");
                    } else {
                        writer.write(value.toString());
                    }
                }

                writer.write(" }");
            } else if (e.getValue() instanceof JavaObject javaObject) {
                javaObject.write(writer, "");
            } else {
                writer.write(e.getValue().toString());
            }
        }

        writer.write(")\n");
    }

}
