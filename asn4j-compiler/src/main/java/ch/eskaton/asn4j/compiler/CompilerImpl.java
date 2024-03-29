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

import ch.eskaton.asn4j.compiler.java.JavaCompiler;
import ch.eskaton.asn4j.compiler.java.JavaWriter;
import ch.eskaton.asn4j.compiler.java.objs.JavaStructure;
import ch.eskaton.asn4j.compiler.results.CompilationResult;
import ch.eskaton.asn4j.compiler.results.CompiledObject;
import ch.eskaton.asn4j.compiler.results.CompiledObjectClass;
import ch.eskaton.asn4j.compiler.results.CompiledObjectSet;
import ch.eskaton.asn4j.compiler.results.CompiledParameterizedObject;
import ch.eskaton.asn4j.compiler.results.CompiledParameterizedObjectClass;
import ch.eskaton.asn4j.compiler.results.CompiledParameterizedObjectSet;
import ch.eskaton.asn4j.compiler.results.CompiledParameterizedType;
import ch.eskaton.asn4j.compiler.results.CompiledParameterizedValueSetType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.compiler.results.CompiledValue;
import ch.eskaton.asn4j.logging.Logger;
import ch.eskaton.asn4j.logging.LoggerFactory;
import ch.eskaton.asn4j.parser.Parser;
import ch.eskaton.asn4j.parser.ParserException;
import ch.eskaton.asn4j.parser.ast.AssignmentNode;
import ch.eskaton.asn4j.parser.ast.ObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ObjectClassAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ObjectSetAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedObjectClassAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedObjectSetAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedTypeAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedTypeOrObjectClassAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedValueAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedValueOrObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedValueSetTypeAssignmentNode;
import ch.eskaton.asn4j.parser.ast.TypeAssignmentNode;
import ch.eskaton.asn4j.parser.ast.TypeOrObjectClassAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ValueAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ValueOrObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ValueSetTypeAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ValueSetTypeOrObjectSetAssignmentNode;
import ch.eskaton.asn4j.parser.ast.constraints.SubtypeConstraint;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.commons.collections.Tuple2;
import ch.eskaton.commons.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompilerImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private CompilerConfig config;

    private ModuleSource moduleSource;

    private CompilerContext compilerContext;

    private Deque<Tuple2<String, Deque<AssignmentNode>>> assignments = new LinkedList<>();

    public static void main(String[] args) throws IOException, ParserException {
        if (args.length != 4) {
            System.err.println("Usage: ASN1Compiler <ASN1Module> <Include-Path> <Java-Package> <Output-Dir>");
            System.exit(1);
        }

        var config = new CompilerConfig().module(args[1]).pkg(args[2]).outputDir(args[3]);

        new CompilerImpl(config, new FileModuleSource(args[1])).run();
    }

    public CompilerImpl(CompilerConfig config, ModuleSource moduleSource) {
        this.config = config;
        this.moduleSource = moduleSource;

        compilerContext = new CompilerContext(this);
    }

    public void run() throws IOException, ParserException {
        long begin = System.currentTimeMillis();

        loadAndCompileModule(config.getModule());
        generateJavaClasses();

        LOGGER.info("Total compilation time %ss", String.format("%.3f",
                (System.currentTimeMillis() - begin) / 1000.0));
    }

    private void generateJavaClasses() {
        var modules = compilerContext.getCompiledModules();
        var structs = new HashMap<String, JavaStructure>();
        var javaCompiler = new JavaCompiler();


        if (config.isGenerateSource()) {
            for (var module : modules.values()) {
                structs.putAll(javaCompiler.compile(compilerContext, module.getTypes(), config.getPackage()));
            }

            var pkgDir = config.getPackage().replace('.', File.separatorChar);
            var pkgFile = new File(StringUtils.concat(config.getOutputDir(), File.separator, pkgDir));

            if (pkgFile.exists() || pkgFile.mkdirs()) {
                new JavaWriter().write(structs, config.getOutputDir());
            } else {
                throw new CompilerException("Failed to create directory " + pkgFile);
            }
        }
    }

    void loadAndCompileModule(String moduleName) throws IOException, ParserException {
        loadModules();

        var maybeMainAssignments = assignments.stream().filter(t -> Objects.equals(t.get_1(), moduleName)).findFirst();

        if (maybeMainAssignments.isPresent()) {
            var mainAssignments = maybeMainAssignments.get();

            assignments.remove(mainAssignments);
            assignments.push(mainAssignments);
        } else {
            throw new CompilerException("Module %s not found", moduleName);
        }

        compileModules();
    }

    void loadModules() throws IOException, ParserException {
        var moduleStreams = moduleSource.getModules();

        for (var moduleStream : moduleStreams) {
            parseModule(moduleStream);
        }
    }

    void parseModule(Tuple2<String, InputStream> moduleStream) throws IOException, ParserException {
        var parser = new Parser(moduleStream.get_2());

        try {
            var moduleNode = parser.parse();
            var moduleName = moduleNode.getModuleId().getModuleName();

            compilerContext.addModule(moduleName, moduleNode);

            var assignmentNodes = moduleNode.getBody().getAssignments();
            var assignmentsList = assignmentNodes.stream().collect(Collectors.toCollection(LinkedList::new));

            assignments.push(Tuple2.of(moduleName, assignmentsList));

            LOGGER.info("Loaded module %s", moduleName);
        } catch (ParserException e) {
            throw new ParserException("Failed to load module from source %s", e, moduleStream.get_1());
        }
    }

    private void compileModules() {
        for (var moduleAssignments : assignments) {
            var moduleName = moduleAssignments.get_1();

            LOGGER.info("Compiling module %s...", moduleName);

            compilerContext.executeWithModule(moduleName, () -> {
                compileAssignments(moduleAssignments.get_2());

                return null;
            });
        }
    }

    private void compileAssignments(Deque<AssignmentNode> moduleAssignments) {
        var assignmentNodes = new LinkedList<>(moduleAssignments);

        while (true) {
            if (assignmentNodes.isEmpty()) {
                break;
            }

            var assignmentNode = assignmentNodes.pop();

            // check whether assignment has already been compiled on demand
            if (!moduleAssignments.contains(assignmentNode)) {
                moduleAssignments.remove(assignmentNode);

                continue;
            }

            var result = compileAssignment(assignmentNode);

            if (result != null) {
                moduleAssignments.remove(assignmentNode);
            }
        }
    }

    private CompilationResult compileAssignment(AssignmentNode unknownAssignment) {
        if (unknownAssignment instanceof TypeOrObjectClassAssignmentNode assignment) {
            return compileTypeOrObjectClassAssignment(assignment);
        } else if (unknownAssignment instanceof ValueOrObjectAssignmentNode assignment) {
            return compileValueOrObjectAssignmentNode(assignment);
        } else if (unknownAssignment instanceof ObjectSetAssignmentNode assignment) {
            return compileObjectSetAssignment(assignment);
        } else if (unknownAssignment instanceof ValueSetTypeOrObjectSetAssignmentNode assignment) {
            return compileValueSetTypeOrObjectSetAssignment(assignment);
        } else if (unknownAssignment instanceof ParameterizedTypeOrObjectClassAssignmentNode assignment) {
            return compileParameterizedTypeOrObjectClassAssignment(assignment);
        } else if (unknownAssignment instanceof ParameterizedValueAssignmentNode) {
            // ignore values, they are resolved when needed
            return null;
        } else if (unknownAssignment instanceof ParameterizedObjectAssignmentNode) {
            // ignore objects, they are resolved when needed
            return null;
        } else if (unknownAssignment instanceof ParameterizedValueOrObjectAssignmentNode) {
            // ignore values and objects, they are resolved when needed
            return null;
        } else if (unknownAssignment instanceof ParameterizedValueSetTypeAssignmentNode assignment) {
            return compileParameterizedValueSetTypeAssignment(assignment);
        } else if (unknownAssignment instanceof ParameterizedObjectClassAssignmentNode assignment) {
            return compileParameterizedObjectClassAssignment(assignment);
        } else if (unknownAssignment instanceof ParameterizedObjectSetAssignmentNode assignment) {
            return compileParameterizedObjectSetAssignment(assignment);
        }

        throw new IllegalCompilerStateException(unknownAssignment.getPosition(), "Unsupported assignment: %s",
                unknownAssignment.getClass().getSimpleName());
    }

    private CompilationResult compileTypeOrObjectClassAssignment(TypeOrObjectClassAssignmentNode assignment) {
        if (assignment.getObjectClassAssignment().isPresent()) {
            try {
                return compileObjectClassAssignment(assignment.getObjectClassAssignment().get());
            } catch (ResolutionException e) {
                // ignore
            }
        }

        if (assignment.getTypeAssignment().isPresent()) {
            return compileTypeAssignment(assignment.getTypeAssignment().get());
        }

        throw new IllegalCompilerStateException(assignment.getPosition(), "Unhandled assignment: %s", assignment);
    }

    private CompilationResult compileValueOrObjectAssignmentNode(ValueOrObjectAssignmentNode assignment) {
        if (assignment.getObjectAssignment().isPresent()) {
            return compileObjectAssignment(assignment.getObjectAssignment().get());
        }

        if (assignment.getValueAssignment().isPresent()) {
            return compileValueAssignment(assignment.getValueAssignment().get());
        }

        throw new IllegalCompilerStateException(assignment.getPosition(), "Unhandled assignment: %s", assignment);
    }

    private CompiledType compileTypeAssignment(TypeAssignmentNode assignment) {
        return compilerContext.<TypeAssignmentNode, TypeAssignmentCompiler>getCompiler(TypeAssignmentNode.class)
                .compile(compilerContext, assignment);
    }

    private CompilationResult compileValueSetTypeOrObjectSetAssignment(ValueSetTypeOrObjectSetAssignmentNode assignment) {
        if (assignment.getValueSetTypeAssignment().isPresent()) {
            try {
                return compileValueSetTypeAssignment(assignment.getValueSetTypeAssignment().get());
            } catch (ResolutionException e) {
                // ignore
            }
        }

        if (assignment.getObjectSetAssignment().isPresent()) {
            return compileObjectSetAssignment(assignment.getObjectSetAssignment().get());
        }

        throw new IllegalCompilerStateException(assignment.getPosition(), "Unhandled assignment: %s", assignment);
    }

    private CompiledType compileValueSetTypeAssignment(ValueSetTypeAssignmentNode assignment) {
        var type = assignment.getType();

        // check whether node is a valid type
        compilerContext.getCompiledType(type);

        var valueSet = assignment.getValueSet();

        type.setConstraints(List.of(new SubtypeConstraint(valueSet.getPosition(), valueSet)));

        var typeAssignment = new TypeAssignmentNode(assignment.getPosition(), assignment.getReference(), type);

        return compileTypeAssignment(typeAssignment);
    }

    private CompiledValue<Value> compileValueAssignment(ValueAssignmentNode assignment) {
        return compilerContext.<ValueAssignmentNode, ValueAssignmentCompiler>
                getCompiler(ValueAssignmentNode.class).compile(compilerContext, assignment);
    }

    private CompiledObjectClass compileObjectClassAssignment(ObjectClassAssignmentNode assignment) {
        return compilerContext.<ObjectClassAssignmentNode, ObjectClassAssignmentCompiler>
                getCompiler(ObjectClassAssignmentNode.class).compile(compilerContext, assignment);
    }

    private CompiledObject compileObjectAssignment(ObjectAssignmentNode assignment) {
        return compilerContext.<ObjectAssignmentNode, ObjectAssignmentCompiler>
                getCompiler(ObjectAssignmentNode.class).compile(compilerContext, assignment);
    }

    private CompiledObjectSet compileObjectSetAssignment(ObjectSetAssignmentNode assignment) {
        return compilerContext.<ObjectSetAssignmentNode, ObjectSetAssignmentCompiler>
                getCompiler(ObjectSetAssignmentNode.class).compile(assignment);
    }

    private CompilationResult compileParameterizedTypeOrObjectClassAssignment(
            ParameterizedTypeOrObjectClassAssignmentNode assignment) {
        if (assignment.getParameterizedObjectClassAssignment().isPresent()) {
            try {
                var parameterizedObjectClassAssignment = assignment.getParameterizedObjectClassAssignment().get();

                return compileParameterizedObjectClassAssignment(parameterizedObjectClassAssignment);
            } catch (ResolutionException e) {
                // ignore
            }
        }

        if (assignment.getParameterizedTypeAssignment().isPresent()) {
            return compileParameterizedTypeAssignment(assignment.getParameterizedTypeAssignment().get());
        }

        throw new IllegalCompilerStateException(assignment.getPosition(), "Unhandled assignment: %s", assignment);
    }

    private CompiledParameterizedType compileParameterizedTypeAssignment(ParameterizedTypeAssignmentNode assignment) {
        return compilerContext.<ParameterizedTypeAssignmentNode, ParameterizedTypeAssignmentCompiler>
                getCompiler(ParameterizedTypeAssignmentNode.class).compile(compilerContext, assignment);
    }

    private CompiledParameterizedObject compileParameterizedObjectAssignment(
            ParameterizedObjectAssignmentNode assignment) {
        return compilerContext.<ParameterizedObjectAssignmentNode, ParameterizedObjectAssignmentCompiler>
                getCompiler(ParameterizedObjectAssignmentNode.class).compile(compilerContext, assignment);
    }

    private CompiledParameterizedObjectSet compileParameterizedObjectSetAssignment(
            ParameterizedObjectSetAssignmentNode assignment) {
        return compilerContext.<ParameterizedObjectSetAssignmentNode, ParameterizedObjectSetAssignmentCompiler>
                getCompiler(ParameterizedObjectSetAssignmentNode.class).compile(compilerContext, assignment);
    }

    private CompiledParameterizedObjectClass compileParameterizedObjectClassAssignment(
            ParameterizedObjectClassAssignmentNode assignment) {
        return compilerContext.<ParameterizedObjectClassAssignmentNode, ParameterizedObjectClassAssignmentCompiler>
                getCompiler(ParameterizedObjectClassAssignmentNode.class).compile(compilerContext, assignment);
    }

    private CompiledParameterizedValueSetType compileParameterizedValueSetTypeAssignment(
            ParameterizedValueSetTypeAssignmentNode assignment) {
        return compilerContext.<ParameterizedValueSetTypeAssignmentNode, ParameterizedValueSetTypeAssignmentCompiler>
                getCompiler(ParameterizedValueSetTypeAssignmentNode.class).compile(compilerContext, assignment);
    }

    private <T extends CompilationResult, A extends AssignmentNode> Optional<T> compile(String name,
            Optional<String> maybeModuleName,
            BiFunction<String, Collection<AssignmentNode>, Optional<A>> assignmentSelector, Function<A, T> compiler) {
        return maybeModuleName.map(moduleName -> compilerContext.executeWithModule(moduleName, () -> {
            var moduleAssignments = getAssignments(moduleName);

            return compile(name, assignmentSelector, compiler, moduleAssignments);
        })).or(() -> {
            var moduleName = compilerContext.getModule().getModuleId().getModuleName();
            var moduleAssignments = getAssignments(moduleName);

            return Optional.ofNullable(compile(name, assignmentSelector, compiler, moduleAssignments));
        }).flatMap(Function.identity());
    }

    private <T extends CompilationResult, A extends AssignmentNode> Optional<T> compile(String name,
            BiFunction<String, Collection<AssignmentNode>, Optional<A>> assignmentSelector, Function<A, T> compiler,
            Deque<AssignmentNode> assignments) {
        return assignmentSelector.apply(name, assignments).map(compiler::apply);
    }

    private Deque<AssignmentNode> getAssignments(String moduleName) {
        return assignments.stream()
                .filter(t -> Objects.equals(t.get_1(), moduleName))
                .findFirst()
                .map(Tuple2::get_2)
                .orElseThrow(() -> new IllegalCompilerStateException("Module %s unknown", moduleName));
    }

    private <A extends AssignmentNode, I extends AssignmentNode> BiFunction<String, Collection<AssignmentNode>, Optional<A>> getAssignmentSelector(
            Class<I> intermediateClass, Function<I, Optional<A>> assignmentMapper) {
        return (name, assignmentNodes) -> {
            var maybeIntermediateClass = assignmentNodes.stream()
                    .filter(obj -> Objects.equals(name, obj.getReference()))
                    .filter(intermediateClass::isInstance)
                    .map(intermediateClass::cast)
                    .findFirst();

            var maybeAssignment = maybeIntermediateClass.map(assignmentMapper)
                    .flatMap(Function.identity());

            if (maybeAssignment.isPresent()) {
                assignmentNodes.remove(maybeIntermediateClass.get());
            }

            return maybeAssignment;
        };
    }

    private <A extends AssignmentNode> BiFunction<String, Collection<AssignmentNode>, Optional<A>> getAssignmentSelector(
            Class<A> assignmentClass) {
        return getAssignmentSelector(assignmentClass, Optional::ofNullable);
    }

    public Optional<CompiledType> compileType(String name, Optional<String> maybeModuleName) {
        var assignmentSelector = getAssignmentSelector(TypeOrObjectClassAssignmentNode.class,
                TypeOrObjectClassAssignmentNode::getTypeAssignment);

        return compile(name, maybeModuleName, assignmentSelector, this::compileTypeAssignment);
    }

    public Optional<CompiledValue<? extends Value>> compileValue(String name, Optional<String> maybeModuleName) {
        var assignmentSelector = getAssignmentSelector(ValueOrObjectAssignmentNode.class,
                ValueOrObjectAssignmentNode::getValueAssignment);

        return compile(name, maybeModuleName, assignmentSelector, this::compileValueAssignment);
    }

    public Optional<CompiledType> compileValueSetType(String name, Optional<String> maybeModuleName) {
        var assignmentSelector = getAssignmentSelector(ValueSetTypeOrObjectSetAssignmentNode.class,
                ValueSetTypeOrObjectSetAssignmentNode::getValueSetTypeAssignment);

        return compile(name, maybeModuleName, assignmentSelector, this::compileValueSetTypeAssignment);
    }

    public Optional<CompiledObjectClass> compileObjectClass(String name, Optional<String> maybeModuleName) {
        var assignmentSelector = getAssignmentSelector(TypeOrObjectClassAssignmentNode.class,
                TypeOrObjectClassAssignmentNode::getObjectClassAssignment);

        return compile(name, maybeModuleName, assignmentSelector, this::compileObjectClassAssignment);
    }

    public Optional<CompiledObject> compileObject(String name, Optional<String> maybeModuleName) {
        var assignmentSelector = getAssignmentSelector(ValueOrObjectAssignmentNode.class,
                ValueOrObjectAssignmentNode::getObjectAssignment);

        return compile(name, maybeModuleName, assignmentSelector, this::compileObjectAssignment);
    }

    public Optional<CompiledObjectSet> compileObjectSet(String name, Optional<String> maybeModuleName) {
        var assignmentSelector = getAssignmentSelector(ValueSetTypeOrObjectSetAssignmentNode.class,
                ValueSetTypeOrObjectSetAssignmentNode::getObjectSetAssignment);

        return compile(name, maybeModuleName, assignmentSelector, this::compileObjectSetAssignment);
    }

    public Optional<CompiledParameterizedType> compileParameterizedType(String name, Optional<String> maybeModuleName) {
        var assignmentSelector = getAssignmentSelector(ParameterizedTypeOrObjectClassAssignmentNode.class,
                ParameterizedTypeOrObjectClassAssignmentNode::getParameterizedTypeAssignment);

        return compile(name, maybeModuleName, assignmentSelector, this::compileParameterizedTypeAssignment);
    }

    public Optional<CompiledParameterizedObjectClass> compiledParameterizedObjectClass(String name,
            Optional<String> maybeModuleName) {
        var assignmentSelector = getAssignmentSelector(ParameterizedTypeOrObjectClassAssignmentNode.class,
                ParameterizedTypeOrObjectClassAssignmentNode::getParameterizedObjectClassAssignment);

        return compile(name, maybeModuleName, assignmentSelector, this::compileParameterizedObjectClassAssignment);
    }

    public Optional<CompiledParameterizedObject> compiledParameterizedObject(String name,
            Optional<String> maybeModuleName) {
        var assignmentSelector = getAssignmentSelector(ParameterizedValueOrObjectAssignmentNode.class,
                ParameterizedValueOrObjectAssignmentNode::getParameterizedObjectAssignmentNode);

        return compile(name, maybeModuleName, assignmentSelector, this::compileParameterizedObjectAssignment);
    }

    public Optional<CompiledParameterizedObjectSet> compiledParameterizedObjectSet(String name,
            Optional<String> maybeModuleName) {
        var assignmentSelector = getAssignmentSelector(ParameterizedObjectSetAssignmentNode.class);

        return compile(name, maybeModuleName, assignmentSelector, this::compileParameterizedObjectSetAssignment);
    }

    public Optional<CompiledParameterizedValueSetType> compiledParameterizedValueSetType(String name,
            Optional<String> maybeModuleName) {
        var assignmentSelector = getAssignmentSelector(ParameterizedValueSetTypeAssignmentNode.class);

        return compile(name, maybeModuleName, assignmentSelector, this::compileParameterizedValueSetTypeAssignment);
    }

    public CompilerContext getCompilerContext() {
        return compilerContext;
    }

}
