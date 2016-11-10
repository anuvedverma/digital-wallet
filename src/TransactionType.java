/**
 * Created by Anuved on 11/10/2016.
 */
public enum TransactionType {

    TRANSPORTATION("transportation"),
    FOOD("food-related"),
    PARTY("party-related"),
    CLOTHING("clothing"),
    MISC("miscellaneous");

    private final String mTransactionType;

    private TransactionType(String transactionType) { mTransactionType = transactionType; }

    @Override
    public String toString() {
        return mTransactionType;
    }
}
