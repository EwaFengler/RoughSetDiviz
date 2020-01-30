package pl.poznan.put.roughset.criterion;

import java.util.Map;

public class CategoricalCriterion extends Criterion {

    private Map<String, Integer> valuesRanks;

    public CategoricalCriterion(String id, String name, CriterionType type, Map<String, Integer> valuesRanks) {
        super(id, name, type);
        this.valuesRanks = valuesRanks;
    }

    public Integer getValueRank(String value) {
        return valuesRanks.get(value);
    }
}
