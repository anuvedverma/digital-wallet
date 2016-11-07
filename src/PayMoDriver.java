import java.io.*;

/**
 * Created by Anuved on 11/5/2016.
 */

public class PaymoDriver {

    public static void main(String[] args) {
        File batchPaymentsFile = new File("paymo_input/batch_payment_test.csv");
        File streamPaymentsFile = new File("paymo_input/stream_payment_test.csv");
//        File batchPaymentsFile = new File("paymo_input/batch_payment_test.csv");
//        File streamPaymentsFile = new File("paymo_input/stream_payment_badinput.csv");
//        File batchPaymentsFile = new File("paymo_input/batch_payment.csv");
//        File streamPaymentsFile = new File("paymo_input/stream_payment.csv");

        PaymoFraudDetector pfd = new PaymoFraudDetector();
        try {
            pfd.initGraph(batchPaymentsFile);
            pfd.analyzeStream(streamPaymentsFile, new File("paymo_output/output1.txt"), Feature.FEATURE_ONE);

            pfd.initGraph(batchPaymentsFile);
            pfd.analyzeStream(streamPaymentsFile, new File("paymo_output/output2.txt"), Feature.FEATURE_TWO);

            pfd.initGraph(batchPaymentsFile);
            pfd.analyzeStream(streamPaymentsFile, new File("paymo_output/output3.txt"), Feature.FEATURE_THREE);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UninitializedGraphException e) {
            e.printStackTrace();
        }
    }
}
