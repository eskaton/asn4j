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
import ch.eskaton.asn4j.parser.ast.AbstractNode;
import ch.eskaton.asn4j.parser.ast.ExtensionAdditionAlternativeNode;
import ch.eskaton.asn4j.parser.ast.ExtensionAndExceptionNode;
import ch.eskaton.commons.utils.StringUtils;

import java.util.List;

public class AlternativeTypeLists extends AbstractNode {

    private List<NamedType> rootTypeList;

    private ExtensionAndExceptionNode extensionAndException;

    private List<ExtensionAdditionAlternativeNode> extAddAlts;

    private boolean optExtMarker;

    public AlternativeTypeLists(Position position, List<NamedType> rootTypeList) {
        super(position);

    	this.rootTypeList = rootTypeList;
    }

    public AlternativeTypeLists(Position position, List<NamedType> rootTypeList,
    		ExtensionAndExceptionNode extensionAndException, List<ExtensionAdditionAlternativeNode> extAddAlts,
    		boolean optExtMarker) {
        super(position);

    	this.rootTypeList = rootTypeList;
    	this.extensionAndException = extensionAndException;
    	this.extAddAlts = extAddAlts;
    	this.optExtMarker = optExtMarker;
    }

    public List<NamedType> getRootTypeList() {
    	return rootTypeList;
    }

    public ExtensionAndExceptionNode getExtensionAndException() {
    	return extensionAndException;
    }

    public List<ExtensionAdditionAlternativeNode> getExtAddAlts() {
    	return extAddAlts;
    }

    public boolean hasOptExtMarker() {
    	return optExtMarker;
    }

}
