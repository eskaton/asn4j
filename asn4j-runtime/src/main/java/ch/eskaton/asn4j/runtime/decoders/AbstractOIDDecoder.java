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
import ch.eskaton.asn4j.runtime.exceptions.DecodingException;
import ch.eskaton.asn4j.runtime.exceptions.ValidationException;
import ch.eskaton.asn4j.runtime.types.AbstractASN1OID;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractOIDDecoder<T extends AbstractASN1OID> implements TypeDecoder<T> {

    @Override
    public void decode(DecoderStates states, DecoderState state, T obj) {
        List<Integer> components = new ArrayList<>();
        int component = 0;

        for (int i = 0; i < state.length; i++) {
            component <<= 7;
            component |= states.buf[state.pos + i] & 0x7F;

            if ((states.buf[state.pos + i] & 0x80) == 0) {
                decodeComponent(components, component);
                component = 0;
            }
        }

        try {
            obj.setValue(components);
        } catch (ValidationException e) {
            throw new DecodingException(e);
        }
    }

    protected abstract void decodeComponent(List<Integer> components, int component);

}
