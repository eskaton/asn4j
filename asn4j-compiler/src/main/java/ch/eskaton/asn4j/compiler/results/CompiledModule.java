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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CompiledModule implements CompilationResult {

    private String name;

    private HashMap<String, CompiledType> types = new HashMap<>();

    private HashMap<String, CompiledObject> objects = new HashMap<>();

    private HashMap<String, CompiledObjectClass> objectClasses = new HashMap<>();

    private HashMap<String, CompiledObjectSet> objectSets = new HashMap<>();

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
                Objects.equals(objects, that.objects) &&
                Objects.equals(objectClasses, that.objectClasses) &&
                Objects.equals(objectSets, that.objectSets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, types, objects, objectClasses, objectSets);
    }

}
