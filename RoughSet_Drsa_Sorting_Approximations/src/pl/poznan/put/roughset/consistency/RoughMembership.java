package pl.poznan.put.roughset.consistency;

import pl.poznan.put.roughset.alternative.Alternative;
import pl.poznan.put.roughset.decision.ClassUnion;

import java.util.Set;

public class RoughMembership extends ConsistencyMeasure {

    public RoughMembership(double threshold) {
        super(threshold);
    }

    @Override
    public boolean checkIfConsistent(Set<Alternative> dominanceCone, ClassUnion current, ClassUnion opposite) {
        double coneSize = dominanceCone.size();
        long consistent = dominanceCone.stream().filter(v -> current.getAlternatives().contains(v)).count();
        return (consistent / coneSize) >= threshold;
    }
}
