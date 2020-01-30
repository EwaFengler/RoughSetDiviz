package pl.poznan.put.roughset.reduct;

import pl.poznan.put.roughset.alternative.AlternativePair;
import pl.poznan.put.roughset.alternative.Relation;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ConeByCriteriaFinder {

    List<AlternativePair> alternativePairs;
    private Map<AlternativePair, Set<AlternativePair>> positiveCones;
    private Map<AlternativePair, Set<AlternativePair>> negativeCones;
    private CriteriaIndicesMask indicesMask;

    public ConeByCriteriaFinder(List<AlternativePair> alternativePairs, CriteriaIndicesMask indicesMask) {
        this.alternativePairs = alternativePairs;
        this.indicesMask = indicesMask;
        positiveCones = alternativePairs.stream().collect(Collectors.toMap(v -> v, v -> new HashSet<>()));
        negativeCones = alternativePairs.stream().collect(Collectors.toMap(v -> v, v -> new HashSet<>()));
        populateDominanceCones();
    }

    public Set<AlternativePair> getPositiveDominance(AlternativePair alternativePair) {
        return positiveCones.get(alternativePair);
    }

    public Set<AlternativePair> getNegativeDominance(AlternativePair alternativePair) {
        return negativeCones.get(alternativePair);
    }

    private void populateDominanceCones() {
        for (int i = 0; i < alternativePairs.size(); i++) {
            AlternativePair alternativePair = alternativePairs.get(i);
            positiveCones.get(alternativePair).add(alternativePair);
            negativeCones.get(alternativePair).add(alternativePair);

            for (int j = i + 1; j < alternativePairs.size(); j++) {
                AlternativePair otherAlternativePair = alternativePairs.get(j);

                Relation res = alternativePair.compareOnCriteria(otherAlternativePair, indicesMask);

                if (res == Relation.BETTER) {
                    positiveCones.get(otherAlternativePair).add(alternativePair);
                    negativeCones.get(alternativePair).add(otherAlternativePair);
                } else if (res == Relation.WORSE) {
                    positiveCones.get(alternativePair).add(otherAlternativePair);
                    negativeCones.get(otherAlternativePair).add(alternativePair);
                } else if (res == Relation.SAME) {
                    positiveCones.get(otherAlternativePair).add(alternativePair);
                    negativeCones.get(alternativePair).add(otherAlternativePair);
                    positiveCones.get(alternativePair).add(otherAlternativePair);
                    negativeCones.get(otherAlternativePair).add(alternativePair);
                }
            }
        }
    }
}