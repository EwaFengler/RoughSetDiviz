package pl.poznan.put.roughset.consistency;

import pl.poznan.put.roughset.alternative.Alternative;
import pl.poznan.put.roughset.decision.ClassUnion;

import java.util.Set;

public class CostMeasureEpsilonPrime extends ConsistencyMeasure {

    public CostMeasureEpsilonPrime(double threshold) {
        super(threshold);
    }

    @Override
    public boolean checkIfConsistent(Set<Alternative> dominanceCone, ClassUnion current, ClassUnion opposite) {
        double currentSize = current.getAlternatives().size();
        long noOfInconsistent = dominanceCone.stream().filter(v -> opposite.getAlternatives().contains(v)).count();
        return noOfInconsistent / currentSize <= threshold;
    }
}
