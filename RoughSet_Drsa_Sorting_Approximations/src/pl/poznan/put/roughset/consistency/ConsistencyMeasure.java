package pl.poznan.put.roughset.consistency;

import pl.poznan.put.roughset.alternative.Alternative;
import pl.poznan.put.roughset.decision.ClassUnion;

import java.util.Set;

public abstract class ConsistencyMeasure {

    protected double threshold;

    public ConsistencyMeasure(double threshold) {
        this.threshold = threshold;
    }

    public abstract boolean checkIfConsistent(Set<Alternative> dominanceCone, ClassUnion current, ClassUnion opposite);
}
