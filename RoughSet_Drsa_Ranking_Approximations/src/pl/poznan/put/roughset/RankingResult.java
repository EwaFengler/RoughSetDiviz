package pl.poznan.put.roughset;

import org.xmcda.*;
import org.xmcda.utils.Coord;
import org.xmcda.value.NA;
import pl.poznan.put.roughset.alternative.AlternativePair;
import pl.poznan.put.roughset.criterion.Criterion;
import pl.poznan.put.roughset.decision.Outranking;
import pl.poznan.put.roughset.reduct.Reduct;

import java.util.List;
import java.util.Set;

public class RankingResult {

    private static final String QUALITY_OF_APPROXIMATION = "Quality of approximation";

    private List<AlternativePair> alternativePairs;
    private Outranking s;
    private Outranking sc;
    private List<Reduct> reducts;
    private double qualityOfApprox;

    public RankingResult(List<AlternativePair> alternativePairs, Outranking s, Outranking sc, List<Reduct> reducts, double qualityOfApprox) {
        this.alternativePairs = alternativePairs;
        this.s = s;
        this.sc = sc;
        this.reducts = reducts;
        this.qualityOfApprox = qualityOfApprox;
    }

    public Granules getXmcdaDominanceCones() {
        Granules granules = new Granules();

        for (AlternativePair alternativePair : alternativePairs) {
            Granule positiveCone = buildConeGranule(alternativePair, "positiveCone", alternativePair.getPositiveDominance());
            granules.add(positiveCone);

            Granule negativeCone = buildConeGranule(alternativePair, "negativeCone", alternativePair.getNegativeDominance());
            granules.add(negativeCone);
        }

        return granules;
    }

    public Approximations getXmcdaApproximations() {
        Approximations approximations = new Approximations();
        BuildBothXmcdaApproximations(approximations, s);
        BuildBothXmcdaApproximations(approximations, sc);
        return approximations;
    }

    public CriteriaSets<NA> getXmcdaReducts() {
        CriteriaSets<NA> criteriaSets = new CriteriaSets<>();
        reducts.forEach(r -> criteriaSets.add(buildXmcdaCriteriaSet(r)));
        return criteriaSets;
    }

    public ProgramParameters<Double> getXmcdaQualityOfApprox() {
        ProgramParameters<Double> programParameters = new ProgramParameters<>();
        programParameters.add(buildXmcdaProgramParameter());
        return programParameters;
    }

    private Granule buildConeGranule(AlternativePair alternativePair, String coneDirection, Set<AlternativePair> dominanceCone) {
        Granule cone = new Granule();
        Argument argument = buildXmcdaArgument(alternativePair);
        Association association = buildXmcdaAssociation(dominanceCone);

        cone.setMcdaConcept(coneDirection);
        cone.setArgument(argument);
        cone.setAssociation(association);

        return cone;
    }

    private Argument buildXmcdaArgument(AlternativePair alternativePair) {
        Argument argument = new Argument();
        AlternativesMatrix<Integer> argumentPair = buildXmcdaArgumentAlternatives(alternativePair);
        argument.setAlternativesMatrix(argumentPair);
        return argument;
    }

    private AlternativesMatrix<Integer> buildXmcdaArgumentAlternatives(AlternativePair alternativePair) {
        AlternativesMatrix<Integer> argumentAlternatives = new AlternativesMatrix<>();

        Coord<Alternative, Alternative> coord = buildXmcdaMatrixCoord(alternativePair);
        putCoord(argumentAlternatives, coord);

        return argumentAlternatives;
    }

    private Association buildXmcdaAssociation(Set<AlternativePair> dominanceCone) {
        Association association = new Association();
        association.setAlternativesMatrix(buildXmcdaAlternativesMatrix(dominanceCone));
        return association;
    }

    private void BuildBothXmcdaApproximations(Approximations approximations, Outranking outranking) {
        Decisions decisions = buildXmcdaDecisions(outranking);

        Approximation lowerApprox = buildXmcdaApproximation("lower", outranking.getLowerApprox());
        lowerApprox.setDecisionsClasses(decisions);
        approximations.add(lowerApprox);

        Approximation upperApprox = buildXmcdaApproximation("upper", outranking.getUpperApprox());
        upperApprox.setDecisionsClasses(decisions);
        approximations.add(upperApprox);
    }

    private Decisions buildXmcdaDecisions(Outranking outranking) {
        Decisions decisions = new Decisions();
        Decision decision = new Decision();
        decision.setCategoryID(outranking.getId());
        decisions.add(decision);
        return decisions;
    }

    private Approximation buildXmcdaApproximation(String approxType, Set<AlternativePair> approxAsSet) {
        Approximation approx = new Approximation();
        approx.setMcdaConcept(approxType);

        AlternativesMatrix<Integer> approxAlternatives = buildXmcdaAlternativesMatrix(approxAsSet);
        approx.setAlternativesMatrix(approxAlternatives);
        return approx;
    }

    private AlternativesMatrix<Integer> buildXmcdaAlternativesMatrix(Set<AlternativePair> alternativePairSet) {
        AlternativesMatrix<Integer> alternativesMatrix = new AlternativesMatrix<>();
        addCoords(alternativePairSet, alternativesMatrix);
        return alternativesMatrix;
    }

    private void addCoords(Set<AlternativePair> alternativePairSet, AlternativesMatrix<Integer> alternativesMatrix) {
        for (AlternativePair alternativePair : alternativePairSet) {
            Coord<Alternative, Alternative> coord = buildXmcdaMatrixCoord(alternativePair);
            putCoord(alternativesMatrix, coord);
        }
    }

    private Coord<Alternative, Alternative> buildXmcdaMatrixCoord(AlternativePair alternativePair) {
        Alternative alt1 = new Alternative(alternativePair.getAlternativeX().getId());
        Alternative alt2 = new Alternative(alternativePair.getAlternativeY().getId());
        return new Coord<>(alt1, alt2);
    }

    private void putCoord(AlternativesMatrix<Integer> argumentAlternatives, Coord<Alternative, Alternative> coord) {
        QualifiedValues<Integer> qualifiedValues = new QualifiedValues<>(new QualifiedValue<>(1));
        argumentAlternatives.put(coord, qualifiedValues);
    }

    private CriteriaSet<NA> buildXmcdaCriteriaSet(Reduct reduct) {
        CriteriaSet<NA> criteriaSet = new CriteriaSet<>();
        criteriaSet.setId(reduct.toString());
        addCriteria(reduct, criteriaSet);
        return criteriaSet;
    }

    private void addCriteria(Reduct reduct, CriteriaSet<NA> criteriaSet) {
        for (Criterion criterion : reduct.getCriteria()) {
            criteriaSet.put(new org.xmcda.Criterion(criterion.getId()), buildXmcdaNaQualifiedValues());
        }
    }

    private QualifiedValues<NA> buildXmcdaNaQualifiedValues() {
        QualifiedValues<NA> qualifiedValues = new QualifiedValues<>();
        QualifiedValue<NA> qualifiedValue = buildXmcdaNaQualifiedValue();
        qualifiedValues.add(qualifiedValue);
        return qualifiedValues;
    }

    private QualifiedValue<NA> buildXmcdaNaQualifiedValue() {
        QualifiedValue<NA> qualifiedValue = new QualifiedValue<>();
        qualifiedValue.setValue(NA.na);
        return qualifiedValue;
    }

    private ProgramParameter<Double> buildXmcdaProgramParameter() {
        ProgramParameter<Double> programParameter = new ProgramParameter<>();
        programParameter.setValues(buildXmcdaRealQualifiedValues());
        return programParameter;
    }

    private QualifiedValues<Double> buildXmcdaRealQualifiedValues() {
        QualifiedValues<Double> qualifiedValues = new QualifiedValues<>();
        qualifiedValues.add(buildXmcdaRealQualifiedValue());
        return qualifiedValues;
    }

    private QualifiedValue<Double> buildXmcdaRealQualifiedValue() {
        QualifiedValue<Double> qualifiedValue = new QualifiedValue<>();
        qualifiedValue.setName(QUALITY_OF_APPROXIMATION);
        qualifiedValue.setValue(qualityOfApprox);
        return qualifiedValue;
    }
}
