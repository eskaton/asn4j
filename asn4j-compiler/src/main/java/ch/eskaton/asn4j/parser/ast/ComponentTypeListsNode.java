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
import ch.eskaton.asn4j.parser.ast.types.ComponentType;
import ch.eskaton.asn4j.parser.ast.types.ExtensionAdditionGroup;

import java.util.List;

public class ComponentTypeListsNode extends AbstractNode {

    private List<ComponentType> rootComponents;

    private List<ComponentType> extensionRootComponents;

    private ExtensionAndExceptionNode extensionAndException;

    private List<ExtensionAdditionGroup> extensionAdditions;

    private Boolean optionalExtensionMarker = false;

    public ComponentTypeListsNode(Position position, List<ComponentType> rootComponents,
            ExtensionAndExceptionNode extensionAndException, List<ExtensionAdditionGroup> extensionAdditions,
            Boolean extensionEndMark) {
        super(position);

        this.rootComponents = rootComponents;
        this.extensionAndException = extensionAndException;
        this.extensionAdditions = extensionAdditions;
        this.optionalExtensionMarker = extensionEndMark;
    }

    public ComponentTypeListsNode(Position position, List<ComponentType> rootComponents,
            ExtensionAndExceptionNode extensionAndException, List<ExtensionAdditionGroup> extensionAdditions,
            Boolean optionalExtensionMarker, List<ComponentType> extensionRootComponents) {
        super(position);

        this.rootComponents = rootComponents;
        this.extensionAndException = extensionAndException;
        this.extensionAdditions = extensionAdditions;
        this.optionalExtensionMarker = optionalExtensionMarker;
        this.extensionRootComponents = extensionRootComponents;
    }

    public ComponentTypeListsNode(Position position, List<ComponentType> rootComponents) {
        super(position);

        this.rootComponents = rootComponents;
    }

    public List<ComponentType> getRootComponents() {
        return rootComponents;
    }

    public List<ComponentType> getExtensionRootComponents() {
        return extensionRootComponents;
    }

    public ExtensionAndExceptionNode getExtensionAndException() {
        return extensionAndException;
    }

    public List<ExtensionAdditionGroup> getExtensionAdditions() {
        return extensionAdditions;
    }

    public Boolean getOptionalExtensionMarker() {
        return optionalExtensionMarker;
    }

}
