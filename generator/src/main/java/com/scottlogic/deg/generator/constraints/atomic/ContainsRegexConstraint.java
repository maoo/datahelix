package com.scottlogic.deg.generator.constraints.atomic;

import com.scottlogic.deg.generator.Field;

import java.util.Objects;
import java.util.regex.Pattern;

public class ContainsRegexConstraint implements AtomicConstraint {
    public final Field field;
    public final Pattern regex;

    public ContainsRegexConstraint(Field field, Pattern regex) {
        this.field = field;
        this.regex = regex;
    }

    @Override
    public String toDotLabel() {
        return String.format("%s contains /%s/", field.name, regex);
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContainsRegexConstraint constraint = (ContainsRegexConstraint) o;
        return Objects.equals(field, constraint.field) && Objects.equals(regex.toString(), constraint.regex.toString());
    }

    @Override
    public int hashCode(){
        return Objects.hash(field, regex.toString());
    }

    @Override
    public String toString() {
        return String.format("`%s` contains /%s/", field.name, regex);
    }
}