/*
 * Copyright 2019 Scott Logic Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.scottlogic.deg.profile.v0_1;

import com.scottlogic.deg.common.ValidationException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class SchemaVersionValidator {
    private String directoryContainingSchemas;
    public SchemaVersionValidator(String directoryContainingSchemas) {
        this.directoryContainingSchemas = directoryContainingSchemas;
    }

    public URL getSchemaFile(String schemaVersion) throws MalformedURLException {
        validateSchemaVersion(schemaVersion);
        String protocol = "file://";
        return new URL(protocol + directoryContainingSchemas + schemaVersion + "/datahelix.schema.json");
    }

    private void validateSchemaVersion(String schemaVersion) {
        List<String> supportedSchemaVersions = new ArrayList<>();
        final String path = "profileschema";
        final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

        try {
            // Taken from https://stackoverflow.com/a/20073154/
            if (jarFile.isFile()) {  // Run with JAR file
                final JarFile jar = new JarFile(jarFile);
                final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
                while (entries.hasMoreElements()) {
                    final String name = entries.nextElement().getName();
                    if (name.startsWith(path + "/")) { //filter according to the path
                        if (name.split("/").length == 2) {
                            supportedSchemaVersions.add(name.split("/")[1]);
                        }
                    }
                }
                jar.close();
            } else {
                supportedSchemaVersions = getSupportedSchemaVersions();
            }
        } catch (IOException e) {

        }

        if (!supportedSchemaVersions.contains(schemaVersion)) {
            String errorMessage = "This version of the generator does not support v" +
                schemaVersion +
                " of the schema. Supported schema versions are " +
                supportedSchemaVersions;
            throw new ValidationException(errorMessage);
        }
    }

    private List<String> getSupportedSchemaVersions() {
        File file = new File(directoryContainingSchemas);
        String[] directoriesArray = file.list((current, name) -> new File(current, name).isDirectory());
        List<String> directories = new ArrayList<>();
        if (directoriesArray != null) {
            directories = Arrays.asList(directoriesArray);
        }
        return directories;
    }
}
