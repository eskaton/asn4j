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

import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.parser.ast.Node;
import ch.eskaton.asn4j.parser.ast.Quadruple;
import ch.eskaton.asn4j.parser.ast.Tuple;
import ch.eskaton.asn4j.parser.ast.values.AbstractStringValue;
import ch.eskaton.asn4j.parser.ast.values.AmbiguousValue;
import ch.eskaton.asn4j.parser.ast.values.BitStringValue;
import ch.eskaton.asn4j.parser.ast.values.BooleanValue;
import ch.eskaton.asn4j.parser.ast.values.ChoiceValue;
import ch.eskaton.asn4j.parser.ast.values.CollectionOfValue;
import ch.eskaton.asn4j.parser.ast.values.CollectionValue;
import ch.eskaton.asn4j.parser.ast.values.EnumeratedValue;
import ch.eskaton.asn4j.parser.ast.values.ExternalValueReference;
import ch.eskaton.asn4j.parser.ast.values.IRIValue;
import ch.eskaton.asn4j.parser.ast.values.IntegerValue;
import ch.eskaton.asn4j.parser.ast.values.NamedValue;
import ch.eskaton.asn4j.parser.ast.values.NullValue;
import ch.eskaton.asn4j.parser.ast.values.ObjectIdentifierValue;
import ch.eskaton.asn4j.parser.ast.values.OctetStringValue;
import ch.eskaton.asn4j.parser.ast.values.RelativeIRIValue;
import ch.eskaton.asn4j.parser.ast.values.RelativeOIDValue;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.StringValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.commons.ImmutableReference;
import ch.eskaton.commons.Reference;
import ch.eskaton.commons.utils.Dispatcher;

import java.util.function.Function;

import static ch.eskaton.commons.utils.Utils.callWith;

public class ValueFormatter {

    public static final Dispatcher<Value, Class<? extends Value>, Reference<? extends Value>, String> DISPATCHER =
            new Dispatcher<Value, Class<? extends Value>, Reference<? extends Value>, String>()
                    .withComparator((t, u) -> u.isInstance(t))
                    .withException(t -> new CompilerException("Formatter for value of type %s not defined", t));

    static {
        addCase(AmbiguousValue.class, new AmbiguousValueFormatter()::format);
        addCase(BitStringValue.class, new BitStringValueFormatter()::format);
        addCase(BooleanValue.class, new BooleanValueFormatter()::format);
        addCase(CollectionOfValue.class, new CollectionOfValueFormatter()::format);
        addCase(CollectionValue.class, new CollectionValueFormatter()::format);
        addCase(ChoiceValue.class, new ChoiceValueFormatter()::format);
        addCase(EnumeratedValue.class, new EnumeratedValueFormatter()::format);
        addCase(ExternalValueReference.class, new ExternalValueReferenceFormatter()::format);
        addCase(AbstractStringValue.class, new AbstractStringValueFormatter()::format);
        addCase(IntegerValue.class, new IntegerValueFormatter()::format);
        addCase(IRIValue.class, new IRIValueFormatter()::format);
        addCase(NullValue.class, new NullValueFormatter()::format);
        addCase(ObjectIdentifierValue.class, new ObjectIdentifierValueFormatter()::format);
        addCase(OctetStringValue.class, new OctetStringValueFormatter()::format);
        addCase(Quadruple.class, new QuadrupleFormatter()::format);
        addCase(RelativeIRIValue.class, new RelativeIRIValueFormatter()::format);
        addCase(RelativeOIDValue.class, new RelativeOIDValueFormatter()::format);
        addCase(SimpleDefinedValue.class, new SimpleDefinedValueFormatter()::format);
        addCase(StringValue.class, new StringValueFormatter()::format);
        addCase(Tuple.class, new TupleFormatter()::format);
        addCase(NamedValue.class, new NamedValueFormatter()::format);
    }

    private ValueFormatter() {
    }

    public static String formatValue(Node node) {
        if ((node instanceof Value value)) {
            return DISPATCHER.execute(value, new ImmutableReference<>(value));
        }

        throw new CompilerException(node.getPosition(), "Invalid value: %s", node);
    }

    private static <V extends Value> void addCase(Class<V> valueClazz, Function<V, String> formatter) {
        DISPATCHER.withCase(valueClazz,
                maybeArgs -> callWith(args -> formatter.apply((V) args.get()), maybeArgs.get()));
    }

}
