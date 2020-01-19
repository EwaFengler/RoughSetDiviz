package pl.poznan.put.roughset.decision;

import pl.poznan.put.roughset.alternative.Alternative;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class RoughSetClass {

    public static final String APPROXIMATION_UPPER = "upper";
    public static final String APPROXIMATION_LOWER = "lower";

    private String id;
    private String name;
    private Set<Alternative> alternatives = new HashSet<>();
    private Set<Alternative> lowerApprox = new HashSet<>();
    private Set<Alternative> upperApprox = new HashSet<>();

    public RoughSetClass(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addAlternative(Alternative alternative) {
        alternatives.add(alternative);
    }

    public void addAlternativesToUpperApprox(Set<Alternative> alternatives) {
        upperApprox.addAll(alternatives);
    }

    public void addAlternativesToLowerApprox(Set<Alternative> alternatives) {
        lowerApprox.addAll(alternatives);
    }

    public String getId() {
        return id;
    }

    public Set<Alternative> getAlternatives() {
        return alternatives;
    }

    public Set<Alternative> getLowerApprox() {
        return lowerApprox;
    }

    public Set<Alternative> getUpperApprox() {
        return upperApprox;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoughSetClass that = (RoughSetClass) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
