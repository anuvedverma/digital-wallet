import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Anuved on 11/5/2016.
 *
 * @Class PaymoDriver
 * Main class to start program from terminal/bash.
 */
public class PaymoDriver {

    /* Main method: handles 5 arguments:
     * @args[0]: input batch payment file for initializing graph
     * @args[1]: input stream payment file for transaction analysis
     * @args[2]: output file with transaction results for Feature 1 (first degree distance)
     * @args[3]: output file with transaction results for Feature 2 (second degree distance)
     * @args[4]: output file with transaction results for Feature 3 (fourth degree distance)
     * @args[5]: output file with transaction results for Feature 4 (message analysis for targeted ads)
      * */
    public static void main(String[] args) {

        // get input files from args
        File batchPaymentsFile = new File(args[0]);
        File streamPaymentsFile = new File(args[1]);

        // create new fraud detector
        PaymoFraudDetector pfd = new PaymoFraudDetector();
        try {
            // initialize graph with file from arg0, analyze stream from arg1, output to arg2
            pfd.initGraph(batchPaymentsFile);
            pfd.analyzeStream(streamPaymentsFile, new File(args[2]), Feature.FEATURE_ONE);

            // initialize graph with file from arg0, analyze stream from arg1, output to arg3
            pfd.initGraph(batchPaymentsFile);
            pfd.analyzeStream(streamPaymentsFile, new File(args[3]), Feature.FEATURE_TWO);

            // initialize graph with file from arg0, analyze stream from arg1, output to arg4
            pfd.initGraph(batchPaymentsFile);
            pfd.analyzeStream(streamPaymentsFile, new File(args[4]), Feature.FEATURE_THREE);

            // initialize graph with file from arg0, analyze stream from arg1, output to arg5
            pfd.initGraph(batchPaymentsFile);
            pfd.analyzeStream(streamPaymentsFile, new File(args[5]), Feature.FEATURE_FOUR);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UninitializedGraphException e) {
            e.printStackTrace();
        }
    }
}
