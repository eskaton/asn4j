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
import ch.eskaton.asn4j.compiler.il.BinaryBooleanExpression;
import ch.eskaton.asn4j.compiler.il.BooleanExpression;
import ch.eskaton.asn4j.compiler.il.BooleanFunctionCall;
import ch.eskaton.asn4j.compiler.il.BooleanFunctionCall.ArrayEquals;
import ch.eskaton.asn4j.compiler.il.BooleanFunctionCall.SetEquals;
import ch.eskaton.asn4j.compiler.il.Condition;
import ch.eskaton.asn4j.compiler.il.Conditions;
import ch.eskaton.asn4j.compiler.il.Declaration;
import ch.eskaton.asn4j.compiler.il.Expression;
import ch.eskaton.asn4j.compiler.il.Foreach;
import ch.eskaton.asn4j.compiler.il.Function;
import ch.eskaton.asn4j.compiler.il.FunctionCall;
import ch.eskaton.asn4j.compiler.il.FunctionCall.ArrayLength;
import ch.eskaton.asn4j.compiler.il.FunctionCall.BigIntegerCompare;
import ch.eskaton.asn4j.compiler.il.FunctionCall.BitStringSize;
import ch.eskaton.asn4j.compiler.il.FunctionCall.GetSize;
import ch.eskaton.asn4j.compiler.il.FunctionCall.ToArray;
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
import ch.eskaton.asn4j.compiler.utils.BitStringUtils;
import ch.eskaton.asn4j.parser.ast.values.CollectionOfValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.types.ASN1Null;
import ch.eskaton.commons.utils.CollectionUtils;
import ch.eskaton.commons.utils.StreamsUtils;
import ch.eskaton.commons.utils.StringUtils;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
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
                throw new CompilerException("Unimplemented case: " + visibility);
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
            throw new CompilerException("Unhandled statements type: %s",
                    statement.getClass().getSimpleName());
        }
    }

    private String translateExpression(CompilerContext ctx, JavaClass javaClass, Expression expression) {
        if (expression instanceof BooleanExpression) {
            return translateBooleanExpression(ctx, javaClass, (BooleanExpression) expression);
        } else if (expression instanceof Variable) {
            return ((Variable) expression).getName();
        } else if (expression instanceof ILValue) {
            Object value = ((ILValue) expression).getValue();

            if (value instanceof byte[]) {
                return BitStringUtils.getInitializerString((byte[]) value);
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
                return String.valueOf(value) + "L";
            } else if (value instanceof CollectionOfValue) {
                List<Value> values = ((CollectionOfValue) value).getValues();

                String initString = values.stream()
                        .map(v -> getInitializerString(ctx, ((ILValue) expression).getTypeName().get(), v))
                        .collect(Collectors.joining(", "));

                javaClass.addStaticImport(Arrays.class, "asList");

                return "asList(" + initString + ")";
            }

            return String.valueOf(value);
        } else if (expression instanceof FunctionCall) {
            FunctionCall functionCall = (FunctionCall) expression;
            String function;
            Optional<String> object;
            String arguments = "";

            if (functionCall instanceof BigIntegerCompare) {
                function = "compareTo";
                object = ofNullable(translateArg(ctx, javaClass, functionCall, 0));
                arguments = "BigInteger.valueOf(" + translateArg(ctx, javaClass, functionCall, 1) + ")";
            } else if (functionCall instanceof ArrayLength) {
                return translateArg(ctx, javaClass, functionCall, 0) + ".length";
            } else if (functionCall instanceof BitStringSize) {
                return "ASN1BitString.getSize(" + translateArg(ctx, javaClass, functionCall, 0) + ", " +
                        translateArg(ctx, javaClass, functionCall, 1) + ")";
            } else if (functionCall instanceof GetSize) {
                object = ofNullable(translateArg(ctx, javaClass, functionCall, 0));
                function = "size";
            } else if (functionCall instanceof ToArray) {
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
                        throw new CompilerException("Unsupported type %s in ToString function");
                }
            } else {
                function = functionCall.getFunction()
                        .orElseThrow(() -> new CompilerException("Undefined function of type %s",
                                functionCall.getClass().getSimpleName()));
                object = functionCall.getObject().map(o -> translateExpression(ctx, javaClass, o));

                arguments = StreamsUtils.fromIndex(functionCall.getArguments(), 0)
                        .map(expr -> translateExpression(ctx, javaClass, expr))
                        .collect(joining(", "));
            }

            return object.map(o -> o + ".").orElse("") + function + "(" + arguments + ")";
        } else {
            throw new CompilerException("Unhandled expression type: %s",
                    expression.getClass().getSimpleName());
        }
    }

    private String translateArg(CompilerContext ctx, JavaClass javaClass, FunctionCall functionCall, int arg) {
        return translateExpression(ctx, javaClass, functionCall.getArguments().get(arg));
    }

    private String translateBooleanExpression(CompilerContext ctx, JavaClass javaClass, BooleanExpression booleanExpression) {
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
                case GE:
                    operator = " >= ";
                    break;
                case LE:
                    operator = " <= ";
                    break;
                default:
                    throw new CompilerException("Unhandled operator type: %s",
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
            } else if (functionCall instanceof SetEquals) {
                String object = translateArg(ctx, javaClass, functionCall, 0);
                String argument = translateArg(ctx, javaClass, functionCall, 1);

                return object + ".equals(" + argument + ")";

            } else {
                String function = functionCall.getFunction()
                        .orElseThrow(() -> new CompilerException("Undefined function of type %s",
                                functionCall.getClass().getSimpleName()));
                Optional<String> object = functionCall.getObject().map(obj -> translateExpression(ctx, javaClass, obj));
                String arguments = argumentsToString(ctx, javaClass, functionCall);

                return object.map(obj -> obj + ".").orElse("") + function + "(" + arguments + ")";
            }
        } else {
            throw new CompilerException("Unhandled boolean expression type: %s",
                    booleanExpression.getClass().getSimpleName());
        }
    }

    private String argumentsToString(CompilerContext ctx, JavaClass javaClass, BooleanFunctionCall functionCall) {
        return functionCall.getArguments().stream()
                .map(expr -> translateExpression(ctx, javaClass, expr))
                .collect(joining(", "));
    }

    private String toJavaType(JavaClass javaClass, ILType type) {
        switch (type.getBaseType()) {
            case BOOLEAN:
                return boolean.class.getSimpleName();
            case BIG_INTEGER:
                return typeWithImport(javaClass, BigInteger.class);
            case BYTE_ARRAY:
                return byte[].class.getSimpleName();
            case NULL:
                return typeWithImport(javaClass, ASN1Null.Value.class);
            case INTEGER:
                return int.class.getSimpleName();
            case INTEGER_ARRAY:
                return int[].class.getSimpleName();
            case LIST:
                return typeWithImport(javaClass, List.class) + getTypeParameter(type);
            case SET:
                return typeWithImport(javaClass, Set.class) + getTypeParameter(type);
            case STRING:
                return String.class.getSimpleName();
            case STRING_ARRAY:
                return String[].class.getSimpleName();
            case CUSTOM:
                return getTypeString((ILParameterizedType) type);
            default:
                throw new CompilerException("Unimplemented case: " + type);
        }
    }

    private String getTypeParameter(ILType type) {
        return type instanceof ILParameterizedType ?
                "<" + getTypeString((ILParameterizedType) type) + ">" : "";
    }

    private String getTypeString(ILParameterizedType type) {
        return CompilerUtils.getTypeParameterString(type.getTypeParameter());
    }

    private String typeWithImport(JavaClass javaClass, Class<?> clazz) {
        javaClass.addImport(clazz);

        return clazz.getSimpleName();
    }

}
