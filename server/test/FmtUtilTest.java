import org.junit.Test;
import play.test.UnitTest;
import utils.FmtUtil;

import java.util.HashMap;

public class FmtUtilTest extends UnitTest {

    /**
     * Test trimTransactionText
     */
    @Test
    public void testTrimTransactionText() {
        String expected;
        String actual;
        expected = "STATOIL INNHERREDSVEIE";
        actual = FmtUtil.trimTransactionText(
                "4569971071500000 29.03 NOK 500,37 STATOIL INNHERREDSVEIE");
        assertEquals(expected, actual);
        expected = "JACK & JONES OSTERSUND";
        actual = FmtUtil.trimTransactionText(
                "4569971071500000 08.04 SEK 1180,00 JACK & JONES OSTERSUND");
        assertEquals(expected, actual);
        expected = "JACK & JONES OSTERSUND";
        actual = FmtUtil.trimTransactionText(
                "4569971071500000 08.04 SEK 1180,00 JACK & JONES OSTERSUND");
        assertEquals(expected, actual);
        expected = "NIDAR AS BLOMSTADVN 2 TRONDHEIM";
        actual = FmtUtil.trimTransactionText(
                "27.03 NIDAR AS BLOMSTADVN 2 TRONDHEIM");
        assertEquals(expected, actual);
        expected = "SATS Norge AS";
        actual = FmtUtil.trimTransactionText(
                "TIL: SATS Norge AS                      BETNR:       279");
        assertEquals(expected, actual);
        expected = "REMA 1000 MELHUS";
        actual = FmtUtil.trimTransactionText("REMA 1000 MELHUS");
        assertEquals(expected, actual);
        expected = "";
        actual = FmtUtil.trimTransactionText(null);
        assertEquals(expected, actual);
    }

    /**
     * Test encode
     */
    @Test
    public void testEncode() {
        String expected = "theKey=this+is+some+key&otherKey=this+is+some+other+key";
        String actual = FmtUtil.encode(new HashMap<String, String>() {{
            put("otherKey", "this is some other key");
            put("theKey", "this is some key");
        }});
        assertEquals(expected, actual);
    }

    /**
     * Test firstWord
     */
    @Test
    public void firstWord() {
        String expected;
        String actual;
        expected = "hello";
        actual = FmtUtil.firstWord("hello world");
        assertEquals(expected, actual);
        expected = "hello";
        actual = FmtUtil.firstWord("hello");
        assertEquals(expected, actual);
        expected = "";
        actual = FmtUtil.firstWord("");
        assertEquals(expected, actual);
        expected = "";
        actual = FmtUtil.firstWord(null);
        assertEquals(expected, actual);
    }
}
