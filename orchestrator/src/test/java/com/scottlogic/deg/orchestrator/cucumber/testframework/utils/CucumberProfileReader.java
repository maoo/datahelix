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

package com.scottlogic.deg.orchestrator.cucumber.testframework.utils;

import com.google.gson.JsonParseException;
import com.google.inject.Inject;
import com.scottlogic.deg.common.profile.Field;
import com.scottlogic.deg.generator.profile.Profile;
import com.scottlogic.deg.common.profile.ProfileFields;
import com.scottlogic.deg.generator.profile.Rule;
import com.scottlogic.deg.generator.profile.constraints.Constraint;
import com.scottlogic.deg.profile.dto.FieldDTO;
import com.scottlogic.deg.profile.dto.ProfileDTO;
import com.scottlogic.deg.profile.dto.RuleDTO;
import com.scottlogic.deg.profile.reader.*;
import com.scottlogic.deg.generator.profile.RuleInformation;
import com.scottlogic.deg.profile.reader.atomic.AtomicConstraintValueReader;
import com.scottlogic.deg.profile.reader.atomic.FromFileReader;
import com.scottlogic.deg.profile.serialisation.ProfileSerialiser;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class CucumberProfileReader extends JsonProfileReader {

    private final CucumberTestState state;

    @Inject
    public CucumberProfileReader(CucumberTestState state, MainConstraintReader mainConstraintReader) {
        super(null, mainConstraintReader);
        this.state = state;
    }

    @Override
    public Profile read() throws IOException {
        return read(createJson());
    }

    private String createJson() throws IOException{
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.schemaVersion = "0.10";
        profileDTO.fields = state.profileFields;

        RuleDTO ruleDTO = new RuleDTO();
        ruleDTO.constraints = state.constraints;
        profileDTO.rules = Arrays.asList(ruleDTO);

        return new ProfileSerialiser().serialise(profileDTO);
    }


}
