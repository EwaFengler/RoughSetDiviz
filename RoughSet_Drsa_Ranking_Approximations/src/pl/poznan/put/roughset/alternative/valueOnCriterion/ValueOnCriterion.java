package pl.poznan.put.roughset.alternative.valueOnCriterion;

import pl.poznan.put.roughset.alternative.Relation;

public abstract class ValueOnCriterion {

    public abstract Relation compare(ValueOnCriterion valueOnCriterion);

    public abstract PairValueOnCriterion subtract(ValueOnCriterion valueOnCriterion);
}
