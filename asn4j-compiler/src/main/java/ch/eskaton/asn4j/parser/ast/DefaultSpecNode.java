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
import ch.eskaton.asn4j.parser.ast.values.AmbiguousValue;
import ch.eskaton.asn4j.parser.ast.values.ExternalValueReference;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;
import ch.eskaton.asn4j.parser.ast.values.Value;

public class DefaultSpecNode extends DefaultOptionalitySpecNode<Node> {

    public DefaultSpecNode(Position position, Node node) {
        super(position, node);
    }

    public DefaultValueSpecNode toDefaultValueSpec() {
        Node spec = getSpec();

        if (spec != null) {
            if (spec instanceof ObjectDefnNode || spec instanceof ObjectFromObjectNode) {
                return null;
            }

            return new DefaultValueSpecNode(getPosition(), (Value) spec);
        }

        return null;
    }

    public DefaultObjectSpecNode toDefaultObjectSpec() {
        Node spec = getSpec();

        if (spec != null) {
            if (spec instanceof ObjectDefnNode || spec instanceof ObjectFromObjectNode) {
                return new DefaultObjectSpecNode(getPosition(), (ObjectNode) spec);
            } else if (spec instanceof SimpleDefinedValue || spec instanceof AmbiguousValue
                    && ((AmbiguousValue) spec).getValue(SimpleDefinedValue.class) != null) {
                if (spec instanceof AmbiguousValue) {
                    spec = ((AmbiguousValue) spec).getValue(SimpleDefinedValue.class);
                }

                SimpleDefinedValue valueRef = (SimpleDefinedValue) spec;
                ObjectReferenceNode objectRef = new ObjectReferenceNode(getPosition(), valueRef.getValue());
                objectRef.setParameters(valueRef.getParameters());

                return new DefaultObjectSpecNode(getPosition(), objectRef);
            } else if (spec instanceof ExternalValueReference) {
                ExternalValueReference valueRef = (ExternalValueReference) spec;
                ExternalObjectReferenceNode objectRef = new ExternalObjectReferenceNode(getPosition(), valueRef.getModule(),
                        valueRef.getValue());
                objectRef.setParameters(valueRef.getParameters());

                return new DefaultObjectSpecNode(getPosition(), objectRef);
            }

        }

        return null;
    }

}
