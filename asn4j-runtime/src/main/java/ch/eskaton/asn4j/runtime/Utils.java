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

package ch.eskaton.asn4j.runtime;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ch.eskaton.asn4j.runtime.annotations.ASN1Tag;
import ch.eskaton.asn4j.runtime.exceptions.DecodingException;
import ch.eskaton.asn4j.runtime.types.ASN1Type;
import ch.eskaton.commons.utils.ReflectionUtils;

public class Utils {

    private Utils() {
    }

    public static List<ASN1Tag> getTags(Class<? extends ASN1Type> clazz) {
    	List<ASN1Tag> tags = new ArrayList<>(10);
    	ASN1Tag tag;

    	while (true) {
    		tag = clazz.getAnnotation(ASN1Tag.class);

    		while (tag == null) {
    			clazz = (Class<? extends ASN1Type>) clazz.getSuperclass();
    			tag = clazz.getAnnotation(ASN1Tag.class);
    		}

    		tags.add(tag);

    		if (tag.mode() != ASN1Tag.Mode.Explicit
    				|| tag.clazz() == Clazz.Universal) {
    			break;
    		}

    		clazz = (Class<? extends ASN1Type>) clazz.getSuperclass();
    	}

    	return tags;
    }

    public static <T extends ASN1Type> List<ASN1Tag> getTags(Class<T> type, ASN1Tag tag) {
    	List<ASN1Tag> tags;

    	if (tag != null) {
    		if (tag.mode() == ASN1Tag.Mode.Implicit) {
    			tags = new LinkedList<>();
    			tags.add(0, tag);
    		} else {
    			tags = getTags(type);
    			tags.add(0, tag);
    		}
    	} else {
    		tags = getTags(type);
    	}

    	if (tags.isEmpty()) {
    		throw new DecodingException("Invalid type provided: "
    				+ type.getClass().getSimpleName()
    				+ ". No ASN1Tag annotation found");
    	}

    	return tags;
    }

    public static List<Field> getComponents(ASN1Type type) {
    	List<Field> compFields = new ArrayList<>(20);
    	Class<?> clazz = type.getClass();

    	do {
    		Field[] fields = clazz.getDeclaredFields();

    		for (Field field : fields) {
    			compFields.add(field);
    		}

    		clazz = clazz.getSuperclass();
    	} while (clazz != null
    			&& ReflectionUtils.implementsInterface(clazz, ASN1Type.class));

    	return compFields;
    }

}
