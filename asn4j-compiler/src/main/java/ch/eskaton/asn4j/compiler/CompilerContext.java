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
import ch.eskaton.asn4j.compiler.defaults.DefaultsCompiler;
import ch.eskaton.asn4j.compiler.java.JavaClass;
import ch.eskaton.asn4j.compiler.java.JavaModifier;
import ch.eskaton.asn4j.compiler.java.JavaStructure;
import ch.eskaton.asn4j.compiler.java.JavaWriter;
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
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.asn4j.parser.ast.types.Null;
import ch.eskaton.asn4j.parser.ast.types.ObjectIdentifier;
import ch.eskaton.asn4j.parser.ast.types.OctetString;
import ch.eskaton.asn4j.parser.ast.types.Real;
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
import ch.eskaton.asn4j.parser.ast.values.DefinedValue;
import ch.eskaton.asn4j.parser.ast.values.ExternalValueReference;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.NamedNumber;
import ch.eskaton.asn4j.parser.ast.values.ObjectIdentifierValue;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Tag;
import ch.eskaton.asn4j.runtime.TagId;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.types.ASN1BitString;
import ch.eskaton.asn4j.runtime.types.ASN1Boolean;
import ch.eskaton.asn4j.runtime.types.ASN1Choice;
import ch.eskaton.asn4j.runtime.types.ASN1EnumeratedType;
import ch.eskaton.asn4j.runtime.types.ASN1GeneralizedTime;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1Null;
import ch.eskaton.asn4j.runtime.types.ASN1ObjectIdentifier;
import ch.eskaton.asn4j.runtime.types.ASN1OctetString;
import ch.eskaton.asn4j.runtime.types.ASN1Real;
import ch.eskaton.asn4j.runtime.types.ASN1Sequence;
import ch.eskaton.asn4j.runtime.types.ASN1SequenceOf;
import ch.eskaton.asn4j.runtime.types.ASN1Set;
import ch.eskaton.asn4j.runtime.types.ASN1SetOf;
import ch.eskaton.asn4j.runtime.types.ASN1VisibleString;
import ch.eskaton.commons.utils.StringUtils;

import java.io.File;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import static ch.eskaton.asn4j.compiler.CompilerUtils.formatName;
import static ch.eskaton.asn4j.compiler.CompilerUtils.resolveAmbiguousValue;

public class CompilerContext {

    private HashMap<String, HashMap<String, Type>> definedTypes = new HashMap<>();

    @SuppressWarnings("serial")
    private Map<Class<?>, Compiler<?>> compilers = new HashMap<Class<?>, Compiler<?>>() {
        {
            put(BitString.class, new BitStringCompiler());
            put(BooleanType.class, new BooleanCompiler());
            put(Choice.class, new ChoiceCompiler());
            put(ComponentType.class, new ComponentTypeCompiler());
            put(EnumeratedType.class, new EnumeratedTypeCompiler());
            put(IntegerType.class, new IntegerCompiler());
            put(Null.class, new NullCompiler());
            put(OctetString.class, new OctetStringCompiler());
            put(Real.class, new RealCompiler());
            put(SequenceType.class, new SequenceCompiler());
            put(SequenceOfType.class, new SequenceOfCompiler());
            put(SetType.class, new SetCompiler());
            put(SetOfType.class, new SetOfCompiler());
            put(Type.class, new TypeCompiler());
            put(TypeReference.class, new TypeReferenceCompiler());
            put(TypeAssignmentNode.class, new TypeAssignmentCompiler());
            put(VisibleString.class, new VisibleStringCompiler());
            put(SelectionType.class, new SelectionTypeCompiler());
            put(ObjectIdentifier.class, new ObjectIdentifierCompiler());
        }
    };

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
    private Map<String, String> runtimeTypes = new HashMap<String, String>() {
        {
            put(BooleanType.class.getSimpleName(), ASN1Boolean.class.getSimpleName());
            put(BitString.class.getSimpleName(), ASN1BitString.class.getSimpleName());
            put(Choice.class.getSimpleName(), ASN1Choice.class.getSimpleName());
            put(EnumeratedType.class.getSimpleName(), ASN1EnumeratedType.class.getSimpleName());
            put(GeneralizedTime.class.getSimpleName(), ASN1GeneralizedTime.class.getSimpleName());
            put(IntegerType.class.getSimpleName(), ASN1Integer.class.getSimpleName());
            put(Null.class.getSimpleName(), ASN1Null.class.getSimpleName());
            put(ObjectIdentifier.class.getSimpleName(), ASN1ObjectIdentifier.class.getSimpleName());
            put(OctetString.class.getSimpleName(), ASN1OctetString.class.getSimpleName());
            put(SequenceType.class.getSimpleName(), ASN1Sequence.class.getSimpleName());
            put(SequenceOfType.class.getSimpleName(), ASN1SequenceOf.class.getSimpleName());
            put(SetType.class.getSimpleName(), ASN1Set.class.getSimpleName());
            put(SetOfType.class.getSimpleName(), ASN1SetOf.class.getSimpleName());
            put(Real.class.getSimpleName(), ASN1Real.class.getSimpleName());
            put(VisibleString.class.getSimpleName(), ASN1VisibleString.class.getSimpleName());
        }
    };

    private TypeResolver typeResolver = new TypeResolver() {
        public TypeAssignmentNode getType(String type) {
            return CompilerContext.this.getTypeName(type);
        }

        public Type getBase(String type) {
            return CompilerContext.this.getBase(type);
        }
    };

    private ConstraintCompiler constraintCompiler = new ConstraintCompiler(typeResolver);

    private DefaultsCompiler defaultsCompiler = new DefaultsCompiler(this, typeResolver);

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

    public void addType(String typeName, Type typeDef) {
        String moduleName = currentModule.peek().getModuleId().getModuleName();
        HashMap<String, Type> moduleTypes = definedTypes.computeIfAbsent(moduleName, key -> new HashMap<>());
        moduleTypes.put(typeName, typeDef);
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
        Tag tag = type.getTag();

        if (tag != null && ClassType.UNIVERSAL.equals(tag.getClazz())) {
            throw new CompilerException("UNIVERSAL class not allowed in type " + name);
        }

        JavaClass javaClass = new JavaClass(pkg, name, tag, CompilerUtils
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

    public TypeAssignmentNode getTypeName(String type) {
        // TODO: what to do if the type isn't known in the current module
        return (TypeAssignmentNode) getModule().getBody().getAssignments(type);
    }

    public Type getBase(String type) {
        // TODO: what to do if the type isn't known in the current module
        while (true) {
            // Check for implicitly defined types
            if (GeneralizedTime.class.getSimpleName().equals(type) || UTCTime.class.getSimpleName().equals(type)) {
                return new VisibleString();
            }

            TypeAssignmentNode assignment = (TypeAssignmentNode) getModule().getBody().getAssignments(type);
            Type base = assignment.getType();

            if (base instanceof TypeReference) {
                type = ((TypeReference) base).getType();
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
                String asn1TypeName = ((TypeReference) type).getType();
                addReferencedType(asn1TypeName);
                typeName = formatName(asn1TypeName);
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
                || type instanceof Choice
                || type instanceof ObjectIdentifier) {
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
        return defineType(type, name, true);
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
        this.<Type, TypeCompiler>getCompiler(Type.class).compile(this, typeName, type);
        addType(name, type);
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

    public ObjectIdentifierValue resolveObjectIdentifierValue(DefinedValue ref) throws CompilerException {
        return resolveObjectIdentifierValue(resolveDefinedValue(ref));
    }

    public ObjectIdentifierValue resolveObjectIdentifierValue(String ref) throws CompilerException {
        return resolveObjectIdentifierValue(resolveReference(ref));
    }

    public ObjectIdentifierValue resolveObjectIdentifierValue(ValueOrObjectAssignmentNode<?, ?> valueAssignment)
            throws CompilerException {
        Node type = valueAssignment.getType();
        Node value = valueAssignment.getValue();

        type = resolveTypeReference(type);

        if (type instanceof ObjectIdentifier) {
            value = CompilerUtils.resolveAmbiguousValue(value, ObjectIdentifierValue.class);

            if (!(value instanceof ObjectIdentifierValue)) {
                throw new CompilerException("Object identifier expected");
            }

            return ((ObjectIdentifierValue) value);
        }

        throw new CompilerException("Failed to resolve an object identifier value");
    }

    public Node resolveTypeReference(Node type) {
        while (type instanceof TypeReference) {
            TypeAssignmentNode assignment = getTypeAssignment(((TypeReference) type).getType());

            if (assignment != null) {
                type = assignment.getType();
            }
        }

        return type;
    }

    private TypeAssignmentNode getTypeAssignment(String type) {
        return getTypeAssignment(type, null);
    }

    public BigInteger resolveIntegerValue(DefinedValue ref) throws CompilerException {
        return resolveIntegerValue(resolveDefinedValue(ref));
    }

    public BigInteger resolveIntegerValue(String ref) throws CompilerException {
        return resolveIntegerValue(resolveReference(ref));
    }

    private BigInteger resolveIntegerValue(ValueOrObjectAssignmentNode<?, ?> valueAssignment) {
        Node type = valueAssignment.getType();
        Node value = valueAssignment.getValue();

        if (type instanceof IntegerType) {
            if (!(value instanceof IntegerValue)) {
                throw new CompilerException("Integer expected");
            }
            return ((IntegerValue) value).getValue();
        } else if (type instanceof TypeReference) {
            if (resolveAmbiguousValue(value, SimpleDefinedValue.class) != null) {
                value = resolveAmbiguousValue(value, SimpleDefinedValue.class);
                AssignmentNode assignment = getModule().getBody().getAssignments(((TypeReference) type).getType());

                if (assignment != null && assignment instanceof TypeAssignmentNode) {
                    TypeAssignmentNode typeAssignment = (TypeAssignmentNode) assignment;

                    if (typeAssignment.getType() instanceof IntegerType) {
                        NamedNumber namedNumber = ((IntegerType) typeAssignment.getType())
                                .getNamedNumber(((SimpleDefinedValue) value).getValue());

                        if (namedNumber.getValue() != null) {
                            return namedNumber.getValue().getNumber();
                        } else {
                            return resolveIntegerValue(namedNumber.getRef());
                        }
                    }
                }
            } else if (value instanceof IntegerValue) {
                return ((IntegerValue) value).getValue();
            }
        }

        throw new CompilerException("Failed to resolve an integer value");
    }

    public ValueOrObjectAssignmentNode<?, ?> resolveDefinedValue(DefinedValue ref) throws CompilerException {
        if (ref instanceof ExternalValueReference) {
            throw new CompilerException("External references not yet supported");
            // TODO: external references
        } else {
            return resolveReference(((SimpleDefinedValue) ref).getValue());
        }
    }

    private ValueOrObjectAssignmentNode<?, ?> resolveReference(String reference) {
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

    public void compileConstraint(JavaClass javaClass, String name, Type node) throws CompilerException {
        constraintCompiler.compileConstraint(javaClass, name, node);
    }

    public void compileDefault(JavaClass javaClass, String typeName, String field, ComponentType component)
            throws CompilerException {
        defaultsCompiler.compileDefault(javaClass, typeName, field, component);
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
        if (type instanceof TypeReference) {
            HashMap<String, Type> moduleTypes = getTypesOfCurrentModule();

            Type referencedType = moduleTypes.computeIfAbsent(((TypeReference) type).getType(), compiler::compileType);
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

    private HashMap<String, Type> getTypesOfCurrentModule() {
        return definedTypes.computeIfAbsent(currentModule.peek().getModuleId().getModuleName(), key -> new HashMap<>());
    }

}
