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

package ch.eskaton.asn4j.compiler.values;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.compiler.Parameters;
import ch.eskaton.asn4j.compiler.ValueCompiler;
import ch.eskaton.asn4j.compiler.ValueResolutionException;
import ch.eskaton.asn4j.compiler.results.CompiledType;
import ch.eskaton.asn4j.parser.ast.types.Choice;
import ch.eskaton.asn4j.parser.ast.types.Type;
import ch.eskaton.asn4j.parser.ast.values.ChoiceValue;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.runtime.types.TypeName;

import java.util.Optional;

import static ch.eskaton.asn4j.compiler.utils.TypeFormatter.formatType;

public class ChoiceValueCompiler extends AbstractValueCompiler<ChoiceValue> {

    public ChoiceValueCompiler() {
        super(TypeName.CHOICE, ChoiceValue.class);
    }

    @Override
    public ChoiceValue doCompile(CompilerContext ctx, CompiledType compiledType, Value value,
            Optional<Parameters> maybeParameters) {
        if (!value.getClass().isAssignableFrom(getValueClass())) {
            throw invalidValueError(value);
        }

        var type = compiledType.getType();
        var choiceValue = (ChoiceValue) value;
        var choiceType = (Choice) type;
        var resolvedType = ctx.resolveSelectedType(type);
        var valueType = choiceValue.getType();

        if (valueType != null) {
            // if the type is null, the value is defined in the context of the currently compiled CHOICE,
            // otherwise it must match the type of the latter
            checkTypes(ctx, resolvedType, ctx.resolveSelectedType(valueType));
        }

        var maybeNamedType = choiceType.getAllAlternatives().stream()
                .filter(nt -> nt.getName().equals(choiceValue.getId())).findFirst();

        if (!maybeNamedType.isPresent()) {
            throw new ValueResolutionException(choiceValue.getPosition(), "Invalid component %s in %s",
                    choiceValue.getId(), TypeName.CHOICE);
        }

        var namedType = maybeNamedType.get();
        var compiledValue = new ValueCompiler().compile(ctx, null, namedType.getType(), choiceValue.getValue(),
                maybeParameters);

        choiceValue.setValue(compiledValue.getValue());

        return choiceValue;
    }

    private void checkTypes(CompilerContext ctx, Type type1, Type type2) {
        if (!type1.equals(type2)) {
            throw new ValueResolutionException("Can't use a value of type %s where %s is expected",
                    formatType(ctx, type2), formatType(ctx, type1));
        }
    }

}
