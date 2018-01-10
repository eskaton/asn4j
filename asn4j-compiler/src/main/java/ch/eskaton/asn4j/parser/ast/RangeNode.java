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

import ch.eskaton.asn4j.parser.ast.constraints.AbstractConstraint;
import ch.eskaton.commons.utils.StringUtils;

public class RangeNode extends AbstractConstraint {

    private EndpointNode lower;

    private EndpointNode upper;

    public RangeNode(EndpointNode lower, EndpointNode upper) {
    	this.lower = lower;
    	this.upper = upper;
    }

    public EndpointNode getLower() {
    	return lower;
    }

    public EndpointNode getUpper() {
    	return upper;
    }

    @Override
    public int hashCode() {
    	final int prime = 31;
    	int result = 1;
    	result = prime * result + ((lower == null) ? 0 : lower.hashCode());
    	result = prime * result + ((upper == null) ? 0 : upper.hashCode());
    	return result;
    }

    @Override
    public boolean equals(Object obj) {
    	if (this == obj)
    		return true;
    	if (obj == null)
    		return false;
    	if (getClass() != obj.getClass())
    		return false;
    	RangeNode other = (RangeNode) obj;
    	if (lower == null) {
    		if (other.lower != null)
    			return false;
    	} else if (!lower.equals(other.lower))
    		return false;
    	if (upper == null) {
    		if (other.upper != null)
    			return false;
    	} else if (!upper.equals(other.upper))
    		return false;
    	return true;
    }

    @Override
    public String toString() {
    	return StringUtils.concat("Range[", lower.isInclusive() ? "[" : "(",
    			lower.getValue(), "..", upper.getValue(),
    			upper.isInclusive() ? "]" : ")", "]");
    }

}
