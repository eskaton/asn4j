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

package ch.eskaton.asn4j.compiler.utils;

import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.parser.ast.Node;
import ch.eskaton.asn4j.parser.ast.OIDComponentNode;
import ch.eskaton.asn4j.parser.ast.QuadrupleNode;
import ch.eskaton.asn4j.parser.ast.TupleNode;
import ch.eskaton.asn4j.parser.ast.values.AmbiguousValue;
import ch.eskaton.asn4j.parser.ast.values.BooleanValue;
import ch.eskaton.asn4j.parser.ast.values.CollectionOfValue;
import ch.eskaton.asn4j.parser.ast.values.CollectionValue;
import ch.eskaton.asn4j.parser.ast.values.EnumeratedValue;
import ch.eskaton.asn4j.parser.ast.values.HasStringValue;
import ch.eskaton.asn4j.parser.ast.values.IRIValue;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.ObjectIdentifierValue;
import ch.eskaton.asn4j.parser.ast.values.RelativeIRIValue;
import ch.eskaton.asn4j.parser.ast.values.RelativeOIDValue;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.StringValue;

import java.util.stream.Collectors;

public class ValueFormatter {

    private ValueFormatter() {
    }

    public static String formatValue(Node node) {
        if (node instanceof SimpleDefinedValue value) {
            return value.getValue();
        } else if (node instanceof StringValue value) {
            return value.getCString();
        } else if (node instanceof ObjectIdentifierValue value) {
            return value.getComponents().stream()
                    .map(OIDComponentNode::getId)
                    .map(String::valueOf)
                    .collect(Collectors.joining("."));
        } else if (node instanceof RelativeOIDValue value) {
            return value.getComponents().stream()
                    .map(OIDComponentNode::getId)
                    .map(String::valueOf)
                    .collect(Collectors.joining("."));
        } else if (node instanceof RelativeIRIValue value) {
            return value.getArcIdentifierTexts().stream().collect(Collectors.joining("/"));
        } else if (node instanceof IRIValue value) {
            return "/" + value.getArcIdentifierTexts().stream().collect(Collectors.joining("/"));
        } else if (node instanceof EnumeratedValue value) {
            var id = value.getId();
            var intValue = value.getValue();

            if (id != null && intValue != null) {
                return String.format("%s(%d)", id, intValue);
            } else if (id != null) {
                return id;
            } else {
                return String.valueOf(value.getValue());
            }
        } else if (node instanceof IntegerValue value) {
            return value.getValue().toString();
        } else if (node instanceof BooleanValue value) {
            return value.getValue() ? "TRUE" : "FALSE";
        } else if (node instanceof CollectionOfValue value) {
            return String.format("{%s}", value.getValues().stream()
                    .map(ValueFormatter::formatValue)
                    .collect(Collectors.joining(", ")));
        } else if (node instanceof CollectionValue value) {
            return String.format("{%s}", value.getValues().stream()
                    .map(namedValue -> "%s: %s".formatted(namedValue.getName(), formatValue(namedValue.getValue())))
                    .collect(Collectors.joining(", ")));
        } else if (node instanceof AmbiguousValue value) {
            return value.getValues().stream().map(ValueFormatter::formatValue).collect(Collectors.joining(", "));
        } else if (node instanceof TupleNode value) {
            return String.format("{%s, %s}", value.getColumn(), value.getRow());
        } else if (node instanceof QuadrupleNode value) {
            return String.format("{%s, %s, %s, %s}", value.getGroup(), value.getPlane(), value.getRow(),
                    value.getCell());
        } else if (node instanceof HasStringValue value) {
            return value.getValue();
        }

        throw new IllegalCompilerStateException("Formatter for value of type %s not defined", node.getClass());
    }

}
