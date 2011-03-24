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

public class RouteTest extends FunctionalTest {

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
    public void testEncode() {
        Map<String, String> params = new HashMap<String, String>() {{
            put("otherKey", "this is some other key");
            put("theKey", "this is some key");
        }};
        assertEquals("theKey=this+is+some+key&otherKey=this+is+some+other+key",
                FmtUtil.encode(params));
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

    @Test
    public void testRouteSaveNew() {
        final String json = "[\n" +
                "    {\n" +
                "        \"_id\": 4,\n" +
                "        \"accountingDate\": \"2009-04-15 02:00:00\",\n" +
                "        \"amountIn\": 0.0,\n" +
                "        \"amountOut\": 3000.0,\n" +
                "        \"text\": \"Skjermkort\",\n" +
                "        \"trimmedText\": \"Skjermkort\",\n" +
                "        \"internal\": true,\n" +
                "        \"timestamp\": 1,\n" +
                "        \"dirty\": true,\n" +
                "        \"type\": {\n" +
                "            \"name\": \"Visa\",\n" +
                "            \"id\": 18\n" +
                "        },\n" +
                "        \"tag\": {\n" +
                "            \"name\": \"Datautstyr\",\n" +
                "            \"id\": 18\n" +
                "        },\n" +
                "        \"user\": {\n" +
                "            \"deviceId\": \"some_random_id\",\n" +
                "            \"id\": 18\n" +
                "        },\n" +
                "        \"id\": 1000\n" +
                "    }\n" +
                "]";
        Map<String, String> params = new HashMap<String, String>() {{
            put("registrationId", "some_random_id");
            put("json", json);
        }};
        Response response = POST("/transactions/save", params);
        assertIsOk(response);
        assertContentType("application/json", response);
        assertCharset("utf-8", response);
        assertTrue(getContent(response).length() > 0);
        assertTrue(Transaction.count("_id", 4) == 1);
    }

    @Test
    public void testRouteSaveNewNotDirty() {
        final String json = "[\n" +
                "    {\n" +
                "        \"_id\": 5,\n" +
                "        \"accountingDate\": \"2009-04-15 02:00:00\",\n" +
                "        \"amountIn\": 0.0,\n" +
                "        \"amountOut\": 3000.0,\n" +
                "        \"text\": \"Skjermkort\",\n" +
                "        \"trimmedText\": \"Skjermkort\",\n" +
                "        \"internal\": false,\n" +
                "        \"timestamp\": 1,\n" +
                "        \"dirty\": false,\n" +
                "        \"type\": {\n" +
                "            \"name\": \"Visa\",\n" +
                "            \"id\": 18\n" +
                "        },\n" +
                "        \"tag\": {\n" +
                "            \"name\": \"Datautstyr\",\n" +
                "            \"id\": 18\n" +
                "        },\n" +
                "        \"user\": {\n" +
                "            \"deviceId\": \"some_random_id\",\n" +
                "            \"id\": 18\n" +
                "        },\n" +
                "        \"id\": 1000\n" +
                "    }\n" +
                "]";
        Map<String, String> params = new HashMap<String, String>() {{
            put("registrationId", "some_random_id");
            put("json", json);
        }};
        Response response = POST("/transactions/save", params);
        assertIsOk(response);
        assertContentType("application/json", response);
        assertCharset("utf-8", response);
        assertTrue(getContent(response).length() > 0);
        assertTrue(Transaction.count("_id", 5) == 0);
    }

    @Test
    public void testRouteSaveExisting() {
        Transaction existing = Transaction.find("_id", 3).first();
        final String json = "[\n" +
                "    {\n" +
                "        \"_id\": 3,\n" +
                "        \"accountingDate\": \"2009-04-15 02:00:00\",\n" +
                "        \"amountIn\": 0.0,\n" +
                "        \"amountOut\": 300.0,\n" +
                "        \"text\": \"Harddisk\",\n" +
                "        \"trimmedText\": \"Harddisk\",\n" +
                "        \"internal\": true,\n" +
                "        \"timestamp\": 1,\n" +
                "        \"dirty\": true,\n" +
                "        \"type\": {\n" +
                "            \"name\": \"Visa\",\n" +
                "            \"id\": 23\n" +
                "        },\n" +
                "        \"tag\": {\n" +
                "            \"name\": \"Annet\",\n" +
                "            \"id\": 26\n" +
                "        },\n" +
                "        \"user\": {\n" +
                "            \"deviceId\": \"some_random_id\",\n" +
                "            \"id\": 23\n" +
                "        },\n" +
                "        \"id\": " + existing.id + "\n" +
                "    }\n" +
                "]";
        Map<String, String> params = new HashMap<String, String>() {{
            put("registrationId", "some_random_id");
            put("json", json);
        }};
        Response response = POST("/transactions/save", params);
        assertIsOk(response);
        assertContentType("application/json", response);
        assertCharset("utf-8", response);
        assertTrue(getContent(response).length() > 0);
        Transaction t = Transaction.findById(existing.id);
        assertNotNull(t);
        assertNotNull(t.tag);
        assertEquals("Annet", t.tag.name);
    }

    @Test
    public void testRouteSaveExistingNotDirty() {
        Transaction existing = Transaction.find("_id", 3).first();
        final String json = "[\n" +
                "    {\n" +
                "        \"_id\": 3,\n" +
                "        \"accountingDate\": \"2009-04-15 02:00:00\",\n" +
                "        \"amountIn\": 0.0,\n" +
                "        \"amountOut\": 300.0,\n" +
                "        \"text\": \"Harddisk\",\n" +
                "        \"trimmedText\": \"Harddisk\",\n" +
                "        \"internal\": true,\n" +
                "        \"timestamp\": 1,\n" +
                "        \"dirty\": false,\n" +
                "        \"type\": {\n" +
                "            \"name\": \"Visa\",\n" +
                "            \"id\": 23\n" +
                "        },\n" +
                "        \"tag\": {\n" +
                "            \"name\": \"Annet\",\n" +
                "            \"id\": 26\n" +
                "        },\n" +
                "        \"user\": {\n" +
                "            \"deviceId\": \"some_random_id\",\n" +
                "            \"id\": 23\n" +
                "        },\n" +
                "        \"id\": " + existing.id + "\n" +
                "    }\n" +
                "]";
        Map<String, String> params = new HashMap<String, String>() {{
            put("registrationId", "some_random_id");
            put("json", json);
        }};
        Response response = POST("/transactions/save", params);
        assertIsOk(response);
        assertContentType("application/json", response);
        assertCharset("utf-8", response);
        assertTrue(getContent(response).length() > 0);
        Transaction t = Transaction.findById(existing.id);
        assertNotNull(t);
        assertNotNull(t.tag);
        assertEquals("Datautstyr", t.tag.name);
    }
}