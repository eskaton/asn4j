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

import ch.eskaton.asn4j.runtime.Decoder;
import ch.eskaton.asn4j.runtime.DecoderState;
import ch.eskaton.asn4j.runtime.DecoderStates;
import ch.eskaton.asn4j.runtime.exceptions.DecodingException;
import ch.eskaton.asn4j.runtime.types.ASN1OpenType;
import ch.eskaton.asn4j.runtime.types.ASN1Type;

public class OpenTypeDecoder {

    public <T extends ASN1Type> T decode(Decoder decoder, DecoderStates states, DecoderState state, boolean optional) {
        var newState = decoder.decode(states);

        if (newState == null) {
            if (optional) {
                return null;
            }

            throw new DecodingException("Empty open type");
        }

        byte[] buf = new byte[state.length];

        System.arraycopy(states.buf, state.pos, buf, 0, state.length);

        return (T) new DecodableASN1OpenType(decoder, buf);
    }

    private static class DecodableASN1OpenType extends ASN1OpenType {

        private final Decoder decoder;

        private final byte[] bytes;

        public DecodableASN1OpenType(Decoder decoder, byte[] bytes) {
            this.decoder = decoder;
            this.bytes = bytes;
        }

        @Override
        public <T extends ASN1Type> T decode(Class<T> type) {
            var value = decoder.decode(type, bytes);

            super.setValue(value);

            return value;
        }
    }

}
