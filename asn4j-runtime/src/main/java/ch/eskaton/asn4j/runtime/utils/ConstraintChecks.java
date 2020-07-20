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

package ch.eskaton.asn4j.runtime.utils;

import ch.eskaton.asn4j.runtime.types.ASN1BitString;

import java.math.BigInteger;

public class ConstraintChecks {

    private ConstraintChecks() {
    }

    public static boolean checkMinLength(String value, long min) {
        if (value == null) {
            return false;
        }

        return value.length() >= min;
    }

    public static boolean checkMaxLength(String value, long max) {
        if (value == null) {
            return false;
        }

        return value.length() <= max;
    }

    public static boolean checkLengthEquals(String value, long length) {
        if (value == null) {
            return false;
        }

        return value.length() == length;
    }

    public static boolean checkMinLength(byte[] value, Integer unusedBits, long min) {
        if (value == null || unusedBits == null) {
            return false;
        }

        return ASN1BitString.getSize(value, unusedBits) >= min;
    }

    public static boolean checkMaxLength(byte[] value, Integer unusedBits, long max) {
        if (value == null || unusedBits == null) {
            return false;
        }

        return ASN1BitString.getSize(value, unusedBits) <= max;
    }

    public static boolean checkLengthEquals(byte[] value, Integer unusedBits, long length) {
        if (value == null || unusedBits == null) {
            return false;
        }

        return ASN1BitString.getSize(value, unusedBits) == length;
    }

    public static boolean checkLowerBound(BigInteger value, long min) {
        if (value == null) {
            return false;
        }

        return value.compareTo(BigInteger.valueOf(min)) >= Integer.valueOf(0);
    }

    public static boolean checkUpperBound(BigInteger value, long max) {
        if (value == null) {
            return false;
        }

        return value.compareTo(BigInteger.valueOf(max)) <= Integer.valueOf(0);
    }

    public static boolean checkEquals(BigInteger value, long other) {
        if (value == null) {
            return false;
        }

        return value.compareTo(BigInteger.valueOf(other)) == Integer.valueOf(0);
    }

}
