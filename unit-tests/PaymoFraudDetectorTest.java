import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.text.ParseException;

/**
 * Created by Anuved on 11/6/2016.
 */
public class PaymoFraudDetectorTest {

    private PaymoFraudDetector mPaymoFraudDetector;
    private File mBatchPaymentsFile;
    private File mStreamPaymentsFile;

    private File mOutput1;
    private File mOutput2;
    private File mOutput3;

    @Before
    public void initPaymoFraudDetector() throws FileNotFoundException {
        mBatchPaymentsFile = new File("unit-tests/test-2-paymo-trans/paymo_input/batch_payment_test.csv");
        mStreamPaymentsFile = new File("unit-tests/test-2-paymo-trans/paymo_input/stream_payment_test.csv");
        mOutput1 = new File("unit-tests/test-2-paymo-trans/paymo_output/output1.txt");
        mOutput2 = new File("unit-tests/test-2-paymo-trans/paymo_output/output2.txt");
        mOutput3 = new File("unit-tests/test-2-paymo-trans/paymo_output/output3.txt");
        mPaymoFraudDetector = new PaymoFraudDetector();
    }

    @Test
    public void testParsePaymoTransaction() throws ParseException, TransactionParseException {
        String transaction = "2016-11-02 09:38:53, 1, 2, 23.74, \uD83E\uDD84";
        PaymoTransaction paymoTransaction = PaymoFraudDetector.parsePaymoTransaction(transaction);

        assert paymoTransaction.getTimestamp().equals(mPaymoFraudDetector.getDateFormat().parse("2016-11-02 09:38:53"));
        assert paymoTransaction.getPaymoUser1().getUserID() == 1;
        assert paymoTransaction.getPaymoUser2().getUserID() == 2;
        assert paymoTransaction.getTransactionAmount() == 23.74;
        assert paymoTransaction.getMessage().equals("\uD83E\uDD84");
    }

    @Test
    public void testParsePaymoTransactionWithCommas() throws ParseException, TransactionParseException {
        String transaction = "2016-11-02 09:38:53, 1, 2, 23.74, To be, or not to be";
        PaymoTransaction paymoTransaction = PaymoFraudDetector.parsePaymoTransaction(transaction);

        // test commas in message
        assert paymoTransaction.getTimestamp().equals(mPaymoFraudDetector.getDateFormat().parse("2016-11-02 09:38:53"));
        assert paymoTransaction.getPaymoUser1().getUserID() == 1;
        assert paymoTransaction.getPaymoUser2().getUserID() == 2;
        assert paymoTransaction.getTransactionAmount() == 23.74;
        System.out.println(paymoTransaction.getMessage());
        assert paymoTransaction.getMessage().equals("To be, or not to be");
    }

    @Test
    public void testParsePaymoTransactionWithNewLines() throws ParseException, IOException {
        String transaction = "2016-11-02 09:59:42, 27372, 18944, 5.59, Total Due       Cost Split - Michael to Pay     Date to be Paid\n" +
                " JP HOSA Dues paid in August - 35.00     $17.50\n" +
                " CCP Cheer Shoes paid in August - 80.00  $40.00\n" +
                " CCP Frequency paid in August - 215.00   $107.50\n" +
                " SUBTOTAL        $165.00\n" +
                " Susan Paid for Claires contacts $220 the 70/30 rule means Michael owes Susan $145.2 of that amount       Michael Paid Jacks contacts and glasses $363 (30% that is due from Susan is $119.79) - the difference between what were Claires costs and Jacks means that Michael owes Susan $25.41\n" +
                " AMOUNT TO PAID TO SUSAN $355.41         11/6/15 ";

        BufferedReader br = new BufferedReader(new StringReader(transaction));
        PaymoTransaction paymoTransaction = new PaymoTransaction();
        String line;
        while( (line=br.readLine()) != null ) {
            try {
                paymoTransaction = PaymoFraudDetector.parsePaymoTransaction(transaction);
            } catch (TransactionParseException e) {
                paymoTransaction.updateMessage(e.getUnparsedMessage());
            }
        }

        // test commas in message
        assert paymoTransaction.getTimestamp().equals(mPaymoFraudDetector.getDateFormat().parse("2016-11-02 09:59:42, 27372"));
        assert paymoTransaction.getPaymoUser1().getUserID() == 27372;
        assert paymoTransaction.getPaymoUser2().getUserID() == 18944;
        assert paymoTransaction.getTransactionAmount() == 5.59;

        String message = "Total Due       Cost Split - Michael to Pay     Date to be Paid\n" +
                " JP HOSA Dues paid in August - 35.00     $17.50\n" +
                " CCP Cheer Shoes paid in August - 80.00  $40.00\n" +
                " CCP Frequency paid in August - 215.00   $107.50\n" +
                " SUBTOTAL        $165.00\n" +
                " Susan Paid for Claires contacts $220 the 70/30 rule means Michael owes Susan $145.2 of that amount       Michael Paid Jacks contacts and glasses $363 (30% that is due from Susan is $119.79) - the difference between what were Claires costs and Jacks means that Michael owes Susan $25.41\n" +
                " AMOUNT TO PAID TO SUSAN $355.41         11/6/15 ";

        System.out.println(paymoTransaction.getMessage());
        assert paymoTransaction.getMessage().equals(message);
    }


    @Test
    public void testInitGraph() throws FileNotFoundException {
        mPaymoFraudDetector.initGraph(mBatchPaymentsFile);
        PaymoGraph paymoGraph = mPaymoFraudDetector.getPaymoGraph();
        assert paymoGraph.numVertices() == 10;
    }

    @Test
    public void testAnalyzeStreamWithInit() throws IOException, UninitializedGraphException {

        mPaymoFraudDetector.initGraph(mBatchPaymentsFile);
        mPaymoFraudDetector.analyzeStream(mStreamPaymentsFile, mOutput1, Feature.FEATURE_ONE);

        File output = mPaymoFraudDetector.getOutputFile();
        BufferedReader br = new BufferedReader(new FileReader(output));
        String line;
        while((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }

    @Test(expected = UninitializedGraphException.class)
    public void testAnalyzeStreamWithoutInit() throws IOException, UninitializedGraphException {
        mPaymoFraudDetector.analyzeStream(mStreamPaymentsFile, mOutput1, Feature.FEATURE_ONE);
    }

    @Test
    public void testAnalyzeStreamFeatureOne() throws IOException, UninitializedGraphException {
        String trusted = "trusted";
        String unverified = "unverified";

        mPaymoFraudDetector.initGraph(mBatchPaymentsFile);
        mPaymoFraudDetector.analyzeStream(mStreamPaymentsFile,mOutput1, Feature.FEATURE_ONE);

        File output = mPaymoFraudDetector.getOutputFile();
        BufferedReader br = new BufferedReader(new FileReader(output));
        assert br.readLine().equals(unverified);
        assert br.readLine().equals(unverified);
        assert br.readLine().equals(unverified);
        assert br.readLine().equals(unverified);
        assert br.readLine().equals(trusted);
        assert br.readLine().equals(unverified);
        assert br.readLine().equals(unverified);
        assert br.readLine().equals(unverified);
        assert br.readLine().equals(unverified);
        assert br.readLine().equals(unverified);
        assert br.readLine().equals(unverified);
        Assert.assertNull(br.readLine());
    }

    @Test
    public void testAnalyzeStreamFeatureTwo() throws IOException, UninitializedGraphException {
        String trusted = "trusted";
        String unverified = "unverified";

        mPaymoFraudDetector.initGraph(mBatchPaymentsFile);
        mPaymoFraudDetector.analyzeStream(mStreamPaymentsFile, mOutput2, Feature.FEATURE_TWO);

        File output = mPaymoFraudDetector.getOutputFile();
        BufferedReader br = new BufferedReader(new FileReader(output));
        assert br.readLine().equals(unverified);
        assert br.readLine().equals(trusted);
        assert br.readLine().equals(trusted);
        assert br.readLine().equals(unverified);
        assert br.readLine().equals(trusted);
        assert br.readLine().equals(trusted);
        assert br.readLine().equals(trusted);
        assert br.readLine().equals(unverified);
        assert br.readLine().equals(unverified);
        assert br.readLine().equals(trusted);
        assert br.readLine().equals(unverified);
        Assert.assertNull(br.readLine());
    }

    @Test
    public void testAnalyzeStreamFeatureThree() throws IOException, UninitializedGraphException {
        String trusted = "trusted";
        String unverified = "unverified";

        mPaymoFraudDetector.initGraph(mBatchPaymentsFile);
        mPaymoFraudDetector.analyzeStream(mStreamPaymentsFile, mOutput3, Feature.FEATURE_THREE);

        File output = mPaymoFraudDetector.getOutputFile();
        BufferedReader br = new BufferedReader(new FileReader(output));
        assert br.readLine().equals(unverified);
        assert br.readLine().equals(trusted);
        assert br.readLine().equals(trusted);
        assert br.readLine().equals(trusted);
        assert br.readLine().equals(trusted);
        assert br.readLine().equals(trusted);
        assert br.readLine().equals(trusted);
        assert br.readLine().equals(trusted);
        assert br.readLine().equals(trusted);
        assert br.readLine().equals(trusted);
        assert br.readLine().equals(trusted);
        Assert.assertNull(br.readLine());
    }

}
