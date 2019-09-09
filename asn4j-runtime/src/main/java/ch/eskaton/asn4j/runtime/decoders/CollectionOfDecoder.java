/*
 *
 *  *  Copyright (c) 2015, Adrian Moser
 *  *  All rights reserved.
 *  *
 *  *  Redistribution and use in source and binary forms, with or without
 *  *  modification, are permitted provided that the following conditions are met:
 *  *  * Redistributions of source code must retain the above copyright
 *  *  notice, this list of conditions and the following disclaimer.
 *  *  * Redistributions in binary form must reproduce the above copyright
 *  *  notice, this list of conditions and the following disclaimer in the
 *  *  documentation and/or other materials provided with the distribution.
 *  *  * Neither the name of the author nor the
 *  *  names of its contributors may be used to endorse or promote products
 *  *  derived from this software without specific prior written permission.
 *  *
 *  *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  *  DISCLAIMED. IN NO EVENT SHALL AUTHOR BE LIABLE FOR ANY
 *  *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package ch.eskaton.asn4j.runtime.decoders;

import ch.eskaton.asn4j.runtime.Decoder;
import ch.eskaton.asn4j.runtime.DecoderStates;
import ch.eskaton.asn4j.runtime.DecodingResult;
import ch.eskaton.asn4j.runtime.types.ASN1CollectionOf;
import ch.eskaton.asn4j.runtime.types.ASN1Type;

import java.lang.reflect.ParameterizedType;
import java.util.LinkedList;
import java.util.List;

public abstract class CollectionOfDecoder<T extends ASN1CollectionOf> implements CollectionDecoder<T> {

    @SuppressWarnings("unchecked")
    public void decode(Decoder decoder, DecoderStates states, T obj) {
        Class parent = obj.getClass();

        while (!(parent.getGenericSuperclass() instanceof ParameterizedType)) {
            parent = parent.getSuperclass();
        }

        ParameterizedType pt = (ParameterizedType) parent.getGenericSuperclass();
        Class<ASN1Type> typeParam = (Class<ASN1Type>) pt.getActualTypeArguments()[0];
        List<ASN1Type> elements = new LinkedList<>();
        DecodingResult<ASN1Type> result;

        do {
            result = decoder.decode(typeParam, states, null, true);

            if (result != null) {
                ASN1Type element = result.getObj();

                if (element != null) {
                    elements.add(element);
                }
            }
        } while (result != null);

        obj.setValues(elements);
    }

}
