package pl.poznan.put.roughset.alternative.valueOnCriterion;

import pl.poznan.put.roughset.alternative.Relation;

public class PairValueOnCriterion extends ValueOnCriterion {

    private String id;
    private double value;

    public PairValueOnCriterion(String id, double value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public Relation compare(ValueOnCriterion valueOnCriterion) {
        PairValueOnCriterion otherData = (PairValueOnCriterion) valueOnCriterion;

        if (this.value > otherData.value) {
            return Relation.BETTER;
        }

        if (this.value < otherData.value) {
            return Relation.WORSE;
        }

        return Relation.SAME;
    }

    @Override
    public PairValueOnCriterion subtract(ValueOnCriterion valueOnCriterion) {
        throw new UnsupportedOperationException();
    }
}
