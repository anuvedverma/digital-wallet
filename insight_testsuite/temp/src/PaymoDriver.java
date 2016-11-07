import java.io.*;

/**
 * Created by Anuved on 11/5/2016.
 */

public class PaymoDriver {

    public static void main(String[] args) {
//        System.out.println(System.getProperty("user.dir"));
//        System.out.println(System.getProperty("paymo_input/batch_payment.txt"));
        File batchPaymentsFile = new File(args[0]);
        File streamPaymentsFile = new File(args[1]);

        PaymoFraudDetector pfd = new PaymoFraudDetector();
        try {
            pfd.initGraph(batchPaymentsFile);
            pfd.analyzeStream(streamPaymentsFile, new File(args[2]), Feature.FEATURE_ONE);

            pfd.initGraph(batchPaymentsFile);
            pfd.analyzeStream(streamPaymentsFile, new File(args[3]), Feature.FEATURE_TWO);

            pfd.initGraph(batchPaymentsFile);
            pfd.analyzeStream(streamPaymentsFile, new File(args[4]), Feature.FEATURE_THREE);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UninitializedGraphException e) {
            e.printStackTrace();
        }
    }
}
