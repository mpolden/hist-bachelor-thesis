import models.Transaction;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Http.Response;
import play.test.Fixtures;
import play.test.FunctionalTest;

public class RouteTest extends FunctionalTest {

    @Before
    public void setUp() {
        Fixtures.deleteAll();
        new Import().doJob();
    }

    @Test
    public void testRouteTransactions() {
        Response response = GET("/transactions");
        assertIsOk(response);
        assertContentType("application/json", response);
        assertCharset("utf-8", response);
        assertTrue(getContent(response).length() > 0);
    }

    @Test
    public void testRouteTransactionsAfter() {
        Response response = GET("/transactions/0");
        assertIsOk(response);
        assertContentType("application/json", response);
        assertCharset("utf-8", response);
        assertTrue(getContent(response).length() > 0);
    }

    @Test
    public void testRouteSaveNew() {
        final String json = "[{\"_id\":1,\"accountingDate\":\"2011-02-17 00:00:00\"," +
                "\"fixedDate\":\"2011-02-17 00:00:00\",\"amountIn\":0.0,\"amountOut\":1321.0,\"text\":\"test\"," +
                "\"internal\":true,\"timestamp\":1300379994253,\"dirty\":true,\"type\":{\"name\":\"Kontant\",\"id\":6}," +
                "\"tag\":{\"name\":\"Bil\",\"id\":5},\"id\":0}]";
        Response response = POST("/transactions", "application/json", json);
        assertIsOk(response);
        assertContentType("application/json", response);
        assertCharset("utf-8", response);
        assertTrue(getContent(response).length() > 0);
        assertTrue(Transaction.count("internal", true) == 1);
    }

    @Test
    public void testRouteSaveNewNotDirty() {
        final String json = "[{\"_id\":1,\"accountingDate\":\"2011-02-17 00:00:00\"," +
                "\"fixedDate\":\"2011-02-17 00:00:00\",\"amountIn\":0.0,\"amountOut\":1321.0,\"text\":\"test\"," +
                "\"internal\":true,\"timestamp\":1300379994253,\"dirty\":false,\"type\":{\"name\":\"Kontant\",\"id\":6}," +
                "\"tag\":{\"name\":\"Bil\",\"id\":5},\"id\":0}]";
        Response response = POST("/transactions", "application/json", json);
        assertIsOk(response);
        assertContentType("application/json", response);
        assertCharset("utf-8", response);
        assertTrue(getContent(response).length() > 0);
        assertTrue(Transaction.count("internal", true) == 0);
    }

    @Test
    public void testRouteSaveExisting() {
        final String json = "[{\"_id\":0,\"accountingDate\":\"2009-04-15 00:00:00\"," +
                "\"fixedDate\":\"2009-04-15 00:00:00\",\"amountIn\":0.0,\"amountOut\":1272.56," +
                "\"text\":\"456997107150**** 09.04 SEK 1550,00 CLAS OHLSON AB (49)\",\"archiveRef\":\"50001685147\"," +
                "\"internal\":false,\"timestamp\":1239746400000,\"dirty\":true,\"type\":{\"name\":\"Visa\",\"id\":1}," +
                "\"tag\":{\"name\":\"Bil\",\"id\":4},\"id\":7}]";
        Response response = POST("/transactions", "application/json", json);
        assertIsOk(response);
        assertContentType("application/json", response);
        assertCharset("utf-8", response);
        assertTrue(getContent(response).length() > 0);
        Transaction t = Transaction.find("order by accountingDate desc").first();
        assertNotNull(t);
        assertNotNull(t.tag);
        assertEquals("Bil", t.tag.name);
    }

    @Test
    public void testRouteSaveExistingNotDirty() {
        final String json = "[{\"_id\":0,\"accountingDate\":\"2009-04-15 00:00:00\"," +
                "\"fixedDate\":\"2009-04-15 00:00:00\",\"amountIn\":0.0,\"amountOut\":1272.56," +
                "\"text\":\"456997107150**** 09.04 SEK 1550,00 CLAS OHLSON AB (49)\",\"archiveRef\":\"50001685147\"," +
                "\"internal\":false,\"timestamp\":1239746400000,\"dirty\":false,\"type\":{\"name\":\"Visa\",\"id\":1}," +
                "\"tag\":{\"name\":\"Dagligvarer\",\"id\":4},\"id\":7}]";
        Response response = POST("/transactions", "application/json", json);
        assertIsOk(response);
        assertContentType("application/json", response);
        assertCharset("utf-8", response);
        assertTrue(getContent(response).length() > 0);
        Transaction t = Transaction.find("order by accountingDate desc").first();
        assertNotNull(t);
        assertNotNull(t.tag);
        assertEquals("Datautstyr", t.tag.name);
    }
}