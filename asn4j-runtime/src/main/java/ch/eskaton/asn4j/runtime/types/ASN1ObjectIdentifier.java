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

package ch.eskaton.asn4j.runtime.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.commons.utils.StringUtils;

@ASN1Tag(clazz = ASN1Tag.Clazz.Universal, tag = 6, mode = ASN1Tag.Mode.Explicit, constructed = false)
public class ASN1ObjectIdentifier implements ASN1Type {

    private List<Integer> components;

    public void setValue(Integer... ints) {
    	components = new ArrayList<Integer>(Arrays.asList(ints));
    }

    public void setValue(List<Integer> components) {
    	this.components = new ArrayList<Integer>(components);
    }

    public List<Integer> getValue() {
    	return components;
    }

    @Override
    public String toString() {
    	return components != null ? StringUtils.join(components, ".") : "null";
    }

    @Override
    public int hashCode() {
    	final int prime = 31;
    	int result = 1;
    	result = prime * result
    			+ ((components == null) ? 0 : components.hashCode());
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
    	ASN1ObjectIdentifier other = (ASN1ObjectIdentifier) obj;
    	if (components == null) {
    		if (other.components != null)
    			return false;
    	} else if (!components.equals(other.components))
    		return false;
    	return true;
    }

}
