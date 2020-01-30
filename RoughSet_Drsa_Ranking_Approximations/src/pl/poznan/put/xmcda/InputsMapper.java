package pl.poznan.put.xmcda;

import org.xmcda.*;
import org.xmcda.utils.Coord;
import org.xmcda.value.ValuedLabel;
import pl.poznan.put.roughset.Ranking;
import pl.poznan.put.roughset.alternative.Alternative;
import pl.poznan.put.roughset.alternative.AlternativePair;
import pl.poznan.put.roughset.alternative.valueOnCriterion.ValueOnCategoricalCriterion;
import pl.poznan.put.roughset.alternative.valueOnCriterion.ValueOnNumericalCriterion;
import pl.poznan.put.roughset.consistency.CostMeasureEpsilon;
import pl.poznan.put.roughset.consistency.CostMeasureEpsilonPrime;
import pl.poznan.put.roughset.consistency.RoughMembership;
import pl.poznan.put.roughset.consistency.ConsistencyMeasure;
import pl.poznan.put.roughset.criterion.CategoricalCriterion;
import pl.poznan.put.roughset.criterion.Criterion;
import pl.poznan.put.roughset.criterion.CriterionType;
import pl.poznan.put.roughset.criterion.NumericalCriterion;
import pl.poznan.put.roughset.decision.Outranking;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InputsMapper {

    private XMCDA xmcda;
    private ProgramExecutionResult errors;

    private List<Criterion> criteria = new ArrayList<>();
    private Outranking s;
    private Outranking sc;
    private List<Alternative> alternatives = new ArrayList<>();
    private List<AlternativePair> alternativePairs = new ArrayList<>();

    public InputsMapper(XMCDA xmcda, ProgramExecutionResult errors) {
        this.xmcda = xmcda;
        this.errors = errors;
    }

    public Ranking mapToRanking() {
        extractCriteriaWithDomains();
        extractCategories();
        extractAlternatives();
        extractValuesOnCriteria();
        extractPreferences();
        ConsistencyMeasure consistencyMeasure = extractConsistencyMeasure();

        return new Ranking(criteria, alternativePairs, s, sc, consistencyMeasure);
    }

    private void extractCategories() {
        Category categoryS = xmcda.categories.getActiveCategories().get(0);
        s = new Outranking(categoryS.id());

        Category categorySc = xmcda.categories.getActiveCategories().get(1);
        sc = new Outranking(categorySc.id());
    }

    private void extractCriteriaWithDomains() {
        Map<String, String> names = xmcda.criteria.getActiveCriteria().stream()
                .collect(Collectors.toMap(c -> c.id(), c -> c.name()));

        CriteriaScales criteriaScales = xmcda.criteriaScalesList.get(0);

        for (org.xmcda.Criterion criterion : xmcda.criteria.getActiveCriteria()) {
            CriterionScales criterionScales = criteriaScales.get(criterion);
            Scale scale = criterionScales.get(0);

            if (scale instanceof QuantitativeScale) {
                QuantitativeScale<?> qScale = (QuantitativeScale<?>) scale;
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
    }

    private boolean isGain(Scale.PreferenceDirection preferenceDirection) {
        return preferenceDirection == Scale.PreferenceDirection.MAX;
    }

    private void extractAlternatives() {
        alternatives = xmcda.alternatives.getActiveAlternatives().stream()
                .map(a -> new Alternative(a.id()))
                .collect(Collectors.toList());
    }

    private void extractValuesOnCriteria() {
        @SuppressWarnings("unchecked")
        PerformanceTable<String> performanceTable = (PerformanceTable<String>) xmcda.performanceTablesList.get(0);

        for (org.xmcda.Alternative alternative : performanceTable.getAlternatives()) {
            findAlternative(alternative.id()).ifPresent(v -> {
                for (org.xmcda.Criterion criterion : performanceTable.getCriteria()) {
                    findCriterion(criterion.id()).ifPresent(c ->
                            insertValueOnCriterion(v, c, performanceTable.getValue(alternative, criterion))
                    );
                }
            });
        }
    }

    private Optional<Alternative> findAlternative(String id) {
        return alternatives.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst();
    }

    private Optional<Criterion> findCriterion(String id) {
        return criteria.stream()
                .filter(attr -> attr.getId().equals(id))
                .findFirst();
    }

    private void insertValueOnCriterion(Alternative alternative, Criterion criterion, Object value) {
        //TODO check if value belongs to domain, declared in criteria file
        if (criterion instanceof NumericalCriterion) {
            NumericalCriterion nc = (NumericalCriterion) criterion;
            double doubleValue = (double) value;
            alternative.addToValuesOnCriteria(new ValueOnNumericalCriterion(nc, doubleValue));
        } else {
            CategoricalCriterion cc = (CategoricalCriterion) criterion;
            alternative.addToValuesOnCriteria(new ValueOnCategoricalCriterion(cc, (String) value));
        }
    }

    private void extractPreferences() {
        for (Coord<org.xmcda.Alternative, org.xmcda.Alternative> coord : xmcda.alternativesMatricesList.get(0).keySet()) {
            findAlternative(coord.x.id()).ifPresent(x ->
                    findAlternative(coord.y.id()).ifPresent(y ->
                            createAlternativePair(x, y, s)
                    )
            );
        }
        for (Coord<org.xmcda.Alternative, org.xmcda.Alternative> coord : xmcda.alternativesMatricesList.get(1).keySet()) {
            findAlternative(coord.x.id()).ifPresent(x ->
                    findAlternative(coord.y.id()).ifPresent(y ->
                            createAlternativePair(x, y, sc)
                    )
            );
        }
    }

    private void createAlternativePair(Alternative x, Alternative y, Outranking s) {
        AlternativePair alternativePair = new AlternativePair(x, y, s);
        s.addAlternativePair(alternativePair);
        alternativePairs.add(alternativePair);
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


