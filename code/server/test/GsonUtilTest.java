import org.junit.Test;
import play.test.UnitTest;
import utils.GsonUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GsonUtilTest extends UnitTest {

    @Test
    public void testRenderJSONWithDateFmt() {
        final Date now = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String expected = String.format("\"%s\"", sdf.format(now));
        String actual = GsonUtil.renderJSONWithDateFmt("yyyy-MM-dd HH:mm:ss",
                now);
        assertEquals(expected, actual);
    }
}
