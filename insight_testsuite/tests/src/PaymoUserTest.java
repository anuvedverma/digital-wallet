import org.junit.Test;

import java.util.HashSet;

/**
 * Created by Anuved on 11/6/2016.
 */
public class PaymoUserTest {

    @Test
    public void testEquals() {
        PaymoUser user1 = new PaymoUser(24);
        PaymoUser user2 = new PaymoUser(24);
        assert (user1.equals(user2));
    }

    @Test
    public void testNotEquals() {
        PaymoUser user1 = new PaymoUser(24);
        PaymoUser user2 = new PaymoUser(25);
        assert (!user1.equals(user2));

    }

    @Test
    public void testHashChodeSame() {
        HashSet<PaymoUser> hashSet = new HashSet<>();
        PaymoUser user1 = new PaymoUser(24);
        PaymoUser user2 = new PaymoUser(24);

        hashSet.add(user1);
        hashSet.add(user2);

        assert (hashSet.size() == 1);
    }

    @Test
    public void testHashCodeDifferent() {
        HashSet<PaymoUser> hashSet = new HashSet<>();
        PaymoUser user1 = new PaymoUser(24);
        PaymoUser user2 = new PaymoUser(25);

        hashSet.add(user1);
        hashSet.add(user2);

        assert (hashSet.size() == 2);
    }



}
