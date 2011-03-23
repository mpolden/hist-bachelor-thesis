import models.Transaction;
import models.TransactionTag;
import models.TransactionType;
import models.User;
import org.junit.Before;
import org.junit.Test;
import play.test.Fixtures;
import play.test.UnitTest;

import java.util.List;

public class ModelTest extends UnitTest {

    @Before
    public void setUp() {
        Fixtures.deleteAll();
        Fixtures.load("fixtures-test.yml");
    }

    @Test
    public void testTransactionFields() {
        List<Transaction> transactions = Transaction.findAll();
        assertNotNull(transactions);
        for (Transaction t : transactions) {
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
    }

    @Test
    public void testTransactionTagFields() {
        List<TransactionTag> transactionTags = TransactionTag.findAll();
        assertNotNull(transactionTags);
        for (TransactionTag t : transactionTags) {
            assertNotNull(t);
            assertNotNull(t.name);
        }
    }

    @Test
    public void testTransactionTypeFields() {
        List<TransactionType> transactionTypes = TransactionType.findAll();
        assertNotNull(transactionTypes);
        for (TransactionType t : transactionTypes) {
            assertNotNull(t);
            assertNotNull(t.name);
        }
    }

    @Test
    public void testUserFields() {
        List<User> users = User.findAll();
        assertNotNull(users);
        for (User u : users) {
            assertNotNull(u);
            assertNotNull(u.deviceId);
        }
    }
}
