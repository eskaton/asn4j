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

import ch.eskaton.asn4j.logging.Logger;
import ch.eskaton.asn4j.logging.LoggerFactory;
import ch.eskaton.commons.collections.Tuple2;
import ch.eskaton.commons.io.FileSourceInputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileModuleSource implements ModuleSource {

    private static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final String ASN_1_EXTENSION = "asn1";

    public static final String ASN_EXTENSION = "asn";

    private List<String> includePaths;

    public FileModuleSource(String includePaths) {
        this.includePaths = Arrays.asList(includePaths.split(File.pathSeparator));
    }

    @Override
    public List<Tuple2<String, InputStream>> getModules() {
        List<Tuple2<String, InputStream>> modules = new ArrayList<>();

        for (var includePath : includePaths) {
            try (DirectoryStream<Path> directoryStream = getDirectoryStream(includePath)) {
                for (Path path : directoryStream) {
                    var file = path.toFile();

                    LOGGER.info("Adding ASN.1 source file: %s", file);

                    modules.add(Tuple2.of(file.getAbsolutePath(), new FileSourceInputStream(path.toFile())));
                }
            } catch (DirectoryIteratorException | IOException e) {
                throw new CompilerException(e);
            }
        }

        return modules;
    }

    private DirectoryStream<Path> getDirectoryStream(String path) throws IOException {
        return Files.newDirectoryStream(Paths.get(path), "*.{%s,%s}".formatted(ASN_EXTENSION, ASN_1_EXTENSION));
    }

}
