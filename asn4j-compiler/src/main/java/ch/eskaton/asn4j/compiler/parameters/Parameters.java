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

import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.parser.ast.AbstractNode;
import ch.eskaton.asn4j.parser.ast.ActualParameter;
import ch.eskaton.asn4j.parser.ast.Node;
import ch.eskaton.asn4j.parser.ast.ParameterNode;
import ch.eskaton.asn4j.runtime.utils.ToString;
import ch.eskaton.commons.collections.Tuple2;
import ch.eskaton.commons.utils.StreamsUtils;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Parameters {

    private final String parameterizedName;

    private final List<ParameterNode> definitions;

    private final List<ActualParameter> values;

    private final Set<ParameterNode> usedParameters = new HashSet<>();

    public Parameters(String parameterizedName, List<ParameterNode> definitions, List<ActualParameter> values) {
        if (definitions.size() != values.size()) {
            throw new IllegalCompilerStateException(
                    "Size of parameter definitions (%d) and parameter values (%d) don't match",
                    definitions.size(), values.size());
        }

        this.parameterizedName = parameterizedName;
        this.definitions = definitions;
        this.values = values;
    }

    public String getParameterizedName() {
        return parameterizedName;
    }

    public List<ParameterNode> getDefinitions() {
        return definitions;
    }

    public List<ActualParameter> getValues() {
        return values;
    }

    public Parameters values(List<ActualParameter> values) {
        return new Parameters(parameterizedName, definitions, values);
    }

    public List<Tuple2<ParameterNode, ActualParameter>> getDefinitionsAndValues() {
        var definitionsStream = getDefinitions().stream();
        var valuesStream = getValues().stream();

        return StreamsUtils.zip(definitionsStream, valuesStream).collect(Collectors.toList());
    }

    public Optional<Tuple2<ParameterNode, ActualParameter>> getDefinitionAndValue(String parameterName) {
        var definitionsStream = getDefinitions().stream();
        var valuesStream = getValues().stream();

        return StreamsUtils.zip(definitionsStream, valuesStream)
                .filter(t -> t.get_1().getReference().getName().equals(parameterName))
                .findFirst();
    }

    public Optional<ParameterNode> getDefinition(String parameterName) {
        var maybeDefinition = getDefinitionAndValue(parameterName);

        return maybeDefinition.map(Tuple2::get_1);
    }

    public Optional<Node> getValue(String parameterName) {
        var maybeDefinition = getDefinitionAndValue(parameterName);

        return maybeDefinition.map(Tuple2::get_2);
    }

    public void markAsUsed(ParameterNode parameter) {
        if (!definitions.contains(parameter)) {
            throw new IllegalCompilerStateException("Parameter doesn't exist: %s", parameter);
        }

        usedParameters.add(parameter);
    }

    public List<ParameterNode> getUnusedParameters() {
        var unusedParameters = new LinkedList<>(definitions);

        unusedParameters.removeAll(usedParameters);

        return unusedParameters.stream()
                .sorted(Comparator.comparing(AbstractNode::getPosition))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public String toString() {
        return ToString.get(this);
    }

}
