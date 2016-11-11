import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;

/**
 * Created by Anuved on 11/6/2016.
 */
public class PaymoGraphTest {

    private PaymoGraph mPaymoGraph;

    /* Init PaymoGraph with batch_payment.csv before each test */
    @Before
    public void initPaymoGraph() throws FileNotFoundException {
        File batchPaymentsFile = new File("unit-tests/test-paymo-trans/paymo_input/batch_payment.csv");

        PaymoFraudDetector pfd = new PaymoFraudDetector();
        pfd.initGraph(batchPaymentsFile);

        mPaymoGraph = pfd.getPaymoGraph();
    }

    /* Test transaction between two new users */
    @Test
    public void testAddTransactionTwoNewUsers() {
        // new transaction
        Date date = new Date(System.currentTimeMillis());
        PaymoUser user1 = new PaymoUser(Integer.MAX_VALUE);
        PaymoUser user2 = new PaymoUser(Integer.MAX_VALUE-1);
        Double amount = new Double(23.74);
        String message = "test";

        int sizeBeforeTransaction = mPaymoGraph.numVertices();

        mPaymoGraph.addTransaction(new PaymoTransaction(date, user1, user2, amount, message));
        int sizeAfterTransaction = mPaymoGraph.numVertices();

        assert (mPaymoGraph.contains(user1));
        assert (mPaymoGraph.contains(user2));
        assert (sizeAfterTransaction == sizeBeforeTransaction + 2);
        assert (mPaymoGraph.getVertex(user1).hasNeighbor(user2));
        assert (mPaymoGraph.getVertex(user2).hasNeighbor(user1));
    }

    /* Test transaction between two existing users */
    @Test
    public void testAddTransactionTwoExistingUsers() {

        // first transaction
        Date date = new Date(System.currentTimeMillis());
        PaymoUser user1 = new PaymoUser(Integer.MAX_VALUE);
        PaymoUser user2 = new PaymoUser(Integer.MAX_VALUE-1);
        Double amount = new Double(23.74);
        String message = "test";
        mPaymoGraph.addTransaction(new PaymoTransaction(date, user1, user2, amount, message));

        // second transaction
        Date dateSecond = new Date(System.currentTimeMillis());
        PaymoUser user1Second = new PaymoUser(Integer.MAX_VALUE);
        PaymoUser user2Second = new PaymoUser(Integer.MAX_VALUE-1);
        Double amountSecond = new Double(15.31);
        String messageSecond = "test";

        // get size changes for second transaction
        int sizeBeforeSecondTransaction = mPaymoGraph.numVertices();
        mPaymoGraph.addTransaction(new PaymoTransaction(dateSecond, user1Second, user2Second, amountSecond, messageSecond));
        int sizeAfterSecondTransaction = mPaymoGraph.numVertices();

        // assertions
        assert (sizeAfterSecondTransaction == sizeBeforeSecondTransaction);
        assert (mPaymoGraph.getVertex(user1).hasNeighbor(user2));
        assert (mPaymoGraph.getVertex(user2).hasNeighbor(user1));
    }

    /* Test transaction between one new user and one existing */
    @Test
    public void testAddTransactionFirstNewSecondExisting() {

        // first transaction
        Date date = new Date(System.currentTimeMillis());
        PaymoUser user1 = new PaymoUser(Integer.MAX_VALUE);
        PaymoUser user2 = new PaymoUser(Integer.MAX_VALUE-1);
        Double amount = new Double(23.74);
        String message = "test";
        mPaymoGraph.addTransaction(new PaymoTransaction(date, user1, user2, amount, message));

        // second transaction
        Date dateSecond = new Date(System.currentTimeMillis());
        PaymoUser user1Second = new PaymoUser(Integer.MAX_VALUE-2);
        PaymoUser user2Second = new PaymoUser(Integer.MAX_VALUE);
        Double amountSecond = new Double(15.31);
        String messageSecond = "test";

        // get size changes for second transaction
        int sizeBeforeSecondTransaction = mPaymoGraph.numVertices();
        mPaymoGraph.addTransaction(new PaymoTransaction(dateSecond, user1Second, user2Second, amountSecond, messageSecond));
        int sizeAfterSecondTransaction = mPaymoGraph.numVertices();

        // assertions
        assert (sizeAfterSecondTransaction == sizeBeforeSecondTransaction + 1);
        assert (mPaymoGraph.getVertex(user1).hasNeighbor(user2));
        assert (mPaymoGraph.getVertex(user2).hasNeighbor(user1));
    }

    @Test
    public void testAddTransactionFirstExistingSecondNew() {

        // first transaction
        Date date = new Date(System.currentTimeMillis());
        PaymoUser user1 = new PaymoUser(Integer.MAX_VALUE);
        PaymoUser user2 = new PaymoUser(Integer.MAX_VALUE-1);
        Double amount = new Double(23.74);
        String message = "test";
        mPaymoGraph.addTransaction(new PaymoTransaction(date, user1, user2, amount, message));

        // second transaction
        Date dateSecond = new Date(System.currentTimeMillis());
        PaymoUser user1Second = new PaymoUser(Integer.MAX_VALUE);
        PaymoUser user2Second = new PaymoUser(Integer.MAX_VALUE-2);
        Double amountSecond = new Double(15.31);
        String messageSecond = "test";

        // get size changes for second transaction
        int sizeBeforeSecondTransaction = mPaymoGraph.numVertices();
        mPaymoGraph.addTransaction(new PaymoTransaction(dateSecond, user1Second, user2Second, amountSecond, messageSecond));
        int sizeAfterSecondTransaction = mPaymoGraph.numVertices();

        // assertions
        assert (sizeAfterSecondTransaction == sizeBeforeSecondTransaction + 1);
        assert (mPaymoGraph.getVertex(user1).hasNeighbor(user2));
        assert (mPaymoGraph.getVertex(user2).hasNeighbor(user1));
    }
}
