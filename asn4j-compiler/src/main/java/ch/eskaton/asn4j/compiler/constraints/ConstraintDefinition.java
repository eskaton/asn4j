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

import ch.eskaton.asn4j.compiler.constraints.ast.Node;
import ch.eskaton.asn4j.compiler.constraints.ast.BinOpNode;

import static ch.eskaton.asn4j.compiler.constraints.ast.NodeType.INTERSECTION;

public class ConstraintDefinition {

    private Node roots;

    private Node extensions;

    private boolean extensible;

    public ConstraintDefinition(Node roots, Node extensions) {
        this(roots, extensions, false);
    }

    public ConstraintDefinition(Node roots, Node extensions, boolean extensible) {
        this.roots = roots;
        this.extensions = extensions;
        this.extensible = extensible;
    }

    public Node getRoots() {
        return roots;
    }

    public void setRoots(Node roots) {
        this.roots = roots;
    }

    public ConstraintDefinition roots(Node roots) {
        setRoots(roots);

        return this;
    }

    public Node getExtensions() {
        return extensions;
    }

    public void setExtensions(Node extensions) {
        this.extensions = extensions;
    }

    public ConstraintDefinition extensions(Node extensions) {
        setExtensions(extensions);

        return this;
    }

    public void setExtensible(boolean extensible) {
        this.extensible = extensible;
    }

    public boolean isExtensible() {
        return extensible;
    }

    public ConstraintDefinition extensible(boolean extensible) {
        setExtensible(extensible);

        return this;
    }

    public ConstraintDefinition serialApplication(ConstraintDefinition other) {
        Node roots = new BinOpNode(INTERSECTION, getRoots(), other.getRoots());
        Node extensions = other.getExtensions();
        boolean extensible = other.isExtensible();

        return new ConstraintDefinition(roots, extensions, extensible);
    }

}
