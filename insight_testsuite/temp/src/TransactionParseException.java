import java.text.ParseException;

/**
 * Created by Anuved on 11/6/2016.
 *
 * @ClassException TransactionParseException
 * Exception thrown if Paymo an input stream payment line is unparseable.
 * Throws exception, as well as the stream payment line that caused the exception.
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
