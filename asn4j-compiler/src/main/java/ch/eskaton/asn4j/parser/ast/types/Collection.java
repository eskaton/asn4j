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

import java.util.ArrayList;
import java.util.List;

import ch.eskaton.asn4j.parser.ast.ComponentTypeListsNode;
import ch.eskaton.commons.StringUtils;

public abstract class Collection extends AbstractType {

	private List<ComponentType> rootComponents;

	private List<ComponentType> extRootComponents;

	private Object extAndEx;

	private Object optExtMarker;

	private Object extAdditions;

	public Collection() {
	}

	public Collection(ComponentTypeListsNode compTypes) {
		this.rootComponents = compTypes.getRootComponents();
		this.extAndEx = compTypes.getExtAndEx();
		this.extAdditions = compTypes.getExtAdditions();
		this.optExtMarker = compTypes.getExtEndMarker();
		this.extRootComponents = compTypes.getExtRootComponents();
	}

	public Collection(Object extAndEx, Object optExtMarker) {
		this.extAndEx = extAndEx;
		this.optExtMarker = optExtMarker;
	}

	public List<ComponentType> getAllComponents() {
		List<ComponentType> components = new ArrayList<ComponentType>(2);
		if (rootComponents != null) {
			components.addAll(rootComponents);
		}

		if (extRootComponents != null) {
			components.addAll(extRootComponents);
		}

		return components;
	}

	public List<ComponentType> getRootComponents() {
		return rootComponents;
	}

	public List<ComponentType> getExtRootComponents() {
		return extRootComponents;
	}

	public Object getExtAndEx() {
		return extAndEx;
	}

	public Object getOptExtMarker() {
		return optExtMarker;
	}

	public Object getExtAdditions() {
		return extAdditions;
	}

	protected abstract String getType();

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		if (rootComponents != null) {
			sb.append("(").append(StringUtils.join(rootComponents, ","))
					.append(")");
		}

		if (extAndEx != null) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(extAndEx);
		}

		if (extAdditions != null) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(extAdditions);
		}

		if (optExtMarker != null) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(optExtMarker);
		}

		if (extRootComponents != null) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append("(").append(StringUtils.join(extRootComponents, ","))
					.append(")");
		}

		return StringUtils.concat(getType() + "[", sb.toString(), "]");
	}

}
