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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scottlogic.deg.common.profile.Field;
import com.scottlogic.deg.common.profile.Types;
import com.scottlogic.deg.generator.config.detail.CombinationStrategyType;
import com.scottlogic.deg.generator.config.detail.DataGenerationType;
import com.scottlogic.deg.common.profile.constraintdetail.AtomicConstraintType;
import com.scottlogic.deg.profile.dto.ConstraintDTO;
import com.scottlogic.deg.profile.dto.FieldDTO;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import static com.scottlogic.deg.common.profile.FieldBuilder.createField;
import static com.scottlogic.deg.common.profile.FieldBuilder.createInternalField;

/**
 * Class to represent the state during cucumber test running and execution
 */
public class CucumberTestState {
    public DataGenerationType dataGenerationType = DataGenerationType.FULL_SEQUENTIAL;
    public CombinationStrategyType combinationStrategyType = CombinationStrategyType.PINNING;

    /**
     * Boolean to represent if the generation mode is validating or violating.
     * If true, generation is in violate mode.
     */
    public Boolean shouldViolate;
    public boolean expectExceptions;

    /** If true, we inject a no-op generation engine during the test (e.g. because we're just testing profile validation) */
    private Boolean shouldSkipGeneration;

    Boolean shouldSkipGeneration() { return shouldSkipGeneration; }
    void disableGeneration() { shouldSkipGeneration = true; }

    public long maxRows = 200;

    Boolean generationHasAlreadyOccured;

    List<List<Object>> generatedObjects;
    List<FieldDTO> profileFields;
    List<ConstraintDTO> constraints;
    List<Exception> testExceptions;
    Map<String, List<List<String>>> inMapFiles;

    private final List<AtomicConstraintType> contstraintsToNotViolate = new ArrayList<>();

    public CucumberTestState() {
        this.initialise();
    }

    public void initialise(){//TODO PAUL cleanup
        profileFields = new ArrayList<>();
        constraints = new ArrayList<>();
        testExceptions = new ArrayList<>();
        generatedObjects = new ArrayList<>();
        inMapFiles = new HashMap<>();
        contstraintsToNotViolate.clear();
        generationHasAlreadyOccured = false;
        shouldSkipGeneration = false;
        shouldViolate = false;
    }

    public void addConstraint(String fieldName, String constraintName, List<Object> value) {
        if (value == null)
            addConstraint(fieldName, constraintName, (Object)value);
        else
            addConstraint(fieldName, constraintName, getSetValues(value));
    }

    public void addInMapConstraint(String fieldName, String key, String file) {

        ConstraintDTO dto = new ConstraintDTO();
        dto.field = fieldName;
        dto.is = "inMap";
        dto.key = key;
        dto.file = file;
        dto.values = getValuesFromMap(file, key);
        this.addConstraintToList(dto);
    }

    public void addNotConstraint(String fieldName, String constraintName, List<Object> value) {
        if (value == null)
            addNotConstraint(fieldName, constraintName, (Object)value);
        else
            addNotConstraint(fieldName, constraintName, getSetValues(value));
    }

    public void addConstraint(String fieldName, String constraintName, Object value) {
        ConstraintDTO dto = this.createConstraint(fieldName, constraintName, value);
        this.addConstraintToList(dto);
    }

    public void addNotConstraint(String fieldName, String constraintName, Object value) {
        ConstraintDTO notDto = new ConstraintDTO();
        notDto.not = this.createConstraint(fieldName, constraintName, value);
        this.addConstraintToList(notDto);
    }

    public void addConstraintsFromJson(String constraintProfile) throws IOException {
        ConstraintHolder holder = this.deserialise(constraintProfile);
        this.constraints.addAll(holder.constraints);
    }

    public void addMapFile(String name, List<List<String>> map) {
        this.inMapFiles.put(name, map);
       // this.profileFields.add(createInternalField(name, Types.NUMERIC)); //TODO paul
    }

    private List<Object> getValuesFromMap(String name, String key) {
        List<List<String>> map = this.inMapFiles.get(name);
        int index = map.get(0).indexOf(key);
        List<Object> rtnList = new ArrayList<>();

        for (int i = 1; i < map.size() ; i++) {
            rtnList.add(map.get(i).get(index));
        }
        return rtnList;
    }

    private Collection<Object> getSetValues(List<Object> values) {
        if (values == null){
            throw new IllegalArgumentException("Values cannot be null");
        }

        values.stream()//TODO PAUL what is this????
            .filter(value -> value instanceof Exception)
            .map(value -> (Exception)value)
            .forEach(this::addException);

        return values.stream()
            .filter(value -> !(value instanceof Exception))
            .collect(Collectors.toSet());
    }

    public void addField(String fieldName) {
        FieldDTO fieldDTO = new FieldDTO();
        fieldDTO.name = fieldName;
        this.profileFields.add(fieldDTO);
    }

    public void addException(Exception e){
        this.testExceptions.add(e);
    }

    public void addConstraintToNotViolate(AtomicConstraintType atomicConstraintType){
        contstraintsToNotViolate.add(atomicConstraintType);
    }

    public List<AtomicConstraintType> getConstraintsToNotViolate() {
        return contstraintsToNotViolate;
    }

    private ConstraintDTO createConstraint(String fieldName, String constraintName, Object value) {
        ConstraintDTO dto = new ConstraintDTO();
        dto.field = fieldName;
        dto.is = this.extractConstraint(constraintName);
        if (value != null){
            if (value instanceof String) {
                dto.value = value;
            }
            else if (value instanceof Collection){
                dto.values = (Collection<Object>) value;
            } else {
                dto.value = value;
            }
        }
        return dto;
    }

    private String extractConstraint(String gherkinConstraint) {
        List<String> allConstraints = Arrays.asList(gherkinConstraint.split(" "));
        return allConstraints.get(0) + allConstraints
            .stream()
            .skip(1)
            .map(value -> value.substring(0, 1).toUpperCase() + value.substring(1))
            .collect(Collectors.joining());
    }

    private void addConstraintToList(ConstraintDTO constraintDTO) {
        this.constraints.add(constraintDTO);
    }

    private ConstraintHolder deserialise(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        return mapper.readerFor(ConstraintHolder.class).readValue(json);
    }

    public void setFieldUnique(String fieldName) {
        profileFields = profileFields.stream()
            .map(fieldDTO -> {
                if (fieldDTO.name.equals(fieldName)) {
                    fieldDTO.unique = true;
                }
                return fieldDTO;
            }).collect(Collectors.toList());
    }

    public void setFieldType(String fieldName, String types) {
        profileFields = profileFields.stream()
            .map(fieldDTO -> {
                if (fieldDTO.name.equals(fieldName)) {
                    fieldDTO.type = types;
                }
                return fieldDTO;
            }).collect(Collectors.toList());
    }

    public void setFieldFormatting(String fieldName, String formatting) {
        profileFields = profileFields.stream()
            .map(fieldDTO -> {
                if (fieldDTO.name.equals(fieldName)) {
                    fieldDTO.formatting = formatting;
                }
                return fieldDTO;
            }).collect(Collectors.toList());
    }
}

class ConstraintHolder {
    public Collection<ConstraintDTO> constraints;
    public ConstraintHolder(){}
}
