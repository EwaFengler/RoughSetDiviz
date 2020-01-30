package pl.poznan.put.roughset.consistency;

import pl.poznan.put.roughset.alternative.AlternativePair;
import pl.poznan.put.roughset.decision.Outranking;

import java.util.Set;

public class CostMeasureEpsilonPrime extends ConsistencyMeasure {

    public CostMeasureEpsilonPrime(double threshold) {
        super(threshold);
    }

    @Override
    public boolean checkIfConsistent(Set<AlternativePair> dominanceCone, Outranking current, Outranking opposite) {
        double currentSize = current.getAlternativePairs().size();
        long noOfInconsistent = dominanceCone.stream().filter(vp -> vp.getOutranking().equals(opposite)).count();
        return noOfInconsistent / currentSize <= threshold;
    }
}