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

import ch.eskaton.asn4j.compiler.results.CompiledValue;
import ch.eskaton.asn4j.parser.ParserException;
import ch.eskaton.asn4j.parser.ast.values.Value;
import ch.eskaton.asn4j.test.TestUtils;
import ch.eskaton.commons.collections.Tuple2;
import org.hamcrest.text.MatchesPattern;

import java.io.IOException;

import static ch.eskaton.asn4j.test.TestUtils.module;
import static ch.eskaton.commons.utils.Utils.rootCause;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CompilerTestUtils {

    public static final String MODULE_NAME = "TEST-MODULE";

    public static CompiledValue getCompiledValue(String body, Class<? extends Value> valueClass, String name)
            throws IOException, ParserException {
        var module = module(MODULE_NAME, body);
        var moduleSource = new StringModuleSource(Tuple2.of(MODULE_NAME, module));
        var compiler = new CompilerImpl(compilerConfig(MODULE_NAME), moduleSource);

        compiler.run();

        var ctx = compiler.getCompilerContext();
        var value = ctx.getCompiledModule(MODULE_NAME).getValues().get(name);

        assertNotNull(value);
        assertTrue(value.getValue().getClass().isAssignableFrom(valueClass));

        return value;
    }

    public static CompilerConfig compilerConfig(String moduleName) {
        return new CompilerConfig().module(moduleName).generateSource(false);
    }

    public static void testModule(String moduleName, String body, Class<? extends Exception> expected, String message) {
        var module = module(moduleName, body);
        var moduleSource = new StringModuleSource(Tuple2.of(moduleName, module));
        var exception = TestUtils.assertThrows(
                () -> new CompilerImpl(compilerConfig(moduleName), moduleSource).run(), expected);

        exception.ifPresent(e -> assertException(message, e));
    }

    private static void assertException(String message, Throwable th) {
        var matcher = MatchesPattern.matchesPattern(message);
        var rootCause = th;

        do {
            if (matcher.matches(rootCause.getMessage())) {
                return;
            }

            if (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
                rootCause = rootCause.getCause();
            } else {
                break;
            }
        } while (true);

        assertThat(rootCause.getMessage(), matcher);
    }

}
