import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * Created by Anuved on 11/6/2016.
 */
public class PaymoFeaturesTest {

    private PaymoGraph mPaymoGraph;

    /* Init PaymoGraph with batch_payment.txt before each test */
    @Before
    public void initPaymoGraph() throws FileNotFoundException {
        File batchPaymentsFile = new File("unit-tests/test-paymo-trans/paymo_input/batch_payment.txt");

        PaymoFraudDetector pfd = new PaymoFraudDetector();
        pfd.initGraph(batchPaymentsFile);

        mPaymoGraph = pfd.getPaymoGraph();
    }

    /* Test Feature 1: trusted up to 1st degree */
    @Test
    public void testFeature1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // placeholder date and amount
        Date date = new Date(System.currentTimeMillis());
        Double amount = new Double(1.00);
        String message = "test";

        // set up users
        PaymoUser user1 = new PaymoUser(Integer.MAX_VALUE);
        PaymoUser user2 = new PaymoUser(Integer.MAX_VALUE-1);
        PaymoUser user3 = new PaymoUser(Integer.MAX_VALUE-2);

        // run transactions
        mPaymoGraph.addTransaction(new PaymoTransaction(date, user1, user2, amount, message));
        mPaymoGraph.addTransaction(new PaymoTransaction(date, user1, user3, amount, message));

        // use reflection to test private methods
        Method method = PaymoGraph.class.getDeclaredMethod("isWithinOneDegree", Vertex.class, Vertex.class);
        method.setAccessible(true);

        // test feature1 method
        Object output;
        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user1), mPaymoGraph.getVertex(user2));
        assert ((Boolean) output == true);
        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user2), mPaymoGraph.getVertex(user1));
        assert ((Boolean) output == true);

        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user1), mPaymoGraph.getVertex(user3));
        assert ((Boolean) output == true);
        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user3), mPaymoGraph.getVertex(user1));
        assert ((Boolean) output == true);

        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user2), mPaymoGraph.getVertex(user3));
        assert ((Boolean) output == false);
        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user3), mPaymoGraph.getVertex(user2));
        assert ((Boolean) output == false);
    }

    /* Test Feature 2: trusted up to 2nd degree */
    @Test
    public void testFeature2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // placeholder date and amount
        Date date = new Date(System.currentTimeMillis());
        Double amount = new Double(1.00);
        String message = "test";

        // set up users
        PaymoUser user1 = new PaymoUser(Integer.MAX_VALUE);
        PaymoUser user2 = new PaymoUser(Integer.MAX_VALUE-1);
        PaymoUser user3 = new PaymoUser(Integer.MAX_VALUE-2);
        PaymoUser user4 = new PaymoUser(Integer.MAX_VALUE-3);

        // run transactions
        mPaymoGraph.addTransaction(new PaymoTransaction(date, user1, user2, amount, message));
        mPaymoGraph.addTransaction(new PaymoTransaction(date, user1, user3, amount, message));
        mPaymoGraph.addTransaction(new PaymoTransaction(date, user3, user4, amount, message));

        // use reflection to test private methods
        Method method = PaymoGraph.class.getDeclaredMethod("isWithinTwoDegrees", Vertex.class, Vertex.class);
        method.setAccessible(true);

        // test feature1 method
        Object output;
        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user1), mPaymoGraph.getVertex(user2));
        assert ((Boolean) output == true);
        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user2), mPaymoGraph.getVertex(user1));
        assert ((Boolean) output == true);

        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user1), mPaymoGraph.getVertex(user3));
        assert ((Boolean) output == true);
        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user3), mPaymoGraph.getVertex(user1));
        assert ((Boolean) output == true);

        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user2), mPaymoGraph.getVertex(user3));
        assert ((Boolean) output == true);
        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user3), mPaymoGraph.getVertex(user2));
        assert ((Boolean) output == true);

        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user2), mPaymoGraph.getVertex(user4));
        assert ((Boolean) output == false);
        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user4), mPaymoGraph.getVertex(user2));
        assert ((Boolean) output == false);
    }

    /* Test Feature 3: trusted up to 4th degree */
    @Test
    public void testFeature3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // placeholder date and amount
        Date date = new Date(System.currentTimeMillis());
        Double amount = new Double(1.00);
        String message = "test";

        // set up users
        PaymoUser user1 = new PaymoUser(Integer.MAX_VALUE);
        PaymoUser user2 = new PaymoUser(Integer.MAX_VALUE-1);
        PaymoUser user3 = new PaymoUser(Integer.MAX_VALUE-2);
        PaymoUser user4 = new PaymoUser(Integer.MAX_VALUE-3);
        PaymoUser user5 = new PaymoUser(Integer.MAX_VALUE-4);
        PaymoUser user6 = new PaymoUser(Integer.MAX_VALUE-5);

        // run transactions
        mPaymoGraph.addTransaction(new PaymoTransaction(date, user1, user2, amount, message));
        mPaymoGraph.addTransaction(new PaymoTransaction(date, user1, user3, amount, message));
        mPaymoGraph.addTransaction(new PaymoTransaction(date, user3, user4, amount, message));
        mPaymoGraph.addTransaction(new PaymoTransaction(date, user4, user5, amount, message));
        mPaymoGraph.addTransaction(new PaymoTransaction(date, user5, user6, amount, message));

        // set degree threshold
        Integer degreeThreshold = 4;

        // use reflection to test private methods
        Method method = PaymoGraph.class.getDeclaredMethod("isWithinXDegrees", Vertex.class, Vertex.class, int.class);
        method.setAccessible(true);

        // test feature1 method
        Object output;
        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user1), mPaymoGraph.getVertex(user2), degreeThreshold);
        assert ((Boolean) output == true);
        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user2), mPaymoGraph.getVertex(user1), degreeThreshold);
        assert ((Boolean) output == true);

        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user1), mPaymoGraph.getVertex(user3), degreeThreshold);
        assert ((Boolean) output == true);
        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user3), mPaymoGraph.getVertex(user1), degreeThreshold);
        assert ((Boolean) output == true);

        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user2), mPaymoGraph.getVertex(user3), degreeThreshold);
        assert ((Boolean) output == true);
        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user3), mPaymoGraph.getVertex(user2), degreeThreshold);
        assert ((Boolean) output == true);

        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user2), mPaymoGraph.getVertex(user4), degreeThreshold);
        assert ((Boolean) output == true);
        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user4), mPaymoGraph.getVertex(user2), degreeThreshold);
        assert ((Boolean) output == true);

        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user2), mPaymoGraph.getVertex(user4), degreeThreshold);
        assert ((Boolean) output == true);
        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user4), mPaymoGraph.getVertex(user2), degreeThreshold);
        assert ((Boolean) output == true);

        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user2), mPaymoGraph.getVertex(user5), degreeThreshold);
        assert ((Boolean) output == true);
        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user5), mPaymoGraph.getVertex(user2), degreeThreshold);
        assert ((Boolean) output == true);

        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user2), mPaymoGraph.getVertex(user6), degreeThreshold);
        assert ((Boolean) output == false);
        output = method.invoke(mPaymoGraph, mPaymoGraph.getVertex(user6), mPaymoGraph.getVertex(user2), degreeThreshold);
        assert ((Boolean) output == false);
    }

}
