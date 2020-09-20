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

package ch.eskaton.asn4j.compiler.values.formatters;

import ch.eskaton.asn4j.compiler.IllegalCompilerStateException;
import ch.eskaton.asn4j.parser.ast.Node;
import ch.eskaton.asn4j.parser.ast.Quadruple;
import ch.eskaton.asn4j.parser.ast.Tuple;
import ch.eskaton.asn4j.parser.ast.values.AmbiguousValue;
import ch.eskaton.asn4j.parser.ast.values.BitStringValue;
import ch.eskaton.asn4j.parser.ast.values.BooleanValue;
import ch.eskaton.asn4j.parser.ast.values.CollectionOfValue;
import ch.eskaton.asn4j.parser.ast.values.CollectionValue;
import ch.eskaton.asn4j.parser.ast.values.EnumeratedValue;
import ch.eskaton.asn4j.parser.ast.values.ExternalValueReference;
import ch.eskaton.asn4j.parser.ast.values.HasStringValue;
import ch.eskaton.asn4j.parser.ast.values.IRIValue;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.NullValue;
import ch.eskaton.asn4j.parser.ast.values.ObjectIdentifierValue;
import ch.eskaton.asn4j.parser.ast.values.OctetStringValue;
import ch.eskaton.asn4j.parser.ast.values.RelativeIRIValue;
import ch.eskaton.asn4j.parser.ast.values.RelativeOIDValue;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.StringValue;

public class ValueFormatter {

    private ValueFormatter() {
    }

    public static String formatValue(Node node) {
        if (node instanceof SimpleDefinedValue value) {
            return new SimpleDefinedValueFormatter().format(value);
        } else if (node instanceof ExternalValueReference value) {
            return new ExternalValueReferenceFormatter().format(value);
        } else if (node instanceof StringValue value) {
            return new StringValueFormatter().format(value);
        } else if (node instanceof NullValue value) {
            return new NullValueFormatter().format(value);
        } else if (node instanceof ObjectIdentifierValue value) {
            return new ObjectIdentifierValueFormatter().format(value);
        } else if (node instanceof RelativeOIDValue value) {
            return new RelativeOIDValueFormatter().format(value);
        } else if (node instanceof RelativeIRIValue value) {
            return new RelativeIRIValueFormatter().format(value);
        } else if (node instanceof IRIValue value) {
            return new IRIValueFormatter().format(value);
        } else if (node instanceof EnumeratedValue value) {
            return new EnumeratedValueFormatter().format(value);
        } else if (node instanceof OctetStringValue value) {
            return new OctetStringValueFormatter().format(value);
        } else if (node instanceof BitStringValue value) {
            return new BitStringValueFormatter().format(value);
        } else if (node instanceof IntegerValue value) {
            return new IntegerValueFormatter().format(value);
        } else if (node instanceof BooleanValue value) {
            return new BooleanValueFormatter().format(value);
        } else if (node instanceof CollectionOfValue value) {
            return new CollectionOfValueFormatter().format(value);
        } else if (node instanceof CollectionValue value) {
            return new CollectionValueFormatter().format(value);
        } else if (node instanceof AmbiguousValue value) {
            return new AmbiguousValueFormatter().format(value);
        } else if (node instanceof Tuple value) {
            return new TupleNodeFormatter().format(value);
        } else if (node instanceof Quadruple value) {
            return new QuadrupleNodeFormatter().format(value);
        } else if (node instanceof HasStringValue value) {
            return new HasStringValueFormatter().format(value);
        }

        throw new IllegalCompilerStateException("Formatter for value of type %s not defined", node.getClass());
    }

}
