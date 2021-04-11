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

package ch.eskaton.asn4j.compiler.parameters;

import ch.eskaton.asn4j.compiler.CompilerContext;
import ch.eskaton.asn4j.parser.ast.ObjectReference;
import ch.eskaton.asn4j.parser.ast.ObjectSetReference;
import ch.eskaton.asn4j.parser.ast.ParameterNode;
import ch.eskaton.asn4j.parser.ast.values.SimpleDefinedValue;

import static ch.eskaton.asn4j.compiler.parameters.ParameterGovernorHelper.getParameterObjectClass;
import static ch.eskaton.asn4j.compiler.parameters.ParameterGovernorHelper.getParameterType;

public class ParameterPredicates {

    private ParameterPredicates() {
    }

    static boolean isTypeParameter(ParameterNode definition, String reference) {
        return definition.getGovernor() == null && checkName(definition, reference);
    }

    static boolean isValueParameter(CompilerContext ctx, Parameters parameters, ParameterNode definition,
            SimpleDefinedValue simpleDefinedValue) {
        var value = simpleDefinedValue.getReference();
        var paramGovernor = definition.getGovernor();

        return paramGovernor != null &&
                !value.isBlank() &&
                value.toLowerCase().equals(value) &&
                checkName(definition, value) &&
                getParameterType(ctx, parameters, paramGovernor) != null;
    }

    static boolean isObjectClassParameter(ParameterNode definition, String reference) {
        var paramGovernor = definition.getGovernor();

        return paramGovernor != null &&
                reference.toUpperCase().equals(reference) &&
                checkName(definition, reference);
    }

    public static boolean isObjectParameter(CompilerContext ctx, Parameters parameters, ParameterNode definition,
            ObjectReference objectReference) {
        var reference = objectReference.getReference();

        return checkNameAndObjectClass(ctx, parameters, definition, reference);
    }

    public static boolean isObjectSetParameter(CompilerContext ctx, Parameters parameters, ParameterNode definition,
            ObjectSetReference objectSetReference) {
        var reference = objectSetReference.getReference();

        return checkNameAndObjectClass(ctx, parameters, definition, reference);
    }

    private static boolean checkNameAndObjectClass(CompilerContext ctx, Parameters parameters, ParameterNode definition,
            String reference) {
        var paramGovernor = definition.getGovernor();

        if (checkName(definition, reference)) {
            var objectClass = getParameterObjectClass(ctx, parameters, paramGovernor);

            return objectClass != null;
        }

        return false;
    }

    private static boolean checkName(ParameterNode definition, String reference) {
        return definition.getReference().getName().equals(reference);
    }

}
