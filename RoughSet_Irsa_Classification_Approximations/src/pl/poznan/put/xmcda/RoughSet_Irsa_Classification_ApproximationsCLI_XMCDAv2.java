package pl.poznan.put.xmcda;

import org.xmcda.ProgramExecutionResult;
import org.xmcda.XMCDA;
import org.xmcda.converters.v2_v3.XMCDAConverter;
import org.xmcda.parsers.xml.xmcda_v2.XMCDAParser;
import pl.poznan.put.roughset.Classification;
import pl.poznan.put.roughset.ClassificationResult;

import java.io.File;
import java.util.Map;

public class RoughSet_Irsa_Classification_ApproximationsCLI_XMCDAv2 {

    public static void main(String[] args) throws Utils.InvalidCommandLineException {
        final Utils.Arguments params = Utils.parseCmdLineArguments(args);

        final String indir = params.inputDirectory;
        final String outdir = params.outputDirectory;

        final File prgExecResults = new File(outdir, "messages.xml");

        final ProgramExecutionResult executionResult = new ProgramExecutionResult();

        final XMCDA xmcda;

        org.xmcda.v2.XMCDA xmcda_v2 = new org.xmcda.v2.XMCDA();
        Utils.loadXMCDAv2(xmcda_v2, new File(indir, "alternatives.xml"), true,
                executionResult, "alternatives");
        Utils.loadXMCDAv2(xmcda_v2, new File(indir, "categories.xml"), true,
                executionResult, "categories");
        Utils.loadXMCDAv2(xmcda_v2, new File(indir, "assignments.xml"), true,
                executionResult, "alternativesAffectations");
        Utils.loadXMCDAv2(xmcda_v2, new File(indir, "criteria.xml"), true,
                executionResult, "criteria");
        Utils.loadXMCDAv2(xmcda_v2, new File(indir, "performance_table.xml"), true,
                executionResult, "performanceTable");

        if (!isExecutionSuccessful(executionResult)) {
            Utils.writeProgramExecutionResultsAndExit(prgExecResults, executionResult, Utils.XMCDA_VERSION.v2);
        }

        try {
            xmcda = XMCDAConverter.convertTo_v3(xmcda_v2);
        } catch (Throwable t) {
            executionResult.addError(Utils.getMessage("Could not convert inputs to XMCDA v3, reason: ", t));
            Utils.writeProgramExecutionResultsAndExit(prgExecResults, executionResult, Utils.XMCDA_VERSION.v2);
            return;
        }


        final Classification classification = InputsHandler.checkAndExtractInputs(xmcda, executionResult);

        if (!isExecutionSuccessful(executionResult) || classification == null) {
            Utils.writeProgramExecutionResultsAndExit(prgExecResults, executionResult, Utils.XMCDA_VERSION.v2);
            return;
        }

        ClassificationResult classificationResult = classification.generate();

        Map<String, XMCDA> xmcdaResults = OutputsHandler.convert(classificationResult);

        org.xmcda.v2.XMCDA results_v2;
        for (String outputName : xmcdaResults.keySet()) {
            File outputFile = new File(outdir, String.format("%s.xml", outputName));
            try {
                results_v2 = XMCDAConverter.convertTo_v2(xmcdaResults.get(outputName));
                if (results_v2 == null)
                    throw new IllegalStateException("Conversion from v3 to v2 returned a null value");
            } catch (Throwable throwable) {
                final String err = String.format("Could not convert %s into XMCDA_v2, reason: ", outputName);
                executionResult.addError(Utils.getMessage(err, throwable));
                continue;
            }
            try {
                XMCDAParser.writeXMCDA(results_v2, outputFile, OutputsHandler.xmcdaV2Tag(outputName));
            } catch (Throwable throwable) {
                final String err = String.format("Error while writing %s.xml, reason: ", outputName);
                executionResult.addError(Utils.getMessage(err, throwable));
                outputFile.delete();
            }
        }
        Utils.writeProgramExecutionResultsAndExit(prgExecResults, executionResult, Utils.XMCDA_VERSION.v2);
    }

    private static boolean isExecutionSuccessful(ProgramExecutionResult executionResult) {
        return executionResult.isOk() || executionResult.isWarning();
    }
}