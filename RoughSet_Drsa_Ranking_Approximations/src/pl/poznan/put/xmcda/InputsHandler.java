package pl.poznan.put.xmcda;

import org.xmcda.PerformanceTable;
import org.xmcda.ProgramExecutionResult;
import org.xmcda.XMCDA;
import pl.poznan.put.roughset.Ranking;

public class InputsHandler {

    public static Ranking checkAndExtractInputs(XMCDA xmcda, ProgramExecutionResult xmcda_exec_result) {
        checkInputs(xmcda, xmcda_exec_result);

        if (xmcda_exec_result.isError()) {
            return null;
        }

        InputsMapper inputsMapper = new InputsMapper(xmcda, xmcda_exec_result);
        return inputsMapper.mapToRanking();
    }

    private static void checkInputs(XMCDA xmcda, ProgramExecutionResult errors) {
        checkPerformanceTable(xmcda, errors);
        checkAlternatives(xmcda, errors);
        checkCriteria(xmcda, errors);
        checkCriteriaScales(xmcda, errors);
        checkCategories(xmcda, errors);
        checkPreferences(xmcda, errors);
        checkParameters(xmcda, errors);
    }

    private static void checkPerformanceTable(XMCDA xmcda, ProgramExecutionResult errors) {
        if (xmcda.performanceTablesList.size() == 0) {
            errors.addError("No performance table has been supplied");
        } else if (xmcda.performanceTablesList.size() > 1) {
            errors.addError("More than one performance table has been supplied");
        } else {
            PerformanceTable<?> p = xmcda.performanceTablesList.get(0);
            if (p.hasMissingValues()) {
                errors.addError("The performance table has missing values");
            }
        }
    }

    private static void checkAlternatives(XMCDA xmcda, ProgramExecutionResult errors) {
        if (xmcda.alternatives.isEmpty()) {
            errors.addError("No alternatives list has been supplied.");
        }
    }

    private static void checkCategories(XMCDA xmcda, ProgramExecutionResult errors) {
        if (xmcda.categories.isEmpty()) {
            errors.addError("No categories has been supplied.");
        } else if (xmcda.categories.getActiveCategories().isEmpty()) {
            errors.addError("All categories are inactive.");
        }
    }

    private static void checkCriteria(XMCDA xmcda, ProgramExecutionResult errors) {
        if (xmcda.criteria.isEmpty()) {
            errors.addError("No criteria list has been supplied.");
        } else if (xmcda.criteria.getActiveCriteria().isEmpty()) {
            errors.addError("All criteria are inactive.");
        }
    }

    private static void checkCriteriaScales(XMCDA xmcda, ProgramExecutionResult errors) {
        if (xmcda.criteriaScalesList.isEmpty()) {
            errors.addError("No criteria list has been supplied.");
        } else if (xmcda.criteriaScalesList.size() > 1) {
            errors.addError("More than one criteriaScales list has been supplied.");
        }
    }

    private static void checkPreferences(XMCDA xmcda, ProgramExecutionResult errors) {
        if (xmcda.alternativesMatricesList.size() != 2) {
            errors.addError("Two preferences lists required");
        }
    }

    private static void checkParameters(XMCDA xmcda, ProgramExecutionResult errors) {
        if (xmcda.programParametersList.isEmpty()) {
            errors.addError("No parameters list has been supplied.");
        } else if (xmcda.programParametersList.size() > 1) {
            errors.addError("More than one parameters list has been supplied.");
        } else if (xmcda.programParametersList.get(0).size() != 2)
            errors.addError("Parameters list must contain two elements: consistencyMeasure and ConsistencyThreshold");
    }
}
