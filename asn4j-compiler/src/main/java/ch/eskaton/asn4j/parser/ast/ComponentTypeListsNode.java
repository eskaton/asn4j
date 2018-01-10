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

import java.util.List;

import ch.eskaton.asn4j.parser.ast.types.ComponentType;

public class ComponentTypeListsNode implements Node {

    private List<ComponentType> rootComponents;

    private List<ComponentType> extRootComponents;

    private ExtensionAndExceptionNode extAndEx;

    private Object extAdditions;

    private Boolean extEndMarker = false;

    public ComponentTypeListsNode(List<ComponentType> rootComponents,
    		ExtensionAndExceptionNode extAndEx, Object extAdditions,
    		Boolean extEndMarker) {
    	this.rootComponents = rootComponents;
    	this.extAndEx = extAndEx;
    	this.extAdditions = extAdditions;
    	this.extEndMarker = extEndMarker;
    }

    public ComponentTypeListsNode(List<ComponentType> rootComponents,
    		ExtensionAndExceptionNode extAndEx, Object extAdditions,
    		Boolean optExtMarker, List<ComponentType> extRootComponents) {
    	this.rootComponents = rootComponents;
    	this.extAndEx = extAndEx;
    	this.extAdditions = extAdditions;
    	this.extEndMarker = optExtMarker;
    	this.extRootComponents = extRootComponents;
    }

    public ComponentTypeListsNode(List<ComponentType> rootComponents) {
    	this.rootComponents = rootComponents;
    }

    public List<ComponentType> getRootComponents() {
    	return rootComponents;
    }

    public List<ComponentType> getExtRootComponents() {
    	return extRootComponents;
    }

    public ExtensionAndExceptionNode getExtAndEx() {
    	return extAndEx;
    }

    public Object getExtAdditions() {
    	return extAdditions;
    }

    public Boolean getExtEndMarker() {
    	return extEndMarker;
    }

}
