import org.junit.Test;

import java.util.HashSet;

/**
 * Created by Anuved on 11/6/2016.
 */
public class PaymoTransactionTest {

    /* Test to check output of getTransactionTypeAsString() */
    @Test
    public void testGetTransactionTypeAsString() {
        HashSet<TransactionType> transactionTypes = new HashSet<>();
        transactionTypes.add(TransactionType.TRANSPORTATION);
        transactionTypes.add(TransactionType.FOOD);
        transactionTypes.add(TransactionType.CLOTHING);
        transactionTypes.add(TransactionType.MISC);

        PaymoTransaction paymoTransaction = new PaymoTransaction();
        paymoTransaction.setTransactionTypes(transactionTypes);

        System.out.println(paymoTransaction.getTransactionTypesAsString());
    }

    /* Test Feature 4: determine transaction type, and trusted up to 3rd degree */
    @Test
    public void testFeature4() throws TransactionParseException {
        String message = "2016-11-02 09:59:42, 4, 1, 10.51, River. Bus. Beers. What a great time, isn't it?";
        PaymoTransaction paymoTransaction = PaymoFraudDetector.parsePaymoTransaction(message);

        assert paymoTransaction.isTransportationType() == true;
        assert paymoTransaction.isPartyType() == true;
    }
}
