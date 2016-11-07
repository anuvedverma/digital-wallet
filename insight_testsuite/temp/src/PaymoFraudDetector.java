import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Anuved on 11/5/2016.
 */
public class PaymoFraudDetector {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private static final String FILE_SEPARATOR = ",";

	private PaymoGraph mPaymoGraph;
    private File mOutputFile;

	public void initGraph(File batchPaymentsFile) throws FileNotFoundException {
        mPaymoGraph = new PaymoGraph();

        BufferedReader batchFileReader = new BufferedReader(new FileReader(batchPaymentsFile));
		String line;

        try {
            line = batchFileReader.readLine(); // skip header

            PaymoTransaction paymoTransaction = new PaymoTransaction();
            while((line = batchFileReader.readLine()) != null) {
                try {
                    // parse transaction
                    paymoTransaction = parsePaymoTransaction(line);
                    System.out.println("# " + paymoTransaction.getMessage());

                    // verify transaction
                    paymoTransaction.setVerified(true);
                    mPaymoGraph.addTransaction(paymoTransaction);
                } catch (TransactionParseException e) {
//                    e.printStackTrace();
//                    System.out.println(e.getUnparsedMessage());
                    paymoTransaction.updateMessage(e.getUnparsedMessage());
                }
			}

            // close buffered reader
            batchFileReader.close();

        } catch (IOException e) {
			e.printStackTrace();
		}
    }

    public void analyzeStream(File streamPaymentsFile, File outputFile) throws FileNotFoundException, UninitializedGraphException {
        analyzeStream(streamPaymentsFile, outputFile, Feature.FEATURE_ONE);
    }

    public void analyzeStream(File streamPaymentsFile, File outputFile, Feature featureLevel) throws FileNotFoundException, UninitializedGraphException {

        if(mPaymoGraph == null)
            throw new UninitializedGraphException();

        mOutputFile = outputFile;

        int firstDegree = 1;
        int secondDegree = 2;
        int fourthDegree = 4;

        BufferedReader streamFileReader = new BufferedReader(new FileReader(streamPaymentsFile));
        String line;
        try {
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(mOutputFile));

            line = streamFileReader.readLine(); // skip header

            PaymoTransaction paymoTransaction = new PaymoTransaction();
            while((line = streamFileReader.readLine()) != null) {

                try {
                    // parse transaction
                    paymoTransaction = parsePaymoTransaction(line);
                    System.out.println("* " + paymoTransaction.getMessage());
                    PaymoUser user1 = paymoTransaction.getPaymoUser1();
                    PaymoUser user2 = paymoTransaction.getPaymoUser2();
                    mPaymoGraph.addVertex(user1);
                    mPaymoGraph.addVertex(user2);

                    switch (featureLevel) {
                        case FEATURE_ONE:
                            if(mPaymoGraph.withinDegree(user1, user2, firstDegree))
                                paymoTransaction.setVerified(true);
                            break;
                        case FEATURE_TWO:
                            if(mPaymoGraph.withinDegree(user1, user2, secondDegree))
                                paymoTransaction.setVerified(true);
                            break;
                        case FEATURE_THREE:
                            if(mPaymoGraph.withinDegree(user1, user2, fourthDegree))
                                paymoTransaction.setVerified(true);
                            break;
                        default:
                            if(mPaymoGraph.withinDegree(user1, user2, 1))
                                paymoTransaction.setVerified(true);
                            break;
                    }

                    // write to output file
                    if(paymoTransaction.isVerified())
                        outputWriter.write("trusted" + "\n");
                    else outputWriter.write("unverified" + "\n");

                    // update graph
                    mPaymoGraph.addTransaction(paymoTransaction);

                } catch (TransactionParseException e) {
//                    e.printStackTrace();
//                    System.out.println(e.getUnparsedMessage());
                    paymoTransaction.updateMessage(e.getUnparsedMessage());
                }
            }

            // flush and close buffered readers/writers
            outputWriter.flush();
            outputWriter.close();
            streamFileReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PaymoGraph getPaymoGraph() {
        return mPaymoGraph;
    }


    // parse transaction
    public static PaymoTransaction parsePaymoTransaction(String input) throws TransactionParseException {

        if(!validateLine(input))
            throw new TransactionParseException(input);

        String[] lineArr = input.split(FILE_SEPARATOR);

        Date timestamp;
        try {
            timestamp = DATE_FORMAT.parse(lineArr[0].trim());
        } catch (ParseException e) {
            e.printStackTrace();
            timestamp = new Date(0);
        }

        PaymoUser user1 = new PaymoUser(Integer.parseInt(lineArr[1].trim()));
        PaymoUser user2 = new PaymoUser(Integer.parseInt(lineArr[2].trim()));
        Double amount = Double.parseDouble(lineArr[3].trim());

        String message = "";
        for (int i = 4; i < lineArr.length; i++)
            message += lineArr[i] + FILE_SEPARATOR;
        message = message.trim().substring(0, message.length()-2);

        return new PaymoTransaction(timestamp, user1, user2, amount, message);
    }

    public static boolean validateLine(String input) {
        Pattern pattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}), (\\d+), (\\d+), (\\d+.\\d{2}), (.*)");
        return pattern.matcher(input).find();
    }

    public File getOutputFile() {
        return mOutputFile;
    }

    public SimpleDateFormat getDateFormat() {
        return DATE_FORMAT;
    }
}
