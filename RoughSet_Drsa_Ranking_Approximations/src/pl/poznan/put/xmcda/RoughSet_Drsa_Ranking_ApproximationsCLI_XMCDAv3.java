package pl.poznan.put.xmcda;

import org.xmcda.ProgramExecutionResult;
import org.xmcda.XMCDA;
import pl.poznan.put.roughset.Ranking;
import pl.poznan.put.roughset.RankingResult;

import java.io.File;
import java.util.Map;

public class RoughSet_Drsa_Ranking_ApproximationsCLI_XMCDAv3 {

    public static void main(String[] args) throws Utils.InvalidCommandLineException {

        final Utils.Arguments params = Utils.parseCmdLineArguments(args);

        final String indir = params.inputDirectory;
        final String outdir = params.outputDirectory;

        final File prgExecResults = new File(outdir, "messages.xml");

        final ProgramExecutionResult executionResult = new ProgramExecutionResult();

        final XMCDA xmcda = new XMCDA();

        loadFiles(indir, executionResult, xmcda);

        if (!isExecutionSuccessful(executionResult)) {
            Utils.writeProgramExecutionResultsAndExit(prgExecResults, executionResult, Utils.XMCDA_VERSION.v3);
        }

        Ranking ranking = InputsHandler.checkAndExtractInputs(xmcda, executionResult);

        if (!(isExecutionSuccessful(executionResult)) || ranking == null) {
            Utils.writeProgramExecutionResultsAndExit(prgExecResults, executionResult, Utils.XMCDA_VERSION.v3);
            return;
        }
        RankingResult rankingResult = ranking.generate();

        Map<String, XMCDA> xmcdaResults = OutputsHandler.convert(rankingResult);

        final org.xmcda.parsers.xml.xmcda_v3.XMCDAParser parser = new org.xmcda.parsers.xml.xmcda_v3.XMCDAParser();

        for (String key : xmcdaResults.keySet()) {
            File outputFile = new File(outdir, String.format("%s.xml", key));
            try {
                parser.writeXMCDA(xmcdaResults.get(key), outputFile, OutputsHandler.xmcdaV3Tag(key));
            } catch (Throwable throwable) {
                final String err = String.format("Error while writing %s.xml, reason: ", key);
                executionResult.addError(Utils.getMessage(err, throwable));
                outputFile.delete();
            }
        }
        Utils.writeProgramExecutionResultsAndExit(prgExecResults, executionResult, Utils.XMCDA_VERSION.v3);
    }

    private static void loadFiles(String indir, ProgramExecutionResult executionResult, XMCDA xmcda) {
        Utils.loadXMCDAv3(xmcda, new File(indir, "categories.xml"), true,
                executionResult, "categories");
        Utils.loadXMCDAv3(xmcda, new File(indir, "alternatives.xml"), true,
                executionResult, "alternatives");
        Utils.loadXMCDAv3(xmcda, new File(indir, "preferences_S.xml"), true,
                executionResult, "alternativesMatrix");
        Utils.loadXMCDAv3(xmcda, new File(indir, "preferences_Sc.xml"), true,
                executionResult, "alternativesMatrix");
        Utils.loadXMCDAv3(xmcda, new File(indir, "criteria.xml"), true,
                executionResult, "criteria");
        Utils.loadXMCDAv3(xmcda, new File(indir, "criteria_scales.xml"), true,
                executionResult, "criteriaScales");
        Utils.loadXMCDAv3(xmcda, new File(indir, "performance_table.xml"), true,
                executionResult, "performanceTable");
        Utils.loadXMCDAv3(xmcda, new File(indir, "parameters.xml"), true,
                executionResult, "programParameters");
    }

    private static boolean isExecutionSuccessful(ProgramExecutionResult executionResult) {
        return executionResult.isOk() || executionResult.isWarning();
    }
}
