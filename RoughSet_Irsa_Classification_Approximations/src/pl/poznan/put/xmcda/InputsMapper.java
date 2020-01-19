package pl.poznan.put.xmcda;

import org.xmcda.PerformanceTable;
import org.xmcda.ProgramExecutionResult;
import org.xmcda.XMCDA;
import pl.poznan.put.roughset.Classification;
import pl.poznan.put.roughset.alternative.Alternative;
import pl.poznan.put.roughset.alternative.ValueOnAttribute;
import pl.poznan.put.roughset.attribute.Attribute;
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

    public Classification mapToClassification() {
        Set<Alternative> alternatives = extractAlternatives();
        Set<RoughSetClass> roughSetClasses = extractRoughSetClasses();
        extractAssignments(alternatives, roughSetClasses);
        List<Attribute> attributes = extractAttributes();
        //TODO extractCriteriaDomains
        extractValuesOnAttributes(alternatives, attributes);

        return new Classification(attributes, alternatives, roughSetClasses);
    }

    private Set<Alternative> extractAlternatives() {
        return xmcda.alternatives.getActiveAlternatives().stream()
                .map(a -> new Alternative(a.id()))
                .collect(Collectors.toSet());
    }

    private Set<RoughSetClass> extractRoughSetClasses() {
        return xmcda.categories.getActiveCategories().stream()
                .map(c -> new RoughSetClass(c.id(), c.name()))
                .collect(Collectors.toSet());
    }

    private void extractAssignments(Set<Alternative> alternatives, Set<RoughSetClass> roughSetClasses) {
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
                alternatives.stream()
                        .filter(v -> v.getId().equals(a))
                        .findFirst()
                        .ifPresent(rscOptional.get()::addAlternative);
            } else {
                errors.addError("Category: " + r + " was not declared in criteria file");
            }
        });
    }

    private List<Attribute> extractAttributes() {
        return xmcda.criteria.getActiveCriteria().stream()
                .map(c -> new Attribute(c.id(), c.name()))
                .collect(Collectors.toList());
    }

    private void extractValuesOnAttributes(Set<Alternative> alternatives, List<Attribute> attributes) {

        // already converted to String in checkInputs()
        @SuppressWarnings("unchecked")
        PerformanceTable<String> xmcda_perf_table = (PerformanceTable<String>) xmcda.performanceTablesList.get(0);

        xmcda_perf_table.getAlternatives().forEach(xA ->
                alternatives.stream()
                        .filter(a -> a.getId().equals(xA.id()))
                        .findFirst()
                        .ifPresent(a ->
                                xmcda_perf_table.getCriteria().forEach(xC -> {
                                    attributes.stream()
                                            .filter(attr -> attr.getId().equals(xC.id()))
                                            .findFirst()
                                            .ifPresent(attr -> a.addToValuesOnAttributes(new ValueOnAttribute(attr, xmcda_perf_table.getValue(xA, xC))));

                                    //TODO check if value belongs to domain, delared in criteria file
                                })
                        )
        );
    }
}


