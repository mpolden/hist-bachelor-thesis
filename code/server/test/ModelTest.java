import models.Transaction;
import models.TransactionTag;
import models.TransactionType;
import org.junit.Test;
import play.test.UnitTest;

public class ModelTest extends UnitTest {

    @Test
    public void testTransactionFields() {
        Transaction t = Transaction.findById(1L);
        assertNotNull(t.accountingDate);
        assertNotNull(t.fixedDate);
        assertNotNull(t.amountIn);
        assertNotNull(t.amountOut);
        assertNotNull(t.text);
        assertNotNull(t.archiveRef);
        assertNotNull(t.type);
        assertNotNull(t.tag);
    }

    @Test
    public void testTransactionTagFields() {
        TransactionTag t = TransactionTag.findById(1L);
        assertNotNull(t.name);
    }

    @Test
    public void testTransactionTypeFields() {
        TransactionType t = TransactionType.findById(1L);
        assertNotNull(t.name);
    }
}
