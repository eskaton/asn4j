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

package ch.eskaton.asn4j.compiler.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class JavaUtils {

    public static MethodBuilder method() {
        return new MethodBuilder();
    }

    public enum Modifier {
        PackagePrivate(""), Private("private"), Protected("protected"), Public("public");

        private final String modifier;

        Modifier(String modifier) {
            this.modifier = modifier;
        }

        @Override
        public String toString() {
            return modifier;
        }
    }

    public static class MethodBuilder {

        private List<String> annotations = new ArrayList<>();

        private Modifier modifier = Modifier.Public;

        private String returnType = "void";

        private String name;

        private List<String> body;

        public MethodBuilder annotation(String annotation) {
            annotations.add(annotation);
            return this;
        }

        public MethodBuilder modifier(Modifier modifier) {
            this.modifier = modifier;
            return this;
        }

        public MethodBuilder returnType(String returnType) {
            this.returnType = returnType;
            return this;
        }

        public MethodBuilder name(String name) {
            this.name = name;
            return this;
        }

        public MethodBuilder body(String body) {
            return body(Arrays.asList(body.split("\n")));
        }

        public MethodBuilder body(List<String> body) {
            this.body = body.stream().map(b -> "\t\t" + b + "\n").collect(toList());
            return this;
        }

        public MethodBuilder appendBody(String body) {
            if (this.body == null) {
                this.body = new ArrayList<>();
            }

            this.body.add("\t\t" + body + "\n");

            return this;
        }

        public JavaLiteralMethod build() {
            return new JavaLiteralMethod(toString());
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append(annotations.stream().map(a -> "\t" + a + "\n").collect(joining()));

            sb.append("\t");
            sb.append(modifier);
            sb.append(" ");
            sb.append(returnType);
            sb.append(" ");
            sb.append(name);
            sb.append("() {\n");

            if(body != null) {
                sb.append(body.stream().collect(joining()));
            }
            
            sb.append("\t}");

            return sb.toString();
        }
    }

}
