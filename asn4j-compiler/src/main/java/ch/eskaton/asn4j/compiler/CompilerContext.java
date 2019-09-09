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
import ch.eskaton.asn4j.compiler.java.JavaWriter;
import ch.eskaton.asn4j.compiler.java.objs.JavaClass;
import ch.eskaton.asn4j.compiler.java.objs.JavaModifier;
import ch.eskaton.asn4j.compiler.java.objs.JavaStructure;
import ch.eskaton.asn4j.compiler.resolvers.BitStringValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.BooleanValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.CollectionOfValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.DefaultValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.EnumeratedValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.IntegerValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.OctetStringValueResolver;
import ch.eskaton.asn4j.compiler.resolvers.ValueResolver;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ParserException;
import ch.eskaton.asn4j.parser.ast.AssignmentNode;
import ch.eskaton.asn4j.parser.ast.ExportsNode;
import ch.eskaton.asn4j.parser.ast.ImportNode;
import ch.eskaton.asn4j.parser.ast.ModuleNode;
import ch.eskaton.asn4j.parser.ast.ModuleRefNode;
import ch.eskaton.asn4j.parser.ast.Node;
import ch.eskaton.asn4j.parser.ast.ReferenceNode;
import ch.eskaton.asn4j.parser.ast.TypeAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ValueOrObjectAssignmentNode;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.BooleanType;
import ch.eskaton.asn4j.parser.ast.types.Choice;
import ch.eskaton.asn4j.parser.ast.types.ClassType;
import ch.eskaton.asn4j.parser.ast.types.ComponentType;
import ch.eskaton.asn4j.parser.ast.types.EnumeratedType;
import ch.eskaton.asn4j.parser.ast.types.ExternalTypeReference;
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
import ch.eskaton.asn4j.parser.ast.values.BooleanValue;
import ch.eskaton.asn4j.parser.ast.values.ChoiceValue;
import ch.eskaton.asn4j.parser.ast.values.CollectionOfValue;
import ch.eskaton.asn4j.parser.ast.values.CollectionValue;
import ch.eskaton.asn4j.parser.ast.values.DefinedValue;
import ch.eskaton.asn4j.parser.ast.values.EnumeratedValue;
import ch.eskaton.asn4j.parser.ast.values.ExternalValueReference;
import ch.eskaton.asn4j.parser.ast.values.IRIValue;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.NullValue;
import ch.eskaton.asn4j.parser.ast.values.ObjectIdentifierValue;
import ch.eskaton.asn4j.parser.ast.values.OctetStringValue;
import ch.eskaton.asn4j.parser.ast.values.RealValue;
import ch.eskaton.asn4j.parser.ast.values.RelativeIRIValue;
import ch.eskaton.asn4j.parser.ast.values.RelativeOIDValue;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.StringValue;
import ch.eskaton.asn4j.parser.ast.values.Tag;
import ch.eskaton.asn4j.parser.ast.values.TimeValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.TagId;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.exceptions.ASN1RuntimeException;
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
import ch.eskaton.commons.collections.Sets;
import ch.eskaton.commons.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static ch.eskaton.asn4j.compiler.CompilerUtils.formatName;
import static ch.eskaton.asn4j.compiler.CompilerUtils.resolveAmbiguousValue;
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
            .put(ExternalTypeReference.class, new ExternalTypeReferenceCompiler())
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
            .put(BooleanValue.class, new BooleanValueResolver(CompilerContext.this))
            .put(IntegerValue.class, new IntegerValueResolver(CompilerContext.this))
            .put(Integer.class, new EnumeratedValueResolver(CompilerContext.this))
            .put(BitStringValue.class, new BitStringValueResolver(CompilerContext.this))
            .put(OctetStringValue.class, new OctetStringValueResolver(CompilerContext.this))
            .put(ObjectIdentifierValue.class, new DefaultValueResolver<>(CompilerContext.this, ObjectIdentifier.class,
                    ObjectIdentifierValue.class))
            .put(RelativeOIDValue.class, new DefaultValueResolver<>(CompilerContext.this, RelativeOID.class,
                    RelativeOIDValue.class))
            .put(IRIValue.class, new DefaultValueResolver<>(CompilerContext.this, IRI.class, IRIValue.class))
            .put(RelativeIRIValue.class, new DefaultValueResolver<>(CompilerContext.this, RelativeIRI.class,
                    RelativeIRIValue.class))
            .put(CollectionOfValue.class, new CollectionOfValueResolver(CompilerContext.this))
            .build();

    @SuppressWarnings("serial")
    private Set<String> builtinTypes = Sets.<String>builder()
            .add(BooleanType.class.getSimpleName())
            .add(BitString.class.getSimpleName())
            .add(IntegerType.class.getSimpleName())
            .add(Null.class.getSimpleName())
            .add(OctetString.class.getSimpleName())
            .add(Real.class.getSimpleName())
            .add(VisibleString.class.getSimpleName())
            .build();

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

    private Map<Class<? extends Type>, Class<? extends Value>> types2values =
            Maps.<Class<? extends Type>, Class<? extends Value>>builder()
                    .put(BooleanType.class, BooleanValue.class)
                    .put(BitString.class, BitStringValue.class)
                    .put(Choice.class, ChoiceValue.class)
                    .put(EnumeratedType.class, EnumeratedValue.class)
                    .put(GeneralizedTime.class, TimeValue.class)
                    .put(UTCTime.class, TimeValue.class)
                    .put(IntegerType.class, IntegerValue.class)
                    .put(Null.class, NullValue.class)
                    .put(ObjectIdentifier.class, ObjectIdentifierValue.class)
                    .put(RelativeOID.class, RelativeOIDValue.class)
                    .put(IRI.class, IRIValue.class)
                    .put(RelativeIRI.class, RelativeIRIValue.class)
                    .put(OctetString.class, OctetStringValue.class)
                    .put(SequenceType.class, CollectionOfValue.class)
                    .put(SequenceOfType.class, CollectionOfValue.class)
                    .put(SetType.class, CollectionValue.class)
                    .put(SetOfType.class, CollectionOfValue.class)
                    .put(Real.class, RealValue.class)
                    .put(VisibleString.class, StringValue.class)
                    .build();

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

    public void addType(String typeName, CompiledType compiledType) {
        HashMap<String, CompiledType> moduleTypes = getTypesOfCurrentModule();
        moduleTypes.put(typeName, compiledType);
    }

    @SuppressWarnings("unchecked")
    public <T extends Node, C extends Compiler<T>> C getCompiler(Class<T> clazz) {
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

        if (currentClass.isEmpty()) {
            structs.put(javaClass.getName(), javaClass);
        } else {
            currentClass.peek().addInnerClass(javaClass);
            javaClass.addModifier(JavaModifier.Static);
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

    public String getTypeName(Type type) {
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
            }
        } else if (type instanceof ExternalTypeReference) {
            typeName = CompilerUtils.formatTypeName(((ExternalTypeReference) type).getType());
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
            if (assignment instanceof TypeAssignmentNode && typeName.equals(assignment.getReference())) {
                return (TypeAssignmentNode) assignment;
            }
        }

        return null;
    }

    public Class<? extends Value> getValueType(Class<? extends Type> typeClass) {
        Class<? extends Value> valueClass = types2values.get(typeClass);

        if (valueClass == null) {
            throw new ASN1RuntimeException(String.format("Value class for type %s not defined", typeClass.getSimpleName()));
        }

        return valueClass;
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

    public ValueOrObjectAssignmentNode resolveDefinedValue(DefinedValue ref) {
        if (ref instanceof ExternalValueReference) {
            return resolveExternalReference(((ExternalValueReference) ref));
        } else {
            return resolveReference(((SimpleDefinedValue) ref));
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

        AssignmentNode assignment = modules.get(moduleName).getBody().getAssignments(symbolName);

        if (!(assignment instanceof ValueOrObjectAssignmentNode)) {
            throw new CompilerException("Failed to resolve reference " + reference);
        }

        return (ValueOrObjectAssignmentNode<?, ?>) assignment;
    }

    public Optional<ValueOrObjectAssignmentNode> tryResolveAllReferences(SimpleDefinedValue reference) {
        Optional<ValueOrObjectAssignmentNode> assignment = tryResolveReference(reference);
        Optional<ValueOrObjectAssignmentNode> tmpAssignment = assignment;

        while (tmpAssignment.isPresent()) {
            assignment = tmpAssignment;

            reference = resolveAmbiguousValue(tmpAssignment.get().getValue(), SimpleDefinedValue.class);

            if (reference == null) {
                break;
            }

            tmpAssignment = tryResolveReference(reference);

            if (tmpAssignment.equals(assignment)) {
                break;
            }
        }

        return assignment;
    }

    public Optional<ValueOrObjectAssignmentNode> tryResolveReference(SimpleDefinedValue reference) {
        return tryResolveReference(reference.getValue());
    }

    public Optional<ValueOrObjectAssignmentNode> tryResolveReference(String symbolName) {
        AssignmentNode assignment = getModule().getBody().getAssignments(symbolName);

        if (assignment == null) {
            Optional<ImportNode> imp = getImport(symbolName);

            if (imp.isPresent()) {
                ModuleRefNode moduleRef = imp.get().getReference();
                String moduleName = moduleRef.getName();
                ModuleNode module = getModule(moduleName);

                ensureSymbolIsExported(module, symbolName);

                assignment = modules.get(moduleName).getBody().getAssignments(symbolName);
            }
        }

        if (assignment instanceof ValueOrObjectAssignmentNode) {
            return Optional.of((ValueOrObjectAssignmentNode) assignment);
        }

        return Optional.empty();
    }

    public ValueOrObjectAssignmentNode resolveReference(SimpleDefinedValue reference) {
        return resolveReference(reference.getValue());
    }

    public ValueOrObjectAssignmentNode resolveReference(String symbolName) {
        Optional<ValueOrObjectAssignmentNode> assignment = tryResolveReference(symbolName);

        if (!assignment.isPresent()) {
            throw new CompilerException("Failed to resolve reference " + symbolName);
        }

        return assignment.get();
    }

    public void ensureSymbolIsExported(ModuleNode module, String symbolName) {
        if (!isSymbolExported(module, symbolName)) {
            String format = "Module %s uses the symbol %s from module %s which the latter doesn't export";
            throw new CompilerException(format, currentModule.peek().getModuleId().getModuleName(), symbolName,
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

    public ConstraintDefinition compileConstraint(JavaClass javaClass, String name, Type node) {
        return constraintCompiler.compileConstraint(javaClass, name, node);
    }

    public void compileDefault(JavaClass javaClass, String field, String typeName, Type type, Value value) {
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
        if (type instanceof TypeReference) {
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
                        throw new CompilerException(format, currentModule.peek().getModuleId().getModuleName(),
                                typeName, moduleName);
                    }

                    moduleTypes = definedTypes.get(moduleName);
                    compiledType = Optional.ofNullable(moduleTypes.get(typeName));
                }
            }

            if (!compiledType.isPresent()) {
                throw new CompilerException("Failed to resolve type %s in module %s ", typeName,
                        currentModule.peek().getModuleId().getModuleName());
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

    public CompiledType getCompiledType(Type type) {
        return getCompiledType(type, false).orElse(new CompiledType(type));
    }

    public CompiledType getCompiledBaseType(Type node) {
        CompiledType compiledType;

        do {
            compiledType = getCompiledType(node);

            node = compiledType.getType();
        } while (node instanceof TypeReference);

        return compiledType;
    }

    private HashMap<String, CompiledType> getTypesOfCurrentModule() {
        return definedTypes.computeIfAbsent(currentModule.peek().getModuleId().getModuleName(), key -> new HashMap<>());
    }

}
