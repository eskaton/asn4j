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

package ch.eskaton.asn4j.compiler.constraints;

import java.util.Collection;

public abstract class AbstractConstraintDefinition<V, C extends Collection<V>, T extends ConstraintValues<V, C, T>,
        D extends ConstraintDefinition<V, C, T, D>> implements ConstraintDefinition<V, C, T, D> {

    protected T rootValues;

    protected T extensionValues;

    public AbstractConstraintDefinition() {
        this.rootValues = createValues();
        this.extensionValues = createValues();
    }

    public AbstractConstraintDefinition(T rootValues, T extensionValues) {
        this();

        setRootValues(rootValues);
        setExtensionValues(extensionValues);
    }

    public T getRootValues() {
        return rootValues;
    }

    public void setRootValues(T rootValues) {
        if (rootValues == null) {
            this.rootValues = createValues();
        } else {
            this.rootValues = rootValues;
        }
    }

    public T getExtensionValues() {
        return extensionValues;
    }

    public void setExtensionValues(T extensionValues) {
        if (extensionValues == null) {
            this.extensionValues = createValues();
        } else {
            this.extensionValues = extensionValues;
        }
    }

}
