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
import ch.eskaton.asn4j.compiler.CompilerUtils;
import ch.eskaton.asn4j.compiler.NamedCompiler;
import ch.eskaton.asn4j.compiler.Parameters;
import ch.eskaton.asn4j.compiler.java.objs.JavaAnnotation;
import ch.eskaton.asn4j.compiler.java.objs.JavaClass;
import ch.eskaton.asn4j.compiler.java.objs.JavaDefinedField;
import ch.eskaton.asn4j.compiler.java.objs.JavaEnum;
import ch.eskaton.asn4j.compiler.java.objs.JavaGetter;
import ch.eskaton.asn4j.compiler.java.objs.JavaTypedSetter;
import ch.eskaton.asn4j.compiler.results.CompiledChoiceType;
import ch.eskaton.asn4j.compiler.results.CompiledComponent;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.types.Choice;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.asn4j.runtime.annotations.ASN1Alternative;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.java.objs.JavaVisibility.PRIVATE;
import static ch.eskaton.asn4j.compiler.java.objs.JavaVisibility.PUBLIC;

public class ChoiceCompiler implements NamedCompiler<Choice, CompiledType> {

    public static final String CLEAR_FIELDS = "clearFields";

    private static final String CHOICE_ENUM = "Choice";

    private static final String CHOICE_FIELD = "choice";

    @Override
    public CompiledType compile(CompilerContext ctx, String name, Choice node, Optional<Parameters> maybeParameters) {
        var tags = CompilerUtils.getTagIds(ctx, node);
        var javaClass = ctx.createClass(name, node, tags);
        var fieldNames = new ArrayList<String>();
        var typeEnum = new JavaEnum(CHOICE_ENUM);
        var componentVerifiers = List.of(
                new NameUniquenessVerifier(TypeName.CHOICE),
                new TagUniquenessVerifier(TypeName.CHOICE),
                new UntaggedOpenTypeVerifier(TypeName.CHOICE));
        var bodyBuilder = javaClass.method().modifier(PUBLIC).annotation("@Override")
                .returnType(ASN1Type.class.getSimpleName()).name("getValue").body();
        var clearFields = "\t\t" + CLEAR_FIELDS + "();\n";

        bodyBuilder.append("switch(" + CHOICE_FIELD + ") {");

        var compiledType = ctx.createCompiledType(CompiledChoiceType.class, node, name);

        compiledType.setTags(tags);

        var components = new ArrayList<CompiledComponent>();

        for (NamedType namedType : node.getRootAlternatives()) {
            var typeConstant = CompilerUtils.formatConstant(namedType.getName());
            var compiledComponent = compileChoiceNamedType(ctx, javaClass, namedType,
                    maybeParameters, typeConstant, clearFields);
            var fieldName = compiledComponent.getName();
            var component = compiledComponent.getCompiledType();

            component.setParent(compiledType);

            componentVerifiers.forEach(v -> v.verify(compiledComponent));

            components.add(compiledComponent);
            fieldNames.add(fieldName);
            typeEnum.addEnumConstant(typeConstant);
            bodyBuilder.append("\tcase " + typeConstant + ":").append("\t\treturn " + fieldName + ";");
        }

        bodyBuilder.append("}").append("").append("return null;");

        bodyBuilder.finish().build();

        javaClass.addEnum(typeEnum);
        javaClass.addField(new JavaDefinedField(CHOICE_ENUM, CHOICE_FIELD), true, false);

        addClearFieldsMethod(javaClass, fieldNames);

        compiledType.getComponents().addAll(components);

        var hasComponentConstraint = CompilerUtils.compileComponentConstraints(ctx, compiledType);

        if (node.hasConstraint() || hasComponentConstraint) {
            var constraintDef = ctx.compileConstraintAndModule(name, compiledType);

            compiledType.setConstraintDefinition(constraintDef.get_1());

            javaClass.addModule(ctx, constraintDef.get_2());
            javaClass.addImport(ConstraintViolatedException.class);
        }

        ctx.finishClass();

        return compiledType;
    }

    private CompiledComponent compileChoiceNamedType(CompilerContext ctx, JavaClass javaClass,
            NamedType namedType, Optional<Parameters> maybeParameters, String typeConstant, String beforeCode) {
        var name = CompilerUtils.formatName(namedType.getName());
        var compiledType = ctx.defineType(namedType, maybeParameters);
        var typeName = compiledType.getName();
        var field = new JavaDefinedField(typeName, name);
        var tags = compiledType.getTags();

        field.addAnnotation(new JavaAnnotation(ASN1Alternative.class).addParameter("name", '"' + typeConstant + '"'));

        if (tags.isPresent() && !tags.get().isEmpty()) {
            field.addAnnotation(CompilerUtils.getTagsAnnotation(tags.get()));
        }

        javaClass.addField(field, false, false);
        javaClass.addMethod(new JavaTypedSetter(typeName, name, CHOICE_FIELD, CHOICE_ENUM + "." + typeConstant,
                beforeCode));
        javaClass.addMethod(new JavaGetter(typeName, name, field.hasDefault()));

        return new CompiledComponent(name, compiledType);
    }

    private void addClearFieldsMethod(JavaClass javaClass, List<String> fieldNames) {
        javaClass.method().modifier(PRIVATE).name(CLEAR_FIELDS).body()
                .append(fieldNames.stream().map(f -> f + " = null;").collect(Collectors.toList())).finish().build();
    }

}
