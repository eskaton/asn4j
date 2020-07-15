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
import ch.eskaton.asn4j.parser.ast.types.ExternalTypeReference;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;

public class FieldSpecNode extends AbstractFieldSpecNode {

    private Node type;

    private boolean unique;

    public FieldSpecNode(Position position, String reference, Node type, boolean unique,
            OptionalitySpecNode optionalitySpec) {
        super(position, reference, optionalitySpec);

        this.type = type;
        this.unique = unique;
    }

    public Node getType() {
        return type;
    }

    public boolean isUnique() {
        return unique;
    }

    public FixedTypeValueFieldSpecNode toFixedTypeValueFieldSpec() {
        if ((type instanceof ObjectClassReference)) {
            return null;
        }

        return new FixedTypeValueFieldSpecNode(getPosition(), getReference(), (Type) type, unique,
                getOptionalitySpec());
    }

    public ObjectFieldSpecNode toObjectFieldSpec() {
        if (unique) {
            return null;
        }

        OptionalitySpecNode optionalitySpec = getOptionalitySpec();

        if (optionalitySpec instanceof DefaultSpecNode) {
            optionalitySpec = ((DefaultSpecNode) optionalitySpec).toDefaultObjectSpec();

            if (optionalitySpec == null) {
                return null;
            }
        }

        if (type instanceof TypeReference) {
            TypeReference typeRef = (TypeReference) type;

            if (typeRef.getConstraints() != null) {
                return null;
            }

            ObjectClassReference objRef = new ObjectClassReference(typeRef.getPosition(), typeRef.getType());

            objRef.setParameters(typeRef.getParameters());

            return new ObjectFieldSpecNode(objRef.getPosition(), getReference(), objRef, optionalitySpec);
        } else if (type instanceof ExternalTypeReference) {
            ExternalTypeReference typeRef = (ExternalTypeReference) type;

            if (typeRef.getConstraints() != null) {
                return null;
            }

            ExternalObjectClassReferenceNode objRef = new ExternalObjectClassReferenceNode(typeRef.getPosition(),
                    typeRef.getModule(), typeRef.getType());

            objRef.setParameters(typeRef.getParameters());

            return new ObjectFieldSpecNode(objRef.getPosition(), getReference(), objRef, optionalitySpec);
        } else if (type instanceof ObjectClassReference) {
            return new ObjectFieldSpecNode(type.getPosition(), getReference(), (ObjectClassReference) type,
                    optionalitySpec);
        }

        return null;
    }

}
