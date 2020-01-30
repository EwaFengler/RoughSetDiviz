package pl.poznan.put.roughset.reduct;

import pl.poznan.put.roughset.alternative.AlternativePair;
import pl.poznan.put.roughset.consistency.ConsistencyMeasure;
import pl.poznan.put.roughset.criterion.Criterion;
import pl.poznan.put.roughset.decision.Outranking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ReductFinder {

    private List<AlternativePair> alternativePairs;
    private List<Boolean> reducts;
    private Outranking s;
    private Outranking sc;
    private ConsistencyMeasure consistencyMeasure;
    private List<Criterion> criteria;
    private int noOfCriteria;
    private int noOfSets;

    public ReductFinder(List<AlternativePair> alternativePairs, Outranking s, Outranking sc, ConsistencyMeasure consistencyMeasure, List<Criterion> criteria) {
        this.alternativePairs = alternativePairs;
        this.s = s;
        this.sc = sc;
        this.consistencyMeasure = consistencyMeasure;
        this.criteria = criteria;
        this.noOfCriteria = criteria.size();

        noOfSets = 1 << noOfCriteria;
        reducts = new ArrayList<>(Collections.nCopies(noOfSets, true));
        reducts.set(0, false);
    }

    public List<Reduct> findReducts() {
        for (int i = reducts.size() - 1; i > 0; i--) {
            if (canBeReduct(i)) {
                if (isReduct(convertToMask(i))) {
                    removeSupersetsFromReducts(i);
                } else {
                    removeSetFromReducts(i);
                    removeSubsetsFromReducts(i);
                }
            }
        }

        return IntStream.range(1, noOfSets)
                .filter(this::canBeReduct)
                .mapToObj(this::convertToMask)
                .map(mask -> new Reduct(mask, criteria))
                .collect(Collectors.toList());
    }

    private boolean isReduct(CriteriaIndicesMask indicesMask) {
        PositiveConeByCriteriaFinder coneFinder = new PositiveConeByCriteriaFinder(alternativePairs, indicesMask);

        for (AlternativePair alternativePair : s.getLowerApprox()) {
            Set<AlternativePair> oldCone = alternativePair.getPositiveDominance();
            Set<AlternativePair> newCone = coneFinder.getPositiveDominance(alternativePair);

            if (oldCone.size() != newCone.size()) {
                if (!consistencyMeasure.checkIfConsistent(newCone, s, sc)) {
                    return false;
                }
            }
        }

        return true;
    }

    private CriteriaIndicesMask convertToMask(int i) {
        return new CriteriaIndicesMask(i, noOfCriteria);
    }

    private void removeSupersetsFromReducts(int i) {
        for (int j = reducts.size() - 1; j > i; j--) {
            if (isSuperset(i, j)) {
                removeSetFromReducts(j);
            }
        }
    }

    private void removeSubsetsFromReducts(int i) {
        for (int j = i - 1; j > 0; j--) {
            if (isSubset(i, j)) {
                removeSetFromReducts(j);
            }
        }
    }

    private void removeSetFromReducts(int i) {
        reducts.set(i, Boolean.FALSE);
    }

    private boolean isSuperset(int i, int j) {
        return (i & j) == i;
    }

    private boolean isSubset(int i, int j) {
        return (i | j) == i;
    }

    private Boolean canBeReduct(int i) {
        return reducts.get(i);
    }
}
