package pl.poznan.put.roughset.consistency;

import pl.poznan.put.roughset.alternative.AlternativePair;
import pl.poznan.put.roughset.decision.Outranking;

import java.util.Set;

public class RoughMembership extends ConsistencyMeasure {

    public RoughMembership(double threshold) {
        super(threshold);
    }

    @Override
    public boolean checkIfConsistent(Set<AlternativePair> dominanceCone, Outranking current, Outranking opposite) {
        double coneSize = dominanceCone.size();
        long consistent = dominanceCone.stream().filter(vp -> vp.getOutranking().equals(current)).count();
        return consistent / coneSize >= threshold;
    }
}