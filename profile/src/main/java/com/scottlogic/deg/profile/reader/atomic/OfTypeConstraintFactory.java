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


package com.scottlogic.deg.profile.reader.atomic;

import com.scottlogic.deg.common.profile.Field;
import com.scottlogic.deg.common.profile.SpecificFieldType;
import com.scottlogic.deg.common.profile.constraintdetail.DateTimeGranularity;
import com.scottlogic.deg.common.profile.constraintdetail.NumericGranularityFactory;
import com.scottlogic.deg.generator.profile.constraints.Constraint;
import com.scottlogic.deg.generator.profile.constraints.atomic.*;
import com.scottlogic.deg.profile.reader.file.names.NameRetriever;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class OfTypeConstraintFactory {
    public static Optional<Constraint> create(Field field, SpecificFieldType specificFieldType){
        String type = specificFieldType.getType();
        switch (specificFieldType) {
            case INTEGER:
                return Optional.of(new IsGranularToNumericConstraint(field, NumericGranularityFactory.create(BigDecimal.ONE)));
            case ISIN:
            case SEDOL:
            case CUSIP:
            case RIC:
                return Optional.of(new MatchesStandardConstraint(field, StandardConstraintTypes.valueOf(type.toUpperCase())));
            case FIRST_NAME:
            case LAST_NAME:
            case FULL_NAME:
                return Optional.of(new IsInSetConstraint(field, NameRetriever.loadNamesFromFile(NameConstraintTypes.lookupProfileText(type.toLowerCase()))));
            case DATE:
                return Optional.of(new IsGranularToDateConstraint(field, new DateTimeGranularity(ChronoUnit.DAYS)));
            default:
                return Optional.empty();
        }
    }
}
