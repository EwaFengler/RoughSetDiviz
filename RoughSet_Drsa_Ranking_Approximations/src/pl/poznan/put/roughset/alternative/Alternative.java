package pl.poznan.put.roughset.alternative;

import pl.poznan.put.roughset.alternative.valueOnCriterion.PairValueOnCriterion;
import pl.poznan.put.roughset.alternative.valueOnCriterion.ValueOnCriterion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Alternative {

    private String id;
    private List<ValueOnCriterion> valuesOnCriteria = new ArrayList<>();

    public Alternative(String id) {
        this.id = id;
    }

    public void addToValuesOnCriteria(ValueOnCriterion valueOnCriterion) {
        this.valuesOnCriteria.add(valueOnCriterion);
    }

    public List<PairValueOnCriterion> subtract(Alternative alternative) {
        return IntStream.range(0, valuesOnCriteria.size())
                .mapToObj(i -> subtractOnCriterion(alternative, i))
                .collect(Collectors.toList());
    }

    private PairValueOnCriterion subtractOnCriterion(Alternative alternative, int i) {
        return this.valuesOnCriteria.get(i).subtract(alternative.valuesOnCriteria.get(i));
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alternative alternative = (Alternative) o;
        return id.equals(alternative.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

