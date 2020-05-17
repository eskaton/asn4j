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
import ch.eskaton.asn4j.parser.ast.ComponentTypeListsNode;
import ch.eskaton.asn4j.parser.ast.ExtensionAndExceptionNode;
import ch.eskaton.commons.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class Collection extends AbstractType {

    private List<ComponentType> rootComponents;

    private List<ComponentType> extensionRootComponents;

    private ExtensionAndExceptionNode extensionAndException;

    private Boolean optionalExtensionMarker;

    private List<ExtensionAdditionGroup> extensionAdditions;

    public Collection(Position position) {
        super(position);
    }

    public Collection(Position position, ComponentTypeListsNode compTypes) {
        super(position);

        this.rootComponents = compTypes.getRootComponents();
        this.extensionAndException = compTypes.getExtensionAndException();
        this.extensionAdditions = compTypes.getExtensionAdditions();
        this.optionalExtensionMarker = compTypes.getOptionalExtensionMarker();
        this.extensionRootComponents = compTypes.getExtensionRootComponents();
    }

    public Collection(Position position, ExtensionAndExceptionNode extensionAndException, Boolean optionalExtensionMarker) {
        super(position);

        this.extensionAndException = extensionAndException;
        this.optionalExtensionMarker = optionalExtensionMarker;
    }

    public List<ComponentType> getAllRootComponents() {
        List<ComponentType> components = new ArrayList<>();

        if (rootComponents != null) {
            components.addAll(rootComponents);
        }

        if (extensionRootComponents != null) {
            components.addAll(extensionRootComponents);
        }

        return components;
    }

    public List<ComponentType> getAllComponents() {
        List<ComponentType> components = new ArrayList<>();

        if (rootComponents != null) {
            components.addAll(rootComponents);
        }

        if (extensionAdditions != null) {
            extensionAdditions.stream().map(extension -> extension.getComponents()).forEach(components::addAll);
        }

        if (extensionRootComponents != null) {
            components.addAll(extensionRootComponents);
        }

        return components;
    }


    public List<ComponentType> getRootComponents() {
        return rootComponents;
    }

    public List<ComponentType> getExtensionRootComponents() {
        return extensionRootComponents;
    }

    public Object getExtensionAndException() {
        return extensionAndException;
    }

    public Boolean getOptionalExtensionMarker() {
        return optionalExtensionMarker;
    }

    public Object getExtensionAdditions() {
        return extensionAdditions;
    }

    protected abstract String getType();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (rootComponents != null) {
            sb.append("(").append(StringUtils.join(rootComponents, ","))
                    .append(")");
        }

        if (extensionAndException != null) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(extensionAndException);
        }

        if (extensionAdditions != null) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(extensionAdditions);
        }

        if (optionalExtensionMarker != null) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(optionalExtensionMarker);
        }

        if (extensionRootComponents != null) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("(").append(StringUtils.join(extensionRootComponents, ","))
                    .append(")");
        }

        return StringUtils.concat(getType() + "[", sb.toString(), "]");
    }

}
