package pl.poznan.put.xmcda;

import org.xmcda.PerformanceTable;
import org.xmcda.ProgramExecutionResult;
import org.xmcda.XMCDA;
import pl.poznan.put.roughset.Sorting;

public class InputsHandler {

    public static Sorting checkAndExtractInputs(XMCDA xmcda, ProgramExecutionResult xmcda_exec_result) {
        checkInputs(xmcda, xmcda_exec_result);

        if (xmcda_exec_result.isError()) {
            return null;
        }

        InputsMapper inputsMapper = new InputsMapper(xmcda, xmcda_exec_result);
        return inputsMapper.mapToClassification();
    }

    private static void checkInputs(XMCDA xmcda, ProgramExecutionResult errors) {
        checkPerformanceTable(xmcda, errors);
        checkAlternatives(xmcda, errors);
        checkCriteria(xmcda, errors);
        checkCategories(xmcda, errors);
        checkAssignments(xmcda, errors);
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

    private static void checkCriteria(XMCDA xmcda, ProgramExecutionResult errors) {
        if (xmcda.criteria.isEmpty()) {
            errors.addError("No criteria list has been supplied.");
        } else if (xmcda.criteria.getActiveCriteria().isEmpty()) {
            errors.addError("All criteria are inactive.");
        }
    }

    private static void checkCategories(XMCDA xmcda, ProgramExecutionResult errors) {
        if (xmcda.categories.isEmpty()) {
            errors.addError("No categories have been supplied.");
        } else if (xmcda.categories.getActiveCategories().isEmpty()) {
            errors.addError("All categories are inactive.");
        }
    }

    private static void checkAssignments(XMCDA xmcda, ProgramExecutionResult errors) {
        if (xmcda.alternativesAssignmentsList.size() == 0) {
            errors.addError("No assignments list has been supplied.");
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
