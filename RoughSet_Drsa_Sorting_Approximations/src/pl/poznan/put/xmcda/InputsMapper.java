package pl.poznan.put.xmcda;

import org.xmcda.*;
import org.xmcda.utils.ValueConverters;
import org.xmcda.value.ValuedLabel;
import pl.poznan.put.roughset.Sorting;
import pl.poznan.put.roughset.alternative.Alternative;
import pl.poznan.put.roughset.alternative.ValueOnCriterion.ValueOnCategoricalCriterion;
import pl.poznan.put.roughset.alternative.ValueOnCriterion.ValueOnNumericalCriterion;
import pl.poznan.put.roughset.consistency.CostMeasureEpsilon;
import pl.poznan.put.roughset.consistency.CostMeasureEpsilonPrime;
import pl.poznan.put.roughset.consistency.ConsistencyMeasure;
import pl.poznan.put.roughset.consistency.RoughMembership;
import pl.poznan.put.roughset.criterion.CategoricalCriterion;
import pl.poznan.put.roughset.criterion.Criterion;
import pl.poznan.put.roughset.criterion.CriterionType;
import pl.poznan.put.roughset.criterion.NumericalCriterion;
import pl.poznan.put.roughset.decision.RoughSetClass;

import java.util.*;
import java.util.stream.Collectors;

public class InputsMapper {

    private XMCDA xmcda;
    private ProgramExecutionResult errors;

    public InputsMapper(XMCDA xmcda, ProgramExecutionResult errors) {
        this.xmcda = xmcda;
        this.errors = errors;
    }

    public Sorting mapToClassification() {
        List<Alternative> alternatives = extractAlternatives();
        Map<String, Integer> classHierarchy = extractClassHierarchy();
        List<RoughSetClass> roughSetClasses = extractRoughSetClasses(classHierarchy);
        extractAssignments(alternatives, roughSetClasses);
        List<Criterion> criteria = extractCriteriaWithDomains();
        extractValuesOnCriteria(alternatives, criteria);
        ConsistencyMeasure consistencyMeasure = extractConsistencyMeasure();

        return new Sorting(criteria, alternatives, roughSetClasses, consistencyMeasure);
    }

    private List<Alternative> extractAlternatives() {
        return xmcda.alternatives.getActiveAlternatives().stream()
                .map(a -> new Alternative(a.id()))
                .collect(Collectors.toList());
    }

    private Map<String, Integer> extractClassHierarchy() {
        Map<String, Integer> classHierarchy = new HashMap<>();

        try {
            CategoriesValues<Integer> categoriesValues = xmcda.categoriesValuesList.get(0).convertTo(Integer.class);

            for (Map.Entry<Category, LabelledQValues<Integer>> entry : categoriesValues.entrySet()) {
                String category = entry.getKey().id();
                Integer value = entry.getValue().get(0).getValue();
                classHierarchy.put(category, value);
            }

        } catch (ValueConverters.ConversionException e) {
            errors.addError("Categories values must be integers");//TODO przenieść
        }

        return classHierarchy;
    }

    private List<RoughSetClass> extractRoughSetClasses(Map<String, Integer> classHierarchy) {
        return xmcda.categories.getActiveCategories().stream()
                .map(c -> new RoughSetClass(c.id(), c.name(), classHierarchy))
                .collect(Collectors.toList());
    }

    private void extractAssignments(List<Alternative> alternatives, List<RoughSetClass> roughSetClasses) {
        HashMap<String, String> alternativesAssignments = new HashMap<>();

        boolean nonUnique = xmcda.alternativesAssignmentsList.get(0).stream()
                .map(a -> alternativesAssignments.putIfAbsent(a.getAlternative().id(), a.getCategory().id()))
                .anyMatch(Objects::nonNull);

        if (nonUnique) {
            errors.addError("Only one assignment for each alternative allowed");
        }

        alternativesAssignments.forEach((a, r) -> {
            Optional<RoughSetClass> rscOptional = roughSetClasses.stream().filter(rsc -> rsc.getId().equals(r)).findFirst();

            if (rscOptional.isPresent()) {
                findAlternative(alternatives, a)
                        .ifPresent(rscOptional.get()::addAlternative);
            } else {
                errors.addError("Category: " + r + " was not declared in criteria file");
            }
        });

        //TODO check if all alternatives are assigned to category
    }

    private List<Criterion> extractCriteriaWithDomains() {
        Map<String, String> names = xmcda.criteria.getActiveCriteria().stream()
                .collect(Collectors.toMap(c -> c.id(), c -> c.name()));
        List<Criterion> criteria = new ArrayList<>();

        CriteriaScales criteriaScales = xmcda.criteriaScalesList.get(0);

        for (org.xmcda.Criterion criterion : xmcda.criteria.getActiveCriteria()) {
            CriterionScales criterionScales = criteriaScales.get(criterion);
            Scale scale = criterionScales.get(0);

            if (scale instanceof QuantitativeScale) {
                QuantitativeScale qScale = (QuantitativeScale) scale;
                CriterionType criterionType = isGain(qScale.getPreferenceDirection()) ? CriterionType.GAIN : CriterionType.COST;
                criteria.add(new NumericalCriterion(criterion.id(), names.get(criterion.id()), criterionType));

            } else if (scale instanceof QualitativeScale) {
                @SuppressWarnings("unchecked")
                QualitativeScale<Integer> qScale = (QualitativeScale<Integer>) scale;
                CriterionType criterionType = isGain(qScale.getPreferenceDirection()) ? CriterionType.GAIN : CriterionType.COST;

                Map<String, Integer> categoryMap = qScale.stream()
                        .collect(Collectors.toMap(ValuedLabel::getLabel, vl -> vl.getValue().getValue()));

                criteria.add(new CategoricalCriterion(criterion.id(), names.get(criterion.id()), criterionType, categoryMap));

            } else {
                errors.addError("All criteria must be either quantitative or qualitative");
            }
        }
        return criteria;
    }

    private boolean isGain(Scale.PreferenceDirection preferenceDirection) {
        return preferenceDirection == Scale.PreferenceDirection.MAX;
    }

    private void extractValuesOnCriteria(List<Alternative> alternatives, List<Criterion> criteria) {
        @SuppressWarnings("unchecked")
        PerformanceTable<String> performanceTable = (PerformanceTable<String>) xmcda.performanceTablesList.get(0);

        for (org.xmcda.Alternative alternative : performanceTable.getAlternatives()) {
            findAlternative(alternatives, alternative.id()).ifPresent(a -> {
                for (org.xmcda.Criterion criterion : performanceTable.getCriteria()) {
                    findCriterion(criteria, criterion.id()).ifPresent(c ->
                            insertValueOnCriterion(a, c, performanceTable.getValue(alternative, criterion))
                    );
                }
            });
        }
    }

    private Optional<Alternative> findAlternative(List<Alternative> alternatives, String id) {
        return alternatives.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst();
    }

    private Optional<Criterion> findCriterion(List<Criterion> criteria, String id) {
        return criteria.stream()
                .filter(attr -> attr.getId().equals(id))
                .findFirst();
    }

    private void insertValueOnCriterion(Alternative alternative, Criterion criterion, Object value) {
        //TODO check if value belongs to domain, delared in criteria file
        if (criterion instanceof NumericalCriterion) {
            NumericalCriterion nc = (NumericalCriterion) criterion;
            double doubleValue = (double) value;
            alternative.addToValuesOnCriteria(new ValueOnNumericalCriterion(nc, doubleValue));
        } else {
            CategoricalCriterion cc = (CategoricalCriterion) criterion;
            alternative.addToValuesOnCriteria(new ValueOnCategoricalCriterion(cc, value.toString()));
        }
    }

    private ConsistencyMeasure extractConsistencyMeasure() {
        ProgramParameters<?> parameters = xmcda.programParametersList.get(0);
        ProgramParameter<?> measureParameter;
        ProgramParameter<?> thresholdParameter;

        if(parameters.get(0).id().equals("consistencyMeasure")){
            measureParameter = parameters.get(0);
            thresholdParameter = parameters.get(1);
        } else {
            thresholdParameter = parameters.get(0);
            measureParameter = parameters.get(1);
        }

        Double thresholdValue = (Double) thresholdParameter.getValues().get(0).getValue();
        String measureName = (String) measureParameter.getValues().get(0).getValue();

        switch (measureName){
            case "rough_membership":
                return new RoughMembership(thresholdValue);
            case "epsilon":
                return new CostMeasureEpsilon(thresholdValue);
            case "epsilon_prime":
                return new CostMeasureEpsilonPrime(thresholdValue);
            default:
                return new RoughMembership(1);
        }
    }
}


