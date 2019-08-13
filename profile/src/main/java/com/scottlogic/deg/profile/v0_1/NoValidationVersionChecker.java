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

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.scottlogic.deg.profile.serialisation.SchemaVersionGetter;
import com.sun.javafx.geom.transform.Identity;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NoValidationVersionChecker implements SchemaVersionValidator {
    private final SchemaVersionGetter schemaVersionGetter;
    private final File profile;
    @Inject
    public NoValidationVersionChecker(
        SchemaVersionGetter schemaVersionGetter,
        @Named("config:profileFile") File profile
    ) {
        this.schemaVersionGetter = schemaVersionGetter;
        this.profile = profile;
    }

    /**
     * @return URL of schema file that matches the input profile's schemaVersion
     * @throws IOException
     */
    @Override
    public URL getSchemaFile() throws IOException {
        String schemaVersion = schemaVersionGetter.getSchemaVersionOfJson(profile.toPath());
        URL exactMatchSchemaVersion = this.getClass().getResource(
            "/profileschema/" +
            schemaVersion +
            "/datahelix.schema.json");
        if (exactMatchSchemaVersion != null) {
            return exactMatchSchemaVersion;
        } else {
            return getDefaultSchemaVersion();
        }
    }

    private URL getDefaultSchemaVersion() {
        List<String> schemaVersions = getSupportedSchemaVersionsFromResources();
        String highestVersion = schemaVersions.stream().reduce("0", (a, b) -> {
            if (Double.parseDouble(a) < Double.parseDouble(b)) {
                return b;
            } else {
                return a;
            }
        });
        return this.getClass().getResource(
        "/profileschema/" +
            highestVersion +
            "/datahelix.schema.json");
    }

    private List<String> getSupportedSchemaVersionsFromResources() {
        String directoryContainingSchemas = this.getClass().getResource("/profileschema").getPath();
        File file = new File(directoryContainingSchemas);
        String[] directoriesArray = file.list((current, name) -> new File(current, name).isDirectory());
        List<String> directories;
        if (directoriesArray != null) {
            directories = Arrays.asList(directoriesArray);
        } else {
            directories = new ArrayList<>();
        }
        return directories;
    }
}
