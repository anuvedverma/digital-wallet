import java.io.*;

/**
 * Created by Anuved on 11/5/2016.
 *
 * @Class PaymoDriverNoArgs
 * Main class to start program from IDE for testing purposes. NOT TO BE RUN FROM TERMINAL/BASH.
 */
public class PaymoDriverNoArgs {

    public static void main(String[] args) {

        /* Hard-coded input files for quick testing */
        File batchPaymentsFile = new File("paymo_input/batch_payment_test.txt");
        File streamPaymentsFile = new File("paymo_input/stream_payment_test.txt");
//        File batchPaymentsFile = new File("paymo_input/batch_payment_test.txt");
//        File streamPaymentsFile = new File("paymo_input/stream_payment_badinput.txt");
//        File batchPaymentsFile = new File("paymo_input/batch_payment.txt");
//        File streamPaymentsFile = new File("paymo_input/stream_payment.txt");

        // create new fraud detector
        PaymoFraudDetector pfd = new PaymoFraudDetector();
        try {
            // initialize graph with batchPaymentsFile, analyze stream from streamPaymentsFile, output new file
            pfd.initGraph(batchPaymentsFile);
            pfd.analyzeStream(streamPaymentsFile, new File("paymo_output/output1.txt"), Feature.FEATURE_ONE);

            // initialize graph with batchPaymentsFile, analyze stream from streamPaymentsFile, output new file
            pfd.initGraph(batchPaymentsFile);
            pfd.analyzeStream(streamPaymentsFile, new File("paymo_output/output2.txt"), Feature.FEATURE_TWO);

            // initialize graph with batchPaymentsFile, analyze stream from streamPaymentsFile, output new file
            pfd.initGraph(batchPaymentsFile);
            pfd.analyzeStream(streamPaymentsFile, new File("paymo_output/output3.txt"), Feature.FEATURE_THREE);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UninitializedGraphException e) {
            e.printStackTrace();
        }
    }
}
