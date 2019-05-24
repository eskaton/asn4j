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

package ch.eskaton.asn4j.parser.ast;

import ch.eskaton.asn4j.parser.Position;
import ch.eskaton.asn4j.parser.Token.TokenType;

public class PrimitiveFieldNameNode extends AbstractNode {

    private String reference;

    private TokenType type;

    public PrimitiveFieldNameNode(Position position, String reference, TokenType type) {
        super(position);

        this.reference = reference;
        this.type = type;
    }

    public String getReference() {
        return reference;
    }

    public boolean isTypeFieldReference() {
        return type == TokenType.TYPE_FIELD_REFERENCE;
    }

    public boolean isValueSetFieldReference() {
        return isTypeFieldReference();
    }

    public boolean isObjectSetFieldReference() {
        return isTypeFieldReference();
    }

    public boolean isValueFieldReference() {
        return type == TokenType.VALUE_FIELD_REFERENCE;
    }

    public boolean isObjectFieldReference() {
        return isValueFieldReference();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[reference=" + reference + ", type=" + type + "]";
    }

}
