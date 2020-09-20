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

package ch.eskaton.asn4j.compiler.results;

import ch.eskaton.asn4j.runtime.utils.ToString;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CompiledModule implements CompilationResult {

    private String name;

    private HashMap<String, CompiledType> types = new HashMap<>();

    private HashMap<String, CompiledValue> values = new HashMap<>();

    private HashMap<String, CompiledObject> objects = new HashMap<>();

    private HashMap<String, CompiledObjectClass> objectClasses = new HashMap<>();

    private HashMap<String, CompiledObjectSet> objectSets = new HashMap<>();

    private HashMap<String, CompiledParameterizedType> parameterizedTypes = new HashMap<>();

    private HashMap<String, CompiledParameterizedObjectClass> parameterizedObjectClass = new HashMap<>();

    private HashMap<String, CompiledParameterizedObjectSet> parameterizedObjectSet = new HashMap<>();

    private HashMap<String, CompiledParameterizedValueSetType> parameterizedValueSetType = new HashMap<>();

    public CompiledModule(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addType(String name, CompiledType compiledType) {
        types.put(name, compiledType);
    }

    public Map<String, CompiledType> getTypes() {
        return Collections.unmodifiableMap(types);
    }

    public void addValue(String name, CompiledValue compiledValue) {
        values.put(name, compiledValue);
    }

    public Map<String, CompiledValue> getValues() {
        return Collections.unmodifiableMap(values);
    }

    public void addObject(String name, CompiledObject compiledObject) {
        objects.put(name, compiledObject);
    }

    public Map<String, CompiledObject> getObjects() {
        return Collections.unmodifiableMap(objects);
    }

    public void addObjectClass(String name, CompiledObjectClass compiledObjectClass) {
        objectClasses.put(name, compiledObjectClass);
    }

    public Map<String, CompiledObjectClass> getObjectClasses() {
        return Collections.unmodifiableMap(objectClasses);
    }

    public void addObjectSet(String name, CompiledObjectSet compiledObjectSet) {
        objectSets.put(name, compiledObjectSet);
    }

    public Map<String, CompiledObjectSet> getObjectSets() {
        return Collections.unmodifiableMap(objectSets);
    }

    public void addParameterizedType(String name, CompiledParameterizedType compiledParameterizedType) {
        parameterizedTypes.put(name, compiledParameterizedType);
    }

    public Map<String, CompiledParameterizedType> getParameterizedTypes() {
        return Collections.unmodifiableMap(parameterizedTypes);
    }

    public void addParameterizedObjectClass(String name, CompiledParameterizedObjectClass compiledParameterizedObjectClass) {
        parameterizedObjectClass.put(name, compiledParameterizedObjectClass);
    }

    public Map<String, CompiledParameterizedObjectClass> getParameterizedObjectClass() {
        return Collections.unmodifiableMap(parameterizedObjectClass);
    }

    public void addParameterizedObjectSet(String name, CompiledParameterizedObjectSet compiledParameterizedObjectSet) {
        parameterizedObjectSet.put(name, compiledParameterizedObjectSet);
    }

    public Map<String, CompiledParameterizedObjectSet> getParameterizedObjectSet() {
        return Collections.unmodifiableMap(parameterizedObjectSet);
    }

    public void addParameterizedValueSetType(String name, CompiledParameterizedValueSetType compiledParameterizedValueSetType) {
        parameterizedValueSetType.put(name, compiledParameterizedValueSetType);
    }

    public Map<String, CompiledParameterizedValueSetType> getParameterizedValueSetType() {
        return Collections.unmodifiableMap(parameterizedValueSetType);
    }

    @Override
    public String toString() {
        return ToString.get(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CompiledModule that = (CompiledModule) o;

        return Objects.equals(name, that.name) &&
                Objects.equals(types, that.types) &&
                Objects.equals(values, that.values) &&
                Objects.equals(objects, that.objects) &&
                Objects.equals(objectClasses, that.objectClasses) &&
                Objects.equals(objectSets, that.objectSets) &&
                Objects.equals(parameterizedTypes, that.parameterizedTypes) &&
                Objects.equals(parameterizedObjectClass, that.parameterizedObjectClass) &&
                Objects.equals(parameterizedObjectSet, that.parameterizedObjectSet) &&
                Objects.equals(parameterizedValueSetType, that.parameterizedValueSetType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, types, values, objects, objectClasses, objectSets, parameterizedTypes,
                parameterizedObjectClass, parameterizedObjectSet, parameterizedValueSetType);
    }

}
