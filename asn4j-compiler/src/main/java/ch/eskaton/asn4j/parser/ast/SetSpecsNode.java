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

import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.commons.StringUtils;

public class SetSpecsNode implements Node {

	private ElementSet rootElements;

	private ElementSet additionalElements;

	private boolean extensionMarker;

	public SetSpecsNode(boolean extensionMarker) {
		this(null, extensionMarker, null);
	}

	public SetSpecsNode(ElementSet rootElements) {
		this(rootElements, false, null);
	}

	public SetSpecsNode(ElementSet rootElements, boolean extensionMarker) {
		this(rootElements, extensionMarker, null);
	}

	public SetSpecsNode(ElementSet rootElements, boolean extensionMarker,
			ElementSet additionalElements) {
		this.rootElements = rootElements;
		this.extensionMarker = extensionMarker;
		this.additionalElements = additionalElements;
	}

	public ElementSet getRootElements() {
		return rootElements;
	}

	public ElementSet getAdditionalElements() {
		return additionalElements;
	}

	public boolean hasExtensionMarker() {
		return extensionMarker;
	}

	// public boolean isElementSetSpecs() {
	// return rootElements != null;
	// }

	public ElementSetSpecsNode toElementSetSpecs() {
		if (rootElements != null) {
			return new ElementSetSpecsNode(rootElements, extensionMarker,
					additionalElements);
		}

		return null;
	}

	public ObjectSetSpecNode toObjectSetSpec() {
		return new ObjectSetSpecNode(rootElements, extensionMarker,
				additionalElements);
	}

	@Override
	public String toString() {
		return StringUtils.concat("SetSpecs[",
				(rootElements != null ? String.valueOf(rootElements) : ""),
				(extensionMarker ? (rootElements != null ? ", " : "") + "..."
						: ""), (additionalElements != null ? (extensionMarker
						|| rootElements != null ? ", " : "")
						+ String.valueOf(additionalElements) : ""), "]");
	}

}
