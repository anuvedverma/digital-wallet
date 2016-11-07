import java.util.Date;

/**
 * Created by Anuved on 11/5/2016.
 */
public class PaymoTransaction {

    private static PaymoTransaction mLastTransaction;

    // declare member variables
    private Date mTimestamp;
    private PaymoUser mPaymoUser1;
    private PaymoUser mPaymoUser2;
    private Double mTransactionAmount;
    private String mMessage;
    private Boolean mVerified;

    /* Constructor to handle Paymo Transactions from Batch */
    public PaymoTransaction(Date timestamp, PaymoUser paymoUser1, PaymoUser paymoUser2,
                            Double transactionAmount, String message, Boolean verified) {
        mTimestamp = timestamp;
        mPaymoUser1 = paymoUser1;
        mPaymoUser2 = paymoUser2;
        mTransactionAmount = transactionAmount;
        mMessage = message;
        mVerified = verified;
    }

    /* Constructor to handle Paymo Transactions from Stream */
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
