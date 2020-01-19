package pl.poznan.put.roughset.reduct;

import pl.poznan.put.roughset.alternative.IndiscernibilityClass;
import pl.poznan.put.roughset.attribute.Attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ReductFinder {

    private List<IndiscernibilityClass> indiscernibilityClasses;
    private List<Attribute> attributes;
    private List<Boolean> reducts;
    private int noOfAttr;
    private int noOfSets;

    public ReductFinder(List<IndiscernibilityClass> indiscernibilityClasses, List<Attribute> attributes) {
        this.indiscernibilityClasses = indiscernibilityClasses;
        this.attributes = attributes;
        this.noOfAttr = attributes.size();
        noOfSets = 1 << noOfAttr;
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
                .filter(reducts::get)
                .mapToObj(this::convertToMask)
                .map(mask -> new Reduct(mask, attributes))
                .collect(Collectors.toList());
    }

    private boolean isReduct(AttributeIndicesMask indicesMask) {
        for (int i = 0; i < indiscernibilityClasses.size(); i++) {
            IndiscernibilityClass indClass1 = indiscernibilityClasses.get(i);

            if (indClass1.coversOneClass()) {
                for (int j = i + 1; j < indiscernibilityClasses.size(); j++) {
                    IndiscernibilityClass indClass2 = indiscernibilityClasses.get(j);

                    if (!coverSameRoughSet(indClass1, indClass2)) {
                        if (indClass1.equalsOnAttributes(indClass2, indicesMask)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean coverSameRoughSet(IndiscernibilityClass indClass1, IndiscernibilityClass indClass2) {
        return indClass2.getRoughSetClasses().equals(indClass1.getRoughSetClasses());
    }

    private AttributeIndicesMask convertToMask(int i) {
        return new AttributeIndicesMask(i, noOfAttr);
    }

    private void removeSubsetsFromReducts(int i) {
        for (int j = i - 1; j > 0; j--) {
            if (isSubset(i, j)) {
                removeSetFromReducts(j);
            }
        }
    }

    private void removeSupersetsFromReducts(int i) {
        for (int j = reducts.size() - 1; j > i; j--) {
            if (isSuperset(i, j)) {
                removeSetFromReducts(j);
            }
        }
    }

    private boolean isSuperset(int i, int j) {
        return (i & j) == i;
    }

    private void removeSetFromReducts(int i) {
        reducts.set(i, Boolean.FALSE);
    }

    private Boolean canBeReduct(int i) {
        return reducts.get(i);
    }

    private boolean isSubset(int i, int j) {
        return (i | j) == i;
    }
}