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

package ch.eskaton.asn4j.compiler.il;

import ch.eskaton.asn4j.runtime.utils.ToString;

import java.util.List;
import java.util.Optional;

public class BooleanFunctionCall extends FunctionCall implements BooleanExpression {

    public BooleanFunctionCall(Optional<String> function, Expression... arguments) {
        super(function, arguments);
    }

    public BooleanFunctionCall(Optional<String> function, List<Expression> arguments) {
        super(function, arguments);
    }

    @Override
    public String toString() {
        return ToString.get(this);
    }

    public static class ArrayEquals extends BooleanFunctionCall {

        public ArrayEquals(Expression argument1, Expression argument2) {
            super(Optional.empty(), argument1, argument2);
        }

    }

    public static class SetEquals extends BooleanFunctionCall {

        public SetEquals(Expression argument1, Expression argument2) {
            super(Optional.empty(), argument1, argument2);
        }

    }

    public static class MapEquals extends BooleanFunctionCall {

        public MapEquals(Expression argument1, Expression argument2) {
            super(Optional.empty(), argument1, argument2);
        }

    }

    public static class StringEquals extends BooleanFunctionCall {

        public StringEquals(Expression argument1, Expression argument2) {
            super(Optional.empty(), argument1, argument2);
        }

    }

    public static class CheckStringMinLength extends BooleanFunctionCall {

        public CheckStringMinLength(Expression argument, Expression length) {
            super(Optional.empty(), argument, length);
        }

    }

    public static class CheckStringMaxLength extends BooleanFunctionCall {

        public CheckStringMaxLength(Expression argument, Expression length) {
            super(Optional.empty(), argument, length);
        }

    }

    public static class CheckStringLengthEquals extends BooleanFunctionCall {

        public CheckStringLengthEquals(Expression argument, Expression length) {
            super(Optional.empty(), argument, length);
        }

    }

    public static class CheckBitStringMinLength extends BooleanFunctionCall {

        public CheckBitStringMinLength(Expression value, Expression unusedBits, Expression length) {
            super(Optional.empty(), value, unusedBits, length);
        }

    }

    public static class CheckBitStringMaxLength extends BooleanFunctionCall {

        public CheckBitStringMaxLength(Expression value, Expression unusedBits, Expression length) {
            super(Optional.empty(), value, unusedBits, length);
        }

    }

    public static class CheckBitStringLengthEquals extends BooleanFunctionCall {

        public CheckBitStringLengthEquals(Expression value, Expression unusedBits, Expression length) {
            super(Optional.empty(), value, unusedBits, length);
        }

    }

    public static class CheckLowerBound extends BooleanFunctionCall {

        public CheckLowerBound(Expression argument, Expression bound) {
            super(Optional.empty(), argument, bound);
        }

    }

    public static class CheckUpperBound extends BooleanFunctionCall {

        public CheckUpperBound(Expression argument, Expression bound) {
            super(Optional.empty(), argument, bound);
        }

    }

    public static class CheckEquals extends BooleanFunctionCall {

        public CheckEquals(Expression argument, Expression bound) {
            super(Optional.empty(), argument, bound);
        }

    }

}
