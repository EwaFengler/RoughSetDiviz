package pl.poznan.put.roughset.alternative.ValueOnCriterion;

import pl.poznan.put.roughset.alternative.Relation;
import pl.poznan.put.roughset.criterion.CategoricalCriterion;
import pl.poznan.put.roughset.criterion.CriterionType;

public class ValueOnCategoricalCriterion extends ValueOnCriterion {

    private CategoricalCriterion criterion;
    private String value;

    public ValueOnCategoricalCriterion(CategoricalCriterion criterion, String value) {
        this.criterion = criterion;
        this.value = value;
    }

    @Override
    public Relation compare(ValueOnCriterion valueOnCriterion) {
        ValueOnCategoricalCriterion otherData = (ValueOnCategoricalCriterion) valueOnCriterion;

        if (this.criterion.getValueRank(this.value) > this.criterion.getValueRank(otherData.value)) {
            return isGain() ? Relation.BETTER : Relation.WORSE;
        }
        if (this.criterion.getValueRank(this.value) < this.criterion.getValueRank(otherData.value)) {
            return isGain() ? Relation.WORSE : Relation.BETTER;
        }
        return Relation.SAME;
    }

    private boolean isGain() {
        return this.criterion.getType() == CriterionType.GAIN;
    }
}
