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

package ch.eskaton.asn4j.compiler;

import ch.eskaton.asn4j.compiler.results.CompiledFixedTypeValueField;
import ch.eskaton.asn4j.compiler.results.CompiledObjectClass;
import ch.eskaton.asn4j.compiler.results.CompiledObjectSet;
import ch.eskaton.asn4j.parser.ast.ObjectDefnNode;
import ch.eskaton.asn4j.parser.ast.ObjectReference;
import ch.eskaton.asn4j.parser.ast.ObjectSetAssignmentNode;
import ch.eskaton.asn4j.parser.ast.ObjectSetElements;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.commons.collections.Sets;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObjectSetAssignmentCompiler implements Compiler<ObjectSetAssignmentNode> {

    private CompilerContext ctx;

    public ObjectSetAssignmentCompiler(CompilerContext ctx) {
        this.ctx = ctx;
    }

    public CompiledObjectSet compile(ObjectSetAssignmentNode node) {
        var objectClassReference = node.getObjectClassReference();
        var objectSet = node.getObjectSet();
        var objectSetName = node.getReference();
        var objectClass = ctx.getCompiledObjectClass(objectClassReference);

        System.out.println("Compiling object set " + objectSetName);

        var compiledObjectSet = ctx.createCompiledObjectSet(objectSetName, objectClass);
        var values = compile(objectClass, objectSet.getRootElements());

        objectClass.getFields().stream()
                .filter(CompiledFixedTypeValueField.class::isInstance)
                .map(CompiledFixedTypeValueField.class::cast)
                .filter(CompiledFixedTypeValueField::isUnique)
                .forEach(f -> {
                    var fieldName = f.getName();
                    var fieldValues = new HashSet<Value>();

                    for (var value : values) {
                        var fieldValue = (Value) value.get(fieldName);

                        if (fieldValue == null) {
                            // skip optional fields
                            continue;
                        }

                        if (fieldValues.contains(fieldValue)) {
                            throw new CompilerException(fieldValue.getPosition(), "Duplicate value in object set: %s",
                                    fieldValue);
                        }

                        fieldValues.add(fieldValue);
                    }
                });

        compiledObjectSet.setValues(values);

        return compiledObjectSet;
    }

    private Set<Map<String, Object>> compile(CompiledObjectClass objectClass, ElementSet elementSet) {
        List<Elements> operands = elementSet.getOperands();

        switch (elementSet.getOperation()) {
            case ALL:
                return new HashSet<>();
            case EXCLUDE:
                if (operands.size() == 1) {
                    return new HashSet<>();
                } else {
                    return exclusion(objectClass, operands);
                }
            case UNION:
                return union(objectClass, operands);
            case INTERSECTION:
                return intersection(objectClass, operands);
            default:
                throw new CompilerException(elementSet.getPosition(), "Unimplemented operation: %s",
                        elementSet.getOperation());
        }
    }

    private Set<Map<String, Object>> exclusion(CompiledObjectClass objectClass, List<Elements> operands) {
        var op1 = compile(objectClass, operands.get(0));
        var op2 = compile(objectClass, operands.get(1));

        op1.removeAll(op2);

        return op1;
    }

    private Set<Map<String, Object>> intersection(CompiledObjectClass objectClass, List<Elements> operands) {
        return operands.stream().map(op -> compile(objectClass, op)).reduce((op1, op2) -> {
            op1.retainAll(op2);

            return op1;
        }).orElse(new HashSet<>());
    }

    private Set<Map<String, Object>> union(CompiledObjectClass objectClass, List<Elements> operands) {
        return operands.stream().map(op -> compile(objectClass, op)).reduce((op1, op2) -> {
            op1.addAll(op2);

            return op1;
        }).orElse(new HashSet<>());
    }

    private Set<Map<String, Object>> compile(CompiledObjectClass objectClass, Elements elements) {
        if (elements instanceof ElementSet) {
            return compile(objectClass, (ElementSet) elements);
        } else if (elements instanceof ObjectSetElements) {
            return Sets.<Map<String, Object>>builder().add(compile(objectClass, (ObjectSetElements) elements)).build();
        } else {
            throw new CompilerException(elements.getPosition(), "Unsupported elements: %s",
                    elements.getClass().getSimpleName());
        }
    }

    private Map<String, Object> compile(CompiledObjectClass objectClass, ObjectSetElements elements) {
        var element = elements.getElement();

        if (element instanceof ObjectDefnNode objectDefn) {
            return ctx.<ObjectDefnNode, ObjectDefnCompiler>getCompiler(ObjectDefnNode.class)
                    .compile(objectClass, objectDefn);
        } else if (element instanceof ObjectReference objectReference) {
            return ctx.getCompiledObject(objectReference).getObjectDefinition();
        } else {
            throw new CompilerException(elements.getPosition(), "Unsupported element: %s",
                    element.getClass().getSimpleName());
        }
    }

}
