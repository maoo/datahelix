package com.scottlogic.deg.common.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scottlogic.deg.common.profile.Types;

public enum DtoTypes {
    @JsonProperty("numeric")
    DECIMAL(Types.NUMERIC),
    @JsonProperty("integer")
    INTEGER(Types.NUMERIC),
    @JsonProperty("string")
    STRING(Types.STRING),
    @JsonProperty("ISIN")
    ISIN(Types.STRING),
    @JsonProperty("SEDOL")
    SEDOL(Types.STRING),
    @JsonProperty("CUSIP")
    CUSIP(Types.STRING),
    @JsonProperty("RIC")
    RIC(Types.STRING),
    @JsonProperty("firstname")
    FIRSTNAME(Types.STRING),
    @JsonProperty("lastname")
    LASTNAME(Types.STRING),
    @JsonProperty("fullname")
    FULLNAME(Types.STRING),
    @JsonProperty("datetime")
    DATETIME(Types.DATETIME);

    private final Types typeMapping;

    DtoTypes(Types typeMapping) {
        this.typeMapping = typeMapping;
    }

    public Types getTypeMapping() {
        return typeMapping;
    }
}
