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

import ch.eskaton.asn4j.compiler.java.*;
import ch.eskaton.asn4j.parser.ast.ModuleNode.TagMode;
import ch.eskaton.asn4j.parser.ast.types.Choice;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.asn4j.parser.ast.values.Tag;
import ch.eskaton.asn4j.runtime.TaggingMode;
import ch.eskaton.asn4j.runtime.annotations.ASN1Alternative;
import ch.eskaton.asn4j.runtime.types.ASN1Type;

import java.util.ArrayList;
import java.util.List;

public class ChoiceCompiler implements NamedCompiler<Choice> {

    private static final String CHOICE_TYPE_ENUM = "Type";

    private static final String CHOICE_TYPE_FIELD = "type";

    public void compile(CompilerContext ctx, String name, Choice node)
            throws CompilerException {

        JavaClass javaClass = ctx.createClass(name, node, true);

        List<String> fieldNames = new ArrayList<String>();
        JavaEnum typeEnum = new JavaEnum(CHOICE_TYPE_ENUM);

        StringBuilder sb = new StringBuilder();

        sb.append("\t@Override\n\tpublic ")
                .append(ASN1Type.class.getSimpleName())
                .append(" getValue() {\n\t\tswitch(type) {\n");

        for (NamedType type : node.getRootTypeList()) {
            String typeConstant = CompilerUtils.formatConstant(type.getName());
            String fieldName = compileChoiceNamedType(ctx, javaClass, type,
                                                      CHOICE_TYPE_ENUM + "." + typeConstant);
            typeEnum.addEnumConstant(typeConstant);
            fieldNames.add(fieldName);
            sb.append("\t\t\tcase ").append(typeConstant)
                    .append(":\n\t\t\t\treturn ").append(fieldName)
                    .append(";\n");
        }

        sb.append("\t\t}\n\n\t\treturn null;\n\t}\n\n");

        javaClass.addEnum(typeEnum);
        javaClass.addField(new JavaDefinedField(CHOICE_TYPE_ENUM,
                                                CHOICE_TYPE_FIELD));
        javaClass
                .addMethod(new JavaGetter(CHOICE_TYPE_ENUM, CHOICE_TYPE_FIELD));

        javaClass.createEqualsAndHashCode();
        javaClass.addMethod(new JavaLiteralMethod(sb.toString()));

        ctx.finishClass();
    }

    private String compileChoiceNamedType(CompilerContext ctx,
            JavaClass javaClass, NamedType namedType, String typeConstant)
            throws CompilerException {
        String name;
        String typeName = ctx.getType(namedType);
        name = CompilerUtils.formatName(namedType.getName());
        Tag tag = namedType.getType().getTag();
        TaggingMode taggingMode = namedType.getType().getTaggingMode();

        JavaDefinedField field = new JavaDefinedField(typeName, name);

        field.addAnnotation(new JavaAnnotation(ASN1Alternative.class));

        if (tag != null) {
            JavaAnnotation tagAnnotation = CompilerUtils.getTagAnnotation(
                    ctx.getModule(), tag, taggingMode);
            field.addAnnotation(tagAnnotation);
        }

        javaClass.addField(field);
        javaClass.addMethod(new JavaTypedSetter(typeName, name,
                                                CHOICE_TYPE_FIELD, typeConstant));
        return name;
    }

}
