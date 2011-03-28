import models.Transaction;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Http;
import play.test.Fixtures;
import play.test.FunctionalTest;
import utils.FmtUtil;

import java.util.HashMap;
import java.util.Map;

public class RouteSaveTest extends FunctionalTest {

    private static Http.Response POST(String url, Map<String, String> params) {
        return POST(url, "application/x-www-form-urlencoded",
                FmtUtil.encode(params));
    }

    @Before
    public void setUp() {
        Fixtures.deleteAll();
        Fixtures.load("fixtures-test.yml");
    }

    @Test
    public void testRouteSaveNew() {
        final String json = "[\n" +
                "    {\n" +
                "        \"_id\": 4,\n" +
                "        \"date\": \"2009-04-15 02:00:00\",\n" +
                "        \"amount\": 3000.0,\n" +
                "        \"text\": \"Skjermkort\",\n" +
                "        \"trimmedText\": \"Skjermkort\",\n" +
                "        \"internal\": true,\n" +
                "        \"timestamp\": 1,\n" +
                "        \"dirty\": true,\n" +
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
        Http.Response response = POST("/transactions/save", params);
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
                "        \"date\": \"2009-04-15 02:00:00\",\n" +
                "        \"amount\": 3000.0,\n" +
                "        \"text\": \"Skjermkort\",\n" +
                "        \"trimmedText\": \"Skjermkort\",\n" +
                "        \"internal\": false,\n" +
                "        \"timestamp\": 1,\n" +
                "        \"dirty\": false,\n" +
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
        Http.Response response = POST("/transactions/save", params);
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
                "        \"date\": \"2009-04-15 02:00:00\",\n" +
                "        \"amount\": 300.0,\n" +
                "        \"text\": \"Harddisk\",\n" +
                "        \"trimmedText\": \"Harddisk\",\n" +
                "        \"internal\": true,\n" +
                "        \"timestamp\": 1,\n" +
                "        \"dirty\": true,\n" +
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
        Http.Response response = POST("/transactions/save", params);
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
                "        \"date\": \"2009-04-15 02:00:00\",\n" +
                "        \"amount\": 300.0,\n" +
                "        \"text\": \"Harddisk\",\n" +
                "        \"trimmedText\": \"Harddisk\",\n" +
                "        \"internal\": true,\n" +
                "        \"timestamp\": 1,\n" +
                "        \"dirty\": false,\n" +
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
        Http.Response response = POST("/transactions/save", params);
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
