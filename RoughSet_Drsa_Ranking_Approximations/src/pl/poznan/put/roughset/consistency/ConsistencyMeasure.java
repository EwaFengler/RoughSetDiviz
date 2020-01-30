package pl.poznan.put.roughset.consistency;

import pl.poznan.put.roughset.alternative.AlternativePair;
import pl.poznan.put.roughset.decision.Outranking;

import java.util.Set;

public abstract class ConsistencyMeasure {

    protected double threshold;

    public ConsistencyMeasure(double threshold) {
        this.threshold = threshold;
    }

    public abstract boolean checkIfConsistent(Set<AlternativePair> dominanceCone, Outranking current, Outranking opposite);
}
