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

import ch.eskaton.asn4j.compiler.java.objs.JavaAnnotation;
import ch.eskaton.asn4j.compiler.java.objs.JavaClass;
import ch.eskaton.asn4j.compiler.java.objs.JavaClass.BodyBuilder;
import ch.eskaton.asn4j.compiler.java.objs.JavaDefinedField;
import ch.eskaton.asn4j.compiler.java.objs.JavaEnum;
import ch.eskaton.asn4j.compiler.java.objs.JavaGetter;
import ch.eskaton.asn4j.compiler.java.objs.JavaTypedSetter;
import ch.eskaton.asn4j.compiler.results.CompiledChoiceType;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.types.Choice;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.asn4j.parser.ast.values.Tag;
import ch.eskaton.asn4j.runtime.TagId;
import ch.eskaton.asn4j.runtime.TaggingMode;
import ch.eskaton.asn4j.runtime.annotations.ASN1Alternative;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.asn4j.runtime.types.TypeName;
import ch.eskaton.commons.MutableReference;
import ch.eskaton.commons.collections.Tuple2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.java.objs.JavaVisibility.PRIVATE;
import static ch.eskaton.asn4j.compiler.java.objs.JavaVisibility.PUBLIC;

public class ChoiceCompiler implements NamedCompiler<Choice, CompiledType> {

    public static final String CLEAR_FIELDS = "clearFields";

    private static final String CHOICE_ENUM = "Choice";

    private static final String CHOICE_FIELD = "choice";

    @Override
    public CompiledType compile(CompilerContext ctx, String name, Choice node) {
        JavaClass javaClass = ctx.createClass(name, node, true);
        List<String> fieldNames = new ArrayList<>();
        JavaEnum typeEnum = new JavaEnum(CHOICE_ENUM);

        BodyBuilder builder = javaClass.method().modifier(PUBLIC).annotation("@Override")
                .returnType(ASN1Type.class.getSimpleName()).name("getValue").body();

        String clearFields = "\t\t" + CLEAR_FIELDS + "();\n";

        builder.append("switch(" + CHOICE_FIELD + ") {");

        var compiledType = ctx.createCompiledType(CompiledChoiceType.class, node, name);
        var components = new ArrayList<Tuple2<String, CompiledType>>();
        var seenTags = new HashMap<TagId, NamedType>();

        for (NamedType namedType : node.getRootAlternatives()) {
            var tagIds = getTagId(ctx, namedType);

            tagIds.forEach(tagId -> {
                var seenComponent = seenTags.get(tagId);

                if (seenComponent != null) {
                    throw new CompilerException("Duplicate tags in %s %s: %s and %s", TypeName.CHOICE, name,
                            seenComponent.getName(), namedType.getName());
                }
            });

            tagIds.stream().forEach(t -> seenTags.put(t, namedType));

            var typeConstant = CompilerUtils.formatConstant(namedType.getName());
            var component = compileChoiceNamedType(ctx, javaClass, namedType, typeConstant, clearFields);
            var fieldName = component.get_1();

            component.get_2().setParent(compiledType);

            components.add(component);
            fieldNames.add(fieldName);
            typeEnum.addEnumConstant(typeConstant);
            builder.append("\tcase " + typeConstant + ":").append("\t\treturn " + fieldName + ";");
        }

        builder.append("}").append("").append("return null;");

        builder.finish().build();

        javaClass.addEnum(typeEnum);
        javaClass.addField(new JavaDefinedField(CHOICE_ENUM, CHOICE_FIELD), true, false);

        addClearFieldsMethod(javaClass, fieldNames);

        compiledType.getComponents().addAll(components);

        var hasComponentConstraint = new MutableReference<>(false);

        compiledType.getComponents().stream().forEach(component -> {
            var componentName = component.get_1();
            var compiledComponent = component.get_2();
            var componentType = compiledComponent.getType();

            if (componentType.getConstraints() != null) {
                var constraintDef = ctx.compileConstraint(componentName, compiledComponent);

                compiledComponent.setConstraintDefinition(constraintDef);

                hasComponentConstraint.set(true);
            }
        });

        if (node.hasConstraint() || hasComponentConstraint.get()) {
            var constraintDef = ctx.compileConstraint(javaClass, name, compiledType);

            compiledType.setConstraintDefinition(constraintDef);
        }

        ctx.finishClass();

        return compiledType;
    }

    private Tuple2<String, CompiledType> compileChoiceNamedType(CompilerContext ctx, JavaClass javaClass, NamedType namedType,
            String typeConstant, String beforeCode) {
        String name = CompilerUtils.formatName(namedType.getName());
        CompiledType compiledType = ctx.defineType(namedType);
        String typeName = ctx.getTypeName(namedType);
        Tag tag = namedType.getType().getTag();
        TaggingMode taggingMode = namedType.getType().getTaggingMode();
        JavaDefinedField field = new JavaDefinedField(typeName, name);

        field.addAnnotation(new JavaAnnotation(ASN1Alternative.class).addParameter("name", '"' + typeConstant + '"'));

        if (tag != null) {
            JavaAnnotation tagAnnotation = CompilerUtils.getTagAnnotation(ctx.getModule(), tag, taggingMode);
            field.addAnnotation(tagAnnotation);
        }

        javaClass.addField(field, false, false);
        javaClass.addMethod(new JavaTypedSetter(typeName, name, CHOICE_FIELD, CHOICE_ENUM + "." + typeConstant,
                beforeCode));
        javaClass.addMethod(new JavaGetter(typeName, name, field.hasDefault()));

        return Tuple2.of(name, compiledType);
    }

    private void addClearFieldsMethod(JavaClass javaClass, List<String> fieldNames) {
        javaClass.method().modifier(PRIVATE).name(CLEAR_FIELDS).body()
                .append(fieldNames.stream().map(f -> f + " = null;").collect(Collectors.toList())).finish().build();
    }

    private Set<TagId> getTagId(CompilerContext ctx, NamedType namedType) {
        var type = ctx.resolveSelectedType(namedType.getType());

        return ctx.getTagId(type);
    }

}
