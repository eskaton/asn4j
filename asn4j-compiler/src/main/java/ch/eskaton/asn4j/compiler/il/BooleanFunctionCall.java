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

        public CheckStringMinLength(List<Expression> arguments) {
            super(Optional.empty(), arguments);
        }

    }

    public static class CheckStringMaxLength extends BooleanFunctionCall {

        public CheckStringMaxLength(List<Expression> arguments) {
            super(Optional.empty(), arguments);
        }

    }

    public static class CheckStringLengthEquals extends BooleanFunctionCall {

        public CheckStringLengthEquals(List<Expression> arguments) {
            super(Optional.empty(), arguments);
        }

    }

    public static class CheckUCStringMinLength extends BooleanFunctionCall {

        public CheckUCStringMinLength(List<Expression> arguments) {
            super(Optional.empty(), arguments);
        }

    }

    public static class CheckUCStringMaxLength extends BooleanFunctionCall {

        public CheckUCStringMaxLength(List<Expression> arguments) {
            super(Optional.empty(), arguments);
        }

    }

    public static class CheckUCStringLengthEquals extends BooleanFunctionCall {

        public CheckUCStringLengthEquals(List<Expression> arguments) {
            super(Optional.empty(), arguments);
        }

    }

    public static class CheckBitStringMinLength extends BooleanFunctionCall {

        public CheckBitStringMinLength(List<Expression> arguments) {
            super(Optional.empty(), arguments);
        }

    }

    public static class CheckBitStringMaxLength extends BooleanFunctionCall {

        public CheckBitStringMaxLength(List<Expression> arguments) {
            super(Optional.empty(), arguments);
        }

    }

    public static class CheckBitStringLengthEquals extends BooleanFunctionCall {

        public CheckBitStringLengthEquals(List<Expression> arguments) {
            super(Optional.empty(), arguments);
        }

    }

    public static class CheckOctetStringMinLength extends BooleanFunctionCall {

        public CheckOctetStringMinLength(List<Expression> arguments) {
            super(Optional.empty(), arguments);
        }

    }

    public static class CheckOctetStringMaxLength extends BooleanFunctionCall {

        public CheckOctetStringMaxLength(List<Expression> arguments) {
            super(Optional.empty(), arguments);
        }

    }

    public static class CheckOctetStringLengthEquals extends BooleanFunctionCall {

        public CheckOctetStringLengthEquals(List<Expression> arguments) {
            super(Optional.empty(), arguments);
        }

    }

    public static class CheckLowerBound extends BooleanFunctionCall {

        public CheckLowerBound(List<Expression> arguments) {
            super(Optional.empty(), arguments);
        }

    }

    public static class CheckUpperBound extends BooleanFunctionCall {

        public CheckUpperBound(List<Expression> arguments) {
            super(Optional.empty(), arguments);
        }

    }

    public static class CheckEquals extends BooleanFunctionCall {

        public CheckEquals(List<Expression> arguments) {
            super(Optional.empty(), arguments);
        }

    }

    public static class CheckCollectionMinSize extends BooleanFunctionCall {

        public CheckCollectionMinSize(List<Expression> arguments) {
            super(Optional.empty(), arguments);
        }

    }

    public static class CheckCollectionMaxSize extends BooleanFunctionCall {

        public CheckCollectionMaxSize(List<Expression> arguments) {
            super(Optional.empty(), arguments);
        }

    }

    public static class CheckCollectionSizeEquals extends BooleanFunctionCall {

        public CheckCollectionSizeEquals(List<Expression> arguments) {
            super(Optional.empty(), arguments);
        }

    }

}
