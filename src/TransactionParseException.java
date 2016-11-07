import java.text.ParseException;

/**
 * Created by Anuved on 11/6/2016.
 */
public class TransactionParseException extends Exception {

    private String mUnparsedMessage;

    public TransactionParseException(String message) {
        mUnparsedMessage = message;
    }

    public String getUnparsedMessage() {
        return mUnparsedMessage;
    }
}
