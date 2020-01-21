package pl.poznan.put.roughset;

import org.xmcda.*;
import org.xmcda.value.NA;
import pl.poznan.put.roughset.alternative.Alternative;
import pl.poznan.put.roughset.criterion.Criterion;
import pl.poznan.put.roughset.decision.ClassUnion;
import pl.poznan.put.roughset.decision.RoughSetClass;
import pl.poznan.put.roughset.reduct.Reduct;

import java.util.List;
import java.util.Set;

public class SortingResult {

    private static final String QUALITY_OF_APPROXIMATION = "Quality of approximation";

    private List<Alternative> alternatives;
    private List<RoughSetClass> roughSetClasses;
    private List<Reduct> reducts;
    private double qualityOfApprox;

    public SortingResult(List<Alternative> alternatives, List<RoughSetClass> roughSetClasses, List<Reduct> reducts, double qualityOfApprox) {
        this.alternatives = alternatives;
        this.roughSetClasses = roughSetClasses;
        this.reducts = reducts;
        this.qualityOfApprox = qualityOfApprox;
    }

    public Granules getXmcdaDominanceCones() {
        Granules granules = new Granules();

        for (Alternative alternative : alternatives) {
            Granule positiveCone = buildXmcdaGranule(alternative, Alternative.CONE_POSITIVE, alternative.getPositiveDominance());
            granules.add(positiveCone);

            Granule negativeCone = buildXmcdaGranule(alternative, Alternative.CONE_NEGATIVE, alternative.getNegativeDominance());
            granules.add(negativeCone);
        }
        return granules;
    }

    public Approximations getXmcdaApproximations() {
        Approximations approximations = new Approximations();
        buildBothXmcdaApproximations(approximations, roughSetClasses.get(0).getDownwardUnion());
        for (int i = 1; i < roughSetClasses.size() - 1; i++) {
            RoughSetClass roughSetClass = roughSetClasses.get(i);
            buildBothXmcdaApproximations(approximations, roughSetClass.getDownwardUnion());
            buildBothXmcdaApproximations(approximations, roughSetClass.getUpwardUnion());
        }
        buildBothXmcdaApproximations(approximations, roughSetClasses.get(roughSetClasses.size() - 1).getUpwardUnion());
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

    private Granule buildXmcdaGranule(Alternative alternative, String coneDirection, Set<Alternative> dominanceCone) {
        Granule cone = new Granule();
        Argument argument = buildXmcdaArgument(alternative);
        Association association = buildXmcdaAssociation(dominanceCone);

        cone.setMcdaConcept(coneDirection);
        cone.setArgument(argument);
        cone.setAssociation(association);

        return cone;
    }

    private Argument buildXmcdaArgument(Alternative alternative) {
        Argument argument = new Argument();
        argument.setAlternative(new org.xmcda.Alternative(alternative.getId()));
        return argument;
    }

    private Association buildXmcdaAssociation(Set<Alternative> dominanceCone) {
        Association association = new Association();
        AlternativesSet<Object> alternativesSet = buildXmcdaAlternativesSet(dominanceCone);
        association.setAlternativesSet(alternativesSet);
        return association;
    }

    private void buildBothXmcdaApproximations(Approximations approximations, ClassUnion union) {
        Decisions decisions = buildXmcdaDecisions(union);

        Approximation lowerApprox = buildXmcdaApproximation("lower", union.getLowerApprox());
        lowerApprox.setDecisionsClasses(decisions);
        approximations.add(lowerApprox);

        Approximation upperApprox = buildXmcdaApproximation("upper", union.getUpperApprox());
        upperApprox.setDecisionsClasses(decisions);
        approximations.add(upperApprox);
    }

    private Decisions buildXmcdaDecisions(ClassUnion union) {
        Decisions decisions = new Decisions();
        Decision decision = buildXmcdaDecision(union);
        decisions.add(decision);
        return decisions;
    }

    private Decision buildXmcdaDecision(ClassUnion union) {
        Decision decision = new Decision();
        CategoriesInterval categoriesInterval = buildXmcdaCategoriesInterval(union);
        decision.setCategoriesInterval(categoriesInterval);
        return decision;
    }

    private CategoriesInterval buildXmcdaCategoriesInterval(ClassUnion union) {
        CategoriesInterval categoriesInterval = new CategoriesInterval();

        Category bound = buildXmcdaCategory(union.getBound());
        if(union.getUnionType().equals(ClassUnion.ClassUnionType.DOWNWARD)){
            categoriesInterval.setUpperBound(bound);
        } else {
            categoriesInterval.setLowerBound(bound);
        }

        return categoriesInterval;
    }

    private Category buildXmcdaCategory(RoughSetClass roughSetClass) {
        return new Category(roughSetClass.getId());
    }

    private Approximation buildXmcdaApproximation(String approxType, Set<Alternative> approxAsSet) {
        Approximation approx = new Approximation();
        approx.setMcdaConcept(approxType);
        approx.setAlternativesSet(buildXmcdaAlternativesSet(approxAsSet));
        return approx;
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

    private AlternativesSet<Object> buildXmcdaAlternativesSet(Set<Alternative> dominanceCone) {
        AlternativesSet<Object> alternativesSet = new AlternativesSet<>("");
        addAlternatives(dominanceCone, alternativesSet);
        return alternativesSet;
    }

    private void addAlternatives(Set<Alternative> approxAsSet, AlternativesSet<Object> approxAlternatives) {
        for (Alternative alternative : approxAsSet) {
            org.xmcda.Alternative xmcdaAlternative = new org.xmcda.Alternative(alternative.getId());
            approxAlternatives.put(xmcdaAlternative, null); //TODO co z nullem?
        }
    }
}
