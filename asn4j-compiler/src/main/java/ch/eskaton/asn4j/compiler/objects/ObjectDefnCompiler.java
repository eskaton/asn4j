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

package ch.eskaton.asn4j.compiler.objects;

import ch.eskaton.asn4j.compiler.Compiler;
import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.Formatter;
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.compiler.parameters.Parameters;
import ch.eskaton.asn4j.compiler.results.CompiledFixedTypeValueField;
import ch.eskaton.asn4j.compiler.results.CompiledObjectClass;
import ch.eskaton.asn4j.compiler.results.CompiledObjectField;
import ch.eskaton.asn4j.compiler.results.CompiledObjectSetField;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.compiler.results.CompiledTypeField;
import ch.eskaton.asn4j.compiler.results.CompiledVariableTypeValueField;
import ch.eskaton.asn4j.compiler.types.TypeFromObjectCompiler;
import ch.eskaton.asn4j.compiler.types.formatters.TypeFormatter;
import ch.eskaton.asn4j.compiler.values.ValueResolutionException;
import ch.eskaton.asn4j.compiler.values.formatters.ValueFormatter;
import ch.eskaton.asn4j.parser.Parser;
import ch.eskaton.asn4j.parser.Parser.DefinedSyntaxReParser;
import ch.eskaton.asn4j.parser.ParserException;
import ch.eskaton.asn4j.parser.Position;
import ch.eskaton.asn4j.parser.RequiredToken;
import ch.eskaton.asn4j.parser.ast.DefaultSyntaxNode;
import ch.eskaton.asn4j.parser.ast.DefinedSyntaxNode;
import ch.eskaton.asn4j.parser.ast.ExternalObjectReference;
import ch.eskaton.asn4j.parser.ast.FieldSettingNode;
import ch.eskaton.asn4j.parser.ast.Group;
import ch.eskaton.asn4j.parser.ast.LiteralNode;
import ch.eskaton.asn4j.parser.ast.Node;
import ch.eskaton.asn4j.parser.ast.ObjectDefnNode;
import ch.eskaton.asn4j.parser.ast.ObjectReference;
import ch.eskaton.asn4j.parser.ast.ObjectSetElements;
import ch.eskaton.asn4j.parser.ast.PrimitiveFieldNameNode;
import ch.eskaton.asn4j.parser.ast.Setting;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.types.TypeFromObject;
import ch.eskaton.asn4j.parser.ast.values.DefinedValue;
import ch.eskaton.asn4j.parser.ast.values.ExternalValueReference;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.utils.ToString;
import ch.eskaton.commons.collections.Tuple2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ObjectDefnCompiler implements Compiler<ObjectDefnNode> {

    private CompilerContext ctx;

    public ObjectDefnCompiler(CompilerContext ctx) {
        this.ctx = ctx;
    }

    public Map<String, Object> compile(CompiledObjectClass objectClass, ObjectDefnNode objectDefinition,
            Optional<Parameters> maybeParameters) {
        var syntax = objectDefinition.getSyntax();
        Map<String, Object> compiledObject;

        if (syntax instanceof DefaultSyntaxNode) {
            compiledObject = compileDefaultSyntax(objectClass, (DefaultSyntaxNode) syntax, maybeParameters);
        } else if (syntax instanceof DefinedSyntaxNode) {
            try {
                compiledObject = compileDefinedSyntax(objectClass, (DefinedSyntaxNode) syntax, maybeParameters);
            } catch (ParserException e) {
                throw new CompilerException(e);
            }
        } else {
            throw new CompilerException(syntax.getPosition(), "Unsupported syntax: %s",
                    syntax.getClass().getSimpleName());
        }

        verifyIntegrity(objectClass, objectDefinition, compiledObject);

        return compiledObject;
    }

    private Map<String, Object> compileDefinedSyntax(CompiledObjectClass objectClass, DefinedSyntaxNode syntax,
            Optional<Parameters> maybeParameters) throws ParserException {
        var maybeSyntaxSpec = objectClass.getSyntax();

        if (maybeSyntaxSpec.isEmpty()) {
            throw new CompilerException(syntax.getPosition(),
                    "Object definition for object of object class '%s' uses defined syntax notation which isn't specified",
                    objectClass.getName());
        }

        var syntaxSpec = maybeSyntaxSpec.get();
        var parser = createParser(syntax.getDefinedSyntax());
        var values = new HashMap<String, Object>();

        compile(objectClass, syntaxSpec, parser, values, maybeParameters, new GroupState(false, false, false));

        if (!parser.isEof()) {
            var token = parser.peekToken();

            throw new CompilerException(token.getPosition(), "Unexpected data in defined syntax: %s", token);
        }

        verifyValues(objectClass, syntax.getPosition(), values);

        return values;
    }

    private DefinedSyntaxReParser createParser(String definedSyntax) {
        try {
            return new Parser(new ByteArrayInputStream(definedSyntax.getBytes())).new DefinedSyntaxReParser();
        } catch (IOException e) {
            throw new CompilerException(e);
        }
    }

    private GroupState compile(CompiledObjectClass objectClass, List<? extends Object> syntaxSpec,
            DefinedSyntaxReParser parser, HashMap<String, Object> values, Optional<Parameters> maybeParameters,
            GroupState state) throws ParserException {
        if (parser.isEof()) {
            return state;
        }

        var currentState = state.accepted(false);
        var hasField = false;

        for (var spec : syntaxSpec) {
            if (spec instanceof Group) {
                currentState = compile(objectClass, ((Group) spec).getGroup(), parser, values, maybeParameters,
                        currentState.optional(true));

                hasField = currentState.hasField || hasField;
            } else if (spec instanceof RequiredToken requiredToken) {
                currentState = compileRequiredToken(objectClass, parser, values, maybeParameters, currentState,
                        requiredToken);

                if (!currentState.accepted) {
                    return currentState;
                }

                hasField = currentState.hasField || hasField;
            } else {
                throw new IllegalCompilerStateException(((Node) spec).getPosition(),
                        "Unsupported type in defined syntax: %s", spec);
            }
        }

        if (currentState.optional) {
            if (!hasField) {
                throw new CompilerException("There must be at least one field in an optional group");
            } else {
                currentState = currentState.hasField(true);
            }
        }

        return currentState;
    }

    private GroupState compileRequiredToken(CompiledObjectClass objectClass, DefinedSyntaxReParser parser,
            HashMap<String, Object> values, Optional<Parameters> maybeParameters, GroupState state,
            RequiredToken requiredToken) throws ParserException {
        if (parser.isEof()) {
            return state;
        }

        var token = requiredToken.getToken();

        if (token instanceof LiteralNode literalNodeSpec) {
            var element = parser.parseLiteral();

            if (!(element instanceof LiteralNode literalNode)) {
                var unexpectedToken = parser.peekToken();

                throw new CompilerException(unexpectedToken.getPosition(), "Expected literal '%s' but found '%s'",
                        literalNodeSpec.getText(), unexpectedToken.getText());
            }

            if (!literalNodeSpec.getText().equals(literalNode.getText())) {
                if (state.optional && !state.accepted) {
                    parser.push(element);

                    return state;
                }

                throw new CompilerException(element.getPosition(), "Expected literal '%s' but found '%s'",
                        literalNodeSpec.getText(), literalNode.getText());
            } else {
                state = state.accepted(true);
            }
        } else if (token instanceof PrimitiveFieldNameNode fieldNameNode) {
            var element = parser.parseSetting();

            if (!(element instanceof Setting)) {
                var formattedElement = Formatter.format(ctx, element);

                throw new CompilerException(element.getPosition(), "Expected setting but found '%s'", formattedElement);
            }

            compilePrimitiveFieldName(objectClass, values, maybeParameters, element, fieldNameNode);

            state = state.hasField(true).accepted(true);
        }

        return state;
    }

    private void compilePrimitiveFieldName(CompiledObjectClass objectClass, HashMap<String, Object> values,
            Optional<Parameters> maybeParameters, Setting setting, PrimitiveFieldNameNode fieldNameNode) {
        var fieldName = fieldNameNode.getReference();
        var maybeField = objectClass.getField(fieldName);

        if (maybeField.isEmpty()) {
            throw new IllegalCompilerStateException(fieldNameNode.getPosition(),
                    "Syntax of object class '%s' references the undefined field '%s'. " +
                            "This should never happen, since the existence of fields is verified " +
                            "when an object class is compiled",
                    objectClass.getName(), fieldNameNode);
        }

        var value = compile(objectClass, maybeParameters, setting, fieldNameNode);

        values.put(value.get_1(), value.get_2());
    }

    private Map<String, Object> compileDefaultSyntax(CompiledObjectClass objectClass, DefaultSyntaxNode syntaxNode,
            Optional<Parameters> maybeParameters) {
        var values = syntaxNode.getFieldSetting().stream()
                .map(setting -> compile(objectClass, setting, maybeParameters))
                .collect(Collectors.toMap(Tuple2::get_1, Tuple2::get_2));

        verifyValues(objectClass, syntaxNode.getPosition(), values);

        return values;
    }

    private void verifyValues(CompiledObjectClass objectClass, Position position, Map<String, Object> values) {
        var fields = objectClass.getFields();

        for (var field : fields) {
            if (!field.isOptional()) {
                var fieldName = field.getName();

                if (!values.containsKey(fieldName)) {
                    var maybeDefaultValue = field.getDefaultValue();

                    if (maybeDefaultValue.isPresent()) {
                        values.put(fieldName, maybeDefaultValue.get());
                    } else {
                        throw new CompilerException(position, "Field '%s' is mandatory", fieldName);
                    }
                }
            }
        }
    }

    private Tuple2<String, Object> compile(CompiledObjectClass objectClass, FieldSettingNode fieldSettingNode,
            Optional<Parameters> maybeParameters) {
        var fieldName = fieldSettingNode.getFieldName();
        var setting = fieldSettingNode.getSetting();

        return compile(objectClass, maybeParameters, setting, fieldName);
    }

    private Tuple2<String, Object> compile(CompiledObjectClass objectClass, Optional<Parameters> maybeParameters,
            Setting setting, PrimitiveFieldNameNode fieldName) {
        var reference = fieldName.getReference();
        var maybeField = objectClass.getField(reference);

        if (maybeField.isPresent()) {
            var field = maybeField.get();

            if (field instanceof CompiledFixedTypeValueField) {
                return compileFixedTypeValueField((CompiledFixedTypeValueField) field, reference, setting,
                        maybeParameters);
            } else if (field instanceof CompiledTypeField) {
                return compileTypeField(setting, reference, maybeParameters);
            } else if (field instanceof CompiledVariableTypeValueField) {
                return compileVariableTypeValueField(setting, reference);
            } else if (field instanceof CompiledObjectField) {
                return compileObjectField((CompiledObjectField) field, reference, setting, maybeParameters);
            } else if (field instanceof CompiledObjectSetField) {
                return compileObjectSetField((CompiledObjectSetField) field, reference, setting, maybeParameters);
            } else {
                throw new IllegalCompilerStateException(fieldName.getPosition(), "Unsupported field '%s' of type %s",
                        reference, field.getClass().getSimpleName());
            }
        } else {
            throw new CompilerException(fieldName.getPosition(), "Invalid reference %s", reference);
        }
    }

    private Tuple2<String, Object> compileObjectSetField(CompiledObjectSetField field, String reference,
            Setting setting, Optional<Parameters> maybeParameters) {
        if (setting.getObjectSet().isPresent()) {
            var objectSet = setting.getObjectSet().get();
            var roots = compileElement(objectSet.getRootElements(), maybeParameters);
            var extensions = compileElement(objectSet.getExtensionElements(), maybeParameters);

            roots.addAll(extensions);

            return Tuple2.of(reference, roots);
        } else {
            throw new IllegalCompilerStateException(setting.getPosition(), "Unsupported setting: %s",
                    setting.getClass().getSimpleName());
        }
    }

    private Set<Object> compileElements(ElementSet elementSet, Optional<Parameters> maybeParameters) {
        var operands = elementSet.getOperands();

        switch (elementSet.getOperation()) {
            case UNION:
                return operands.stream().map(e -> compileElement(e, maybeParameters)).flatMap(Collection::stream)
                        .collect(Collectors.toSet());
            case INTERSECTION:
                var result = compileElement(operands.get(0), maybeParameters);

                operands.stream().skip(1).forEach(operand -> result.removeAll(compileElement(operand, maybeParameters)));

                return result;
            default:
                throw new IllegalCompilerStateException(elementSet.getPosition(), "Unsupported operation: %s",
                        elementSet.getOperation());
        }
    }

    private Set<Object> compileElement(Elements elements, Optional<Parameters> maybeParameters) {
        if (elements == null) {
            return Collections.emptySet();
        }

        if (elements instanceof ElementSet elementSet) {
            return compileElements(elementSet, maybeParameters);
        } else if (elements instanceof ObjectSetElements objectSetElements) {
            return compileObjectSetElements(objectSetElements, maybeParameters);
        }

        throw new IllegalCompilerStateException(elements.getPosition(), "Unsupported elements: %s",
                elements.getClass().getSimpleName());

    }

    private Set<Object> compileObjectSetElements(ObjectSetElements objectSetElements, Optional<Parameters> maybeParameters) {
        var element = objectSetElements.getElement();

        if (element instanceof ObjectReference objectReference) {
            return Collections.singleton(ctx.getCompiledObject(objectReference));
        }

        throw new IllegalCompilerStateException(objectSetElements.getPosition(), "Unsupported elements: %s",
                objectSetElements.getClass().getSimpleName());
    }

    private Tuple2<String, Object> compileObjectField(CompiledObjectField field, String reference, Setting setting,
            Optional<Parameters> maybeParameters) {
        Map<String, Object> object;

        if (setting.getValue().isPresent() &&
                setting.getValue().get() instanceof SimpleDefinedValue simpleDefinedValue) {
            object = ctx.getCompiledObject(toObjectReference(simpleDefinedValue)).getObjectDefinition();
        } else if (setting.getObject().isPresent() &&
                setting.getObject().get() instanceof ObjectDefnNode objectDefnNode) {
            var compiler = ctx.<ObjectDefnNode, ObjectDefnCompiler>getCompiler(ObjectDefnNode.class);

            object = compiler.compile(field.getObjectClass(), objectDefnNode,
                    maybeParameters);
        } else {
            throw new IllegalCompilerStateException(setting.getPosition(), "Unsupported setting: %s",
                    setting.getClass().getSimpleName());
        }

        return Tuple2.of(reference, object);
    }

    private Tuple2<String, Object> compileVariableTypeValueField(Setting setting, String reference) {
        if (setting.getValue().isEmpty()) {
            var formattedNode = Formatter.format(ctx, setting);

            throw new CompilerException(setting.getPosition(), "Invalid value: %s", formattedNode);
        }

        var value = setting.getValue().get();

        return Tuple2.of(reference, value);
    }

    private Tuple2<String, Object> compileTypeField(Setting setting, String reference,
            Optional<Parameters> maybeParameters) {
        if (setting.getType().isEmpty()) {
            var formattedNode = Formatter.format(ctx, setting);

            throw new CompilerException(setting.getPosition(), "Invalid type: %s", formattedNode);
        }

        var type = setting.getType().get();

        if (type instanceof TypeFromObject typeFromObject) {
            return Tuple2.of(reference, new TypeFromObjectCompiler().compile(ctx, null, typeFromObject, maybeParameters));
        }

        return Tuple2.of(reference, ctx.getCompiledType(type));
    }

    private Tuple2<String, Object> compileFixedTypeValueField(CompiledFixedTypeValueField field, String reference,
            Setting setting, Optional<Parameters> maybeParameters) {
        if (setting.getValue().isEmpty()) {
            var formattedNode = Formatter.format(ctx, setting);

            throw new CompilerException(setting.getPosition(), "Invalid value: %s", formattedNode);
        }

        var compiledField = field;
        var compiledType = compiledField.getCompiledType();
        var value = setting.getValue().get();
        Value resolvedValue;

        if (value instanceof DefinedValue) {
            resolvedValue = ctx.getCompiledValue(compiledType.getType(), value, maybeParameters).getValue();
        } else {
            var type = compiledType.getType();

            resolvedValue = ctx.getValue(type, value);
        }

        return Tuple2.of(reference, resolvedValue);
    }

    private ObjectReference toObjectReference(SimpleDefinedValue simpleDefinedValue) {
        var position = simpleDefinedValue.getPosition();
        var ref = simpleDefinedValue.getReference();

        if (simpleDefinedValue instanceof ExternalValueReference externalValueReference) {
            var module = externalValueReference.getModule();

            return new ExternalObjectReference(position, module, ref);
        } else {
            return new ObjectReference(position, ref);
        }
    }

    private void verifyIntegrity(CompiledObjectClass objectClass, ObjectDefnNode objectDefinition,
            Map<String, Object> objectFields) {
        for (var field : objectClass.getFields()) {
            if (field instanceof CompiledVariableTypeValueField variableTypeValueField) {
                var fieldName = field.getName();
                var fieldValue = (Value) objectFields.get(fieldName);
                var fieldType = ((CompiledType) objectFields.get(variableTypeValueField.getReference())).getType();

                if (fieldValue instanceof SimpleDefinedValue simpleDefinedValue) {
                    fieldValue = ctx.getCompiledValue(simpleDefinedValue).getValue();
                }

                try {
                    ctx.getValue(fieldType, fieldValue);

                    objectFields.put(fieldName, fieldValue);
                } catch (ValueResolutionException e) {
                    throw new CompilerException(objectDefinition.getPosition(),
                            "The value for %s in the object definition for %s must be of the type %s but found the value %s",
                            fieldName, objectClass.getName(), TypeFormatter.formatType(ctx, fieldType),
                            ValueFormatter.formatValue(fieldValue));
                }
            }
        }
    }

    private static class GroupState {

        boolean optional;

        boolean accepted;

        boolean hasField;

        public GroupState(boolean optional, boolean accepted, boolean hasField) {
            this.optional = optional;
            this.accepted = accepted;
            this.hasField = hasField;
        }

        GroupState accepted(boolean accepted) {
            return new GroupState(optional, accepted, hasField);
        }

        GroupState optional(boolean optional) {
            return new GroupState(optional, accepted, hasField);
        }

        GroupState hasField(boolean hasField) {
            return new GroupState(optional, accepted, hasField);
        }

        @Override
        public String toString() {
            return ToString.get(this);
        }
    }

}
