import models.Transaction;
import models.TransactionTag;
import models.User;
import org.junit.Before;
import org.junit.Test;
import play.test.Fixtures;
import play.test.UnitTest;

public class ImportTest extends UnitTest {

    @Before
    public void setUp() {
        Fixtures.deleteAll();
        Fixtures.load("fixtures-test.yml");
    }

    @Test
    public void testImportTransaction() {
        assertTrue(Transaction.count() > 0);
    }

    @Test
    public void testImportTransactionTag() {
        assertTrue(TransactionTag.count() > 0);
    }

    @Test
    public void testImportUsers() {
        assertTrue(User.count() > 0);
    }
}
