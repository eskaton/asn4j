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

import ch.eskaton.asn4j.compiler.java.JavaAnnotation;
import ch.eskaton.asn4j.parser.ast.ModuleNode;
import ch.eskaton.asn4j.parser.ast.types.ClassType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.Tag;
import ch.eskaton.asn4j.runtime.Clazz;
import ch.eskaton.asn4j.runtime.TagId;
import ch.eskaton.asn4j.runtime.TaggingMode;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag.Mode;
import ch.eskaton.commons.utils.StringUtils;

public class CompilerUtils {

    private CompilerUtils() {
    }

    static String formatTypeName(String name) {
        return StringUtils.initCap(formatName(name));
    }

    static String formatName(String name) {
        StringBuilder sb = new StringBuilder();
        boolean cap = false;

        for (char c : name.toCharArray()) {
            if (c == '-') {
                cap = true;
            } else if (cap == true) {
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

    static Mode getTaggingMode(ModuleNode module, Type type) throws CompilerException {
        TaggingMode taggingMode = type.getTaggingMode();

        if (taggingMode != null) {
            switch (taggingMode) {
                case Explicit:
                    return ASN1Tag.Mode.Explicit;
                case Implicit:
                    return ASN1Tag.Mode.Implicit;
            }
        }

        switch (module.getTagMode()) {
            case Explicit:
                return ASN1Tag.Mode.Explicit;
            case Implicit:
                return ASN1Tag.Mode.Implicit;
            case Automatic:
                throw new CompilerException("Automatic tagging not supported");
            default:
                return Mode.Explicit;
        }
    }

    public static JavaAnnotation getTagAnnotation(ModuleNode module, Tag tag, TaggingMode taggingMode) {
        String taggingModeString;

        if (taggingMode != null) {
            taggingModeString = taggingMode.toString();
        } else {
            taggingModeString = module.getTagMode().toString();
        }

        return getTagAnnotation(tag, taggingModeString, Boolean.TRUE);
    }

    public static JavaAnnotation getTagAnnotation(Tag tag, String taggingModeString, Boolean constructed) {
        JavaAnnotation tagAnnotation = new JavaAnnotation(ASN1Tag.class);

        tagAnnotation.addParameter("tag", tag.getClassNumber().getClazz().toString());
        tagAnnotation.addParameter("clazz", "Clazz."
                + (tag.getClazz() != null ? StringUtils.initCap(tag.getClazz().toString().toLowerCase())
                : Clazz.ContextSpecific.toString()));
        tagAnnotation.addParameter("constructed", constructed.toString().toLowerCase());
        tagAnnotation.addParameter("mode", ASN1Tag.class.getSimpleName() + ".Mode." + taggingModeString);

        return tagAnnotation;
    }

    public static TagId toTagId(Tag tag) {
        Clazz clazz;
        ClassType clazzType = tag.getClazz();

        if (clazzType == null) {
            clazz = Clazz.ContextSpecific;
        } else {

            switch (clazzType) {
                case APPLICATION:
                    clazz = Clazz.Application;
                    break;
                case PRIVATE:
                    clazz = Clazz.Private;
                    break;
                case UNIVERSAL:
                    clazz = Clazz.Universal;
                    break;
                default:
                    throw new CompilerException("Unknown class type: " + clazzType.name());
            }
        }

        // TODO: handle references in class number
        return new TagId(clazz, tag.getClassNumber().getClazz());
    }

    public static String getDefaultFieldName(String field) {
        return "$default_" + field;
    }

}
