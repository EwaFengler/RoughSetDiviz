package pl.poznan.put.roughset.decision;

import pl.poznan.put.roughset.alternative.Alternative;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassUnion {

    private RoughSetClass bound;
    private ClassUnionType unionType;

    private Set<Alternative> alternatives = new HashSet<>();
    private Set<Alternative> upperApprox = new HashSet<>();
    private Set<Alternative> lowerApprox = new HashSet<>();

    public ClassUnion(RoughSetClass bound, ClassUnionType unionType, List<Alternative> alternatives) {
        this.bound = bound;
        this.unionType = unionType;
        this.alternatives.addAll(alternatives);
    }

    public void addAllToUpperApprox(Set<Alternative> alternatives) {
        upperApprox.addAll(alternatives);
    }

    public void addAllToLowerApprox(Set<Alternative> alternatives) {
        lowerApprox.addAll(alternatives);
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

    public RoughSetClass getBound() {
        return bound;
    }

    public ClassUnionType getUnionType() {
        return unionType;
    }

    public enum ClassUnionType {
        UPWARD, DOWNWARD;
    }
}
