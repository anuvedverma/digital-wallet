import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by Anuved on 11/5/2016.
 *
 *  @Class PaymoTransaction
 * This class captures information about a Paymo transaction between two users.
 *
 * A PaymoTransaction object stores the parsed data from an incoming transaction,
 * as well whether the transaction was valid or not.
 */
public class PaymoTransaction {

	/* Parsed data from incoming transaction includes timestamp,
	the two users between whom the transaction occured, the transaction amount,
	and the message provided by the payer in the transaction */
	private Date mTimestamp;
	private PaymoUser mPaymoUser1;
	private PaymoUser mPaymoUser2;
	private Double mTransactionAmount;
	private String mMessage;

	// stores the types of transaction types a particular transaction qualifies as
	private HashSet<TransactionType> mTransactionTypes;

	/* Hashsets that define certain keywords/emojis to be used for recognizing the transaction type */
	private static HashSet<String> TRANSPORTATION_KEYWORDS = transportationKeywords();
	private static HashSet<String> FOOD_KEYWORDS = foodKeywords();
	private static HashSet<String> PARTY_KEYWORDS = partyKeywords();
	private static HashSet<String> CLOTHING_KEYWORDS = clothingKeywords();

	/* Keeps track of whether the transaction is verified (default false) */
	private Boolean mVerified;

	/* Constructor to handle Paymo Transactions from batch payments (allows pre-set verification status) */
	public PaymoTransaction(Date timestamp, PaymoUser paymoUser1, PaymoUser paymoUser2,
							Double transactionAmount, String message, Boolean verified) {
		mTimestamp = timestamp;
		mPaymoUser1 = paymoUser1;
		mPaymoUser2 = paymoUser2;
		mTransactionAmount = transactionAmount;
		mMessage = message;
		mVerified = verified;
		mTransactionTypes = analyzeTransactionTypes(message);
	}

	/* Constructor to handle Paymo Transactions from stream payments (default unverified) */
	public PaymoTransaction(Date timestamp, PaymoUser paymoUser1, PaymoUser paymoUser2,
							Double transactionAmount, String message) {
		mTimestamp = timestamp;
		mPaymoUser1 = paymoUser1;
		mPaymoUser2 = paymoUser2;
		mTransactionAmount = transactionAmount;
		mMessage = message;
		mTransactionTypes = analyzeTransactionTypes(message);
		mVerified = false;
	}

	public PaymoTransaction() {}

	/* Getters */
	public Date getTimestamp() {
		return mTimestamp;
	}

	public PaymoUser getPaymoUser1() {
		return mPaymoUser1;
	}

	public PaymoUser getPaymoUser2() {
		return mPaymoUser2;
	}

	public Double getTransactionAmount() {
		return mTransactionAmount;
	}

	public String getMessage() { return mMessage; }

	public void updateMessage(String message) { mMessage += "\n" + message; }

	public Boolean isVerified() {
		return mVerified;
	}

    public Collection<TransactionType> getTransactionTypes() { return mTransactionTypes; }

    public Boolean isTransportationType() { return mTransactionTypes.contains(TransactionType.TRANSPORTATION); }

    public Boolean isFoodType() { return mTransactionTypes.contains(TransactionType.FOOD); }

    public Boolean isPartyType() { return mTransactionTypes.contains(TransactionType.PARTY); }

    public Boolean isMiscType() { return mTransactionTypes.contains(TransactionType.MISC); }

    public String getTransactionTypesAsString() {
        String output = "";
        for(TransactionType transactionType : mTransactionTypes)
            output += transactionType.toString() + ", ";

        output = output.trim();
        output = output.substring(0, output.length()-1);
        return output;
    }

    /* Setter */
	public void setVerified(Boolean verified) {
		mVerified = verified;
	}

    public void setTransactionTypes(HashSet<TransactionType> transactionTypes) { mTransactionTypes = transactionTypes; }

    /* Analyze message to determine what categories (TransactionType) this transaction fits in.
    * Use shingles of sizes 2 - 6 to find keyword/lemma matches for transaction types.
    * Will likely result in many false positive and negatives-- that's okay for our purposes as this is a proof of concept.
    * */
    private HashSet<TransactionType> analyzeTransactionTypes(String input) {
        String message = input.toLowerCase().replaceAll("\\p{P}", ""); // remove all punctuation and standardize case
//		String[] words = message.split(" ");

		// define shingle sizes
		int k2 = 2;
		int k3 = 3;
		int k4 = 4;
		int k5 = 5;
		int k6 = 6;
		int[] shingleSizes = {k2, k3, k4, k5, k6};

		// init
		HashSet<TransactionType> transactionTypes = new HashSet<>();
		String shingle = "";

		// iterate through all shingle sizes
		for (int i = 0; i < shingleSizes.length; i++) {
			int shingleSize = shingleSizes[i];
			for (int j = 0; j + shingleSize <= message.length(); j++) {
				// extract shingle from string
				shingle = message.substring(j, j + shingleSize);

				// test shingle against keyword hashsets for membership
				if(TRANSPORTATION_KEYWORDS.contains(shingle))
					transactionTypes.add(TransactionType.TRANSPORTATION);
				if(FOOD_KEYWORDS.contains(shingle))
					transactionTypes.add(TransactionType.FOOD);
				if(PARTY_KEYWORDS.contains(shingle))
					transactionTypes.add(TransactionType.PARTY);
				if(CLOTHING_KEYWORDS.contains(shingle))
					transactionTypes.add(TransactionType.CLOTHING);
			}
		}

		if(transactionTypes.isEmpty())
			transactionTypes.add(TransactionType.MISC);

        return transactionTypes;
    }

	/* Define keywords to detect transportation-related transaction messages */
	private static HashSet<String> transportationKeywords() {
		HashSet<String> transportationKeywords = new HashSet<>();
		transportationKeywords.add("uber");
		transportationKeywords.add("lyft");
		transportationKeywords.add("taxi");
		transportationKeywords.add("bus");
		transportationKeywords.add("gas");
		transportationKeywords.add("hotel");
		transportationKeywords.add("motel");
		transportationKeywords.add("ride ");
		transportationKeywords.add("\uD83D\uDE95"); // taxi
		return transportationKeywords;
	}

	/* Define keywords and emojis to detect food/restaurant-related transaction messages */
	private static HashSet<String> foodKeywords() {
		HashSet<String> foodKeywords = new HashSet<>();
		foodKeywords.add("food");
		foodKeywords.add("grocer");
		foodKeywords.add("dinner");
		foodKeywords.add("lunch");
		foodKeywords.add("breakf");
		foodKeywords.add("pizz");
		foodKeywords.add("\uD83C\uDF54"); // burger
		foodKeywords.add("\uD83C\uDF55"); // pizza slice
		foodKeywords.add("\uD83C\uDF57"); // poultry leg
		foodKeywords.add("\uD83C\uDF5F"); // fries
		return foodKeywords;
	}

	/* Define keywords and emojis to detect party/drinking-related transaction messages */
	private static HashSet<String> partyKeywords() {
		HashSet<String> partyKeywords = new HashSet<>();
		partyKeywords.add("drink");
		partyKeywords.add("drank");
		partyKeywords.add("beers");
		partyKeywords.add("alcoho");
		partyKeywords.add("liquor");
		partyKeywords.add("party");
		partyKeywords.add("club");
		partyKeywords.add("\uD83C\uDF7A"); // beer
		partyKeywords.add("\uD83C\uDF7B"); // clinking beer
		partyKeywords.add("\uD83C\uDF77"); // wine
		partyKeywords.add("\uD83C\uDF78"); // cocktail
		partyKeywords.add("\uD83D\uDC83"); // dance
		return partyKeywords;
	}

	/* Define keywords and emojis to detect clothing-related transaction messages */
	private static HashSet<String> clothingKeywords() {
		HashSet<String> clothingKeywords = new HashSet<>();
		clothingKeywords.add("cloth");
		clothingKeywords.add("shirt");
		clothingKeywords.add("pant");
		clothingKeywords.add("dress");
		clothingKeywords.add("\uD83D\uDC55"); // shirt
		return clothingKeywords;
	}

}
