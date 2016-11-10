import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Anuved on 11/5/2016.
 *
 * @Class PaymoFraudDetector
 * This class handles the overall logic for the application including file I/O, parsing,
 * exception handling, etc.
 *
 * Stores the current state of the graph, as well as the output file in case needed for downstream logic.
 */
public class PaymoFraudDetector {

	/******************************* MEMBER VARIABLES ************************************************/

	/* Constants used for parsing input data */
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private static final String FILE_SEPARATOR = ",";

    /* Constants for text written to output file */
    private static final String TRUSTED = "trusted";
    private static final String UNVERIFIED = "unverified";

    private static final String TRANSPORTATION = "transportation";
    private static final String FOOD = "food/groceries";
    private static final String PARTY = "party/drinks";
    private static final String CLOTHING = "clothing";
    private static final String MISC = "miscellaneous";


    /* Determines how many degrees to span user network analysis depending on which feature level was requested */
    private static final int FEATURE_ONE_DEGREES = 1;
    private static final int FEATURE_TWO_DEGREES = 2;
    private static final int FEATURE_THREE_DEGREES = 4;

	/* File to which output will be written (tracked mostly just for downstream analysis, testing, etc.) */
	private File mOutputFile;

	/* Tracks current state of entire user network */
	private PaymoGraph mPaymoGraph;

	/***********************************************************************************************************/


	/****************************************** CLASS FUNCTIONS ************************************************/

	/* Initializes graph based on batch payments input file */
	public void initGraph(File batchPaymentsFile) throws FileNotFoundException {

		mPaymoGraph = new PaymoGraph();

		// prepare buffered reader to read batch payment file
		BufferedReader batchFileReader = new BufferedReader(new FileReader(batchPaymentsFile));
		String line;

		try {
			line = batchFileReader.readLine(); // skip header

			// new transaction object
			PaymoTransaction paymoTransaction = new PaymoTransaction();
			while((line = batchFileReader.readLine()) != null) {
				try {
					// parse transaction
					paymoTransaction = parsePaymoTransaction(line);
//					 System.out.println("# " + paymoTransaction.getMessage());

					// verify transaction
					paymoTransaction.setVerified(true);
					mPaymoGraph.addTransaction(paymoTransaction);

				}
				// if we get un-parseable line (ie. message that spans multiple lines), add line to previous transaction
				catch (TransactionParseException e) {
					paymoTransaction.updateMessage(e.getUnparsedMessage());
				}
			}

			// close buffered reader
			batchFileReader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


    /* Analyzes input stream of payments to determine */
	public void analyzeStream(File streamPaymentsFile, File outputFile, Feature featureLevel) throws FileNotFoundException, UninitializedGraphException {

		if(mPaymoGraph == null)
			throw new UninitializedGraphException();

        // save output file as class member so it can be retrieved later
        mOutputFile = outputFile;

        // prepare buffered reader to read batch payment file
		BufferedReader streamFileReader = new BufferedReader(new FileReader(streamPaymentsFile));
		String line;

        try {
			BufferedWriter outputWriter = new BufferedWriter(new FileWriter(mOutputFile));

			line = streamFileReader.readLine(); // skip header

            // new transaction object
			PaymoTransaction paymoTransaction = new PaymoTransaction();
			while((line = streamFileReader.readLine()) != null) {

				try {
					// parse transaction
					paymoTransaction = parsePaymoTransaction(line);
//					System.out.println("* " + paymoTransaction.getMessage());

                    // add users from transaction to graph (addVertex() ensures we're not adding existing members)
                    PaymoUser user1 = paymoTransaction.getPaymoUser1();
					PaymoUser user2 = paymoTransaction.getPaymoUser2();
					mPaymoGraph.addVertex(user1);
					mPaymoGraph.addVertex(user2);

                    /* set transaction verification status to true only if user1 and user2
                        are within X degrees from each other in the network */
					switch (featureLevel) {
						case FEATURE_ONE:
							if(mPaymoGraph.withinDegree(user1, user2, FEATURE_ONE_DEGREES))
								paymoTransaction.setVerified(true);
							break;
						case FEATURE_TWO:
							if(mPaymoGraph.withinDegree(user1, user2, FEATURE_TWO_DEGREES))
								paymoTransaction.setVerified(true);
							break;
						case FEATURE_THREE:
							if(mPaymoGraph.withinDegree(user1, user2, FEATURE_THREE_DEGREES))
								paymoTransaction.setVerified(true);
							break;
						default:
							if(mPaymoGraph.withinDegree(user1, user2, 1))
								paymoTransaction.setVerified(true);
							break;
					}

					// write to output file
					if(paymoTransaction.isVerified())
						outputWriter.write(TRUSTED + "\n");
					else outputWriter.write(UNVERIFIED + "\n");

					// update graph
					mPaymoGraph.addTransaction(paymoTransaction);
				}

                // if we get un-parseable line (ie. message that spans multiple lines), add line to previous transaction
                catch (TransactionParseException e) {
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


	/* Validates and parses a line of Paymo transaction data */
	public static PaymoTransaction parsePaymoTransaction(String input) throws TransactionParseException {

        // if data is in invalid format, throw exception
		if(!validateLine(input))
			throw new TransactionParseException(input);

        // parse transaction data
		String[] lineArr = input.split(FILE_SEPARATOR);
        String timestampString = lineArr[0].trim();
        String user1String = lineArr[1].trim();
        String user2String = lineArr[2].trim();
        String amountString = lineArr[3].trim();

        // parse message
        String messageString = "";
        for (int i = 4; i < lineArr.length; i++) // remainder of line goes into message, even if it contains commas
            messageString += lineArr[i] + FILE_SEPARATOR; // add deleted file separator back in

        String message = messageString.trim();
        message = message.substring(0, message.length()-1); // remove last comma added from above for-loop


        // convert parsed Strings into appropriate objects
		Date timestamp;
		try {
			timestamp = DATE_FORMAT.parse(timestampString);
		} catch (ParseException e) {
			e.printStackTrace();
			timestamp = new Date(0);
		}

		PaymoUser user1 = new PaymoUser(Integer.parseInt(user1String));
		PaymoUser user2 = new PaymoUser(Integer.parseInt(user2String));
		Double amount = Double.parseDouble(amountString);

		return new PaymoTransaction(timestamp, user1, user2, amount, message);
	}

    /* Validates input line by matching with RegEx */
	public static boolean validateLine(String input) {
		Pattern pattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}), (\\d+), (\\d+), (\\d+.\\d{2}), (.*)");
		return pattern.matcher(input).find();
	}


    /* Getters */
    public PaymoGraph getPaymoGraph() {
        return mPaymoGraph;
    }

	public File getOutputFile() {
		return mOutputFile;
	}

	public SimpleDateFormat getDateFormat() {
		return DATE_FORMAT;
	}

    /* Setters */
    public void setOutputFile(File outputFile) { mOutputFile = outputFile; }
}
