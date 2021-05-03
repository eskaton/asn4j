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

import ch.eskaton.asn4j.compiler.constraints.AbstractConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.ConstraintCompiler;
import ch.eskaton.asn4j.compiler.constraints.ConstraintDefinition;
import ch.eskaton.asn4j.compiler.defaults.AbstractDefaultCompiler;
import ch.eskaton.asn4j.compiler.defaults.DefaultsCompiler;
import ch.eskaton.asn4j.compiler.il.BooleanExpression;
import ch.eskaton.asn4j.compiler.il.Module;
import ch.eskaton.asn4j.compiler.java.objs.JavaClass;
import ch.eskaton.asn4j.compiler.objects.ObjectNodeCompiler;
import ch.eskaton.asn4j.compiler.objects.ObjectSetCompiler;
import ch.eskaton.asn4j.compiler.parameters.ParameterGovernorHelper;
import ch.eskaton.asn4j.compiler.parameters.ParameterPredicates;
import ch.eskaton.asn4j.compiler.parameters.Parameters;
import ch.eskaton.asn4j.compiler.parameters.ParametersHelper;
import ch.eskaton.asn4j.compiler.results.CompilationResult;
import ch.eskaton.asn4j.compiler.results.CompiledBuiltinType;
import ch.eskaton.asn4j.compiler.results.CompiledChoiceType;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionType;
import ch.eskaton.asn4j.compiler.results.CompiledModule;
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
import ch.eskaton.asn4j.compiler.results.HasChildComponents;
import ch.eskaton.asn4j.compiler.results.UnNamedCompiledValue;
import ch.eskaton.asn4j.compiler.types.EnumeratedTypeCompiler;
import ch.eskaton.asn4j.compiler.types.SelectionTypeCompiler;
import ch.eskaton.asn4j.compiler.types.formatters.TypeFormatter;
import ch.eskaton.asn4j.compiler.values.AbstractValueCompiler;
import ch.eskaton.asn4j.compiler.values.ValueCompiler;
import ch.eskaton.asn4j.compiler.values.ValueResolutionException;
import ch.eskaton.asn4j.parser.ParserException;
import ch.eskaton.asn4j.parser.ast.ActualParameter;
import ch.eskaton.asn4j.parser.ast.ElementSetSpecsNode;
import ch.eskaton.asn4j.parser.ast.ExportsNode;
import ch.eskaton.asn4j.parser.ast.ExternalObjectReference;
import ch.eskaton.asn4j.parser.ast.ExternalObjectSetReference;
import ch.eskaton.asn4j.parser.ast.ImportNode;
import ch.eskaton.asn4j.parser.ast.ModuleNode;
import ch.eskaton.asn4j.parser.ast.Node;
import ch.eskaton.asn4j.parser.ast.ObjectClassNode;
import ch.eskaton.asn4j.parser.ast.ObjectClassReference;
import ch.eskaton.asn4j.parser.ast.ObjectNode;
import ch.eskaton.asn4j.parser.ast.ObjectReference;
import ch.eskaton.asn4j.parser.ast.ObjectSetReference;
import ch.eskaton.asn4j.parser.ast.ObjectSetSpecNode;
import ch.eskaton.asn4j.parser.ast.ParameterNode;
import ch.eskaton.asn4j.parser.ast.ReferenceNode;
import ch.eskaton.asn4j.parser.ast.constraints.Constraint;
import ch.eskaton.asn4j.parser.ast.types.BitString;
import ch.eskaton.asn4j.parser.ast.types.CollectionOfType;
import ch.eskaton.asn4j.parser.ast.types.EnumeratedType;
import ch.eskaton.asn4j.parser.ast.types.ExternalTypeReference;
import ch.eskaton.asn4j.parser.ast.types.HasModuleName;
import ch.eskaton.asn4j.parser.ast.types.IntegerType;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.asn4j.parser.ast.types.SelectionType;
import ch.eskaton.asn4j.parser.ast.types.SequenceType;
import ch.eskaton.asn4j.parser.ast.types.SetType;
import ch.eskaton.asn4j.parser.ast.types.SimpleDefinedType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeFromObject;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.types.UsefulType;
import ch.eskaton.asn4j.parser.ast.values.DefinedValue;
import ch.eskaton.asn4j.parser.ast.values.ExternalValueReference;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.commons.collections.Tuple2;

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
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static ch.eskaton.commons.utils.ReflectionUtils.getInstance;

public class CompilerContext {

    private final HashMap<String, CompiledModule> definedModules = new HashMap<>();

    private final TypeConfiguration config = new TypeConfiguration(this);

    private final ConstraintCompiler constraintCompiler = new ConstraintCompiler(this);

    private final DefaultsCompiler defaultsCompiler = new DefaultsCompiler(this);

    private final Map<String, ModuleNode> modules = new HashMap<>();

    private final Deque<ModuleNode> currentModule = new LinkedList<>();

    private Deque<Type> currentType = new LinkedList<>();

    private CompilerImpl compiler;

    public CompilerContext(CompilerImpl compiler) {
        this.compiler = compiler;
    }

    private CompiledModule getCurrentCompiledModule() {
        return getCompiledModule(getCurrentModuleName());
    }

    public CompiledModule getCompiledModule(String moduleName) {
        return definedModules.computeIfAbsent(moduleName, CompiledModule::new);
    }

    public Map<String, CompiledModule> getCompiledModules() {
        return definedModules;
    }

    private void addType(String name, CompiledType compiledType) {
        getCurrentCompiledModule().addType(name, compiledType);
    }

    private <V extends Value> void addValue(String name, CompiledValue<V> compiledValue) {
        getCurrentCompiledModule().addValue(name, compiledValue);
    }

    private void addObjectClass(String name, CompiledObjectClass compiledObjectClass) {
        getCurrentCompiledModule().addObjectClass(name, compiledObjectClass);
    }

    private void addObjectSet(String name, CompiledObjectSet compiledObjectSet) {
        getCurrentCompiledModule().addObjectSet(name, compiledObjectSet);
    }

    private void addObject(String name, CompiledObject compiledObject) {
        getCurrentCompiledModule().addObject(name, compiledObject);
    }

    private void addParameterizedType(String name, CompiledParameterizedType compiledParameterizedType) {
        getCurrentCompiledModule().addParameterizedType(name, compiledParameterizedType);
    }

    private void addParameterizedObjectClass(String name,
            CompiledParameterizedObjectClass compiledParameterizedObjectClass) {
        getCurrentCompiledModule().addParameterizedObjectClass(name, compiledParameterizedObjectClass);
    }

    private void addParameterizedObject(String name,
            CompiledParameterizedObject compiledParameterizedObject) {
        getCurrentCompiledModule().addParameterizedObject(name, compiledParameterizedObject);
    }

    private void addParameterizedObjectSet(String name,
            CompiledParameterizedObjectSet compiledParameterizedObjectSet) {
        getCurrentCompiledModule().addParameterizedObjectSet(name, compiledParameterizedObjectSet);
    }

    private void addParameterizedValueSetType(String name,
            CompiledParameterizedValueSetType compiledParameterizedValueSetType) {
        getCurrentCompiledModule().addParameterizedValueSetType(name, compiledParameterizedValueSetType);
    }

    public <T extends Node, C extends Compiler<T>> C getCompiler(Class<T> clazz) {
        return config.getCompiler(clazz);
    }

    public <T extends Type, V extends Value> AbstractDefaultCompiler<V> getDefaultCompiler(Class<T> clazz) {
        return config.getDefaultCompiler(clazz);
    }

    public <T extends Type, C extends AbstractConstraintCompiler> C getConstraintCompiler(Class<T> clazz) {
        return config.getConstraintCompiler(clazz);
    }

    public <T extends Type, V extends Value, C extends AbstractValueCompiler<V>> C getValueCompiler(Class<T> clazz) {
        return config.getValueCompiler(clazz);
    }

    public ModuleNode getModule() {
        return currentModule.peek();
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

    public String getTypeName(Type type) {
        String name = null;

        if (type instanceof NamedType) {
            name = ((NamedType) type).getName();
            type = ((NamedType) type).getType();
        }

        return getTypeName(type, name);
    }

    public String getTypeName(Type type, String name) {
        return config.getTypeNameSupplier(type.getClass()).getName(type, name);
    }

    public Optional<Type> getTypeParameter(Parameters parameters, SimpleDefinedType definedType) {
        var reference = definedType.getType();

        return ParametersHelper.getTypeParameter(parameters, reference);
    }

    public Class<? extends Value> getValueType(Type type) {
        var typeClass = getTypeClass(type);

        return getValueClass(typeClass);
    }

    private Class<? extends Type> getTypeClass(Type type) {
        if (CompilerUtils.isAnyTypeReference(type)) {
            var compiledType = getCompiledType(type);

            return compiledType.getType().getClass();
        } else {
            return type.getClass();
        }
    }

    public Class<? extends Value> getValueClass(Class<? extends Type> typeClass) {
        return config.getValueClass(typeClass);
    }

    public boolean isBuiltin(Type type) {
        return config.isBuiltin(type.getClass());
    }

    private boolean isBuiltin(Class<? extends Type> typeClass) {
        return config.isBuiltin(typeClass);
    }

    public boolean isRuntimeType(String typeName) {
        return config.isRuntimeType(typeName);
    }

    public void ensureSymbolIsExported(ModuleNode module, String symbolName) {
        if (!isSymbolExported(module, symbolName)) {
            var format = "Module '%s' uses the symbol '%s' from module' %s' which the latter doesn't export";

            throw new CompilerException(format, getCurrentModuleName(), symbolName,
                    module.getModuleId().getModuleName());
        }
    }

    public Optional<BooleanExpression> buildExpression(Module module, CompiledType compiledType,
            ch.eskaton.asn4j.compiler.constraints.ast.Node node) {
        return constraintCompiler.buildExpression(module, compiledType, node);
    }

    public Optional<Tuple2<ConstraintDefinition, Module>> compileConstraintAndModule(String name,
            CompiledType compiledType, Optional<Parameters> maybeParameters) {
        return constraintCompiler.compileConstraintAndModule(name, compiledType, maybeParameters);
    }

    public Optional<ConstraintDefinition> compileConstraint(String name, CompiledType compiledType,
            Optional<Parameters> maybeParameters) {
        return constraintCompiler.compileConstraint(name, compiledType, maybeParameters);
    }

    public Optional<ConstraintDefinition> compileConstraint(CompiledType compiledType,
            Optional<Parameters> maybeParameters) {
        return constraintCompiler.compileConstraint(compiledType, maybeParameters);
    }

    public ConstraintDefinition compileConstraint(Type type, Constraint constraint,
            Optional<Parameters> maybeParameters) {
        return constraintCompiler.compileConstraint(type, constraint, maybeParameters);
    }

    public ConstraintDefinition compileConstraint(CompiledType compiledType, Constraint constraint,
            Optional<Parameters> maybeParameters) {
        return constraintCompiler.compileConstraint(compiledType, constraint, maybeParameters);
    }

    public void addConstraint(CompiledType compiledType, Module module, ConstraintDefinition definition) {
        constraintCompiler.addConstraint(compiledType, module, definition);
    }

    public CompiledValue<Value> compileDefault(Type type, Value value, Optional<Parameters> maybeParameters) {
        return defaultsCompiler.compileDefault(type, value, maybeParameters);
    }

    public void addDefaultField(CompilerContext ctx, JavaClass javaClass, String field, String typeName,
            CompiledValue<Value> compiledValue) {
        defaultsCompiler.addDefaultField(ctx, javaClass, field, typeName, compiledValue);
    }

    public CompiledCollectionType getCompiledCollectionType(CompiledType compiledType) {
        return Optional.ofNullable(getCompiledBaseType(compiledType))
                .filter(CompiledCollectionType.class::isInstance)
                .map(CompiledCollectionType.class::cast)
                .orElseThrow(() -> new CompilerException("Failed to resolve the type of %s", compiledType));
    }

    public CompiledChoiceType getCompiledChoiceType(CompiledType compiledType) {
        return Optional.ofNullable(getCompiledBaseType(compiledType))
                .filter(CompiledChoiceType.class::isInstance)
                .map(CompiledChoiceType.class::cast)
                .orElseThrow(() -> new CompilerException("Failed to resolve the type of %s", compiledType));
    }

    public <T> T withNewType(Supplier<T> supplier) {
        Deque<Type> oldType = currentType;

        try {
            currentType = new LinkedList<>();

            return supplier.get();
        } finally {
            currentType = oldType;
        }
    }

    public CompiledBuiltinType getCompiledBuiltinType(Class<? extends Type> typeClass) {
        if (isBuiltin(typeClass)) {
            var type = getInstance(typeClass,
                    e -> new CompilerException("Failed to create type class: %s", e, typeClass.getSimpleName()));

            return new CompiledBuiltinType(type);
        }

        throw new IllegalCompilerStateException("%s is not a builtin type", typeClass.getSimpleName());
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
        if (isResolvableReference(type)) {
            var typeName = ((TypeReference) type).getType();

            return getCompiledType(typeName);
        } else if (type instanceof ExternalTypeReference externalTypeReference) {
            var moduleName = externalTypeReference.getModule();
            var typeName = externalTypeReference.getType();

            return getCompiledType(moduleName, typeName);
        } else if (type instanceof EnumeratedType enumeratedType) {
            var enumeratedTypeCompiler = (EnumeratedTypeCompiler) this.getCompiler(type.getClass());

            return enumeratedTypeCompiler.createCompiledType(this, null, enumeratedType);
        } else if (isBuiltin(type)) {
            return new CompiledBuiltinType(type);
        } else if (type instanceof SelectionType selectionType) {
            var compiler = this.<SelectionType, SelectionTypeCompiler>getCompiler(SelectionType.class);

            return compiler.compile(this, null, selectionType, Optional.empty());
        }

        throw new IllegalCompilerStateException("The type %s is not expected", type.getClass().getSimpleName());
    }

    public CompiledType resolveTypeFromObject(TypeFromObject type, Optional<Parameters> maybeParameters) {
        var referencedObjects = type.getReference();

        if (!(referencedObjects instanceof ObjectReference)) {
            var formattedType = TypeFormatter.formatType(this, type);

            throw new IllegalCompilerStateException(type.getPosition(), "%s contains an invalid object reference",
                    formattedType);
        }

        var objectReference = (ObjectReference) referencedObjects;
        var compiledObject = getCompiledObject(objectReference, maybeParameters);
        var objectDefinition = compiledObject.getObjectDefinition();
        var fieldNames = type.getField().getPrimitiveFieldNames();
        Object resolvedType = null;

        for (var fieldName : fieldNames) {
            var fieldReference = fieldName.getReference();

            if (!fieldName.isTypeFieldReference() || objectDefinition == null ||
                    !objectDefinition.containsKey(fieldReference)) {
                var formattedType = TypeFormatter.formatType(this, type);

                throw new CompilerException(fieldName.getPosition(), "%s in %s doesn't refer to a type",
                        fieldName.getReference(), formattedType);
            }

            resolvedType = objectDefinition.get(fieldReference);

            if (resolvedType instanceof Map) {
                objectDefinition = (Map<String, Object>) resolvedType;
            } else {
                objectDefinition = null;
            }
        }

        if (!(resolvedType instanceof CompiledType)) {
            var formattedType = TypeFormatter.formatType(this, type);

            throw new CompilerException(type.getPosition(), "%s doesn't refer to a value", formattedType);
        }

        return (CompiledType) resolvedType;
    }

    public CompiledType getCompiledType(SimpleDefinedType simpleDefinedType) {
        if (CompilerUtils.isUsefulType(simpleDefinedType)) {
            throw new IllegalArgumentException("A UsefulType is not allowed here: " + simpleDefinedType);
        }

        var reference = simpleDefinedType.getType();
        var maybeModuleName = CompilerUtils.toExternalTypeReference(simpleDefinedType)
                .map(ExternalTypeReference::getModule);

        if (maybeModuleName.isEmpty()) {
            return getCompiledType(reference);
        }

        return getCompiledType(maybeModuleName.get(), reference);
    }

    public CompiledType getCompiledType(String reference) {
        Objects.requireNonNull(reference);

        try {
            return getCompilationResult(reference, Optional.empty(), "Type", this::getTypesOfCurrentModule,
                    (ref, mod) -> compiler.compileType(ref, mod), this::getTypesOfModule);
        } catch (ResolutionException e1) {
            try {
            return getCompilationResult(reference, Optional.empty(), "ValueSet", this::getTypesOfCurrentModule,
                    (ref, mod) -> compiler.compileValueSetType(ref, mod), this::getTypesOfModule);
            } catch (ResolutionException e2) {
                throw e1;
            }
        }
    }

    public CompiledType getCompiledType(String moduleName, String reference) {
        Objects.requireNonNull(moduleName);
        Objects.requireNonNull(reference);

        return getCompilationResult(reference, Optional.ofNullable(moduleName), "Type",
                () -> getTypesOfModule(moduleName), (ref, mod) -> compiler.compileType(ref, mod),
                this::getTypesOfModule);
    }

    public CompiledValue<? extends Value> getCompiledValue(DefinedValue definedValue) {
        var reference = definedValue.getReference();

        if (definedValue instanceof ExternalValueReference externalValueReference) {
            return getCompiledValue(externalValueReference.getModule(), reference);
        } else if (definedValue instanceof SimpleDefinedValue) {
            return getCompiledValue(reference);
        } else {
            throw new IllegalCompilerStateException(definedValue.getPosition(), "Unsupported type: %s",
                    definedValue.getClass().getSimpleName());
        }
    }

    public CompiledValue<? extends Value> getCompiledValue(String reference) {
        return getCompilationResult(reference, Optional.empty(), "Value", this::getValuesOfCurrentModule,
                (ref, mod) -> compiler.compileValue(ref, mod), this::getValuesOfModule);
    }

    public CompiledValue<? extends Value> getCompiledValue(String moduleName, String reference) {
        return getCompilationResult(reference, Optional.ofNullable(moduleName), "Value",
                () -> getValuesOfModule(moduleName), (ref, mod) -> compiler.compileValue(ref, mod),
                this::getValuesOfModule);
    }

    public <V extends Value> CompiledValue<V> getCompiledValue(Type type, Value value) {
        return getCompiledValue(type, value, Optional.empty());
    }

    public <V extends Value> CompiledValue<V> getCompiledValue(Type type, Value value,
            Optional<Parameters> maybeParameters) {
        return new ValueCompiler().compile(this, null, type, value, maybeParameters);
    }

    public <V extends Value> V getValue(Type type, Value value) {
        return (V) getCompiledValue(type, value).getValue();
    }

    public <V extends Value> CompiledValue<V> getCompiledValue(Class<IntegerValue> valueClass,
            DefinedValue definedValue) {
        var compiledValue = getCompiledValue(definedValue);
        var value = compiledValue.getValue();

        if (value.getClass().isAssignableFrom(valueClass)) {
            return (CompiledValue<V>) compiledValue;
        }

        throw new ValueResolutionException(definedValue.getPosition(), "Failed to resolve reference %s.", definedValue);
    }

    /**
     * Looks up the compiled object for the given object reference. The object may be compiled if it isn't already.
     *
     * @param objectReference an object reference
     * @return a compiled object
     */
    public CompiledObject getCompiledObject(ObjectReference objectReference) {
        var reference = objectReference.getReference();
        var maybeModuleName = CompilerUtils.toExternalObjectReference(objectReference)
                .map(ExternalObjectReference::getModule);

        if (maybeModuleName.isEmpty()) {
            return getCompiledObject(reference);
        }

        return getCompiledObject(maybeModuleName.get(), reference);
    }

    public CompiledObject getCompiledObject(ObjectReference objectReference,
            Optional<Parameters> maybeParameters) {
        var maybeModuleName = CompilerUtils.toExternalObjectReference(objectReference)
                .map(ExternalObjectReference::getModule);

        if (maybeModuleName.isPresent() || maybeParameters.isEmpty()) {
            return getCompiledObject(objectReference);
        }

        var parameters = maybeParameters.get();
        var maybeParameter = parameters.getDefinitionsAndValues().stream().
                filter(tuple -> ParameterPredicates.isObjectParameter(this, parameters, tuple.get_1(), objectReference))
                .findAny();

        if (maybeParameter.isPresent()) {
            var node = maybeParameter.get().get_2();
            var parameter = maybeParameter.get().get_1();
            var compiledObjectClass = ParameterGovernorHelper.getParameterObjectClass(this, parameters, parameter.getGovernor());

            parameters.markAsUsed(parameter);

            if (node instanceof ActualParameter actualParameter) {
                var maybeObject = actualParameter.getObject();

                if (maybeObject.isPresent()) {
                    var object = maybeObject.get();
                    var compiler = this.<ObjectNode, ObjectNodeCompiler>getCompiler(ObjectNode.class);

                    return compiler.compile(this, null, compiledObjectClass, object, maybeParameters);
                }
            }

            throw new CompilerException(node.getPosition(), "Expected an object but found: %s", node);
        }

        throw new CompilerException(objectReference.getPosition(), "Failed to resolve object reference: %s",
                objectReference);
    }

    private CompiledObject getCompiledObject(String reference) {
        return getCompilationResult(reference, Optional.empty(), "Object", this::getObjectsOfCurrentModule,
                compiler::compileObject, this::getObjectsOfModule);
    }

    public CompiledObject getCompiledObject(String moduleName, String reference) {
        return getCompilationResult(reference, Optional.of(moduleName), "Object",
                () -> getObjectsOfModule(moduleName), compiler::compileObject, this::getObjectsOfModule);
    }

    /**
     * Looks up the compiled object class for the given object class reference. The object class may be compiled if it
     * isn't already.
     *
     * @param objectClassReference an object class reference
     * @return a compiled object class
     */
    public CompiledObjectClass getCompiledObjectClass(ObjectClassReference objectClassReference) {
        var reference = objectClassReference.getReference();

        return getCompiledObjectClass(reference);
    }

    public CompiledObjectClass getCompiledObjectClass(String reference) {
        return getCompilationResult(reference, Optional.empty(), "ObjectClass", this::getObjectClassesOfCurrentModule,
                compiler::compileObjectClass, this::getObjectClassesOfModule);
    }

    public CompiledObjectClass getCompiledObjectClass(String moduleName, String reference) {
        return getCompilationResult(reference, Optional.of(moduleName), "ObjectClass",
                () -> getObjectClassesOfModule(moduleName), compiler::compileObjectClass,
                this::getObjectClassesOfModule);
    }

    /**
     * Looks up the compiled object set for the given object set reference. The object set may be compiled if it
     * isn't already.
     *
     * @param objectSetReference an object set reference
     * @return a compiled object set
     */
    public CompiledObjectSet getCompiledObjectSet(ObjectSetReference objectSetReference) {
        var reference = objectSetReference.getReference();
        var maybeModuleName = CompilerUtils.toExternalObjectSetReference(objectSetReference)
                .map(ExternalObjectSetReference::getModule);

        if (maybeModuleName.isEmpty()) {
            return getCompiledObjectSet(reference);
        }

        return getCompiledObjectSet(maybeModuleName.get(), reference);
    }


    public CompiledObjectSet getCompiledObjectSet(ObjectSetReference objectSetReference,
            Optional<Parameters> maybeParameters) {
        var maybeModuleName = CompilerUtils.toExternalObjectSetReference(objectSetReference)
                .map(ExternalObjectSetReference::getModule);

        if (maybeModuleName.isPresent() || maybeParameters.isEmpty()) {
            return getCompiledObjectSet(objectSetReference);
        }

        var parameters = maybeParameters.get();
        var maybeParameter = parameters.getDefinitionsAndValues().stream().
                filter(tuple -> ParameterPredicates.isObjectSetParameter(this, parameters, tuple.get_1(), objectSetReference))
                .findAny();

        if (maybeParameter.isPresent()) {
            var node = maybeParameter.get().get_2();

            if (!(node instanceof ActualParameter maybeObjectSet) ||
                    maybeObjectSet.getObjectSetSpec().isEmpty()) {
                throw new CompilerException(node.getPosition(), "Expected an object set but found: %s", node);
            }

            var valueSetOrObjectSet = (ActualParameter) node;
            var elementSetSpecs = valueSetOrObjectSet.getObjectSetSpec().get();
            var parameter = maybeParameter.get().get_1();
            var compiledObjectClass = ParameterGovernorHelper.getParameterObjectClass(this, parameters, parameter.getGovernor());

            parameters.markAsUsed(parameter);

            return new ObjectSetCompiler(this).getCompiledObjectSet(this, null, compiledObjectClass,
                    elementSetSpecs.getRootElements(), Optional.empty());
        }

        throw new CompilerException(objectSetReference.getPosition(), "Failed to resolve object set reference: %s",
                objectSetReference);
    }

    public CompiledObjectSet getCompiledObjectSet(String reference) {
        return getCompilationResult(reference, Optional.empty(), "ObjectSet",
                this::getObjectSetsOfCurrentModule, compiler::compileObjectSet, this::getObjectSetsOfModule);
    }

    public CompiledObjectSet getCompiledObjectSet(String moduleName, String reference) {
        return getCompilationResult(reference, Optional.of(moduleName), "ObjectSet",
                () -> getObjectSetsOfModule(moduleName), compiler::compileObjectSet, this::getObjectSetsOfModule);
    }

    /**
     * Looks up the compiled parameterized type for the given parameterized type reference. The compiled
     * parameterized type may be compiled if it isn't already.
     *
     * @param reference a parameterized type reference
     * @return a compiled parameterized type
     */
    public CompiledParameterizedType getCompiledParameterizedType(String reference) {
        return getCompilationResult(reference, Optional.empty(), "ParameterizedType",
                this::getParameterizedTypesOfCurrentModule, compiler::compileParameterizedType,
                this::getParameterizedTypesOfModule);
    }

    /**
     * Looks up the compiled parameterized type for the given module name and parameterized type reference. The compiled
     * parameterized type may be compiled if it isn't already.
     *
     * @param moduleName a module name
     * @param reference  a parameterized type reference
     * @return a compiled parameterized type
     */
    public CompiledParameterizedType getCompiledParameterizedType(String moduleName, String reference) {
        return getCompilationResult(reference, Optional.of(moduleName), "ParameterizedType",
                () -> getParameterizedTypesOfModule(moduleName), compiler::compileParameterizedType,
                this::getParameterizedTypesOfModule);
    }

    /**
     * Looks up the compiled parameterized object class for the given parameterized object class reference. The compiled
     * parameterized object class may be compiled if it isn't already.
     *
     * @param reference an parameterized object class reference
     * @return a compiled parameterized object class reference type
     */
    public CompiledParameterizedObjectClass getCompiledParameterizedObjectClass(String reference) {
        return getCompilationResult(reference, Optional.empty(), "ParameterizedObjectClass",
                this::getParameterizedObjectClassesOfCurrentModule, compiler::compiledParameterizedObjectClass,
                this::getParameterizedObjectClassesOfModule);
    }

    /**
     * Looks up the compiled parameterized object class for the given module name and parameterized object class
     * reference. The compiled parameterized object class may be compiled if it isn't already.
     *
     * @param moduleName a module name
     * @param reference  an parameterized object class reference
     * @return a compiled parameterized object class reference type
     */
    public CompiledParameterizedObjectClass getCompiledParameterizedObjectClass(String moduleName, String reference) {
        return getCompilationResult(reference, Optional.of(moduleName), "ParameterizedObjectClass",
                () -> getParameterizedObjectClassesOfModule(moduleName), compiler::compiledParameterizedObjectClass,
                this::getParameterizedObjectClassesOfModule);
    }

    /**
     * Looks up the compiled parameterized object for the given parameterized object reference. The compiled
     * parameterized object may be compiled if it isn't already.
     *
     * @param reference a parameterized object reference
     * @return a compiled parameterized object
     */
    public CompiledParameterizedObject getCompiledParameterizedObject(String reference) {
        return getCompilationResult(reference, Optional.empty(), "ParameterizedObject",
                this::getParameterizedObjectsOfCurrentModule, compiler::compiledParameterizedObject,
                this::getParameterizedObjectsOfModule);
    }

    /**
     * Looks up the compiled parameterized object for the given module name and parameterized object reference.
     * The compiled parameterized object may be compiled if it isn't already.
     *
     * @param moduleName a module name
     * @param reference  a parameterized object reference
     * @return a compiled parameterized object
     */
    public CompiledParameterizedObject getCompiledParameterizedObject(String moduleName, String reference) {
        return getCompilationResult(reference, Optional.of(moduleName), "ParameterizedObject",
                () -> getParameterizedObjectsOfModule(moduleName), compiler::compiledParameterizedObject,
                this::getParameterizedObjectsOfModule);
    }

    /**
     * Looks up the compiled parameterized object set for the given parameterized object set reference. The compiled
     * parameterized object set may be compiled if it isn't already.
     *
     * @param reference a parameterized object set reference
     * @return a compiled parameterized object set
     */
    public CompiledParameterizedObjectSet getCompiledParameterizedObjectSet(String reference) {
        return getCompilationResult(reference, Optional.empty(), "ParameterizedObjectSet",
                this::getParameterizedObjectSetsOfCurrentModule, compiler::compiledParameterizedObjectSet,
                this::getParameterizedObjectSetsOfModule);
    }

    /**
     * Looks up the compiled parameterized object set for the given module name and parameterized object set reference.
     * The compiled parameterized object set may be compiled if it isn't already.
     *
     * @param moduleName a module name
     * @param reference  a parameterized object set reference
     * @return a compiled parameterized object set
     */
    public CompiledParameterizedObjectSet getCompiledParameterizedObjectSet(String moduleName, String reference) {
        return getCompilationResult(reference, Optional.of(moduleName), "ParameterizedObjectSet",
                () -> getParameterizedObjectSetsOfModule(moduleName), compiler::compiledParameterizedObjectSet,
                this::getParameterizedObjectSetsOfModule);
    }

    /**
     * Looks up the compiled parameterized value set type for the given parameterized value set type reference. The
     * compiled parameterized value set type may be compiled if it isn't already.
     *
     * @param reference a parameterized value set type reference
     * @return a compiled parameterized value set type
     */
    public CompiledParameterizedValueSetType getCompiledParameterizedValueSetType(String reference) {
        return getCompilationResult(reference, Optional.empty(), "ParameterizedValueSetType",
                this::getParameterizedValueSetTypesOfCurrentModule, compiler::compiledParameterizedValueSetType,
                this::getParameterizedValueSetTypesOfModule);
    }

    /**
     * Looks up the compiled parameterized value set type for the given module name and parameterized value set type
     * reference. The compiled parameterized value set type may be compiled if it isn't already.
     *
     * @param moduleName a module name
     * @param reference  a parameterized value set type reference
     * @return a compiled parameterized value set type
     */
    public CompiledParameterizedValueSetType getCompiledParameterizedValueSetType(String moduleName, String reference) {
        return getCompilationResult(reference, Optional.of(moduleName), "ParameterizedValueSetType",
                () -> getParameterizedValueSetTypesOfModule(moduleName), compiler::compiledParameterizedValueSetType,
                this::getParameterizedValueSetTypesOfModule);
    }

    private <T extends CompilationResult> T getCompilationResult(String reference, Optional<String> maybeModuleName,
            String nodeName, Supplier<Map<String, T>> moduleAccessor,
            BiFunction<String, Optional<String>, Optional<T>> compiler,
            Function<String, Map<String, T>> importAccessor) {
        var moduleCompilationResult = moduleAccessor.get();
        var compilationResult = Optional.ofNullable(moduleCompilationResult.get(reference));

        if (compilationResult.isEmpty()) {
            compilationResult = compiler.apply(reference, maybeModuleName);
        }

        if (compilationResult.isEmpty()) {
            compilationResult = getImportedCompilationResult(reference, nodeName, compiler, importAccessor);
        }

        return compilationResult.orElseThrow(
                () -> new ResolutionException("Failed to resolve %s '%s'", nodeName, reference));
    }

    private <T extends CompilationResult> Optional<T> getImportedCompilationResult(String reference, String nodeName,
            BiFunction<String, Optional<String>, Optional<T>> compiler, Function<String, Map<String, T>> importAccessor) {
        var maybeImport = getImport(reference);

        if (maybeImport.isPresent()) {
            var moduleName = maybeImport.get().getReference().getName();
            var module = getModule(moduleName);

            if (!isSymbolExported(module, reference)) {
                var format = "Module '%s' uses the %s '%s' from module '%s' which the latter doesn't export";

                throw new CompilerException(format, getCurrentModuleName(), nodeName, reference, moduleName);
            }

            var maybeResult = Optional.ofNullable(importAccessor.apply(moduleName).get(reference));

            if (maybeResult.isEmpty()) {
                return compiler.apply(reference, Optional.ofNullable(moduleName));
            }

            return maybeResult;
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
        var exports = module.getBody().getExports();

        if (exports.getMode() == ExportsNode.Mode.SPECIFIC) {
            return exports.getSymbols().stream().anyMatch(s -> s.getName().equals(symbol));
        } else if (exports.getMode() == ExportsNode.Mode.ALL) {
            return true;
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
        var type = compiledType.getType();

        if (CompilerUtils.isAnyTypeReference(type)) {
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
        } while (isResolvableReference(type) && !(type instanceof CompiledBuiltinType));

        return compiledType;
    }

    public CompiledType findCompiledBaseType(CompiledType compiledType) {
        var type = compiledType.getType();

        if (type instanceof SimpleDefinedType simpleDefinedType && !(type instanceof UsefulType)) {
            return findCompiledBaseType(compiledType.getModuleName(), simpleDefinedType);
        }

        return compiledType;
    }

    public CompiledType findCompiledBaseType(String moduleName, SimpleDefinedType type) {
        var compiledType = getTypesOfModule(moduleName).get(type.getType());

        return findCompiledBaseType(compiledType);
    }

    private boolean isResolvableReference(Type type) {
        return type instanceof TypeReference && !(type instanceof UsefulType);
    }

    private Map<String, CompiledType> getTypesOfCurrentModule() {
        return getTypesOfModule(getCurrentModuleName());
    }

    private Map<String, CompiledType> getTypesOfModule(String moduleName) {
        return Optional.ofNullable(definedModules.get(moduleName))
                .map(CompiledModule::getTypes)
                .orElseGet(Map::of);
    }

    private Map<String, CompiledValue<? extends Value>> getValuesOfCurrentModule() {
        return getValuesOfModule(getCurrentModuleName());
    }

    private Map<String, CompiledValue<? extends Value>> getValuesOfModule(String moduleName) {
        return Optional.ofNullable(definedModules.get(moduleName))
                .map(CompiledModule::getValues)
                .orElseGet(Map::of);
    }

    private Map<String, CompiledObject> getObjectsOfCurrentModule() {
        return getObjectsOfModule(getCurrentModuleName());
    }

    private Map<String, CompiledObject> getObjectsOfModule(String moduleName) {
        return Optional.ofNullable(definedModules.get(moduleName))
                .map(CompiledModule::getObjects)
                .orElseGet(Map::of);
    }

    private Map<String, CompiledObjectClass> getObjectClassesOfCurrentModule() {
        return getObjectClassesOfModule(getCurrentModuleName());
    }

    private Map<String, CompiledObjectClass> getObjectClassesOfModule(String moduleName) {
        return Optional.ofNullable(definedModules.get(moduleName))
                .map(CompiledModule::getObjectClasses)
                .orElseGet(Map::of);
    }

    private Map<String, CompiledObjectSet> getObjectSetsOfCurrentModule() {
        return getObjectSetsOfModule(getCurrentModuleName());
    }

    private Map<String, CompiledObjectSet> getObjectSetsOfModule(String moduleName) {
        return Optional.ofNullable(definedModules.get(moduleName))
                .map(CompiledModule::getObjectSets)
                .orElseGet(Map::of);
    }

    private Map<String, CompiledParameterizedType> getParameterizedTypesOfCurrentModule() {
        return getParameterizedTypesOfModule(getCurrentModuleName());
    }

    private Map<String, CompiledParameterizedType> getParameterizedTypesOfModule(String moduleName) {
        return Optional.ofNullable(definedModules.get(moduleName))
                .map(CompiledModule::getParameterizedTypes)
                .orElseGet(Map::of);
    }

    private Map<String, CompiledParameterizedObjectClass> getParameterizedObjectClassesOfCurrentModule() {
        return getParameterizedObjectClassesOfModule(getCurrentModuleName());
    }

    private Map<String, CompiledParameterizedObjectClass> getParameterizedObjectClassesOfModule(String moduleName) {
        return Optional.ofNullable(definedModules.get(moduleName))
                .map(CompiledModule::getParameterizedObjectClass)
                .orElseGet(Map::of);
    }

    private Map<String, CompiledParameterizedObject> getParameterizedObjectsOfCurrentModule() {
        return getParameterizedObjectsOfModule(getCurrentModuleName());
    }

    private Map<String, CompiledParameterizedObject> getParameterizedObjectsOfModule(String moduleName) {
        return Optional.ofNullable(definedModules.get(moduleName))
                .map(CompiledModule::getParameterizedObject)
                .orElseGet(Map::of);
    }

    private Map<String, CompiledParameterizedObjectSet> getParameterizedObjectSetsOfCurrentModule() {
        return getParameterizedObjectSetsOfModule(getCurrentModuleName());
    }

    private Map<String, CompiledParameterizedObjectSet> getParameterizedObjectSetsOfModule(String moduleName) {
        return Optional.ofNullable(definedModules.get(moduleName))
                .map(CompiledModule::getParameterizedObjectSet)
                .orElseGet(Map::of);
    }

    private Map<String, CompiledParameterizedValueSetType> getParameterizedValueSetTypesOfCurrentModule() {
        return getParameterizedValueSetTypesOfModule(getCurrentModuleName());
    }

    private Map<String, CompiledParameterizedValueSetType> getParameterizedValueSetTypesOfModule(String moduleName) {
        return Optional.ofNullable(definedModules.get(moduleName))
                .map(CompiledModule::getParameterizedValueSetType)
                .orElseGet(Map::of);
    }

    private String getCurrentModuleName() {
        return currentModule.peek().getModuleId().getModuleName();
    }

    public String getRuntimeTypeName(Class<? extends Type> type) {
        return config.getRuntimeTypeClass(type).getSimpleName();
    }

    public String getRuntimeTypeName(String typeName) {
        try {
            var compiledType = getCompiledType(typeName);
            var baseType = getCompiledBaseType(compiledType).getType();

            return getRuntimeTypeName(baseType.getClass());
        } catch (CompilerException e) {
            return typeName;
        }
    }

    public String getRuntimeTypeName(Type type) {
        var typeClass = getTypeClass(type);

        return getRuntimeTypeName(typeClass);
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

        compiledType.setTags(CompilerUtils.getTagIds(this, type));

        var module = currentModule.peek();

        if (module != null) {
            compiledType.setModuleName(module.getModuleId().getModuleName());
        }

        if (!isSubType && isTopLevelType() && name != null) {
            addType(name, compiledType);
        }

        return compiledType;
    }

    public void startType(Type type) {
        currentType.push(type);
    }

    public void finishType(Type type) {
        if (!currentType.isEmpty()) {
            var typeToFinish = currentType.pop();

            if (typeToFinish.equals(type)) {
                return;
            }
        }

        throw new IllegalCompilerStateException(type.getPosition(), "Tried to finish an unexpected type: %s", type);
    }

    private boolean isTopLevelType() {
        return currentType.size() == 1;
    }

    public <V extends Value> CompiledValue<V> createCompiledValue(CompiledType type, V value) {
        return new UnNamedCompiledValue<>(type, value);
    }

    public <V extends Value> CompiledValue<V> createCompiledValue(String name, CompiledType type, V value) {
        var compiledValue = new CompiledValue<>(name, type, value);

        addValue(name, compiledValue);

        return compiledValue;
    }

    public CompiledObjectClass createCompiledObjectClass(String name) {
        var compiledObjectClass = new CompiledObjectClass(name);

        addObjectClass(name, compiledObjectClass);

        return compiledObjectClass;
    }

    public CompiledObjectSet createCompiledObjectSet(String name, CompiledObjectClass objectClass) {
        var compiledObjectSet = new CompiledObjectSet(name, objectClass);

        addObjectSet(name, compiledObjectSet);

        return compiledObjectSet;
    }

    public CompiledObject createCompiledObject(String name, CompiledObjectClass objectClass,
            Map<String, Object> objectDefinition) {
        var compiledObject = new CompiledObject(name, objectClass, objectDefinition);

        addObject(name, compiledObject);

        return compiledObject;
    }

    public CompiledParameterizedType createCompiledParameterizedType(String name, Type type,
            List<ParameterNode> parameters) {
        var compiledParameterizedType = new CompiledParameterizedType(name, type, parameters);

        addParameterizedType(name, compiledParameterizedType);

        return compiledParameterizedType;
    }

    public CompiledParameterizedObjectClass createCompiledParameterizedObjectClass(String name,
            ObjectClassNode objectClass, List<ParameterNode> parameters) {
        var compiledParameterizedObjectClass = new CompiledParameterizedObjectClass(name, objectClass, parameters);

        addParameterizedObjectClass(name, compiledParameterizedObjectClass);

        return compiledParameterizedObjectClass;
    }

    public CompiledParameterizedObject createCompiledParameterizedObject(String name, ObjectClassNode objectClass,
            ObjectNode objectDefinition, List<ParameterNode> parameters) {
        var compiledParameterizedObject = new CompiledParameterizedObject(name, objectClass, objectDefinition,
                parameters);

        addParameterizedObject(name, compiledParameterizedObject);

        return compiledParameterizedObject;
    }

    public CompiledParameterizedObjectSet createCompiledParameterizedObjectSet(String name, ObjectClassNode objectClass,
            ObjectSetSpecNode objectSet, List<ParameterNode> parameters) {
        var compiledParameterizedObjectSet = new CompiledParameterizedObjectSet(name, objectClass, objectSet,
                parameters);

        addParameterizedObjectSet(name, compiledParameterizedObjectSet);

        return compiledParameterizedObjectSet;
    }

    public CompiledParameterizedValueSetType createCompiledParameterizedValueSetType(String name, Type type,
            ElementSetSpecsNode elementSet, List<ParameterNode> parameters) {
        var compiledParameterizedValueSetType = new CompiledParameterizedValueSetType(name, type, elementSet,
                parameters);

        addParameterizedValueSetType(name, compiledParameterizedValueSetType);

        return compiledParameterizedValueSetType;
    }

    public <T extends CompiledType & HasChildComponents> Optional<T> findCompiledTypeRecursive(Type type) {
        if (!(type instanceof HasModuleName)) {
            throw new IllegalCompilerStateException(type.getPosition(), "Type has no module name: ", type);
        }

        var moduleName = ((HasModuleName) type).getModuleName();

        if (type instanceof TypeReference typeReference) {
            var compiledType = getTypesOfModule(moduleName).get(typeReference.getType());

            if (compiledType instanceof HasChildComponents) {
                return Optional.of((T) compiledType);
            }

            return Optional.empty();
        }

        var componentStream = getTypesOfModule(moduleName).values().stream();

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

    <T> T executeWithModule(String moduleName, Supplier<T> supplier) {
        var module = getModule(moduleName);

        if (module != null) {
            currentModule.push(module);

            try {
                return supplier.get();
            } finally {
                currentModule.pop();
            }
        }

        return null;
    }

}
