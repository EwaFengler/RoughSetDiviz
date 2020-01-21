package pl.poznan.put.xmcda;

import org.xmcda.*;
import pl.poznan.put.roughset.SortingResult;

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

    public static Map<String, XMCDA> convert(SortingResult result) {
        final HashMap<String, XMCDA> xmcdaResults = new HashMap<>();

        XMCDA xmcdaDominanceCones = new XMCDA();
        xmcdaDominanceCones.granules.addAll(result.getXmcdaDominanceCones());
        xmcdaResults.put(Granules.TAG, xmcdaDominanceCones);

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