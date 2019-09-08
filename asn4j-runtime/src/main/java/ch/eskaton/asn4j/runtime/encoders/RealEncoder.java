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

package ch.eskaton.asn4j.runtime.encoders;

import ch.eskaton.asn4j.runtime.Encoder;
import ch.eskaton.asn4j.runtime.exceptions.EncodingException;
import ch.eskaton.asn4j.runtime.types.ASN1Real;

import java.math.BigDecimal;

public class RealEncoder implements TypeEncoder<ASN1Real> {

    @Override
    public byte[] encode(Encoder encoder, ASN1Real obj) {
        switch (obj.getType()) {
            case NORMAL:
                break;

            case PLUS_INFINITY:
                return new byte[] { 0x40 };

            case MINUS_INFINITY:
                return new byte[] { 0x41 };

            case NOT_A_NUMBER:
                return new byte[] { 0x42 };

            case MINUS_ZERO:
                return new byte[] { 0x43 };

            default:
                throw new EncodingException("Unsupported ASN1Real " + obj.toString());
        }

        if (BigDecimal.ZERO.equals(obj.getValue())) {
            return new byte[] {};
        }

        String value = obj.getValue().toString();
        byte[] source = obj.getValue().toString().getBytes();
        byte[] target;
        byte form = 1;

        if (value.indexOf('E') != -1) {
            form = 3;
        } else if (value.indexOf('.') != -1) {
            form = 2;
        }

        if (value.charAt(0) == '-') {
            target = new byte[source.length + 1];
            System.arraycopy(source, 0, target, 1, source.length);
        } else {
            target = new byte[source.length + 2];
            System.arraycopy(source, 0, target, 2, source.length);
            target[1] = ' ';
        }

        target[0] = form;

        return target;
    }
}
