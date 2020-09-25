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

package ch.eskaton.asn4j.compiler.types;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.CompilerUtils;
import ch.eskaton.asn4j.compiler.NamedCompiler;
import ch.eskaton.asn4j.compiler.Parameters;
import ch.eskaton.asn4j.compiler.java.objs.JavaAnnotation;
import ch.eskaton.asn4j.compiler.java.objs.JavaClass;
import ch.eskaton.asn4j.compiler.java.objs.JavaConstructor;
import ch.eskaton.asn4j.compiler.java.objs.JavaDefinedField;
import ch.eskaton.asn4j.compiler.java.objs.JavaParameter;
import ch.eskaton.asn4j.compiler.java.objs.JavaVisibility;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionComponent;
import ch.eskaton.asn4j.compiler.results.CompiledCollectionType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.types.Choice;
import ch.eskaton.asn4j.parser.ast.types.Collection;
import ch.eskaton.asn4j.parser.ast.types.ComponentType;
import ch.eskaton.asn4j.runtime.annotations.ASN1Component;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;
import ch.eskaton.asn4j.runtime.types.TypeName;
import ch.eskaton.commons.collections.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.CompilerUtils.formatName;

public abstract class AbstractCollectionCompiler<T extends Collection> implements NamedCompiler<T, CompiledType> {

    private final TypeName typeName;

    private final List<Function<TypeName, ComponentVerifier>> componentVerifierSuppliers;

    public AbstractCollectionCompiler(TypeName typeName,
            Function<TypeName, ComponentVerifier>... componentVerifierSupplier) {
        this.typeName = typeName;
        this.componentVerifierSuppliers = new ArrayList<>(Arrays.asList(componentVerifierSupplier));
        this.componentVerifierSuppliers.add(NameUniquenessVerifier::new);
    }

    public CompiledType compile(CompilerContext ctx, String name, T node, Optional<Parameters> maybeParameters) {
        var tags = CompilerUtils.getTagIds(ctx, node);
        var javaClass = ctx.createClass(name, node, tags);
        var componentVerifiers = componentVerifierSuppliers.stream()
                .map(s -> s.apply(typeName))
                .collect(Collectors.toList());
        var compiledType = ctx.createCompiledType(CompiledCollectionType.class, node, name);

        compiledType.setTags(tags);

        compileComponents(ctx, name, maybeParameters, componentVerifiers, compiledType,
                node.getAllRootComponents(), true);
        compileComponents(ctx, name, maybeParameters, componentVerifiers, compiledType,
                node.getExtensionAdditionComponents(), false);


        CompilerUtils.compileComponentConstraints(ctx, compiledType);

        var constraintDef = ctx.compileConstraintAndModule(name, compiledType);

        compiledType.setConstraintDefinition(constraintDef.map(Tuple2::get_1).orElse(null));

        if (compiledType.getConstraintDefinition().isPresent()) {
            javaClass.addModule(ctx, constraintDef.get().get_2());
            javaClass.addImport(ConstraintViolatedException.class);
        }

        createJavaConstructors(name, compiledType, javaClass);

        compiledType.getComponents().forEach(component -> addJavaField(ctx, component));

        ctx.finishClass();

        return compiledType;
    }


    private void compileComponents(CompilerContext ctx, String name, Optional<Parameters> maybeParameters,
            List<ComponentVerifier> componentVerifiers, CompiledCollectionType compiledType,
            List<ComponentType> components, boolean isRoot) {
        for (ComponentType component : components) {
            try {
                var compiler = ctx.<ComponentType, ComponentTypeCompiler>getCompiler(ComponentType.class);
                var compiledComponents = compiler.compile(ctx, compiledType, component, isRoot, maybeParameters);

                compiledComponents.forEach(c -> componentVerifiers.forEach(v -> v.verify(c)));
            } catch (CompilerException e) {
                if (component.getNamedType() != null) {
                    throw new CompilerException("Failed to compile component '%s' in %s '%s'", e,
                            component.getNamedType().getName(), typeName, name);
                } else {
                    throw new CompilerException("Failed to compile a component in %s '%s'", e, typeName, name);
                }
            }
        }
    }

    private void addJavaField(CompilerContext ctx, CompiledCollectionComponent compiledCollectionComponent) {
        var compiledType = compiledCollectionComponent.getCompiledType();
        var maybeDefaultValue = compiledCollectionComponent.getDefaultValue();
        var hasDefault = maybeDefaultValue.isPresent();
        var isOptional = compiledCollectionComponent.isOptional();
        var type = compiledType.getType();
        var javaClass = ctx.getCurrentClass();
        var javaTypeName = compiledType.getName();
        var javaFieldName = formatName(compiledCollectionComponent.getName());
        var field = new JavaDefinedField(javaTypeName, javaFieldName, hasDefault);
        var compAnnotation = new JavaAnnotation(ASN1Component.class);

        if (isOptional) {
            compAnnotation.addParameter("optional", "true");
        } else if (hasDefault) {
            compAnnotation.addParameter("hasDefault", "true");

            if (type instanceof Choice) {
                javaClass.addStaticImport(ch.eskaton.commons.utils.Utils.class, "with");
            }

            var defaultValue = maybeDefaultValue.get();

            ctx.addDefaultField(ctx, javaClass, field.getName(), javaTypeName, defaultValue);
        }

        field.addAnnotation(compAnnotation);

        var tags = compiledType.getTags();

        if (tags.isPresent() && !tags.get().isEmpty()) {
            field.addAnnotation(CompilerUtils.getTagsAnnotation(tags.get()));
        }

        javaClass.addField(field);
    }


    private void createJavaConstructors(String name, CompiledCollectionType compiledType, JavaClass javaClass) {
        var ctor = new JavaConstructor(JavaVisibility.PUBLIC, name);
        var ctorBody = new StringBuilder();

        compiledType.getComponents().forEach(component -> {
            var argType = component.getCompiledType().getName();
            var argName = CompilerUtils.formatName(component.getName());

            ctor.getParameters().add(new JavaParameter(argType, argName));
            ctorBody.append("\t\tthis.%s = %s;\n".formatted(argName, argName));
        });

        ctor.setBody(Optional.of(ctorBody.toString()));
        javaClass.addMethod(ctor);
    }
}
