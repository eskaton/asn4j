/*
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

import ch.eskaton.asn4j.compiler.constraints.ConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.ConstraintDefinition;
import ch.eskaton.asn4j.compiler.defaults.DefaultsCompiler;
import ch.eskaton.asn4j.compiler.il.BooleanExpression;
import ch.eskaton.asn4j.compiler.il.Module;
import ch.eskaton.asn4j.compiler.java.JavaWriter;
import ch.eskaton.asn4j.compiler.java.objs.JavaClass;
import ch.eskaton.asn4j.compiler.java.objs.JavaModifier;
import ch.eskaton.asn4j.compiler.java.objs.JavaStructure;
import ch.eskaton.asn4j.compiler.resolvers.ValueResolver;
import ch.eskaton.asn4j.compiler.results.AnonymousCompiledType;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.compiler.results.HasChildComponents;
import ch.eskaton.asn4j.parser.ParserException;
import ch.eskaton.asn4j.parser.ast.AssignmentNode;
import ch.eskaton.asn4j.parser.ast.ExportsNode;
import ch.eskaton.asn4j.parser.ast.ImportNode;
import ch.eskaton.asn4j.parser.ast.ModuleNode;
import ch.eskaton.asn4j.parser.ast.ModuleRefNode;
import ch.eskaton.asn4j.parser.ast.Node;
import ch.eskaton.asn4j.parser.ast.ReferenceNode;
import ch.eskaton.asn4j.parser.ast.TypeAssignmentNode;
import ch.eskaton.asn4j.parser.ast.TypeOrObjectClassAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ValueOrObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.constraints.Constraint;
import ch.eskaton.asn4j.parser.ast.types.BMPString;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.BooleanType;
import ch.eskaton.asn4j.parser.ast.types.Choice;
import ch.eskaton.asn4j.parser.ast.types.ClassType;
import ch.eskaton.asn4j.parser.ast.types.CollectionOfType;
import ch.eskaton.asn4j.parser.ast.types.EnumeratedType;
import ch.eskaton.asn4j.parser.ast.types.ExternalTypeReference;
import ch.eskaton.asn4j.parser.ast.types.GeneralString;
import ch.eskaton.asn4j.parser.ast.types.GeneralizedTime;
import ch.eskaton.asn4j.parser.ast.types.GraphicString;
import ch.eskaton.asn4j.parser.ast.types.IA5String;
import ch.eskaton.asn4j.parser.ast.types.IRI;
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.asn4j.parser.ast.types.Null;
import ch.eskaton.asn4j.parser.ast.types.NumericString;
import ch.eskaton.asn4j.parser.ast.types.ObjectIdentifier;
import ch.eskaton.asn4j.parser.ast.types.OctetString;
import ch.eskaton.asn4j.parser.ast.types.PrintableString;
import ch.eskaton.asn4j.parser.ast.types.Real;
import ch.eskaton.asn4j.parser.ast.types.RelativeIRI;
import ch.eskaton.asn4j.parser.ast.types.RelativeOID;
import ch.eskaton.asn4j.parser.ast.types.SelectionType;
import ch.eskaton.asn4j.parser.ast.types.SequenceOfType;
import ch.eskaton.asn4j.parser.ast.types.SequenceType;
import ch.eskaton.asn4j.parser.ast.types.SetOfType;
import ch.eskaton.asn4j.parser.ast.types.SetType;
import ch.eskaton.asn4j.parser.ast.types.T61String;
import ch.eskaton.asn4j.parser.ast.types.TeletexString;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.types.UTCTime;
import ch.eskaton.asn4j.parser.ast.types.UTF8String;
import ch.eskaton.asn4j.parser.ast.types.UniversalString;
import ch.eskaton.asn4j.parser.ast.types.UsefulType;
import ch.eskaton.asn4j.parser.ast.types.VideotexString;
import ch.eskaton.asn4j.parser.ast.types.VisibleString;
import ch.eskaton.asn4j.parser.ast.values.AbstractValue;
import ch.eskaton.asn4j.parser.ast.values.DefinedValue;
import ch.eskaton.asn4j.parser.ast.values.ExternalValueReference;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Tag;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.TagId;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.types.ASN1BMPString;
import ch.eskaton.asn4j.runtime.types.ASN1Boolean;
import ch.eskaton.asn4j.runtime.types.ASN1GeneralString;
import ch.eskaton.asn4j.runtime.types.ASN1GeneralizedTime;
import ch.eskaton.asn4j.runtime.types.ASN1GraphicString;
import ch.eskaton.asn4j.runtime.types.ASN1IA5String;
import ch.eskaton.asn4j.runtime.types.ASN1Null;
import ch.eskaton.asn4j.runtime.types.ASN1NumericString;
import ch.eskaton.asn4j.runtime.types.ASN1OctetString;
import ch.eskaton.asn4j.runtime.types.ASN1PrintableString;
import ch.eskaton.asn4j.runtime.types.ASN1TeletexString;
import ch.eskaton.asn4j.runtime.types.ASN1UTCTime;
import ch.eskaton.asn4j.runtime.types.ASN1UTF8String;
import ch.eskaton.asn4j.runtime.types.ASN1UniversalString;
import ch.eskaton.asn4j.runtime.types.ASN1VideotexString;
import ch.eskaton.asn4j.runtime.types.ASN1VisibleString;
import ch.eskaton.commons.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static ch.eskaton.asn4j.compiler.CompilerUtils.formatName;
import static ch.eskaton.asn4j.compiler.CompilerUtils.formatTypeName;
import static ch.eskaton.asn4j.compiler.CompilerUtils.resolveAmbiguousValue;
import static ch.eskaton.asn4j.parser.NoPosition.NO_POSITION;

public class CompilerContext {

    private HashMap<String, HashMap<String, CompiledType>> definedTypes = new HashMap<>();

    private TypeConfiguration config = new TypeConfiguration(this);

    private ConstraintCompiler constraintCompiler = new ConstraintCompiler(this);

    private DefaultsCompiler defaultsCompiler = new DefaultsCompiler(this);

    private Deque<JavaClass> currentClass = new LinkedList<>();

    private Map<String, JavaStructure> structs = new HashMap<>();

    private Map<String, ModuleNode> modules = new HashMap<>();

    private Deque<ModuleNode> currentModule = new LinkedList<>();

    private CompilerImpl compiler;

    private String pkg;

    private String outputDir;

    public CompilerContext(CompilerImpl compiler, String pkg, String outputDir) {
        this.compiler = compiler;
        this.pkg = pkg;
        this.outputDir = outputDir;
    }

    private void addType(String typeName, CompiledType compiledType) {
        var moduleTypes = getTypesOfCurrentModule();

        moduleTypes.put(typeName, compiledType);
    }

    @SuppressWarnings("unchecked")
    public <T extends Node, C extends Compiler<T>> C getCompiler(Class<T> clazz) {
        return config.getCompiler(clazz);
    }

    public ModuleNode getModule() {
        return currentModule.peek();
    }

    public void finishClass() {
        finishClass(true);
    }

    public void finishClass(boolean createEqualsAndHashCode) {
        JavaClass javaClass = currentClass.pop();

        if (createEqualsAndHashCode) {
            javaClass.createEqualsAndHashCode();
        }

        if (currentClass.isEmpty()) {
            structs.put(javaClass.getName(), javaClass);
        } else {
            currentClass.peek().addInnerClass(javaClass);
            javaClass.addModifier(JavaModifier.STATIC);
        }
    }

    public JavaClass createClass(String name, Type type, boolean constructed) {
        String className = formatName(name);
        Tag tag = type.getTag();

        if (tag != null && ClassType.UNIVERSAL.equals(tag.getClazz())) {
            throw new CompilerException("UNIVERSAL class not allowed in type " + name);
        }

        JavaClass javaClass = new JavaClass(pkg, className, tag, CompilerUtils
                .getTaggingMode(getModule(), type), constructed, getTypeName(type));

        currentClass.push(javaClass);

        return javaClass;
    }

    public JavaClass getCurrentClass() {
        return currentClass.peek();
    }

    public Optional<JavaClass> getClass(String className) {
        JavaStructure struct = structs.get(className);

        if (struct instanceof JavaClass) {
            return Optional.of((JavaClass) struct);
        }

        return Optional.empty();
    }

    public void pushModule(ModuleNode module) {
        currentModule.push(module);
    }

    public void pushModule(String moduleName) {
        currentModule.push(modules.get(moduleName));
    }

    public void popModule() {
        currentModule.pop();
    }

    public void duplicateModule() {
        currentModule.push(getModule());
    }

    public boolean isModuleLoaded(String moduleName) {
        return modules.containsKey(moduleName);
    }

    public void addModule(String moduleName, ModuleNode module) {
        modules.put(moduleName, module);
    }

    public ModuleNode getModule(String moduleName) {
        ModuleNode module = modules.get(moduleName);

        if (module == null) {
            try {
                compiler.loadAndCompileModule(moduleName);

                module = modules.get(moduleName);
            } catch (IOException | ParserException e) {
                throw new CompilerException("Failed to load module %s", moduleName, e);
            }
        }

        return module;
    }

    public Type getBase(String typeName) {
        return resolveType(currentModule.peek(), typeName);
    }

    public Type resolveType(String moduleName, String typeName) {
        return resolveType(getModule(moduleName), typeName);
    }

    private Type resolveType(ModuleNode module, String typeName) {
        // TODO: what to do if the type isn't known in the current module
        while (true) {
            // Check for implicitly defined types
            if (GeneralizedTime.class.getSimpleName().equals(typeName)) {
                return new GeneralizedTime(NO_POSITION, typeName);
            } else if (UTCTime.class.getSimpleName().equals(typeName)) {
                return new UTCTime(NO_POSITION, typeName);
            }

            Optional<TypeAssignmentNode> assignment = getTypeAssignment(module, typeName);

            if (assignment.isEmpty()) {
                var moduleName = findImport(typeName);

                if (moduleName.isPresent()) {
                    return resolveType(getModule(moduleName.get()), typeName);
                }

                throw new CompilerException("Failed to resolve a type: %s", typeName);
            }

            Type base = assignment.get().getType();

            if (base instanceof TypeReference) {
                typeName = ((TypeReference) base).getType();
            } else {
                return base;
            }
        }
    }

    private Optional<String> findImport(String typeName) {
        return currentModule.peek().getBody().getImports().stream()
                .filter(importNode -> importNode.getSymbols().stream()
                        .filter(sym -> sym.getName().equals(typeName))
                        .findFirst().isPresent()
                ).map(importNode -> importNode.getReference().getName())
                .findAny();
    }

    public Type getBase(Type type) {
        if (type instanceof TypeReference) {
            return getBase((TypeReference) type);
        }

        return type;
    }

    public Type getBase(TypeReference type) {
        return getBase(type.getType());
    }

    public String getTypeName(Type type) {
        String name = null;

        if (type instanceof NamedType) {
            name = ((NamedType) type).getName();
            type = ((NamedType) type).getType();
        }

        return getTypeName(type, name);
    }

    public String getTypeName(Type type, String name) {
        String typeName;

        if (type instanceof TypeReference) {
            if (type instanceof UsefulType) {
                typeName = switch (((UsefulType) type).getType()) {
                    case "GeneralizedTime" -> ASN1GeneralizedTime.class.getSimpleName();
                    case "UTCTime" -> ASN1UTCTime.class.getSimpleName();
                    default -> throw new IllegalCompilerStateException("Unsupported UsefulType: %s",
                            ((UsefulType) type).getType());
                };
            } else {
                typeName = CompilerUtils.formatTypeName(((TypeReference) type).getType());
            }
        } else if (type instanceof ExternalTypeReference) {
            typeName = CompilerUtils.formatTypeName(((ExternalTypeReference) type).getType());
        } else if (type instanceof Null) {
            typeName = ASN1Null.class.getSimpleName();
        } else if (type instanceof BooleanType) {
            typeName = ASN1Boolean.class.getSimpleName();
        } else if (type instanceof VisibleString) {
            typeName = ASN1VisibleString.class.getSimpleName();
        } else if (type instanceof NumericString) {
            typeName = ASN1NumericString.class.getSimpleName();
        } else if (type instanceof PrintableString) {
            typeName = ASN1PrintableString.class.getSimpleName();
        } else if (type instanceof IA5String) {
            typeName = ASN1IA5String.class.getSimpleName();
        } else if (type instanceof GraphicString) {
            typeName = ASN1GraphicString.class.getSimpleName();
        } else if (type instanceof GeneralString) {
            typeName = ASN1GeneralString.class.getSimpleName();
        } else if (type instanceof TeletexString) {
            typeName = ASN1TeletexString.class.getSimpleName();
        } else if (type instanceof T61String) {
            typeName = ASN1TeletexString.class.getSimpleName();
        } else if (type instanceof VideotexString) {
            typeName = ASN1VideotexString.class.getSimpleName();
        } else if (type instanceof UniversalString) {
            typeName = ASN1UniversalString.class.getSimpleName();
        } else if (type instanceof UTF8String) {
            typeName = ASN1UTF8String.class.getSimpleName();
        } else if (type instanceof BMPString) {
            typeName = ASN1BMPString.class.getSimpleName();
        } else if (type instanceof OctetString) {
            typeName = ASN1OctetString.class.getSimpleName();
        } else if (type instanceof EnumeratedType) {
            typeName = getTypeName(type, name, isSubtypeNeeded(type));
        } else if (type instanceof IntegerType) {
            typeName = getTypeName(type, name, isSubtypeNeeded(type));
        } else if (type instanceof Real) {
            typeName = getTypeName(type, name, false);
        } else if (type instanceof BitString) {
            typeName = getTypeName(type, name, isSubtypeNeeded(type));
        } else if (type instanceof SequenceType
                || type instanceof SequenceOfType
                || type instanceof SetType
                || type instanceof SetOfType
                || type instanceof Choice) {
            typeName = getTypeName(type, name, true);
        } else if (type instanceof ObjectIdentifier
                || type instanceof RelativeOID
                || type instanceof IRI
                || type instanceof RelativeIRI) {
            typeName = getTypeName(type, name, false);
        } else if (type instanceof SelectionType) {
            SelectionType selectionType = (SelectionType) type;
            Type selectedType = resolveType(type);

            if (selectedType != type) {
                return getTypeName(selectedType);
            }

            throw new CompilerException(String.format("Unknown SelectionType: %s < %s",
                    selectionType.getId(), ((TypeReference) selectionType.getType()).getType()));
        } else {
            throw new CompilerException("Unsupported type: " + type.getClass());
        }

        return typeName;
    }

    private String getTypeName(Type type, String name, boolean newType) {
        String typeName;

        if (newType && name != null) {
            typeName = CompilerUtils.formatTypeName(name);
        } else {
            typeName = getRuntimeTypeName(type.getClass());

            if (typeName == null) {
                throw new CompilerException("No runtime class available for type " + type);
            }
        }

        return typeName;
    }

    public CompiledType defineType(Type type) {
        String name = null;

        if (type instanceof NamedType) {
            name = ((NamedType) type).getName();
            type = ((NamedType) type).getType();
        }

        return defineType(type, name);
    }

    public CompiledType defineType(Type type, String name) {
        if (type instanceof EnumeratedType) {
            return defineType(type, name, isSubtypeNeeded(type));
        } else if (type instanceof IntegerType) {
            return defineType(type, name, isSubtypeNeeded(type));
        } else if (type instanceof BitString) {
            return defineType(type, name, isSubtypeNeeded(type));
        } else if (type instanceof SequenceType
                || type instanceof SequenceOfType
                || type instanceof SetType
                || type instanceof SetOfType
                || type instanceof Choice) {
            return defineType(type, name, true);
        } else if (type instanceof ObjectIdentifier
                || type instanceof RelativeOID
                || type instanceof IRI
                || type instanceof RelativeIRI) {
            return defineType(type, name, false);
        }

        return createCompiledType(type, getTypeName(type, name), true);
    }

    private CompiledType defineType(Type type, String name, boolean newType) {
        if (newType && name != null) {
            return compileType(type, getTypeName(type, name));
        }

        return createCompiledType(type, getTypeName(type, name));
    }

    private CompiledType compileType(Type type, String typeName) {
        return this.<Type, TypeCompiler>getCompiler(Type.class).compile(this, typeName, type);
    }

    public Optional<TypeAssignmentNode> getTypeAssignment(String typeName) {
        return getTypeAssignment(currentModule.peek(), typeName);
    }

    public Optional<TypeAssignmentNode> getTypeAssignment(String moduleName, String typeName) {
        var module = Optional.ofNullable(modules.get(moduleName));

        if (module.isPresent()) {
            return getTypeAssignment(module.get(), typeName);
        }

        return Optional.empty();
    }

    private Optional<TypeAssignmentNode> getTypeAssignment(ModuleNode module, String typeName) {
        return module.getBody().getAssignments().stream()
                .filter(TypeAssignmentNode.class::isInstance)
                .map(TypeAssignmentNode.class::cast)
                .filter(a -> typeName.equals(a.getReference()))
                .findFirst();
    }

    public Class<? extends Value> getValueType(Type type) {
        Class<? extends Type> typeClass;

        if (type instanceof TypeReference) {
            typeClass = resolveTypeReference(type).getClass();
        } else {
            typeClass = type.getClass();
        }

        return config.getValueClass(typeClass);
    }

    public boolean isConstructed(Type type) {
        return config.isConstructed(type.getClass());
    }

    public <V extends Value> V resolveValue(Class<V> valueClass, Optional<Type> type, V value) {
        return getValueResolver(valueClass).resolve(type, value);
    }

    public <V extends Value> V resolveValue(Class<V> valueClass, DefinedValue ref) {
        return getValueResolver(valueClass).resolve(ref);
    }

    public <V extends Value> V resolveValue(Class<V> valueClass, String ref) {
        return getValueResolver(valueClass).resolve(ref);
    }

    public <V extends Value> V resolveValue(Class<V> valueClass, String moduleName, String reference) {
        var module = getModule(moduleName);

        if (module != null) {
            currentModule.push(module);

            try {
                return getValueResolver(valueClass).resolve(reference);
            } finally {
                currentModule.pop();
            }
        }

        return null;
    }

    public <V extends Value> V resolveGenericValue(Class<V> valueClass, Type type, Value value) {
        var resolvedValue = getValueResolver(valueClass).resolveGeneric(type, value);

        if (resolvedValue instanceof AbstractValue) {
            ((AbstractValue) resolvedValue).setType(type);
        }

        return resolvedValue;
    }

    private <V extends Value> ValueResolver<V> getValueResolver(Class<V> valueClass) {
        return config.getValueResolver(valueClass);
    }

    public Type resolveTypeReference(String reference) {
        // TODO: what to do if the type isn't known in the current module
        return Optional.ofNullable(((TypeAssignmentNode) getModule().getBody().getAssignment(reference)))
                .map(TypeOrObjectClassAssignmentNode::getType)
                .orElseThrow(() -> new CompilerException("Failed to resolve reference to %s", reference));
    }

    public <T extends Type> T resolveTypeReference(Class<T> typeClass, String reference) {
        Type type = resolveTypeReference(reference);

        if (!type.getClass().equals(typeClass)) {
            throw new CompilerException("Failed to resolve reference %s to type %s. Found type: %s",
                    reference, typeClass.getSimpleName(), formatTypeName(type));
        }

        return (T) type;
    }

    public <T extends Type> T resolveTypeReference(Class<T> typeClass, String moduleName, String reference) {
        Type type = resolveType(moduleName, reference);

        if (!type.getClass().equals(typeClass)) {
            throw new CompilerException("Failed to resolve reference %s to type %s. Found type: %s",
                    reference, typeClass.getSimpleName(), formatTypeName(type));
        }

        return (T) type;
    }

    public Type resolveTypeReference(Type typeReference) {
        while (typeReference instanceof TypeReference) {
            if (typeReference instanceof GeneralizedTime || typeReference instanceof UTCTime) {
                return typeReference;
            }

            Optional<TypeAssignmentNode> assignment = getTypeAssignment(((TypeReference) typeReference).getType());

            if (assignment.isPresent()) {
                var node = assignment.get().getType();

                if (!(node instanceof Type)) {
                    throw new CompilerException("Invalid type: %s", node.getClass().getSimpleName());
                }

                typeReference = node;
            } else {
                throw new CompilerException("Failed to resolve reference to %s",
                        ((TypeReference) typeReference).getType());
            }
        }

        return typeReference;
    }

    public ValueOrObjectAssignmentNode resolveDefinedValue(DefinedValue ref) {
        if (ref instanceof ExternalValueReference) {
            return resolveExternalReference(((ExternalValueReference) ref));
        } else {
            return resolveValueReference(((SimpleDefinedValue) ref));
        }
    }

    private ValueOrObjectAssignmentNode resolveExternalReference(ExternalValueReference reference) {
        String moduleName = reference.getModule();
        String symbolName = reference.getValue();
        ModuleNode module;

        try {
            module = getModule(moduleName);
        } catch (CompilerException e) {
            throw new CompilerException("Failed to resolve external reference %s.%s", moduleName, symbolName, e);
        }

        ensureSymbolIsExported(module, symbolName);

        AssignmentNode assignment = modules.get(moduleName).getBody().getAssignment(symbolName);

        if (!(assignment instanceof ValueOrObjectAssignmentNode)) {
            throw new CompilerException("Failed to resolve reference " + reference);
        }

        return (ValueOrObjectAssignmentNode<?, ?>) assignment;
    }

    public Optional<ValueOrObjectAssignmentNode> tryResolveAllValueReferences(SimpleDefinedValue reference) {
        Optional<ValueOrObjectAssignmentNode> assignment = tryResolveValueReference(reference);
        Optional<ValueOrObjectAssignmentNode> tmpAssignment = assignment;

        while (tmpAssignment.isPresent()) {
            assignment = tmpAssignment;

            reference = resolveAmbiguousValue(tmpAssignment.get().getValue(), SimpleDefinedValue.class);

            if (reference == null) {
                break;
            }

            tmpAssignment = tryResolveValueReference(reference);

            if (tmpAssignment.equals(assignment)) {
                break;
            }
        }

        return assignment;
    }

    public Optional<ValueOrObjectAssignmentNode> tryResolveValueReference(SimpleDefinedValue reference) {
        if (reference instanceof ExternalValueReference) {
            return Optional.ofNullable(resolveExternalReference(((ExternalValueReference) reference)));
        }

        return tryResolveValueReference(reference.getValue());
    }

    public Optional<ValueOrObjectAssignmentNode> tryResolveValueReference(String symbolName) {
        AssignmentNode assignment = getModule().getBody().getAssignment(symbolName);

        if (assignment == null) {
            Optional<ImportNode> imp = getImport(symbolName);

            if (imp.isPresent()) {
                ModuleRefNode moduleRef = imp.get().getReference();
                String moduleName = moduleRef.getName();
                ModuleNode module = getModule(moduleName);

                ensureSymbolIsExported(module, symbolName);

                assignment = modules.get(moduleName).getBody().getAssignment(symbolName);
            }
        }

        if (assignment instanceof ValueOrObjectAssignmentNode) {
            return Optional.of((ValueOrObjectAssignmentNode) assignment);
        }

        return Optional.empty();
    }

    public ValueOrObjectAssignmentNode resolveValueReference(SimpleDefinedValue reference) {
        return resolveValueReference(reference.getValue());
    }

    public ValueOrObjectAssignmentNode resolveValueReference(String symbolName) {
        Optional<ValueOrObjectAssignmentNode> assignment = tryResolveValueReference(symbolName);

        if (!assignment.isPresent()) {
            throw new CompilerException("Failed to resolve reference " + symbolName);
        }

        return assignment.get();
    }

    public void ensureSymbolIsExported(ModuleNode module, String symbolName) {
        if (!isSymbolExported(module, symbolName)) {
            String format = "Module %s uses the symbol %s from module %s which the latter doesn't export";
            throw new CompilerException(format, getCurrentModuleName(), symbolName,
                    module.getModuleId().getModuleName());
        }
    }

    public void writeClasses() {
        String pkgDir = pkg.replace('.', File.separatorChar);

        File pkgFile = new File(StringUtils.concat(outputDir, File.separator, pkgDir));

        if (pkgFile.exists() || pkgFile.mkdirs()) {
            new JavaWriter().write(structs, outputDir);
        } else {
            throw new CompilerException("Failed to create directory " + pkgFile);
        }
    }

    public Optional<BooleanExpression> buildExpression(Module module, CompiledType compiledType,
            ch.eskaton.asn4j.compiler.constraints.ast.Node node) {
        return constraintCompiler.buildExpression(module, compiledType, node);
    }

    public ConstraintDefinition compileConstraint(JavaClass javaClass, String name, CompiledType compiledType) {
        return constraintCompiler.compileConstraint(javaClass, name, compiledType);
    }

    public ConstraintDefinition compileConstraint(CompiledType compiledType) {
        return constraintCompiler.compileConstraint(compiledType);
    }

    public ConstraintDefinition compileConstraint(Type type, Constraint constraint) {
        return constraintCompiler.compileConstraint(type, constraint);
    }

    public ConstraintDefinition compileConstraint(CompiledType compiledType, Constraint constraint) {
        return constraintCompiler.compileConstraint(compiledType, constraint);
    }

    public void addConstraint(CompiledType compiledType, Module module, ConstraintDefinition definition) {
        constraintCompiler.addConstraint(compiledType, module, definition);
    }

    public void compileDefault(JavaClass javaClass, String field, String typeName, Type type, Value value) {
        defaultsCompiler.compileDefault(javaClass, field, typeName, type, value);
    }

    public Type resolveType(Type type) {
        if (type instanceof SelectionType) {
            String selectedId = ((SelectionType) type).getId();
            Type selectedType = ((SelectionType) type).getType();

            if (selectedType instanceof TypeReference) {
                Object assignment = getModule().getBody().getAssignment(((TypeReference) selectedType).getType());

                if (assignment instanceof TypeAssignmentNode) {
                    Type collectionType = ((TypeAssignmentNode) assignment).getType();

                    if (collectionType instanceof Choice) {
                        return ((Choice) collectionType).getRootAlternatives().stream()
                                .filter(t -> t.getName().equals(selectedId))
                                .findFirst()
                                .orElseThrow(() -> new CompilerException("Selected type not found")).getType();
                    }
                }
            }
        }

        return type;
    }

    public TagId getTagId(Type type) {
        Optional<CompiledType> maybeReferencedType = getCompiledType(type, true);

        if (maybeReferencedType.isPresent()) {
            Type referencedType = maybeReferencedType.get().getType();
            Tag tag = referencedType.getTag();

            if (tag != null) {
                return CompilerUtils.toTagId(tag);
            } else {
                return getTagId(referencedType);
            }
        }

        String typeName = getTypeName(type);

        try {
            Class<?> typeClazz = Class.forName("ch.eskaton.asn4j.runtime.types." + typeName);
            ASN1Tag tagAnnotation = typeClazz.getAnnotation(ASN1Tag.class);
            return TagId.fromTag(tagAnnotation);
        } catch (ClassNotFoundException e) {
            throw new CompilerException("Unknown type: " + type);
        }
    }

    public CompiledCollectionType getCompiledCollectionType(CompiledType compiledType) {
        return Optional.ofNullable(getCompiledBaseType(compiledType))
                .filter(CompiledCollectionType.class::isInstance)
                .map(CompiledCollectionType.class::cast)
                .orElseThrow(() -> new CompilerException("Failed to resolve the type of %s", compiledType));
    }

    public <T> T withNewClass(Supplier<T> supplier) {
        Deque<JavaClass> oldClass = currentClass;

        try {
            currentClass = new LinkedList<>();

            return supplier.get();
        } finally {
            currentClass = oldClass;
        }
    }

    public Optional<CompiledType> getCompiledType(Type type, boolean isSubType) {
        if (isResolvableReference(type)) {
            String typeName = ((TypeReference) type).getType();
            HashMap<String, CompiledType> moduleTypes = getTypesOfCurrentModule();
            Optional<CompiledType> compiledType = Optional.ofNullable(moduleTypes.get(typeName));

            if (!compiledType.isPresent()) {
                if (!isSubType) {
                    compiledType = withNewClass(() -> compiler.compileType(typeName));
                } else {
                    compiledType = compiler.compileType(typeName);
                }
            }

            if (!compiledType.isPresent()) {
                Optional<ImportNode> imp = getImport(typeName);

                if (imp.isPresent()) {
                    String moduleName = imp.get().getReference().getName();
                    ModuleNode module = getModule(moduleName);

                    if (!isSymbolExported(module, typeName)) {
                        String format = "Module %s uses the type %s from module %s which the latter doesn't export";
                        throw new CompilerException(format, getCurrentModuleName(),
                                typeName, moduleName);
                    }

                    moduleTypes = definedTypes.get(moduleName);
                    compiledType = Optional.ofNullable(moduleTypes.get(typeName));
                }
            }

            if (!compiledType.isPresent()) {
                throw new CompilerException("Failed to resolve type %s in module %s ", typeName, getCurrentModuleName());
            }

            return compiledType;
        }

        return Optional.empty();
    }

    private Optional<ImportNode> getImport(String symbolName) {
        List<ImportNode> imports = currentModule.peek().getBody().getImports();

        for (ImportNode imp : imports) {
            Optional<ReferenceNode> symbol = imp.getSymbols().stream()
                    .filter(s -> s.getName().equals(symbolName))
                    .findAny();

            if (symbol.isPresent()) {
                return Optional.of(imp);
            }
        }

        return Optional.empty();
    }

    private boolean isSymbolExported(ModuleNode module, String symbol) {
        ExportsNode exports = module.getBody().getExports();

        if (exports.getMode() == ExportsNode.Mode.SPECIFIC) {
            return exports.getSymbols().stream().anyMatch(s -> s.getName().equals(symbol));
        }

        return false;
    }

    /**
     * Resolves the compiled base type for compiledType. If it already is a base
     * type it is returned as is.
     *
     * @param compiledType a compiled type
     * @return a compiled type
     */
    public CompiledType getCompiledBaseType(CompiledType compiledType) {
        if (compiledType.getType() instanceof TypeReference ||
                compiledType.getType() instanceof ExternalTypeReference) {
            return getCompiledBaseType(compiledType.getType());
        }

        return compiledType;
    }

    /**
     * Resolves the compiled base type for type.
     *
     * @param type a type
     * @return a compiled type
     */
    public CompiledType getCompiledBaseType(Type type) {
        CompiledType compiledType;

        do {
            compiledType = getCompiledType(type);

            type = compiledType.getType();
        } while (isResolvableReference(type));

        return compiledType;
    }

    private boolean isResolvableReference(Type type) {
        return type instanceof TypeReference && !(type instanceof UsefulType);
    }

    /**
     * Looks up the compiled type for the given type. Type may be compiled if it isn't already.
     * If no compiled type can be found, it is assumed that type is a builtin type which is
     * wrapped in a compiled type.
     *
     * @param type a type
     * @return a compiled type
     */
    public CompiledType getCompiledType(Type type) {
        return getCompiledType(type, false).orElse(new AnonymousCompiledType(type));
    }

    private HashMap<String, CompiledType> getTypesOfCurrentModule() {
        return definedTypes.computeIfAbsent(getCurrentModuleName(), key -> new HashMap<>());
    }

    private String getCurrentModuleName() {
        return currentModule.peek().getModuleId().getModuleName();
    }

    public String getRuntimeTypeName(Class<? extends Type> type) {
        return config.getRuntimeTypeClass(type).getSimpleName();
    }

    public String getRuntimeTypeName(String typeName) {
        try {
            Type type = getBase(typeName);

            return getRuntimeTypeName(type.getClass());
        } catch (CompilerException e) {
            return typeName;
        }
    }

    public String getRuntimeTypeName(Type type) {
        return getRuntimeTypeName(resolveTypeReference(type).getClass());
    }

    public List<String> getTypeParameter(Type type) {
        return getTypeParameter(type, Optional.empty());
    }

    public List<String> getTypeParameter(Type type, Optional<String> parentName) {
        LinkedList<String> typeNames = new LinkedList<>();

        while (type instanceof CollectionOfType) {
            type = ((CollectionOfType) type).getType();

            typeNames.add(getContentType(type, parentName).orElse(getTypeName(type)));
        }

        return typeNames;
    }

    public Optional<String> getContentType(Type type, Optional<String> parentName) {
        if (parentName.isPresent() && isSubtypeNeeded(type)) {
            return Optional.of(parentName.get() + "." + getTypeName(type, parentName.get() + "Content"));
        }

        return Optional.empty();
    }

    public boolean isSubtypeNeeded(Type type) {
        return (type instanceof SequenceType ||
                type instanceof SetType ||
                type instanceof EnumeratedType ||
                type instanceof IntegerType && ((IntegerType) type).getNamedNumbers() != null ||
                type instanceof BitString && ((BitString) type).getNamedBits() != null);
    }

    public List<String> getParameterizedType(Type node) {
        LinkedList<String> typeNames = new LinkedList<>();
        Type type = node;

        do {
            typeNames.add(getTypeName(type));

            if (type instanceof CollectionOfType) {
                type = ((CollectionOfType) type).getType();
            } else {
                break;
            }
        } while (true);

        return typeNames;
    }

    public CompiledType createCompiledType(Type type, String name) {
        return createCompiledType(CompiledType.class, type, name);
    }

    public CompiledType createCompiledType(Type type, String name, boolean isSubType) {
        return createCompiledType(CompiledType.class, type, name, isSubType);
    }

    public <T extends CompiledType> T createCompiledType(Class<T> compiledTypeClass, Type type, String name) {
        return createCompiledType(compiledTypeClass, type, name, false);
    }

    public <T extends CompiledType> T createCompiledType(Class<T> compiledTypeClass, Type type, String name,
            boolean isSubType) {
        Constructor<T> ctor;

        try {
            ctor = compiledTypeClass.getDeclaredConstructor(Type.class, String.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalCompilerStateException("Constructor not found in %s", e, compiledTypeClass);
        }

        ctor.setAccessible(true);

        T compiledType;

        try {
            compiledType = ctor.newInstance(type, name);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalCompilerStateException("Constructor %s threw an exception", e, compiledTypeClass);
        }

//        if (isSubType && currentClass.size() == 1) {
//            System.out.println(String.format("Created compiled type %s (%s): isSubType=%s stackSize=%s", compiledType.getName(), type, isSubType, currentClass.size()));
//        }

        // TODO: Optimize condition
        if (!isSubType && isTopLevelType()) {
            addType(name, compiledType);
        }

        return compiledType;
    }

    private boolean isTopLevelType() {
        return currentClass.size() == 1;
    }

    public <T extends CompiledType & HasChildComponents> Optional<T> findCompiledTypeRecursive(Type type) {
        if (type instanceof TypeReference) {
            // type references are not nested, but may not yet be compiled, so we force the compilation here
            var compiledType = getCompiledType(type);

            if (compiledType instanceof HasChildComponents) {
                return Optional.of((T) compiledType);
            }

            return Optional.empty();
        }

        var componentStream = getTypesOfCurrentModule().entrySet().stream().map(Map.Entry::getValue);

        return findCompiledTypeRecursiveAux(type, componentStream);
    }

    private <T extends CompiledType & HasChildComponents> Optional<T> findCompiledTypeRecursive(Type type,
            T compiledType) {
        if (Objects.equals(type, compiledType.getType())) {
            return Optional.of(compiledType);
        }

        var componentStream = compiledType.getChildComponents().stream();

        return findCompiledTypeRecursiveAux(type, componentStream);
    }

    private <T extends CompiledType & HasChildComponents> Optional<T> findCompiledTypeRecursiveAux(Type type,
            Stream<? extends CompiledType> componentStream) {
        return componentStream.filter(HasChildComponents.class::isInstance)
                .map(c -> findCompiledTypeRecursive(type, (T) c))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

}
