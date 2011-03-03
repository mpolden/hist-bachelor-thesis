import org.junit.Test;
import play.test.UnitTest;
import utils.GsonUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GsonUtilsTest extends UnitTest {

    @Test
    public void testRenderJSONWithDateFmt() {
        final Date now = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String expected = String.format("\"%s\"", sdf.format(now));
        String actual = GsonUtils.renderJSONWithDateFmt("yyyy-MM-dd HH:mm:ss",
                now);
        assertEquals(expected, actual);
    }
}
