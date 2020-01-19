package pl.poznan.put.xmcda;

import org.xmcda.*;
import pl.poznan.put.roughset.ClassificationResult;

import java.util.HashMap;
import java.util.Map;

public class OutputsHandler {

    public static String xmcdaV3Tag(String outputName) {
        if (outputName.equals("messages")) {
            return "programExecutionResult";
        }
        return outputName;
    }

    public static String xmcdaV2Tag(String outputName) {
        if (outputName.equals("messages")) {
            return "methodMessages";
        }
        return outputName;
    }

    public static Map<String, XMCDA> convert(ClassificationResult result) {
        final HashMap<String, XMCDA> xmcdaResults = new HashMap<>();

        XMCDA xmcdaIndiscernibility = new XMCDA();
        xmcdaIndiscernibility.granules.addAll(result.getXmcdaIndiscernibilityClasses());
        xmcdaResults.put(Granules.TAG, xmcdaIndiscernibility);

        XMCDA xmcdaApproximations = new XMCDA();
        xmcdaApproximations.approximations.addAll(result.getXmcdaApproximations());
        xmcdaResults.put(Approximations.TAG, xmcdaApproximations);

        XMCDA xmcdaReducts = new XMCDA();
        xmcdaReducts.criteriaSets = result.getXmcdaReducts();
        xmcdaResults.put(CriteriaSets.TAG, xmcdaReducts);

        XMCDA xmcdaQualityOfApprox = new XMCDA();
        xmcdaQualityOfApprox.programParametersList.add(result.getXmcdaQualityOfApprox());
        xmcdaResults.put(ProgramParameters.TAG, xmcdaQualityOfApprox);

        return xmcdaResults;
    }
}