import org.junit.Test;
import play.test.UnitTest;
import utils.GsonUtil;

import java.util.Date;

public class GsonUtilTest extends UnitTest {

    @Test
    public void testRenderJSONWithDateFmt() {
        String expected = "\"2010-01-01 00:00:00\"";
        String actual = GsonUtil.renderJSONWithDateFmt("yyyy-MM-dd HH:mm:ss",
                new Date(1262300400000L));
        assertEquals(expected, actual);
    }
}
