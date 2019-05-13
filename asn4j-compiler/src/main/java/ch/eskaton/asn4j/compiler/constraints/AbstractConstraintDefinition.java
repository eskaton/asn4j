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

public abstract class AbstractConstraintDefinition<V, C extends Collection<V>, T extends GenericConstraint<T>,
        D extends ConstraintDefinition<V, C, T, D>> implements ConstraintDefinition<V, C, T, D> {

    private T roots;

    private T extensions;

    private boolean extensible;

    public AbstractConstraintDefinition() {
        this.roots = createConstraint();
        this.extensions = createConstraint();
    }

    public AbstractConstraintDefinition(T roots, T extensions) {
        this();

        setRoots(roots);
        setExtensions(extensions);
    }

    @Override
    public T getRoots() {
        return roots;
    }

    @Override
    public void setRoots(T roots) {
        if (roots == null) {
            this.roots = createConstraint();
        } else {
            this.roots = roots;
        }
    }

    @Override
    public D roots(T roots) {
        setRoots(roots);

        return (D) this;
    }

    @Override
    public T getExtensions() {
        return extensions;
    }

    @Override
    public void setExtensions(T extensions) {
        if (extensions == null) {
            this.extensions = createConstraint();
        } else {
            this.extensions = extensions;
        }
    }

    @Override
    public D extensions(T extensions) {
        setExtensions(extensions);

        return (D) this;
    }

    @Override
    public boolean isExtensible() {
        return extensible;
    }

    @Override
    public void setExtensible(boolean extensible) {
        this.extensible = extensible;
    }

    @Override
    public D extensible(boolean extensible) {
        setExtensible(extensible);

        return (D) this;
    }

}
