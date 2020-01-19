package pl.poznan.put.roughset;

import pl.poznan.put.roughset.alternative.Alternative;
import pl.poznan.put.roughset.alternative.IndiscernibilityClass;
import pl.poznan.put.roughset.attribute.Attribute;
import pl.poznan.put.roughset.decision.RoughSetClass;
import pl.poznan.put.roughset.reduct.Reduct;
import pl.poznan.put.roughset.reduct.ReductFinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Classification {

    private Set<Alternative> alternatives;
    private List<Attribute> attributes;

    private Set<RoughSetClass> roughSetClasses;
    private List<IndiscernibilityClass> indiscernibilityClasses = new ArrayList<>();

    //TODO ładniej
    private double qualityOfApprox = 0;

    public Classification(List<Attribute> attributes, Set<Alternative> alternatives, Set<RoughSetClass> roughSetClasses) {
        this.alternatives = alternatives;
        this.attributes = attributes;
        this.roughSetClasses = roughSetClasses;
    }

    public ClassificationResult generate() {
        populateIndiscernibilityClasses();
        produceApproximations();
        List<Reduct> reducts = findReducts();

        return new ClassificationResult(indiscernibilityClasses, roughSetClasses, reducts, qualityOfApprox);
    }

    private void populateIndiscernibilityClasses() {
        for (RoughSetClass roughSetClass : roughSetClasses) {
            for (Alternative alternative : roughSetClass.getAlternatives()) {
                IndiscernibilityClass indiscernibilityClass = getIndiscernibilityClass(alternative);
                indiscernibilityClass.addAlternative(alternative);
                indiscernibilityClass.addRoughSetClass(roughSetClass);
            }
        }
    }

    private IndiscernibilityClass getIndiscernibilityClass(Alternative alternative) {
        return indiscernibilityClasses.stream()
                .filter(ic -> ic.matchesAlternative(alternative))
                .findFirst()
                .orElseGet(() -> createIndiscernibilityClass(alternative));
    }

    private IndiscernibilityClass createIndiscernibilityClass(Alternative alternative) {
        IndiscernibilityClass indClass = new IndiscernibilityClass(alternative.getValuesOnAttributes());
        indiscernibilityClasses.add(indClass);
        return indClass;
    }

    private void produceApproximations() {
        indiscernibilityClasses.forEach(i -> i.getRoughSetClasses().forEach(r -> fillClassApprox(i, r)));
        qualityOfApprox /= alternatives.size();
    }

    private void fillClassApprox(IndiscernibilityClass indiscernibilityClass, RoughSetClass roughSetClass) {
        Set<Alternative> indiscernibleAlternatives = indiscernibilityClass.getAlternatives();
        roughSetClass.addAlternativesToUpperApprox(indiscernibleAlternatives);

        if (indiscernibilityClass.coversOneClass()) {
            roughSetClass.addAlternativesToLowerApprox(indiscernibleAlternatives);
            qualityOfApprox += indiscernibleAlternatives.size();
        }
    }

    private List<Reduct> findReducts() {
        ReductFinder reductFinder = new ReductFinder(indiscernibilityClasses, attributes);
        return reductFinder.findReducts();
    }

}
//TODO variantData może być nie w kolejności!!!!!!! -> zamienić na set?