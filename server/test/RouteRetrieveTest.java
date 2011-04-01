import models.Transaction;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Http.Response;
import play.test.Fixtures;
import play.test.FunctionalTest;
import utils.FmtUtil;
import utils.GsonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteRetrieveTest extends FunctionalTest {

    private static Response POST(String url, Map<String, String> params) {
        return POST(url, "application/x-www-form-urlencoded",
                FmtUtil.encode(params));
    }

    @Before
    public void setUp() {
        Fixtures.deleteAll();
        Fixtures.load("fixtures-test.yml");
    }

    @Test
    public void testRouteTransactions() {
        Map<String, String> params = new HashMap<String, String>() {{
            put("registrationId", "some_random_id");
        }};
        Response response = POST("/transactions/all", params);
        assertIsOk(response);
        assertContentType("application/json", response);
        assertCharset("utf-8", response);
        final String body = getContent(response);
        assertTrue(body.length() > 0);
        List<Transaction> transactions = GsonUtil.parseTransactions(body);
        assertTrue(!transactions.isEmpty());
    }

    @Test
    public void testRouteTransactionsAfter() {
        Map<String, String> params = new HashMap<String, String>() {{
            put("registrationId", "some_random_id");
        }};
        Response response = POST("/transactions/0", params);
        assertIsOk(response);
        assertContentType("application/json", response);
        assertCharset("utf-8", response);
        final String body = getContent(response);
        assertTrue(body.length() > 0);
        List<Transaction> transactions = GsonUtil.parseTransactions(body);
        assertTrue(!transactions.isEmpty());
    }
}