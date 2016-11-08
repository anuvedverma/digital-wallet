import java.util.Date;

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

    private static PaymoTransaction mLastTransaction;

    /* Parsed data from incoming transaction includes timestamp,
    the two users between whom the transaction occured, the transaction amount,
    and the message provided by the payer in the transaction */
    private Date mTimestamp;
    private PaymoUser mPaymoUser1;
    private PaymoUser mPaymoUser2;
    private Double mTransactionAmount;
    private String mMessage;

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
    }

    /* Constructor to handle Paymo Transactions from stream payments (default unverified) */
    public PaymoTransaction(Date timestamp, PaymoUser paymoUser1, PaymoUser paymoUser2,
                            Double transactionAmount, String message) {
        mTimestamp = timestamp;
        mPaymoUser1 = paymoUser1;
        mPaymoUser2 = paymoUser2;
        mTransactionAmount = transactionAmount;
        mMessage = message;
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

    /* Setter */
    public void setVerified(Boolean verified) {
        mVerified = verified;
    }
}
