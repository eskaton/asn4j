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
import ch.eskaton.asn4j.runtime.types.ASN1ObjectIdentifier;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

public class ObjectIdentifierEncoder implements TypeEncoder<ASN1ObjectIdentifier> {

    public byte[] encode(Encoder encoder, ASN1ObjectIdentifier obj)
            throws EncodingException {
        List<Integer> components = obj.getValue();
        ByteArrayOutputStream value = new ByteArrayOutputStream();
        int component = 0;

        if (components == null || components.size() < 2) {
            throw new EncodingException("Invalid OID: " + obj);
        }

        for (int i = 0; i < components.size(); i++) {
            if (i == 0) {
                component = 40 * components.get(i);
            } else if (i == 1) {
                component += components.get(i);
            } else {
                component = components.get(i);
            }

            if (i != 0 || components.size() == 1) {
                LinkedList<Integer> list = new LinkedList<Integer>();

                while (component > 127) {
                    list.push(component & 0x7F);
                    component >>= 7;
                }

                list.push(component);

                int listSize = list.size();

                for (int j = 0; j < listSize; j++) {
                    if (j == listSize - 1) {
                        value.write(list.get(j));
                    } else {
                        value.write(list.get(j) | 0x80);
                    }
                }
            }
        }

        return value.toByteArray();
    }

}
