package pl.poznan.put.roughset.alternative;

import pl.poznan.put.roughset.alternative.valueOnCriterion.PairValueOnCriterion;
import pl.poznan.put.roughset.decision.Outranking;
import pl.poznan.put.roughset.reduct.CriteriaIndicesMask;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class AlternativePair {

    public static final String CONE_POSITIVE = "positiveCone";
    public static final String CONE_NEGATIVE = "negativeCone";

    private Alternative alternativeX;
    private Alternative alternativeY;
    private Outranking outranking;
    private Set<AlternativePair> positiveDominance = new HashSet<>();
    private Set<AlternativePair> negativeDominance = new HashSet<>();

    private List<PairValueOnCriterion> alternativeDistance;

    public AlternativePair(Alternative alternativeX, Alternative alternativeY, Outranking outranking) {
        this.alternativeX = alternativeX;
        this.alternativeY = alternativeY;
        this.outranking = outranking;
        this.alternativeDistance = alternativeX.subtract(alternativeY);

        this.positiveDominance.add(this);
        this.negativeDominance.add(this);
    }

    public Relation compare(AlternativePair alternativePair) {
        return IntStream.range(0, alternativeDistance.size())
                .mapToObj(i -> compareOnCriterion(alternativePair, i))
                .reduce(Relation.SAME, Relation::joinFlags);
    }

    public Relation compareOnCriteria(AlternativePair alternativePair, CriteriaIndicesMask indicesMask) {
        return indicesMask.getIndicesStream()
                .mapToObj(i -> compareOnCriterion(alternativePair, i))
                .reduce(Relation.SAME, Relation::joinFlags);
    }

    private Relation compareOnCriterion(AlternativePair alternativePair, int i) {
        return this.alternativeDistance.get(i).compare(alternativePair.alternativeDistance.get(i));
    }

    public void addToPositiveDominance(AlternativePair alternativePair) {
        positiveDominance.add(alternativePair);
    }

    public void addToNegativeDominance(AlternativePair alternativePair) {
        negativeDominance.add(alternativePair);
    }

    public Alternative getAlternativeX() {
        return alternativeX;
    }

    public Alternative getAlternativeY() {
        return alternativeY;
    }

    public Outranking getOutranking() {
        return outranking;
    }

    public Set<AlternativePair> getPositiveDominance() {
        return positiveDominance;
    }

    public Set<AlternativePair> getNegativeDominance() {
        return negativeDominance;
    }
}
