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

import ch.eskaton.asn4j.compiler.ParameterUsageVerifier.Kind;
import ch.eskaton.asn4j.compiler.java.objs.JavaAnnotation;
import ch.eskaton.asn4j.compiler.results.AbstractCompiledParameterizedResult;
import ch.eskaton.asn4j.compiler.results.CompiledChoiceType;
import ch.eskaton.asn4j.compiler.results.CompiledComponent;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.compiler.results.HasComponents;
import ch.eskaton.asn4j.parser.ast.ExternalObjectReference;
import ch.eskaton.asn4j.parser.ast.ExternalObjectSetReference;
import ch.eskaton.asn4j.parser.ast.HasPosition;
import ch.eskaton.asn4j.parser.ast.ModuleNode;
import ch.eskaton.asn4j.parser.ast.Node;
import ch.eskaton.asn4j.parser.ast.OIDComponentNode;
import ch.eskaton.asn4j.parser.ast.ObjectReference;
import ch.eskaton.asn4j.parser.ast.ObjectSetReference;
import ch.eskaton.asn4j.parser.ast.ParameterNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedNode;
import ch.eskaton.asn4j.parser.ast.ReferenceNode;
import ch.eskaton.asn4j.parser.ast.types.ClassType;
import ch.eskaton.asn4j.parser.ast.types.ExternalTypeReference;
import ch.eskaton.asn4j.parser.ast.types.NamedType;
import ch.eskaton.asn4j.parser.ast.types.OpenType;
import ch.eskaton.asn4j.parser.ast.types.SelectionType;
import ch.eskaton.asn4j.parser.ast.types.SimpleDefinedType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeFromObject;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.types.UsefulType;
import ch.eskaton.asn4j.parser.ast.values.AmbiguousValue;
import ch.eskaton.asn4j.parser.ast.values.Tag;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.Clazz;
import ch.eskaton.asn4j.runtime.TagId;
import ch.eskaton.asn4j.runtime.TaggingMode;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tags;
import ch.eskaton.commons.MutableReference;
import ch.eskaton.commons.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.ParameterUsageVerifier.checkUnusedParameters;
import static ch.eskaton.asn4j.runtime.TaggingMode.EXPLICIT;
import static ch.eskaton.asn4j.runtime.TaggingMode.IMPLICIT;

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

    public static String formatConstant(String name) {
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


    static List<TaggingMode> getTaggingModes(ModuleNode module, Type type) {
        return getTaggingModes(module, type.getTaggingModes());
    }

    private static List<TaggingMode> getTaggingModes(ModuleNode module, List<Optional<TaggingMode>> taggingModes) {
        return taggingModes.stream()
                .map(optionalTaggingMode -> optionalTaggingMode.orElseGet(() -> getDefaultTaggingMode(module)))
                .collect(Collectors.toList());
    }

    private static TaggingMode getDefaultTaggingMode(ModuleNode module) {
        return switch (module.getTagMode()) {
            case IMPLICIT -> IMPLICIT;
            case AUTOMATIC -> throw new CompilerException("Automatic tagging not supported");
            default -> EXPLICIT;
        };
    }

    public static JavaAnnotation getTagsAnnotation(List<TagId> tags) {
        var tagAnnotations = new LinkedList<JavaAnnotation>();

        for (var i = 0; i < tags.size(); i++) {
            tagAnnotations.add(getTagAnnotation(tags.get(i)));
        }

        var tagsAnnotation = new JavaAnnotation(ASN1Tags.class);

        tagsAnnotation.addParameter("tags", tagAnnotations);

        return tagsAnnotation;
    }

    public static JavaAnnotation getTagAnnotation(TagId tag) {
        JavaAnnotation tagAnnotation = new JavaAnnotation(ASN1Tag.class);

        tagAnnotation.addParameter("tag", tag.getTag());
        tagAnnotation.addParameter("clazz", "Clazz."
                + (tag.getClazz() != null ? tag.getClazz().toString()
                : Clazz.CONTEXT_SPECIFIC.toString()));

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

    public static List<TagId> toTagIds(List<Tag> tags) {
        if (tags == null) {
            return Collections.emptyList();
        }

        return tags.stream().map(CompilerUtils::toTagId).collect(Collectors.toList());
    }

    public static List<TagId> getTagIds(CompilerContext ctx, TypeFromObject node, Type type) {
        var typeTags = new ArrayList<>(toTagIds(node.getTags()));
        var taggingModes = getTaggingModes(ctx.getModule(), node);

        typeTags.addAll(toTagIds(type.getTags()));
        taggingModes.addAll(getTaggingModes(ctx.getModule(), type));

        return getTagIds(ctx, type, typeTags, taggingModes);
    }

    public static List<TagId> getTagIds(CompilerContext ctx, Type type) {
        var typeTags = new ArrayList<>(toTagIds(type.getTags()));
        var taggingModes = getTaggingModes(ctx.getModule(), type);

        return getTagIds(ctx, type, typeTags, taggingModes);
    }

    private static LinkedList<TagId> getTagIds(CompilerContext ctx, Type type, ArrayList<TagId> typeTags, List<TaggingMode> taggingModes) {
        var tags = new LinkedList<TagId>();
        var explicit = true;

        if (typeTags.size() != taggingModes.size()) {
            throw new IllegalCompilerStateException(type.getPosition(), "#typeTags %d != #taggingModes %d",
                    typeTags.size(), taggingModes.size());
        }

        for (var i = 0; i < typeTags.size(); i++) {
            var tag = typeTags.get(i);

            if (tag.getClazz() == Clazz.UNIVERSAL) {
                throw new CompilerException("UNIVERSAL class not allowed");
            }

            if (explicit) {
                tags.add(tag);
            }

            explicit = taggingModes.get(i) == TaggingMode.EXPLICIT;
        }

        if (ctx.isBuiltin(type)) {
            // check for built-in types first because some type references (useful types) are treated as built-ins
            if (explicit) {
                tags.addAll(getTagIdsOfBuiltinType(ctx, type));
            }
        } else if (type instanceof SimpleDefinedType simpleDefinedType) {
            var maybeCompiledTags = getTagsOfTypeReference(ctx, simpleDefinedType);

            addTags(tags, maybeCompiledTags, explicit);
        } else if (type instanceof NamedType namedType) {
            tags.addAll(getTagIds(ctx, namedType.getType()));
        } else if (type instanceof SelectionType selectionType) {
            var compiledType = ctx.getCompiledType(selectionType.getType());

            if (compiledType instanceof CompiledChoiceType compiledChoiceType) {
                var maybeComponent = compiledChoiceType.getComponent(selectionType.getId());

                if (maybeComponent.isEmpty()) {
                    throw new CompilerException(type.getPosition(),
                            "Selection type doesn't refer to known component: %s", type);
                }

                var maybeCompiledTags = maybeComponent.get().getCompiledType().getTags();

                addTags(tags, maybeCompiledTags, explicit);
            } else {
                throw new CompilerException(type.getPosition(), "Selection type doesn't refer to a Choice: %s", type);
            }
        } else if (type instanceof OpenType) {
            // ignore
        } else {
            throw new IllegalCompilerStateException("Unexpected type: %s", type);
        }

        return tags;
    }

    private static Optional<List<TagId>> getTagsOfTypeReference(CompilerContext ctx,
            SimpleDefinedType simpleDefinedType) {
        var maybeParameters = simpleDefinedType.getParameters();

        if (maybeParameters.isPresent()) {
            var typeName = simpleDefinedType.getType();
            var compiledParameterizedType = ctx.getCompiledParameterizedType(typeName);
            var parameterizedType = compiledParameterizedType.getType();

            return Optional.ofNullable(getTagIds(ctx, parameterizedType));
        } else {
            var compiledType = ctx.getCompiledType(simpleDefinedType);

            return compiledType.getTags();
        }
    }

    private static void addTags(LinkedList<TagId> tags, Optional<List<TagId>> maybeCompiledTags, boolean explicit) {
        if (maybeCompiledTags.isPresent()) {
            var compiledTags = maybeCompiledTags.get();

            if (explicit) {
                tags.addAll(compiledTags);
            } else {
                if (compiledTags.size() > 1) {
                    tags.addAll(compiledTags.subList(1, compiledTags.size()));
                }
            }
        }
    }

    private static LinkedList<TagId> getTagIdsOfBuiltinType(CompilerContext ctx, Type type) {
        var tags = new LinkedList<TagId>();
        var typeName = ctx.getRuntimeTypeName(type);

        try {
            var typeClazz = Class.forName("ch.eskaton.asn4j.runtime.types." + typeName);
            var tagAnnotation = typeClazz.getAnnotation(ASN1Tags.class);

            if (tagAnnotation != null) {
                var asn1Tags = tagAnnotation.tags();

                if (asn1Tags != null) {
                    tags.addAll(TagId.fromTags(Arrays.asList(asn1Tags)));
                }
            }
        } catch (ClassNotFoundException e) {
            throw new CompilerException("Unknown type: %s", type);
        }

        return tags;
    }

    public static Set<TagId> getLeadingTagId(CompiledType compiledType) {
        if (compiledType instanceof CompiledChoiceType compiledChoiceType) {
            return compiledChoiceType.getComponents().stream()
                    .map(CompiledComponent::getCompiledType)
                    .map(CompiledType::getTags)
                    .map(tags -> tags.map(t -> t.stream().findFirst())).
                            flatMap(Optional::stream)
                    .flatMap(Optional::stream)
                    .collect(Collectors.toSet());
        } else {
            return compiledType.getTags()
                    .map(tags -> tags.stream().findFirst().map(Set::of).orElse(Set.of()))
                    .orElse(Set.of());
        }
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

    public static String getTypeParameterString(Optional<List<String>> typeNames) {
        return typeNames.map(CompilerUtils::getTypeParameterString).orElse("");
    }

    public static String getTypeParameterString(List<String> typeNames) {
        ArrayList<String> reversedTypeNames = new ArrayList<>(typeNames);

        Collections.reverse(reversedTypeNames);

        return reversedTypeNames.stream().reduce("", (s1, s2) -> s1.isEmpty() ? s2 : s2 + "<" + s1 + ">");
    }

    public static List<Integer> getComponentIds(List<OIDComponentNode> components) {
        return components.stream().map(OIDComponentNode::getId).collect(Collectors.toList());
    }

    public static boolean compileComponentConstraints(CompilerContext ctx,
            HasComponents<? extends CompiledComponent> compiledType, Optional<Parameters> maybeParameters) {
        var hasComponentConstraint = new MutableReference<>(false);

        compiledType.getComponents().stream().forEach(component -> {
            var componentName = component.getName();
            var compiledComponent = component.getCompiledType();
            var componentType = compiledComponent.getType();

            if (componentType.getConstraints() != null && compiledComponent.getConstraintDefinition().isEmpty()) {
                var constraintDef = ctx.compileConstraint(componentName, compiledComponent, maybeParameters);

                compiledComponent.setConstraintDefinition(constraintDef.orElse(null));

                hasComponentConstraint.set(true);
            }
        });

        return hasComponentConstraint.get();
    }

    public static boolean isUsefulType(Type type) {
        return type instanceof UsefulType;
    }

    public static boolean isTypeReference(Type type) {
        return type instanceof TypeReference;
    }

    public static boolean isExternalTypeReference(Type type) {
        return type instanceof ExternalTypeReference;
    }

    public static boolean isAnyTypeReference(Type type) {
        return isTypeReference(type) || isExternalTypeReference(type);
    }

    public static Optional<ExternalTypeReference> toExternalTypeReference(SimpleDefinedType simpleDefinedType) {
        return simpleDefinedType instanceof ExternalTypeReference externalTypeReference ?
                Optional.of(externalTypeReference) :
                Optional.empty();
    }

    public static Optional<ExternalObjectReference> toExternalObjectReference(ObjectReference objectReference) {
        return objectReference instanceof ExternalObjectReference externalObjectReference ?
                Optional.of(externalObjectReference) :
                Optional.empty();
    }

    public static Optional<ExternalObjectSetReference> toExternalObjectSetReference(ObjectSetReference objectSetReference) {
        return objectSetReference instanceof ExternalObjectSetReference externalObjectSetReference ?
                Optional.of(externalObjectSetReference) :
                Optional.empty();
    }

    public static CompiledType compileTypeReference(CompilerContext ctx, TypeReference typeReference,
            Optional<Parameters> maybeParameters) {
        var referencedTypeName = typeReference.getType();
        var maybeTypeRefParams = typeReference.getParameters();

        if (maybeParameters.isPresent()) {
            if (maybeTypeRefParams.isPresent()) {
                var compiledType = getCompiledParameterizedType(ctx, typeReference,
                        (parameters -> updateParameters(maybeParameters.get(), parameters)));

                return updateTags(ctx, typeReference, compiledType);
            }

            var maybeResolvedType = ctx.getTypeParameter(maybeParameters.get(), typeReference);

            if (maybeResolvedType.isPresent()) {
                var resolvedType = maybeResolvedType.get();

                if (isAnyTypeReference(resolvedType)) {
                    var compiledType = ctx.getCompiledType((SimpleDefinedType) resolvedType);

                    return updateTags(ctx, typeReference, compiledType);
                }

                if (typeReference.hasConstraint()) {
                    resolvedType = Clone.clone(resolvedType);
                    resolvedType.setConstraints(typeReference.getConstraints());
                }

                var compiler = ctx.<Type, NamedCompiler<Type, CompiledType>>getCompiler((Class<Type>) resolvedType.getClass());
                var compiledType = compiler.compile(ctx, null, resolvedType, maybeParameters);

                return updateTags(ctx, typeReference, compiledType);
            }
        } else if (maybeTypeRefParams.isPresent()) {
            var compiledType = getCompiledParameterizedType(ctx, typeReference, UnaryOperator.identity());

            return updateTags(ctx, typeReference, compiledType);
        }

        var compiledType = ctx.getCompiledType(referencedTypeName);

        return updateTags(ctx, typeReference, compiledType);
    }

    private static CompiledType updateTags(CompilerContext ctx, TypeReference typeReference, CompiledType compiledType) {
        if (!typeReference.getTags().isEmpty()) {
            compiledType = compiledType.copy();

            var tagIds = getTagIds(ctx, typeReference);

            compiledType.setTags(tagIds);
        }

        return compiledType;
    }

    private static CompiledType getCompiledParameterizedType(CompilerContext ctx, TypeReference typeReference,
            UnaryOperator<Parameters> parametersProvider) {
        var reference = typeReference.getType();
        var compiledParameterizedType = ctx.getCompiledParameterizedType(reference);
        var parameters = createParameters(typeReference, reference, compiledParameterizedType);
        var updatedParameters = parametersProvider.apply(parameters);
        var maybeUpdatedParameters = Optional.of(updatedParameters);
        var type = compiledParameterizedType.getType();
        var compiler = ctx.<Type, NamedCompiler<Type, CompiledType>>getCompiler((Class<Type>) type.getClass());
        var compiledType = compiler.compile(ctx, ctx.getTypeName(type), type, maybeUpdatedParameters);

        checkUnusedParameters(Kind.TYPE, maybeUpdatedParameters);

        return compiledType;
    }

    /**
     * Substitutes references in the output parameters with the values of matching input parameters.
     * <p/>
     * In the following example Type1 of AbstractSet1 is the output parameter and its associated value is the reference
     * Type2. The input parameter is Type2 of AbstractSet2 with the value BOOLEAN. The method substitutes the reference
     * Type2 in the output with the Value BOOLEAN from the input, because the reference matches the input parameters
     * name.
     *
     * <pre>
     *  Set ::= AbstractSet2 {BOOLEAN}
     *
     *  AbstractSet1 {Type1} ::= SET {
     *      field Type1
     *  }
     *
     *  AbstractSet2 {Type2} ::= SET {
     *      COMPONENTS OF AbstractSet1 {Type2}
     *  }
     * </pre>
     *
     * @param inputParameters  Input parameters
     * @param outputParameters Output parameters
     * @return Updated parameters
     */
    public static Parameters updateParameters(Parameters inputParameters, Parameters outputParameters) {
        var parameterValues = outputParameters.getDefinitionsAndValues().stream().map(definitionAndValue -> {
            var actualParameter = definitionAndValue.get_2();
            var maybeType = actualParameter.getType();

            if (maybeType.isPresent() && maybeType.get() instanceof TypeReference paramReference) {
                var paramReferenceName = paramReference.getType();
                var maybeParameter = inputParameters.getDefinitionAndValue(paramReferenceName);

                if (maybeParameter.isPresent()) {
                    var parameter = maybeParameter.get();

                    definitionAndValue.set_2(parameter.get_2());
                    inputParameters.markAsUsed(parameter.get_1());
                }
            }

            return definitionAndValue.get_2();
        }).collect(Collectors.toList());

        return outputParameters.values(parameterValues);
    }

    /**
     * Matches the parameter definitions to the actual parameters of the reference and wraps them in a
     * parameters object.
     *
     * @param node                A reference
     * @param name                Name of the object that is being compiled
     * @param parameterizedResult The compiled parameterized result
     * @return A parameters object
     */
    public static <T extends HasPosition & ParameterizedNode> Parameters createParameters(T node, String name,
            AbstractCompiledParameterizedResult parameterizedResult) {
        var maybeParameterValues = node.getParameters();
        var parameterizedTypeName = parameterizedResult.getName();
        var parameterDefinitions = parameterizedResult.getParameters();
        var parameterValues = maybeParameterValues.orElse(List.of());
        var parameterValuesCount = parameterValues.size();
        var parameterDefinitionsCount = parameterDefinitions.size();

        if (parameterValuesCount != parameterDefinitionsCount) {
            var parameterNames = parameterDefinitions.stream()
                    .map(ParameterNode::getReference)
                    .map(ReferenceNode::getName)
                    .collect(Collectors.joining(", "));

            throw new CompilerException(node.getPosition(),
                    "'%s' passes %d parameters but '%s' expects: %s",
                    name, parameterValuesCount, parameterizedTypeName, parameterNames);
        }

        return new Parameters(parameterizedTypeName, parameterDefinitions, parameterValues);
    }

}
