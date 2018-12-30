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

import ch.eskaton.asn4j.parser.ast.types.Choice;
import ch.eskaton.asn4j.parser.ast.types.EnumeratedType;
import ch.eskaton.asn4j.parser.ast.types.IRI;
import ch.eskaton.asn4j.parser.ast.types.ObjectIdentifier;
import ch.eskaton.asn4j.parser.ast.types.RelativeOID;
import ch.eskaton.asn4j.parser.ast.types.SelectionType;
import ch.eskaton.asn4j.parser.ast.types.SequenceOfType;
import ch.eskaton.asn4j.parser.ast.types.SequenceType;
import ch.eskaton.asn4j.parser.ast.types.SetOfType;
import ch.eskaton.asn4j.parser.ast.types.SetType;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.types.TypeReference;
import ch.eskaton.asn4j.parser.ast.types.UsefulType;

public class TypeCompiler implements NamedCompiler<Type> {

    @SuppressWarnings("unchecked")
    public void compile(CompilerContext ctx, String name, Type node) throws CompilerException {
        if (node instanceof SequenceType) {
            ctx.<SequenceType, SequenceCompiler>getCompiler(SequenceType.class).compile(ctx, name, (SequenceType) node);
        } else if (node instanceof SequenceOfType) {
            ctx.<SequenceOfType, SequenceOfCompiler>getCompiler(SequenceOfType.class).compile(ctx, name, (SequenceOfType) node);
        } else if (node instanceof SetType) {
            ctx.<SetType, SetCompiler>getCompiler(SetType.class).compile(ctx, name, (SetType) node);
        } else if (node instanceof SetOfType) {
            ctx.<SetOfType, SetOfCompiler>getCompiler(SetOfType.class).compile(ctx, name, (SetOfType) node);
        } else if (node instanceof Choice) {
            ctx.<Choice, ChoiceCompiler>getCompiler(Choice.class).compile(ctx, name, (Choice) node);
        } else if (node instanceof ObjectIdentifier) {
            ctx.<ObjectIdentifier, ObjectIdentifierCompiler>getCompiler(ObjectIdentifier.class)
                    .compile(ctx, name, (ObjectIdentifier) node);
        } else if (node instanceof RelativeOID) {
            ctx.<RelativeOID, RelativeOIDCompiler>getCompiler(RelativeOID.class)
                    .compile(ctx, name, (RelativeOID) node);
        } else if (node instanceof IRI) {
            ctx.<IRI, IRICompiler>getCompiler(IRI.class).compile(ctx, name, (IRI) node);
        } else if (node instanceof TypeReference) {
            if (node instanceof UsefulType) {
                ctx.<UsefulType, UsefulTypeCompiler>getCompiler(UsefulType.class).compile(ctx, name, (UsefulType) node);
            } else {
                ctx.<TypeReference, TypeReferenceCompiler>getCompiler(TypeReference.class).compile(ctx, name, (TypeReference) node);
            }
        } else if (node instanceof SelectionType) {
            ctx.<SelectionType, SelectionTypeCompiler>getCompiler(SelectionType.class)
                    .compile(ctx, name, (SelectionType) node);
        } else if (node instanceof EnumeratedType) {
            ctx.<EnumeratedType, EnumeratedTypeCompiler>getCompiler(EnumeratedType.class)
                    .compile(ctx, name, (EnumeratedType) node);
        } else {
            if (ctx.isBuiltin(node.getClass().getSimpleName())) {
                ctx.<Type, BuiltinTypeCompiler<Type>>getCompiler((Class<Type>) node.getClass()).compile(ctx, name, node);
            } else {
                throw new CompilerException("Unsupported Type: " + node.getClass());
            }
        }
    }

}
