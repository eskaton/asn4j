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

public interface ConstraintDefinition<V, C extends Collection<V>, T extends ConstraintValues<V, C, T>, D extends ConstraintDefinition<V, C, T, D>> {

    T createValues();

    D createDefinition();

    T getRootValues();

    void setRootValues(T values);

    D rootValues(T values);

    T getExtensionValues();

    void setExtensionValues(T values);

    D extensionValues(T values);

    void setExtensible(boolean extensible);

    boolean isExtensible();

    D extensible(boolean extensible);

    default D serialApplication(D other) {
        T roots = getRootValues().intersection(other.getRootValues());
        T extensions = other.getExtensionValues();
        boolean extensible = other.isExtensible();

        return createDefinition().rootValues(roots).extensionValues(extensions).extensible(extensible);
    }

    default D intersection(D other) {
        T roots = getRootValues().intersection(other.getRootValues());
        T extensions = null;
        boolean extensible = false;

        if (!isExtensionEmpty() && !other.isExtensionEmpty()) {
            extensions = getRootValues().union(getExtensionValues())
                    .intersection(other.getRootValues().union(other.getExtensionValues()))
                    .exclude(roots);
            extensible = true;
        } else if (isExtensionEmpty()) {
            extensions = getRootValues().intersection(other.getExtensionValues());
            extensible = true;
        } else if (other.isExtensionEmpty()) {
            extensions = other.getRootValues().intersection(getExtensionValues());
            extensible = true;
        }

        return createDefinition().rootValues(roots).extensionValues(extensions).extensible(extensible);
    }

    default D union(D other) {
        T roots = getRootValues().union(other.getRootValues());
        T extensions = getExtensionValues().union(other.getExtensionValues());
        boolean extensible = false;

        if (!isExtensionEmpty() && !other.isExtensionEmpty()) {
            extensions = roots.union(extensions).exclude(roots);
            extensible = true;
        } else if (!isExtensionEmpty() || !other.isExtensionEmpty()) {
            extensible = true;
        }

        return createDefinition().rootValues(roots).extensionValues(extensions).extensible(extensible);
    }

    default boolean isRootEmpty() {
        return getRootValues().getValues().isEmpty();
    }

    default boolean isExtensionEmpty() {
        return getExtensionValues().getValues().isEmpty();
    }
}
