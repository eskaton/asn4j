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
import ch.eskaton.asn4j.compiler.java.JavaClass;
import ch.eskaton.asn4j.compiler.java.JavaModifier;
import ch.eskaton.asn4j.compiler.java.JavaStructure;
import ch.eskaton.asn4j.compiler.java.JavaWriter;
import ch.eskaton.asn4j.compiler.resolvers.BitStringValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.DefaultValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.IntegerValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.ValueResolver;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.AssignmentNode;
import ch.eskaton.asn4j.parser.ast.ModuleNode;
import ch.eskaton.asn4j.parser.ast.Node;
import ch.eskaton.asn4j.parser.ast.TypeAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ValueOrObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.BooleanType;
import ch.eskaton.asn4j.parser.ast.types.Choice;
import ch.eskaton.asn4j.parser.ast.types.ClassType;
import ch.eskaton.asn4j.parser.ast.types.ComponentType;
import ch.eskaton.asn4j.parser.ast.types.EnumeratedType;
import ch.eskaton.asn4j.parser.ast.types.GeneralizedTime;
import ch.eskaton.asn4j.parser.ast.types.IRI;
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.asn4j.parser.ast.types.Null;
import ch.eskaton.asn4j.parser.ast.types.ObjectIdentifier;
import ch.eskaton.asn4j.parser.ast.types.OctetString;
import ch.eskaton.asn4j.parser.ast.types.Real;
import ch.eskaton.asn4j.parser.ast.types.RelativeIRI;
import ch.eskaton.asn4j.parser.ast.types.RelativeOID;
import ch.eskaton.asn4j.parser.ast.types.SelectionType;
import ch.eskaton.asn4j.parser.ast.types.SequenceOfType;
import ch.eskaton.asn4j.parser.ast.types.SequenceType;
import ch.eskaton.asn4j.parser.ast.types.SetOfType;
import ch.eskaton.asn4j.parser.ast.types.SetType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.types.UTCTime;
import ch.eskaton.asn4j.parser.ast.types.UsefulType;
import ch.eskaton.asn4j.parser.ast.types.VisibleString;
import ch.eskaton.asn4j.parser.ast.values.BitStringValue;
import ch.eskaton.asn4j.parser.ast.values.DefinedValue;
import ch.eskaton.asn4j.parser.ast.values.ExternalValueReference;
import ch.eskaton.asn4j.parser.ast.values.IRIValue;
import ch.eskaton.asn4j.parser.ast.values.ObjectIdentifierValue;
import ch.eskaton.asn4j.parser.ast.values.RelativeIRIValue;
import ch.eskaton.asn4j.parser.ast.values.RelativeOIDValue;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Tag;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.TagId;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.types.ASN1BitString;
import ch.eskaton.asn4j.runtime.types.ASN1Boolean;
import ch.eskaton.asn4j.runtime.types.ASN1Choice;
import ch.eskaton.asn4j.runtime.types.ASN1EnumeratedType;
import ch.eskaton.asn4j.runtime.types.ASN1GeneralizedTime;
import ch.eskaton.asn4j.runtime.types.ASN1IRI;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1Null;
import ch.eskaton.asn4j.runtime.types.ASN1ObjectIdentifier;
import ch.eskaton.asn4j.runtime.types.ASN1OctetString;
import ch.eskaton.asn4j.runtime.types.ASN1Real;
import ch.eskaton.asn4j.runtime.types.ASN1RelativeIRI;
import ch.eskaton.asn4j.runtime.types.ASN1RelativeOID;
import ch.eskaton.asn4j.runtime.types.ASN1Sequence;
import ch.eskaton.asn4j.runtime.types.ASN1SequenceOf;
import ch.eskaton.asn4j.runtime.types.ASN1Set;
import ch.eskaton.asn4j.runtime.types.ASN1SetOf;
import ch.eskaton.asn4j.runtime.types.ASN1UTCTime;
import ch.eskaton.asn4j.runtime.types.ASN1VisibleString;
import ch.eskaton.commons.collections.Maps;
import ch.eskaton.commons.utils.StringUtils;

import java.io.File;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;

import static ch.eskaton.asn4j.compiler.CompilerUtils.formatName;
import static ch.eskaton.asn4j.parser.NoPosition.NO_POSITION;

public class CompilerContext {

    private HashMap<String, HashMap<String, CompiledType>> definedTypes = new HashMap<>();

    private Map<Class<?>, Compiler<?>> compilers = Maps.<Class<?>, Compiler<?>>builder()
            .put(BitString.class, new BitStringCompiler())
            .put(BooleanType.class, new BooleanCompiler())
            .put(Choice.class, new ChoiceCompiler())
            .put(ComponentType.class, new ComponentTypeCompiler())
            .put(EnumeratedType.class, new EnumeratedTypeCompiler())
            .put(IntegerType.class, new IntegerCompiler())
            .put(Null.class, new NullCompiler())
            .put(OctetString.class, new OctetStringCompiler())
            .put(Real.class, new RealCompiler())
            .put(SequenceType.class, new SequenceCompiler())
            .put(SequenceOfType.class, new SequenceOfCompiler())
            .put(SetType.class, new SetCompiler())
            .put(SetOfType.class, new SetOfCompiler())
            .put(Type.class, new TypeCompiler())
            .put(TypeReference.class, new TypeReferenceCompiler())
            .put(TypeAssignmentNode.class, new TypeAssignmentCompiler())
            .put(VisibleString.class, new VisibleStringCompiler())
            .put(SelectionType.class, new SelectionTypeCompiler())
            .put(ObjectIdentifier.class, new ObjectIdentifierCompiler())
            .put(RelativeOID.class, new RelativeOIDCompiler())
            .put(IRI.class, new IRICompiler())
            .put(RelativeIRI.class, new RelativeIRICompiler())
            .put(GeneralizedTime.class, new GeneralizedTimeCompiler())
            .put(UTCTime.class, new UTCTimeCompiler())
            .build();

    private Map<Class<?>, ValueResolver<?>> valueResolvers = Maps.<Class<?>, ValueResolver<?>>builder()
            .put(BigInteger.class, new IntegerValueResolver(CompilerContext.this))
            .put(BitStringValue.class, new BitStringValueResolver(CompilerContext.this))
            .put(ObjectIdentifierValue.class, new DefaultValueResolver<>(CompilerContext.this, ObjectIdentifier.class, ObjectIdentifierValue.class))
            .put(RelativeOIDValue.class, new DefaultValueResolver<>(CompilerContext.this, RelativeOID.class, RelativeOIDValue.class))
            .put(IRIValue.class, new DefaultValueResolver<>(CompilerContext.this, IRI.class, IRIValue.class))
            .put(RelativeIRIValue.class, new DefaultValueResolver<>(CompilerContext.this, RelativeIRI.class, RelativeIRIValue.class))
            .build();

    @SuppressWarnings("serial")
    private Set<String> builtinTypes = new HashSet<String>() {
        {
            add(BooleanType.class.getSimpleName());
            add(BitString.class.getSimpleName());
            add(IntegerType.class.getSimpleName());
            add(Null.class.getSimpleName());
            add(OctetString.class.getSimpleName());
            add(Real.class.getSimpleName());
            add(VisibleString.class.getSimpleName());
        }
    };

    @SuppressWarnings("serial")
    private Map<String, String> runtimeTypes = Maps.<String, String>builder()
            .put(BooleanType.class.getSimpleName(), ASN1Boolean.class.getSimpleName())
            .put(BitString.class.getSimpleName(), ASN1BitString.class.getSimpleName())
            .put(Choice.class.getSimpleName(), ASN1Choice.class.getSimpleName())
            .put(EnumeratedType.class.getSimpleName(), ASN1EnumeratedType.class.getSimpleName())
            .put(GeneralizedTime.class.getSimpleName(), ASN1GeneralizedTime.class.getSimpleName())
            .put(UTCTime.class.getSimpleName(), ASN1UTCTime.class.getSimpleName())
            .put(IntegerType.class.getSimpleName(), ASN1Integer.class.getSimpleName())
            .put(Null.class.getSimpleName(), ASN1Null.class.getSimpleName())
            .put(ObjectIdentifier.class.getSimpleName(), ASN1ObjectIdentifier.class.getSimpleName())
            .put(RelativeOID.class.getSimpleName(), ASN1RelativeOID.class.getSimpleName())
            .put(IRI.class.getSimpleName(), ASN1IRI.class.getSimpleName())
            .put(RelativeIRI.class.getSimpleName(), ASN1RelativeIRI.class.getSimpleName())
            .put(OctetString.class.getSimpleName(), ASN1OctetString.class.getSimpleName())
            .put(SequenceType.class.getSimpleName(), ASN1Sequence.class.getSimpleName())
            .put(SequenceOfType.class.getSimpleName(), ASN1SequenceOf.class.getSimpleName())
            .put(SetType.class.getSimpleName(), ASN1Set.class.getSimpleName())
            .put(SetOfType.class.getSimpleName(), ASN1SetOf.class.getSimpleName())
            .put(Real.class.getSimpleName(), ASN1Real.class.getSimpleName())
            .put(VisibleString.class.getSimpleName(), ASN1VisibleString.class.getSimpleName())
            .build();

    private ConstraintCompiler constraintCompiler = new ConstraintCompiler(this);

    private DefaultsCompiler defaultsCompiler = new DefaultsCompiler(this);

    private Stack<JavaClass> currentClass = new Stack<>();

    private Map<String, JavaStructure> structs = new HashMap<>();

    private Map<String, Set<String>> referencedTypes = new HashMap<>();

    private Map<String, ModuleNode> modules = new HashMap<>();

    private Stack<ModuleNode> currentModule = new Stack<>();

    private CompilerImpl compiler;

    private String pkg;

    private String outputDir;

    public CompilerContext(CompilerImpl compiler, String pkg, String outputDir) {
        this.compiler = compiler;
        this.pkg = pkg;
        this.outputDir = outputDir;
    }

    public void addType(String typeName, CompiledType compiledType) {
        HashMap<String, CompiledType> moduleTypes = getTypesOfCurrentModule();
        moduleTypes.put(typeName, compiledType);
    }

    @SuppressWarnings("unchecked")
    public <T extends Node, C extends Compiler<T>> C getCompiler(Class<T> clazz) throws CompilerException {
        Compiler<?> compiler = compilers.get(clazz);

        if (compiler == null) {
            throw new CompilerException("No compiler for node-type " + clazz.getSimpleName());
        }

        return (C) compiler;
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

        if (currentClass.empty()) {
            structs.put(javaClass.getName(), javaClass);
        } else {
            currentClass.peek().addInnerClass(javaClass);
            javaClass.addModifier(JavaModifier.Static);
        }
    }

    public JavaClass createClass(String name, Type type, boolean constructed) throws CompilerException {
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

    public TypeAssignmentNode getTypeAssignment(TypeReference type) {
        // TODO: what to do if the type isn't known in the current module
        return (TypeAssignmentNode) getModule().getBody().getAssignments(type.getType());
    }

    public Type getBase(Type type) {
        if (type instanceof TypeReference) {
            return getBase((TypeReference) type);
        }

        return type;
    }

    public Type getBase(TypeReference type) {
        // TODO: what to do if the type isn't known in the current module
        while (true) {
            String typeName = type.getType();

            // Check for implicitly defined types
            if (GeneralizedTime.class.getSimpleName().equals(typeName) ||
                    UTCTime.class.getSimpleName().equals(typeName)) {
                return new VisibleString(NO_POSITION);
            }

            TypeAssignmentNode assignment = (TypeAssignmentNode) getModule().getBody().getAssignments(typeName);

            if (assignment == null) {
                throw new CompilerException(type.getPosition(), "Failed to resolve a type: " + typeName);
            }

            Type base = assignment.getType();

            if (base instanceof TypeReference) {
                type = (TypeReference) base;
            } else {
                return base;
            }
        }
    }

    public String getTypeName(Type type) throws CompilerException {
        String typeName;
        String name = null;

        if (type instanceof NamedType) {
            name = ((NamedType) type).getName();
            type = ((NamedType) type).getType();
        }

        if (type instanceof TypeReference) {
            if (type instanceof UsefulType) {
                typeName = ((UsefulType) type).getType();
            } else {
                typeName = CompilerUtils.formatTypeName(((TypeReference) type).getType());
                addReferencedType(typeName);
            }
        } else if (type instanceof Null) {
            typeName = ASN1Null.class.getSimpleName();
        } else if (type instanceof BooleanType) {
            typeName = ASN1Boolean.class.getSimpleName();
        } else if (type instanceof VisibleString) {
            typeName = ASN1VisibleString.class.getSimpleName();
        } else if (type instanceof OctetString) {
            typeName = ASN1OctetString.class.getSimpleName();
        } else if (type instanceof EnumeratedType) {
            typeName = ASN1EnumeratedType.class.getSimpleName();
        } else if (type instanceof IntegerType) {
            typeName = defineType(type, name, ((IntegerType) type).getNamedNumbers() != null);
        } else if (type instanceof BitString) {
            typeName = defineType(type, name, ((BitString) type).getNamedBits() != null);
        } else if (type instanceof SequenceType
                || type instanceof SequenceOfType
                || type instanceof SetType
                || type instanceof SetOfType
                || type instanceof Choice) {
            typeName = defineType(type, name, true);
        } else if (type instanceof ObjectIdentifier
                || type instanceof RelativeOID
                || type instanceof IRI
                || type instanceof RelativeIRI) {
            typeName = defineType(type, name);
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

    private String defineType(Type type, String name) {
        return defineType(type, name, false);
    }

    private String defineType(Type type, String name, boolean newType) {
        String typeName;

        if (newType && name != null) {
            typeName = CompilerUtils.formatTypeName(name);
            compileType(type, typeName, name);
        } else {
            String runtimeClass = runtimeTypes.get(type.getClass().getSimpleName());

            if (runtimeClass == null) {
                throw new CompilerException("No runtime class available for type " + type);
            }

            typeName = runtimeClass;
        }

        return typeName;
    }

    private void compileType(Type type, String typeName, String name) {
        CompiledType compiledType = this.<Type, TypeCompiler>getCompiler(Type.class).compile(this, typeName, type);

        addType(name, compiledType);
    }

    public void addReferencedType(String typeName) {
        String moduleName = getModule().getModuleId().getModuleName();

        if (!referencedTypes.containsKey(moduleName)) {
            referencedTypes.put(moduleName, new HashSet<>());
        }

        referencedTypes.get(moduleName).add(typeName);
    }

    public TypeAssignmentNode getTypeAssignment(String typeName, String moduleName) {
        ModuleNode module;

        if (moduleName != null) {
            if (!modules.containsKey(moduleName)) {
                return null;
            }

            module = modules.get(moduleName);
        } else {
            module = getModule();
        }

        for (AssignmentNode assignment : module.getBody().getAssignments()) {
            if (assignment instanceof TypeAssignmentNode) {
                if (typeName.equals(assignment.getReference())) {
                    return (TypeAssignmentNode) assignment;
                }
            }
        }

        return null;
    }

    public boolean isBuiltin(String name) {
        return builtinTypes.contains(name);
    }

    public <T> T resolveValue(Class<T> valueClass, Type type, T value) {
        return getValueResolver(valueClass).resolve(type, value);
    }

    public <T> T resolveValue(Class<T> valueClass, DefinedValue ref) {
        return getValueResolver(valueClass).resolve(ref);
    }

    public <T> T resolveValue(Class<T> valueClass, String ref) {
        return getValueResolver(valueClass).resolve(ref);
    }

    public <T> T resolveGenericValue(Class<T> valueClass, Type type, Value value) {
        return getValueResolver(valueClass).resolveGeneric(type, value);
    }

    private <T> ValueResolver<T> getValueResolver(Class<T> valueClass) {
        ValueResolver<T> valueResolver = (ValueResolver<T>) valueResolvers.get(valueClass);

        if (valueResolver == null) {
            throw new CompilerException("No value resolver defined for type " + valueClass.getSimpleName());
        }

        return valueResolver;
    }

    public Node resolveTypeReference(Node type) {
        while (type instanceof TypeReference) {
            TypeAssignmentNode assignment = getTypeAssignment(((TypeReference) type).getType());

            if (assignment != null) {
                type = assignment.getType();
            } else {
                throw new CompilerException("Failed to resolve reference to " + ((TypeReference) type).getType());
            }
        }

        return type;
    }

    private TypeAssignmentNode getTypeAssignment(String type) {
        return getTypeAssignment(type, null);
    }

    public ValueOrObjectAssignmentNode<?, ?> resolveDefinedValue(DefinedValue ref) throws CompilerException {
        if (ref instanceof ExternalValueReference) {
            throw new CompilerException("External references not yet supported");
            // TODO: external references
        } else {
            return resolveReference(((SimpleDefinedValue) ref).getValue());
        }
    }

    public ValueOrObjectAssignmentNode<?, ?> resolveReference(String reference) {
        AssignmentNode assignment = getModule().getBody().getAssignments(reference);

        if (!(assignment instanceof ValueOrObjectAssignmentNode)) {
            throw new CompilerException("Failed to resolve reference " + reference);
        }

        return (ValueOrObjectAssignmentNode<?, ?>) assignment;
    }

    public void writeClasses() throws CompilerException {
        String pkgDir = pkg.replace('.', File.separatorChar);

        File pkgFile = new File(StringUtils.concat(outputDir, File.separator, pkgDir));

        if (!pkgFile.exists()) {
            if (!pkgFile.mkdirs()) {
                throw new CompilerException("Failed to create directory " + pkgFile);
            }
        }

        new JavaWriter().write(structs, outputDir);
    }

    public ConstraintDefinition compileConstraint(JavaClass javaClass, String name, Type node) throws CompilerException {
        return constraintCompiler.compileConstraint(javaClass, name, node);
    }

    public void compileDefault(JavaClass javaClass, String field, String typeName, Type type, Value value)
            throws CompilerException {
        defaultsCompiler.compileDefault(javaClass, field, typeName, type, value);
    }

    public Type resolveType(Type type) {
        if (type instanceof SelectionType) {
            String selectedId = ((SelectionType) type).getId();
            Type selectedType = ((SelectionType) type).getType();

            if (selectedType instanceof TypeReference) {
                Object assignment = getModule().getBody().getAssignments(((TypeReference) selectedType).getType());

                if (assignment instanceof TypeAssignmentNode) {
                    Type collectionType = ((TypeAssignmentNode) assignment).getType();

                    if (collectionType instanceof Choice) {
                        return ((Choice) collectionType).getRootTypeList().stream()
                                .filter(t -> t.getName().equals(selectedId))
                                .findFirst().orElseThrow(() -> new CompilerException("Selected type not found")).getType();
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

    public Optional<CompiledType> getCompiledType(Type type, boolean isSubType) {
        if (type instanceof TypeReference) {
            HashMap<String, CompiledType> moduleTypes = getTypesOfCurrentModule();
            CompiledType compiledType = moduleTypes.get(((TypeReference) type).getType());

            if (compiledType == null) {
                if (!isSubType) {
                    Stack<JavaClass> oldClass = currentClass;
                    currentClass = new Stack<>();

                    compiledType = compiler.compileType(((TypeReference) type).getType());

                    currentClass = oldClass;
                } else {
                    compiledType = compiler.compileType(((TypeReference) type).getType());
                }
            }

            return Optional.ofNullable(compiledType);
        }

        return Optional.empty();
    }

    public Optional<CompiledType> getCompiledType(Type type) {
        return getCompiledType(type, false);
    }

    private HashMap<String, CompiledType> getTypesOfCurrentModule() {
        return definedTypes.computeIfAbsent(currentModule.peek().getModuleId().getModuleName(), key -> new HashMap<>());
    }

}
