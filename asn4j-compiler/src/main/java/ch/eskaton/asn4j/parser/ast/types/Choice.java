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

package ch.eskaton.asn4j.parser.ast.types;

import ch.eskaton.asn4j.parser.Position;
import ch.eskaton.asn4j.parser.ast.ExceptionIdentificationNode;
import ch.eskaton.asn4j.parser.ast.ExtensionAdditionAlternativeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Choice extends AbstractType {

    private List<NamedType> rootAlternatives;

    private boolean extensible;

    private ExceptionIdentificationNode exceptionId;

    private List<ExtensionAdditionAlternativeNode> extensionAdditionAlternatives;

    private boolean optionalExtensionMarker;

    public Choice(Position position, AlternativeTypeLists alternatives) {
        super(position);

        this.rootAlternatives = alternatives.getRootAlternatives();
        this.extensible = alternatives.getExtensionAndException() != null;
        this.exceptionId = this.extensible ? alternatives.getExtensionAndException().getExceptionId() : null;
        this.extensionAdditionAlternatives = alternatives.getExtensionAdditionAlternatives();
        this.optionalExtensionMarker = alternatives.hasExtensionMarker();
    }

    public List<NamedType> getRootAlternatives() {
        return rootAlternatives;
    }

    public List<NamedType> getAllAlternatives() {
        var allAlternatives = new ArrayList<NamedType>();

        allAlternatives.addAll(rootAlternatives);

        if (extensionAdditionAlternatives != null) {
            List<NamedType> extensionAdditionAlternativesList = extensionAdditionAlternatives.stream()
                    .map(this::getNamedTypes)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            allAlternatives.addAll(extensionAdditionAlternativesList);
        }

        return allAlternatives;
    }

    private List<NamedType> getNamedTypes(ExtensionAdditionAlternativeNode a) {
        return a.getNamedType() != null ? List.of(a.getNamedType()) :
                a.getExtensionAdditionAlternativesGroup().getAlternatives();
    }

    public boolean isExtensible() {
        return extensible;
    }

    public ExceptionIdentificationNode getExceptionId() {
        return exceptionId;
    }

    public List<ExtensionAdditionAlternativeNode> getExtensionAdditionAlternatives() {
        return extensionAdditionAlternatives;
    }

    public boolean isOptionalExtensionMarker() {
        return optionalExtensionMarker;
    }

}
