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
    public void testFeature4_1() throws TransactionParseException {
        String message = "2016-11-02 09:59:42, 4, 1, 10.51, River. Bus. Beers. What a great time, isn't it?";
        PaymoTransaction paymoTransaction = PaymoFraudDetector.parsePaymoTransaction(message);

        assert paymoTransaction.isTransportationType() == true;
        assert paymoTransaction.isPartyType() == true;
    }

    @Test
    public void testFeature4_2() throws TransactionParseException {
        String message = "2016-11-03 09:58:07, 4, 6, 39.09, \uD83C\uDF7A\uD83C\uDF7A\uD83C\uDF7A\uD83C\uDFCC\uD83C\uDFCC\uD83C\uDFCC";
        PaymoTransaction paymoTransaction = PaymoFraudDetector.parsePaymoTransaction(message);

        assert paymoTransaction.isPartyType() == true;
    }

    @Test
    public void testFeature4_3() throws TransactionParseException {
        String message = "2016-11-01 17:49:26, 49466, 00000, 25.32, Uber";
        PaymoTransaction paymoTransaction = PaymoFraudDetector.parsePaymoTransaction(message);

        assert paymoTransaction.isTransportationType() == true;
    }

    @Test
    public void testFeature4_4() throws TransactionParseException {
        String message = "2016-11-01 17:49:26, 6989, 00000, 25.32, dinner";
        PaymoTransaction paymoTransaction = PaymoFraudDetector.parsePaymoTransaction(message);

        assert paymoTransaction.isFoodType() == true;
    }
}

//