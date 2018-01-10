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

import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.commons.utils.StringUtils;

public class ComponentType extends AbstractType {

    public enum CompType {
    	NamedType, NamedTypeOpt, NamedTypeDef, Type
    }

    private CompType compType;

    private NamedType namedType;

    private Type type;

    private Value value;

    public ComponentType(CompType compType, Type type) {
    	this.compType = compType;
    	this.type = type;
    }

    public ComponentType(CompType compType, NamedType namedType) {
    	this.compType = compType;
    	this.namedType = namedType;
    }

    public ComponentType(CompType compType, NamedType namedType, Value value) {
    	this.compType = compType;
    	this.namedType = namedType;
    	this.value = value;
    }

    public CompType getCompType() {
    	return compType;
    }

    public NamedType getNamedType() {
    	return namedType;
    }

    public Type getType() {
    	return type;
    }

    public Value getValue() {
    	return value;
    }

    @Override
    public String toString() {
    	switch (compType) {
    		case NamedType:
    			return StringUtils.concat("ComponentType[namedType=",
    					namedType, "]");
    		case NamedTypeOpt:
    			return StringUtils.concat("ComponentType[namedType=",
    					namedType, ",optional", "]");
    		case NamedTypeDef:
    			return StringUtils.concat("ComponentType[namedType=",
    					namedType, ",default=", value, "]");
    		case Type:
    			return StringUtils.concat("ComponentType[Components of type=",
    					type, "]");
    		default:
    			throw new RuntimeException("Implementation error");
    	}
    }

}
