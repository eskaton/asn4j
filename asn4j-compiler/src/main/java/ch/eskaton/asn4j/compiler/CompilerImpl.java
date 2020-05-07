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

import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.Parser;
import ch.eskaton.asn4j.parser.ParserException;
import ch.eskaton.asn4j.parser.ast.AssignmentNode;
import ch.eskaton.asn4j.parser.ast.ImportNode;
import ch.eskaton.asn4j.parser.ast.ModuleBodyNode;
import ch.eskaton.asn4j.parser.ast.ModuleNode;
import ch.eskaton.asn4j.parser.ast.ModuleRefNode;
import ch.eskaton.asn4j.parser.ast.ObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ObjectClassAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ObjectSetAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedAssignmentNode;
import ch.eskaton.asn4j.parser.ast.TypeAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ValueAssignmentNode;
import ch.eskaton.commons.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class CompilerImpl {

    public static final String ASN_1_EXTENSION = ".asn1";

    public static final String ASN_EXTENSION = ".asn";

    private String module;

    private String[] includePath;

    private CompilerContext compilerContext;

    private Deque<Deque<AssignmentNode>> assignments = new LinkedList<>();

    public static void main(String[] args) throws IOException, ParserException {
        if (args.length != 4) {
            System.err.println("Usage: ASN1Compiler <ASN1Module> <Include-Path> <Java-Package> <Output-Dir>");
            System.exit(1);
        }

        new CompilerImpl(args[0], args[1], args[2], args[3]).run();
    }

    public CompilerImpl(String module, String path, String pkg, String outputDir) {
        this.module = module;
        this.includePath = path.split(File.pathSeparator);

        compilerContext = new CompilerContext(this, pkg, outputDir);
    }

    public void run() throws IOException, ParserException {
        long begin = System.currentTimeMillis();

        module = stripExtension(module);

        loadAndCompileModule(module);

        compilerContext.writeClasses();

        System.out.println("Total compilation time " + String.format("%.3f",
                (System.currentTimeMillis() - begin) / 1000.0) + "s");
    }

    void loadAndCompileModule(String moduleName) throws IOException, ParserException {
        load(moduleName);

        try {
            compilerContext.pushModule(compilerContext.getModule(moduleName));
            compileModule(moduleName);
        } finally {
            compilerContext.popModule();
        }
    }

    private void compileModule(String moduleName) throws IOException, ParserException {
        System.out.println("Compiling module " + moduleName + "...");

        ModuleNode module = compilerContext.getModule(moduleName);

        compileImports(module);
        compileBody(module);
    }

    private void compileBody(ModuleNode module) {
        ModuleBodyNode moduleBody = module.getBody();

        if (moduleBody != null) {
            assignments.push(new LinkedList<>(moduleBody.getAssignments()));

            while (!assignments.peek().isEmpty()) {
                compileAssignment(assignments.peek().pop());
            }

            assignments.pop();
        }
    }

    private void compileAssignment(AssignmentNode assignment) {
        if (assignment instanceof TypeAssignmentNode) {
            compileTypeAssignment((TypeAssignmentNode) assignment);
        } else if (assignment instanceof ValueAssignmentNode) {
            // ignore values, they are resolved when needed
        } else if (assignment instanceof ParameterizedAssignmentNode) {
            compileParameterizedAssignment((ParameterizedAssignmentNode) assignment);
        } else if (assignment instanceof ObjectSetAssignmentNode) {
            compileObjectSetAssignment((ObjectSetAssignmentNode) assignment);
        } else if (assignment instanceof ObjectAssignmentNode) {
            // ignore objects, they are resolved when needed
        } else if (assignment instanceof ObjectClassAssignmentNode) {
            compileObjectClassAssignment((ObjectClassAssignmentNode) assignment);
        }
    }

    private CompiledType compileTypeAssignment(TypeAssignmentNode assignment) {
        return compilerContext.<TypeAssignmentNode, TypeAssignmentCompiler>getCompiler(TypeAssignmentNode.class)
                .compile(compilerContext, assignment);
    }

    private void compileObjectClassAssignment(ObjectClassAssignmentNode assignment) {
        // TODO Auto-generated method stub
    }

    private void compileObjectSetAssignment(ObjectSetAssignmentNode assignment) {
        // TODO Auto-generated method stub
    }

    private void compileParameterizedAssignment(ParameterizedAssignmentNode assignment) {
        // TODO Auto-generated method stub
    }

    private void compileImports(ModuleNode module) throws IOException, ParserException {
        List<ImportNode> imports = module.getBody().getImports();

        for (ImportNode imp : imports) {
            ModuleRefNode importedModule = imp.getReference();
            String importedModuleName = importedModule.getName();

            if (!compilerContext.isModuleLoaded(importedModuleName)) {
                loadAndCompileModule(importedModuleName);
            }
        }
    }

    public void load(String moduleName) throws IOException, ParserException {
        System.out.println("Loading module " + moduleName + "...");

        String moduleFile = null;

        for (String path : includePath) {
            String file = StringUtils.concat(path, File.separator, moduleName);
            if (new File(file + ASN_1_EXTENSION).exists()) {
                moduleFile = file + ASN_1_EXTENSION;
            } else if (new File(file + ASN_EXTENSION).exists()) {
                moduleFile = file + ".asn";
            }
        }

        if (moduleFile == null) {
            throw new IOException("Module " + moduleName + " not found in include path");
        }

        Parser parser = new Parser(moduleFile);

        try {
            ModuleNode module = parser.parse();
            compilerContext.addModule(moduleName, module);
            System.out.println("Loaded module " + moduleName);
        } catch (ParserException e) {
            throw new ParserException("Failed to load module " + moduleName, e);
        }
    }

    private String stripExtension(String module) {
        if (module.endsWith(ASN_1_EXTENSION)) {
            module = module.substring(0, module.length() - 5);
        } else if (module.endsWith(".asn")) {
            module = module.substring(0, module.length() - 4);
        }

        return module;
    }

    public Optional<CompiledType> compileType(String name) {
        Optional<AssignmentNode> maybeTypeAssignment = assignments.peek().stream().filter(
                obj -> obj instanceof TypeAssignmentNode && name.equals(obj.getReference())).findFirst();

        if (maybeTypeAssignment.isPresent()) {
            TypeAssignmentNode assignment = (TypeAssignmentNode) maybeTypeAssignment.get();

            assignments.peek().remove(assignment);

            return Optional.of(compileTypeAssignment(assignment));
        }

        return Optional.empty();
    }

}
