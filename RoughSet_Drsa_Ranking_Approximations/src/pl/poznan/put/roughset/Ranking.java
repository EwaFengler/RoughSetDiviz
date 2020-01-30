package pl.poznan.put.roughset;

import pl.poznan.put.roughset.alternative.AlternativePair;
import pl.poznan.put.roughset.alternative.Relation;
import pl.poznan.put.roughset.consistency.ConsistencyMeasure;
import pl.poznan.put.roughset.criterion.Criterion;
import pl.poznan.put.roughset.decision.Outranking;
import pl.poznan.put.roughset.reduct.Reduct;
import pl.poznan.put.roughset.reduct.ReductFinder;

import java.util.List;

public class Ranking {

    private List<Criterion> criteria;
    private List<AlternativePair> alternativePairs;
    private Outranking s;
    private Outranking sc;
    private ConsistencyMeasure consistencyMeasure;
    private double qualityOfApprox;

    public Ranking(List<Criterion> criteria, List<AlternativePair> alternativePairs, Outranking s, Outranking sc, ConsistencyMeasure consistencyMeasure) {
        this.criteria = criteria;
        this.alternativePairs = alternativePairs;
        this.s = s;
        this.sc = sc;
        this.consistencyMeasure = consistencyMeasure;
    }

    public RankingResult generate() {
        populateDominanceCones();
        populateApproximations();
        List<Reduct> reducts = getReducts();
        qualityOfApprox = 1; //TODO

        return new RankingResult(alternativePairs, s, sc, reducts, qualityOfApprox);
    }

    private void populateDominanceCones() {
        for (int i = 0; i < alternativePairs.size(); i++) {
            AlternativePair alternativePair = alternativePairs.get(i);

            for (int j = i + 1; j < alternativePairs.size(); j++) {
                AlternativePair otherAlternativePair = alternativePairs.get(j);

                Relation res = alternativePair.compare(otherAlternativePair);

                if (res == Relation.BETTER) {
                    markDominance(alternativePair, otherAlternativePair);
                } else if (res == Relation.WORSE) {
                    markDominance(otherAlternativePair, alternativePair);
                } else if (res == Relation.SAME) {
                    markDominance(otherAlternativePair, alternativePair);
                    markDominance(alternativePair, otherAlternativePair);
                }
            }
        }
    }

    private void markDominance(AlternativePair dominating, AlternativePair dominated) {
        dominating.addToNegativeDominance(dominated);
        dominated.addToPositiveDominance(dominating);
    }

    private void populateApproximations() {
        for (AlternativePair alternativePair : alternativePairs) {
            if (alternativePair.getOutranking() == s) {
                s.addAllToUpperApprox(alternativePair.getPositiveDominance());
            }
            if (alternativePair.getOutranking() == sc) {
                sc.addAllToUpperApprox(alternativePair.getNegativeDominance());
            }

            if (consistencyMeasure.checkIfConsistent(alternativePair.getPositiveDominance(), s, sc)) {
                s.addAllToLowerApprox(alternativePair.getPositiveDominance());
            }
            if (consistencyMeasure.checkIfConsistent(alternativePair.getNegativeDominance(), sc, s)) {
                sc.addAllToLowerApprox(alternativePair.getNegativeDominance());
            }
        }
    }

    private List<Reduct> getReducts() {
        ReductFinder reductFinder = new ReductFinder(alternativePairs, s, sc, consistencyMeasure, criteria);
        return reductFinder.findReducts();
    }

}