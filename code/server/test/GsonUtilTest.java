import models.Transaction;
import models.TransactionTag;
import models.TransactionType;
import org.junit.Test;
import play.test.UnitTest;
import utils.GsonUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GsonUtilTest extends UnitTest {

    @Test
    public void testMakeJSON() {
        String expected = "\"2010-01-01 00:00:00\"";
        String actual = GsonUtil.makeJSON(new Date(1262300400000L));
        assertEquals(expected, actual);
    }

    @Test
    public void parseTransactions() {
        final String json = "[{\"accountingDate\":\"2009-04-15 00:00:00\"," +
                "\"amountIn\":0.0," +
                "\"amountOut\":1272.56," +
                "\"text\":\"456997107150**** 09.04 SEK 1550,00 CLAS OHLSON AB (49)\"," +
                "\"internal\":false," +
                "\"timestamp\":1239746400000,\"type\":{\"name\":\"Visa\",\"id\":1}," +
                "\"tag\":{\"name\":\"Datautstyr\",\"id\":4},\"id\":7}]";
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Transaction t = new Transaction();
        try {
            t.accountingDate = sdf.parse("2009-04-15 00:00:00");
            t.amountIn = 0.0;
            t.amountOut = 1272.56;
            t.text = "456997107150**** 09.04 SEK 1550,00 CLAS OHLSON AB (49)";
            final TransactionType type = new TransactionType();
            type.name = "Visa";
            type.id = 1L;
            t.type = type;
            final TransactionTag tag = new TransactionTag();
            tag.id = 4L;
            tag.name = "Datautstyr";
            t.tag = tag;
            t.timestamp = 1239746400000L;
            t.internal = false;
            t.id = 7L;
        } catch (ParseException e) {
            assertTrue(false);
        }
        List<Transaction> expected = new ArrayList<Transaction>() {{
            add(t);
        }};
        List<Transaction> actual = GsonUtil.parseTransactions(json);
        assertEquals(expected, actual);
    }
}
