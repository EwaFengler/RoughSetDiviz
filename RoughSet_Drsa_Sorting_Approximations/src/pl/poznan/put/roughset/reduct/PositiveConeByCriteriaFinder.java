package pl.poznan.put.roughset.reduct;

import pl.poznan.put.roughset.alternative.Alternative;
import pl.poznan.put.roughset.alternative.Relation;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PositiveConeByCriteriaFinder {

    List<Alternative> alternatives;
    private Map<Alternative, Set<Alternative>> positiveCones;
    private CriteriaIndicesMask indicesMask;

    public PositiveConeByCriteriaFinder(List<Alternative> alternatives, CriteriaIndicesMask indicesMask) {
        this.alternatives = alternatives;
        this.indicesMask = indicesMask;
        positiveCones = alternatives.stream().collect(Collectors.toMap(v -> v, v -> new HashSet<>()));
        populateDominanceCones();
    }

    public Set<Alternative> getPositiveDominance(Alternative alternative) {
        return positiveCones.get(alternative);
    }

    private void populateDominanceCones() {
        for (int i = 0; i < alternatives.size(); i++) {
            Alternative alternative = alternatives.get(i);

            for (int j = i + 1; j < alternatives.size(); j++) {
                Alternative otherAlternative = alternatives.get(j);

                Relation res = alternative.compareOnCriteria(otherAlternative, indicesMask);

                if (res == Relation.BETTER) {
                    positiveCones.get(otherAlternative).add(alternative);
                } else if (res == Relation.WORSE) {
                    positiveCones.get(alternative).add(otherAlternative);
                } else if (res == Relation.SAME) {
                    positiveCones.get(otherAlternative).add(alternative);
                    positiveCones.get(alternative).add(otherAlternative);
                }
            }
        }
    }
}
