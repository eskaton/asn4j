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

package ch.eskaton.asn4j.parser.ast;

import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.parser.Position;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptySet;

public class ModuleBodyNode extends AbstractNode {

    private ExportsNode exports;

    private List<ImportNode> imports = new ArrayList<>();

    private Map<String, AssignmentNode> assignments = new HashMap<>();

    public ModuleBodyNode(Position position, ExportsNode exports, List<ImportNode> imports,
            List<AssignmentNode> assignments) {
        super(position);

        this.exports = exports;

        if (imports != null) {
            this.imports.addAll(imports);
        }

        if (assignments != null) {
            for (AssignmentNode assignment : assignments) {
                var typeName = assignment.getReference();

                if (this.assignments.containsKey(assignment.getReference())) {
                    throw new CompilerException("Type %s is already defined", typeName);
                }

                this.assignments.put(typeName, assignment);
            }
        }
    }

    public ExportsNode getExports() {
        return exports;
    }

    public List<ImportNode> getImports() {
        return imports;
    }

    public Collection<AssignmentNode> getAssignments() {
        if (assignments != null) {
            return assignments.values();
        } else {
            return emptySet();
        }
    }

    public AssignmentNode getAssignments(String name) {
        if (assignments != null && assignments.containsKey(name)) {
            return assignments.get(name);
        }

        return null;
    }

}
