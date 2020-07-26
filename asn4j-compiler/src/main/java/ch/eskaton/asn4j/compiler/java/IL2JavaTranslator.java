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

package ch.eskaton.asn4j.compiler.java;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.CompilerUtils;
import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.compiler.il.BinaryBooleanExpression;
import ch.eskaton.asn4j.compiler.il.BooleanExpression;
import ch.eskaton.asn4j.compiler.il.BooleanFunctionCall;
import ch.eskaton.asn4j.compiler.il.BooleanFunctionCall.ArrayEquals;
import ch.eskaton.asn4j.compiler.il.BooleanFunctionCall.MapEquals;
import ch.eskaton.asn4j.compiler.il.BooleanFunctionCall.SetEquals;
import ch.eskaton.asn4j.compiler.il.BooleanFunctionCall.StringEquals;
import ch.eskaton.asn4j.compiler.il.Condition;
import ch.eskaton.asn4j.compiler.il.Conditions;
import ch.eskaton.asn4j.compiler.il.Declaration;
import ch.eskaton.asn4j.compiler.il.Expression;
import ch.eskaton.asn4j.compiler.il.Foreach;
import ch.eskaton.asn4j.compiler.il.Function;
import ch.eskaton.asn4j.compiler.il.FunctionCall;
import ch.eskaton.asn4j.compiler.il.FunctionCall.GetMapValue;
import ch.eskaton.asn4j.compiler.il.FunctionCall.ToArray;
import ch.eskaton.asn4j.compiler.il.ILListValue;
import ch.eskaton.asn4j.compiler.il.ILMapValue;
import ch.eskaton.asn4j.compiler.il.ILParameterizedType;
import ch.eskaton.asn4j.compiler.il.ILType;
import ch.eskaton.asn4j.compiler.il.ILValue;
import ch.eskaton.asn4j.compiler.il.ILVisibility;
import ch.eskaton.asn4j.compiler.il.NegationExpression;
import ch.eskaton.asn4j.compiler.il.Parameter;
import ch.eskaton.asn4j.compiler.il.ReturnStatement;
import ch.eskaton.asn4j.compiler.il.Statement;
import ch.eskaton.asn4j.compiler.il.Variable;
import ch.eskaton.asn4j.compiler.java.objs.JavaClass;
import ch.eskaton.asn4j.compiler.java.objs.JavaVisibility;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.types.ASN1Null;
import ch.eskaton.asn4j.runtime.utils.ConstraintChecks;
import ch.eskaton.commons.collections.Maps;
import ch.eskaton.commons.utils.StreamsUtils;
import ch.eskaton.commons.utils.StringUtils;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ch.eskaton.asn4j.compiler.java.JavaUtils.getInitializerString;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

public class IL2JavaTranslator {

    public JavaVisibility translateVisibility(ILVisibility visibility) {
        switch (visibility) {
            case PRIVATE:
                return JavaVisibility.PRIVATE;
            case PROTECTED:
                return JavaVisibility.PROTECTED;
            case PUBLIC:
                return JavaVisibility.PUBLIC;
            default:
                throw new IllegalCompilerStateException("Unimplemented case: " + visibility);
        }
    }

    public void translateFunction(CompilerContext ctx, JavaClass javaClass, Function function) {
        JavaClass.MethodBuilder method = javaClass.method()
                .modifier(translateVisibility(function.getVisibility()))
                .name(function.getName())
                .returnType(toJavaType(javaClass, function.getReturnType()));

        if (function.isOverriden()) {
            method.annotation(Override.class);
        }

        for (Parameter parameter : function.getParameters()) {
            method.parameter(toJavaType(javaClass, parameter.getType()), parameter.getName());
        }

        JavaClass.BodyBuilder body = method.body();

        for (Statement statement : function.getStatements()) {
            body.append(translateStatement(ctx, javaClass, statement));
        }

        body.finish().build();
    }

    private String translateStatement(CompilerContext ctx, JavaClass javaClass, Statement statement) {
        if (statement instanceof ReturnStatement) {
            Expression expression = ((ReturnStatement) statement).getExpression();

            return "return " + translateExpression(ctx, javaClass, expression) + ";";
        } else if (statement instanceof Conditions) {
            StringBuilder code = new StringBuilder();
            boolean isElse = false;

            for (Condition condition : ((Conditions) statement).getConditions()) {
                if (isElse) {
                    code.append(" else ");
                }

                if (condition.getExpression() != null) {
                    code.append("if (").append(translateBooleanExpression(ctx, javaClass, condition.getExpression()))
                            .append(") {");
                } else {
                    code.append("{");
                }

                condition.getStatements().forEach(stmt -> code.append(translateStatement(ctx, javaClass, stmt)));

                code.append("}");

                isElse = true;
            }

            return code.toString();
        } else if (statement instanceof Foreach) {
            Foreach foreach = (Foreach) statement;
            Declaration declaration = foreach.getDeclaration();
            Expression expression = foreach.getExpresssion();
            StringBuilder code = new StringBuilder();

            code.append("for (")
                    .append(toJavaType(javaClass, declaration.getType()))
                    .append(" ")
                    .append(declaration.getVariable().getName())
                    .append(" : ")
                    .append(translateExpression(ctx, javaClass, expression))
                    .append(") {");

            foreach.getStatements().stream().forEach(stmt -> code.append(translateStatement(ctx, javaClass, stmt)));

            code.append("}");

            return code.toString();
        } else {
            throw new IllegalCompilerStateException("Unhandled statements type: %s",
                    statement.getClass().getSimpleName());
        }
    }

    private String translateExpression(CompilerContext ctx, JavaClass javaClass, Expression expression) {
        if (expression instanceof BooleanExpression) {
            return translateBooleanExpression(ctx, javaClass, (BooleanExpression) expression);
        } else if (expression instanceof Variable) {
            return ((Variable) expression).getName();
        } else if (expression instanceof ILValue) {
            ILValue ilValue = (ILValue) expression;
            Object value = ilValue.getValue();

            if (value instanceof byte[]) {
                var byteValue = (byte[]) value;
                var bytesStr = IntStream.range(0, byteValue.length).boxed()
                        .map(i -> String.format("(byte) 0x%02x", byteValue[i]))
                        .collect(Collectors.joining(", "));

                return "new byte[] { " + bytesStr + " }";
            } else if (value instanceof int[]) {
                return "new int[] {" + IntStream.range(0, ((int[]) value).length).boxed().map(
                        i -> String.format("%d", ((int[]) value)[i])).collect(Collectors.joining(", "))
                        + "}";
            } else if (value instanceof String[]) {
                return "new String[] {" + Arrays.stream((String[]) value).map(StringUtils::dquote)
                        .collect(Collectors.joining(", ")) + "}";
            } else if (value instanceof ASN1Null.Value && ASN1Null.Value.NULL.equals(value)) {
                javaClass.addStaticImport(ASN1Null.Value.class, "NULL");
            } else if (value instanceof Long || value instanceof BigInteger) {
                return value + "L";
            } else if (value instanceof Integer) {
                return "Integer.valueOf(" + value + ")";
            } else if (value instanceof Value) {
                String typeName = ilValue.getTypeName().orElseThrow(
                        () -> new CompilerException("Type name expected for value: %s", ilValue.getValue()));

                return getInitializerString(ctx, typeName, (Value) value);
            } else if (value instanceof String) {
                return StringUtils.dquote((String) value);
            } else if (value instanceof Boolean) {
                return (boolean) value ? "Boolean.TRUE" : "Boolean.FALSE";
            }

            return String.valueOf(value);
        } else if (expression instanceof ILListValue) {
            var values = ((ILListValue) expression).getValue();
            var initString = values.stream().map(expr -> translateExpression(ctx, javaClass, expr))
                    .collect(joining(", "));

            javaClass.addStaticImport(Arrays.class, "asList");

            return "asList(" + initString + ")";
        } else if (expression instanceof ILMapValue) {
            var keyType = toJavaType(javaClass, ((ILMapValue) expression).getKeyType());
            var valueType = toJavaType(javaClass, ((ILMapValue) expression).getValueType());
            var associations = ((ILMapValue) expression).getValue();

            return "Maps.<%s, %s>builder()".formatted(keyType, valueType) + associations.stream()
                    .map(t -> ".put(" + translateExpression(ctx, javaClass, t.get_1()) + ", " +
                            translateExpression(ctx, javaClass, t.get_2()) + ")")
                    .collect(Collectors.joining("")) + ".build()";
        } else if (expression instanceof FunctionCall) {
            FunctionCall functionCall = (FunctionCall) expression;
            String function;
            Optional<String> object;
            String arguments = "";

            if (functionCall instanceof ToArray) {
                object = ofNullable(translateExpression(ctx, javaClass,
                        functionCall.getObject().orElseThrow(() -> new CompilerException("object must not be null"))));
                function = "toArray";
                ILType type = ((ToArray) functionCall).getType();
                String javaType = toJavaType(javaClass, type);

                switch (type.getBaseType()) {
                    case INTEGER:
                        function = "stream().mapToInt(Integer::intValue)." + function;
                        break;
                    case STRING:
                        arguments = "new " + javaType + "[] {}";
                        break;
                    default:
                        throw new IllegalCompilerStateException("Unsupported type %s in ToString function",
                                type.getBaseType());
                }
            } else if (functionCall instanceof GetMapValue) {
                var type = toJavaType(javaClass, ((GetMapValue) functionCall).getType());
                function = "get";
                object = ofNullable(translateExpression(ctx, javaClass,
                        functionCall.getObject().orElseThrow(() -> new CompilerException("object must not be null"))));
                arguments = translateExpression(ctx, javaClass, functionCall.getArguments().get(0));

                return "((" + type + ")" + object.map(o -> o + ".").orElse("") + function + "(" + arguments + "))";
            } else {
                function = functionCall.getFunction()
                        .orElseThrow(() -> new CompilerException("Undefined function of type %s",
                                functionCall.getClass().getSimpleName()));
                object = functionCall.getObject().map(o -> translateExpression(ctx, javaClass, o));

                arguments = StreamsUtils.fromIndex(functionCall.getArguments(), 0)
                        .map(expr -> translateExpression(ctx, javaClass, expr))
                        .collect(joining(", "));
            }

            if (object.isPresent()) {
                return "(" + object.get() + " != null ? " + object.get() + "." + function + "(" + arguments + ")" + ": null" + ")";
            } else {
                return function + "(" + arguments + ")";
            }
        } else {
            throw new IllegalCompilerStateException("Unhandled expression type: %s",
                    expression.getClass().getSimpleName());
        }
    }

    private String translateArg(CompilerContext ctx, JavaClass javaClass, FunctionCall functionCall, int arg) {
        return translateExpression(ctx, javaClass, functionCall.getArguments().get(arg));
    }

    private String translateBooleanExpression(CompilerContext ctx, JavaClass javaClass,
            BooleanExpression booleanExpression) {
        if (booleanExpression instanceof BinaryBooleanExpression) {
            BinaryBooleanExpression binaryBooleanExpression = (BinaryBooleanExpression) booleanExpression;
            String operator;

            switch (binaryBooleanExpression.getOperator()) {
                case OR:
                    operator = " || ";
                    break;
                case AND:
                    operator = " && ";
                    break;
                case EQ:
                    operator = " == ";
                    break;
                case NE:
                    operator = " != ";
                    break;
                case GE:
                    operator = " >= ";
                    break;
                case LE:
                    operator = " <= ";
                    break;
                default:
                    throw new IllegalCompilerStateException("Unhandled operator type: %s",
                            binaryBooleanExpression.getOperator());
            }

            return "(" + binaryBooleanExpression.getExpressions().stream()
                    .map(expr -> translateExpression(ctx, javaClass, expr))
                    .collect(Collectors.joining(operator)) + ")";
        } else if (booleanExpression instanceof NegationExpression) {
            return "(!(" + translateExpression(ctx, javaClass,
                    ((NegationExpression) booleanExpression).getExpression()) + "))";
        } else if (booleanExpression instanceof BooleanFunctionCall) {
            BooleanFunctionCall functionCall = (BooleanFunctionCall) booleanExpression;

            if (functionCall instanceof ArrayEquals) {
                String arguments = argumentsToString(ctx, javaClass, functionCall);

                javaClass.addImport(Arrays.class);

                return "Arrays.equals(" + arguments + ")";
            } else if (functionCall instanceof SetEquals || functionCall instanceof MapEquals ||
                    functionCall instanceof StringEquals) {
                String object = translateArg(ctx, javaClass, functionCall, 0);
                String argument = translateArg(ctx, javaClass, functionCall, 1);

                return object + ".equals(" + argument + ")";
            } else if (functionCall instanceof BooleanFunctionCall.CheckStringMinLength) {
                return getCheckCall(ctx, javaClass, functionCall, "checkMinLength");
            } else if (functionCall instanceof BooleanFunctionCall.CheckStringMaxLength) {
                return getCheckCall(ctx, javaClass, functionCall, "checkMaxLength");
            } else if (functionCall instanceof BooleanFunctionCall.CheckStringLengthEquals) {
                return getCheckCall(ctx, javaClass, functionCall, "checkLengthEquals");
            } else if (functionCall instanceof BooleanFunctionCall.CheckUCStringMinLength) {
                return getCheckCall(ctx, javaClass, functionCall, "checkUCMinLength");
            } else if (functionCall instanceof BooleanFunctionCall.CheckUCStringMaxLength) {
                return getCheckCall(ctx, javaClass, functionCall, "checkUCMaxLength");
            } else if (functionCall instanceof BooleanFunctionCall.CheckUCStringLengthEquals) {
                return getCheckCall(ctx, javaClass, functionCall, "checkUCLengthEquals");
            } else if (functionCall instanceof BooleanFunctionCall.CheckBitStringMinLength) {
                return getCheckCall(ctx, javaClass, functionCall, "checkMinLength");
            } else if (functionCall instanceof BooleanFunctionCall.CheckBitStringMaxLength) {
                return getCheckCall(ctx, javaClass, functionCall, "checkMaxLength");
            } else if (functionCall instanceof BooleanFunctionCall.CheckBitStringLengthEquals) {
                return getCheckCall(ctx, javaClass, functionCall, "checkLengthEquals");
            } else if (functionCall instanceof BooleanFunctionCall.CheckOctetStringMinLength) {
                return getCheckCall(ctx, javaClass, functionCall, "checkMinLength");
            } else if (functionCall instanceof BooleanFunctionCall.CheckOctetStringMaxLength) {
                return getCheckCall(ctx, javaClass, functionCall, "checkMaxLength");
            } else if (functionCall instanceof BooleanFunctionCall.CheckOctetStringLengthEquals) {
                return getCheckCall(ctx, javaClass, functionCall, "checkLengthEquals");
            } else if (functionCall instanceof BooleanFunctionCall.CheckLowerBound) {
                return getCheckCall(ctx, javaClass, functionCall, "checkLowerBound");
            } else if (functionCall instanceof BooleanFunctionCall.CheckUpperBound) {
                return getCheckCall(ctx, javaClass, functionCall, "checkUpperBound");
            } else if (functionCall instanceof BooleanFunctionCall.CheckEquals) {
                return getCheckCall(ctx, javaClass, functionCall, "checkEquals");
            } else if (functionCall instanceof BooleanFunctionCall.CheckCollectionMinSize) {
                return getCheckCall(ctx, javaClass, functionCall, "checkMinSize");
            } else if (functionCall instanceof BooleanFunctionCall.CheckCollectionMaxSize) {
                return getCheckCall(ctx, javaClass, functionCall, "checkMaxSize");
            } else if (functionCall instanceof BooleanFunctionCall.CheckCollectionSizeEquals) {
                return getCheckCall(ctx, javaClass, functionCall, "checkSizeEquals");
            } else {
                String function = functionCall.getFunction()
                        .orElseThrow(() -> new CompilerException("Undefined function of type %s",
                                functionCall.getClass().getSimpleName()));
                Optional<String> object = functionCall.getObject().map(obj -> translateExpression(ctx, javaClass, obj));
                String arguments = argumentsToString(ctx, javaClass, functionCall);

                return object.map(obj -> obj + ".").orElse("") + function + "(" + arguments + ")";
            }
        } else {
            throw new IllegalCompilerStateException("Unhandled boolean expression type: %s",
                    booleanExpression.getClass().getSimpleName());
        }
    }

    private String getCheckCall(CompilerContext ctx, JavaClass javaClass, BooleanFunctionCall functionCall, String method) {
        String arguments = StreamsUtils.fromIndex(functionCall.getArguments(), 0)
                .map(expr -> translateExpression(ctx, javaClass, expr))
                .collect(joining(", "));

        javaClass.addStaticImport(ConstraintChecks.class, method);

        return String.format("%s(%s)", method, arguments);
    }

    private String argumentsToString(CompilerContext ctx, JavaClass javaClass, BooleanFunctionCall functionCall) {
        return Optional.ofNullable(functionCall.getArguments()).map(arguments -> arguments.stream()
                .map(expr -> translateExpression(ctx, javaClass, expr))
                .collect(joining(", "))).orElse("");
    }

    private String toJavaType(JavaClass javaClass, ILType type) {
        if (type == null) {
            return void.class.getSimpleName();
        }

        switch (type.getBaseType()) {
            case BOOLEAN:
                return Boolean.class.getSimpleName();
            case BIG_INTEGER:
                return typeWithImport(javaClass, BigInteger.class);
            case BYTE_ARRAY:
                return byte[].class.getSimpleName();
            case NULL:
                return typeWithImport(javaClass, ASN1Null.Value.class);
            case INTEGER:
                return Integer.class.getSimpleName();
            case INTEGER_ARRAY:
                return int[].class.getSimpleName();
            case LIST:
                return typeWithImport(javaClass, List.class) + getTypeParameter(type);
            case MAP:
                return typeWithImport(javaClass, Map.class, Maps.class) + getTypeParameter(type);
            case SET:
                return typeWithImport(javaClass, Set.class) + getTypeParameter(type);
            case STRING:
                return String.class.getSimpleName();
            case STRING_ARRAY:
                return String[].class.getSimpleName();
            case CUSTOM:
                return getTypeString((ILParameterizedType) type);
            default:
                throw new IllegalCompilerStateException("Unimplemented case: %s", type);
        }
    }

    private String getTypeParameter(ILType type) {
        return type instanceof ILParameterizedType ?
                "<" + getTypeString((ILParameterizedType) type) + ">" : "";
    }

    private String getTypeString(ILParameterizedType type) {
        return type.getTypeParameters().stream()
                .map(CompilerUtils::getTypeParameterString)
                .collect(Collectors.joining(", "));
    }

    private String typeWithImport(JavaClass javaClass, Class<?> clazz, Class<?>... additionalClasses) {
        javaClass.addImport(clazz);

        Arrays.asList(additionalClasses).forEach(javaClass::addImport);

        return clazz.getSimpleName();
    }

}
