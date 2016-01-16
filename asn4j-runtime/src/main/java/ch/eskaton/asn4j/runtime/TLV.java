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

import java.io.IOException;

import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;

public class TLV {

	public int pos;

	public ASN1Tag.Clazz clazz;

	public boolean constructed;

	public int tag;

	public int length;

	public int nextTlv;

	private TLV() {
	}

	public static TLV getTLV(byte[] buf, int pos, int length)
			throws IOException {
		if (pos >= buf.length) {
			throw new IOException("Premature end of input");
		}

		TLV tlv = new TLV();
		int id = buf[pos++];
		tlv.clazz = ASN1Tag.Clazz.values()[(id >> 6) & 0x3];
		tlv.constructed = ((id >> 5) & 0x1) == 1 ? true : false;
		tlv.tag = id & 0x1f;

		if (tlv.tag == 0x1f) {
			int longTag = 0;
			int c = 0;

			do {
				if (pos >= buf.length) {
					throw new IOException("Premature end of input");
				}

				c = buf[pos++];

				longTag = (longTag << 7) | (c & 0x7F);

			} while ((c & 0x80) != 0);
		}

		if (pos >= buf.length) {
			throw new IOException("Premature end of input");
		}

		tlv.length = buf[pos++] & 0xFF;

		if ((tlv.length & 0x80) != 0) {
			int sizeLen = tlv.length & 0x7F;
			tlv.length = 0;

			while (sizeLen-- > 0) {
				if (pos >= buf.length) {
					throw new IOException("Premature end of input");
				}

				int c = buf[pos++];

				tlv.length = (tlv.length << 8) | (c & 0xFF);
			}
		}

		tlv.pos = pos;

		if (length > 0 && tlv.length >= length) {
			tlv.nextTlv = -1;
		} else {
			if (tlv.constructed && tlv.length == 0) {
				tlv.length = -1;
				tlv.nextTlv = -1;
			} else {
				tlv.nextTlv = pos + tlv.length;
			}
		}

		return tlv;
	}

}
