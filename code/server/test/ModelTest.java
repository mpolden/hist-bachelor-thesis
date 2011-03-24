import models.Transaction;
import models.TransactionTag;
import models.TransactionType;
import models.User;
import org.junit.Before;
import org.junit.Test;
import play.test.Fixtures;
import play.test.UnitTest;

public class ModelTest extends UnitTest {

    @Before
    public void setUp() {
        Fixtures.deleteAll();
        Fixtures.load("fixtures-test.yml");
    }

    @Test
    public void testTransactionFields() {
        Transaction t = Transaction.all().first();
        assertNotNull(t);
        assertNotNull(t.accountingDate);
        assertNotNull(t.amountIn);
        assertNotNull(t.amountOut);
        assertNotNull(t.text);
        assertNotNull(t.type);
        assertNotNull(t.tag);
        assertNotNull(t.internal);
        assertNotNull(t.timestamp);
        assertNotNull(t.dirty);
        assertNotNull(t._id);
        assertNotNull(t.user);
    }

    @Test
    public void testTransactionTagFields() {
        TransactionTag t = TransactionTag.all().first();
        assertNotNull(t);
        assertNotNull(t.name);
    }

    @Test
    public void testTransactionTypeFields() {
        TransactionType t = TransactionType.all().first();
        assertNotNull(t);
        assertNotNull(t.name);
    }

    @Test
    public void testUserFields() {
        User u = User.all().first();
        assertNotNull(u);
        assertNotNull(u.deviceId);
    }
}
