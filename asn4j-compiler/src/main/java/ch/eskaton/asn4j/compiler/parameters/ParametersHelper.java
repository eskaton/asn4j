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

package ch.eskaton.asn4j.compiler.parameters;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.results.AbstractCompiledParameterizedResult;
import ch.eskaton.asn4j.compiler.types.formatters.TypeFormatter;
import ch.eskaton.asn4j.compiler.values.ValueResolutionException;
import ch.eskaton.asn4j.compiler.values.formatters.ValueFormatter;
import ch.eskaton.asn4j.parser.ast.ActualParameter;
import ch.eskaton.asn4j.parser.ast.DummyGovernor;
import ch.eskaton.asn4j.parser.ast.HasPosition;
import ch.eskaton.asn4j.parser.ast.ObjectClassNode;
import ch.eskaton.asn4j.parser.ast.ParameterNode;
import ch.eskaton.asn4j.parser.ast.ParameterizedNode;
import ch.eskaton.asn4j.parser.ast.ReferenceNode;
import ch.eskaton.asn4j.parser.ast.types.Null;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.values.NullValue;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.commons.collections.Tuple2;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ch.eskaton.asn4j.compiler.parameters.ParameterGovernorHelper.getParameterType;
import static ch.eskaton.asn4j.compiler.parameters.ParameterPredicates.isObjectClassParameter;
import static ch.eskaton.asn4j.compiler.parameters.ParameterPredicates.isTypeParameter;
import static ch.eskaton.asn4j.compiler.parameters.ParameterPredicates.isValueParameter;

public class ParametersHelper {

    private ParametersHelper() {
    }

    public static Optional<Type> getTypeParameter(Parameters parameters, String reference) {
        var maybeParameter = parameters.getDefinitionsAndValues().stream()
                .filter(tuple -> isTypeParameter(tuple.get_1(), reference))
                .findAny();

        if (maybeParameter.isPresent()) {
            var parameter = maybeParameter.get();
            var node = parameter.get_2();
            var maybeType = node.getType();

            if (maybeType.isPresent()) {
                var type = maybeType.get();

                parameters.markAsUsed(parameter.get_1());

                return Optional.of(type);
            }
        }

        return Optional.empty();
    }

    public static Optional<ObjectClassNode> getObjectClassParameter(Parameters parameters, String reference) {
        var maybeParameter = parameters.getDefinitionsAndValues().stream().
                filter(tuple -> isObjectClassParameter(tuple.get_1(), reference))
                .findAny();

        if (maybeParameter.isPresent()) {
            var parameter = maybeParameter.get();
            var actualParameter = parameter.get_2();
            var maybeObjectClass = actualParameter.getObjectClassReference();

            if (maybeObjectClass.isPresent()) {
                var objectClassNode = maybeObjectClass.get();

                parameters.markAsUsed(parameter.get_1());

                return Optional.of(objectClassNode);
            }
        }

        return Optional.empty();
    }

    public static Optional<Value> getValueParameter(CompilerContext ctx, Parameters parameters,
            SimpleDefinedValue simpleDefinedValue) {
        var maybeParameter = parameters.getDefinitionsAndValues().stream().
                filter(tuple -> isValueParameter(ctx, parameters, tuple.get_1(), simpleDefinedValue))
                .findAny();

        if (maybeParameter.isPresent()) {
            var parameter = maybeParameter.get();
            var parameterDefinition = parameter.get_1();
            var parameterValue = parameter.get_2();

            parameterValue = handleNull(ctx, parameters, parameterDefinition, parameterValue);

            var maybeValue = parameterValue.getValue();

            if (maybeValue.isPresent()) {
                var value = maybeValue.get();
                var governor = parameterDefinition.getGovernor();
                var expectedType = getParameterType(ctx, parameters, governor);

                if (expectedType == null) {
                    var formattedValue = ValueFormatter.formatValue(value);

                    throw new CompilerException(governor.getPosition(),
                            "Failed to resolve resolve the type for parameter value: %s", formattedValue);
                }

                try {
                    // verify that the value is of the expected type
                    ctx.getValue(expectedType, value);

                    parameters.markAsUsed(parameterDefinition);

                    return Optional.of(value);
                } catch (ValueResolutionException e) {
                    var formattedType = TypeFormatter.formatType(ctx, expectedType);
                    var formattedValue = ValueFormatter.formatValue(value);

                    throw new CompilerException(parameterValue.getPosition(),
                            "Expected a value of type %s but found: %s", formattedType, formattedValue);
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Converts a null type to a null value if the parameter has a governor of type null. Since the token NULL is
     * ambiguous in an ActualParameterList the parser always treats it as a type.
     *
     * @param ctx                 The compiler context
     * @param parameters          A parameters object containing all parameters
     * @param parameterDefinition The parameter definition
     * @param parameterValue      The parameter value
     * @return The original parameter value or a null value if the governor is a null type
     */
    private static ActualParameter handleNull(CompilerContext ctx, Parameters parameters,
            ParameterNode parameterDefinition, ActualParameter parameterValue) {
        var maybeType = parameterValue.getType();

        if (maybeType.isPresent() && maybeType.get() instanceof Null && parameterDefinition.getGovernor() != null) {
            var governor = parameterDefinition.getGovernor();
            var type = getParameterType(ctx, parameters, governor);

            if (type instanceof Null) {
                var position = parameterValue.getPosition();
                var actualParameter = new ActualParameter(position);

                actualParameter.setValue(new NullValue(position));

                return actualParameter;
            }
        }

        return parameterValue;
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
            var parameterNode = definitionAndValue.get_1();
            var actualParameter = definitionAndValue.get_2();

            if (parameterNode.getGovernor() instanceof DummyGovernor governor) {
                var maybeParameter = inputParameters.getDefinitionAndValue(governor);

                updateParameter(inputParameters, definitionAndValue, maybeParameter);
            } else {
                var maybeType = actualParameter.getType();

                if (maybeType.isPresent() && maybeType.get() instanceof TypeReference paramReference) {
                    var paramReferenceName = paramReference.getType();
                    var maybeParameter = inputParameters.getDefinitionAndValue(paramReferenceName);

                    updateParameter(inputParameters, definitionAndValue, maybeParameter);
                }
            }

            return definitionAndValue.get_2();
        }).collect(Collectors.toList());

        return outputParameters.values(parameterValues);
    }

    private static void updateParameter(Parameters inputParameters,
            Tuple2<ParameterNode, ActualParameter> definitionAndValue,
            Optional<Tuple2<ParameterNode, ActualParameter>> maybeParameter) {
        if (maybeParameter.isPresent()) {
            var parameter = maybeParameter.get();

            definitionAndValue.set_2(parameter.get_2());
            inputParameters.markAsUsed(parameter.get_1());
        }
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
