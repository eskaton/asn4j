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

import java.util.List;

import ch.eskaton.asn4j.parser.Position;
import ch.eskaton.asn4j.parser.ast.ExceptionIdentificationNode;
import ch.eskaton.asn4j.parser.ast.ExtensionAdditionAlternativeNode;
import ch.eskaton.commons.utils.StringUtils;

public class Choice extends AbstractType {

    private List<NamedType> rootTypeList;

    private boolean ext;

    private ExceptionIdentificationNode exId;

    private List<ExtensionAdditionAlternativeNode> extAddAlts;

    private boolean optExtMarker;

    public Choice(Position position, AlternativeTypeLists alternatives) {
        super(position);

    	this.rootTypeList = alternatives.getRootTypeList();
    	this.ext = alternatives.getExtensionAndException() != null;
    	this.exId = this.ext ? alternatives.getExtensionAndException().getExceptionId() : null;
    	this.extAddAlts = alternatives.getExtAddAlts();
    	this.optExtMarker = alternatives.hasOptExtMarker();
    }

    public List<NamedType> getRootTypeList() {
    	return rootTypeList;
    }

    public boolean isExt() {
    	return ext;
    }

    public ExceptionIdentificationNode getExId() {
    	return exId;
    }

    public List<ExtensionAdditionAlternativeNode> getExtAddAlts() {
    	return extAddAlts;
    }

    public boolean isOptExtMarker() {
    	return optExtMarker;
    }

    @Override
    public String toString() {
    	return StringUtils.concat("Choice[ext=", ext, ", optExtMarker=",
    			optExtMarker, (exId != null ? ", exceptionId=" + exId : ""),
    			", root=(", StringUtils.join(rootTypeList, ","),
    			"), extAlts=(",
    			extAddAlts != null ? StringUtils.join(extAddAlts, ",") : "",
    			")", "]");
    }

}
