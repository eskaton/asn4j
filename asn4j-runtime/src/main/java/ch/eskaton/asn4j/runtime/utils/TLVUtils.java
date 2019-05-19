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

import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.exceptions.EncodingException;
import ch.eskaton.asn4j.runtime.types.ASN1Type;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TLVUtils {

    private static final double LOG10_2_DIV_8 = Math.log10(2) / 8;

    public static byte[] getTagLength(ASN1Type obj, int contentLen)
            throws EncodingException {
        List<ASN1Tag> tags = RuntimeUtils.getTags(obj.getClass());

        return getTagLength(tags, contentLen);
    }

    public static byte[] getTagLength(ASN1Tag tag, ASN1Type obj, int contentLength) {
        if (tag.mode() != ASN1Tag.Mode.Implicit) {
            ArrayList<ASN1Tag> tags = new ArrayList<>();

            tags.add(tag);
            tags.addAll(RuntimeUtils.getTags(obj.getClass()));

            return getTagLength(tags, contentLength);
        } else {
            return getTagLength(tag, contentLength);
        }
    }

    public static byte[] getTagLength(List<ASN1Tag> tags, int contentLen) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Stack<byte[]> bufs = new Stack<>();
        ASN1Tag tag;

        for (int i = tags.size() - 1; i >= 0; i--) {
            tag = tags.get(i);
            byte[] lenBuf = getLength(contentLen);
            byte[] tagBuf = getTag(tag);
            bufs.push(lenBuf);
            bufs.push(tagBuf);
            contentLen += lenBuf.length + tagBuf.length;
        }

        try {
            for (int i = bufs.size() - 1; i >= 0; i--) {
                baos.write(bufs.get(i));
            }
        } catch (IOException e) {
            throw new EncodingException(e);
        }

        return baos.toByteArray();
    }

    public static byte[] getTagLength(ASN1Tag tag, int contentLen) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            baos.write(getTag(tag));
            baos.write(getLength(contentLen));
        } catch (IOException e) {
            throw new EncodingException(e);
        }

        return baos.toByteArray();
    }

    public static byte[] getLength(int contentLen) {
        byte[] buf;

        if (contentLen > 127) {
            int len = (int) Math.ceil(Math.log10(contentLen + 1.0d) / LOG10_2_DIV_8);
            buf = new byte[len + 1];
            buf[0] = (byte) (len & 0x7f | 0x80);

            for (int i = len; i > 0; i--) {
                buf[i] = (byte) (contentLen & 0xFF);
                contentLen >>= 8;
            }
        } else {
            buf = new byte[] { (byte) (contentLen & 0x7f) };
        }

        return buf;
    }

    public static byte[] getTag(ASN1Tag tag) {
        byte[] buf;
        int tagNum = tag.tag();

        if (tagNum > 30) {
            int len = (int) Math.ceil(Math.log10(tagNum + 1.0d) / Math.log10(2) / 7.0d);
            buf = new byte[len + 1];
            buf[0] |= 0x1f;

            for (int i = len; i > 0; i--) {
                buf[i] = (byte) ((tagNum & 0x7f) | 0x80);
                tagNum >>= 7;
            }

            buf[len] &= ~0x80;
        } else {
            buf = new byte[] { (byte) (tagNum & 0x1f) };
        }

        buf[0] |= (byte) ((tag.clazz().ordinal() & 0x3) << 6) | (byte) ((tag.constructed() ? 1 : 0) << 5);

        return buf;
    }

}
