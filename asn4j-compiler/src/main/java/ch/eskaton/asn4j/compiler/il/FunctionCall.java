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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FunctionCall implements Expression {

    private Optional<String> function;

    private Optional<Expression> object;

    private List<Expression> arguments;

    public FunctionCall(Optional<String> function, Optional<Expression> object, Expression... arguments) {
        this.function = function;
        this.object = object;
        this.arguments = Arrays.asList(arguments);
    }

    public FunctionCall(Optional<String> function, Expression... arguments) {
        this.function = function;
        this.object = Optional.empty();
        this.arguments = Arrays.asList(arguments);
    }

    public FunctionCall(Optional<String> function, List<Expression> arguments) {
        this.function = function;
        this.object = Optional.empty();
        this.arguments = arguments;
    }

    public Optional<String> getFunction() {
        return function;
    }

    public Optional<Expression> getObject() {
        return object;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return ToString.get(this);
    }

    public static class BigIntegerCompare extends FunctionCall {

        public BigIntegerCompare(Expression... arguments) {
            super(Optional.empty(), arguments);
        }

        @Override
        public String toString() {
            return ToString.get(this);
        }

    }

    public static class ArrayLength extends FunctionCall {

        public ArrayLength(Expression argument) {
            super(Optional.empty(), argument);
        }

        @Override
        public String toString() {
            return ToString.get(this);
        }

    }

    public static class BitStringSize extends FunctionCall {

        public BitStringSize(Expression argument1, Expression argument2) {
            super(Optional.empty(), argument1, argument2);
        }

        @Override
        public String toString() {
            return ToString.get(this);
        }

    }

    public static class GetSize extends FunctionCall {

        public GetSize(Expression argument) {
            super(Optional.empty(), argument);
        }

        @Override
        public String toString() {
            return ToString.get(this);
        }

    }

    public static class ToArray extends FunctionCall {

        private ILType type;

        public ToArray(ILType type, Expression object) {
            super(Optional.empty(), Optional.of(object));

            this.type = type;
        }

        public ILType getType() {
            return type;
        }

        @Override
        public String toString() {
            return ToString.get(this);
        }

    }

    public static class GetMapValue extends FunctionCall {

        private ILType type;

        public GetMapValue(Expression map, ILValue key, ILType type) {
            super(Optional.empty(), Optional.of(map), key);

            this.type = type;
        }

        public ILType getType() {
            return type;
        }

        @Override
        public String toString() {
            return ToString.get(this);
        }

    }

}
