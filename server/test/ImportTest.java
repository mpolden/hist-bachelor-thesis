import models.Transaction;
import models.TransactionTag;
import models.User;
import org.junit.Before;
import org.junit.Test;
import play.test.Fixtures;
import play.test.UnitTest;

/**
 * Test case for Import
 */
public class ImportTest extends UnitTest {

    /**
     * Delete all fixtures and import test fixture before tests run
     */
    @Before
    public void setUp() {
        Fixtures.deleteAll();
        Fixtures.load("fixtures-test.yml");
    }

    /**
     * Test import of transactions
     */
    @Test
    public void testImportTransaction() {
        assertTrue(Transaction.count() > 0);
    }

    /**
     * Test import of transaction tags
     */
    @Test
    public void testImportTransactionTag() {
        assertTrue(TransactionTag.count() > 0);
    }

    /**
     * Test import of users
     */
    @Test
    public void testImportUsers() {
        assertTrue(User.count() > 0);
    }
}
