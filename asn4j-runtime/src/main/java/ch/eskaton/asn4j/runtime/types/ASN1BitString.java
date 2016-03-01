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

package ch.eskaton.asn4j.runtime.types;

import ch.eskaton.asn4j.runtime.ASN1RuntimeException;
import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;

@ASN1Tag(clazz = ASN1Tag.Clazz.Universal, tag = 3, mode = ASN1Tag.Mode.Explicit, constructed = false)
public class ASN1BitString implements ASN1Type {

	protected byte[] value = new byte[0];

	private int usedBits;

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		int usedBytes = 0;

		for (int i = 0; i < value.length; i++) {
			if (value[i] != 0) {
				usedBytes = i + 1;
			}
		}

		this.value = new byte[usedBytes];
		System.arraycopy(value, 0, this.value, 0, usedBytes);

		usedBits = 0;

		calcUsedBits(this.value.length - 1);
	}

	public void setBit(int bit) {
		int length = (bit - 1) / 8;

		if (length >= value.length) {
			byte[] tmpValue = new byte[length + 1];
			System.arraycopy(value, 0, tmpValue, 0, value.length);
			value = tmpValue;
		}

		value[length] = value[length] |= getBit(bit);

		if (length >= usedBits / 8) {
			calcUsedBits(length);
		}
	}

	public boolean testBit(int bit) throws ASN1RuntimeException {
		int pos = bit / 8;

		if (pos > value.length) {
			throw new RuntimeException("Bit position out of range");
		}

		return (value[pos] & getBit(bit)) != 0;
	}

	public void clearBit(int bit) throws ASN1RuntimeException {
		int pos = bit / 8;

		if (pos > value.length) {
			throw new ASN1RuntimeException("Bit position out of range");
		}

		value[pos] = (byte) (value[pos] & ~getBit(bit));

		if (pos == value.length - 1) {
			while (value[pos] == 0) {
				if (pos == 0) {
					value = new byte[] {};
					usedBits = 0;
					return;
				}
				pos--;
			}

			if (pos != value.length - 1) {
				byte[] tmpValue = new byte[pos + 1];
				System.arraycopy(value, 0, tmpValue, 0, pos + 1);
				value = tmpValue;
				calcUsedBits(pos);
			}
		}

	}

	public int getUnusedBits() {
		return (8 - usedBits % 8) % 8;
	}

	private int getBit(int bit) {
		return 1 << ((8 - bit) % 8);
	}

	private void calcUsedBits(int pos) {
		for (int i = pos; i >= 0; i--) {
			if (value[i] != 0) {
				byte b = value[i];
				int bit = 0;

				while (bit < 7) {
					if (!((b & 0x01) == 1)) {
						bit++;
						b >>= 1;
					} else {
						break;
					}
				}

				usedBits = (i + 1) * 8 - bit;
				break;
			}
		}
	}

	@Override
	public int hashCode() {
		return 31 + ((value == null) ? 0 : value.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		ASN1BitString other = (ASN1BitString) obj;

		if (value == null) {
			if (other.value != null)
				return false;
		} else {
			if (value.length != other.value.length) {
				return false;
			}

			for (int i = 0; i < value.length; i++) {
				if ((value[i] != other.value[i])) {
					return false;
				}
			}
		}

		return true;
	}

}
