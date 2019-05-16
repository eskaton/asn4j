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

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.CompilerException;
import ch.eskaton.asn4j.compiler.java.JavaClass;
import ch.eskaton.asn4j.compiler.java.JavaClass.BodyBuilder;
import ch.eskaton.asn4j.parser.ast.constraints.ContainedSubtype;
import ch.eskaton.asn4j.parser.ast.constraints.ElementSet;
import ch.eskaton.asn4j.parser.ast.constraints.Elements;
import ch.eskaton.asn4j.parser.ast.constraints.SingleValueConstraint;
import ch.eskaton.asn4j.parser.ast.types.BooleanType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.values.BooleanValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.exceptions.ConstraintViolatedException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static ch.eskaton.asn4j.compiler.java.JavaVisibility.Protected;

public class BooleanConstraintCompiler extends AbstractConstraintCompiler<Boolean, Set<Boolean>, BooleanValueConstraint,
        BooleanConstraintDefinition> {

    public BooleanConstraintCompiler(CompilerContext ctx) {
        super(ctx);
    }

    @Override
    Optional<Bounds> getBounds(Optional<BooleanConstraintDefinition> constraint) {
        return Optional.empty();
    }

    @Override
    protected BooleanConstraintDefinition createDefinition(BooleanValueConstraint roots,
            BooleanValueConstraint extensions) {
        return new BooleanConstraintDefinition(roots, extensions);
    }

    @Override
    protected BooleanValueConstraint createConstraint() {
        return new BooleanValueConstraint();
    }

    protected BooleanValueConstraint calculateElements(Type base, Elements elements, Optional<Bounds> bounds)
            throws CompilerException {
        if (elements instanceof ElementSet) {
            return compileConstraint(base, (ElementSet) elements, bounds);
        } else if (elements instanceof SingleValueConstraint) {
            Value value = ((SingleValueConstraint) elements).getValue();
            if (value instanceof BooleanValue) {
                return new BooleanValueConstraint(new HashSet<>(
                        Collections.singletonList(((BooleanValue) value).getValue())));
            } else {
                throw new CompilerException("Invalid single-value constraint %s for BOOLEAN type",
                        value.getClass().getSimpleName());
            }
        } else if (elements instanceof ContainedSubtype) {
            Type type = ((ContainedSubtype) elements).getType();
            return calculateContainedSubtype(type);
        } else {
            throw new CompilerException("Invalid constraint %s for BOOLEAN type",
                    elements.getClass().getSimpleName());
        }
    }

    private BooleanValueConstraint calculateContainedSubtype(Type type) throws CompilerException {
        if (type instanceof BooleanType) {
            return BooleanValueConstraint.ALL.copy();
        } else if (type instanceof TypeReference) {
            return compileConstraints(type, ctx.getBase((TypeReference) type)).getRoots();
        } else {
            throw new CompilerException("Invalid type %s in constraint for BOOLEAN type", type);
        }
    }

    @Override
    public void addConstraint(JavaClass javaClass, ConstraintDefinition definition) {
        BooleanValueConstraint rootValues = ((BooleanConstraintDefinition) definition).getRoots();
        BooleanValueConstraint extensionValues = ((BooleanConstraintDefinition) definition).getExtensions();
        Set<Boolean> values = rootValues.union(extensionValues).getValues();

        if (values.size() == 2) {
            return;
        }

        BodyBuilder builder = javaClass.method().annotation("@Override").modifier(Protected)
                .returnType(boolean.class).name("checkConstraint").parameter("Boolean", "value")
                .exception(ConstraintViolatedException.class).body();

        if (values.isEmpty()) {
            builder.append("return false;");
        } else {
            builder.append("if(value == " + values.iterator().next() + ") {")
                    .append("\treturn true;")
                    .append("} else {")
                    .append("\treturn false;")
                    .append("}");
        }

        builder.finish().build();
    }

}
