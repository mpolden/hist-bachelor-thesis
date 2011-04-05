import models.Transaction;
import models.TransactionTag;
import models.User;
import org.junit.Before;
import org.junit.Test;
import play.test.Fixtures;
import play.test.UnitTest;

/**
 * Test case for model
 */
public class ModelTest extends UnitTest {

    /**
     * Delete all fixtures and import test fixture before tests run
     */
    @Before
    public void setUp() {
        Fixtures.deleteAll();
        Fixtures.load("fixtures-test.yml");
    }

    /**
     * Test Transaction fields
     */
    @Test
    public void testTransactionFields() {
        Transaction t = Transaction.all().first();
        assertNotNull(t);
        assertNotNull(t.date);
        assertNotNull(t.amount);
        assertNotNull(t.text);
        assertNotNull(t.tag);
        assertNotNull(t.internal);
        assertNotNull(t.timestamp);
        assertNotNull(t.dirty);
        assertNotNull(t._id);
        assertNotNull(t.user);
    }

    /**
     * Test TransactionTag fields
     */
    @Test
    public void testTransactionTagFields() {
        TransactionTag t = TransactionTag.all().first();
        assertNotNull(t);
        assertNotNull(t.name);
    }

    /**
     * Test User fields
     */
    @Test
    public void testUserFields() {
        User u = User.all().first();
        assertNotNull(u);
        assertNotNull(u.deviceId);
    }
}
