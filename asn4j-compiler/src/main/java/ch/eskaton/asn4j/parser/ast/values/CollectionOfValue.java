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

package ch.eskaton.asn4j.parser.ast.values;

import ch.eskaton.asn4j.parser.Position;
import ch.eskaton.asn4j.parser.ast.QuadrupleNode;
import ch.eskaton.asn4j.parser.ast.TupleNode;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

public class CollectionOfValue extends AbstractValue implements HasSize {

    private static final BigInteger[] MAX_TUPLE_VALUES = {
            BigInteger.valueOf(7), BigInteger.valueOf(15)
    };

    private static final BigInteger[] MAX_QUADRUPLE_VALUES = {
            BigInteger.valueOf(127), BigInteger.valueOf(255),
            BigInteger.valueOf(255), BigInteger.valueOf(255)
    };

    private Boolean isTuple = null;

    private Boolean isQuadruple = null;

    private List<Value> values;

    public CollectionOfValue(Position position, List<Value> values) {
        super(position);

        this.values = values;
    }

    public List<Value> getValues() {
        return values;
    }

    @Override
    public int getSize() {
        return getValues().size();
    }

    public boolean isTuple() {
        if (isTuple != null) {
            return isTuple;
        }

        isTuple = checkValues(getValues(), MAX_TUPLE_VALUES);

        return isTuple;
    }

    public TupleNode toTuple() {
        List<Value> values = getValues();

        if (!isTuple()) {
            return null;
        }

        return new TupleNode(getPosition(), getShort(values, 0), getShort(values, 1));
    }

    public boolean isQuadruple() {
        if (isQuadruple != null) {
            return isQuadruple;
        }

        isQuadruple = checkValues(getValues(), MAX_QUADRUPLE_VALUES);

        return isQuadruple;
    }

    public QuadrupleNode toQuadruple() {
        List<Value> values = getValues();

        if (!isQuadruple()) {
            return null;
        }

        return new QuadrupleNode(getPosition(), getShort(values, 0), getShort(values, 1),
                getShort(values, 2), getShort(values, 3));
    }

    private short getShort(List<Value> values, int pos) {
        return ((IntegerValue) values.get(pos)).getValue().shortValue();
    }

    private boolean checkValues(List<Value> values, BigInteger[] maxValues) {
        int pos = 0;

        if (values.size() != maxValues.length) {
            return false;
        }

        for (Value value : values) {
            if (!(value instanceof IntegerValue)
                    || ((IntegerValue) value).getValue().compareTo(BigInteger.ZERO) < 0
                    || ((IntegerValue) value).getValue().compareTo(maxValues[pos++]) > 0) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        CollectionOfValue that = (CollectionOfValue) o;

        return Objects.equals(isTuple, that.isTuple) &&
                Objects.equals(isQuadruple, that.isQuadruple) &&
                Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isTuple, isQuadruple, values);
    }

}
