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

package ch.eskaton.asn4j.runtime.decoders;

import ch.eskaton.asn4j.runtime.DecoderState;
import ch.eskaton.asn4j.runtime.DecoderStates;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;
import ch.eskaton.asn4j.runtime.exceptions.DecodingException;
import ch.eskaton.asn4j.runtime.types.ASN1Real;
import ch.eskaton.asn4j.runtime.types.ASN1Real.Type;

import java.math.BigDecimal;

public class RealDecoder {

    public void decode(DecoderStates states, DecoderState state, ASN1Real obj) throws ConstraintViolatedException {
        outer:
        switch (state.tlv.length) {
            case 0:
                obj.setValue(BigDecimal.ZERO);
                break;

            case 1:
                switch (states.buf[state.tlv.pos]) {
                    case 0x40:
                        obj.setType(Type.PLUS_INFINITY);
                        break outer;

                    case 0x41:
                        obj.setType(Type.MINUS_INFINITY);
                        break outer;

                    case 0x42:
                        obj.setType(Type.NOT_A_NUMBER);
                        break outer;

                    case 0x43:
                        obj.setType(Type.MINUS_ZERO);
                        break outer;
                }

            default:
                if ((states.buf[state.tlv.pos] >> 6) == 0x00) {
                    int offset = 1 + (states.buf[state.tlv.pos + 1] == 0x20 ? 1 : 0);
                    byte[] buf = new byte[state.tlv.length - offset];
                    System.arraycopy(states.buf, state.tlv.pos + offset, buf, 0,
                                     state.tlv.length - offset);
                    obj.setValue(new BigDecimal(new String(buf)));
                } else {
                    throw new DecodingException("Binary encoding not yet supported");
                }
        }
    }

}
