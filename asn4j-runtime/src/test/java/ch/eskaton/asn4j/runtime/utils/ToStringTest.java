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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ToStringTest {

    @Test
    public void testAllFields() {
        assertEquals("A[a=a]", new A().toString());
    }

    @Test
    public void testAllFieldsExplict() {
        assertEquals("K[a=a, b=b]", new K().toString());
    }

    @Test
    public void testExplicitFields() {
        assertEquals("B[b=b]", new B().toString());
    }

    @Test
    public void testFieldExclusion() {
        assertEquals("C[a=a]", new C().toString());
    }

    @Test
    public void testNested() {
        assertEquals("D[a=A[a=a]]", new D().toString());
    }

    @Test
    public void testRecursion() {
        E e = new E();
        F f = new F(e);

        e.setF(f);

        assertEquals("E[b=b, f=F[e=E[b=b, f=...]]]", e.toString());
    }

    @Test
    public void testRenameProperty() {
        assertEquals("G[string=a]", new G().toString());
    }

    @Test
    void testMapValue() {
        assertEquals("H[a=A, b=B]", new H().toString());
    }

    @Test
    public void testMapRecursion() {
        I i = new I();
        J j = new J(i);

        i.setJ(j);

        assertEquals("I[b=b, j=J[i=-I[b=b, j=...]-]]", i.toString());
    }

    private static class A {

        private String a = "a";

        @Override
        public String toString() {
            return ToString.get(this);
        }
    }

    private static class B {

        private String a = "a";

        private String b = "b";

        @Override
        public String toString() {
            return ToString.get(this, "b");
        }
    }

    private static class C {

        private String a = "a";

        private String b = "b";

        @Override
        public String toString() {
            return ToString.getExcept(this, "b");
        }
    }

    private static class D {

        private A a = new A();

        private String b = "b";

        @Override
        public String toString() {
            return ToString.getExcept(this, "b");
        }
    }

    private static class E {

        private String b = "b";

        private F f;

        public void setF(F f) {
            this.f = f;
        }

        @Override
        public String toString() {
            return ToString.get(this);
        }
    }

    private static class F {

        private E e;

        public F(E e) {
            this.e = e;
        }

        @Override
        public String toString() {
            return ToString.get(this);
        }
    }

    private static class G {

        private String a = "a";

        @Override
        public String toString() {
            return ToString.builder(this).add("string", a).build();
        }
    }

    private static class H {

        private String a = "a";

        private String b = "b";

        @Override
        public String toString() {
            return ToString.builder(this)
                    .add("a", a)
                    .add("b", b)
                    .map("a", v -> v.toString().toUpperCase())
                    .map("b", v -> v.toString().toUpperCase())
                    .build();
        }
    }

    private static class I {

        private String b = "b";

        private J j;

        public void setJ(J j) {
            this.j = j;
        }

        @Override
        public String toString() {
            return ToString.get(this);
        }
    }

    private static class J {

        private I i;

        public J(I i) {
            this.i = i;
        }

        @Override
        public String toString() {
            return ToString.builder(this)
                    .add("i", i)
                    .map("i", v -> "-" + v.toString() + "-")
                    .build();
        }
    }

    private static class K {

        private String a = "a";

        private String b = "b";

        @Override
        public String toString() {
            return ToString.builder(this).addAll().build();
        }
    }

}
