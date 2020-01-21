package pl.poznan.put.roughset.alternative;

import pl.poznan.put.roughset.alternative.ValueOnCriterion.ValueOnCriterion;
import pl.poznan.put.roughset.reduct.CriteriaIndicesMask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

public class Alternative {

    public static final String CONE_POSITIVE = "positiveCone";
    public static final String CONE_NEGATIVE = "negativeCone";

    private String id;
    private ArrayList<ValueOnCriterion> valuesOnCriteria = new ArrayList<>();
    private Set<Alternative> positiveDominance = new HashSet<>();
    private Set<Alternative> negativeDominance = new HashSet<>();

    public Alternative(String id) {
        this.id = id;
        this.positiveDominance.add(this);
        this.negativeDominance.add(this);
    }

    public String getId() {
        return id;
    }

    public void addToValuesOnCriteria(ValueOnCriterion valueOnCriterion) {
        this.valuesOnCriteria.add(valueOnCriterion);
    }

    public void addToPositiveDominance(Alternative alternative) {
        positiveDominance.add(alternative);
    }

    public void addToNegativeDominance(Alternative alternative) {
        negativeDominance.add(alternative);
    }

    public Relation compare(Alternative alternative) {
        return IntStream.range(0, valuesOnCriteria.size())
                .mapToObj(i -> compareOnCriterion(alternative, i))
                .reduce(Relation.SAME, Relation::joinFlags);
    }

    public Relation compareOnCriteria(Alternative alternative, CriteriaIndicesMask indicesMask) {
        return indicesMask.getIndicesStream()
                .mapToObj(i -> compareOnCriterion(alternative, i))
                .reduce(Relation.SAME, Relation::joinFlags);
    }

    private Relation compareOnCriterion(Alternative alternative, int i) {
        return this.valuesOnCriteria.get(i).compare(alternative.valuesOnCriteria.get(i));
    }

    public Set<Alternative> getPositiveDominance() {
        return positiveDominance;
    }

    public Set<Alternative> getNegativeDominance() {
        return negativeDominance;
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

