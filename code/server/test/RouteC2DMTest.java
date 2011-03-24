import org.junit.Test;
import play.Play;
import play.test.FunctionalTest;

import java.util.Properties;

public class RouteC2DMTest extends FunctionalTest {

    @Test
    public void testConfiguration() {
        Properties conf = Play.configuration;
        assertNotNull(conf.getProperty("c2dm.auth.url"));
        assertNotNull(conf.getProperty("c2dm.auth.email"));
        assertNotNull(conf.getProperty("c2dm.auth.password"));
        assertNotNull(conf.getProperty("c2dm.auth.service"));
        assertNotNull(conf.getProperty("c2dm.auth.source"));
        assertNotNull(conf.getProperty("c2dm.auth.accountType"));
        assertNotNull(conf.getProperty("c2dm.push.url"));
        assertNotNull(conf.getProperty("c2dm.push.token"));
    }
}
