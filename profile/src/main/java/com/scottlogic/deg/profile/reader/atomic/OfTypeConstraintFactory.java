package com.scottlogic.deg.profile.reader.atomic;

import com.scottlogic.deg.common.profile.Field;
import com.scottlogic.deg.common.profile.constraintdetail.ParsedGranularity;
import com.scottlogic.deg.common.util.DtoTypes;
import com.scottlogic.deg.generator.profile.constraints.Constraint;
import com.scottlogic.deg.generator.profile.constraints.atomic.*;
import com.scottlogic.deg.profile.reader.InvalidProfileException;
import com.scottlogic.deg.profile.reader.file.names.NameRetriever;

import java.math.BigDecimal;
import java.util.Optional;


public class OfTypeConstraintFactory {
    public static Optional<Constraint> create(Field field, DtoTypes value){
        switch (value) {
            case DECIMAL:
            case STRING:
            case DATETIME:
                return Optional.empty();

            case INTEGER:
                return Optional.of(new IsGranularToNumericConstraint(field,
                    new ParsedGranularity(BigDecimal.ONE))
                );

            case ISIN:
            case SEDOL:
            case CUSIP:
            case RIC:
                return Optional.of(new MatchesStandardConstraint(
                    field,
                    StandardConstraintTypes.valueOf(value.toString()))
                );

            case FIRSTNAME:
            case LASTNAME:
            case FULLNAME:
                return Optional.of(new IsInSetConstraint(
                    field,
                    NameRetriever.loadNamesFromFile(NameConstraintTypes.lookupProfileText(value.toString().toLowerCase())))
                );
        }

        throw new InvalidProfileException("Profile is invalid: no constraints known for \"is\": \"ofType\", \"value\": \"" + value + "\"");
    }
}
