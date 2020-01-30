package pl.poznan.put.roughset.alternative.valueOnCriterion;

import pl.poznan.put.roughset.alternative.Relation;
import pl.poznan.put.roughset.criterion.CriterionType;
import pl.poznan.put.roughset.criterion.NumericalCriterion;

public class ValueOnNumericalCriterion extends ValueOnCriterion {

    private NumericalCriterion criterion;
    private double value;

    public ValueOnNumericalCriterion(NumericalCriterion criterion, double value) {
        this.criterion = criterion;
        this.value = value;
    }

    @Override
    public Relation compare(ValueOnCriterion valueOnCriterion) {
        ValueOnNumericalCriterion otherData = (ValueOnNumericalCriterion) valueOnCriterion;

        if (this.value > otherData.value) {
            return isGain() ? Relation.BETTER : Relation.WORSE;
        }

        if (this.value < otherData.value) {
            return isGain() ? Relation.WORSE : Relation.BETTER;
        }

        return Relation.SAME;
    }

    @Override
    public PairValueOnCriterion subtract(ValueOnCriterion valueOnCriterion) {
        ValueOnNumericalCriterion otherData = (ValueOnNumericalCriterion) valueOnCriterion;
        double value = isGain() ? this.value - otherData.value : otherData.value - this.value;
        return new PairValueOnCriterion(this.criterion.getId(), value);
    }

    private boolean isGain() {
        return this.criterion.getCriterionType() == CriterionType.GAIN;
    }

}
