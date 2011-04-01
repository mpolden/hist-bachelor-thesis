package no.kantega.android.afp.utils;

import no.kantega.android.afp.models.Transaction;
import no.kantega.android.afp.models.TransactionTag;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GsonUtilTest {

    @Test
    public void testParseTransactions() {
        final String json = "[{\"date\":\"2009-04-15 00:00:00\"," +
                "\"amount\":1272.56," +
                "\"text\":\"CLAS OHLSON AB (49)\"," +
                "\"internal\":false," +
                "\"dirty\":true," +
                "\"timestamp\":1239746400000," +
                "\"tag\":{\"name\":\"Datautstyr\",\"id\":4},\"id\":7}]";
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Transaction t = new Transaction();
        try {
            t.setDate(sdf.parse("2009-04-15 00:00:00"));
            t.setAmount(1272.56);
            t.setText("CLAS OHLSON AB (49)");
            t.setId(1);
            final TransactionTag tag = new TransactionTag();
            tag.setId(4);
            tag.setName("Datautstyr");
            t.setTag(tag);
            t.setTimestamp(1239746400000L);
            t.setInternal(false);
            t.setDirty(true);
            t.setId(7);
        } catch (ParseException e) {
            assertTrue(false);
        }
        List<Transaction> expected = new ArrayList<Transaction>() {{
            add(t);
        }};
        List<Transaction> actual = GsonUtil.parseTransactions(json);
        assertEquals(expected, actual);
    }

    @Test
    public void testParseMap() {
        final List<Map<String, String>> expected = new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("count", "6");
                put("tag", "Transport");
            }});
            add(new HashMap<String, String>() {{
                put("count", "1");
                put("tag", "Helse");
            }});
        }};
        final String json = "[{\"count\":6,\"tag\":\"Transport\"},{\"count\":1,\"tag\":\"Helse\"}]";
        final List<Map<String, String>> actual = GsonUtil.parseMap(json);
        assertEquals(expected, actual);
    }
}
