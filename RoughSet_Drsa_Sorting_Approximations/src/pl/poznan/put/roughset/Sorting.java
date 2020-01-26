package pl.poznan.put.roughset;

import pl.poznan.put.roughset.alternative.Alternative;
import pl.poznan.put.roughset.alternative.Relation;
import pl.poznan.put.roughset.consistency.ConsistencyMeasure;
import pl.poznan.put.roughset.criterion.Criterion;
import pl.poznan.put.roughset.decision.ClassUnion;
import pl.poznan.put.roughset.decision.RoughSetClass;
import pl.poznan.put.roughset.reduct.Reduct;
import pl.poznan.put.roughset.reduct.ReductFinder;

import java.util.List;
import java.util.stream.Collectors;

public class Sorting {

    private List<Criterion> criteria;
    private List<Alternative> alternatives;
    private List<RoughSetClass> roughSetClasses;
    private ConsistencyMeasure consistencyMeasure;
    private int alternativesInLower = 0;
    private int alternativesInUpper = 0;

    public Sorting(List<Criterion> criteria, List<Alternative> alternatives, List<RoughSetClass> roughSetClasses, ConsistencyMeasure consistencyMeasure) {
        this.criteria = criteria;
        this.alternatives = alternatives;
        this.roughSetClasses = roughSetClasses;
        this.consistencyMeasure = consistencyMeasure;
    }

    public SortingResult generate() {
        populateDominanceCones();
        sortRoughSetClasses();
        populateUnions();
        produceApproximations();
        List<Reduct> reducts = findReducts();
        double qualityOfApprox = 1.0 - (alternativesInUpper - alternativesInLower) / (alternatives.size() * 1.0);

        return new SortingResult(alternatives, roughSetClasses, reducts, qualityOfApprox);
    }

    private void populateDominanceCones() {
        for (int i = 0; i < alternatives.size(); i++) {
            Alternative alternative = alternatives.get(i);

            for (int j = i + 1; j < alternatives.size(); j++) {
                Alternative otherAlternative = alternatives.get(j);

                Relation relation = alternative.compare(otherAlternative);

                if (relation == Relation.BETTER) {
                    markDominance(alternative, otherAlternative);
                } else if (relation == Relation.WORSE) {
                    markDominance(otherAlternative, alternative);
                } else if (relation == Relation.SAME) {
                    markDominance(alternative, otherAlternative);
                    markDominance(otherAlternative, alternative);
                }
            }
        }
    }

    private void markDominance(Alternative dominating, Alternative dominated) {
        dominating.addToNegativeDominance(dominated);
        dominated.addToPositiveDominance(dominating);
    }

    private void sortRoughSetClasses() {
        roughSetClasses = roughSetClasses.stream().sorted().collect(Collectors.toList());
    }

    private void populateUnions() {
        populateUnionForWorst();
        for (int k = 1; k < roughSetClasses.size() - 1; k++) {
            RoughSetClass roughSetClass = roughSetClasses.get(k);

            List<Alternative> notBetter = getNotBetterAlternatives(k);
            roughSetClass.setDownwardUnion(new ClassUnion(roughSetClass, ClassUnion.ClassUnionType.DOWNWARD, notBetter));

            List<Alternative> notWorse = getNotWorseAlternatives(k);
            roughSetClass.setUpwardUnion(new ClassUnion(roughSetClass, ClassUnion.ClassUnionType.UPWARD, notWorse));
        }
        populateUnionForBest();
    }

    private void populateUnionForWorst() {
        RoughSetClass roughSetClass = roughSetClasses.get(0);
        List<Alternative> notBetter = getNotBetterAlternatives(0);
        roughSetClass.setDownwardUnion(new ClassUnion(roughSetClass, ClassUnion.ClassUnionType.DOWNWARD, notBetter));
    }

    private void populateUnionForBest() {
        RoughSetClass roughSetClass = roughSetClasses.get(roughSetClasses.size() - 1);
        List<Alternative> notWorse = getNotWorseAlternatives(roughSetClasses.size() - 1);
        roughSetClass.setUpwardUnion(new ClassUnion(roughSetClass, ClassUnion.ClassUnionType.UPWARD, notWorse));
    }

    private void produceApproximations() {
        produceApproximationsForWorst();

        for (int i = 1; i < roughSetClasses.size() - 1; i++) {
            RoughSetClass current = roughSetClasses.get(i);
            RoughSetClass next = roughSetClasses.get(i + 1);
            RoughSetClass prev = roughSetClasses.get(i - 1);

            produceUpwardUnionApproximations(prev, current);
            produceDownwardUnionApproximations(next, current);
        }
        produceApproximationsForBest();
    }

    private void produceApproximationsForWorst() {
        RoughSetClass current = roughSetClasses.get(0);

        if (roughSetClasses.size() > 1) {
            RoughSetClass next = roughSetClasses.get(1);
            produceDownwardUnionApproximations(next, current);
        }
    }

    private void produceApproximationsForBest() {
        int noOfClasses = roughSetClasses.size();
        RoughSetClass current = roughSetClasses.get(noOfClasses - 1);

        if (noOfClasses > 1) {
            RoughSetClass prev = roughSetClasses.get(noOfClasses - 2);
            produceUpwardUnionApproximations(prev, current);
        }
    }

    private void produceDownwardUnionApproximations(RoughSetClass next, RoughSetClass current) {
        ClassUnion downwardUnion = current.getDownwardUnion();

        for (Alternative alternative : downwardUnion.getAlternatives()) {
            downwardUnion.addAllToUpperApprox(alternative.getNegativeDominance());

            if (consistencyMeasure.checkIfConsistent(alternative.getNegativeDominance(), downwardUnion, next.getUpwardUnion())) {
                downwardUnion.addAllToLowerApprox(alternative.getNegativeDominance());
                alternativesInLower++;
            }
        }
        alternativesInUpper += downwardUnion.getAlternatives().size();
    }

    private void produceUpwardUnionApproximations(RoughSetClass prev, RoughSetClass current) {
        ClassUnion upwardUnion = current.getUpwardUnion();

        for (Alternative alternative : upwardUnion.getAlternatives()) {
            upwardUnion.addAllToUpperApprox(alternative.getPositiveDominance());

            if (consistencyMeasure.checkIfConsistent(alternative.getPositiveDominance(), upwardUnion, prev.getDownwardUnion())) {
                upwardUnion.addAllToLowerApprox(alternative.getPositiveDominance());
                alternativesInLower++;
            }
        }
        alternativesInUpper += upwardUnion.getAlternatives().size();
    }

    private List<Alternative> getNotWorseAlternatives(int k) {
        return roughSetClasses.stream()
                .skip(k)
                .flatMap(rs -> rs.getAlternatives().stream())
                .collect(Collectors.toList());
    }

    private List<Alternative> getNotBetterAlternatives(int k) {
        return roughSetClasses.stream()
                .limit(k + 1)
                .flatMap(rs -> rs.getAlternatives().stream())
                .collect(Collectors.toList());
    }

    private List<Reduct> findReducts() {
        ReductFinder reductFinder = new ReductFinder(alternatives, roughSetClasses, criteria, consistencyMeasure);
        return reductFinder.findReducts();
    }
}