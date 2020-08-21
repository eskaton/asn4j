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
import ch.eskaton.asn4j.parser.ast.ModuleNode;
import ch.eskaton.asn4j.parser.ast.Node;
import ch.eskaton.asn4j.parser.ast.OIDComponentNode;
import ch.eskaton.asn4j.parser.ast.types.ClassType;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.values.AmbiguousValue;
import ch.eskaton.asn4j.parser.ast.values.Tag;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.Clazz;
import ch.eskaton.asn4j.runtime.TagId;
import ch.eskaton.asn4j.runtime.TaggingMode;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag.Mode;
import ch.eskaton.commons.utils.StreamsUtils;
import ch.eskaton.commons.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CompilerUtils {

    private CompilerUtils() {
    }

    public static String formatTypeName(String name) {
        return StringUtils.initCap(formatName(name));
    }

    public static String formatName(String name) {
        StringBuilder sb = new StringBuilder();
        boolean cap = false;

        for (char c : name.toCharArray()) {
            if (c == '-') {
                cap = true;
            } else if (cap) {
                cap = false;
                if ('a' <= c && c <= 'z') {
                    sb.append((char) (c & ~0x20));
                } else {
                    sb.append(c);
                }
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    static String formatConstant(String name) {
        StringBuilder sb = new StringBuilder();

        for (char c : name.toCharArray()) {
            if (c == '-') {
                sb.append('_');
            } else {
                if ('a' <= c && c <= 'z') {
                    sb.append((char) (c & ~0x20));
                } else {
                    sb.append(c);
                }
            }
        }

        return sb.toString();
    }

    static Mode getTaggingMode(ModuleNode module, Type type) {
        TaggingMode taggingMode = type.getTaggingMode();

        if (taggingMode != null) {
            switch (taggingMode) {
                case EXPLICIT:
                    return ASN1Tag.Mode.EXPLICIT;
                case IMPLICIT:
                    return ASN1Tag.Mode.IMPLICIT;
            }
        }

        switch (module.getTagMode()) {
            case EXPLICIT:
                return ASN1Tag.Mode.EXPLICIT;
            case IMPLICIT:
                return ASN1Tag.Mode.IMPLICIT;
            case AUTOMATIC:
                throw new CompilerException("Automatic tagging not supported");
            default:
                return Mode.EXPLICIT;
        }
    }

    public static JavaAnnotation getTagAnnotation(ModuleNode module, Tag tag, TaggingMode taggingMode) {
        String taggingModeString;

        if (taggingMode != null) {
            taggingModeString = taggingMode.toString();
        } else {
            taggingModeString = module.getTagMode().toString();
        }

        return getTagAnnotation(tag, taggingModeString);
    }

    public static JavaAnnotation getTagAnnotation(Tag tag, String taggingModeString) {
        JavaAnnotation tagAnnotation = new JavaAnnotation(ASN1Tag.class);

        tagAnnotation.addParameter("tag", tag.getClassNumber().getClazz().toString());
        tagAnnotation.addParameter("clazz", "Clazz."
                + (tag.getClazz() != null ? tag.getClazz().toString()
                : Clazz.CONTEXT_SPECIFIC.toString()));
        tagAnnotation.addParameter("mode", ASN1Tag.class.getSimpleName() + ".Mode." + taggingModeString);

        return tagAnnotation;
    }

    public static TagId toTagId(Tag tag) {
        Clazz clazz;
        ClassType clazzType = tag.getClazz();

        if (clazzType == null) {
            clazz = Clazz.CONTEXT_SPECIFIC;
        } else {

            switch (clazzType) {
                case APPLICATION:
                    clazz = Clazz.APPLICATION;
                    break;
                case PRIVATE:
                    clazz = Clazz.PRIVATE;
                    break;
                case UNIVERSAL:
                    clazz = Clazz.UNIVERSAL;
                    break;
                default:
                    throw new CompilerException(tag.getPosition(), "Unknown class type: %s", clazzType.name());
            }
        }

        // TODO: handle references in class number
        return new TagId(clazz, tag.getClassNumber().getClazz());
    }

    public static String getDefaultFieldName(String field) {
        return "$default_" + field;
    }


    public static <V extends Value> V resolveAmbiguousValue(Node value, Class<V> valueClass) {
        if (value instanceof AmbiguousValue) {
            return ((AmbiguousValue) value).getValue(valueClass);
        }

        if (valueClass.isAssignableFrom(value.getClass())) {
            return (V) value;
        }

        return null;
    }

    public static String getTypeName(Type type) {
        if (type instanceof TypeReference) {
            return ((TypeReference) type).getType();
        }

        return type.getClass().getSimpleName();
    }

    public static String formatTypeName(Type type) {
        return (type instanceof NamedType ? ((NamedType) type).getName() : getTypeName(type));
    }

    public static String getTypeParameterString(Optional<List<String>> typeNames) {
        return typeNames.map(CompilerUtils::getTypeParameterString).orElse("");
    }

    public static String getTypeParameterString(List<String> typeNames) {
        ArrayList<String> reversedTypeNames = new ArrayList<>(typeNames);

        Collections.reverse(reversedTypeNames);

        return reversedTypeNames.stream().reduce("", (s1, s2) -> s1.isEmpty() ? s2 : s2 + "<" + s1 + ">");
    }

    public static String formatValue(Value value) {
        if (value instanceof AmbiguousValue) {
            return "Multiple possible value interpretations found: \n" +
                    StreamsUtils.zipWithIndex(1, ((AmbiguousValue) value).getValues().stream().map(Object::toString))
                            .map(tuple -> tuple.get_1() + ". " + tuple.get_2()).collect(Collectors.joining("\n"));

        } else {
            return value.toString();
        }
    }

    public static List<Integer> getComponentIds(List<OIDComponentNode> components) {
        return components.stream().map(OIDComponentNode::getId).collect(Collectors.toList());
    }

}
