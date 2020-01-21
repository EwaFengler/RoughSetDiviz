package pl.poznan.put.xmcda;

import java.util.ArrayList;
import java.util.Arrays;

public class RoughSet_Drsa_Sorting_Approximations {
    public static void main(String[] args) throws Exception {
        final ArrayList<String> argsList = new ArrayList<>(Arrays.asList(args));
        if (argsList.remove("--v2")) {
            RoughSet_Drsa_Sorting_ApproximationsCLI_XMCDAv2.main(argsList.toArray(new String[]{}));
        } else if (argsList.remove("--v3")) {
            RoughSet_Drsa_Sorting_ApproximationsCLI_XMCDAv3.main(argsList.toArray(new String[]{}));
        } else {
            System.err.println("missing mandatory option --v2 or --v3");
            System.exit(-1);
        }
    }
}
