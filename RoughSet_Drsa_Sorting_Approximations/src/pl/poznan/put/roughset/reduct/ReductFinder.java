package pl.poznan.put.roughset.reduct;

import pl.poznan.put.roughset.alternative.Alternative;
import pl.poznan.put.roughset.consistency.ConsistencyMeasure;
import pl.poznan.put.roughset.criterion.Criterion;
import pl.poznan.put.roughset.decision.ClassUnion;
import pl.poznan.put.roughset.decision.RoughSetClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ReductFinder {

    private List<Alternative> alternatives;
    private List<Boolean> reducts;
    private List<RoughSetClass> roughSetClasses;
    private List<Criterion> criteria;
    private ConsistencyMeasure consistencyMeasure;
    private int noOfCriteria;
    private int noOfSets;

    public ReductFinder(List<Alternative> alternatives, List<RoughSetClass> roughSetClasses, List<Criterion> criteria, ConsistencyMeasure consistencyMeasure) {
        this.alternatives = alternatives;
        this.roughSetClasses = roughSetClasses;
        this.criteria = criteria;
        this.noOfCriteria = criteria.size();
        this.consistencyMeasure = consistencyMeasure;
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
        ConeByCriteriaFinder coneFinder = new ConeByCriteriaFinder(alternatives, indicesMask);

        for (int i = 0; i < roughSetClasses.size() - 1; i++){
            ClassUnion currentUnion = roughSetClasses.get(i).getDownwardUnion();
            ClassUnion oppositeUnion = roughSetClasses.get(i+1).getUpwardUnion();

            for (Alternative alternative : currentUnion.getLowerApprox()) {
                Set<Alternative> newCone = coneFinder.getNegativeDominance(alternative);
                Set<Alternative> oldCone = alternative.getNegativeDominance();
                if (hasQualityChanged(currentUnion, oppositeUnion, newCone, oldCone)) return false;
            }
        }

        for (int i = 1; i < roughSetClasses.size(); i++) {
            ClassUnion currentUnion = roughSetClasses.get(i).getUpwardUnion();
            ClassUnion oppositeUnion = roughSetClasses.get(i - 1).getDownwardUnion();

            for (Alternative alternative : currentUnion.getLowerApprox()) {
                Set<Alternative> newCone = coneFinder.getPositiveDominance(alternative);
                Set<Alternative> oldCone = alternative.getPositiveDominance();
                if (hasQualityChanged(currentUnion, oppositeUnion, newCone, oldCone)) return false;
            }
        }
        return true;
    }

    private boolean hasQualityChanged(ClassUnion currentUnion, ClassUnion oppositeUnion, Set<Alternative> newCone, Set<Alternative> oldCone) {
        if (oldCone.size() != newCone.size()) {
            return !consistencyMeasure.checkIfConsistent(newCone, currentUnion, oppositeUnion);
        }
        return false;
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