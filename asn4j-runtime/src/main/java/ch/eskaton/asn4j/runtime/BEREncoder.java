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

package ch.eskaton.asn4j.runtime;

import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.encoders.BitStringEncoder;
import ch.eskaton.asn4j.runtime.encoders.BooleanEncoder;
import ch.eskaton.asn4j.runtime.encoders.ChoiceEncoder;
import ch.eskaton.asn4j.runtime.encoders.EnumerationTypeEncoder;
import ch.eskaton.asn4j.runtime.encoders.IRIEncoder;
import ch.eskaton.asn4j.runtime.encoders.IntegerEncoder;
import ch.eskaton.asn4j.runtime.encoders.NullEncoder;
import ch.eskaton.asn4j.runtime.encoders.ObjectIdentifierEncoder;
import ch.eskaton.asn4j.runtime.encoders.OctetStringEncoder;
import ch.eskaton.asn4j.runtime.encoders.RealEncoder;
import ch.eskaton.asn4j.runtime.encoders.RelativeIRIEncoder;
import ch.eskaton.asn4j.runtime.encoders.RelativeOIDEncoder;
import ch.eskaton.asn4j.runtime.encoders.SequenceEncoder;
import ch.eskaton.asn4j.runtime.encoders.SequenceOfEncoder;
import ch.eskaton.asn4j.runtime.encoders.SetEncoder;
import ch.eskaton.asn4j.runtime.encoders.SetOfEncoder;
import ch.eskaton.asn4j.runtime.encoders.TypeEncoder;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;
import ch.eskaton.asn4j.runtime.exceptions.EncodingException;
import ch.eskaton.asn4j.runtime.types.ASN1BitString;
import ch.eskaton.asn4j.runtime.types.ASN1Boolean;
import ch.eskaton.asn4j.runtime.types.ASN1Choice;
import ch.eskaton.asn4j.runtime.types.ASN1EnumeratedType;
import ch.eskaton.asn4j.runtime.types.ASN1IRI;
import ch.eskaton.asn4j.runtime.types.ASN1Integer;
import ch.eskaton.asn4j.runtime.types.ASN1Null;
import ch.eskaton.asn4j.runtime.types.ASN1ObjectIdentifier;
import ch.eskaton.asn4j.runtime.types.ASN1OctetString;
import ch.eskaton.asn4j.runtime.types.ASN1Real;
import ch.eskaton.asn4j.runtime.types.ASN1RelativeIRI;
import ch.eskaton.asn4j.runtime.types.ASN1RelativeOID;
import ch.eskaton.asn4j.runtime.types.ASN1Sequence;
import ch.eskaton.asn4j.runtime.types.ASN1SequenceOf;
import ch.eskaton.asn4j.runtime.types.ASN1Set;
import ch.eskaton.asn4j.runtime.types.ASN1SetOf;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.asn4j.runtime.types.ASN1VisibleString;
import ch.eskaton.asn4j.runtime.utils.TLVUtils;
import ch.eskaton.commons.utils.HexDump;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BEREncoder implements Encoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(BEREncoder.class);

    @SuppressWarnings("serial")
    private Map<Class<? extends ASN1Type>, TypeEncoder<? extends ASN1Type>> encoders =
            new HashMap<Class<? extends ASN1Type>, TypeEncoder<? extends ASN1Type>>() {
        {
            put(ASN1BitString.class, new BitStringEncoder());
            put(ASN1Boolean.class, new BooleanEncoder());
            put(ASN1Choice.class, new ChoiceEncoder());
            put(ASN1EnumeratedType.class, new EnumerationTypeEncoder());
            put(ASN1Integer.class, new IntegerEncoder());
            put(ASN1Real.class, new RealEncoder());
            put(ASN1Null.class, new NullEncoder());
            put(ASN1ObjectIdentifier.class, new ObjectIdentifierEncoder());
            put(ASN1RelativeOID.class, new RelativeOIDEncoder());
            put(ASN1IRI.class, new IRIEncoder());
            put(ASN1RelativeIRI.class, new RelativeIRIEncoder());
            put(ASN1OctetString.class, new OctetStringEncoder());
            put(ASN1Sequence.class, new SequenceEncoder());
            put(ASN1SequenceOf.class, new SequenceOfEncoder());
            put(ASN1Set.class, new SetEncoder());
            put(ASN1SetOf.class, new SetOfEncoder());
        }
    };

    public byte[] encode(ASN1Type obj) throws EncodingException, ConstraintViolatedException {
        byte[] encoded = encode(obj, null);

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Encoded value of type " + obj.getClass().getSimpleName() + " = " + HexDump
                    .toHexString(encoded));
        }

        return encoded;
    }

    @SuppressWarnings("unchecked")
    public <T extends ASN1Type, C extends TypeEncoder<T>> C getEncoder(Class<T> clazz) throws EncodingException {
        TypeEncoder<?> encoder = encoders.get(clazz);

        if (encoder == null) {
            throw new EncodingException("No encoder for node-type " + clazz.getSimpleName());
        }

        return (C) encoder;
    }

    @SuppressWarnings("rawtypes")
    public byte[] encode(ASN1Type obj, ASN1Tag tag) throws EncodingException, ConstraintViolatedException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf;

        if (obj instanceof ASN1Boolean) {
            buf = this.<ASN1Boolean, BooleanEncoder> getEncoder(
                    ASN1Boolean.class).encode(this, (ASN1Boolean) obj);
        } else if (obj instanceof ASN1Integer) {
            buf = this.<ASN1Integer, IntegerEncoder> getEncoder(
                    ASN1Integer.class).encode(this, (ASN1Integer) obj);
        } else if (obj instanceof ASN1EnumeratedType) {
            buf = this.<ASN1EnumeratedType, EnumerationTypeEncoder>getEncoder(ASN1EnumeratedType.class)
                    .encode(this, (ASN1EnumeratedType) obj);
        } else if (obj instanceof ASN1Real) {
            buf = this.<ASN1Real, RealEncoder>getEncoder(ASN1Real.class).encode(this, (ASN1Real) obj);
        } else if (obj instanceof ASN1BitString) {
            buf = this.<ASN1BitString, BitStringEncoder> getEncoder(
                    ASN1BitString.class).encode(this, (ASN1BitString) obj);
        } else if (obj instanceof ASN1OctetString) {
            buf = this.<ASN1OctetString, OctetStringEncoder>getEncoder(ASN1OctetString.class)
                    .encode(this, (ASN1OctetString) obj);
        } else if (obj instanceof ASN1VisibleString) {
            buf = ((ASN1VisibleString) obj).getValue().getBytes();
        } else if (obj instanceof ASN1Null) {
            buf = this.<ASN1Null, NullEncoder>getEncoder(ASN1Null.class).encode(this, (ASN1Null) obj);
        } else if (obj instanceof ASN1ObjectIdentifier) {
            buf = this.<ASN1ObjectIdentifier, ObjectIdentifierEncoder>getEncoder(ASN1ObjectIdentifier.class)
                    .encode(this, (ASN1ObjectIdentifier) obj);
        } else if (obj instanceof ASN1RelativeOID) {
            buf = this.<ASN1RelativeOID, RelativeOIDEncoder>getEncoder(ASN1RelativeOID.class)
                    .encode(this, (ASN1RelativeOID) obj);
        } else if (obj instanceof ASN1IRI) {
            buf = this.<ASN1IRI, IRIEncoder>getEncoder(ASN1IRI.class).encode(this, (ASN1IRI) obj);
        } else if (obj instanceof ASN1RelativeIRI) {
            buf = this.<ASN1RelativeIRI, RelativeIRIEncoder>getEncoder(ASN1RelativeIRI.class).encode(this, (ASN1RelativeIRI) obj);
        } else if (obj instanceof ASN1Sequence) {
            buf = this.<ASN1Sequence, SequenceEncoder> getEncoder(
                    ASN1Sequence.class).encode(this, (ASN1Sequence) obj);
        } else if (obj instanceof ASN1Set) {
            buf = this.<ASN1Set, SetEncoder> getEncoder(ASN1Set.class).encode(this, (ASN1Set) obj);
        } else if (obj instanceof ASN1SequenceOf) {
            buf = this.<ASN1SequenceOf, SequenceOfEncoder> getEncoder(ASN1SequenceOf.class)
                    .encode(this, (ASN1SequenceOf<?>) obj);
        } else if (obj instanceof ASN1SetOf) {
            buf = this.<ASN1SetOf, SetOfEncoder>getEncoder(ASN1SetOf.class).encode(this, (ASN1SetOf<?>) obj);
        } else if (obj instanceof ASN1Choice) {
            buf = this.<ASN1Choice, ChoiceEncoder>getEncoder(ASN1Choice.class).encode(this, (ASN1Choice) obj);
        } else {
            throw new EncodingException("Unsupported type: " + obj.getClass().getSimpleName());
        }

        try {
            if (tag != null) {
                baos.write(TLVUtils.getTagLength(tag, obj, buf.length));
            } else {
                baos.write(TLVUtils.getTagLength(obj, buf.length));
            }

            baos.write(buf);
        } catch (IOException e) {
            throw new EncodingException(e);
        }

        return baos.toByteArray();
    }

}
