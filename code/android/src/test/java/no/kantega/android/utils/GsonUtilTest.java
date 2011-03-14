package no.kantega.android.utils;

import no.kantega.android.models.Transaction;
import no.kantega.android.models.TransactionTag;
import no.kantega.android.models.TransactionType;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GsonUtilTest {

    @Test
    public void parseTransactions() {
        final String json = "[{\"accountingDate\":\"2009-04-15 00:00:00\"," +
                "\"fixedDate\":\"2009-04-15 00:00:00\",\"amountIn\":0.0," +
                "\"amountOut\":1272.56," +
                "\"text\":\"456997107150**** 09.04 SEK 1550,00 CLAS OHLSON AB (49)\"," +
                "\"archiveRef\":\"50001685147\",\"internal\":false," +
                "\"timestamp\":1239746400000,\"type\":{\"name\":\"Visa\",\"id\":1}," +
                "\"tag\":{\"name\":\"Datautstyr\",\"id\":4},\"id\":7}]";
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Transaction t = new Transaction();
        try {
            t.setAccountingDate(sdf.parse("2009-04-15 00:00:00"));
            t.setFixedDate(sdf.parse("2009-04-15 00:00:00"));
            t.setAmountIn(0.0);
            t.setAmountOut(1272.56);
            t.setText("456997107150**** 09.04 SEK 1550,00 CLAS OHLSON AB (49)");
            t.setArchiveRef("50001685147");
            final TransactionType type = new TransactionType();
            type.setName("Visa");
            t.setType(type);
            final TransactionTag tag = new TransactionTag();
            tag.setName("Datautstyr");
            t.setTag(tag);
            t.setTimestamp(1239746400000L);
            t.setInternal(false);
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
