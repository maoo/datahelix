package com.scottlogic.deg.generator.builders;

import com.scottlogic.deg.generator.constraints.Constraint;

public class SingleConstraintBuilder extends ConstraintChainBuilder<Constraint> {
    public SingleConstraintBuilder() { }

    public SingleConstraintBuilder(ConstraintChainBuilder<Constraint> builder) {
        super(builder);
    }

    @Override
    Constraint buildInner() {
        if (constraints.size() == 0) {
            throw new RuntimeException("Unable to build single constraint, no constraints specified.");
        }

        if (constraints.size() > 1) {
            throw new RuntimeException("Unable to build single constraint, more than 1 constraint specified.");
        }

        return constraints.get(0);
    }

    @Override
    public ConstraintChainBuilder<Constraint> copy() {
        return new SingleConstraintBuilder(this);
    }
}