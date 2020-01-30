package pl.poznan.put.roughset.decision;

import pl.poznan.put.roughset.alternative.AlternativePair;

import java.util.HashSet;
import java.util.Set;

public class Outranking {

    private String id;
    private OutrankingType outrankingType;

    private Set<AlternativePair> alternativePairs = new HashSet<>();
    private Set<AlternativePair> lowerApprox = new HashSet<>();
    private Set<AlternativePair> upperApprox = new HashSet<>();

    public Outranking(String id) {
        this.id = id;
    }

    public void addAlternativePair(AlternativePair alternativePair) {
        alternativePairs.add(alternativePair);
    }

    public void addAllToLowerApprox(Set<AlternativePair> alternativePairs) {
        lowerApprox.addAll(alternativePairs);
    }

    public void addAllToUpperApprox(Set<AlternativePair> alternativePairs) {
        upperApprox.addAll(alternativePairs);
    }

    public String getId() {
        return id;
    }

    public Set<AlternativePair> getAlternativePairs() {
        return alternativePairs;
    }

    public Set<AlternativePair> getLowerApprox() {
        return lowerApprox;
    }

    public Set<AlternativePair> getUpperApprox() {
        return upperApprox;
    }

    public enum OutrankingType {
        S, Sc;
    }
}
