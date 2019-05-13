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

public interface ConstraintDefinition<V, C extends Collection<V>, T extends GenericConstraint<T>,
        D extends ConstraintDefinition<V, C, T, D>> {

    T createConstraint();

    D createDefinition();

    T getRoots();

    void setRoots(T roots);

    D roots(T roots);

    T getExtensions();

    void setExtensions(T extensions);

    D extensions(T extensions);

    void setExtensible(boolean extensible);

    boolean isExtensible();

    D extensible(boolean extensible);

    default D serialApplication(D other) {
        T roots = getRoots().intersection(other.getRoots());
        T extensions = other.getExtensions();
        boolean extensible = other.isExtensible();

        return createDefinition().roots(roots).extensions(extensions).extensible(extensible);
    }

    default D intersection(D other) {
        T roots = getRoots().intersection(other.getRoots());
        T extensions = null;
        boolean extensible = false;

        if (!isExtensionEmpty() && !other.isExtensionEmpty()) {
            extensions = getRoots().union(getExtensions())
                    .intersection(other.getRoots().union(other.getExtensions()))
                    .exclude(roots);
            extensible = true;
        } else if (isExtensionEmpty()) {
            extensions = getRoots().intersection(other.getExtensions());
            extensible = true;
        } else if (other.isExtensionEmpty()) {
            extensions = other.getRoots().intersection(getExtensions());
            extensible = true;
        }

        return createDefinition().roots(roots).extensions(extensions).extensible(extensible);
    }

    default D union(D other) {
        T roots = getRoots().union(other.getRoots());
        T extensions = getExtensions().union(other.getExtensions());
        boolean extensible = false;

        if (!isExtensionEmpty() && !other.isExtensionEmpty()) {
            extensions = roots.union(extensions).exclude(roots);
            extensible = true;
        } else if (!isExtensionEmpty() || !other.isExtensionEmpty()) {
            extensible = true;
        }

        return createDefinition().roots(roots).extensions(extensions).extensible(extensible);
    }

    default boolean isRootEmpty() {
        return getRoots().isEmpty() && !getRoots().isInverted();
    }

    default boolean isExtensionEmpty() {
        return getExtensions().isEmpty() && !getExtensions().isInverted();
    }

}
