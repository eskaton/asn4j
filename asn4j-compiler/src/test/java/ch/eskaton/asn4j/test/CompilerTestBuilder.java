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

package ch.eskaton.asn4j.test;

import ch.eskaton.asn4j.compiler.CompilerConfig;
import ch.eskaton.asn4j.compiler.CompilerImpl;
import ch.eskaton.asn4j.compiler.StringModuleSource;
import ch.eskaton.asn4j.compiler.constraints.ast.AbstractNode;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ParserException;
import ch.eskaton.commons.collections.Tuple2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CompilerTestBuilder {

    private CompilerTest compilerTest = new CompilerTest();

    public ModuleBuilder moduleBuilder() {
        return new ModuleBuilder(this);
    }

    public TypeTestBuilder typeTestBuilder() {
        return new TypeTestBuilder(this);
    }

    void addModule(String name, String body, boolean implicitTags) {
        var source = """
                %s DEFINITIONS %s TAGS ::=
                BEGIN
                    %s
                END
                """.formatted(name, implicitTags ? "IMPLICIT" : "EXPLICIT", body);

        compilerTest.addModuleSource(Tuple2.of(name, source));
    }

    private void addTest(Test test) {
        compilerTest.addTest(test);
    }

    public CompilerTestBuilder mainModule(String mainModule) {
        compilerTest.setMainModule(mainModule);

        return this;
    }

    public CompilerTest build() {
        return compilerTest;
    }

    public class CompilerTest {

        private HashSet<Tuple2<String, String>> moduleSources = new HashSet<>();

        private String mainModule;

        private Test test;

        public void addModuleSource(Tuple2<String, String> moduleSource) {
            moduleSources.add(moduleSource);

            if (mainModule == null) {
                mainModule = moduleSource.get_1();
            }
        }

        public void run() throws IOException, ParserException {
            var stringModuleSource = new StringModuleSource(moduleSources.toArray(new Tuple2[] {}));
            var config = new CompilerConfig().module(mainModule).generateSource(false);
            var compiler = new CompilerImpl(config, stringModuleSource);

            if (test.getModuleName() == null) {
                test.setModuleName(mainModule);
            }

            test.execute(compiler);
        }

        public void setMainModule(String mainModule) {
            this.mainModule = mainModule;
        }

        public void addTest(Test test) {
            this.test = test;
        }

    }

    public static class ModuleBuilder {

        private final CompilerTestBuilder compilerTestBuilder;

        private String moduleName;

        private String body;

        private boolean implicitTags = false;

        public ModuleBuilder(CompilerTestBuilder compilerTestBuilder) {
            this.compilerTestBuilder = compilerTestBuilder;
        }

        public ModuleBuilder name(String moduleName) {
            this.moduleName = moduleName;

            return this;
        }

        public ModuleBuilder body(String body) {
            this.body = body;

            return this;
        }

        public ModuleBuilder implicitTags(boolean implicitTags) {
            this.implicitTags = implicitTags;

            return this;
        }

        public CompilerTestBuilder build() {
            Objects.requireNonNull(moduleName);
            Objects.requireNonNull(body);

            compilerTestBuilder.addModule(moduleName, body, implicitTags);

            return compilerTestBuilder;
        }

    }

    public static class TypeTestBuilder {

        private final CompilerTestBuilder compilerTestBuilder;

        private String moduleName;

        private String typeName;

        private List<TypeVerifier> verifiers = new ArrayList<>();

        public TypeTestBuilder(CompilerTestBuilder compilerTestBuilder) {
            this.compilerTestBuilder = compilerTestBuilder;
        }

        public TypeTestBuilder moduleName(String moduleName) {
            this.moduleName = moduleName;

            return this;
        }

        public TypeTestBuilder typeName(String typeName) {
            this.typeName = typeName;

            return this;
        }

        public void addVerifier(TypeVerifier verifier) {
            verifiers.add(verifier);
        }

        public <T extends AbstractNode> ConstraintTestBuilder<T> constraintTestBuilder(Class<T> nodeClass) {
            return new ConstraintTestBuilder<>(this, nodeClass);
        }

        public CompilerTestBuilder build() {
            Objects.requireNonNull(typeName);

            var test = new TypeTest(moduleName, typeName, verifiers);

            compilerTestBuilder.addTest(test);

            return compilerTestBuilder;
        }
    }

    public static class ConstraintTestBuilder<T extends AbstractNode> {

        private final TypeTestBuilder typeTestBuilder;

        private Class<T> nodeClass;

        private Consumer<T> constraintConsumer;

        public ConstraintTestBuilder(TypeTestBuilder typeTestBuilder, Class<T> nodeClass) {
            this.typeTestBuilder = typeTestBuilder;
            this.nodeClass = nodeClass;
        }

        public ConstraintTestBuilder verify(Consumer<T> constraintConsumer) {
            this.constraintConsumer = constraintConsumer;

            return this;
        }

        public TypeTestBuilder build() {
            var verifier = new ConstraintVerifier<>(nodeClass, constraintConsumer);

            typeTestBuilder.addVerifier(verifier);

            return typeTestBuilder;
        }

    }

    private interface Test {

        String getModuleName();

        void setModuleName(String moduleName);

        void execute(CompilerImpl compiler) throws IOException, ParserException;

    }

    private abstract static class AbstractTest implements Test {

        private String moduleName;

        public AbstractTest(String moduleName) {
            this.moduleName = moduleName;
        }

        public String getModuleName() {
            return moduleName;
        }

        public void setModuleName(String moduleName) {
            this.moduleName = moduleName;
        }

    }

    static class TypeTest extends AbstractTest {

        private final String typeName;

        private final List<TypeVerifier> verifiers;

        public TypeTest(String moduleName, String typeName, List<TypeVerifier> verifiers) {
            super(moduleName);

            this.typeName = typeName;
            this.verifiers = verifiers;
        }

        public void execute(CompilerImpl compiler) throws IOException, ParserException {
            compiler.run();

            var context = compiler.getCompilerContext();
            var compiledType = context.getCompiledModule(getModuleName()).getTypes().get(typeName);

            assertNotNull(compiledType);

            verifiers.forEach(verifier -> verifier.execute(compiledType));
        }

    }

    interface TypeVerifier {

        void execute(CompiledType compiledType);

    }

    static class ConstraintVerifier<T extends AbstractNode> implements TypeVerifier {

        private Class<T> nodeClass;

        private final Consumer<T> verifier;

        public ConstraintVerifier(Class<T> nodeClass, Consumer<T> verifier) {
            this.nodeClass = nodeClass;
            this.verifier = verifier;
        }

        public void execute(CompiledType compiledType) {
            var maybeConstraintDefinition = compiledType.getConstraintDefinition();

            assertTrue(maybeConstraintDefinition.isPresent());

            var constraintDefinition = maybeConstraintDefinition.get();
            var roots = constraintDefinition.getRoots();

            assertTrue(nodeClass.isAssignableFrom(roots.getClass()));

            verifier.accept(nodeClass.cast(roots));
        }

    }

}
