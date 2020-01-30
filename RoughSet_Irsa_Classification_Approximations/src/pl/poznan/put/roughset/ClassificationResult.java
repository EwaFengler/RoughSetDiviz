package pl.poznan.put.roughset;

import org.xmcda.*;
import pl.poznan.put.roughset.alternative.Alternative;
import pl.poznan.put.roughset.alternative.IndiscernibilityClass;
import pl.poznan.put.roughset.attribute.Attribute;
import pl.poznan.put.roughset.decision.RoughSetClass;
import pl.poznan.put.roughset.reduct.Reduct;

import java.util.List;
import java.util.Set;

public class ClassificationResult {

    private static final String QUALITY_OF_APPROXIMATION = "Quality of approximation";

    private List<IndiscernibilityClass> indiscernibilityClasses;
    private Set<RoughSetClass> roughSetClasses;
    private List<Reduct> reducts;
    private double qualityOfApprox;

    public ClassificationResult(List<IndiscernibilityClass> indiscernibilityClasses, Set<RoughSetClass> roughSetClasses, List<Reduct> reducts, double qualityOfApprox) {
        this.indiscernibilityClasses = indiscernibilityClasses;
        this.roughSetClasses = roughSetClasses;
        this.reducts = reducts;
        this.qualityOfApprox = qualityOfApprox;
    }

    public Granules getXmcdaIndiscernibilityClasses() {
        Granules granules = new Granules();

        for (IndiscernibilityClass indiscernibilityClass : indiscernibilityClasses) {
            for (Alternative alternative : indiscernibilityClass.getAlternatives()) {
                Granule granule = buildXmcdaGranule(indiscernibilityClass.getAlternatives(), alternative);
                granules.add(granule);
            }
        }
        return granules;
    }

    public Approximations getXmcdaApproximations() {
        Approximations approximations = new Approximations();

        for (RoughSetClass roughSetClass : roughSetClasses) {
            Decisions decisions = buildXmcdaDecisions(roughSetClass);

            Set<Alternative> lowerApproxAlternatives = roughSetClass.getLowerApprox();
            Approximation lowerApprox = buildXmcdaApproximation(lowerApproxAlternatives, RoughSetClass.APPROXIMATION_LOWER);
            lowerApprox.setDecisionsClasses(decisions);
            approximations.add(lowerApprox);

            Set<Alternative> upperApproxAlternatives = roughSetClass.getUpperApprox();
            Approximation upperApprox = buildXmcdaApproximation(upperApproxAlternatives, RoughSetClass.APPROXIMATION_UPPER);
            upperApprox.setDecisionsClasses(decisions);
            approximations.add(upperApprox);
        }
        return approximations;
    }

    public CriteriaSets<Object> getXmcdaReducts() {
        CriteriaSets<Object> criteriaSets = new CriteriaSets<>();
        reducts.forEach(r -> criteriaSets.add(buildXmcdaCriteriaSet(r)));
        return criteriaSets;
    }

    public ProgramParameters<Double> getXmcdaQualityOfApprox() {
        ProgramParameters<Double> programParameters = new ProgramParameters<>();
        programParameters.add(buildXmcdaProgramParameter());
        return programParameters;
    }

    private Granule buildXmcdaGranule(Set<Alternative> alternatives, Alternative argumentAlternative) {
        Granule granule = new Granule();
        Argument argument = buildXmcdaArgument(argumentAlternative);
        Association association = buildXmcdaAssociation(alternatives);

        granule.setMcdaConcept(IndiscernibilityClass.GRANULE_TYPE);
        granule.setArgument(argument);
        granule.setAssociation(association);

        return granule;
    }

    private Argument buildXmcdaArgument(Alternative alternative) {
        Argument argument = new Argument();
        argument.setAlternative(new org.xmcda.Alternative(alternative.getId()));
        return argument;
    }

    private Association buildXmcdaAssociation(Set<Alternative> alternatives) {
        Association association = new Association();
        AlternativesSet<Object> alternativesSet = buildXmcdaAlternativesSet(alternatives);
        association.setAlternativesSet(alternativesSet);
        return association;
    }

    private Approximation buildXmcdaApproximation(Set<Alternative> approx, String approxType) {
        Approximation approximation = new Approximation();
        approximation.setMcdaConcept(approxType);
        approximation.setAlternativesSet(buildXmcdaAlternativesSet(approx));
        return approximation;
    }

    private Decisions buildXmcdaDecisions(RoughSetClass roughSetClass) {
        Decisions decisions = new Decisions();
        Decision decision = buildXmcdaDecision(roughSetClass);
        decisions.add(decision);
        return decisions;
    }

    private Decision buildXmcdaDecision(RoughSetClass roughSetClass) {
        Decision decision = new Decision();
        decision.setCategoryID(roughSetClass.getId());
        return decision;
    }

    private void addCriteria(Reduct reduct, CriteriaSet<?> criteriaSet) {
        for (Attribute attribute : reduct.getAttributes()) {
            criteriaSet.put(new Criterion(attribute.getId()), null);
        }
    }

    private CriteriaSet<Object> buildXmcdaCriteriaSet(Reduct reduct) {
        CriteriaSet<Object> criteriaSet = new CriteriaSet<>();
        criteriaSet.setId(reduct.toString());
        addCriteria(reduct, criteriaSet);
        return criteriaSet;
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

    private AlternativesSet<Object> buildXmcdaAlternativesSet(Set<Alternative> alternatives) {
        AlternativesSet<Object> alternativesSet = new AlternativesSet<>("");
        addAlternatives(alternativesSet, alternatives);
        return alternativesSet;
    }

    private void addAlternatives(AlternativesSet<Object> alternativesSet, Set<Alternative> alternatives) {
        for (Alternative alternative : alternatives) {
            alternativesSet.put(new org.xmcda.Alternative(alternative.getId()), null);
        }
    }
}
