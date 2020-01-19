package pl.poznan.put.roughset.alternative;

import pl.poznan.put.roughset.decision.RoughSetClass;
import pl.poznan.put.roughset.reduct.AttributeIndicesMask;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class IndiscernibilityClass {

    public static final String GRANULE_TYPE = "indiscernibility";

    private List<ValueOnAttribute> valuesOnAttributes;
    private Set<Alternative> alternatives;
    private Set<RoughSetClass> roughSetClasses;

    public IndiscernibilityClass(List<ValueOnAttribute> valuesOnAttributes) {
        this.valuesOnAttributes = valuesOnAttributes;
        alternatives = new HashSet<>();
        roughSetClasses = new HashSet<>();
    }

    public void addAlternative(Alternative alternative) {
        alternatives.add(alternative);
    }

    public void addRoughSetClass(RoughSetClass roughSetClass) {
        roughSetClasses.add(roughSetClass);
    }

    public List<ValueOnAttribute> getValuesOnAttributes() {
        return valuesOnAttributes;
    }

    public Set<Alternative> getAlternatives() {
        return alternatives;
    }

    public Set<RoughSetClass> getRoughSetClasses() {
        return roughSetClasses;
    }

    public boolean coversOneClass() {
        return roughSetClasses.size() == 1;
    }

    public boolean equalsOnAttributes(IndiscernibilityClass otherClass, AttributeIndicesMask indicesMask) {
        return indicesMask.getIndicesStream()
                .allMatch(i -> this.getValuesOnAttributes().get(i).equals(otherClass.getValuesOnAttributes().get(i)));
    }

    public boolean matchesAlternative(Alternative alternative) {
        return this.valuesOnAttributes.equals(alternative.getValuesOnAttributes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndiscernibilityClass that = (IndiscernibilityClass) o;
        return Objects.equals(valuesOnAttributes, that.valuesOnAttributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valuesOnAttributes);
    }
}